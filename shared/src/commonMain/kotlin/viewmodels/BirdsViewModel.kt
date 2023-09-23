package viewmodels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import models.Bird


sealed interface BirdsUIState {
    data object Loading : BirdsUIState
    data object Error : BirdsUIState

    data class BirdsDataState(
        val categorizedBirds: Map<String, List<Bird>> = emptyMap(),
        val selectedBirdIndex: Int = 0
    ): BirdsUIState
}


class BirdsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<BirdsUIState>(BirdsUIState.Loading)
    val viewState = _viewState.asStateFlow()

    // make this injectable
    // todo: koin setup
    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) { json() }
        }
    }

    init {
        viewModelScope.launch {
            getBirds()
                .onSuccess { birds ->
                    val categorizedBirdsMap = birds.groupBy { it.category }
                    _viewState.value = BirdsUIState.BirdsDataState(categorizedBirdsMap)
                }
                .onFailure {
                    _viewState.value = BirdsUIState.Error
                }
        }
    }

    fun selectCategoryTab(tabIndex: Int) {
        (viewState.value as? BirdsUIState.BirdsDataState)?.categorizedBirds?.let {
            BirdsUIState.BirdsDataState(
                categorizedBirds = it,
                selectedBirdIndex = tabIndex
            )
        }?.let { _viewState.value = it }
    }

    private suspend fun getBirds(): Result<List<Bird>> = try {
        val birds = httpClient
            .get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<Bird>>()
        Result.success(birds)
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}
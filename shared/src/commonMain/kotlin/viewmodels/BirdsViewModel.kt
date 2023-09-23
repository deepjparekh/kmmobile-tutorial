package viewmodels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Bird

data class BirdsViewState(
    val birds: List<Bird> = emptyList()
)

class BirdsViewModel: ViewModel() {
    private val _viewState = MutableStateFlow(BirdsViewState())
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
            _viewState.update { it.copy(birds = getBirds()) }
        }
    }

    private suspend fun getBirds(): List<Bird> = httpClient
        .get("https://sebi.io/demo-image-api/pictures.json")
        .body<List<Bird>>()

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import models.Bird
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import viewmodels.BirdsUIState
import viewmodels.BirdsViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val birdsViewModel: BirdsViewModel = getViewModel(
            key = Unit,
            factory = viewModelFactory { BirdsViewModel() }
        )
        BirdsScreen(viewModel = birdsViewModel)
    }
}

@Composable
fun BirdsScreen(viewModel: BirdsViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    val currentViewState = viewState

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (currentViewState) {
            is BirdsUIState.BirdsDataState -> {
                BirdsDataView(
                    categorizedBirds = currentViewState.categorizedBirds,
                    selectedBirdIndex = currentViewState.selectedBirdIndex,
                ) {
                    viewModel.selectCategoryTab(it)
                }
            }
            is BirdsUIState.Error -> BirdsErrorView()
            is BirdsUIState.Loading -> BirdsLoadingSpinner()
        }
    }
}

@Composable
fun BirdsLoadingSpinner() {
    CircularProgressIndicator(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .size(80.dp)
    )
}

@Composable
fun BirdsErrorView() {
    Text(text = "Failed Loading Birds")
}

@Composable
fun BirdsDataView(
    categorizedBirds: Map<String, List<Bird>>,
    selectedBirdIndex: Int,
    onCategoryClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BirdCategories(
            selectedCategoryIndex = selectedBirdIndex,
            categories = categorizedBirds.keys
        ) {
            onCategoryClick(it)

        }
        BirdsGrid(categorizedBirds.values.elementAt(selectedBirdIndex))
    }
}

@Composable
fun BirdCategories(
    selectedCategoryIndex: Int,
    categories: Set<String>,
    onCategorySelected: (Int) -> Unit
) {
    if (categories.isEmpty()) {
        Logger.d("No categories")
        Text("No categories")
    } else {
        TabRow(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            selectedTabIndex = selectedCategoryIndex
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = index == selectedCategoryIndex,
                    onClick = { onCategorySelected(index) },
                ) { Text(category) }
            }
        }
    }
}

@Composable
fun BirdsGrid(birds: List<Bird>) {
    AnimatedVisibility(visible = birds.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            items(birds) { BirdItem(bird = it) }
        }
    }
}

@Composable
fun BirdItem(bird: Bird) {
    Column {
        KamelImage(
            asyncPainterResource(data = "https://sebi.io/demo-image-api/${bird.path}"),
            contentDescription = "$Bird of category: ${bird.category}"
        )
        Text(bird.author)
    }
}
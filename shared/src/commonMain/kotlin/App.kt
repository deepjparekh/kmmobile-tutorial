import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
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
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import models.Bird
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import viewmodels.BirdsViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {

        val birdsViewModel: BirdsViewModel = getViewModel(
            key = Unit,
            factory = viewModelFactory { BirdsViewModel() }
        )

        BirdsList(viewModel = birdsViewModel)
    }
}

@Composable
fun BirdsList(viewModel: BirdsViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    AnimatedVisibility(visible = viewState.birds.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement =  Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
            items(viewState.birds) { BirdItem(bird = it) }
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
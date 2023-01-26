package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TestViewModel : ViewModel() {

    private val _itemsFlow: MutableStateFlow<List<ItemModel>> = MutableStateFlow(emptyList())
    val itemsFlow: StateFlow<List<ItemModel>>
        get() = _itemsFlow.asStateFlow()

    fun addItem(itemModel: ItemModel) {
        _itemsFlow.update { list ->
            list.toMutableList().apply {
                add(itemModel)
            }
        }
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: TestViewModel = viewModel()
) {
    val listOfItems = remember(viewModel) {
        viewModel.itemsFlow
    }.collectAsState()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isSnackBarShow by remember {
        mutableStateOf(false)
    }
    Box(modifier.fillMaxSize()) {
        ItemListScreen(
            lazyListState = lazyListState,
            items = listOfItems.value
        )
        ExampleFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            onClick = {
                val itemsSize = listOfItems.value.size
                viewModel.addItem(ItemModel(itemsSize))
                isSnackBarShow = true
                scope.launch {
                    lazyListState.scrollToItem(itemsSize.inc())
                }
            }
        )
        AnimatedVisibility(visible = isSnackBarShow) {
            SnackBarExample(
                onClick = {
                    scope.launch {
                        isSnackBarShow = false
                    }
                }
            )
        }
    }
}

@Composable
fun SnackBarExample(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Snackbar(
        modifier = modifier.padding(8.dp),
        action = {
            OutlinedButton(
                onClick = onClick
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    ) {
        Text(
            text = "Added",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun ItemListScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    items: List<ItemModel>
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            SingleItem(
                itemModel = item
            )
        }
    }
}

@Composable
fun SingleItem(
    modifier: Modifier = Modifier,
    itemModel: ItemModel
) {
    var isTextClicked by remember {
        mutableStateOf(false)
    }
    val maxLines = animateIntAsState(
        targetValue = if (isTextClicked) 10 else 1,
        animationSpec = tween(3000, 300)
    )
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable {
                    isTextClicked = isTextClicked.not()
                },
            text = "${itemModel.id} very very very very very very very very very very very very very very very very very very very very very long text  very very very very very long text  very very very very very long text",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            maxLines = maxLines.value,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ExampleFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Image(
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
    }
}

data class ItemModel(val id: Int)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview(
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        MainScreen()
    }
}
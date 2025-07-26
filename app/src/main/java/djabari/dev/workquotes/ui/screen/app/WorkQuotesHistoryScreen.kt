package djabari.dev.workquotes.ui.screen.app

import android.content.ClipData
import android.content.ClipDescription
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import djabari.dev.workquotes.data.model.Quote
import djabari.dev.workquotes.di.workQuotesComponent
import djabari.dev.workquotes.ui.components.field.SearchTextField
import djabari.dev.workquotes.ui.components.tile.QuoteHistoryTile
import djabari.dev.workquotes.ui.theme.WorkQuotesTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorkQuotesHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkQuotesHistoryViewModel = viewModel {
        WorkQuotesHistoryViewModel(
            workQuotesRepository = workQuotesComponent().workQuotesRepository,
            savedStateHandle = createSavedStateHandle()
        )
    },
    onBack: () -> Unit = {},
) {
    val searchQueryState by viewModel.searchQueryFlow.collectAsStateWithLifecycle()
    val historyPagingListState = viewModel.workQuotesHistoryFlow.collectAsLazyPagingItems()

    WorkQuotesHistoryScreenContent(
        modifier = modifier,
        searchQueryState = searchQueryState,
        onSearch = viewModel::search,
        historyPagingListState = historyPagingListState,
        onDismiss = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkQuotesHistoryScreenContent(
    modifier: Modifier = Modifier,
    searchQueryState: String,
    onSearch: (String) -> Unit,
    historyPagingListState: LazyPagingItems<Quote>,
    onDismiss: () -> Unit = {}
) {
    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    SearchTextField(
                        searchQuery = searchQueryState,
                        onSearch = onSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        if (historyPagingListState.itemCount == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No history found",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = historyPagingListState.itemCount,
                    key = historyPagingListState.itemKey(),
                    contentType = historyPagingListState.itemContentType()
                ) { index ->
                    val item = historyPagingListState[index]!!
                    QuoteHistoryTile(
                        data = item,
                        modifier = Modifier
                            .animateItem()
                            .animateContentSize()
                    ) {
                        // copy quote to clipboard
                        scope.launch {
                            clipboardManager.setClipEntry(
                                ClipEntry(
                                    clipData = ClipData(
                                        ClipDescription(
                                            "Quote",
                                            arrayOf("text/plain")
                                        ),
                                        ClipData.Item(
                                            "\"${item.content}\" — ${item.author}"
                                        )
                                    )
                                )
                            )
                            // show toast or snackbar
                            Toast.makeText(
                                context,
                                "Quote copied to clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PreviewWorkQuotesHistoryScreen() {
    WorkQuotesTheme {
        Surface {
            val sampleList = List(10) { index ->
                Quote(
                    id = "$index",
                    content = "Sample quote $index",
                    author = "Author $index",
                    tags = List(5) { "Tag ${it + 1}" }
                )
            }
            val pagingData = PagingData.from(sampleList)
            val flow = flowOf(pagingData)
            val sampleLazyPagingItem = flow.collectAsLazyPagingItems()
            WorkQuotesHistoryScreenContent(
                searchQueryState = "",
                historyPagingListState = sampleLazyPagingItem,
                onSearch = {}
            )
        }
    }
}
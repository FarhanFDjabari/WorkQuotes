package djabari.dev.workquotes.ui.screen.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import djabari.dev.workquotes.R
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.di.workQuotesComponent
import djabari.dev.workquotes.ui.components.button.BaseCheckBox
import djabari.dev.workquotes.ui.components.button.BaseOutlinedButton
import djabari.dev.workquotes.ui.components.button.BasePrimaryButton
import djabari.dev.workquotes.ui.components.button.DropdownButton
import djabari.dev.workquotes.ui.components.dialog.AlertDialog
import djabari.dev.workquotes.ui.components.dialog.BaseDialog
import djabari.dev.workquotes.ui.components.form.AutoCompleteFlowField
import djabari.dev.workquotes.ui.components.form.AutoCompleteSuggestionItem
import djabari.dev.workquotes.ui.components.form.NumberFlowField
import djabari.dev.workquotes.ui.components.form.TextFlowField
import djabari.dev.workquotes.ui.components.form.TimeFlowField
import djabari.dev.workquotes.ui.components.sheet.AlertType
import djabari.dev.workquotes.ui.theme.WorkQuotesTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorkQuotesConfigScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkQuotesConfigViewModel = viewModel {
        WorkQuotesConfigViewModel(
            repository = workQuotesComponent().workQuotesRepository,
            workScheduler = workQuotesComponent().workQuoteWorkScheduler,
            configPreferences = workQuotesComponent().providesConfigPreference(),
            savedStateHandle = createSavedStateHandle()
        )
    },
    onOpenHistory: () -> Unit = {}
) {
    val selectedQuoteFrequency by viewModel.selectedQuoteFrequency.collectAsStateWithLifecycle()
    val selectedTime by viewModel.selectedQuoteTime.collectAsStateWithLifecycle()
    val maxNumberOfQuotes by viewModel.currentQuoteSize.collectAsStateWithLifecycle()
    val selectedQuoteTags by viewModel.selectedQuoteTags.collectAsStateWithLifecycle()
    val savingConfigState by viewModel.savingConfigState.collectAsStateWithLifecycle()
    val availableTagsState by viewModel.availableTags.collectAsStateWithLifecycle()
    var showApplyConfirmationDialog by remember { mutableStateOf(false) }
    var showSaveConfigSuccessDialog by rememberSaveable { mutableStateOf(false) }
    val configEnableState by viewModel.configEnabled.collectAsStateWithLifecycle()

    if (showApplyConfirmationDialog) {
        AlertDialog(
            builder = {
                title = "Apply Configuration"
                message = "Are you sure to apply the quote configuration?"
                confirmButtonText = "Apply"
                cancelButtonText = "Cancel"
                hasActionButton = true
                alertType = AlertType.CONFIRMATION
            },
            onSuccess = {
                showApplyConfirmationDialog = false
                viewModel.saveConfig()
            },
            onCancel = {
                showApplyConfirmationDialog = false
            },
            onDismiss = {
                showApplyConfirmationDialog = false
            }
        )
    }

    when (savingConfigState) {
        WorkQuotesConfigViewModel.ViewState.SaveConfigSuccess -> {
            showSaveConfigSuccessDialog = true
            viewModel.resetSavingState()
        }
        is WorkQuotesConfigViewModel.ViewState.SaveConfigError -> {
            viewModel.resetSavingState()
        }
        else -> {
            viewModel.resetSavingState()
        }
    }

    if (showSaveConfigSuccessDialog) {
        AlertDialog(
            builder = {
                title = "Success"
                message = "Configuration saved successfully"
                confirmButtonText = "OK"
                hasActionButton = true
                alertType = AlertType.SUCCESS
            },
            onSuccess = {
                showSaveConfigSuccessDialog = false
            },
            onDismiss = {
                showSaveConfigSuccessDialog = false
            }
        )
    }
    
    WorkQuotesConfigScreenContent(
        modifier = modifier,
        isConfigEnabled = configEnableState,
        availableTags = availableTagsState,
        quoteFrequencyList = viewModel.quoteFrequencyList,
        selectedQuotesFrequency = selectedQuoteFrequency,
        selectedTime = selectedTime,
        maxNumberOfQuotes = maxNumberOfQuotes,
        selectedQuoteTags = selectedQuoteTags,
        onOpenHistory = onOpenHistory,
        onConfigEnabled = {
            viewModel.setConfigEnabled(it)
        },
        onMaxQuoteChanged = {
            viewModel.setQuoteSize(it)
        },
        onQuotesFrequencySelected = {
            viewModel.setQuoteFrequency(it)
        },
        onTimeSelected = {
            viewModel.setQuoteTime(it)
        },
        onTagsChanged = {
            viewModel.setQuoteTags(it)
        },
        onApplyConfig = {
            showApplyConfirmationDialog = true
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkQuotesConfigScreenContent(
    modifier: Modifier = Modifier,
    isConfigEnabled: Boolean = true,
    availableTags: List<String> = emptyList(),
    quoteFrequencyList: List<WorkQuotesFrequency>,
    selectedQuotesFrequency: WorkQuotesFrequency?,
    selectedTime: LocalTime?,
    maxNumberOfQuotes: Int,
    selectedQuoteTags: List<String>,
    onConfigEnabled: (Boolean) -> Unit = {},
    onQuotesFrequencySelected: (WorkQuotesFrequency) -> Unit = {},
    onTimeSelected: (LocalTime) -> Unit = {},
    onMaxQuoteChanged: (Int) -> Unit = {},
    onTagsChanged: (List<String>) -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onApplyConfig: () -> Unit = {}
) {
    var showSelectTagsDialog by rememberSaveable { mutableStateOf(false) }

    if (showSelectTagsDialog) {
        BaseDialog(
            onDismissRequest = {
                showSelectTagsDialog = false
            },
            title = "Select Available Tags",
            singleLineTitle = true
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                contentPadding = PaddingValues(
                    vertical = 8.dp
                )
            ) {
                itemsIndexed(
                    items = availableTags
                ) { index, tag ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newTags = if (selectedQuoteTags.contains(tag)) {
                                    selectedQuoteTags - tag
                                } else {
                                    selectedQuoteTags + tag
                                }
                                onTagsChanged(newTags)
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            BaseCheckBox(
                                isSelected = selectedQuoteTags.contains(tag),
                            ) {
                                val newTags = if (selectedQuoteTags.contains(tag)) {
                                    selectedQuoteTags - tag
                                } else {
                                    selectedQuoteTags + tag
                                }
                                onTagsChanged(newTags)
                            }
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .weight(1f),
                            )
                        }
                    }
                    if (index < availableTags.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_quotes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                        )
                        Text(text = "WorkQuotes")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onOpenHistory()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Enable Work Quotes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Switch(
                    checked = isConfigEnabled,
                    modifier = Modifier.height(32.dp),
                    onCheckedChange = {
                        onConfigEnabled(it)
                    }
                )
            }
            DropdownButton(
                items = quoteFrequencyList,
                selectedIndex = selectedQuotesFrequency?.let {
                    quoteFrequencyList.indexOf(it)
                } ?: -1,
                onItemSelected = { index, item ->
                    onQuotesFrequencySelected(item)
                },
                selectedItemToString = { WorkQuotesFrequency.toString(it) },
                label = "Quote Frequency",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
            TimeFlowField(
                label = "Time",
                hintText = "Select time",
                value = selectedTime,
                initialTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
            ) { hour, minute, second, nanosecond ->
                onTimeSelected(LocalTime(hour, minute, second, nanosecond))
            }
            NumberFlowField(
                label = "Max Number of Quotes",
                value = "$maxNumberOfQuotes",
                minValue = 1,
                maxValue = 10,
                onValueChange = {
                    onMaxQuoteChanged(it.toIntOrNull() ?: 1)
                },
                modifier = Modifier.fillMaxWidth()
            )
            TextFlowField(
                modifier = Modifier,
                label = "Tags",
                isReadOnly = true,
                value = selectedQuoteTags.joinToString(", "),
                placeholderText = "Choose quote tags (ex. funny, motivation, etc)",
                onClick = {
                    showSelectTagsDialog = true
                }
            ) {}
            Spacer(Modifier.height(24.dp))
            BasePrimaryButton(
                onClick = {
                    onApplyConfig()
                },
                text = "Apply Configurations",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewWorkQuotesConfigScreen() {
    var configEnabled by remember { mutableStateOf(true) }
    WorkQuotesTheme {
        Surface {
            WorkQuotesConfigScreenContent(
                isConfigEnabled = configEnabled,
                selectedQuotesFrequency = null,
                quoteFrequencyList = WorkQuotesFrequency.entries.toList(),
                selectedTime = null,
                maxNumberOfQuotes = 1,
                selectedQuoteTags = listOf("motivating", "life"),
                onConfigEnabled = {
                    configEnabled = it
                }
            )
        }
    }
}
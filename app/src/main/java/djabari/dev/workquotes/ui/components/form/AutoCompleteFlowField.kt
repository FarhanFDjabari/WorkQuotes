package djabari.dev.workquotes.ui.components.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import djabari.dev.workquotes.ui.components.field.CustomBasicTextField

data class AutoCompleteSuggestionItem(
    val text: String,
    val id: String
)

@Composable
fun AutoCompleteFlowField(
    value: String,
    suggestions: List<AutoCompleteSuggestionItem>,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholderText: String? = null,
    isRequired: Boolean = false,
    isReadOnly: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(6.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithoutLabel(
        top = 8.dp,
        bottom = 8.dp,
        start = 14.dp,
        end = 14.dp,
    ),
    enabled: Boolean = true,
    showEmptyState: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = if (enabled) MaterialTheme.colorScheme.onSurface else OutlinedTextFieldDefaults.colors().disabledTextColor
    ),
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    validation: (String) -> Boolean = { true },
    onClick: () -> Unit = {},
    onSuggestionSelected: (AutoCompleteSuggestionItem) -> Unit = {},
    onValueChange: (String) -> Unit
) {
    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = textFieldValueState.copy(text = value)

    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition) {
            textFieldValueState = textFieldValue
        }
    }
    // Last String value that either text field was recomposed with or updated in the onValueChange
    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
    // CoreTextField's onValueChange is called multiple times without recomposition in between.
    var lastTextValue by remember(value) { mutableStateOf(value) }

    var errorText: String? by rememberSaveable { mutableStateOf(null) }
    var filteredSuggestions by remember { mutableStateOf(suggestions) }
    var showSuggestions by remember { mutableStateOf(false) }

    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        label?.let {
            FieldHeader(
                label = label,
                isRequired = isRequired,
                enable = enabled
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        CustomBasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValueState = newValue
                val stringChangedSinceLastInvocation = lastTextValue != newValue.text
                lastTextValue = newValue.text

                if (stringChangedSinceLastInvocation) {
                    onValueChange(newValue.text)
                    showSuggestions = newValue.text.isNotEmpty()
                    filteredSuggestions = if (newValue.text.isEmpty()) {
                        suggestions
                    } else {
                        suggestions.filter { it.text.contains(newValue.text, ignoreCase = true) }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            isReadOnly = isReadOnly,
            minLines = 1,
            maxLines = 1,
            enabled = enabled,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            placeholderText = placeholderText,
            onClick = onClick,
            colors = colors,
            trailingIcon = trailingIcon,
            shape = shape,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            contentPadding = contentPadding,
            suffix = suffix,
            prefix = prefix,
            isError = !validation(textFieldValue.text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            errorText = if (!validation(textFieldValue.text)) {
                errorMessage ?: "Must not empty"
            } else {
                null
            }

            if (errorText != null) {
                errorText?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 8.sp
                        ),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(6.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceBright,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .animateContentSize()
            ) {
                if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        itemsIndexed(
                            items = filteredSuggestions,
                            key = { _, suggestion -> suggestion.id }
                        ) { index, suggestion ->
                            CompositionLocalProvider(LocalContentColor provides contentColor) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled) {
                                            // Update text with selected suggestion and move cursor to the end
                                            textFieldValueState = TextFieldValue(
                                                text = suggestion.text,
                                                selection = TextRange(suggestion.text.length)
                                            )
                                            val stringChangedSinceLastInvocation = lastTextValue != suggestion.text
                                            lastTextValue = suggestion.text

                                            // Hide suggestions after selection
                                            showSuggestions = false

                                            if (stringChangedSinceLastInvocation) {
                                                onValueChange(suggestion.text)
                                            }

                                            onSuggestionSelected(suggestion)
                                        }
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = suggestion.text,
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                }
                            }

                            if (index < filteredSuggestions.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                } else if (showSuggestions && filteredSuggestions.isEmpty() && showEmptyState) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import djabari.dev.workquotes.ui.components.field.chip.BasicChipTextField
import djabari.dev.workquotes.ui.components.field.chip.Chip
import djabari.dev.workquotes.ui.components.field.chip.ChipStyle
import djabari.dev.workquotes.ui.components.field.chip.ChipTextFieldDefaults
import djabari.dev.workquotes.ui.components.field.chip.ChipTextFieldState
import djabari.dev.workquotes.ui.components.field.chip.CloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Chip> TagFlowField(
    state: ChipTextFieldState<T>,
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: (value: String) -> T?,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    innerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    label: String? = null,
    labelAction: @Composable (() -> Unit)? = null,
    isRequired: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipHorizontalSpacing: Dp = 4.dp,
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithoutLabel(
        top = 8.dp,
        bottom = 8.dp,
        start = 14.dp,
        end = 14.dp,
    ),
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = { CloseButton(state, it) },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    validation: (String) -> Boolean = { true },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    var errorText: String? by rememberSaveable { mutableStateOf(null) }
    // Copied from androidx.compose.foundation.text.BasicTextField.kt
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }
    var lastTextValue by remember(value) { mutableStateOf(value) }
    val mappedOnValueChange: (TextFieldValue) -> Unit = { newTextFieldValueState ->
        textFieldValueState = newTextFieldValueState

        val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
        lastTextValue = newTextFieldValueState.text

        if (stringChangedSinceLastInvocation) {
            onValueChange(newTextFieldValueState.text)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            label?.let {
                FieldHeader(
                    label = label,
                    isRequired = isRequired,
                    enable = enabled,
                    modifier = Modifier.weight(1f)
                )
            }
            labelAction?.let {
                labelAction()
            }
        }

        BasicChipTextField(
            state = state,
            onSubmit = {
                onSubmit(it.text)
            },
            value = textFieldValue,
            onValueChange = mappedOnValueChange,
            modifier = innerModifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            readOnlyChips = readOnlyChips,
            isError = isError,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            chipStyle = chipStyle,
            chipHorizontalSpacing = chipHorizontalSpacing,
            chipLeadingIcon = chipLeadingIcon,
            chipTrailingIcon = chipTrailingIcon,
            onChipClick = onChipClick,
            onChipLongClick = onChipLongClick,
            interactionSource = interactionSource,
            colors = colors,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = if (state.chips.isEmpty() && value.isEmpty()) "" else " ",
                    innerTextField = innerTextField,
                    enabled = !readOnly,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    contentPadding = contentPadding,
                    colors = colors,
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            shape = shape,
                            colors = colors,
                            focusedBorderThickness = 1.dp,
                            unfocusedBorderThickness = 1.dp,
                        )
                    }
                )
            }
        )

        errorText =
            if (validation(value)) {
                errorMessage ?: "Must not empty"
            } else {
                null
            }

        if (errorText != null) {
            errorText?.let {
                Text(
                    text = it,
                    style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 8.sp
                        ),
                )
            }
        }
    }
}
package djabari.dev.workquotes.ui.components.field

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBasicTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholderText: String? = null,
    isReadOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    shape: Shape = RoundedCornerShape(6.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithoutLabel(
        top = 8.dp,
        bottom = 8.dp,
        start = 14.dp,
        end = 14.dp,
    ),
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = colors.unfocusedTextColor
    ),
    placeholderStyle: TextStyle = textStyle,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit
) {
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                    extraBufferCapacity = 16,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST
                )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Release -> {
                        onClick()
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        interactionSource = interactionSource,
        isReadOnly = isReadOnly,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        cursorBrush = SolidColor(colors.cursorColor),
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = colors,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            trailingIcon = trailingIcon,
            placeholder = {
                placeholderText?.let {
                    Text(
                        text = placeholderText,
                        style = placeholderStyle,
                        maxLines = maxLines
                    )
                }
            },
            prefix = prefix,
            suffix = suffix,
            contentPadding = contentPadding,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBasicTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    placeholderText: String? = null,
    isReadOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    shape: Shape = RoundedCornerShape(6.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithoutLabel(
        top = 8.dp,
        bottom = 8.dp,
        start = 14.dp,
        end = 14.dp,
    ),
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = colors.unfocusedTextColor
    ),
    placeholderStyle: TextStyle = textStyle,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onValueChange: (TextFieldValue) -> Unit
) {
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                    extraBufferCapacity = 16,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST
                )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Release -> {
                        onClick()
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        interactionSource = interactionSource,
        isReadOnly = isReadOnly,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        cursorBrush = SolidColor(colors.cursorColor),
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value.text,
            innerTextField = innerTextField,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = colors,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            trailingIcon = trailingIcon,
            placeholder = {
                placeholderText?.let {
                    Text(
                        text = placeholderText,
                        style = placeholderStyle,
                        maxLines = maxLines
                    )
                }
            },
            prefix = prefix,
            suffix = suffix,
            contentPadding = contentPadding,
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
}

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    value: String,
    isReadOnly: Boolean = false,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int? = null,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.primary),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    interactionSource: MutableInteractionSource? = null,
    onValueChange: (String) -> Unit = {},
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() }
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        interactionSource = interactionSource,
        readOnly = isReadOnly,
        minLines = minLines,
        maxLines = maxLines ?: Int.MAX_VALUE,
        enabled = enabled,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        cursorBrush = cursorBrush,
        textStyle = textStyle,
        decorationBox = decorationBox
    )
}

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    isReadOnly: Boolean = false,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int? = null,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.primary),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    interactionSource: MutableInteractionSource? = null,
    onValueChange: (TextFieldValue) -> Unit = {},
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() }
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        interactionSource = interactionSource,
        readOnly = isReadOnly,
        minLines = minLines,
        maxLines = maxLines ?: Int.MAX_VALUE,
        enabled = enabled,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        cursorBrush = cursorBrush,
        textStyle = textStyle,
        decorationBox = decorationBox
    )
}
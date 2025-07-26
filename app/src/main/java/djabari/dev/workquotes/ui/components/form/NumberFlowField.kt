package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import djabari.dev.workquotes.ui.components.field.BaseTextField
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberFlowField(
    modifier: Modifier = Modifier,
    label: String? = null,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    value: String,
    unit: String? = null,
    placeholderText: String? = null,
    isRequired: Boolean = false,
    isReadOnly: Boolean = false,
    editable: Boolean = true,
    isError: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    validation: (String) -> Boolean = { true },
    errorText: String? = null,
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    val enabled = !(isReadOnly || !editable)
    var errorMessage: String? by rememberSaveable(errorText) { mutableStateOf(errorText) }

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Release -> {
                        if (isReadOnly) onClick()
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    fun updateValue(newValue: String) {
        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {

            newValue.toLongOrNull()?.let { numericValue ->
                if (numericValue in minValue..maxValue) {
                    onValueChange(newValue)
                }
            } ?: onValueChange(newValue)
        }
    }

    fun incrementValue() {
        val newValue = (value.toLongOrNull() ?: 0) + 1
        updateValue(newValue.toString())
    }

    fun decrementValue() {
        val newValue = (value.toLongOrNull() ?: 0) - 1
        updateValue(newValue.toString())
    }

    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        label?.let {
            FieldHeader(
                label = label,
                isRequired = isRequired,
                enable = enabled,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .height(40.dp)
                .background(
                    color = when {
                        isError -> OutlinedTextFieldDefaults.colors().errorContainerColor
                        enabled -> MaterialTheme.colorScheme.surface
                        else -> OutlinedTextFieldDefaults.colors().disabledContainerColor
                    },
                    shape = shape
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outline
                        }
                    ),
                    shape = shape
                )
        ) {
            BaseTextField(
                value = value,
                onValueChange = { newValue ->
                    updateValue(newValue)
                },
                keyboardActions = keyboardActions,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Default
                ),
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else OutlinedTextFieldDefaults.colors().disabledTextColor
                ),
            ) { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    colors = colors,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    placeholder = {
                        placeholderText?.let {
                            Text(
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1
                            )
                        }
                    },
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        top = 0.dp,
                        bottom = 0.dp,
                        start = 14.dp,
                        end = 14.dp,
                    ),
                    container = {

                    }
                )
            }

            unit?.let {
                Text(
                    text = unit,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .width(98.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                        )
                        .clickable(
                            enabled = enabled && (value.toLongOrNull() ?: 0) > minValue,
                            onClick = {
                                decrementValue()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled && (value.toLongOrNull() ?: 0) > minValue) MaterialTheme.colorScheme.onSurface
                        else OutlinedTextFieldDefaults.colors().disabledTextColor
                    )
                }
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp)
                        )
                        .clickable(
                            enabled = enabled && (value.toLongOrNull() ?: 0) < maxValue,
                            onClick = {
                                incrementValue()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled && (value.toLongOrNull() ?: 0) < maxValue) MaterialTheme.colorScheme.onSurface
                        else OutlinedTextFieldDefaults.colors().disabledTextColor
                    )
                }
            }
        }

        errorMessage =
            if (!validation(value)) {
                errorText ?: "Must not empty"
            } else {
                null
            }

        if (errorMessage != null) {
            errorMessage?.let {
                Text(
                    text = it,
                    style =
                    TextStyle(
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 8.sp,
                    ),
                )
            }
        }
    }
}
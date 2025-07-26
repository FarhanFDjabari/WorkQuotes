package djabari.dev.workquotes.ui.components.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactNumberTextField(
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    value: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
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

    fun updateValue(newValue: String) {
        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
            if (newValue.isEmpty()) {
                onValueChange("0")
                return
            }

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(28.dp)
            .background(
                color = if (enabled) MaterialTheme.colorScheme.surfaceContainer
                else OutlinedTextFieldDefaults.colors().disabledContainerColor,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .width(27.dp)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        bottomStart = 4.dp
                    )
                )
                .clickable(
                    enabled = enabled,
                    onClick = {
                        decrementValue()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (enabled && (value.toLongOrNull()
                        ?: 0) > minValue
                ) MaterialTheme.colorScheme.onSurface
                else OutlinedTextFieldDefaults.colors().disabledTextColor
            )
        }

        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        )

        BaseTextField(
            value = value,
            onValueChange = { newValue ->
                updateValue(newValue)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Default
            ),
            enabled = enabled,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else OutlinedTextFieldDefaults.colors().disabledTextColor,
                textAlign = TextAlign.Center,
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
                placeholder = {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    top = 0.dp,
                    bottom = 0.dp,
                    end = 0.dp,
                    start = 0.dp
                ),
                container = {
                    Box(modifier = Modifier)
                }
            )
        }

        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        )

        Box(
            modifier = Modifier
                .width(27.dp)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topEnd = 4.dp,
                        bottomEnd = 4.dp
                    )
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
                modifier = Modifier.size(16.dp),
                tint = if (enabled && (value.toLongOrNull()
                        ?: 0) < maxValue
                ) MaterialTheme.colorScheme.onSurface
                else OutlinedTextFieldDefaults.colors().disabledTextColor
            )
        }
    }
}
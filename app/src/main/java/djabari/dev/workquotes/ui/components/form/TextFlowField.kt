package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import djabari.dev.workquotes.ui.components.field.CustomBasicTextField

@Composable
fun TextFlowField(
    label: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    placeholderText: String? = null,
    isRequired: Boolean = false,
    isReadOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
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
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = if (enabled) MaterialTheme.colorScheme.onSurface
        else OutlinedTextFieldDefaults.colors().disabledTextColor
    ),
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    labelAction: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    val validationResult = if (isRequired && enabled) value.isEmpty() else false

    var errorText: String? by rememberSaveable { mutableStateOf(null) }

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

        CustomBasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            isReadOnly = isReadOnly,
            minLines = minLines,
            maxLines = maxLines,
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
            isError = isError,
        )

        errorText =
            if (validationResult) {
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
                            fontSize = 8.sp,
                        ),
                )
            }
        }
    }
}

package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import djabari.dev.workquotes.ui.components.field.CustomBasicTextField

@Composable
fun SelectionFlowField(
    label: String,
    hintText: String? = null,
    value: String? = null,
    modifier: Modifier = Modifier,
    textFieldHeight: Dp = 40.dp,
    isRequired: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    startIcon: @Composable (() -> Unit)? = null,
    endIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onFieldClicked: () -> Unit,
) {
    val validationResult = if (isRequired && enabled) value == null else false

    var errorText: String? by rememberSaveable { mutableStateOf(null) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FieldHeader(
                label = label,
                isRequired = isRequired,
                enable = enabled,
                modifier = Modifier.weight(1f),
            )
            if (enabled) {
                Text(
                    "Select",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable(
                            enabled = enabled,
                            onClick = onFieldClicked
                        )
                )
            }
        }

        CustomBasicTextField(
            value = value?: "",
            onValueChange = { },
            onClick = onFieldClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            singleLine = singleLine,
            isReadOnly = true,
            enabled = enabled,
            placeholderText = hintText,
            leadingIcon = startIcon,
            trailingIcon = endIcon,
            isError = isError,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else OutlinedTextFieldDefaults.colors().disabledTextColor
            ),
            colors = colors
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
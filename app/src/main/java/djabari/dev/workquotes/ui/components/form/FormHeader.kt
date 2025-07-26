package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FieldHeader(
    label: String,
    enable: Boolean,
    isRequired: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        if (isRequired && enable) {
            Text(text = "*", color = MaterialTheme.colorScheme.error)
        }
    }
}

package djabari.dev.workquotes.ui.components.field

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    placeholder: String = "Search",
    isEnabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    onSearch: (String) -> Unit,
) {
    CustomBasicTextField(
        value = searchQuery,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(20.dp)
            )
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = if (isEnabled) MaterialTheme.colorScheme.onSurface else OutlinedTextFieldDefaults.colors().disabledTextColor
        ),
        colors = OutlinedTextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        trailingIcon = trailingIcon,
        placeholderText = placeholder,
        modifier = modifier,
        onClick = onClick,
        onValueChange = onSearch
    )
}
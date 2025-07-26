package djabari.dev.workquotes.ui.components.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import djabari.dev.workquotes.ui.components.field.CustomBasicTextField
import djabari.dev.workquotes.ui.components.form.FieldHeader
import djabari.dev.workquotes.ui.theme.WorkQuotesTheme

@Composable
fun <T> DropdownButton(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(40.dp),
    enabled: Boolean = true,
    label: String? = null,
    notSetLabel: String? = null,
    items: List<T>,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        DropdownButtonItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        label?.let {
            FieldHeader(
                label = label,
                isRequired = false,
                enable = enabled,
            )
        }
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            CustomBasicTextField(
                value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "-",
                onClick = {
                    expanded = true
                },
                colors = colors,
                enabled = enabled,
                modifier = modifier,
                onValueChange = {},
                isReadOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(if (expanded) 180f else 0f)
                    )
                }
            )
        }

        if (expanded) {
            Dialog(
                onDismissRequest = { expanded = false },
            ) {
                WorkQuotesTheme {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                    ) {
                        val listState = rememberLazyListState()
                        if (selectedIndex > -1) {
                            LaunchedEffect("ScrollToSelected") {
                                listState.scrollToItem(index = selectedIndex)
                            }
                        }

                        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                            if (notSetLabel != null) {
                                item {
                                    DropdownButtonItem(
                                        text = notSetLabel,
                                        selected = false,
                                        enabled = false,
                                        onClick = { },
                                    )
                                }
                            }
                            itemsIndexed(items) { index, item ->
                                val selectedItem = index == selectedIndex
                                drawItem(
                                    item,
                                    selectedItem,
                                    true
                                ) {
                                    onItemSelected(index, item)
                                    expanded = false
                                }

                                if (index < items.lastIndex) {
                                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun DropdownButtonItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = 1f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
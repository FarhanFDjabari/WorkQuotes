package djabari.dev.workquotes.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import djabari.dev.workquotes.utils.DeviceUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BaseDialog(
    modifier: Modifier = Modifier
        .padding(horizontal = 16.dp),
    title: String? = null,
    singleLineTitle: Boolean = false,
    onDismissRequest: () -> Unit = {},
    showCloseButton: Boolean = true,
    enableDismiss: Boolean = true,
    usePlatformDefaultWidth: Boolean = DeviceUtils.isTablet(),
    action: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = usePlatformDefaultWidth,
            dismissOnClickOutside = enableDismiss,
            dismissOnBackPress = enableDismiss
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
    ) {
        Column {
            if (title != null || showCloseButton || action != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = if (singleLineTitle) 1 else Int.MAX_VALUE,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp)
                                .basicMarquee(
                                    iterations = Int.MAX_VALUE
                                )
                        )
                    }
                    if (showCloseButton) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    onDismissRequest()
                                }
                        )
                    }
                    action?.let {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            action()
                        }
                    }
                }
            }
            content(this)
            footer?.let {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp
                )
                it()
            }
        }
    }
}
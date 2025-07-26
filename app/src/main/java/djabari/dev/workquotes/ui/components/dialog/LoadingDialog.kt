package djabari.dev.workquotes.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import djabari.dev.workquotes.utils.DeviceUtils

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    message: String,
    enableDismiss: Boolean = true,
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = enableDismiss,
            dismissOnClickOutside = enableDismiss,
            usePlatformDefaultWidth = DeviceUtils.isTablet(),
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = if (DeviceUtils.isTablet()) 0.dp else 16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                strokeCap = StrokeCap.Round,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
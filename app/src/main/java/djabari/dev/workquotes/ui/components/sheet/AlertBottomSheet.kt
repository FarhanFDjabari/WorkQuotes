package djabari.dev.workquotes.ui.components.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import djabari.dev.workquotes.ui.components.button.BaseOutlinedButton
import djabari.dev.workquotes.ui.components.button.BasePrimaryButton
import kotlinx.serialization.Serializable

@Serializable
data class AlertOption(
    var title: String? = null,
    var message: String? = null,
    var confirmButtonText: String? = null,
    var cancelButtonText: String? = null,
    /**
     * Whether a dialog should have their confirm and cancel button visible or not.
     * If the value is null, then the setting is default, which follows the existing flow (before 2023-11-9).
     */
    var hasActionButton: Boolean? = null,
    var skipPartiallyExpand: Boolean = true,
    var alertType: AlertType = AlertType.INFORMATION,
)

@Serializable
enum class AlertType {
    CONFIRMATION, SUCCESS, ERROR, INFORMATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertBottomSheet(
    modifier: Modifier = Modifier,
    builder: AlertOption.() -> Unit,
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    onSuccess: (SheetState) -> Unit = {},
    onCancel: (SheetState) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val option = AlertOption().apply(builder)

    BaseBottomSheet(
        onDismiss = onDismiss,
        skipPartiallyExpanded = option.skipPartiallyExpand,
        modifier = modifier,
        properties = properties,
        showHandle = false
    ) { state ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.semantics {
                        contentDescription = "Drag handle"
                    },
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 40.dp, height = 4.dp))
                }
            }
            // Title
            option.title?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            // Message
            option.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            // Action buttons
            if (option.hasActionButton == true) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    option.cancelButtonText?.let {
                        BaseOutlinedButton(
                            text = it,
                            modifier = Modifier
                                .weight(1f),
                            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline),
                            onClick = {
                                onCancel(state)
                            }
                        )
                    }
                    option.confirmButtonText?.let {
                        BasePrimaryButton(
                            text = it,
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onSuccess(state)
                            }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
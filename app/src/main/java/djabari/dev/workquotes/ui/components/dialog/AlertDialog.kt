package djabari.dev.workquotes.ui.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import djabari.dev.workquotes.R
import djabari.dev.workquotes.ui.components.button.BasePrimaryButton
import djabari.dev.workquotes.ui.components.sheet.AlertOption
import djabari.dev.workquotes.ui.components.sheet.AlertType
import djabari.dev.workquotes.ui.theme.WorkQuotesTheme

@Composable
internal fun AlertDialog(
    modifier: Modifier = Modifier,
    builder: AlertOption.() -> Unit,
    onSuccess: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val option = AlertOption().apply(builder)

    BaseDialog(
        showCloseButton = false,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentSize(),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (option.alertType) {
                    AlertType.CONFIRMATION -> {
                        Icon(
                            painter = painterResource(R.drawable.rounded_question_mark_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                    AlertType.SUCCESS -> {
                        Icon(
                            painter = painterResource(R.drawable.baseline_check_circle_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                    AlertType.ERROR -> {
                        Icon(
                            painter = painterResource(R.drawable.baseline_dangerous_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                    AlertType.INFORMATION -> {
                        Icon(
                            painter = painterResource(R.drawable.rounded_exclamation_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                }
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
            }
        },
        footer = if (option.hasActionButton == true) {
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    option.cancelButtonText?.let {
                        BasePrimaryButton(
                            text = it,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onCancel()
                            }
                        )
                    }
                    option.confirmButtonText?.let {
                        BasePrimaryButton(
                            text = it,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSuccess()
                            }
                        )
                    }
                }
            }
        } else null
    )
}

@PreviewLightDark
@Composable
private fun AlertDialogPreview() {
    WorkQuotesTheme {
        Surface {
            AlertDialog(
                builder = {
                    title = "Confirmation"
                    message = "Are you sure you want to proceed?"
                    alertType = AlertType.CONFIRMATION
                    confirmButtonText = "Yes"
                    cancelButtonText = "No"
                    hasActionButton = true
                },
                onSuccess = { /* Handle success */ },
                onCancel = { /* Handle cancel */ },
                onDismiss = { /* Handle dismiss */ }
            )
        }
    }
}
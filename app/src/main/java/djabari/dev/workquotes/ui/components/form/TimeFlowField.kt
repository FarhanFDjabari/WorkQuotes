package djabari.dev.workquotes.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import djabari.dev.workquotes.ui.components.button.BaseTextButton
import djabari.dev.workquotes.ui.components.dialog.TimePickerDialog
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFlowField(
    modifier: Modifier = Modifier,
    label: String,
    hintText: String? = null,
    value: LocalTime? = null,
    timeFormat: DateTimeFormat<LocalTime> = LocalTime.Format {
        hour()
        char(':')
        minute()
    },
    is24Hour: Boolean = true,
    initialTime: LocalTime? = null,
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    startIcon: @Composable (() -> Unit)? = null,
    onDateChanged: (hour: Int, minute: Int, second: Int, nanosecond: Int) -> Unit,
) {
    var openTimeBottomSheet by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 0,
        initialMinute = initialTime?.minute ?: 0,
        is24Hour = is24Hour
    )

    if (openTimeBottomSheet) {
        TimePickerDialog(
            content = {
                TimePicker(
                    state = timePickerState
                )
            },
            onDismissRequest = {
                openTimeBottomSheet = false
            },
            confirmButton = {
                BaseTextButton(
                    label = "OK",
                    onClick = {
                        onDateChanged(timePickerState.hour, timePickerState.minute, 0, 0)
                        openTimeBottomSheet = false
                    }
                )
            },
        )
    }

    SelectionFlowField(
        label = label,
        hintText = hintText,
        modifier = modifier,
        value = value?.format(timeFormat),
        enabled = enabled,
        isRequired = isRequired,
        startIcon = startIcon,
        isError = isError,
        errorMessage = errorMessage,
        endIcon = {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = colors.unfocusedTextColor,
                modifier = Modifier
                    .size(20.dp)
            )
        },
        colors = colors,
        onFieldClicked = {
            openTimeBottomSheet = true
        }
    )
}
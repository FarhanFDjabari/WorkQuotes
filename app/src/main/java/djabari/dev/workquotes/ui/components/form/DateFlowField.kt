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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFlowField(
    label: String,
    hintText: String? = null,
    modifier: Modifier = Modifier,
    value: LocalDateTime? = null,
    dateFormat: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
        dayOfMonth()
        char('-')
        monthNumber()
        char('-')
        year()
        chars(", ")
        hour()
        char(':')
        minute()
    },
    withTime: Boolean = false,
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxDay: Instant? = null,
    minDay: Instant? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    startIcon: @Composable (() -> Unit)? = null,
    onDateChanged: (Instant) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openBottomSheet by remember { mutableStateOf(false) }
    var openTimeBottomSheet by remember { mutableStateOf(false) }

//    val minDays = minDay?.let {
//        val daysDiff = Clock.System.now().daysUntil(
//            minDay,
//            TimeZone.currentSystemDefault()
//        )
//        if (daysDiff < 0) {
//            daysDiff - 1
//        } else {
//            daysDiff
//        }
//    }
//
//    val maxDays = maxDay?.let {
//        Clock.System.now().daysUntil(
//            maxDay,
//            TimeZone.currentSystemDefault()
//        )
//    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.fromEpochMilliseconds(utcTimeMillis)
                val minDate = minDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.date
                val maxDate = maxDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.date
                return (minDate == null || date.toLocalDateTime(TimeZone.currentSystemDefault()).date >= minDate) &&
                        (maxDate == null || date.toLocalDateTime(TimeZone.currentSystemDefault()).date <= maxDate)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val maxYear = maxDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.year ?: Int.MAX_VALUE
                val minYear = minDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.year ?: Int.MIN_VALUE
                return year in minYear..maxYear
            }
        }
    )

    val timePickerState = rememberTimePickerState(
        initialHour = minDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.hour ?: 0,
        initialMinute = minDay?.toLocalDateTime(TimeZone.currentSystemDefault())?.minute ?: 0,
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it)
    }

    if (openBottomSheet) {
        DatePickerDialog(
            onDismissRequest = { openBottomSheet = false },
            confirmButton = {
                BaseTextButton(
                    label = "OK",
                    enabled = selectedDate != null,
                    onClick = {
                        if (withTime) {
                            openTimeBottomSheet = true
                        } else {
                            selectedDate?.let {
                                onDateChanged(it)
                            }
                        }
                        openBottomSheet = false
                    }
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.surface,
                selectedYearContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                currentYearContentColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

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
                        selectedDate?.let {
                            val localDateTimeInstant = it.toLocalDateTime(TimeZone.currentSystemDefault()).run {
                                LocalDateTime(year, monthNumber, dayOfMonth, timePickerState.hour, timePickerState.minute)
                            }.toInstant(TimeZone.currentSystemDefault())

                            onDateChanged(localDateTimeInstant)
                        }
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
        value = value?.format(dateFormat),
        enabled = enabled,
        isRequired = isRequired,
        startIcon = startIcon,
        isError = isError,
        errorMessage = errorMessage,
        endIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = colors.unfocusedTextColor,
                modifier = Modifier
                    .size(20.dp)
            )
        },
        singleLine = singleLine,
        colors = colors,
        onFieldClicked = {
            openBottomSheet = true
        }
    )
}
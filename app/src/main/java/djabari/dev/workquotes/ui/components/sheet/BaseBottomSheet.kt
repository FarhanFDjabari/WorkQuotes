package djabari.dev.workquotes.ui.components.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun BaseBottomSheet(
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    showHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable (SheetState) -> Unit = { }
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    val nestedConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return available.copy(x = 0f)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return available.copy(x = 0f)
            }
        }
    }

    ModalBottomSheet(
        modifier = modifier,
        properties = properties,
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = if (showHandle) {
            {
                BottomSheetDefaults.DragHandle(
                    width = 40.dp,
                    height = 4.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else null,
        content = {
            if (showHandle) Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().nestedScroll(nestedConnection),
                contentAlignment = Alignment.Center
            ) {
                content(state)
            }
        }
    )
}
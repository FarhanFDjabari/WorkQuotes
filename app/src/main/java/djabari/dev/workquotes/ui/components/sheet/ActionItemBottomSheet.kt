package djabari.dev.workquotes.ui.components.sheet

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
data class ActionSheetItemData(
    val action: ActionItemData,
    val iconId: Int,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
)

@Parcelize
@Serializable
data class ActionItemData(
    val id: String,
    val title: String,
    val group: Int = 0,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItemBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    actions: @Composable ColumnScope.(SheetState) -> Unit = {}
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        modifier = modifier,
        showHandle = false
    ) { sheet ->
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 40.dp, height = 4.dp))
                }
            }
            actions(sheet)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ActionBottomSheetItem(
    modifier: Modifier = Modifier,
    data: ActionSheetItemData,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
    onClick: (ActionItemData) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = containerColor)
            .clickable { onClick(data.action) }
            .padding(paddingValues = contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = data.action.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            ),
            modifier = Modifier
                .weight(1f)
        )
        Icon(
            painter = painterResource(data.iconId),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ActionBottomSheetItemGroup(
    modifier: Modifier = Modifier,
    items: List<ActionSheetItemData>,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = Color.Unspecified,
    useDivider: Boolean = true,
    onClick: (ActionItemData) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = containerColor),
    ) {
        repeat(items.size) {
            ActionBottomSheetItem(
                data = items[it],
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                containerColor = items[it].containerColor ?: containerColor,
                contentColor = items[it].contentColor ?: contentColor,
                onClick = onClick
            )
            if (it < items.size - 1 && useDivider) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
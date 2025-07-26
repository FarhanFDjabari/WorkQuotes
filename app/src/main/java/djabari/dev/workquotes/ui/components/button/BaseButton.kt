package djabari.dev.workquotes.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BasePrimaryButton(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier.height(40.dp),
    enabled: Boolean = true,
    elevation: ButtonElevation? = null,
    shape: Shape = RoundedCornerShape(6.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        enabled = enabled,
        elevation = elevation,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = contentPadding
    ) {
        leadingIcon?.let {
            leadingIcon()
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text,
            style = textStyle,
            textAlign = TextAlign.Center
        )
        trailingIcon?.let {
            Spacer(Modifier.width(4.dp))
            trailingIcon()
        }
    }
}

@Composable
fun BaseOutlinedButton(
    text: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    containerColor: Color = Color.Unspecified,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    modifier: Modifier = Modifier.height(40.dp),
    elevation: ButtonElevation? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(6.dp),
    border: BorderStroke? = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onPrimary),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        border = border,
        contentPadding = contentPadding
    ) {
        icon?.let {
            icon()
            if (text != null) Spacer(Modifier.width(4.dp))
        }
        text?.let {
            Text(
                text,
                style = textStyle,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BaseChipButton(
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    selectedBorderColor: Color = selectedContentColor,
    unselectedBorderColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
    text: String,
    elevation: Dp = 0.dp,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        modifier = modifier,
        label = {
            Text(
                text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = elevation,
        ),
        enabled = isEnabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedContainerColor,
            selectedLabelColor = selectedContentColor,
            selectedLeadingIconColor = selectedContentColor,
            selectedTrailingIconColor = selectedContentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
            disabledSelectedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = isEnabled,
            selected = isSelected,
            selectedBorderColor = selectedBorderColor,
            borderColor = unselectedBorderColor,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        ),
        onClick = onClick,
    )
}

@Composable
fun BaseTextButton(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    label: String,
    labelStyle: TextStyle? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    TextButton(
        modifier = modifier
            .height(40.dp),
        onClick = onClick,
        contentPadding = contentPadding,
        elevation = elevation,
        shape = shape,
        enabled = enabled,
        colors = colors,
        border = border,
        interactionSource = interactionSource,
        content = {
            leadingIcon?.let {
                it()
                Spacer(Modifier.width(6.dp))
            }
            Text(
                label,
                style = labelStyle ?: MaterialTheme.typography.titleSmall
            )
            trailingIcon?.let {
                Spacer(Modifier.width(6.dp))
                it()
            }
        },
    )
}

@Composable
fun BaseRadioButton(
    enabled: Boolean = true,
    isSelected: Boolean = false,
    scale: Float = 0.8f,
    size: Dp = 20.dp,
    onSelected: (Boolean) -> Unit = {}
) {
    RadioButton(
        enabled = enabled,
        onClick = {
            onSelected(!isSelected)
        },
        selected = isSelected,
        colors = RadioButtonDefaults.colors(
            disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedColor = MaterialTheme.colorScheme.primary,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledUnselectedColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
            .scale(scale)
            .size(size)
    )
}

@Composable
fun BaseCheckBox(
    enabled: Boolean = true,
    isSelected: Boolean = false,
    scale: Float = 0.8f,
    size: Dp = 20.dp,
    onSelected: (Boolean) -> Unit = {}
) {
    Checkbox(
        enabled = enabled,
        checked = isSelected,
        onCheckedChange = {
            onSelected(it)
        },
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.primary,
            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledCheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledUncheckedColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
            .scale(scale)
            .size(size)
    )
}
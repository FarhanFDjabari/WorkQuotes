package djabari.dev.workquotes.utils

import android.content.res.Resources
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

object DeviceUtils {
    val deviceName: String
        get() = Build.MODEL
    val osVersion: String
        get() = "${Build.VERSION.SDK_INT}"
    val deviceVendor: String
        get() = Build.MANUFACTURER

    private val displayMetrics: android.util.DisplayMetrics? by lazy {
        Resources.getSystem().displayMetrics
    }
    @Composable
    fun getScreenWidth(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp
    }

    @Composable
    fun getScreenHeight(): Dp {
        return LocalConfiguration.current.screenHeightDp.dp
    }

    fun isTablet(): Boolean {
        val screenWidth = displayMetrics?.widthPixels ?: 0
        val screenHeight = displayMetrics?.heightPixels ?: 0
        val yInch = screenHeight / (displayMetrics?.ydpi ?: 1f)
        val xInch = screenWidth / (displayMetrics?.xdpi ?: 1f)
        val diagonalInch = sqrt(xInch * xInch + yInch * yInch)
        return diagonalInch >= 7
    }
}
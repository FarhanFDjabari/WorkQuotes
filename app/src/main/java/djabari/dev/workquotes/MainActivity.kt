package djabari.dev.workquotes

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import djabari.dev.workquotes.navigation.WorkQuotesNavigation
import djabari.dev.workquotes.ui.screen.app.WorkQuotesConfigScreen
import djabari.dev.workquotes.ui.screen.app.WorkQuotesHistoryScreen
import djabari.dev.workquotes.ui.theme.WorkQuotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        setContent {
            val navController = rememberNavController()
            var navigationTarget: String? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                val target = intent.getStringExtra("navigation_target")
                if (target == "quotes_history") {
                    navigationTarget = target
                }
            }

            LaunchedEffect(navigationTarget) {
                when (navigationTarget) {
                    "quotes_history" -> {
                        navController.navigate(WorkQuotesNavigation.History)
                    }
                    else -> {
                        // Handle other navigation targets if needed
                    }
                }
                navigationTarget = null
            }

            WorkQuotesTheme {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = WorkQuotesNavigation.Home
                    ) {
                        composable<WorkQuotesNavigation.Home> {
                            WorkQuotesConfigScreen(
                                onOpenHistory = {
                                    navController.navigate(WorkQuotesNavigation.History)
                                }
                            )
                        }
                        composable<WorkQuotesNavigation.History> {
                            WorkQuotesHistoryScreen(
                                onBack = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate(WorkQuotesNavigation.Home) {
                                            popUpTo(WorkQuotesNavigation.Home) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreate()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
            }
        }
    }
}
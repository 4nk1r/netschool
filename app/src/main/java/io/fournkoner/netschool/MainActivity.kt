package io.fournkoner.netschool

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.fournkoner.netschool.ui.navigation.AppNavigation
import io.fournkoner.netschool.ui.style.NetSchoolTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            NetSchoolTheme(darkTheme = isDarkTheme) {
                AppNavigation()

                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(Color.Transparent, !isDarkTheme)
                }
            }
        }
        extendScreen()
    }

    private fun extendScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

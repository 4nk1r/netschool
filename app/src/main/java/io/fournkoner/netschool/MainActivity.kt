package io.fournkoner.netschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.fournkoner.netschool.ui.navigation.AppBottomNavigation
import io.fournkoner.netschool.ui.style.NetSchoolTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            NetSchoolTheme(darkTheme = isDarkTheme) {
                AppBottomNavigation()

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
package com.fadetogo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.fadetogo.app.ui.FadeToGoNavigation
import com.fadetogo.app.ui.theme.DeepBlack
import com.fadetogo.app.ui.theme.FadeToGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // makes the app draw behind the status bar
        // so the black background extends all the way to the top
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // makes status bar icons white so they're visible on black background
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        setContent {
            FadeToGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    FadeToGoNavigation()
                }
            }
        }
    }
}
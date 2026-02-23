package com.fadetogo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.fadetogo.app.ui.FadeToGoNavigation
import com.fadetogo.app.ui.theme.DeepBlack
import com.fadetogo.app.ui.theme.FadeToGoTheme
import com.fadetogo.app.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    // create AuthViewModel at the activity level
    // this means every screen shares the SAME instance
    // preventing the jitter caused by multiple instances being created
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        setContent {
            FadeToGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    // pass the shared authViewModel down to navigation
                    FadeToGoNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}
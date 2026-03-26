package edu.moravian.csci215.tic_tac_toe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview

/**
 * The main entry point for the Android app.
 * Colors the OS status bar and navigation bar with the app's primary color.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(primaryLight.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(primaryLight.toArgb()),
        )
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

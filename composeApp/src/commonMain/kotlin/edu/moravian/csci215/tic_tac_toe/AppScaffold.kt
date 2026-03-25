package edu.moravian.csci215.tic_tac_toe


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

/**
 * The shared app scaffold that wraps the entire navigation graph. Hosts the snackbar
 * and the optional top app bar, keeping the Scaffold outside the NavHost as required.
 *
 * @param snackbarHostState the shared snackbar state used by all screens
 * @param topBar optional top app bar composable (absent on the Welcome screen)
 * @param content the inner content (the NavHost)
 */
@Composable
fun AppScaffold(
    snackbarHostState: SnackbarHostState,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = content
    )
}
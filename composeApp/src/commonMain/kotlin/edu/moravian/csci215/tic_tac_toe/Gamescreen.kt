package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * The game screen where the actual Tic-Tac-Toe board is played.
 * TODO: implement the board UI, turn logic, and AI delay.
 *
 * @param paddingValues  insets from the outer Scaffold
 * @param player1Type    type string for player 1 (Human / Easy AI / etc.)
 * @param player1Name    display name for player 1
 * @param player2Type    type string for player 2
 * @param player2Name    display name for player 2
 * @param showSnackbar   suspending callback to display error snackbars
 * @param onGameOver     called with (p1Wins, p2Wins, ties) when the round ends
 */
@Composable
fun GameScreen(
    paddingValues: PaddingValues,
    player1Type: String,
    player1Name: String,
    player2Type: String,
    player2Name: String,
    showSnackbar: suspend (String) -> Unit,
    onGameOver: (Int, Int, Int) -> Unit
) {
    // TODO: build board, turn indicator, AI logic
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "Game Screen\n$player1Name vs $player2Name",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
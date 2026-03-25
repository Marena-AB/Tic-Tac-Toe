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
 * The game-over screen that reports the result of the round and cumulative scores.
 * TODO: implement result display, score breakdown, final board, and play-again button.
 *
 * @param paddingValues  insets from the outer Scaffold
 * @param player1Type    type string for player 1
 * @param player1Name    display name for player 1
 * @param player2Type    type string for player 2
 * @param player2Name    display name for player 2
 * @param player1Wins    cumulative wins for player 1
 * @param player2Wins    cumulative wins for player 2
 * @param ties           cumulative tied rounds
 * @param onPlayAgain    called when the user wants to play another round
 */
@Composable
fun GameOverScreen(
    paddingValues: PaddingValues,
    player1Type: String,
    player1Name: String,
    player2Type: String,
    player2Name: String,
    player1Wins: Int,
    player2Wins: Int,
    ties: Int,
    onPlayAgain: () -> Unit
) {
    // TODO: build result UI, score summary, final board snapshot, play-again button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "Game Over\n$player1Name $player1Wins – $player2Wins $player2Name\nTies: $ties",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
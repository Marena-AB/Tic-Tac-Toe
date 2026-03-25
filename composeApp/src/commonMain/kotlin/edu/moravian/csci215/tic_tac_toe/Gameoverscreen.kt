package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.tic_tac_toe.game.Board

/**
 * The game-over screen shown after every round. Displays who won (or if it
 * was a tie), the final board state, the cumulative score, and a button to
 * play again.
 *
 * @param paddingValues insets from the outer Scaffold
 * @param player1Type   type string for player 1
 * @param player1Name   display name for player 1
 * @param player2Type   type string for player 2
 * @param player2Name   display name for player 2
 * @param player1Wins   cumulative wins for player 1 (already includes this round)
 * @param player2Wins   cumulative wins for player 2 (already includes this round)
 * @param ties          cumulative tied rounds (already includes this round)
 * @param finalBoard    the board state at the end of the round
 * @param onPlayAgain   called when the user wants to start a new round
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
    finalBoard: Board,
    onPlayAgain: () -> Unit
) {
    // Determine what happened this round based on the final board
    val resultText = when {
        finalBoard.hasWon('X') -> "$player1Name${AppStrings.WINS_SUFFIX}"
        finalBoard.hasWon('O') -> "$player2Name${AppStrings.WINS_SUFFIX}"
        else                   -> AppStrings.TIE_RESULT
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ResultHeadline(resultText)

        Spacer(modifier = Modifier.height(24.dp))

        FinalBoardDisplay(board = finalBoard)

        Spacer(modifier = Modifier.height(24.dp))

        ScoreCard(
            player1Name = player1Name,
            player1Wins = player1Wins,
            player2Name = player2Name,
            player2Wins = player2Wins,
            ties        = ties
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick  = onPlayAgain,
            shape    = RoundedCornerShape(50),
            modifier = Modifier
                .defaultMinSize(minWidth = 160.dp)
                .height(52.dp)
        ) {
            Text(
                text  = AppStrings.PLAY_AGAIN_BUTTON,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

/**
 * Large, prominent headline showing the round result (e.g. "Inky wins!" or "It's a tie!").
 *
 * @param text the result string to display
 */
@Composable
private fun ResultHeadline(text: String) {
    Text(
        text      = text,
        style     = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
        color     = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
}

/**
 * A read-only display of the final board state. Pieces are drawn as text inside
 * a grid with the same visual style as the game board, but cells are not tappable.
 *
 * @param board the final board to render
 */
@Composable
private fun FinalBoardDisplay(board: Board) {
    Column(
        modifier = Modifier
            .size(240.dp) // fixed square so it doesn't dominate the screen
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        for (r in 0..2) {
            if (r > 0) {
                HorizontalDivider(
                    thickness = 2.dp,
                    color     = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                for (c in 0..2) {
                    if (c > 0) {
                        VerticalDivider(
                            thickness = 2.dp,
                            color     = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                    // Each cell just displays the piece — no click handler
                    Box(
                        modifier         = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        val piece = board[r, c]
                        if (piece != ' ') {
                            Text(
                                text  = piece.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A card showing the cumulative win/tie totals for both players.
 *
 * @param player1Name display name for player 1
 * @param player1Wins cumulative wins for player 1
 * @param player2Name display name for player 2
 * @param player2Wins cumulative wins for player 2
 * @param ties        cumulative ties
 */
@Composable
private fun ScoreCard(
    player1Name: String,
    player1Wins: Int,
    player2Name: String,
    player2Wins: Int,
    ties: Int
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text  = AppStrings.SCORE_HEADER,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScorePill(label = player1Name, value = player1Wins)
                ScorePill(label = AppStrings.TIES_LABEL, value = ties)
                ScorePill(label = player2Name, value = player2Wins)
            }
        }
    }
}

/**
 * A small pill showing a single score entry (label + number).
 *
 * @param label the player name or "Ties"
 * @param value the count to display
 */
@Composable
private fun ScorePill(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text  = value.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
    }
}
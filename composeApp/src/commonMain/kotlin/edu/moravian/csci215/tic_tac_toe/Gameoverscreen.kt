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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.tic_tac_toe.game.Board

/**
 * The game-over screen shown after every round. Displays who won (or if it
 * was a tie), the final board state, the score summary, and a Play Again button.
 * Layout adapts between portrait (stacked) and landscape (side by side).
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
    onPlayAgain: () -> Unit,
) {
    val resultText = when {
        finalBoard.hasWon('X') -> "$player1Name${AppStrings.WINS_SUFFIX}"
        finalBoard.hasWon('O') -> "$player2Name${AppStrings.WINS_SUFFIX}"
        else -> AppStrings.TIE_RESULT
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
    ) {
        val isLandscape = maxWidth > maxHeight
        // Shrink elements in landscape so everything fits without scrolling
        val boardSize = if (isLandscape) 140.dp else 200.dp
        val cardWidth = if (isLandscape) 160.dp else 200.dp

        if (isLandscape) {
            // Landscape: headline on top, then board + score side by side, then button
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                ResultHeadline(resultText, compact = true)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FinalBoardDisplay(board = finalBoard, size = boardSize)
                    ScoreCard(
                        player1Name = player1Name,
                        player1Wins = player1Wins,
                        player2Name = player2Name,
                        player2Wins = player2Wins,
                        ties = ties,
                        width = cardWidth,
                    )
                }
                PlayAgainButton(onClick = onPlayAgain)
            }
        } else {
            // Portrait: everything stacked vertically
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                ResultHeadline(resultText, compact = false)
                FinalBoardDisplay(board = finalBoard, size = boardSize)
                ScoreCard(
                    player1Name = player1Name,
                    player1Wins = player1Wins,
                    player2Name = player2Name,
                    player2Wins = player2Wins,
                    ties = ties,
                    width = cardWidth,
                )
                PlayAgainButton(onClick = onPlayAgain)
            }
        }
    }
}

/**
 * Large headline showing the round result (e.g. "Inky wins!" or "It's a tie!").
 *
 * @param text    the result string to display
 * @param compact if true uses a smaller text style for landscape mode
 */
@Composable
private fun ResultHeadline(text: String, compact: Boolean) {
    Text(
        text = text,
        style = if (compact) {
            MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
        } else {
            MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
        },
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}

/**
 * A read-only display of the final board state.
 *
 * @param board the final board to render
 * @param size  the width and height of the board
 */
@Composable
private fun FinalBoardDisplay(board: Board, size: Dp) {
    Column(
        modifier = Modifier
            .size(size)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        for (r in 0..2) {
            if (r > 0) {
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                for (c in 0..2) {
                    if (c > 0) {
                        VerticalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val piece = board[r, c]
                        if (piece != ' ') {
                            Text(
                                text = piece.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                ),
                                color = MaterialTheme.colorScheme.primary,
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
 * @param width       the width of the card
 */
@Composable
private fun ScoreCard(
    player1Name: String,
    player1Wins: Int,
    player2Name: String,
    player2Wins: Int,
    ties: Int,
    width: Dp,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        ),
        modifier = Modifier.width(width),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = AppStrings.SCORE_HEADER,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ScorePill(label = player1Name, value = player1Wins)
            ScorePill(label = AppStrings.TIES_LABEL, value = ties)
            ScorePill(label = player2Name, value = player2Wins)
        }
    }
}

/**
 * A single score row showing a label and its count.
 *
 * @param label the player name or "Ties"
 * @param value the count to display
 */
@Composable
private fun ScorePill(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * The "Play Again" button.
 *
 * @param onClick called when the button is tapped
 */
@Composable
private fun PlayAgainButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .defaultMinSize(minWidth = 160.dp)
            .height(48.dp),
    ) {
        Text(
            text = AppStrings.PLAY_AGAIN_BUTTON,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

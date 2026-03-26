package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.tic_tac_toe.game.AIPlayer
import edu.moravian.csci215.tic_tac_toe.game.Board
import edu.moravian.csci215.tic_tac_toe.game.EasyAIPlayer
import edu.moravian.csci215.tic_tac_toe.game.HardAIPlayer
import edu.moravian.csci215.tic_tac_toe.game.HumanPlayer
import edu.moravian.csci215.tic_tac_toe.game.MediumAIPlayer
import edu.moravian.csci215.tic_tac_toe.game.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.ai_thinking_error
import tictactoe.composeapp.generated.resources.easy_ai
import tictactoe.composeapp.generated.resources.hard_ai
import tictactoe.composeapp.generated.resources.medium_ai
import tictactoe.composeapp.generated.resources.spot_taken_error

/**
 * Converts a player-type string (from navigation args) to a [Player] instance.
 * Note: these strings must match exactly what is stored in the string resources.
 */
private fun playerFromType(type: String, easyAi: String, mediumAi: String, hardAi: String): Player =
    when (type) {
        easyAi -> EasyAIPlayer()
        mediumAi -> MediumAIPlayer()
        hardAi -> HardAIPlayer()
        else -> HumanPlayer()
    }

/**
 * The game screen where the Tic-Tac-Toe match is played.
 *
 * Player 1 always plays 'X' (goes first); Player 2 always plays 'O'.
 * Human moves are handled by tapping a cell; AI moves happen automatically
 * after a short delay. The layout switches between portrait (indicator above
 * the board) and landscape (indicator to the side) automatically.
 *
 * @param paddingValues insets from the outer Scaffold
 * @param player1Type   type string for player 1 (Human / Easy AI / etc.)
 * @param player1Name   display name for player 1
 * @param player2Type   type string for player 2
 * @param player2Name   display name for player 2
 * @param player1Wins   cumulative wins for player 1 coming into this round
 * @param player2Wins   cumulative wins for player 2 coming into this round
 * @param ties          cumulative tied rounds coming into this round
 * @param showSnackbar  suspending callback to show error snackbars
 * @param onGameOver    called with updated (p1Wins, p2Wins, ties, board) when the round ends
 */
@Composable
fun GameScreen(
    paddingValues: PaddingValues,
    player1Type: String,
    player1Name: String,
    player2Type: String,
    player2Name: String,
    player1Wins: Int,
    player2Wins: Int,
    ties: Int,
    showSnackbar: suspend (String) -> Unit,
    onGameOver: (Int, Int, Int, Board) -> Unit,
) {
    val easyAi = stringResource(Res.string.easy_ai)
    val mediumAi = stringResource(Res.string.medium_ai)
    val hardAi = stringResource(Res.string.hard_ai)
    val spotTakenError = stringResource(Res.string.spot_taken_error)
    val aiThinkingError = stringResource(Res.string.ai_thinking_error)

    var board by remember { mutableStateOf(Board()) }
    var isAiThinking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Create player objects once; type strings don't change mid-game
    val player1 = remember { playerFromType(player1Type, easyAi, mediumAi, hardAi) }
    val player2 = remember { playerFromType(player2Type, easyAi, mediumAi, hardAi) }

    // Re-runs every time the board changes. Handles game-over and AI turns.
    LaunchedEffect(board) {
        if (board.isGameOver) {
            delay(600L) // let the player see the final state
            val newP1Wins = player1Wins + if (board.hasWon('X')) 1 else 0
            val newP2Wins = player2Wins + if (board.hasWon('O')) 1 else 0
            val newTies = ties + if (board.hasTied) 1 else 0
            onGameOver(newP1Wins, newP2Wins, newTies, board)
            return@LaunchedEffect
        }

        // Play AI move if it is an AI's turn
        val currentPlayer = if (board.turn == 'X') player1 else player2
        if (currentPlayer is AIPlayer) {
            isAiThinking = true
            delay(700L) // short pause so moves feel deliberate
            val (r, c) = currentPlayer.findMove(board, board.turn)
            board = board.playPiece(r, c) ?: board
            isAiThinking = false
        }
    }

    val onCellTapped: (Int, Int) -> Unit = { r, c ->
        when {
            isAiThinking -> {
                scope.launch { showSnackbar(aiThinkingError) }
            }

            (if (board.turn == 'X') player1 else player2) is AIPlayer -> {
                scope.launch { showSnackbar(aiThinkingError) }
            }

            else -> {
                val newBoard = board.playPiece(r, c)
                if (newBoard == null) {
                    scope.launch { showSnackbar(spotTakenError) }
                } else {
                    board = newBoard
                }
            }
        }
    }

    val currentName = if (board.turn == 'X') player1Name else player2Name

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
    ) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TurnIndicator(
                    name = currentName,
                    piece = board.turn,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                Box(
                    modifier = Modifier.weight(2f).fillMaxHeight().padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BoardGrid(
                        board = board,
                        onCellTapped = onCellTapped,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TurnIndicator(
                    name = currentName,
                    piece = board.turn,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                )
                BoardGrid(
                    board = board,
                    onCellTapped = onCellTapped,
                    modifier = Modifier.weight(1f).padding(16.dp),
                )
            }
        }
    }
}

/**
 * Displays whose turn it is in a descriptive format, e.g. "Inky, play your X".
 *
 * @param name     the current player's name
 * @param piece    the current player's piece ('X' or 'O')
 * @param modifier layout modifier applied to the container
 */
@Composable
private fun TurnIndicator(name: String, piece: Char, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "$name, play your $piece",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * Renders the 3×3 game board as a square grid of [BoardCell]s separated by
 * colored divider lines.
 *
 * @param board        the current board state
 * @param onCellTapped called with (row, col) when the player taps a cell
 * @param modifier     layout modifier applied to the grid container
 */
@Composable
private fun BoardGrid(
    board: Board,
    onCellTapped: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.aspectRatio(1f)) {
        for (r in 0..2) {
            if (r > 0) {
                HorizontalDivider(
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                for (c in 0..2) {
                    if (c > 0) {
                        VerticalDivider(
                            thickness = 3.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        )
                    }
                    BoardCell(
                        piece = board[r, c],
                        onClick = { onCellTapped(r, c) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        }
    }
}

/**
 * A single cell in the board grid. Displays the piece ('X' or 'O') if one has
 * been played, or appears blank to blend into the background when empty.
 *
 * @param piece    the piece at this cell: 'X', 'O', or ' ' (empty)
 * @param onClick  called when the user taps this cell
 * @param modifier layout modifier
 */
@Composable
private fun BoardCell(
    piece: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        if (piece != ' ') {
            Text(
                text = piece.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                ),
            )
        }
    }
}

package edu.moravian.csci215.tic_tac_toe

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.moravian.csci215.tic_tac_toe.game.Board
import edu.moravian.csci215.tic_tac_toe.game.Board.Companion.toStringRepresentation
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.app_title
import tictactoe.composeapp.generated.resources.back

/** Route for the welcome screen. */
@Serializable
object WelcomeRoute

/**
 * Route for the game screen, carrying all player info and cumulative scores.
 *
 * @param p1Type player 1 type string
 * @param p1Name player 1 display name
 * @param p2Type player 2 type string
 * @param p2Name player 2 display name
 * @param p1Wins cumulative wins for player 1
 * @param p2Wins cumulative wins for player 2
 * @param ties   cumulative ties
 */
@Serializable
data class GameRoute(
    val p1Type: String,
    val p1Name: String,
    val p2Type: String,
    val p2Name: String,
    val p1Wins: Int = 0,
    val p2Wins: Int = 0,
    val ties: Int = 0,
)

/**
 * Route for the game-over screen, carrying all player info, scores, and the
 * serialized final board state.
 *
 * @param p1Type   player 1 type string
 * @param p1Name   player 1 display name
 * @param p2Type   player 2 type string
 * @param p2Name   player 2 display name
 * @param p1Wins   cumulative wins for player 1
 * @param p2Wins   cumulative wins for player 2
 * @param ties     cumulative ties
 * @param boardStr serialized board string from [Board.toStringRepresentation]
 */
@Serializable
data class GameOverRoute(
    val p1Type: String,
    val p1Name: String,
    val p2Type: String,
    val p2Name: String,
    val p1Wins: Int,
    val p2Wins: Int,
    val ties: Int,
    val boardStr: String,
)

/**
 * Root composable for the app. Sets up the theme, shared Scaffold, and the
 * navigation graph. The Scaffold is intentionally placed *outside* the NavHost
 * so that the snackbar and optional top bar persist across navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    // Show the back-arrow top bar on every screen except the Welcome screen
    val showTopBar = currentDestination?.route != WelcomeRoute::class.qualifiedName

    AppTheme {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = { Text(stringResource(Res.string.app_title)) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                // OS-specific Material back arrow (fonts.google.com/icons)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(Res.string.back),
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            },
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = WelcomeRoute,
            ) {
                composable<WelcomeRoute> {
                    WelcomeScreen(
                        paddingValues = paddingValues,
                        onStartGame = { p1Type, p1Name, p2Type, p2Name ->
                            navController.navigate(
                                GameRoute(p1Type, p1Name, p2Type, p2Name),
                            )
                        },
                        showSnackbar = { message -> snackbarHostState.showSnackbar(message) },
                    )
                }

                composable<GameRoute> { backStack ->
                    val route = backStack.toRoute<GameRoute>()
                    GameScreen(
                        paddingValues = paddingValues,
                        player1Type = route.p1Type,
                        player1Name = route.p1Name,
                        player2Type = route.p2Type,
                        player2Name = route.p2Name,
                        player1Wins = route.p1Wins,
                        player2Wins = route.p2Wins,
                        ties = route.ties,
                        showSnackbar = { message -> snackbarHostState.showSnackbar(message) },
                        onGameOver = { newP1Wins, newP2Wins, newTies, finalBoard ->
                            navController.navigate(
                                GameOverRoute(
                                    p1Type = route.p1Type,
                                    p1Name = route.p1Name,
                                    p2Type = route.p2Type,
                                    p2Name = route.p2Name,
                                    p1Wins = newP1Wins,
                                    p2Wins = newP2Wins,
                                    ties = newTies,
                                    boardStr = finalBoard.toStringRepresentation(),
                                ),
                            )
                        },
                    )
                }

                // ── Game Over Screen ───────────────────────────────────────
                composable<GameOverRoute> { backStack ->
                    val route = backStack.toRoute<GameOverRoute>()
                    GameOverScreen(
                        paddingValues = paddingValues,
                        player1Type = route.p1Type,
                        player1Name = route.p1Name,
                        player2Type = route.p2Type,
                        player2Name = route.p2Name,
                        player1Wins = route.p1Wins,
                        player2Wins = route.p2Wins,
                        ties = route.ties,
                        finalBoard = Board.createFromString(route.boardStr),
                        onPlayAgain = {
                            navController.navigate(
                                GameRoute(
                                    p1Type = route.p1Type,
                                    p1Name = route.p1Name,
                                    p2Type = route.p2Type,
                                    p2Name = route.p2Name,
                                    p1Wins = route.p1Wins,
                                    p2Wins = route.p2Wins,
                                    ties = route.ties,
                                ),
                            ) {
                                // Pop game-over off the back stack so Back goes to Welcome
                                popUpTo<WelcomeRoute>()
                            }
                        },
                    )
                }
            }
        }
    }
}

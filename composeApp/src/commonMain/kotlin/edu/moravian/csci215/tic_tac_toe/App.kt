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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.moravian.csci215.tic_tac_toe.game.Board
import edu.moravian.csci215.tic_tac_toe.game.Board.Companion.toStringRepresentation

/** Navigation route constants for the three app screens. */
internal object Routes {
    const val WELCOME = "welcome"

    // Game route carries cumulative wins so scores persist across rounds.
    const val GAME =
        "game/{p1Type}/{p1Name}/{p2Type}/{p2Name}/{p1Wins}/{p2Wins}/{ties}"

    // Game-over route also carries the serialized board string for the final board display.
    const val GAME_OVER =
        "gameOver/{p1Type}/{p1Name}/{p2Type}/{p2Name}/{p1Wins}/{p2Wins}/{ties}/{boardStr}"

    /** Builds the game route (wins default to 0 for the first round). */
    fun game(
        p1Type: String,
        p1Name: String,
        p2Type: String,
        p2Name: String,
        p1Wins: Int = 0,
        p2Wins: Int = 0,
        ties: Int = 0,
    ) = "game/$p1Type/$p1Name/$p2Type/$p2Name/$p1Wins/$p2Wins/$ties"

    /** Builds the game-over route, encoding the board as a string. */
    fun gameOver(
        p1Type: String,
        p1Name: String,
        p2Type: String,
        p2Name: String,
        p1Wins: Int,
        p2Wins: Int,
        ties: Int,
        board: Board,
    ) = "gameOver/$p1Type/$p1Name/$p2Type/$p2Name/$p1Wins/$p2Wins/$ties/${board.toStringRepresentation()}"
}

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
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    // Show the back-arrow top bar on every screen except the Welcome screen
    val showTopBar = currentRoute != null && !currentRoute.startsWith(Routes.WELCOME)

    AppTheme {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = { Text(AppStrings.APP_TITLE) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                // OS-specific Material back arrow (fonts.google.com/icons)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = AppStrings.BACK,
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
                startDestination = Routes.WELCOME,
            ) {
                composable(Routes.WELCOME) {
                    WelcomeScreen(
                        paddingValues = paddingValues,
                        onStartGame = { p1Type, p1Name, p2Type, p2Name ->
                            navController.navigate(Routes.game(p1Type, p1Name, p2Type, p2Name))
                        },
                        showSnackbar = { message -> snackbarHostState.showSnackbar(message) },
                    )
                }

                composable(
                    route = Routes.GAME,
                    arguments = listOf(
                        navArgument("p1Type") { type = NavType.StringType },
                        navArgument("p1Name") { type = NavType.StringType },
                        navArgument("p2Type") { type = NavType.StringType },
                        navArgument("p2Name") { type = NavType.StringType },
                        navArgument("p1Wins") { type = NavType.IntType },
                        navArgument("p2Wins") { type = NavType.IntType },
                        navArgument("ties") { type = NavType.IntType },
                    ),
                ) { backStack ->
                    val p1Type = backStack.arguments?.getString("p1Type") ?: AppStrings.HUMAN
                    val p1Name = backStack.arguments?.getString("p1Name") ?: ""
                    val p2Type = backStack.arguments?.getString("p2Type") ?: AppStrings.HUMAN
                    val p2Name = backStack.arguments?.getString("p2Name") ?: ""
                    val p1Wins = backStack.arguments?.getInt("p1Wins") ?: 0
                    val p2Wins = backStack.arguments?.getInt("p2Wins") ?: 0
                    val ties = backStack.arguments?.getInt("ties") ?: 0

                    GameScreen(
                        paddingValues = paddingValues,
                        player1Type = p1Type,
                        player1Name = p1Name,
                        player2Type = p2Type,
                        player2Name = p2Name,
                        player1Wins = p1Wins,
                        player2Wins = p2Wins,
                        ties = ties,
                        showSnackbar = { message -> snackbarHostState.showSnackbar(message) },
                        onGameOver = { newP1Wins, newP2Wins, newTies, finalBoard ->
                            navController.navigate(
                                Routes.gameOver(
                                    p1Type,
                                    p1Name,
                                    p2Type,
                                    p2Name,
                                    newP1Wins,
                                    newP2Wins,
                                    newTies,
                                    finalBoard,
                                ),
                            )
                        },
                    )
                }
                composable(
                    route = Routes.GAME_OVER,
                    arguments = listOf(
                        navArgument("p1Type") { type = NavType.StringType },
                        navArgument("p1Name") { type = NavType.StringType },
                        navArgument("p2Type") { type = NavType.StringType },
                        navArgument("p2Name") { type = NavType.StringType },
                        navArgument("p1Wins") { type = NavType.IntType },
                        navArgument("p2Wins") { type = NavType.IntType },
                        navArgument("ties") { type = NavType.IntType },
                        navArgument("boardStr") { type = NavType.StringType },
                    ),
                ) { backStack ->
                    val p1Type = backStack.arguments?.getString("p1Type") ?: AppStrings.HUMAN
                    val p1Name = backStack.arguments?.getString("p1Name") ?: ""
                    val p2Type = backStack.arguments?.getString("p2Type") ?: AppStrings.HUMAN
                    val p2Name = backStack.arguments?.getString("p2Name") ?: ""
                    val p1Wins = backStack.arguments?.getInt("p1Wins") ?: 0
                    val p2Wins = backStack.arguments?.getInt("p2Wins") ?: 0
                    val ties = backStack.arguments?.getInt("ties") ?: 0
                    val boardStr = backStack.arguments?.getString("boardStr") ?: ""
                    // Reconstruct the Board from its string representation
                    val finalBoard = Board.createFromString(boardStr)

                    GameOverScreen(
                        paddingValues = paddingValues,
                        player1Type = p1Type,
                        player1Name = p1Name,
                        player2Type = p2Type,
                        player2Name = p2Name,
                        player1Wins = p1Wins,
                        player2Wins = p2Wins,
                        ties = ties,
                        finalBoard = finalBoard,
                        onPlayAgain = {
                            navController.navigate(
                                Routes.game(p1Type, p1Name, p2Type, p2Name, p1Wins, p2Wins, ties),
                            ) {
                                // Pop game-over off the back stack so Back goes to Welcome
                                popUpTo(Routes.WELCOME)
                            }
                        },
                    )
                }
            }
        }
    }
}

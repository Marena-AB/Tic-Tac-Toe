
package edu.moravian.csci215.tic_tac_toe

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


private val AppPrimary   = Color(0xFF6B3FA0) // deep purple
private val AppOnPrimary = Color(0xFFFFFFFF)
private val AppSurface   = Color(0xFFF3EAF9)
private val AppBackground = Color(0xFFEDE0F5)

private val TicTacToeColorScheme = lightColorScheme(
    primary       = AppPrimary,
    onPrimary     = AppOnPrimary,
    surface       = AppSurface,
    background    = AppBackground,
    onSurface     = AppPrimary,
    onBackground  = AppPrimary,
)


/** Navigation route constants for the three app screens. */
internal object Routes {
    const val WELCOME = "welcome"

    // The game route carries the four player parameters as path segments.
    const val GAME = "game/{p1Type}/{p1Name}/{p2Type}/{p2Name}"

    const val GAME_OVER = "gameOver/{p1Type}/{p1Name}/{p2Type}/{p2Name}/{p1Wins}/{p2Wins}/{ties}"

    /** Builds the fully-qualified game route string. */
    fun game(p1Type: String, p1Name: String, p2Type: String, p2Name: String) =
        "game/$p1Type/$p1Name/$p2Type/$p2Name"

    /** Builds the fully-qualified game-over route string. */
    fun gameOver(
        p1Type: String, p1Name: String,
        p2Type: String, p2Name: String,
        p1Wins: Int, p2Wins: Int, ties: Int
    ) = "gameOver/$p1Type/$p1Name/$p2Type/$p2Name/$p1Wins/$p2Wins/$ties"
}


/**
 * Wraps content in the app's custom MaterialTheme.
 * @param content the content to display
 */
@Composable
fun TicTacToeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TicTacToeColorScheme, content = content)
}


/**
 * Root composable for the app. Sets up the theme, shared scaffold, and the
 * navigation graph. The Scaffold is intentionally placed *outside* the NavHost
 * so that the snackbar and (optional) top bar persist across navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController       = rememberNavController()
    val snackbarHostState   = remember { SnackbarHostState() }
    val currentEntry        = navController.currentBackStackEntryAsState().value
    val currentRoute        = currentEntry?.destination?.route

    // Show a back-arrow top bar on every screen except Welcome.
    val showTopBar = currentRoute != null && !currentRoute.startsWith(Routes.WELCOME)

    TicTacToeTheme {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = { Text(AppStrings.APP_TITLE) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                // Uses the OS-specific Material arrow-back icon (see fonts.google.com/icons)
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = AppStrings.BACK
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController    = navController,
                startDestination = Routes.WELCOME
            ) {
                composable(Routes.WELCOME) {
                    WelcomeScreen(
                        paddingValues = paddingValues,
                        onStartGame   = { p1Type, p1Name, p2Type, p2Name ->
                            navController.navigate(Routes.game(p1Type, p1Name, p2Type, p2Name))
                        },
                        showSnackbar  = { message -> snackbarHostState.showSnackbar(message) }
                    )
                }

                composable(
                    route     = Routes.GAME,
                    arguments = listOf(
                        navArgument("p1Type") { type = NavType.StringType },
                        navArgument("p1Name") { type = NavType.StringType },
                        navArgument("p2Type") { type = NavType.StringType },
                        navArgument("p2Name") { type = NavType.StringType },
                    )
                ) { backStack ->
                    val p1Type = backStack.arguments?.getString("p1Type") ?: AppStrings.HUMAN
                    val p1Name = backStack.arguments?.getString("p1Name") ?: ""
                    val p2Type = backStack.arguments?.getString("p2Type") ?: AppStrings.HUMAN
                    val p2Name = backStack.arguments?.getString("p2Name") ?: ""

                    GameScreen(
                        paddingValues = paddingValues,
                        player1Type   = p1Type,
                        player1Name   = p1Name,
                        player2Type   = p2Type,
                        player2Name   = p2Name,
                        showSnackbar  = { message -> snackbarHostState.showSnackbar(message) },
                        onGameOver    = { p1Wins, p2Wins, ties ->
                            navController.navigate(
                                Routes.gameOver(p1Type, p1Name, p2Type, p2Name, p1Wins, p2Wins, ties)
                            )
                        }
                    )
                }

                composable(
                    route     = Routes.GAME_OVER,
                    arguments = listOf(
                        navArgument("p1Type")  { type = NavType.StringType },
                        navArgument("p1Name")  { type = NavType.StringType },
                        navArgument("p2Type")  { type = NavType.StringType },
                        navArgument("p2Name")  { type = NavType.StringType },
                        navArgument("p1Wins")  { type = NavType.IntType },
                        navArgument("p2Wins")  { type = NavType.IntType },
                        navArgument("ties")    { type = NavType.IntType },
                    )
                ) { backStack ->
                    val p1Type  = backStack.arguments?.getString("p1Type")  ?: AppStrings.HUMAN
                    val p1Name  = backStack.arguments?.getString("p1Name")  ?: ""
                    val p2Type  = backStack.arguments?.getString("p2Type")  ?: AppStrings.HUMAN
                    val p2Name  = backStack.arguments?.getString("p2Name")  ?: ""
                    val p1Wins  = backStack.arguments?.getInt("p1Wins")     ?: 0
                    val p2Wins  = backStack.arguments?.getInt("p2Wins")     ?: 0
                    val ties    = backStack.arguments?.getInt("ties")       ?: 0

                    GameOverScreen(
                        paddingValues = paddingValues,
                        player1Type   = p1Type,
                        player1Name   = p1Name,
                        player2Type   = p2Type,
                        player2Name   = p2Name,
                        player1Wins   = p1Wins,
                        player2Wins   = p2Wins,
                        ties          = ties,
                        onPlayAgain   = {
                            navController.navigate(Routes.game(p1Type, p1Name, p2Type, p2Name)) {
                                // Pop the game-over entry so back goes to welcome, not a dead game
                                popUpTo(Routes.WELCOME)
                            }
                        }
                    )
                }
            }
        }
    }
}

package edu.moravian.csci215.tic_tac_toe

/**
 * Centralized string constants for all user-facing text in the app.
 * Using constants here satisfies the "no hard-coded strings" code-quality requirement
 * without requiring an Android-only string resource file.
 */
internal object AppStrings {
    const val APP_TITLE     = "Tic-Tac-Toe"
    const val BACK          = "Back"

    const val HUMAN         = "Human"
    const val EASY_AI       = "Easy AI"
    const val MEDIUM_AI     = "Medium AI"
    const val HARD_AI       = "Hard AI"

    const val WELCOME_TITLE     = "Welcome to Tic-Tac-Toe!"
    const val PLAYER_1_LABEL    = "Player 1"
    const val PLAYER_2_LABEL    = "Player 2"
    const val PLAYER_1_NAME     = "Player 1 Name"
    const val PLAYER_2_NAME     = "Player 2 Name"
    const val START_BUTTON      = "Start!"
    const val EMPTY_NAME_ERROR  = "Player names cannot be empty."

    const val PLAY_PROMPT           = "%s, play your %s"
    const val SPOT_TAKEN_ERROR      = "That spot is already taken!"
    const val AI_THINKING_ERROR     = "Wait for the AI to play."

    const val WINS_LABEL        = "%s wins"
    const val TIE_LABEL         = "It's a tie!"
    const val SCORE_LABEL       = "%s: %d  |  %s: %d  |  Ties: %d"
    const val PLAY_AGAIN_BUTTON = "Play Again"
}

/** Names to randomly assign to new players. */
internal val RANDOM_NAMES = listOf(
    "Kappa", "Inky", "Blinky", "Pinky", "Clyde",
    "Ace",   "Rex",  "Luna",   "Nova",  "Zen"
)
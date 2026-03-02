package edu.moravian.csci215.tic_tac_toe.game

import kotlinx.serialization.Serializable

/**
 * A tic-tac-toe player that gets its input from a human.
 * For humans this does nothing, the UI will deal with this.
 */
@Serializable
class HumanPlayer : Player

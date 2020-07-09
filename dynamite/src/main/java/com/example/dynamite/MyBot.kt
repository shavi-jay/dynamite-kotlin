package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move

class MyBot : Bot {
    override fun makeMove(gamestate: Gamestate): Move {
        // Are you debugging?
        // Put a breakpoint in this method to see when we make a move

        if (gamestate.rounds.size % 500 == 0 && gamestate.rounds.size > 0) {
            when (gamestate.rounds[gamestate.rounds.size-1].p1) {
                Move.P -> return Move.D
                Move.R -> return Move.D
                Move.S -> return Move.D
                Move.D -> return Move.D
            }
        } else {
            return Move.R
        }

        return Move.S
    }

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }
}
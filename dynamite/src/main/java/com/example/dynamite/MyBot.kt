package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move


class MyBot : Bot {
    private var dynamiteCount = 0
    override fun makeMove(gamestate: Gamestate): Move {

        val ranFloat = listOf(1,2,3)

        val ran = ranFloat.shuffled().first()

        val ranFloat2 = listOf(1,2,3,4,5,6,7,8)

        val ran2 = ranFloat2.shuffled().first()

        if (dynamiteCount < 100) {
            if (ran2 == 1) {
                dynamiteCount += 1
                return Move.D
            }
        }

        return when (ran) {
            1 -> Move.R
            2 -> Move.P
            3 -> Move.S
            else -> Move.W
        }

    }
}
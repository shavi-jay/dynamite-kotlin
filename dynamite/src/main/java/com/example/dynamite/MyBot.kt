package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move

class MyBot : Bot {
    private var moveMap = Array<Array<Array<Int>>>(5) {
        Array<Array<Int>>(5) {
            Array<Int>(5) { 1 }
        }
    }

    private var drawMap = Array<Array<Array<Int>>>(15) {
        Array<Array<Int>>(3) {
            Array<Int>(5) { 1 }
        }
    }

    private var react1Map = Array<Array<Array<Int>>>(5) {
        Array<Array<Int>>(5) {
            Array<Int>(5) { 1 }
        }
    }

    private var react2Map = Array<Array<Array<Int>>>(5) {
        Array<Array<Int>>(5) {
            Array<Int>(5) { 1 }
        }
    }

    private var yourDynamiteMove = 0
    private var myDynamiteMove = 0

    private var drawState = 0

    override fun makeMove(gamestate: Gamestate): Move {

        if (gamestate.rounds.isEmpty()){
            return randomMove()
        }

        if (gamestate.rounds.size > 0) {
            if (lastMoveP2(gamestate) == Move.D) {
                yourDynamiteMove += 1
            }
        }

        if (gamestate.rounds.size > 1) {
            updateMoveMap(gamestate)
            updateDrawMap(gamestate)
        }

        if (gamestate.rounds.size > 2) {
            updateP1ReactMap(gamestate)
            updateP2ReactMap(gamestate)
        }

        //If there is a draw
        if (drawCount(gamestate) > 1) {
            drawMoveMaker(moveReverser(randomArray(drawMoveArray(gamestate))),gamestate)
        }


        //Random Dynamite
        if (myDynamiteMove < 100) {
            when (gamestate.rounds.size) {
                in 1..100 -> {
                    var moveList = 1..25
                    if (moveList.shuffled().first() == 1) {
                        myDynamiteMove += 1
                        return Move.D
                    }
                }
                in 101..1000 -> {
                    var moveList = 1..20
                    if (moveList.shuffled().first() == 1) {
                        myDynamiteMove += 1
                        return Move.D
                    }
                }
                in 1001..1500 -> {
                    var moveList = 1..15
                    if (moveList.shuffled().first() == 1) {
                        myDynamiteMove += 1
                        return Move.D
                    }
                }
                in 1501..2000 -> {
                    var moveList = 1..7
                    if (moveList.shuffled().first() == 1) {
                        myDynamiteMove += 1
                        return Move.D
                    }
                }
            }
        }



        return if(gamestate.rounds.size > 10) {
            var moveArray = moveMapProbabilityArray(gamestate)
            var react1Array = reactP1MapProbabilityArray(gamestate)
            var react2Array = reactP2MapProbabilityArray(gamestate)
            moveWinner(moveReverser(randomArray(moveArray + react1Array + react2Array)))
        } else {
            randomMove()
        }
    }

    //General Move Map

    private fun updateMoveMap(gamestate: Gamestate) {
        val gameDuration = gamestate.rounds.size
        val myMoveBefore = moveConverter(gamestate.rounds[gameDuration - 2].p1)
        val yourMoveBefore = moveConverter(gamestate.rounds[gameDuration - 2].p2)
        val yourMoveAfter = moveConverter(gamestate.rounds[gameDuration - 1].p2)
        moveMap[myMoveBefore][yourMoveBefore][yourMoveAfter] += 5
    }

    private fun moveMapProbabilityArray(gamestate: Gamestate): Array<Int> {
        val myLastMove = moveConverter(lastMoveP1(gamestate))
        val yourLastMove = moveConverter(lastMoveP2(gamestate))
        var probArray = Array<Int>(5){0}
        for (i in 0..4) {
            val arrayFill = (moveMap[myLastMove][yourLastMove][i])
            probArray[i] = arrayFill*arrayFill
            if (i == 3 && yourDynamiteMove == 100) { //if opponent's dynamite moves have been exceeded
                probArray[i] = 1
            }
        }
        return probArray
    }

    //P1 React Move Map

    private fun updateP1ReactMap(gamestate: Gamestate) {
        val gameDuration = gamestate.rounds.size
        val myTwoMovesBefore = moveConverter(gamestate.rounds[gameDuration - 3].p1)
        val myMoveBefore = moveConverter(gamestate.rounds[gameDuration - 2].p1)
        val yourMoveAfter = moveConverter(gamestate.rounds[gameDuration - 1].p2)
        react1Map[myTwoMovesBefore][myMoveBefore][yourMoveAfter] += 5
    }

    private fun reactP1MapProbabilityArray(gamestate: Gamestate): Array<Int> {
        val gameDuration = gamestate.rounds.size
        val myLastTwoMove = moveConverter(gamestate.rounds[gameDuration - 2].p1)
        val myLastMove = moveConverter(gamestate.rounds[gameDuration - 1].p1)
        var probArray = Array<Int>(5){0}
        for (i in 0..4) {
            var arrayFill = (react1Map[myLastTwoMove][myLastMove][i])
            probArray[i] = arrayFill*arrayFill
            if (i == 3 && yourDynamiteMove == 100) { //if opponent's dynamite moves have been exceeded
                probArray[i] = 1
            }
        }
        return probArray
    }

    //P2 React Move Map

    private fun updateP2ReactMap(gamestate: Gamestate) {
        val gameDuration = gamestate.rounds.size
        val yourTwoMovesBefore = moveConverter(gamestate.rounds[gameDuration - 3].p2)
        val yourMoveBefore = moveConverter(gamestate.rounds[gameDuration - 2].p2)
        val yourMoveAfter = moveConverter(gamestate.rounds[gameDuration - 1].p2)
        react2Map[yourTwoMovesBefore][yourMoveBefore][yourMoveAfter] += 5
    }

    private fun reactP2MapProbabilityArray(gamestate: Gamestate): Array<Int> {
        val gameDuration = gamestate.rounds.size
        val yourLastTwoMove = moveConverter(gamestate.rounds[gameDuration - 2].p2)
        val yourLastMove = moveConverter(gamestate.rounds[gameDuration - 1].p2)
        var probArray = Array<Int>(5){0}
        for (i in 0..4) {
            var arrayFill = (react2Map[yourLastTwoMove][yourLastMove][i])
            probArray[i] = arrayFill*arrayFill
            if (i == 3 && yourDynamiteMove == 100) { //if opponent's dynamite moves have been exceeded
                probArray[i] = 1
            }
        }
        return probArray
    }

    //Winning Move

    private fun moveWinner(move: Move): Move {
        return when (move) {
            Move.R -> Move.P
            Move.P -> Move.S
            Move.S -> Move.R
            Move.D -> if (yourDynamiteMove < 100) {
                Move.W
            } else {
                randomMove()
            }
            Move.W -> randomMove()
        }
    }

    //Draw Behaviour

    private fun drawCount(gamestate: Gamestate): Int {
        val gameLength = gamestate.rounds.size
        var counter = 0
        var isDraw = true
        if (gameLength == 0) {
            return 0
        } else {
            while (isDraw && counter < (gameLength - 1)) {
                isDraw = (gamestate.rounds[gameLength - 1 - counter].p1 == gamestate.rounds[gameLength - 1 - counter].p2)
                if (isDraw) {
                    counter += 1
                }
            }
        }
        return counter
    }

    private fun drawConverter(move: Move): Int {
        return when (move) {
            Move.R -> 0
            Move.P -> 0
            Move.S -> 0
            Move.D -> 1
            Move.W -> 2
        }
    }

    private fun updateDrawMap(gamestate: Gamestate) {
        var twoMovesAgo = gamestate.rounds[gamestate.rounds.size - 2].p1
        if (drawState > 0) {
            drawMap[drawState][drawConverter(twoMovesAgo)][moveConverter(lastMoveP1(gamestate))] += 5
        }
        drawState = drawCount(gamestate)
    }

    private fun drawMoveArray(gamestate: Gamestate): Array<Int> {
        val myLastMove = drawConverter(lastMoveP1(gamestate))
        var probArray = Array<Int>(5){0}
        for (i in 0..4) {
            probArray[i] = (drawMap[drawCount(gamestate)][myLastMove][i])
            if (i == 3 && yourDynamiteMove == 100) { //if opponent's dynamite moves have been exceeded
                probArray[i] = 1
            }
        }
        return probArray
    }

    private fun drawMoveMaker(move: Move, gamestate: Gamestate): Move {
        return when (move) {
            Move.R -> {dynamiteResponseRPS(gamestate,Move.R)}
            Move.P -> {dynamiteResponseRPS(gamestate,Move.P)}
            Move.S -> {dynamiteResponseRPS(gamestate,Move.S)}
            Move.D -> {dynamiteResponseD(gamestate)}
            Move.W -> randomMove()
        }
    }


    private fun dynamiteResponseRPS(gamestate: Gamestate, move: Move): Move {
        var moveList = 1..(maxOf(12 - 2*drawCount(gamestate),3))
        return when (moveList.shuffled().first()) {
            in 1..2 -> {
                if (myDynamiteMove < 100) {
                    myDynamiteMove += 1
                    Move.D
                } else {
                    moveWinner(move)
                }
            }
            else -> moveWinner(move)
        }
    }

    private fun dynamiteResponseD(gamestate: Gamestate): Move {
        return if (yourDynamiteMove != 100) { //If there are still opponent's dynamites left
            var moveList = 1..4
            when (moveList.shuffled().first()) {
                1 -> {
                    if (myDynamiteMove < 100) {
                        myDynamiteMove += 1
                        Move.D
                    } else {
                        Move.W
                    }
                }
                else -> randomMove()
            }
        } else { //If there are no dynamites left
            var moveList = 1..(maxOf(12 - 2 * drawCount(gamestate), 2))
            when (moveList.shuffled().first()) {
                1 -> {
                    if (myDynamiteMove < 100) {
                        myDynamiteMove += 1
                        Move.D
                    } else {
                        randomMove()
                    }
                }
                else -> randomMove()
            }
        }
    }

    //Helpful functions

    private fun lastMoveP1(gamestate: Gamestate): Move{
        return gamestate.rounds[gamestate.rounds.size -1].p1
    }

    private fun lastMoveP2(gamestate: Gamestate): Move{
        return gamestate.rounds[gamestate.rounds.size -1].p2
    }

    private fun moveConverter(move: Move): Int {
        return when (move) {
            Move.R -> 0
            Move.P -> 1
            Move.S -> 2
            Move.D -> 3
            Move.W -> 4
        }
    }

    private fun moveReverser(number: Int): Move {
        return when (number) {
            0 -> Move.R
            1 -> Move.P
            2 -> Move.S
            3 -> Move.D
            4 -> Move.W
            else -> Move.R
        }
    }

    //Randomiser Functions

    private fun randomMove(): Move {
        var moveList = listOf(0,1,2)
        return moveReverser(moveList.shuffled().first())
    }

    private fun randomArray(array: Array<Int>): Int{
        val arrayLength = array.size
        var cumulativeSum = Array<Int>(arrayLength){array[0]}
        for (i in 1 until (arrayLength)) {
            cumulativeSum[i] = array[i] + cumulativeSum[i - 1]

        }
        var randomList = 1..cumulativeSum[arrayLength-1]
        val randomNumber = randomList.shuffled().first()
        var outputNumber = 0
        for (i in 1 until (arrayLength-1)){
            if (randomNumber > cumulativeSum[i-1]){
                outputNumber = i
            }
        }
        return outputNumber
    }

}
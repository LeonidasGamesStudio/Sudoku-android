package com.example.sudoku.board.gridGeneration

import com.example.sudoku.board.NumberEntryArrayCopier
import com.example.sudoku.board.SudokuBoardView
import java.util.Random as rand

class GridJumbler (private val sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, private val size: Int){
    fun jumble() {
        rotatePuzzle(sudokuNumbers, size)
        shiftPuzzle(sudokuNumbers, size)
        recodePuzzle(sudokuNumbers, size)
        return
    }

    private fun rotatePuzzle(sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        when (rand().nextInt(3)) {
            0 -> return
            1 -> rotate90(sudokuNumbers, size)
            else -> rotate270(sudokuNumbers, size)
        }
        return
    }

    private fun rotate90(sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        val arrayCopier = NumberEntryArrayCopier(sudokuNumbers, size)
        val originalGrid = arrayCopier.copyArray()
        for (i in 0 until size) {
            for (j in 0 until size) {
                sudokuNumbers[j][i + ((((size - 1) / 2) - i) * 2)].changeNum(originalGrid[i][j].getNum(), true)
                sudokuNumbers[j][i + ((((size - 1) / 2) - i) * 2)].changeType(originalGrid[i][j].getType(), true)
            }
        }
        return
    }

    private fun rotate270(sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        val arrayCopier = NumberEntryArrayCopier(sudokuNumbers, size)
        val originalGrid = arrayCopier.copyArray()
        for (x in 0 until size) {
            for (y in 0 until size) {
                val distanceFromOrigin = y - 4
                val newDistance = -distanceFromOrigin
                val yCoord = newDistance + 4
                sudokuNumbers[yCoord][x].changeNum(originalGrid[x][y].getNum(), true)
                sudokuNumbers[yCoord][x].changeType(originalGrid[x][y].getType(), true)

            }
        }
        return
    }

    private fun shiftPuzzle(sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        val arrayCopier = NumberEntryArrayCopier(sudokuNumbers, size)
        val originalGrid = arrayCopier.copyArray()
        //do rows
        //shift 0, 1, 2 times
        shiftRows(originalGrid, sudokuNumbers, size)

        val secondArrayCopier = NumberEntryArrayCopier(sudokuNumbers, size)
        val secondOriginalGrid = secondArrayCopier.copyArray()
        //do columns
        shiftColumns(secondOriginalGrid, sudokuNumbers, size)
        //shiftColumns
        return
    }

    private fun shiftRows(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        val shiftAmount = rand().nextInt(3)          //for top and bottom squares
        shiftR(originalGrid, sudokuNumbers, 0, 2, shiftAmount, size)
        shiftR(originalGrid, sudokuNumbers, 6, 8, -shiftAmount, size)

        val toSwapOrNotToSwap = rand().nextInt(2)
        if (toSwapOrNotToSwap == 1){
            swapTopAndBottomOfCentre(originalGrid, sudokuNumbers, size)
        }
    }

    private fun swapTopAndBottomOfCentre(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>,
                                         size: Int){
        for (j in 0 until size){
            sudokuNumbers[3][j].changeNum(originalGrid[5][j].getNum(), true)
            sudokuNumbers[3][j].changeType(originalGrid[5][j].getType(), true)
            sudokuNumbers[5][j].changeNum(originalGrid[3][j].getNum(), true)
            sudokuNumbers[5][j].changeType(originalGrid[3][j].getType(), true)
        }
    }

    private fun shiftR(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>,
                       lowerBound: Int, upperBound: Int, shiftDistance: Int, size: Int){
        for (i in lowerBound..upperBound){
            var nextPosition = i + shiftDistance
            if (nextPosition > upperBound){
                nextPosition -= 3
            }else if(nextPosition < lowerBound){
                nextPosition += 3
            }
            for (j in 0 until size){
                sudokuNumbers[nextPosition][j].changeNum(originalGrid[i][j].getNum(), true)
                sudokuNumbers[nextPosition][j].changeType(originalGrid[i][j].getType(), true)
            }
        }
    }

    private fun shiftColumns(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int){
        val shiftAmount = rand().nextInt(3)          //for top and bottom squares
        shiftC(originalGrid, sudokuNumbers, 0, 2, shiftAmount, size)
        shiftC(originalGrid, sudokuNumbers, 6, 8, -shiftAmount, size)

        val toSwapOrNotToSwap = rand().nextInt(2)
        if (toSwapOrNotToSwap == 1){
            swapLeftAndRightOfCentre(originalGrid, sudokuNumbers, size)
        }
    }

    private fun swapLeftAndRightOfCentre(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>,
                                         size: Int){
        for (i in 0 until size){
            sudokuNumbers[i][3].changeNum(originalGrid[i][5].getNum(), true)
            sudokuNumbers[i][3].changeType(originalGrid[i][5].getType(), true)
            sudokuNumbers[i][5].changeNum(originalGrid[i][3].getNum(), true)
            sudokuNumbers[i][5].changeType(originalGrid[i][3].getType(), true)
        }
    }

    private fun shiftC(originalGrid: Array<Array<SudokuBoardView.NumberEntry>>, sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>,
                       lowerBound: Int, upperBound: Int, shiftDistance: Int, size: Int){
        for (j in lowerBound..upperBound){
            var nextPosition = j + shiftDistance
            if (nextPosition > upperBound){
                nextPosition -= 3
            }else if(nextPosition < lowerBound){
                nextPosition += 3
            }
            for (i in 0 until size){
                sudokuNumbers[i][nextPosition].changeNum(originalGrid[i][j].getNum(), true)
                sudokuNumbers[i][nextPosition].changeType(originalGrid[i][j].getType(), true)
            }
        }
    }

    private fun recodePuzzle(sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, size: Int) {
        val map = getMap(size)
        for (i in 0 until size){
            for (j in 0 until size){
                map[sudokuNumbers[i][j].getNum()]?.let { sudokuNumbers[i][j].changeNum(it, true) }
            }
        }
        return
    }

    private fun getMap(size: Int): Map<Int, Int>{
        val listOfNums = (1..size).toMutableList()
        val listOfKeys = (1..size).toMutableList()
        val map = mutableMapOf<Int, Int>()
        for (i in 0 until size) {
            val numsIndex = rand().nextInt((listOfNums.size))
            val keysIndex = rand().nextInt((listOfKeys.size))
            val num = listOfNums[numsIndex]
            val key = listOfKeys[keysIndex]
            listOfNums.removeAt(numsIndex)
            listOfKeys.removeAt(keysIndex)
            map[key] = num
        }
        return map
    }
}
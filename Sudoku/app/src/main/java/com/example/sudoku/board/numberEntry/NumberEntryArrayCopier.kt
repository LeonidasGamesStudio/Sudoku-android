package com.example.sudoku.board.numberEntry
import com.example.sudoku.board.numberEntry.NumberEntry

class NumberEntryArrayCopier (private val sudokuNumbers: Array<Array<NumberEntry>>, private val size: Int){
    fun copyArray(): Array<Array<NumberEntry>> {
        val newArray = Array(9) {Array(9) { NumberEntry(0, 0) } }
        for(i in 0 until size){
            for(j in 0 until size) {
                newArray[i][j].changeNum(sudokuNumbers[i][j].getNum(), false)
                newArray[i][j].changeType(sudokuNumbers[i][j].getType(), false)
            }
        }
        return newArray
    }
}
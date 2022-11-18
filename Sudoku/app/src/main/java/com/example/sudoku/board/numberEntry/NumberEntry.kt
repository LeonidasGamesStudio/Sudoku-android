package com.example.sudoku.board.numberEntry

import com.example.sudoku.board.values.TYPE_START
import com.example.sudoku.board.values.TYPE_START_CONFLICT

class NumberEntry(private var number: Int, private var type: Int){
    //type: 1 = normal, 3 = conflict, 2 = start, 0 = unfilled/pencilled
    private var pencilNumbers = BooleanArray(10) {false}
    fun changeNum(newNum: Int, override: Boolean){
        if ((type != TYPE_START && type != TYPE_START_CONFLICT) || override) {
            number = newNum
        }
    }

    fun changeType(newType: Int, override: Boolean){
        if ((type != TYPE_START && type != TYPE_START_CONFLICT) || override){
            type = newType
        }
    }

    fun getNum(): Int {
        return number
    }

    fun getType(): Int {
        return type
    }

    fun setPencil(number: Int){
        pencilNumbers[number] = !pencilNumbers[number]
    }

    fun getPencil(number: Int): Boolean {
        return pencilNumbers[number]
    }

    fun clearPencilNumbers() {
        for (i in pencilNumbers.indices){
            pencilNumbers[i] = false
        }
    }
}
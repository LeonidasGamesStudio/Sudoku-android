package com.example.sudoku.board.values

import com.example.sudoku.board.numberEntry.NumberEntry

class GridModel(size: Int) {
    class UndoStackEntry(val numberEntry: NumberEntry, val posX: Int, val posY: Int)

    val sudokuNumbers = Array(size) { Array(size) { NumberEntry(0,0) } }
    var undoStack = ArrayDeque<UndoStackEntry>()
    var pencil = false
}
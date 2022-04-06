package com.example.sudoku.board

class SudokuPresets {
    fun returnPreset(puzzleNum: Int): Array<IntArray> {
        when (puzzleNum) {
            1 -> {
                return arrayOf(
                    intArrayOf(8, 0, 0, 7, 3, 9, 0, 0, 6),
                    intArrayOf(3, 7, 0, 4, 6, 5, 0, 0, 0),
                    intArrayOf(0, 4, 0, 1, 8, 2, 0, 0, 9),
                    intArrayOf(0, 0, 0, 6, 0, 0, 0, 4, 0),
                    intArrayOf(0, 5, 4, 3, 0, 0, 6, 1, 0),
                    intArrayOf(0, 6, 0, 5, 0, 0, 0, 0, 0),
                    intArrayOf(4, 0, 0, 8, 5, 3, 0, 7, 0),
                    intArrayOf(0, 0, 0, 2, 7, 1, 0, 6, 4),
                    intArrayOf(1, 0, 0, 9, 4, 0, 0, 0, 2)
                )
            }
            2 -> {
                return arrayOf(
                    intArrayOf(1, 0, 0, 7, 3, 4, 0, 0, 6),
                    intArrayOf(3, 7, 0, 4, 6, 5, 0, 0, 0),
                    intArrayOf(0, 4, 0, 1, 8, 2, 0, 0, 9),
                    intArrayOf(0, 0, 0, 6, 0, 0, 0, 4, 0),
                    intArrayOf(0, 5, 4, 3, 0, 0, 6, 1, 0),
                    intArrayOf(0, 6, 0, 5, 0, 0, 0, 0, 0),
                    intArrayOf(4, 0, 0, 8, 5, 3, 0, 7, 0),
                    intArrayOf(0, 0, 0, 2, 9, 7, 0, 6, 4),
                    intArrayOf(1, 0, 0, 9, 4, 0, 0, 0, 3)
                )
            }
            3 -> {
                return arrayOf(
                    intArrayOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
                    intArrayOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
                    intArrayOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
                    intArrayOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
                    intArrayOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
                    intArrayOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
                    intArrayOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
                    intArrayOf(2, 8, 7, 4, 1, 9, 6, 3, 5),
                    intArrayOf(3, 4, 5, 2, 8, 6, 1, 7, 0)
                )
            }
            else -> return Array(9) { IntArray(9) { 0 } }
        }
    }
}
package com.example.sudoku

import com.example.sudoku.board.PuzzleJumbler
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun testRotation() {
        assertEquals(arrayOf(
            intArrayOf(8, 0, 0, 7, 3, 9, 0, 0, 6),
            intArrayOf(3, 7, 0, 4, 6, 5, 0, 0, 0),
            intArrayOf(0, 4, 0, 1, 8, 2, 0, 0, 9),
            intArrayOf(0, 0, 0, 6, 0, 0, 0, 4, 0),
            intArrayOf(0, 5, 4, 3, 0, 0, 6, 1, 0),
            intArrayOf(0, 6, 0, 5, 0, 0, 0, 0, 0),
            intArrayOf(4, 0, 0, 8, 5, 3, 0, 7, 0),
            intArrayOf(0, 0, 0, 2, 7, 1, 0, 6, 4),
            intArrayOf(1, 0, 0, 9, 4, 0, 0, 0, 2)
        ), PuzzleJumbler(sudokuNumbers = arrayOf(
            intArrayOf(8, 0, 0, 7, 3, 9, 0, 0, 6),
            intArrayOf(3, 7, 0, 4, 6, 5, 0, 0, 0),
            intArrayOf(0, 4, 0, 1, 8, 2, 0, 0, 9),
            intArrayOf(0, 0, 0, 6, 0, 0, 0, 4, 0),
            intArrayOf(0, 5, 4, 3, 0, 0, 6, 1, 0),
            intArrayOf(0, 6, 0, 5, 0, 0, 0, 0, 0),
            intArrayOf(4, 0, 0, 8, 5, 3, 0, 7, 0),
            intArrayOf(0, 0, 0, 2, 7, 1, 0, 6, 4),
            intArrayOf(1, 0, 0, 9, 4, 0, 0, 0, 2)
        ), size = 9))
    }
}
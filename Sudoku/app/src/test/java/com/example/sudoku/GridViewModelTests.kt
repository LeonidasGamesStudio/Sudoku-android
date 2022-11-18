package com.example.sudoku

import com.example.sudoku.board.values.GridValuesViewModel
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class GridViewModelTests {
    private val grid = GridValuesViewModel(9)

    @Before
    fun before() {
        grid.loadSaveData("720000320052004200004200006200123200000000420022720062000092004200000000625200927200002242420000520000629272002252720042000000329242120062220052006272000092421232")
    }

    @Test
    fun getSaveData_isCorrect(){
        assertEquals(grid.getSaveData(), "720000320052004200004200006200123200000000420022720062000092004200000000625200927200002242420000520000629272002252720042000000329242120062220052006272000092421232")
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun addPresets_isCorrect() {
        assertEquals(grid.sudokuNumbers[0][0].getNum(), 7)
    }

    @Test
    fun checkFilled_isCorrect() {
        assertTrue(grid.checkFilledNumbers(4))             //9 fours in sample
        assertFalse(grid.checkFilledNumbers(8))          //0 eights in sample
        assertFalse(grid.checkFilledNumbers(3))         //4 threes in sample
    }

    @Test
    fun addNumber_isCorrect() {

    }


}
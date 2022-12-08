package com.example.sudoku.board.values

import androidx.lifecycle.ViewModel
import com.example.sudoku.board.values.GridModel.UndoStackEntry
import com.example.sudoku.board.gridGeneration.GridJumbler
import com.example.sudoku.board.gridGeneration.SudokuPresetsDifficulty
import com.example.sudoku.board.numberEntry.NumberEntry

const val TYPE_EMPTY = 0
const val TYPE_NORMAL = 1
const val TYPE_START = 2
const val TYPE_NORMAL_CONFLICT = 3
const val TYPE_START_CONFLICT = 4

class GridValuesViewModel() : ViewModel() {
    private val size = 9
    private val gridModel = GridModel(size)
    private var _selectedRow = -1
    val selectedRow: Int
        get() = _selectedRow
    private var _selectedCol = -1
    val selectedCol: Int
        get() = _selectedCol

    fun setSelectedRow (row: Int) {
        if (row > -1 && row < 9) {
            _selectedRow = row
        }
    }

    fun setSelectedCol (col: Int) {
        if (col > -1 && col < 9) {
            _selectedCol = col
        }
    }

    //gets presets from sudokuPresets and adds them to the sudoku board
    //public fun done on startup in sudoku board fragment
    fun addPresets(presetNum: Int) {
        if (presetNum != 0) {
            var presets = SudokuPresetsDifficulty().returnPreset(presetNum)
            for (i in 0..8) {
                for (j in 0..8) {
                    val number = presets.first().digitToInt()
                    presets = presets.drop(1)
                    if (number != 0) {
                        gridModel.sudokuNumbers[i][j].changeNum(number, false)
                        gridModel.sudokuNumbers[i][j].changeType(TYPE_START, false)
                    }
                }
            }
        }
    }

    fun jumbleGrid(){
        GridJumbler(gridModel.sudokuNumbers, size).jumble()
    }

    fun checkSelectedNum(): Int{
        return gridModel.sudokuNumbers[_selectedRow][_selectedCol].getNum()
    }

    fun checkFilledNumbers(number: Int): Boolean {
        var amount = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (gridModel.sudokuNumbers[r][c].getNum() == number){
                    amount += 1
                }
            }
        }

        if (amount == 9){
            return true
        }
        return false
    }

    fun getSaveData(): String {
        var numbers = ""
        for (i in 0 until size){
            for (j in 0 until size){
                numbers += gridModel.sudokuNumbers[i][j].getNum().toString()
                numbers += gridModel.sudokuNumbers[i][j].getType().toString()
            }
        }
        return numbers
    }

    fun loadSaveData(data: String){
        var saveData = data
        for (i in size - 1 downTo 0 ){
            for (j in size - 1 downTo 0){
                val type = saveData.last()
                saveData = saveData.dropLast(1)
                val number = saveData.last()
                saveData = saveData.dropLast(1)
                gridModel.sudokuNumbers[i][j].changeNum(number.digitToInt(), true)
                gridModel.sudokuNumbers[i][j].changeType(type.digitToInt(), true)
            }
        }
    }

    fun numberInput(number: Int): Boolean {
        return if (_selectedRow == -1 || _selectedCol == -1) {
            false
        } else {
            addNumberToMatrix(number)
            true
        }
    }

    fun addNumberToMatrix(number: Int){
        if (!gridModel.pencil) {
            gridModel.undoStack.add(
                UndoStackEntry(
                    NumberEntry(
                        gridModel.sudokuNumbers[_selectedRow][_selectedCol].getNum(),
                        gridModel.sudokuNumbers[_selectedRow][_selectedCol].getType()
                    ), _selectedRow, _selectedCol
                )
            )
            if (number != 0) {          //0 is when the eraser button is selected

                gridModel.sudokuNumbers[_selectedRow][_selectedCol].changeNum(
                    number,
                    false
                )    //change num to button pressed
                gridModel.sudokuNumbers[_selectedRow][_selectedCol].changeType(
                    TYPE_NORMAL,
                    false
                ) //change type to 1
            } else {  //if eraser is clicked, change num to 0 and change type to 0 = empty
                gridModel.sudokuNumbers[_selectedRow][_selectedCol].changeNum(number, false)
                gridModel.sudokuNumbers[_selectedRow][_selectedCol].changeType(TYPE_EMPTY, false)
            }
            checkBoardConflicts()   //checks board for all conflicts. could speed up by only checking
            //affected squares, but seems fast enough for now
            //TODO Near production, see if this is fast enough particularly on older phones
        } else if (number != 0) {
            gridModel.sudokuNumbers[_selectedRow][_selectedCol].setPencil(number) //set a pencilled number
        } else {
            //clear pencil numbers
            gridModel.sudokuNumbers[_selectedRow][_selectedCol].clearPencilNumbers()
        }
    }

    fun checkWinCondition(): Boolean {
        for (i in 0 until size){
            for (j in 0 until size){
                if (gridModel.sudokuNumbers[i][j].getType() != 1 && gridModel.sudokuNumbers[i][j].getType() != TYPE_START){
                    return false
                }
            }
        }
        return true
    }

    //function to check all board conflicts. This runs every time the view is invalidated to update
    //any clashes. type is set to 3 for a conflict, 1 for no conflict
    private fun checkBoardConflicts() {
        for (j in 0 until size) {        //iterate through rows
            for (i in 0 until size) {    //iterate through columns
                if (gridModel.sudokuNumbers[i][j].getNum() != 0) {    //if there is a number in the square
                    if (checkCellConflicts(
                            i,
                            j,
                            gridModel.sudokuNumbers[i][j].getNum()
                        )
                    ) {  //check for conflicts with specific cell
                        if (gridModel.sudokuNumbers[i][j].getType() == TYPE_NORMAL) {
                            gridModel.sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL_CONFLICT,
                                false
                            )  //conflict sets type to 3
                        } else if (gridModel.sudokuNumbers[i][j].getType() == TYPE_START) {
                            gridModel.sudokuNumbers[i][j].changeType(TYPE_START_CONFLICT, true)
                        }
                    } else {
                        if (gridModel.sudokuNumbers[i][j].getType() == TYPE_NORMAL_CONFLICT) {
                            gridModel.sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL,
                                false
                            )  //conflict sets type to 3
                        } else if (gridModel.sudokuNumbers[i][j].getType() == TYPE_START_CONFLICT) {
                            gridModel.sudokuNumbers[i][j].changeType(TYPE_START, true)
                        }
                    }
                }
            }
        }
    }

    //parent function to check for cell conflicts, calls checkrow, checkcolumn and checksquare
    //if positive, returns 1 for conflict
    private fun checkCellConflicts(row: Int, column: Int, number: Int): Boolean {
        return checkRow(row, column, number) || checkColumn(row, column, number) || checkSquare(row, column, number)
    }

    //checks row for any clashes with current number
    private fun checkRow(row: Int, column: Int, number: Int): Boolean {
        for (i in 0 until size){
            if(i != column){
                if (gridModel.sudokuNumbers[row][i].getNum() == number){
                    return true
                }
            }
        }
        return false
    }

    //checks column for any clashes with current number
    private fun checkColumn(row: Int, column: Int, number: Int): Boolean {
        for (i in 0 until size){
            if (i != row){
                if (gridModel.sudokuNumbers[i][column].getNum() == number){
                    return true
                }
            }
        }
        return false
    }

    //checks the square of the selected cell for any conflicts with the number inputted
    private fun checkSquare(row: Int, column: Int, number: Int): Boolean {
        val squareX = (row / 3) * 3
        val squareY = (column / 3) * 3

        for (i in squareX..squareX + 2){
            for (j in squareY..squareY + 2){
                if (i != row || j != column){
                    if (gridModel.sudokuNumbers[i][j].getNum() == number){
                        return true
                    }
                }
            }
        }
        return false
    }

    fun undoMove(): Boolean{
        val move = gridModel.undoStack.lastOrNull()
        gridModel.undoStack.removeLastOrNull()
        return if(move != null) {
            gridModel.sudokuNumbers[move.posX][move.posY].changeNum(move.numberEntry.getNum(), false)
            gridModel.sudokuNumbers[move.posX][move.posY].changeType(move.numberEntry.getType(), false)
            checkBoardConflicts()
            true
        }else{
            false
        }
    }

    fun getType(row: Int, column: Int): Int {
        return gridModel.sudokuNumbers[row][column].getType()
    }

    fun getNum(row: Int, column: Int): Int {
        return gridModel.sudokuNumbers[row][column].getNum()
    }

    fun getPencil(pencilNum: Int, row: Int, column: Int): Boolean {
        return gridModel.sudokuNumbers[row][column].getPencil(pencilNum)
    }

    fun changePencil() {
        gridModel.pencil = !gridModel.pencil
    }
}
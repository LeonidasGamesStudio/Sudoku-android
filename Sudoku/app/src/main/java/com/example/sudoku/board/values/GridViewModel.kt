package com.example.sudoku.board.values

import androidx.lifecycle.ViewModel
import com.example.sudoku.board.UndoStackEntry
import com.example.sudoku.board.gridGeneration.GridJumbler
import com.example.sudoku.board.gridGeneration.SudokuPresetsDifficulty
import com.example.sudoku.board.numberEntry.NumberEntry

const val TYPE_EMPTY = 0
const val TYPE_NORMAL = 1
const val TYPE_START = 2
const val TYPE_NORMAL_CONFLICT = 3
const val TYPE_START_CONFLICT = 4

class GridValuesViewModel(private var size: Int) : ViewModel() {
    var sudokuNumbers = Array(size) { Array(size) { NumberEntry(0,0) } }
    private var undoStack = ArrayDeque<UndoStackEntry>()
    var pencil = false


    //gets presets from sudokuPresets and adds them to the sudoku board
    //public fun done on startup in sudoku board fragment
    fun addPresets(presetNum: Int) {
        val sudokuPresets = SudokuPresetsDifficulty()

        if (presetNum != 0) {
            var presets = sudokuPresets.returnPreset(presetNum)
            for (i in 0..8) {
                for (j in 0..8) {
                    val number = presets.first().digitToInt()
                    presets = presets.drop(1)
                    if (number != 0) {
                        sudokuNumbers[i][j].changeNum(number, false)
                        sudokuNumbers[i][j].changeType(TYPE_START, false)
                    }
                }
            }
        }
    }

    fun jumbleGrid(){
        GridJumbler(sudokuNumbers, size).jumble()
    }

    fun checkCurrentNum(row: Int, col: Int): Int{
        return sudokuNumbers[row][col].getNum()
    }

    fun checkFilledNumbers(number: Int): Boolean {
        var amount = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (sudokuNumbers[r][c].getNum() == number){
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
                numbers += sudokuNumbers[i][j].getNum().toString()
                numbers += sudokuNumbers[i][j].getType().toString()
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
                sudokuNumbers[i][j].changeNum(number.digitToInt(), true)
                sudokuNumbers[i][j].changeType(type.digitToInt(), true)
            }
        }
    }

    fun addNumberToMatrix(number: Int, row: Int, col: Int){
        if (!pencil) {
            undoStack.add(
                UndoStackEntry(
                    NumberEntry(
                        sudokuNumbers[row][col].getNum(),
                        sudokuNumbers[row][col].getType()
                    ), row, col
                )
            )
            if (number != 0) {          //0 is when the eraser button is selected

                sudokuNumbers[row][col].changeNum(
                    number,
                    false
                )    //change num to button pressed
                sudokuNumbers[row][col].changeType(
                    TYPE_NORMAL,
                    false
                ) //change type to 1
            } else {  //if eraser is clicked, change num to 0 and change type to 0 = empty
                sudokuNumbers[row][col].changeNum(number, false)
                sudokuNumbers[row][col].changeType(TYPE_EMPTY, false)
            }
            checkBoardConflicts()   //checks board for all conflicts. could speed up by only checking
            //affected squares, but seems fast enough for now
            //TODO Near production, see if this is fast enough particularly on older phones
        } else if (number != 0) {
            sudokuNumbers[row][col].setPencil(number) //set a pencilled number
        } else {
            //clear pencil numbers
            sudokuNumbers[row][col].clearPencilNumbers()
        }
    }

    fun checkWinCondition(): Boolean {
        for (i in 0 until size){
            for (j in 0 until size){
                if (sudokuNumbers[i][j].getType() != 1 && sudokuNumbers[i][j].getType() != TYPE_START){
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
                if (sudokuNumbers[i][j].getNum() != 0) {    //if there is a number in the square
                    if (checkCellConflicts(
                            i,
                            j,
                            sudokuNumbers[i][j].getNum()
                        )
                    ) {  //check for conflicts with specific cell
                        if (sudokuNumbers[i][j].getType() == TYPE_NORMAL) {
                            sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL_CONFLICT,
                                false
                            )  //conflict sets type to 3
                        } else if (sudokuNumbers[i][j].getType() == TYPE_START) {
                            sudokuNumbers[i][j].changeType(TYPE_START_CONFLICT, true)
                        }
                    } else {
                        if (sudokuNumbers[i][j].getType() == TYPE_NORMAL_CONFLICT) {
                            sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL,
                                false
                            )  //conflict sets type to 3
                        } else if (sudokuNumbers[i][j].getType() == TYPE_START_CONFLICT) {
                            sudokuNumbers[i][j].changeType(TYPE_START, true)
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
                if (sudokuNumbers[row][i].getNum() == number){
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
                if (sudokuNumbers[i][column].getNum() == number){
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
                    if (sudokuNumbers[i][j].getNum() == number){
                        return true
                    }
                }
            }
        }
        return false
    }

    fun undoMove(): Boolean{
        val move = undoStack.lastOrNull()
        undoStack.removeLastOrNull()
        return if(move != null) {
            sudokuNumbers[move.posX][move.posY].changeNum(move.numberEntry.getNum(), false)
            sudokuNumbers[move.posX][move.posY].changeType(move.numberEntry.getType(), false)
            true
        }else{
            false
        }
    }
}
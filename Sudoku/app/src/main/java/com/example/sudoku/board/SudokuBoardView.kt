package com.example.sudoku.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TableRow
import android.widget.Toast
import com.example.sudoku.R
import com.example.sudoku.board.gridGeneration.SudokuPresetsDifficulty
import com.example.sudoku.board.ui.Paints
import kotlin.math.abs
import com.example.sudoku.board.gridGeneration.GridJumbler
import com.example.sudoku.board.numberEntry.NumberEntry

const val TYPE_EMPTY = 0
const val TYPE_NORMAL = 1
const val TYPE_START = 2
const val TYPE_NORMAL_CONFLICT = 3
const val TYPE_START_CONFLICT = 4

class UndoStackEntry(val numberEntry: NumberEntry, val posX: Int, val posY: Int)

class SudokuBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    private val sqrtSize = 3
    private val size = 9
    private var cellSizePixels = 0F
    private var selectedRow = -1
    private var selectedColumn = -1
    private var numberTextSize = 64F
    private var undoStack = ArrayDeque<UndoStackEntry>()
    private val paints = Paints(numberTextSize)

    fun getSelectedRow(): Int {
        return selectedRow
    }

    var pencil = false

    var sudokuNumbers = Array(size) { Array(size) { NumberEntry(0,0) } }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val boardFragment: ViewGroup = parent.parent as ViewGroup
        val timer = boardFragment.findViewById<Chronometer>(R.id.timer)
        val funcButtons = boardFragment.findViewById<TableRow>(R.id.funcButtons)
        val sizePixels: Int
        val newHeightSpec: Int
        if(timer.y > 0){
            val maxHeight = abs(((timer.y - timer.height) - (funcButtons.y - funcButtons.height)) + funcButtons.height)
            newHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), AT_MOST)
            sizePixels = widthMeasureSpec.coerceAtMost(newHeightSpec)
        }else{
            sizePixels = widthMeasureSpec.coerceAtMost(heightMeasureSpec)
        }



        numberTextSize = (sizePixels / 100).toFloat()
        setMeasuredDimension(sizePixels, sizePixels)
    }
    //this is called every time anything is changed. number added, erased, pencil added, hint
    //function to call is invalidate()

    override fun onDraw(canvas: Canvas) {
        requestLayout()
        cellSizePixels = (width / size).toFloat()


        fillCells(canvas)
        drawLines(canvas)

        drawNumbers(canvas)

    }

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
            GridJumbler(sudokuNumbers, size).jumble()
        }
    }

    //checks the current number. public for other classes to access when selected row/column are
    //unknown
    fun checkCurrentNum(): Int {
        return sudokuNumbers[selectedRow][selectedColumn].getNum()
    }

    //checks to see if all of one number has been filled or if the final one has been cleared
    fun checkFilledNumbers(checkNumber: Int): Boolean {
        var amount = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (sudokuNumbers[r][c].getNum() == checkNumber){
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
        invalidate()
    }

    //takes selected row and column and shades them. One colour for selected cell, another for
    //conflicting cells
    private fun fillCells(canvas: Canvas){
        if (selectedRow == -1 || selectedColumn == -1) return
        for (r in 0..size) {
            for (c in 0..size){
                if (r == selectedRow && c == selectedColumn) {
                    fillCell(canvas, r, c, paints.selectedCellPaint)
                } else if (r == selectedRow || c == selectedColumn) {
                    fillCell(canvas, r, c, paints.conflictingCellPaint)
                }else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedColumn / sqrtSize){
                    fillCell(canvas, r, c, paints.conflictingCellPaint)
                }
            }
        }
    }

    //fills cell with shading to show selected cell and conflicting cell
    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint){
        canvas.drawRect(c * cellSizePixels, r * cellSizePixels, (c+1) * cellSizePixels, (r+1) * cellSizePixels, paint)
    }

    //draws lines for the board
    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paints.extraThickLinePaint)

        for (i in 0 until size) {
            val paintToUse = when (i % sqrtSize){
                0 -> paints.thickLinePaint
                else -> paints.thinLinePaint
            }

            canvas.drawLine(
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                paintToUse
            )

            canvas.drawLine(
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                paintToUse
            )
        }
    }

    //adds a number to either the sudoku number matrix or the pencil number matrix when the button is pressed
    fun addNumberToMatrix(number: Int){
        if (!pencil) {
            undoStack.add(
                UndoStackEntry(
                    NumberEntry(
                        sudokuNumbers[selectedRow][selectedColumn].getNum(),
                        sudokuNumbers[selectedRow][selectedColumn].getType()
                    ), selectedRow, selectedColumn
                )
            )
            if (number != 0) {          //0 is when the eraser button is selected

                sudokuNumbers[selectedRow][selectedColumn].changeNum(
                    number,
                    false
                )    //change num to button pressed
                sudokuNumbers[selectedRow][selectedColumn].changeType(
                    TYPE_NORMAL,
                    false
                ) //change type to 1
            } else {  //if eraser is clicked, change num to 0 and change type to 0 = empty
                sudokuNumbers[selectedRow][selectedColumn].changeNum(number, false)
                sudokuNumbers[selectedRow][selectedColumn].changeType(TYPE_EMPTY, false)
            }
            checkBoardConflicts()   //checks board for all conflicts. could speed up by only checking
            //affected squares, but seems fast enough for now
            //TODO Near production, see if this is fast enough particularly on older phones
        } else if (number != 0) {
            sudokuNumbers[selectedRow][selectedColumn].setPencil(number) //set a pencilled number
        } else {
            //clear pencil numbers
            sudokuNumbers[selectedRow][selectedColumn].clearPencilNumbers()
        }
        invalidate()
    }

    //checks wincon by checking the type of all squares. If type is 1 (inputted with no clash) or 2,
    //then it satisfies win con. If any squares are another number, returns false (no win)
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

    private fun checkBoardConflicts(){
        for (j in 0 until size){        //iterate through rows
            for (i in 0 until size){    //iterate through columns
                if (sudokuNumbers[i][j].getNum() != 0) {    //if there is a number in the square
                    if (checkCellConflicts(i, j, sudokuNumbers[i][j].getNum())) {  //check for conflicts with specific cell
                        if (sudokuNumbers[i][j].getType() == TYPE_NORMAL) {
                            sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL_CONFLICT,
                                false
                            )  //conflict sets type to 3
                        }else if (sudokuNumbers[i][j].getType() == TYPE_START) {
                            sudokuNumbers[i][j].changeType(TYPE_START_CONFLICT, true)
                        }
                    } else {
                        if (sudokuNumbers[i][j].getType() == TYPE_NORMAL_CONFLICT) {
                            sudokuNumbers[i][j].changeType(
                                TYPE_NORMAL,
                                false
                            )  //conflict sets type to 3
                        }else if (sudokuNumbers[i][j].getType() == TYPE_START_CONFLICT) {
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

    //draws all numbers on the board, including pencils
    private fun drawNumbers (canvas: Canvas){
        val yOffset = ((paints.numberPaint.ascent() + paints.numberPaint.descent()) / 2)
        for (i in 0 until size){
            for (j in 0 until size){
                when {
                    sudokuNumbers[i][j].getType() == TYPE_NORMAL -> {
                        canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, paints.numberPaint)
                    }
                    sudokuNumbers[i][j].getType() == TYPE_NORMAL_CONFLICT -> {
                        canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, paints.conflictPaint)
                    }
                    sudokuNumbers[i][j].getType() == TYPE_START -> {
                        canvas.drawText(
                            sudokuNumbers[i][j].getNum().toString(),
                            j * cellSizePixels + (cellSizePixels / 2),
                            i * cellSizePixels + (cellSizePixels / 2) - yOffset,
                            paints.presetPaint
                        )
                    }
                    sudokuNumbers[i][j].getType() == TYPE_START_CONFLICT -> {
                        canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, paints.conflictPaint)
                    }
                    sudokuNumbers[i][j].getType() == TYPE_EMPTY -> {
                        drawPencils(canvas, i, j)
                    }
                }
            }
        }
    }

//    function draws all pencil marks in. might need to check sizes
    private fun drawPencils(canvas: Canvas, i: Int, j: Int){
        for (k in 0..9){
            if (sudokuNumbers[i][j].getPencil(k)){
                val yOffset = (paints.pencilPaint.ascent() + paints.pencilPaint.descent()) / 2
                canvas.drawText(k.toString(),
                    (j * cellSizePixels + (((k - 1) % 3) + 0.5) * (cellSizePixels / 3)).toFloat(),
                    (i * cellSizePixels + (((k - 1) / 3) + 0.5) * (cellSizePixels / 3) - yOffset).toFloat(), paints.pencilPaint)
            //draw pencil
            }
        }
    }

    fun undoMove(){
        val move = undoStack.lastOrNull()
        undoStack.removeLastOrNull()
        if(move != null) {
            sudokuNumbers[move.posX][move.posY].changeNum(move.numberEntry.getNum(), false)
            sudokuNumbers[move.posX][move.posY].changeType(move.numberEntry.getType(), false)
            invalidate()
        }else{
            val text = "No moves left to undo!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }

//    handles touch events. every time the board is touched, it is invalidated and redrawn with shading
    // around selected cell
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    //sets coordinates of selected row and column
    private fun handleTouchEvent(x: Float, y: Float) {
        selectedRow = (y / cellSizePixels).toInt()
        selectedColumn = (x / cellSizePixels).toInt()
        invalidate()
    }
}
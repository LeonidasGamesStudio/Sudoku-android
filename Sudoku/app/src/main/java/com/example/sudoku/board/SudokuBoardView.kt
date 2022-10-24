package com.example.sudoku.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Chronometer
import android.widget.TableRow
import com.example.sudoku.R
import com.example.sudoku.board.gridGeneration.SudokuPresetsDifficulty
import com.example.sudoku.board.gridGeneration.GridJumbler as gridJumbler

const val TYPE_EMPTY = 0
const val TYPE_NORMAL = 1
const val TYPE_START = 2
const val TYPE_CONFLICT = 3

class NumberEntryArrayCopier(private val sudokuNumbers: Array<Array<SudokuBoardView.NumberEntry>>, private val size: Int){
    fun copyArray(): Array<Array<SudokuBoardView.NumberEntry>> {
        val newArray = Array(9) {Array(9) { SudokuBoardView.NumberEntry(0, 0) } }
        for(i in 0 until size){
            for(j in 0 until size) {
                newArray[i][j].changeNum(sudokuNumbers[i][j].getNum(), false)
                newArray[i][j].changeType(sudokuNumbers[i][j].getType(), false)
            }
        }
        return newArray
    }
}

class SudokuBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    class NumberEntry(private var number: Int, private var type: Int){
        //type: 1 = normal, 3 = conflict, 2 = start, 0 = unfilled/pencilled
        private var pencilNumbers = BooleanArray(10) {false}
        fun changeNum(newNum: Int, override: Boolean){
            if ((type != TYPE_START) || override) {
                number = newNum
            }
        }

        fun changeType(newType: Int, override: Boolean){
            if ((type != TYPE_START) || override){
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
    }

    class UndoStackEntry(val numberEntry: NumberEntry, val posX: Int, val posY: Int)


    private val sqrtSize = 3
    private val size = 9

    private var cellSizePixels = 0F
    private var selectedRow = -1
    private var selectedColumn = -1
    private var numberTextSize = 64F
    private var undoStack = ArrayDeque<UndoStackEntry>()
    var pencil = false

    var sudokuNumbers = Array(9) {Array(9) { NumberEntry(0,0) } }

    class paintValues(){    //TODO move paint values into class, if I ever feel like it


    }

    private val numberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    private val conflictPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    private val presetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.DKGRAY
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    private val pencilPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = 32F
        textAlign = Paint.Align.CENTER
    }

    private val extraThickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 16F
    }

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 8F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val timer = findViewById<Chronometer>(R.id.timer)
        val funcButtons = findViewById<TableRow>(R.id.funcButtons)
        val maxHeight = (timer.y - (timer.measuredHeight / 2)) - (funcButtons.y + (funcButtons.measuredHeight / 2))
        val sizePixels = Math.min(widthMeasureSpec, maxHeight.toInt())
        numberTextSize = (sizePixels / 100).toFloat()
        setMeasuredDimension(sizePixels, sizePixels)
    }

    //this is called every time anything is changed. number added, erased, pencil added, hint
    //function to call is invalidate()
    override fun onDraw(canvas: Canvas) {
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
            gridJumbler(sudokuNumbers, size).jumble()
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
                    fillCell(canvas, r, c, selectedCellPaint)
                } else if (r == selectedRow || c == selectedColumn) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedColumn / sqrtSize){
                    fillCell(canvas, r, c, conflictingCellPaint)
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
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), extraThickLinePaint)

        for (i in 0 until size) {
            val paintToUse = when (i % sqrtSize){
                0 -> thickLinePaint
                else -> thinLinePaint
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
        if(!pencil) {
            undoStack.add(
                UndoStackEntry(
                    NumberEntry(
                        sudokuNumbers[selectedRow][selectedColumn].getNum(),
                        sudokuNumbers[selectedRow][selectedColumn].getType()
                    ), selectedRow, selectedColumn
                )
            )
            if (number != 0) {          //0 is when the eraser button is selected
                sudokuNumbers[selectedRow][selectedColumn].changeNum(number, false)    //change num to button pressed
                sudokuNumbers[selectedRow][selectedColumn].changeType(TYPE_NORMAL, false) //change type to 1
            } else {  //if eraser is clicked, change num to 0 and change type to 0 = empty
                sudokuNumbers[selectedRow][selectedColumn].changeNum(number, false)
                sudokuNumbers[selectedRow][selectedColumn].changeType(TYPE_EMPTY, false)
            }
            checkBoardConflicts()   //checks board for all conflicts. could speed up by only checking
            //affected squares, but seems fast enough for now
            //TODO Near production, see if this is fast enough particularly on older phones
        }else if (number != 0){
            sudokuNumbers[selectedRow][selectedColumn].setPencil(number) //set a pencilled number
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
                        sudokuNumbers[i][j].changeType(TYPE_CONFLICT, false)  //conflict sets type to 3
                    } else {
                        sudokuNumbers[i][j].changeType(TYPE_NORMAL, false)   //no clashes sets type to 1
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
        val yOffset = ((numberPaint.ascent() + numberPaint.descent()) / 2)
        for (i in 0 until size){
            for (j in 0 until size){
                when {
                    sudokuNumbers[i][j].getType() == TYPE_NORMAL -> {
                        canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, numberPaint)
                    }
                    sudokuNumbers[i][j].getType() == TYPE_CONFLICT -> {
                        canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, conflictPaint)
                    }
                    sudokuNumbers[i][j].getType() == TYPE_START -> {
                        canvas.drawText(
                            sudokuNumbers[i][j].getNum().toString(),
                            j * cellSizePixels + (cellSizePixels / 2),
                            i * cellSizePixels + (cellSizePixels / 2) - yOffset,
                            presetPaint
                        )
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
                val yOffset = (pencilPaint.ascent() + pencilPaint.descent()) / 2
                canvas.drawText(k.toString(),
                    (j * cellSizePixels + (((k - 1) % 3) + 0.5) * (cellSizePixels / 3)).toFloat(),
                    (i * cellSizePixels + (((k - 1) / 3) + 0.5) * (cellSizePixels / 3) - yOffset).toFloat(), pencilPaint)
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
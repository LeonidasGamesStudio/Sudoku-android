package com.example.sudoku.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context,attributeSet){
    class NumberEntry(num: Int, type: Int){
        private var number = num
        private var type = type     //1 = normal, -1 = conflict, 2 = start, 0 = unfilled/pencilled
        private var pencilNumbers = BooleanArray(10) {false}

        fun changeNum(newNum: Int){
            if (type != 2) {
                number = newNum
            }
        }

        fun changeType(newType: Int){
            if (type != 2){
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


    private val sqrtSize = 3
    private val size = 9

    private var cellSizePixels = 0F
    private var selectedRow = -1
    private var selectedColumn = -1
    private var numberTextSize = 64F
    var pencil = false

    var sudokuNumbers = Array(9) {Array(9) { NumberEntry(0,0) } }

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
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / size).toFloat()
        setNumberSizes()

        fillCells(canvas)
        drawLines(canvas)

        drawNumbers(canvas)

    }

    private fun setNumberSizes(){
        val paintSize = (height / 10).toFloat()
        numberPaint.textSize = paintSize
        conflictPaint.textSize = paintSize
        presetPaint.textSize = paintSize
    }

    fun addPresets(presetNum: Int) {
        val sudokuPresets = SudokuPresets()
        val presets = sudokuPresets.returnPreset(presetNum)

        for (i in 0 .. 8){
            for (j in 0 .. 8){
                if (presets[i][j] != 0) {
                    sudokuNumbers[i][j].changeNum(presets[i][j])
                    sudokuNumbers[i][j].changeType(2)
                }
            }
        }
    }

    fun checkCurrentNum(): Int {
        return sudokuNumbers[selectedRow][selectedColumn].getNum()
    }

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

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint){
        canvas.drawRect(c * cellSizePixels, r * cellSizePixels, (c+1) * cellSizePixels, (r+1) * cellSizePixels, paint)
    }

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

    fun addNumberToMatrix(number: Int){
        //check all squares for conflicts
        if (number != 0) {
            if (!pencil) {
                sudokuNumbers[selectedRow][selectedColumn].changeNum(number)
                sudokuNumbers[selectedRow][selectedColumn].changeType(1)
            }else{
                sudokuNumbers[selectedRow][selectedColumn].setPencil(number)
            }
        }else{
            sudokuNumbers[selectedRow][selectedColumn].changeNum(number)
            sudokuNumbers[selectedRow][selectedColumn].changeType(0)
        }
        checkBoardConflicts()
        invalidate()
    }

    fun checkWinCondition(): Boolean {
        for (i in 0 until size){
            for (j in 0 until size){
                if (sudokuNumbers[i][j].getType() != 1 && sudokuNumbers[i][j].getType() != 2){
                    return false
                }
            }
        }
        return true
    }

    private fun checkBoardConflicts(){
        for (j in 0 until size){        //iterate through rows
            for (i in 0 until size){    //iterate through columns
                if (sudokuNumbers[i][j].getNum() != 0) {
                    if (checkCellConflicts(
                            i,
                            j,
                            sudokuNumbers[i][j].getNum()
                        )
                    ) {  //check for conflicts with specific cell
                        sudokuNumbers[i][j].changeType(-1)
                    } else {
                        sudokuNumbers[i][j].changeType(1)
                    }
                }
            }
        }
    }

    private fun checkCellConflicts(row: Int, column: Int, number: Int): Boolean {
        return checkRow(row, column, number) || checkColumn(row, column, number) || checkSquare(row, column, number)
    }

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

    private fun drawNumbers (canvas: Canvas){
        val yOffset = ((numberPaint.ascent() + numberPaint.descent()) / 2)
        for (i in 0 until size){
            for (j in 0 until size){
                if(sudokuNumbers[i][j].getType() == 1){
                    canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, numberPaint)
                }else if(sudokuNumbers[i][j].getType() == -1){
                    canvas.drawText(sudokuNumbers[i][j].getNum().toString(), j * cellSizePixels + (cellSizePixels / 2), i * cellSizePixels + (cellSizePixels / 2) - yOffset, conflictPaint)
                }else if(sudokuNumbers[i][j].getType() == 2) {
                    canvas.drawText(
                        sudokuNumbers[i][j].getNum().toString(),
                        j * cellSizePixels + (cellSizePixels / 2),
                        i * cellSizePixels + (cellSizePixels / 2) - yOffset,
                        presetPaint
                    )
                }else if(sudokuNumbers[i][j].getType() == 0){
                    drawPencils(canvas, i, j)
                }
            }
        }
    }

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

    private fun handleTouchEvent(x: Float, y: Float) {
        selectedRow = (y / cellSizePixels).toInt()
        selectedColumn = (x / cellSizePixels).toInt()
        invalidate()
    }
}
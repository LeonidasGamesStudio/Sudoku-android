package com.example.sudoku.board.ui

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
import com.example.sudoku.board.numberEntry.NumberEntry
import com.example.sudoku.board.values.*
import com.google.android.material.color.MaterialColors
import kotlin.math.abs


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

    private val paints = Paints(numberTextSize,
        MaterialColors.getColor(this, R.attr.colorPrimary),
        MaterialColors.getColor(this, R.attr.colorPrimaryVariant),
        MaterialColors.getColor(this, R.attr.colorOnPrimary),
        MaterialColors.getColor(this, R.attr.colorSecondary),
        MaterialColors.getColor(this, R.attr.colorSecondaryVariant),
        MaterialColors.getColor(this, R.attr.colorOnSecondary),
        MaterialColors.getColor(this, R.attr.colorOnBackground)
    )

    fun getSelectedRow(): Int {
        return selectedRow
    }



    private var gridViewModel = GridValuesViewModel(size)

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

    fun addPresets(presetNum: Int){
        gridViewModel.addPresets(presetNum)
    }

    fun checkCurrentNum(): Int {
        return gridViewModel.checkCurrentNum(selectedRow, selectedColumn)
    }

    //checks to see if all of one number has been filled or if the final one has been cleared
    fun checkFilledNumbers (number: Int): Boolean {
        return gridViewModel.checkFilledNumbers(number)
    }

    fun getSaveData(): String {
        return gridViewModel.getSaveData()
    }

    fun loadSaveData(data: String){
        gridViewModel.loadSaveData(data)
        invalidate()
    }

    //takes selected row and column and shades them. One colour for selected cell, another for
    //conflicting cells
    private fun fillCells(canvas: Canvas){
        if (selectedRow == -1) return
        for (r in 0 until size) {
            for (c in 0 until size){
                val paint = chooseCellPaint(r, c)
                fillCell(canvas, r, c, paint)
            }
        }
    }

    private fun chooseCellPaint(row: Int, col: Int): Paint {
        val selected = isCellSelected(row, col)
        val preset = isCellPreset(row, col)
        return if (!preset) {
            when (selected) {
                1 -> paints.selectedCellPaint
                2 -> paints.conflictingCellPaint
                else -> paints.white
            }
        } else {
            when (selected) {
                1 -> paints.presetSelectedCellPaint
                2 -> paints.presetConflictingCellPaint
                else -> paints.presetCellPaint
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
        gridViewModel.addNumberToMatrix(number, selectedRow, selectedColumn)
        invalidate()
    }

    //checks wincon by checking the type of all squares. If type is 1 (inputted with no clash) or 2,
    //then it satisfies win con. If any squares are another number, returns false (no win)
    fun checkWinCondition(): Boolean {
        return gridViewModel.checkWinCondition()
    }



    //draws all numbers on the board, including pencils
    private fun drawNumbers (canvas: Canvas){
        val yOffset = ((paints.numberPaint.ascent() + paints.numberPaint.descent()) / 2)
        for (i in 0 until size){
            for (j in 0 until size) {
                val number = gridViewModel.getNum(i, j)
                if (number != 0) {
                    val paint = chooseColor(i, j)
                    canvas.drawText(
                        number.toString(),
                        j * cellSizePixels + (cellSizePixels / 2),
                        i * cellSizePixels + (cellSizePixels / 2) - yOffset,
                        paint
                    )
                }
            }
        }
    }

    private fun chooseColor(row: Int, col: Int): Paint {
        val selected = isCellSelected(row, col)
        val preset = isCellPreset(row, col)
        val conflict = isCellConflicted(row, col)
        return if (conflict) {
            paints.conflictPaint
        } else {
            if (!preset) {
                when (selected) {
                    1 -> paints.selectedNumberPaint
                    2 -> paints.adjacentNumberPaint
                    else -> paints.numberPaint
                }
            } else {
                when (selected) {
                    1 -> paints.selectedPresetPaint
                    2 -> paints.adjacentPresetPaint
                    else -> paints.presetPaint
                }
            }
        }
    }

    private fun isCellConflicted(row: Int, col: Int): Boolean {
        return gridViewModel.getType(row, col) == TYPE_START_CONFLICT ||
                gridViewModel.getType(row, col) == TYPE_NORMAL_CONFLICT
    }

    private fun isCellPreset(row: Int, col: Int): Boolean {
        return when (gridViewModel.getType(row, col)){
            TYPE_EMPTY -> false
            TYPE_NORMAL -> false
            TYPE_START -> true
            TYPE_START_CONFLICT -> true
            else -> false
        }
    }

    // Checks to see the relation of the cell to the current selected cell
    // Returns 1 if it is the same cell, 2 if it is adjacent or within the same square
    // Or 3 if it is non-related
    private fun isCellSelected(row: Int, col: Int): Int {
        return if (selectedRow != -1) {
            if (row == selectedRow && col == selectedColumn) {
                1
            } else if (row == selectedRow || col == selectedColumn) {
                2
            } else if (row / sqrtSize == selectedRow / sqrtSize && col / sqrtSize == selectedColumn / sqrtSize) {
                2
            } else {
                3
            }
        }else{
            3
        }
    }

    //    function draws all pencil marks in. might need to check sizes
    private fun drawPencils(canvas: Canvas, i: Int, j: Int){
        for (k in 0..9){
            if (gridViewModel.getPencil(k, i, j)){
                val yOffset = (paints.pencilPaint.ascent() + paints.pencilPaint.descent()) / 2
                canvas.drawText(k.toString(),
                    (j * cellSizePixels + (((k - 1) % 3) + 0.5) * (cellSizePixels / 3)).toFloat(),
                    (i * cellSizePixels + (((k - 1) / 3) + 0.5) * (cellSizePixels / 3) - yOffset).toFloat(), paints.pencilPaint)
            //draw pencil
            }
        }
    }

    fun undoMove(){
        if(gridViewModel.undoMove()) {
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

    fun changePencil(){
        gridViewModel.changePencil()
    }

    fun jumbleGrid() {
        gridViewModel.jumbleGrid()
    }
}
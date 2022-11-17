package com.example.sudoku.board.ui

import android.graphics.Color
import android.graphics.Paint

class Paints (private val numberTextSize: Float){
    val numberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val conflictPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val presetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.DKGRAY
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val pencilPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = 32F
        textAlign = Paint.Align.CENTER
    }

    val extraThickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 16F
    }

    val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 8F
    }

    val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }
}
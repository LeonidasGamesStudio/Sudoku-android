package com.example.sudoku.board.ui

import android.graphics.Color
import android.graphics.Paint

class Paints(
    private val numberTextSize: Float,
    private val primary: Int,
    private val primaryVariant: Int,
    private val onPrimary: Int,
    private val secondary: Int,
    private val secondaryVariant: Int,
    private val onSecondary: Int,
    private val onBackground: Int
){
    val numberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = primary
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val selectedNumberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = secondary
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val adjacentNumberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = secondaryVariant
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
        color = onSecondary
        colorFilter
        strokeWidth = 16F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val selectedPresetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = onPrimary
        colorFilter
        strokeWidth = 16F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val adjacentPresetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = onPrimary
        colorFilter
        strokeWidth = 16F
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
        color = primary
    }

    val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = secondary
    }
}
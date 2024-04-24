package com.example.sudoku.board.ui

import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.ColorUtils


class Paints(
    private val numberTextSize: Float,
    private val primary: Int,
    private val primaryVariant: Int,
    private val onPrimary: Int,
    private val secondary: Int,
    private val secondaryVariant: Int,
    private val onSecondary: Int,
    private val onBackground: Int
) {
    val numberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val selectedNumberPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER

    }

    val adjacentNumberPaint = Paint().apply {
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
        color = onSecondary
        colorFilter
        strokeWidth = 16F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val selectedPresetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = onSecondary
        colorFilter
        strokeWidth = 16F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val adjacentPresetPaint = Paint().apply {
        style = Paint.Style.FILL
        color = onSecondary
        colorFilter
        strokeWidth = 16F
        textSize = numberTextSize
        textAlign = Paint.Align.CENTER
    }

    val pencilPaint = Paint().apply {
        style = Paint.Style.FILL
        color = onSecondary
        strokeWidth = 2F
        textSize = numberTextSize/3
        textAlign = Paint.Align.CENTER
    }

    val extraThickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = onSecondary
        strokeWidth = 16F
    }

    val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = onSecondary
        strokeWidth = 8F
    }

    val mediumLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = onSecondary
        strokeWidth = 4F
    }

    val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = onSecondary
        strokeWidth = 2F
    }

    val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = secondary
    }

    val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = secondaryVariant
    }

    val presetCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.LTGRAY
    }

    val presetSelectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ColorUtils.blendARGB(Color.LTGRAY, secondary, 0.8f)
    }

    val presetConflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ColorUtils.blendARGB(Color.LTGRAY, secondaryVariant, 0.5f)
    }

    val white = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }
}
package com.example.sudoku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.oneButton.setOnClickListener{ addNumber(1) }
        binding.twoButton.setOnClickListener{ addNumber(2) }
        binding.threeButton.setOnClickListener{ addNumber(3) }
        binding.fourButton.setOnClickListener{ addNumber(4) }
        binding.fiveButton.setOnClickListener{ addNumber(5) }
        binding.sixButton.setOnClickListener{ addNumber(6) }
        binding.sevenButton.setOnClickListener{ addNumber(7) }
        binding.eightButton.setOnClickListener{ addNumber(8) }
        binding.nineButton.setOnClickListener{ addNumber(9) }

    }

    private fun addNumber (number: Int) {

        binding.sudokuBoardView.addNumberToMatrix(number)

    }
}
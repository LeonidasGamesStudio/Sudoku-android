package com.example.sudoku.board

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.databinding.FragmentSudokuBoardBinding
import com.google.android.gms.ads.AdRequest


class SudokuBoardFragment : Fragment() {
    private var _binding: FragmentSudokuBoardBinding? = null
    private val binding get() = _binding!!
    private var timeBegin: Long = System.currentTimeMillis()
    private var gameWon: Boolean = false
    private val boardView = SudokuBoardView(context!!)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSudokuBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sudokuBoardView.addView(boardView)
        binding.oneButton.setOnClickListener{ addNumber(1) }
        binding.twoButton.setOnClickListener{ addNumber(2) }
        binding.threeButton.setOnClickListener{ addNumber(3) }
        binding.fourButton.setOnClickListener{ addNumber(4) }
        binding.fiveButton.setOnClickListener{ addNumber(5) }
        binding.sixButton.setOnClickListener{ addNumber(6) }
        binding.sevenButton.setOnClickListener{ addNumber(7) }
        binding.eightButton.setOnClickListener{ addNumber(8) }
        binding.nineButton.setOnClickListener{ addNumber(9) }
        binding.eraserButton.setOnClickListener{ addNumber(0) }

        binding.pencilButton.setOnClickListener{
            boardView.pencil = boardView.pencil
        }

        binding.undoButton.setOnClickListener{ boardView.undoMove()}

        val value = arguments?.get("levelNumber")
        boardView.addPresets(value as Int)


        val sharedPref = getDefaultSharedPreferences(activity)
        val numbersString = sharedPref.getString("SAVED_NUMBERS", null)
        if (numbersString != null) {
            boardView.loadSaveData(numbersString)
        }

        val timerLong = sharedPref.getLong("TIMER_STOPPED", 0L)
        if (timerLong < 0) {
            binding.timer.base = timerLong + SystemClock.elapsedRealtime()
            binding.timer.start()
        }else{
            binding.timer.start()
        }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onDestroyView() {
        if (!gameWon) {
            val sharedPref = getDefaultSharedPreferences(activity)
            val numbersString = boardView.getSaveData()
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString("SAVED_NUMBERS", numbersString)
                    putLong("TIMER_STOPPED", binding.timer.base - SystemClock.elapsedRealtime())
                    apply()
                }
            }
        }
        super.onDestroyView()
        _binding = null
    }


    private fun addNumber (number: Int) {
        val previousNum = boardView!!.checkCurrentNum()
        boardView.addNumberToMatrix(number)
        checkForFilledNumbers(number, previousNum)
        if (boardView.checkWinCondition()){
            val timeTaken = System.currentTimeMillis() - timeBegin
            moveToWinScreen(timeTaken)
            return
        }
    }

    private fun moveToWinScreen(timeTaken: Long){
        gameWon = true
        val action = SudokuBoardFragmentDirections.actionSudokuBoardFragmentToWinFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun checkForFilledNumbers(number: Int, previousNum: Int){
        if (number != 0) {
            if (boardView!!.checkFilledNumbers(number)) {
                //change string to strikethrough
                when (number) {
                    1 -> binding.oneButton.isEnabled = false
                    2 -> binding.twoButton.isEnabled = false
                    3 -> binding.threeButton.isEnabled = false
                    4 -> binding.fourButton.isEnabled = false
                    5 -> binding.fiveButton.isEnabled = false
                    6 -> binding.sixButton.isEnabled = false
                    7 -> binding.sevenButton.isEnabled = false
                    8 -> binding.eightButton.isEnabled = false
                    else -> binding.nineButton.isEnabled = false
                }
            } else {
                when (number) {
                    1 -> binding.oneButton.isEnabled = true
                    2 -> binding.twoButton.isEnabled = true
                    3 -> binding.threeButton.isEnabled = true
                    4 -> binding.fourButton.isEnabled = true
                    5 -> binding.fiveButton.isEnabled = true
                    6 -> binding.sixButton.isEnabled = true
                    7 -> binding.sevenButton.isEnabled = true
                    8 -> binding.eightButton.isEnabled = true
                    else -> binding.nineButton.isEnabled = true
                }
            }
        }

        if (previousNum != 0) {
            if (boardView!!.checkFilledNumbers(previousNum)) {
                //change string to strikethrough
                when (previousNum) {
                    1 -> binding.oneButton.isEnabled = false
                    2 -> binding.twoButton.isEnabled = false
                    3 -> binding.threeButton.isEnabled = false
                    4 -> binding.fourButton.isEnabled = false
                    5 -> binding.fiveButton.isEnabled = false
                    6 -> binding.sixButton.isEnabled = false
                    7 -> binding.sevenButton.isEnabled = false
                    8 -> binding.eightButton.isEnabled = false
                    else -> binding.nineButton.isEnabled = false
                }
            } else {
                when (previousNum) {
                    1 -> binding.oneButton.isEnabled = true
                    2 -> binding.twoButton.isEnabled = true
                    3 -> binding.threeButton.isEnabled = true
                    4 -> binding.fourButton.isEnabled = true
                    5 -> binding.fiveButton.isEnabled = true
                    6 -> binding.sixButton.isEnabled = true
                    7 -> binding.sevenButton.isEnabled = true
                    8 -> binding.eightButton.isEnabled = true
                    else -> binding.nineButton.isEnabled = true
                }
            }
        }
    }
}
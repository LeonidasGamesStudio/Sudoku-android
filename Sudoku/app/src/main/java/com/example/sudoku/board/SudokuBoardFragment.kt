package com.example.sudoku.board

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.MainActivity
import com.example.sudoku.R
import com.example.sudoku.databinding.FragmentSudokuBoardBinding
import com.google.android.gms.ads.AdRequest


class SudokuBoardFragment : Fragment() {
    private var _binding: FragmentSudokuBoardBinding? = null
    private val binding get() = _binding!!
    private var timeBegin: Long = System.currentTimeMillis()
    private var gameWon: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setDifficultyTitle(arguments?.get("levelNumber") as Int)
        _binding = FragmentSudokuBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setDifficultyTitle(value: Int) {

        val difficultyString = when (value){
            1 -> "Easy"
            2 -> "Medium"
            3 -> "Hard"
            else -> "Expert"
        }
        (requireActivity() as MainActivity).supportActionBar?.title =
            getString(R.string.sudoku_board_fragment_label, difficultyString)
        return
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val difficultyValue = arguments?.get("levelNumber") as Int


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
            binding.sudokuBoardView.pencil = binding.sudokuBoardView.pencil
        }

        binding.undoButton.setOnClickListener{ binding.sudokuBoardView.undoMove()}


        binding.sudokuBoardView.addPresets(difficultyValue as Int)


        val sharedPref = getDefaultSharedPreferences(activity)
        val numbersString = sharedPref.getString("SAVED_NUMBERS", null)
        if (numbersString != null) {
            binding.sudokuBoardView.loadSaveData(numbersString)
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
            val numbersString = binding.sudokuBoardView.getSaveData()
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
        if (binding.sudokuBoardView.getSelectedRow() != -1) {
            val previousNum = binding.sudokuBoardView.checkCurrentNum()
            binding.sudokuBoardView.addNumberToMatrix(number)
            checkForFilledNumbers(number, previousNum)
            if (binding.sudokuBoardView.checkWinCondition()) {
                val timeTaken = System.currentTimeMillis() - timeBegin
                moveToWinScreen(timeTaken)
                return
            }
        }else{
            val text = "No square selected!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }

    private fun moveToWinScreen(timeTaken: Long){
        gameWon = true
        val action = SudokuBoardFragmentDirections.actionSudokuBoardFragmentToWinFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun checkForFilledNumbers(number: Int, previousNum: Int){
        if (number != 0) {
            if (binding.sudokuBoardView.checkFilledNumbers(number)) {
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
            if (binding.sudokuBoardView.checkFilledNumbers(previousNum)) {
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
package com.example.sudoku.menu

import android.os.Bundle
import android.os.SystemClock
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.MainActivity
import com.example.sudoku.R
import com.example.sudoku.databinding.FragmentGameViewBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SudokuBoardFragment : Fragment() {
    private var _binding: FragmentGameViewBinding? = null
    private val binding get() = _binding!!
    private var timeBegin: Long = System.currentTimeMillis()
    private var gameWon: Boolean = false
    private var levelDifficultyValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Sets the title of the fragment. Done dynamically as difficulty is an argument from
        // DifficultySelect but is stored in SharedPref if continuing a game
        setDifficultyTitle()
        _binding = FragmentGameViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Sets the title of the fragment based on the current difficulty level
    private fun setDifficultyTitle() {
        (requireActivity() as MainActivity).supportActionBar?.title =
            getString(R.string.sudoku_board_fragment_label, getDifficultyString())
    }

    // Returns the string for the difficulty title
    private fun getDifficultyString(): String {
        return when (getDifficultyLevel()) {
            1 -> "Easy"
            2 -> "Medium"
            3 -> "Hard"
            else -> "Expert"
        }
    }

    // Returns a difficulty value to determine the difficulty string
    private fun getDifficultyLevel(): Int {
        val sharedPref = getDefaultSharedPreferences(activity)
        val savedDifficulty = sharedPref.getInt("DIFFICULTY", 0)
        levelDifficultyValue = arguments?.getInt("levelNumber")!!
        return if (savedDifficulty != 0) {
            savedDifficulty
        } else {
            // Non-null asserted call as the difficulty select menu AND the continue menu both
            // send an argument
           levelDifficultyValue
        }
    }

    // Runs set up for the game
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpNumberButtons()
        setUpPencilButtons()
        setUpOtherFuncButtons()
        setUpBoard()
        setUpTimer()
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    // Sets click listeners to the number buttons
    private fun setUpNumberButtons() {
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

    // Sets up pencil button and alternate pencil button
    private fun setUpPencilButtons() {
        binding.pencilButton.setOnClickListener{
            binding.pencilButton.visibility = View.GONE
            binding.pencilButtonAlt.visibility = View.VISIBLE
            binding.sudokuBoardView.changePencil()
        }
        binding.pencilButtonAlt.setOnClickListener{
            binding.pencilButtonAlt.visibility = View.GONE
            binding.pencilButton.visibility = View.VISIBLE
            binding.sudokuBoardView.changePencil()
        }
    }

    //Sets up the other functional buttons (Undo, Hint, Eraser)
    private fun setUpOtherFuncButtons() {
        binding.undoButton.setOnClickListener{ binding.sudokuBoardView.undoMove()}
        binding.eraserButton.setOnClickListener{ addNumber(0) }
    }

    //Adds numbers to the board and jumbles them if there is no save data
    private fun setUpBoard() {
        if (levelDifficultyValue != 0) {
            binding.sudokuBoardView.addPresets(levelDifficultyValue)
            binding.sudokuBoardView.jumbleGrid()
        } else {
            loadSaveData()
        }
    }

    private fun loadSaveData() {
        val sharedPref = getDefaultSharedPreferences(activity)
        val numbersString = sharedPref.getString("SAVED_NUMBERS", null)
        if (numbersString != null) {
            binding.sudokuBoardView.loadSaveData(numbersString)
        }
    }

    private fun setUpTimer() {
        val sharedPref = getDefaultSharedPreferences(activity)
        val timerLong = sharedPref.getLong("TIMER_STOPPED", 0L)
        if (timerLong < 0) {
            binding.timer.base = timerLong + SystemClock.elapsedRealtime()
            binding.timer.start()
        }else{
            binding.timer.start()
        }
    }

    override fun onDestroyView() {
        if (!gameWon) {
            val sharedPref = getDefaultSharedPreferences(activity)
            val numbersString = binding.sudokuBoardView.getSaveData()
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString("SAVED_NUMBERS", numbersString)
                    putLong("TIMER_STOPPED", binding.timer.base - SystemClock.elapsedRealtime())
                    if (levelDifficultyValue != 0){
                        putInt("DIFFICULTY", levelDifficultyValue)
                    }
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
                binding.timer.stop()
                val timeTaken = System.currentTimeMillis() - timeBegin
                showWinDialog(timeTaken)
                return
            }
        }else{
            val text = "Please select a cell first!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }

    private fun showWinDialog(timeTaken: Long){
        gameWon = true
        val timeInSecs = timeTaken/1000
        val timeString = DateUtils.formatElapsedTime(timeInSecs)
        MaterialAlertDialogBuilder( requireContext())
            .setTitle(getString(R.string.win_title))
            .setMessage(getString(R.string.win_message, timeString))
            .setCancelable(false)
            .setPositiveButton("Okay") { _, _ ->
                val action = SudokuBoardFragmentDirections.actionSudokuBoardFragmentToMainMenuFragment()
                view?.findNavController()?.navigate(action)
            }.show()
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
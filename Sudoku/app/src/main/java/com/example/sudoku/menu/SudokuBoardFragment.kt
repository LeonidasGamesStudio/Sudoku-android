package com.example.sudoku.menu

import android.os.Bundle
import android.os.SystemClock
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.MainActivity
import com.example.sudoku.R
import com.example.sudoku.board.values.GridValuesViewModel
import com.example.sudoku.board.values.StatsViewModel
import com.example.sudoku.databinding.FragmentGameViewBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SudokuBoardFragment : Fragment() {
    private var _binding: FragmentGameViewBinding? = null
    private val binding get() = _binding!!
    private var timeBegin: Long = System.currentTimeMillis()
    private var gameWon: Boolean = false
    private var levelDifficultyValue = 0
    private val viewModel: GridValuesViewModel by viewModels()

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_view, container, false)
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
        _binding?.viewModel = viewModel
        _binding?.lifecycleOwner = viewLifecycleOwner
        setUpNumberButtons()
        setUpPencilButtons()
        setUpOtherFuncButtons()
        setUpBoard()
        setUpTimer()
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    // Sets click listeners to the number buttons
    private fun setUpNumberButtons() {
        binding.oneButton.setOnClickListener{ numberInput(1) }
        binding.twoButton.setOnClickListener{ numberInput(2) }
        binding.threeButton.setOnClickListener{ numberInput(3) }
        binding.fourButton.setOnClickListener{ numberInput(4) }
        binding.fiveButton.setOnClickListener{ numberInput(5) }
        binding.sixButton.setOnClickListener{ numberInput(6) }
        binding.sevenButton.setOnClickListener{ numberInput(7) }
        binding.eightButton.setOnClickListener{ numberInput(8) }
        binding.nineButton.setOnClickListener{ numberInput(9) }
    }

    // Sets up pencil button and alternate pencil button
    private fun setUpPencilButtons() {
        binding.pencilButton.setOnClickListener{
            binding.pencilButton.visibility = View.GONE
            binding.pencilButtonAlt.visibility = View.VISIBLE
            viewModel.changePencil()
        }
        binding.pencilButtonAlt.setOnClickListener{
            binding.pencilButtonAlt.visibility = View.GONE
            binding.pencilButton.visibility = View.VISIBLE
            viewModel.changePencil()
        }
    }

    //Sets up the other functional buttons (Undo, Hint, Eraser)
    private fun setUpOtherFuncButtons() {
        binding.undoButton.setOnClickListener{ undoMove()}
        binding.eraserButton.setOnClickListener{ binding.viewModel?.eraseCell() }
    }

    private fun undoMove() {
        if (viewModel.undoMove()){
            binding.sudokuBoardView.invalidate()
        } else {
            val text = "No moves left to undo!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }
    //Adds numbers to the board and jumbles them if there is no save data
    private fun setUpBoard() {
        if (levelDifficultyValue != 0) {
            viewModel.addPresets(levelDifficultyValue)
            viewModel.jumbleGrid()
        } else {
            loadSaveData()
            binding.sudokuBoardView.invalidate()
        }
    }

    private fun loadSaveData() {
        val sharedPref = getDefaultSharedPreferences(activity)
        val numbersString = sharedPref.getString("SAVED_NUMBERS", null)
        if (numbersString != null) {
            viewModel.loadSaveData(numbersString)
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
            val numbersString = viewModel.getSaveData()
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


    private fun numberInput (number: Int) {
        val previousNum = viewModel.checkSelectedNum()
        when (viewModel.numberInput(number)) {
            -1 -> {                                  //if no row or column is selected
                val text = "Please select a cell first!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
            0 -> {                                  //if eraser selected
                binding.sudokuBoardView.invalidate()
            }
            1 -> {
                viewModel.addTurns()
                binding.sudokuBoardView.invalidate()
                checkForFilledNumbers(number, previousNum)
                if (viewModel.checkWinCondition()) {
                    binding.timer.stop()
                    val timeTaken = System.currentTimeMillis() - timeBegin
                    showWinDialog(timeTaken)
                }
            }
            else -> {

            }

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
            if (viewModel.checkFilledNumbers(number)) {
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
            if (viewModel.checkFilledNumbers(previousNum)) {
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
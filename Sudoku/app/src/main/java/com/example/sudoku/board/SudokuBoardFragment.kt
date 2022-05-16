package com.example.sudoku.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sudoku.databinding.FragmentSudokuBoardBinding
import com.example.sudoku.menu.WinFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SudokuBoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SudokuBoardFragment : Fragment() {
    private var _binding: FragmentSudokuBoardBinding? = null
    private val binding get() = _binding!!
    private var timeBegin: Long = System.currentTimeMillis()


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
            binding.sudokuBoardView.pencil = !binding.sudokuBoardView.pencil
        }

        MobileAds.initialize(this.requireContext()) {}
        val adRequest = AdRequest.Builder().build()

        binding.adView.loadAd(adRequest)

        val value = arguments?.get("levelNumber")
        binding.sudokuBoardView.addPresets(value as Int)
        binding.timer.start()
        timeBegin = System.currentTimeMillis()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addNumber (number: Int) {
        val previousNum = binding.sudokuBoardView.checkCurrentNum()
        binding.sudokuBoardView.addNumberToMatrix(number)
        checkForFilledNumbers(number, previousNum)
        if (binding.sudokuBoardView.checkWinCondition()){
            val timeTaken = System.currentTimeMillis() - timeBegin
            addWinFragment(timeTaken)
            return
        }
    }

    private fun addWinFragment(timeTaken: Long) {
        val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
// Replace the contents of the container with the new fragment
        ft.replace(com.example.sudoku.R.id.win_nav, WinFragment.newInstance(timeTaken))
// Complete the changes added above
        ft.commit()

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
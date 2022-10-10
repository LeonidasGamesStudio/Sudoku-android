package com.example.sudoku.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.sudoku.R
import com.example.sudoku.databinding.FragmentContinueGameBinding
import com.example.sudoku.databinding.FragmentMainMenuBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ContinueGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContinueGameFragment : Fragment() {

    private var _binding: FragmentContinueGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContinueGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yesButton.setOnClickListener{
            val action = ContinueGameFragmentDirections.actionContinueGameFragmentToSudokuBoardFragment(0)
            view.findNavController().navigate(action)
        }
        binding.noButton.setOnClickListener {
            val sharedPref = getDefaultSharedPreferences(activity)
            with (sharedPref.edit()) {
                putString("SAVED_NUMBERS", null)
                putLong("TIMER_STOPPED", 0L)
                apply()
            }
            val action = ContinueGameFragmentDirections.actionContinueGameFragmentToDifficultySelect()
            view.findNavController().navigate(action)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            ContinueGameFragment().apply {
            }
    }
}
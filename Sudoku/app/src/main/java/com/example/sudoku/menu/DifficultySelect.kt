package com.example.sudoku.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.sudoku.databinding.FragmentDifficultySelectBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DifficultySelect : Fragment() {

    private var _binding: FragmentDifficultySelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDifficultySelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.easyButton.setOnClickListener{
            val action = DifficultySelectDirections.actionDifficultySelectToSudokuBoardFragment(1)
            view.findNavController().navigate(action)
        }
        binding.mediumButton.setOnClickListener{
            val action = DifficultySelectDirections.actionDifficultySelectToSudokuBoardFragment(2)
            view.findNavController().navigate(action)
        }
        binding.hardButton.setOnClickListener{
            val action = DifficultySelectDirections.actionDifficultySelectToSudokuBoardFragment(3)
            view.findNavController().navigate(action)
        }
        binding.expertButton.setOnClickListener{
            val action = DifficultySelectDirections.actionDifficultySelectToSudokuBoardFragment(4)
            view.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DifficulySelect.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DifficultySelect().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
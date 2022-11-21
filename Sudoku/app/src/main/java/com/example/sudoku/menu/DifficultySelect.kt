package com.example.sudoku.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.sudoku.databinding.FragmentDifficultySelectBinding

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

    // Sets up difficulty selection buttons. 4 buttons for 4 levels of difficulty from 1-4
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.easyButton.setOnClickListener{ navigateToBoard(1) }
        binding.mediumButton.setOnClickListener{ navigateToBoard(2) }
        binding.hardButton.setOnClickListener{ navigateToBoard(3) }
        binding.expertButton.setOnClickListener{ navigateToBoard(4) }
    }

    // Navigates to GameView with a difficulty selected. Easy (1), Medium (2), Hard (3), Expert (4)
    private fun navigateToBoard(difficulty: Int){
        view?.findNavController()?.navigate(DifficultySelectDirections
            .actionDifficultySelectToSudokuBoardFragment(difficulty))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
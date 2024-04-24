package com.example.sudoku.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.sudoku.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Sets up the behaviour for the Play button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playButton.setOnClickListener {
            view.findNavController().navigate(determineAction())
        }
    }

    // If there is saved data, navigate to Continue fragment.
    // Else navigate to the Difficulty Select fragment
    private fun determineAction(): NavDirections {
        return if (lookForSaveData() == null) {
            MainMenuFragmentDirections.actionMainMenuFragmentToDifficultySelect()
        } else {
            MainMenuFragmentDirections.actionMainMenuFragmentToContinueGameFragment()
        }
    }

    // Check to see if there is save data
    private fun lookForSaveData(): String? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        return sharedPref.getString("SAVED_NUMBERS", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
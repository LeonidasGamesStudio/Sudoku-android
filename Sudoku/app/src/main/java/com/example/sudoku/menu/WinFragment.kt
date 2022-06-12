package com.example.sudoku.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.sudoku.R
import com.example.sudoku.databinding.FragmentWinBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TIME_TAKEN = "timeTaken"

/**
 * A simple [Fragment] subclass.
 * Use the [WinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var timeTaken: Long? = null
    private var _binding: FragmentWinBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            timeTaken = it.getLong(TIME_TAKEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWinBinding.inflate(inflater, container, false)
        timeTaken = timeTaken?.div(1000)

        val timeString: String = if (timeTaken!! > 60){
            val minutes = timeTaken!! / 60
            val seconds = timeTaken!! % 60
            "$minutes minutes and $seconds seconds"
        }else{
            "${timeTaken.toString()} seconds"
        }

        binding.winTime.text = getString(R.string.time_taken, timeString)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okay.setOnClickListener {
            val action = WinFragmentDirections.actionWinFragmentToMainMenuFragment()
            view.findNavController().navigate(action)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment WinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(timeTaken: Long) =
            WinFragment().apply {
                arguments = Bundle().apply {
                    putLong(TIME_TAKEN, timeTaken)
                }
            }


    }
}
package com.example.sudoku.menu

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.adapter.LetterAdapter
import com.example.sudoku.databinding.FragmentLevelSelectBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LevelSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LevelSelectFragment : Fragment() {

    private var _binding: FragmentLevelSelectBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 4)
        recyclerView.adapter = LetterAdapter()
        recyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
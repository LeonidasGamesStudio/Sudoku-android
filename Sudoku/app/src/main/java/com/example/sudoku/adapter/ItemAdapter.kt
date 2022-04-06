package com.example.sudoku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.R
import com.example.sudoku.menu.LevelSelectFragmentDirections

class LetterAdapter : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    private val dataset = (1).rangeTo(20).toList()

    class LetterViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button = view.findViewById<Button>(R.id.button_item)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return LetterViewHolder(adapterLayout)

    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val item = dataset[position]
        holder.button.text = item.toString()
        holder.button.setOnClickListener {
            val action = LevelSelectFragmentDirections.actionLevelSelectFragmentToSudokuBoardFragment(levelNumber = holder.button.text.toString().toInt())
            holder.view.findNavController().navigate(action)
        }
    }
}
package com.example.sudoku.board.values

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatsViewModel : ViewModel() {
    private var _turns = MutableLiveData(0)
    val turns: LiveData<Int> get() = _turns

    fun addTurns(){
        _turns.value = _turns.value?.plus(1)
    }
}
package com.sayler666.gina.gameoflife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class Data(
    val step: Int,
    val resolution: Int,
    val data: Array<Array<Boolean>>
)

@HiltViewModel
class GameOfLifeViewModel @Inject constructor() : ViewModel() {
    private var initData: Array<Array<Boolean>>

    init {
        initData = generateInitialState()
    }

    private var interrupt = false
    private var step = 0

    val generationsFlow = flow {
        while (true) {
            if (!interrupt) {
                initData = generateNextGeneration(initData)
                emit(Data(step++, resolution = resolution, data = initData))
            }
            delay(50)
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000),
        Data(0, resolution = resolution, data = initData)
    )

    fun restart() {
        step = 0
        interrupt = false
        initData = generateInitialState()
    }

    fun stop() {
        interrupt = true
    }

    fun resume() {
        interrupt = false
    }

    private fun generateNextGeneration(currentGen: Array<Array<Boolean>>): Array<Array<Boolean>> {
        val futureGen = currentGen.clone()
        for (x in 0 until resolution) {
            for (y in 0 until resolution) {
                // finding no Of Neighbours that are alive
                var aliveNeighbours = 0
                for (i in -1..1)
                    for (j in -1..1)
                        if ((x + i in 0 until resolution) && (y + j in 0 until resolution))
                            aliveNeighbours += if (currentGen[x + i][y + j]) 1 else 0

                // The cell needs to be subtracted from
                // its neighbours as it was counted before
                aliveNeighbours -= if (currentGen[x][y]) 1 else 0

                // Rules
                // Cell is lonely and dies
                if (currentGen[x][y] && (aliveNeighbours < 2)) futureGen[x][y] = false
                // Cell dies due to over population
                else if (currentGen[x][y] && (aliveNeighbours > 3)) futureGen[x][y] = false
                // A new cell is born
                else if (!currentGen[x][y] && (aliveNeighbours == 3)) futureGen[x][y] = true
            }
        }

        return currentGen
    }

    private fun generateInitialState() =
        Array(resolution) { Array(resolution) { Math.random() < trueThreshold } }

    companion object {
        const val trueThreshold = 0.08
        const val resolution = 100
    }
}

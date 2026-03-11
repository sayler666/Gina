package com.sayler666.gina.gameoflife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewAction.Back
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnRestartPressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnResumePressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnStopPressed
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RESOLUTION = 100
private const val TRUE_THRESHOLD = 0.08

data class GameOfLifeViewState(
    val step: Int = 0,
    val resolution: Int = RESOLUTION,
    val data: Array<Array<Boolean>> = emptyArray(),
    val isPaused: Boolean = false
)

@HiltViewModel(assistedFactory = GameOfLifeViewModel.Factory::class)
class GameOfLifeViewModel @AssistedInject constructor(
    @Assisted val content: String,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(
        GameOfLifeViewState(data = generateInitialState(content))
    )
    val viewState: StateFlow<GameOfLifeViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    @AssistedFactory
    interface Factory {
        fun create(content: String): GameOfLifeViewModel
    }

    init {
        runSimulation()
    }

    private fun runSimulation() {
        viewModelScope.launch {
            while (true) {
                if (!mutableViewState.value.isPaused) {
                    mutableViewState.update { state ->
                        val next = generateNextGeneration(state.data)
                        state.copy(step = state.step + 1, data = next)
                    }
                }
                delay(50)
            }
        }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnStopPressed -> mutableViewState.update { it.copy(isPaused = true) }
            OnResumePressed -> mutableViewState.update { it.copy(isPaused = false) }
            OnRestartPressed -> mutableViewState.update {
                GameOfLifeViewState(data = generateInitialState(content))
            }
            OnBackPressed -> mutableViewActions.trySend(Back)
        }
    }

    sealed interface ViewEvent {
        data object OnStopPressed : ViewEvent
        data object OnResumePressed : ViewEvent
        data object OnRestartPressed : ViewEvent
        data object OnBackPressed : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
    }

    private fun generateNextGeneration(currentGen: Array<Array<Boolean>>): Array<Array<Boolean>> {
        val futureGen = Array(RESOLUTION) { x -> Array(RESOLUTION) { y -> currentGen[x][y] } }
        for (x in 0 until RESOLUTION) {
            for (y in 0 until RESOLUTION) {
                var aliveNeighbours = 0
                for (i in -1..1)
                    for (j in -1..1)
                        if ((x + i in 0 until RESOLUTION) && (y + j in 0 until RESOLUTION))
                            aliveNeighbours += if (currentGen[x + i][y + j]) 1 else 0
                aliveNeighbours -= if (currentGen[x][y]) 1 else 0
                if (currentGen[x][y] && (aliveNeighbours < 2)) futureGen[x][y] = false
                else if (currentGen[x][y] && (aliveNeighbours > 3)) futureGen[x][y] = false
                else if (!currentGen[x][y] && (aliveNeighbours == 3)) futureGen[x][y] = true
            }
        }
        return futureGen
    }

    private fun generateInitialState(content: String): Array<Array<Boolean>> {
        if (content.isEmpty()) return Array(RESOLUTION) { Array(RESOLUTION) { Math.random() < TRUE_THRESHOLD } }
        return Array(RESOLUTION) { x ->
            Array(RESOLUTION) { y ->
                val index = (x * RESOLUTION + y) % content.length
                content[index].code % 2 == 1
            }
        }
    }

}

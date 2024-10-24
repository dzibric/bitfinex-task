package com.mobtech.bitfinex.task.ui.screen.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class CoreViewModel<State : CoreState, Event : CoreEvent>(
    initialState: State
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventFlow = _eventChannel

    val currentState: State
        get() = _uiState.value

    protected fun updateState(update: (State) -> State) {
        _uiState.update { currentState ->
            update(currentState)
        }
    }

    fun emitEvent(event: Event) = launch {
        _eventChannel.send(event)
    }

    protected suspend fun sendEvent(event: Event) {
        _eventChannel.send(event)
    }

    protected open val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Handle coroutine error
    }

    protected inline fun launch(
        scope: CoroutineScope,
        exceptionHandler: CoroutineExceptionHandler? = null,
        crossinline suspendFunction: suspend CoroutineScope.() -> Unit,
    ) = scope.launch(
        context = exceptionHandler ?: coroutineExceptionHandler
    ) {
        suspendFunction()
    }

    protected inline fun launch(
        exceptionHandler: CoroutineExceptionHandler? = null,
        crossinline suspendFunction: suspend CoroutineScope.() -> Unit
    ) = launch(viewModelScope, exceptionHandler, suspendFunction)
}
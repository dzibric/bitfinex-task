package com.mobtech.bitfinex.task.ui.screen.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun <State : CoreState, Event : CoreEvent, VM : CoreViewModel<State, Event>> CoreComposable(
    viewModel: VM,
    handleEvent: (Event) -> Unit = {},
    content: @Composable (State) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.consumeAsFlow().collect { event ->
            handleEvent(event)
        }
    }

    content(uiState)
}
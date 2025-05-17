package com.junevrtech.smartshelf.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * An implementation of a state management store using `StateFlow` and `Channel`.
 * This store ensures thread-safe state updates using a `Mutex` and sequential
 * processing of state reducers using a `Channel`.
 *
 * @param T The type of the state being managed.
 * @property initialValue The initial value of the state.
 * @property scope The `CoroutineScope` used for launching coroutines.
 */
class StatefulStore<T>(
    initialValue: T, private val
    scope: CoroutineScope
) : CoroutineScope by scope, ModelStore<T> {

    /**
     * The internal mutable state flow holding the current state.
     */
    private val _state = MutableStateFlow(initialValue)

    /**
     * The read-only state flow exposed to the outside world.
     */
    override val state: StateFlow<T> get() = _state.asStateFlow()

    /**
     * The channel used to queue state update operations (reducers).
     */
    private val updateChannel = Channel<(T) -> T>(Channel.UNLIMITED)

    /**
     * The mutex used to ensure thread-safe state updates.
     */
    private val mutex = Mutex()

    /**
     * Initializes the store by launching a coroutine that listens for reducers
     * from the `updateChannel` and applies them to the state sequentially.
     */
    init {
        launch {
            for (reducer in updateChannel) {
                mutex.withLock {
                    _state.update { reducer(it) }
                }
            }
        }
    }

    /**
     * Processes a state update operation by sending the reducer to the `updateChannel`.
     * The reducer will be applied to the current state sequentially.
     *
     * @param reducer A function that takes the current state and returns a new state.
     */
    override fun process(reducer: (T) -> T) {
        launch {
            updateChannel.send(reducer)
        }
    }
}
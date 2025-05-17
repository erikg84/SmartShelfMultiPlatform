package com.junevrtech.smartshelf.store

import kotlinx.coroutines.flow.StateFlow

/**
 * An interface representing a state management store using `StateFlow`.
 * This interface defines the contract for a store that holds a state of type `T`
 * and allows processing of state update operations (reducers).
 *
 * @param T The type of the state being managed.
 */
interface ModelStore<T> {

    /**
     * The read-only state flow that represents the current state.
     */
    val state: StateFlow<T>

    /**
     * Processes a state update operation by applying the given reducer to the current state.
     * The reducer is a function that takes the current state and returns a new state.
     *
     * @param reducer A function that takes the current state and returns a new state.
     */
    fun process(reducer: (T) -> T)
}

package org.dallas.smartshelf.util

class ConsumableEvent<T> {
    private var consumed = false

    var value: T? = null
        set(value) {
            consumed = false
            field = value
        }
        get() {
            if (consumed) {
                return null
            }
            consumed = true
            return field
        }

    companion object {
        fun <T> create(t: T): ConsumableEvent<T> {
            return ConsumableEvent<T>().apply {
                value = t
            }
        }
    }
}

fun <T> ConsumableEvent<T>.handleEvent(action: (T) -> Unit) {
    value?.let { event ->
        action(event)
    }
}
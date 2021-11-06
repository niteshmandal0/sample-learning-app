package com.eidu.integration.sample.app.util

import android.os.SystemClock

/**
 * A stopwatch that sums up the time which passes between invocations of [start] and [stop].
 */
class Stopwatch(
    private val currentTimeMs: () -> Long = SystemClock::elapsedRealtime
) {
    private var accumulatedDurationMs = 0L
    private var timeStartedMs: Long? = null
    private val currentDurationMs get() = timeStartedMs?.let { currentTimeMs() - it } ?: 0

    /**
     * The number of milliseconds that has passed between invocations of [start] and [stop].
     * If the stopwatch is currently running, i.e. [stop] has not been called since the last call
     * of [start], the time between the last call of [start] and the current instant is included.
     */
    val totalDurationMs get() = accumulatedDurationMs + currentDurationMs

    /**
     * Starts the stopwatch. Can be called any number of times. If it is called while the stopwatch
     * is already in its started state, nothing happens.
     */
    fun start() {
        timeStartedMs = timeStartedMs ?: currentTimeMs()
    }

    /**
     * Stops the stopwatch. If it is called while the stopwatch is already in its stopped state,
     * nothing happens.
     */
    fun stop() {
        accumulatedDurationMs += currentDurationMs
        timeStartedMs = null
    }
}

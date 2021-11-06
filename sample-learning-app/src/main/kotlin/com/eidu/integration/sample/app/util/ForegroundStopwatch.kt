package com.eidu.integration.sample.app.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import java.io.Closeable

/**
 * A wrapper for a [Stopwatch] which starts and stops it in accordance with an observed [Lifecycle].
 * By default, that lifecycle is the one of the app, which should be appropriate for most use cases.
 *
 * In cases where this instance is not required anymore _before_ [lifecycle] is stopped, [close]
 * should be invoked so that any allocated resources can be freed up.
 */
class ForegroundStopwatch(
    private val lifecycle: Lifecycle = ProcessLifecycleOwner.get().lifecycle,
    private val stopwatch: Stopwatch = Stopwatch()
) : Closeable {

    /**
     * The number of milliseconds that have elapsed since the instantiation of this class while
     * [lifecycle] was in its _resumed_ state.
     */
    val totalDurationMs get() = stopwatch.totalDurationMs

    private val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> stopwatch.start()
            Lifecycle.Event.ON_PAUSE -> stopwatch.stop()
            Lifecycle.Event.ON_STOP -> close()
            else -> {}
        }
    }

    init {
        lifecycle.addObserver(observer)
        if (lifecycle.currentState == Lifecycle.State.RESUMED)
            stopwatch.start()
    }

    /**
     * Stops the observation of [lifecycle]. After this method is invoked, [totalDurationMs] will
     * not change anymore.
     */
    override fun close() {
        lifecycle.removeObserver(observer)
        stopwatch.stop()
    }
}

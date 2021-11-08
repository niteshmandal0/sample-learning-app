package com.eidu.integration.sample.app.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ForegroundStopwatchTest {
    private var lifecycleCurrentState = Lifecycle.State.INITIALIZED
    private lateinit var lifecycleObserver: LifecycleEventObserver

    private val stopwatch: Stopwatch = mockk(relaxUnitFun = true) {
        every { totalDurationMs } returns TOTAL_DURATION_MS
    }

    private val lifecycle: Lifecycle = mockk(relaxUnitFun = true) {
        every { currentState } answers { lifecycleCurrentState }
        every { addObserver(any()) } answers { lifecycleObserver = firstArg() }
    }

    private val lifecycleOwner = LifecycleOwner { lifecycle }

    private val foregroundStopwatch by lazy { ForegroundStopwatch(lifecycle, stopwatch) }

    @Test
    fun `returns elapsed time from underlying stopwatch`() {
        assertThat(foregroundStopwatch.totalDurationMs).isEqualTo(TOTAL_DURATION_MS)
    }

    @Test
    fun `starts stopwatch on instantiation if lifecycle is in resumed state`() {
        `given current lifecycle state`(Lifecycle.State.RESUMED)
        `when instantiating ForegroundStopwatch`()
        `then stopwatch is started`()
    }

    @Test
    fun `does not start stopwatch on instantiation if lifecycle is not in resumed state`() {
        `given current lifecycle state`(Lifecycle.State.CREATED)
        `when instantiating ForegroundStopwatch`()
        `then stopwatch is not started`()
    }

    @Test
    fun `starts stopwatch when lifecycle is resumed`() {
        `when instantiating ForegroundStopwatch`()
        `when lifecycle event occurs`(Lifecycle.Event.ON_RESUME)
        `then stopwatch is started`()
    }

    @Test
    fun `stops stopwatch when lifecycle is paused`() {
        `when instantiating ForegroundStopwatch`()
        `when lifecycle event occurs`(Lifecycle.Event.ON_PAUSE)
        `then stopwatch is stopped`()
    }

    @Test
    fun `stops stopwatch and removes lifecycle observer`() {
        `given current lifecycle state`(Lifecycle.State.RESUMED)
        `when instantiating ForegroundStopwatch`()
        `when closing ForegroundStopwatch`()
        `then stopwatch is stopped`()
        `then lifecycle observer is removed`()
    }

    private fun `given current lifecycle state`(state: Lifecycle.State) = every { lifecycle.currentState } returns state

    private fun `when instantiating ForegroundStopwatch`() = foregroundStopwatch.run { }
    private fun `when closing ForegroundStopwatch`() = foregroundStopwatch.close()

    private fun `when lifecycle event occurs`(event: Lifecycle.Event) =
        lifecycleObserver.onStateChanged(lifecycleOwner, event)

    private fun `then stopwatch is started`() = verify(exactly = 1) { stopwatch.start() }
    private fun `then stopwatch is not started`() = verify(exactly = 0) { stopwatch.start() }
    private fun `then stopwatch is stopped`() = verify(exactly = 1) { stopwatch.stop() }

    private fun `then lifecycle observer is removed`() =
        verify(exactly = 1) { lifecycle.removeObserver(lifecycleObserver) }

    companion object {
        private const val TOTAL_DURATION_MS = 5L
    }
}

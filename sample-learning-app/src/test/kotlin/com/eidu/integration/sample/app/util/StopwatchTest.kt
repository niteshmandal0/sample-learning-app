package com.eidu.integration.sample.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class StopwatchTest {
    private var currentTime = 0L
    private val stopwatch = Stopwatch { currentTime }

    @Test
    fun `returns 0 if never started`() {
        currentTime = 5

        assertThat(stopwatch.totalDurationMs).isEqualTo(0)
    }

    @Test
    fun `measures time since single start`() {
        stopwatch.start()
        currentTime = 5

        assertThat(stopwatch.totalDurationMs).isEqualTo(5)
    }

    @Test
    fun `measures time in single interval`() {
        stopwatch.start()
        currentTime = 5
        stopwatch.stop()

        assertThat(stopwatch.totalDurationMs).isEqualTo(5)
    }

    @Test
    fun `measures time in multiple intervals`() {
        stopwatch.start()
        currentTime = 5
        stopwatch.stop()

        currentTime = 8
        stopwatch.start()
        currentTime = 9
        stopwatch.stop()

        assertThat(stopwatch.totalDurationMs).isEqualTo(6)
    }

    @Test
    fun `measures time in multiple intervals plus time since last start`() {
        stopwatch.start()
        currentTime = 5
        stopwatch.stop()

        currentTime = 8
        stopwatch.start()
        currentTime = 9
        stopwatch.stop()

        currentTime = 20
        stopwatch.start()
        currentTime = 22

        assertThat(stopwatch.totalDurationMs).isEqualTo(8)
    }
}

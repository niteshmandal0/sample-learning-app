package com.eidu.integration.sample.app.ui

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.eidu.integration.ResultItem
import com.eidu.integration.RunLearningUnitResult
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class LearningUnitRunViewModelTest {
    private val viewModel = LearningUnitRunViewModel(
        mockk { every { totalDurationMs } returns ELAPSED_FOREGROUND_TIME_MS }
    ).apply {
        score = SCORE
        additionalData = ADDITIONAL_DATA
        errorDetails = ERROR_DETAILS
    }

    @Test
    fun `creates Success result`() {
        viewModel.resultType = RunLearningUnitResult.ResultType.Success
        assertThat(viewModel.getResult())
            .isEqualTo(
                RunLearningUnitResult.ofSuccess(
                    SCORE, ELAPSED_FOREGROUND_TIME_MS, ADDITIONAL_DATA,
                    listOf(
                        ResultItem("item1", "1 + 2", "4", "3", 0.5f, 500L, 100L),
                        ResultItem("item2", "3 + 4", "7", "7", 1f, 1000L, 200L),
                    )
                )
            )
    }

    @Test
    fun `creates Abort result`() {
        viewModel.resultType = RunLearningUnitResult.ResultType.Abort
        assertThat(viewModel.getResult())
            .isEqualTo(RunLearningUnitResult.ofAbort(SCORE, ELAPSED_FOREGROUND_TIME_MS, ADDITIONAL_DATA, emptyList()))
    }

    @Test
    fun `creates TimeUp result`() {
        viewModel.resultType = RunLearningUnitResult.ResultType.TimeUp
        assertThat(viewModel.getResult())
            .isEqualTo(RunLearningUnitResult.ofTimeUp(SCORE, ELAPSED_FOREGROUND_TIME_MS, ADDITIONAL_DATA, emptyList()))
    }

    @Test
    fun `creates TimeoutInactivity result`() {
        viewModel.resultType = RunLearningUnitResult.ResultType.TimeoutInactivity
        assertThat(viewModel.getResult())
            .isEqualTo(RunLearningUnitResult.ofTimeoutInactivity(SCORE, ELAPSED_FOREGROUND_TIME_MS, ADDITIONAL_DATA, emptyList()))
    }

    @Test
    fun `creates Error result`() {
        viewModel.resultType = RunLearningUnitResult.ResultType.Error
        assertThat(viewModel.getResult())
            .isEqualTo(RunLearningUnitResult.ofError(SCORE, ELAPSED_FOREGROUND_TIME_MS, ERROR_DETAILS, ADDITIONAL_DATA, null))
    }

    companion object {
        private const val SCORE = 0.5f
        private const val ELAPSED_FOREGROUND_TIME_MS = 5000L
        private const val ADDITIONAL_DATA = "additional data"
        private const val ERROR_DETAILS = "error details"
    }
}

package com.eidu.integration.sample.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eidu.integration.RunLearningUnitRequest
import com.eidu.integration.RunLearningUnitResult
import com.eidu.integration.sample.app.util.ForegroundStopwatch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This ViewModel backs the information that is displayed by and can be edited in [MainActivity].
 */
@HiltViewModel
class LearningUnitRunViewModel @Inject constructor() : ViewModel() {
    private val stopwatch = ForegroundStopwatch()

    /**
     * The request that was passed to us by the EIDU app.
     */
    lateinit var request: RunLearningUnitRequest

    /**
     * The number of milliseconds that have passed since the creation of this instance while
     * the app was in the foreground.
     */
    val elapsedForegroundTimeMs get() = stopwatch.totalDurationMs

    /**
     * The [RunLearningUnitResult.ResultType] to return to the EIDU app.
     */
    var resultType by mutableStateOf(RunLearningUnitResult.ResultType.Success)

    /**
     * The score to return to the EIDU app. Must be between 0 and 1.
     */
    var score by mutableStateOf(0f)

    /**
     * If [resultType] is [RunLearningUnitResult.ResultType.Error], optional diagnostic information
     * to return to the EIDU app.
     */
    var errorDetails by mutableStateOf("")

    /**
     * Optionally, an arbitrary string to be included in the reporting of the result.
     */
    var additionalData by mutableStateOf("")

    /**
     * Constructs and returns a [RunLearningUnitResult] from the current state of this instance.
     */
    fun getResult(): RunLearningUnitResult = when (resultType) {
        RunLearningUnitResult.ResultType.Success ->
            RunLearningUnitResult.ofSuccess(
                score,
                elapsedForegroundTimeMs,
                additionalData
            )

        RunLearningUnitResult.ResultType.Abort ->
            RunLearningUnitResult.ofAbort(score, elapsedForegroundTimeMs, additionalData)

        RunLearningUnitResult.ResultType.Error ->
            RunLearningUnitResult.ofError(
                elapsedForegroundTimeMs,
                errorDetails,
                additionalData
            )

        RunLearningUnitResult.ResultType.TimeUp ->
            RunLearningUnitResult.ofTimeUp(
                score,
                elapsedForegroundTimeMs,
                additionalData
            )

        RunLearningUnitResult.ResultType.TimeoutInactivity ->
            RunLearningUnitResult.ofTimeoutInactivity(
                score,
                elapsedForegroundTimeMs,
                additionalData
            )
    }
}

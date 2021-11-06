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

@HiltViewModel
class LearningUnitRunViewModel @Inject constructor() : ViewModel() {
    private val stopwatch = ForegroundStopwatch()

    lateinit var request: RunLearningUnitRequest
    val elapsedForegroundTimeMs get() = stopwatch.totalDurationMs
    var resultType by mutableStateOf(RunLearningUnitResult.ResultType.Success)
    var score by mutableStateOf(0f)
    var errorDetails by mutableStateOf("")
    var additionalData by mutableStateOf("")

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

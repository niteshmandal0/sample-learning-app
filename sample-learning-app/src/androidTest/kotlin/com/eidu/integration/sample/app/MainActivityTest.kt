package com.eidu.integration.sample.app

import android.app.Activity.RESULT_OK
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.center
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThan
import com.eidu.integration.RunLearningUnitRequest
import com.eidu.integration.RunLearningUnitResult
import com.eidu.integration.sample.app.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var device: UiDevice

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        scenario = ActivityScenario.launch(INTENT)
    }

    @Test
    fun displaysRequestInformation() {
        composeTestRule.onNodeWithText("Expand").performClick()
        composeTestRule.onNodeWithText(LEARNING_UNIT_ID).assertIsDisplayed()
        composeTestRule.onNodeWithText(LEARNING_UNIT_RUN_ID).assertIsDisplayed()
        composeTestRule.onNodeWithText(LEARNER_ID).assertIsDisplayed()
        composeTestRule.onNodeWithText(SCHOOL_ID).assertIsDisplayed()
        composeTestRule.onNodeWithText(STAGE).assertIsDisplayed()
        composeTestRule.onNodeWithText(REMAINING_FOREGROUND_TIME_MS.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(INACTIVITY_TIMEOUT_MS.toString()).assertIsDisplayed()
    }

    @Test
    fun returnsSuccessResult() = returnsRegularResult(RunLearningUnitResult.ResultType.Success)

    @Test
    fun returnsAbortResult() = returnsRegularResult(RunLearningUnitResult.ResultType.Abort)

    @Test
    fun returnsTimeUpResult() = returnsRegularResult(RunLearningUnitResult.ResultType.TimeUp)

    @Test
    fun returnsTimeoutInactivityResult() = returnsRegularResult(RunLearningUnitResult.ResultType.TimeoutInactivity)

    @Test
    fun returnsErrorResult() {
        composeTestRule.onNodeWithTag("ResultTypeError").performClick()
        composeTestRule.onNodeWithTag("ErrorDetails").performTextInput(ERROR_DETAILS)
        composeTestRule.onNodeWithTag("AdditionalData").performTextInput(ADDITIONAL_DATA)
        composeTestRule.onNodeWithTag("SendResultButton").performClick()
        assertThat(scenario.result.resultCode).isEqualTo(RESULT_OK)
        with(RunLearningUnitResult.fromIntent(scenario.result.resultData)) {
            assertThat(resultType).isEqualTo(RunLearningUnitResult.ResultType.Error)
            assertThat(foregroundDurationInMs).isLessThan(5000)
            assertThat(errorDetails).isEqualTo(ERROR_DETAILS)
            assertThat(additionalData).isEqualTo(ADDITIONAL_DATA)
        }
    }

    private fun returnsRegularResult(resultType: RunLearningUnitResult.ResultType) {
        composeTestRule.onNodeWithTag("ResultType$resultType").performClick()
        composeTestRule.onNodeWithTag("Score").performGesture { click(center) }
        composeTestRule.onNodeWithTag("AdditionalData").performTextInput(ADDITIONAL_DATA)
        composeTestRule.onNodeWithTag("SendResultButton").performClick()
        assertThat(scenario.result.resultCode).isEqualTo(RESULT_OK)
        with(RunLearningUnitResult.fromIntent(scenario.result.resultData)) {
            assertThat(resultType).isEqualTo(resultType)
            assertThat(score).isCloseTo(0.5f, 0.05f)
            assertThat(foregroundDurationInMs).isLessThan(5000)
            assertThat(additionalData).isEqualTo(ADDITIONAL_DATA)
        }
    }

    companion object {
        private const val LEARNING_UNIT_ID = "LEARNING_UNIT_ID"
        private const val LEARNING_UNIT_RUN_ID = "LEARNING_UNIT_RUN_ID"
        private const val LEARNER_ID = "LEARNER_ID"
        private const val SCHOOL_ID = "SCHOOL_ID"
        private const val STAGE = "STAGE"
        private const val ADDITIONAL_DATA = "ADDITIONAL_DATA"
        private const val ERROR_DETAILS = "ERROR_DETAILS"
        private const val REMAINING_FOREGROUND_TIME_MS = 300_000L
        private const val INACTIVITY_TIMEOUT_MS = 30_000L

        private val REQUEST = RunLearningUnitRequest.of(
            LEARNING_UNIT_ID,
            LEARNING_UNIT_RUN_ID,
            LEARNER_ID,
            SCHOOL_ID,
            STAGE,
            REMAINING_FOREGROUND_TIME_MS,
            INACTIVITY_TIMEOUT_MS
        )

        private val INTENT = REQUEST.toIntent(
            "com.eidu.integration.sample.app",
            MainActivity::class.qualifiedName ?: error("Failed to get name of activity class")
        )
    }
}

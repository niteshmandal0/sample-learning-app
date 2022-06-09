package com.eidu.integration.sample.app.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eidu.integration.RunLearningUnitRequest
import com.eidu.integration.RunLearningUnitResult
import com.eidu.integration.sample.app.R
import com.eidu.integration.sample.app.theme.EIDUIntegrationSampleAppTheme
import com.eidu.integration.sample.app.shared.EiduScaffold
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.text.DecimalFormat

/**
 * This is the activity that is launched by the EIDU app with the request to run a learning unit.
 */
@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LearningUnitRunViewModel by viewModels()

    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
          This is where we extract a RunLearningUnitRequest from the calling intent so that we
          can use its information to control the subsequent behaviour of our app.
         */
        try {
            val request = RunLearningUnitRequest.fromIntent(intent)
            if (request == null) {
                Log.d(TAG, "onCreate: launch intent is not a request to launch a learning unit: $intent")
                finish()
                return
            }
            viewModel.request = request
        } catch (e: IllegalArgumentException) {
            // If we couldn't parse the intent, return a useful error result.
            Log.e(TAG, "onCreate: invalid launch intent: $intent", e)
            sendResult(RunLearningUnitResult.ofError(null, 0L, "Invalid Intent received: $intent", null, null))
            return
        }

        /*
          Create a UI that allows viewing the request, and editing and returning the result to the
          EIDU app.
         */
        setContent {
            EIDUIntegrationSampleAppTheme {
                EiduScaffold(title = { Text(stringResource(R.string.runOf, viewModel.request.learningUnitId)) }) {
                    val scrollState = ScrollState(0)
                    Column(Modifier.verticalScroll(scrollState, true)) {
                        RequestData()
                        Assets()
                        ResultData()
                        SendResultButton()
                    }
                }
            }
        }
    }

    /**
     * Composes a list that describes the information from our request.
     */
    @Composable
    private fun RequestData() {
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.padding(5.dp)
        ) {
            Column {
                var expanded by remember { mutableStateOf(false) }
                ListItem(
                    text = { Text(stringResource(R.string.requestDataTitle)) }
                )
                Divider()
                if (expanded) {
                    ListItem(
                        text = { Text(viewModel.request.learningUnitId) },
                        secondaryText = { Text(stringResource(R.string.learningUnitId)) }
                    )
                    ListItem(
                        text = { Text(viewModel.request.learningUnitRunId) },
                        secondaryText = { Text(stringResource(R.string.learningUnitRunId)) }
                    )
                    ListItem(
                        text = { Text(viewModel.request.learnerId) },
                        secondaryText = { Text(stringResource(R.string.learnerId)) }
                    )
                    ListItem(
                        text = { Text(viewModel.request.schoolId) },
                        secondaryText = { Text(stringResource(R.string.schoolId)) }
                    )
                    ListItem(
                        text = { Text(viewModel.request.stage) },
                        secondaryText = { Text(stringResource(R.string.stage)) }
                    )
                    ListItem(
                        text = {
                            Text(
                                viewModel.request.remainingForegroundTimeInMs?.let { stringResource(R.string.ms, it) }
                                    ?: stringResource(R.string.none)
                            )
                        },
                        secondaryText = { Text(stringResource(R.string.remainingForegroundTime)) }
                    )
                    ListItem(
                        text = {
                            Text(
                                viewModel.request.inactivityTimeoutInMs?.let { stringResource(R.string.ms, it) }
                                    ?: stringResource(R.string.none)
                            )
                        },
                        secondaryText = { Text(stringResource(R.string.inactivityTimeout)) }
                    )
                    Divider()
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) stringResource(R.string.collapse) else stringResource(R.string.expand))
                }
            }
        }
    }

    /**
     * Composes a list that shows the available assets.
     */
    @Composable
    private fun Assets() {
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.padding(5.dp)
        ) {
            Column {
                var expanded by remember { mutableStateOf(false) }
                ListItem(
                    text = { Text(stringResource(R.string.assetsTitle)) }
                )
                Divider()
                if (expanded) {
                    Row(
                        modifier = Modifier.padding(5.dp).align(CenterHorizontally)
                    ) {
                        Text(viewModel.getTextAsset(applicationContext, TEXT_ASSET))
                    }
                    Row(
                        modifier = Modifier.padding(5.dp).align(CenterHorizontally)
                    ) {
                        Image(
                            viewModel.getImageAsset(applicationContext, IMAGE_ASSET).asImageBitmap(),
                            null,
                            modifier = Modifier.fillMaxSize(0.5f)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(5.dp).align(CenterHorizontally)
                    ) {
                        TextButton(onClick = ::playAudio) { Text(stringResource(R.string.playAudio)) }
                    }
                    Divider()
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) stringResource(R.string.collapse) else stringResource(R.string.expand))
                }
            }
        }
    }

    private fun playAudio() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(applicationContext, viewModel.getAssetUri(AUDIO_ASSET))
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    /**
     * Composes a form that can be used to edit the result to return to the EIDU app.
     */
    @Composable
    private fun ResultData() {
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.padding(5.dp)
        ) {
            Column {
                ListItem(text = { Text(stringResource(R.string.resultDataTitle)) })
                ResultType()
                Score()
                ElapsedForegroundTime()
                ErrorDetails()
                AdditionalData()
            }
        }
    }

    @Composable
    private fun ResultType() {
        Column(Modifier.selectableGroup()) {
            RunLearningUnitResult.ResultType.values()
                .forEach { result ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (result == viewModel.resultType),
                                onClick = { viewModel.resultType = result },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp)
                            .testTag("ResultType$result"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (result == viewModel.resultType),
                            onClick = null
                        )
                        Text(
                            text = result.toString(),
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
        }
    }

    @Composable
    private fun Score() {
        if (viewModel.resultType != RunLearningUnitResult.ResultType.Error) {
            Row {
                ListItem(
                    text = { Text(DecimalFormat("0.00").format(viewModel.score)) },
                    secondaryText = { Text(stringResource(R.string.score)) },
                    modifier = Modifier.fillMaxWidth(0.3f)
                )
                Slider(
                    value = viewModel.score,
                    onValueChange = { viewModel.score = it },
                    modifier = Modifier
                        .padding(5.dp, 0.dp)
                        .fillMaxWidth(1f)
                        .testTag("Score")
                )
            }
        }
    }

    @Composable
    private fun ElapsedForegroundTime() {
        var elapsedForegroundTimeMs by remember { mutableStateOf(0L) }

        LaunchedEffect(
            key1 = true,
            block = {
                while (true) {
                    elapsedForegroundTimeMs = viewModel.elapsedForegroundTimeMs
                    delay(100)
                }
            }
        )

        ListItem(
            text = { Text(stringResource(R.string.ms, elapsedForegroundTimeMs)) },
            secondaryText = { Text(stringResource(R.string.foregroundTime)) }
        )
    }

    @Composable
    private fun ErrorDetails() {
        if (viewModel.resultType == RunLearningUnitResult.ResultType.Error) {
            OutlinedTextField(
                value = viewModel.errorDetails,
                onValueChange = { viewModel.errorDetails = it },
                label = { Text(stringResource(R.string.errorDetails)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .testTag("ErrorDetails")
            )
        }
    }

    @Composable
    private fun AdditionalData() {
        OutlinedTextField(
            value = viewModel.additionalData,
            onValueChange = { viewModel.additionalData = it },
            label = { Text(stringResource(R.string.additionalData)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .testTag("AdditionalData")
        )
    }

    /**
     * Composes a button that, when clicked, turns our form data into a result that is returned
     * to the EIDU app.
     */
    @Composable
    private fun SendResultButton() {
        Button(
            onClick = { sendResult(viewModel.getResult()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .testTag("SendResultButton")
        ) {
            Text(stringResource(R.string.sendResult))
        }
    }

    /**
     * Returns a [RunLearningUnitResult] to the EIDU app and finishes the activity.
     */
    private fun sendResult(result: RunLearningUnitResult) {
        setResult(RESULT_OK, result.toIntent())
        finish()
    }

    companion object {
        private const val TEXT_ASSET = "text.txt"
        private const val AUDIO_ASSET = "subfolder/audio.mp3"
        private const val IMAGE_ASSET = "subfolder/image.jpg"
        private val TAG = MainActivity::class.simpleName
    }
}

package com.eidu.integration.sample.app.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eidu.integration.RunLearningUnitRequest
import com.eidu.integration.RunLearningUnitResult
import com.eidu.integration.sample.app.theme.EIDUIntegrationSampleAppTheme
import com.eidu.integration.sample.app.shared.EiduScaffold
import java.text.DecimalFormat
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {

    private val viewModel: LearningUnitRunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            viewModel.request = RunLearningUnitRequest.fromIntent(intent)
        } catch (e: IllegalArgumentException) {
            Log.e("MainActivity", "onCreate: invalid launch intent: $intent", e)
            setResult(
                RESULT_OK,
                RunLearningUnitResult.ofError(
                    0L,
                    "Invalid Intent received: $intent",
                    null
                ).toIntent()
            )
            finish()
            return
        }

        setContent {
            EIDUIntegrationSampleAppTheme {
                EiduScaffold(title = { Text("Run of ${viewModel.request.learningUnitId}") }) {
                    val scrollState = ScrollState(0)
                    Column(Modifier.verticalScroll(scrollState, true)) {
                        RequestData()
                        ResultData()
                        SendResultButton()
                    }
                }
            }
        }
    }

    @Composable
    private fun RequestData() {
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.padding(5.dp)
        ) {
            Column {
                var expanded by remember { mutableStateOf(false) }
                ListItem(
                    text = { Text("Request Data") }
                )
                Divider()
                if (expanded) {
                    ListItem(
                        text = { Text(viewModel.request.learningUnitId) },
                        secondaryText = { Text("Learning Unit ID") }
                    )
                    ListItem(
                        text = { Text(viewModel.request.learningUnitRunId) },
                        secondaryText = { Text("Learning Unit Run ID") }
                    )
                    ListItem(
                        text = { Text(viewModel.request.learnerId) },
                        secondaryText = { Text("Learner ID") }
                    )
                    ListItem(
                        text = { Text(viewModel.request.schoolId) },
                        secondaryText = { Text("School ID") }
                    )
                    ListItem(
                        text = { Text(viewModel.request.stage) },
                        secondaryText = { Text("Stage") }
                    )
                    ListItem(
                        text = { Text("${viewModel.request.remainingForegroundTimeInMs}") },
                        secondaryText = { Text("Remaining Foreground Time") }
                    )
                    ListItem(
                        text = { Text("${viewModel.request.inactivityTimeoutInMs}") },
                        secondaryText = { Text("Inactivity Timeout") }
                    )
                    Divider()
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Collapse" else "Expand")
                }
            }
        }
    }

    @Composable
    private fun ResultData() {
        Card(
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.padding(5.dp)
        ) {
            Column {
                LaunchedEffect(
                    key1 = true,
                    block = {
                        foregroundTimeTimer {
                            viewModel.elapsedForegroundTimeMs = it
                        }
                    }
                )
                ListItem(
                    text = { Text("Result Data") }
                )
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
                                    .padding(horizontal = 16.dp),
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
                if (viewModel.resultType != RunLearningUnitResult.ResultType.Error) {
                    Row {
                        ListItem(
                            text = { Text(DecimalFormat("0.00").format(viewModel.score)) },
                            secondaryText = { Text("Score") },
                            modifier = Modifier.fillMaxWidth(0.3f)
                        )
                        Slider(
                            value = viewModel.score,
                            onValueChange = { viewModel.score = it },
                            modifier = Modifier
                                .padding(5.dp, 0.dp)
                                .fillMaxWidth(1f)
                        )
                    }
                }
                ListItem(
                    text = { Text("${viewModel.elapsedForegroundTimeMs}") },
                    secondaryText = { Text("Foreground Time") }
                )
                if (viewModel.resultType == RunLearningUnitResult.ResultType.Error) {
                    OutlinedTextField(
                        value = viewModel.errorDetails,
                        onValueChange = { viewModel.errorDetails = it },
                        label = { Text("Error Details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                }
                OutlinedTextField(
                    value = viewModel.additionalData,
                    onValueChange = { viewModel.additionalData = it },
                    label = { Text("Additional Data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
            }
        }
    }

    @Composable
    private fun SendResultButton() {
        Button(
            onClick = { sendResult(viewModel.getResult()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text("Send Result")
        }
    }

    private fun sendResult(result: RunLearningUnitResult) {
        setResult(RESULT_OK, result.toIntent())
        finish()
    }

    private fun foregroundTimeTimer(updateState: (Long) -> Unit) =
        Timer().schedule(
            object : TimerTask() {
                val startTime = System.currentTimeMillis()
                override fun run() {
                    updateState(System.currentTimeMillis() - startTime)
                }
            },
            0L,
            1000L
        )
}

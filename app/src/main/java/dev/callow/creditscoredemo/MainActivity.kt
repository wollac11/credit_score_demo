package dev.callow.creditscoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import dev.callow.creditscoredemo.ui.CreditReportUiState
import dev.callow.creditscoredemo.ui.CreditScoreViewModel
import dev.callow.creditscoredemo.ui.theme.CreditScoreDemoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CreditScoreViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreditScoreDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
                    }
                ) { innerPadding ->
                    val uiState by viewModel.creditReport.collectAsState()
                    CreditScoreScreen(
                        uiState = uiState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        viewModel.fetchCreditReport()
    }
}

@Composable
fun CreditScoreScreen(uiState: CreditReportUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is CreditReportUiState.Loading -> {
                CircularProgressIndicator()
            }

            is CreditReportUiState.Success -> {
                val creditInfo = uiState.report.creditReportInfo
                DonutView(
                    value = creditInfo.score,
                    maxValue = creditInfo.maxScoreValue,
                    label = "Your credit score is:"
                )
            }

            is CreditReportUiState.Error -> {
                Text(
                    text = "Error: ${uiState.message}"
                )
            }
        }
    }
}

@Composable
fun DonutView(value: Int, maxValue: Int, strokeWidth: Dp = 20.dp, size: Dp = 250.dp, label: String = "") {
    val angle = 360f * value / maxValue.toFloat()

    Box(contentAlignment = Alignment.Center, modifier = Modifier.requiredSize(size)) {
        Canvas(modifier = Modifier.size(size)) {
            // Background arc
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx())
            )
            // Foreground arc
            drawArc(
                color = Color.Blue,
                startAngle = -90f, // Start from the top
                sweepAngle = angle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column {
            // Label
            Text(
                text = label,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = (size.value / 14).dp.value.toInt().sp // Adjust font size based on diameter
            )
            // Value
            Text(
                text = value.toString(),
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 4).dp.value.toInt().sp // Adjust font size based on diameter
            )
            // Max value
            Text(
                text = "out of $maxValue",
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = (size.value / 14).dp.value.toInt().sp // Adjust font size based on diameter
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DonutViewPreview() {
    CreditScoreDemoTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            DonutView(value = 327, maxValue = 700, label = "Your credit score is:")
        }
    }
}
package dev.callow.creditscoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
                    label = stringResource(R.string.credit_score_label)
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
fun DonutView(value: Int, maxValue: Int, strokeWidth: Dp = 4.dp, size: Dp = 250.dp, label: String = "") {
    val angle = remember(value, maxValue) { 360f * value / maxValue.toFloat() }
    val scoreColor = determineScoreColor(value = value, maxValue = maxValue) // Determine colour based on score
    var animationPlayed by remember { mutableStateOf(false) }

    // Score arc animation (run when first displayed or value changes)
    val animatedAngle by animateFloatAsState(
        targetValue = if (animationPlayed) angle else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "DonutViewAngleAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.requiredSize(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            // Outer ring
            drawArc(
                color = Color(0xFF757575),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx)
            )
            // Inner arc (representing the score)
            val inset = strokeWidthPx * 2 // Define an inset for the inner ring to create a gap
            drawArc(
                color = scoreColor,
                startAngle = -90f, // Start from the top
                sweepAngle = animatedAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - 2 * inset, this.size.height - 2 * inset)
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
                color = scoreColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Light,
                fontSize = (size.value / 4).dp.value.toInt().sp // Adjust font size based on diameter
            )
            // Max value
            Text(
                text = stringResource(R.string.total_score_label, maxValue),
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
            DonutView(value = 327, maxValue = 700, label = stringResource(R.string.credit_score_label))
        }
    }
}

@Composable
fun determineScoreColor(value: Int, maxValue: Int): Color {
    if (maxValue == 0) return Color.Gray // Handle division by zero or invalid max
    val percentage = value.toFloat() / maxValue
    return when {
        percentage <= 0.25f -> Color(0xFFE57373) // Red
        percentage <= 0.50f -> Color(0xFFFFB74D) // Orange
        percentage <= 0.75f -> Color(0xFFFFF176) // Yellow
        else -> Color(0xFF81C784) // Green
    }
}
package dev.callow.creditscoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
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
                Text(
                    text = "Your credit score is: ${uiState.report.creditReportInfo.score}"
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

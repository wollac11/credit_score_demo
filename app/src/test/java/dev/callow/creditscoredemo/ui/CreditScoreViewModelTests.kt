import android.content.Context
import android.util.Log
import app.cash.turbine.test
import dev.callow.creditscoredemo.data.model.CoachingSummary
import dev.callow.creditscoredemo.data.model.CreditReportInfo
import dev.callow.creditscoredemo.data.model.CreditReportResponse
import dev.callow.creditscoredemo.data.repository.CreditReportRepository
import dev.callow.creditscoredemo.ui.CreditReportUiState
import dev.callow.creditscoredemo.ui.CreditScoreViewModel
import dev.callow.creditscoredemo.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CreditScoreViewModelTests {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val mockErrorMessage: String = "Mocked error message"

    private lateinit var viewModel: CreditScoreViewModel
    private lateinit var mockRepository: CreditReportRepository
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockkStatic(Log::class)

        // Capture and print Log.d calls
        every { Log.d(any(), any()) } answers {
            val tag = firstArg<String>()
            val msg = secondArg<String>()
            println("LOG.D (mock): $tag: $msg")
            0
        }

        mockRepository = mockk()
        mockContext = mockk()
        // This simulates applicationContext.getString(R.string.fetch_error) in the VM
        every { mockContext.getString(any()) } returns mockErrorMessage
    }

    @Test
    fun `fetchCreditReport success transitions from Loading to Success`() = runTest {
        val mockCreditReportInfo = CreditReportInfo(
            score = 514,
            scoreBand = 4,
            clientRef = "CS-SED-655426-708782",
            status = "MATCH",
            maxScoreValue = 700,
            minScoreValue = 0,
            monthsSinceLastDefaulted = -1,
            hasEverDefaulted = false,
            monthsSinceLastDelinquent = 1,
            hasEverBeenDelinquent = true,
            percentageCreditUsed = 44,
            percentageCreditUsedDirectionFlag = 1,
            changedScore = 0,
            currentShortTermDebt = 13758,
            currentShortTermNonPromotionalDebt = 13758,
            currentShortTermCreditLimit = 30600,
            currentShortTermCreditUtilisation = 44,
            changeInShortTermDebt = 549,
            currentLongTermDebt = 24682,
            currentLongTermNonPromotionalDebt = 24682,
            currentLongTermCreditLimit = null,
            currentLongTermCreditUtilisation = null,
            changeInLongTermDebt = -327,
            numPositiveScoreFactors = 9,
            numNegativeScoreFactors = 0,
            equifaxScoreBand = 4,
            equifaxScoreBandDescription = "Excellent",
            daysUntilNextReport = 9
        )
        val mockCoachingSummary = CoachingSummary(
            activeTodo = false,
            activeChat = true,
            numberOfTodoItems = 0,
            numberOfCompletedTodoItems = 0,
            selected = true
        )
        val mockSuccessResponse = CreditReportResponse(
            accountIDVStatus = "PASS",
            creditReportInfo = mockCreditReportInfo,
            dashboardStatus = "PASS",
            personaType = "INEXPERIENCED",
            coachingSummary = mockCoachingSummary,
            augmentedCreditScore = null
        )

        coEvery { mockRepository.getCreditReport() } returns mockSuccessResponse

        viewModel = CreditScoreViewModel(mockRepository, mockContext)

        viewModel.creditReport.test {
            // Assert the initial state is set to loading
            assertEquals(CreditReportUiState.Loading, awaitItem())

            // Start the fetch operation
            viewModel.fetchCreditReport()

            // Expect the Success state after the mock repository is called
            val successState = awaitItem()
            assertTrue(successState is CreditReportUiState.Success)
            assertEquals(mockSuccessResponse, (successState as CreditReportUiState.Success).report)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetchCreditReport failure transitions from Loading to Error`() = runTest {
        coEvery { mockRepository.getCreditReport() } throws RuntimeException("This error should show in the console")

        viewModel = CreditScoreViewModel(mockRepository, mockContext)

        viewModel.creditReport.test {
            // Assert that the initial state is Loading
            assertEquals(CreditReportUiState.Loading, awaitItem())

            // Start the fetch operation
            viewModel.fetchCreditReport()

            // Expect the Error state
            val errorState = awaitItem()
            assertTrue(errorState is CreditReportUiState.Error)
            // Error message should come from mocked context and not be the exception message
            assertEquals(mockErrorMessage, (errorState as CreditReportUiState.Error).message)

            cancelAndConsumeRemainingEvents()
        }
    }
}
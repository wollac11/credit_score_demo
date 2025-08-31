# Credit Score Demo App

This is an Android application built for a technical task that fetches a user's credit score from a remote endpoint and displays it in a custom, animated donut view using Jetpack Compose.

<img width="2400" height="1080" alt="Screenshot_20250831_114315" src="https://github.com/user-attachments/assets/00b9186c-96f5-4305-afb3-31167947340f" />


## Features

-   **Donut View:** A custom, animated donut view that visualises the credit score.
-   **Dynamic Colouring**: The colour of the score and the donut view's arc changes dynamically based on the credit score value (from red for poor scores to green for excellent scores).
-   **State-Driven UI:** The UI reacts to different states (Loading, Success, Error) exposed by the ViewModel.
-   **Clean Architecture:** The app follows modern Android architecture principles, separating UI, data, and business logic.
-   **Unit Tested:** The ViewModel's logic is unit-tested to ensure reliability and correct state management.

## Architecture

The application is built using the MVVM (Model-View-ViewModel) pattern which decouples the business logic from the views and is structured into distinct layers:

-   **Model:** This layer is responsible for the application's data and business logic. It is comprised of:
    
    -   **Network Layer:**
	    - `CreditReportApiService` defines the Retrofit interface for the API endpoint.
	    -   `CreditReportResponse` and its nested classes are the Moshi data models for parsing the JSON response.
    -   **Data Layer:**
	    -   `CreditReportRepository` follows the repository pattern, acting as a single source of truth for credit report data and abstracting the data source from the ViewModel. 
        
-   **View:** This layer is responsible for displaying the UI. It is built entirely with Jetpack Compose and includes:
	-   `MainActivity` hosts the main screen.
    -   `CreditScoreScreen` observes the state from the ViewModel and displays the appropriate UI.
    -   `DonutView` is a reusable, customisable composable for displaying the score.
        
-   **ViewModel:** This layer acts as a bridge between the Model and the View.
	-   The `CreditScoreViewModel` fetches data from the Model (via the repository) and manages the UI state (`CreditReportUiState`) which is exposed to the View via a `StateFlow`.

## Tech Stack & Key Libraries

-   **Kotlin:** The programming language used for this project, following Google's official recommendation for modern native Android development.
    
-   **Jetpack Compose:** For building the entire user interface.
    
-   **Coroutines & Flow:** For asynchronous operations and managing streams of data.
    
-   **Hilt:** For dependency injection, simplifying the management of dependencies across the app.
    
-   **Retrofit:** For making network requests to the remote endpoint.
    
-   **Moshi:** For efficient JSON serialisation and deserialisation.
    
-   **ViewModel:** To store and manage UI-related data and expose it to the view.
    
-   **Mockk & Turbine:** For mocking dependencies and testing `StateFlow` in ViewModel unit tests.
    

## Testing

The `CreditScoreViewModelTests` class contains unit tests that cover the following scenarios:

1.  **Success:** Verifies that the UI state correctly transitions from `Loading` to `Success` when the API call is successful.
    
2.  **Failure:** Verifies that the UI state transitions from `Loading` to `Error` when the API call fails, ensuring the correct error message is propagated.

The tests use a `MainCoroutineRule` to manage the dispatcher for coroutines in a test environment.


## Future Improvements & Extensions

The current application is fairly simple but provides a solid foundation. Given more time, the following improvements could be made to enhance its functionality and robustness:

-   **Persistence and Caching:** Implement a local database using `Room` to cache the credit report data. This would provide offline support and a faster, more seamless user experience on subsequent app launches.
    
-   **Expanded Test Coverage:**
	-    Introduce UI/e2e tests to verify UI behaviour and interactions.
 	-    Add integration tests for the repository and data layers.
    
-   **Enhanced Data Refreshing:**
    -   Implement a pull-to-refresh gesture on the main screen.
    -   Add a mechanism to auto-refresh the data when the app detects a restored internet connection.
        
-   **UI Enhancements:**
    -   Apply a colour gradient to the donut view's arc for a more polished visual effect.
    -   Display more information from the `CreditReportResponse`, such as the positive and negative score factors.
        
-   **Accessibility:** Add content descriptions and other accessibility features to ensure the app is fully usable with screen readers like TalkBack.
    
-   **Localisation:** Provide translations for all user-facing strings to make the app accessible to a wider audience.

## How to Build and Run

1.  Clone the repository.
    
2.  Open the project in the latest version of Android Studio.
    
3.  Let Gradle sync the dependencies.
    
4.  Run the `app` configuration on an emulator or a physical device.

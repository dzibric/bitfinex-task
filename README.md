# Android App with MVI, Compose, and KMP Integration

This project demonstrates an implementation of the Model-View-Intent (MVI) architecture using Jetpack Compose. It also features integration with KMP (Kotlin Multiplatform) libraries like Koin and Ktor for dependency injection and networking. The app follows a clean architecture structure with feature-oriented packaging and adheres to modern Android development best practices.

## Features

- **MVI Architecture**: Utilized for handling UI state and user interactions in a scalable way.
- **Jetpack Compose**: The entire UI is built using Compose, Android's modern toolkit for building native UI.
- **Abstraction Layers**: The project is divided into three layers:
    - **Data Layer**: Responsible for managing data sources (e.g., API calls using Ktor).
    - **Domain Layer**: Contains the business logic and use cases.
    - **UI Layer**: Manages the presentation logic using Compose and MVI.
- **Koin**: Dependency injection framework, used for providing instances of ViewModels, repositories, and network services.
- **Ktor**: Networking library used for making API requests, making the project easier to scale for KMP integration.
- **Feature-Oriented Packaging**: Each feature in the app has its own package, promoting better separation of concerns and modularity.
- **Coroutines + Flow**: Used for managing asynchronous tasks and continuous data streams, ensuring smooth UI updates.
- **Core Abstractions**:
    - `CoreViewModel`: A base ViewModel that helps manage UI state and handle MVI intents.
    - `CoreComposable`: Simplifies state management and UI rendering within Composables.
- **Navigation**: `NavHost` is used to handle navigation, combined with `Routes` and `Screens` classes for easier route management and navigation logic.

### Additional Features
- **Sorting Functionality**: Included for data organization and improved user experience.

## Connectivity Handling

A combined **Connectivity Observer** has been implemented alongside handling network error responses. Since the exact requirement was unclear, the solution covers both aspects (observing network changes and handling error responses gracefully).

## Testing

- **Unit Testing**: ViewModel and UseCase have been tested to ensure business logic and state management correctness.

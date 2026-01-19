## CatalogViewer - application that displays a catalog of books. The app supports searching, viewing book details, and marking books as favorites. ##

### Build and Run Android Application

To `build` and `run the development version` of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat assembleDebug
  ```
  
To run the `unit tests`, use the test configuration from the run widget in your IDE’s toolbar or run it from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew testDebugUnitTest
  ```
- on Windows
  ```shell
  .\gradlew.bat testDebugUnitTest
  ```

## Architecture & Trade-offs
This project follows **Clean Architecture** with a focus on **MVI (Unidirectional Data Flow)**.

### 1. Boilerplate vs. Scalability (Clean Architecture)
**Trade-off**
The project is split into multiple modules: `:domain`, `:data`, `:presentation`, and `:app`. 
This results in a noticeable increase in boilerplate code, including:
* Repository interface
* Data mapper
* Multiple Gradle dependency configurations

**Reasoning**
This approach was chosen instead of a *MVVM + Repository* structure to fully isolate business logic from the Android framework.
But it was simplified (no UseCases), because: there is no certain business logic and the search could be done in the repository.

As a result:
* The `:domain` module is pure Kotlin and framework-independent (could be used in a multiplatform app)
* Data implementations can be swapped freely (JSON -> ROOM -> REST API)
* No changes are required in domain logic or UI layers
* The system remains flexible and adaptable to future requirements

This trade-off favors long-term maintainability and testability over initial development speed.

---

### 2. MVI Complexity vs. State Consistency
**Trade-off**
The application uses an MVI pattern with `StateFlow` for UI state and `SharedFlow` for one-off effects.
This is more complex than using simple `mutableStateOf` or LiveData and requires explicit definitions for:

* `Action`
* `UiState`
* `Effect`

**Reasoning**
This design eliminates *state fragmentation*, where multiple mutable states can fall out of sync. Benefits include:

* A single source of truth for UI state
* Predictable state transitions
* Easier debugging
* This works well for Compose and state hoisting.
* Straightforward flow testing using Turbine

The added structure improves reliability and test coverage at the cost of additional upfront complexity.

---


### 3. Full Screen vs. Bottom Sheet (Compose Navigation)

**Trade-off**
In Jetpack Compose, presenting content in a BottomSheet
is often perceived as faster and more lightweight than navigating to a new screen. However,
bottom sheets introduce additional complexity in state handling, lifecycle management,
and navigation consistency compared to a dedicated full-screen destination.

**Reasoning**
Choosing a new screen (navigation destination) over a bottom sheet provides several architectural and UX advantages:

* The screen integrates naturally with the NavController, back stack, deep links, and system back behavior, without custom handling.
* Simpler state management, unlike the bottom sheets often require synchronizing multiple states (sheet visibility, UI state, partial expansion). 
* The screen has a single lifecycle-bound UiState, which aligns better with MVI and reduces edge cases.
* Full screens align better with Material navigation patterns for primary user flows like `Detail View`, while bottom sheets are more suitable for transient or contextual actions.

A dedicated screen was chosen because it integrates naturally with Navigation and system back behavior,
has simple state and avoids overloading the `CatalogScreen` with additional UI and logic.

---
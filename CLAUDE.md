# Gina App

Personal diary/journal app. Local SQLite (Room), no backend. Feature-complete.

**Stack:** Kotlin + Coroutines, Jetpack Compose, MVI, Hilt DI, Room, Jetpack Nav3, Firebase
Crashlytics

---

## Modules

- `:app` — Nav wiring, DI, `GinaApp`, `NavDisplay`, thin route wrappers
- `:core` — Pure Kotlin utils (no Compose), `Permissions` utility
- `:core-ui` — Shared Compose components, theme, `LocalNavigator`
- `:resources` — All string resources (centralized `strings.xml`)
- `:navigation` — `Route` sealed interface + all route data classes/objects, `Navigator` (navigate/back/replace/navigateToRoot/popUntil), `ImagePreviewSource` sealed interface, `EntryProviderInstaller`/`NavEntryFallback` typealiases; routes in `routes/` sub-package; `Route.showScaffoldElements` property controls bottom bar visibility
- `:domain-model` — Pure data models
- `:data-database` — Room, DAOs, `JournalRepository`, use cases
- `:data-network` — Retrofit network layer: `ZenQuotesService`, `QuoteApiModel` (`@Serializable`), `NetworkModule` DI; package `com.sayler666.gina.network`; no database deps
- `:feature-journal` — Journal list screen, VM, state, mappers, use cases
- `:feature-day` — Day details, edit, add screens (with attachments, mood)
- `:feature-calendar` — Calendar screen & VM
- `:feature-gallery` — Gallery screen
- `:feature-insights` — Insights/stats screen
- `:feature-settings` — Settings screen, storage, view models
- `:feature-friends` — Friends management
- `:feature-reminders` — All reminder logic: use cases, receivers, state, DI
- `:feature-setup` — App startup/setup flow: permission grant + database selection; `SetupScreen`, `SetupViewModel`, `SetupNavModule`
- `:feature-game-of-life` — Conway's Game of Life simulation; `GameOfLifeViewModel` (MVI, `@AssistedInject content: String` seeds grid from day content), `GameOfLifeScreen`, `GameOfLifeNavModule`; no database deps
- `:build-logic` — Gradle plugins

**Module dependencies:** `:domain-model` ← `:navigation` ← `:core` ← `:resources` ← `:core-ui` ←
`:data-database` ← `feature-*` → `:app` (all)

---

## Routes

All routes in package `com.sayler666.gina.navigation.routes`, organized by feature file:

- `BottomNavRoutes.kt` — `Journal`, `Calendar`, `Gallery`, `Insights`, `Settings` (`showScaffoldElements = true`)
- `DayRoutes.kt` — `DayDetails(dayId, way: Way)`, `DayDetailsEdit(dayId)`, `AddDay(date?: LocalDate)`, `ImagePreview(initialAttachmentId, source: ImagePreviewSource)`, `ImagePreviewTmp(image: ByteArray, mimeType)`; also `ImagePreviewSource` sealed interface (`Gallery`, `Day(dayId, attachmentIds)`, `Journal(attachmentIds)`)
- `SetupRoutes.kt` — `Startup`
- `FriendsRoutes.kt` — `ManageFriends`
- `GameOfLifeRoutes.kt` — `GameOfLife(content: String)`

`Route.kt` (sealed interface) also lives in the `routes/` sub-package.

---

## ViewModel Pattern

```kotlin
// State
private val mutableViewState = MutableStateFlow(createInitialState())
val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

// Actions
private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
val viewActions = mutableViewActions.receiveAsFlow()

// Observe flows individually
init {
    observeFlow1()
    observeFlow2()
}

private fun observeFlow1() {
    flow.onEach { value ->
        mutableViewState.update { it.copy(field = value) }
    }.launchIn(viewModelScope)
}

// Event entry point
fun onViewEvent(event: ViewEvent) {
    when (event) {
        is MyEvent -> mutableViewActions.trySend(MyAction)
    }
}

sealed interface ViewEvent {
    data object MyEvent : ViewEvent
}
sealed interface ViewAction {
    data object MyAction : ViewAction
}
```

---

## Screen Pattern

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    val navigator = LocalNavigator.current

    BackHandler { viewModel.onViewEvent(OnBackPressed) }
    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
            is ShowToast -> Toast.makeText(..., action.message, ...).show()
        }
    }

    Content(state = viewState, viewEvent = viewModel::onViewEvent)
}

@Composable
private fun Content(state: ViewState?, viewEvent: (ViewEvent) -> Unit) {
    // Pure UI — no ViewModel, no LaunchedEffect, no side effects
}
```

- All string and resources are in :resources module

---

## Navigation (Jetpack Nav3)

- All routes in `routes/` sub-package of `:navigation` (`com.sayler666.gina.navigation.routes`)
- `GinaMainViewModel` owns `backStack: SnapshotStateList<Route>` (survives config changes); determines start route (`Startup` or `Journal`) on first load
- `GinaApp` owns `Navigator` wrapping backStack; provides it via `LocalNavigator`
- `:app` thin wrappers extract Route params, pass to feature screens
- Feature screens get Route data directly, no SavedStateHandle
- Route params via `@AssistedInject` + `@HiltViewModel(assistedFactory = ...)`

---

## Code Rules

**Refactoring:**

- Models → `:domain-model`
- Kotlin utils → `:core`
- Compose components → `:core-ui`
- Use cases → `:data-database` (next to repo)
- Feature code → `feature-*` (never `:app`)

**Naming:**

- `mutable*` — internal StateFlow/SharedFlow
- `ViewEvent.On*` — user interactions
- `ViewAction.*` — side effects
- **String resources** — use `feature_*` prefix for feature strings (e.g., `settings_*` in
  feature-settings, `gallery_*` in feature-gallery)

**Imports:**

- `hiltViewModel` from `androidx.hilt.lifecycle.viewmodel.compose`
- Not `androidx.hilt.navigation.compose`

---

## Do NOT Touch

- Room schema (user data loss)
- `GinaDatabaseProvider`
- Firebase config

---

## Refactoring (Planned)

1. **Split `JournalRepository`** — 4 concerns (days, attachments, friends, mood) → 4 repos

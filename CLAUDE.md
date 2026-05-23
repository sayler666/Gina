# Gina App

Personal diary/journal app. Local SQLite (Room), no backend. Feature-complete.

**Stack:** Kotlin + Coroutines, Jetpack Compose, MVI, Hilt DI, Room, Jetpack Nav3, Firebase Crashlytics

---

## Modules

- `:app` — Nav wiring, DI, `GinaApp`, `NavDisplay`, thin route wrappers
- `:core` — Pure Kotlin utils (no Compose), `Permissions` utility
- `:core-ui` — Shared Compose components, theme, `LocalNavigator`
- `:resources` — All string resources (centralized `strings.xml`)
- `:navigation` — `Route` sealed interface + all route data classes/objects, `Navigator` (navigate/back/replace/navigateToRoot/popUntil), `ImagePreviewSource` sealed interface; routes in `routes/` sub-package; `Route.showScaffoldElements` controls bottom bar visibility
- `:domain-model` — Pure data models
- `:data-database` — Room, DAOs, `JournalRepository`, use cases
- `:data-network` — Retrofit: `ZenQuotesService`, `NetworkModule` DI; no database deps
- `:feature-journal` — Journal list screen, VM, state, mappers, use cases
- `:feature-day` — Day details, edit, add screens (with attachments, mood)
- `:feature-calendar` — Calendar screen & VM
- `:feature-gallery` — Gallery screen
- `:feature-insights` — Insights/stats screen
- `:feature-settings` — Settings screen, storage, view models
- `:feature-friends` — Friends management
- `:feature-reminders` — All reminder logic: use cases, receivers, state, DI
- `:feature-setup` — App startup/setup flow: `SetupScreen`, `SetupViewModel`
- `:feature-game-of-life` — Conway's Game of Life simulation
- `:build-logic` — Gradle plugins

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

---

## Database Architecture

- `GinaDatabase`, `DaysDao`, `RawDao` — `@Singleton` via `JournalDatabaseModule`; inject directly
- `JournalRepository` — query layer; prefer over injecting DAOs directly
- `DatabaseFileManager` — file ops only (SAF import/export/sync); not for queries
- DB file: `getDatabasePath("gina_journal.db")`; syncs to external SAF URI after writes
- Replacing the DB file requires a full process restart (`Runtime.exit(0)`)

---

## Code Rules

**Naming:**

- `mutable*` — internal StateFlow/SharedFlow
- `ViewEvent.On*` — user interactions
- `ViewAction.*` — side effects
- **String resources** — use `feature_*` prefix for feature strings (e.g., `settings_*` in feature-settings)

**Navigation:**

- Route params via `@AssistedInject` + `@HiltViewModel(assistedFactory = ...)`

---

## Banned Antipatterns

| Banned | Fix |
|--------|-----|
| `mutableViewState.value = newState` | `mutableViewState.update { it.copy(...) }` |
| `GlobalScope.launch { }` | `viewModelScope.launch { }` |
| ViewModel as composable param | Pass `(ViewEvent) -> Unit` lambda only |
| `@Entity` on domain model | Entity in `data-database/`, map in repository |
| `collectAsState()` | `collectAsStateWithLifecycle()` |
| `Text("Save")` hardcoded | `stringResource(R.string.feature_save)` |
| `fontSize = 16.sp` hardcoded | `MaterialTheme.typography.bodyLarge` |
| God ViewModel (>200 lines) | Split screen or extract UseCases |
| Feature module depending on feature module | Only depend on `core-*` and `domain-model` |

---

## Compose Performance

**Recomposition:**

- Annotate all `ViewState` / `UiState` data classes with `@Immutable`
- Use `remember(key) { derivedStateOf { } }` for expensive computed values
- Extract sub-composables to create separate recomposition scopes

**LazyColumn:**

- Always `key = { it.id }` on every `items()`
- Add `contentType` when row layouts differ
- Stable lambdas: `remember(id) { { onEvent(ItemClicked(id)) } }`
- Never sort/filter inline in `items { }` — do it in ViewModel

---

## UseCase Rules

| Create UseCase | Skip UseCase |
|----------------|--------------|
| Logic shared by 2+ ViewModels | Single-line repository delegate |
| Multi-repository orchestration | One ViewModel, one repository |
| Needs isolated unit test | Simple CRUD with no extra logic |

---

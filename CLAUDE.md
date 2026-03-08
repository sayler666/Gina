# Gina App

## Description
Personal diary/journal app. Local SQLite database (Room). No backend, no cloud sync.
Feature-complete — no new features planned.

## Stack
- Kotlin + Coroutines, Jetpack Compose + Material3
- MVI architecture, Hilt DI, Room, Jetpack Nav3
- Firebase: Crashlytics

## Current Module Structure
- `:app` — navigation wiring, DI graph, MainActivity, `GinaApp` + `NavDisplay`, thin route wrappers
- `:core` — pure Kotlin utils only (no Compose)
- `:core-ui` — Compose only — shared components, theme, modifiers
- `:data-database` — Room, DAOs, entities, repositories, use cases
- `:domain-model` — pure Kotlin models, no logic
- `:feature-calendar` — calendar UI and ViewModel
- `:feature-day` — day details, day edit, add day, image preview, attachments, working copy, mood
- `:feature-friends` — friends UI, ViewModels, and use cases
- `:feature-gallery` — gallery UI, ViewModel, and image thumbnail repository
- `:feature-insights` — insights UI, ViewModels, and use cases
- `:feature-settings` — settings UI and logic
- `:navigation` — `Route` sealed interface (all routes), `Navigator` backstack manager
- `:build-logic` — Gradle convention plugins

## Module Dependency Rules
- `:domain-model` → no dependencies
- `:navigation` → `:domain-model` only
- `:core` → `:domain-model` only
- `:core-ui` → `:core`, `:domain-model`, `:navigation`
- `:data-database` → `:domain-model`, `:core`
- `feature-*` → `:core`, `:core-ui`, `:domain-model`, `:data-database`
- `feature-*` → cross-feature dependencies allowed if necessary
- `:app` → all modules, owns Navigation and top-level DI

## Navigation Pattern (Jetpack Nav3)
- All routes defined in `:navigation` as `Route` sealed interface (`Route.Journal`, `Route.DayDetails(dayId, way)`, etc.)
- `GinaApp` owns the backstack (`vm.backStack: SnapshotStateList<Route>`) and a `Navigator` wrapping it
- `Navigator` is provided via `LocalNavigator` composition local (defined in `core-ui`) — screens call `LocalNavigator.current` to navigate
- `NavDisplay` in `GinaApp` has a centralized `entryProvider` that dispatches each `Route` to the appropriate screen composable
- `:app` thin wrapper composables extract params from `Route` and pass them to feature screens
  - e.g., `app/dayDetails/DayDetailsScreen.kt` calls `FeatureDayDetailsScreen(dayId = route.dayId)`
  - App wrappers remain in a different package from the feature screen they wrap (convention unchanged)
- Feature screens receive `Route` data directly — no `DestinationsNavigator`, no `NavController`, no `SavedStateHandle` for nav args
- Transitions are handled inline in `entryProvider` via `NavEntry` metadata (see `GinaApp.kt` for `Route.DayDetails` slide transitions)

## ViewModel Nav Args Pattern
- ViewModels that need route params use `@HiltViewModel(assistedFactory = ...)` + `@AssistedInject`
- Route params are injected as `@Assisted` constructor params
- App wrapper composable calls `hiltViewModel<VM, VM.Factory>(key = ...) { factory -> factory.create(route.param) }`
- No `SavedStateHandle` for nav args — params come directly from the `Route` object

## Refactoring Rules — follow when writing any new code
- New models → `:domain-model`
- New Kotlin utils → `:core` (no Compose allowed)
- New shared Compose components → `:core-ui` (Theme enum already there)
- New use cases → `:data-database` next to relevant repository
- New feature code → target `feature-*` module, never `:app`
- `:app` gets only navigation wiring (thin wrappers, `GinaApp`, `NavDisplay`) and DI

## MVI Pattern
- `viewState: StateFlow<State?>` — built with `combine()`, `WhileSubscribed(500)`
- `viewActions: Flow<Action>` — `Channel(BUFFERED)` + `receiveAsFlow()` for one-shot events
- `onViewEvent(event: ViewEvent)` — single entry point for all UI events
- `ViewEvent` / `ViewAction` — sealed interfaces nested inside ViewModel
- Screen split: `Screen()` entry composable (side effects) + private `Content()` (pure UI)
- `BackHandler` always delegates to ViewModel via `OnBackPressed`

## Naming Conventions
- `mutable*` prefix for internal MutableStateFlow/MutableSharedFlow
- `ViewEvent.On*` — e.g. `OnBackPressed`, `OnContentChanged`
- `ViewAction.*` — e.g. `Back`, `ShowDiscardDialog`

## Code Conventions
- `hiltViewModel` import: use `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` (not `androidx.hilt.navigation.compose`)
- Resource annotations on `val` constructor params require explicit target: `@param:StringRes val name: Int` (Kotlin 2.3+)

## Do NOT Touch
- Room schema — breakage loses user data
- `GinaDatabaseProvider` — DB initialization flow
- Firebase configuration

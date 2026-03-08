# Gina App

## Description
Personal diary/journal app. Local SQLite database (Room). No backend, no cloud sync.
Feature-complete — no new features planned.

## Stack
- Kotlin + Coroutines, Jetpack Compose + Material3
- MVI architecture, Hilt DI, Room, Jetpack Nav3
- Firebase: Crashlytics

## Current Module Structure
- `:app` — navigation wiring, DI graph, MainActivity, `GinaApp` + `NavDisplay`, thin route wrappers; also hosts `Journal` feature (not yet extracted: `JournalScreen`, `JournalViewModel`, `JournalMapper`, `JournalState`, `PreviousYearsAttachmentsUseCase`), `SelectDatabase` screen+VM, `GameOfLife` screen+VM, and `RemindersViewModelImpl`
- `:core` — pure Kotlin utils only (no Compose); includes `ViewModelSlice`, `BottomNavigationVisibilityManager`, `BottomBarState`
- `:core-ui` — Compose only — shared components, theme, modifiers; includes `LocalNavigator`, `NavigationBarColor`, `BOTTOM_NAV_HEIGHT`, `AttachmentState`
- `:data-database` — Room, DAOs, entities, `JournalRepository` (days + attachments + friends + mood analytics), use cases; includes `GinaDatabaseProvider`, `DatabaseSettingsStorage`, `GetDaysUseCase`
- `:domain-model` — pure Kotlin models, no logic; includes `Way` enum
- `:feature-calendar` — calendar UI and ViewModel
- `:feature-day` — day details, day edit, add day, image preview, attachments, working copy, mood; use cases live under `dayDetails/usecase/`; `DayQuoteProvider` + `ReminderDismissUseCase` interfaces defined here, bound in app's `DayProvidersModule`
- `:feature-friends` — friends UI, ViewModels, use cases (`Add/Delete/Edit/GetAll/GetAllByRecent/GetFriend`), `FriendsMapper`, `FriendsPicker`
- `:feature-gallery` — gallery UI, ViewModel, `ImageAttachmentsRepository`, thumbnail handling
- `:feature-insights` — insights UI, ViewModels, use cases (`GetAvgMoodByMonthsUseCase`, `GetAvgMoodByWeeksUseCase`); cross-feature deps on `:feature-friends` and `:feature-calendar`
- `:feature-settings` — settings UI, `AppSettings`, `SettingsStorage`, `ImageOptimizationViewModel`, `RemindersViewModel` interface + `ReminderState`
- `:navigation` — `Route` sealed interface (all routes), `ImagePreviewSource` sealed interface, `Navigator` backstack manager
- `:build-logic` — Gradle convention plugins

## All Routes (`Route` sealed interface in `:navigation`)
Bottom nav roots: `Journal`, `Calendar`, `Gallery`, `Insights`, `Settings`
Other screens: `SelectDatabase`, `ManageFriends`, `GameOfLife`
Parameterised: `DayDetails(dayId, way)`, `DayDetailsEdit(dayId)`, `AddDay(date?)`, `ImagePreview(initialAttachmentId, source)`, `ImagePreviewTmp(image, mimeType)`
Supporting: `ImagePreviewSource` — `Gallery`, `Day(dayId, attachmentIds)`, `Journal(attachmentIds)`

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

## Refactoring Plan (prioritised)

### 1. Extract `DayEditingViewModelSlice` — eliminate attachment/friend duplication
`AddDayViewModel` and `DayDetailsEditViewModel` share ~100 lines of identical logic: `addAttachments`, `removeAttachment`, `optimizeAttachment`, friend search/select, working copy integration. Extract into a `ViewModelSlice` (pattern already exists in `:core`) consumed by both VMs.

### 2. Split `JournalRepository` by domain (305 lines, 4 concerns)
`JournalRepository` handles days, attachments, friends, and mood analytics. Split into `DayRepository`, `AttachmentRepository`, `FriendsRepository`, and `MoodAnalyticsRepository` — each with a focused interface and a single DAO dependency.

### 3. Extract `Journal` feature out of `:app` into `:feature-journal`
`JournalScreen`, `JournalViewModel`, `JournalMapper`, `JournalState`, and `PreviousYearsAttachmentsUseCase` live in `:app`, violating the rule that `:app` is for navigation wiring only. Create `:feature-journal` following the same pattern as other feature modules.

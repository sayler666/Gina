# Gina App

## Description
Personal diary/journal app. Local SQLite database (Room). No backend, no cloud sync.
Feature-complete — no new features planned.

## Stack
- Kotlin + Coroutines, Jetpack Compose + Material3
- MVI architecture, Hilt DI, Room, Compose Destinations (migration planned)
- Firebase: Crashlytics + Analytics only
- No unit tests

## Current Module Structure
- `:app` — navigation wiring, DI graph, MainActivity, thin @Destination wrappers only
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
- `:build-logic` — Gradle convention plugins

## Module Dependency Rules
- `:domain-model` → no dependencies
- `:core` → `:domain-model` only
- `:core-ui` → `:core`, `:domain-model`
- `:data-database` → `:domain-model`, `:core`
- `feature-*` → `:core`, `:core-ui`, `:domain-model`, `:data-database`
- `feature-*` → cross-feature dependencies allowed if necessary
- `:app` → all modules, owns Navigation and top-level DI

## Modularization Pattern
- Feature modules expose composables with nav callbacks — no `@Destination` annotation
- `:app` has thin `@RootNavGraph @Destination` wrappers that wire `DestinationsNavigator` → lambdas
- App wrapper package must differ from the feature package it wraps — same package causes `NoSuchMethodError` at runtime (dex class collision)
  - Convention: feature uses its own package, app wrapper uses `com.sayler666.gina.<feature>` (no `.ui`)
- `DayDetailsTransitions` and `ImagePreviewTransitions` stay in `:app` — they use `appDestination()` which is KSP-generated in `:app`

## Nav Args + KSP Rule
- Nav args with **default values** MUST be defined in `:app`, not in feature modules
  - KSP (Compose Destinations) cannot detect default values from compiled bytecode in other modules
- Nav args without defaults can live in feature modules
- Feature ViewModels read args via `savedStateHandle.get<T>("argName")` — never use generated `XxxDestination.argsFrom(savedStateHandle)`
- Nav args currently in `:app`:
  - `DayDetailsScreenNavArgs` — `app/.../dayDetails/ui/DayDetailsNavArgs.kt`
  - `AddDayScreenNavArgs` — `app/.../addDay/ui/AddDayNavArgs.kt`
  - `ImagePreviewScreenNavArgs`, `ImagePreviewTmpScreenNavArgs` — `app/.../attachments/ui/ImagePreviewNavArgs.kt`
- `const val` strings (e.g. `ADD_DAY_URL`) can stay in feature modules — usable in annotations across modules

## Refactoring Rules — follow when writing any new code
- New models → `:domain-model`
- New Kotlin utils → `:core` (no Compose allowed)
- New shared Compose components → `:core-ui` (Theme enum already there)
- New use cases → `:data-database` next to relevant repository
- New feature code → target `feature-*` module, never `:app`
- `:app` gets only navigation wiring, nav args, and DI

## Planned Migrations (in order)
1. **Navigation** — migrate from Compose Destinations to Jetpack Nav3. Each feature module will expose its own Nav3 destinations.

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

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
- `:app` — remaining features, ViewModels, UI, DI, Navigation (being refactored)
- `:core` — pure Kotlin utils only (no Compose)
- `:core-ui` — Compose only — shared components, theme, modifiers
- `:data-database` — Room, DAOs, entities, repositories, use cases
- `:domain-model` — pure Kotlin models, no logic
- `:feature-calendar` — calendar UI and ViewModel
- `:feature-friends` — friends UI, ViewModels, and use cases
- `:feature-insights` — insights UI, ViewModels, and use cases
- `:feature-settings` — settings UI and logic
- `:build-logic` — Gradle convention plugins

## Target Module Structure (refactor in progress)
```
app/                    # entry point, DI graph, MainActivity, Navigation only
build-logic/
core/                   # pure Kotlin only, no Compose
core-ui/                # Compose only — shared components, theme, modifiers
data-database/          # extracted
domain-model/           # extracted
feature/
  feature-calendar/     # extracted
  feature-day-edit/     # addDay + dayDetailsEdit combined
  feature-day-details/
  feature-friends/
  feature-gallery/
  feature-insights/
  feature-settings/     # extracted
```

## Module Dependency Rules
- `:domain-model` → no dependencies
- `:core` → `:domain-model` only
- `:core-ui` → `:core`, `:domain-model`
- `:data-database` → `:domain-model`, `:core`
- `feature-*` → `:core`, `:core-ui`, `:domain-model`, `:data-database`
- `feature-*` → cross-feature dependencies allowed if necessary
- `:app` → all modules, owns Navigation and top-level DI

## Refactoring Rules — follow when writing any new code
- New models → `:domain-model`
- New Kotlin utils → `:core` (no Compose allowed)
- New shared Compose components → `:core-ui` (Theme enum already there)
- New use cases → `:data-database` next to relevant repository
- New feature code → target `feature-*` module, never `:app`
- `:app` gets only navigation wiring and DI

## Planned Migrations (in order)
1. **Modularization** — extract `:app` features into `feature-*` modules (in progress)
2. **Navigation** — migrate from Compose Destinations to Jetpack Nav3 after modularization is complete. Each feature module will expose its own Nav3 destinations.

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

## Do NOT Touch
- Room schema — breakage loses user data
- `GinaDatabaseProvider` — DB initialization flow
- Firebase configuration

# Gina

Personal diary/journal app for Android. All data is stored locally — no backend, no cloud sync.
Apps like OneSync, Dropsync, etc., could be used to back up database file to your favourite cloud.

## Features

- Write daily journal entries with rich text formatting
- Attach images to entries
- Tag entries with friends
- Track mood per entry
- Calendar view
- Gallery of all attached images
- Insights — mood charts, heatmap, statistics
- Reminder notifications
- Image compression settings

## Tech Stack

- **Language:** Kotlin + Coroutines
- **UI:** Jetpack Compose + Material3
- **Architecture:** MVI
- **DI:** Hilt
- **Database:** Room (local SQLite)
- **Navigation:** Jetpack Nav3
- **Crash reporting:** Firebase Crashlytics

## Module Structure

| Module | Description |
|---|---|
| `:app` | Nav wiring, DI, `GinaApp`, `NavDisplay`, thin route wrappers |
| `:core` | Pure Kotlin utils (no Compose), `Permissions` utility |
| `:core-ui` | Shared Compose components, theme, `LocalNavigator` |
| `:resources` | All string resources (centralized `strings.xml`) |
| `:navigation` | `Route` sealed interface, `Navigator`, routes in `routes/` sub-package |
| `:domain-model` | Pure data models |
| `:data-database` | Room, DAOs, `JournalRepository`, use cases |
| `:data-network` | Retrofit: `ZenQuotesService`, `NetworkModule` DI; no database deps |
| `:feature-journal` | Journal list screen, VM, state, mappers, use cases |
| `:feature-day` | Day details, edit, add screens (with attachments, mood) |
| `:feature-calendar` | Calendar screen & VM |
| `:feature-gallery` | Gallery screen |
| `:feature-insights` | Insights/stats screen |
| `:feature-settings` | Settings screen, storage, view models |
| `:feature-friends` | Friends management |
| `:feature-reminders` | All reminder logic: use cases, receivers, state, DI |
| `:feature-setup` | App startup/setup flow: `SetupScreen`, `SetupViewModel` |
| `:feature-game-of-life` | Conway's Game of Life simulation |
| `:build-logic` | Gradle convention plugins |

## Building

```bash
./gradlew assembleDebug
```

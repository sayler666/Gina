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
| `:app` | Navigation wiring, DI graph, `MainActivity` |
| `:navigation` | `Route` sealed interface, `Navigator` |
| `:domain-model` | Pure Kotlin models |
| `:core` | Pure Kotlin utilities, `ViewModelSlice` |
| `:core-ui` | Shared Compose components, theme |
| `:data-database` | Room, DAOs, repositories, use cases |
| `:feature-calendar` | Calendar screen |
| `:feature-day` | Day details, edit, add, image preview |
| `:feature-friends` | Friends management |
| `:feature-gallery` | Image gallery |
| `:feature-insights` | Mood and activity insights |
| `:feature-settings` | App settings, reminders |
| `:build-logic` | Gradle convention plugins |

## Building

```bash
./gradlew assembleDebug
```

# RefranyDiari

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-757575?style=flat&logo=materialdesign&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-087CFA?style=flat&logo=ktor&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)

Android app that shows a **daily selection of Catalan proverbs** (*refranys*), one per topic. Built with Kotlin and Jetpack Compose.

## Backend

This app works together with **[RefranyDiari-API](https://github.com/lluisruscalleda/RefranyDiari-API)**, a FastAPI service that groups proverbs by topic (OpenAI) and can serve a deterministic daily set.

| | |
|---|---|
| API repo | [lluisruscalleda/RefranyDiari-API](https://github.com/lluisruscalleda/RefranyDiari-API) |
| Deployed API | [https://refranydiari-api.onrender.com](https://refranydiari-api.onrender.com) |

The client talks to the API via Ktor (`ApiService.kt`), posting proverb lists to be grouped (e.g. `/group-refranys`).

## Features

- Five topic sections with Catalan-inspired colors
- One proverb per topic chosen from the date (same day → same set)
- Local asset corpora by theme (`app/src/main/assets/`)
- Daily notification reminder

### Topic files

- `Temps_i_Naturalesa.txt`
- `Treball_i_Vida_Quotidiana.txt`
- `Saviesa_i_Consells.txt`
- `Relacions_i_Societat.txt`
- `Sort_i_Destí.txt`

## Requirements

- Android Studio (Ladybug or newer recommended)
- JDK 11+
- Android SDK 35 (`minSdk` 24)

## Build & run

1. Clone this repo and open it in Android Studio
2. Let Gradle sync
3. Run the `app` configuration on an emulator or device

```bash
./gradlew :app:assembleDebug
```

## Project layout

```
.
├── app/
│   ├── src/main/java/.../MainActivity.kt   # UI + daily selection
│   ├── src/main/java/.../ApiService.kt     # HTTP client → RefranyDiari-API
│   └── src/main/assets/                    # Proverbs by topic
├── gradle/
└── README.md
```

## Related

- API documentation and local setup: [RefranyDiari-API](https://github.com/lluisruscalleda/RefranyDiari-API)

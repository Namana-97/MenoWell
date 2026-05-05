# MenoWell Android

Android client for the MenoWell backend.

Default backend URL for emulator:
`http://10.0.2.2:8000/`

Open this folder in Android Studio and let Gradle sync.

Folder structure:
```text
menowell_android/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── README.md
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/menowell/
            ├── MainActivity.kt
            ├── MenoWellApp.kt
            ├── core/
            ├── data/
            ├── ui/
            └── viewmodel/
```

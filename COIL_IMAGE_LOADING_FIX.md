# Coil image-loading dependency fix

This project now uses Coil 3 consistently. All Compose image imports use `coil3.compose`, and `app/build.gradle.kts` declares:

- `io.coil-kt.coil3:coil-compose:3.3.0`
- `io.coil-kt.coil3:coil-network-okhttp:3.3.0`

In Android Studio, sync Gradle after opening the project. The first sync requires internet access to Maven Central. Then run **Build > Clean Project** and **Build > Rebuild Project**.

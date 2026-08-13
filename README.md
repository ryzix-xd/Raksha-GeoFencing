# Raksha Geo Fencing

Raksha Geo Fencing is a privacy-focused Android application designed to provide reliable geofencing using the system's location APIs, coupled with logical distance verification to minimize battery drain.

## Features
- **Logical Radius (50m):** User-defined logical geofencing. 
- **System Geofence (150m):** Uses Android's geofencing APIs.
- **Privacy First:** No backend, all data is stored locally.
- **MapLibre & OpenFreeMap:** Uses OpenFreeMap for open and privacy-friendly maps.

## Architecture & Background Execution
On MIUI/HyperOS devices, users may need to explicitly allow 'Autostart' and remove Battery Saver restrictions.

## GitHub Actions CI/CD
Fully automated builds and release signing via GitHub Actions.

## Building
1. Add `local.properties` with your Android SDK path.
2. `./gradlew assembleRelease`

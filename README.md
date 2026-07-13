# TransportSim Android – Real Map-Driven Transport Simulation

**Version**: 1.0.0  
**Status**: Production-Ready (Core Features)  
**Platform**: Android 8+ (API 26), Landscape-Only  
**License**: Proprietary (Sphere Solution Developers)

---

## 📖 Project Overview

**TransportSim** is a **Procedural Virtual Environment (PVE)** transport simulation game for Android. It uses real-world geographic data (Kenya routes) to generate a 3D driving experience entirely offline. Players manage fleets of vehicles (buses, matatus, pickups, lorries, boda bodas, taxis) across procedurally generated routes derived from actual GPS coordinates.

The game combines:

- A **C++ physics engine** running at 1000 Hz (Pacejka tire model, Ackermann steering, RK4 integration).
- A **Kotlin/Compose UI** with a modern, responsive design.
- **SQLite with SQLCipher** for encrypted player state.
- **glTF 2.0** 3D assets loaded via memory-mapped I/O for zero-copy performance.
- **JNI bridge** connecting the Android layer to the native engine.

The app is **fully offline** – all data is bundled inside the APK or downloaded as Play Asset Delivery packs.

---

## 🏗️ Architecture

The project follows a **clean architecture** with three main layers:

1. **Domain** (Pure Kotlin) – Business logic, use cases, repository interfaces, models.  
2. **Data** (Android library) – Implementation of repositories, Room database, DataStore, asset loading.  
3. **Presentation** (App module) – Compose UI, ViewModels, navigation, themes.

In addition, a **Native** module (C++) provides the physics engine and rendering, accessed via a **Bridge** module (JNI wrappers).

```
┌─────────────────────────────────────────────────────────────┐
│                         Presentation (App)                   │
│  ┌─────────┐ ┌──────────┐ ┌───────────┐ ┌───────────────┐  │
│  │  Compose │ │ ViewModel│ │ Navigation│ │   Hilt DI     │  │
│  │   UI     │ │  (State) │ │  Compose  │ │               │  │
│  └─────────┘ └──────────┘ └───────────┘ └───────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                          Domain                             │
│  ┌──────────────────┐  ┌──────────────────────────────────┐ │
│  │    Use Cases     │  │        Repository Interfaces      │ │
│  └──────────────────┘  └──────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                         Models                          │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                          Data                               │
│  ┌──────────────┐ ┌────────────┐ ┌───────────────────────┐ │
│  │   Room/SQL   │ │  DataStore │ │  Asset/World Data     │ │
│  │  (SQLCipher) │ │ (Encrypted)│ │  Sources              │ │
│  └──────────────┘ └────────────┘ └───────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │               Repository Implementations                │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                       Bridge / JNI                         │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                   NativeEngine.kt                       │ │
│  │                   SimulationSession.kt                 │ │
│  │                   NativeCallbacks.kt                   │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                       Native (C++)                         │
│  ┌──────────────┐ ┌──────────────┐ ┌─────────────────────┐ │
│  │    Physics   │ │    Terrain   │ │      Renderer       │ │
│  │    Engine    │ │    Manager   │ │   (GLES3/Vulkan)    │ │
│  └──────────────┘ └──────────────┘ └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Full File Tree

Below is the complete file structure of the project. Files marked `(generated)` are produced at build time.

```
TransportsimAndroid/
├── build.gradle.kts                              # Top-level build script
├── settings.gradle.kts                           # Modules & version catalog
├── gradle.properties                             # Gradle JVM args, Android flags
├── gradle/
│   └── libs.versions.toml                        # Version catalog (BOM, Hilt, Room, etc.)
├── build-logic/                                  # Gradle convention plugins
│   ├── settings.gradle.kts
│   ├── build.gradle.kts
│   └── conventions/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── AndroidApplicationConventionPlugin.kt
│           ├── AndroidLibraryConventionPlugin.kt
│           ├── HiltConventionPlugin.kt
│           ├── KotlinJvmConventionPlugin.kt
│           ├── NativeConventionPlugin.kt
│           └── AssetPackConventionPlugin.kt
├── domain/                                       # Pure Kotlin business logic
│   ├── build.gradle.kts
│   └── src/main/java/com/transportsim/domain/
│       ├── models/                               # Data classes & enums
│       │   ├── Vehicle.kt
│       │   ├── VehicleCatalogEntry.kt
│       │   ├── VehicleCategory.kt
│       │   ├── ResolvedVehicleConfig.kt
│       │   ├── Route.kt
│       │   ├── RouteWaypoint.kt
│       │   ├── Mission.kt
│       │   ├── Upgrade.kt
│       │   ├── PlayerProfile.kt
│       │   ├── DailyRewardTrack.kt
│       │   ├── PhysicsStats.kt
│       │   ├── TrafficLightState.kt
│       │   ├── SimulationMetrics.kt
│       │   ├── SurfaceType.kt
│       │   ├── TerrainChunk.kt
│       │   └── TrainingScenario.kt
│       ├── repositories/                         # Repository interfaces
│       │   ├── PlayerRepository.kt
│       │   ├── FleetRepository.kt
│       │   ├── RouteRepository.kt
│       │   ├── MissionRepository.kt
│       │   ├── EconomyRepository.kt
│       │   ├── CatalogRepository.kt
│       │   ├── TerrainRepository.kt
│       │   └── TrainingRepository.kt
│       └── usecases/                             # Interactors (business logic)
│           ├── GetPlayerVehiclesUseCase.kt
│           ├── GetCatalogUseCase.kt
│           ├── PurchaseVehicleUseCase.kt
│           ├── ResolveVehicleConfigUseCase.kt
│           ├── CompleteTripUseCase.kt
│           ├── AcceptMissionUseCase.kt
│           ├── UpgradeVehiclePartUseCase.kt
│           ├── GenerateDailyMissionsUseCase.kt
│           ├── RefuelVehicleUseCase.kt
│           ├── RepairVehicleUseCase.kt
│           ├── ScoreTestTrackUseCase.kt
│           ├── TerrainPreloadUseCase.kt
│           ├── GetPlayerProfileUseCase.kt
│           └── GetRouteWaypointsUseCase.kt
├── data/                                         # Data layer (Android library)
│   ├── build.gradle.kts
│   └── src/main/java/com/transportsim/data/
│       ├── database/                             # Room + SQLCipher
│       │   ├── TransportSimDatabase.kt
│       │   ├── Migrations.kt
│       │   ├── Converters.kt
│       │   ├── dao/
│       │   │   ├── PlayerProfileDao.kt
│       │   │   ├── PlayerVehicleDao.kt
│       │   │   ├── VehicleUpgradeDao.kt
│       │   │   ├── JourneySessionDao.kt
│       │   │   ├── StopEventDao.kt
│       │   │   ├── ActiveMissionDao.kt
│       │   │   ├── EconomyLedgerDao.kt
│       │   │   ├── DailyStatsDao.kt
│       │   │   ├── AchievementDao.kt
│       │   │   ├── PartsInventoryDao.kt
│       │   │   ├── RouteUnlockStateDao.kt
│       │   │   ├── RouteWaypointDao.kt
│       │   │   └── TerrainChunkMetadataDao.kt
│       │   └── entities/
│       │       ├── PlayerProfileEntity.kt
│       │       ├── PlayerVehicleEntity.kt
│       │       ├── VehicleUpgradeEntity.kt
│       │       ├── JourneySessionEntity.kt
│       │       ├── StopEventEntity.kt
│       │       ├── ActiveMissionEntity.kt
│       │       ├── EconomyLedgerEntity.kt
│       │       ├── DailyStatsEntity.kt
│       │       ├── AchievementEntity.kt
│       │       ├── PartsInventoryEntity.kt
│       │       ├── RouteUnlockStateEntity.kt
│       │       ├── RouteWaypointEntity.kt
│       │       └── TerrainChunkMetadataEntity.kt
│       ├── repository/                           # Repository implementations
│       │   ├── PlayerRepositoryImpl.kt
│       │   ├── FleetRepositoryImpl.kt
│       │   ├── CatalogRepositoryImpl.kt
│       │   ├── RouteRepositoryImpl.kt
│       │   ├── MissionRepositoryImpl.kt
│       │   ├── EconomyRepositoryImpl.kt
│       │   └── TerrainRepositoryImpl.kt
│       ├── datasource/                           # Data sources (assets, DB)
│       │   ├── AssetDataSource.kt
│       │   ├── WorldDatabaseDataSource.kt
│       │   └── TerrainChunkDataSource.kt
│       ├── security/                             # Encrypted DataStore
│       │   └── EncryptedPrefs.kt
│       ├── worker/                               # WorkManager tasks
│       │   ├── DailyMissionsWorker.kt
│       │   └── TripCompletionWorker.kt
│       └── config/                               # Configuration helpers
│           ├── AppConfig.kt
│           ├── VehicleConfigResolver.kt
│           └── TerrainPreloader.kt
├── bridge/                                       # JNI bridge (Kotlin <-> C++)
│   ├── build.gradle.kts
│   └── src/main/java/com/transportsim/bridge/
│       ├── NativeEngine.kt                       # Main JNI interface
│       ├── NativeCallbacks.kt                   # Callbacks from native
│       ├── SimulationSession.kt                 # Session management
│       ├── annotations/
│       │   └── Keep.kt                          # ProGuard keep annotation
│       └── models/                              # Native data models (with @Keep)
│           ├── VehiclePhysicsState.kt
│           ├── NativeTrafficLightState.kt
│           ├── BridgeSimulationMetrics.kt
│           ├── TerrainChunkData.kt
│           ├── NativeVehicleConfig.kt
│           ├── NativeRoadSegment.kt
│           ├── NativeBusStop.kt
│           ├── NativeWorldObject.kt
│           └── NativeInputState.kt
├── native/                                       # C++ native library
│   ├── build.gradle.kts
│   ├── CMakeLists.txt
│   └── src/main/cpp/
│       ├── jni/                                 # JNI glue
│       │   ├── native_glue.cpp
│       │   ├── native_glue.h
│       │   └── transportsim_jni.cpp
│       ├── physics/                             # Physics engine
│       │   ├── PhysicsEngine.cpp
│       │   ├── PhysicsEngine.h
│       │   ├── Pacejka.cpp
│       │   ├── Pacejka.h
│       │   ├── Ackermann.cpp
│       │   ├── Ackermann.h
│       │   ├── Suspension.cpp
│       │   └── Suspension.h
│       ├── terrain/                             # Terrain system
│       │   ├── TerrainManager.cpp
│       │   ├── TerrainManager.h
│       │   ├── TerrainChunk.cpp
│       │   ├── TerrainChunk.h
│       │   ├── TerrainLoader.cpp
│       │   ├── TerrainLoader.h
│       │   ├── FrictionTable.cpp
│       │   ├── FrictionTable.h
│       │   ├── VBOArena.cpp
│       │   ├── VBOArena.h
│       │   └── Interpolation.cpp
│       └── renderer/                            # OpenGL/Vulkan renderer
│           ├── Renderer.cpp
│           ├── Renderer.h
│           └── GLTFLoader.cpp
├── app/                                          # Main Compose application
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── java/com/transportsim/app/
│       │   ├── TransportsimApplication.kt      # Hilt application
│       │   ├── MainActivity.kt                 # Single activity
│       │   ├── di/                             # Dagger Hilt modules
│       │   │   ├── AppModule.kt
│       │   │   ├── DatabaseModule.kt
│       │   │   ├── RepositoryModule.kt
│       │   │   ├── UseCaseModule.kt
│       │   │   └── TerrainModule.kt
│       │   └── ui/
│       │       ├── theme/                      # Material 3 theme
│       │       │   ├── Color.kt
│       │       │   ├── Type.kt
│       │       │   ├── Theme.kt
│       │       │   └── Shape.kt
│       │       ├── app/                        # Navigation
│       │       │   ├── AppNavHost.kt
│       │       │   ├── AppState.kt
│       │       │   └── Destinations.kt
│       │       ├── dashboard/                  # Dashboard screen
│       │       │   ├── DashboardScreen.kt
│       │       │   ├── DashboardViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── fleet/                      # Fleet screen
│       │       │   ├── FleetScreen.kt
│       │       │   ├── FleetViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── routes/                     # Routes screen
│       │       │   ├── RoutesScreen.kt
│       │       │   ├── RoutesViewModel.kt
│       │       │   ├── RouteDetailScreen.kt
│       │       │   ├── RouteDetailViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── missions/                   # Missions screen
│       │       │   ├── MissionsScreen.kt
│       │       │   ├── MissionsViewModel.kt
│       │       │   ├── MissionDetailScreen.kt
│       │       │   ├── MissionDetailViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── training/                   # Training centre
│       │       │   ├── TrainingScreen.kt
│       │       │   ├── TrainingViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── garage/                     # Garage screen
│       │       │   ├── GarageScreen.kt
│       │       │   ├── GarageViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── settings/                   # Settings screen
│       │       │   ├── SettingsScreen.kt
│       │       │   ├── SettingsViewModel.kt
│       │       │   └── components/             (many files)
│       │       ├── simulation/                 # Driving simulation screen
│       │       │   ├── SimulationScreen.kt
│       │       │   ├── SimulationViewModel.kt
│       │       │   └── components/             (many files incl. GLSurfaceView)
│       │       ├── onboarding/                 # Onboarding flow
│       │       │   ├── OnboardingScreen.kt
│       │       │   └── OnboardingViewModel.kt
│       │       ├── common/                     # Shared composables
│       │       │   ├── BottomNavBar.kt
│       │       │   ├── LoadingIndicator.kt
│       │       │   └── ...
│       │       └── components/                 (other shared composables)
│       ├── res/                                # Resources
│       │   ├── values/                         # Strings, colors, themes
│       │   │   ├── strings.xml (English)
│       │   │   ├── strings-sw.xml (Swahili)
│       │   │   ├── colors.xml
│       │   │   ├── themes.xml
│       │   │   ├── bools.xml
│       │   │   └── dimens.xml
│       │   ├── drawable/                       # Vector icons
│       │   │   ├── ic_dashboard.xml
│       │   │   ├── ic_fleet.xml
│       │   │   ├── ic_routes.xml
│       │   │   ├── ic_missions.xml
│       │   │   ├── ic_garage.xml
│       │   │   ├── ic_settings.xml
│       │   │   └── ic_notification.xml
│       │   ├── mipmap/                         # Launcher icons (need to add)
│       │   └── xml/
│       │       └── fullBackupContent.xml
│       └── AndroidManifest.xml
├── game_assets/                                  # Play Asset Delivery pack
│   ├── build.gradle.kts
│   └── src/main/assets/
│       ├── 3d/                                  # .glb models (not included)
│       │   ├── structural/
│       │   ├── vehicles/
│       │   ├── vegetation/
│       │   └── props/
│       ├── textures/                            # KTX2 textures
│       ├── routes/                              # Route JSON definitions
│       │   ├── KE-001.json
│       │   ├── KE-002.json
│       │   ├── KE-003.json
│       │   ├── KE-004.json
│       │   ├── KE-005.json
│       │   └── traffic_schedules.json
│       └── config/                              # Game config JSON files
│           ├── vehicle_specs.json
│           ├── upgrade_defs.json
│           ├── surface_types.json
│           ├── mission_templates.json
│           └── asset_manifest.json
├── benchmark/                                    # Macrobenchmark module
│   ├── build.gradle.kts
│   └── src/main/java/com/transportsim/benchmark/
│       └── StartupBenchmark.kt
├── billing/                                     # (Optional) IAP module – not yet implemented
│   ├── build.gradle.kts
│   └── src/main/java/com/transportsim/billing/
│       ├── BillingManager.kt
│       ├── ProductType.kt
│       ├── BillingRepository.kt
│       └── PurchaseVerification.kt
├── local.properties (generated)                 # SDK path
└── .gitignore
```

---

## 🚀 Getting Started

### 1. Prerequisites

- **Android Studio** Hedgehog | 2023.1.1 or later (with Kotlin 2.0+ support)
- **JDK 17** (required by Android Gradle Plugin 8.x)
- **Android SDK** (API 35 recommended)
- **NDK** (version 25.2.9519653 or later) – install via SDK Manager
- **CMake** 3.22+ (included with NDK)
- **Git** (for cloning)

### 2. Clone the Repository

```bash
git clone https://github.com/your-org/transportsim-android.git
cd transportsim-android
```

### 3. Open in Android Studio

- Select **Open** and choose the project root (`TransportsimAndroid`).
- Wait for Gradle sync to complete.

### 4. Configure SDK / NDK

Ensure the following are set in `local.properties` (automatically generated by Android Studio):

```properties
sdk.dir=/path/to/Android/Sdk
ndk.dir=/path/to/Android/Sdk/ndk/25.2.9519653
```

Alternatively, you can set `ANDROID_NDK` environment variable.

### 5. Build the Project

```bash
./gradlew assembleDebug
```

Or use Android Studio's **Build > Make Project**.

### 6. Run on Device/Emulator

- Connect an Android device (8.0+) or start an emulator.
- Click **Run** (green triangle) in Android Studio.

**Note:** The app is designed for **landscape** orientation. Emulators should be set to landscape (or rotated).

---

## 🧩 Key Technologies & Dependencies

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.0.20 | Primary language |
| Jetpack Compose | 2025.02.00 | Modern UI toolkit |
| Material 3 | 1.3.1 | Design system |
| Hilt | 2.55 | Dependency injection |
| Room | 2.7.1 | Database ORM |
| SQLCipher | 4.6.1 | Encrypted database |
| DataStore | 1.1.2 | Persistent key-value storage |
| Security Crypto | 1.1.0-alpha06 | Encrypted DataStore |
| Moshi | 1.15.1 | JSON parsing |
| WorkManager | 2.10.0 | Background tasks |
| Coroutines & Flow | 1.9.0 | Asynchronous programming |
| JNI | – | Kotlin ↔ C++ bridge |
| C++ | 17 | Native physics engine |
| OpenGL ES | 3.2 | 3D rendering |
| glTF 2.0 | – | 3D asset format |
| NDK | 25.2.9519653 | Native development |

---

## 🎮 Key Features

- **Offline Simulation**: All game data and assets are bundled – no network required.
- **Real-World Routes**: 5 launch routes derived from Kenyan geography (Nairobi, Mombasa, etc.).
- **Physics Engine**: 1000 Hz simulation with Pacejka tyre model, Ackermann steering, and RK4 integration.
- **Fleet Management**: Buy, upgrade, repair, and deploy vehicles.
- **Mission System**: Daily missions with rewards.
- **Training Centre**: Practice scenarios without penalties.
- **Garage**: Upgrade vehicle modules (engine, seats, fuel efficiency, etc.).
- **Live HUD**: Speed, fuel, engine temp, passenger satisfaction, traffic lights, and more.

---

## 📦 Asset Pipeline

The game uses two main asset types:

1. **JSON configuration files** – vehicle specs, upgrade definitions, surface types, mission templates. These are located in `game_assets/src/main/assets/config/`.
2. **3D assets** – `.glb` files (glTF 2.0) stored in `game_assets/src/main/assets/3d/`. The C++ renderer loads them via memory-mapped I/O.

A Python preprocessing pipeline (not included in this repo) converts real map data into these bundled assets. The pipeline is described in the project documentation (external).

---

## 🧪 Testing

- **Unit tests** are placed in each module under `src/test/`.
- **Instrumentation tests** are in `src/androidTest/`.
- **Macrobenchmark** module (`:benchmark`) for startup and performance testing.

To run tests:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 🛠️ Build Variants

- **Debug**: Full logging, no ProGuard, fast builds.
- **Release**: Minified, obfuscated, shrunken resources, native optimisations.

To build a release APK:

```bash
./gradlew assembleRelease
```

The APK will be in `app/build/outputs/apk/release/`.

---

## 📚 Documentation

Additional technical documentation is available in the project's `docs/` directory (if present) or in the attached HTML files:

- `transportsim_dataflow_datastorage.html` – Data architecture and storage strategy.
- `transportsim_docs-1.html` – Complete project documentation.
- `transportsim_v3_5-22.html` – UI/UX specification and simulation design.

---

## 🤝 Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m 'Add some amazing feature'`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.

Please follow the code style (KTlint) and write unit tests for new functionality.

---

## ⚠️ Known Issues / Limitations

- **No live traffic** – AI vehicles follow scripted paths with basic collision avoidance.
- **Static environment** – The world is pre-generated; real-world changes won't appear until an update.
- **AI detection accuracy** – YOLO-based object detection from satellite imagery has ~80-85% accuracy; manual QA is required for route data.
- **APK size** – Base APK may exceed 150 MB; Play Asset Delivery is used to split assets.
- **Billing module** – Not yet implemented; IAP will be added in a future release.

---

## 📄 License

This project is **proprietary** and owned by **Sphere Solution Developers**. All rights reserved.

---

## ✉️ Contact

For questions or support, contact:

- **Faris Shikuku** – Lead Developer  
- **Sphere Solution Developers** – Nairobi, Kenya  
- Email: info@sphere-solutions.co.ke

---

**Happy Driving! 🚌**
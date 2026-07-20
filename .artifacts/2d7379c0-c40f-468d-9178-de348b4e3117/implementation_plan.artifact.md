# Implementation Plan - Training Center

Implement the Training Center with database integration, matching the design guide. This includes tracking progress, difficulty badges, and a "Not Yet Implemented" dialog for scenarios.

## User Review Required

> [!IMPORTANT]
> The existing `TrainingScenario` model and `TrainingRepository` methods will be refactored to separate the static scenario data from the dynamic progress data (`TrainingScenarioWithProgress`). This is a breaking change for any existing code using these stubs, but current usages are minimal.

> [!NOTE]
> The database version will be incremented to 3 (currently 2 in `TransportSimDatabase.kt`) to accommodate the new `TrainingProgressEntity`. `fallbackToDestructiveMigration()` is currently enabled, which is fine for development.

## Proposed Changes

### [Data Layer]

#### [NEW] [TrainingProgressEntity.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/entities/TrainingProgressEntity.kt)
- Define the `TrainingProgressEntity` for Room.

#### [NEW] [TrainingProgressDao.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/dao/TrainingProgressDao.kt)
- Define the DAO for training progress operations.

#### [MODIFY] [TransportSimDatabase.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/TransportSimDatabase.kt)
- Add `TrainingProgressEntity` to the database entities.
- Add `trainingProgressDao()` abstract function.
- Increment version to 3.

#### [NEW] [training_scenarios.json](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/assets/config/training_scenarios.json)
- Add the scenario data provided in the guide.

#### [MODIFY] [AssetDataSource.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/datasource/AssetDataSource.kt)
- Add `loadTrainingScenarios()` and `TrainingScenariosContainer`.

### [Domain Layer]

#### [MODIFY] [TrainingScenario.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/models/TrainingScenario.kt)
- Update `TrainingScenario` and `TrainingDifficulty`.
- Add `TrainingScenarioWithProgress` and `TrainingProgress`.

#### [MODIFY] [TrainingRepository.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/repositories/TrainingRepository.kt)
- Update interface methods to support progress tracking.

### [Implementation Layer]

#### [MODIFY] [TrainingRepositoryImpl.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/repository/TrainingRepositoryImpl.kt)
- Implement repository methods using `AssetDataSource` and `TrainingProgressDao`.

### [App/UI Layer]

#### [MODIFY] [TrainingViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/training/TrainingViewModel.kt)
- Update to use `TrainingScenarioWithProgress`.
- Implement level-based auto-unlock logic.
- Add "Not Yet Implemented" dialog state.

#### [MODIFY] [TrainingScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/training/TrainingScreen.kt)
- Update layout to match design, including the hero card and the scenarios list.
- Add the "Not Yet Implemented" dialog.

#### [MODIFY] UI Components
- Update [TrainingScenarioCard.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/training/components/TrainingScenarioCard.kt)
- Update [TrainingHeroCard.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/training/components/TrainingHeroCard.kt)
- Update [TrainingProgressCard.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/training/components/TrainingProgressCard.kt)

## Verification Plan

### Automated Tests
- I'll check if there are existing tests for Training and if they need updates.
- I can run a build to ensure no compilation errors.

### Manual Verification
- Deploy to device/emulator.
- Navigate to Training Center.
- Verify that scenarios are loaded and difficulty badges are correct.
- Verify progress tracking (if possible to simulate completion).
- Verify "Not Yet Implemented" dialog appears on clicking "Start".

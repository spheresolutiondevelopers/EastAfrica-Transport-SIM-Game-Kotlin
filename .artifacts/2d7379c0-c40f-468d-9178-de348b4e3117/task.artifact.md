# Task: Implement Training Center

Implement the Training Center with database integration, matching the design guide.

## TODO
- [x] **Data Layer**
    - [x] Create `TrainingProgressEntity.kt`
    - [x] Create `TrainingProgressDao.kt`
    - [x] Update `TransportSimDatabase.kt` (Add entity, DAO, increment version)
    - [x] Create `training_scenarios.json` asset
    - [x] Update `AssetDataSource.kt` (Add loading logic)
- [x] **Domain Layer**
    - [x] Update `TrainingScenario.kt` (Refactor models)
    - [x] Update `TrainingRepository.kt` (Update interface)
- [x] **Repository Implementation**
    - [x] Update `TrainingRepositoryImpl.kt` (Full implementation)
- [x] **App/UI Layer**
    - [x] Update `TrainingViewModel.kt`
    - [x] Update `TrainingScreen.kt`
    - [x] Update `TrainingScenarioCard.kt`
    - [x] Update `TrainingHeroCard.kt`
    - [x] Update `TrainingProgressCard.kt`
- [x] **Verification**
    - [x] Build project
    - [x] Verify UI layout and interactions

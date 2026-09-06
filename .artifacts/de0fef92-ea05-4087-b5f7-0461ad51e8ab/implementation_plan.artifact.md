# Dashboard Real Data Integration

The goal is to replace the hardcoded "today's performance" statistics in the Dashboard with real data from the database. Currently, values for revenue, passengers, cargo, and on-time rate are hardcoded in the `DashboardViewModel`.

## User Review Required

> [!IMPORTANT]
> The real data tracking for daily performance (revenue, passengers, etc.) is currently defined in the database schema (`DailyStatsEntity`) but is not yet being populated by the game logic. By switching to real data, these values will initially show **0** until the tracking logic is implemented in subsequent tasks. This fulfills the user's request that "this should be zero and info should be obtained from the real data".

## Proposed Changes

### [Domain] Performance Models & Repositories

#### [NEW] [DailyPerformance.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/models/DailyPerformance.kt)
- Create a domain model representing daily performance stats.

#### [MODIFY] [EconomyRepository.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/repositories/EconomyRepository.kt)
- Add `observeTodayPerformance(): Flow<DailyPerformance>` to the interface.

### [Data] Database & Repository Implementation

#### [MODIFY] [DailyStatsEntity.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/entities/DailyStatsEntity.kt)
- Add `cargoKg: Int` and `onTimeRatePct: Float` to the entity.

#### [MODIFY] [TransportSimDatabase.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/TransportSimDatabase.kt)
- Increment database version to **4**.

#### [MODIFY] [EconomyRepositoryImpl.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/repository/EconomyRepositoryImpl.kt)
- Implement `observeTodayPerformance()` by querying the `DailyStatsDao`.

### [UI] Dashboard Integration

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardViewModel.kt)
- Collect today's performance from `EconomyRepository`.
- Remove hardcoded values from `loadDashboardData()`.

## Verification Plan

### Automated Tests
- Verify that `EconomyRepositoryImpl` correctly maps the DAO entity to the domain model.
- Verify that the `DashboardViewModel` updates the `UiState` when the flow emits.

### Manual Verification
- Deploy the app and navigate to the Dashboard.
- Verify that the revenue, passengers, cargo, and on-time rate are all showing **0** (as the database is initially empty).
- (Optional) Use a database inspector to insert a row into `daily_stats` for today's date and verify the dashboard updates in real-time.

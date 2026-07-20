package com.transportsim.domain.models

data class FleetVehicle(
    val catalogEntry: VehicleCatalogEntry,
    val ownedVehicle: Vehicle? // null if not owned
) {
    val isOwned: Boolean get() = ownedVehicle != null
    val vehicleId: Int? get() = ownedVehicle?.vehicleId
    val status: VehicleStatus? get() = ownedVehicle?.status
    val conditionPct: Float? get() = ownedVehicle?.conditionPct
}

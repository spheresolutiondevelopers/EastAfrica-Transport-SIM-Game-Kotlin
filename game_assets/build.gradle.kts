plugins {
    id("transportsim.assetpack")
}

assetPack {
    packName.set("game_assets")
    dynamicDelivery {
        deliveryType.set("install-time")
    }
}

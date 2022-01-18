package util.absfeatures

import commonUtil.interfaces.ShippingCarrierInterface

object ShippingCarriersController {
    fun getShippingRoutesCarriersBasedOnFeatureFlag(
        shippingCarriersWithFlags: List<Pair<ShippingCarrierInterface, Boolean>>,
        defaultCarriers: ShippingCarrierInterface
    ): ShippingCarrierInterface {
        val updatedCarriers = defaultCarriers.carriers.toMutableList()

        shippingCarriersWithFlags.forEach {
            val featureShippingCarriers = it.first
            val isFeatureOn = it.second

            if (isFeatureOn) {
                val commonCarriers = defaultCarriers.carriers.intersect(featureShippingCarriers.carriers)
                val carriersToRemove = defaultCarriers.carriers.minus(commonCarriers)
                val carriersToAdd = featureShippingCarriers.carriers.minus(commonCarriers)

                carriersToRemove.forEach { carrier ->
                    updatedCarriers.remove(carrier)
                }
                carriersToAdd.forEach { carrier ->
                    updatedCarriers.add(carrier)
                }
            }
        }
        return defaultCarriers.also { it.carriers = updatedCarriers }
    }
}

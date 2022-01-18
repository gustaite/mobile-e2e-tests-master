package test.basic.states.builders

import test.basic.states.interfaces.FeatureState
import test.basic.states.interfaces.StateMachineBuilder
import util.absfeatures.AbTestController

class TransactionStatusDesyncStateMachineBuilder : StateMachineBuilder {
    enum class TestStates : FeatureState {
        NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON
    }
    override val abFlagsAndStates: List<Pair<Boolean, FeatureState>> = listOf(
        Pair(AbTestController.isNasaShipmentTransactionStatusDesyncOn(), TestStates.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON)
    )
}

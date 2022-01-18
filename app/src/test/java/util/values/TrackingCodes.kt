package util.values

import api.data.models.transaction.VintedCarrier
import util.EnvironmentManager.isAndroid
import api.TestDataApi.trackingCodes
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert

class TrackingCodes(val codes: List<String>) {

    companion object {
        fun getTrackingNumber(carrier: VintedCarrier): String {
            val trackingNumbers = trackingCodes.carriers.firstOrNull { it.carrierId == carrier.id }
                .let {
                    VintedAssert.assertNotNull(
                        it,
                        "Tracking codes for carrier (id: ${carrier.id}, name: ${carrier.name}) was not found in the list "
                    ); it!!
                }.trackingCodes.let {
                    if (isAndroid) it.android else it.ios
                }

            var number: String? = null
            commonUtil.Util.retryUntil(
                block = {
                    number = chooseTrackingNumber(trackingNumbers)
                    number != null
                },
                tryForSeconds = 180
            )
            VintedAssert.assertNotNull(number, "Available tracking number was not found")
            return number!!
        }

        private val usedTrackingCodes = mutableListOf<String>()

        @Synchronized
        private fun updateTrackingCode(trackingCode: String, add: Boolean) {
            if (add) usedTrackingCodes.add(trackingCode) else usedTrackingCodes.remove(trackingCode)
        }

        fun addTrackingCodeIntoUsedList(trackingCode: String) {
            updateTrackingCode(trackingCode = trackingCode, add = true)
        }

        fun removeTrackingCodeFromUsedList(trackingCode: String) {
            updateTrackingCode(trackingCode = trackingCode, add = false)
        }

        private fun chooseTrackingNumber(trackingCodes: List<String>): String? {
            val number = trackingCodes.random()
            commonUtil.reporting.Report.addMessage("Tracking number $number checked")
            return if (usedTrackingCodes.contains(number)) {
                sleepWithinStep(1000)
                null
            } else {
                addTrackingCodeIntoUsedList(trackingCode = number)
                number
            }
        }
    }
}

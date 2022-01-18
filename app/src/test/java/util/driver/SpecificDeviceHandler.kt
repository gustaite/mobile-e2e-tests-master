package util.driver

import util.EnvironmentManager.specificDevices
import util.values.Devices

object SpecificDeviceHandler {
    private val usesDevices = mutableMapOf<String, Boolean>()

    fun getDeviceUdid(): String? {
        return if (specificDevices.count() > 0) {
            lookForAvailableDeviceAndReturnUdid()
        } else null
    }

    @Synchronized
    private fun lookForAvailableDeviceAndReturnUdid(): String? {
        var udid: String? = null
        loop@ for (i in 0 until specificDevices.count()) {
            val name = specificDevices[i]
            if (!isDeviceBusy(name)) {
                udid = Devices.getUdid(name)
                break@loop
            }
        }
        return udid
    }

    @Synchronized
    private fun isDeviceBusy(deviceName: String): Boolean {
        return if (usesDevices.getOrDefault(deviceName, false)) {
            return true
        } else {
            usesDevices[deviceName] = true
            false
        }
    }
}

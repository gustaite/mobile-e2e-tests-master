package util.testng

import commonUtil.testng.config.ConfigManager
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.specificDevices
import util.EnvironmentManager.threadCount
import util.SeleniumGridManager

object ThreadCalculator {
    fun decideThreadCount(): Int {
        return if (isAndroid && specificDevices.count() > 0) {
            specificDevices.count() // Used when running on specific devices
        } else {
            calculateGridNodes()
        }
    }

    private fun calculateGridNodes(): Int {
        val threadsResponse = SeleniumGridManager.getGridNodesCount()

        return if (threadsResponse.realResponse) {
            threadsResponse.getCountByPlatform(ConfigManager.platform)
        } else {
            threadCount
        }
    }
}

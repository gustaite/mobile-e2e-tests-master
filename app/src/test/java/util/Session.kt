package util

import commonUtil.thread
import util.EnvironmentManager.isAndroid
import util.driver.WebDriverFactory.driver
import commonUtil.extensions.isInitialized
import util.driver.IosAppSync
import util.driver.IosInfoKey

class Session(val platformVersion: String, val deviceManufacturer: String, val deviceModel: String, val deviceUdid: String, val node: String, val nodePort: String, val id: String, val iosAppFileDate: String?) {
    companion object {
        private var sd: Map<String, Any?> by thread.lateinit()
        val sessionDetails by thread {
            if (!driver.isInitialized()) throw NullPointerException("Driver was not initialized")
            sd = driver.sessionDetails
            val (host, port) = SeleniumGridManager.getNodeInformation(driver.sessionId)
            val iosAppCreationDate = IosAppSync.getInfoAboutApp(IosInfoKey.BuildDate)
            Session(
                platformVersion = sd["platformVersion"].toString(),
                deviceManufacturer = if (isAndroid) sd["deviceManufacturer"].toString() else "Apple",
                deviceModel = if (isAndroid) sd["deviceModel"].toString() else sd["deviceName"].toString(),
                deviceUdid = if (isAndroid) sd["deviceUDID"].toString() else sd["udid"].toString(),
                node = host,
                nodePort = port,
                id = "${driver.sessionId}",
                iosAppFileDate = iosAppCreationDate
            )
        }
        val isOldIos: Boolean = !isAndroid && (sessionDetails.platformVersion.contains("11.") || sessionDetails.platformVersion.contains("12."))
    }
}

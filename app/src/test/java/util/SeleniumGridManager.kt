package util

import commonUtil.testng.config.ConfigManager
import api.controllers.grid.SeleniumGridAPI
import api.data.responses.VintedPlatformServletResponse
import commonUtil.asserts.VintedAssert
import org.testng.ITestContext
import commonUtil.reporting.Report
import khttp.get
import org.openqa.selenium.remote.SessionId
import util.EnvironmentManager.isRemoteSeleniumGrid
import util.EnvironmentManager.threadCount
import java.lang.Exception
import java.net.URI

object SeleniumGridManager {
    private const val NEW_SELENIUM_GRID_ADDRESS = "http://192.168.16.250:4444"
    private const val APPIUM_LOCALHOST_ADDRESS = "http://127.0.0.1:4723"
    private const val MOBILE_GRID_FROM_HOME_ADDRESS = "https://mobile-grid.vinted.net"
    private val SELENIUM_GRID_ADDRESS: String get() = System.getProperty("SELENIUM_GRID_URL")?.let { it.ifEmpty { null } } ?: NEW_SELENIUM_GRID_ADDRESS

    private const val minimumNodesCount = 8

    val serverAddress get() = if (isRemoteSeleniumGrid) {
        if (System.getenv("NEW_JENKINS") == "true") "$SELENIUM_GRID_ADDRESS/wd/hub"
        else "$MOBILE_GRID_FROM_HOME_ADDRESS/wd/hub"
    } else "$APPIUM_LOCALHOST_ADDRESS/wd/hub"

    fun getGridNodesCount(): VintedPlatformServletResponse {
        return if (!isRemoteSeleniumGrid) {
            VintedPlatformServletResponse(ios = threadCount, android = threadCount, realResponse = false)
        } else {
            try {
                SeleniumGridAPI.getPlatformNodesCount(serverAddress)
            } catch (e: AssertionError) {
                Report.addMessage("PlatformServlet was not found on Selenium grid")
                VintedPlatformServletResponse(ios = threadCount, android = threadCount, realResponse = false)
            }
        }
    }

    fun stopSuiteWhenMinimumAmountOfNodesNotReached(context: ITestContext) {
        val response = getGridNodesCount()
        Report.addMessage("Context name: ${context.name}")
        if (response.realResponse && context.name != "Test for scrolling in catalog") { // Skip validation also then running catalogScrollConfiguration
            val suiteThreadCount = context.suite.xmlSuite.threadCount
            Report.addMessage("Suite thread count: $suiteThreadCount")
            val count = response.getCountByPlatform(ConfigManager.platform)
            if (EnvironmentManager.specificDevices.count() < 1) {
                VintedAssert.assertTrue(
                    count >= minimumNodesCount && suiteThreadCount <= count,
                    "There was less nodes than needed to run the tests. Minimum nodes: $minimumNodesCount" +
                        " Actual nodes: $count Suite threads: $suiteThreadCount Platform: ${ConfigManager.platform}"
                )
            }
        }
    }

    fun getNodeInformation(sessionId: SessionId): Pair<String, String> {
        return try {
            val response = get(serverAddress.replace("wd/hub", "grid/api/testsession?session=$sessionId"))
            val json = response.jsonObject
            val url = URI(json.get("proxyId").toString())
            Pair(url.host, url.port.toString())
        } catch (e: Exception) {
            Report.addMessage("Cough exception: ${e.message}")
            Pair("Could not get host", "N/A")
        }
    }
}

package util.driver

import RobotFactory.deepLink
import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.config.PortalFactory.isSandbox
import commonUtil.extensions.isInitialized
import commonUtil.reporting.Report
import commonUtil.thread
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.AndroidMobileCapabilityType
import io.appium.java_client.remote.IOSMobileCapabilityType
import io.appium.java_client.remote.MobileCapabilityType
import io.qameta.allure.Step
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.DesiredCapabilities
import util.Android
import util.EnvironmentManager.ImplicitlyWaitTimeout
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isRealIosDevice
import util.EnvironmentManager.isiOS
import util.SeleniumGridManager
import util.Util
import util.app.App.appPath
import util.reporting.AllureReportTestsWithoutSessionHelper
import util.values.Devices
import util.values.StevesMap
import java.net.URL
import java.util.concurrent.TimeUnit
import java.lang.reflect.Method

object WebDriverFactory {
    val drivers: MutableList<AppiumDriver<MobileElement>> = mutableListOf()
    private val permissions =
        "{\"${portal.mobile.appPackage.ios}\": {\"notifications\": \"YES\",\"camera\": \"YES\",\"location\": \"always\",\"medialibrary\": \"YES\",\"photos\": \"YES\"}}"

    var driver: AppiumDriver<MobileElement> by thread.lateinit()

    @Step("Quit all drivers")
    fun quitAll() {
        Report.addMessage("Drivers to quit: ${drivers.size}")
        drivers.distinct().parallelStream().forEach {
            Util.retryOnException(
                block = { it.quit() },
                count = 3
            )
        }
    }

    @Step("[INFO] is driver available")
    private fun isDriverAvailable(): Boolean {
        var sessionError = false
        if (driver.isInitialized()) {
            try {
                // Make call on driver to verify session still available
                driver.orientation
                Report.addMessage("Still existing driver with sessionId: ${driver.sessionId}")
            } catch (e: WebDriverException) {
                sessionError = true
                quitDriver(1)
                deepLink.reset()
                Report.addMessage("Driver exception caught. Probably session timeout met previously on this thread \n Here is exception: ${e.message}")
            }
            return !sessionError
        }
        return false
    }

    @Step("Get or set the driver")
    fun getOrSetDriver(method: Method? = null) {
        if (isDriverAvailable()) {
            Report.addMessage("Driver was already created. Will reuse it.")
        } else {
            retryOnException({ createDriver() }, method)?.let {
                driver = it
                if (isiOS) {
                    driver.asIOSDriver().let { iOSDriver ->
                        iOSDriver.setSetting("snapshotTimeout", "30")
                        // boundElementsByIndex should prevent StaleElementExceptions from happening
                        iOSDriver.setSetting("boundElementsByIndex", true)
                    }
                }
                Report.addMessage("Created new driver. Session id: ${it.sessionId}")
                addOrRemoveDriver(it, true)
                it.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            }
        }
        ImplicitlyWaitTimeout = 10
    }

    @Step("[INFO] Adds or removes driver from list")
    @Synchronized
    private fun addOrRemoveDriver(driver: AppiumDriver<MobileElement>, add: Boolean) {
        if (add) {
            drivers.add(driver)
        } else {
            drivers.remove(driver)
        }
    }

    @Step("[INFO] Creates driver")
    private fun createDriver(): AppiumDriver<MobileElement> {
        val serverAddress = URL(SeleniumGridManager.serverAddress)
        return if (isAndroid) {
            createAndroidDriver(serverAddress)
        } else {
            IOSDriver(serverAddress, iOSCapabilities())
        }
    }

    @Step("[INFO] Create Android driver")
    private fun createAndroidDriver(serverAddress: URL): AppiumDriver<MobileElement> {
        val localDriver: AppiumDriver<MobileElement> = AndroidDriver(serverAddress, androidCapabilities())
        if (Android.wasWifiDisconnected(localDriver.asAndroidDriver())) {
            val steveInfo = getSteveInfo(localDriver)
            val deviceInfo = getDeviceInfo(localDriver)
            localDriver.quit()
            throw WebDriverException("Wifi detected OFF. So reenabled and throw exception to recreate driver. Steve info: $steveInfo Device info: $deviceInfo")
        }
        return localDriver
    }

    private fun getDeviceInfo(localDriver: AppiumDriver<MobileElement>): String {
        val sb = StringBuilder()
        runCatching {
            val udid = localDriver.sessionDetails["deviceUDID"].toString()
            sb.append("DEVICE_UDID: $udid ")
            sb.append("DEVICE_NAME: ${Devices.getName(udid)}")
        }
        return sb.toString()
    }

    @Step("[INFO] Get information about steve with wifi device off")
    private fun getSteveInfo(localDriver: AppiumDriver<MobileElement>): String {
        val (host, port) = SeleniumGridManager.getNodeInformation(localDriver.sessionId)
        val steve = StevesMap.getSteve(host)
        return "Steve name: ${steve?.name ?: "N/A"} ZeroTier: ${steve?.zeroTier ?: "N/A"} IP: $host:$port"
    }

    @Step("[INFO] retry action on exception")
    private fun retryOnException(block: () -> AppiumDriver<MobileElement>, method: Method?, count: Int = 3): AppiumDriver<MobileElement>? {
        var errorMessage = ""
        var fullErrorMessage = ""
        for (i in 0 until count) {
            try {
                return block()
            } catch (e: WebDriverException) {
                errorMessage = e.localizedMessage.take(250)
                fullErrorMessage = e.localizedMessage
                if (e.localizedMessage.contains("does not exist or is not accessible")) {
                    VintedAssert.fail("App/apk was not found:\n${e.localizedMessage}")
                }
                Report.addMessage("Retry $i, error: $e")
            }
        }
        AllureReportTestsWithoutSessionHelper.extractDeviceUdidFromErrorMessage(fullErrorMessage, method)
        VintedAssert.fail("Was not able to create a driver. WebDriverException message: $errorMessage")
        return null
    }

    @Step("Quit driver")
    fun quitDriver(retryCount: Int = 3) {
        Util.retryOnException(
            block = { driver.quit() },
            count = retryCount
        )
        addOrRemoveDriver(driver, false)
    }

    private fun androidCapabilities(): DesiredCapabilities {
        val capabilities = createCommonCapabilities()

        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300)
        capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, true)
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android")
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2")
        capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true)
        capabilities.setCapability("appium:enforceAppInstall", true)
        capabilities.setCapability(AndroidMobileCapabilityType.ADB_EXEC_TIMEOUT, "30000")
        if (isSandbox) {
            capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, "com.vinted.activities.CountrySelectionActivity")
        }

        val udid = SpecificDeviceHandler.getDeviceUdid()
        if (!udid.isNullOrEmpty()) {
            capabilities.setCapability(MobileCapabilityType.UDID, udid)
        }

        if (portal.setDefaultLanguage) {
            capabilities.setCapability(
                AndroidMobileCapabilityType.OPTIONAL_INTENT_ARGUMENTS,
                "-a \"android.intent.action.VIEW\" -d \"vinted://change_language?code=${portal.country.language.code}\""
            )
        }

        return capabilities
    }

    private fun iOSCapabilities(): DesiredCapabilities {
        val capabilities = createCommonCapabilities()

        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120)
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS")
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 8")
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13.2")
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest")
        capabilities.setCapability(MobileCapabilityType.NO_RESET, false)
        capabilities.setCapability(IOSMobileCapabilityType.CONNECT_HARDWARE_KEYBOARD, "false")
        capabilities.setCapability("appium:permissions", permissions)
        capabilities.setCapability(IOSMobileCapabilityType.USE_NEW_WDA, "true")
        capabilities.setCapability("appium:waitForQuiescence", "false")
        capabilities.setCapability(IOSMobileCapabilityType.MAX_TYPING_FREQUENCY, 7)
        capabilities.setCapability(IOSMobileCapabilityType.SIMPLE_ISVISIBLE_CHECK, "true")
        capabilities.setCapability(IOSMobileCapabilityType.COMMAND_TIMEOUTS, "{\"setValue\": 90000, \"findElement\": 30000, \"findElements\": 30000, \"performTouch\":60000, \"default\": 15000 }")

        // Seems like setting up language and locale capabilities makes additional problems on simulators
/*        val locale = LocaleFactory.getLocale()
        capabilities.setCapability(MobileCapabilityType.LANGUAGE, locale.language)
        capabilities.setCapability(MobileCapabilityType.LOCALE, locale.toString())*/
        if (isRealIosDevice) {
            capabilities.setCapability(IOSMobileCapabilityType.XCODE_ORG_ID, "4Y2CNF6C99")
            capabilities.setCapability(IOSMobileCapabilityType.XCODE_SIGNING_ID, "iPhone Developer")
            capabilities.setCapability(MobileCapabilityType.UDID, "e8b55f58b5eff7901667ac92aa4e4d2b3b4e2092")
            capabilities.setCapability(IOSMobileCapabilityType.UPDATE_WDA_BUNDLEID, portal.mobile.appPackage.ios)
        }
        return capabilities
    }

    private fun createCommonCapabilities(): DesiredCapabilities {
        val capabilities = DesiredCapabilities()
        capabilities.setCapability(MobileCapabilityType.APP, appPath().also { commonUtil.reporting.Report.addMessage("PATH to app was: '$it'") })
        return capabilities
    }
}

fun AppiumDriver<MobileElement>.asAndroidDriver() = this as AndroidDriver
fun AppiumDriver<MobileElement>.asIOSDriver() = this as IOSDriver

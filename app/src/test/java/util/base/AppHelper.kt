package util.base

import RobotFactory.deepLink
import RobotFactory.globalRobot
import RobotFactory.welcomeRobot
import commonUtil.reporting.Report
import commonUtil.testng.config.ConfigManager
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import org.openqa.selenium.WebDriverException
import util.Android
import util.IOS
import util.driver.WebDriverFactory
import util.driver.asAndroidDriver
import util.driver.asIOSDriver
import util.image.Screenshot

class AppHelper {

    @Step("Restart app")
    fun restartApp() {
        resetApp()
        deepLink.reset()
        iOSActivateApp()
        selectSandboxAndLanguage()
        welcomeRobot.assertWelcomeScreenIsVisible()
        Report.addMessage("App was reset and no login was performed in this step")
        RecordingHelper().startRecording()
    }

    @Step("Activate iOS app")
    fun iOSActivateApp() {
        IOS.doIfiOS {
            WebDriverFactory.driver.asIOSDriver().activateApp(ConfigManager.portal.mobile.appPackage.ios)
        }
    }

    @Step("Reset app")
    private fun resetApp() {
        kotlin.runCatching {
            Report.addImage(Screenshot.takeScreenshot().screenshot)
        }
        WebDriverFactory.driver.resetApp()
    }

    @Step("Restore app and phone state")
    fun restoreAppState() {
        Android.doIfAndroid {
            // Try to close notification tray all the time. There's no way to tell if it's open or not
            Android.closeNotificationsTray()
            Android.closeKeyboard()

            val currentActivity = WebDriverFactory.driver.asAndroidDriver().currentActivity()
            val expectedActivity = "com.vinted.activities.MDActivity"
            if (currentActivity != expectedActivity) {
                commonUtil.reporting.Report.addMessage("Current activity '$currentActivity' did not match expected '$expectedActivity' activity ")
                try {
                    Android.clickHome()
                    Android.clickBack()
                } catch (e: WebDriverException) {
                    commonUtil.reporting.Report.addMessage("Exception ${e.message}")
                }
            }
        }
    }

    @Step("Select sandbox")
    private fun selectSandbox() {
        if (PortalFactory.isSandbox) globalRobot.selectSandboxAndConfirm()
    }

    @Step("Select sandbox and language")
    fun selectSandboxAndLanguage() {
        Android.closeKeyboard()
        selectSandbox()
        welcomeRobot.assertWelcomeScreenIsVisible()
    }
}

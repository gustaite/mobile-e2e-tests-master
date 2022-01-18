package util.deepLinks

import RobotFactory.cmpCookiesRobot
import RobotFactory.iOSPermissionRobot
import RobotFactory.navigationRobot
import RobotFactory.settingsRobot
import commonUtil.testng.config.ConfigManager.portal
import api.TestDataApi
import api.data.models.VintedUser
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.data.enums.VintedCountries
import commonUtil.reporting.Report
import commonUtil.thread
import io.qameta.allure.Step
import org.openqa.selenium.NoAlertPresentException
import robot.BaseRobot
import robot.profile.settings.SettingsRobot
import robot.welcome.WelcomeRobot
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.IOS
import util.Util
import util.base.BaseTest.Companion.loggedInUser
import util.driver.Wait
import util.values.Visibility

/**
 * https://github.com/vinted/android/wiki/App-URIs
 */

class DeepLink : BaseRobot() {

    companion object DeepLinkCount {
        private var iOSDeepLinkCounter by thread(0)
    }

    val profile: Profile get() = Profile()
    val setting: Setting get() = Setting()
    val verification: Verification get() = Verification()
    val forum: Forum get() = Forum()
    val payment: Payment get() = Payment()
    val item: Item get() = Item()
    val miscellaneous: Miscellaneous get() = Miscellaneous()
    val conversation: Conversation get() = Conversation()
    val catalog: Catalog get() = Catalog()

    @Step("Deeplink reset")
    fun reset() {
        iOSDeepLinkCounter = 0
        Report.addMessage("Deep link counter was reset to 0 on thread: ${Thread.currentThread().id}")
    }

    @Step("Disable push notifications screen")
    fun disablePushNotificationScreen() {
        IOS.doIfiOS {
            openURL("autotests/toggle_feature?feature=skip_push_notifications_access&enabled=true")
        }
    }

    @Step("Open feed")
    fun goToFeed() {
        openURL("home")
        if (modalOkButton.isVisible(3)) modalOkButton.click()
    }

    @Step("Open main settings")
    fun goToSettings(): SettingsRobot {
        commonUtil.Util.retryUntil(
            block = {
                openURL("user/settings")
                settingsRobot.isSettingsActionBarVisible()
            },
            tryForSeconds = 5
        )
        return settingsRobot
    }

    @Step("Login to {user.username} with deep link")
    fun loginToAccount(user: VintedUser) {
        loginToAccountWithDeepLink(user)
        cmpCookiesRobot.acceptCmpCookies()
        navigationRobot.waitForNavigationBarIsVisible()
        loggedInUser = user
    }

    @Step("Login to {user.username} with deep link and do not accept CMP")
    fun loginToAccountAndDoNotAcceptCMP(user: VintedUser) {
        loginToAccountWithDeepLink(user)
        cmpCookiesRobot.assertCmpSettingsButtonVisibility(visibility = Visibility.Visible)
        loggedInUser = user
    }

    @Step("Login to {user.username} with deep link")
    private fun loginToAccountWithDeepLink(user: VintedUser) {
        if (isAndroid) {
            openURL("account_login?login=${user.username}&password=${TestDataApi.passwords.encoded}")
        } else {
            openURL("login?username=${user.username}&password=${user.password}")
        }
        handleAlertIos()
    }

    @Step("Handle alert (only IOS)")
    fun handleAlertIos() {
        IOS.doIfiOS {
            Wait.waitForAlert()
            try {
                driver!!.switchTo().alert().accept()
                Report.addMessage("Alert was present and test handled it")
            } catch (e: NoAlertPresentException) {
                Report.addMessage("No alerts existed")
            }
        }
    }

    @Step("Logout")
    fun logout(): WelcomeRobot {
        openURL("logout")
        return WelcomeRobot()
    }

    fun startLogsCollection(methodName: String) {
        openURL("autotests/logs/start_test_case?name=$methodName")
    }

    fun stopLogsCollection(logFile: String) {
        openURL("autotests/logs/export_test_case_logs?output=$logFile")
    }

    fun selectLanguageiOS() {
        if (isiOS && portal.setDefaultLanguage) {
            selectLanguageiOS(portal.country)
        }
    }

    @Step("Select language '{country.language.code}' (iOS only)")
    fun selectLanguageiOS(country: VintedCountries) {
        IOS.doIfiOS {
            sleepWithinStep(1500) // Try to add timeout before changing language as sometimes it is changed back to LT
            openURL("change_language?code=${country.language.code}")
            sleepWithinStep(3000) // No other way how to know when language change is executed
        }
    }

    @Step("Open link with full url: {fullUrl}")
    fun openLinkWithFullUrl(fullUrl: String) {
        openURL(fullUrl)
    }

    @Step("Open url {url}")
    fun openURL(url: String) {
        val fullUrl = if (url.contains("://")) url else "${portal.mobile.scheme}://$url"
        Report.addMessage("Deep link: $fullUrl")
        Report.addMessage("Counter: $iOSDeepLinkCounter / Thread: ${Thread.currentThread().id}")
        Util.retryOnException(
            block = {
                driver!!.get(fullUrl)
                if (iOSDeepLinkCounter == 0) iOSPermissionRobot.handleOpenUrlPermission()
            },
            count = 2
        )
        iOSDeepLinkCounter = 1
        Report.addMessage("OpenURL END: Counter: $iOSDeepLinkCounter / Thread: ${Thread.currentThread().id}")
    }
}

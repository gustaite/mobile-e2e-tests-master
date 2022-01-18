package test.basic

import RobotFactory.deepLink
import RobotFactory.feedRobot
import RobotFactory.navigationRobot
import api.controllers.GlobalAPI
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.driver.WebDriverFactory.driver
import java.lang.reflect.Method

@RunMobile(country = VintedCountry.INT, neverRunOnSandbox = true)
@Feature("Localisation tests")
class LocalisationTests : BaseTest() {

    @LoginToNewUser
    @Test(description = "Change app language and assert that it is changed")
    fun testChangingAppLanguage() {
        val randomLanguage = GlobalAPI.getLanguagesByCountry(loggedInUser.billingAddress.countryId, loggedInUser)
            .filter { !it.current && it.code != "en-fr" }.random()

        deepLink
            .goToSettings()
            .openLanguageSelection()
            .selectLanguage(randomLanguage)
        feedRobot.waitForItemsToBeVisible()
        navigationRobot.assertNavigationBarIsVisible()
        deepLink
            .goToSettings()
            .assertLanguageHasChangedTo(randomLanguage)
    }

    @AfterMethod(description = "Reset app and select default Lithuanian language")
    fun resetAppAndSelectLanguage(method: Method) {
        if (method.name == "testChangingAppLanguage") {
            driver.resetApp()
            deepLink.reset()
        }
    }
}

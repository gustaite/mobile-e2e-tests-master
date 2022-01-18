package test.basic

import RobotFactory.cmpCookiesRobot
import RobotFactory.cmpCookiesSettingsRobot
import RobotFactory.cmpWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.navigationWorkflowRobot
import RobotFactory.workflowRobot
import api.factories.UserFactory
import commonUtil.testng.ResetAppBeforeTest
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.VintedDriver
import util.base.BaseTest
import util.values.ToggleName
import util.values.ToggleValue
import util.values.Visibility

@Feature("Cmp Cookies tests")
class CmpCookiesTests : BaseTest() {

    @BeforeMethod(description = "Create user and login")
    fun createNewUserAndLogin() {
        loggedInUser = UserFactory.createRandomUser()
        deepLink.loginToAccountAndDoNotAcceptCMP(loggedInUser)
    }

    @ResetAppBeforeTest
    @RunMobile
    @Test(description = "Assert that cmp toggles can be accepted from preference center")
    @TmsLinks(TmsLink("24492"), TmsLink("27812"))
    fun testAcceptAllCookiesFromPreferenceCenter() {
        cmpCookiesRobot.openCmpCookiesSettings()
        cmpCookiesSettingsRobot.allowAllCmpCookies()
        navigationWorkflowRobot.openPrivacyScreenFromProfileTab()
        cmpCookiesSettingsRobot.assertAllCmpToggleValues(toggleValue = ToggleValue.ON)
    }

    @Issue("ADS-1260")
    @ResetAppBeforeTest
    @RunMobile
    @Test(description = "Assert that cmp toggles can be personalised from preference center")
    @TmsLinks(TmsLink("24493"), TmsLink("24499"))
    fun testPersonaliseCookiesFromPreferenceCenter() {
        cmpCookiesRobot
            .openCmpCookiesSettings()
            .scrollDownToPerformanceCookieWithValueAndAssertTitleIsVisible(toggleValue = ToggleValue.OFF)
            .clickOnToggleAndAssertValue(toggleName = ToggleName.PERFORMANCE_COOKIES, initialToggleValue = ToggleValue.OFF, expectedToggleValue = ToggleValue.ON)
            .confirmMyChoicesAndOpenPrivacyScreenFromProfileTab()
            .scrollDownToPerformanceCookieWithValueAndAssertTitleIsVisible(toggleValue = ToggleValue.ON)
            .assertOneCmpToggleValue(toggleName = ToggleName.PERFORMANCE_COOKIES, toggleValue = ToggleValue.ON)
            .assertAtLeastOneToggleValue(toggleValue = ToggleValue.OFF)
    }

    // TODO : apply for Android when links ids are implemented
    @ResetAppBeforeTest
    @RunMobile(platform = VintedPlatform.IOS)
    @Test(description = "Assert that all cmp and vendors toggles can be accepted from cmp screen")
    @TmsLink("24497")
    fun testAcceptAllCookiesFromCmpScreen() {
        cmpCookiesRobot.acceptCmpCookies()
        navigationWorkflowRobot.openPrivacyScreenFromProfileTab()
        cmpCookiesSettingsRobot
            .assertAllCmpToggleValues(ToggleValue.ON)
            .openVendorsLinkAndAssertAllowAllAndFirstVendorToggleValue(allowAllValue = ToggleValue.ON, firsToggleValue = ToggleValue.ON)
    }

    // TODO : apply for Android when links ids are implemented
    @ResetAppBeforeTest
    @RunMobile(platform = VintedPlatform.IOS)
    @Test(description = "Assert that vendors toggles can be personalised and do not affect cmp toggles")
    @TmsLink("24497")
    fun testPersonaliseVendorsToggles() {
        cmpCookiesRobot.acceptCmpCookies()
        navigationWorkflowRobot
            .openPrivacyScreenFromProfileTab()
            .openVendorsLinkAndAssertAllowAllAndFirstVendorToggleValue(allowAllValue = ToggleValue.ON, firsToggleValue = ToggleValue.ON)
            .clickAllowAllToggleAndAssertToggleValues(allowAllValue = ToggleValue.OFF, firsToggleValue = ToggleValue.OFF)
        workflowRobot.clickBack()
        cmpWorkflowRobot.confirmMyChoicesAndOpenPrivacyScreenFromProfileTab()
        cmpCookiesSettingsRobot
            .assertAllCmpToggleValues(ToggleValue.ON)
            .openVendorsLinkAndAssertAllowAllAndFirstVendorToggleValue(allowAllValue = ToggleValue.OFF, firsToggleValue = ToggleValue.OFF)
            .clickFirstVendorToggleAndAssertToggleValue(ToggleValue.ON)
        workflowRobot.clickBack()
        cmpWorkflowRobot
            .confirmMyChoicesAndOpenPrivacyScreenFromProfileTab()
            .openVendorsLinkAndAssertAllowAllAndFirstVendorToggleValue(allowAllValue = ToggleValue.OFF, firsToggleValue = ToggleValue.ON)
    }

    @ResetAppBeforeTest
    @RunMobile
    @Test(description = "Assert that CMP modal is visible/not visible when reopening app if accepted/not accepted before")
    @TmsLinks(TmsLink("24495"), TmsLink("24501"))
    fun testCmpModalVisibilityWhenReopeningApp() {
        cmpCookiesRobot.assertCmpSettingsButtonVisibility(visibility = Visibility.Visible)
        VintedDriver.sendAppToBackgroundAndOpenAgain()
        cmpCookiesRobot
            .assertCmpSettingsButtonVisibility(visibility = Visibility.Visible)
            .acceptCmpCookies()
        VintedDriver.sendAppToBackgroundAndOpenAgain()
        cmpCookiesRobot.assertCmpSettingsButtonVisibility(visibility = Visibility.Invisible)
    }
}

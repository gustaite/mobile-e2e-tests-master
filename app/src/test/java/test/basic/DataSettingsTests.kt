package test.basic

import RobotFactory.dataSettingsRobot
import RobotFactory.dataSettingsWorkflowRobot
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import robot.profile.settings.Link
import robot.profile.settings.Toggle
import util.base.BaseTest
import util.values.Enablement
import util.values.Visibility

@RunMobile
@LoginToNewUser
@Feature("Data Settings Tests")
class DataSettingsTests : BaseTest() {

    @RunMobile(country = VintedCountry.INT, message = "Test for INT only")
    @Test(description = "Asserting that Third Party and Personalised Content Toggles clicks work when Personalised Content ON")
    fun testIfPartyAndContentTogglesClicksWork() {
        dataSettingsWorkflowRobot
            .navigateAndOpenDataSettings()
            .assertPartyAndContentTogglesON()
            .disablePartyAndContentTogglesAndAssertTheyAreOFF()
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_INT, message = "Test for all except INT")
    @Test(description = "Asserting that Third Party Toggles click works when Personalised Content OFF")
    fun testIfPartyToggleClickWorks() {
        dataSettingsWorkflowRobot
            .navigateAndOpenDataSettings()
            .assertPartyToggleValue(Toggle.ON)
            .disablePartyToggleAndAssertItIsOFF()
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_INT, message = "Test for all except INT")
    @Test(description = "Asserting Third Party Toggle Remains ON when clicking on Third Party link when Personalised Content OFF")
    fun testIfPartyLinkDoesNotTurnThirdPartyToggleOFF() {
        dataSettingsWorkflowRobot
            .navigateAndOpenDataSettings()
        dataSettingsRobot
            .assertThirdPartyToggleValue(Toggle.ON)
            .clickOnLinkAndGoBack(Link.PARTY)
        dataSettingsRobot
            .assertThirdPartyToggleValue(Toggle.ON)
        dataSettingsWorkflowRobot
            .goBackToProfileAndOpenDataSettings()
        dataSettingsWorkflowRobot
            .disableThirdPartyToggleAndAssertItIsOFF()
    }

    @RunMobile(country = VintedCountry.INT, message = "Test for INT only")
    @Test(description = "Asserting Third Party and Personalized Content Toggles are not changed after clicking on Third Party link when Personalised Content ON")
    fun testIfPartyLinkRemainsPartyAndContentToggles() {
        dataSettingsWorkflowRobot
            .navigateAndOpenDataSettings()
            .assertPartyAndContentToggleValues(Toggle.ON, Toggle.ON)
            .clickOnThirdPartyToggleAndAssertValue(Toggle.OFF)
        dataSettingsRobot
            .clickOnLinkAndGoBack(Link.PARTY)
            .assertPartyAndContentToggleValues(Toggle.OFF, Toggle.ON)
            .clickOnPartyAndContentToggles()
            .assertPartyToggleONAndContentToggleOFFWithBackToProfile(Toggle.ON, Toggle.OFF)
    }

    @Test(description = "Download user data export when email is not verified")
    @TmsLink("23887")
    fun testDownloadUserDataWhenEmailIsNotConfirmed() {
        dataSettingsWorkflowRobot.navigateAndOpenDataSettings()
        dataSettingsRobot
            .clickOnDownloadAccountData()
            .clickOnRequestData()
            .assertEmailConfirmationBannerVisibility(Visibility.Visible)
            .clickOnDownloadAccountData()
            .assertRequestDataButtonEnablement(Enablement.ENABLED)
    }
}

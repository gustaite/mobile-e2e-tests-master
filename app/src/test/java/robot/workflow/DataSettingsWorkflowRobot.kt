package robot.workflow

import RobotFactory.dataSettingsRobot
import RobotFactory.dataSettingsWorkflowRobot
import RobotFactory.navigationRobot
import RobotFactory.settingsRobot
import RobotFactory.workflowRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.settings.DataSettingsRobot
import robot.profile.settings.Toggle

class DataSettingsWorkflowRobot : BaseRobot() {

    @Step("Navigate to and open Data Settings screen")
    fun navigateAndOpenDataSettings(): DataSettingsWorkflowRobot {
        navigationRobot
            .openProfileTab()
            .openSettingsScreen()
            .openDataSettings()

        return this
    }

    @Step("Go back to Profile and open Data Settings screen again")
    fun goBackToProfileAndOpenDataSettings(): DataSettingsRobot {
        workflowRobot.clickBack()
        settingsRobot.openDataSettings()

        return DataSettingsRobot()
    }

    @Step("Click on Third Party toggle and assert its value {toggleValueParty}")
    fun clickOnThirdPartyToggleAndAssertValue(toggleValueParty: Toggle): DataSettingsRobot {
        dataSettingsRobot
            .clickOnThirdPartyToggle()
            .assertThirdPartyToggleValue(toggleValueParty)

        return DataSettingsRobot()
    }

    @Step("Disable Third Party toggle and assert it is OFF with back to Profile")
    fun disableThirdPartyToggleAndAssertItIsOFF(): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .assertThirdPartyToggleValue(Toggle.ON)
        dataSettingsWorkflowRobot
            .clickOnThirdPartyToggleAndAssertValue(Toggle.OFF)
        workflowRobot.clickBack()
        settingsRobot.openDataSettings()
        dataSettingsRobot
            .assertThirdPartyToggleValue(Toggle.OFF)

        return this
    }

    @Step("Disable Third Party and Personalised Content Toggles and assert they are OFF with back to Profile")
    fun disablePartyAndContentTogglesAndAssertTheyAreOFF(): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .clickOnThirdPartyToggle()
            .clickOnPersonalisedContentToggle()
        assertPartyAndContentToggleValues(Toggle.OFF, Toggle.OFF)
        goBackToProfileAndOpenDataSettings()
        assertPartyAndContentToggleValues(Toggle.OFF, Toggle.OFF)

        return this
    }

    @Step("Disable Data Personalization and Third Party Toggles and assert they are OFF with back to Profile")
    fun disablePartyToggleAndAssertItIsOFF(): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .clickOnThirdPartyToggle()
        assertPartyToggleValue(Toggle.OFF)
        goBackToProfileAndOpenDataSettings()
        assertPartyToggleValue(Toggle.OFF)

        return this
    }

    @Step("Click on Third Party and Personalised Content Toggles")
    fun clickOnPartyAndContentToggles(): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .clickOnThirdPartyToggle()
            .clickOnPersonalisedContentToggle()
        assertPartyAndContentToggleValues(Toggle.ON, Toggle.OFF)

        return this
    }

    @Step("Assert Third Party Toggle is {toggleValueParty} and Personalised Content Toggle is {toggleValueContent} after navigating from profile")
    fun assertPartyToggleONAndContentToggleOFFWithBackToProfile(
        toggleValueParty: Toggle,
        toggleValueContent: Toggle
    ): DataSettingsWorkflowRobot {
        goBackToProfileAndOpenDataSettings()
        assertPartyAndContentToggleValues(toggleValueParty, toggleValueContent)

        return this
    }

    @Step("Assert Data Personalization, Third Party and Personalised Content Toggles are ON")
    fun assertPartyAndContentTogglesON(): DataSettingsWorkflowRobot {
        assertPartyAndContentToggleValues(Toggle.ON, Toggle.ON)

        return this
    }

    @Step("Assert Data Personalization {toggleValueData} and Third Party {toggleValueParty} Toggle values")
    fun assertPartyToggleValue(
        toggleValueParty: Toggle
    ): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .assertThirdPartyToggleValue(toggleValueParty)

        return this
    }

    @Step("Assert Third Party {toggleValueParty} and Personalised Content {toggleValueContent} Toggle values")
    fun assertPartyAndContentToggleValues(
        toggleValueParty: Toggle,
        toggleValueContent: Toggle
    ): DataSettingsWorkflowRobot {
        dataSettingsRobot
            .assertThirdPartyToggleValue(toggleValueParty)
            .assertPersonalisedContentToggleValue(toggleValueContent)

        return this
    }
}

package robot.profile.settings

import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.user.userApi
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.workflow.DataSettingsWorkflowRobot
import util.IOS
import util.VintedDriver
import util.assertVisibilityEquals
import util.base.BaseTest
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Enablement
import util.values.Visibility

class DataSettingsRobot {

    private val thirdPartyToggleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("third_party_tracking_toggle"),
            iOSBy = VintedBy.accessibilityId("tracking")
        )

    private val thirdPartyLinkElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("privacy_policy_link"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("user_settings_third_party_tracking_policy_link"))
        )

    private val personalisedContentToggleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("personalized_recommendations_toggle"),
            iOSBy = VintedBy.accessibilityId("user_personalistaion")
        )

    private val downloadAccountDataElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("data_settings_export"),
            iOSBy = VintedBy.accessibilityId((IOS.getElementValue("user_settings_data_export_cell_body")))
        )

    private val requestDataButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("data_export_email_confirmation_button"),
            iOSBy = VintedBy.accessibilityId("data_export_request_data")
        )

    private val emailConfirmationBanner: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("view_notification_container"), iosElement = { emailConfirmationBannerIos() })

    private fun emailConfirmationBannerIos(): VintedElement {
        var emailConfirmationText = IOS.getElementValue("email_confirm_change_sent")
        emailConfirmationText = emailConfirmationText.replace("%{confirm_email}%", loggedInUser.email)
        return IOS.findElementByTextContains(emailConfirmationText)
    }

    @Step("Click on Link")
    fun clickOnLinkAndGoBack(link: Link): DataSettingsWorkflowRobot {
        when (link) {
            Link.PARTY -> thirdPartyLinkElement.click()
        }
        workflowRobot.clickBack()
        return DataSettingsWorkflowRobot()
    }

    @Step("Click on Third Party Toggle")
    fun clickOnThirdPartyToggle(): DataSettingsRobot {
        thirdPartyToggleElement.click()
        return this
    }

    @Step("Assert Third Party Tracking Toggle value")
    fun assertThirdPartyToggleValue(toggle: Toggle): DataSettingsRobot {
        AssertApi.assertApiResponseWithWait(
            actual = { BaseTest.loggedInUser.userApi.getInfo().thirdPartyTracking.toString() },
            expected = toggle.toggleValueParty.toString(),
            errorMessage = "User's Third-party Tracking Toggle should be ${toggle.toggleValueParty}"
        )

        return this
    }

    @Step("Assert Personalised content Toggle value")
    fun assertPersonalisedContentToggleValue(toggle: Toggle): DataSettingsRobot {
        AssertApi.assertApiResponseWithWait(
            actual = { BaseTest.loggedInUser.userApi.getInfo().personalisedContent.toString() },
            expected = toggle.toggleValueContent.toString(),
            errorMessage = "User's Personalised Content Toggle should be ${toggle.toggleValueContent}"
        )
        return this
    }

    @Step("Click on Personalised Content Toggle")
    fun clickOnPersonalisedContentToggle(): DataSettingsRobot {
        personalisedContentToggleElement.click()
        return this
    }

    @Step("Click on download account data")
    fun clickOnDownloadAccountData(): DataSettingsRobot {
        downloadAccountDataElement.click()
        return this
    }

    @Step("Click on request data")
    fun clickOnRequestData(): DataSettingsRobot {
        requestDataButtonElement.click()
        return this
    }

    @Step("Assert that request data button is {enablement}")
    fun assertRequestDataButtonEnablement(enablement: Enablement): DataSettingsRobot {
        val buttonEnablement = requestDataButtonElement.isEnabled
        VintedAssert.assertEquals(
            buttonEnablement,
            enablement.value,
            "Request data button element expected to be with enablement value ${enablement.value}, but was found to be $buttonEnablement"
        )
        return this
    }

    @Step("Assert that email confirmation banner is {visibility}")
    fun assertEmailConfirmationBannerVisibility(visibility: Visibility): DataSettingsRobot {
        VintedAssert.assertVisibilityEquals(emailConfirmationBanner, visibility, "Email confirmation banner expected to be with visibility  $visibility")
        return this
    }
}

enum class Toggle(val toggleValueParty: Boolean, val toggleValueContent: Boolean) {
    ON(true, true),
    OFF(false, false)
}

enum class Link {
    PARTY
}

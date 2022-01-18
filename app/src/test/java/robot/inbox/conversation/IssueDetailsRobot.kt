package robot.inbox.conversation

import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.Session.Companion.sessionDetails
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ConversationElementTexts
import util.values.ElementByLanguage.Companion.contactUsText
import util.values.LaboratoryDevice

class IssueDetailsRobot : BaseRobot() {

    private val resolveIssueOrCancelAndKeepButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_complaint_resolve_button"),
            iOSBy = VintedBy.accessibilityId("refund")
        )

    private val contactUsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("complaint_note"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeButton[`name CONTAINS '$contactUsText'`]")
        )

    private val agreeAndSubmitButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { modalOkButton },
            iOSBy = VintedBy.iOSNsPredicateString("name == '${ConversationElementTexts.agreeAndSubmitText}'")
        )

    private val imageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_carousel_pager"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeImage")
        )

    private val moreInfoLink: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "complaint_more_info",
            iosTranslationKey = "complaint_proof_more_info"
        )

    @Step("Assert proof image, resolve button and more info link is visible")
    fun assertProofImageResolveButtonAndMoreInfoLinkIsVisible(): IssueDetailsRobot {
        assertResolveButtonVisible()
        assertProofImageVisible()
        assertMoreInfoLinkIsVisible()
        return this
    }

    @Step("Assert 'Resolve issue' button is visible")
    private fun assertResolveButtonVisible(): IssueDetailsRobot {
        VintedAssert.assertTrue(resolveIssueOrCancelAndKeepButton.isVisible(), "'Resolve issue' button should be visible")
        return this
    }

    @Step("Assert proof image visible")
    private fun assertProofImageVisible(): IssueDetailsRobot {
        VintedAssert.assertTrue(imageElement.isVisible(25), "Proof image should be visible")
        return this
    }

    @Step("Assert more info link is visible")
    private fun assertMoreInfoLinkIsVisible(): IssueDetailsRobot {
        VintedAssert.assertTrue(moreInfoLink.isVisible(), "More info link should be visible")
        return this
    }

    @Step("Submit to Team Vinted")
    fun submitToVinted(): ConversationRobot {
        Android.scrollDownABit()
        commonUtil.reporting.Report.addMessage("Device model ${sessionDetails.deviceModel}")
        val yOnce =
            when (sessionDetails.deviceModel) {
                LaboratoryDevice.S10.model, LaboratoryDevice.XCOVER_PRO.model -> -1
                else -> -10
            }
        contactUsButton.withScrollIos()
            .tapRightBottomCorner(
                xStep = if (isAndroid) 100 else 50,
                yOnce = yOnce,
                visibilityCheck = { agreeAndSubmitButton.isVisible(1) }
            )
        agreeAndSubmitButton.click()
        return conversationRobot
    }
}

package robot.webview

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.conversation.ProvideProofRobot
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.values.ConversationElementTexts

class WebViewRobot : BaseRobot() {
    private val webViewElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.className("android.webkit.WebView"),
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.className("XCUIElementTypeWebView"),
                    iosBy2 = VintedBy.iOSNsPredicateString("name CONTAINS '${IOS.getElementValue("help_center_contextual_title")}'")
                )
            }
        )

    private val provideProofButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText("submit", ConversationElementTexts.provideProofText),
            iOSBy = VintedBy.accessibilityId("continue_to_contact_us")
        )

    @Step("Check if webview is opened")
    fun assertWebViewIsVisible(): WebViewRobot {
        VintedAssert.assertTrue(webViewElement.withWait(waitFor = WaitFor.Visible, seconds = 25).isVisible(), "Webview should be visible")
        return this
    }

    @Step("Click provide proof button")
    fun clickProvideProofButton(): ProvideProofRobot {
        provideProofButton.withScrollIos().let {
            VintedAssert.assertTrue(it.withWait().isVisible(), "Provide proof button should be visible")
            it.click()
        }
        return ProvideProofRobot()
    }
}

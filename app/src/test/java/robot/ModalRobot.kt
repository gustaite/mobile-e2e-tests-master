package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import util.Android
import util.IOS
import util.VintedDriver
import org.testng.SkipException
import util.driver.VintedBy
import util.driver.VintedElement

class ModalRobot : BaseRobot() {

    private val modalElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("modal_container"))

    private val leavingVintedModalTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("modal_title", Android.getElementValue("conversation_external_link_modal_title")),
            VintedBy.accessibilityId(IOS.getElementValue("conversation_external_link_modal_title"))
        )

    private val modalContinueButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_primary_button", "conversation_external_link_modal_continue")

    private val modalCancelButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_secondary_button", "conversation_external_link_modal_cancel")

    private fun modalElement(text: String) = VintedDriver.findElement(androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false), iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true))

    @Step("Wait till modal is visible")
    fun isModalVisible(): Boolean {
        return modalElementAndroid.isVisible(3)
    }

    @Step("Assert leaving Vinted modal is visible")
    fun assertLeavingVintedModalIsVisible(): ModalRobot {
        VintedAssert.assertTrue(leavingVintedModalTitleElement.withWait().isVisible(), "Modal title should be visible")
        return this
    }

    @Step("Check if continue button is visible and clickable")
    fun checkIfContinueButtonIsVisibleAndClickable(): ModalRobot {
        VintedAssert.assertTrue(modalContinueButtonElement.withWait().isVisible(), "Modal continue button should be visible")
        Android.doIfAndroid {
            VintedAssert.assertTrue(modalContinueButtonElement.withWait().mobileElement.getAttribute("clickable")!!.equals("true"), "Continue button should be clickable")
        }
        return this
    }

    @Step("Close leaving Vinted modal")
    fun clickCancelToCloseModal() {
        modalCancelButtonElement.click()
    }

    @Step("Assert element with text '{text}' is visible in modal")
    fun assertElementWithTextIsVisibleInModal(text: String): ModalRobot {
        if (text == "") throw SkipException("Skipping test because provided string is empty")
        val cleanedUpText = text.cleanStringFromHTMLTags().changeNewLineWithSpace()
        VintedAssert.assertTrue(modalElement(cleanedUpText).isVisible(), "Element with text '$cleanedUpText' should be visible in modal")
        return this
    }

    private fun String.cleanStringFromHTMLTags(): String {
        val linkText = Jsoup.parse(this).select("a").first()?.text()

        if (linkText != null) {
            val displayedText = Jsoup.clean(this, Safelist())
            return displayedText.replace(linkText, " $linkText")
        }

        return this
    }

    private fun String.changeNewLineWithSpace(): String {
        return this.replace("\n", " ")
    }
}

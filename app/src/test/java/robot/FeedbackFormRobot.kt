package robot

import io.qameta.allure.Step
import org.openqa.selenium.NoSuchElementException
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class FeedbackFormRobot : BaseRobot() {

    private val feedbackRatingElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("feedback_rating"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[2]")
        )

    private val feedbackInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextView")
        )

    private val cancelButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("cancel"))
    private val submitButton: VintedElement get() = VintedDriver.elementByIdAndTranslationKey("menu_submit_button", "send")

    @Step("Select rating, type feedback and send it")
    fun selectRatingLeaveFeedbackAndSubmit(feedback: String): FeedbackFormRobot {
        feedbackInputElement.withWait().sendKeys(feedback)
        Android.closeKeyboard()
        feedbackRatingElement.click()
        submitButton.click()
        return this
    }

    @Step("Close rate first sell if window exists")
    fun closeFirstSellRateIFExists() {
        try {
            cancelButtonIos.click()
        } catch (e: NoSuchElementException) {
        }
    }
}

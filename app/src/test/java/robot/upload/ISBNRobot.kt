package robot.upload

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ISBNRobot : BaseRobot() {

    private val isbnNumberField: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextField")
        )

    private val submitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("isbn_submit"),
            iOSBy = VintedBy.accessibilityId("submit")
        )

    @Step("Enter ISBN number: {isbn} and submit")
    fun enterISBNAndSubmit(isbn: String): UploadItemRobot {
        isbnNumberField.withWait().sendKeys(isbn)
        Android.closeKeyboard()
        IOS.hideKeyboard()
        if (submitButton.isVisible()) {
            submitButton.click()
        }
        return UploadItemRobot()
    }
}

package robot.upload

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class SellingPriceRobot : BaseRobot() {

    private val priceField: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextField")
        )

    private val submitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("submit_button"),
            iOSBy = VintedBy.accessibilityId("price_done_button")
        )

    @Step("Enter price: {price} and submit")
    fun enterPriceAndSubmit(price: String): UploadItemRobot {
        priceField.withWait().clear()
        priceField.click().sendKeys(price)
        Android.closeKeyboard()
        submitButton.click()

        return UploadItemRobot()
    }
}

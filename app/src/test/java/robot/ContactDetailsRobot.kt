package robot

import RobotFactory.checkoutRobot
import io.qameta.allure.Step
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ContactDetailsRobot : BaseRobot() {

    private val phoneNumberInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("phone_number_input")
        )

    private val phoneNumberConfirmButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("contact_details_continue_button"),
            iOSBy = VintedBy.accessibilityId("phone_number_confirm_action")
        )

    @Step("Add phone number {phoneNumber}")
    fun addPhoneNumber(phoneNumber: String): ContactDetailsRobot {
        IOS.doIfiOS {
            phoneNumberInputElement.click()
            // .clear() does not clear number prefix on iOS
            repeat(4) { IOS.pressDeleteInKeyboard() }
        }
        phoneNumberInputElement.click().clear().sendKeys(phoneNumber)
        return this
    }

    @Step("Click confirm phone number")
    fun confirmPhoneNumber(): CheckoutRobot {
        phoneNumberConfirmButtonElement.click()
        return checkoutRobot
    }
}

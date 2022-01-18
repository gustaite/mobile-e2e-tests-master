package robot.profile.balance

import RobotFactory.billingAddressRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedElement
import util.driver.VintedBy

class BillingAddressValidationRobot : BaseRobot() {

    private val androidFullNameValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("full_address_name", Android.INPUT_VALIDATION_FIELD_ID)
        )

    private val androidAddressLine1ValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("full_address_line_1", Android.INPUT_VALIDATION_FIELD_ID)
        )

    private val androidPostalCodeValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("postal_code_input", Android.INPUT_VALIDATION_FIELD_ID)
        )

    @Step("Assert error validation messages are visible on address line 1 and post code")
    fun assertErrorValidationMessagesVisibleOnAddressLine1AndPostalCode(): BillingAddressRobot {
        Android.closeKeyboard()
        VintedAssert.assertTrue(androidAddressLine1ValidationElement.isVisible(), "Address line 1 error validation message should be visible")
        VintedAssert.assertTrue(androidPostalCodeValidationElement.isVisible(), "Postal code error validation message should be visible")
        return billingAddressRobot
    }

    @Step("Assert error validation message is visible on full name")
    fun assertErrorValidationMessageVisibleOnFullName(): BillingAddressRobot {
        VintedAssert.assertTrue(androidFullNameValidationElement.isVisible(), "Full name error validation message should be visible")
        return billingAddressRobot
    }
}

package robot.profile.balance

import RobotFactory.paymentAccountDetailsRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedElement
import util.driver.VintedBy

class PaymentAccountValidationRobot : BaseRobot() {

    private val fullNameValidationElement: VintedElement
        get() =
            VintedDriver.findElement(
                androidBy = VintedBy.scrollableSetWithParentAndChild("payments_account_details_name", Android.INPUT_VALIDATION_FIELD_ID),
                iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextField[`name CONTAINS \"${IOS.getElementValue("create_payments_account_error_no_name_message")}\"`]")
            )

    private val birthdayValidationElement: VintedElement
        get() =
            VintedDriver.findElement(
                androidBy = VintedBy.scrollableSetWithParentAndChild("payments_account_details_birthday", Android.INPUT_VALIDATION_FIELD_ID),
                iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name CONTAINS \"${IOS.getElementValue("create_payments_account_error_no_birthday_message")}\"`]")
            )

    private val androidBillingAddressValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("payments_account_address", Android.CELL_VALIDATION_FIELD_ID)
        )

    private val billingAddressValidationElement: VintedElement
        get() =
            VintedDriver.elementByIdAndTranslationKey({ androidBillingAddressValidationElement }, "create_payments_account_error_no_billing_address_message")

    private val socialSecurityNumberValidationElement: VintedElement
        get() =
            VintedDriver.findElement(
                androidBy = VintedBy.scrollableSetWithParentAndChild("payments_account_details_personal_id_input", Android.INPUT_VALIDATION_FIELD_ID),
                iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'personal_id_number_input'`]/XCUIElementTypeOther/XCUIElementTypeOther[3]")
            )

    @Step("Assert error validation messages are shown on birthday and billing address")
    fun assertErrorValidationMessageVisibleOnFullName(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(fullNameValidationElement.isVisible(), "Full name validation element should be visible")
        return paymentAccountDetailsRobot
    }

    @Step("Assert error validation messages are shown on birthday and billing address")
    fun assertErrorValidationMessagesVisibleOnBirthdayBillingAddressAndSocialSecurityNumber(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(birthdayValidationElement.isVisible(), "Birthday validation element should be visible")
        VintedAssert.assertTrue(billingAddressValidationElement.isVisible(), "Billing address validation element should be visible")
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)) {
            VintedAssert.assertTrue(socialSecurityNumberValidationElement.isVisible(), "Social security number validation element should be visible")
        }
        return paymentAccountDetailsRobot
    }
}

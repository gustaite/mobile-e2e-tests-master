package robot.profile

import RobotFactory
import RobotFactory.billingAddressRobot
import commonUtil.data.enums.VintedBillingAddress
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedElement

class AddressRobot : BaseRobot() {

    private val fullAddressSaveButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("full_address_save", "done")

    private val personalDetailsScreenTitleElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("full_address_phone_number", "checkout_personal_details")

    @Step("Insert full address or personal details")
    fun insertFullAddressOrPersonalDetailsInfo(billingAddress: VintedBillingAddress): AddressRobot {
        if (billingAddress.needsPersonalDetails && personalDetailsScreenTitleElement.isVisible()) {
            billingAddressRobot.fillPhoneNumber(billingAddress.getPhoneNumber())
        } else {
            billingAddressRobot.fillBillingAddress(billingAddress)
        }
        return this
    }

    @Step("Save full address")
    fun saveFullAddress(): ShippingScreenRobot {
        fullAddressSaveButtonElement.click()
        return RobotFactory.shippingScreenRobot
    }
}

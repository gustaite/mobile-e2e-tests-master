package robot.profile.balance

import RobotFactory.paymentAccountDetailsRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedBillingAddress
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.Session.Companion.isOldIos
import util.base.BaseTest
import util.base.BaseTest.Companion.loggedInUser
import util.driver.*
import util.values.ElementByLanguage.Companion.postalCodeText

class BillingAddressRobot : BaseRobot() {

    private val androidFullNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "full_address_name",
                Android.INPUT_FIELD_ID
            )
        )
    private val fullNameElement: VintedElement
        get() =
            inputElement({ androidFullNameElement }, getValueByIosVersion("add_address_name_title", "checkout_billing_shipping_name_placeholder"))

    private val filledNameElement: VintedElement get() = inputElement({ null }, "${loggedInUser.realName}")

    private val androidAddressLine1Element: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "full_address_line_1",
                Android.INPUT_FIELD_ID
            )
        )
    private val addressLine1Element: VintedElement
        get() =
            inputElement({ androidAddressLine1Element }, getValueByIosVersion("add_address_line1_title", "add_address_line1_placeholder"))

    private val androidAddressLine2Element: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "full_address_line_2",
                Android.INPUT_FIELD_ID
            )
        )
    private val addressLine2Element: VintedElement
        get() =
            inputElement({ androidAddressLine2Element }, getValueByIosVersion("add_address_line2_title", "add_address_line2_placeholder"))

    private val postalCodeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "postal_code_input",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField' AND name == '$postalCodeText'")
        )

    private val androidCityElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild(
                        "postal_code_city_selector",
                        Android.INPUT_FIELD_ID
                    ),
                    androidBy2 = VintedBy.setWithParentAndChild(
                        "postal_code_city_single",
                        Android.INPUT_FIELD_ID
                    )
                )
            },
        )

    private fun cityElement(city: String): VintedElement =
        inputElement({ androidCityElement }, if (isOldIos) IOS.getElementValue("checkout_billing_shipping_city_label") else city)

    private val citySelectorButtonIos get() = VintedDriver.findElement(iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name == 'city_selector_cell'`]/XCUIElementTypeButton"))

    private val saveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("full_address_save"),
            iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("done")}' || name == 'submit'")
        )
    private val cancelButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("cancel"))
    private val closeButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("close"))

    private val personalDetailsTextFieldElement: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id(Android.INPUT_FIELD_ID),
            VintedBy.className("XCUIElementTypeTextField")
        )

    val validationRobot get() = BillingAddressValidationRobot()

    @Step("Fill billing address")
    fun fillBillingAddress(billingAddress: VintedBillingAddress): BillingAddressRobot {
        enterFullName(billingAddress.fullName)
        enterAddress1Line(billingAddress.address1)
        enterAddress2Line(billingAddress.address2)
        enterPostalCode(billingAddress.postalCode)
        assertThatCityIsFoundByPostalCode(billingAddress.city)
        return this
    }

    @Step("Enter full name: {fullName}")
    fun enterFullName(fullName: String): BillingAddressRobot {
        IOS.doIfiOS {
            if (!filledNameElement.isVisible(2)) {
                fullNameElement.clearOldIos()
                fullNameElement.sendKeys(fullName)
            }
        }
        Android.doIfAndroid {
            fullNameElement.sendKeys(fullName)
            Android.closeKeyboard()
        }

        return this
    }

    @Step("Clear full name")
    fun clearFullName(): BillingAddressRobot {
        Android.doIfAndroid {
            fullNameElement.click().clear()
            Android.closeKeyboard()
        }
        return this
    }

    @Step("Enter address line 1")
    fun enterAddress1Line(address: String): BillingAddressRobot {
        addressLine1Element.clear().sendKeys(address)
        Android.closeKeyboard()
        return this
    }

    @Step("Enter address line 2")
    fun enterAddress2Line(address: String): BillingAddressRobot {
        if (address.isEmpty()) {
            commonUtil.reporting.Report.addMessage("Second address was empty")
            return this
        }

        addressLine2Element.clear().sendKeys(address)
        Android.closeKeyboard()
        return this
    }

    @Step("Enter postal code")
    fun enterPostalCode(postalCode: String): BillingAddressRobot {
        postalCodeElement.clear().sendKeys(postalCode)
        Android.closeKeyboard()
        closeKeyboardIos()
        return this
    }

    // todo remove when MARIOS-989 issue is fixed
    @Step("US and Android only: Clear postal code")
    fun clearPostalCodeUSAndroid(): BillingAddressRobot {
        if (isAndroid && (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US))) postalCodeElement.clear()
        return this
    }

    @Step("Assert city {city} is found by postal code")
    fun assertThatCityIsFoundByPostalCode(city: String): BillingAddressRobot {
        IOS.doIfiOS {
            IOS.scrollDown()
            if (!cityElement(city).isVisible(1)) {
                citySelectorButtonIos.click()
                clickSave()
            }
        }

        val cityTextElement = cityElement(city)

        Wait.forElementTextToMatch(cityElement(city), city)
        val actualCity = cityTextElement.text
        if (isOldIos) {
            return this // Old ios cannot see value of city element
        }
        VintedAssert.assertEquals(actualCity.lowercase(), city.lowercase(), "City found by postal code")
        return this
    }

    @Step("Click save")
    fun clickSave(): PaymentAccountDetailsRobot {
        saveButton.click()
        return paymentAccountDetailsRobot
    }

    @Step("Fill phone number")
    fun fillPhoneNumber(phoneNumber: String): BillingAddressRobot {
        personalDetailsTextFieldElement.last().sendKeys(phoneNumber)
        closeKeyboardIos()
        return this
    }

    private fun getValueByIosVersion(oldValue: String, newValue: String): String = IOS.getElementValue(if (isOldIos) oldValue else newValue)

    private fun inputElement(androidElement: () -> VintedElement?, value: String): VintedElement = if (isAndroid) {
        androidElement()!!
    } else {
        if (isOldIos) {
            VintedDriver.findElement(iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[\$name ==[c] \"${value}\"\$]/XCUIElementTypeTextField"))
        } else {
            VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("value ==[c] '$value'"))
        }
    }

    private fun closeKeyboardIos() {
        IOS.doIfiOS {
            if (cancelButtonIos.isVisible()) {
                cancelButtonIos.click()
                closeButtonIos.click()
            } else {
                IOS.hideKeyboard()
                VintedDriver.pullDownToRefresh()
            }
        }
    }

    @Step("Open, fill and save billing address")
    fun openFillAndSaveBillingAddress(): PaymentAccountDetailsRobot {
        paymentAccountDetailsRobot
            .clickAddBillingAddress()
            .fillBillingAddress(BaseTest.loggedInUser.billingAddress)
            .clickSave()
        return paymentAccountDetailsRobot
    }
}

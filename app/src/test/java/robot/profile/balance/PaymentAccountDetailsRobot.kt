package robot.profile.balance

import RobotFactory.walletRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedBillingAddress
import commonUtil.data.enums.VintedPortal
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.BaseRobot
import robot.CalendarRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.reporting.AllureReport
import java.time.LocalDate

class PaymentAccountDetailsRobot : BaseRobot() {

    private val fullNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "payments_account_details_name",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.className("XCUIElementTypeTextField")
        )

    private val androidBirthdayElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payments_account_details_birthday",
                Android.INPUT_FIELD_ID
            )
        )

    private val birthdayElement: VintedElement
        get() =
            VintedDriver.elementByIdAndTranslationKey({ androidBirthdayElement }, "create_bank_account_birthdate")

    private val addBillingAddressButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("address_block_new_address"),
            VintedBy.accessibilityId("add_address_block")
        )

    private val editBillingAddressButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("address_block_existing_address"),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeAny[`name == '${loggedInUser.realName}'`]")
        )

    private val socialSecurityNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild(
                        "payments_account_details_ssn_input",
                        Android.INPUT_FIELD_ID
                    ),
                    androidBy2 = VintedBy.setWithParentAndChild(
                        "payments_account_details_personal_id_input",
                        Android.INPUT_FIELD_ID
                    )
                )
            },
            iOSBy = VintedBy.accessibilityId("personal_id_number_input")
        )

    private val billingAddressBlockElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("address_block_existing_address"))
    private val billingAddressNameElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("view_cell_title"))
    private val billingAddressElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id(Android.CELL_BODY_FIELD_ID))

    private val saveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("payments_account_submit_button"),
            iOSBy = VintedBy.accessibilityId("submit")
        )

    private val continueButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("payments_account_details_continue"),
            iOSBy = VintedBy.accessibilityId("personal_details_continue")
        )

    val validationRobot get() = PaymentAccountValidationRobot()

    private val cancelButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_button"),
            iOSBy = VintedBy.accessibilityId("cancel")
        )

    private val closeButtonIos: VintedElement get() = IOS.findElementByTranslationKey("close")

    @Step("Enter full name")
    fun enterFullName(fullName: String): PaymentAccountDetailsRobot {
        fullNameElement.click().clear().sendKeys(fullName)
        return this
    }

    @Step("Clear full name")
    fun clearFullName(): PaymentAccountDetailsRobot {
        fullNameElement.click().withWait().clear()
        if (isiOS) {
            cancelButton.click()
            closeButtonIos.click()
        }
        return this
    }

    @Step("Enter birthday")
    fun enterBirthday(): PaymentAccountDetailsRobot {
        birthdayElement.click()
        AllureReport.addScreenshot()
        CalendarRobot().selectYesterdayInCalendar()
        CalendarRobot().clickOkInCalendar()
        AllureReport.addScreenshot()
        return this
    }

    @Step("Enter security number")
    fun enterSecurityNumber(): PaymentAccountDetailsRobot {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)) {
            socialSecurityNumberElement.sendKeys(VintedShippingAddress.US.socialSecurityNumber!!)
            IOS.doIfiOS {
                // issue with keyboard after entering security number in US
                clickBack()
                closeModal()
            }
            return this
        }
        commonUtil.reporting.Report.addMessage("Social security is only for US and should not be visible in other countries")
        VintedAssert.assertTrue(socialSecurityNumberElement.isInvisible(1), "Social security number element should be not visible")
        return this
    }

    @Step("Click add billing address")
    fun clickAddBillingAddress(): BillingAddressRobot {
        Android.closeKeyboard()
        VintedDriver.scrollDown()
        addBillingAddressButton.tap()
        return BillingAddressRobot()
    }

    @Step("Click edit billing address")
    fun clickEditBillingAddress(): BillingAddressRobot {
        Android.closeKeyboard()
        editBillingAddressButton.tap()
        return BillingAddressRobot()
    }

    @Step("Click save")
    fun clickSave(): WalletRobot {
        saveButton.withWait(waitFor = WaitFor.Visible, seconds = 3).click()
        return walletRobot
    }

    @Step("Assert social security number is hidden")
    fun assertSocialSecurityNumberIsHidden(): PaymentAccountDetailsRobot {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)) {
            val secureText = "*********"
            if (isAndroid) {
                VintedAssert.assertEquals(socialSecurityNumberElement.text, secureText, "Social security number should be hidden")
            } else {
                VintedAssert.assertTrue(
                    IOS.findElementByTextContains(secureText, type = IOS.ElementType.TEXT_FIELD).isVisible(),
                    "Social security number should be hidden"
                )
            }
            return this
        }
        commonUtil.reporting.Report.addMessage("Social security is only for US")
        return this
    }

    @Step(
        "Assert billing address is {billingAddress.fullName}, {billingAddress.address1} {billingAddress.address2}, " +
            "{billingAddress.postalCode} {billingAddress.city} "
    )
    fun assertBillingAddress(billingAddress: VintedBillingAddress): PaymentAccountDetailsRobot {
        if (isAndroid) {
            VintedAssert.assertTrue(billingAddressBlockElementAndroid.isVisible(), "Billing address block element should be visible")
            VintedAssert.assertEquals(billingAddressNameElementAndroid.text, billingAddress.fullName, "Billing address real name")
            val address = billingAddressElementAndroid.text
            VintedAssert.assertTrue(
                address.contains(billingAddress.address1, true),
                "Billing address should be ${billingAddress.address1}, but address is: $address"
            )
            if (billingAddress.address2.isNotEmpty()) {
                VintedAssert.assertTrue(
                    address.contains(billingAddress.address2, true),
                    "Billing address 2 should be ${billingAddress.address2}, but address is: $address"
                )
            }
            VintedAssert.assertTrue(
                address.contains(billingAddress.postalCode, true),
                "Postal code should be ${billingAddress.postalCode}, but address is: $address"
            )
            val city = billingAddress.city.replace(", CA", "")
            VintedAssert.assertTrue(
                address.contains(city, true),
                "City should be $city, but address is: $address"
            )
        } else {
            assertIosAddressElement(billingAddress.fullName)
            assertIosAddressElement(billingAddress.address1)
            if (billingAddress.address2.isNotEmpty()) {
                assertIosAddressElement(billingAddress.address2)
            }
            assertIosAddressElement(billingAddress.postalCode)
            assertIosAddressElement(billingAddress.city)
        }

        return this
    }

    private fun assertIosAddressElement(value: String) {
        VintedAssert.assertTrue(IOS.findElementByTextContains(value).isVisible(), "address should contains: $value")
    }

    @Step("Assert billing name is {realName}")
    fun assertBillingName(realName: String): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(VintedDriver.findElementByText(realName).isVisible(10), "Billing full name")
        return this
    }

    @Step("Assert birthday is not empty in payment account details")
    fun assertBirthday(): PaymentAccountDetailsRobot {
        if (isAndroid) {
            VintedAssert.assertTrue(birthdayElement.text.isNotEmpty(), "Birthday should not be empty")
        } else {
            VintedAssert.assertTrue(IOS.findElementByTextContains("${LocalDate.now().year - 18}").isVisible(), "Birthday  should not be empty")
        }
        return this
    }

    @Step("Click continue")
    fun clickContinue() {
        Android.closeKeyboard()
        IOS.hideKeyboard()
        continueButton.click()
    }
}

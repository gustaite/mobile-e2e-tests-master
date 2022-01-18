package robot.profile

import RobotFactory.securityWebViewRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.base.BaseTest.Companion.loggedInUser
import util.data.CreditCardDetails
import util.driver.VintedBy
import util.driver.VintedElement

class NewCreditCardRobot : BaseRobot() {
    // TODO: remove androidBy1 once cc_saving_trust_and_safety_improvements is scaled
    private val androidCreditCardNameElement: VintedElement
        get() = Android.findAllElement(
            androidBy1 = VintedBy.setWithParentAndChild(
                "credit_card_holder_name_input_v2",
                Android.INPUT_FIELD_ID
            ),
            androidBy2 = VintedBy.setWithParentAndChild(
                "credit_card_holder_name_input",
                Android.INPUT_FIELD_ID
            )
        )

    private val creditCardNameElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey({ androidCreditCardNameElement }, "add_credit_card_name_placeholder")

    private val filledCreditCardNameElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("value CONTAINS '${loggedInUser.realName}'"))

    // TODO: remove androidBy1 once cc_saving_trust_and_safety_improvements is scaled
    private val creditCardNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild("credit_card_number_input_v2", Android.INPUT_FIELD_ID),
                    androidBy2 = VintedBy.setWithParentAndChild("credit_card_number_input", Android.INPUT_FIELD_ID)
                )
            },
            iOSBy = VintedBy.accessibilityId("card_number_input")
        )

    // TODO: remove androidBy1 once cc_saving_trust_and_safety_improvements is scaled
    private val expirationDateElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild("credit_card_expiration_v2", Android.INPUT_FIELD_ID),
                    androidBy2 = VintedBy.setWithParentAndChild("credit_card_expiration", Android.INPUT_FIELD_ID)
                )
            },
            iOSBy = VintedBy.accessibilityId("expiration_date_input"),
        )

    // TODO: remove androidBy1 once cc_saving_trust_and_safety_improvements is scaled
    private val cvvCodeElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild("credit_card_cvv_v2", Android.INPUT_FIELD_ID),
                    androidBy2 = VintedBy.setWithParentAndChild("credit_card_cvv", Android.INPUT_FIELD_ID)
                )
            },
            iOSBy = VintedBy.accessibilityId("card_cvv_input")
        )

    // TODO: remove androidBy1 once cc_saving_trust_and_safety_improvements is scaled
    private val postalCodeElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild("credit_card_post_code_v2", Android.INPUT_FIELD_ID),
                    androidBy2 = VintedBy.setWithParentAndChild("credit_card_post_code", Android.INPUT_FIELD_ID)
                )
            },
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[5]/XCUIElementTypeTextField")
        )

    private val creditCardButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_submit"),
            iOSBy = VintedBy.accessibilityId("submit")
        )

    private val checkedSaveCreditCardBoxElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_submit"),
            iOSBy = VintedBy.iOSNsPredicateString("label == 'Box checked'")
        )

    private val validationElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("view_input_validation"),
            VintedBy.className("XCUIElementTypeStaticText")
        )

    private val cancelButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("cancel"))

    private val closeButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("close"))

    private val rememberCreditCardElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_save_data"),
            iOSBy = VintedBy.accessibilityId("remember_checkbox_cell")
        )

    @Step("Insert credentials and save credit card")
    fun insertNewCreditCardInfo(creditCardDetails: CreditCardDetails.CreditCard): NewCreditCardRobot {
        IOS.doIfiOS {
            if (filledCreditCardNameElementIos.isInvisible()) {
                creditCardNameElement.sendKeys(creditCardDetails.name)
            }
        }
        creditCardNumberElement.withWait().sendKeys(creditCardDetails.number)
        when {
            isAndroid ->
                expirationDateElement.sendKeys(creditCardDetails.date_month, "/", creditCardDetails.date_year)
            else -> {
                expirationDateElement.sendKeys(creditCardDetails.date_month, creditCardDetails.date_year)
            }
        }
        cvvCodeElement.sendKeys(creditCardDetails.cvv)
        // ToDo review this after US production will be migrated to Internation
        if (portal == VintedPortal.US) {
            postalCodeElement.sendKeys("64153")
            IOS.doIfiOS {
                cancelButtonIos.click()
                closeButtonIos.click()
            }
        }
        return this
    }

    @Step("Save credit card and handle 3ds (if needed)")
    fun saveCreditCardAndHandle3dsIfNeeded(): PaymentsScreenRobot {
        saveCreditCard()
        securityWebViewRobot.simulateSuccessful3dsResponseAfterAddingCreditCard()
        return PaymentsScreenRobot()
    }

    @Step("Save credit card")
    fun saveCreditCard(): PaymentsScreenRobot {
        IOS.hideKeyboard()
        creditCardButtonElement.click()
        return PaymentsScreenRobot()
    }

    @Step("Check validations in new credit card screen")
    fun assertValidationsAreVisible() {
        creditCardNameElement.sendKeys("Abc")
        creditCardNumberElement.sendKeys("1234")
        expirationDateElement.sendKeys("01", "/", "15")
        cvvCodeElement.click()
        Android.closeKeyboard()
        saveCreditCard()
        Android.closeKeyboard()
        if (validationElementList.size < 3) {
            Android.closeKeyboard()
        }
        VintedAssert.assertTrue(validationElementList[0].mobileElement.isDisplayed, "Name validation should be visible")
        VintedAssert.assertTrue(validationElementList[1].mobileElement.isDisplayed, "Number validation should be visible")
        VintedAssert.assertTrue(validationElementList[2].mobileElement.isDisplayed, "Date validation should be visible")
    }

    // ToDo change unselect card functionality after 21.41 release is built
    @Step("Unselect save credit card option")
    fun unselectSaveCreditCardOption(): NewCreditCardRobot {
        if (isRememberCreditCardElementChecked()) rememberCreditCardElement.click()
        return this
    }

    // ToDo change select card functionality after 21.41 release is built
    @Step("Select save credit card option")
    fun selectSaveCreditCardOption(): NewCreditCardRobot {
        if (!isRememberCreditCardElementChecked()) rememberCreditCardElement.click()
        return this
    }

    @Step("Check if remember credit card element is checked")
    private fun isRememberCreditCardElementChecked(): Boolean {
        return if (isAndroid) {
            rememberCreditCardElement.isElementChecked()
        } else {
            checkedSaveCreditCardBoxElement.isVisible()
        }
    }
}

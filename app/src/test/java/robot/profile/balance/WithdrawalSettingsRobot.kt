package robot.profile.balance

import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android.Companion.INPUT_FIELD_ID
import util.IOS
import util.VintedDriver
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.UserBankAccountDetailsTexts.Companion.accountNumberText
import util.values.UserBankAccountDetailsTexts.Companion.ibanNumberText
import util.values.UserBankAccountDetailsTexts.Companion.sortCodeOrRoutingNumberText

class WithdrawalSettingsRobot : BaseRobot() {

    private val withdrawalBankAccountNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("bank_account_name", INPUT_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextField[`value == \"${loggedInUser.realName}\"`]"),
        )

    private val withdrawalBankSortOrRoutingElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("bank_account_additional_no", INPUT_FIELD_ID),
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.iOSClassChain(
                        "**/XCUIElementTypeTextField[`(label == '${IOS.getElementValue(
                            "create_bank_account_sort_code_title"
                        )}' && value == '${IOS.getElementValue("create_bank_account_sort_code_hint")}')`]"
                    ),
                    iosBy2 = VintedBy.iOSClassChain(
                        "**/XCUIElementTypeTextField[`(label == '${IOS.getElementValue(
                            "create_bank_account_routing_number_title"
                        )}' && value == '${IOS.getElementValue("create_bank_account_routing_number_hint")}')`]"
                    )
                )
            }
        )

    private val withdrawalBankAccountIbanElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("bank_account_account_no", INPUT_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeTextField[`(label == '${IOS.getElementValue(
                    "create_bank_account_iban_section_title"
                )}' && value == '${IOS.getElementValue("create_bank_account_iban_hint")}')`]"
            )
        )

    private val withdrawalBankAccountNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("bank_account_account_no", INPUT_FIELD_ID),
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.iOSClassChain(
                        "**/XCUIElementTypeTextField[`(label == '${IOS.getElementValue(
                            "create_bank_account_us_account_number_title"
                        )}' && value == '${IOS.getElementValue("create_bank_account_us_account_number_hint")}')`]"
                    ),
                    iosBy2 = VintedBy.iOSClassChain(
                        "**/XCUIElementTypeTextField[`(label == '${IOS.getElementValue(
                            "create_bank_account_gb_account_number_title"
                        )}' && value == '${IOS.getElementValue("create_bank_account_gb_account_number_hint")}')`]"
                    )
                )
            }
        )

    @Step("Add bank account holders name, iban and sort code if needed")
    fun addPayoutBankAccountDetails(): WithdrawalSettingsRobot {
        addAccountHoldersName()
        addSortCodeOrRoutingNumber()
        addIbanOrAccountNumber()
        return this
    }

    @Step("Add account holders name")
    private fun addAccountHoldersName() {
        withdrawalBankAccountNameElement.sendKeys(loggedInUser.realName)
    }

    @Step("UK/US only: Add sort code/routing number")
    private fun addSortCodeOrRoutingNumber() {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.UK) || (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)))
            withdrawalBankSortOrRoutingElement.sendKeys(sortCodeOrRoutingNumberText)
    }

    @Step("Add IBAN or Account number")
    private fun addIbanOrAccountNumber() {
        try {
            withdrawalBankAccountNumberElement.sendKeys(accountNumberText)
        } catch (e: NotImplementedError) {
            withdrawalBankAccountIbanElement.sendKeys(ibanNumberText)
        }
    }
}

package robot.profile.balance

import RobotFactory.paymentAccountDetailsRobot
import RobotFactory.withdrawalRobot
import api.controllers.user.userApi
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement
import java.math.BigDecimal
import util.values.Visibility

class WalletRobot : BaseRobot() {

    private fun balanceIosClassChain(index: Int): String =
        "**/XCUIElementTypeCell[`name != 'invoices_history_invoice_line'`]/XCUIElementTypeStaticText[`${IOS.predicateWithCurrencySymbols}`][$index]"

    private val activateVintedBalanceElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("invoice_pay_out_action", Android.getElementValue("invoice_confirm_details")),
            iOSBy = VintedBy.accessibilityId("invoice_confirm_identity")
        )

    private val settingsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("action_menu_my_id"),
            iOSBy = VintedBy.accessibilityId("invoice_id_button")
        )
    private val pendingBalanceAmountElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("invoice_pending_balance"),
            iOSBy = VintedBy.iOSClassChain(balanceIosClassChain(1))
        )
    private val availableBalanceAmountElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("invoice_balance"),
            iOSBy = VintedBy.iOSClassChain(balanceIosClassChain(2))
        )
    private val withdrawMoneyButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("invoice_pay_out_action", Android.getElementValue("invoice_cash_out")),
            iOSBy = VintedBy.accessibilityId("invoice_make_payout_short_button_title")
        )

    private fun invoiceTitleElement(itemName: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "balance_invoice_line_cell",
                Android.CELL_TITLE_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name CONTAINS 'invoices_history_invoice_line'`]/XCUIElementTypeStaticText[`NOT (name CONTAINS '$itemName' OR ${IOS.predicateWithCurrencySymbols})`]")
        )

    private fun itemNameElement(itemName: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "balance_invoice_line_cell",
                "balance_invoice_line_status"
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name CONTAINS 'invoices_history_invoice_line'`]/XCUIElementTypeStaticText[`name CONTAINS '$itemName'`]")
        )

    private fun invoiceAmountElement(): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.id("balance_invoice_line_amount"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name CONTAINS 'invoices_history_invoice_line'`]/XCUIElementTypeStaticText[`${IOS.predicateWithCurrencySymbols}`]")
        )

    private val invoiceNavigationArrowElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("balance_invoice_line_nav_arrow"))

    @Step("Click activate Vinted balance")
    fun clickActivateVintedBalance(): PaymentAccountDetailsRobot {
        activateVintedBalanceElement.click()
        return paymentAccountDetailsRobot
    }

    @Step("Assert activate Vinted balance button is visible")
    fun assertActivateBalanceButtonIsVisible(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(activateVintedBalanceElement.isVisible(), "Activate Vinted Balance button should be visible")
        return paymentAccountDetailsRobot
    }

    @Step("Assert withdraw money button is visible")
    fun assertWithdrawMoneyButtonIsVisible(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(withdrawMoneyButton.isVisible(), "Withdraw money button should be visible")
        return paymentAccountDetailsRobot
    }

    @Step("Assert withdraw money from Vinted wallet button is disabled")
    fun assertWithdrawMoneyFromVintedWalletButtonIsDisabled(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(
            withdrawMoneyButton.isVisible() && !withdrawMoneyButton.isEnabled,
            "Withdraw money from Vinted balance button should be visible and disabled"
        )
        return paymentAccountDetailsRobot
    }

    @Step("Click withdraw money from Vinted balance")
    fun clickWithdrawMoneyFromVintedBalance(): WithdrawalRobot {
        withdrawMoneyButton.click()
        return withdrawalRobot
    }

    @Step("Open payment account details")
    fun openPaymentAccountDetails(): PaymentAccountDetailsRobot {
        VintedAssert.assertTrue(settingsElement.isVisible(10), "Settings element should be visible")
        settingsElement.click()
        return paymentAccountDetailsRobot
    }

    @Step("Assert at least one invoice is visible")
    fun assertInvoiceIsVisible(itemName: String, visibility: Visibility = Visibility.Visible): WalletRobot {
        VintedAssert.assertTrue(invoiceTitleElement(itemName).isVisible(), "Invoice title element should be visible") // Bought or Sold
        VintedAssert.assertTrue(itemNameElement(itemName).isVisible(), "Item name element should be visible")
        VintedAssert.assertTrue(invoiceAmountElement().isVisible(), "Invoice amount element should be visible")
        assertInvoiceNavigationArrowVisibility(visibility)
        return this
    }

    @Step("Assert invoice navigation arrow element is {visibility}")
    private fun assertInvoiceNavigationArrowVisibility(visibility: Visibility): WalletRobot {
        Android.doIfAndroid {
            VintedAssert.assertVisibilityEquals(invoiceNavigationArrowElementAndroid, visibility, "Invoice navigation arrow element should be $visibility")
        }
        return this
    }

    @Step("Assert invoice amount is {expectedAmount}")
    private fun assertInvoiceAmount(expectedAmount: String): WalletRobot {
        invoiceAmountElement().let {
            val text = it.text
            PriceFactory.assertContains(text, expectedAmount, "Invoice amount does not match. Actual: $text Expected: $expectedAmount")
        }
        return this
    }

    @Step("Assert item name is {name}")
    private fun assertItemName(name: String): WalletRobot {
        itemNameElement(name).let {
            val text = it.text
            VintedAssert.assertTrue(text.contains(name), "Item name does not match. Actual: $text Expected: $name")
        }
        return this
    }

    @Step("Assert balance amount is {expectedAmount}")
    private fun assertBalanceAmount(expectedAmount: String): WalletRobot {
        availableBalanceAmountElement.let {
            val text = it.text
            PriceFactory.assertContains(text, expectedAmount, "Balance amount does not match. Actual: $text Expected: $expectedAmount")
        }
        return this
    }

    @Step("Assert invoice title is {expectedTitle}")
    private fun assertInvoiceTitle(itemName: String, expectedTitle: String): WalletRobot {
        invoiceTitleElement(itemName).let {
            val text = it.text
            VintedAssert.assertTrue(text.contains(expectedTitle), "Invoice title does not match. Actual: $text Expected: $expectedTitle")
        }
        return this
    }

    @Step("Assert wallet and invoice amounts are correct and item title is expected")
    fun assertWalletInvoiceAmountAndItemTitleMatching(amount: BigDecimal, itemName: String, invoiceTitle: String): WalletRobot {
        val amountFormatted = PriceFactory.getFormattedPriceWithCurrencySymbol(amount.toDouble())
        assertBalanceAmount(amountFormatted)
        assertInvoiceAmount(amountFormatted)
        assertItemName(itemName)
        assertInvoiceTitle(itemName, invoiceTitle)
        return this
    }

    @Step("Assert withdraw money button and pending balance elements are visible")
    fun assertWithdrawMoneyAndPendingBalanceElementsAreVisible(): WalletRobot {
        VintedAssert.assertTrue(withdrawMoneyButton.isVisible(), "Withdraw money button should be visible")
        VintedAssert.assertTrue(pendingBalanceAmountElement.isVisible(), "Pending balance amount element should be visible")
        return this
    }

    @Step("Assert bump order invoice amount is correct and order title is expected")
    fun assertVASOrderInvoiceAmountAndOrderTitleMatching(amount: String, subtitle: String): WalletRobot {
        val invoiceTitle = loggedInUser.userApi.currentInvoice().invoiceLines?.first()?.title
        VintedAssert.assertFalse(invoiceTitle.isNullOrEmpty(), "Invoice title was not found")
        assertInvoiceAmount("-$amount")
        assertItemName(subtitle)
        assertInvoiceTitle(subtitle, invoiceTitle!!)
        return this
    }
}

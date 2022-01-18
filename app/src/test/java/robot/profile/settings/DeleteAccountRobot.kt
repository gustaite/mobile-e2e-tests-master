package robot.profile.settings

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import org.openqa.selenium.By
import robot.BaseRobot
import util.Android
import util.EnvironmentManager.isiOS
import util.IOS
import util.VintedDriver
import util.driver.*

class DeleteAccountRobot : BaseRobot() {

    private val transactionDeleteCheckbox: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_delete_tx_checkbox"),
            iOSBy = VintedBy.accessibilityId("delete_account_transaction_statement")
        )

    private val deleteButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_delete_confirm_button"),
            iOSBy = VintedBy.accessibilityId("delete_account_button")
        )

    private val validationErrorElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidElement = {
                VintedDriver.findElement(androidBy = VintedBy.setWithParentAndChild("user_delete_tx_checkbox_cell", "view_cell_validation"))
            },
            iosTranslationKey = "delete_user_obligation"
        )

    @Step("Mark checkbox indicating that transactions are completed")
    fun markTransactionCheckboxAsChecked(): DeleteAccountRobot {
        checkUncheckElement(transactionDeleteCheckbox, true)
        return this
    }

    @Step("Click to delete account")
    fun clickDeleteAccount(): DeleteAccountRobot {
        deleteButton.withScrollIos().click()
        if (deleteButton.isVisible()) {
            commonUtil.reporting.Report.addMessage("Click again")
            deleteButton.click()
        }
        return this
    }

    @Step("Check that completed transactions checkbox is marked")
    fun isTransactionsCheckboxChecked(): Boolean {
        return transactionDeleteCheckbox.isElementChecked()
    }

    @Step("ONLY Android: assert validation count is {count}")
    fun assertValidationCount(count: Int): DeleteAccountRobot {
        if (isiOS) {
            return this
        }

        val byLocator: By = VintedBy.id("view_cell_validation")
        Wait.waitForElementCount(byLocator, count)
        val list: List<VintedElement> = VintedDriver.findElementList(androidBy = byLocator)
        VintedAssert.assertEquals(list.size, count, "Validation error should be $count")

        return this
    }

    @Step("ONLY Android: check if transaction and data store checkboxes are not checked")
    fun assertTransactionCheckboxIsNotChecked(): DeleteAccountRobot {
        Android.doIfAndroid {
            VintedAssert.assertFalse(isTransactionsCheckboxChecked(), "Delete transactions checkbox should not be checked by default")
        }
        return this
    }

    private fun checkUncheckElement(element: VintedElement, check: Boolean) {
        if (isiOS) {
            IOS.tap(element.center.getX() + (element.size.getWidth() / 2 - 20), element.center.getY() - 15)
            return
        }

        val isChecked = element.isElementChecked()
        if (check && !isChecked) {
            element.click()
        }
        if (!check && isChecked) {
            element.click()
        }
    }

    @Step("Assert validation error is visible and delete button is not clickable")
    fun assertValidationErrorVisible(): DeleteAccountRobot {
        VintedAssert.assertTrue(validationErrorElement.withWait().isVisible(), "Validation error should be visible when there are transactions in progress")
        VintedAssert.assertFalse(deleteButton.isEnabled, "Delete button should not be clickable")
        return this
    }
}

package robot.payments

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class IDealRobot : BaseRobot() {

    private val chooseBankElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"pl-pm-ideal_3-bankIdList\")"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeOther' && value CONTAINS 'Choix de la banque'")
        )

    private val bankOptionsDropDownList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("android:id/text1"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypePicker/XCUIElementTypePickerWheel")
        )

    private val payButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"pl-pm-ideal_3-payBtn\")"),
            iOSBy = VintedBy.accessibilityId("PAYER AVEC IDEAL")
        )

    private val confirmTransactionButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().className(\"android.widget.Button\").textStartsWith(\"Confirm\")"),
            iOSBy = VintedBy.accessibilityId("Confirm Transaction")
        )

    private val okButton: VintedElement get() = VintedDriver.findElement(
        androidBy = VintedBy.id("android:id/button1"),
        iOSBy = VintedBy.accessibilityId("Done")
    )

    @Step("Pay with iDeal")
    fun payWithIDeal() {
        clickChooseBankDropDownElement()
        selectBankFromDropdownList()
        clickPayUsingIDealButton()
        clickConfirmTransactionButton()
    }

    @Step("Click on choose bank dropdown element")
    private fun clickChooseBankDropDownElement() {
        chooseBankElement.click()
    }

    @Step("Select bank from dropdown list")
    private fun selectBankFromDropdownList() {
        val bankSimulationName = "Issuer Simulation V3 - ING"
        VintedAssert.assertTrue(bankOptionsDropDownList.isNotEmpty(), "Choose bank option dropdown list should not be empty")
        Android.doIfAndroid { bankOptionsDropDownList[1].click() }
        IOS.doIfiOS {
            bankOptionsDropDownList[0].sendKeys(bankSimulationName)
            okButton.click()
        }
    }

    @Step("Click pay using iDeal button")
    private fun clickPayUsingIDealButton() {
        payButtonElement.click()
    }

    @Step("Click confirm transaction button")
    private fun clickConfirmTransactionButton() {
        confirmTransactionButton.click()
    }
}

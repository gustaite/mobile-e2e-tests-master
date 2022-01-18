package robot.payments

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class SofortRobot : BaseRobot() {
    companion object {
        private const val bankAccountCode = "88888888"
        private const val tanNumber = "12345"
    }

    private val payButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiScrollable(UiSelector()).scrollIntoView(UiSelector().resourceId(\"pl-pm-pmapinofield_3-payBtn\"))"),
            iOSBy = VintedBy.accessibilityId("MIT KLARNA PAY NOW BEZAHLEN")
        )

    private val acceptCookiesButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().className(\"android.widget.Button\").textContains(\"Alle akzeptieren\")"),
            iOSBy = VintedBy.accessibilityId("Alle akzeptieren")
        )

    private val countryNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"MultipaysSessionSenderCountryId\")"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeOther' && value CONTAINS 'Deutschland'")
        )

    private val bankNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"BankCodeSearch\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextField")
        )

    private val accountNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"BackendFormLOGINNAMEUSERID\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'main'`]/XCUIElementTypeOther[5]/XCUIElementTypeTextField")
        )

    private val accountPinNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"BackendFormUSERPIN\")"),
            iOSBy = VintedBy.className("XCUIElementTypeSecureTextField")
        )

    private val firstBankAccountElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"account-1\")"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && value CONTAINS 'Girokonto (Max Mustermann)'")
        )

    private val tanNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"BackendFormTan\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'main'`]/XCUIElementTypeOther[4]/XCUIElementTypeTextField")
        )

    private val continueButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiScrollable(UiSelector()).scrollIntoView(UiSelector().className(\"android.widget.Button\").textMatches(\"Weiter\"))"),
            iOSBy = VintedBy.accessibilityId("Weiter")
        )

    @Step("Pay with Sofort")
    fun payWithSofort() {
        clickPayUsingSofortButton()
        clickAcceptCookies()
        enterBankCodeAndClickContinue()
        enterAccountAndPinNumber()
        assertBankAccountIsVisibleAndClickContinue()
        enterTanNumberAndClickContinue()
    }

    @Step("Click pay using Sofort button")
    private fun clickPayUsingSofortButton() {
        payButton.click()
    }

    @Step("Click accept cookies button if exists")
    private fun clickAcceptCookies() {
        acceptCookiesButton.withWait().clickIfExists()
    }

    @Step("Enter bank code $bankAccountCode and click continue")
    private fun enterBankCodeAndClickContinue() {
        VintedAssert.assertTrue(countryNameElement.isVisible(10), "Country name element should be visible")
        bankNameElement.withWait().sendKeys(bankAccountCode)
        IOS.hideKeyboard()
        Android.doIfAndroid { continueButton.click() }
    }

    @Step("Enter account $bankAccountCode, pin number $bankAccountCode and click continue in Demo bank screen")
    private fun enterAccountAndPinNumber() {
        accountNumberElement.withWait().sendKeys(bankAccountCode)
        accountPinNumberElement.withWait().sendKeys(bankAccountCode)
        IOS.hideKeyboard()
        Android.closeKeyboard()
        Android.scrollDownABit()
        Android.doIfAndroid { continueButton.click() }
    }

    @Step("Assert Bank Account is visible and click continue")
    private fun assertBankAccountIsVisibleAndClickContinue() {
        VintedAssert.assertTrue(firstBankAccountElement.isVisible(10), "First bank account element should be visible")
        Android.doIfAndroid {
            VintedAssert.assertTrue(
                firstBankAccountElement.isElementChecked(),
                "First bank account element in the list should be checked"
            )
        }
        continueButton.click()
    }

    @Step("Enter TAN number $tanNumber and click continue")
    private fun enterTanNumberAndClickContinue() {
        tanNumberElement.sendKeys(tanNumber)
        IOS.hideKeyboard()
        Android.doIfAndroid {
            Android.closeKeyboard()
            continueButton.click()
        }
    }
}

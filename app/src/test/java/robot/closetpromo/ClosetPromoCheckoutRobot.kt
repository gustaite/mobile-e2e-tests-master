package robot.closetpromo

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.*
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.savedCreditCardText

class ClosetPromoCheckoutRobot : BaseRobot() {

    private val confirmClosetPromoOrderButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("order_submit"), iOSBy = VintedBy.accessibilityId("confirm"))

    private val orderSummaryElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("order_summary"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("promote_closet_order_total_amount_text"))
        )

    private val reviewOrderButtonElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("closet_promo_prepare_submit"),
            VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value CONTAINS '${ElementByLanguage.reviewOrderButtonText}'")
        )

    private val savedCreditCardElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextContains(savedCreditCardText)
        )

    @Step("Confirm closet promo order")
    fun confirmOrder() {
        VintedAssert.assertTrue(confirmClosetPromoOrderButton.withWait(WaitFor.Visible).isVisible(), "Confirm button should be visible")
        confirmClosetPromoOrderButton.click()
    }

    @Step("Check if closet promo order summary is displayed")
    fun assertOrderSummaryIsDisplayed(): ClosetPromoCheckoutRobot {
        VintedAssert.assertTrue(orderSummaryElement.isVisible(20), "Order price summary should be displayed")
        return this
    }

    @Step("Click review order in Closet Promo pre-checkout screen (when buying not for the first time)")
    fun clickReviewOrder(): ClosetPromoCheckoutRobot {
        reviewOrderButtonElement.click()
        return this
    }

    @Step("Click on saved credit card")
    fun clickOnSavedCreditCard(): ClosetPromoCheckoutRobot {
        Android.doIfAndroid { savedCreditCardElement.click() }
        return this
    }
}

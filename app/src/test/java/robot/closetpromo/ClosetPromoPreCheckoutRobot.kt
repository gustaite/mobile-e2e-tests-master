package robot.closetpromo

import RobotFactory.closetPromoPreCheckoutRobot
import RobotFactory.paymentsScreenRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.PaymentsScreenRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ClosetPromoPreCheckoutRobot : BaseRobot() {

    private val prepareButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_prepare_submit"),
            iOSBy = VintedBy.accessibilityId("confirm")
        )

    private val helpIconElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("menu_closet_promotion_help_center"), iOSBy = VintedBy.accessibilityId("help"))

    private val priceElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_option_price"),
            iOSBy = VintedBy.iOSClassChain("**XCUIElementTypeCell/XCUIElementTypeStaticText[`${IOS.predicateWithCurrencySymbolsGrouped}`]")
        )

    private val closetPromoDurationElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_period_duration"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '${IOS.getElementValue("day_count_other")}'")
        )

    private val howItWorksButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_prepare_how_it_works"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_how_it_works_btn"))
        )

    private val closetPromoHelpModalElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "modal_title",
                Android.getElementValue("closet_promo_value_proposition_dialog_title")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_value_proposition_dialog_title"))
        )

    private val closeClosetPromoHelpModalButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { modalOkButton },
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_value_proposition_dismiss"))
        )

    @Step("Click confirm in pre-checkout screen")
    fun clickConfirm(): PaymentsScreenRobot {
        prepareButton.click()
        return paymentsScreenRobot
    }

    @Step("Click on how it works button")
    fun clickOnHowItWorks() {
        howItWorksButton.click()
    }

    @Step("Check if closet promo price is displayed")
    fun assertPriceIsDisplayed(): ClosetPromoPreCheckoutRobot {
        VintedAssert.assertTrue(priceElement.isVisible(), "Closet promo price should be displayed")
        return closetPromoPreCheckoutRobot
    }

    @Step("Check if closet promo duration is displayed")
    fun assertDurationIsDisplayed(): ClosetPromoPreCheckoutRobot {
        VintedAssert.assertTrue(closetPromoDurationElement.isVisible(), "Closet promo duration should be displayed")
        return this
    }

    @Step("Click on help icon")
    fun clickOnHelpIcon(): ClosetPromoPreCheckoutRobot {
        helpIconElement.click()
        return closetPromoPreCheckoutRobot
    }

    @Step("Assert 'closet promo help modal' is visible")
    fun assertClosetPromoHelpModalVisible(): ClosetPromoPreCheckoutRobot {
        VintedAssert.assertTrue(closetPromoHelpModalElement.isVisible(), "Closet promo help modal should be visible")
        return closetPromoPreCheckoutRobot
    }

    @Step("Close 'closet promo help' modal")
    fun closeClosetPromoHelpModal(): ClosetPromoPreCheckoutRobot {
        closeClosetPromoHelpModalButton.click()
        return closetPromoPreCheckoutRobot
    }
}

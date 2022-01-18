package robot.bumps

import RobotFactory
import io.qameta.allure.Step
import util.*
import RobotFactory.bumpsPreCheckoutRobot
import api.controllers.user.userApi
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.PortalFactory
import robot.*
import robot.payments.PaymentMethodsRobot
import robot.webview.WebViewRobot
import util.base.BaseTest.Companion.loggedInUser
import util.driver.*

class BumpsPreCheckoutRobot : BaseRobot() {
    private val infoIconElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("menu_bump_help_center"),
            iOSBy = VintedBy.accessibilityId("help")
        )

    private val knowMoreElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "modal_secondary_button",
            "item_push_up_value_alert_learn_more_button"
        )

    private val closeButtonIos: VintedElement
        get() = VintedDriver.findElement(null, iOSBy = VintedBy.accessibilityId("close"))

    private val bumpDurationElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("push_up_period_duration"),
            VintedBy.iOSNsPredicateString("name CONTAINS '${IOS.getElementValue("day_count_other")}' && visible == 1")
        )

    private val vasPricingRulesElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "dynamic_bump_price_pricing_explanation",
            "item_bump_dynamic_pricing_rules_note"
        )

    private val bumpsInfoModalOkElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("modal_primary_button"),
            iOSBy = VintedBy.accessibilityId("cancel")
        )

    private val bumpInfoModalTitle: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "modal_title",
            "item_push_up_value_dialog_title_2"
        )

    private val confirmButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_bump_prepare_submit"),
            iOSBy = VintedBy.accessibilityId("confirm")
        )

    private val addMoreItemsButtonAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("item_bump_prepare_add_more"))
    private val itemsToBumpImageElementListAndroid: List<VintedElement> get() = Android.findElementList(VintedBy.id("bump_item_image"))
    private val itemsToBumpLargeImageElementListAndroid: List<VintedElement> get() = Android.findElementList(VintedBy.id("dynamic_bump_price_item_photo"))

    @Step("Close bumps info modal")
    fun closeBumpsInfoModalIfVisible(): BumpsPreCheckoutRobot {
        if (bumpInfoModalTitle.isVisible()) {
            Android.doIfAndroid { closeModal() }
            IOS.doIfiOS { bumpsInfoModalOkElement.tap() }
        }
        return bumpsPreCheckoutRobot
    }

    @Step("Assert info icon is visible and click on it")
    fun clickOnInfoIcon(): BumpsPreCheckoutRobot {
        VintedAssert.assertTrue(infoIconElement.isVisible(), "Info icon should be visible")
        infoIconElement.withWait(WaitFor.Visible).click()
        return bumpsPreCheckoutRobot
    }

    @Step("Click 'know more' to open bumps info web view")
    fun clickKnowMore(): WebViewRobot {
        knowMoreElement.withWait(WaitFor.Visible).tap()
        return RobotFactory.webViewRobot
    }

    @Step("Leave bumps info screen")
    fun leaveBumpsInfoScreen() {
        IOS.doIfiOS { closeButtonIos.click() }
        Android.doIfAndroid { clickBack() }
    }

    @Step("Click on pricing rules info element")
    fun openPricingRulesScreen() {
        vasPricingRulesElement.withWait(WaitFor.Visible).click()
    }

    @Step("Click close")
    fun clickClose(): BumpsPreCheckoutRobot {
        leaveBumpsInfoScreen()
        return bumpsPreCheckoutRobot
    }

    @Step("Check if right amount of bump duration elements are displayed")
    fun assertRightAmountOfBumpDurationElementsAreDisplayed(): BumpsPreCheckoutRobot {
        val bumpDuration = bumpDurationElementList.size
        val expectedBumpDuration = loggedInUser.userApi.getPushUpOptions().count()
        VintedAssert.assertTrue(
            bumpDuration == expectedBumpDuration,
            "$expectedBumpDuration bump duration options should be displayed, but was: $bumpDuration"
        )
        return bumpsPreCheckoutRobot
    }

    @Step("Assert confirm button is clickable")
    fun checkIfConfirmButtonIsClickable() {
        confirmButton.withWait(WaitFor.Visible).click()
        VintedAssert.assertTrue(infoIconElement.isInvisible(), "Info icon should become invisible after click")
    }

    @Step("Click add more items button")
    fun clickAddMoreItems(): BumpsItemsSelectionRobot {
        addMoreItemsButtonAndroid.click()
        return RobotFactory.bumpsItemsSelectionRobot
    }

    @Step("Check if right number of items is displayed in pre-checkout screen")
    fun assertRightNumberOfItemsIsDisplayed(expectedNumberOfItems: Int): BumpsPreCheckoutRobot {
        val itemImageElementList = if (isDynamicPricingBump()) {
            itemsToBumpLargeImageElementListAndroid
        } else itemsToBumpImageElementListAndroid
        VintedAssert.assertTrue(itemImageElementList.size == expectedNumberOfItems, "$expectedNumberOfItems items should be visible")
        return bumpsPreCheckoutRobot
    }

    @Step("Confirm order")
    fun clickConfirmOrderButton(): PaymentMethodsRobot {
        confirmButton.click()
        return RobotFactory.paymentMethodsRobot
    }

    @Step("Is dynamic pricing bump")
    fun isDynamicPricingBump(): Boolean {
        // only countries with dynamic pricing bumps
        return PortalFactory.isCurrentRegardlessEnv(listOf(VintedPortal.PL, VintedPortal.LT, VintedPortal.CZ, VintedPortal.US))
    }
}

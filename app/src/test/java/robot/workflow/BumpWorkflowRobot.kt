package robot.workflow

import RobotFactory.bumpStatisticsRobot
import RobotFactory.bumpsCheckoutRobot
import RobotFactory.bumpsPreCheckoutRobot
import RobotFactory.deepLink
import RobotFactory.itemRobot
import RobotFactory.navigationRobot
import RobotFactory.paymentMethodsRobot
import RobotFactory.userProfileClosetRobot
import RobotFactory.userProfileRobot
import RobotFactory.userShortInfoSectionRobot
import RobotFactory.webViewRobot
import api.AssertApi
import api.controllers.item.getItemInformation
import api.controllers.user.userApi
import api.data.models.VintedItem
import io.qameta.allure.Step
import robot.BaseRobot
import robot.bumps.BumpStatisticsRobot
import robot.bumps.BumpsCheckoutRobot
import robot.bumps.BumpsPreCheckoutRobot
import robot.item.ItemActions
import robot.item.ItemRobot
import robot.payments.PaymentMethods
import robot.payments.PaymentMethodsRobot
import util.Android
import util.base.BaseTest
import util.EnvironmentManager.isAndroid
import util.IOS
import util.PriceFactory

class BumpWorkflowRobot : BaseRobot() {

    @Step("Select items for bump through bump banner and assert all elements are visible")
    fun selectItemsToBumpThroughBumpBannerAndAssertAllElementsAreVisible(itemsCount: Int = 1): BumpsCheckoutRobot {
        deepLink.profile
            .goToMyProfile()
            .clickOnBumpBanner()
            .assertItemsGridIsDisplayed()
            .addItemToTheList(itemsCount)
            .assertCellWithSelectedItemsIsDisplayed()
            .assertFirstSelectedItemIsVisible()
            .clickSubmit()
            .closeBumpsInfoModalIfVisible()
            .clickConfirmOrderButton()
        return bumpsCheckoutRobot
    }

    @Step("Open first item and assert bump label and statistics elements visibility")
    fun openFirstItemAssertBumpLabelAndStatisticsElementsVisibility(): BumpStatisticsRobot {
        userShortInfoSectionRobot
            .assertShortUserSectionIsVisible()
        userProfileRobot
            .openFirstItem()
            .assertBumpLabelVisibility()
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
        return bumpStatisticsRobot
    }

    @Step("Check if right screen is displayed after bumping an item, go to item and assert it is bumped")
    fun assertRightScreenDisplayedAfterBumpingAndCheckItemIsBumped(item: VintedItem) {
        assertRightScreenDisplayedAfterBumpingAndGoToBumpedItem(item)
            .assertBumpLabelVisibility()
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
            .goBackToUserClosetScreen()
            .assertLabelIsVisible(ItemActions.PROMOTED)
            .clickOnFirstBumpButton()
        bumpStatisticsRobot
            .assertBumpStatisticsHeaderIsVisible()
    }

    @Step("Open item and assert bump label and statistics elements are visible")
    fun openItemAndAssertBumpLabelAndStatisticsElementsAreVisible(item: VintedItem): BumpStatisticsRobot {
        userShortInfoSectionRobot.assertShortUserSectionIsVisible()
        if (isAndroid) {
            deepLink.item.goToItem(item)
        } else {
            userProfileRobot
                .openItemByPrice(item)
        }
            .assertBumpLabelVisibility()
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
        return bumpStatisticsRobot
    }

    @Step("Go to bump checkout while bumping first item in the closet")
    fun goToBumpCheckoutWhileBumpingFirstItemInTheCloset(item: VintedItem): PaymentMethodsRobot {
        deepLink.profile
            .goToMyProfile()
            .openFirstItemOrByUsingTitle(item)
            .clickOnBumpButton()
            .clickConfirmOrderButton()
        return paymentMethodsRobot
    }

    @Step("Check if all elements are visible in bumps checkout screen")
    fun assertElementsInBumpsCheckoutScreen(): BumpsCheckoutRobot {
        bumpsCheckoutRobot
            .assertOrderDetailsButtonIsVisibleAndClick()
            .assertOrderSummaryCellIsDisplayedAndCloseIt()
        bumpsCheckoutRobot
            .assertPaymentMethodInfoCellIsVisible()
            .assertTermsAndConditionsNoteIsVisible()
        return bumpsCheckoutRobot
    }

    @Step("Bump item using {paymentMethod} payment method")
    fun bumpItemUsingThirdPartyPaymentMethod(paymentMethod: PaymentMethods, item: VintedItem) {
        goToBumpCheckoutWhileBumpingFirstItemInTheCloset(item)
            .selectThirdPartyPaymentMethod(paymentMethod)
        assertElementsInBumpsCheckoutScreen()
            .confirmOrder()
        paymentMethodsRobot.payUsingSelectedThirdPartyPaymentMethod(paymentMethod)
        assertRightScreenDisplayedAfterBumpingAndCheckItemIsBumped(item)
    }

    @Step("Get formatted bump order price")
    fun getBumpOrderPrice(): String {
        val bumpOrderPrice = BaseTest.loggedInUser.userApi.getPushUpOptions().first { it.default }.price
        return PriceFactory.getFormattedPriceWithCurrencySymbol(bumpOrderPrice)
    }

    @Step("Make sure item is bumpable")
    fun makeSureItemIsBumpable(item: VintedItem): BumpWorkflowRobot {
        if (item.pushUp == null) return this

        AssertApi.assertApiResponseWithWait(
            actual = { BaseTest.loggedInUser.getItemInformation(item).canPushUp.also { commonUtil.reporting.Report.addMessage("Item was bump-able: $it") } },
            expected = true,
            errorMessage = "Item should be bump-able but it was not",
            sleepTime = 500,
            retryCount = 50
        )
        return this
    }

    @Step("Open profile screen and click on first bump button")
    fun openProfileAndClickFirstBumpButton() {
        navigationRobot
            .openProfileTab()
            .clickOnUserProfile()
        userProfileClosetRobot
            .clickOnFirstBumpButton()
    }

    @Step("Open bumps info screen, check if web view is displayed and go back")
    fun openAndLeaveBumpsInfoWebView(): BumpsPreCheckoutRobot {
        bumpsPreCheckoutRobot
            .clickOnInfoIcon()
            .clickKnowMore()
            .assertWebViewIsVisible()
        bumpsPreCheckoutRobot
            .leaveBumpsInfoScreen()
        return bumpsPreCheckoutRobot
    }

    @Step("Only dynamic price bump countries: Open pricing rules screen, check if web view is displayed and close it")
    fun openAndClosePricingInfoScreen(): BumpsPreCheckoutRobot {
        if (bumpsPreCheckoutRobot.isDynamicPricingBump()) {
            bumpsPreCheckoutRobot.openPricingRulesScreen()
            webViewRobot.assertWebViewIsVisible()
            bumpsPreCheckoutRobot.clickClose()
        }
        return bumpsPreCheckoutRobot
    }

    @Step("Check if right screen is displayed after bumping an item")
    fun assertRightScreenDisplayedAfterBumpingAndGoToBumpedItem(item: VintedItem): ItemRobot {
        Android.doIfAndroid {
            userShortInfoSectionRobot.assertShortUserSectionIsVisible()
            deepLink.item.goToItem(item)
        }
        IOS.doIfiOS { itemRobot.assertDescription(item.description) }
        return itemRobot
    }
}

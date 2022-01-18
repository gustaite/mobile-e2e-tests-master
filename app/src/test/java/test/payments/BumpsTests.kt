package test.payments

import RobotFactory.bumpWorkflowRobot
import RobotFactory.bumpsCheckoutRobot
import RobotFactory.bumpsPreCheckoutRobot
import RobotFactory.checkoutRobot
import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.paymentMethodsRobot
import RobotFactory.userProfileRobot
import RobotFactory.userShortInfoSectionRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.item.getAllItems
import api.controllers.item.getItems
import api.controllers.user.notificationSettingsApi
import api.controllers.user.userApi
import api.data.models.VintedItem
import api.factories.UserFactory
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.LoginToNewUser
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.base.BaseTest
import util.EnvironmentManager.isAndroid
import util.data.CreditCardDetails
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import org.testng.SkipException
import util.values.ElementByLanguage
import util.values.Visibility

@Feature("Bump Tests")
class BumpsTests : BaseTest() {
    private var item: VintedItem by thread.lateinit()

    @RunMobile
    @LoginToNewUser
    @Test(description = "Test elements in bumps pre-checkout screen")
    @TmsLink("25386")
    fun testElementsInBumpsPreCheckoutScreen() {
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        bumpWorkflowRobot
            .makeSureItemIsBumpable(item)
            .openProfileAndClickFirstBumpButton()
        bumpsPreCheckoutRobot
            .closeBumpsInfoModalIfVisible()
        bumpWorkflowRobot
            .openAndLeaveBumpsInfoWebView()
        bumpWorkflowRobot
            .openAndClosePricingInfoScreen()
            .assertRightAmountOfBumpDurationElementsAreDisplayed()
            .checkIfConfirmButtonIsClickable()
    }

    @RunMobile(platform = VintedPlatform.ANDROID, message = "Test for android only")
    @LoginToNewUser
    @Test(description = "Test adding and removing an item from the list in bumps checkout")
    @TmsLink("25354")
    fun testAddingAndRemovingItemInBumpsCheckout() {
        // need this because two items are required in the test
        repeat(2) { item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM) }

        bumpWorkflowRobot
            .makeSureItemIsBumpable(item)
            .openProfileAndClickFirstBumpButton()
        bumpsPreCheckoutRobot
            .closeBumpsInfoModalIfVisible()
            .assertRightNumberOfItemsIsDisplayed(expectedNumberOfItems = 1)
            .clickAddMoreItems()
            .removeItemFromTheList(0)
            .addItemToTheList(2)
            .clickSubmit()
            .assertRightNumberOfItemsIsDisplayed(expectedNumberOfItems = 2)
            .clickAddMoreItems()
            .removeItemFromTheList()
            .clickSubmit()
            .assertRightNumberOfItemsIsDisplayed(1)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX)
    @LoginToDefaultUser
    @Test(description = "Test buying one bump through bump banner")
    @TmsLinks(TmsLink("5494"), TmsLink("17794"), TmsLink("17890"), TmsLink("17884"), TmsLink("17880"))
    fun testBuyingOneBumpThroughBumpBanner() {
        ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        bumpWorkflowRobot.selectItemsToBumpThroughBumpBannerAndAssertAllElementsAreVisible()
        paymentMethodsRobot.clickOnSavedCreditCardCell()
        bumpWorkflowRobot
            .assertElementsInBumpsCheckoutScreen()
            .confirmOrder()

        sleepWithinStep(3000) // Wait for pushUp to happen in backend
        val item = loggedInUser.getItems().filter { !it.canPushUp && it.promoted && it.pushUp != null }.maxByOrNull { it.pushUp!!.nextPushUpTime }
        VintedAssert.assertNotNull(item, "Pushed item was not found on user: ${loggedInUser.username}")
        bumpWorkflowRobot.openItemAndAssertBumpLabelAndStatisticsElementsAreVisible(item!!)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX)
    @LoginToNewUser
    @Test(description = "Test bumping one item through item screen with credit card added from checkout and saved")
    @TmsLink("7012")
    fun testBumpingOneItemWithCreditCardAddedFromCheckoutAndSaved() {
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        bumpWorkflowRobot
            .goToBumpCheckoutWhileBumpingFirstItemInTheCloset(item)
            .selectNewCreditCardPaymentOptionForVAS()
            .selectSaveCreditCardOption()
            .insertNewCreditCardInfo(creditCardDetails = CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
        bumpWorkflowRobot
            .assertElementsInBumpsCheckoutScreen()
            .confirmOrder()
        bumpWorkflowRobot
            .assertRightScreenDisplayedAfterBumpingAndGoToBumpedItem(item)
            .assertBumpLabelVisibility()
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX)
    @LoginToNewUser
    @Test(description = "Test bumping one item through item screen with credit card added from checkout and not saved")
    @TmsLink("7013")
    fun testBumpingOneItemWithCreditCardAddedFromCheckoutAndNotSaved() {
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        bumpWorkflowRobot
            .goToBumpCheckoutWhileBumpingFirstItemInTheCloset(item)
            .selectNewCreditCardPaymentOptionForVAS()
            .unselectSaveCreditCardOption()
            .insertNewCreditCardInfo(creditCardDetails = CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
        bumpWorkflowRobot
            .assertElementsInBumpsCheckoutScreen()
            .confirmOrder()
        bumpWorkflowRobot
            .assertRightScreenDisplayedAfterBumpingAndGoToBumpedItem(item)
            .assertBumpLabelVisibility()
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.ALL_EXCEPT_US, message = "SB_US bump statistics is not being calculated after Vitess migration")
    @LoginToDefaultUser
    @Test(description = "Test elements in bumps statistics screen")
    @TmsLink("9927")
    fun testBumpStatisticsScreen() {
        // TODO: adding skip exception for now, will investigate further, how to solve it better
        val item = loggedInUser.getAllItems().items
            .filter { it.statsVisible && it.promoted && it.pushUp != null && (it.performance != null && it.performance!!.recentlyPromoted && it.performance!!.impressions > 0) && it.canBuy!! }
            .maxByOrNull { it.pushUp!!.nextPushUpTime }
            ?: throw SkipException("Item with stats and performance data was not found on user: ${loggedInUser.username}")

        if (isAndroid) {
            deepLink.item.goToItem(item)
        } else {
            deepLink.profile.goToMyProfile()
                .openItemByPrice(item) // Statistics button is not loaded on IOS when going to item via deepLink
        }
            .clickOnBumpStatisticsButton()
            .assertBumpStatisticsHeaderIsVisible()
            .assertAllBumpStatisticsElementsAreVisible()
    }

    @Issue("NOM-285")
    @RunMobile(env = VintedEnvironment.SANDBOX)
    @Test(description = "Buy two items at once through bump banner")
    @TmsLinks(TmsLink("17798"), TmsLink("17894"), TmsLink("17888"), TmsLink("17882"))
    fun testBuyingTwoBumpsAtOnce() {
        val user = UserFactory.createRandomUser()
        repeat(2) {
            ItemAPI.uploadItem(
                itemOwner = user,
                type = ItemRequestBuilder.VintedType.SIMPLE_ITEM
            )
        }
        deepLink.loginToAccount(user)

        bumpWorkflowRobot.selectItemsToBumpThroughBumpBannerAndAssertAllElementsAreVisible(2)
        paymentMethodsRobot
            .selectNewCreditCardPaymentOptionForVAS()
            .insertNewCreditCardInfo(creditCardDetails = CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
        bumpsCheckoutRobot
            .assertOrderDetailsButtonIsVisibleAndClick()
            .assertRightAmountOfItemsAreDisplayedInOrderSummary(2)
            .assertOrderSummaryCellIsDisplayedAndCloseIt()
            .confirmOrder()
        userShortInfoSectionRobot
            .assertShortUserSectionIsVisible()
        userProfileRobot
            .assertBumpLabelsAreVisible(2)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_US_PL_CZ_LT)
    @Issue("SP3310-129")
    @Test(description = "Test bumping one item through bump banner with Vinted Wallet money")
    @TmsLinks(TmsLink("17795"), TmsLink("17891"), TmsLink("17885"), TmsLink("17881"))
    fun testBumpingOneItemWithVintedWallet() {
        val user = UserFactory.createRandomUser()
        ItemAPI.uploadItem(itemOwner = user, type = ItemRequestBuilder.VintedType.SIMPLE_ITEM, price = "10")
        user.userApi.addFundsToWalletAndWaitUntilItWillBeAvailable(10.00)

        deepLink.loginToAccount(user)
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
        val bumpOrderInvoiceSubtitle = ElementByLanguage.bumpOrderInvoiceSubtitleText

        bumpWorkflowRobot
            .selectItemsToBumpThroughBumpBannerAndAssertAllElementsAreVisible()
            .assertPayWithVintedWalletOptionIsVisible()
        bumpWorkflowRobot
            .assertElementsInBumpsCheckoutScreen()
            .confirmOrder()
        bumpWorkflowRobot
            .openFirstItemAssertBumpLabelAndStatisticsElementsVisibility()
            .goBackToUserClosetScreen()
        deepLink.goToFeed()
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .assertInvoiceIsVisible(itemName = bumpOrderInvoiceSubtitle, visibility = Visibility.Invisible)
            .assertVASOrderInvoiceAmountAndOrderTitleMatching(amount = bumpWorkflowRobot.getBumpOrderPrice(), subtitle = bumpOrderInvoiceSubtitle)
            .assertWithdrawMoneyAndPendingBalanceElementsAreVisible()
    }
}

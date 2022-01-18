package test.payments

import RobotFactory.checkoutRobot
import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.paymentsScreenRobot
import RobotFactory.workflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.LoginToWithItemsUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.*
import util.base.BaseTest
import util.data.CreditCardDetails

@Feature("Credit card test")
class CreditCardTests : BaseTest() {

    @RunMobile(country = VintedCountry.LT, message = "Test for LT only")
    @LoginToNewUser
    @Test(description = "Add new credit card in settings and then delete it")
    @TmsLinks(TmsLink("371"), TmsLink("372"))
    fun testAddNewCreditCard() {
        val creditCard = EnvironmentManager.creditCardCredentials()!!.credit_card

        workflowRobot
            .addCreditCardAndCheckIfAdded(creditCard)
            .clickDeleteCreditCard()
            .checkIfCreditCardDeleted()
    }

    @RunMobile(platform = VintedPlatform.ANDROID, message = "Test for Android only")
    @LoginToNewUser
    @Test(description = "Check if validations are displayed in new credit card screen")
    fun testValidationsInCreditCardScreen() {
        deepLink
            .goToSettings()
            .openPaymentsSettings()
            .openNewCreditCardScreen()
            .assertValidationsAreVisible()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS, message = "Test for payment countries only")
    @LoginToNewUser
    @CreateOneTestUser
    @Issue("MARIOS-540")
    @Test(description = "Test if credit card added and saved in checkout is displayed in settings")
    @TmsLink("94")
    fun testAddingAndSavingCreditCardInCheckout() {
        val oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )

        deepLink
            .item.goToItem(oneTestUserItem)
            .clickBuyButton()
            .openPaymentMethodsSection()
            .selectCreditCardPaymentMethod()
            .selectSaveCreditCardOption()
            .insertNewCreditCardInfo(CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
            .leaveCheckout()
        deepLink.goToFeed()
        deepLink
            .goToSettings()
            .openPaymentsSettings()
            .checkIfCreditCardDeleteButtonIsVisible()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS, message = "Test for payment countries only")
    @LoginToNewUser
    @CreateOneTestUser
    @Test(description = "Test if credit card added and not saved in checkout is not displayed in settings")
    @TmsLink("95")
    fun testAddingAndNotSavingCreditCardInCheckout() {
        val oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )

        deepLink.item
            .goToItem(oneTestUserItem)
            .clickBuyButton()
            .openPaymentMethodsSection()
            .selectCreditCardPaymentMethod()
            .unselectSaveCreditCardOption()
            .insertNewCreditCardInfo(CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
            .leaveCheckout()
        deepLink.goToFeed()
        deepLink
            .goToSettings()
            .openPaymentsSettings()
            .checkIfCreditCardDeleted()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS, message = "Test for payment countries only")
    @LoginToNewUser
    @CreateOneTestUser
    @Test(description = "Test if credit card added in settings is displayed in item checkout")
    @TmsLink("92")
    fun testIfCreditCardAddedInSettingsIsVisibleInItemCheckout() {
        val oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )

        workflowRobot
            .addCreditCardAndCheckIfAdded(CreditCardDetails.CreditCard())
        deepLink.item
            .goToItem(oneTestUserItem)
            .clickBuyButton()
            .assertCreditCardExpirationDateIsDisplayed()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX)
    @LoginToNewUser
    @Issue("MARIOS-540")
    @Test(description = "Test if credit card added in settings is displayed in bumps checkout")
    @TmsLink("92")
    fun testIfCreditCardAddedInSettingsIsVisibleInBumpsCheckout() {
        ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        workflowRobot
            .addCreditCardAndCheckIfAdded(CreditCardDetails.CreditCard())
        navigationRobot
            .openProfileTab()
            .clickOnUserProfile()
            .openFirstItem()
            .clickOnBumpButton()
            .clickConfirmOrderButton()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_COUNTRIES_WITH_CLOSET_PROMO)
    @LoginToWithItemsUser
    @Test(description = "Test if credit card added in settings is displayed in closet promo checkout")
    @TmsLink("92")
    fun testIfCreditCardVisibleOnClosetPromo() {
        workflowRobot
            .addCreditCardAndCheckIfAdded(CreditCardDetails.CreditCard())

        deepLink.profile
            .goToMyProfile()
            .clickOnClosetPromoBanner()
            .clickConfirm()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
        paymentsScreenRobot.clickFirstCreditCardAndOpenCheckout()
        checkoutRobot.assertCreditCardExpirationDateIsDisplayed()
    }
}

package test.payments

import RobotFactory.actionBarRobot
import RobotFactory.billingAddressRobot
import RobotFactory.conversationWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.paymentAccountDetailsRobot
import RobotFactory.paymentWorkflowRobot
import RobotFactory.walletRobot
import RobotFactory.withdrawalRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.paymentsApi
import api.controllers.user.transactionApi
import api.controllers.user.userApi
import api.data.models.VintedItem
import api.data.models.isNotNull
import api.data.models.transaction.VintedTransaction
import api.values.UserBillingAddress
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.Android
import util.IOS
import util.base.BaseTest

@LoginToNewUser
@Feature("Wallet tests")
class WalletTests : BaseTest() {

    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()

    @RunMobile(country = VintedCountry.PAYMENTS, message = "Test for payment countries only")
    @Test(description = "Check if user can activate wallet")
    @TmsLink("270")
    fun testActivatingWallet() {
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .clickActivateVintedBalance()
        paymentWorkflowRobot
            .addPaymentAccountNameBirthdayAndSecurityNumberDetails()
            .openFillAndSaveBillingAddress()
            .clickSave()
            .openPaymentAccountDetails()
            .assertBillingName(loggedInUser.realName!!)
            .assertBirthday()
            .assertSocialSecurityNumberIsHidden()
            .assertBillingAddress(loggedInUser.billingAddress)
    }

    @RunMobile(country = VintedCountry.PAYMENTS, message = "Test for payment countries only", platform = VintedPlatform.ANDROID)
    @Test(description = "Check if user can edit billing address on wallet")
    fun testActivatingWalletEditBillingAddress() {
        loggedInUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .openPaymentAccountDetails()
            .clickEditBillingAddress()
            .fillBillingAddress(UserBillingAddress.summerResidence)
            .clickSave()
            .clickSave()
            .openPaymentAccountDetails()
            .assertBillingName(loggedInUser.realName!!)
            .assertBirthday()
            .assertSocialSecurityNumberIsHidden()
            .assertBillingAddress(UserBillingAddress.summerResidence)
    }

    @Issue("MARIOS-989")
    @RunMobile(country = VintedCountry.PAYMENTS, message = "Test for payment countries only")
    @Test(description = "Check if user can activate wallet - error validation")
    @TmsLink("270")
    fun testActivatingWalletErrorValidation() {
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .clickActivateVintedBalance()
            .clickSave()
        paymentAccountDetailsRobot.validationRobot
            .assertErrorValidationMessagesVisibleOnBirthdayBillingAddressAndSocialSecurityNumber()
            .clearFullName()
            .clickSave()
        paymentAccountDetailsRobot.validationRobot
            .assertErrorValidationMessageVisibleOnFullName()

        IOS.doIfiOS {
            commonUtil.reporting.Report.addMessage("Billing address validation elements were not checked because those are not visible for Appium in IOS")
        }

        Android.doIfAndroid {
            paymentAccountDetailsRobot
                .clickAddBillingAddress()
                .clearPostalCodeUSAndroid()
                .clickSave()
            billingAddressRobot.validationRobot
                .assertErrorValidationMessagesVisibleOnAddressLine1AndPostalCode()
                .clearFullName()
                .clickSave()
            billingAddressRobot.validationRobot
                .assertErrorValidationMessageVisibleOnFullName()
        }
    }

    @RunMobile(
        env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS,
        message = "Test for sandbox and payment countries only"
    )
    @Test(description = "Test withdraw money from Vinted Wallet")
    @TmsLinks(
        TmsLink("16501"), TmsLink("17793"), TmsLink("17841"), TmsLink("17815"),
        TmsLink("17791"), TmsLink("17814")
    )
    fun testMoneyPayoutFromVintedWallet() {
        val amount = 10.00
        loggedInUser.userApi.addFundsToWalletAndWaitUntilItWillBeAvailable(amount)
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .clickWithdrawMoneyFromVintedBalance()
            .clickAddNewPayoutBankAccount()
            .addPayoutBankAccountDetails()
        actionBarRobot.submit()
        withdrawalRobot
            .clickWithdrawMoneyToBankAccount()
            .assertWithdrawalProgressElementsAreVisible(amount.toString())
            .clickBack()
        walletRobot.assertWithdrawMoneyFromVintedWalletButtonIsDisabled()
    }

    @RunMobile(
        env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS,
        message = "Test for sandbox and payment countries only"
    )
    @Test(description = "Test first time seller Vinted Wallet Confirmation")
    @TmsLink("")
    fun testFirstTimeSellerWalletConfirmation() {
        deepLink.payment.goToWallet()
        walletRobot.assertActivateBalanceButtonIsVisible()

        loggedInUser.skipPartCleanup = true
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )
        transaction = defaultUser.transactionApi.buyItemWithLabelledShipping(loggedInUser, item)

        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickGenerateLabel()
        paymentWorkflowRobot
            .addPaymentAccountNameBirthdayAndSecurityNumberDetails()
            .openFillAndSaveBillingAddress()
            .clickSave()

        loggedInUser.isNotNull().transactionApi.completeTransactionByTransactionId(txId = transaction.id)

        deepLink.payment.goToWallet()
        walletRobot.assertWithdrawMoneyButtonIsVisible()
    }
}

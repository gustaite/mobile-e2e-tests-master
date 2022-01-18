package test.payments

import RobotFactory.deepLink
import RobotFactory.deleteAccountRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.models.isNotNull
import api.data.models.transaction.VintedTransaction
import commonUtil.testng.LoginToNewUser
import api.data.models.transaction.VintedTransactionStatus
import api.data.models.transaction.getId
import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.base.BaseTest
import util.testng.*
import commonUtil.thread
import io.qameta.allure.TmsLink
import util.EnvironmentManager.preferredShipmentType

@LoginToNewUser
@Feature("Transactions tests")
class SellerSideTests : BaseTest() {
    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()

    @BeforeMethod(description = "Upload an item for the seller")
    fun beforeMethod() {
        item = ItemAPI.uploadItem(itemOwner = loggedInUser, type = ItemRequestBuilder.VintedType.SIMPLE_ITEM, price = "1")
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.WITHOUT_DROP_OFF_POINT_SHIPPING)
    @Test(description = "Test seller side after selling an item with non-drop-off-point shipping")
    fun testSellerSideAfterSellingAnItem() {
        transaction = defaultUser.transactionApi.buyItemWithRandomShipping(seller = loggedInUser, item = item, transactionShipmentType = preferredShipmentType)
        deepLink.conversation.goToConversation(transaction.conversationId)
        VintedAssert.assertEquals(transaction.status, VintedTransactionStatus.STATUS_DEBIT_PROCESSED, "Transaction status does not match)")
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.WITHOUT_DROP_OFF_POINT_SHIPPING)
    @Test(description = "Test seller cannot delete account when he has transactions in progress")
    @TmsLink("105")
    fun testTryToDeleteProfileWhileHavingIncompleteTransaction() {
        transaction = defaultUser.transactionApi.buyItemWithRandomShipping(seller = loggedInUser, item = item, transactionShipmentType = preferredShipmentType)

        deepLink.profile.goToDeleteAccount()
        deleteAccountRobot
            .markTransactionCheckboxAsChecked()
            .assertTransactionCheckboxIsNotChecked()
            .assertValidationErrorVisible()
    }

    @AfterMethod(description = "Complete ongoing transaction")
    fun afterMethod() {
        defaultUser.isNotNull().transactionApi.completeTransactionByTransactionId(txId = transaction.getId())
    }
}

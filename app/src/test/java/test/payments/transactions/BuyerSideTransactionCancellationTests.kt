package test.payments.transactions

import RobotFactory.checkoutWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.inAppNotificationRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.data.models.VintedItem
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.base.BaseTest
import api.controllers.user.notificationSettingsApi
import api.controllers.user.paymentsApi
import api.controllers.user.transactionApi
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.asserts.VintedAssert
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.config.PortalFactory
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@RunMobile(country = VintedCountry.PAYMENTS, env = VintedEnvironment.SANDBOX)
@LoginToDefaultUser
@CreateOneTestUser
@Feature("Transaction cancellation buyer side tests")
class BuyerSideTransactionCancellationTests : BaseTest() {
    private var oneTestUserItem: VintedItem? by thread.lateinit()
    private var deliveryType: VintedShipmentDeliveryType by thread.lateinit()

    @BeforeMethod(description = "Create item for oneTestUser")
    fun createOneTestUserAndItem() {
        oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )

        oneTestUser.paymentsApi.addPaymentsAccountAndValidateItExists()
    }

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Get random delivery type")
    fun getRandomDeliveryType() {
        deliveryType = when {
            (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)) -> VintedShipmentDeliveryType.HOME
            (PortalFactory.isCurrentRegardlessEnv(listOf(VintedPortal.CZ, VintedPortal.LT))) -> VintedShipmentDeliveryType.PICK_UP
            else -> listOf(VintedShipmentDeliveryType.PICK_UP, VintedShipmentDeliveryType.HOME).random()
        }
    }

    @Test(description = "Test buyer side transaction cancellation views after buyer cancelling transaction")
    fun testTransactionCancelledFromBuyerSide() {
        deepLink.item
            .goToItem(oneTestUserItem!!)
            .clickBuyButton()
            .assertAllPricesAreDisplayed()

        checkoutWorkflowRobot
            .selectPickUpOrHomeDeliveryTypeByItemFillDeliveryDetailsAndSelectPickUpPointIfNeeded(oneTestUserItem!!, deliveryType)
            .setShippingContactInformationForBuyerIfRequired(oneTestUserItem!!)
            .addAddressIfPhoneWasDeletedWhenChangingDeliveryTypeInPL()
            .clickBuy()
            .assertRefundProcessedElementVisibility(visibility = Visibility.Invisible)
            .openConversationDetails()
            .clickCancelOrderButton()
            .selectReasonAndCancelOrder()
            .assertConversationScreenIsVisible()
            .assertRefundProcessedElementVisibility(visibility = Visibility.Visible)
    }

    @Test(description = "Test buyer side transaction cancellation views after seller cancelling transaction")
    fun testTransactionCancelledFromSellerSide() {
        inAppNotificationRobot.closeInAppNotificationIfExists()
        deepLink.item
            .goToItem(oneTestUserItem!!)
            .clickBuyButton()
            .assertAllPricesAreDisplayed()

        checkoutWorkflowRobot
            .selectPickUpOrHomeDeliveryTypeByItemFillDeliveryDetailsAndSelectPickUpPointIfNeeded(oneTestUserItem!!, deliveryType)
            .setShippingContactInformationForBuyerIfRequired(oneTestUserItem!!)
            .addAddressIfPhoneWasDeletedWhenChangingDeliveryTypeInPL()
            .clickBuy()
            .assertRefundProcessedElementVisibility(visibility = Visibility.Invisible)

        deepLink.goToFeed()
        val transaction = oneTestUser.transactionApi.getTransactionByItemId(oneTestUserItem!!)
        VintedAssert.assertNotNull(transaction, "Transaction with oneTestUserItem was not found. Item id: ${oneTestUserItem?.id}")
        oneTestUser.transactionApi.cancelTransaction(transaction!!)
        deepLink.conversation
            .goToConversation(transaction.conversationId)
            .assertRefundProcessedElementVisibility(visibility = Visibility.Visible)
    }
}

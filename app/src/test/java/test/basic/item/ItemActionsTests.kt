package test.basic.item

import RobotFactory.deepLink
import RobotFactory.deletionReasonRobot
import RobotFactory.feedbackFormRobot
import RobotFactory.itemDeleteConfirmationRobot
import RobotFactory.itemRobot
import RobotFactory.rateAppRobot
import RobotFactory.reportReasonRobot
import RobotFactory.reserveRobot
import RobotFactory.sellRobot
import RobotFactory.swapRobot
import RobotFactory.userProfileRobot
import RobotFactory.workflowRobot
import api.controllers.ConversationAPI
import api.controllers.item.*
import api.controllers.user.feedbackApi
import api.data.models.VintedItem
import api.data.models.isNotNull
import commonUtil.data.Price
import commonUtil.testng.*
import commonUtil.testng.mobile.RunMobile
import commonUtil.thread
import io.qameta.allure.*
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.item.ItemActions
import util.*
import util.base.BaseTest
import util.testng.*
import util.values.Visibility

@RunMobile
@Feature("Item actions tests")
@LoginToMainThreadUser
class ItemActionsTests : BaseTest() {
    private var item: VintedItem? by thread.lateinit()

    @BeforeMethod
    fun createNewItem() {
        loggedInUser.deleteAllItems()
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
    }

    @Test(description = "Hide item and check if label appears")
    @TmsLink("24444")
    fun testHideItem() {
        deepLink.item.goToItem(item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.HIDE)
            .assertHiddenLabelVisibility(Visibility.Visible)

        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .assertLabelIsVisible(ItemActions.HIDE)
    }

    @Test(description = "Delete item")
    @TmsLink("77")
    fun testDeleteItem() {
        deepLink.item.goToItem(item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.DELETE)
        itemDeleteConfirmationRobot.confirmItemDeletion()
        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .assertClosetIsEmpty()
    }

    @Test(description = "Reserve item")
    @CreateOneTestUser
    @TmsLink("24445")
    fun testReserveItem() {
        deepLink.item.goToItem(item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.RESERVE)
        reserveRobot
            .reserve(oneTestUser)
            .assertReservedButtonIsVisible()
            .clickReservation()
            .assertSellAndUnreserveButtonsAreVisible()
            .assertUsernameIsVisible(oneTestUser)
            .assertCorrectItemWasReserved(item!!)

        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen.assertLabelIsVisible(ItemActions.RESERVE)
    }

    @Test(description = "Delete item which has active conversation and get deletion reasons")
    @TmsLink("77")
    fun testDeleteItemWithDeleteReasons() {
        deepLink.item.goToItem(item!!)

        ConversationAPI.createConversation(sender = otherUser, recipient = loggedInUser, message = "Test message", item = item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.DELETE)
        deletionReasonRobot
            .selectRandomItemDeletionReason(item!!)
            .confirmItemDeletion()

        deepLink.profile.goToMyProfile()

        userProfileRobot
            .closetScreen
            .assertClosetIsEmpty()
    }

    @Test(description = "Check if there is a possibility to share item")
    fun testShareDialogOpensFromItem() {
        deepLink.item.goToItem(item!!)

        itemRobot
            .clickShareButton()
            .assertSharingOptionsAreVisible()
    }

    @ResetAppBeforeTest // Add reset annotation in case rating feedback keeps persisting after test was retried.
    @Test(description = "Sell item")
    fun testSellItem() {
        val feedback = "feedback after sold item"
        val item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = Price.getMinimumItemPrice()
        )
        deepLink.item.goToItem(item)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.SOLD)
        sellRobot
            .sell(otherUser)
            .selectRatingLeaveFeedbackAndSubmit(feedback)

        IOS.doIfiOS { rateAppRobot.clickRateAppLater() }
        itemRobot.assertSoldLabelIsVisible()

        deepLink.profile.goToUserProfile(otherUser.id)

        userProfileRobot
            .closetScreen.shortUserInfo
            .assertOneFeedbackVisible()
            .clickFeedbackSection()
        userProfileRobot.feedbackScreen
            .assertFeedbackText(feedback)
    }

    @Test(description = "Swap item")
    @CreateOneTestUser
    fun testSwapItem() {
        val feedback = "feedback after swap item"
        deepLink.item.goToItem(item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.SWAPPED)
        swapRobot
            .swap(oneTestUser)
            .selectRatingLeaveFeedbackAndSubmit(feedback)

        IOS.doIfiOS { rateAppRobot.clickRateAppLater() }
        itemRobot.assertSwapLabelIsVisible()

        deepLink.profile.goToUserProfile(oneTestUser.id)

        userProfileRobot
            .closetScreen.shortUserInfo
            .assertOneFeedbackVisible()
            .clickFeedbackSection()
        userProfileRobot.feedbackScreen
            .assertFeedbackText(feedback)
    }

    @Test(description = "Check mark as sold from receiver's side and leave feedback")
    @CreateOneTestUser
    @TmsLink("23797")
    fun testMarkAsSoldFromReceiverSide() {
        val oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = Price.getMinimumItemPrice()
        )
        val txConversationId = ItemAPI.markItemAsSold(
            item = oneTestUserItem,
            receiver = loggedInUser,
            seller = oneTestUser,
            price = oneTestUserItem.priceNumeric
        ).conversationId

        deepLink.conversation
            .goToConversation(txConversationId)
            .assertItemTitle(oneTestUserItem.title)
            .assertItemPrice(oneTestUserItem.priceNumeric)
            .assertSellerInfoMessageIsVisible()
            .openFeedbackForm()
            .selectRatingLeaveFeedbackAndSubmit("Just simple feedback")
        IOS.doIfiOS { feedbackFormRobot.closeFirstSellRateIFExists() }

        deepLink.profile.goToUserProfile(oneTestUser.id)

        userProfileRobot
            .closetScreen.shortUserInfo
            .assertOneFeedbackVisible()
            .clickFeedbackSection()
        userProfileRobot.feedbackScreen
            .assertFeedbackText("Just simple feedback")
    }

    @Test(description = "Check reserved item from receiver's side.")
    fun testReservationFromReceiverSide() {
        val otherUserItem = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        val reservation = ItemAPI.reserve(item = otherUserItem, seller = otherUser, receiver = loggedInUser)

        deepLink.conversation
            .goToConversation(reservation.userMsgThreadId!!)
            .assertItemTitle(otherUserItem.title)
            .assertItemPrice(otherUserItem.priceNumeric)
            .assertSellerInfoMessageIsVisible()
    }

    @Test(description = "Report an item and check that user is returned to item after it")
    @TmsLink("90")
    fun testReportItem() {
        val item = ItemAPI.uploadItem(itemOwner = otherUser, type = ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        deepLink.item.goToItem(item)
        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.REPORT)
        reportReasonRobot
            .selectRandomReportReason()
            .typeReportCommentIfCommentSectionIsVisible(reportComment = "Report generated by Vinted test automation. Delete the report.")
            .submitReport()
            .closeSuccessfulReportScreen()
            .assertItemTitle(item.title) // This confirms that after report user is returned to the item
    }

    @Test(description = "Check if mark as sold and swapped buttons are visible when needed")
    @TmsLinks(TmsLink("5232"), TmsLink("5233"))
    fun testMarkAsSoldAndSwappedButtonsVisibility() {
        val noSwapItem = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITHOUT_SWAP_OPTION, title = "Item without swap option")

        workflowRobot
            .checkItemsActionsVisibility(item = noSwapItem, soldButtonVisibility = Visibility.Visible, swappedButtonVisibility = Visibility.Invisible)
        workflowRobot
            .checkItemsActionsVisibility(item = item!!, soldButtonVisibility = Visibility.Visible, swappedButtonVisibility = Visibility.Visible)
    }

    @AfterMethod(description = "Delete feedBacks after test")
    fun deleteFeedBacksAfterTest() {
        val feedBacks = mainUser.isNotNull().feedbackApi.getFeedbackList(otherUser.id).filter { it.canDelete }
        feedBacks.forEach {
            mainUser.isNotNull().feedbackApi.deleteFeedback(it.id)
        }
    }
}

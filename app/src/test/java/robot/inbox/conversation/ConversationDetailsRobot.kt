package robot.inbox.conversation

import RobotFactory.bundleRobot
import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.item.BundleRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class ConversationDetailsRobot : BaseRobot() {
    private val blockButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("user_profile_menu_block"), scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = IOS.getElementValue("profile_block_member"), onlyVisibleInScreen = true)
        )

    private val unblockButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("user_profile_menu_unblock"), scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = IOS.getElementValue("profile_unblock_member"), onlyVisibleInScreen = true)
        )

    private val deleteConversationButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("order_details_action_delete_conversation"), scroll = false),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("conversation_details_action_delete_conversation"))
        )

    private val deleteConfirmationButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("general_delete_prompt_ok"), scroll = false),
            iOSBy = VintedBy.accessibilityId("delete_conversation_modal_confirm")
        )

    private val addMoreItemsButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("order_details_add_more_items_to_bundle")),
            iOSBy = VintedBy.iOSTextByBuilder(text = IOS.getElementValue("conversation_details_action_add_more_items"), onlyVisibleInScreen = true)
        )

    private val updateOrderButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("order_details_edit_bundle")),
            iOSBy = VintedBy.iOSTextByBuilder(text = IOS.getElementValue("conversation_details_action_update_order"), onlyVisibleInScreen = true)
        )

    private val cancelOrderButton
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("order_details_action_cancel"), scroll = false),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("conversation_details_action_cancel_transaction"))
        )

    @Step("Block user")
    fun blockUser(): ConversationRobot {
        doActionsWith { blockButton }
        return conversationRobot
    }

    @Step("Unblock member")
    fun unblockUser(): ConversationRobot {
        doActionsWith { unblockButton }
        return conversationRobot
    }

    @Step("Delete conversation")
    fun deleteConversation() {
        deleteConversationButton.click()
        deleteConfirmationButton.click()
    }

    private fun doActionsWith(element: () -> VintedElement) {
        element.invoke().tap()
        VintedAssert.assertTrue(element().isInvisible(2), "Element should become invisible after tap")
        clickBack()
    }

    @Step("Click add more items button")
    fun clickAddMoreItemsButton(): BundleRobot {
        addMoreItemsButton.click()
        return bundleRobot
    }

    @Step("Click update order button")
    fun clickUpdateOrderButton(): BundleRobot {
        updateOrderButton.click()
        return bundleRobot
    }

    @Step("Click cancel order button")
    fun clickCancelOrderButton(): OrderCancellationRobot {
        cancelOrderButton.withScrollDownSimple().click()
        return OrderCancellationRobot()
    }
}

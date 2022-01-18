package robot.inbox

import RobotFactory.conversationRobot
import RobotFactory.navigationRobot
import RobotFactory.newMessageRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.conversation.ConversationRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class InboxRobot : BaseRobot() {

    private val inboxBadgeElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("tab_badge_value"))

    private val newMessageButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("floating_action_button"),
            iOSBy = VintedBy.accessibilityId("compose")
        )

    private fun userUsernameElement(username: String): VintedElement = VintedDriver.findElement(
        androidBy = VintedBy.androidTextByBuilder(text = username, scroll = false),
        iOSBy = VintedBy.accessibilityId(username)
    )

    private fun messageWithTextPreviewElement(message: String): VintedElement = VintedDriver.findElement(
        androidBy = VintedBy.androidIdAndText("conversation_list_cell_body", message),
        iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name CONTAINS \"conversation_summary_\"`]/XCUIElementTypeStaticText[`name == '$message'`]")
    )

    private val emptyStateMessageTextElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("empty_state_no_conversations")),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("empty_state_text_messages"))
        )

    private fun inboxTitleElementAndroid(text: String) =
        VintedDriver.findElement(androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"android:id/text1\").textContains(\"$text\")"))

    @Step("Assert unread message bubble is {visibility}")
    fun assertInboxUnreadMessageBubbleVisibility(visibility: Visibility) {
        VintedAssert.assertVisibilityEquals(inboxBadgeElementAndroid, visibility, "Inbox badge should be $visibility", waitForVisible = 25)
    }

    @Step("Click send new message button")
    fun clickSendNewMessageButton(): NewMessageRobot {
        newMessageButton.click()
        return newMessageRobot
    }

    @Step("Assert deleted user's username placeholder '{deletedUserPlaceholder}' is visible")
    fun assertDeletedUserUsernamePlaceholderIsVisibleInInbox(deletedUserPlaceholder: String): InboxRobot {
        commonUtil.Util.retryAction(
            block = {
                userUsernameElement(deletedUserPlaceholder).isVisible(2)
            },
            actions = {
                VintedDriver.pullDownToRefresh()
            },
            retryCount = 2
        )

        VintedAssert.assertTrue(
            userUsernameElement(deletedUserPlaceholder).isVisible(),
            "Deleted user text '$deletedUserPlaceholder' should be visible"
        )
        return this
    }

    @Step("Click on a '{username}' username to open conversation")
    fun clickOnConversationUsingUsername(username: String): ConversationRobot {
        userUsernameElement(username).click()
        return conversationRobot
    }

    @Step("Assert inbox is visible")
    fun assertInboxIsVisible(): InboxRobot {
        VintedAssert.assertTrue(newMessageButton.isVisible(), "Inbox should be visible (checked by new message button visibility)")
        return this
    }

    @Step("Assert inbox title is visible")
    fun assertInboxTitle(title: String): InboxRobot {
        if (isAndroid) {
            VintedAssert.assertTrue((inboxTitleElementAndroid(title)).isVisible(), "Navigation bar name element was not found using text $title")
        } else navigationRobot.assertNavigationBarNameText(title)
        return this
    }

    @Step("Assert message text: '{message}' is visible in preview")
    fun assertMessageTextIsVisibleInPreview(message: String): InboxRobot {
        VintedAssert.assertTrue(messageWithTextPreviewElement(message).withWait().isVisible(), "Message '$message' preview should be visible")
        return this
    }

    @Step("Assert empty state message is visible")
    fun assertEmptyStateMessageTextIsVisible(): InboxRobot {
        IOS.doIfiOS { VintedDriver.pullDownToRefresh() }
        VintedAssert.assertTrue(
            emptyStateMessageTextElement.withWait().isVisible(),
            "Empty state message text should be visible"
        )
        return this
    }
}

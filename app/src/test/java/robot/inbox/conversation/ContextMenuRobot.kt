package robot.inbox.conversation

import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.assertVisibilityEquals
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class ContextMenuRobot : BaseRobot() {

    private val contextMenuElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("context_menu_layout"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'copy' OR name == 'remove'")
        )

    private val contextMenuCopyMessageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                Android.CELL_TITLE_FIELD_ID,
                Android.getElementValue("context_menu_copy")
            ),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'copy'")
        )

    private val contextMenuRemoveMessageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("context_menu_remove"), scroll = false),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'remove'")
        )

    private val modalNoGoBackButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_secondary_button", "context_menu_remove_dialog_button_cancel")

    private val modalRemoveButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_primary_button", "context_menu_remove_dialog_button_remove")

    @Step("Assert context menu element is {visibility}")
    fun assertContextMenuElementVisibility(visibility: Visibility): ContextMenuRobot {
        VintedAssert.assertVisibilityEquals(contextMenuElement, visibility, "Context menu element  should be $visibility")
        return this
    }

    @Step("Assert copy option in context menu is {visibility}")
    fun assertCopyOptionInContextMenuVisibility(visibility: Visibility): ContextMenuRobot {
        VintedAssert.assertVisibilityEquals(contextMenuCopyMessageElement, visibility, "Copy option in context menu should be $visibility")
        return this
    }

    @Step("Assert remove option in context menu is not visible")
    fun assertRemoveOptionInContextMenuIsNotVisible(): ContextMenuRobot {
        VintedAssert.assertTrue(contextMenuRemoveMessageElement.isInvisible(), "Remove option in context menu should not be visible")
        return this
    }

    @Step("Click remove message")
    fun clickRemoveMessage(): ContextMenuRobot {
        contextMenuRemoveMessageElement.click()
        return this
    }

    @Step("Click copy message")
    fun clickCopyMessage(): ConversationRobot {
        contextMenuCopyMessageElement.click()
        return conversationRobot
    }

    @Step("Click NO button in remove message modal")
    fun clickNoInRemoveMessageModal(): ContextMenuRobot {
        modalNoGoBackButtonElement.click()
        return this
    }

    @Step("Click REMOVE button in remove message modal")
    fun clickRemoveInRemoveMessageModal(): ContextMenuRobot {
        modalRemoveButtonElement.click()
        return this
    }
}

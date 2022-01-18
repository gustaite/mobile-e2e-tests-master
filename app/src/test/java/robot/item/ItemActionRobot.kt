package robot.item

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.*
import commonUtil.extensions.escapeApostrophe
import util.values.Visibility

class ItemActionRobot : BaseRobot() {
    private fun hideActionButton(action: ItemActions): VintedElement {
        return VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue(action.androidTranslationKey), scroll = false),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeOther/XCUIElementTypeButton[`name == '${
                IOS.getElementValue(action.iosTranslationKey).escapeApostrophe()
                }'`]"
            )
        )
    }

    private val iosCancelElement: VintedElement get() = IOS.findElementByTranslationKey("cancel")

    @Step("Click on {action} action")
    fun clickOnAction(action: ItemActions): ItemRobot {
        hideActionButton(action).withWait(WaitFor.Visible).withWait(WaitFor.Click).tap()
        return ItemRobot()
    }

    @Step("Assert mark as {action} button is {visibility}")
    fun assertActionButtonVisibility(action: ItemActions, visibility: Visibility): ItemActionRobot {
        VintedAssert.assertVisibilityEquals(hideActionButton(action), visibility, "Mark as ${action.name} button should be $visibility")
        return this
    }

    @Step("Close actions modal")
    fun closeActionsModal(): ItemRobot {
        Android.doIfAndroid { Android.clickBack() }
        IOS.doIfiOS { iosCancelElement.click() }
        return RobotFactory.itemRobot
    }
}

enum class ItemActions(val androidTranslationKey: String, val iosTranslationKey: String) {
    SOLD("menu_item_mark_as_sold", "sell_item"),
    SWAPPED("menu_item_swap", "swap_item"),
    RESERVE("item_btn_mark_reserve", "reserve_item"),
    HIDE("item_btn_mark_hide", "hide_item"),
    EDIT("general_edit", "edit_item"),
    DELETE("general_delete", "delete"),
    REPORT("item_fragment_overflow_menu_report", "alert_the_moderator"),
    PROMOTED("item_promoted", "pushed_up_item_state_title")
}

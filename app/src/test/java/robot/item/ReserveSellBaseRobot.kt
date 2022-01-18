package robot.item

import robot.ActionBarRobot
import robot.BaseRobot
import util.Android
import util.EnvironmentManager.isAndroid
import api.data.models.VintedUser
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

abstract class ReserveSellBaseRobot : BaseRobot() {
    private val actionBarRobot: ActionBarRobot get() = ActionBarRobot()

    private val userInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextField")
        )

    private fun autocompleteUserSelectionElement(username: String): VintedElement {
        return VintedDriver.findElement(
            VintedBy.id("user_selection_user_cell"),
            VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS '$username'")
        )
    }

    fun selectUser(user: VintedUser) {
        if (isAndroid) {
            userInputElement.click()
            Android.sendKeysUsingKeyboard(user.username)
        } else {
            userInputElement.sendKeys(user.username)
        }
        autocompleteUserSelectionElement(user.username).clickWithRetryOnException(2)
        actionBarRobot.submit()
    }
}

package robot.item

import RobotFactory.userProfileRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.EnvironmentManager.isiOS
import util.IOS
import util.VintedDriver
import util.driver.VintedElement

class ItemDeleteModalConfirmationRobot : BaseRobot() {

    private val confirmButton: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey({ modalOkButton }, "delete")

    private val confirmationTextElementIos: VintedElement
        get() = IOS.findElementByTranslationKey("delete_item_confirmation_message")

    @Step("Confirm item deletion")
    fun confirmItemDeletion() {
        if (isiOS) VintedAssert.assertTrue(confirmationTextElementIos.isVisible(), "ConfirmationTextElement should be visible")
        confirmButton.click()
        IOS.doIfiOS { sleepWithinStep(1000) }
        userProfileRobot.waitUntilSuccessNotificationDisappears()
    }
}

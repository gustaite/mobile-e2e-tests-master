package robot.item

import api.controllers.item.getDeletionReasons
import api.data.models.VintedItem
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.base.BaseTest.Companion.loggedInUser
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class DeletionReasonRobot : BaseRobot() {

    private val deleteItemSubmitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_deletion_with_reasons_submit"),
            iOSBy = VintedBy.accessibilityId("deletion_reasons_confirmation_button")
        )

    @Step("Select random deletion reason")
    fun selectRandomItemDeletionReason(item: VintedItem): DeletionReasonRobot {
        val randomReasonText = loggedInUser.getDeletionReasons(item).random().reasonText
        commonUtil.reporting.Report.addMessage("Chosen reason: $randomReasonText")
        findDeletionReasonElement(randomReasonText).click()

        return this
    }

    @Step("Confirm item deletion")
    fun confirmItemDeletion() {
        deleteItemSubmitButton.click()
    }

    private fun findDeletionReasonElement(reasonText: String): VintedElement {
        return VintedDriver.findElement(
            VintedBy.androidUIAutomator(
                "UiSelector().resourceId(\"${Android.ID}list_single_choice_text\").text(\"$reasonText\")"
            ),
            VintedBy.accessibilityId(reasonText)
        )
    }
}

package robot.item

import api.controllers.item.ItemAPI
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.base.BaseTest.Companion.loggedInUser
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ReportReasonRobot : BaseRobot() {

    private var isCommentSectionVisible: Boolean? = false

    private val commentSectionInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextView")
        )

    private val submitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("submit"),
            iOSBy = VintedBy.accessibilityId("report_reason_submit")
        )

    private val reportPostActionSkipButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("report_post_action_skip"),
            iOSBy = VintedBy.accessibilityId("report_post_action_done")
        )

    @Step("Select random report reason")
    fun selectRandomReportReason(): ReportReasonRobot {
        val firstLevelReason = ItemAPI.getItemReportReasons(user = loggedInUser).reportReasons.random()

        isCommentSectionVisible = firstLevelReason.options?.isCommentVisible
        IOS.iOSScrollDownToElementNewWay(firstLevelReason.title)
        VintedDriver.findElementByText(firstLevelReason.title).click()

        if (firstLevelReason.children != null) {
            val secondLevelReason = firstLevelReason.children!!.random()

            isCommentSectionVisible = secondLevelReason.options?.isCommentVisible
            IOS.iOSScrollDownToElementNewWay(secondLevelReason.title)
            VintedDriver.findElementByText(secondLevelReason.title).click()
        }
        return this
    }

    @Step("Type report comment if comment section is visible")
    fun typeReportCommentIfCommentSectionIsVisible(reportComment: String): ReportReasonRobot {
        if (isCommentSectionVisible == true) {
            commentSectionInputElement.sendKeys(reportComment)
            Android.closeKeyboard()
        }
        return this
    }

    @Step("Submit report")
    fun submitReport(): ReportReasonRobot {
        Util.retryOnException({ submitButton.click() }, 2)
        return this
    }

    @Step("Close successful report screen")
    fun closeSuccessfulReportScreen(): ItemRobot {
        reportPostActionSkipButton.tap()
        return ItemRobot()
    }
}

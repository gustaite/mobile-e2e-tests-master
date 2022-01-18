package robot.notificationsettings

import RobotFactory.settingsRobot
import api.controllers.user.notificationSettingsApi
import api.data.models.VintedUser
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.data.enums.VintedNotificationSettingsTypes

class NotificationSettingsRobot : BaseRobot() {

    private val androidSettingsFirstToggleElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("settings_group_item_toggle_switch"))

    private fun notificationFirstElementTitleElementIos(user: VintedUser): VintedElement {
        commonUtil.reporting.Report.addMessage("Global switch title: ${user.notificationSettingsApi.getNotificationsGlobalSwitch()}")
        return VintedDriver.findElement(iOSBy = VintedBy.accessibilityId(user.notificationSettingsApi.getNotificationsGlobalSwitch()))
    }

    private val individualSettingElementsList: List<VintedElement>?
        get() = VintedDriver.findElementList(
            VintedBy.id("settings_group_item_toggle_title"),
            VintedBy.className("XCUIElementTypeCell")
        )

    @Step("Turn off push notifications")
    fun turnOffNotifications(user: VintedUser): NotificationSettingsRobot {
        if (isAndroid) {
            androidSettingsFirstToggleElement.click()
        } else {
            val element = notificationFirstElementTitleElementIos(user)
            val x = element.center.getX() + 160
            val y = element.center.getY()
            commonUtil.reporting.Report.addMessage("x: $x y: $y")
            IOS.tap(x, y)
        }
        return this
    }

    @Step("Assert {type} notification settings stays off")
    fun assertNotificationsStaysOff(type: VintedNotificationSettingsTypes): NotificationSettingsRobot {
        clickBack()
        if (type == VintedNotificationSettingsTypes.PUSH) {
            settingsRobot.openPushNotificationSettings()
        } else {
            settingsRobot.openEmailNotificationSettings()
        }

        commonUtil.reporting.Report.addMessage("List size: ${individualSettingElementsList!!.size}, List elements: $individualSettingElementsList")
        if (individualSettingElementsList!!.size > 1) VintedAssert.fail("Only global switch should be visible when settings are off")
        return this
    }

    @Step("Turn on notifications")
    fun turnOnNotifications(user: VintedUser): NotificationSettingsRobot {
        if (isAndroid) {
            androidSettingsFirstToggleElement.click()
        } else {
            val element = notificationFirstElementTitleElementIos(user)
            val x = element.center.getX() * 2
            val y = element.center.getY()
            commonUtil.reporting.Report.addMessage("x: $x y: $y")
            IOS.tap(x, y)
        }
        return this
    }

    @Step("Assert that {type} notifications stays on")
    fun assertNotificationsStaysOn(type: VintedNotificationSettingsTypes) {
        clickBack()
        if (type == VintedNotificationSettingsTypes.PUSH) {
            settingsRobot.openPushNotificationSettings()
        } else {
            settingsRobot.openEmailNotificationSettings()
        }

        commonUtil.reporting.Report.addMessage("List size: ${individualSettingElementsList!!.size}, List elements: $individualSettingElementsList")
        if (individualSettingElementsList!!.size == 1) VintedAssert.fail("All settings should be visible when settings are on")
    }
}

package util.deepLinks

import RobotFactory.deepLink
import RobotFactory.uploadItemRobot
import api.data.models.VintedItem
import commonUtil.Util.Companion.sleepWithinStep
import io.qameta.allure.Step
import robot.item.ItemRobot
import robot.upload.UploadItemRobot
import util.EnvironmentManager

class Item {
    @Step("Open item")
    fun goToItem(item: VintedItem): ItemRobot {
        // ToDo try adding 2000 sleep before navigating to item as it seems item is not opened otherwise (item not found message)
        sleepWithinStep(2000)
        deepLink.openURL("item?id=${item.id}")
        return ItemRobot()
    }

    @Step("Open upload form")
    fun goToUploadForm(): UploadItemRobot {
        deepLink.openURL("item/upload")
        return uploadItemRobot
    }

    @Step("Open item editing")
    fun goToItemEditing(item: VintedItem): UploadItemRobot {
        if (EnvironmentManager.isAndroid) deepLink.openURL("item_edit?id=${item.id}") else deepLink.openURL("item/edit?id=${item.id}")
        return uploadItemRobot
    }
}

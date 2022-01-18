package test.basic

import RobotFactory.deepLink
import RobotFactory.delayedPublicationWorkflowRobot
import RobotFactory.feedRobot
import RobotFactory.userProfileRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest

@RunMobile(country = VintedCountry.PL, env = VintedEnvironment.SANDBOX)
@Feature("Delayed publication")
@LoginToMainThreadUser
class DelayedPublicationTests : BaseTest() {

    @BeforeMethod(description = "Open upload form")
    fun beforeTest() {
        ItemAPI.uploadItem(
            loggedInUser,
            ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            brand_id = 10,
            title = "Testing delayed item",
            description = "Delayed item",
            price = "20"
        )
    }

    @Test(description = "Test if item is delayed when matches pre upload item moderation rule")
    fun testIfItemIsDelayed() {
        deepLink.profile.goToUserProfile(loggedInUser.id)
        userProfileRobot.openClosetTab()
        feedRobot.openItem()
        delayedPublicationWorkflowRobot
            .checkIfDelayedItemInformationModalWorks()
            .assertBumpAndShareButtonsAreNotVisible()
            .assertItemActionsAreNotAvailable()
    }
}

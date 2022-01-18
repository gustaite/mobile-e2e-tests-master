package test.basic.links

import RobotFactory.catalogRobot
import RobotFactory.deepLink
import RobotFactory.userProfileRobot
import api.controllers.user.userApi
import commonUtil.testng.CreateOneTestUser
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.*
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import util.base.BaseTest

@RunMobile
@Feature("User profile links tests")
@LoginToMainThreadUser
class UserProfileLinksTests : BaseTest() {

    @CreateOneTestUser
    @Test(description = "Catalog external link open catalog")
    fun testCatalogExternalLink() {
        val catalogURL = Links.getCatalogExternalLink()
        oneTestUser.userApi.updateInfo(catalogURL)

        deepLink.profile.goToUserProfile(oneTestUser.id)
        userProfileRobot.openAboutTab()
        userProfileRobot.aboutScreen
            .clickOnDescription()
        catalogRobot.assertCatalogLayoutIsVisible()
    }
}

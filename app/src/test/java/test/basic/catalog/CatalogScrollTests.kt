package test.basic.catalog

import RobotFactory.catalogRobot
import RobotFactory.navigationRobot
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.SkipRetryOnFailure
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import org.testng.annotations.Test
import util.*
import util.base.BaseTest
import util.testng.*

@RunMobile
@LoginToMainThreadUser
@Test(groups = [TestGroups.CATALOG_SCROLL_TEST])
class CatalogScrollTests : BaseTest() {

    @SkipRetryOnFailure
    @RunMobile(platform = VintedPlatform.ANDROID, message = "Test for Android only")
    @Test(description = "Check if catalog or item screen doesn't freeze")
    fun testNavigatingInCatalogAndItemScreen() {
        navigationRobot.openBrowseTab().openAllCategories()
        val repeatCount = if (portal.isSandbox) 10 else 50
        repeat(repeatCount) {
            catalogRobot.openRandomItemAndGoBack()
            sleepWithinStep(300)
            Android.scrollDownABit()
        }
    }
}

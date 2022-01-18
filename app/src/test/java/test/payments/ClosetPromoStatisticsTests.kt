package test.payments

import RobotFactory.closetPromoWorkflowRobot
import RobotFactory.deepLink
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.item.getItems
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import util.base.BaseTest

class ClosetPromoStatisticsTests : BaseTest() {

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_COUNTRIES_WITH_CLOSET_PROMO)
    @LoginToDefaultUser
    @Test(description = "Buy CP for default user by Credit Card")
    @TmsLink("17796")
    fun testBuyClosetPromoForDefaultUser() {
        var userItemsCount = loggedInUser.getItems().count()

        while (userItemsCount < 5) {
            ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
            userItemsCount ++
        }

        deepLink.profile
            .goToMyProfile()
            .clickOnClosetPromoBanner()
        closetPromoWorkflowRobot
            .reviewOrderAndBuyClosetPromo()
            .clickOnPromoStatisticsBannerAndAssertStatisticsHeader()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_COUNTRIES_WITH_CLOSET_PROMO)
    @LoginToDefaultUser
    @Test(description = "Test elements in Closet Promo statistics screen")
    @TmsLink("18948")
    fun testClosetPromoStatisticsScreen() {
        deepLink.profile
            .goToMyProfile()
            .clickOnClosetPromoBanner()
        closetPromoWorkflowRobot
            .assertAllClosetPromoStatisticsElementsAreVisible()
            .assertClosetPromoStatisticsTipsElementsOpenAppropriateScreens()
    }
}

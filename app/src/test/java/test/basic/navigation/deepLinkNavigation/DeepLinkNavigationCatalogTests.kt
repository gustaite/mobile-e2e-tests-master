package test.basic.navigation.deepLinkNavigation

import RobotFactory.browseRobot
import RobotFactory.catalogRobot
import RobotFactory.deepLink
import RobotFactory.modalRobot
import RobotFactory.navigationRobot
import api.controllers.GlobalAPI
import api.controllers.user.searchApi
import commonUtil.testng.LoginToMainThreadUser
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import java.lang.reflect.Method

@RunMobile
@LoginToMainThreadUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationCatalogTests : BaseTest() {

    @Test(description = "Test if deepLink navigation to 'Items searches' screen is working")
    fun testDeepLinkNavigationToItemsSearchesScreen() {
        deepLink.catalog.goToItemsSearchesScreen()
        browseRobot.assertItemsTabIsVisible()
    }

    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Test if deepLink navigation to 'Items filtered by brand' screen is working")
    fun testDeepLinkNavigationToItemsFilteredByBrandScreen() {
        val brandId = GlobalAPI.getBrands(loggedInUser).last().id
        deepLink.catalog.goToItemsFilteredByBrandId(brandId)
        catalogRobot.assertBrandBannerIsVisible()
    }

    @RunMobile(platform = VintedPlatform.IOS, message = "Test only for iOS")
    @Test(description = "Test if deeplink navigation to 'User search' screen is working")
    fun testDeepLinkNavigationToUnsubscribeSearch() {
        val search = loggedInUser.searchApi.createSearch("nike")
        deepLink.catalog.unsubscribeFromSavedSearch(searchId = search.id!!)
        modalRobot
            .assertElementWithTextIsVisibleInModal(NavigationDataProviders.searchUnsubscribedModalTitle)
    }

    @AfterMethod(description = "Close modal if it is visible")
    fun afterMethod_a_closeModalIfVisible(method: Method) {
        if (method.name == "testDeepLinkNavigationToUnsubscribeSearch" && !navigationRobot.isModalInvisible()) navigationRobot.closeModal()
    }
}

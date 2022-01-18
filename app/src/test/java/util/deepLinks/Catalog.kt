package util.deepLinks

import RobotFactory.browseRobot
import RobotFactory.catalogRobot
import RobotFactory.deepLink
import io.qameta.allure.Step
import robot.BaseRobot
import robot.browse.BrowseRobot
import robot.browse.CatalogRobot
import util.IOS

class Catalog : BaseRobot() {

    @Step("Open catalog with searched text '{searchText}'")
    fun goToCatalogWithSearchValue(searchText: String): CatalogRobot {
        deepLink.openURL("items?q=$searchText")
        return CatalogRobot()
    }

    @Step("Unsubscribe from saved search")
    fun unsubscribeFromSavedSearch(searchId: Long) {
        IOS.doIfiOS {
            deepLink.openURL("catalog_saved_search_unsubscribe?id=$searchId")
        }
    }

    @Step("Open browse screen")
    fun goToBrowseTab(): BrowseRobot {
        deepLink.openURL("catalog")
        return browseRobot
    }

    @Step("Open catalog by id {catalogId}")
    fun goToItemsByCatalogId(catalogId: Long): CatalogRobot {
        deepLink.openURL("items?catalog_id=$catalogId")
        return catalogRobot
    }

    @Step("Open catalog with all items")
    fun goToAllItems(): CatalogRobot {
        deepLink.openURL("items")
        return catalogRobot
    }

    @Step("Open 'Items searches' screen")
    fun goToItemsSearchesScreen() {
        deepLink.openURL("search/items")
    }

    @Step("Open 'Brand' screen")
    fun goToItemsFilteredByBrandId(brandId: Long): CatalogRobot {
        deepLink.openURL("brand?id=$brandId")
        return catalogRobot
    }
}

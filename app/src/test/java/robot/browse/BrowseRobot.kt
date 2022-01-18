package robot.browse

import RobotFactory
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.Util
import util.VintedDriver
import util.driver.*

class BrowseRobot : BaseRobot() {
    private val allCategoriesElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("category_cell"),
            // TODO remove iosBy1 after 22.2 build is built
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId(IOS.getElementValue("catalog_filter_catalog_all")),
                    iosBy2 = VintedBy.accessibilityId("catalog_0_0")
                )
            }
        )

    private val categoryCellElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("category_cell"),
            VintedBy.iOSNsPredicateString("name BEGINSWITH 'catalog_0'")
        )

    private fun tabElement(tabName: String): VintedElement = VintedDriver.findElement(
        VintedBy.androidText(tabName),
        VintedBy.accessibilityId(tabName)
    )

    private val itemsTabElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.androidText(Android.getElementValue("search_tab_title_items")), iOSBy = VintedBy.accessibilityId(IOS.getElementValue("search_scope_items")))

    @Step("Open {categoryTitle} tab")
    fun openTab(categoryTitle: String): BrowseRobot {
        tabElement(categoryTitle).click()
        return BrowseRobot()
    }

    @Step("Open all categories")
    fun openAllCategories(): CatalogRobot {
        Util.retryOnException({ allCategoriesElement.withWait(waitFor = WaitFor.Visible).click() }, 2)
        return CatalogRobot()
    }

    @Step("Click on search field")
    fun clickOnSearchFieldInBrowseScreen(): SearchScreenRobot {
        RobotFactory.searchScreenRobot.clickOnSearchField()
        return RobotFactory.searchScreenRobot
    }

    @Step("Assert browse tab is displayed")
    fun assertBrowseTabIsDisplayed(): BrowseRobot {
        VintedAssert.assertTrue(isAllCategoriesElementVisible(), "All categories element should be visible")
        return this
    }

    @Step("Open first category")
    fun openFirstCategory(): CatalogRobot {
        categoryCellElementList.first().tap()
        return CatalogRobot()
    }

    fun isAllCategoriesElementVisible(): Boolean {
        return allCategoriesElement.isVisible()
    }

    @Step("Assert 'Items' tab is visible")
    fun assertItemsTabIsVisible() {
        VintedAssert.assertTrue(itemsTabElement.isVisible(), "Items tab element should be visible")
    }
}

package robot.personalization

import RobotFactory.navigationRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedCatalogs
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import robot.FeedRobot
import util.Android
import util.IOS
import util.Util
import util.Util.SearchTextOperator.MATCHES
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.values.Action
import util.values.ElementByLanguage
import util.values.Personalization

class CategoriesAndSizesRobot : BaseRobot() {
    private fun sizeElement(text: String): VintedElement {
        return VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text),
            iosElement = {
                IOS.findAllElement(
                    // iosBy1 fixes issue when Size is only one letter which matches accessibilityId first letter
                    iosBy1 = VintedBy.iOSTextByBuilder(text = "$text\\\\b.*", onlyVisibleInScreen = true, searchType = MATCHES),
                    iosBy2 = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true, searchType = Util.SearchTextOperator.STARTS_WITH)
                )
            }
        )
    }

    private fun sizeGroupNameElement(text: String) = VintedDriver.findElement(
        androidBy = VintedBy.androidUIAutomator("UiScrollable(UiSelector().scrollable(true).instance(0)).scrollIntoView(UiSelector().resourceId(\"${Android.ID}label_text\").text(\"$text\"))"),
        iOSBy = VintedBy.accessibilityId(text)
    )

    @Step("Select and assert personalization sizes for all catalogs")
    fun selectAndAssertPersonalizationSizes(): CategoriesAndSizesRobot {
        for (catalog in portal.catalogs) {
            executeActionOnSpecificCategory(catalog, Action.SELECT)
        }
        return this
    }

    @Step("Select/Unselect and assert personalization sizes from specific category")
    fun executeActionOnSpecificCategory(catalog: VintedCatalogs, action: Action): CategoriesAndSizesRobot {
        val categoryTitle = Personalization.getCategoryTitleByCatalog(catalog)
        val sizeTitle = Personalization.getCategorySizeTitlesByCatalog(catalog)

        when (action) {
            Action.SELECT -> selectAndAssertSizes(categoryTitle, sizeTitle)
            Action.UNSELECT -> unselectAndAssertSizesAndGoBackToProfileTab(categoryTitle, sizeTitle)
        }
        return this
    }

    @Step("Select {sizeText} size in women category and go back to feed")
    fun selectPersonalizationSizesAndGoBackToFeed(sizeText: String): FeedRobot {
        openSizesSelectionInPersonalization(Personalization.womenCategoryTitle)
        selectSizesInTheList(listOf(sizeText))
        repeat(3) { clickBack() } // Go back to profile tab
        navigationRobot.openFeedTab()
        return FeedRobot()
    }

    @Step("Select and assert sizes from {categoryTitle} category")
    fun selectAndAssertSizes(categoryTitle: String, sizeTitle: List<String>): CategoriesAndSizesRobot {
        openSizesSelectionInPersonalization(categoryTitle)
        selectSizesInTheList(sizeTitle)
        clickBack()
        assertSelectedSizes(getFormattedSelectedSizes(sizeTitle), Action.SELECT)

        return this
    }

    @Step("Unselect and assert sizes from {categoryTitle} category and go back to profile tab")
    fun unselectAndAssertSizesAndGoBackToProfileTab(categoryTitle: String, sizeTitle: List<String>): CategoriesAndSizesRobot {
        openSizesSelectionInPersonalization(categoryTitle)
        unselectSizesInTheList(sizeTitle)
        clickBack()
        assertSelectedSizes(getFormattedSelectedSizes(sizeTitle), Action.UNSELECT)

        repeat(2) { clickBack() }
        return this
    }

    @Step("Open sizes selection in personalization: {categoryTitle}")
    fun openSizesSelectionInPersonalization(categoryTitle: String) {
        VintedDriver.findElementByText(categoryTitle).tap()
    }

    @Step("Unselect sizes: {list}")
    fun unselectSizesInTheList(list: List<String>) {
        for (i in list.indices) {
            IOS.doIfiOS { VintedDriver.scrollUpABit() }
            sizeElement(list[i]).click()
        }
    }

    @Step("Assert size group name and click on sizes: {list}")
    fun selectSizesInTheList(list: List<String>) {
        assertSizeGroupNameIsVisibleInSizesScreen(list)
        for (i in list.indices) {
            IOS.doIfiOS { VintedDriver.scrollUpABit() }
            sizeElement(list[i]).withScrollIos().click()
        }
    }

    @Step("Assert size group name is visible in sizes screen")
    fun assertSizeGroupNameIsVisibleInSizesScreen(list: List<String>) {
        val name = ElementByLanguage.sizeGroupName(list)
        commonUtil.reporting.Report.addMessage("Size group name: $name")
        commonUtil.Util.retryAction(
            { sizeGroupNameElement(name).isVisible() },
            {
                IOS.scrollDown()
                repeat(2) { Android.scrollDownABit() }
            }
        )
        VintedAssert.assertTrue(
            sizeGroupNameElement(name).isVisible(),
            "Size group name $name should be visible above first sizes"
        )
    }

    @Step("Assert sizes visibility by {catalog} catalog")
    fun assertSizeVisibilityByCatalog(catalog: VintedCatalogs, action: Action) {
        val sizeTitle = Personalization.getCategorySizeTitlesByCatalog(catalog)
        assertSelectedSizes(getFormattedSelectedSizes(sizeTitle), action)
    }

    @Step("Assert sizes {sizesText} visibility")
    fun assertSelectedSizes(sizesText: String, action: Action) {
        VintedAssert.assertEquals(
            VintedDriver.findElementByText(sizesText).withWait(WaitFor.Visible, 1).isVisible(),
            action.action.value, "Element with text '$sizesText' should be ${action.action}"
        )
    }

    private fun getFormattedSelectedSizes(listOfSizes: List<String>): String {
        return String.format("%s", listOfSizes).removeSurrounding(prefix = "[", suffix = "]")
    }
}

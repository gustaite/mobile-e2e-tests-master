package robot.profile

import RobotFactory.userProfileClosetRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.tabs.UserProfileClosetRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.closetFilterScreenTitleText

class UserProfileClosetOrganiseRobot : BaseRobot() {

    private val closetFilterTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "actionbar_label",
                closetFilterScreenTitleText
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("filter_title"))
        )

    private val closetFilterSortElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_filter_sort"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("filter_sorting_title"))
        )

    private val closetFilterCategoryElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_filter_category"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("filter_category"))
        )

    private val closetFilterSortCellSubtitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "closet_filter_sort",
                "filter_cell_subtitle"
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("sort_by_relevance"))
        )

    private fun closetFilterCategoryCellSubtitleElement(selectedCategory: String) =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "closet_filter_category",
                "filter_cell_subtitle"
            ),
            iOSBy = VintedBy.iOSTextByBuilder(selectedCategory)
        )

    private val closetFilterShowResultsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_filter_show_results"),
            iOSBy = VintedBy.accessibilityId("filter_show_results")
        )

    @Step("Click on Closet Filter Category cell")
    fun clickOnClosetFilterCategory(): UserProfileClosetOrganiseRobot {
        closetFilterCategoryElement.click()
        return this
    }

    @Step("Assert Elements in Closet filters first screen are visible")
    fun assertElementsInClosetFiltersScreen(): UserProfileClosetOrganiseRobot {
        VintedAssert.assertTrue(closetFilterTitleElement.isVisible(), "Closet screen name not visible")
        VintedAssert.assertTrue(closetFilterSortElement.isVisible(), "Closet filter sort cell was not visible")
        VintedAssert.assertTrue(closetFilterCategoryElement.isVisible(), "Closet filter category cell not visible")
        return this
    }

    @Step("Check if selected sorting option {sortingOption} is displayed as selected in filter screen")
    fun assertSelectedSortingOptionIsDisplayedInClosetFilter(sortingOption: String): UserProfileClosetOrganiseRobot {
        closetFilterSortCellSubtitleElement.text.let { text ->
            VintedAssert.assertEquals(
                text, sortingOption,
                "'$text' is visible instead of '$sortingOption'"
            )
        }
        return this
    }

    @Step("Click show results in users closet filtering")
    fun clickOnFilterShowResults(): UserProfileClosetRobot {
        closetFilterShowResultsElement.click()
        return userProfileClosetRobot
    }

    @Step("Assert parkas category was selected")
    fun assertSelectedCategoryInClosetFilters(categoryName: String): UserProfileClosetOrganiseRobot {
        closetFilterCategoryCellSubtitleElement(categoryName).text.let { text ->
            VintedAssert.assertEquals(
                text, categoryName,
                "Category name should be $categoryName but was $text"
            )
        }
        return this
    }
}

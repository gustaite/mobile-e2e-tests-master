package robot.workflow

import RobotFactory.categoriesRobot
import RobotFactory.feedRobot
import RobotFactory.userProfileClosetOrganiseRobot
import RobotFactory.userProfileRobot
import io.qameta.allure.Step
import robot.profile.UserProfileClosetOrganiseRobot
import util.base.BaseTest
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.SortingOptions
import util.values.Personalization

class UserProfileClosetWorkflowRobot : BaseTest() {

    @Step("Click more and assert category is {category}")
    fun openItemAndAssertCategory(category: Long): UserProfileClosetWorkflowRobot {
        feedRobot
            .openItem()
            .clickMore()
            .assertCategory(category)
        return this
    }

    @Step("Choose to filter {category} category")
    fun chooseCategoryToFilter(category: String): UserProfileClosetOrganiseRobot {
        userProfileClosetOrganiseRobot
            .clickOnClosetFilterCategory()
        categoriesRobot
            .selectCategory(Personalization.womenCategoryTitle)
            .selectCategoryAndSubcategory(ElementByLanguage.getCategoriesAndSubcategories(0))
        userProfileClosetOrganiseRobot
            .assertSelectedCategoryInClosetFilters(category)
        return userProfileClosetOrganiseRobot
    }

    @Step("Open Closet Filtering and assert default values")
    fun openClosetFilteringAndAssertDefaultValues(): UserProfileClosetWorkflowRobot {
        userProfileRobot
            .clickClosetFilterButton()
            .assertElementsInClosetFiltersScreen()
            .assertSelectedSortingOptionIsDisplayedInClosetFilter(SortingOptions[0])
        return this
    }
}

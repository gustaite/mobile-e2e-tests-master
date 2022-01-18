package robot.browse

import RobotFactory
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.DropOffPointSelectionRobot
import robot.profile.tabs.UserProfileClosetRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor

class SearchScreenRobot : BaseRobot() {

    private val searchFieldElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId(Android.INPUT_FIELD_ID),
            VintedBy.accessibilityId("search_bar")
        )

    private val dropOffPointSearchFieldIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.className("XCUIElementTypeSearchField"))

    private val subscribeButtonAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("search_row_icon"))

    private val clearSearchKeywordButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("clear"))

    private val membersSearchElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.androidUIAutomator("UiSelector().className(\"android.widget.TextView\").instance(1)"))

    private val membersSearchElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            { membersSearchElementAndroid },
            "search_scope_members"
        )

    private fun userCellWithSearchedTextElement(searchedText: String): VintedElement = VintedDriver.findElement(
        VintedBy.androidUIAutomator("UiSelector().className(\"android.widget.TextView\").textMatches(\"$searchedText\")"),
        VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS '$searchedText'")
    )

    private val recentSearchContainerElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("search_row_title"),
            VintedBy.className("XCUIElementTypeCell")
        )

    private val searchResultElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("drop_off_search_row_container"))

    private val clearSearchValueButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("Clear text"))

    private val firstSearchResultElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell"))
    private val searchResultElement: VintedElement
        get() = {
            val text = ", "
            VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(
                    text = text, scroll = false,
                    searchType = Util.SearchTextOperator.CONTAINS
                ),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, searchType = Util.SearchTextOperator.CONTAINS, onlyVisibleInScreen = true)
            )
        }()

    @Step("Search for {searchValue}")
    fun searchFor(searchValue: String): CatalogRobot {
        searchFieldElement.sendKeys(searchValue)
        Android.clickEnter()
        IOS.pressSearchInKeyboard()
        return RobotFactory.catalogRobot
    }

    @Step("Click on search field")
    fun clickOnSearchField(): SearchScreenRobot {
        if (searchFieldElement.isVisible()) {
            searchFieldElement.click()
        } else {
            IOS.doIfiOS { dropOffPointSearchFieldIos.tap() }
        }
        return this
    }

    @Step("Click on subscribe button to subscribe or unsubscribe search")
    fun clickOnSubscribeButton(): SearchScreenRobot {
        Android.doIfAndroid {
            subscribeButtonAndroid.click()
        }
        return this
    }

    @Step("Click members tab")
    fun clickMembersTab(): SearchScreenRobot {
        membersSearchElement.click()
        return this
    }

    @Step("Click on first user in search results to open profile")
    fun openProfileThatMatchesSearchedValue(searchedValue: String): UserProfileClosetRobot {
        userCellWithSearchedTextElement(searchedValue).click()
        return RobotFactory.userProfileClosetRobot
    }

    @Step("Check if number of recent searches is {expectedNumber}")
    fun assertRecentSearchesCount(expectedNumber: Int): SearchScreenRobot {
        VintedAssert.assertTrue(recentSearchContainerElementList.size == expectedNumber, "Recent searches count does not match")
        return RobotFactory.searchScreenRobot
    }

    @Step("Open first recent search")
    fun openSearch(searchIndex: Int): CatalogRobot {
        recentSearchContainerElementList[searchIndex].withWait(WaitFor.Visible).click()
        return RobotFactory.catalogRobot
    }

    @Step("Click on search result")
    fun clickOnDropOffPointSearchResult(): DropOffPointSelectionRobot {
        if (isAndroid && searchResultElementAndroid.withWait().isVisible()) {
            searchResultElementAndroid.click()
        } else if (searchResultElement.isVisible()) {
            searchResultElement.tap()
        } else if (isiOS) {
            // On Int ", " is not visible so tapping first element
            firstSearchResultElementIos.tap()
        }
        return RobotFactory.dropOffPointSelectionRobot
    }

    @Step("Click on drop off point search field")
    fun insertDropOffPointSearchValue(searchValue: String): SearchScreenRobot {
        IOS.doIfiOS {
            if (clearSearchValueButtonIos.withWait().isVisible()) clearSearchValueButtonIos.click()
            dropOffPointSearchFieldIos.sendKeys(searchValue)
            IOS.pressSearchInKeyboard()
        }
        Android.doIfAndroid { searchFieldElement.sendKeys(searchValue) }
        return this
    }

    @Step("Clear search keyword")
    fun clearKeywordFromSearchBarIos(): SearchScreenRobot {
        IOS.doIfiOS { clearSearchKeywordButtonIos.click() }
        return this
    }
}

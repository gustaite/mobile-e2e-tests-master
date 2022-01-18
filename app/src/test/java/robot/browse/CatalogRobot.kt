package robot.browse

import RobotFactory
import RobotFactory.filtersRobot
import RobotFactory.workflowRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.*
import robot.workflow.WorkflowRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.Util.Companion.retryOnException
import util.driver.*
import util.values.CatalogFilterButton
import util.values.ElementByLanguage.Companion.closetFilterScreenTitleText
import util.values.Visibility

class CatalogRobot : BaseRobot() {

    private val catalogLayoutElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("catalog_layout"))

    private val saveSearchIconElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("action_menu_subscribe_search"),
            VintedBy.accessibilityId("search_save")
        )

    private val removeSearchElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("search_remove"))

    private val searchSubscribedModalTitleElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_content", "search_saved_first_time_alert_title")

    private val suggestedBrandNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "brand_cell",
                "view_cell_title"
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[2]/**/XCUIElementTypeOther[1]/**/XCUIElementTypeOther/XCUIElementTypeStaticText[1]")
        )

    private fun horizontalFiltersButtons(button: CatalogFilterButton = CatalogFilterButton.Filter): VintedElement =
        VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filters_list", Android.getElementValue(getFilterKey(button).second)),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue(getFilterKey(button).first))
        )

    private val horizontalFilterButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_title")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_title"))
        )

    private val horizontalSizeButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_size")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_size"))
        )

    private val horizontalBrandButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_brand")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_brand"))
        )

    private val horizontalStatusButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_condition")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_condition"))
        )

    private val horizontalColorButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_color")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_color"))
        )

    private val horizontalPriceButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("item_price_screen_title")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_price_title"))
        )

    private val horizontalMaterialButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("catalog_filter_material_heading")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("catalog_filter_material_heading"))
        )

    private val horizontalSortingButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("horizontal_filter_chip", Android.getElementValue("filter_sorting_title")),
            VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("filter_sorting_title"))
        )

    private val brandBannerElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("brand_cell"),
            iOSBy = VintedBy.accessibilityId("brand_follow_btn")
        )

    private val itemBrandNameAndroidElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_box_info_1"))

    // Todo change iOS element when 'turn off a11y project' PR will be merged
    private val itemsElementsList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("item_box_image"),
            VintedBy.iOSNsPredicateString("${IOS.predicateWithCurrencySymbolsByName} || name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_global_item_box")}'"),
        )

    private fun iosBrandNameElementList(brandName: String): List<VintedElement> {
        return VintedDriver.findElementList(iOSBy = VintedBy.iOSTextByBuilder(text = brandName, searchType = Util.SearchTextOperator.CONTAINS))
    }

    private fun sizeElementList(selectedSize: String): List<VintedElement> {
        return VintedDriver.findElementList(iOSBy = VintedBy.iOSTextByBuilder(text = selectedSize, onlyVisibleInScreen = true))
    }

    private val sortingOptionsListElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.className("android.widget.ListView"),
            iOSBy = VintedBy.className("XCUIElementTypeSheet")
        )

    private fun sortingOptionElement(sortingOption: String): VintedElement = VintedDriver.findElement(
        VintedBy.androidUIAutomator("text(\"$sortingOption\") "),
        VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name CONTAINS '$sortingOption'")
    )

    private val horizontalFiltersListElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("horizontal_filters_list"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/**/XCUIElementTypeCollectionView")
        )

    private val horizontalFiltersBarFilterButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("horizontal_filters_list", "horizontal_filter_chip"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/**/XCUIElementTypeCollectionView[\$name == '$closetFilterScreenTitleText' || label == '$closetFilterScreenTitleText'\$]")
        )

    private val popularItemsScreenTitleElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("actionbar_label", "catalog_popular_items_title")

    @Step("Assert that catalog is visible")
    fun assertCatalogLayoutIsVisible(): CatalogRobot {
        VintedAssert.assertTrue(isCatalogLayoutVisible(), "Catalog should be visible")
        return this
    }

    fun isCatalogLayoutVisible(withWait: Long = 5): Boolean {
        val element: VintedElement = if (isiOS) {
            saveSearchIconElement
        } else {
            catalogLayoutElementAndroid
        }
        return element.withWait(WaitFor.Visible, withWait).isVisible()
    }

    @Step("Assert that catalog with suggested brand '{expectedBrandName} is visible")
    fun assertSuggestedBrandNameIsVisibleInCatalog(expectedBrandName: String) {
        assertCatalogLayoutIsVisible()
        val actualSuggestedBrandNameLowerCase = suggestedBrandNameElement.text.lowercase()

        VintedAssert.assertEquals(
            actualSuggestedBrandNameLowerCase,
            expectedBrandName,
            "'$expectedBrandName' should be visible but found '$actualSuggestedBrandNameLowerCase' (lower case)"
        )
    }

    @Step("Open catalog filters")
    fun openCatalogFilters(): FiltersRobot {
        swipeLeftOnHorizontalFilters(horizontalFilterButton)
        horizontalFilterButton.click()
        return FiltersRobot()
    }

    private fun getFilterKey(filter: CatalogFilterButton): Pair<String, String> {
        return when (filter) {
            CatalogFilterButton.Filter -> Pair("filter_title", "filter_title")
            CatalogFilterButton.Size -> Pair("filter_size", "filter_size")
            CatalogFilterButton.Brand -> Pair("filter_brand", "filter_brand")
            CatalogFilterButton.Condition -> Pair("filter_condition", "filter_condition")
            CatalogFilterButton.Color -> Pair("filter_color", "filter_color")
            CatalogFilterButton.Price -> Pair("filter_price_title", "item_price_screen_title")
            CatalogFilterButton.Sort -> Pair("filter_sorting_title", "filter_sorting_title")
            else -> Pair("NOTIMPLEMENTED", "NOTIMPLEMENTED")
        }
    }

    @Step("Check if filtering buttons are visible in catalog")
    fun assertFilteringButtonsAreVisible(filters: List<CatalogFilterButton>): CatalogRobot {
        filters.forEach {
            if (it == CatalogFilterButton.Condition || it == CatalogFilterButton.Sort) horizontalFiltersListElement.swipeLeft()
            VintedAssert.assertTrue(horizontalFiltersButtons(it).isVisible(), "Filtering button: $it should be visible.")
        }
        return this
    }

    @Step("Assert horizontal filters bar filter and size buttons are {visibility} in catalog")
    fun assertHorizontalFiltersBarFilterAndSizeButtonsVisibility(visibility: Visibility): CatalogRobot {
        VintedAssert.assertVisibilityEquals(horizontalFiltersBarFilterButton, visibility, "Filter button should be $visibility")
        VintedAssert.assertVisibilityEquals(horizontalSizeButton, visibility, "Size button should be $visibility")
        return this
    }

    @Step("Check if sorting button is visible in catalog")
    fun assertSortingButtonIsVisible(): CatalogRobot {
        horizontalFiltersListElement.swipeLeft()
        VintedAssert.assertTrue(horizontalSortingButton.isVisible(), "Sorting button should be visible")
        return this
    }

    @Step("Assert horizontal material button visibility is {visibility}")
    fun assertHorizontalMaterialButtonVisibility(visibility: Visibility): CatalogRobot {
        swipeLeftOnHorizontalFilters(horizontalMaterialButton)
        VintedAssert.assertVisibilityEquals(horizontalMaterialButton, visibility, "Horizontal material button should be $visibility")
        horizontalFiltersListElement.swipeRight()
        return this
    }

    @Step("Assert brand banner is visible in catalog")
    fun assertBrandBannerIsVisible(): CatalogRobot {
        VintedAssert.assertTrue(brandBannerElement.isVisible(), "Brand banner should be visible")
        return this
    }

    @Step("Assert brand banner is not visible in catalog")
    fun assertBrandBannerIsNotVisible(): CatalogRobot {
        VintedAssert.assertTrue(brandBannerElement.isInvisible(), "Brand banner should not be visible")
        return this
    }

    @Step("Check if there are elements in catalog that has searched brand value '{searchedBrand}'")
    fun assertSearchedBrandNameIsVisibleInCatalog(searchedBrand: String) {
        if (isAndroid) {
            val foundItemBrandName = itemBrandNameAndroidElement.text
            VintedAssert.assertTrue(
                foundItemBrandName.equals(searchedBrand, ignoreCase = true),
                "Searched for '$searchedBrand' but found '$foundItemBrandName'"
            )
        } else {
            val numberOfBrandNameElementsIos = iosBrandNameElementList(searchedBrand).size
            val expectedNumberOfBrandElements = 3
            VintedAssert.assertTrue(
                numberOfBrandNameElementsIos >= expectedNumberOfBrandElements,
                "elements with searched brand name should be more or equal $expectedNumberOfBrandElements, but was $numberOfBrandNameElementsIos"
            )
        }
    }

    @Step("Click on sorting button in catalog")
    fun clickOnSortingButton(): CatalogRobot {
        swipeLeftOnHorizontalFilters(horizontalSortingButton)
        horizontalSortingButton.click()
        return this
    }

    @Step("Check if modal with sorting options is displayed (only IOS)")
    fun assertSortingOptionsModalIsVisible(): CatalogRobot {
        IOS.doIfiOS { VintedAssert.assertTrue(sortingOptionsListElement.isVisible(), "Modal with sorting options should be visible") }
        return this
    }

    @Step("Select {sortingOption} sorting option")
    fun selectSortingOption(sortingOption: String): CatalogRobot {
        sortingOptionElement(sortingOption).click()
        Android.doIfAndroid { filtersRobot.clickShowFilterResults() }
        return this
    }

    @Step("Subscribe search in catalog")
    fun subscribeSearchInCatalog(): CatalogRobot {
        saveSearchIconElement.click()
        return this
    }

    @Step("Unsubscribe search in catalog")
    fun unsubscribeSearchInCatalog(): CatalogRobot {
        if (isiOS) {
            removeSearchElementIos.click()
        } else {
            saveSearchIconElement.click()
        }
        return this
    }

    @Step("Check if 'search was subscribed' modal appears")
    fun assertSearchSubscribedModalIsDisplayed(): CatalogRobot {
        VintedAssert.assertTrue(searchSubscribedModalTitleElement.isVisible(), "Search was subscribed modal should be displayed")
        return this
    }

    @Step("Close 'search was subscribed' modal")
    fun closeSearchSubscribedModalIfVisible(): CatalogRobot {
        if (modalOkButton.isVisible()) {
            closeModal()
        }
        return this
    }

    @Step("Check if items in catalog are size {selectedSize}")
    fun checkItemsSizes(selectedSize: String): CatalogRobot {
        val itemsWithSelectedSizeCount = sizeElementList(selectedSize).size
        val expectedItemsInSelectedSearchCount = if (itemsElementsList.size > 2) 2 else itemsElementsList.size
        VintedAssert.assertTrue(
            itemsWithSelectedSizeCount >= expectedItemsInSelectedSearchCount,
            "items with selected size should be equal or grater than $expectedItemsInSelectedSearchCount, but was $itemsWithSelectedSizeCount"
        )
        return this
    }

    @Step("Open random visible item and go back")
    fun openRandomItemAndGoBack(): CatalogRobot {
        sleepWithinStep(100)
        retryOnException(
            {
                itemsElementsList.random().tap()
            },
            3
        )
        sleepWithinStep(100)
        clickBack()
        return this
    }

    @Step("Assert items list is not empty")
    fun assertItemsListIsNotEmpty(): CatalogRobot {
        VintedAssert.assertTrue(VintedElement.isListVisible({ itemsElementsList }), "Items list should not be empty")
        return this
    }

    @Step("Click on size filter button")
    fun openSizeFilter(): SizeRobot {
        horizontalSizeButton.click()
        return RobotFactory.sizeRobot
    }

    @Step("Click on brand filter button")
    fun openBrandFilter(): WorkflowRobot {
        swipeLeftOnHorizontalFilters(horizontalBrandButton)
        horizontalBrandButton.click()
        return workflowRobot
    }

    @Step("Click on status filter button")
    fun openStatusFilter(): ConditionRobot {
        swipeLeftOnHorizontalFilters(horizontalStatusButton)
        horizontalStatusButton.click()
        return RobotFactory.conditionRobot
    }

    @Step("Click on color filter button")
    fun openColorFilter(): ColorRobot {
        swipeLeftOnHorizontalFilters(horizontalColorButton)
        horizontalColorButton.click()
        return RobotFactory.colorRobot
    }

    @Step("Click on price filter button")
    fun openPriceFilter(): CatalogRobot {
        swipeLeftOnHorizontalFilters(horizontalPriceButton)
        horizontalPriceButton.click()
        return this
    }

    @Step("Swipe left on horizontal filters")
    private fun swipeLeftOnHorizontalFilters(targetElement: VintedElement) {
        if (!targetElement.withWait(WaitFor.Visible, 5).isVisible(1)) {
            horizontalFiltersListElement.swipeLeft()
        }
    }

    @Step("Assert subscribe button is visible")
    fun assertSubscribeButtonIsVisible(): CatalogRobot {
        VintedAssert.assertTrue(saveSearchIconElement.withWait(WaitFor.Visible).isVisible(), "Subscribe button should be visible")
        return this
    }

    @Step("Assert if popular items screen is displayed")
    fun assertPopularItemsScreenOpen(): CatalogRobot {
        VintedAssert.assertTrue(popularItemsScreenTitleElement.isVisible(), "Popular items screen title is displayed")
        return this
    }
}

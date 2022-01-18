package robot.browse

import RobotFactory
import RobotFactory.catalogRobot
import RobotFactory.materialRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.*
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.extensions.removeListSurroundingsAndReturnString

class FiltersRobot : BaseRobot() {
    private fun sortingOptionElement(sortingOption: String): VintedElement = VintedDriver.findElement(
        VintedBy.id("filter_cell_subtitle"),
        VintedBy.accessibilityId(sortingOption)
    )

    private fun sizeTextElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
        )

    private val sizesSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("filter_item_size", "size")

    private val colorsSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("filter_item_color", "color")

    private val materialsSectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("filter_item_material"),
            iOSBy = VintedBy.accessibilityId("material")
        )

    private val priceSectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("filter_item_price"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("filter_price_title"))
        )

    private val itemsForSwapSwitcherElementAndroid: VintedElement
        get() = Android.findAllElement(
            androidBy1 = VintedBy.scrollableId("filter_swap_checkbox"),
            androidBy2 = VintedBy.scrollableId("view_toggle_switch")
        )

    private val filterShowResultsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("catalog_filter_show_results"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'filter_show_results' || name == 'selection_action'")
        )

    private val itemsForSwapCellElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("for_swap_cell"))

    private val androidCategorySectionElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("filter_category_parent"))

    private val categoriesSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey({ androidCategorySectionElement }, "category")

    private val conditionsSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("filter_item_state", "state")

    private val brandsSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("filter_item_brand", "label")

    private val clearFiltersButton: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("action_menu_catalog_filter_clear", "filter_clear")

    private val iosPriceFromElement: VintedElement get() = IOS.findElementByTranslationKey("item_filter_price_from_title")

    private val iosPriceToElement: VintedElement get() = IOS.findElementByTranslationKey("item_filter_price_to_title")

    private fun filterCellSubtitleElement(selectedOption: String) = VintedDriver.findElementByText(selectedOption)

    private val priceElementList: List<VintedElement> get() = VintedDriver.findElementList(
        VintedBy.id(Android.INPUT_FIELD_ID),
        VintedBy.className("XCUIElementTypeTextField")
    )

    @Step("Assert sizes {sizesText} are visible in filters")
    fun assertSizesInFilters(sizesText: List<String>): FiltersRobot {
        if (sizesText.isNotEmpty()) {
            val expectedText = sizesText.removeListSurroundingsAndReturnString()
            VintedAssert.assertTrue(sizeTextElement(expectedText).isVisible(), "Element with text '$expectedText' should be visible in screen")
        }
        return this
    }

    @Step("Click show filter results")
    fun clickShowFilterResults(): CatalogRobot {
        filterShowResultsButton.click()
        return catalogRobot
    }

    @Step("Assert sizes {sizesText} are not visible in filters")
    fun assertSizesAreNotSelectedInFilters(sizesText: List<String>): FiltersRobot {
        val sizesString = sizesText.removeListSurroundingsAndReturnString()
        VintedAssert.assertTrue(sizeTextElement(sizesString).isInvisible(), "No sizes should be selected in filters")
        return this
    }

    @Step("Click on 'items for swap' switch element")
    fun clickOnItemsForSwapSwitcher(): FiltersRobot {
        if (isAndroid) {
            itemsForSwapSwitcherElementAndroid.click()
        } else {
            val x = itemsForSwapCellElementIos.withScrollIos().location.getX() + itemsForSwapCellElementIos.rect.width + 25
            val y = itemsForSwapCellElementIos.center.getY()
            commonUtil.reporting.Report.addMessage("x: $x y: $y")
            IOS.tap(x, y)
        }
        return this
    }

    @Step("Check if randomly selected sorting option is displayed as selected one in filter screen")
    fun assertSelectedSortingOptionIsDisplayedInFilter(sortingOption: String): FiltersRobot {
        val sortingOptionInFilterText = sortingOptionElement(sortingOption).text
        VintedAssert.assertEquals(
            sortingOptionInFilterText,
            sortingOption,
            "'$sortingOptionInFilterText' is visible instead of '$sortingOption'"
        )
        return FiltersRobot()
    }

    @Step("Open catalogs and categories screen")
    fun openCatalogsAndCategoriesScreen(): CategoriesRobot {
        categoriesSectionElement.click()
        return CategoriesRobot()
    }

    @Step("Open colors selection screen")
    fun openColorsScreen(): ColorRobot {
        colorsSectionElement.click()
        return ColorRobot()
    }

    @Step("Open material selection screen")
    fun openMaterialsScreen(): MaterialRobot {
        materialsSectionElement.click()
        return materialRobot
    }

    @Step("Open conditions selection screen")
    fun openConditionsScreen(): ConditionRobot {
        conditionsSectionElement.click()
        return ConditionRobot()
    }

    @Step("Open sizes selection screen")
    fun openSizesScreen(): SizeRobot {
        sizesSectionElement.click()
        return RobotFactory.sizeRobot
    }

    @Step("Open brands selection screen")
    fun openBrandsScreen(): BrandRobot {
        brandsSectionElement.click()
        return BrandRobot()
    }

    @Step("Open price selection screen")
    fun openPriceScreen(): PriceRobot {
        priceSectionElement.click()
        return PriceRobot()
    }

    @Step("Click on clear filters button")
    fun clearFilters(): FiltersRobot {
        clearFiltersButton.click()
        return FiltersRobot()
    }

    @Step("Check if option '{selectedOption}' is selected")
    fun assertGivenOptionIsSelected(selectedOption: String): FiltersRobot {
        VintedAssert.assertTrue(filterCellSubtitleElement(selectedOption).isVisible(), "'$selectedOption' should be visible in filters")
        return this
    }

    @Step("Check if option '{selectedOption}' is not selected")
    fun assertGivenOptionIsNotSelected(selectedOption: String): FiltersRobot {
        VintedAssert.assertFalse(filterCellSubtitleElement(selectedOption).isVisible(), "'$selectedOption' should not be visible in filters")
        return this
    }

    @Step("Check if material section is not visible in filters")
    fun assertMaterialSectionIsNotVisible(): FiltersRobot {
        VintedAssert.assertFalse(materialsSectionElement.isVisible(1), "Material section should be invisible")
        return this
    }

    @Step("Insert price old way")
    fun insertPriceOldWay(minPrice: String, maxPrice: String): FiltersRobot {
        if (priceElementList.isEmpty()) {
            Android.scrollDown()
        }
        val priceFirstElement = priceElementList.first()
        val priceSecondElement = priceElementList.last()
        if (isAndroid) {
            priceFirstElement.sendKeys(minPrice)
            priceSecondElement.sendKeys(maxPrice)
        } else {
            iosPriceFromElement.click()
            priceSecondElement.sendKeys(minPrice)
            iosPriceToElement.click()
            priceFirstElement.sendKeys(maxPrice)
            iosPriceFromElement.click() // Without doing click on PriceFrom element value does not get saved on navigating to brand section
            if (brandsSectionElement.isVisible()) {
                brandsSectionElement.click() // opening and leaving this screen to close the keyboard in filters screen
                clickBack()
            }
        }
        return this
    }

    @Step("Insert minimum price {minPrice} and maximum price {maxPrice}")
    fun insertPrice(minPrice: String, maxPrice: String): FiltersRobot {
        if (priceSectionElement.isVisible()) {
            openPriceScreen().insertPriceAndGoBackToFilters(minPrice, maxPrice)
        } else {
            insertPriceOldWay(minPrice, maxPrice)
        }
        return this
    }

    @Step("Insert minimum price {minPrice} and maximum price {maxPrice} and show results")
    fun insertPriceAndShowResults(minPrice: String, maxPrice: String) {
        if (isiOS && priceSectionElement.isVisible()) {
            openPriceScreen().insertPriceAndShowResults(minPrice, maxPrice)
        } else {
            insertPriceOldWay(minPrice, maxPrice)
            clickShowFilterResults()
        }
    }
}

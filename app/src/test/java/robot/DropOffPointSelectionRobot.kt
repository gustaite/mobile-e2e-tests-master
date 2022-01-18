package robot

import RobotFactory.dropOffPointSelectionRobot
import RobotFactory.searchScreenRobot
import RobotFactory.workflowRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.browse.SearchScreenRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class DropOffPointSelectionRobot : BaseRobot() {

    private val searchIconElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("menu_drop_off_point_selection_search"),
            iOSBy = VintedBy.accessibilityId("search")
        )

    private val searchBarElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeSearchField")
        )

    private val androidDropOffPointScreenTabsList: List<VintedElement> get() = Android.findElementList(VintedBy.id("android:id/text1"))

    private val iosListedDropOffPointsTabElement: VintedElement get() = VintedDriver.findElement(
        iOSBy = VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("carrier_selection_pudo_list"))
    )

    private val dropOffPointAbTestOnCellsElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("shipping_selection_cell"),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeOther/*/XCUIElementTypeOther")
        )

    private val dropOffPointAbTestOffCellsElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("shipping_selection_row_container"),
            VintedBy.className("XCUIElementTypeCell")
        )

    private val confirmDropOffPointButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("submit_button"),
            iOSBy = VintedBy.accessibilityId("submit")
        )

    private val parcelShopInfoElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "drop_off_point_information_basic_info_cell",
            "parcel_shop_selection_information_get_direction_title"
        )

    private val selectParcelShopButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("shipping_point_information_submit_button"),
            iOSBy = VintedBy.accessibilityId("select_parcel_shop_button")
        )

    private val shippingDetailsSaveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("shipping_details_save"),
            iOSBy = VintedBy.accessibilityId("confirm_shipping_details_button")
        )

    private val searchThisAreaButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("shipping_point_map_search_this_area_button"),
            iOSBy = VintedBy.accessibilityId("search_this_area_shop_button")
        )

    private val shippingPointMapElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("shipping_point_map_view_container"),
            iOSBy = VintedBy.accessibilityId("shipping_point_selection_map")
        )

    @Step("Click on search icon and search tab")
    fun clickOnSearchIcon(): SearchScreenRobot {
        if (searchIconElement.isVisible(10)) searchIconElement.click()
        searchBarElement.click()
        return searchScreenRobot
    }

    @Step("Assert shipping points map is visible")
    private fun assertShippingPointsMapIsVisible(): DropOffPointSelectionRobot {
        VintedAssert.assertTrue(shippingPointMapElement.isVisible(), "Shipping points map should be visible")
        return this
    }

    @Step("Zoom country map")
    fun zoomCountryMap(): SearchScreenRobot {
        val x = shippingPointMapElement.center.getX()
        val y = shippingPointMapElement.center.getY()

        Android.doubleTap(x, y)
        IOS.doubleTap(x, y)
        return searchScreenRobot
    }

    @Step("Assert Search This Area button is {visibility}")
    fun assertSearchThisAreaButtonVisibility(visibility: Visibility): SearchScreenRobot {
        VintedAssert.assertVisibilityEquals(searchThisAreaButtonElement, visibility, "Search This Area button should be $visibility")
        return searchScreenRobot
    }

    @Step("Click on Search This Area")
    fun clickOnSearchThisArea(): SearchScreenRobot {
        searchThisAreaButtonElement.click()
        return searchScreenRobot
    }

    @Step("Assert Drop Off Point list is {visibility}")
    fun assertDropOffPointListVisibility(visibility: Visibility): SearchScreenRobot {
        VintedAssert.assertEquals(
            VintedElement.isListVisible({ dropOffPointAbTestOffCellsElementList }), visibility.value,
            "Drop Off Point list should be $visibility"
        )
        return searchScreenRobot
    }

    @Step("Open drop off points list tab")
    fun openDropOffPointsListIfVisible(): DropOffPointSelectionRobot {
        sleepWithinStep(600)
        if (isiOS && iosListedDropOffPointsTabElement.isVisible()) {
            iosListedDropOffPointsTabElement.click()
        } else if (isAndroid && VintedElement.isListVisible({ androidDropOffPointScreenTabsList })) {
            commonUtil.Util.retryUntil(block = { androidDropOffPointScreenTabsList.count() > 1 }, tryForSeconds = 5)
            androidDropOffPointScreenTabsList.last().click()
        }
        return this
    }

    @Step("Select random drop off point")
    fun selectDropOffPointFromList(): DropOffPointSelectionRobot {
        dropOffPointAbTestOffCellsElementList.first().click()
        return this
    }

    @Step("Confirm selected parcel shop")
    fun confirmSelectedParcelShop(): CheckoutRobot {
        if (selectParcelShopButton.isVisible()) {
            selectParcelShopButton.click()
        } else if (confirmDropOffPointButton.isVisible()) {
            confirmDropOffPointButton.click()
        }
        saveShippingDetails()
        return RobotFactory.checkoutRobot
    }

    @Step("Click shipping details save button if visible")
    fun saveShippingDetails() {
        if (shippingDetailsSaveButton.isVisible()) {
            shippingDetailsSaveButton.click()
        }
    }

    @Step("Select drop off point from the list or search for it")
    fun selectDropOffPoint(): CheckoutRobot {
        openDropOffPointsListIfVisible()
        if (dropOffPointAbTestOffCellsElementList.isNotEmpty()) {
            selectDropOffPointFromList()
        } else (
            workflowRobot.searchForDropOffPointAndSelectItFromList()
            )
        confirmSelectedParcelShop()
        return RobotFactory.checkoutRobot
    }

    @Step("Search and assert Drop Off Points are found through Search This Area button")
    fun assertDropOffPointsAreFoundBySearchThisAreaButton(): DropOffPointSelectionRobot {
        // TODO android workaround because of existing bug "android/issues/16267"
        Android.doIfAndroid {
            workflowRobot.searchForDropOffPointAndClickOnSearchResult()
        }
        assertShippingPointsMapIsVisible()
        zoomCountryMap()
        assertSearchThisAreaButtonVisibility(Visibility.Visible)
        clickOnSearchThisArea()
        openDropOffPointsListIfVisible()
        assertDropOffPointListVisibility(Visibility.Visible)
        return dropOffPointSelectionRobot
    }
}

package robot.bumps

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class BumpsItemsSelectionRobot : BaseRobot() {
    private val addBumpButtonsList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.androidText(Android.getElementValue("multiple_selection_select_action")),
            VintedBy.iOSClassChain("**/XCUIElementTypeButton[`label == '${IOS.getElementValue("item_footer_view_select_button")}'`]")
        )

    private val removeBumpButtonsList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.androidText(Android.getElementValue("multiple_selection_deselect_action")),
            VintedBy.iOSClassChain("**/XCUIElementTypeButton[`label == '${IOS.getElementValue("item_footer_view_deselect_button")}'`]")
        )

    private val submitButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("submit_button"), iOSBy = VintedBy.accessibilityId("confirm"))

    private val itemGridElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_grid_recycler_view"),
            iOSBy = VintedBy.className("XCUIElementTypeCollectionView")
        )

    private val selectedItemsCellElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("selected_items"))

    private val firstSelectedItemElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("first_item"))

    @Step("Click on bump button to add item to the list")
    fun addItemToTheList(itemsCount: Int = 1): BumpsItemsSelectionRobot {
        repeat(itemsCount) { addBumpButtonsList[0].click() }
        return this
    }

    @Step("Click on bump button to remove item from the list")
    fun removeItemFromTheList(itemIndex: Int = 1): BumpsItemsSelectionRobot {
        removeBumpButtonsList[itemIndex].click()
        return this
    }

    @Step("Click submit")
    fun clickSubmit(): BumpsPreCheckoutRobot {
        submitButton.click()
        return RobotFactory.bumpsPreCheckoutRobot
    }

    @Step("Check if items grid is displayed")
    fun assertItemsGridIsDisplayed(): BumpsItemsSelectionRobot {
        VintedAssert.assertTrue(itemGridElement.isVisible(), "Items grid should be visible")
        return this
    }

    @Step("Check if cell with selected items is displayed on Android")
    fun assertCellWithSelectedItemsIsDisplayed(): BumpsItemsSelectionRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(selectedItemsCellElementAndroid.isVisible(), "Cell with selected items should be visible")
        }
        return this
    }

    @Step("Check if first selected item is visible in selected items cell on Android")
    fun assertFirstSelectedItemIsVisible(): BumpsItemsSelectionRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(firstSelectedItemElementAndroid.isVisible(), "One item should be visible is selected items cell")
        }
        return this
    }
}

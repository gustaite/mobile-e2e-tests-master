package robot.bumps

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class BumpOrderDetailsRobot : BaseRobot() {

    private val itemName = "Good auto_test item"
    private val orderSummaryItemCellElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.setWithParentAndChild("push_up_order_summary_row_cell", "view_cell_image"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeStaticText[`label != '${IOS.getElementValue("push_up_order_direct_payment_pice_message")}'  && label BEGINSWITH '$itemName'`]")
        )
    private val closeButtonIos: VintedElement
        get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("close"))

    @Step("Check if order summary cell is displayed")
    fun assertOrderSummaryCellIsDisplayedAndCloseIt(): BumpsCheckoutRobot {
        VintedAssert.assertTrue(orderSummaryItemCellElementList.isNotEmpty(), "Cell with order summary should be visible")
        Android.doIfAndroid { clickBack() }
        IOS.doIfiOS { closeButtonIos.click() }
        return RobotFactory.bumpsCheckoutRobot
    }

    @Step("Check if right amount of items with bump prices are displayed")
    fun assertRightAmountOfItemsAreDisplayedInOrderSummary(itemsCount: Int): BumpOrderDetailsRobot {
        val cellsWithItemsCount = orderSummaryItemCellElementList.count()
        VintedAssert.assertEquals(cellsWithItemsCount, itemsCount, "$cellsWithItemsCount items are displayed but should be $itemsCount")
        return this
    }
}

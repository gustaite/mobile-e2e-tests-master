package robot.upload

import RobotFactory.filtersRobot
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class PriceRobot : BaseRobot() {

    private val priceFromElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "filter_price_from",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("item_filter_price_from_title"))
        )

    private val priceToElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "filter_price_to",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("item_filter_price_to_title"))
        )

    @Step("Insert minimum price {minPrice} and maximum price {maxPrice} and go back filters")
    fun insertPriceAndGoBackToFilters(minPrice: String, maxPrice: String) {
        insertPrice(minPrice, maxPrice)
        clickBack()
    }

    @Step("Insert minimum price {minPrice} and maximum price {maxPrice} and show results")
    fun insertPriceAndShowResults(minPrice: String, maxPrice: String) {
        insertPrice(minPrice, maxPrice)
        filtersRobot.clickShowFilterResults()
    }

    private fun insertPrice(minPrice: String, maxPrice: String) {
        priceFromElement.sendKeys(minPrice)
        priceToElement.sendKeys(maxPrice)
    }
}

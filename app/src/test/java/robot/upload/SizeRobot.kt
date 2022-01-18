package robot.upload

import RobotFactory.webViewRobot
import RobotFactory.workflowRobot
import api.controllers.GlobalAPI
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.browse.FiltersRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage
import util.values.Visibility

class SizeRobot : BaseRobot() {

    private val sizesTitleElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("label_text"),
            // TODO remove iosBy1 after 22.2 build is built
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.iOSClassChain("**/XCUIElementTypeOther/XCUIElementTypeStaticText"),
                    iosBy2 = VintedBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeOther")
                )
            }
        )

    private fun sizeElement(selectedSize: String): VintedElement {
        return VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = selectedSize),
            iosElement = {
                IOS.findAllElement(
                    // iosBy1 fixes issue when Size is only one letter which matches accessibilityId first letter
                    iosBy1 = VintedBy.iOSTextByBuilder(text = "$selectedSize\\\\b.*", onlyVisibleInScreen = true, searchType = Util.SearchTextOperator.MATCHES),
                    iosBy2 = VintedBy.iOSTextByBuilder(text = selectedSize, onlyVisibleInScreen = true, searchType = Util.SearchTextOperator.STARTS_WITH)
                )
            }
        )
    }

    private val sizeSuggestionTitleCellIos: VintedElement
        get() = IOS.findElementByTranslationKey("size_picker_suggestions")

    private val sizeListTitleElement: List<VintedElement> get() = Android.findElementList(
        VintedBy.id("label_container")
    )

    private val beddingSizesElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.className("android.widget.CheckBox"),
            iOSBy = VintedBy.iOSNsPredicateString("name MATCHES 'size_.'")
        )

    private val sizesChartHeaderTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("sizes_chart_header_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '${IOS.getElementValue("size_picker_sizes_view_size_guide")}'")
        )

    @Step("Select first size")
    fun selectFirstSize(): UploadItemRobot {
        VintedDriver.findElementByText(ElementByLanguage.Size).click()
        return UploadItemRobot()
    }

    @Step("Check if all bedding sizes are visible")
    fun assertBeddingSizesAreVisible(): SizeRobot {
        VintedAssert.assertTrue(VintedElement.isListVisible({ beddingSizesElementList }), "Bedding sizes elements list should be visible")
        VintedAssert.assertEquals(beddingSizesElementList.count(), 4, "Bedding sizes count does not match")
        return this
    }

    @Step("Assert sizes {sizesText} visibility")
    fun assertSizesVisibility(sizesText: String, visibility: Visibility): SizeRobot {
        VintedAssert.assertVisibilityEquals(sizeElement(sizesText), visibility, "Element with text '$sizesText' should be $visibility")
        return this
    }

    @Step("Select first home decor item size")
    fun selectFirstHomeDecorItemSize(): UploadItemRobot {
        VintedDriver.findElementByText(ElementByLanguage.HomeDecorTextileSize).click()
        return UploadItemRobot()
    }

    @Step("Get random size on screen from API")
    fun getRandomSizeOnScreenFromApi(): String {
        val sizesTitle = sizesTitleElement.text
        commonUtil.reporting.Report.addMessage("Size title was: $sizesTitle")
        return GlobalAPI.getSizes(user = loggedInUser).first { it.description == sizesTitle && it.sizes.count() > 0 }.sizes.take(8).random().title
    }

    @Step("Select size {size}")
    fun selectSize(size: String): SizeRobot {
        sizeElement(size).click()
        return RobotFactory.sizeRobot
    }

    @Step("Select size {selectedSize}")
    fun selectSizeAndGoBackToFilters(selectedSize: String): FiltersRobot {
        var times = 1
        while (times < 5 && !sizeElement(selectedSize).isVisible(1)) {
            IOS.scrollDown()
            IOS.doIfiOS { if (!sizeElement(selectedSize).isVisible(1)) VintedDriver.scrollDownABit() }
            times++
        }
        sizeElement(selectedSize).click()
        clickBack()
        return FiltersRobot()
    }

    @Step("Check if size suggestions are displayed")
    fun assertSizeSuggestionsAreDisplayed(sizes: List<String>): SizeRobot {
        workflowRobot
            .checkIfSuggestionElementsAreDisplayed({ sizeListTitleElement }, { sizeSuggestionTitleCellIos })
            .checkIfSuggestionsAreDisplayed(sizes)
        return this
    }

    @Step("Click on sizes guide link and assert WebView is visible")
    fun clickOnSizesGuideLinkAndAssertWebViewIsVisible(): SizeRobot {
        sizesChartHeaderTextElement.tapRightBottomCorner(10, -10) { sizesChartHeaderTextElement.isInvisible() }
        webViewRobot.assertWebViewIsVisible()
        clickBack()
        return this
    }
}

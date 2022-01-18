package robot.upload

import RobotFactory.uploadItemRobot
import commonUtil.extensions.escapeApostrophe
import RobotFactory.workflowRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import org.openqa.selenium.Keys
import org.openqa.selenium.NoSuchElementException
import robot.BaseRobot
import robot.workflow.UploadFormWorkflowRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.values.ElementByLanguage.Companion.brandNotFoundText
import util.values.ElementByLanguage.Companion.createCustomBrandText
import util.values.Visibility

class BrandRobot : BaseRobot() {
    private val luxuryBrands: Array<String> = arrayOf("Versace", "Gucci", "Roberto Cavalli", "Burberry")

    private val noMatchingBrandLabelElement: VintedElement
        get() = VintedDriver.findElement(
            null,
            VintedBy.accessibilityId(IOS.getElementValue("no_matching_brands_heading"))
        )
    private val noBrandElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("use_no_brand")),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("use_no_brand"))
        )

    private val searchInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("search")
        )

    private fun brandElement(brandName: String): VintedElement =
        VintedDriver.findElement(
            VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_cell_title\").textMatches(\"$brandName\")"),
            VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' && label CONTAINS '${brandName.escapeApostrophe()}'")
        )

    private val brandElementsList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.className("android.widget.TextView"),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeStaticText")
        )

    private val brandSuggestionTitleCellIos: VintedElement
        get() = IOS.findElementByTranslationKey("brand_picker_suggestions")

    private val brandListTitleElement: List<VintedElement> get() = Android.findElementList(
        VintedBy.id("label_container")
    )

    // TODO remove iosBy1 after 22.2 build is built
    private val firstBrandElementIos: VintedElement
        get() = VintedDriver.findElement(
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.iOSClassChain("**XCUIElementTypeCell[1]"),
                    iosBy2 = VintedBy.accessibilityId("brands_0_cell")
                )
            }
        )

    private val androidBackButton: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("actionbar_button"))

    private val customBrandSectionTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidText(brandNotFoundText),
            VintedBy.accessibilityId(IOS.getElementValue("brand_picker_custom_section_title"))
        )

    private val customBrandElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id(Android.CELL_BODY_FIELD_ID),
            VintedBy.iOSTextByBuilder(createCustomBrandText, Util.SearchTextOperator.CONTAINS)
        )

    private val availableBrandsHeadingElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId(IOS.getElementValue("available_brands_heading")))

    @Step("Select no brand")
    fun selectNoBrand(): UploadItemRobot {
        Android.doIfAndroid {
            searchInputElement.withWait()
            Android.closeKeyboard()
        }

        IOS.doIfiOS {
            IOS.hideKeyboard()
            availableBrandsHeadingElementIos.click()
            IOS.scrollDown(true)
            commonUtil.Util.retryAction(
                {
                    try {
                        noMatchingBrandLabelElement.isVisible(1).let { isVisible ->
                            if (isVisible) VintedDriver.scrollDownABit(); isVisible
                        }
                    } catch (exception: NoSuchElementException) {
                        false
                    }
                },
                { IOS.scrollDown() }, 10
            )
        }

        noBrandElement.click()
        return UploadItemRobot()
    }

    @Step("Select luxury brand")
    fun selectLuxuryBrand(): UploadFormWorkflowRobot {
        val brand = luxuryBrands.random()

        if (isiOS) {
            searchInputElement.withWait().sendKeys(brand)
        } else {
            searchInputElement.click()
            Android.sendKeysUsingKeyboard(brand) // brand search in Android does not work with element.sendKeys
        }

        brandElement(brand).click()
        return UploadFormWorkflowRobot()
    }

    @Step("Return randomly selected brand")
    fun returnRandomBrand(): String {
        if (isiOS) {
            VintedAssert.assertTrue(searchInputElement.isVisible(10), "SearchInputElement should be visible")
        }
        val randomBrand = brandElementsList.random().text
        commonUtil.reporting.Report.addMessage("Selected brand is '$randomBrand'")
        return randomBrand
    }

    @Step("Search for {selectedBrand}")
    fun searchBrand(selectedBrand: String): BrandRobot {
        if (isiOS) {
            searchInputElement.withWait().sendKeys(selectedBrand, Keys.ENTER)
        } else {
            searchInputElement.click()
            Android.sendKeysUsingKeyboard(selectedBrand)
        }
        return this
    }

    @Step("Click back to leave brands screen")
    fun leaveBrandsScreen() {
        if (isAndroid) androidBackButton.click() else clickBack()
    }

    @Step("Select {selectedBrand} brand")
    fun selectBrand(selectedBrand: String): BrandRobot {
        if (isAndroid) {
            brandElement(selectedBrand).click()
        } else {
            firstBrandElementIos.withWait(waitFor = WaitFor.Visible, seconds = 10).isVisible()
            brandElement(selectedBrand).click()
        }
        return this
    }

    @Step("Create Custom brand")
    fun createCustomBrand(): UploadItemRobot {
        customBrandElement.click()
        return uploadItemRobot
    }

    @Step("Check if brands suggestions are displayed {brands} and choose first brand")
    fun assertBrandsSuggestionsAreDisplayed(brands: List<String>): BrandRobot {
        workflowRobot
            .checkIfSuggestionElementsAreDisplayed({ brandListTitleElement }, { brandSuggestionTitleCellIos })
            .checkIfSuggestionsAreDisplayed(brands)
        return this
    }

    @Step("Check if Custom brand selection is {visibility} and {customBrand} is displayed")
    fun assertCustomBrandSuggestionVisibility(visibility: Visibility, customBrand: String): BrandRobot {
        VintedAssert.assertVisibilityEquals(customBrandSectionTitleElement, visibility, "Custom brand creation label should be $visibility")
        VintedAssert.assertVisibilityEquals(customBrandElement, visibility, "Custom brand cell should be $visibility")
        customBrandElement.text.let {
            VintedAssert.assertTrue(
                it.contains(createCustomBrandText) && it.contains(customBrand),
                "Custom brand cell should contain '$createCustomBrandText' and '$customBrand' but was '$it'"
            )
        }
        return this
    }
}

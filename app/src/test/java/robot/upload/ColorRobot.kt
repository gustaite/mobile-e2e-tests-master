package robot.upload

import RobotFactory.colorRobot
import RobotFactory.workflowRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.ActionBarRobot
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage

class ColorRobot : BaseRobot() {

    private val actionBarRobot: ActionBarRobot = ActionBarRobot()
    private fun colorElement(selectedColor: String) = VintedDriver.findElement(
        androidBy = VintedBy.androidTextByBuilder(text = selectedColor, scroll = false),
        iOSBy = VintedBy.iOSTextByBuilder(text = selectedColor, searchType = Util.SearchTextOperator.CONTAINS, onlyVisibleInScreen = true)
    )

    private val colorListTitleElement: List<VintedElement> get() = Android.findElementList(VintedBy.id("label_container"))
    private val colorSuggestionTitleCellIos: VintedElement get() = IOS.findElementByTranslationKey("color_picker_suggestions")

    @Step("Select first two colors")
    fun selectColors(): UploadItemRobot {
        val (firstColor, secondColor) = ElementByLanguage.FirstTwoColors

        commonUtil.reporting.Report.addMessage("Selects first color: $firstColor")
        VintedDriver.findElementByText(firstColor).click()

        commonUtil.reporting.Report.addMessage("Selects second color: $secondColor")
        VintedDriver.findElementByText(secondColor).click()

        actionBarRobot.submitInColorScreen()

        return UploadItemRobot()
    }

    @Step("Select color")
    fun performClickOnColor(selectedColor: String): ColorRobot {
        if (isAndroid) {
            colorElement(selectedColor).click()
        } else {
            val element = colorElement(selectedColor).withWait()
            if (element.isVisible()) element.click() else VintedAssert.fail("Color element was not found using text $selectedColor")
        }
        return this
    }

    @Step("Select color {colorTitle}")
    fun selectColor(colorTitle: String): ColorRobot {
        performClickOnColor(colorTitle)
        return colorRobot
    }

    @Step("Unselect color {colorTitle}")
    fun unselectColor(colorTitle: String): ColorRobot {
        performClickOnColor(colorTitle)
        return colorRobot
    }

    @Step("Check if colors suggestions are displayed")
    fun assertColorsSuggestionsAreDisplayed(colors: List<String>): ColorRobot {
        workflowRobot
            .checkIfSuggestionElementsAreDisplayed({ colorListTitleElement }, { colorSuggestionTitleCellIos })
            .checkIfSuggestionsAreDisplayed(colors)
        return this
    }
}

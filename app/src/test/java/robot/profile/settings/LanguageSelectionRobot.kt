package robot.profile.settings

import api.data.responses.VintedLanguage
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.EnvironmentManager.isiOS
import util.IOS.ElementType.BUTTON
import util.Util.SearchTextOperator.CONTAINS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage

class LanguageSelectionRobot : BaseRobot() {
    private fun languageElement(language: String): VintedElement =
        VintedDriver.findElementByText(text = language, searchType = CONTAINS)

    private val confirmButton: VintedElement
        get() {
            val text = ElementByLanguage.getElementValueByPlatform(key = "save")
            return VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false, searchType = CONTAINS),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true, searchType = CONTAINS, elementType = BUTTON)
            )
        }

    private val languagesList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.setWithParentAndChild("item_list_language_cell", Android.CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeCell")
        )

    @Step("Select {language.title} language")
    fun selectLanguage(language: VintedLanguage) {
        languageElement(language.title).click()
        confirmButton.click()
    }

    @Step("Get visible languages count")
    fun getLanguagesCount(): Int {
        return if (isiOS) {
            languagesList.count() - 1
        } else languagesList.count()
    }

    @Step("Check if language '{language}' is available")
    fun isLanguageVisible(language: String): Boolean {
        return languageElement(language).isVisible(1)
    }
}

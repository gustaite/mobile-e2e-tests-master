package robot.profile

import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.settings.AccountProfileSaveSectionRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.IOS
import util.Util
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ToggleValue

class UserProfileEditRobot : BaseRobot() {
    val saveSection get() = AccountProfileSaveSectionRobot()
    private val cityNameText get() = portal.country.cities.popular.title

    private val aboutMeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("${Android.Scroll.SCROLL_INTO_VIEW_PREFIX}${Android.ID}about_me\").childSelector(UiSelector().resourceId(\"${Android.INPUT_FIELD_ID}\")).instance(0))"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name = 'description'`]/**/XCUIElementTypeTextView")
        )

    private val profileEditNavigationBarElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(
                text = Android.getElementValue("page_title_user_profile_edit"),
                scroll = false
            ),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && (name == '${IOS.getElementValue("edit_profile")}' || name == 'Keisti paskyrą')")
        )

    private val locationViewSwitchAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("view_toggle_switch"))

    private val myLocationSection: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_profile_my_location_cell"),
            iOSBy = VintedBy.accessibilityId("location")
        )

    private fun myLocationCellSubtitle(countryTitle: String): VintedElement {
        return VintedDriver.findElement(
            VintedBy.scrollableId("user_profile_my_location_cell_subtitle"),
            VintedBy.iOSNsPredicateString("(type == 'XCUIElementTypeStaticText' && name == '$cityNameText, $countryTitle') || name == 'location'")
        )
    }

    private fun countryElement(country: String): VintedElement =
        VintedDriver.findElementByText(text = country, searchType = Util.SearchTextOperator.CONTAINS)

    private val cityElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_cell_title\").textMatches(\"$cityNameText\")"),
            VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name == '$cityNameText'")
        )

    private val locationCityInputValue: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId(Android.INPUT_FIELD_ID),
            VintedBy.accessibilityId("search_bar")
        )

    @Step("Enter about me description")
    fun enterAboutMe(aboutMe: String): UserProfileEditRobot {
        aboutMeElement.click().sendKeys(aboutMe)
        return this
    }

    @Step("Assert about me description is {aboutMe}")
    fun assertAboutMe(aboutMe: String) {
        val actualText = aboutMeElement.text
        VintedAssert.assertEquals(actualText, aboutMe, "About me should be $aboutMe but found $actualText")
    }

    @Step("Assert edit profile screen is visible")
    fun assertEditProfileScreenIsVisible() {
        VintedAssert.assertTrue(profileEditNavigationBarElement.isVisible(), "Profile layout element should be visible")
    }

    @Step("Click on Location switch ")
    fun clickOnLocationSwitch(countryTitle: String): UserProfileEditRobot {
        if (isAndroid) {
            locationViewSwitchAndroid.click()
        } else {
            val x = myLocationCellSubtitle(countryTitle).withScrollIos().center.getX() + 70
            val y = myLocationCellSubtitle(countryTitle).center.getY() + 54
            IOS.tap(x, y)
        }
        return this
    }

    @Step("Assert location switch value")
    fun assertLocationSwitchValue(toggleValue: ToggleValue): UserProfileEditRobot {
        if (isAndroid) {
            val isChecked = locationViewSwitchAndroid.isElementChecked()
            VintedAssert.assertEquals(isChecked, toggleValue.value, "Location switch value should be $toggleValue")
        }
        return this
    }

    @Step("Assert location subtitle contains city")
    fun assertLocationSubtitleContainsCity(countryTitle: String): UserProfileEditRobot {
        val text = myLocationCellSubtitle(countryTitle).withScrollIos().text
        val expected = "$cityNameText, $countryTitle"
        VintedAssert.assertTrue(text.contains(expected), "Location should contain $expected, but was: $text")
        return this
    }

    @Step("Click on My Location ")
    fun clickOnMyLocation(): UserProfileEditRobot {
        myLocationSection.withScrollIos().tapWithRetry()
        return this
    }

    @Step("Select first country {countryTitle}")
    fun selectCountryInLocation(countryTitle: String) {
        countryElement(countryTitle).withScrollIos().click()
    }

    @Step("Send city keys for location")
    fun sendLocationCity(): UserProfileEditRobot {
        locationCityInputValue.sendKeys(cityNameText)
        return this
    }

    @Step("Select first city")
    fun selectCityInLocation() {
        cityElement.click()
    }
}

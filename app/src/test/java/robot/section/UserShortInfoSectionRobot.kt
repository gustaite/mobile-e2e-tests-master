package robot.section

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import org.openqa.selenium.NoSuchElementException
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor

class UserShortInfoSectionRobot : BaseRobot() {

    private val ratingViewElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId("rating_view"),
            VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_global_rating_stars")}'")
        )

    private val shortUserInfoSection: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_short_info_cell"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name == 'user_profile'`]")
        )

    @Step("Assert that user short info is visible")
    fun assertShortUserSectionIsVisible(): UserShortInfoSectionRobot {
        VintedAssert.assertTrue(shortUserInfoSection.withWait(seconds = 10).isVisible(10), "Short user info should be visible")
        return this
    }

    @Step("Click on user info")
    fun clickOnUserInfo() {
        shortUserInfoSection.tap()
    }

    @Step("Assert that rating is visible")
    fun assertRatingIsVisible(): UserShortInfoSectionRobot {
        VintedAssert.assertTrue(ratingViewElement.isVisible(), "Rating should be visible")
        return this
    }

    @Step("Assert that 1 feedback is visible in short info section")
    fun assertOneFeedbackVisible(): UserShortInfoSectionRobot {
        val actualText = ratingViewElement.withWait(WaitFor.Visible).text
        if (EnvironmentManager.isAndroid) {
            VintedAssert.assertTrue(
                actualText.contains("""^(1 \S+)""".toRegex()),
                "Expected text pattern is: 1 <any letters>, but is: $actualText"
            )
        } else {
            val expectedText = "1 ${IOS.getElementValue("rating_star_reviews_one")}"
            VintedAssert.assertTrue(
                actualText.contains(expectedText, true),
                "Expected text: \"$expectedText\" actual: \"$actualText\" (ignore case)"
            )
        }
        return this
    }

    @Step("Click on feedback section")
    fun clickFeedbackSection() {
        ratingViewElement.click()
    }

    @Step("Assert username is {expectedUsername}")
    fun assertUsername(expectedUsername: String): UserShortInfoSectionRobot {
        try {
            VintedDriver.findElement(
                VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_cell_title\").text(\"$expectedUsername\")"),
                VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value == '$expectedUsername'")
            )
        } catch (e: NoSuchElementException) {
            VintedAssert.fail("Element with username '$expectedUsername' was not found", e)
        }
        return this
    }
}

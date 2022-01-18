package robot.profile.tabs

import RobotFactory.userProfileRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.FollowingRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.driver.*
import util.image.ImageFactory
import util.image.ImageRecognition
import util.values.ElementByLanguage.Companion.getElementValueByPlatform
import commonUtil.testng.config.PortalFactory
import util.EnvironmentManager.isiOS

class UserInfoVerificationSection(private val tab: UserProfileVerificationInfoTabs) : BaseRobot() {
    private val cityNameText get() = portal.country.cities.popular.title

    private fun cityAndCountryToggleBasedText(countryTile: String): String = "$cityNameText, $countryTile"

    private val userEmailPhoneVerificationTextElement: VintedElement
        get() {
            val android = if (tab == UserProfileVerificationInfoTabs.Closet) VintedBy.id("user_closet_verifications_text")
            else VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}user_profile_email_confirmed\").childSelector(UiSelector().className(\"android.widget.TextView\"))")
            val iosNsPredicateString = "name CONTAINS '${IOS.getElementValue("user_profile_email_verified")}'"
            return VintedDriver.findElement(
                android,
                VintedBy.iOSNsPredicateString(iosNsPredicateString)
            )
        }

    private val locationElement: VintedElement
        get() {
            val androidId = if (tab == UserProfileVerificationInfoTabs.Closet) "user_closet_location_text" else "user_profile_location_text"
            return VintedDriver.findElement(
                VintedBy.id(androidId),
                VintedBy.iOSNsPredicateString("value CONTAINS '$cityNameText' || name CONTAINS '$cityNameText' || label CONTAINS '$cityNameText'")
            )
        }

    val followsTextElement: VintedElement
        get() {
            val androidId = if (tab == UserProfileVerificationInfoTabs.Closet) "user_closet_follows_text" else "user_profile_follow_text"
            val aboutTabOldPredicate = "(type == 'XCUIElementTypeButton' && ((name CONTAINS '${IOS.getElementValue("user_followers_count_one")}' OR name CONTAINS '${IOS.getElementValue("user_followers_count_few")}' " +
                "OR value CONTAINS '${IOS.getElementValue("user_followers_count_few")}') && (name != '${IOS.getElementValue("user_followers_count_one")}' " +
                "&& value != '${IOS.getElementValue("user_followers_count_few")}')))"
            val predicateStringSelector = "name MATCHES 'Followers: . following, . followed' || name MATCHES 'Followers: . followers, . following' || $aboutTabOldPredicate || name CONTAINS 'followers and follows'"
            return VintedDriver.findElement(
                VintedBy.id(androidId),
                VintedBy.iOSNsPredicateString(predicateStringSelector)
            )
        }

    @Step("Assert that expected username is visible")
    fun assertUsername(expectedUsername: String) {
        VintedAssert.assertTrue(
            VintedDriver.findElementByText(expectedUsername).isVisible(15),
            "Username should be $expectedUsername"
        )
    }

    @Step("Assert that email is verified")
    fun assertEmailIsVerified(): UserInfoVerificationSection {
        var expectedText = if (isAndroid && PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL) && tab == UserProfileVerificationInfoTabs.Closet) "Adres mailowy"
        else getElementValueByPlatform(key = "user_profile_email_verified")
        val actualText = userEmailPhoneVerificationTextElement.text.trim().replace("Member verifications ", "")
        expectedText = if (expectedText.endsWith(32.toChar())) expectedText.replace(32.toChar().toString(), "") else expectedText
        VintedAssert.assertEquals(
            actualText, expectedText,
            "Email should be verified. Expected: $expectedText , actual: $actualText"
        )

        return this
    }

    @Step("Assert city is visible")
    fun assertCityIsVisible(countryTitle: String): UserInfoVerificationSection {
        val text = locationElement.text
        val expectedCityAndCountryText = cityAndCountryToggleBasedText(countryTile = countryTitle)
        VintedAssert.assertTrue(
            text.contains(expectedCityAndCountryText),
            "Location text: $text should contain city: $expectedCityAndCountryText}"
        )
        return this
    }

    @Step("Assert city is not visible")
    fun assertCityIsNotVisible(countryTitle: String): UserInfoVerificationSection {
        if (locationElement.isVisible()) {
            val text = locationElement.text
            VintedAssert.assertFalse(
                text.contains(cityAndCountryToggleBasedText(countryTitle)),
                "Location text: $text should contain only country: ${portal.country.title}"
            )
        }
        return this
    }

    @Step("Assert that user has 1 follower and following 2 users")
    fun assert1FollowerAnd2FollowingAreVisible(): UserInfoVerificationSection {
        VintedAssert.assertTrue(followsTextElement.isVisible(), "Follows element should be visible")

        val expectedText =
            "1 ${
            getElementValueByPlatform(
                androidKey = "user_profile_followers_label",
                iosKey = "user_followers_count_one"
            )
            }, 2 " +
                getElementValueByPlatform(
                    androidKey = "user_profile_following_label",
                    iosKey = "user_following_count_other"
                )

        val expectedTextIos1 = "Followers: 2 following, 1 followed"
        val expectedTextIos2 = "Followers: 1 followers, 2 following"
        val expectedTextIos3 = "This member has 1 followers and follows 2 other members"

        val text = followsTextElement.text
        VintedAssert.assertTrue(
            text.equals(expectedText, ignoreCase = true) || text.equals(expectedTextIos1, ignoreCase = true) || text.equals(expectedTextIos2, ignoreCase = true) || text.equals(expectedTextIos3, ignoreCase = true),
            "Text: \"$text\" should be equal to: \"$expectedText\" (ignoring case) or to: \"$expectedTextIos1\" or to: \"$expectedTextIos2\" or to: \"$expectedTextIos3\""
        )
        return this
    }

    @Step("Click on followers")
    fun clickOnFollowers(): FollowingRobot {
        val y = followsTextElement.center.y
        var x = followsTextElement.location.x
        // iOS takes icon into same element
        if (tab == UserProfileVerificationInfoTabs.Closet && isiOS) {
            x = 44
        }
        VintedDriver.tap(x, y)
        return FollowingRobot()
    }

    @Step("Click on following users")
    fun clickOnFollowing(isOnClosetTab: Boolean): FollowingRobot {
        VintedAssert.assertTrue(followsTextElement.isVisible(), "Follows text element should be visible")
        var x = followsTextElement.location.x
        val threshold = if (isAndroid) 0.38 else 0.5
        val (isInImage, result) = ImageRecognition.isImageInElement(followsTextElement, ImageFactory.NUMBER_2, threshold = threshold)

        val y = followsTextElement.center.y

        if (isInImage) {
            x += result!!.rect.point.x
        } else {
            VintedAssert.fail("Was looking for 2 following and didn't find")
        }
        commonUtil.reporting.Report.addMessage("x: $x\ny: $y")

        Android.tap(x, y)
        iOSClickOnFollowing(isOnClosetTab, y)

        return FollowingRobot()
    }

    @Step("Click on following (only IOS)")
    private fun iOSClickOnFollowing(isOnClosetTab: Boolean, y: Int) {
        if (EnvironmentManager.isiOS) {
            val startX: Int
            val endX: Int
            followsTextElement.let {
                startX = it.location.x
                endX = it.size.width - 100
            }

            loop@ for (point in endX downTo startX step 7) {
                if (FollowingRobot().isAtleastOneFollowingButtonVisible()) {
                    break@loop // Break loop if Following screen is opened
                }

                IOS.tap(point, y)
                if (isOnClosetTab && userProfileRobot.isVerifiedInfoVisible()) {
                    userProfileRobot.openClosetTab() // Open Closet tab is other tab is currently open
                }
            }
        }
    }
}

enum class UserProfileVerificationInfoTabs {
    Closet,
    About
}

package robot.profile.tabs

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.userProfileRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.Image
import io.qameta.allure.Step
import robot.BaseRobot
import robot.NavigationRobot
import robot.inbox.NewMessageRobot
import robot.profile.SellerPoliciesRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import util.image.ImageRecognition
import util.values.ElementByLanguage.Companion.daysText
import util.values.ElementByLanguage.Companion.hoursText
import util.values.ElementByLanguage.Companion.justNowText
import util.values.ElementByLanguage.Companion.minutesText
import util.values.ElementByLanguage.Companion.yesterdayText

class ProfileAboutTabRobot : BaseRobot() {

    private val newMessageButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_profile_send_message"),
            iOSBy = VintedBy.accessibilityId("profile_message")
        )

    private val followUserButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_profile_follow"),
            iOSBy = VintedBy.accessibilityId("follow")
        )

    private val androidCompleteProfileButton: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_profile_complete_profile"))

    private val descriptionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_profile_description"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[1]/**/XCUIElementTypeOther[1]/**/XCUIElementTypeOther[2]")
        )

    private val lastLoginTextElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("user_profile_last_login_text"),
            VintedBy.iOSNsPredicateString("value CONTAINS '${IOS.getElementValue("last_login")}'")
        )

    private val avatarElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_profile_avatar"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/XCUIElementTypeOther")
        )

    private fun followersCountElement(followersCount: String): VintedElement {
        return VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}user_profile_follow_text\").textStartsWith(\"$followersCount\")"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_followers")}'")
        )
    }

    private val businessIdentity: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("business_user_verified_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'business_identity_verifier' || name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_verifications")}'")
        )

    private fun siretNumberElement(siretNumber: String): VintedElement {
        return VintedDriver.findElement(
            androidBy = VintedBy.id("business_user_siret_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'legal_code' || (name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_verifications")}' && name CONTAINS '$siretNumber')")
        )
    }

    private val proSellerPoliciesCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_policies_button"),
            iOSBy = VintedBy.accessibilityId("business_account_policies")
        )

    var userInfo: UserInfoVerificationSection = UserInfoVerificationSection(UserProfileVerificationInfoTabs.About)

    private val userProfileNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_profile_name"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/**/XCUIElementTypeStaticText")
        )

    @Step("Scroll down")
    fun scrollDown(): ProfileAboutTabRobot {
        IOS.scrollDown()
        if (isAndroid) androidCompleteProfileButton.mobileElement.tagName
        return this
    }

    @Step("Click new message button")
    fun clickNewMessageButton(): NewMessageRobot {
        newMessageButton.withScrollIos().click()
        return NewMessageRobot()
    }

    @Step("Assert last login is visible")
    fun assertLastUserLoginIsVisible() {
        val actualText = lastLoginTextElement.text
        VintedAssert.assertTrue(
            checkIfTextContainsLoginInformation(actualText),
            "Last login should contain hours, minutes or days ago. But was $actualText"
        )
    }

    private fun checkIfTextContainsLoginInformation(loginInformation: String): Boolean {
        return (
            loginInformation.contains(("([0-60])".toRegex())) &&
                ((loginInformation.contains(minutesText)) || (loginInformation.contains(hoursText) || (loginInformation.contains(daysText))))
            ) || loginInformation.contains(justNowText) || loginInformation.contains(yesterdayText) || (
            (loginInformation.contains(minutesText)) || (loginInformation.contains(hoursText)) ||
                (loginInformation.contains(daysText))
            )
    }

    @Step("Assert followers count is {beforeCount} before following user")
    fun assertFollowersCountBeforeFollow(beforeCount: String): ProfileAboutTabRobot {
        Android.scrollDown()
        if (isiOS) {
            VintedAssert.assertTrue(followersCountElement(beforeCount).text.contains(beforeCount), "User should have 0 followers")
        } else {
            VintedAssert.assertTrue(followersCountElement(beforeCount).isVisible(), "User should have 0 followers")
        }
        return this
    }

    @Step("Click follow button")
    fun clickFollowButton(): ProfileAboutTabRobot {
        followUserButton.click()
        return this
    }

    @Step("Assert followers count is {afterCount} after following user")
    fun assertFollowersCountAfterFollow(afterCount: String): UserProfileRobot {
        if (isiOS) {
            VintedAssert.assertTrue(followersCountElement(afterCount).text.contains(afterCount), "User should have 1 follower")
        } else {
            VintedAssert.assertTrue(followersCountElement(afterCount).isVisible(), "User should have 1 follower")
        }
        return userProfileRobot
    }

    @Step("Click on description")
    fun clickOnDescription() {
        descriptionElement.clickWithRetryOnException(3)
    }

    @Step("Check if profile photo has changed")
    fun assertProfilePhotoHasChanged(image: Image) {
        var retryCount = 5
        var isImageInScreen: Boolean
        do {
            deepLink.goToFeed()
            deepLink.profile.goToMyProfile()
            userProfileRobot.openAboutTab()
            isImageInScreen =
                ImageRecognition.isImageInElement(element = avatarElement, image = image, retryCount = 2, threshold = 0.45).first
            retryCount--
            sleepWithinStep(200)
        } while (isImageInScreen && retryCount > 0)

        VintedAssert.assertFalse(isImageInScreen, "Default avatar should not be visible in screen")
    }

    @Step("Assert business identity and siret number are visible")
    fun assertBusinessIdentityVerifiedAndSiretNumberAreVisible(siretNumber: String): ProfileAboutTabRobot {
        VintedAssert.assertTrue(businessIdentity.isVisible(), "Business identity verified should be visible")
        VintedAssert.assertTrue(siretNumberElement(siretNumber).isVisible(), "Siret number should be visible")
        return this
    }

    @Step("Click on seller policies cell")
    fun clickOnSellerPoliciesCell(): SellerPoliciesRobot {
        IOS.doIfiOS { scrollDown() }
        proSellerPoliciesCell.click()
        return RobotFactory.sellerPoliciesRobot
    }

    @Step("Assert username in users about profile is {username}")
    fun assertUsernameInUserAboutProfile(username: String): NavigationRobot {
        userProfileNameElement.text.let { userProfileName ->
            VintedAssert.assertEquals(
                userProfileName,
                username,
                "Username in user profile should be $username but was $userProfileName"
            )
        }
        return navigationRobot
    }
}

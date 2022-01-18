package robot.profile.tabs

import RobotFactory.bumpsPreCheckoutRobot
import RobotFactory.inAppNotificationRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileClosetWorkflowRobot
import RobotFactory.userProfileRobot
import api.controllers.item.*
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.extensions.changeSimpleSpaceToSpecial
import io.qameta.allure.Step
import robot.BaseRobot
import robot.SharingOptionsRobot
import robot.bumps.BumpsPreCheckoutRobot
import robot.inbox.NewMessageRobot
import robot.item.ItemActions
import robot.profile.FollowAction
import robot.section.UserShortInfoSectionRobot
import robot.upload.UploadItemRobot
import robot.workflow.UserProfileClosetWorkflowRobot
import util.*
import util.base.BaseTest.Companion.withItemsUser
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.IOS.ElementType.ANY
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.Wait
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.chooseValueByPlatform
import util.values.Visibility

class UserProfileClosetRobot : BaseRobot() {

    private val sendMessageButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_send_message"),
            iOSBy = VintedBy.accessibilityId("profile_message")
        )

    private val followUserButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_follow"),
            iOSBy = VintedBy.accessibilityId("follow")
        )

    private val iOSUnfollowUserButton: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("following"))

    private fun itemImageElementList(price: String): List<VintedElement> {
        return VintedDriver.findElementList(VintedBy.id("item_box_image"), VintedBy.iOSNsPredicateString("name CONTAINS '${price.changeSimpleSpaceToSpecial()}'"))
    }

    private fun itemImageElementListForSkipAuthentication(price: String): List<VintedElement> {
        return VintedDriver.findElementList(
            VintedBy.id("item_box_image"),
            VintedBy.iOSClassChain("**/XCUIElementTypeAny[`name CONTAINS '$price' AND ${IOS.predicateWithCurrencySymbolsGrouped}`]")
        )
    }

    private val androidItemPriceElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_box_title"))
    private fun itemPriceElement(price: String): VintedElement {
        return if (isAndroid) androidItemPriceElement
        else VintedDriver.findElement(iOSBy = VintedBy.iOSTextByBuilder(text = price, searchType = Util.SearchTextOperator.CONTAINS, elementType = ANY))
    }

    private val androidItemStatusElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_box_status_text"))
    private fun itemStatusElement(action: ItemActions): VintedElement {
        val translationKey = when (action) {
            ItemActions.HIDE -> "item_state_hidden"
            ItemActions.PROMOTED -> ItemActions.PROMOTED.iosTranslationKey
            else -> "reserved_item"
        }
        return VintedDriver.elementByIdAndTranslationKey({ androidItemStatusElement }, translationKey)
    }

    private val emptyClosetElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(androidBy1 = VintedBy.id("view_empty_state_icon"), androidBy2 = VintedBy.id("decorative_animation_view"))
            },
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("empty_state_title_my_items"))
        )

    private val editDraftButton: VintedElement
        get() = {
            val text = ElementByLanguage.getElementValueByPlatform(key = "edit_draft_label")
            VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true, elementType = ANY)
            )
        }()

    private val proBadgeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("pro_label"),
            iOSBy = VintedBy.accessibilityId("business_account_badge")
        )

    private val userVerificationsTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_closet_verifications"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_verifications")}'")
        )

    private val userLocationTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_closet_location_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_location")}'")
        )

    private val userEmailTextElement: VintedElement
        get() = VintedDriver.findElement(
            // email selector in user closet and user about screens
            androidBy = VintedBy.androidIdMatches(".*_email_text"),
            iOSBy = VintedBy.accessibilityId("email")
        )

    private val androidUserPhoneNumberTextElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_profile_closet_phone"))

    private val userFollowsTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_closet_follows"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_followers")}'")
        )

    val userInfo: UserInfoVerificationSection get() = UserInfoVerificationSection(UserProfileVerificationInfoTabs.Closet)
    val shortUserInfo: UserShortInfoSectionRobot get() = UserShortInfoSectionRobot()

    private val bumpItemButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentIdAndChildClassname(
                "item_box_actions",
                "android.widget.Button"
            ),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'item_box_action'")
        )

    private val profileInfoTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild("user_short_info_cell", Android.CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name == \"user_profile\"`]/**/XCUIElementTypeStaticText"),
        )

    private val proUserUsernameInfoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_short_info_user_badge"),
            iOSBy = VintedBy.iOSNsPredicateString("(value BEGINSWITH '@') && value.length > 2")
        )

    private fun closetFilterHeaderTextElement(itemsCount: String) = VintedDriver.findElement(
        androidBy = VintedBy.setWithParentAndChild("closet_filter_header", "view_cell_title"),
        iOSBy = VintedBy.iOSTextByBuilder(text = itemsCount, searchType = Util.SearchTextOperator.STARTS_WITH)
    )

    @Step("Assert closet is empty")
    fun assertClosetIsEmpty(): UserProfileClosetRobot {
        VintedAssert.assertTrue(emptyClosetElement.withScrollIos().isVisible(), "Closet should be empty")
        return this
    }

    @Step("Assert {action} label is visible")
    fun assertLabelIsVisible(action: ItemActions): UserProfileClosetRobot {
        VintedAssert.assertTrue(itemStatusElement(action).withScrollIos().isVisible(), "Item status label should be visible")
        return this
    }

    @Step("Click on send message button")
    fun clickMessageButton(): NewMessageRobot {
        sendMessageButton.click()
        return NewMessageRobot()
    }

    @Step("Click on {followAction.value} button and assert that follower number is {count}")
    fun clickOnFollowUnfollowButtonAndAssertChangesInFollowersSection(followAction: FollowAction, count: Int): UserProfileClosetRobot {
        var followButton = if (isiOS && followAction == FollowAction.Unfollow) iOSUnfollowUserButton else followUserButton
        val followUserButtonValue = followButton.text
        followButton.click()

        followButton = if (isiOS && followAction != FollowAction.Unfollow) iOSUnfollowUserButton else followUserButton

        Wait.forElementTextToChange(followButton, followUserButtonValue)

        val expectedFollowers = count.toString()
        val actualFollowersText = userInfo.followsTextElement.text.first { it.isDigit() }.toString()
        VintedAssert.assertTrue(actualFollowersText == expectedFollowers, "User followers count expected to be $expectedFollowers, but is $actualFollowersText")
        VintedAssert.assertNotEquals(followButton.text, followUserButtonValue, "${followAction.value} button should change after press it.")
        return this
    }

    @Step("Assert user's item is visible in closet")
    fun assertItemsIsVisibleInCloset(itemCount: Int, price: String) {
        Android.doIfAndroid {
            inAppNotificationRobot.closeInAppNotificationIfExists()
            userProfileRobot.openClosetTab()
            Android.scrollDownABit()
        }
        IOS.scrollDown()
        val formattedPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(price, false)

        val errorText = chooseValueByPlatform(
            androidValue = "In closet should be $itemCount item's image.",
            iosValue = "Should be $itemCount item(s) with price $formattedPrice"
        )
        VintedAssert.assertEquals(itemImageElementList(formattedPrice).size, itemCount, errorText)
        VintedAssert.assertTrue(itemPriceElement(formattedPrice).isVisible(), "Item with price $formattedPrice was not found")

        Android.doIfAndroid {
            PriceFactory.assertEquals(itemPriceElement(formattedPrice).text, formattedPrice, "Price does not match")
        }
    }

    @Step("Assert user's item is visible in closet for skip authentication")
    fun assertItemsIsVisibleInClosetForSkipAuthentication(itemCount: Int, price: String) {
        IOS.scrollDown()
        val errorText = "Should be $itemCount item(s) with price $price"
        VintedAssert.assertEquals(itemImageElementListForSkipAuthentication(price).size, itemCount, errorText)
        VintedAssert.assertTrue(bumpItemButtonElement.isVisible(), "Item bump button is not visible")
    }

    @Step("Click on first bump button")
    fun clickOnFirstBumpButton(): BumpsPreCheckoutRobot {
        if (!bumpItemButtonElement.withScrollIos().isVisible()) VintedDriver.scrollDown()
        bumpItemButtonElement.click()
        return bumpsPreCheckoutRobot
    }

    @Step("Edit draft")
    fun editDraft(): UploadItemRobot {
        editDraftButton.withScrollIos().click()
        return uploadItemRobot
    }

    @Step("Assert only {itemsCount} item(s) is(are) displayed in closet filter header")
    fun assertClosetFilterHeaderItemsCount(itemsCount: String): UserProfileClosetWorkflowRobot {
        closetFilterHeaderTextElement(itemsCount).text.let { text ->
            VintedAssert.assertTrue(
                text.startsWith(itemsCount),
                "Item count should be $itemsCount but was $text"
            )
        }
        return userProfileClosetWorkflowRobot
    }

    @Step("Assert {itemsCount} items were generated in other users closet")
    fun assertClosetItemsCountWithApi(itemsCount: Int): UserProfileClosetWorkflowRobot {
        val itemListCount = withItemsUser.getItems().size
        VintedAssert.assertEquals(itemListCount, itemsCount, "Should be $itemsCount items but was $itemListCount")
        return userProfileClosetWorkflowRobot
    }

    @Step("Check if pro badge is {visibility}")
    fun assertProBadgeVisibility(visibility: Visibility): UserProfileClosetRobot {
        VintedAssert.assertVisibilityEquals(proBadgeElement, visibility, "Pro badge should be $visibility")
        return this
    }

    @Step("Check business account elements")
    fun checkBusinessAccountInfoElements(email: String): UserProfileClosetRobot {
        val softAssert = VintedSoftAssert()
        softAssert.assertTrue(userVerificationsTextElement.isVisible(), "Verifications should be displayed")
        softAssert.assertTrue(userLocationTextElement.isVisible(), "Location should be displayed")
        softAssert.assertEquals(userEmailTextElement.text, email, "Email $email should be displayed")
        Android.doIfAndroid {
            softAssert.assertTrue(!androidUserPhoneNumberTextElement.isVisible(), "Phone should not be displayed")
        }
        softAssert.assertTrue(userFollowsTextElement.isVisible(), "Following info should be displayed")
        softAssert.assertAll()
        return this
    }

    @Step("Android only: Click on user email address")
    fun clickOnUserEmailAddressAndroid(): UserProfileClosetRobot {
        // email is not clickable on iOS simulator
        Android.doIfAndroid {
            userEmailTextElement.click()
        }
        return this
    }

    @Step("Android only: Click on user email address and assert open with options are visible")
    fun clickOnUserEmailAddressAndAssertOpenWithOptionsAreVisibleAndroid(): ProfileAboutTabRobot {
        // email is not clickable on iOS simulator
        Android.doIfAndroid {
            clickOnUserEmailAddressAndroid()
            SharingOptionsRobot().assertSharingOptionsAreVisible()
        }
        return ProfileAboutTabRobot()
    }

    @Step("Assert Profile info element contains name {name}")
    fun assertProfileInfoElementContainsName(name: String): UserProfileClosetRobot {
        profileInfoTextElement.text.let { nameInProfileInfo ->
            VintedAssert.assertTrue(
                nameInProfileInfo == name,
                "Profile info element should contain text $name but was $nameInProfileInfo"
            )
        }
        return this
    }

    @Step("Assert Profile info element contains username {username}")
    fun assertProfileInfoElementContainsUsername(username: String): UserProfileClosetRobot {
        proUserUsernameInfoElement.text.let { proUserUsername ->
            VintedAssert.assertTrue(
                proUserUsername == username,
                "Profile info element should contain text $username but was $proUserUsername"
            )
        }
        return this
    }
}

package test.basic

import RobotFactory.brandActionsRobot
import RobotFactory.bundleRobot
import RobotFactory.catalogRobot
import RobotFactory.checkoutRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.favoriteItemsRobot
import RobotFactory.feedRobot
import RobotFactory.inboxRobot
import RobotFactory.itemRobot
import RobotFactory.navigationRobot
import RobotFactory.offerWorkflowRobot
import RobotFactory.profileAboutTabRobot
import RobotFactory.profileTabRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileRobot
import RobotFactory.welcomeRobot
import RobotFactory.workflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.factories.UserFactory
import commonUtil.testng.CreateOneTestUser
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.profile.FollowAction
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile

@RunMobile(country = VintedCountry.ALL_EXCEPT_NEWLY_ADDED_TO_INTERNATIONAL_PLATFORM, platform = VintedPlatform.IOS, message = "Tests for iOS only")
@Feature("Skip authentication tests")
@TmsLink("6")
class SkipAuthenticationTests : BaseTest() {

    @BeforeMethod(description = "Create new user")
    fun beforeMethod_a_createNewUser() {
        loggedInUser = UserFactory.createRandomUser()
    }

    @Test(description = "Test if skip authentication works for profile navigation")
    fun testSkipAuthenticationForProfileNavigation() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
            .openProfileTab()
        welcomeRobot
            .assertWelcomeScreenIsVisible()
            .clickSignIn()
            .signInWithEmailOrUsername(mainUser.username, mainUser.password)
        profileTabRobot
            .assertProfileInfoElementIsVisible()
            .clickOnUserProfile()
            .assertClosetTabIsVisible()
    }

    @Test(description = "Test if skip authentication works for inbox navigation")
    fun testSkipAuthenticationForInboxNavigation() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
            .openInbox()
        welcomeRobot
            .assertWelcomeScreenIsVisible()
            .clickSignIn()
            .signInWithEmailOrUsername(mainUser.username, mainUser.password)
        inboxRobot
            .assertInboxIsVisible()
    }

    @Test(description = "Test if skip authentication works for item draft")
    fun testSkipAuthenticationForItemDraft() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
            .openSellTab()
        uploadItemRobot
            .clickAddFirstPhoto()
            .selectPhotosFromGallery()
            .enterAndAssertTitle()
            .saveDraft()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        feedRobot.waitForItemsToBeVisible()
        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .editDraft()
            .assertPhotoIsVisible()
    }

    @Test(description = "Test if skip authentication works for item upload")
    fun testSkipAuthenticationForItemUpload() {
        val price = "12"

        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
            .openSellTab()
        uploadItemRobot
            .selectDefaultValuesOnItemUploadWithPhoto(price, false)
            .clickSaveAndWait()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        feedRobot.waitForItemsToBeVisible()
        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .assertItemsIsVisibleInClosetForSkipAuthentication(1, price)
    }

    @Test(description = "Test if skip authentication works for 'Follow user' button")
    @CreateOneTestUser
    fun testSkipAuthenticationForFollowUserButton() {
        val followersCountBeforeFollow = "0"
        val followersCountAfterFollow = "1"

        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        deepLink.profile.goToUserProfile(oneTestUser.id)
        userProfileRobot
            .openAboutTab()
            .assertFollowersCountBeforeFollow(followersCountBeforeFollow)
            .clickFollowButton()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        profileAboutTabRobot
            .assertFollowersCountAfterFollow(followersCountAfterFollow)
            .openClosetTab()
            .clickOnFollowUnfollowButtonAndAssertChangesInFollowersSection(FollowAction.Unfollow, 0)
    }

    @Test(description = "Test if skip authentication works for 'Ask Seller' button")
    @CreateOneTestUser
    fun testSkipAuthenticationForAskSeller() {
        val item = ItemAPI.uploadItem(oneTestUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        deepLink.item.goToItem(item)
        itemRobot
            .clickItemMessageButtonForSkipAuthentication()
            .signUpWithNewEmailForSkipAuthentication()
        conversationRobot.assertConversationScreenIsVisible()
    }

    // ToDo it is not possible to run this test as user is not logged and app default to LT country by IP
    // Probably could be enabled back after payments will be available in LT
    /*@RunMobile(country = VintedCountry.PAYMENTS, platform = VintedPlatform.IOS)
    @CreateOneTestUser
    @Test(description = "Test if skip authentication works for 'Buy now' button")*/
    fun testSkipAuthenticationForBuyNow() {
        val item = ItemAPI.uploadItem(oneTestUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)

        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        deepLink.item.goToItem(item)
        itemRobot.clickBuyButton()
        welcomeRobot
            .assertWelcomeScreenIsVisible()
            .clickSignIn()
            .signInWithEmailOrUsername(mainUser.username, mainUser.password)
        checkoutRobot
            .assertAllPricesAreDisplayed()
            .assertItemPhotoIsVisibleInCheckout()
    }

    @Test(description = "Test if skip authentication works for favorite item button")
    fun testSkipAuthenticationForFavoriteItem() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        feedRobot
            .scrollDownIfNewHomePageIsVisible()
            .assertFirstHeartIconIsNotRed()
            .clickOnFirstHeartIcon()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        navigationRobot
            .openProfileTab()
            .openMyFavouriteItemsScreen()
        favoriteItemsRobot
            .assertHeartIconIsRed()
            .unfavoriteItemAndRefresh()
            .assertEmptyStateTitleIsVisible()
    }

    @Test(description = "Test if skip authentication works for 'Shop bundles' button")
    fun testSkipAuthenticationForShopBundles() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        offerWorkflowRobot
            .goToProfileAndCheckShopButton(withItemsUser)
            .clickShopBundles(withItemsUser)
        bundleRobot.addBundleElementsAndContinueForSkipAuthentication(2)
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        bundleRobot
            .continueToCheckoutForSkipAuthentication()
            .assertConversationScreenIsVisible()
            .assertBundleItemsCount(2)
    }

    @Test(description = "Test if skip authentication works for 'Subscribe search' button")
    fun testSkipAuthenticationForSearchSubscribe() {
        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        deepLink.catalog
            .goToAllItems()
            .subscribeSearchInCatalog()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        catalogRobot
            .assertSearchSubscribedModalIsDisplayed()
            .closeSearchSubscribedModalIfVisible()
        deepLink.catalog
            .goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .assertRecentSearchesCount(1)
            .openSearch(0)
            .unsubscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
    }

    @Test(description = "Test if skip authentication works for 'Follow brand' button")
    fun testSkipAuthenticationForFollowBrand() {
        val brand = "nike"

        welcomeRobot
            .clickSkip()
            .assertProfileTabIsVisible()
        deepLink.catalog.goToCatalogWithSearchValue(brand)
        brandActionsRobot.followBrand()
        workflowRobot.signUpWithNewEmailForSkipAuthentication()
        brandActionsRobot.assertUnfollowButtonIsVisible()
    }
}

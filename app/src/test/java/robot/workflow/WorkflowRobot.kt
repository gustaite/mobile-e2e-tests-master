package robot.workflow

import RobotFactory.brandRobot
import RobotFactory.catalogRobot
import RobotFactory.checkoutRobot
import RobotFactory.closetPromoPreCheckoutRobot
import RobotFactory.deepLink
import RobotFactory.dropOffPointSelectionRobot
import RobotFactory.feedRobot
import RobotFactory.filtersRobot
import RobotFactory.itemRobot
import RobotFactory.navigationRobot
import RobotFactory.paymentsScreenRobot
import RobotFactory.searchScreenRobot
import RobotFactory.settingsRobot
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userAccountSettingsRobot
import RobotFactory.webViewRobot
import RobotFactory.welcomeRobot
import RobotFactory.workflowRobot
import RobotFactory.favoriteItemsRobot
import api.AssertApi
import api.controllers.GlobalAPI
import api.controllers.item.getItems
import api.controllers.user.searchApi
import api.controllers.user.transactionApi
import api.controllers.user.userApi
import api.data.models.VintedItem
import api.data.models.VintedUser
import api.data.models.transaction.VintedTransaction
import api.data.models.transaction.VintedTransactionShipmentType
import api.factories.UserFactory
import api.values.mandatoryShippingCarriers
import api.values.preferredChoiceShippingCarriers
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.data.Price
import commonUtil.data.enums.VintedMandatoryShippingCarriers
import commonUtil.data.enums.VintedPreferredChoiceShippingCarriers
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.extensions.logAndReturnListSize
import io.qameta.allure.Step
import io.qameta.allure.TmsLink
import robot.BaseRobot
import robot.DropOffPointSelectionRobot
import robot.*
import robot.browse.CatalogRobot
import robot.browse.FiltersRobot
import robot.browse.SearchScreenRobot
import robot.closetpromo.ClosetPromoPreCheckoutRobot
import robot.item.ItemActions
import robot.item.ItemRobot
import robot.profile.PaymentsScreenRobot
import robot.profile.UserProfileEditRobot
import robot.profile.settings.UserAccountSettingsRobot
import robot.upload.BrandRobot
import util.Android
import util.base.BaseTest.Companion.defaultUser
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.preferredShipmentType
import util.IOS
import util.VintedDriver
import util.data.CreditCardDetails
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.driver.WebDriverFactory
import util.values.Visibility

class WorkflowRobot : BaseRobot() {

    private fun suggestionsTextElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
        )

    @Step("Create new random account and sign in")
    fun createNewAccountAndSignIn(): VintedUser {
        val user = UserFactory.createRandomUser()
        signIn(user)

        return user
    }

    @Step("Perform actions after password is changed")
    fun workflowAfterPasswordIsChanged() {
        userAccountSettingsRobot.assertAccountSettingsScreenIsVisible()
        deepLink.goToSettings()
        deepLink.goToSettings()

        settingsRobot.clickLogout()
    }

    @Step("Navigate to profile details screen")
    fun navigateToProfileDetails(): UserProfileEditRobot {
        return deepLink.goToSettings().openProfileDetails()
    }

    @Step("Navigate to account settings screen")
    fun navigateToAccountSettings(): UserAccountSettingsRobot {
        return deepLink.goToSettings().openAccountSettings()
    }

    @Step("Sign in with username {user.username} / password {user.password} User id: {user.id}")
    fun signIn(user: VintedUser): WorkflowRobot {
        deepLink.loginToAccount(user)
        loggedInUser = user
        return this
    }

    @Step("Logout from account")
    fun logoutFromAccount() {
        navigationRobot
            .openProfileTab()
            .openSettingsScreen()
            .clickLogout()
    }

    @Step("Search and select brand {selectedBrand}")
    fun openBrandSelectionAndSelectBrand(selectedBrand: String): FiltersRobot {
        filtersRobot
            .openBrandsScreen()
        searchAndSelectBrand(selectedBrand)
            .leaveBrandsScreen()
        return FiltersRobot()
    }

    @Step("Search and select brand")
    fun searchAndSelectBrand(brandTitle: String): BrandRobot {
        brandRobot
            .searchBrand(brandTitle)
            .selectBrand(brandTitle)
        return brandRobot
    }

    @Step("Open categories screen and select subcategory")
    fun openCategoriesAndSelectSubcategory(catalogTitle: String, catalogAndCategoriesList: List<String>) {
        filtersRobot
            .openCatalogsAndCategoriesScreen()
            .selectCategory(catalogTitle)
            .selectCategoryAndSubcategoryInFiltersAndBrowseScreen(catalogAndCategoriesList)
    }

    @Step("Go to browse tab and open search")
    fun goToBrowseTabAndOpenSearch(searchIndex: Int): CatalogRobot {
        deepLink.catalog.goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .openSearch(searchIndex)
        return catalogRobot
    }

    @Step("Try subscribing and unsubscribing search from recent searches screen")
    fun subscribeAndUnsubscribeSearchFromSearchScreen() {
        Android.doIfAndroid {
            searchScreenRobot.clickOnSubscribeButton()
            catalogRobot.closeSearchSubscribedModalIfVisible()
            AssertApi.assertApiResponseWithWait(
                actual = { loggedInUser.searchApi.getSubscribedSearchesList().logAndReturnListSize() },
                expected = 1,
                errorMessage = "saved searches"
            )
            searchScreenRobot.clickOnSubscribeButton()
            catalogRobot.closeSearchSubscribedModalIfVisible()
            AssertApi.assertApiResponseWithWait(
                actual = { loggedInUser.searchApi.getSubscribedSearchesList().logAndReturnListSize() },
                expected = 0,
                errorMessage = "saved searches"
            )
        }
    }

    @Step("Open section in upload and check if 2 lists or suggestions cell are displayed")
    fun checkIfSuggestionElementsAreDisplayed(
        suggestionsElementList: () -> List<VintedElement>, suggestionsElement: () -> VintedElement
    ): WorkflowRobot {
        if (isAndroid) {
            Android.closeKeyboard()
            VintedAssert.assertTrue(suggestionsElementList().size == 2, "2 lists should be visible")
        } else {
            VintedAssert.assertTrue(
                suggestionsElement().withWait(WaitFor.Visible).isVisible(),
                "Suggestions cell should be visible"
            )
        }
        return workflowRobot
    }

    @Step("Check if {searchedText} suggestions are displayed")
    fun checkIfSuggestionsAreDisplayed(searchedText: List<String>) {
        val softAssert = VintedSoftAssert()
        searchedText.forEach { suggestions ->
            softAssert.assertTrue(
                suggestionsTextElement(suggestions).isVisible(),
                "Element with text $suggestions should be displayed"
            )
        }
        softAssert.assertAll()
    }

    @Step("Search for drop off point and click on search result")
    fun searchForDropOffPointAndClickOnSearchResult(): DropOffPointSelectionRobot {
        dropOffPointSelectionRobot
            .clickOnSearchIcon()
            .clickOnSearchField()
            .insertDropOffPointSearchValue(loggedInUser.billingAddress.city)
            .clickOnDropOffPointSearchResult()
        return dropOffPointSelectionRobot
    }

    @Step("Search for drop off point and select it from the list")
    fun searchForDropOffPointAndSelectItFromList(): DropOffPointSelectionRobot {
        searchForDropOffPointAndClickOnSearchResult()
            .openDropOffPointsListIfVisible()
            .selectDropOffPointFromList()
        return dropOffPointSelectionRobot
    }

    @Step("Change shipping address to Austrian and check shipping options")
    fun changeAddressToATAndCheckCarriers() {
        loggedInUser.userApi
            .updateShippingAddress(VintedShippingAddress.AT)
        deepLink
            .goToSettings()
            .openShippingSettings()
            .assertShippingCarriersName(
                getVisibleCarriersList(
                    VintedPreferredChoiceShippingCarriers.AT,
                    VintedMandatoryShippingCarriers.AT
                )
            )
    }

    @Step("Get available carriers list")
    fun getVisibleCarriersList(
        preferredCarriers: VintedPreferredChoiceShippingCarriers = loggedInUser.preferredChoiceShippingCarriers(),
        mandatoryCarriers: VintedMandatoryShippingCarriers = loggedInUser.mandatoryShippingCarriers()
    ): List<String> {
        return preferredCarriers.carriers + mandatoryCarriers.carriers
    }

    @Step("Open closet promo how it works info screen, check if webview is visible and close it")
    fun checkClosetPromoHowItWorksScreenAndLeaveIt(): ClosetPromoPreCheckoutRobot {
        closetPromoPreCheckoutRobot.clickOnHowItWorks()
        webViewRobot.assertWebViewIsVisible()
        closetPromoPreCheckoutRobot.clickBack()
        return closetPromoPreCheckoutRobot
    }

    @Step("Get expected carrier preferences for toggle")
    fun getExpectedCarrierPreferencesForToggle(preferredChoiceCarrierId: Long): Boolean {
        return loggedInUser.userApi.getCarrierPreferences().first { it.carrierId == preferredChoiceCarrierId }.enabled
    }

    @Step("Open closet promo help modal, check if modal is displayed and close it")
    fun checkClosetPromoHelpModalAndCloseIt(): ClosetPromoPreCheckoutRobot {
        closetPromoPreCheckoutRobot
            .clickOnHelpIcon()
            .assertClosetPromoHelpModalVisible()
            .closeClosetPromoHelpModal()
        return closetPromoPreCheckoutRobot
    }

    @Step("Add new credit card in settings and check if it's added")
    @TmsLink("25691")
    fun addCreditCardAndCheckIfAdded(creditCard: CreditCardDetails.CreditCard): PaymentsScreenRobot {
        deepLink
            .goToSettings()
            .openPaymentsSettings()
            .openNewCreditCardScreen()
            .insertNewCreditCardInfo(creditCard)
            .saveCreditCardAndHandle3dsIfNeeded()
        paymentsScreenRobot
            .checkIfCreditCardDeleteButtonIsVisible()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed(
                dateMonth = creditCard.date_month,
                dateYear = creditCard.date_year
            )
        return paymentsScreenRobot
    }

    @Step("Sell item to make sure what Vinted wallet contains money")
    fun sellItemToGetMoneyInVintedWallet(
        buyer: VintedUser, seller: VintedUser, item: VintedItem
    ): VintedTransaction {
        val needToAddCreditCard: Boolean = buyer.username != defaultUser.username
        WebDriverFactory.driver.orientation // Call on driver to keep session
        // ToDo add possibility to create and complete with random shipping option
        val localPreferredShipmentType = if (preferredShipmentType == null) VintedTransactionShipmentType.labelled else preferredShipmentType
        return when (localPreferredShipmentType) {
            VintedTransactionShipmentType.labelled -> buyer.transactionApi.createAndCompleteTransactionWithLabelledShipping(
                seller = seller,
                item = item,
                addCreditCardAndShippingAddressForBuyer = needToAddCreditCard
            )
            VintedTransactionShipmentType.instructionsUntracked -> buyer.transactionApi.createAndCompleteTransactionWithUntrackedShipping(
                seller = seller,
                item = item,
                addCreditCardAndShippingAddressForBuyer = needToAddCreditCard
            )
            else -> throw NotImplementedError("Test is not supported for shipment type: ${preferredShipmentType?.name}")
        }
    }

    @Step("Select default values for item upload, save them and go to item")
    fun selectDefaultPriceShippingValuesOnItemUploadAndGoToItem(): ItemRobot {
        val price = Price.getRandomPriceInRange()

        uploadFormWorkflowRobot.selectAndAssertPriceInUploadForm(price)

        uploadItemRobot
            .selectRandomShippingOption()
            .clickSaveAndWait()

        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .assertItemsIsVisibleInCloset(1, price)

        val itemList = loggedInUser.getItems()
        deepLink.item.goToItem(itemList.first())
        return itemRobot
    }

    @Step("Sign Up as a new user workflow after skip authentication")
    fun signUpWithNewEmailForSkipAuthentication() {
        val user = UserFactory.generateUser()

        welcomeRobot
            .assertWelcomeScreenIsVisible()
            .clickSignUp()
            .signUpWithNewEmail(user)
    }

    @Step("Open browse tab and click on search field")
    fun goToBrowseTabAndClickOnSearchField(): SearchScreenRobot {
        deepLink.catalog
            .goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        return searchScreenRobot
    }

    @Step("Sleep for 2 seconds and check if keyboard is open then close it")
    fun sleepAndCheckIfKeyboardIsOpenAndCloseIt() {
        sleepWithinStep(2000)
        if (isAndroid) {
            Android.checkIfKeyboardIsOpenAndClose()
        } else {
            IOS.checkIfKeyboardIsOpenAndClose()
        }
    }

    @Step("Assert horizontal filters bar filter and size buttons visibility when scrolling up and down in catalog")
    fun assertHorizontalFiltersBarFilterAndSizeButtonsVisibilityWhenScrolling() {
        VintedDriver.scrollDown()
        catalogRobot.assertHorizontalFiltersBarFilterAndSizeButtonsVisibility(Visibility.Invisible)
        VintedDriver.scrollUpABit()
        catalogRobot.assertHorizontalFiltersBarFilterAndSizeButtonsVisibility(Visibility.Visible)
    }

    @Step("Check {item.title} actions visibility")
    fun checkItemsActionsVisibility(
        item: VintedItem,
        soldButtonVisibility: Visibility,
        swappedButtonVisibility: Visibility
    ): ItemRobot {
        deepLink.item
            .goToItem(item)
            .openItemActions()
            .assertActionButtonVisibility(action = ItemActions.SOLD, visibility = soldButtonVisibility)
            .assertActionButtonVisibility(action = ItemActions.SWAPPED, visibility = swappedButtonVisibility)
            .closeActionsModal()
        return itemRobot
    }

    @Step("Go to feed and search, send app to background for a while, go to feed and search again")
    fun goToFeedAndSearchSendAppToBackgroundAndGoToFeedAndSearch() {
        Android.doIfAndroid {
            navigationRobot.openFeedTab()
            navigationRobot.openBrowseTab()

            VintedDriver.sendAppToBackgroundAndOpenAgain()

            navigationRobot.openFeedTab()
            navigationRobot.openBrowseTab()
        }
    }

    @Step("iOS Only: Navigate to uploaded item")
    fun navigateToUploadedItem(item: VintedItem) {
        IOS.doIfiOS { sleepWithinStep(2200); deepLink.item.goToItem(item) }
    }

    @Step("Wait a bit and navigate to users profile")
    fun navigateToProfile() {
        IOS.doIfiOS { sleepWithinStep(1200) }
        deepLink.profile.goToMyProfile()
    }

    @Step("Check if main homepage blocks are displayed")
    fun checkIfMainHomepageBlocksAreDisplayed(): FeedRobot {
        feedRobot
            .assertCatalogsAreDisplayed()
            .assertBrandListIsVisible()
            .assertPopularSearchesAreDisplayed()
            .assertNewsFeedTitleIsDisplayed()
        return feedRobot
    }

    @Step("Assert if see all button in popular items block, redirects to Popular items screen")
    fun assertPopularItemsSeeAllButtonRedirect(): CatalogRobot {
        feedRobot
            .clickSeeAllInPopularItemsBlock()
            .assertPopularItemsScreenOpen()
        return CatalogRobot()
    }

    @Step("Add two items to favorites list and check if Favorite items block is shown")
    fun favoriteTwoItemsAndCheckIfFavoriteItemsBlockDisplayed(): WorkflowRobot {
        feedRobot.scrollDownToSkipHomepageElements()
        repeat(2) {
            VintedDriver.scrollDown()
            feedRobot
                .clickOnFirstHeartIcon()
            sleepWithinStep(200)
        }

        feedRobot.assertPersonalizationButtonIsVisible()

        VintedDriver.pullDownToRefresh()

        feedRobot.assertFavoriteItemsBlockIsVisible()
        return this
    }

    @Step("Assert if see all button in favorite items block, redirects to Favorite items screen")
    fun assertFavoritesSeeAllButtonRedirect(): FavoriteItemsRobot {
        feedRobot.clickSeeAllInFavoritesBlock()
        favoriteItemsRobot.assertFavoriteItemsScreenOpen()
        return FavoriteItemsRobot()
    }

    @Step("Open {catalogTitleAndPosition.first} category from homepage and check if it's selected in filters")
    fun openCategoryFromHomepageAndCheckIfRightCategoryIsSelected(catalogTitleAndPosition: Pair<String, Int>) {
        feedRobot
            .openCatalog(catalogTitleAndPosition)
            .openAllCategories()
            .openCatalogFilters()
            .assertGivenOptionIsSelected(catalogTitleAndPosition.first)
    }

    @Step("Assert categories in homepage leads to right catalog")
    fun assertEachCategoryInHomepageWorks() {
        val categoriesList = GlobalAPI.getCatalogs(loggedInUser)

        if (categoriesList.isNotEmpty()) {
            categoriesList.forEach { category ->
                openCategoryFromHomepageAndCheckIfRightCategoryIsSelected(Pair(category.title, categoriesList.indexOf(category)))
                deepLink.goToFeed()
            }
        }
    }

    @Step("Assert brand searching from feed works")
    fun assertBrandSearchingFromFeedWorks(): FeedRobot {
        val brandValue = "Nike"

        feedRobot
            .assertSearchButtonVisible()
        searchScreenRobot
            .clickOnSearchField()
            .searchFor(brandValue)
            .assertBrandBannerIsVisible()
            .assertSearchedBrandNameIsVisibleInCatalog(brandValue)
        clickBack()
        return feedRobot
    }
}

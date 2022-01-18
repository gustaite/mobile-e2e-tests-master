package util.absfeatures

import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import org.testng.SkipException
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.Session
import util.values.LaboratoryDevice
import java.lang.reflect.Method

class SkipTestController {

    @Step("Skip test when condition is met by class or test name")
    fun skipWhenConditionMetByClassOrTestName(method: Method): Throwable? {
        val exceptionByClass = skipTestWhenConditionMetByClass(method.declaringClass.name)
        val exceptionByTest = skipTestWhenConditionMetByTest(method.name)
        val exceptionByBug = skipTestsDueToPersistingBugs(method.name)

        return exceptionByClass ?: exceptionByTest ?: exceptionByBug ?: skipTestIfNameContainsWordBundle(method.name)
    }

    @Step("Class '{className}' checked for skip condition")
    private fun skipTestWhenConditionMetByClass(className: String): Throwable? {
        return when (className) {
            "test.basic.upload.UploadLuxuryItemTests" -> {
                skipTestWhenLuxuryItemCollectMorePhotosIsOFF()
            }
            "test.payments.ClosetPromoTests",
            "test.payments.BumpsTests" -> {
                skipTestWhenPortalMergeSourceFsON()
            }
            "test.basic.upload.UploadFormWebPhotoTests" -> { skipTestWhenWebPhotosModerationIsOFF() }
            "test.basic.inbox.MessageActionsTests" -> { skipTestWhenConversationMessageContextMenuRemoveIsOFF() }
            "test.basic.DelayedPublicationTests" -> { skipTestWhenDelayedPublicationAbTestIsOFF() }
            else -> null
        }
    }

    @Step("Test '{testName}' checked for skip condition")
    private fun skipTestWhenConditionMetByTest(testName: String): Throwable? {
        return when (testName) {
            "testTurnedOffCarrierIsNotVisibleInCheckout" -> {
                skipTestDueToDragonCheckout()
            }
            "testBuyingClosetPromoWithCreditCard",
            "testBuyingClosetPromoCardNotSaved",
            "testBuyingClosetPromoWithVintedWallet",
            "testIfCreditCardAddedInSettingsIsVisibleInBumpsCheckout",
            "testIfCreditCardVisibleOnClosetPromo" -> {
                skipTestWhenPortalMergeSourceFsON()
            }
            "testLeavingVintedExternalLinkModal" -> FeatureController.skipTestExternalLinkModalIsOff()
            "testBuyClosetPromoForDefaultUser" -> skipTestWhenClosetPromotionPerformanceAvailable()
            "testClosetPromoStatisticsScreen" -> skipTestWhenClosetPromotionPerformanceNotAvailable()

            "testMembersSearchIsWorking",
            "testEditingSubscribedSearchWithBrandFilter",
            "testCreatingTwoSubscribedSearches",
            "testEditingSubscribedSearchWithKeywordAndFilters" -> skipTestWhenSearchSuggestionsUiUpdatesIsON()

            "testDeepLinkNavigationToDonationsOverviewScreen" -> skipTestWhenDonationsFsIsOFF()

            "testSuspiciousPhotoInConversation", "testHideItem", "testReserveItem", "testSkipAuthenticationForItemUpload",
            "testFeedRefreshAfterSettingPersonalizationSizes", "testElementsInBumpsPreCheckoutScreen",
            "testThatCategoriesAreSuggested", "testSkipAuthenticationForItemDraft", "testItemFavoritingAndUnfavoritingFromFeed",
            "testItemFavoring", "testSkipAuthenticationForShopBundles", "testSkipAuthenticationForFavoriteItem",
            "testThatDraftConvertsToItem", "testFavoritesBlockHomepage", "testOfferFlowViewsFromBuyerSideBuyerSendsOffer",
            "testOfferFlowViewsFromBuyerSideSellerSendsOffer", "testActivatingWallet", "testNewUserHomepage",
            "testBrandBannerIsDisplayed", "testBuyingTwoBumpsAtOnce", "testBumpingOneItemWithVintedWallet",
            "testBuyingOneBumpThroughBumpBanner", "testBumpingOneItemWithPayPal", "testBumpingOneItemWithIDeal",
            "testBumpingOneItemWithBlik", "testBumpingOneItemWithDotPay", "testBumpingOneItemWithSofort",
            "testDeletePhotos", "testEditItemPrice",
            "testItemDescriptionAndMoreSection", "testFilteringItemsInUsersClosetProd", "testBrandAuthenticationFlow",
            "testIfNonPaymentCountriesDoNotHaveShippingOptions", "testSuggestionsFromDescription", "testItemUploadWithCustomBrandCreation",
            "testItemUploadWithStandardShipping", "testRearrangePicturesInUploadViewWhenUploading", "testFilteringItemsInUsersCloset",
            "testUploadABookInHomeDecor", "testB2cUserCannotUploadToBeautyCategory", "testHeavyPackageSizeSelection",
            "testEditShippingOptions", "testNoShippingOption", "testMaterialAndSizeFieldsInHomeDecor",
            "testUploadATextileInHomeDecor", "testItemUploadWithCustomShipping",
            -> skipIosTestDueToAccessibility()

            "testRefundsSecurityPolicyIsVisible" -> skipTestItemTransactionalFlowTransparencyAbTestIsOFF()
            "testCopyPaste" -> skipTestWhenConversationMessageContextMenuRemoveIsON()
            "testAddNewAddressInSettingsAndAssertItIsVisibleInCheckout",
            "testPreferredChoiceCarriersInShippingSettings" -> skipTestDueToABTestConfiguredIncorrectly()
            "testPopularItemsBlockHomepage" -> skipTestWhenDisablePopularItemsIsON()
            else -> null
        }
    }

    @Step("Test '{testName}' checked for skip condition for word 'Bundle'")
    private fun skipTestIfNameContainsWordBundle(testName: String): Throwable? {
        return if (isiOS && testName.lowercase().contains("bundle")) {
            SkipException("Skipped because this test is broken due to iOS accessibility (a11y) project")
        } else null
    }

    @Step("Test '{testName}' checked for skip condition by device")
    fun skipParticularDeviceByTestName(testName: String): Throwable? {
        return when (testName) {
            "testSignUpWithEmail",
            "testUserPasswordChanging",
            "testUserDeletion" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.MI_NOTE_10,
                    LaboratoryDevice.REDMI_NOTE_9_PRO
                )
            )

            "testRearrangePicturesInCameraView" -> skipTestOnParticularDevices(
                listOf(LaboratoryDevice.A30s, LaboratoryDevice.NOKIA_53)
            )
            "testRearrangePicturesInUploadViewWhenUploading",
            "testRearrangePicturesInUploadViewWhenEditingItem",
            "testRearrangePicturesInUploadViewWhenEditingDraft" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_4_XL, LaboratoryDevice.XPERIA_XZ3, LaboratoryDevice.A30s,
                    LaboratoryDevice.S6_EDGE,
                    LaboratoryDevice.NOKIA_53, LaboratoryDevice.PIXEL_6
                )
            )

            "testIHaveAProblemFlowSuspendTransactionTryEscalatingToSupportBuyerSideViews",
            "testIHaveAProblemFlowSuspendTransactionTryEscalatingToSupportSellerSideViews" -> skipTestOnParticularDevices(
                listOf(LaboratoryDevice.PIXEL_3_XL)
            )
            "testThatCategoriesAreSuggested" -> skipTestOnParticularDevices(
                listOf(LaboratoryDevice.A30s, LaboratoryDevice.NOKIA_53)
            )
            "testShareDialogOpensFromItem", "testShareDialog" -> skipTestOnParticularDevices(
                listOf(LaboratoryDevice.A71, LaboratoryDevice.GALAXY_NOTE_10_LITE, LaboratoryDevice.S10_PLUS)
            )

            "testElementsInBumpsPreCheckoutScreen", "testAddingAndRemovingItemInBumpsCheckout",
            "testBuyingOneBumpThroughBumpBanner", "testBumpingOneItemWithCreditCardAddedFromCheckoutAndSaved",
            "testBumpingOneItemWithCreditCardAddedFromCheckoutAndNotSaved",
            "testBumpStatisticsScreen", "testBuyingTwoBumpsAtOnce", "testBumpingOneItemWithVintedWallet",
            "testBuyingClosetPromoWithVintedWallet", "testIfCreditCardVisibleOnClosetPromo",
            "testAddNewAddressInSettingsAndAssertItIsVisibleInCheckout" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_2, LaboratoryDevice.PIXEL_3_XL,
                    LaboratoryDevice.PIXEL_3_XL_2, LaboratoryDevice.PIXEL_4_XL, LaboratoryDevice.PIXEL_5,
                    LaboratoryDevice.PIXEL_5_2, LaboratoryDevice.PIXEL_6
                )
            )
            "testInformationModalAboutCancelingCapturedPhotos" -> skipTestOnParticularDevices(
                listOf(LaboratoryDevice.A70, LaboratoryDevice.ONE_PLUS_7T_PRO, LaboratoryDevice.A50s)
            )
            // todo remove this after https://vinted.atlassian.net/browse/MARIOS-540 is fixed
            "testPreferredChoiceCarriersInShippingSettings",
            "testIfCreditCardAddedInSettingsIsVisibleInItemCheckout",
            "testIfCreditCardAddedInSettingsIsVisibleInBumpsCheckout",
            "testAddingAndNotSavingCreditCardInCheckout",
            "testAddNewCreditCard",
            "testChangingRealName",
            "testBuyingClosetPromoWithCreditCard",
            "testBuyingClosetPromoCardNotSaved",
            "testAddingAndSavingCreditCardInCheckout",
            // all TransactionsTests
            "testBuyingAnItemWithPickUpShippingMethodInSandbox",
            "testBuyingAnItemWithHomeDeliveryShippingMethodInSandbox",
            "testBuyingAnItemWithCustomShippingMethodInSandbox",
            "testBuyingAnItemWithNoShippingMethodInSandbox" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_2, LaboratoryDevice.PIXEL_3_XL,
                    LaboratoryDevice.PIXEL_3_XL_2, LaboratoryDevice.PIXEL_4_XL, LaboratoryDevice.PIXEL_5,
                    LaboratoryDevice.PIXEL_5_2, LaboratoryDevice.PIXEL_6
                ),
                "Due to issue: https://vinted.atlassian.net/browse/MARIOS-540"
            )

            "testInAppNotificationLeadsToConversation" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.S10e, LaboratoryDevice.A90_5G, LaboratoryDevice.S20_FE
                )
            )

            "testPushNotificationLeadsToConversation" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.A70, LaboratoryDevice.A72, LaboratoryDevice.A72_2, LaboratoryDevice.A72_3,
                    LaboratoryDevice.S21, LaboratoryDevice.S21_2, LaboratoryDevice.S21_3, LaboratoryDevice.S21_4,
                    LaboratoryDevice.S21_5, LaboratoryDevice.S21_6, LaboratoryDevice.S21_7, LaboratoryDevice.S21_8,
                    LaboratoryDevice.S21_ULTRA, LaboratoryDevice.MOTOROLA_ONE_EDGE
                )
            )

            "testUnreadMessageBubbleDisappearsAfterReadingMessage" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.A90_5G, LaboratoryDevice.S21, LaboratoryDevice.XPERIA_5, LaboratoryDevice.XPERIA_5_2
                )
            )

            "testAboutVerificationInformationCheck" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.NOKIA_53, LaboratoryDevice.GALAXY_NOTE_10_PLUS
                )
            )
            "testWebPhotoWarning" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_3_XL, LaboratoryDevice.PIXEL_3_XL_2, LaboratoryDevice.MI_NOTE_10
                )
            )

            "testHomeDecorFilters", "testSortingInCatalog", "testThatFiltersAndSortingButtonsAreVisibleInCatalog",
            "testHorizontalFilters" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_6, LaboratoryDevice.NOKIA_53
                )
            )
            "testClosetVerificationInformationCheck" -> skipTestOnParticularDevices(
                listOf(
                    LaboratoryDevice.PIXEL_6
                )
            )

            else -> null
        }
    }

    @Step("Skip test because of the persisting bug, which is not being fixed")
    private fun skipTestsDueToPersistingBugs(testName: String): Throwable? {
        return when (Pair(testName, portal)) {
            Pair("testPtBuyerFromEs", VintedPortal.INT),
            Pair("testSuggestionsFromDescription", VintedPortal.US),
            Pair("testIfItemIsDelayed", portal) -> SkipException("Skipped because this test is broken due to persisting issue, which is not yet fixed")
            else -> null
        }
    }

    @Step("Skip test for iOS due to accessibility (a11y) project changes")
    private fun skipIosTestDueToAccessibility(): Throwable? {
        return if (isiOS) {
            SkipException("Skipped because this test is broken due to iOS accessibility (a11y) project")
        } else null
    }

    // TODO improve test to work with dragon checkout
    @Step("Skip test because of scaled dragon checkout")
    private fun skipTestDueToDragonCheckout(): Throwable {
        return SkipException("Temporary skipped because this test is not working with dragon checkout")
    }

    @Step("Skip test when portal_merge_source FS is ON")
    private fun skipTestWhenPortalMergeSourceFsON(): Throwable? {
        return if (FeatureController.isOn(FeatureNames.PORTAL_MERGE_SOURCE)) {
            SkipException("Skipped because portal_merge_source FS is ON")
        } else null
    }

    @Step("Skip test for particular device models (only Android)")
    fun skipTestOnParticularDevices(devices: List<LaboratoryDevice>, customMessageDueToIssue: String = ""): Throwable? {
        return if (isAndroid) {
            if (devices.map { it.model }.any { model -> model == Session.sessionDetails.deviceModel }) {
                SkipException("Skipped because this test does not work on device: ${devices.joinToString { it.deviceName }} $customMessageDueToIssue")
            } else null
        } else null
    }

    @Step("Skip test When closet_promotion_performance available")
    private fun skipTestWhenClosetPromotionPerformanceAvailable(): Throwable? {
        return if (AbTestController.isClosetPromotionPerformanceAvailable()) {
            SkipException("Skipped - Closet Promo already bought")
        } else null
    }

    @Step("Skip test When closet_promotion_performance not available")
    private fun skipTestWhenClosetPromotionPerformanceNotAvailable(): Throwable? {
        return if (!AbTestController.isClosetPromotionPerformanceAvailable()) {
            SkipException("Skipped statistics for Closet Promo as they are not generated yet")
        } else null
    }

    @Step("Skip test when search_suggestions_ui_updates AB test is ON")
    private fun skipTestWhenSearchSuggestionsUiUpdatesIsON(): Throwable? {
        return if (AbTestController.isSearchSuggestionUiUpdateOn()) {
            SkipException("Skipped because AB test search_suggestions_ui_updates is ON")
        } else null
    }

    @Step("Skip test when luxury_items_collect_more_photos AB test is OFF")
    private fun skipTestWhenLuxuryItemCollectMorePhotosIsOFF(): Throwable? {
        return if (!AbTestController.isLuxuryItemsCollectMorePhotosOn()) {
            SkipException("Skipped because AB test luxury_items_collect_more_photos is OFF")
        } else null
    }

    @Step("Skip test when 'donations' FS is OFF")
    private fun skipTestWhenDonationsFsIsOFF(): Throwable? {
        return if (!AbTestController.isDonationsOn()) {
            SkipException("Skipped because FS 'donations' is OFF")
        } else null
    }

    @Step("Skip test when web photos moderation FS is OFF")
    private fun skipTestWhenWebPhotosModerationIsOFF(): Throwable? {
        return if (!AbTestController.isWebPhotoModerationOn()) {
            SkipException("Skipped because FS web photo moderation is OFF")
        } else null
    }

    @Step("Skip test when 'item_transactional_flow_transparency' Ab test is OFF")
    private fun skipTestItemTransactionalFlowTransparencyAbTestIsOFF(): Throwable? {
        return if (!AbTestController.isItemTransactionalFlowTransparencyOn()) {
            SkipException("Skipped because Ab test 'item_transactional_flow_transparency' is OFF")
        } else null
    }

    @Step("Skip test when 'conversation_message_context_menu_remove' FS is OFF")
    private fun skipTestWhenConversationMessageContextMenuRemoveIsOFF(): Throwable? {
        return if (!AbTestController.isConversationMessageContextMenuRemoveOn()) {
            SkipException("Skipped because FS 'conversation_message_context_menu_remove' is OFF")
        } else null
    }

    @Step("Skip test when 'conversation_message_context_menu_remove' FS is ON")
    private fun skipTestWhenConversationMessageContextMenuRemoveIsON(): Throwable? {
        return if (AbTestController.isConversationMessageContextMenuRemoveOn()) {
            SkipException("Skipped because FS 'conversation_message_context_menu_remove' is ON")
        } else null
    }

    @Step("Skip test when 'delayed_publication' Ab test is OFF")
    private fun skipTestWhenDelayedPublicationAbTestIsOFF(): Throwable? {
        return if (!AbTestController.isDelayedPublicationOn()) {
            SkipException("Skipped because Ab test 'delayed_publication' is OFF")
        } else null
    }

    @Step("Skip test when 'disable_popular_items' Ab test is ON")
    private fun skipTestWhenDisablePopularItemsIsON(): Throwable? {
        return if (AbTestController.isDisablePopularItemsOn()) {
            SkipException("Skipped because Ab test 'disable_popular_items' is ON")
        } else null
    }

    // TODO remove when address_form_unification will be scaled in PL
    @Step("Skip test due to AB test configured incorrectly")
    private fun skipTestDueToABTestConfiguredIncorrectly(): Throwable? {
        return if (isiOS && PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL)) {
            SkipException("Temporary skipped because AB test address_form_unification is configured incorrectly in PL on iOS")
        } else null
    }
}

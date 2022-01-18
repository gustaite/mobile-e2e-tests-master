package util.absfeatures

import api.controllers.absfeatures.VintedAbTestVariants
import api.controllers.absfeatures.getAbTestVariant
import api.controllers.absfeatures.isAbTestOn
import api.controllers.absfeatures.isFeatureOn
import api.controllers.user.userApi
import commonUtil.testng.config.PortalFactory.isCurrentRegardlessEnv
import commonUtil.data.enums.VintedPortal
import io.qameta.allure.Step
import util.EnvironmentManager.isiOS
import util.absfeatures.AbTestNames.ADDRESS_FORM_UNIFICATION
import util.absfeatures.AbTestNames.DELAYED_PUBLICATION
import util.absfeatures.AbTestNames.CHECKOUT_PHONE_NUMBER_V2
import util.absfeatures.AbTestNames.DISABLE_POPULAR_ITEMS
import util.absfeatures.AbTestNames.LUXURY_ITEMS_COLLECT_MORE_PHOTOS
import util.absfeatures.AbTestNames.SEARCH_SUGGESTIONS_REDESIGN
import util.absfeatures.AbTestNames.ZEBRA_HANDLE_3DS_DURING_CARD_ADD
import util.absfeatures.FeatureNames.DONATIONS
import util.absfeatures.FeatureNames.HANDLE_3DS_DURING_CARD_ADD
import util.absfeatures.FeatureNames.HERMES_UK_POSTABLE
import util.absfeatures.FeatureNames.INTERNATIONAL_CUSTOM_SHIPPING
import util.absfeatures.AbTestNames.ITEM_TRANSACTIONAL_FLOW_TRANSPARENCY
import util.absfeatures.AbTestNames.SHIPPING_LABEL_PHONE_NUMBER_V2
import util.absfeatures.FeatureNames.BRANDS_LOOKUP_USING_ELASTICSEARCH
import util.absfeatures.FeatureNames.CONVERSATION_MESSAGE_CONTEXT_MENU_REMOVE
import util.absfeatures.FeatureNames.DHL_NL_TO_HOME
import util.absfeatures.FeatureNames.KYC_EDUCATION
import util.absfeatures.FeatureNames.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC
import util.absfeatures.FeatureNames.SEUR_HOME_ES
import util.absfeatures.FeatureNames.SEUR_SHOP_ES
import util.absfeatures.FeatureNames.UPDATED_REFERRALS
import util.absfeatures.FeatureNames.WEB_PHOTO_MODERATION
import util.absfeatures.FeatureNames.YODEL_UK
import util.base.BaseTest.Companion.userForAbTestOrFeatureCheck

object AbTestController {

    @Step("Check if single_pudo_carrier_v1 Ab Test variant Is B or C")
    fun isSinglePudoCarrierV1BCVariants(): Boolean {
        return userForAbTestOrFeatureCheck.getAbTestVariant(AbTestNames.SINGLE_PUDO_CARRIER_V1).let { variant ->
            variant != VintedAbTestVariants.OFF && variant != VintedAbTestVariants.A
        }
    }

    @Step("Check if single_pudo_carrier_v1 Ab Test is ON")
    fun isSinglePudoCarrierIsOn(): Boolean {
        return userForAbTestOrFeatureCheck.getAbTestVariant(AbTestNames.SINGLE_PUDO_CARRIER_V1) != VintedAbTestVariants.OFF
    }

    @Step("Check if 'buyer_protection_fee_bottom_sheet' Ab Test is ON")
    fun isBuyerProtectionFeeBottomSheetOn(): Boolean {
        return userForAbTestOrFeatureCheck.getAbTestVariant(AbTestNames.BUYER_PROTECTION_FEE_BOTTOM_SHEET) != VintedAbTestVariants.OFF
    }

    @Step("Check if address_form_unification Ab Test is ON")
    fun isAddressFormUnificationIsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(ADDRESS_FORM_UNIFICATION)
    }

    @Step("Check if checkout_phone_number_collection_v2 Ab Test is ON")
    fun isCheckoutPhoneNumberOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(CHECKOUT_PHONE_NUMBER_V2)
    }

    @Step("Check if shipping_label_phone_number_collection_v2 Ab Test is ON")
    fun isShippingLabelPhoneNumberOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(SHIPPING_LABEL_PHONE_NUMBER_V2)
    }

    @Step("Check if closet_promotion_performance available")
    fun isClosetPromotionPerformanceAvailable(): Boolean {
        return userForAbTestOrFeatureCheck.userApi.getClosetPromotionPerformance().showStats
    }

    @Step("Check if search_suggestions_redesign Is ON")
    fun isSearchSuggestionUiUpdateOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(SEARCH_SUGGESTIONS_REDESIGN)
    }

    @Step("Check if international custom shipping should be visible")
    fun isInternationalShippingOn(): Boolean {
        return !isCurrentRegardlessEnv(listOf(VintedPortal.UK, VintedPortal.US, VintedPortal.CZ, VintedPortal.LT)) && userForAbTestOrFeatureCheck.isFeatureOn(INTERNATIONAL_CUSTOM_SHIPPING, true)
    }

    @Step("Check if luxury_items_collect_more_photos is ON")
    fun isLuxuryItemsCollectMorePhotosOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(LUXURY_ITEMS_COLLECT_MORE_PHOTOS)
    }

    @Step("Check if 'donations' FS is ON")
    fun isDonationsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(DONATIONS, true)
    }

    @Step("Check if 'zebra_handle_3ds_during_card_add' ab test is ON ")
    fun isHandle3dsDuringCardAddOn(): Boolean {
        val isFeatureOn = userForAbTestOrFeatureCheck.isFeatureOn(HANDLE_3DS_DURING_CARD_ADD, true)
        val isAbTestOn = userForAbTestOrFeatureCheck.isAbTestOn(ZEBRA_HANDLE_3DS_DURING_CARD_ADD)
        return isFeatureOn || isAbTestOn
    }

    @Step("Check if 'updated_referrals' FS is ON")
    fun isUpdatedReferralsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(UPDATED_REFERRALS, true)
    }

    @Step("Check if web_photo_moderation FS is ON")
    fun isWebPhotoModerationOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(WEB_PHOTO_MODERATION, true)
    }

    @Step("Check if 'brands_lookup_using_elasticsearch' FS is on")
    fun isBrandsLookupUsingElasticSearchOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(BRANDS_LOOKUP_USING_ELASTICSEARCH, true)
    }

    @Step("Check if 'android_kyc_education' or 'ios_kyc_education' FS is on")
    fun isKycEducationOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(KYC_EDUCATION, true)
    }

    @Step("Check if 'item_transactional_flow_transparency' Ab test is on")
    fun isItemTransactionalFlowTransparencyOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(ITEM_TRANSACTIONAL_FLOW_TRANSPARENCY)
    }

    @Step("Check if 'hermes_uk_postable' FS is on")
    fun isHermesUkPostableOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(HERMES_UK_POSTABLE, true)
    }

    @Step("Check if 'yodel_uk' AB is on")
    fun isYodelUkOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(YODEL_UK)
    }

    @Step("Check if 'seur_home_es'  Ab test is on")
    fun isSeurHomeEsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(SEUR_HOME_ES, true)
    }

    @Step("Check if 'seur_shop_es'  Ab test is on")
    fun isSeurShopEsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(SEUR_SHOP_ES, true)
    }

    @Step("Check if 'dhl_nl_to_home' FS test is on")
    fun isDhlNlToHome(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(DHL_NL_TO_HOME, true)
    }

    @Step("Check if 'conversation_message_context_menu_remove' FS is on")
    fun isConversationMessageContextMenuRemoveOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(CONVERSATION_MESSAGE_CONTEXT_MENU_REMOVE, true)
    }

    @Step("Check if 'nasa_shipment_transaction_status_desync' FS is on")
    fun isNasaShipmentTransactionStatusDesyncOn(): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC, true)
    }

    @Step("iOS only: Check if cp_insights_page_rework_with_adoption Ab Test is ON")
    fun isCpInsightsReworkOnIos(): Boolean {
        return isiOS && userForAbTestOrFeatureCheck.getAbTestVariant(AbTestNames.CP_INSIGHTS_REWORK).let { variant -> variant != VintedAbTestVariants.OFF && variant != VintedAbTestVariants.A }
    }

    @Step("Check if 'delayed_publication' Ab test is on")
    fun isDelayedPublicationOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(DELAYED_PUBLICATION)
    }

    @Step("Check if 'return_label_mvp' AB test is on")
    fun isReturnLabelMvpOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(AbTestNames.RETURN_LABEL_MVP)
    }

    @Step("Check if 'disable_popular_items' Ab test is on")
    fun isDisablePopularItemsOn(): Boolean {
        return userForAbTestOrFeatureCheck.isAbTestOn(DISABLE_POPULAR_ITEMS)
    }
}

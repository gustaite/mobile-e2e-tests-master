package util.absfeatures

import util.values.ElementByLanguage.Companion.chooseValueByPlatform

object FeatureNames {
    // ToDo maybe move this to FeatureName on common-api?
    // https://admin.vinted.net/features/157
    const val INTERNATIONAL_CUSTOM_SHIPPING = "international_custom_shipping"
    // https://admin.vinted.net/features/792
    val CMP_INTEGRATION get() = chooseValueByPlatform(androidValue = "sp_cmp_integration_android", iosValue = "cmp_integration_ios")
    // https://admin.vinted.net/features/1136
    const val EXTERNAL_LINK_MODAL = "external_link_modal"
    // https://admin.vinted.net/features/1177 removes VAS banners and buttons
    const val PORTAL_MERGE_SOURCE = "portal_merge_source"
    // cpm OneTrust framework for iOS and Android
    val OT_CMP_INTEGRATION get() = chooseValueByPlatform(androidValue = "ot_cmp_android", iosValue = "ot_cmp_ios")
    // https://people.vinted.net/issues/24683-psd2-sca-compliance-vinted-action-plan
    // https://vinted.atlassian.net/wiki/spaces/PF/pages/2174877980/21.10+Handle+3ds+during+add+card+Mangopay
    const val HANDLE_3DS_DURING_CARD_ADD = "handle_3ds_during_card_add"
    // https://people.vinted.net/issues/24345-let-s-take-care-of-the-world-together-donations-project
    const val DONATIONS = "donations"
    // https://people.vinted.net/issues/25226-mario-referral-program-rework-ux-design
    const val UPDATED_REFERRALS = "updated_referrals"
    // https://people.vinted.net/issues/24373-autobahn-new-convolution-neural-network-model-for-web-photos-classification
    const val WEB_PHOTO_MODERATION = "web_photo_moderation"
    // Android: https://admin.vinted.net/features/1836, iOS: https://admin.vinted.net/features/1832
    val KYC_EDUCATION get() = chooseValueByPlatform(androidValue = "android_kyc_education", iosValue = "ios_kyc_education")
    // https://people.vinted.net/issues/25385-brand-lookup-refactoring-those-dropdowns-in-upload-and-catalog-filters
    const val BRANDS_LOOKUP_USING_ELASTICSEARCH = "brands_lookup_using_elasticsearch"
    // https://vinted.atlassian.net/wiki/spaces/PF/pages/27651407927/Hermes+UK+Postable+service+shop2mailbox
    const val HERMES_UK_POSTABLE = "hermes_uk_postable"
    // https://analytics.vinted.net/ab_tests/experiments/1255
    const val YODEL_UK = "yodel_uk"
    // https://admin.vinted.net/features/1898
    const val SEUR_HOME_ES = "seur_home_es"
    // https://admin.vinted.net/features/1900
    const val SEUR_SHOP_ES = "seur_shop_es"
    // https://vinted.atlassian.net/wiki/spaces/PF/pages/27688140993/DHL+NL+Shop2Home+service+in+NL+NL+and+NL+BE+routes
    const val DHL_NL_TO_HOME = "dhl_nl_to_home"
    // https://admin.vinted.net/features/1848
    const val CONVERSATION_MESSAGE_CONTEXT_MENU_REMOVE = "conversation_message_context_menu_remove"
    // https://admin.vinted.net/features/2037
    const val NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC = "nasa_shipment_transaction_status_desync"
}

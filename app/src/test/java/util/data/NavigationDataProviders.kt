package util.data

import commonUtil.testng.config.ConfigManager.portal
import api.data.models.getTextByUserCountry
import commonUtil.data.VintedCountriesTextValue
import commonUtil.data.enums.VintedPortal
import util.Android
import util.EnvironmentManager.isAndroid
import util.IOS
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.chooseValueByPlatform
import util.values.ElementByLanguage.Companion.getElementValueByPlatform
import commonUtil.testng.config.PortalFactory
import util.AppTexts.userByCountry
import util.absfeatures.AbTestController

class NavigationDataProviders {
    companion object {
        const val PROFILE_TAB = "PROFILE_TAB"
        const val INBOX_TAB = "INBOX_TAB"
        const val FEED_TAB = "FEED_TAB"
        const val SELL_TAB = "SELL_TAB"
        const val BROWSE_TAB = "BROWSE_TAB"

        const val PROFILE_VINTED_GUIDE_TAB = "PROFILE_VINTED_GUIDE_TAB"
        const val PROFILE_MY_FAVOURITES_TAB = "PROFILE_MY_FAVOURITES_TAB"
        const val PROFILE_PEROSONALISATION_TAB = "PROFILE_PERSONALISATION_TAB"
        const val PROFILE_BALANCE_TAB = "PROFILE_BALANCE_TAB"
        const val PROFILE_MY_ORDERS_TAB = "PROFILE_MY_ORDERS_TAB"
        const val PROFILE_BUNDLE_DISCOUNTS_TAB = "PROFILE_BUNDLE_DISCOUNTS_TAB"
        const val PROFILE_FORUM_TAB = "PROFILE_FORUM_TAB"
        const val PROFILE_INVITE_FRIENDS_TAB = "PROFILE_INVITE_FRIENDS_TAB"
        const val PROFILE_HOLIDAY_MODE_TAB = "PROFILE_HOLIDAY_MODE_TAB"
        const val PROFILE_SETTINGS_TAB = "PROFILE_SETTINGS_TAB"
        const val PROFILE_PRIVACY_TAB = "PROFILE_PRIVACY_TAB"
        const val PROFILE_ABOUT_VINTED_TAB = "PROFILE_ABOUT_VINTED_TAB"
        const val HELP_CENTER_TAB = "HELP_CENTER_TAB"

        const val PROFILE_SETTINGS_PROFILE_DETAILS_TAB = "PROFILE_SETTINGS_PROFILE_DETAILS_TAB"
        const val PROFILE_SETTINGS_PAYMENTS_TAB = "PROFILE_SETTINGS_PAYMENTS_TAB"
        const val PROFILE_SETTINGS_POSTAGE_TAB = "PROFILE_SETTINGS_POSTAGE_TAB"
        const val PROFILE_SETTINGS_PUSH_NOTIFICATIONS_TAB = "PROFILE_SETTINGS_PUSH_NOTIFICATION_TAB"
        const val PROFILE_SETTINGS_EMAIL_NOTIFICATIONS_TAB = "PROFILE_SETTINGS_EMAIL_NOTIFICATIONS_TAB"
        const val PROFILE_SETTINGS_DATA_SETTINGS_TAB = "PROFILE_SETTINGS_DATA_SETTINGS_TAB"
        const val PROFILE_SETTINGS_SECURITY_TAB = "PROFILE_SETTINGS_SECURITY_TAB"

        private val profileTitleTextValues = VintedCountriesTextValue(
            fr = "Profil",
            de = "Profil",
            pl = "Profil",
            lt = "Paskyra",
            cz = "Profil",
            uk = "Profile",
            us = "Profile"
        )

        private val profileTitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = profileTitleTextValues)

        private val inboxTitleTextValues = VintedCountriesTextValue(
            fr = "Messages",
            de = "Nachrichten",
            pl = "Wiadomości",
            lt = "Žinutės",
            cz = chooseValueByPlatform(androidValue = "Zprávy", iosValue = "Oznámení"),
            uk = chooseValueByPlatform(androidValue = "Messages", iosValue = "Inbox"),
            us = chooseValueByPlatform(androidValue = "Messages", iosValue = "Inbox")
        )

        private val inboxTitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = inboxTitleTextValues)

        private val updatedReferralsScreenTitleTextValues = VintedCountriesTextValue(
            fr = "Invite tes amis",
            de = "Lade Freunde ein",
            pl = "Zaproś znajomych",
            lt = "Pakviesk draugus",
            cz = "Pozvat přátele",
            uk = "Invite friends",
            us = "Invite friends"
        )

        private val updatedReferralsScreenTitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = updatedReferralsScreenTitleTextValues)

        private val sellTitle get() = getElementValueByPlatform(androidKey = "page_title_item_create", iosKey = "add_item")

        private val vintedGuideTitle get() = getElementValueByPlatform(key = "user_menu_vinted_guide")

        private val myFavouritesTitle
            get() = if (isAndroid) {
                if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.LT)) "Pažymėti" else Android.getElementValue("bundles_list_header_favourite_items")
                    .substringBefore(" ")
            } else IOS.getElementValue("favorite_clothes")

        private val personalisationTitle
            get() = getElementValueByPlatform(
                androidKey = "user_personalisation_settings_page_title",
                iosKey = "personalize_feed_navigation_title"
            )

        private val balanceTitle get() = getElementValueByPlatform(androidKey = "invoice_page_title", iosKey = "payments_activity_title")

        private val myOrdersTitle get() = getElementValueByPlatform(androidKey = "page_title_my_orders", iosKey = "messages_my_orders_title")

        private val bundlesDiscountTitle
            get() = getElementValueByPlatform(
                androidKey = "bundles_discount_screen_title",
                iosKey = "settings_bundle_discounts_screen_title"
            )

        val forumTitle get() = getElementValueByPlatform("forum_home_title")

        val inviteFriendsTitle
            get() = if (!AbTestController.isUpdatedReferralsOn()) {
                getElementValueByPlatform(key = "referral2_title").replace("!", "")
            } else {
                updatedReferralsScreenTitleText
            }

        private val holidayModeTitle get() = getElementValueByPlatform(key = "holiday_screen_title")

        private val settingsTitle get() = getElementValueByPlatform(androidKey = "user_settings_page_title", iosKey = "settings")

        private val privacyTitle get() = getElementValueByPlatform(key = "consent_manager_manage_preferences_title")

        private val aboutVintedTitle get() = getElementValueByPlatform(androidKey = "about_page_title", iosKey = "get_to_know_links_title")

        private val helpCenterTitle get() = getElementValueByPlatform(androidKey = "help_center_general_topics_title", iosKey = "faq_all_topics")

        private val profileDetailsTitle get() = getElementValueByPlatform(androidKey = "user_profile_btn_update", iosKey = "edit_profile")

        private val paymentsTitle
            get() = if (isAndroid) {
                when (portal) {
                    VintedPortal.DE -> Android.getElementValue("item_description_payment_options")
                    else -> Android.getElementValue("settings_payment_options")
                }
            } else IOS.getElementValue("settings_payment_options")

        private val postageTitle get() = getElementValueByPlatform(key = "settings_shipping_options")

        val pushNotificationsTitle
            get() = getElementValueByPlatform(
                androidKey = "user_settings_mobile_notification_screen_title",
                iosKey = "settings_push_notifications_title"
            )

        val emailNotificationsTitle
            get() = getElementValueByPlatform(
                androidKey = "user_settings_email_notification_screen_title",
                iosKey = "settings_email_notifications_title"
            )

        private val dataSettingsTitle get() = getElementValueByPlatform(key = "data_settings_screen_title")

        private val securityTitle get() = getElementValueByPlatform(key = "settings_security")

        val addCreditCardTitle
            get() = ElementByLanguage.getElementValueByPlatform(
                androidKey = "page_title_new_credit_card",
                iosKey = "credit_card_list_title"
            )

        val activateWalletTitle get() = getElementValueByPlatform(androidKey = "setup_wallet_view_title", iosKey = "setup_wallet_view_title")

        val promoteClosetTitle
            get() = getElementValueByPlatform(
                androidKey = "page_title_promote_closet_prepare",
                iosKey = "page_title_promote_closet_prepare"
            )

        val itemPushUpMultipleSelectionPage
            get() = getElementValueByPlatform(
                androidKey = "multiple_selection_header_title",
                iosKey = "multiple_items_selection_screen_title"
            )
        val itemPushUpPageTitle
            get() = getElementValueByPlatform(
                androidKey = "page_title_item_push_up_periods",
                iosKey = "push_up_order_review_screen_name"
            )

        val phoneVerificationPageTitle get() = getElementValueByPlatform("verification_screen_confirm_phone")
        val phoneEditPageTitle get() = getElementValueByPlatform(androidKey = "security_title", iosKey = "settings_security")
        val emailChangePageTitle get() = getElementValueByPlatform("email_change_title")
        val paymentsIdentityEducationPageTitle get() = getElementValueByPlatform("kyc_education_title")
        val paymentsIdentityPageTitle get() = getElementValueByPlatform("id_proof_header_title")

        val contactSupportPageTitle get() = getElementValueByPlatform(androidKey = "contact_form_title", iosKey = "help_center_contextual_title")
        val donationsOverviewPageTitle get() = getElementValueByPlatform("donations_screen_title")
        val walletPageTitle get() = getElementValueByPlatform(androidKey = "invoice_page_title", iosKey = "payments_activity_title")
        val bundleDiscountsPageTitle get() = getElementValueByPlatform(androidKey = "bundles_discount_screen_title", iosKey = "settings_bundle_discounts_screen_title")
        val settingsShippingOptionsTitle get() = getElementValueByPlatform("settings_shipping_options")
        val customizationCategoriesSizesTitle get() = getElementValueByPlatform(androidKey = "feed_size_categories_title", iosKey = "feed_size_categories_header")
        val writeFeedbackPageTitle get() = getElementValueByPlatform(androidKey = "new_feedback_title", iosKey = "write_feedback")
        val profileReviewsPageTitle get() = getElementValueByPlatform("user_profile_tabs_reviews")
        val searchUnsubscribedModalTitle get() = getElementValueByPlatform("search_unsubscribed_alert_title")
        val cmpVendorsListPageTitle get() = getElementValueByPlatform("consent_manager_vendor_list_title")
    }

    enum class NavigationBarNavigation(val tab: String, val screenTitle: String) {
        PROFILE(PROFILE_TAB, profileTitleText),
        INBOX(INBOX_TAB, inboxTitleText),
        FEED(FEED_TAB, ""),
        SELL(SELL_TAB, sellTitle),
        BROWSE(BROWSE_TAB, "")
    }

    enum class ProfileTabNavigation(val tab: String, val screenTitle: String) {
        VINTED_GUIDE(PROFILE_VINTED_GUIDE_TAB, vintedGuideTitle),
        MY_FAVOURITES(PROFILE_MY_FAVOURITES_TAB, myFavouritesTitle),
        PERSONALISATION(PROFILE_PEROSONALISATION_TAB, personalisationTitle),
        BALANCE(PROFILE_BALANCE_TAB, balanceTitle),
        MY_ORDERS(PROFILE_MY_ORDERS_TAB, myOrdersTitle),
        BUNDLES_DISCOUNT(PROFILE_BUNDLE_DISCOUNTS_TAB, bundlesDiscountTitle),
        NEW_FORUM(PROFILE_FORUM_TAB, forumTitle),
        INVITE_FRIENDS(PROFILE_INVITE_FRIENDS_TAB, inviteFriendsTitle),
        HOLIDAY_MODE(PROFILE_HOLIDAY_MODE_TAB, holidayModeTitle),
        SETTINGS(PROFILE_SETTINGS_TAB, settingsTitle),
        PRIVACY(PROFILE_PRIVACY_TAB, privacyTitle),
        ABOUT_VINTED(PROFILE_ABOUT_VINTED_TAB, aboutVintedTitle),
        HELP_CENTER(HELP_CENTER_TAB, helpCenterTitle)
    }

    enum class SettingsTabNavigation(val tab: String, val screenTitle: String) {
        PROFILE_DETAILS(PROFILE_SETTINGS_PROFILE_DETAILS_TAB, profileDetailsTitle),
        PAYMENTS(PROFILE_SETTINGS_PAYMENTS_TAB, paymentsTitle),
        POSTAGE(PROFILE_SETTINGS_POSTAGE_TAB, postageTitle),
        PUSH_NOTIFICATIONS(PROFILE_SETTINGS_PUSH_NOTIFICATIONS_TAB, pushNotificationsTitle),
        EMAIL_NOTIFICATIONS(PROFILE_SETTINGS_EMAIL_NOTIFICATIONS_TAB, emailNotificationsTitle),
        DATA_SETTINGS(PROFILE_SETTINGS_DATA_SETTINGS_TAB, dataSettingsTitle),
        SECURITY(PROFILE_SETTINGS_SECURITY_TAB, securityTitle)
    }
}

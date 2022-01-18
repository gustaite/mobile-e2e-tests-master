package util.deepLinks

import RobotFactory.deepLink
import commonUtil.extensions.SURLEncoder
import commonUtil.extensions.encodeWithDefaultEncodingAndChangePlusSign
import io.qameta.allure.Step

class Miscellaneous {
    @Step("Open 'Donations overview' screen")
    fun goToDonationsOverview() {
        deepLink.openURL("donations_overview")
    }

    @Step("Open 'Contact support' screen")
    fun goToContactSupport(faqEntryId: Long) {
        deepLink.openURL("contact_support?faq_entry_id=$faqEntryId")
    }

    @Step("Open 'App alert' with texts")
    fun showAppAlertUsingTexts(title: String, subtitle: String, closeTitle: String) {
        val title1 = SURLEncoder.encodeWithDefaultEncodingAndChangePlusSign(title)
        val subtitle1 = SURLEncoder.encodeWithDefaultEncodingAndChangePlusSign(subtitle)
        val closeTitle1 = SURLEncoder.encodeWithDefaultEncodingAndChangePlusSign(closeTitle)
        deepLink.openURL("app_alert?title=$title1&subtitle=$subtitle1&close_title=$closeTitle1")
    }

    @Step("Open 'App alert' with keys")
    fun showAppAlertUsingKeys(titleKey: String, subtitleKey: String, closeTitleKey: String) {
        deepLink.openURL("app_alert?title_key=$titleKey&subtitle_key=$subtitleKey&close_title_key=$closeTitleKey")
    }

    @Step("Go to business account invoice instructions")
    fun goToBusinessAccountInvoiceInstructions(transactionId: Long) {
        deepLink.openURL("transaction/business_account_invoice_instructions?transaction_id$transactionId")
    }

    @Step("Load external url")
    fun loadExternalUrl(url: String) {
        deepLink.openURL("external?url$url")
    }
}

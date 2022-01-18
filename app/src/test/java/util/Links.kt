package util

import commonUtil.testng.config.ConfigManager.portal
import api.data.models.VintedUser
import api.data.models.VintedItem
import util.base.BaseTest.Companion.systemConfiguration

class Links {
    companion object {
        private val deepLinkScheme = portal.mobile.scheme
        private val portalURL = portal.url.baseUrl

        fun getItemUploadFormExternalLink(): String = "${portalURL}items/new"

        fun getItemUploadDeepLink(): String = "$deepLinkScheme://item/upload"

        fun getItemDeepLink(item: VintedItem): String = "$deepLinkScheme://item?id=${item.id}"

        fun getItemExternalLink(item: VintedItem): String {
            val itemPrefix = systemConfiguration.urlPrefixItems
            return "$portalURL$itemPrefix/${item.id}"
        }

        fun getUserProfileExternalLink(user: VintedUser): String {
            val userPrefix = systemConfiguration.urlPrefixMember
            return "$portalURL$userPrefix/${user.id}"
        }

        fun getUserProfileDeepLink(user: VintedUser): String = "$deepLinkScheme://user?id=${user.id}"

        fun getPaymentsIdentityExternalLink(): String = "${portalURL}e/payments_identity"

        fun getCatalogExternalLink(): String {
            val itemPrefix = systemConfiguration.urlPrefixItems
            return "$portalURL$itemPrefix?search_text=Gucci"
        }
    }
}

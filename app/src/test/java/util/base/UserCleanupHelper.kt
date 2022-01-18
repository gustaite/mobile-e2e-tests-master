package util.base

import api.controllers.item.deleteAllItems
import api.controllers.user.conversationApi
import api.controllers.user.personalizationApi
import api.controllers.user.searchApi
import api.controllers.user.userApi
import api.data.models.VintedUser
import commonUtil.extensions.isInitialized
import util.base.BaseTest.Companion.businessUser
import util.base.BaseTest.Companion.defaultUser
import util.base.BaseTest.Companion.loggedInUser

class UserCleanupHelper {
    fun getLoggedInUserCleanupTasks(): List<() -> Unit> {
        var loggedInUserCleanupTasks = listOf<() -> Unit>()
        val loggedInUser: VintedUser? = loggedInUser
            .takeIf { it.isInitialized() }
            .also { commonUtil.reporting.Report.addMessage("Logged in user: ${it?.username}") }
            .takeUnless {
                val skipCleanup = when (it?.username) {
                    defaultUser.username -> {
                        commonUtil.reporting.Report.addMessage("Skipped cleanup for default user")
                        true
                    }
                    businessUser?.username -> {
                        commonUtil.reporting.Report.addMessage("Skipped cleanup for business user")
                        true
                    }
                    else -> false
                }
                skipCleanup
            }

        loggedInUser?.let {
            loggedInUserCleanupTasks = listOf<() -> Unit>(
                {
                    if (!loggedInUser.skipPartCleanup) loggedInUser.deleteAllItems() else commonUtil.reporting.Report.addMessage(
                        "Items delete were skipped for user ${loggedInUser.username}"
                    )
                },
                {
                    if (!loggedInUser.skipPartCleanup) loggedInUser.conversationApi.deleteConversations() else commonUtil.reporting.Report.addMessage(
                        "Conversation delete were skipped for user ${loggedInUser.username}"
                    )
                },
                { loggedInUser.personalizationApi.updatePersonalizationSizes(emptyList()) },
                { loggedInUser.userApi.markAsReadAllNotifications() },
                { loggedInUser.userApi.removeItemsFromFavorites() },
                { loggedInUser.personalizationApi.unfavoriteAllBrands() },
                { loggedInUser.personalizationApi.unFollowFollowedUsers() },
                { loggedInUser.searchApi.deleteRecentSearches() },
                { loggedInUser.userApi.disableHolidays() }
            )
        }
        return loggedInUserCleanupTasks
    }

    fun getUserCleanupTask(user: VintedUser?, userType: String): List<() -> Unit> {
        user?.let {
            val actualUser = user.also { commonUtil.reporting.Report.addMessage("$userType user: ${it.username}") }
            return listOf {
                if (!actualUser.skipPartCleanup) actualUser.deleteAllItems() else commonUtil.reporting.Report.addMessage(
                    "Items delete were skipped for other user ${actualUser.username}"
                )
            }
        }
        return emptyList()
    }
}

package robot

import RobotFactory.browseRobot
import RobotFactory.feedRobot
import RobotFactory.forumHomeRobot
import RobotFactory.inboxRobot
import RobotFactory.navigationRobot
import RobotFactory.uploadItemRobot
import commonUtil.asserts.VintedAssert
import commonUtil.extensions.escapeApostrophe
import io.qameta.allure.Step
import robot.browse.BrowseRobot
import robot.forum.ForumHomeRobot
import robot.inbox.InboxRobot
import robot.profile.ProfileTabRobot
import robot.upload.UploadItemRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class NavigationRobot : BaseRobot() {

    private val feedElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_tab_discover"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTabBar/XCUIElementTypeButton[1]")
        )

    private val profileElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_tab_profile"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTabBar/XCUIElementTypeButton[5]")
        )

    private val inboxElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_tab_inbox"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTabBar/XCUIElementTypeButton[4]")
        )

    private val sellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_tab_add_item"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTabBar/XCUIElementTypeButton[3]")
        )

    private val browseElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_tab_browse"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTabBar/XCUIElementTypeButton[2]")
        )

    private val navigationBarElements: List<VintedElement>
        get() = VintedDriver.findElementListWithoutPolling(
            androidBy = VintedBy.id("navigation_tabs_tab_strip"),
            iOSBy = VintedBy.className("XCUIElementTypeTabBar")
        )

    private fun navigationBarNameElement(text: String): VintedElement = VintedDriver.findElement(
        androidElement = {
            Android.findAllElement(
                androidBy1 = VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}actionbar_label\").textContains(\"$text\")"),
                androidBy2 = VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}forum_actionbar_title\").textContains(\"$text\")")
            )
        },
        iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && name CONTAINS '${text.escapeApostrophe()}'")
    )

    private val backendErrorDescriptionElementIos: VintedElement
        get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId(IOS.getElementValue("backend_error_description")))

    @Step("Assert profile tab is visible")
    fun assertProfileTabIsVisible(): NavigationRobot {
        VintedAssert.assertTrue(profileElement.isVisible(), "User should see profile tab")
        return navigationRobot
    }

    @Step("Open feed tab")
    fun openFeedTab(): FeedRobot {
        feedElement.click()
        return feedRobot
    }

    @Step("Open profile tab")
    fun openProfileTab(): ProfileTabRobot {
        profileElement.click()
        return ProfileTabRobot()
    }

    @Step("Open forum from profile tab")
    fun openForum(): ForumHomeRobot {
        openProfileTab().openForumTab()
        return forumHomeRobot
    }

    @Step("Open inbox")
    fun openInbox(): InboxRobot {
        inboxElement.click()
        return inboxRobot
    }

    @Step("Open sell tab")
    fun openSellTab(): UploadItemRobot {
        sellElement.click()
        return uploadItemRobot
    }

    @Step("Open browse tab")
    fun openBrowseTab(): BrowseRobot {
        browseElement.click()
        return browseRobot
    }

    @Step("iOS only: Assert backend error is not visible")
    private fun assertBackendErrorIsInvisibleIos(): NavigationRobot {
        IOS.doIfiOS {
            VintedAssert.assertTrue(backendErrorDescriptionElementIos.isInvisible(1), "Backend error should not be visible")
        }
        return this
    }

    @Step("Assert navigation bar text is '{text}'")
    fun assertNavigationBarNameText(text: String) {
        assertBackendErrorIsInvisibleIos()
        VintedAssert.assertTrue(navigationBarNameElement(text).isVisible(), "Navigation bar name element was not found using text $text")
    }

    @Step("Assert that navigation bar is visible")
    fun assertNavigationBarIsVisible() {
        VintedAssert.assertTrue(waitForNavigationBarIsVisible(), "Navigation bar should be visible")
        VintedAssert.assertTrue(navigationBarElements.count() == 1, "Only one navigation bar should exist")
    }

    @Step("Wait for navigation to be visible")
    fun waitForNavigationBarIsVisible(): Boolean = VintedElement.isListVisible({ navigationBarElements }, waitSec = 30)
}

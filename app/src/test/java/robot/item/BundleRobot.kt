package robot.item

import RobotFactory.conversationRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.conversation.ConversationRobot
import util.Android
import util.EnvironmentManager.isAndroid
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class BundleRobot : BaseRobot() {
    private val addBundleButtons
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.androidText(Android.getElementValue("multiple_selection_select_action")),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeButton[`name == 'item_box_action'`]/XCUIElementTypeStaticText[`name CONTAINS '${IOS.getElementValue(
                    "item_footer_view_add_button"
                )}'`]"
            )
        )
    private val removeBundleButtons
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.androidText(Android.getElementValue("multiple_selection_deselect_action")),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeButton[`name == 'item_box_action'`]/XCUIElementTypeStaticText[`name CONTAINS '${IOS.getElementValue(
                    "item_footer_view_remove_button"
                )}'`]"
            )
        )

    private val continueButton get() = VintedDriver.findElement(
        androidBy = VintedBy.id("menu_one_button"),
        iOSBy = VintedBy.accessibilityId("confirm")
    )

    private val androidConfirmButton get() = VintedDriver.findElement(androidBy = VintedBy.id("bundle_preview_confirm_button"))

    private val checkMarkElements: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("item_box_checked"),
            iOSBy = VintedBy.accessibilityId("check")
        )

    // change to VintedBy.id("item_img") in 20.23.0
    private val bundleItemsImageElements: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceIdMatches(\".*img\")"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' && rect.width > 25 && rect.width < 35 && NOT (name CONTAINS 'check')")
        )

    @Step("Add elements {elementsCount} to bundle and continue")
    fun addBundleElementsAndContinue(elementsCount: Int): ConversationRobot {
        addBundleElements(elementsCount)
        continueToCheckout()
        return conversationRobot
    }

    @Step("Add elements {elementsCount} to bundle for skip authentication")
    fun addBundleElementsAndContinueForSkipAuthentication(elementsCount: Int) {
        addBundleElements(elementsCount)
        continueButton.click()
    }

    @Step("Remove elements {elementsCount} from bundle and continue")
    fun removeBundleElementsAndContinue(elementsCount: Int): ConversationRobot {
        removeBundleElements(elementsCount)
        continueToCheckout()
        return conversationRobot
    }

    @Step("Continue to checkout")
    private fun continueToCheckout() {
        if (isAndroid) {
            continueButton.click()
            androidConfirmButton.click()
        } else {
            repeat(2) { continueButton.click() }
        }
        closeModalIfVisible()
    }

    @Step("Continue to checkout for skip authentication")
    fun continueToCheckoutForSkipAuthentication(): ConversationRobot {
        continueButton.click()
        closeModalIfVisible()
        return conversationRobot
    }

    @Step("Add elements {elementsCount} to bundle")
    fun addBundleElements(elementsCount: Int): BundleRobot {
        Android.doIfAndroid {
            commonUtil.Util.retryAction(
                block = {
                    addBundleButtons.count() > 0
                },
                actions = {
                    sleepWithinStep(200)
                    Android.scrollDown()
                },
                retryCount = 5
            )
        }
        Android.doIfAndroid { VintedDriver.scrollDownABit(0.8, 0.7) }
        repeat(elementsCount) { addBundleButtons[0].click() }
        return this
    }

    @Step("Remove elements {elementsCount} from bundle")
    fun removeBundleElements(elementsCount: Int): BundleRobot {
        repeat(elementsCount) { removeBundleButtons[0].click() }
        return this
    }

    @Step("Assert {checkMarkCount} checkMarks are visible")
    fun assertCheckMarksAreVisible(checkMarkCount: Int): BundleRobot {
        if (checkMarkCount > 0) {
            VintedAssert.assertTrue(VintedElement.isListVisible({ checkMarkElements }), "Check mark elements should be visible")
            VintedAssert.assertEquals(checkMarkElements.count(), checkMarkCount, "Check mark count does not match expected")
        } else {
            VintedAssert.assertFalse(VintedElement.isListVisible({ checkMarkElements }, 1), "Check mark elements should not be visible")
        }
        return this
    }

    @Step("Assert {imagesCount} bundle items images in the bottom of screen are visible")
    fun assertBundleItemImagesAreVisible(imagesCount: Int): BundleRobot {
        if (imagesCount > 0) {
            VintedAssert.assertTrue(VintedElement.isListVisible({ bundleItemsImageElements }), "Bundle item images should be visible")
            VintedAssert.assertEquals(bundleItemsImageElements.count(), imagesCount, "Bundle item images count does not match expected")
        } else {
            VintedAssert.assertFalse(VintedElement.isListVisible({ bundleItemsImageElements }, 1), "Bundle item images should not be visible")
        }
        return this
    }

    // Seems that this modal is in testing phase currently on sandbox
    @Step("Close reservation modal if visible")
    private fun closeModalIfVisible() {
        val reserveModal = VintedDriver.findElement(
            VintedBy.id("modal_primary_button"),
            VintedBy.iOSNsPredicateString("value == 'Ok, got it.'")
        )
        if (reserveModal.isVisible()) reserveModal.click()
    }

    fun isAddBundleElementsVisible(): Boolean {
        return VintedElement.isListVisible({ addBundleButtons })
    }
}

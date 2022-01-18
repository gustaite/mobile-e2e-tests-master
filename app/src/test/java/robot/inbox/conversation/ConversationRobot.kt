package robot.inbox.conversation

import RobotFactory.actionBarRobot
import RobotFactory.contextMenuRobot
import RobotFactory.conversationRobot
import RobotFactory.conversationWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.inAppNotificationRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.Image
import commonUtil.data.enums.VintedPortal
import io.qameta.allure.Step
import org.openqa.selenium.NoSuchElementException
import robot.*
import robot.workflow.WorkflowRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.IOS.ElementType
import util.IOS.ElementType.*
import util.Session.Companion.sessionDetails
import util.driver.*
import commonUtil.extensions.escapeApostrophe
import commonUtil.testng.config.PortalFactory
import util.image.ImageFactory
import util.image.ImageRecognition
import util.values.ConversationElementTexts
import util.values.ConversationElementTexts.codeLabelText
import util.values.Visibility

class ConversationRobot : BaseRobot() {

    val conversationDetailsRobot get() = ConversationDetailsRobot()
    val offerActionsRobot get() = OfferActionsRobot()

    private val conversationDetailsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("conversation_order_details"),
            iOSBy = VintedBy.accessibilityId("info")
        )

    private val messageInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("conversation_text_input_bar")
        )

    private val addPhotoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("conversation_input_add_photo"),
            iOSBy = VintedBy.accessibilityId("camera_buttton")
        )

    private val conversationPhotoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_chat_image"),
            iOSBy = VintedBy.accessibilityId("conversation_photo")
        )

    private val conversationPhotoElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("view_chat_image"),
            iOSBy = VintedBy.accessibilityId("conversation_photo")
        )

    private val sendButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_conversation_input_submit"),
            iOSBy = VintedBy.accessibilityId("send_button")
        )

    private val autoCompleteFirstValueElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("(value BEGINSWITH '#' OR value BEGINSWITH '@') && value.length > 2"))
    private val copyButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("name == 'Copy' || name == 'Kopieren' || name == 'Kopiuj' || name == 'Kopírovat'"))
    private val pasteButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("name == 'Paste' || name == 'Einsetzen' || name == 'Wklej' || name == 'Vložit'"))

    private val conversationItemPriceElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                "message_handover_details_container",
                "view_item_message_header_body"
            )
        )

    private fun conversationItemPriceElement(containsText: String): VintedElement =
        VintedDriver.findElement(
            { conversationItemPriceElementAndroid },
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther/XCUIElementTypeStaticText[`name CONTAINS '$containsText'`]"),
            expectedText = containsText
        )

    private fun conversationItemTitleElement(title: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("view_cell_title", title),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther/XCUIElementTypeStaticText[`name == '$title'`]")
        )

    private val fakeSellerMessageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("fake_seller_message"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTable/XCUIElementTypeCell[2]")
        )

    private val leaveFeedBackButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("conversation_action_buttons"),
            iOSBy = VintedBy.accessibilityId("message_action_leave_feedback")
        )

    private val selectPackageSizeButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("conversation_action_buttons"),
            iOSBy = VintedBy.accessibilityId("message_action_confirm_package_size")
        )

    private val itemImageElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("view_item_message_header_photo"))

    private val itemBuyButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("message_header_action_primary", Android.getElementValue("transaction_btn_buy")),
            VintedBy.iOSNsPredicateString("name == 'message_action_buy'")
        )

    private val itemMakeOfferButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText("message_header_action_secondary", Android.getElementValue("transaction_btn_make_offer")),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'message_action_request_offer' || name == 'message_action_offer'")
        )

    private val itemMarkAsReservedButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableIdWithText(
                "message_header_action_secondary",
                Android.getElementValue("conversation_mark_item_reserved")
            ),
            iOSBy = VintedBy.accessibilityId("message_action_reserve")
        )

    private fun bundleItemImageElement(number: Int): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.id("view_item_message_header_photo"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name CONTAINS '$number'`]/XCUIElementTypeStaticText")
        )

    private fun bundleItemTitleElement(itemCount: Int) =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "message_handover_details",
                "view_cell_title"
            ),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name ENDSWITH '$itemCount ${IOS.getElementValue("items_count_other")}'")
        )

    private fun messageElement(messageText: String, findByBeginning: Boolean = false, elementType: ElementType = STATIC_TEXT): VintedElement {
        return if (findByBeginning) {
            val iosPredicateSearch = if (elementType == ANY) "" else "type == '${elementType.typeName}' AND"
            VintedDriver.findElement(
                VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_chat_text\").textStartsWith(\"$messageText\")"),
                VintedBy.iOSNsPredicateString("$iosPredicateSearch name BEGINSWITH '$messageText'")
            )
        } else {
            VintedDriver.findElement(
                VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_chat_text\").text(\"$messageText\")"),
                VintedBy.accessibilityId(messageText)
            )
        }
    }

    private fun messageElementList(message: String) =
        VintedDriver.findElementList(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_chat_text\").textStartsWith(\"$message\")"),
            iOSBy = VintedBy.iOSTextByBuilder(text = message, searchType = Util.SearchTextOperator.STARTS_WITH)
        )

    private val transactionCancelledElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextContains(ConversationElementTexts.transactionCancelledText),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeStaticText[`name CONTAINS '${ConversationElementTexts.transactionCancelledText}'`]")
        )
    private val refundProcessedElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(ConversationElementTexts.refundProcessedText, searchType = Util.SearchTextOperator.CONTAINS),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeStaticText[`name CONTAINS '${ConversationElementTexts.refundProcessedText}'`]")
        )
    private val transactionSuspendedElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(ConversationElementTexts.transactionSuspendedText, searchType = Util.SearchTextOperator.STARTS_WITH),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeStaticText[`name CONTAINS '${ConversationElementTexts.transactionSuspendedText}'`]")
        )
    private val transactionSubmittedToSupportElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextContains(ConversationElementTexts.transactionSubmittedToSupportText),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeStaticText[`name CONTAINS '${ConversationElementTexts.transactionSubmittedToSupportText.escapeApostrophe()}'`]")
        )
    private val transactionItemsReuploadElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(ConversationElementTexts.transactionItemsReuploadedText),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeStaticText[`name CONTAINS '${ConversationElementTexts.transactionItemsReuploadedText}'`]")
        )
    private val generateLabelButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(ConversationElementTexts.generateLabelText),
            iOSBy = VintedBy.accessibilityId("message_action_get_shipping_label")
        )
    private val downloadLabelButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = ConversationElementTexts.downloadLabelText, scroll = true),
            iOSBy = VintedBy.accessibilityId("message_action_download_shipping_label")
        )

    private val codeLabelElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("collapsible_vinted_text_view", codeLabelText),
            iOSBy = VintedBy.iOSNsPredicateString("label BEGINSWITH '$codeLabelText'")
        )

    private val reuploadItemButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(ConversationElementTexts.reuploadItemText),
            iOSBy = VintedBy.accessibilityId("message_action_reupload")
        )
    private val alreadySentButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextContains(ConversationElementTexts.alreadySentText),
            iOSBy = VintedBy.accessibilityId("message_action_cancellation_decline")
        )
    private val okButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(ConversationElementTexts.okButtonText),
            iOSBy = VintedBy.accessibilityId("message_action_cancellation_confirm")
        )

    private val trackParcelButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(ConversationElementTexts.trackParcelText),
            iOSBy = VintedBy.accessibilityId("message_action_track_shipment")
        )
    private val haveProblemButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "conversation_action_buttons",
                ConversationElementTexts.haveProblemText
            ),
            iOSBy = VintedBy.accessibilityId("message_action_i_have_issues")
        )
    private val everythingIsOkButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidClassNameAndText(
                "android.widget.Button",
                ConversationElementTexts.everythingIsOkText
            ),
            iOSBy = VintedBy.accessibilityId("message_action_all_is_good")
        )
    private val deliveryInstructionsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "conversation_action_buttons",
                ConversationElementTexts.deliveryInstructionsText
            ),
            iOSBy = VintedBy.accessibilityId("message_action_tracked_shipping_instructions")
        )
    private val goToBalanceButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "conversation_action_buttons",
                ConversationElementTexts.goToBalanceText
            ),
            iOSBy = VintedBy.accessibilityId("message_action_goto_wallet")
        )

    private val shipmentInformationLink: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "collapsible_vinted_text_view",
                ConversationElementTexts.shipmentInformationText
            ),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '${ConversationElementTexts.shipmentInformationText}'")
        )

    private val collapsibleTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("collapsible_text_container"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '${ConversationElementTexts.shipmentInformationText}'")
        )

    private val issueDetailsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidClassNameAndText("android.widget.Button", ConversationElementTexts.issueDetailsText),
            iOSBy = VintedBy.accessibilityId("message_action_view_complaint")
        )

    private val resolveIssueOrCancelAndKeepButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.androidIdAndText(
                        "conversation_action_buttons",
                        ConversationElementTexts.resolveIssueText
                    ),
                    androidBy2 = VintedBy.androidIdAndText(
                        "conversation_action_buttons",
                        ConversationElementTexts.cancelAndKeepText
                    )
                )
            },
            iOSBy = VintedBy.accessibilityId("message_action_resolve_complaint")
        )

    private val continueToRefundButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { modalOkButton },
            iOSBy = VintedBy.iOSNsPredicateString("name == '${ConversationElementTexts.continueToRefundText.escapeApostrophe()}'")
        )

    private val confirmButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { modalOkButton },
            iOSBy = VintedBy.accessibilityId(
                IOS.getElementValue("transaction_action_modal_everything_is_ok_confirm").escapeApostrophe()
            )
        )

    private val educationActionConfirmButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("education_action"))
    private val emptyStateMessagesTitle: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("message_list_empty_state_view"), iOSBy = VintedBy.accessibilityId(IOS.getElementValue("empty_state_title_messages")))
    private val emptyStateNotificationsTitle: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("autoloading_list_empty_state_view"), iOSBy = VintedBy.accessibilityId(IOS.getElementValue("empty_state_title_notifications")))

    @Step("Assert empty state messages title is visible")
    fun assertEmptyStateMessagesTitleIsVisible() {
        VintedAssert.assertTrue(emptyStateMessagesTitle.isVisible(), "Empty state messages title should be visible")
    }

    @Step("Assert empty state notifications title is visible")
    fun assertEmptyStateNotificationsTitleIsVisible() {
        VintedAssert.assertTrue(emptyStateNotificationsTitle.isVisible(), "Empty state notifications title is visible")
    }

    @Step("Open conversation details screen")
    fun openConversationDetails(): ConversationDetailsRobot {
        conversationDetailsButton.withWait(waitFor = WaitFor.Visible).withWait(WaitFor.Click).click()
        return ConversationDetailsRobot()
    }

    @Step("Assert message input visibility, it should be {visibility}")
    fun assertMessageInputVisibility(visibility: Visibility): ConversationRobot {
        VintedAssert.assertVisibilityEquals(messageInputElement, visibility, "Message input should be $visibility", waitForVisible = 45, waitForInvisible = 5)
        return this
    }

    @Step("Send message {messageText} and assert that it is visible")
    fun sendMessageAndAssertItIsVisible(messageText: String) {
        messageInputElement.click()
        messageInputElement.sendKeys(messageText)
        clickSend()

        assertMessageVisibility(messageText, Visibility.Visible)
    }

    @Step("Assert '{messageText}' message element is {visibility}")
    fun assertMessageVisibility(messageText: String, visibility: Visibility): ConversationRobot {
        VintedAssert.assertVisibilityEquals(messageElement(messageText), visibility, "Message with text: '$messageText' should be $visibility")
        return this
    }

    @Step("Click on button to add photo")
    fun clickToAddPhoto(): WorkflowRobot {
        addPhotoElement.tap()
        return WorkflowRobot()
    }

    @Step("Assert that photo is visible in the conversation")
    fun assertConversationPhotoIsVisible() {
        IOS.doIfiOS { VintedDriver.pullDownToRefresh() } // iOS needs to update screen, otherwise selector is not founded
        VintedAssert.assertTrue(conversationPhotoElement.isVisible(10), "Photo should be visible")
    }

    @Step("Type {message}, select autocomplete value and send")
    fun typeMessageSelectAutocompleteValueAndSend(message: String): ConversationRobot {
        if (isAndroid) {
            var waitForAutocomplete: Long = 300
            for (i in 1..6) {
                androidAutocomplete(message, waitForAutocompleteToAppear = waitForAutocomplete)
                val value = messageInputElement.text.trim()
                if (value != message) {
                    break
                } else {
                    messageInputElement.clear()
                    Android.closeKeyboard()
                    commonUtil.reporting.Report.addMessage("retry: #$i message: $value")
                    waitForAutocomplete += 300
                }
            }
        } else {
            messageInputElement.click().sendKeys(message)
            VintedAssert.assertTrue(
                autoCompleteFirstValueElementIos.withWait(WaitFor.Visible).isVisible(), "Hashtag/mention suggestions did not appear"
            )
            autoCompleteFirstValueElementIos.click()
        }
        clickSend()

        return ConversationRobot()
    }

    @Step("Click send")
    fun clickSend(): ConversationRobot {
        if (isAndroid && !sendButton.isVisible()) {
            Android.closeKeyboard()
        }
        sendButton.click()
        return conversationRobot
    }

    private fun androidAutocomplete(message: String, waitForAutocompleteToAppear: Long) {
        messageInputElement.withWait(seconds = 10).click()

        Android.sendKeysUsingKeyboard(message)
        sleepWithinStep(waitForAutocompleteToAppear)

        Android.tap(messageInputElement.center.getX(), messageInputElement.center.getY() - 100)
    }

    @Step("Assert that incomplete input {message} does not equal sent message")
    fun assertInputIsNotEqualToSentMessage(message: String): ConversationRobot {
        VintedAssert.assertTrue(messageElement(message, findByBeginning = true, elementType = ANY).isVisible(10), "Message element should be visible")
        val text = messageElement(message, findByBeginning = true, elementType = ANY).text
        commonUtil.reporting.Report.addMessage("Sent message: $text")
        VintedAssert.assertNotEquals(text, message, "Incomplete input '$text' does not equal sent message '$message'")

        return ConversationRobot()
    }

    @Step("Click on message by it's beginning: {message}")
    fun clickOnMessageByTextBeginning(message: String): String {
        val messageElement = messageElement(message, findByBeginning = true, elementType = ANY)
        val messageElementText = messageElement.text
        messageElement.click()
        return messageElementText
    }

    @Step("Find message: {message} and click it")
    fun clickOnMessage(message: String) {
        messageElement(message).tap()
    }

    @Step("Long press on message: {message}")
    fun longPressOnMessage(message: String): ContextMenuRobot {
        messageElement(message).withWait().performLongPress()
        return contextMenuRobot
    }

    @Step("Long press on image")
    fun longPressOnImage(): ContextMenuRobot {
        conversationPhotoElement.performLongPress()
        return contextMenuRobot
    }

    @Step("Copy the message")
    fun copyText(message: String): ConversationRobot {
        val element = messageElement(message).withWait()
        element.performLongPress()
        val x: Int
        val y: Int
        if (isAndroid) {
            when (sessionDetails.deviceManufacturer) {
                "Xiaomi" -> {
                    // On Xiaomi phones copy button is above message element so manipulation on X axis is not necessary
                    element.center.let {
                        x = it.getX()
                        y = it.getY() - 130
                    }
                }
                else -> {
                    element.center.let {
                        x = it.getX() / 2 - 10
                        y = it.getY() - 110
                    }
                }
            }
            Android.tap(x, y)
        } else {
            copyButtonIos.click()
        }
        return this
    }

    @Step("Paste the message")
    fun pasteText(): ConversationRobot {
        if (isAndroid) {
            val yCorrection = if (sessionDetails.deviceManufacturer == "Xiaomi") -115 else -110
            messageInputElement.click()
            messageInputElement.performLongPress()
            messageInputElement.center.let {
                val x = it.getX() / 5 + 20
                val y = it.getY() + yCorrection
                Android.tap(x, y)
            }
        } else {
            messageInputElement.click()
            messageInputElement.performLongPress()
            pasteButtonIos.tap()
        }
        return this
    }

    @Step("Send pasted message {message} and assert message with same text count")
    fun sendPastedTextAndAssert(message: String) {
        var list = listOf<VintedElement>()
        clickSend()
        commonUtil.Util.retryAction(
            block = {
                list = messageElementList(message)
                VintedElement.isListVisible({ list }, 25)
                list.size >= 2
            },
            actions = {
                commonUtil.reporting.Report.addMessage("Message was not pasted or sent")
                conversationWorkflowRobot.copyAndPasteMessageAccordingToConversationMessageContextMenuValue(message)
            }
        )
        VintedAssert.assertTrue(list.size >= 2, "Two or more messages with text '$message' should be visible but found ${list.size} message(s)")
    }

    @Step("Assert item price")
    fun assertItemPrice(price: String): ConversationRobot {
        val expectedPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(price)
        val actualPrice = try {
            conversationItemPriceElement(expectedPrice).text
        } catch (e: NoSuchElementException) {
            Android.scrollUpABit()
            conversationItemPriceElement(expectedPrice).text
        }
        commonUtil.reporting.Report.addMessage(
            """
                Price element value: $actualPrice
                Formatted item price: $expectedPrice
                Numeric item price from API: $price
            """.trimIndent()
        )

        PriceFactory.assertStartsWith(actualPrice, expectedPrice, "Price element value should start with $expectedPrice but was: $actualPrice")
        return this
    }

    @Step("Assert item title is {expectedTitle}")
    fun assertItemTitle(expectedTitle: String): ConversationRobot {
        conversationItemTitleElement(expectedTitle).let { titleElement ->
            Android.doIfAndroid {
                if (!titleElement.isVisible()) Android.scrollUp()
            }
            VintedAssert.assertTrue(titleElement.isVisible(10), "Item title should be visible")
        }
        val itemTitle = conversationItemTitleElement(expectedTitle).text
        VintedAssert.assertEquals(itemTitle, expectedTitle, "Item title should be: '$expectedTitle' but found '$itemTitle'")
        return this
    }

    @Step("Assert seller info message is visible")
    fun assertSellerInfoMessageIsVisible(): ConversationRobot {
        VintedAssert.assertTrue(fakeSellerMessageElement.isVisible(), "First seller info message should be visible")
        return this
    }

    @Step("Open feedback form")
    fun openFeedbackForm(): FeedbackFormRobot {
        leaveFeedBackButton.click()
        return FeedbackFormRobot()
    }

    @Step("Open select package size screen")
    fun openParcelSizeSelectionForm(): ParcelSizeRobot {
        selectPackageSizeButton.click()
        return ParcelSizeRobot()
    }

    @Step("Click on a photo")
    fun clickOnPhoto(): ConversationRobot {
        conversationPhotoElement.tap()
        return this
    }

    @Step("Assert photo thumbnail is visible")
    fun assertPhotoThumbnailIsVisible(file: Image) {
        VintedAssert.assertTrue(conversationPhotoElement.isVisible(10), "Photo element should be visible")
        val (isPhotoThumbnailVisible) = ImageRecognition.isImageInElement(
            element = conversationPhotoElement,
            image = file,
            threshold = 0.41
        )
        VintedAssert.assertTrue(isPhotoThumbnailVisible, "Photo thumbnail should be visible")
    }

    fun isConversationScreenVisible(): Boolean {
        return if (isAndroid) {
            itemImageElementAndroid.withWait().isVisible()
        } else {
            messageInputElement.withWait().isVisible()
        }
    }

    @Step("Assert item image is visible in conversation screen")
    fun assertConversationScreenIsVisible(): ConversationRobot {
        Android.doIfAndroid {
            if (!itemImageElementAndroid.withWait().isVisible(10)) {
                Android.scrollUp()
                VintedAssert.assertTrue(itemImageElementAndroid.withWait().isVisible(), "Item image should be visible")
            }
        }
        IOS.doIfiOS { assertMessageInputVisibility(Visibility.Visible) }
        return this
    }

    @Step("Assert item buy and make offer button are visible: {isVisible}")
    fun assertBuyAndMakeOfferButtonVisibility(isVisible: Boolean): ConversationRobot {
        VintedAssert.assertEquals(itemBuyButton.isVisible(), isVisible, "Buy button should be visible: $isVisible")
        VintedAssert.assertEquals(itemMakeOfferButton.isVisible(), isVisible, "Make offer button should be visible: $isVisible")
        return this
    }

    @Step("Click item buy from conversation screen")
    fun clickItemBuyButton(): CheckoutRobot {
        itemBuyButton.isVisible(1)
        Android.scrollUpABit() // Seems wait with scroll up helps to prevent issue when element below or above actual button is clicked
        itemBuyButton.click()
        return CheckoutRobot()
    }

    @Step("Click make offer from conversation screen")
    fun clickItemMakeOfferButton(): OfferRobot {
        itemMakeOfferButton.isVisible(1)
        Android.scrollUpABit() // Seems wait with scroll up helps to prevent issue when element below actual button is clicked
        itemMakeOfferButton.click()
        return OfferRobot()
    }

    @Step("Assert bundle items count {count}")
    fun assertBundleItemsCount(count: Int): ConversationRobot {
        bundleItemTitleElement(count).text.let { text ->
            VintedAssert.assertTrue(text.contains(count.toString()), "Bundle items count. Expected to contain: '$count' but text was: '$text'")
        }
        return this
    }

    @Step("Click on bundled image")
    fun clickOnBundledImage(number: Int): ConversationDetailsRobot {
        bundleItemImageElement(number).tap()
        return ConversationDetailsRobot()
    }

    @Step("Assert transaction cancelled element visibility {visibility}")
    fun assertTransactionCancelledElementVisibility(visibility: Visibility): ConversationRobot {
        VintedAssert.assertVisibilityEquals(transactionCancelledElement.withScrollIos(), visibility, "Transaction cancelled element visibility", waitForVisible = 15, waitForInvisible = 10)
        return this
    }

    @Step("Assert refund processed element visibility {visibility}")
    fun assertRefundProcessedElementVisibility(visibility: Visibility): ConversationRobot {
        Android.doIfAndroid {
            openConversationDetails()
            clickBack()
            Android.scrollDown()
        }
        VintedAssert.assertVisibilityEquals(refundProcessedElement.withScrollIos(), visibility, "Refund processed element visibility", waitForVisible = 20, waitForInvisible = 10)
        return this
    }

    @Step("Assert Re-upload button visibility {visibility}")
    fun assertTransactionItemsReuploadedElementVisibility(visibility: Visibility): ConversationRobot {
        VintedAssert.assertVisibilityEquals(transactionItemsReuploadElement, visibility, "Transaction items Re-upload button visibility")
        return this
    }

    @Step("Assert reupload item button visible and click it")
    fun assertReuploadItemButtonVisibleAndClickIt(): ConversationRobot {
        VintedAssert.assertTrue(reuploadItemButton.withScrollDownSimple().isVisible(), "Reupload item button should be visible")
        reuploadItemButton.click()
        return this
    }

    @Step("Click generate label")
    fun clickGenerateLabel(): LabelDeliveryRobot {
        assertGenerateLabelButtonIsVisible()
        generateLabelButton.click()
        return LabelDeliveryRobot()
    }

    @Step("Assert generate label button is visible")
    fun assertGenerateLabelButtonIsVisible(): LabelDeliveryRobot {
        VintedAssert.assertTrue(generateLabelButton.isVisible(), "Generate label button should be visible")
        return LabelDeliveryRobot()
    }

    @Step("Assert download label button/code label text is visible")
    fun assertDownloadLabelButtonOrCodeLabelIsVisible(waitSeconds: Long = 60) {
        if (isCodeLabel()) {
            assertCodeLabelIsVisible()
        } else {
            assertDownloadLabelButtonIsVisible(waitSeconds)
        }
    }

    @Step("Assert download label button is visible")
    private fun assertDownloadLabelButtonIsVisible(waitSeconds: Long) {
        VintedAssert.assertTrue(isDownloadLabelButtonVisible(waitSeconds), "Download label button should be visible")
    }

    @Step("Assert code label text is visible")
    private fun assertCodeLabelIsVisible() {
        VintedAssert.assertTrue(isCodeLabelTextVisible(), "Code label text should be visible")
    }

    @Step("Go to conversation and back until download label button/ code label is visible")
    fun goBackAndToConversationUntilDownloadLabelButtonOrCodeLabelIsVisible(conversationId: Long, waitSeconds: Long = 60): ConversationRobot {
        commonUtil.Util.retryUntil(
            block = {
                clickBack()
                deepLink.conversation.goToConversation(conversationId)
                isDownloadLabelButtonOrCodeLabelVisible()
            },
            tryForSeconds = waitSeconds
        )
        return this
    }

    @Step("Is download label button/ code label is visible")
    private fun isDownloadLabelButtonOrCodeLabelVisible(waitSeconds: Long = 10): Boolean {
        return if (isCodeLabel()) {
            isCodeLabelTextVisible()
        } else {
            isDownloadLabelButtonVisible(waitSeconds)
        }
    }

    @Step("Is code label text visible")
    private fun isCodeLabelTextVisible(): Boolean {
        return codeLabelElement.isVisible()
    }

    @Step("Is download label button visible")
    private fun isDownloadLabelButtonVisible(waitSeconds: Long = 10): Boolean {
        return downloadLabelButton.isVisible(waitSeconds)
    }

    @Step("Is code label")
    private fun isCodeLabel(): Boolean {
        return PortalFactory.isCurrentRegardlessEnv(listOf(VintedPortal.CZ, VintedPortal.LT))
    }

    @Step("Click already sent")
    fun clickAlreadySent(): ConversationRobot {
        VintedAssert.assertTrue(alreadySentButton.withWait(seconds = 2).isVisible(), "Already sent button should be visible")
        alreadySentButton.click()
        return this
    }

    @Step("Click ok")
    fun clickOk(): ConversationRobot {
        VintedAssert.assertTrue(okButton.withWait(seconds = 2).isVisible(), "Ok button should be visible")
        okButton.click()
        return this
    }

    @Step("Confirm education action if visible (only IOS)")
    fun confirmEducationActionIos(): ConversationRobot {
        IOS.doIfiOS {
            if (educationActionConfirmButtonIos.isVisible()) educationActionConfirmButtonIos.click()
        }
        return conversationRobot
    }

    // TODO - to remove when CZ merge leftovers are deleted. Currently can be reused in other inbox tests if they fail
    @Step("Close note if visible")
    fun goToInboxAndCloseNoteIfVisible(): ConversationRobot {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.CZ)) {
            deepLink.conversation.goToInbox()
            actionBarRobot.closeBottomSheetComponentIfVisible()
        }
        return conversationRobot
    }

    @Step("Assert mark as reserved button is visible: {visibility.value}")
    fun assertMarkAsReservedButtonVisibility(visibility: Visibility): ConversationRobot {
        VintedAssert.assertVisibilityEquals(itemMarkAsReservedButton, visibility, "Mark as reserved button should be visible: $visibility", waitForInvisible = 5)
        return this
    }

    @Step("Assert track parcel button visible and click it")
    fun assertTrackParcelButtonVisibleAndClickIt(): ShipmentTrackingRobot {
        VintedAssert.assertTrue(trackParcelButton.withScrollIos().isVisible(30), "Track parcel button should be visible")
        trackParcelButton.click()
        return ShipmentTrackingRobot()
    }

    @Step("Assert shipment information button visible and click it")
    fun assertShipmentInformationButtonVisibleAndClickIt(): ShipmentTrackingRobot {
        collapsibleTextElement.click()
        VintedAssert.assertTrue(shipmentInformationLink.isVisible(), "Shipment information button should be visible")
        if (isAndroid) {
            val (isInImage, result) = ImageRecognition.isImageInElement(shipmentInformationLink, image = ImageFactory.TRACKING_LINK, threshold = 0.2)
            var x = shipmentInformationLink.location.x
            var y = shipmentInformationLink.location.y

            if (isInImage) {
                x += result!!.rect.point.x
                y += result.rect.point.y
            } else {
                throw NullPointerException("Was looking for image and didn't find")
            }
            commonUtil.reporting.Report.addMessage("x: $x y: $y")
            Android.tap(x, y)
        } else {
            val x = haveProblemButton.center.getX()
            val y = haveProblemButton.location.getY() - 49
            commonUtil.reporting.Report.addMessage("x : $x y: $y")
            IOS.tap(x, y)
        }
        return ShipmentTrackingRobot()
    }

    @Step("Assert have problem and everything is ok buttons visibility {visibility}")
    fun assertHaveProblemAndEverythingIsOkButtonsVisibility(visibility: Visibility): ConversationRobot {
        inAppNotificationRobot.closeInAppNotificationIfExists()
        Android.scrollDown()
        VintedAssert.assertVisibilityEquals(haveProblemButton.withWait(), visibility, "Have problem button should be visible: $visibility")
        VintedAssert.assertVisibilityEquals(everythingIsOkButton, visibility, "Everything is OK button should be visible: $visibility")
        return this
    }

    @Step("Click 'Everything is OK' button")
    fun clickEverythingIsOkButton(): ConversationRobot {
        inAppNotificationRobot.closeInAppNotificationIfExists()
        everythingIsOkButton.click()
        confirmButton.click()
        return this
    }

    @Step("Assert go to balance and delivery instructions buttons visibility")
    fun assertGoToBalanceAndDeliveryInstructionsButtonsVisibility(): ConversationRobot {
        VintedAssert.assertTrue(deliveryInstructionsButton.isVisible(), "Delivery instructions button should be visible")
        VintedAssert.assertTrue(goToBalanceButton.isInvisible(), "Go to balance button should not be visible")
        return this
    }

    @Step("Click delivery instructions button")
    fun clickDeliveryInstructionsButton(): DeliveryInstructionsRobot {
        deliveryInstructionsButton.click()
        return DeliveryInstructionsRobot()
    }

    @Step("Click have a problem button")
    fun clickHaveProblemButton(): HelpCenterRobot {
        haveProblemButton.click()
        return HelpCenterRobot()
    }

    @Step("Assert 'issue details' and 'resolve issue' or 'cancel & keep' buttons are visible")
    fun assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible(): ConversationRobot {
        Android.scrollDownABit()
        VintedAssert.assertTrue(issueDetailsButton.isVisible(), "Issue details button should be visible")
        VintedAssert.assertTrue(resolveIssueOrCancelAndKeepButton.isVisible(), "'Resolve issue' or 'cancel & keep' button should be visible")
        return this
    }

    @Step("Assert transaction suspended element visible")
    fun assertTransactionSuspendedElementVisible(): ConversationRobot {
        VintedAssert.assertTrue(transactionSuspendedElement.withWait().isVisible(), "Transaction suspended element should be visible")
        return this
    }

    @Step("Assert transaction submitted to support element visible")
    fun assertTransactionSubmittedToSupportElementVisible(): ConversationRobot {
        VintedAssert.assertTrue(transactionSubmittedToSupportElement.isVisible(), "Transaction submitted to support element should be visible")
        return this
    }

    @Step("Click 'resolve issue' or 'cancel and keep'")
    fun clickResolveIssueOrCancelAndKeepButton(): ConversationRobot {
        resolveIssueOrCancelAndKeepButton.click()
        if (continueToRefundButton.isVisible(3)) continueToRefundButton.click()
        return this
    }

    @Step("Click 'view issue details'")
    fun clickViewIssueDetails(): IssueDetailsRobot {
        issueDetailsButton.click()
        return IssueDetailsRobot()
    }

    @Step("Assert {imagesCount} image(s) is(are) displayed in conversation")
    fun assertImagesInConversationCount(imagesCount: Int): ConversationRobot {
        VintedAssert.assertEquals(conversationPhotoElementList.count(), imagesCount, "Conversation images count does not match")
        return this
    }
}

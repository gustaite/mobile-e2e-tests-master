package robot.upload

import RobotFactory
import RobotFactory.brandRobot
import RobotFactory.cameraRobot
import RobotFactory.colorRobot
import RobotFactory.conditionRobot
import RobotFactory.inAppNotificationRobot
import RobotFactory.materialRobot
import RobotFactory.shippingOptionRobot
import RobotFactory.sizeRobot
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import RobotFactory.cameraAndGalleryWorkflowRobot
import commonUtil.testng.config.PortalFactory
import commonUtil.testng.config.PortalFactory.isPaymentCountry
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import io.qameta.allure.Step
import org.openqa.selenium.NoSuchElementException
import robot.BaseRobot
import robot.ModalRobot
import robot.upload.photo.CameraRobot
import robot.upload.photo.CameraAndGalleryWorkflowRobot
import robot.workflow.UploadFormWorkflowRobot
import util.*
import util.Android.Companion.scrollUntilVisibleAndroid
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.IOS.ElementType.BUTTON
import util.driver.MobileSelector
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.Wait
import util.image.ImageFactory
import util.values.ElementByLanguage
import util.values.LaboratoryDevice
import util.values.ScrollDirection
import util.values.UploadFormElementTexts.Companion.suggestedCategoryPathElementText
import util.values.UploadFormElementTexts.Companion.suggestedCategoryElementText
import util.values.UploadFormElementTexts.Companion.suggestedElementText
import util.values.Visibility

class UploadItemRobot : BaseRobot() {

    private val categoryTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                CATEGORY_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'catalog_cell'")
        )

    private val categoryValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                CATEGORY_CONTAINER_NAME_ANDROID,
                "item_form_categories_style"
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'catalog_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "upload_form_category"
                )}'`]"
            )
        )

    private val saveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiScrollable(UiSelector().scrollable(true).instance(0)).scrollIntoView(UiSelector().resourceIdMatches(\"(${Android.ID}submit).*?${Android.Scroll.SUFFIX}"),
            iOSBy = VintedBy.accessibilityId("submit_button")
        )

    private val photoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("upload_carousel_photo"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS 'open_photo'")
        )

    private val addPhotoButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("add_photo_btn"),
            iOSBy = VintedBy.accessibilityId("add_photo")
        )

    private val addFirstPhotoButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("add_photo"),
            iOSBy = VintedBy.accessibilityId("add_photo")
        )

    private val photoTipsButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("photo_tips_btn"),
            VintedBy.iOSNsPredicateString("name CONTAINS '${IOS.getElementValue("upload_photos_phototips_label_part_linkified")}'")
        )

    private val titleField: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild("input_title", Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("item_upload_title_text_field")
        )

    private val descriptionField: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                "input_description_cell",
                "input_description"
            ),
            VintedBy.className("XCUIElementTypeTextView")
        )

    private val hashtagFirstElement: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '#'"))

    private val brandTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                BRAND_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'brand_cell'")
        )

    private val brandValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                BRAND_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'brand_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "label"
                )}'`]"
            )
        )

    private val sizeTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                SIZE_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'size_cell'")
        )

    private val sizeValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                SIZE_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'size_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "upload_form_size"
                )}'`]"
            )
        )

    private val conditionTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                CONDITION_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'status_cell'")
        )

    private val conditionValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                CONDITION_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'status_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "state"
                )}'`]"
            )
        )

    private val colorTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                COLOR_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'color_cell'")
        )

    private val colorValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                COLOR_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'color_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_upload_colors_button"
                )}'`]"
            )
        )

    private val materialTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                MATERIAL_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'materials_cell'")
        )

    private val materialValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                MATERIAL_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'materials_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_editor_details_material"
                )}'`]"
            )
        )

    private val priceTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.setWithParentAndChild(
                PRICE_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'price_cell'")
        )

    private val priceValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.setWithParentAndChild(
                PRICE_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'price_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_upload_price_label"
                )}'`]"
            )
        )

    private val shippingOptionTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                SHIPPING_OPTION_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            // TODO change to accessibility id in 21.32
            VintedBy.iOSNsPredicateString("name CONTAINS 'package_size_selection_cell'")
        )

    private val shippingOptionValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                SHIPPING_OPTION_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'package_size_selection_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_upload_package_size"
                )}'`]"
            )
        )

    private val closeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_button"),
            iOSBy = VintedBy.accessibilityId("close")
        )

    private val isbnTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                ISBN_CONTAINER_NAME_ANDROID,
                Android.CELL_TITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'isbn_cell'`]/**/XCUIElementTypeStaticText[`value == '${IOS.getElementValue(
                    "item_editor_details_isbn"
                )}'`]"
            )
        )

    private val isbnValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                ISBN_CONTAINER_NAME_ANDROID,
                Android.CELL_SUBTITLE_FIELD_ID
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'isbn_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_editor_details_isbn"
                )}'`]"
            )
        )

    private val authorSectionValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                AUTHOR_CONTAINER_NAME_ANDROID,
                "item_form_book_author"
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'author_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_editor_details_book_author"
                )}'`]"
            )
        )

    private val titleSectionValueElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                TITLE_CONTAINER_NAME_ANDROID,
                "item_form_book_title"
            ),
            VintedBy.iOSClassChain(
                "**/XCUIElementTypeAny[`name == 'bookTitle_cell'`]/**/XCUIElementTypeStaticText[`value != '${IOS.getElementValue(
                    "item_editor_details_book_title"
                )}'`]"
            )
        )

    private val saveDraftButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("item_drafts_keep_it")),
            iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("item_drafts_keep_it")}' || label == '${IOS.getElementValue("item_drafts_keep_it")}'")
        )

    private val discardDraftButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("item_drafts_forget_it")),
            iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("item_drafts_forget_it")}' || label == '${IOS.getElementValue("item_drafts_forget_it")}'")
        )

    private val closeModalButton: VintedElement
        get() = VintedDriver.findElement(
            null,
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeAny[`name == 'close'`][-1]")
        )

    private val brandAuthenticityNoticeElement: VintedElement
        get() {
            val textNote = ElementByLanguage.getElementValueByPlatform(key = "item_upload_brand_authenticity_note")
            val textCta = ElementByLanguage.getElementValueByPlatform(key = "item_upload_brand_authenticity_cta")
            val text = "$textNote $textCta"
            return VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, elementType = BUTTON)
            )
        }

    val validationRobot get() = UploadItemFormValidationRobot()

    private val suggestedElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("catalog_picker_suggested")),
            iOSBy = VintedBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' && value == '${IOS.getElementValue(
                    "catalog_picker_suggested"
                )}'"
            )
        )

    private val suggestedCategoryElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(suggestedCategoryElementText),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && value == '$suggestedCategoryElementText'")
        )

    private val suggestedCategoryPathElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(suggestedCategoryPathElementText),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && value == '$suggestedCategoryPathElementText'")
        )

    private val carouselPhotosElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("upload_carousel_photo"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS 'open_photo'")
        )

    private val carouselButtonContainerElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("carousel_btn_container"),
            iOSBy = VintedBy.accessibilityId("add_photo")
        )

    private val removePhotoElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("upload_carousel_remove"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS 'remove_photo'")
        )

    private val progressDetailsIconAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("progress_view"))

    @Step("Assert title input field is visible")
    fun assertTitleInputFieldIsVisible() {
        VintedAssert.assertTrue(titleField.withWait().isVisible(), "Title input field should be visible")
    }

    @Step("Click on title")
    fun clickTitle() {
        titleField.tap()
    }

    @Step("Click on description")
    fun clickDescription() {
        descriptionField.tap()
    }

    @Step("Click add photo")
    fun clickAddFirstPhoto(): CameraAndGalleryWorkflowRobot {
        IOS.scrollUp()
        addFirstPhotoButton.tap()
        return cameraAndGalleryWorkflowRobot
    }

    @Step("Click add photo")
    fun clickAddPhoto(): CameraAndGalleryWorkflowRobot {
        try {
            addPhotoButton.tap()
        } catch (e: NoSuchElementException) {
            handleAndroidEmptySource()
            addPhotoButton.tap()
        }
        return cameraAndGalleryWorkflowRobot
    }

    @Step("Click photo tips button")
    fun clickPhotoTips(): PhotoTipModalRobot {
        val x = photoTipsButton.withWait().center.x
        val y = photoTipsButton.center.y
        val addToX = if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.DE) && isAndroid) 290 else 90
        VintedDriver.tap(x + addToX, y)
        return PhotoTipModalRobot()
    }

    @Step("Click on proof of authenticity link")
    fun clickProofOfAuthenticity(): UploadFormWorkflowRobot {
        brandAuthenticityNoticeElement.withScrollDownUntilElementIsInTopThirdOfScreen().tapRightBottomCorner(10, -10) { brandAuthenticityNoticeElement.isInvisible() }
        return uploadFormWorkflowRobot
    }

    @Step("Open categories section")
    fun openCategoriesSection(): CategoriesRobot {
        IOS.iOSScrollDownToElementNewWay(Pair(MobileSelector.IosPredicateString, "name == 'catalog_cell' || name == 'category_section'"))
        categoryTitleElement.tapWithRetry()
        return CategoriesRobot()
    }

    @Step("Assert category is visible")
    fun assertCategoryIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(categoryValueElement, "Selected category should be visible")
        return this
    }

    @Step("Assert photo is visible")
    fun assertPhotoIsVisible(): UploadItemRobot {
        VintedAssert.assertTrue(photoElement.isVisible(10), "Photo should be visible")
        return this
    }

    @Step("Assert add photo button visibility {visible}")
    fun assertAddButtonVisibility(visible: Boolean): UploadItemRobot {
        Android.doIfAndroid {
            val isVisible = addPhotoButton.isVisible(3)
            if (isVisible) {
                handleAndroidEmptySource()
            }
        }
        VintedAssert.assertEquals(addPhotoButton.isVisible(3), visible, "Add photo button should be visible: $visible\n")
        return this
    }

    @Step("Enter title and check value")
    fun enterAndAssertTitle(): UploadItemRobot {
        val title = "Test item title"
        titleField.withWait().sendKeysIndividually(title)
        VintedAssert.assertEquals(titleField.text.substringBefore("."), title, "Title does not match expected")
        return this
    }

    @Step("Enter description and assert its value")
    fun enterAndAssertDescription(description: String = "awesome item"): UploadItemRobot {
        if (isiOS) {
            descriptionField.withWait().sendKeysIndividually(description)
        } else {
            descriptionField.click()
            Android.sendKeysUsingKeyboard(description)
        }
        VintedAssert.assertEquals(
            descriptionField.text, description,
            "Description - $description - should be visible"
        )
        Android.closeKeyboard()
        return this
    }

    @Step("Enter description with hashtag")
    fun enterAndAssertDescriptionWithHashtag(): UploadItemRobot {
        val textWithHashtag = "long item description #v"
        if (isiOS) {
            descriptionField.click().sendKeys(textWithHashtag)
            VintedAssert.assertTrue(hashtagFirstElement.isVisible(), "HashtagFirstElement should be visible")
            val hashtagFirstElementValue = hashtagFirstElement.mobileElement.getAttribute("name")
            hashtagFirstElement.click()
            VintedAssert.assertTrue(
                descriptionField.text.contains(hashtagFirstElementValue),
                "Description should contains '$hashtagFirstElementValue'"
            )
            closeElement.click()
            closeModalButton.click()
        } else {
            descriptionField.click()
            Android.sendKeysUsingKeyboard(textWithHashtag) // hashtag suggestions do not appear when using element.sendKeys

            commonUtil.Util.retryAction(
                { descriptionField.text != textWithHashtag },
                {
                    sleepWithinStep(800) // wait for hashtags to appear

                    val x = descriptionField.center.getX()
                    val y = descriptionField.center.getY()
                    val height = descriptionField.size.getHeight()

                    when (Session.sessionDetails.deviceManufacturer) {
                        "LGE" -> Android.tap(x, y + height) // LGE keyboard hidden after sendkeys
                        else -> Android.tap(x, y - height)
                    }

                    descriptionField.click()
                    sleepWithinStep(100) // wait till hashtag is placed in input field
                },
                3
            )

            VintedAssert.assertNotEquals(
                descriptionField.text, textWithHashtag,
                "Selected hashtag should not equal to hashtag input: $textWithHashtag"
            )
            Android.closeKeyboard()
        }
        return this
    }

    @Step("Open brands section")
    fun openBrandsSection(): BrandRobot {
        IOS.scrollDown()
        brandTitleElement.click()
        return brandRobot
    }

    @Step("Assert brand is visible")
    fun assertBrandIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(brandValueElement, "Brand should be selected")
        return this
    }

    @Step("Assert brand name matches selected brand {selectedBrand} in brand cell")
    fun assertBrandNameMatches(selectedBrand: String): UploadItemRobot {
        IOS.scrollDown()
        VintedAssert.assertEquals(brandValueElement.text, selectedBrand, "Brand name should match $selectedBrand but was ${brandValueElement.text}")
        return this
    }

    @Step("Open sizes section")
    fun openSizesSection(): SizeRobot {
        sizeTitleElement.click()
        return sizeRobot
    }

    @Step("Assert size is visible")
    fun assertSizeIsVisible(size: String): UploadItemRobot {
        assertIfTextInSectionExists(sizeValueElement, "Size should be selected")
        VintedAssert.assertEquals(sizeValueElement.text, size, "Size does not match")
        return this
    }

    @Step("iOS Only: Assert selected color name matches")
    fun assertColorNameMatches(color: String): UploadItemRobot {
        IOS.doIfiOS {
            colorValueElement.text.let { colorTextValue ->
                VintedAssert.assertEquals(colorTextValue, color, "Color name should match $color but was $colorTextValue")
            }
        }
        return this
    }

    @Step("Assert home decor textile size is visible")
    fun assertHomeDecorTextileSizeIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(sizeValueElement, "Size should be selected")
        VintedAssert.assertEquals(sizeValueElement.text, ElementByLanguage.HomeDecorTextileSize, "Home decor textile size should be visible")
        return this
    }

    @Step("Open conditions section")
    fun openConditionsSection(): ConditionRobot {
        conditionTitleElement.click()
        return conditionRobot
    }

    @Step("Assert condition is visible")
    fun assertConditionIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(conditionValueElement, "Condition should be selected")
        return this
    }

    @Step("Open colors section")
    fun openColorsSection(): ColorRobot {
        colorTitleElement.withScrollIos().click()
        return colorRobot
    }

    @Step("Assert colors are visible")
    fun assertColorsAreVisible(): UploadItemRobot {
        VintedAssert.assertTrue(colorValueElement.withScrollIos().isVisible(), "Color value element should be visible")
        return this
    }

    @Step("Open materials section")
    fun openMaterialsSection(): MaterialRobot {
        materialTitleElement.withWait(seconds = 10).click()
        return materialRobot
    }

    @Step("Assert material is visible")
    fun assertMaterialIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(materialValueElement, "Material should be selected")
        return this
    }

    @Step("Open selling price section")
    fun openSellingPriceSection(): SellingPriceRobot {
        IOS.scrollDown()
        priceTitleElement.scrollUntilVisibleAndroid(ScrollDirection.DOWN).withScrollIos().click()
        return SellingPriceRobot()
    }

    @Step("Assert price is {price}")
    fun assertPrice(price: String): UploadItemRobot {
        priceValueElement.scrollUntilVisibleAndroid(ScrollDirection.DOWN)

        assertIfTextInSectionExists(priceValueElement, "Price should be visible")
        commonUtil.reporting.Report.addMessage(price)
        val formatPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(price)
        val priceValue = priceValueElement.text
        PriceFactory.assertEquals(priceValue, formatPrice, "Price should be as entered")
        return this
    }

    @Step("Open shipping options section")
    fun openShippingOptionsSection(): ShippingOptionRobot {
        shippingOptionTitleElement.withScrollIos().click()
        return shippingOptionRobot
    }

    @Step("Assert shipping option is {title}")
    fun assertShippingOption(title: String): UploadItemRobot {
        assertIfTextInSectionExists(shippingOptionValueElement, "Shipping option should be visible")
        VintedAssert.assertEquals(shippingOptionValueElement.text, title, "Shipping option title does not match expected")
        return this
    }

    @Step("Assert shipping option is {visibility}")
    fun assertShippingOptionVisibility(visibility: Visibility): UploadFormWorkflowRobot {
        VintedAssert.assertVisibilityEquals(shippingOptionTitleElement.withScrollIos(), visibility, "Shipping option should be: '$visibility'")
        return uploadFormWorkflowRobot
    }

    @Step("Click save and wait")
    fun clickSaveAndWait() {
        clickSave()
        inAppNotificationRobot.closeInAppNotificationIfExists()
        if (isiOS) sleepWithinStep(2500) // Wait till upload screen is closed
        waitForProgressDetailsIconToDisappearOnAndroid()
    }

    @Step("Click save")
    fun clickSave(): UploadB2cItemRobot {
        saveButton.withScrollIos().click()
        return UploadB2cItemRobot()
    }

    @Step("Wait for progress details icon to disappear (only Android)")
    private fun waitForProgressDetailsIconToDisappearOnAndroid() {
        Android.doIfAndroid {
            progressDetailsIconAndroid.withWait(seconds = 1)
            Wait.forElementToDisappear(progressDetailsIconAndroid)
        }
    }

    @Step("Click on photo")
    fun clickOnPhoto(): CameraRobot {
        photoElement.click()
        return cameraRobot
    }

    @Step("Assert that brand authenticity notice is visible")
    fun assertBrandAuthenticityNoticeIsVisible() {
        VintedAssert.assertTrue(brandAuthenticityNoticeElement.isVisible(), "Brand authenticity notice (below brand cell) should be visible")
    }

    @Step("Save draft")
    fun saveDraft() {
        closeElement.click()
        saveDraftButton.click()
        sleepWithinStep(500)
    }

    @Step("Discard draft")
    fun discardDraft() {
        closeElement.click()
        discardDraftButton.click()
        sleepWithinStep(500)
    }

    @Step("Assert brands, sizes and colors sections are not visible")
    fun assertBrandsSizesAndColorsSectionsAreNotVisible(): UploadItemRobot {
        VintedAssert.assertTrue(brandTitleElement.isInvisible(2), "Brand section should not be visible")
        assertColorsAndSizesSectionsAreNotVisible()
        return this
    }

    @Step("Assert colors and sizes sections are not visible")
    fun assertColorsAndSizesSectionsAreNotVisible(): UploadItemRobot {
        VintedAssert.assertTrue(colorTitleElement.isInvisible(2), "Colors section should not be visible")
        assertSizeSectionIsNotVisible()
        return this
    }

    @Step("Assert size section is not visible")
    fun assertSizeSectionIsNotVisible(): UploadItemRobot {
        VintedAssert.assertTrue(sizeTitleElement.isInvisible(2), "Sizes section should not be visible")
        return this
    }

    @Step("Open ISBN section")
    fun openISBNSection(): ISBNRobot {
        isbnTitleElement.click()
        return RobotFactory.isbnRobot
    }

    @Step("Assert ISBN is visible")
    fun assertISBNIsVisible(): UploadItemRobot {
        assertIfTextInSectionExists(isbnValueElement, "ISBN number should be prefilled")
        return this
    }

    @Step("Assert book author is {author}")
    fun assertAuthorIsVisible(author: String): UploadItemRobot {
        VintedAssert.assertEquals(authorSectionValueElement.withScrollIos().text, author, "Author does not match expected")
        return this
    }

    @Step("Assert book title is {title}")
    fun assertTitleIsVisible(title: String): UploadFormWorkflowRobot {
        VintedAssert.assertEquals(titleSectionValueElement.text, title, "Title does not match expected")
        return uploadFormWorkflowRobot
    }

    @Step("Select default field values for item upload and assert them")
    private fun selectFieldValuesOnItemUpload(price: String, isUserAuthorized: Boolean): UploadItemRobot {
        uploadFormWorkflowRobot
            .selectAndAssertNoBrandInUploadForm()
            .selectAndAssertCategoryInUploadForm()
            .selectAndAssertFirstSizeInUploadForm()
            .selectAndAssertFirstConditionInUploadForm()
            .selectAndAssertColorsInUploadForm()
            .selectAndAssertPriceInUploadForm(price, isUserAuthorized)
        return this
    }

    @Step("Select default title, description and photo values for item upload and assert them")
    fun selectDefaultPhotoTitleDescriptionValuesOnItemUpload(needPhoto: Boolean = true): UploadItemRobot {
        selectTitleDescriptionValuesOnItemUpload()
        if (needPhoto) {
            clickAddFirstPhoto()
                .selectPhotosFromGallery()
                .assertPhotoIsVisible()
        }
        return this
    }

    @Step("Select title and description in item upload and assert them")
    fun selectTitleDescriptionValuesOnItemUpload(): UploadItemRobot {
        enterAndAssertTitle()
        enterAndAssertDescription()
        return this
    }

    @Step("Select random shipping option and assert it is selected (for Payment countries only)")
    fun selectRandomShippingOption(): UploadItemRobot {
        if (isPaymentCountry) {
            val shippingOptionRobot = openShippingOptionsSection()

            val selectedTitle = shippingOptionRobot
                .selectRandomStandardShippingOptionAndReturnTitle()
            shippingOptionRobot
                .clickSubmitInParcelSizeScreen()
                .assertShippingOption(selectedTitle)
        } else commonUtil.reporting.Report.addMessage("Random shipping option step skipped for non-payment countries")
        return this
    }

    @Step("Select default values for item upload and assert them")
    private fun selectDefaultValuesOnItemUpload(price: String, isUserAuthorized: Boolean = true, needPhoto: Boolean = true): UploadItemRobot {
        selectDefaultPhotoTitleDescriptionValuesOnItemUpload(needPhoto)
        selectFieldValuesOnItemUpload(price, isUserAuthorized)
        selectRandomShippingOption()
        return this
    }

    @Step("Select default values with photo for item upload and assert them")
    fun selectDefaultValuesOnItemUploadWithPhoto(price: String, isUserAuthorized: Boolean = true): UploadItemRobot {
        selectDefaultValuesOnItemUpload(price, isUserAuthorized, true)
        return this
    }

    @Step("Select default values without photo for item upload and assert them")
    fun selectDefaultValuesOnItemUploadWithoutPhoto(price: String, isUserAuthorized: Boolean = true): UploadItemRobot {
        selectDefaultValuesOnItemUpload(price, isUserAuthorized, false)
        return this
    }

    private fun assertIfTextInSectionExists(sectionTextElement: VintedElement, errorMessage: String) {
        VintedAssert.assertTrue(sectionTextElement.isVisible(), errorMessage)
        val value = sectionTextElement.text
        VintedAssert.assertFalse(value.isEmpty(), "Value was not saved")
    }

    private fun handleAndroidEmptySource() {
        Android.clickBack()
        ModalRobot().isModalVisible()
        Android.clickBack()
    }

    companion object {
        private const val CATEGORY_CONTAINER_NAME_ANDROID = "item_form_categories_style_container"
        private const val BRAND_CONTAINER_NAME_ANDROID = "item_form_brand_container"
        private const val SIZE_CONTAINER_NAME_ANDROID = "item_form_size_container"
        private const val CONDITION_CONTAINER_NAME_ANDROID = "item_form_status_container"
        private const val COLOR_CONTAINER_NAME_ANDROID = "item_form_color_container"
        private const val PRICE_CONTAINER_NAME_ANDROID = "item_info_price_cell"
        private const val SHIPPING_OPTION_CONTAINER_NAME_ANDROID = "item_form_packaging_option"
        private const val ISBN_CONTAINER_NAME_ANDROID = "item_form_isbn_container"
        private const val AUTHOR_CONTAINER_NAME_ANDROID = "item_form_book_author_container"
        private const val TITLE_CONTAINER_NAME_ANDROID = "item_form_book_title_container"
        private const val MATERIAL_CONTAINER_NAME_ANDROID = "item_form_material_container"
    }

    @Step("Upload photo of Blazer and assert it is visible in upload form")
    fun uploadPhotoAndAssertVisibilityInUploadForm(): UploadItemRobot {
        clickAddFirstPhoto()
        if (isAndroid) {
            cameraAndGalleryWorkflowRobot.selectPhotoFromGalleryByImageRecognition(ImageFactory.TIPS_IN_GRID)
        } else {
            cameraAndGalleryWorkflowRobot.selectPhotosFromGallery(1)
        }
        assertPhotoIsVisible()
        return this
    }

    @Step("Assert suggested, suggested category and suggested category path elements are visible")
    fun assertSuggestedElementsVisibility(): UploadItemRobot {
        assertSuggestedElementVisibility(visibility = Visibility.Visible)
        assertSuggestedCategoryElementVisibility(visibility = Visibility.Visible)
        assertSuggestedCategoryPathElementVisibility(visibility = Visibility.Visible)
        return this
    }

    @Step("Assert suggested element visibility {visibility}")
    private fun assertSuggestedElementVisibility(visibility: Visibility): UploadItemRobot {
        VintedAssert.assertVisibilityEquals(suggestedElement, visibility, "Element with text $suggestedElementText should be displayed")
        return this
    }

    @Step("Assert suggested category element visibility {visibility}")
    private fun assertSuggestedCategoryElementVisibility(visibility: Visibility): UploadItemRobot {
        VintedAssert.assertVisibilityEquals(suggestedCategoryElement, visibility, "Element with text $suggestedCategoryElementText should be displayed")
        return this
    }

    @Step("Assert suggested category path element visibility {visibility}")
    private fun assertSuggestedCategoryPathElementVisibility(visibility: Visibility): UploadItemRobot {
        VintedAssert.assertVisibilityEquals(suggestedCategoryPathElement, visibility, "Element with text $suggestedCategoryPathElementText should be displayed")
        return this
    }

    @Step("Select suggested category")
    fun selectSuggestedCategory(): UploadItemRobot {
        suggestedCategoryElement.click()
        return this
    }

    @Step("Select suggested category and assert it is visible in upload form")
    fun selectSuggestedCategoryAndAssertVisibilityInUploadForm(): UploadItemRobot {
        selectSuggestedCategory()
        assertCategoryIsVisible()
        return this
    }

    @Step("Open category section and assert category is suggested")
    fun openCategorySectionAndAssertCategorySuggested(): UploadItemRobot {
        openCategoriesSection()
        assertSuggestedElementsVisibility()
        return this
    }

    @Step("Open category section and assert category is suggested from edit")
    fun openCategorySectionAndAssertCategorySuggestedFromEdit(): UploadItemRobot {
        if (isAndroid) {
            openCategoriesSection()
            while (suggestedElement.isInvisible()) {
                uploadItemRobot.clickBack()
            }
            assertSuggestedElementsVisibility()
        } else {
            openCategorySectionAndAssertCategorySuggested()
        }
        return this
    }

    @Step("Rearrange 2 photos")
    fun rearrangePhotosInUploadForm(): UploadItemRobot {
        VintedElement.isListVisible({ carouselPhotosElementList })
        carouselPhotosElementList[0].performDragAndDrop(carouselButtonContainerElement)
        return uploadItemRobot
    }

    @Step("Android only: Swipe right in photo carousel")
    fun swipeRightInPhotoCarousel(devices: List<LaboratoryDevice>): UploadItemRobot {
        Android.doIfAndroid {
            if (devices.map { it.model }.any { model -> model == Session.sessionDetails.deviceModel }) {
                carouselPhotosElementList[0].swipeRight()
            }
        }
        return this
    }

    @Step("Remove {photoCountToRemove} photos")
    fun removeSelectedPhotosAmount(photoCountToRemove: Int): UploadItemRobot {
        repeat(photoCountToRemove) { removePhotoElementList[0].click() }
        return this
    }

    @Step("Remove photo number {photoNumberInList}")
    fun removeOnlyOnePhoto(photoNumberInList: Int): UploadItemRobot {
        removePhotoElementList[photoNumberInList].click()
        return this
    }
}

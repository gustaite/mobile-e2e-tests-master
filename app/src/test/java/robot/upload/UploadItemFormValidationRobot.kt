package robot.upload

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.testng.config.PortalFactory.isPaymentCountry
import util.IOS

class UploadItemFormValidationRobot : BaseRobot() {

    private val photoValidationElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy =
            VintedBy.scrollableId("carousel_hint"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("item_upload_no_photos"))
        )

    private val titleValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("input_title", Android.INPUT_VALIDATION_FIELD_ID)
        )

    private val descriptionValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("input_description_cell", Android.CELL_VALIDATION_FIELD_ID)
        )

    private val categoryValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                "item_form_categories_style_container",
                Android.CELL_VALIDATION_FIELD_ID
            )
        )

    private val brandValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild(
                "item_form_brand_container",
                Android.CELL_VALIDATION_FIELD_ID
            )
        )

    private val conditionValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("item_form_status_container", Android.CELL_VALIDATION_FIELD_ID)
        )

    private val priceValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("item_info_price_cell", Android.CELL_VALIDATION_FIELD_ID)
        )

    private val sizeValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("item_form_size_container", Android.CELL_VALIDATION_FIELD_ID)
        )

    private val colorValidationElement: VintedElement
        get() = VintedDriver.findElement(VintedBy.scrollableSetWithParentAndChild("item_form_color_container", Android.CELL_VALIDATION_FIELD_ID))

    private val parcelSizeValidationElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableSetWithParentAndChild("item_form_packaging_option", Android.CELL_VALIDATION_FIELD_ID)
        )

    @Step("Assert item photo validation message is visible")
    fun assertValidationVisibleOnPhoto(): UploadItemFormValidationRobot {
        VintedAssert.assertTrue(photoValidationElement.isVisible(), "Photo validation message should be visible")
        return this
    }

    @Step("Assert item photo, title and description validation messages are visible")
    fun assertValidationVisibleOnPhotoTitleDescription(): UploadItemFormValidationRobot {
        assertValidationVisibleOnPhoto()
        VintedAssert.assertTrue(titleValidationElement.isVisible(), "Title validation message should be visible")
        VintedAssert.assertTrue(descriptionValidationElement.isVisible(), "Description validation message should be visible")
        return this
    }

    @Step("Assert item category, brand, condition validation messages are visible")
    fun assertValidationVisibleOnCategoryBrandConditionPrice(): UploadItemFormValidationRobot {
        VintedAssert.assertTrue(categoryValidationElement.isVisible(), "Category validation message should be visible")
        VintedAssert.assertTrue(brandValidationElement.isVisible(), "Brand validation message should be visible")
        VintedAssert.assertTrue(conditionValidationElement.isVisible(), "Condition validation message should be visible")
        VintedAssert.assertTrue(priceValidationElement.isVisible(), "Price validation message should be visible")
        return this
    }

    @Step("Assert item size, color validation messages are visible")
    fun assertValidationVisibleOnSizeColor(): UploadItemFormValidationRobot {
        VintedAssert.assertTrue(sizeValidationElement.isVisible(), "Size validation message should be visible")
        VintedAssert.assertTrue(colorValidationElement.isVisible(), "Color validation message should be visible")
        return this
    }

    @Step("Assert item parcel size validation message is visible on Payments countries")
    fun assertValidationVisibleOnParcelSize(): UploadItemFormValidationRobot {
        if (isPaymentCountry) {
            VintedAssert.assertTrue(parcelSizeValidationElement.isVisible(), "Parcel size validation message should be visible")
        } else {
            commonUtil.reporting.Report.addMessage("Parcel size validation message is not checked on not payments countries")
        }
        return this
    }
}

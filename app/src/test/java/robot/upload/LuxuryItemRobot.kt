package robot.upload

import RobotFactory.uploadItemRobot
import RobotFactory.cameraAndGalleryWorkflowRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.photo.CameraAndGalleryWorkflowRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.assertVisibilityEquals
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class LuxuryItemRobot : BaseRobot() {

    private val luxuryItemWarningTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(Android.getElementValue("item_upload_luxury_item_validation_error_message")),
            iOSBy = VintedBy.iOSTextByBuilder(IOS.getElementValue("item_upload_luxury_item_validation_error_message"))
        )

    private val luxuryItemModalTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(Android.getElementValue("item_upload_luxury_photos_modal_title")),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("item_upload_luxury_photos_modal_title"))
        )

    private val luxuryModalCloseButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_right_action"),
            iOSBy = VintedBy.accessibilityId("luxury_modal_close")
        )

    private val luxuryModalAddMorePhotosButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_primary_button", "item_upload_luxury_photos_modal_button_add")

    private val luxuryModalSaveAsDraftButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_secondary_button", "item_upload_luxury_photos_modal_button_save_as_draft")

    @Step("Assert luxury item warning under the photo upload is {visibility}")
    fun assertLuxuryItemWarningTextVisibility(visibility: Visibility): UploadItemRobot {
        VintedAssert.assertVisibilityEquals(luxuryItemWarningTextElement, visibility, "Luxury item warning under the photo upload should be $visibility")
        return uploadItemRobot
    }

    @Step("Assert luxury item modal and its title are {visibility}")
    fun assertLuxuryItemsModalTitleVisibility(visibility: Visibility): LuxuryItemRobot {
        VintedAssert.assertVisibilityEquals(luxuryItemModalTitleElement, visibility, "Luxury item warning modal and its title should be $visibility")
        return this
    }

    @Step("Click on add more photos button in luxury item warning modal")
    fun clickOnAddMorePhotosInLuxuryItemWarningModal(): CameraAndGalleryWorkflowRobot {
        luxuryModalAddMorePhotosButtonElement.click()
        return cameraAndGalleryWorkflowRobot
    }

    @Step("Click on save as draft button in luxury item warning modal")
    fun clickOnSaveAsDraftInLuxuryItemWarningModal(): LuxuryItemRobot {
        luxuryModalSaveAsDraftButtonElement.click()
        return this
    }

    @Step("Close luxury modal")
    fun closeLuxuryModal(): LuxuryItemRobot {
        luxuryModalCloseButtonElement.click()
        return this
    }
}

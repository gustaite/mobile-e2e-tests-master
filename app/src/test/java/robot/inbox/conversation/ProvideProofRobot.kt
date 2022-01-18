package robot.inbox.conversation

import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.photo.CameraAndGalleryWorkflowRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.getElementValueByPlatform

class ProvideProofRobot : BaseRobot() {
    private val addPhotoButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("add_photo"),
            iOSBy = VintedBy.accessibilityId("upload_photo")
        )
    private val inputTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextView")
        )
    private val sendToSellerButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("submit"),
            iOSBy = VintedBy.accessibilityId("cs_submit")
        )

    @Step("Assert Contact US form title")
    fun assertContactUsPageTitle(): ProvideProofRobot {
        VintedAssert.assertEquals(getActionBarTitle(), getElementValueByPlatform("contact_form_title"), "Contact US title does not match")
        return this
    }

    @Step("Select photo from gallery")
    private fun addPhoto(): ProvideProofRobot {
        addPhotoButton.click()
        CameraAndGalleryWorkflowRobot().selectPhotosFromGallery()
        return this
    }

    @Step("Provide proof of damaged item")
    fun provideProof(reason: String): ConversationRobot {
        addPhoto()
        inputTextElement.sendKeys(reason)
        sendToSellerButton.click()
        return conversationRobot
    }
}

package robot.forum

import RobotFactory.actionBarRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.*

class CreateForumTopicRobot : BaseRobot() {

    private val subjectElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("view_input_value", Android.getElementValue("create_topic_input_title_placeholder")),
            iOSBy = VintedBy.accessibilityId("post_topic")
        )

    private val forumTopicTitleEditElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_input_value"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("edit_topic_input_title"))
        )

    private val postInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("mentions_text_area_body"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("edit_post_input_title"))
        )

    private val addPhotoButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("form_add_photo_button"),
            iOSBy = VintedBy.accessibilityId("add_photo")
        )

    private val forumCarouselPhotoElementListAndroid: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("form_carousel_photo")
        )

    private val forumCarouselPhotoElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId("open_photo_0")
        )

    private val forumFirstImageElement: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("gallery_image"),
            iOSBy = VintedBy.className("XCUIElementTypeImage")
        )

    private fun postTextForumElementAndroid(text: String) = VintedDriver.findElement(VintedBy.androidIdAndText("post_body_text", text))

    private val saveNewTopicButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("submit_button"),
            iOSBy = VintedBy.accessibilityId("save_changes_button")
        )

    @Step("Assert Post with text '{expectedText}' is visible (only Android)")
    fun assertPostWithTextIsVisible(expectedText: String): CreateForumTopicRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(
                postTextForumElementAndroid(expectedText).isVisible(), "Post with text '$expectedText' should be visible"
            )
        }
        return this
    }

    @Step("Click save forum topic")
    fun saveForumTopic() {
        saveNewTopicButtonElement.click()
    }

    @Step("Edit Forum Topic title")
    fun editForumTopicTitle(title: String): CreateForumTopicRobot {
        forumTopicTitleEditElement.clear()
        forumTopicTitleEditElement.sendKeys(title)
        return this
    }

    @Step("Edit Forum post input")
    fun editForumPostInput(text: String): CreateForumTopicRobot {
        postInputElement.clear()
        postInputElement.sendKeys(text)
        return this
    }

    @Step("Add photo from gallery")
    fun addPhotoFromGallery(): CreateForumTopicRobot {
        addPhotoButton.click()
        forumFirstImageElement[1].click()
        actionBarRobot.submit()
        return this
    }

    @Step("Assert photo counter is equal to {count}")
    fun assertPhotoCounterEqualsTo(count: Int): CreateForumTopicRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(
                forumCarouselPhotoElementListAndroid.first().isVisible() && forumCarouselPhotoElementListAndroid.count() == count,
                "Assert one photo in forum carousel photos is visible"
            )
        }
        IOS.doIfiOS {
            VintedAssert.assertTrue(forumCarouselPhotoElementIos.isVisible(), "Assert one photo is visible in forum photos carousel")
        }

        return this
    }

    @Step("Enter subject title: {subject}")
    fun enterSubjectTitle(subject: String): CreateForumTopicRobot {
        subjectElement.withWait().sendKeys(subject)
        return this
    }

    @Step("Enter default post text")
    fun enterDefaultPostText(): CreateForumTopicRobot {
        postInputElement.sendKeys(post)
        return this
    }

    val post =
        """
            Fulfilled direction use continual set him propriety continued. Margaret disposed add screened rendered six say his striking confined.

            For who thoroughly her boy estimating conviction. Removed demands expense account in outward tedious do.
        """.trimIndent()
}

enum class TitleActions(val index: Int) {
    EDIT(0),
    DELETE(1)
}

enum class PostActions(val index: Int) {
    EDIT(0),
    WARN(1)
}

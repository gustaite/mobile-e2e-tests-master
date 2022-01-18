package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.image.ImageFactory
import util.image.ImageRecognition
import util.values.ElementByLanguage

class FavoriteItemsRobot : BaseRobot() {

    private val heartIconElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_box_favorites_icon"),
            iOSBy = VintedBy.accessibilityId("heartMediumActive")
        )

    private val emptyStateTitleElement
        get() = {
            val text = ElementByLanguage.getElementValueByPlatform(key = "empty_state_title_favorites")
            VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
            )
        }()

    private val favoriteItemsScreenTitleElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("actionbar_label", "favorite_clothes")

    @Step("Assert favorite items screen is opened")
    fun assertFavoriteItemsScreenOpen(): FavoriteItemsRobot {
        VintedAssert.assertTrue(favoriteItemsScreenTitleElement.isVisible(), "Favorite items screen should be displayed")
        return this
    }

    @Step("Assert item photo")
    fun assertItemPhoto(): FavoriteItemsRobot {
        VintedAssert.assertTrue(ImageRecognition.isImageInScreen(ImageFactory.ITEM_1_PHOTO_ITEM_BOX), "Image occurrence was not found")
        return this
    }

    @Step("Assert heart icon is red")
    fun assertHeartIconIsRed(): FavoriteItemsRobot {
        val (isInImage) =
            ImageRecognition.isImageInElement(element = heartIconElement, image = ImageFactory.ITEM_FAVORITE_HEART_ACTIVE, threshold = 0.42)
        VintedAssert.assertTrue(isInImage, "Image occurrence was not found")
        return this
    }

    @Step("Unfavorite item and refresh")
    fun unfavoriteItemAndRefresh(): FavoriteItemsRobot {
        heartIconElement.tap()
        VintedDriver.pullDownToRefresh()
        return RobotFactory.favoriteItemsRobot
    }

    @Step("Assert empty state title is visible")
    fun assertEmptyStateTitleIsVisible() {
        VintedAssert.assertTrue(emptyStateTitleElement.isVisible(), "Empty state title should be visible")
    }
}

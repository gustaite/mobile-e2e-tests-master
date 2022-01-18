package util.image

import commonUtil.asserts.VintedAssert
import commonUtil.data.Image
import util.driver.VintedElement

class AssertImage {
    companion object {
        fun assertImageIsInScreen(
            image: Image, threshold: Double = 0.5, retryCount: Int = 10,
            errorMessage: String = "Image ${image.name} should be in screen"
        ) {
            VintedAssert.assertTrue(
                ImageRecognition.isImageInScreen(image, threshold = threshold, retryCount = retryCount),
                errorMessage
            )
        }

        fun assertImageIsNotInScreen(
            image: Image, threshold: Double = 0.5, retryCount: Int = 10,
            errorMessage: String = "Image ${image.name} should not be in screen"
        ) {
            VintedAssert.assertFalse(
                ImageRecognition.isImageInScreen(image, threshold = threshold, retryCount = retryCount, shouldBeInElement = false),
                errorMessage
            )
        }

        fun assertImageIsInSelectedElement(
            element: () -> VintedElement, image: Image, threshold: Double = 0.5,
            retryCount: Int = 10,
            errorMessage: String = "Image ${image.name} should be in selected element"
        ) {
            val (isInImage) = ImageRecognition.isImageInElement(
                element = element(),
                image = image,
                threshold = threshold,
                retryCount = retryCount
            )
            VintedAssert.assertTrue(isInImage, errorMessage)
        }

        fun assertImageIsNotInSelectedElement(
            element: () -> VintedElement, image: Image, threshold: Double = 0.5,
            retryCount: Int = 10,
            errorMessage: String = "Image ${image.name} should not be in selected element"
        ) {
            val (isInImage) = ImageRecognition.isImageInElement(
                element = element(),
                image = image,
                threshold = threshold,
                retryCount = retryCount,
                shouldBeInElement = false
            )
            VintedAssert.assertFalse(isInImage, errorMessage)
        }
    }
}

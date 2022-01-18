package util

import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.mobile.VintedPackage
import util.EnvironmentManager.isAndroid
import util.driver.VintedElement
import util.values.Visibility

fun VintedPackage.getPackage(): String {
    return if (isAndroid) this.android else this.ios
}

fun VintedAssert.assertVisibilityEquals(
    element: VintedElement,
    expectedVisibility: Visibility,
    message: String,
    waitForVisible: Long = 5,
    waitForInvisible: Long = 1
) {
    val waitSec: Long = if (expectedVisibility.value) waitForVisible else waitForInvisible
    assertEquals(element.matchesVisibilityState(visibility = expectedVisibility, waitSec), expectedVisibility.value, message)
}

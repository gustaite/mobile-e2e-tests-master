package util.driver

import io.appium.java_client.MobileBy
import org.openqa.selenium.By
import util.Android.Companion.ID
import util.Android.Scroll.SCROLL_INTO_VIEW_PREFIX
import util.Android.Scroll.SUFFIX
import util.IOS
import util.Util

class VintedBy {
    companion object {
        fun id(id: String): By {
            return MobileBy.id(addPackageId(id))
        }

        fun scrollableIdWithIsCheckedFlag(parentId: String, checked: Boolean): By {
            val selector = "$SCROLL_INTO_VIEW_PREFIX${addPackageId(parentId)}\").fromParent(UiSelector().checked($checked)))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun scrollableId(id: String): By {
            val scrollableId = "$SCROLL_INTO_VIEW_PREFIX${addPackageId(id)}$SUFFIX"
            return MobileBy.AndroidUIAutomator(scrollableId)
        }

        fun scrollableIdWithText(parentId: String, text: String): By {
            val selector = "$SCROLL_INTO_VIEW_PREFIX${addPackageId(parentId)}\").fromParent(UiSelector().textContains(\"$text\")))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun scrollableSetWithParentAndChild(parentId: String, childId: String): By {
            val selector = "$SCROLL_INTO_VIEW_PREFIX${addPackageId(parentId)}\").childSelector(UiSelector().resourceId(\"${addPackageId(childId)}\")))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun scrollableSetWithParentIdAndChildClassname(parentId: String, childClassname: String): By {
            val selector = "$SCROLL_INTO_VIEW_PREFIX${addPackageId(parentId)}\").childSelector(UiSelector().className(\"$childClassname\")))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun setWithParentAndChild(parentId: String, childId: String): By {
            val selector = "UiSelector().resourceId(\"${addPackageId(parentId)}\").childSelector(UiSelector().resourceId(\"${addPackageId(childId)}\"))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun androidIdMatches(idRegexExp: String): By {
            return androidUIAutomator("UiSelector().resourceIdMatches(\"$idRegexExp\")")
        }

        fun androidIdAndText(id: String, text: String): By {
            val selector = "UiSelector().resourceId(\"${addPackageId(id)}\").fromParent(UiSelector().textContains(\"$text\"))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun androidClassNameAndText(className: String, text: String): By {
            val selector = "UiSelector().className(\"$className\").fromParent(UiSelector().textContains(\"$text\"))"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun androidText(text: String): By {
            val selector = "UiSelector().text(\"${text}\")"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun androidTextContains(text: String): By {
            val selector = "UiSelector().textContains(\"${text}\")"
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun androidUIAutomator(selector: String): By {
            return MobileBy.AndroidUIAutomator(selector)
        }

        fun className(className: String): By {
            return MobileBy.className(className)
        }

        fun accessibilityId(accessibilityId: String): By {
            return MobileBy.AccessibilityId(accessibilityId)
        }

        fun iOSClassChain(classChain: String): By {
            return MobileBy.iOSClassChain(classChain)
        }

        fun iOSNsPredicateString(predicateString: String): By {
            return MobileBy.iOSNsPredicateString(predicateString)
        }

        fun iOSNsPredicateStringNameOrLabel(nameOrLabelValue: String): By {
            return iOSNsPredicateString("name == '$nameOrLabelValue' || label == '$nameOrLabelValue'")
        }

        fun name(name: String): By {
            return MobileBy.name(name)
        }

        fun xpath(xpath: String): By {
            return MobileBy.xpath(xpath)
        }

        fun androidTextByBuilder(
            text: String, scroll: Boolean = true, searchType: Util.SearchTextOperator = Util.SearchTextOperator.EXACT
        ): By {
            val elementByText = "UiSelector().${searchType.androidUiSelectorTextMethodName}(\"$text\")"
            val scrollableElement = "UiScrollable(UiSelector().scrollable(true).instance(0)).scrollIntoView($elementByText)"
            val by = if (scroll) scrollableElement else elementByText

            return androidUIAutomator(by)
        }

        fun iOSTextByBuilder(
            text: String,
            searchType: Util.SearchTextOperator = Util.SearchTextOperator.EXACT,
            onlyVisibleInScreen: Boolean = false,
            elementType: IOS.ElementType = IOS.ElementType.STATIC_TEXT
        ): By {
            val type = if (elementType.typeName != IOS.ElementType.ANY.typeName) {
                // ToDo Lorenas Button part is temporary and should be removed after creation of 21.29.0 version
                "(type == '${elementType.typeName}' || type == '${IOS.ElementType.BUTTON.typeName}') &&"
            } else {
                ""
            }
            val name = "name ${searchType.iOSOperator}[c] \"$text\""
            val label = "label ${searchType.iOSOperator}[c] \"$text\""
            val visibility = "visible == 1"

            return if (onlyVisibleInScreen) {
                iOSNsPredicateString("($type $name && $visibility) || ($type $label && $visibility)")
            } else {
                iOSNsPredicateString("($type $name) || ($type $label)")
            }
        }

        private fun addPackageId(id: String): String {
            return if (id.contains(":")) id else "${ID}$id"
        }
    }
}

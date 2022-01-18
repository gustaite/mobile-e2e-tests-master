package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class DelayedPublicationRobot : BaseRobot() {
    private val itemDelayedLabelElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "item_alert_cell",
                Android.getElementValue("item_state_delayed_publication")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("item_state_delayed_publication"))
        )

    private val itemDelayedInfoLinkElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText(
                "item_alert_body_suffix",
                Android.getElementValue("delayed_publication_alert_learn_more")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("delayed_publication_alert_learn_more"))
        )
    private val itemDelayedModalTitleElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText(
                "modal_title",
                Android.getElementValue("delayed_publication_modal_title")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("delayed_publication_modal_title"))
        )

    private val itemDelayedModalButtonElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidIdAndText(
                "modal_primary_button",
                Android.getElementValue("delayed_publication_modal_button")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("delayed_publication_modal_button"))
        )

    @Step("Assert delayed item label is visible")
    fun assertDelayedItemLabelIsVisible(): DelayedPublicationRobot {
        VintedAssert.assertTrue(itemDelayedLabelElement.isVisible(), "Delayed item label should be visible")
        return this
    }

    @Step("Assert delayed item information link is visible")
    fun assertDelayedItemInformationLinkIsVisible(): DelayedPublicationRobot {
        VintedAssert.assertTrue(
            itemDelayedInfoLinkElement.isVisible(),
            "Delayed item information link should be visible"
        )
        return this
    }

    @Step("Assert modal about delayed publication is {visibility}")
    fun assertDelayedItemModalVisibility(visibility: Visibility): DelayedPublicationRobot {
        VintedAssert.assertEquals(
            itemDelayedModalTitleElement.isVisible(), visibility.value,
            "Modal about delayed publication should be $visibility"
        )
        VintedAssert.assertEquals(
            itemDelayedModalButtonElement.isVisible(), visibility.value,
            "Delayed publication modal ok button should be $visibility"
        )
        return this
    }

    @Step("Click on delayed publication information link in item view")
    fun clickOnInformationLink(): DelayedPublicationRobot {
        assertDelayedItemLabelIsVisible()
        assertDelayedItemInformationLinkIsVisible()
        itemDelayedInfoLinkElement.click()
        return this
    }

    @Step("Click on OK button in delayed information modal")
    fun clickOnOkInDelayedInformationModal(): DelayedPublicationRobot {
        itemDelayedModalButtonElement.click()
        return this
    }
}

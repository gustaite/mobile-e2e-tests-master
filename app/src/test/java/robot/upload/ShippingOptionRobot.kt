package robot.upload

import RobotFactory.shippingOptionRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.extensions.adaptPrice
import commonUtil.testng.config.PortalFactory.isCurrentRegardlessEnv
import io.qameta.allure.Step
import robot.ActionBarRobot
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.absfeatures.AbTestController
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.Wait
import util.reporting.AllureReport
import util.values.*
import kotlin.random.Random

class ShippingOptionRobot : BaseRobot() {
    private val actionBarRobot: ActionBarRobot get() = ActionBarRobot()
    private fun getPackageElement(title: String): VintedElement {
        return VintedDriver.findElementByText(text = title, searchType = Util.SearchTextOperator.STARTS_WITH)
    }

    private val customShippingElement: VintedElement get() = VintedDriver.elementByIdAndTranslationKey(
        androidId = "custom_packaging_options_cell",
        iosTranslationKey = "common.package_type.description_by_catalog.default.custom"
    )

    private val customShippingPriceElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "custom_packaging_domestic_price", Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("domestic_input_accessibilityIdentifier")

        )

    private val customShippingInternationalPriceElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "custom_packaging_international_price",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("international_input_accessibilityIdentifier"),

        )

    private val customShippingInternationalElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidId = "packaging_options_international_custom_shipping",
            iosTranslationKey = "package_size_selection_international_shipping_price_title"
        )

    private val noShippingElement: VintedElement get() = VintedDriver.elementByIdAndTranslationKey(
        androidId = "pickup_only_packaging_options_container",
        iosTranslationKey = "common.package_type.description_by_catalog.default.no_shipping"
    )

    private fun packageSizeElement(text: String) = VintedDriver.findElement(
        androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
        iOSBy = VintedBy.accessibilityId(text)
    )

    @Step("Select random standard shipping option and return selected title")
    fun selectRandomStandardShippingOptionAndReturnTitle(): String {
        val randomIndex = getRandomIndex()
        val title = getShippingOptionTitle(randomIndex)
        commonUtil.reporting.Report.addMessage("Selected title: $title")
        clickOnShippingMethod(title)
        AllureReport.addScreenshot()
        return title
    }

    @Step("Select {shippingOptionType} shipping option")
    fun selectShippingOptionAndReturnTitle(shippingOptionType: ShippingOptionTypes): String {
        val index = 0
        return selectShippingOptionAndReturnTitle(index, shippingOptionType)
    }

    @Step("Select {shippingOptionType} shipping option")
    fun selectShippingOptionAndReturnTitle(index: Int, shippingOptionType: ShippingOptionTypes): String {
        val title = getShippingOptionTitle(index, shippingOptionType)
        commonUtil.reporting.Report.addMessage("option title: $title")
        clickOnShippingMethod(title)
        AllureReport.addScreenshot()
        return title
    }

    @Step("Click on shipping method {title}")
    private fun clickOnShippingMethod(title: String) {
        getPackageElement(title).click()
    }

    @Step("Select No Shipping shipping option")
    fun selectNoShippingOptionAndReturnTitle(): String {
        // in iOS US if non shippable category is selected, user cannot open shipping section. No shipping is auto selected
        return if (EnvironmentManager.isiOS && isCurrentRegardlessEnv(VintedPortal.US)) {
            "No shipping"
        } else {
            val title = shippingOptionRobot.selectShippingOptionAndReturnTitle(ShippingOptionTypes.NO_SHIPPING)
            shippingOptionRobot.clickSubmitInParcelSizeScreen()
            title
        }
    }

    @Step("Set price for custom shipping")
    fun setPriceForCustomShipping(price: String, internationalPrice: String): ShippingOptionRobot {
        val priceAdapted = price.adaptPrice()
        val internationalPriceAdapted = internationalPrice.adaptPrice()

        customShippingPriceElement.sendKeys(priceAdapted)

        if (AbTestController.isInternationalShippingOn()) {
            customShippingInternationalPriceElement.sendKeys(internationalPriceAdapted)
        } else {
            VintedAssert.assertFalse(customShippingInternationalElement.isVisible(1), "International price should not be visible based on feature flag")
        }
        return this
    }

    @Step("Assert custom price")
    fun assertCustomPrice(price: String, internationalPrice: String): ShippingOptionRobot {
        val formattedPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(price)

        Wait.forElementTextToMatch(customShippingPriceElement, formattedPrice)
        PriceFactory.assertEquals(customShippingPriceElement.text, formattedPrice, "Custom price")

        if (AbTestController.isInternationalShippingOn()) {
            val formattedInternationalPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(internationalPrice)
            Wait.forElementTextToMatch(customShippingInternationalPriceElement, formattedInternationalPrice)
            PriceFactory.assertEquals(customShippingInternationalPriceElement.text, formattedInternationalPrice, "International price")
        } else {
            VintedAssert.assertFalse(customShippingInternationalElement.isVisible(1), "International price should not be visible in UK")
        }
        return this
    }

    @Step("Assert custom shipping option visibility")
    fun assertCustomOptionVisibility(visibility: Visibility): ShippingOptionRobot {
        VintedAssert.assertVisibilityEquals(customShippingElement, visibility, "Custom option should be: '$visibility'")
        return this
    }

    @Step("Assert no shipping option visibility")
    fun assertNoShippingOptionVisibility(visibility: Visibility): ShippingOptionRobot {
        VintedAssert.assertVisibilityEquals(noShippingElement, visibility, "NoShipping option should be: '$visibility'")
        return this
    }

    @Step("Click submit button")
    fun clickSubmit(): UploadItemRobot {
        actionBarRobot.submit()
        return UploadItemRobot()
    }

    @Step("Click submit button in Parcel Size Screen in upload form")
    fun clickSubmitInParcelSizeScreen(): UploadItemRobot {
        actionBarRobot.submitInParcelSizeScreen()
        return UploadItemRobot()
    }

    @Step("Get random index")
    fun getRandomIndex(): Int {
        val size = ElementByLanguage.StandardShippingOptions.size

        return Random.nextInt(0, size)
    }

    @Step("Select package size")
    fun selectPackageSize(packageSizeTitle: String) {
        packageSizeElement(packageSizeTitle).tap()
    }

    private fun getShippingOptionTitle(
        index: Int,
        shippingType: ShippingOptionTypes = ShippingOptionTypes.STANDARD
    ): String {
        var title = when (shippingType) {
            ShippingOptionTypes.STANDARD -> ElementByLanguage.StandardShippingOptions[index]
            ShippingOptionTypes.CUSTOM -> ElementByLanguage.CustomShippingOption
            ShippingOptionTypes.NO_SHIPPING -> ElementByLanguage.NoShippingOption!!
        }
        if (isCurrentRegardlessEnv(VintedPortal.US) && isAndroid && shippingType != ShippingOptionTypes.NO_SHIPPING) {
            title = title.substringBefore(" ")
        }
        return title
    }
}

enum class ShippingOptionTypes {
    STANDARD,
    CUSTOM,
    NO_SHIPPING
}

package util.values

import commonUtil.testng.config.ConfigManager.portal
import api.controllers.GlobalAPI
import api.values.PersonalizationSizes.BABIES_DIAPERS_SIZE_ID_0
import api.values.PersonalizationSizes.BABIES_DIAPERS_SIZE_ID_1
import api.values.PersonalizationSizes.MEN_CLOTHES_SIZE_ID_0
import api.values.PersonalizationSizes.MEN_CLOTHES_SIZE_ID_1
import api.values.PersonalizationSizes.MEN_CLOTHES_SIZE_ID_2
import api.values.PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_0
import api.values.PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_1
import api.values.PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_2
import commonUtil.data.enums.VintedCatalogs
import util.base.BaseTest.Companion.systemConfiguration
import util.base.BaseTest.Companion.defaultUser

class Personalization {
    companion object {
        val womenSizeTitleM by lazy { getSizesTitles(listOf(WOMEN_CLOTHES_SIZE_ID_2)) }
        val menSizeTitleL by lazy { getSizesTitles(listOf(MEN_CLOTHES_SIZE_ID_2)) }
        val secondBabiesSizeTitle by lazy { getSizesTitles(listOf(BABIES_DIAPERS_SIZE_ID_1)) }
        val womenSizeTitles by lazy { getSizesTitles(listOf(WOMEN_CLOTHES_SIZE_ID_0, WOMEN_CLOTHES_SIZE_ID_1)) }
        val menSizeTitles by lazy { getSizesTitles(listOf(MEN_CLOTHES_SIZE_ID_0, MEN_CLOTHES_SIZE_ID_1)) }
        val babiesSizeTitles by lazy { getSizesTitles(listOf(BABIES_DIAPERS_SIZE_ID_0)) }

        val womenSizeSTitle by lazy { getSizesTitles(listOf(WOMEN_CLOTHES_SIZE_ID_1)) }
        val womenSizeMTitle by lazy { getSizesTitles(listOf(WOMEN_CLOTHES_SIZE_ID_2)) }
        val menSizeSTitle by lazy { getSizesTitles(listOf(MEN_CLOTHES_SIZE_ID_1)) }
        val menSizeMTitle by lazy { getSizesTitles(listOf(MEN_CLOTHES_SIZE_ID_2)) }

        val womenCategoryTitle by lazy { systemConfiguration.feedSettings.categories[0].title }
        val menCategoryTitle by lazy { systemConfiguration.feedSettings.categories[1].title }
        val babiesCategoryTitle by lazy { systemConfiguration.feedSettings.categories[2].title }

        val oneForEachCatalogSizeIds: List<Long> by lazy {
            val sizeIds = mutableListOf<Long>()
            for (catalog in portal.catalogs) {
                when (catalog) {
                    VintedCatalogs.WOMEN -> {
                        sizeIds += WOMEN_CLOTHES_SIZE_ID_1
                    }
                    VintedCatalogs.MEN -> {
                        sizeIds += MEN_CLOTHES_SIZE_ID_1
                    }
                    VintedCatalogs.BABIES -> {
                        sizeIds += BABIES_DIAPERS_SIZE_ID_0
                    }
                }
            }
            sizeIds
        }

        fun getSizesTitles(sizeIds: List<Long>): List<String> {
            val sizeGroups = GlobalAPI.getSizes(user = defaultUser)
            val sizesTitles = mutableListOf<String>()

            for (sizeId in sizeIds) {
                val sizeTitle = sizeGroups
                    .flatMap { sizeGroup -> sizeGroup.sizes }
                    .find { it.id == sizeId }!!.title

                sizesTitles.add(sizeTitle)
            }
            return sizesTitles
        }

        fun getOneSizeTitleByCountry(): String {
            return getSizesTitles(listOf(WOMEN_CLOTHES_SIZE_ID_0)).single()
        }

        fun getCategorySizeTitlesByCatalog(catalog: VintedCatalogs): List<String> = when (catalog) {
            VintedCatalogs.WOMEN -> womenSizeTitles
            VintedCatalogs.MEN -> menSizeTitles
            VintedCatalogs.BABIES -> babiesSizeTitles
        }

        fun getCategoryTitleByCatalog(catalog: VintedCatalogs): String = when (catalog) {
            VintedCatalogs.WOMEN -> womenCategoryTitle
            VintedCatalogs.MEN -> menCategoryTitle
            VintedCatalogs.BABIES -> babiesCategoryTitle
        }
    }
}

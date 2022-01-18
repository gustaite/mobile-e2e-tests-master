package util.values

import commonUtil.testng.config.ConfigManager.portal
import api.controllers.GlobalAPI
import api.data.models.getTextByUserCountry
import api.data.models.getTextsByUserCountry
import commonUtil.data.VintedCountriesTextValue
import commonUtil.data.VintedCountriesTextValues
import commonUtil.data.VintedPortalsTextValue
import commonUtil.data.enums.VintedCatalogs
import commonUtil.data.enums.VintedPortal.*
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import util.Android
import util.AppTexts.userByCountry
import util.EnvironmentManager.isAndroid
import util.IOS
import util.base.BaseTest.Companion.defaultUser
import util.base.BaseTest.Companion.loggedInUser
import util.values.Personalization.Companion.getCategoryTitleByCatalog

class ElementByLanguage {
    companion object {

        fun getElementValueByPlatform(key: String): String {
            return getElementValueByPlatform(androidKey = key, iosKey = key)
        }

        fun getElementValueByPlatform(androidKey: String, iosKey: String): String {
            return if (isAndroid) Android.getElementValue(androidKey) else IOS.getElementValue(iosKey)
        }

        fun chooseValueByPlatform(androidValue: String, iosValue: String): String {
            return if (isAndroid) androidValue else iosValue
        }

        private val forumTopicDiscussionsAboutVintedTextValues = VintedPortalsTextValue(
            int = "AUTRES",
            de = "Mitglieder helfen Mitgliedern - Fragen zu Vinted",
            pl = "Pytania i odpowiedzi użytkowników",
            lt = "Diskusijos apie Vinted",
            cz = "[Nabízím] – Ostatní",
            uk = "Life Outside Vinted",
            us = "Members' Questions and Answers"
        )

        val forumTopicDiscussionsAboutVintedText: String
            get() = PortalFactory.getTextByPortal(portalValues = forumTopicDiscussionsAboutVintedTextValues)

        private val categoryKidsNoShippingItemTextValues = VintedCountriesTextValues(
            fr = listOf("Enfants", "Poussettes"),
            de = listOf("Kinder", "Kinderwagen und Buggies"),
            pl = listOf("Dzieci", "Wózki"),
            lt = listOf("Vaikams", "Vežimukai"),
            uk = listOf("Kids", "Buggies"),
            us = listOf("Kids", "Buggies"),
            cz = listOf("Děti", "Kočárky")
        )

        val categoryKidsNoShippingItemText: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryKidsNoShippingItemTextValues)

        private val categoryHomeBooksItemTextValues = VintedCountriesTextValues(
            fr = listOf("Maison", "Livres"),
            de = listOf("Home", "Bücher"),
            lt = listOf("Namams", "Knygos"),
            uk = listOf("Home", "Books"),
            us = listOf("Home", "Books"),
            cz = listOf("Bydlení", "Knihy")
        )

        val categoryHomeBooksItemText: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryHomeBooksItemTextValues)

        private val categoryHomeTextilesItemTextValues = VintedCountriesTextValues(
            fr = listOf("Maison", "Textiles"),
            de = listOf("Home", "Textilien"),
            lt = listOf("Namams", "Tekstilė"),
            uk = listOf("Home", "Textiles"),
            us = listOf("Home", "Textiles")
        )

        val categoryHomeTextilesItemText: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryHomeTextilesItemTextValues)

        private val categoryHomeAccessoriesItemTextValues = VintedCountriesTextValues(
            fr = listOf("Maison", "Décoration"),
            de = listOf("Home", "Deko"),
            lt = listOf("Namams", "Interjero akcentai"),
            uk = listOf("Home", "Home accessories"),
            us = listOf("Home", "Home accessories"),
            cz = listOf("Bydlení", "Dekorace")
        )

        val categoryHomeAccessoriesItemText: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryHomeAccessoriesItemTextValues)

        private val categoryHomeTablewareItemTextValues = VintedCountriesTextValues(
            fr = listOf("Maison", "Arts de la table"),
            de = listOf("Home", "Essen"),
            lt = listOf("Namams", "Stalo serviravimas"),
            uk = listOf("Home", "Tableware"),
            us = listOf("Home", "Tableware"),
            cz = listOf("Bydlení", "Stolování")
        )

        val CategoryWomenBeautyItem: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryWomenBeautyItemTextValues)

        private val womenCatalogText = getCategoryTitleByCatalog(VintedCatalogs.WOMEN)

        private val categoryWomenBeautyItemTextValues = VintedCountriesTextValues(
            fr = listOf(womenCatalogText, "Beauté"),
            es = listOf(womenCatalogText, "Cuidado y belleza"),
            pt = listOf(womenCatalogText, "Beleza"),
            it = listOf(womenCatalogText, "Bellezza"),
            nl = listOf(womenCatalogText, "Schoonheidsproducten"),
            de = listOf(womenCatalogText, "Pflege & Beauty"),
            lt = listOf(womenCatalogText, "Kosmetika"),
            cz = listOf(womenCatalogText, "Kosmetika"),
            pl = listOf(womenCatalogText, "Kosmetyki"),
            uk = listOf(womenCatalogText, "Beauty"),
            us = listOf(womenCatalogText, "Beauty")
        )

        val CategoryMenBeautyItem: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryMenBeautyItemTextValues)

        private val menCatalogText = getCategoryTitleByCatalog(VintedCatalogs.MEN)

        private val categoryMenBeautyItemTextValues = VintedCountriesTextValues(
            fr = listOf(menCatalogText, "Soins"),
            es = listOf(menCatalogText, "Cuidado y belleza"),
            pt = listOf(menCatalogText, "Cuidados pessoais"),
            it = listOf(menCatalogText, "Bellezza"),
            nl = listOf(menCatalogText, "Schoonheidsproducten"),
            de = listOf(menCatalogText, "Körper- & Gesichtspflege"),
            lt = listOf(menCatalogText, "Kosmetika"),
            cz = listOf(menCatalogText, "Kosmetika"),
            pl = listOf(menCatalogText, "Kosmetyki"),
            uk = listOf(menCatalogText, "Grooming"),
            us = listOf(menCatalogText, "Grooming")
        )

        val categoryHomeTablewareItemText: List<String>
            get() = userByCountry.getTextsByUserCountry(countriesTextValues = categoryHomeTablewareItemTextValues)

        val Size: String
            get() {
                val list = GlobalAPI.getSizes(user = defaultUser)
                val id = 4L
                return list.single { group -> group.id == id }.sizes[0].title
            }

        val threeSizes: List<String>
            get() {
                val list = GlobalAPI.getSizes(user = loggedInUser)
                val id = 4L
                val sizeGroup = list.single { group -> group.id == id }
                return listOf(sizeGroup.sizes[10].title, sizeGroup.sizes[11].title, sizeGroup.sizes[12].title)
            }

        val threeBrands: List<String> = listOf("Derbe", "Davidoff", "Zign")

        val HomeDecorTextileSize: String
            get() {
                val list = GlobalAPI.getSizes(user = defaultUser)
                val id = 55L
                return list.single { group -> group.id == id }.sizes[0].title
            }

        val FirstTwoColors: Pair<String, String>
            get() {
                val list = GlobalAPI.getColors(user = defaultUser)
                return Pair(list[0].title, list[1].title)
            }

        val StandardShippingOptions: List<String>
            get() {
                val list = GlobalAPI.getPackageSizes(user = defaultUser).filter { p -> p.standard }
                return list.map { it.title }
            }

        val CustomShippingOption: String
            get() {
                return GlobalAPI.getPackageSizes(user = defaultUser).first { p -> p.custom }.title
            }

        val NoShippingOption: String?
            get() {
                return GlobalAPI.getPackageSizes(user = defaultUser).find { p -> !p.custom && !p.standard }?.title
            }

        val SortingOptions: List<String>
            get() = listOf(
                getElementValueByPlatform(key = "sort_by_relevance"),
                getElementValueByPlatform(key = "sort_by_price_low_to_high"),
                getElementValueByPlatform(key = "sort_by_price_high_to_low"),
                getElementValueByPlatform(key = "sort_by_newest_first")
            )

        // https://admin.vinted.net/keys/42163
        private val noFiltersSubtitleTextValues = VintedCountriesTextValue(
            fr = "Aucun filtre appliqué",
            de = "Keine Filter",
            pl = "Brak filtrów",
            cz = "Žádný filtr",
            lt = "Nepasirinkti filtrai",
            uk = "No filters",
            us = "No filters"
        )

        val noFiltersSubtitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = noFiltersSubtitleTextValues)

        val DeletedUserUsernamePlaceholder: String
            get() = getElementValueByPlatform(
                androidKey = "user_login_deleted",
                iosKey = "deleted_user"
            )

        val noBrandText: String
            get() = getElementValueByPlatform(key = "use_no_brand")

        private val bumpBannerTextValues = VintedCountriesTextValue(
            fr = "Booste tes articles",
            de = "Pushe deine Artikel",
            pl = "Podbij swoje przedmioty",
            cz = "Topuj své předměty",
            lt = if (portal == SB_LT) "Iškelk savo skelbimus" else "Iškelk savo drabužius",
            uk = "Bump your items",
            us = "Bump your items"
        )

        val bumpBannerText: String get() = userByCountry.getTextByUserCountry(countriesTextValue = bumpBannerTextValues)

        private val closetPromoTextValues = VintedCountriesTextValue(
            fr = "Mets ton Dressing en Vitrine",
            de = "Promote deine Best Matches",
            pl = "Promuj swoją szafę",
            cz = "Propaguj svůj šatník",
            lt = "Išnaudok „Specialiai tau“ skiltį",
            uk = "Spotlight your wardrobe"
        )

        val closetPromoText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = closetPromoTextValues)

        private val closetPromoPromotedTextValues = VintedCountriesTextValue(
            fr = "Ton Dressing en Vitrine",
            de = "Best Matches Promotion",
            pl = "Twoje Sugerowane Oferty",
            cz = "Propagace šatníku",
            lt = "Patalpinta „Specialiai tau“",
            uk = "Wardrobe Spotlight"
        )

        val closetPromoPromotedText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = closetPromoPromotedTextValues)

        private val closetPromoStatisticsPerformanceParagraphTextValues = VintedCountriesTextValue(
            fr = "Ton dressing a été vu",
            de = "Deine Best Matches wurden",
            pl = "Twoje oferty widziano",
            cz = "Tvůj šatník byl zhlédnut",
            lt = "Tavo spintą naujienų sraute ir kataloge nariai matė",
            uk = "Your wardrobe was seen"
        )

        val closetPromoStatisticsPerformanceParagraphText: String
            get() = userByCountry.getTextByUserCountry(
                countriesTextValue = closetPromoStatisticsPerformanceParagraphTextValues
            )

        private val addNewCardIosTextValues = VintedCountriesTextValue(
            fr = "Carte bancaire",
            de = "Kreditkarte",
            pl = "Karta płatnicza",
            cz = "Platební karta",
            lt = "Mokėjimo kortelė' || name Contains 'Pridėti kortelę",
            uk = "Credit card",
            us = "Credit card"
        )

        val addNewCardIosText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = addNewCardIosTextValues)

        private val materialTextileAcrylicTextValues = VintedCountriesTextValue(
            fr = "Acrylique",
            de = "Acryll",
            lt = "Akrilas",
            uk = "Acrylic",
            us = "Acrylic"
        )

        val materialTextileAcrylicText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = materialTextileAcrylicTextValues)

        private val materialMetalTextValues = VintedCountriesTextValue(
            fr = "Métal",
            de = "Metall",
            lt = "Metalas",
            uk = "Metal",
            us = "Metal"
        )

        val materialMetalText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = materialMetalTextValues)

        private val materialCeramicTextValues = VintedCountriesTextValue(
            fr = "Céramique",
            de = "Keramik",
            lt = "Keramika",
            uk = "Ceramic",
            us = "Ceramic"
        )

        val materialCeramicText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = materialCeramicTextValues)

        fun sizeGroupName(sizeList: List<String>): String =
            GlobalAPI.getSizes(user = defaultUser)
                .first { it.sizes.any { size -> size.title == sizeList.first() } }.description

        @Step("Get categories and subcategories list by catalog")
        fun getCategoriesAndSubcategories(catalogNumber: Int): List<String> {
            val catalogList = GlobalAPI.getCatalogs(user = defaultUser)
            val result: MutableList<String> = ArrayList()
            var currentCatalog = catalogList[catalogNumber]

            result.add(currentCatalog.title)
            while (currentCatalog.catalogs.isNotEmpty()) {
                currentCatalog = currentCatalog.catalogs[0]
                result.add(currentCatalog.title)
            }
            return result
        }

        private val bumpLabelTextValues = VintedCountriesTextValue(
            fr = "Boosté",
            de = "Gepusht",
            pl = "Podbity",
            cz = "Topováno",
            lt = "Iškeltas",
            uk = "Bumped",
            us = "Bumped"
        )

        val bumpLabelText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = bumpLabelTextValues)

        private val continueWithEmailTextValues = VintedCountriesTextValue(
            cz = "Nebo pokračuj přes e-mail",
            fr = "Ou continue avec email",
            de = "Oder gehe zu E-Mail",
            it = "Oppure accedi con e-mail",
            lt = "Arba tęsti su el. pašto adresu",
            nl = "Of ga verder met e-mail",
            pl = "Lub użyj adres e-mail",
            es = "Continuar con e-mail",
            uk = "Or continue with email",
            us = "Or continue with email"
        )

        val continueWithEmailText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = continueWithEmailTextValues)

        private val vintedBalanceTextValues = VintedCountriesTextValue(
            fr = "Porte-monnaie",
            de = "Geldbeutel",
            pl = "Portfel",
            cz = "Vinted peněženka",
            lt = "Piniginė",
            uk = "Balance",
            us = "Wallet"
        )

        val vintedBalanceText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = vintedBalanceTextValues)

        private val bumpOrderInvoiceSubtitleTextValues = VintedCountriesTextValue(
            fr = "1 article",
            de = "1 Artikel",
            pl = "1 przedmiot",
            lt = "1 drabužis",
            uk = "1 item",
            us = "1 item",
            cz = "1 předmět"
        )

        val bumpOrderInvoiceSubtitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = bumpOrderInvoiceSubtitleTextValues)

        private val reviewOrderButtonTextValues = VintedCountriesTextValue(
            fr = "Vérifier la commande",
            de = "Auftrag prüfen",
            pl = "Sprawdź zamówienie",
            uk = "Review order",
            us = "Review order",
            lt = "Peržiūrėti užsakymą",
            cz = "Zkontrolovat objednávku"
        )

        val reviewOrderButtonText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = reviewOrderButtonTextValues)

        private val savedCreditCardTextValues = VintedCountriesTextValue(
            fr = "se terminant par",
            de = "endet auf",
            pl = "kończy się z",
            uk = "ending with",
            lt = ", paskutiniai skaitmenys",
            cz = "končící na"
        )

        val savedCreditCardText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = savedCreditCardTextValues)

        private val closetFilterCategoryParkasTextValues = VintedCountriesTextValue(
            fr = "Parkas",
            de = "Parkas",
            pl = "Parki",
            cz = "Parky",
            lt = "Žieminės striukės/parkos",
            uk = "Parkas",
            us = "Parkas"
        )

        val closetFilterCategoryParkasText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = closetFilterCategoryParkasTextValues)

        private val closetFilterScreenTitleTextValues = VintedCountriesTextValue(
            fr = "Filtrer",
            de = "Filter",
            pl = "Filtruj",
            cz = "Filtr",
            lt = "Filtruoti",
            uk = "Filter",
            us = "Filter"
        )

        val closetFilterScreenTitleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = closetFilterScreenTitleTextValues)

        private val brandNotFoundTextValues = VintedCountriesTextValue(
            fr = "Marque non disponible",
            de = "Marke nicht gefunden",
            pl = "Nie znaleziono marki",
            cz = "Značka nebyla nalezena",
            lt = "Prekės ženklas nerastas",
            uk = "Brand not found",
            us = "Brand not found"
        )

        val brandNotFoundText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = brandNotFoundTextValues)

        private val createCustomBrandTextValues = VintedCountriesTextValue(
            fr = "Ajouter la marque",
            de = "erstellen",
            pl = "Utwórz markę",
            cz = "Vytvořit značku",
            lt = "Sukurti prekės ženklą",
            uk = "Create a brand",
            us = "Create a brand"
        )

        val createCustomBrandText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = createCustomBrandTextValues)

        private val addShippingAddressTextValues = VintedCountriesTextValue(
            fr = "Ajouter l'adresse de livraison",
            de = "Versandadresse hinzufügen",
            pl = "Dodaj swój adres dostawy",
            uk = "Add your shipping address",
            cz = "Přidej svoji doručovací adresu",
            lt = "Pridėk siuntimo adresą"
        )

        val addShippingAddressText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = addShippingAddressTextValues)

        private val addPersonalDataTextValues = VintedCountriesTextValue(
            fr = "Ajoute tes informations",
            de = "Vul je gegevens in",
            pl = "Prosimy dodaj swoje dane",
            uk = "Please add your details"
        )

        val addPersonalDataText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = addPersonalDataTextValues)

        private val payPalConfirmButtonTextValues = VintedCountriesTextValue(
            fr = "Suivant",
            de = "Weiter"
        )

        val payPalConfirmButtonText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = payPalConfirmButtonTextValues)

        private val payPalLoginButtonTextValues = VintedCountriesTextValue(
            fr = "Connexion",
            de = "Einloggen"
        )

        val payPalLoginButtonText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = payPalLoginButtonTextValues)

        private val payPalLoginButtonAfterRetryTextValues = VintedCountriesTextValue(
            fr = "Log In",
            de = "Log In"
        )

        val payPalLoginButtonAfterRetryText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = payPalLoginButtonAfterRetryTextValues)

        private val haveTroubleLoggingInTextValues = VintedCountriesTextValue(
            fr = "Vous n'arrivez pas à vous connecter ?",
            de = "Having trouble logging in?"
        )

        val haveTroubleLoggingInText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = haveTroubleLoggingInTextValues)

        private val defaultEmailAddressTextValues = VintedCountriesTextValue(
            fr = "Email ou numéro de mobile",
            de = "E-Mail-Adresse oder Handynummer"
        )

        val defaultEmailAddressText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = defaultEmailAddressTextValues)

        val getExpectedProfileId: Long
            get() {
                return when (portal) {
                    DE -> 51312853
                    LT -> 55040904
                    INT -> 47194649
                    US -> 71037496
                    UK -> 56994306
                    CZ -> 75464284
                    PL -> 7186000
                    else -> throw NotImplementedError("Portal is not supported")
                }
            }

        private val deviceCharacteristicsCmpToggleTextValues = VintedCountriesTextValue(
            fr = "Analyser activement les caractéristiques du terminal pour l’identification",
            it = "Scansione attiva delle caratteristiche del dispositivo ai fini dell’identificazione",
            nl = "De apparaat kenmerken actief scannen voor identificatie",
            es = "Analizar activamente las características del dispositivo para su identificación",
            de = "Sicherheit gewährleisten, Betrug verhindern und Fehler beheben",
            pl = "Zapewnienie bezpieczeństwa, zapobieganie oszustwom i usuwanie błędów",
            uk = "Actively scan device characteristics for identification",
            us = "Actively scan device characteristics for identification",
            lt = "Aktyviai skenuoti įrenginio charakteristikas identifikavimo tikslais",
            cz = "Aktivní vyhledávání identifikačních údajů v rámci vlastností zařízení"
        )

        val deviceCharacteristicsCmpToggleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = deviceCharacteristicsCmpToggleTextValues)

        private val functionalCmpToggleTextValues = VintedCountriesTextValue(
            fr = "Cookies de fonctionnalité",
            it = "Cookie di funzionalità",
            nl = "Functionele cookies",
            es = "Cookies de funcionalidad",
            de = "Funktionelle Cookies",
            pl = "Funkcjonalne pliki cookie",
            uk = "Functional Cookies",
            us = "Functional Cookies",
            lt = "Funkciniai slapukai",
            cz = "Soubory cookie pro lepší funkčnost"
        )

        val functionalCmpToggleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = functionalCmpToggleTextValues)

        private val performanceCmpToggleTextValues = VintedCountriesTextValue(
            fr = "Cookies de performance",
            it = "Cookie di prestazione",
            nl = "Prestatiecookies",
            es = "Cookies de rendimiento",
            de = "Leistungs-Cookies",
            pl = "Pliki cookie wydajności",
            uk = "Performance Cookies",
            us = "Performance Cookies",
            lt = "Našumo slapukai",
            cz = "Soubory cookie pro zvýšení výkonu"
        )

        val performanceCmpToggleText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = performanceCmpToggleTextValues)

        private val vintedProCatalogUploadErrorTextValues = VintedCountriesTextValue(
            fr = "Vinted Pro users cannot upload to this catalog"
        )

        val vintedProCatalogUploadErrorText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = vintedProCatalogUploadErrorTextValues)

        private val minutesTextValues = VintedCountriesTextValue(
            cz = "minutami",
            fr = "minutes",
            de = "Min",
            it = "min",
            lt = "min",
            nl = "min",
            pl = "min",
            es = "minutos",
            uk = "min",
            us = "min"
        )

        val minutesText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = minutesTextValues)

        private val hoursTextValues = VintedCountriesTextValue(
            cz = "hod",
            fr = "heure",
            de = "h",
            it = "or",
            lt = "val.",
            nl = "geleden",
            pl = "godz",
            es = "horas",
            uk = "hour",
            us = "hour"
        )

        val hoursText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = hoursTextValues)

        private val daysTextValues = VintedCountriesTextValue(
            cz = "dny",
            fr = "jour",
            de = "Tag",
            it = "giorno",
            lt = "d.",
            nl = "dag",
            pl = "dni",
            es = "días",
            uk = "day",
            us = "day"
        )

        val daysText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = daysTextValues)

        private val justNowTextValue = VintedCountriesTextValue(
            cz = "před chvílí",
            fr = "a l'instant",
            de = "gerade eben",
            it = "proprio adesso",
            lt = "ką tik",
            nl = "even geleden",
            pl = "teraz",
            es = "hace nada",
            uk = "just now",
            us = "just now"
        )

        val justNowText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = justNowTextValue)

        private val yesterdayTextValue = VintedCountriesTextValue(
            cz = "včera",
            fr = "hier",
            de = "gestern",
            it = "ieri",
            lt = "vakar",
            nl = "gisteren",
            pl = "wczoraj",
            es = "ayer",
            uk = "yesterday",
            us = "yesterday"
        )

        val yesterdayText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = yesterdayTextValue)

        private val contactUsTextValues = VintedCountriesTextValue(
            cz = "Kontaktuj nás",
            fr = "Contacte-nous",
            de = "Kontaktiere uns",
            it = "Contattaci",
            lt = "Susisiek su mumis",
            nl = "Neem contact op",
            pl = "Napisz do nas",
            es = "Escríbenos",
            uk = "Contact us",
            us = "Contact us"
        )

        val contactUsText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = contactUsTextValues)

        private val postalCodeTextValues = VintedCountriesTextValue(
            cz = "PSČ",
            fr = "Code postal",
            de = "Postleitzahl",
            it = "CAP",
            lt = "Pašto kodas",
            nl = "Postcode",
            pl = "Kod pocztowy",
            es = "Código postal",
            uk = "Postcode",
            us = "Postcode' || name == 'ZIP Code"
        )

        val postalCodeText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = postalCodeTextValues)
    }
}

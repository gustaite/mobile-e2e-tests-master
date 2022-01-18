package util.values

import api.data.models.getTextByUserCountry
import api.data.models.getTextsByUserCountry
import commonUtil.data.VintedCountriesTextValue
import commonUtil.data.VintedCountriesTextValues
import util.AppTexts.userByCountry

object ConversationElementTexts {
    private val transactionCancelledTextValues = VintedCountriesTextValue(
        fr = "Expédition annulée",
        de = "Bestellung storniert",
        pl = "Anulowana transakcja",
        uk = "Cancelled transaction",
        us = "Cancelled transaction",
        cz = "Stornovaná transakce",
        lt = "Sandoris atšauktas"
    )

    val transactionCancelledText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = transactionCancelledTextValues)

    private val refundProcessedTextValues = VintedCountriesTextValue(
        fr = "Remboursement en cours",
        de = "Rückerstattung begonnen",
        pl = "Rozpoczęto zwrot środków",
        uk = "Refund started",
        us = "Refund started",
        cz = "Zažádáno o vrácení peněz",
        lt = "Pinigų grąžinimas pradėtas"
    )

    val refundProcessedText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = refundProcessedTextValues)

    private val transactionSuspendedTextValues = VintedCountriesTextValue(
        fr = "Transaction suspendue",
        de = "Bestellvorgang unterbrochen",
        pl = "Zamówienie zawieszone",
        uk = "Order suspended",
        us = "Order suspended",
        cz = "Objednávka pozastavena",
        lt = "Užsakymas sustabdytas"
    )

    val transactionSuspendedText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = transactionSuspendedTextValues)

    private val transactionSubmittedToSupportTextValues = VintedCountriesTextValue(
        fr = "L'équipe support de Vinted étudie ce litige",
        de = "Das Problem wird von unserem Team bearbeitet",
        pl = "Zespół Vinted rozpatruje ten problem",
        uk = "Vinted's support team is reviewing this issue",
        us = "Vinted's support team is reviewing this issue",
        cz = "Uživatelská podpora Vinted právě posuzuje tento problém",
        lt = "Vinted komanda peržiūri šią problemą"
    )

    val transactionSubmittedToSupportText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = transactionSubmittedToSupportTextValues)

    private val transactionItemsReuploadedTextValues = VintedCountriesTextValue(
        fr = "Article remis en ligne",
        de = "Der Artikel wurde wieder eingestellt!",
        pl = "Dodano ponownie!",
        uk = "Re-uploaded!",
        us = "Re-uploaded!",
        cz = "Nahráno znovu!",
        lt = "Įkelta!"
    )

    val transactionItemsReuploadedText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = transactionItemsReuploadedTextValues)

    private val generateLabelTextValues = VintedCountriesTextValue(
        fr = "Télécharger le bordereau d'envoi",
        de = "Versandschein anfordern",
        pl = "Pobierz etykietę wysyłkową",
        uk = "Get shipping label",
        us = "Get shipping label",
        cz = "Získat kód/štítek",
        lt = "Gauti siuntos etiketę"
    )

    val generateLabelText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = generateLabelTextValues)

    private val downloadLabelTextValues = VintedCountriesTextValue(
        fr = "Télécharger",
        de = "Herunterladen",
        pl = "Pobierz",
        uk = "Download",
        us = "Download"
    )

    val downloadLabelText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = downloadLabelTextValues)

    private val codeLabelTextValues = VintedCountriesTextValue(
        cz = "Kód pro odeslání zásilky je:",
        lt = "Tavo siuntos kodas:"
    )

    val codeLabelText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = codeLabelTextValues)

    private val reuploadItemTextValues = VintedCountriesTextValue(
        fr = "Republier",
        de = "Erneut hochladen",
        pl = "Dodaj ponownie",
        uk = "Re-upload item",
        us = "Re-upload item",
        cz = "Vystavit znovu",
        lt = "Įkelti prekę iš naujo"
    )

    val reuploadItemText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = reuploadItemTextValues)

    private val alreadySentTextValues = VintedCountriesTextValue(
        fr = "Non, j'ai déjà expédié",
        de = "Zu spät. Bereits verschickt",
        pl = "Nie, już wysłane",
        uk = "No, already sent",
        us = "No, already sent",
        cz = "Ne, již odesláno",
        lt = "Ne, jau išsiųsta"
    )

    val alreadySentText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = alreadySentTextValues)

    private val trackParcelTextValues = VintedCountriesTextValue(
        fr = "Suivre le colis",
        de = "Sendung verfolgen"
    )

    val trackParcelText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = trackParcelTextValues)

    private val haveProblemTextValues = VintedCountriesTextValue(
        fr = "J'ai un problème",
        de = "Ich habe ein Problem",
        pl = "Mam problem",
        uk = "I have an issue",
        us = "I have an issue",
        cz = "Mám problém",
        lt = "Turiu problemą"
    )

    val haveProblemText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = haveProblemTextValues)

    private val everythingIsOkTextValues = VintedCountriesTextValue(
        fr = "Tout est OK",
        de = "Alles in Ordnung",
        pl = "Wszystko OK",
        uk = "Everything is OK",
        us = "Everything is OK",
        cz = "Vše je OK",
        lt = "Viskas gerai"
    )

    val everythingIsOkText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = everythingIsOkTextValues)

    private val deliveryInstructionsTextValues = VintedCountriesTextValue(
        fr = "Instructions de livraison",
        de = "Versandanleitung"
    )

    val deliveryInstructionsText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = deliveryInstructionsTextValues)

    private val goToBalanceTextValues = VintedCountriesTextValue(
        fr = "Porte-monnaie",
        de = "Zum Geldbeutel"
    )

    val goToBalanceText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = goToBalanceTextValues)

    private val shipmentInformationTextValues = VintedCountriesTextValue(
        fr = "informations de suivi",
        de = "Sendungsinformationen"
    )

    val shipmentInformationText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = shipmentInformationTextValues)

    private val issueDetailsTextValues = VintedCountriesTextValue(
        fr = "Voir les détails du litige",
        de = "Problem ansehen",
        pl = "Zobacz szczegóły sporu",
        uk = "View issue details",
        us = "View issue details",
        cz = "Zobrazit detaily stížnosti",
        lt = "Problemos informacija"
    )

    val issueDetailsText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = issueDetailsTextValues)

    private val resolveIssueTextValues = VintedCountriesTextValue(
        fr = "Clôturer le litige",
        de = "Problem lösen",
        pl = "Zakończ spór",
        uk = "Resolve this issue",
        us = "Resolve this issue",
        cz = "Vyřešit problém",
        lt = "Problema išspręsta"
    )

    val resolveIssueText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = resolveIssueTextValues)

    // ToDo add new values when https://admin.vinted.net/keys/73422 will be updated
    private val cancelAndKeepTextValues = VintedCountriesTextValue(
        fr = "Clôturer le litige",
        de = "Problem lösen",
        pl = "Zakończ spór",
        uk = "Cancel & keep item",
        us = "Cancel & keep item",
        cz = "Vyřešit problém",
        lt = "Problema išspręsta"
    )

    val cancelAndKeepText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = cancelAndKeepTextValues)

    private val provideProofTextValues = VintedCountriesTextValue(
        fr = "Envoyer une preuve",
        de = "Beweise vorlegen",
        pl = "Dostarczenie dowodu",
        uk = "Provide proof",
        us = "Provide proof",
        cz = "Poskytnout důkazy",
        lt = "Pateik įrodymų"
    )

    val provideProofText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = provideProofTextValues)

    private val continueToRefundTextValues = VintedCountriesTextValue(
        fr = "Rembourser l'acheteur",
        de = "Weiter zur Rückerstattung",
        pl = "Dokonaj zwrotu środków",
        uk = "Proceed & refund",
        us = "Proceed & refund",
        cz = "Pokračovat a vrátit peníze",
        lt = "Grąžinti pinigus"
    )

    val continueToRefundText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = continueToRefundTextValues)

    private val agreeAndSubmitTextValues = VintedCountriesTextValue(
        fr = "Accepter et envoyer",
        de = "Anliegen weiterleiten",
        pl = "Zaakceptuj i potwierdź",
        uk = "Agree and submit",
        us = "Agree and submit",
        cz = "Souhlasím, odeslat",
        lt = "Sutikti ir išsiųsti"
    )

    val agreeAndSubmitText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = agreeAndSubmitTextValues)

    private val okButtonTextValues = VintedCountriesTextValue(
        fr = "Confirmer",
        de = "OK",
        pl = "OK",
        uk = "OK",
        us = "OK",
        cz = "OK",
        lt = "Gerai"
    )

    val okButtonText: String
        get() = userByCountry.getTextByUserCountry(countriesTextValue = okButtonTextValues)

    // temp text until translations are ready
    private val parcelShipmentFromOptionsTextValues = VintedCountriesTextValues(
        fr = listOf("From mailbox", "From post office"),
        de = listOf("From mailbox", "From post office"),
        pl = listOf("From mailbox", "From post office"),
        uk = listOf("From mailbox", "From post office"),
        us = listOf("From mailbox", "From post office"),
        cz = listOf("From mailbox", "From post office"),
        lt = listOf("From mailbox", "From post office")
    )

    val parcelShipmentFromOptionsText: List<String>
        get() = userByCountry.getTextsByUserCountry(countriesTextValues = parcelShipmentFromOptionsTextValues)

    private val removedMessagePreviewTextValues = VintedCountriesTextValue(
        fr = "Message supprimé ",
        de = "Nachricht gelöscht",
        pl = "Wiadomość usunięta",
        cz = "Smazaná zpráva",
        lt = "Žinutė ištrinta",
        uk = "Message deleted",
        us = "Message deleted"
    )

    val removedMessagePreviewText: String get() = userByCountry.getTextByUserCountry(countriesTextValue = removedMessagePreviewTextValues)

    val removedMessageByUserText: String
        get() = ElementByLanguage.getElementValueByPlatform(key = "conversation_removed_message_by_user")
}

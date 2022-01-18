import robot.*
import robot.browse.*
import robot.bumps.*
import robot.closetpromo.*
import robot.cmp.CmpCookiesRobot
import robot.cmp.CmpCookiesSettingsRobot
import robot.cmp.CmpVendorsRobot
import robot.forum.*
import robot.global.GlobalRobot
import robot.holidaymode.HolidayBannerRobot
import robot.holidaymode.HolidayModeRobot
import robot.inbox.InboxRobot
import robot.inbox.NewMessageRobot
import robot.inbox.conversation.*
import robot.item.*
import robot.notificationsettings.NotificationSettingsRobot
import robot.payments.*
import robot.personalization.*
import robot.profile.*
import robot.profile.balance.*
import robot.profile.settings.*
import robot.profile.tabs.ProfileAboutTabRobot
import robot.profile.tabs.UserProfileClosetRobot
import robot.profile.tabs.UserProfileRobot
import robot.section.UserShortInfoSectionRobot
import robot.upload.*
import robot.upload.photo.*
import robot.webview.SecurityWebViewRobot
import robot.webview.WebViewRobot
import robot.welcome.*
import robot.workflow.*
import util.deepLinks.DeepLink

object RobotFactory {
    val workflowRobot: WorkflowRobot get() = WorkflowRobot()

    val shipmentWorkflowRobot: ShipmentWorkflowRobot get() = ShipmentWorkflowRobot()

    val navigationRobot: NavigationRobot get() = NavigationRobot()

    val welcomeRobot: WelcomeRobot get() = WelcomeRobot()

    val signInRobot: SignInRobot get() = SignInRobot()

    val signUpRobot: SignUpRobot get() = SignUpRobot()

    val deleteAccountRobot: DeleteAccountRobot get() = DeleteAccountRobot()

    val forumHomeRobot: ForumHomeRobot get() = ForumHomeRobot()

    val createForumTopicRobot: CreateForumTopicRobot get() = CreateForumTopicRobot()

    val forumInnerRobot: ForumInnerRobot get() = ForumInnerRobot()

    val forumTopicInnerRobot: ForumTopicInnerRobot get() = ForumTopicInnerRobot()

    val forumWorkflowRobot: ForumWorkflowRobot get() = ForumWorkflowRobot()

    val forumSavedTopicsRobot: ForumSavedTopicsRobot get() = ForumSavedTopicsRobot()

    val forumMyTopicsRobot: ForumMyTopicsRobot get() = ForumMyTopicsRobot()

    val uploadItemRobot: UploadItemRobot get() = UploadItemRobot()

    val cameraAndGalleryWorkflowRobot: CameraAndGalleryWorkflowRobot get() = CameraAndGalleryWorkflowRobot()

    val userProfileRobot: UserProfileRobot get() = UserProfileRobot()

    val userProfileEditRobot: UserProfileEditRobot get() = UserProfileEditRobot()

    val userAccountSettingsRobot: UserAccountSettingsRobot get() = UserAccountSettingsRobot()

    val notificationRobot: PushNotificationRobot get() = PushNotificationRobot()

    val shippingOptionRobot: ShippingOptionRobot get() = ShippingOptionRobot()

    val deepLink: DeepLink get() = DeepLink()

    val conversationRobot: ConversationRobot get() = ConversationRobot()

    val inAppNotificationRobot: InAppNotificationRobot get() = InAppNotificationRobot()

    val inboxRobot: InboxRobot get() = InboxRobot()

    val settingsRobot: SettingsRobot get() = SettingsRobot()

    val pushNotificationRobot: PushNotificationRobot get() = PushNotificationRobot()

    val catalogRobot: CatalogRobot get() = CatalogRobot()

    val itemRobot: ItemRobot get() = ItemRobot()

    val bundleRobot: BundleRobot get() = BundleRobot()

    val notificationSettingsRobot: NotificationSettingsRobot get() = NotificationSettingsRobot()

    val userProfileClosetRobot: UserProfileClosetRobot get() = UserProfileClosetRobot()

    val paymentsIdentityRobot: PaymentsIdentityRobot get() = PaymentsIdentityRobot()

    val itemDeleteConfirmationRobot: ItemDeleteModalConfirmationRobot get() = ItemDeleteModalConfirmationRobot()

    val reserveRobot: ReserveRobot get() = ReserveRobot()

    val deletionReasonRobot: DeletionReasonRobot get() = DeletionReasonRobot()

    val sellRobot: SellRobot get() = SellRobot()

    val feedbackFormRobot: FeedbackFormRobot get() = FeedbackFormRobot()

    val rateAppRobot: RateAppRobot get() = RateAppRobot()

    val fullImageRobot: FullImageRobot get() = FullImageRobot()

    val swapRobot: SwapRobot get() = SwapRobot()

    val reportReasonRobot: ReportReasonRobot get() = ReportReasonRobot()

    val feedRobot: FeedRobot get() = FeedRobot()

    val personalizationRobot: PersonalizationRobot get() = PersonalizationRobot()

    val personalizationBrandRobot: PersonalizationBrandRobot get() = PersonalizationBrandRobot()

    val browseRobot: BrowseRobot get() = BrowseRobot()

    val filtersRobot: FiltersRobot get() = FiltersRobot()

    val conditionRobot: ConditionRobot get() = ConditionRobot()

    val sizeRobot: SizeRobot get() = SizeRobot()

    val categoriesRobot: CategoriesRobot get() = CategoriesRobot()

    val brandRobot: BrandRobot get() = BrandRobot()

    val materialRobot: MaterialRobot get() = MaterialRobot()

    val cameraRobot: CameraRobot get() = CameraRobot()

    val galleryRobot: GalleryRobot get() = GalleryRobot()

    val iOSPermissionRobot: IOSPermissionRobot get() = IOSPermissionRobot()

    val uploadPhotoActionRobot: UploadPhotoActionRobot get() = UploadPhotoActionRobot()

    val globalRobot: GlobalRobot get() = GlobalRobot()

    val suspiciousPhotoRobot: SuspiciousPhotoRobot get() = SuspiciousPhotoRobot()

    val personalizationWorkflowRobot: PersonalizationWorkflowRobot get() = PersonalizationWorkflowRobot()

    val searchScreenRobot: SearchScreenRobot get() = SearchScreenRobot()

    val categoriesAndSizesRobot: CategoriesAndSizesRobot get() = CategoriesAndSizesRobot()

    val holidayModeRobot: HolidayModeRobot get() = HolidayModeRobot()

    val holidayBannerRobot: HolidayBannerRobot get() = HolidayBannerRobot()

    val bumpsPreCheckoutRobot: BumpsPreCheckoutRobot get() = BumpsPreCheckoutRobot()

    val colorRobot: ColorRobot get() = ColorRobot()

    val brandActionsRobot: BrandActionsRobot get() = BrandActionsRobot()

    val webViewRobot: WebViewRobot get() = WebViewRobot()

    val securityWebViewRobot: SecurityWebViewRobot get() = SecurityWebViewRobot()

    val bumpsItemsSelectionRobot: BumpsItemsSelectionRobot get() = BumpsItemsSelectionRobot()

    val newCreditCardRobot: NewCreditCardRobot get() = NewCreditCardRobot()

    val checkoutRobot: CheckoutRobot get() = CheckoutRobot()

    val billingAddressRobot: BillingAddressRobot get() = BillingAddressRobot()

    val paymentAccountDetailsRobot: PaymentAccountDetailsRobot get() = PaymentAccountDetailsRobot()

    val paymentWorkflowRobot: PaymentWorkflowRobot get() = PaymentWorkflowRobot()

    val newMessageRobot: NewMessageRobot get() = NewMessageRobot()

    val dropOffPointSelectionRobot: DropOffPointSelectionRobot get() = DropOffPointSelectionRobot()

    val homeDeliverySelectionRobot: HomeDeliverySelectionRobot get() = HomeDeliverySelectionRobot()

    val checkoutWorkflowRobot: CheckoutWorkflowRobot get() = CheckoutWorkflowRobot()

    val paymentMethodsRobot: PaymentMethodsRobot get() = PaymentMethodsRobot()

    val languageSelectionRobot: LanguageSelectionRobot get() = LanguageSelectionRobot()

    val brandAuthenticationRobot: BrandAuthenticationRobot get() = BrandAuthenticationRobot()

    val shippingScreenRobot: ShippingScreenRobot get() = ShippingScreenRobot()

    val addressRobot: AddressRobot get() = AddressRobot()

    val favoriteItemsRobot: FavoriteItemsRobot get() = FavoriteItemsRobot()

    val cmpCookiesRobot: CmpCookiesRobot get() = CmpCookiesRobot()

    val closetPromoCheckoutRobot: ClosetPromoCheckoutRobot get() = ClosetPromoCheckoutRobot()

    val closetPromoPreCheckoutRobot: ClosetPromoPreCheckoutRobot get() = ClosetPromoPreCheckoutRobot()

    val closetPromoStatisticRobot: ClosetPromoStatisticRobot get() = ClosetPromoStatisticRobot()

    val userShortInfoSectionRobot: UserShortInfoSectionRobot get() = UserShortInfoSectionRobot()

    val paymentsScreenRobot: PaymentsScreenRobot get() = PaymentsScreenRobot()

    val modalRobot: ModalRobot get() = ModalRobot()

    val ordersRobot: OrdersRobot get() = OrdersRobot()

    val bumpsCheckoutRobot: BumpsCheckoutRobot get() = BumpsCheckoutRobot()

    val bumpOrderDetailsRobot: BumpOrderDetailsRobot get() = BumpOrderDetailsRobot()

    val bumpStatisticsRobot: BumpStatisticsRobot get() = BumpStatisticsRobot()

    val isbnRobot: ISBNRobot get() = ISBNRobot()

    val photoTipModalRobot: PhotoTipModalRobot get() = PhotoTipModalRobot()

    val dataSettingsRobot: DataSettingsRobot get() = DataSettingsRobot()

    val profileTabRobot: ProfileTabRobot get() = ProfileTabRobot()

    val navigationWorkflowRobot: NavigationWorkflowRobot get() = NavigationWorkflowRobot()

    val dataSettingsWorkflowRobot: DataSettingsWorkflowRobot get() = DataSettingsWorkflowRobot()

    val offerWorkflowRobot: OfferWorkflowRobot get() = OfferWorkflowRobot()

    val parcelSizeRobot: ParcelSizeRobot get() = ParcelSizeRobot()

    val shippingOptionsEducationRobot: ShippingOptionsEducationRobot get() = ShippingOptionsEducationRobot()

    val problemWorkflowRobot: ProblemWorkflowRobot get() = ProblemWorkflowRobot()

    val profileAboutTabRobot: ProfileAboutTabRobot get() = ProfileAboutTabRobot()

    val payPalRobot: PayPalRobot get() = PayPalRobot()

    val closetPromoWorkflowRobot: ClosetPromoWorkflowRobot get() = ClosetPromoWorkflowRobot()

    val bumpWorkflowRobot: BumpWorkflowRobot get() = BumpWorkflowRobot()

    val blikAndDotPayRobot: BlikAndDotPayRobot get() = BlikAndDotPayRobot()

    val followingRobot: FollowingRobot get() = FollowingRobot()

    val userProfileEditWorkflowRobot: UserProfileEditWorkflowRobot get() = UserProfileEditWorkflowRobot()

    val actionBarRobot: ActionBarRobot get() = ActionBarRobot()

    val iDealRobot: IDealRobot get() = IDealRobot()

    val sofortRobot: SofortRobot get() = SofortRobot()

    val userProfileClosetOrganiseRobot: UserProfileClosetOrganiseRobot get() = UserProfileClosetOrganiseRobot()

    val userProfileClosetWorkflowRobot: UserProfileClosetWorkflowRobot get() = UserProfileClosetWorkflowRobot()

    val uploadFormWorkflowRobot: UploadFormWorkflowRobot get() = UploadFormWorkflowRobot()

    val deviceSettingsChangeWorkflowRobot: DeviceSettingsChangeWorkflowRobot get() = DeviceSettingsChangeWorkflowRobot()

    val helpCenterRobot: HelpCenterRobot get() = HelpCenterRobot()

    val shipmentTrackingRobot: ShipmentTrackingRobot get() = ShipmentTrackingRobot()

    val cmpCookiesSettingsRobot: CmpCookiesSettingsRobot get() = CmpCookiesSettingsRobot()

    val sellerPoliciesRobot: SellerPoliciesRobot get() = SellerPoliciesRobot()

    val editablePoliciesSettingsRobot: EditablePoliciesSettingsRobot get() = EditablePoliciesSettingsRobot()

    val itemProRobot: ItemProRobot get() = ItemProRobot()

    val luxuryItemRobot: LuxuryItemRobot get() = LuxuryItemRobot()

    val luxuryItemUploadWorkflowRobot: LuxuryItemUploadWorkflowRobot get() = LuxuryItemUploadWorkflowRobot()

    val filtersWorkflow: FiltersWorkflow get() = FiltersWorkflow()

    val contactDetailsRobot: ContactDetailsRobot get() = ContactDetailsRobot()

    val cmpWorkflowRobot: CmpWorkflowRobot get() = CmpWorkflowRobot()

    val buyerProtectionProRobot: BuyerProtectionProRobot get() = BuyerProtectionProRobot()

    val cmpVendorsRobot: CmpVendorsRobot get() = CmpVendorsRobot()

    val webPhotoRobot: WebPhotoRobot get() = WebPhotoRobot()

    val labelDeliveryRobot: LabelDeliveryRobot get() = LabelDeliveryRobot()

    val withdrawalRobot: WithdrawalRobot get() = WithdrawalRobot()

    val withdrawalSettingsRobot: WithdrawalSettingsRobot get() = WithdrawalSettingsRobot()

    val walletRobot: WalletRobot get() = WalletRobot()

    val conversationWorkflowRobot: ConversationWorkflowRobot get() = ConversationWorkflowRobot()

    val contextMenuRobot: ContextMenuRobot get() = ContextMenuRobot()

    val delayedPublicationRobot: DelayedPublicationRobot get() = DelayedPublicationRobot()

    val delayedPublicationWorkflowRobot: DelayedPublicationWorkflowRobot get() = DelayedPublicationWorkflowRobot()
}

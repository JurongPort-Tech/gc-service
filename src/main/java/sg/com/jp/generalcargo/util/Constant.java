package sg.com.jp.generalcargo.util;

public interface Constant {
	public static final String MISCTYPECD_TERMINAL_CD = "TERM_CD";
	public static final String MISCTYPECD_SHIFT = "LW_SHIFT";
	public static final String TEXTPARACD_CS_TO_TERMINAL_CODE_PREFIX = "LW_CS_";
	public static final String TEXTPARACD_CASHSALES_CUST_ACCT_PREFIX = "ACCT";
	public static final String TEXTPARACD_CASHSALES_CUST_CODE_PREFIX = "CODE";
	public static final String DSA_BILL_STATUS_READY = "N";

	public interface ConfigProperties {

		public interface EMAIL {
			public static final String IS_TOPSMAILSESSION = "exception.report.email.isTopsMailSession";
			public static final String SMTP_DEBUG = "exception.report.email.smtpDebug";
			public static final String SMTP_AUTHENTICATION = "exception.report.email.smtpAuthentication";
			public static final String SMTP_HOST = "exception.report.email.smtpHost";
			public static final String SMTP_USER = "exception.report.email.smtpUser";
			public static final String SMTP_PASSWORD = "exception.report.email.smtpPassword";
			public static final String SMTP_PORT = "exception.report.email.smtpPort";
			public static final String SEND_TIMEOUT = "exception.report.email.sendTimeout";
			public static final String CONNECTION_TIMEOUT = "exception.report.email.connectionTimeout";
			public static final String RETRY_WAIT = "exception.report.email.retryWait";
			public static final String ADMIN_EMAIL = "exception.report.email.adminEmail";
			public static final String FROM = "exception.report.email.from";
			public static final String TO = "exception.report.email.to";
			public static final String SUBJECT = "ExceptionReport.email.subject";
			public static final String CONTENT = "ExceptionReport.email.content";
			public static final String ATTACHEDFILE_NAME = "ExceptionReport.attachedFileName";
			public static final String UNCLOSED_LCT_SUBJECT = "GBMS.UnclosedLct.Subject";
			public static final String UNCLOSED_LCT_SENDER = "GBMS.UnclosedLct.Sender";
			public static final String UNCLOSED_LCT_RECIPIENTS = "GBMS.UnclosedLct.Recipients";
			// Added by ZanFeng start
			public static final String BTRREQUEST_EMAILMESSAGE = "GB.BtrRequest.EmailMessage";
			public static final String BTRREQUEST_REJECT_EMAILMESSAGE = "GB.BtrRequest.Reject.EmailMessage";
			// Added by ZanFeng end
			// Added by zhengjiliang 22/7/2011
			public static final String BTRREQUEST_REJECT_SENDER = "GB.BtrRequest.Reject.Sender";
			// end

			// Added by zhengjiliang 18/8/2011 start
			public static final String NEWBA_SENDER_EMAIL = "NewBA.Sender.Email";

			// added by Dongsheng on 15/9/2015
			public static final String VSLCR_SENDER_EMAIL = "VSLCr.Sender.Email";
			public static final String GB_BTRREQUEST_SENDER = "GB.BtrRequest.Sender";
			public static final String NEWBA_SYSTEM_EMAIL = "NewBA.System.Email";

			public static final String NEWBA_VOYAGE_UPDATE_EMAIL = "NewBA.Voyage.Update.Email";
			public static final String NEWBA_VOYAGE_UPDATE_MESSAGE = "NewBA.Voyage.Update.Message";
			public static final String NEWBA_VOYAGE_UPDATE_MESSAGE_SUBJECT = "NewBA.Voyage.Update.Message.Subject";

			public static final String GB_NEWBA_TANDEM_LIFT_EMAIL = "GB.NewBA.Tandem.Lift.Email";
			public static final String CT_NEWBA_TANDEM_LIFT_EMAIL = "CT.NewBA.Tandem.Lift.Email";
			public static final String NEWBA_FLOATING_CRANE_EMAIL = "NewBA.Floating.Crane.Email";
			public static final String GB_HEAVY_LIFT_OVER_WHARF_EMAIL = "GB.Heavy.Lift.Over.Wharf.Email";
			public static final String CT_HEAVY_LIFT_OVER_WHARF_EMAIL = "CT.Heavy.Lift.Over.Wharf.Email";

			public static final String NEWBA_SELECTED_MESSAGE = "NewBA.Selected.Message";
			public static final String NEWBA_CANCELED_MESSAGE = "NewBA.Canceled.Message";
			public static final String NEWBA_SELECTED_MESSAGE_SUBJECT = "NewBA.Selected.Message.Subject";
			public static final String NEWBA_CANCELED_MESSAGE_SUBJECT = "NewBA.Canceled.Message.Subject";
			// end

			// Start CR-OPS- 0000187 - Unify Berth Application
			public static final String NEWBA_UPDATE_GC_OPERATIONS_EMAIL = "NewBA.Update.gcOperations.Email";
			public static final String NEWBA_UPDATE_GC_OPERATIONS_SELECTED_MESSAGE = "NewBA.Update.gcOperations.Selected.Message";
			public static final String NEWBA_UPDATE_GC_OPERATIONS_SELECT_MESSAGE_SUBJECT = "NewBA.Update.gcOperations.Selected.Message.Subject";
			public static final String NEWBA_UPDATE_GC_OPERATIONS_CANCELED_MESSAGE_SUBJECT = "NewBA.Update.gcOperations.Canceled.Message.Subject";
			// end CR-OPS- 0000187 - Unify Berth Application

			// Chue Thing added for ba enhancement
			public static final String NEWBA_MOBILECRANECHECK_FROM = "NewBA.MobileCraneCheck.From";
			public static final String NEWBA_MOBILECRANECHECK_SUBJECT = "NewBA.MobileCraneCheck.Subject";
			public static final String NEWBA_MOBILECRANECHECK_RECIPIENT = "NewBA.MobileCraneCheck.Recipient";
			public static final String NEWBA_MOBILECRANECHECK_MESSAGE = "NewBA.MobileCraneCheck.Message";

			public static final String NEWBA_ASSIGNED_STEVEDORE_FROM = "NewBA.Assigned.Stevedore.From";
			public static final String NEWBA_ASSIGNED_STEVEDORE_SUBJECT = "NewBA.Assigned.Stevedore.Subject";
			public static final String NEWBA_ASSIGNED_STEVEDORE_MESSAGE = "NewBA.Assigned.Stevedore.Message";

			public static final String NEWBA_REMOVAL_STEVEDORE_FROM = "NewBA.Removal.Stevedore.From";
			public static final String NEWBA_REMOVAL_STEVEDORE_SUBJECT = "NewBA.Removal.Stevedore.Subject";
			public static final String NEWBA_REMOVAL_STEVEDORE_MESSAGE = "NewBA.Removal.Stevedore.Message";

			// Added by zhengjiliang 18/9/2011
			public static final String NEWBA_CANCELEDBA_FROM_AGENT_SUBJECT = "NewBA.CanceledBA.From.Agent.Subject";
			public static final String NEWBA_CANCELEDBA_FROM_AGENT_MESSAGE = "NewBA.CanceledBA.From.Agent.Message";
			public static final String NEWBA_CANCELEDBA_FROM_AGENT_RECIPIENTADDRESS = "NewBA.CanceledBA.From.Agent.RecipientAddress";
			// end

			// Added by FPT for VMWM enhancement on 20-Dec-2013
			public static final String VMWM_EMAIL_PROPERTY_PREFIX = "EMAIL.BerthingApplicationSubmitted.Agent";
			public static final String VMWM_EMAIL_PROPERTY_FROM = "EMAIL.BerthingApplicationSubmitted.Agent.from";
			public static final String VMWM_EMAIL_PROPERTY_SUBJECT = "EMAIL.BerthingApplicationSubmitted.Agent.subject";
			public static final String VMWM_EMAIL_PROPERTY_BODY = "EMAIL.BerthingApplicationSubmitted.Agent.body";
			// end

			// Added by HoaBT2 24/01/2014
			public static final String UPDATE_BUNKER_SENDER_EMAIL = "UpdateBunker.Sender.Email";
			public static final String UPDATE_BUNKER_EMAIL = "UpdateBunker.Email";
			public static final String UPDATE_BUNKER_MESSAGE = "UpdateBunker.Message";
			public static final String UPDATE_BUNKER_SUBJECT = "UpdateBunker.Subject";

			// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.
			public static final String VMWM_EMAIL_FORCE_DN_FROM = "EMAIL.WarehouseApplication.from";
			public static final String VMWM_EMAIL_FORCE_DN_SUBJECT = "EMAIL.VM.DNCreated.subject";
			public static final String VMWM_EMAIL_FORCE_DN_BODY = "EMAIL.VM.DNCreated.body";
			// END FPT modify for Warehouse Management CR, 10/02/2014.
		}

	} // end of ConfigProperties

	public interface MailProperties {
		public static final String DEBUG = "mail.debug";
		public static final String AUTHENTICATION = "mail.smtp.auth";
		public static final String PORT = "mail.smtp.port";
		public static final String HOST = "mail.smtp.host";
		public static final String SEND_TIMEOUT = "mail.smtp.timeout";
		public static final String FROM = "mail.from";
		public static final String MIME_ADDRESS_STRICT = "mime.address.strict";
		public static final String STORE_PROTOCOL = "mail.store.protocol";
		public static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";
		public static final String USER = "mail.user";
		public static final String MIME_CHARSET = "mime.charset";
		public static final String CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
	}

	public interface SysProperties {
		public static final String IntergatewayCargoHeader_RecordType = "1~1~A1~M";
		public static final String IntergatewayCargoHeader_CreationDate = "2~13~A12~M";
		public static final String IntergatewayCargoHeader_AbbVesselName = "14~30~A1~M";
		public static final String IntergatewayCargoHeader_AbbVoyNumber = "31~38~A1~M";
		public static final String IntergatewayCargoHeader_Filler = "39~50~A12~M";
		public static final String IntergatewayCargoHeader_FileSize = "50";

		public static final String IntergatewayCargoDetails_RecordType = "1~1~A1~M";
		public static final String IntergatewayCargoDetails_Function = "2~2~A1~M";
		public static final String IntergatewayCargoDetails_BillofLading = "3~22~A1~M";
		public static final String IntergatewayCargoDetails_HSCode = "23~31~A1~M";
		public static final String IntergatewayCargoDetails_PackagingType = "32~33~A1~M";
		public static final String IntergatewayCargoDetails_NumberOfPackage = "34~48~A1~M";
		public static final String IntergatewayCargoDetails_weight = "49~63~A1~M";
		public static final String IntergatewayCargoDetails_volume = "64~78~A1~M";
		public static final String IntergatewayCargoDetails_DGIndicator = "79~79~A1~M";
		public static final String IntergatewayCargoDetails_ShipperName = "80~149~A1~M";
		public static final String IntergatewayCargoDetails_CargoType = "150~150~A1~M";
		public static final String IntergatewayCargoDetails_LoadingVessel = "151~167~A1~M";
		public static final String IntergatewayCargoDetails_LoadingVoyage = "168~179~A1~M";

		public static final String IntergatewayCargoDetails_PortsOfDischarge = "180~184~A1~M";
		public static final String IntergatewayCargoDetails_ContainerNumber = "185~197~A1~M";
		public static final String IntergatewayCargoDetails_DirectionOfIntergateway = "198~199~A1~M";
		public static final String IntergatewayCargoDetails_BookingReferenceOfLoadingVesel = "200~219~A1~M";
		public static final String IntergatewayCargoDetails_AccountNumber = "220~225~A1~M";
		public static final String IntergatewayCargoDetails_TDBNo = "226~237~A1~M";
		public static final String IntergatewayCargoDetails_CargoDescription = "238~437~A200~M";
		public static final String IntergatewayCargoDetails_Marking = "438~637~A1~M";
		public static final String IntergatewayCargoDetails_Filler = "638~650~A1~M";
		public static final String IntergatewayCargoDetails_FileSize = "650";

		public static final String IntergatewayCargoSummary_RecordType = "1~1~A1~M";
		public static final String IntergatewayCargoSummary_TotalRecordsSubmitted = "2~9~N8~M";
		public static final String IntergatewayCargoSummary_Filler = "10~50~A41~M";
		public static final String IntergatewayCargoSummary_FileSize = "50";

	}
}
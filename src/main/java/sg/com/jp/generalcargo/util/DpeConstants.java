package sg.com.jp.generalcargo.util;

public class DpeConstants {
	
	// List
    public static final Integer START = 0;
    public static final Integer LIMIT = 20;

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm a";
    public static final String DATE_TIME_24_FORMAT = "dd/MM/yyyy HH:mm";

    public static final String INACTIVE = "I";
    public static final String ACTIVE = "A";
    public static final String INACTIVE_VALUE = "Inactive";
    public static final String ACTIVE_VALUE = "Active";

    //Outstanding report
    public static final String OUTSTANDING_EDO_FILE_NM = "OutstandingEDOReport";
    public static final String OUTSTANDING_EDO_RPT_NM = "Outstanding EDO Documentation";
    public static final String OUTSTANDING_ESN_FILE_NM = "OutstandingESNReport";
    public static final String OUTSTANDING_ESN_RPT_NM = "Outstanding ESN Documentation";
    public static final String OUTSTANDING_EDO_TYPE = "outstandingEDO";
    public static final String OUTSTANDING_ESN_TYPE = "outstandingESN";

    // Main gate report module
    public static final String LATE_GATE_OUT_CD 	= "LG";
    public static final String WITHOUT_EXIT_CD	 	= "WE";
    public static final String NORMAL_GATE_OUT_CD 	= "GO";

    public static final String MAIN_GATE_DOCUMENTATION_FILE_NM 	= "MainGateDocumentationReport";
    public static final String MAIN_GATE_DOCUMENTATION_RPT_NM 	= "Main Gate Documentation Report";
    public static final String LATE_GATE_OUT_REPORT_NM			= "Late Gate Out Report";
    public static final String GATE_OUT_REPORT_NM				= "Gate Out Report";
    public static final String WITHOUT_EXIT_REPORT_NM			= "Without Exit Date Time Report";

    public static final String TYPE_DELIVERY_NOTE		= "DN";
    public static final String TYPE_UNLOADING_ADVICE	= "UA";
    public static final String TYPE_TENANT_DN			= "TD";
    public static final String TYPE_TENANT_UA			= "TU";
    public static final String TYPE_PASS_OUT_NOTE		= "PN";
    public static final String TYPE_LORRY_CHIT			= "LC";
    public static final String TYPE_NON_TCTS_TENANT_UA  = "NU";

    public static final String DN_REPORT		       = "DN";
    public static final String LORRY_CHIT_REPORT	   = "Lorry Chit";
    public static final String PASS_OUT_NOTE_REPORT	   = "Pass Out Note";
    public static final String TENANT_DN_REPORT		   = "Tenant DN";
    public static final String TENANT_UA_REPORT		   = "Tenant UA";
    public static final String UA_REPORT			   = "UA";

    public static final String TYPE_NON_TCTS_TENANT_UA_REPORT = "Non-TCTS Tenant UA";


    //Container Amendment module
    public static final String	CNTR_INVALID = "Not an active Container";
    public static final String	CNTR_NOT_AUTHORIZE = "You are not authorized to amend this ISO code";
    public static final String	CNTR_NOT_AFTER_GATE_IN = " Container arrived. ISO code amendment is not allowed";
    public static final String	CNTR_NOT_AFTER_GATE_OUT = "Container exited. ISO code amendment is not allowed";
    public static final String	CNTR_ISO_INVALID = "ISO Code is not valid";
    public static final String	CNTR_UPDATE_SUCCESES = "ISO code updated.";
    public static final String	CNTR_UPDATE_FAILED = "ISO code update failed";

    ///Storing Order Container module
    public static final String	DPE_SYSTEM_PARA_MAX_DATE_RANGE_CD  = "DPEMR";
    public static final int	DPE_SYSTEM_PARA_MAX_DATE_RANGE_VALUE_DFT  = 90;//90 DAYS
    public static final String	DPE_SYSTEM_PARA_CNTR_NOT_ARRIVE_CD  = "DPECA";
    public static final int	DPE_SYSTEM_PARA_CNTR_NOT_ARRIVE_VALUE_DFT  = 72;//72 HOURS

    //Bulk Cargo Email Alerts module
    public static final String	DPE_SYSTEM_PARA_BULK_CARGO_SUBMISSION_LATE_TIME_CD  = "DPELT";
    public static final int	DPE_SYSTEM_PARA_BULK_CARGO_SUBMISSION_LATE_TIME_VALUE_DFT  = 12;

    public static final String	DPE_SYSTEM_PARA_BULK_CARGO_LATE_ACTIVITY_CD  		= "DPETS";
    public static final int	DPE_SYSTEM_PARA_BULK_CARGO_LATE_ACTIVITY_VALUE_DFT  = 45;

    //Balance Cargo in Port module
    public static final String	DPE_SYSTEM_PARA_LONG_LYING_CD  = "DPELL";
    public static final int	DPE_SYSTEM_PARA_LONG_LYING_VALUE_DFT  = 5;

    //Long Staying Cntr
    public static final String	DPE_SYSTEM_PARA_LONG_STAYING_CD  = "DPELS";
    public static final int	DPE_SYSTEM_PARA_LONG_STAYING_VALUE_DFT  = 5;

    //Long lying shutout cargo inport
    public static final String	DPE_SYSTEM_PARA_LONG_LYING_SHUT_OUT_CD  = "DPECS";
    public static final int	DPE_SYSTEM_PARA_LONG_LYING_SHUT_VALUE_DFT  = 5;

    //Exception Utility
    public static final String DPE_EXCEPTION_UTILITY_CARGO_EVENT_LOADING_CD  = "CEL";
    public static final String DPE_EXCEPTION_UTILITY_BERTHED_VESSEL_CD  = "BTV";

    public static final String DPE_ACTION_CD_SHIPMENT_OPEN = "SHO";

    public static final String DPE_CNTR_TXT_UNLOAD_OVERSIDE = "ULOS";

    //eForm
    public static final String DPE_EFORM_FLOATING_CRANE  = "Floating Crane";
    public static final String DPE_EFORM_SEP_COMMA = ",";
	public static final String DPE_EFORM_SEP_PDF_FIELD_VALUE = ": ";
	public static final String DPE_EFORM_SEP_MULTI = "~!~";
	public static final String DPE_EFORM_SEP_NEWLINE = "\r?\n";

	public static final String DPE_EXCEPTION_ALERT_JP_BERTHING_STAFF_CD     = "BO";
    public static final String DPE_EXCEPTION_ALERT_FINANCE_CD               = "FN";
    public static final String DPE_EXCEPTION_ALERT_BSX_CD                   = "BSX";

    // Email sender
    public static final String	DPE_EMAIL_SENDER_ADDRESS  = "system@jp.com.sg";
    public static final String DPE_EMAIL_SENDER_ADDRESS_CD = "EMAIL.DPE.sender";

    //Bulk Cargo SMS & Email Alerts module
    //1. No Submission Bulk ESN/ EDO
    public static final String	DPE_LATE_SUBMISSION_FA_ALERT_NM = "Alert Customer to submit Bulk Cargo EDO and ESN";
    public static final String	DPE_LATE_SUBMISSION_FA_ALERT_CD = "FLXALT028";

    public static final String	DPE_LATE_SUBMISSION_EMAIL_SUBJECT = "EMAIL.DPE.alertVesselNoSubmissionBulkEDOESN.subject";
    public static final String	DPE_LATE_SUBMISSION_EMAIL_BODY = "EMAIL.DPE.alertVesselNoSubmissionBulkEDOESN.body";

    //2. Late Submission Bulk ESN/ EDO
    public static final String	DPE_LATE_FIRST_ACTIVITY_FA_ALERT_NM = "Alert on Unloader Standby Charges";
    public static final String	DPE_LATE_FIRST_ACTIVITY_FA_ALERT_CD = "FLXALT029";
    public static final String	DPE_LATE_FIRST_ACTIVITY_TEXT_PARA_INCLUDE_STANDBY_CD = "DPESBICD";

    public static final String	DPE_LATE_FIRST_ACTIVITY_EMAIL_SUBJECT = "EMAIL.DPE.alertVesselLateFirstActivity.subject";
    public static final String	DPE_LATE_FIRST_ACTIVITY_EMAIL_BODY = "EMAIL.DPE.alertVesselLateFirstActivity.body";

    public static final String	DPE_EMAIL_TEMPLATE_PLACE_HOLDER_VESSEL_NAME_TEXT = "<Vessel Name>";
    public static final String	DPE_EMAIL_TEMPLATE_PLACE_HOLDER_IN_VOYAGE_NUMBER_TEXT = "<InVoyage Number>";
    public static final String  DPE_EMAIL_TEMPLATE_PLACE_HOLDER_OUT_VOYAGE_NUMBER_TEXT = "<OutVoyage Number>";
    public static final String	DPE_EMAIL_TEMPLATE_PLACE_HOLDER_ETB_DATE_TIME_TEXT = "<ETB Date Time>";

    //Long Lying Balance In Port alert
    public static final String	DPE_LONG_LYING_BALANCE_IN_PORT_EMAIL_SUBJECT = "EMAIL.DPE.alertLongLyingBalanceInPort.subject";
    public static final String	DPE_LONG_LYING_BALANCE_IN_PORT_EMAIL_CONTENT = "EMAIL.DPE.alertLongLyingBalanceInPort.content";

    //Pending Floating Crane Eform After ATU alert
    public static final String	DPE_PENDING_FLOATING_CRANNE_AFTER_ATU_SUBJECT = "EMAIL.DPE.alertPendingFloatingCraneAfterATU.subject";

    //Long Staying Cntr alert
    public static final String	DPE_LONG_STAYING_CNTR_EMAIL_SUBJECT = "EMAIL.DPE.alertLongStayingCntr.subject";
    public static final String	DPE_LONG_STAYING_CNTR_EMAIL_CONTENT = "EMAIL.DPE.alertLongStayingCntr.content";

    // Lying Cargo ShutOutCargo
    public static final String	DPE_LYING_CARGO_SHUTOUT_EMAIL_SUBJECT = "EMAIL.LyingShutOutCargo.subject";
    public static final String	DPE_LYING_CARGO_SHUTOUT_EMAIL_BODY = "EMAIL.LyingShutOutCargo.body";

    // PassoutNote
    public static final String	DPE_PON_EMAIL_SUBJECT = "EMAIL.PON.subject";
    public static final String	DPE_PON_EMAIL_BODY = "EMAIL.PON.body";

 // OMC Partner
    public static final String	DPE_TCTS_EMAIL_SUBJECT = "EMAIL.OMCTCTS.subject";
    public static final String	DPE_TCTS_EMAIL_BODY = "EMAIL.OMCTCTS.body";

    //20180117 koktsing added. TCTS Alert subject for ASN(Cargo) and Container
    public static final String	DPE_TCTSASN_EMAIL_SUBJECT = "EMAIL.TCTSASN.subject";
    public static final String	DPE_TCTSCNTR_EMAIL_SUBJECT = "EMAIL.TCTSCNTR.subject";

 // Nom Email ALert for Stevedore
    public static final String	EMAIL_NOMSTEV_SUBJECT = "EMAIL.NOMSTEV.subject";
    public static final String	EMAIL_NOMSTEV_BODY = "EMAIL.NOMSTEV.body";

 // Nom Email ALert for Stevedore when no SC appointed
    public static final String	EMAIL_NOMNOSC_SUBJECT = "EMAIL.NOMNOSC.subject";
    public static final String	EMAIL_NOMNOSC_BODY = "EMAIL.NOMNOSC.body";

    //Cargo Enquiry
    public static final String JNR_SCHEME = "JNR";
    public static final int FROM_TO_RANGE = 7;

    //Permissions in DPE
    // for cargo & bulk cargo enquiry
	public static final String DPE_VIEWCARGOENQUIRYDETAILS         = "DPE_ViewCargoEnquiryDetails";
	public static final String DPE_PRINTCARGOENQUIRYDETAILS        = "DPE_PrintCargoEnquiryDetails";
	public static final String DPE_ENQUIRYGENERALBULKCARGO         = "DPE_ EnquiryGeneralBulkCargo";

	// for container amendment ISO Code
	public static final String DPE_AMENDCONTAINERISO               = "DPE_AmendContainerISO";

	// for cargo amendment
	public static final String DPE_AMENDGENERALCARGO               = "DPE_AmendGeneralCargo";

	// for outstanding EDO/ESN
	public static final String DPE_ENQUIRYOUTSTANDING              = "DPE_EnquiryOutstanding";
	public static final String DPE_EXPORTOUTSTANDING               = "DPE_ExportOutstanding";

	// for monitoring shutout/transfer cargo
	public static final String DPE_ENQUIRYSHUTOUTCARGO             = "DPE_EnquiryShutoutCargo";
	public static final String DPE_UPDATESHUTOUTCARGO              = "DPE_UpdateShutoutCargo";
	public static final String DPE_ADDSHUTOUTEDO                   = "DPE_AddShutoutEDO";
	public static final String DPE_UPDATESHUTOUTEDO                = "DPE_UpdateShutoutEDO";
	public static final String DPE_DELETESHUTOUTEDO                = "DPE_DeleteShutoutEDO";
	public static final String DPE_ADDTRANSFERCARGO                = "DPE_AddTransferCargo";
	public static final String DPE_UPDATETRANSFERCARGO             = "DPE_UpdateTransferCargo";

	// for main gate report
	public static final String DPE_LISTMAINGATEDOCUMENTATION       = "DPE_ListMainGateDocumentation";
	public static final String DPE_EXPORTMAINGATEDOC               = "DPE_ExportMainGateDoc";
	public static final String DPE_UPDATEEXITDATETIME              = "DPE_UpdateExitDateTime";

	// for Exception Utilitied - remove event for container and berthed vessel
	public static final String DPE_LISTEVENTLOADCARGOES            = "DPE_ListEventLoadCargoes";
	public static final String DPE_REMOVEEVENTLOADCARGOES          = "DPE_RemoveEventLoadCargoes";
	public static final String DPE_LISTBERTHEDVESSELATB            = "DPE_ListBerthedVesselATB";
	public static final String DPE_REMOVELISTBERTHEDVESSELATB      = "DPE_RemoveListBerthedVesselATB";

	//Reexport Cntr After ATU
	public static final String DPE_ALLOWREEXPORTANYTIME            = "DPE_AllowReexportAnyTime";

	//MISC_EVENT_LOG TYPE for CNTR
	public static final String CNTR_CISO = "CISO";

}

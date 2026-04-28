package sg.com.jp.generalcargo.util;

import java.util.Hashtable;

/**
 * Revision Change ================ Author Date Request No Description of Change
 * Version
 * --------------------------------------------------------------------------------------------------------
 * HanhTD 28 February 2011 TPA Constants that will be used in TPA 1.0
 *
 * 
 * ThanhBTL6B 12 Sept 2013 TPA Update Trailer Size, Trailer Type for new TPA 2.0
 *
 */

public class Constants {

	public final static String SLOT_STATUS_NAME_EMPTY_NORMAL = "Normal";

	public final static String SLOT_STATUS_CODE_EMPTY_NORMAL = "N";

	public final static String SLOT_STATUS_NAME_DG = "DG";

	public final static String SLOT_STATUS_CODE_DG = "D";

	public final static String SLOT_STATUS_NAME_OOG = "OOG";

	public final static String SLOT_STATUS_CODE_OOG = "O";

	// Start added by thanhbtl6b for TPA Enhancement
	public final static String TRAILER_TYPE_CODE_E = "E";

	public final static String TRAILER_TYPE_CODE_L = "L";

	public final static String TRAILER_TYPE_NAME_E = "Empty";

	public final static String TRAILER_TYPE_NAME_L = "Laden";

	// Start added by thanhbtl6b for TPA Enhancement

	public static final String SHOW_SCREEN_ADDING = "ShowAddingScreen";

	public static final String ADD_SLOT_EXECUTE = "ADDSLOT";

	public static final String ADD_AREA_EXECUTE = "ADDAREA";

	public static final String UPDATE_SLOT_EXECUTE = "UPDATESLOT";

	public static final String SHOW_SCREEN_UPDATING = "ShowUpdatingScreen";

	public static final String DELETED_STATUS_CODE = "DLTD";
	public static final int DUPLICATE_SLOT_NUMBER = 1;
	public static final int ADD_SLOT_SUCCEFULLY = 5;
	public static final int EMPTY_SLOT_NUMBER_CODE = 4;
	public static final int EMPTY_AREA_CODE = 3;

	public static final int SLOT_ALREADY_DELETED = 8;
	public static final int UPDATE_SLOT_SUCCESSFULLY = 9;
	public static final int DELETE_SLOT_SUCCESSFULLY = 10;

	public static final int EMPTY_SLOT_TYPE = 11;
	public static final int EMPTY_SLOT_SIZE = 12;
	public static final int EMPTY_CARGO_TYPE = 13;

	public static final String SLOT_STATUS_DELETED = "Deleted";

	public static final String SLOT_STATUS_OPEN = "Open";

	public static final String SLOT_STATUS_CLOSE = "Closed";

	public static final String SLOT_STATUS_RESERVED = "Reserved";

	public static final String SLOT_STATUS_DELETED_CD = "DLTD";

	public static final String SLOT_STATUS_OPEN_CD = "OPN";

	public static final String SLOT_STATUS_CLOSE_CD = "CLSD";

	public static final String SLOT_STATUS_RESERVED_CD = "RSVD";
	public static Hashtable SLOT_TYPE_TABLE = new Hashtable();
	static {
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_EMPTY_NORMAL, SLOT_STATUS_NAME_EMPTY_NORMAL);
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_DG, SLOT_STATUS_NAME_DG);
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_OOG, SLOT_STATUS_NAME_OOG);
	}

	// trailers
	public static Hashtable TRAILER_TYPE_TABLE = new Hashtable();
	static {
		TRAILER_TYPE_TABLE.put(TRAILER_TYPE_CODE_E, TRAILER_TYPE_NAME_E);
		TRAILER_TYPE_TABLE.put(TRAILER_TYPE_CODE_L, TRAILER_TYPE_NAME_L);
	}

	public static Hashtable SLOT_STATUS_TABLE = new Hashtable();

	static {
		SLOT_STATUS_TABLE.put(SLOT_STATUS_DELETED, SLOT_STATUS_DELETED_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_OPEN, SLOT_STATUS_OPEN_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_CLOSE, SLOT_STATUS_CLOSE_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_RESERVED, SLOT_STATUS_RESERVED_CD);
	}

	public static String[] HOUR_LIST = { "0000", "0030", "0100", "0130", "0200", "0230", "0300", "0330", "0400", "0430",
			"0500", "0530", "0530", "0600", "0630", "0700", "0730", "0800", "0830", "0900", "0930", "1000", "1030",
			"1100", "1130", "1200", "1230", "1300", "1330", "1400", "1430", "1500", "1530", "1600", "1630", "1700",
			"1730", "1800", "1830", "1900", "1930", "2000", "2030", "2100", "2130", "2200", "2230", "2300", "2330" };

	/*---------------------Start valueObject.ops.reeferTemperature------------------------*/

	public static final String DATE_FORMAT_FILE_NAME = "yyyyMMddHHmm";
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final String CURRENT_DATE = "currentDate";
	public static final String ADDRESS_TEMPLATE_PRO = "ReportTemplatePath";
	public static final String REEFER_ADDRESS_TEMPLATE_NAME = "REEFER.ADDRESS.TEMPLATE.NAME";
	public static final String EMAIL_ALERT_NOTIFICATION_RVO = "EmailAlertRVO";
	public static final String EMAIL_TYPE = "text/html";
	public static final String REEFER_COUNT = "refCount_";
	public static final String VELOCITY_MAIL_OBJECT = "rVO";
	public static final String FULL_STRING_CLASS = "java.lang.String";
	public static final String REEFER_TEMPERATURE_EJB_NAME = "ReeferTemperature";
	public static final String PARA_REEFER_MOTINTORING_VIEW_TOP = "ReeferMonitoringViewTop";
	public static final String PARA_REEFER_NOTIFICATION_MAIL_ALERT = "ReeferEmailNotificationAlert";
	public static final String PARA_REEFER_DOWNLOAD_TO_EXCEL = "ReeferMonitoringViewTopExcel";
	public static final String PARA_REEFER_MONITORING_TOP = "ReeferMonitoringTop";
	public static final String[] PARA_MONITORING_NAME_OUTPUT = { "vessels", "types", "sizes", "statuses" };
	public static final String[] PARA_MONITORING_LIST_NAME_INPUT = { "CntrNumber", "status", "size", "type", "vessel",
			"inVoyage", "outVoyage", "startDate", "endDate", "hour", "minute" };
	public static final String PARA_REEFER_MONITORING = "reeferMonitoring";
	public static final String PARA_REEFER_MONITORING_LIST = "ReeferMonitoringList";
	public static final String PARA_REEFER_MONITORING_VIEW = "ReeferMonitoringView";
	public static final String PARA_REEFER_MONITORING_VIEW_BACK = "ReeferMonitoringViewBack";
	public static final String LIST_BOTTOM = "listBottom";
	public static final String REEFER_MONITORING_BOTTOM_TEMPERATURE = "ReeferMonitoringBottomTemperature";
	public static final String REEFER_MONITORING_LIST_TEMPERATURE = "ReeferMonitoringTemplate";
	public static final String REEFER_MONITORING_VIEW_BOTTOM = "ReeferMonitoringViewBottom";

	public static final String REEFER_SETTING_LIST = "REEFER_SETTING_LIST";

	public static final int MAX_NUMBER_PAGE = 10;
	public static final String SEND_EMAIL_UNIT = "EMAIL_UNIT";
	public static final String SEND_EMAIL_UNIT_TEL = "TEL";
	public static final String SEND_EMAIL_UNIT_FAX = "FAX";
	public static final String SEND_EMAIL_UNIT_EMAIL = "EMAIL";
	public static final String MISC_TYPE_CODE_EJB_NAME = "MiscCode";

	/*---------------------End valueObject.ops.reeferTemperature------------------------*/

	/*---------------------Start sg.com.ntc.gbcc.util.Constants------------------------*/
	public static final String ALERT_BERTH_CANCELLATION = "BAC";
	public static final String ALERT_PILOTAGE_ERRORPROCESSING = "POE";
	public static final String ALERT_SPECIAL_EXCEPTIONAL_EVENT = "ESE";

	public static final String ALERT_BERTH_CANCELLATION_SUBJECT = "Cancellation of BA Alert";
	public static final String ALERT_PILOTAGE_ERRORPROCESSING_SUBJECT = "Processing error for Pilotage Order Alert";
	public static final String ALERT_SPECIAL_EXCEPTIONAL_EVENT_SUBJECT = "Exceptional/Special Event Alert";

	public static final String YESNO_IND_YES = "Y";
	public static final String YESNO_IND_NO = "N";
	public static final String SEPARATOR = ", ";

	public static final String PARA_CD_PILOTAGE_GRT = "PILOTAGE_GRT";
	public static final String PARA_CD_EST_VSL_ARR = "EST_VSL_ARR_HR";
	public static final String PARA_CD_GC_TON_THRESHOLD = "GC_TON_THRESHOLD";
	public static final String PARA_CD_VSLSCH_PGSIZE = "VSLSCH_PGSIZE";
	public static final String PARA_CD_DEL_IMG_AFT_ATU = "DEL_IMG_AFT_ATU";
	public static final String PARA_CD_SPEC_EVENT_FIL = "SPEC_EVENT_FIL";
	public static final String PARA_CD_STORAGE_ACT_FIL = "STORAGE_ACT_FIL";
	public static final String PARA_CD_VSL_ALONG_WHARF_DIS = "VSL_ALONG_WHARF_DIS";
	// ICR system changes starts
	public static final String PARA_FILTER_CLIENT = "ICR_FIL_CC";
	public static final String PARA_FILTER_SCHEME = "ICR_FIL_SC";
	public static final String PARA_FILTER_TON = "ICR_FIL_TN";
	public static final String PARA_TONNAGE_CC = "ICR_FTN_CC";
	public static final String PARA_CD_CONTAINER_CC = "ICR_CON_CC";
	public static final String PARA_CD_GENERAL_CARGO_CC = "ICR_GC_CC";
	public static final String PARA_CD_CEMENT_VESSEL_CC = "ICR_CV_CC";
	public static final String PARA_CD_BULK_VESSEL_CC = "ICR_BLK_CC";
	public static final String PARA_CONVEYOR_CC = "ICR_CVR_CC";
	public static final String PARA_CD_LLC_EQUIP = "ICR_LLC_EQ";
	public static final String PARA_ICR_DATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final String PARA_FIRST_ACTIVITY = "76-POSITIONING/SHIFTING UL";
	public static final String PARA_LORRY_TONNAGE = "ICR_TR_TON";
	public static final String PARA_CD_TDT_MIN = "ICR_GA_AT";
	// ICR system changes ends

	// ICR2 changes start
	public static final String PARA_BCO_DUR = "ICR2_TSDUR";
	// ICR2 changes end

	public static final String PARA_CD_RULE_01 = "RULE01";
	public static final String PARA_CD_RULE_02 = "RULE02";
	public static final String PARA_CD_RULE_03 = "RULE03";
	public static final String PARA_CD_RULE_07 = "RULE07";
	public static final String PARA_CD_RULE_09 = "RULE09";
	public static final String PARA_CD_RULE_10 = "RULE10";
	public static final String PARA_CD_RULE_12 = "RULE12";
	public static final String PARA_CD_RULE_13 = "RULE13";
	public static final String PARA_CD_RULE_15 = "RULE15";
	public static final String PARA_CD_RULE_16 = "RULE16";
	public static final String PARA_CD_RULE_17 = "RULE17";
	public static final String PARA_CD_RULE_18 = "RULE18";
	public static final String PARA_CD_RULE_19 = "RULE19";
	public static final String PARA_CD_RULE_21 = "RULE21";
	public static final String PARA_CD_RULE_22 = "RULE22";
	public static final String PARA_CD_RULE_23 = "RULE23";
	public static final String PARA_CD_RULE_24 = "RULE24";
	public static final String PARA_CD_RULE_14 = "RULE14";
	public static final String PARA_CD_RULE_05 = "RULE05";

	public static final String PARA_CD_RULE_S01 = "RULES01";
	public static final String PARA_CD_RULE_S02 = "RULES02";

	public static final String MISCTYPECD_PILOTAGE_STATUS = "PT_STATUS";
	public static final String MISCTYPECD_PILOTAGE_REPROGRAM = "PT_REPROG";
	public static final String MISCTYPECD_DELAY_REASON = "WC_DELAY";
	public static final String MISCTYPECD_SHIFT_STEV = "SHIFT_STEV";
	public static final String MISCTYPECD_DAMAGE_CD = "DAMAGE_CD";
	public static final String MISCTYPECD_OPERATION_TYPE = "OPR_TYPE";
	public static final String MISCTYPECD_UNDERPERF_REASON = "CRG_PERFM";
	public static final String MISCTYPECD_WEATHER = "RAIN_CAT";
	public static final String MISCTYPECD_ACTIVITY = "CARGO_ACT";
	public static final String MISCTYPECD_EQTYPE = "EQUIP_RENT";
	public static final String MISCTYPECD_VSLSCHSTATUS = "VSL_SCH";
	public static final String MISCTYPECD_SPECIAL_EVENT = "SPEC_EVENT";
	public static final String MISCTYPECD_ALERT_TYPE = "ALERT_TYP";
	public static final String MISCTYPECD_ALERT_STATUS = "ALT_STATUS";
	public static final String MISCTYPECD_VV_STATUS = "VV_STATUS";
	public static final String MISCTYPECD_MV_STATUS = "MV_STATUS";
	public static final String MISCTYPECD_BERTH_CHK = "BERTH_CHK";
	public static final String MISCTYPECD_RORO_CHK = "RORO_CHK";
	public static final String MISCTYPECD_MISC_STAT = "MISC_STAT";
	public static final String MISCTYPECD_LOCTYPE = "LOCTYPE";
	public static final String MISCTYPECD_STORAGETYPE = "STTYPE";
	public static final String MISCTYPECD_CONTROL_CENTER = "CTRL_CTR";

	public static final String TEXTPARA_RP_XCLIENT_CODE = "SNETRPXCC";

	public static final String JMSMsgType_VesselCall = "VesselCall";
	public static final String JMSMsgType_Berthing = "Berthing";
	public static final String JMSMsgType_BerthNbr = "BerthNbr";
	public static final String JMSMsgType_Warehouse = "Warehouse";
	public static final String JMSMsgType_VESSELCARGOACTIVITY = "VesselCargoActivity";
	public static final String JMSMsgType_ATB_UPDATED = "ATB_Updated";
	public static final String JMSMsgType_ATU_UPDATED = "ATU_Updated";
	public static final String JMSMsgType_DAILYOCCUPANCY_UPDATED = "DailyOccupany_Updated";

	public static final String JMSMsgType_CARGOACTIVITY_UPDATED = "CargoActivity_Updated";
	public static final String JMSMsgType_CARGOACTIVITY_UPDATED_FIRSTACTIVITY = "CargoActivity_Updated_FirstActivity";
	public static final String JMSMsgType_CARGOACTIVITY_UPDATED_LASTACTIVITY = "CargoActivity_Updated_LastActivity";

	public static final String JMSMsgType_SPECIALEVENTINFO_UPDATED = "SpecialEventInfo_Updated";
	public static final String JMSMsgType_BUNKERINGINFO_UPDATED = "BunkeringInfo_Updated";
	public static final String JMSMsgType_WATERSUPPLYINFO_UPDATED = "WaterSupplyInfo_Updated";
	public static final String JMSMsgType_CONTAINERDISCHARGEINFO_UPDATED = "ContainerDischargeInfo_Updated";
	public static final String JMSMsgType_CONTAINERLOADINFO_UPDATED = "ContainerLoadInfo_Updated";
	public static final String JMSMsgType_REEFERAPPINFO_UPDATED = "ReeferAppInfo_Updated";
	public static final String JMSMsgType_VESSELMOVEMENTINFO_UPDATED = "VesselMovementInfo_Updated";
	public static final String JMSMsgType_CRANEWHARFMARK_UPDATED = "CraneWharfmark_Updated";
	public static final String JMSMsgType_CARGOOPENINGBALANCE_UPDATED = "CargoOpeningBalance_Updated";
	public static final String JMSMsgType_CARGODISCLOADINFO_UPDATED = "CargoDiscLoadInfo_Updated";
	public static final String JMSMsgType_CARGODISCHARGLOADUNDERPERFREASON_UPDATED = "CargoDischargeLoadUnderPerfReason_Updated";
	public static final String JMSMsgType_SPACEUTILIZATION_UPDATED = "SpaceUtilizationInfo_Updated";
	public static final String JMSMsgType_ALERTACTION_UPDATED = "AlertAction_Updated";
	public static final String JMSMsgType_TIMESHEETENTRY_CREATED = "TimesheetEntry_Created";
	public static final String JMSMsgType_CRANELIST = "CraneList";
	public static final String JMSMsgType_UNLOADERLIST = "UnloaderList";

	public static final String JMSMsgType_BERTHAPP_UPDATED = "BerthApp_Updated";
	public static final String JMSMsgType_SPECIALEVENTACTIVITY = "SpecialEventActivity";
	public static final String JMSMsgType_STORAGEACTIVITY = "StorageActivity";

	public static final String JMSMsgType_Alert = "Alert";

	// ICR system changes starts
	public static final String JMSMsgType_WESTGATEACTIVITY = "WestGateActivity";
	public static final String JMSMsgType_MAINGATEACTIVITY = "MainGateActivity";
	public static final String JMSMsgType_VESSELPRODUCTIVITYALONGSIDE = "VesselProductivityAlongside";
	public static final String JMSMsgType_VESSELPRODUCTIVITYINCOMING = "VesselProductivityIncoming";
	// ICR system changes ends

	public static final String JMSMsgSubType_ImgVesselCall = "ImgVesselCall";
	public static final String JMSMsgSubType_ImgBerthing = "ImgBerthing";
	public static final String JMSMsgSubType_Alert = "Alert";
	public static final String JMSMsgSubType_SSTALERTLIST = "SSTAlertList";

	public static final String JMSOpsType_Update = "Update";
	public static final String JMSOpsType_Insert = "Insert";
	public static final String JMSOpsType_Remove = "Remove";
	public static final String JMSOpsType_SSTDown = "SST Down";
	public static final String JMSOpsType_SSTPaperLow = "SST Paper Low";

	public static final String ALERTTYPE_VESSEL = "V";
	public static final String ALERTTYPE_BERTH = "B";
	public static final String ALERTTYPE_WAREHOUSE = "W";
	public static final String ALERTTYPE_OPENYARD = "O";

	public static final String ALERTSUBTYPE_PROJECTU = "PROJECTU";
	public static final String ALERTSUBTYPE_ETUDEL = "ETUDEL";
	public static final String ALERTSUBTYPE_LOWPRODUCTIVITY = "LOWPROD";
	public static final String ALERTSUBTYPE_VSLCNL = "VSLCNL";
	public static final String ALERTSUBTYPE_ARRPILOTCHK = "ARRPLTCHK";
	public static final String ALERTSUBTYPE_ARRPILOTCONFCHK = "ARRPLTCFGCHK";
	public static final String ALERTSUBTYPE_DEPPILOTCHK = "DEPPLTCHK";
	public static final String ALERTSUBTYPE_DEPPILOTCONFCHK = "DEPPLTCFGCHK";
	public static final String ALERTSUBTYPE_PLTETUCSTMISMTC = "PLTCSTMISMTC";
	public static final String ALERTSUBTYPE_PLTCSTNOTEQUAL = "PLTCSTNOTEQL";
	public static final String ALERTSUBTYPE_PLTREPROGRAM = "PLTREPROGRAM";
	public static final String ALERTSUBTYPE_PLTSTATUSCHG = "PLTSTATUSCHG";
	public static final String ALERTSUBTYPE_PLTDELETE = "PLTDELETE";
	public static final String ALERTSUBTYPE_PLTSRTMISMTC = "PLTSRTMISMTC";
	public static final String ALERTSUBTYPE_PILOTDELAY = "PILOTDELAY";
	public static final String ALERTSUBTYPE_VSLMOVEREM1 = "VSLMOVEREM1";
	public static final String ALERTSUBTYPE_VSLMOVEREM2 = "VSLMOVEREM2";
	public static final String ALERTSUBTYPE_EQUIPIMMO = "EQUIPIMMO";
	public static final String ALERTSUBTYPE_EXCEPTEVENT = "EXCEPTEVENT";
	public static final String ALERTSUBTYPE_OPRPLANCHK = "OPRPLANCHK";
	public static final String ALERTSUBTYPE_VSLFIRSTCARGOACT = "VSLFSTCRGACT";
	public static final String ALERTSUBTYPE_WATERACT = "WATERACT";
	public static final String ALERTSUBTYPE_BUNKERACT = "BUNKERACT";
	public static final String ALERTSUBTYPE_LATEARRCHG = "LATEARRCHG";
	public static final String ALERTSUBTYPE_CHKFIRSTACT = "CHKFIRSTACT";
	public static final String ALERTSUBTYPE_NOFIRSTACT = "NOFIRSTACT";
	public static final String ALERTSUBTYPE_CHKWATERACT = "CHKWATERACT";
	public static final String ALERTSUBTYPE_CHKBUNKERACT = "CHKBUNKERACT";

	public static final String ALERTSUBTYPE_CHKGCOPENBALRACT = "CHKGCOPENBAL";

	public static final String ALERTSUBTYPE_ATBUPDATEACT = "ATBUPDATEACT";
	public static final String ALERTSUBTYPE_ETB1REMINDER = "ETB1REMINDER";
	public static final String ALERTSUBTYPE_ETB2REMINDER = "ETB2REMINDER";
	public static final String ALERTSUBTYPE_ETU1REMINDER = "ETU1REMINDER";
	public static final String ALERTSUBTYPE_OVERSTAY = "OVERSTAY";
	public static final String ALERTSUBTYPE_GENLASTACT = "GENLASTACT";
	public static final String ALERTSUBTYPE_BULKLASTACT = "BULKLASTACT";
	public static final String ALERTSUBTYPE_BULKSTANDBY = "BLKSTANDBY";
	public static final String ALERTSUBTYPE_BULKSTANDBY62 = "BLKSTANDBY62";
	public static final String ALERTSUBTYPE_NOBALSHT = "NOBALSHT";
	public static final String ALERTSUBTYPE_PLANACTDEV = "PLANACTDEV";
	public static final String ALERTSUBTYPE_ACTSTORAGE = "ACTSTORAGE";

	public static final String RULEPARA_CD_ETUDiff = "ETUDiff";
	public static final String RULEPARA_CD_GCAllowance = "GCAllowance";
	public static final String RULEPARA_CD_BCAllowance = "BCAllowance";
	public static final String RULEPARA_CD_CCAllowance = "CCAllowance";
	public static final String RULEPARA_CD_ETBETUInterval = "ETBETUInterval";
	public static final String RULEPARA_CD_CancelInterval = "CancelInterval";
	public static final String RULEPARA_CD_ArrPilotBooking = "ArrPilotBooking";
	public static final String RULEPARA_CD_DepPilotBooking = "DepPilotBooking";
	public static final String RULEPARA_CD_MoveHaulDist = "MoveHaulDist";
	public static final String RULEPARA_CD_OprPlanInterval = "OprPlanInterval";
	public static final String RULEPARA_CD_LateChargeInterval = "LateChargeInterval";
	public static final String RULEPARA_CD_NextChkInterval = "NextChkInterval";
	public static final String RULEPARA_CD_NextChkIntWaterAct = "NextChkIntWaterAct";
	public static final String RULEPARA_CD_NextChkIntBunkerAct = "NextChkIntBunkerAct";
	public static final String RULEPARA_CD_NextChkIntOpenBalAct = "NextChkIntOpenBalAct";
	public static final String RULEPARA_CD_NextChkIntException = "NextChkIntException";

	public static final String RULEPARA_CD_FirstETBETUIntFr = "FirstETBETUIntFr";
	public static final String RULEPARA_CD_FirstETBETUIntTo = "FirstETBETUIntTo";
	public static final String RULEPARA_CD_SecETBIntervalFr = "SecETBIntervalFr";
	public static final String RULEPARA_CD_SecETBIntervalTo = "SecETBIntervalTo";

	public static final String RULEPARA_CD_GCOverStayDockage = "GCOverStayDockage";
	public static final String RULEPARA_CD_BCOverStayDockage = "BCOverStayDockage";

	public static final String RULEPARA_CD_GCATUIntAfterLastAct = "GCATUIntAfterLastAct";
	public static final String RULEPARA_CD_BCATUIntAfterLastAct = "BCATUIntAfterLastAct";

	public static final String RULEPARA_CD_GCLastActRECUR = "GCLastActRECUR";
	public static final String RULEPARA_CD_BCLastActRECUR = "BCLastActRECUR";

	public static final String RULEPARA_CD_BULKSbyCHK = "BulkSbyChk";
	public static final String RULEPARA_CD_BULKSbyCHK62 = "BulkSbyChk62";
	public static final String RULEPARA_CD_EndShiftChkInt = "EndShiftChkInt";

	public static final String RULEPARA_CD_PLANSTORAGEChk = "PlanStorageChk";
	public static final String RULEPARA_CD_STORAGEOCCUPANCYChk = "StorageOccupancyChk";

	public static final String RULEPARA_CD_SSTACTIVEINTERVAL = "SstActiveInterval";

	// public static final String RULEPARA_CD_GCOverStayDockageInt =
	// "GCOverStayDockageInt";
	// public static final String RULEPARA_CD_BCOverStayDockageInt =
	// "BCOverStayDockageInt";

//	public static final String ALERTSTATUS_Pending = "P";
//	public static final String ALERTSTATUS_Completed = "Y";
//	public static final String ALERTSTATUS_Cancelled = "X";
//	public static final String ALERTSTATUS_Reprogrammed = "R";
//	public static final String ALERTSTATUS_Expired = "E";

	public static final String PARAUNIT_Minute = "MIN";
	public static final String PARAUNIT_Hours = "HRS";
	public static final String PARAUNIT_Percentage = "PCT";

	public static final String PILOTAGE_STATUS_PENDING = "PENDING";
	public static final String PILOTAGE_STATUS_APPROVED = "APPROVED";
	public static final String PILOTAGE_STATUS_DEPLOYED = "DEPLOYED";
	public static final String PILOTAGE_STATUS_ARRIVED = "ARRIVED";
	public static final String PILOTAGE_STATUS_ONBOARD = "ONBOARD";
	public static final String PILOTAGE_STATUS_STARTED = "STARTED";
	public static final String PILOTAGE_STATUS_ENDED = "ENDED";
	public static final String PILOTAGE_STATUS_DELETED = "DELETED";

	public static final String SCHEDULETYPE_ALERT = "A";
	public static final String SCHEDULETYPE_RULE = "R";

	public static final String ALERTSCH_STATUS_PENDING = "P";
	public static final String ALERTSCH_STATUS_COMPLETED = "C";
	public static final String ALERTSCH_STATUS_VOID = "X";

	/* for internal usage */
	public static final String GBCC_CargoType_General = "GC";
	public static final String GBCC_CargoType_Container = "CC";
	public static final String GBCC_CargoType_Bulk = "BC";

	public static final String MWI_RESULT_OK = "OK";
	public static final String MWI_RESULT_ERR = "ERR";

	public static final String StageBay_CargoType_Disc = "D";
	public static final String StageBay_CargoType_DiscTrans = "DT";
	public static final String StageBay_CargoType_Load = "L";
	public static final String StageBay_CargoType_LoadTrans = "LT";

	public static final String PILOTAGEFILE_STATUS_PENDING = "N";
	public static final String PILOTAGEFILE_STATUS_SUCCESS = "Y";
	public static final String PILOTAGEFILE_STATUS_ERROR_VSLNAMENOTFOUND = "E";
	public static final String PILOTAGEFILE_STATUS_ERROR_VVCDNOTFOUND = "F";
	public static final String PILOTAGEFILE_STATUS_ERROR_DELETENOTFOUND = "G";

	public static final String TALLYSHEET_OPRTYPE_Discharge = "D";
	public static final String TALLYSHEET_OPRTYPE_Load = "L";

	public static final String VV_STATUS_PR = "PR";
	public static final String VV_STATUS_AP = "AP";
	public static final String VV_STATUS_AL = "AL";
	public static final String VV_STATUS_BR = "BR";
	public static final String VV_STATUS_UB = "UB";
	public static final String VV_STATUS_CL = "CL";
	public static final String VV_STATUS_CX = "CX";

	public static final String SEQ_TALLYSHEET_ACT = "GBCC_TALLYSHEET_ACT_SEQ";
	public static final String SEQ_TALLYSHEET_EQRENTAL = "GBCC_TALLYSHEET_EQRENTAL_SEQ";
	public static final String SEQ_RULE_ALERT_SCH = "GBCC_RULE_ALERT_SCH_SEQ";
	public static final String SEQ_GBCC_SPECIAL_EVENT = "GBCC_SPECIAL_EVENT_SEQ";
	public static final String SEQ_GBCC_AUDIT_LOG_SEQ = "GBCC_AUDIT_LOG_SEQ";

	public static final String OBJ_UPDATEMODE_DELETE = "D";
	public static final String OBJ_UPDATEMODE_INSERT = "I";
	public static final String OBJ_UPDATEMODE_UPDATE = "U";

	public static final String DATEFORMAT_INPUT_SHORT = "ddMMyyyy";
	public static final String DATEFORMAT_INPUT = "ddMMyyyy HHmm";
	public static final String DATEFORMAT_INPUT_LONG = "ddMMyyyy HHmmss";
	public static final String DATEFORMAT_DISPLAY_SHORT = "dd/MM/yyyy";
	public static final String DATEFORMAT_DISPLAY = "dd/MM/yyyy HH:mm";
	public static final String DATEFORMAT_DISPLAY_LONG = "dd/MM/yyyy HH:mm:ss";

	public static final String BERTHING_STATUS_BA = "BA";
	public static final String BERTHING_STATUS_SH = "SH";
	public static final String BERTHING_STATUS_IH = "IH";
	public static final String BERTHING_STATUS_SW = "SW";
	public static final String BERTHING_STATUS_HA = "HA";

	public static final String BERTHING_STATUSPREFIX_PENDING = "_PD";
	public static final String BERTHING_STATUSPREFIX_ALONGSIDE = "_AL";
	public static final String BERTHING_STATUSPREFIX_SAILED = "_SL";

	public static final String BERTHING_STATUSPREFIX_PREV_ALONGSIDE = "_PREV_AL";
	public static final String BERTHING_STATUSPREFIX_PREV_SAILED = "_PREV_SL";

	/* piggu */
	public static final String INSP_FUNC_VIEW = "VIEW";
	public static final String INSP_FUNC_FOLLOW_UP = "FOLLOW_UP";

	public static final String RI_PARTYTYPE_JP = "JP";
	public static final String RI_PARTYTYPE_ST = "ST";
	public static final String RI_PARTYTYPE_TE = "TE";
	public static final String RI_PARTYTYPE_OT = "OT";

	public static final String LOCTYPE_WAREHOUSE = "WH";
	public static final String LOCTYPE_OPENYARD = "OY";
	public static final String LOCTYPE_LEASEAREA = "LA";
	public static final String LOCTYPE_BERTH = "BR";
	public static final String LOCTYPE_OTHERS = "OA";

	public static final String ORNA_VERIFYNOSO = "V";
	public static final String ORNA_REVERIFYNOSO = "E";
	public static final String ORNA_SUSPENDNOSO = "U";

	// Chua 17/02/2010

	public static final String AUTOJOB_POLLBERTHINGCHANGES = "PollBerthingChangesJob";
	public static final String AUTOJOB_POLLRULESCHEDULE = "PollRuleScheduleJob";
	public static final String AUTOJOB_VALIDATEARRDEPPILOTBOOKING = "ValidateArrDepPilotBookingJob";
	public static final String AUTOJOB_PROCESSPILOTAGEFIL = "ProcessPilotageFileJob";
	public static final String AUTOJOB_POLLGCOPRPLANCHK = "PollGCOprPlanChkJob";
	public static final String AUTOJOB_BULKSTANDBYCHK = "BulkStandByChkJob";
	public static final String AUTOJOB_BULKSTANDBY62CHK = "BulkStandBy62ChkJob";
	public static final String AUTOJOB_VALIDATEVSLMOVEREMINDER = "ValidateVslMoveReminderJob";
	public static final String AUTOJOB_POLLSYNCHRONIZECHANGES = "PollSynchronizeChangesJob";
	public static final String AUTOJOB_POLLBALSHTCHK = "PollBalShtChkJob";
	public static final String AUTOJOB_POLLCRANELIST = "PollCraneListJob";
	public static final String AUTOJOB_POLLUNLOADERLIST = "PollUnloaderListJob";
	public static final String AUTOJOB_POLLSSTALERTLIST = "PollSSTAlertListJob";
	// ICR system changes starts
	public static final String AUTOJOB_VESSELPRODUCTIVITY = "VesselProductivityJob";
	public static final String AUTOJOB_GATEACTIVITY = "GateActivityJob";
	// ICR system changes ends

	public static final String RULEPARA_CD_VslMovementReminder1 = "VslMovementReminder1";
	public static final String RULEPARA_CD_VslMovementReminder2 = "VslMovementReminder2";

	public static final String AUDIT_FIELDTYPE_NEW = "NEW";
	public static final String AUDIT_FIELDTYPE_OLD = "OLD";
	public static final String AUDIT_FIELDTYPE_DEL = "DEL";

	public static final String AUDIT_FNTYPE_LOGIN = "Login";
	public static final String AUDIT_FNTYPE_CHANGEPASSWORD = "Change Password";
	public static final String AUDIT_FNTYPE_VSLOPS = "VSL OPS";
	public static final String AUDIT_FNTYPE_CARGOOPS = "CARGO OPS";
	public static final String AUDIT_FNTYPE_BULKCARGOOPS = "BULK CARGO OPS";
	public static final String AUDIT_FNTYPE_STORAGEOPS = "STORAGE OPS";
	public static final String AUDIT_FNTYPE_INSPECTIONOPS = "INSPECTION OPS";
	public static final String AUDIT_FNTYPE_SPECIALOPS = "SPECIAL OPS";
	public static final String AUDIT_FNTYPE_BCOTIMESHEET = "BCO TIMESHEET";
	public static final String AUDIT_FNTYPE_CONTAINEROPS = "CONTAINER OPS";

	// Login
	public static final String AUDIT_FNSUBTYPE_LOGIN = "Login";
	public static final String AUDIT_FNSUBTYPE_LOGOUT = "Logout";

	// Change Password
	public static final String AUDIT_FNSUBTYPE_CHANGEPASSWORD = "Change Password";

	// VSL OPS
	public static final String AUDIT_FNSUBTYPE_ATB_UPDATE = "ATB Upd";
	public static final String AUDIT_FNSUBTYPE_ATU_UPDATE = "ATU Upd";
	public static final String AUDIT_FNSUBTYPE_VESSELMOVEMENTINFO_UPDATE = "VslMovement Upd";
	public static final String AUDIT_FNSUBTYPE_BUNKERINGINFO_UPDATE = "Bunkering Upd";
	public static final String AUDIT_FNSUBTYPE_WATERSUPPLYINFO_UPDATE = "WaterSupply Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOACTIVITY_UPDATE = "CrgActivity Upd";
	public static final String AUDIT_FNSUBTYPE_HANDINGOVERCHECKLIST_UPDATE = "HandingOver Upd";
	public static final String AUDIT_FNSUBTYPE_TAKINGOVERCHECKLIST_UPDATE = "TakingOver Upd";

	public static final String AUDIT_FNSUBTYPE_BUNKERINGINFO_ADD = "Bunkering Add";
	public static final String AUDIT_FNSUBTYPE_WATERSUPPLYINFO_ADD = "WaterSupply Add";
	public static final String AUDIT_FNSUBTYPE_HANDINGOVERCHECKLIST_ADD = "HandingOver Add";

	public static final String AUDIT_FNSUBTYPE_BUNKERINGINFO_DEL = "Bunkering Del";
	public static final String AUDIT_FNSUBTYPE_WATERSUPPLYINFO_DEL = "WaterSupply Del";

	// CARGO OPS
	public static final String AUDIT_FNSUBTYPE_CONTAINERDISCHARGEINFO_UPDATE = "CtrDisc Upd";
	public static final String AUDIT_FNSUBTYPE_CONTAINERLOADINFO_UPDATE = "CtrLoad Upd";
	public static final String AUDIT_FNSUBTYPE_CRANEWHARFMARK_UPDATE = "CraneWharfM Upd";
	public static final String AUDIT_FNSUBTYPE_CARGODISCLOADINFO_UPDATE = "CrgDiscLoad Upd";
	public static final String AUDIT_FNSUBTYPE_CARGODISCHARGLOADUNDERPERFREASON_UPDATE = "CrgDiscLoad Upd";
	public static final String AUDIT_FNSUBTYPE_TIMESHEETENTRY_CREATE = "TimesheetEntry";
	public static final String AUDIT_FNSUBTYPE_REEFERACT_UPDATE = "Reefer Act Upd";
	public static final String AUDIT_FNSUBTYPE_REEFERACT_ADD = "Reefer Act Add";

	public static final String AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_ADD = "CrgOpenBal Add";
	public static final String AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_DELETE = "CrgOpenBal Del";
	public static final String AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_UPDATE = "CrgOpenBal Upd";

	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLAN_ADD = "CrgOprPlan Add";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLAN_DELETE = "CrgOprPlan Del";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLAN_UPDATE = "CrgOprPlan Upd";

	public static final String AUDIT_FNSUBTYPE_CARGOOPR_UPDATE = "CrgOpr Upd";

	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLANDET_ADD = "CrgOprPlanDet Add";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLANDET_DELETE = "CrgOprPlanDet Del";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLANDET_UPDATE = "CrgOprPlanDet Upd";

	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEET_UPDATE = "CrgTallySheet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEET_DELETE = "CrgTallySheet Del";

	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEETDET_UPDATE = "CrgTallySheetDet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEETDET_DELETE = "CrgTallySheetDet Del";

	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_UPDATE = "CrgTimeSheetAct Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_DELETE = "CrgTimeSheetAct Del";

	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_UPDATE = "CrgEqRental Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_DELETE = "CrgEqRental Del";

	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEET_UPDATE = "CrgTimeSheet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEET_DELETE = "CrgTSTimeSheet Del";

	public static final String AUDIT_FNSUBTYPE_CARGOVSLPROD_ADD = "CrgVslProd Add";
	public static final String AUDIT_FNSUBTYPE_CARGOVSLPROD_DELETE = "CrgVslProd Del";
	public static final String AUDIT_FNSUBTYPE_CARGOVSLPROD_UPDATE = "CrgVslProd Upd";

	// BULK CARGO OPS
	public static final String AUDIT_FNSUBTYPE_BULKCARGOOPR_ADD = "Bulk CrgOpr Add";
	public static final String AUDIT_FNSUBTYPE_BULKCARGOOPR_UPDATE = "Bulk CrgOpr Upd";
	public static final String AUDIT_FNSUBTYPE_BULKCARGOOPR_DELETE = "Bulk CrgOpr Del";

	public static final String AUDIT_FNSUBTYPE_BULKCARGOPROD_ADD = "Bulk CrgProd Add";
	public static final String AUDIT_FNSUBTYPE_BULKCARGOPROD_UPDATE = "Bulk CrgProd Upd";

	// STORAGE OPS
	public static final String AUDIT_FNSUBTYPE_DAILYOCCUPANCY_UPDATE = "DailyOccupany Upd";
	public static final String AUDIT_FNSUBTYPE_SPACEUTILIZATION_UPDATE = "SpaceUtilise Upd";
	public static final String AUDIT_FNSUBTYPE_WAREHOUSEAPPMONITORINFO_UPDATE = "WarehouseApp Upd";

	// INSPECTION OPS
	public static final String AUDIT_FNSUBTYPE_NOSOSTATUS_UPDATE = "NosoStatus Upd";
	public static final String AUDIT_FNSUBTYPE_INSPECTION_UPDATE = "Inspection Upd";
	public static final String AUDIT_FNSUBTYPE_INSPECTION_ADD = "Inspection Add";
	public static final String AUDIT_FNSUBTYPE_INSPECTION_FOLLOWUP = "Inspection Followup";

	// SPECIAL OPS
	public static final String AUDIT_FNSUBTYPE_SPECIALEVENTINFO_UPDATE = "SpecialEvent Upd";
	public static final String AUDIT_FNSUBTYPE_SPECIALEVENTINFO_ADD = "SpecialEvent Add";
	public static final String AUDIT_FNSUBTYPE_ALERTACTION_UPDATE = "AlertAction Upd";

	// BCO TIMESHEET
	public static final String AUDIT_FNSUBTYPE_BCO_CREATETIMESHEET = "Create TimeSheet";
	public static final String AUDIT_FNSUBTYPE_BCO_CREATETIMESHEETENTRY = "CreateTimeSheetEntry";
	public static final String AUDIT_FNSUBTYPE_BCO_ENDTIMESHEET = "End TimeSheet";

	// CONTAINER OPS
	public static final String AUDIT_FNSUBTYPE_CONTAINER_DISCHARGEINFO_UPDATE = "CtrDisInfo Upd";
	public static final String AUDIT_FNSUBTYPE_CONTAINER_LOADINFO_UPDATE = "CtrLoadInfo Upd";

	public static final String AUDIT_KEYID_VVCD = "VV_CD";
	public static final String AUDIT_KEYID_BERTHNBR = "BerthNbr";
	public static final String AUDIT_KEYID_CRANEID = "CraneId";
	public static final String AUDIT_KEYID_APPREFNBR = "AppRefNbr";
	public static final String AUDIT_KEYID_CNTRNBR = "CntrNbr";
	public static final String AUDIT_KEYID_CNTRSEQNBR = "CNTR_SEQ_NBR";
	public static final String AUDIT_KEYID_MARKDTTM = "MarkDttm";
	public static final String AUDIT_KEYID_WAREFNBR = "WARefNbr";
	public static final String AUDIT_KEYID_NOSOID = "NOSO_ID";
	public static final String AUDIT_KEYID_INSPECTION_NBR = "INSP_SEQ_NBR";
	public static final String AUDIT_KEYID_EVENT_NBR = "EVENT_SEQ_NBR";
	public static final String AUDIT_KEYID_TIMESHEET_NBR = "TIMESHEET_NBR";
	public static final String AUDIT_KEYID_CRGTYPE = "CargoType";
	public static final String AUDIT_KEYID_STEVCOCD = "StevCoCd";

	// end Chua

	public static final String CargoOprErr_NOSTEV = "NOSTEV";
	public static final String CargoOprErr_NOOPENBAL = "NOOPENBAL";
	public static final String CargoOprErr_NOFIRSTACTIVITY = "NOFIRSTACTIVITY";
	public static final String CargoOprErr_SHIFTLESSTHANFIRSTACT = "SHIFTLESSTHANFIRSTACT";
	public static final String CargoOprErr_SHIFTGREATERTHANLASTACT = "SHIFTGREATERTHANLASTACT";
	public static final String CargoOprErr_SHIFTGREATERTHANCURRENTTIME = "SHIFTGREATERTHANCURRENTTIME";
	public static final String CargoOprErr_NOPREVSHIFT = "NOPREVSHIFT";
	public static final String CargoOprErr_NOSHIFT = "NOSHIFT";

	public static final String PARA_CAT_CD_VESSEL = "VC";
	public static final String PARA_CAT_CD_STORAGE = "ST";
	public static final String PARA_CAT_CD_GENERAL = "GN";
	public static final String PARA_CAT_CD_CONTROLCENTER = "CC";

	public static final String TABLE_GBCCWATERSUPPLY = "GBCC_WATER_SUPPLY";
	public static final String TABLE_GBCCBUNKERING = "GBCC_BUNKERING";
	public static final String TABLE_GBCCBERTHHANDOVER = "GBCC_BERTH_HANDOVER";
	public static final String TABLE_GBCCBERTHHANDOVERDET = "GBCC_BERTH_HANDOVER_DET";
	public static final String TABLE_GBCCCARGOOPR = "GBCC_CARGO_OPR";
	public static final String TABLE_GBCCCARGOOPRDET = "GBCC_CARGO_OPR_DET";
	public static final String TABLE_GBCCCARGOOPRPLAN = "GBCC_CARGO_OPRPLAN";
	public static final String TABLE_GBCCCARGOOPRPLANDET = "GBCC_CARGO_OPR_PLANDET";
	public static final String TABLE_GBCCCARGOTALLYSHEET = "GBCC_CARGO_TALLYSHEET";
	public static final String TABLE_GBCCCARGOTIMESHEET = "GBCC_CARGO_TIMESHEET";
	public static final String TABLE_GBCCCARGOTALLYSHEETDET = "GBCC_CARGO_TALLYSHEET_DET";
	public static final String TABLE_GBCCCARGOTIMESHEETACT = "GBCC_CARGO_TIMESHEET_ACT";
	public static final String TABLE_GBCCCARGOTALLYSHEETEQRENTAL = "GBCC_CARGO_TIMESHEET_EQRENTAL";
	public static final String TABLE_GBCCBULKCARGOOPR = "GBCC_BULK_CARGO_OPR";
	public static final String TABLE_GBCCBULKCARGOPROD = "GBCC_BULK_CARGO_PROD";
	public static final String TABLE_GBCCCARGOOPENBAL = "GBCC_CARGO_OPEN_BAL";
	public static final String TABLE_GBCCCARGOOPENBALDET = "GBCC_CARGO_OPEN_BAL_DET";
	public static final String TABLE_GBCCREEFERACT = "GBCC_REEFER_ACT";
	public static final String TABLE_GBCCSPECIALEVENT = "GBCC_SPECIAL_EVENT";
	public static final String TABLE_GBCCVSLHATCHPOS = "GBCC_VSL_HATCH_POS";
	public static final String TABLE_GBCCVSLPROD = "GBCC_VSL_PROD";

	public static final String TABLE_BERTHING = "BERTHING";
	public static final String TABLE_VESSELCALL = "VESSEL_CALL";
	public static final String TABLE_CSPSPALLOC = "CSPS_P_ALLOC";
	public static final String TABLE_CSPSBAYITEM = "CSPS_BAY_ITEM";
	public static final String TABLE_CSPSOBAY = "CSPS_O_BAY";
	public static final String TABLE_QUAYCRANE = "QUAY_CRANE";
	public static final String TABLE_WAAPPLNMONITORGDETAILS = "WA_APPLN_MONITORG_DETAILS";
	public static final String TABLE_ORNA_NOSO = "ORNA_NOSO";
	public static final String TABLE_ORNA_INSP = "ORNA_INSP";
	public static final String TABLE_TIMESHEETVSLOPS = "TIMESHEET_VSL_OPS";
	public static final String TABLE_TIMESHEETVSLOPSDETAILS = "TIMESHEET_VSL_OPS_DETAILS";

	// Jasper Report
	public static final String GBCC_ALERTREPORT = "Gbcc.AlertReport";
	
	public static final String MiscAppUpload_MacDir= "Machine";
	public static final String MiscAppUpload_ContractDir= "ContractorPermit";

	
	public static final String ExceptionAlert_ReportRoot = "";
	
	/*---------------------End sg.com.ntc.gbcc.util.Constants------------------------*/

}

package sg.com.jp.generalcargo.util;

public interface TpaConstants {

	// List
	public static final Integer START = 0;
	public static final Integer LIMIT = 20;

	public static final String NA = "N/A";
	public static final String ALL = "All";

	// System Param
	public static final String TPA_TIME = "TPA_TIME";
	public static final String TPA_FRTM = "TPA_FRTM";
	public static final String TPA_BLK = "TPA_BLK";
	public static final String TPA_BACKDT = "TPA_BACKDT";
	public static final String TPA_LST_RN = "TPA_LST_RN";
	public static final String TPA_FREE_BLOCKS = "TPA_FREBLK";

	public static final String DEFAULT_LIMITED_DAYS = "45";
	public static final String DEFAULT_HOURS_PER_BLOCK = "12";

	// TPA Status
	public static final String TPA_STATUS_DRAFT = "D";
	public static final String TPA_STATUS_SUBMITTED = "S";
	public static final String TPA_STATUS_ACCEPTED = "A";
	public static final String TPA_STATUS_REJECTED = "R";
	public static final String TPA_STATUS_VOID = "V";
	public static final String TPA_STATUS_CLOSE_FOR_BILLING = "C";
	public static final String TPA_STATUS_BILLED = "B";

	public static final String TPA_LIST_ACTIVE_STATUS = "L";

	// TPA Type
	public static final String TPA_TYPE = "TPA";

	// Add TPA Mode
	public static final String TPA_DRAFT = "D";
	public static final String TPA_SUBMIT = "S";
	public static final String TPA_ADD_MODE = "NEW";
	public static final String TPA_APPROVE_MODE = "APPROVE";
	public static final String TPA_UPDATE_MODE = "UPDATE";

	// Cargo/Container Status
	public static final String TPA_CNTR_CRG_STATUS_EX = "EX";
	public static final String TPA_CNTR_CRG_STATUS_IM = "IM";
	public static final String TPA_CNTR_CRG_STATUS_LN = "LN";
	public static final String TPA_CNTR_CRG_STATUS_RE = "RE";
	public static final String TPA_CNTR_CRG_STATUS_RS = "RS";
	public static final String TPA_CNTR_CRG_STATUS_SH = "SH";
	public static final String TPA_CNTR_CRG_STATUS_ST = "ST";
	public static final String TPA_CNTR_CRG_STATUS_TS = "TS";
	public static final String TPA_CNTR_CRG_STATUS_TX = "TX";
	public static final String TPA_CNTR_CRG_STATUS_L = "L";
	public static final String TPA_CNTR_CRG_STATUS_T = "T";
	public static final String TPA_CNTR_CRG_STATUS_R = "R";
	public static final String TPA_CNTR_CRG_STATUS_E = "E";

	public static final String TPA_CNTR_CRG_STATUS_EX_NM = "Export";
	public static final String TPA_CNTR_CRG_STATUS_IM_NM = "Import";
	public static final String TPA_CNTR_CRG_STATUS_LN_NM = "Land and Reship";
	public static final String TPA_CNTR_CRG_STATUS_RE_NM = "Re-Export";
	public static final String TPA_CNTR_CRG_STATUS_RS_NM = "Re-Shipment";
	public static final String TPA_CNTR_CRG_STATUS_SH_NM = "Shifting";
	public static final String TPA_CNTR_CRG_STATUS_ST_NM = "Storage";
	public static final String TPA_CNTR_CRG_STATUS_TS_NM = "Tranship";
	public static final String TPA_CNTR_CRG_STATUS_TX_NM = "Transit";
	public static final String TPA_CNTR_CRG_STATUS_L_NM = "Local";
	public static final String TPA_CNTR_CRG_STATUS_T_NM = "Transhipment";
	public static final String TPA_CNTR_CRG_STATUS_R_NM = "Re-export";

	// Cargo/Container Type
	public static final String TPA_CNTR_CRG_TYPE_NORMAL = "N";
	public static final String TPA_CNTR_CRG_TYPE_NORMAL_DES = "Normal";

	public static final String TPA_CNTR_CRG_TYPE_OOG = "O";
	public static final String TPA_CNTR_CRG_TYPE_OOG_DES = "OOG";

	public static final String TPA_CNTR_CRG_TYPE_DG = "D";
	public static final String TPA_CNTR_CRG_TYPE_DG_DES = "DG";

	// Result Status
	public static final String TPA_RESULT_OK = "OK";
	public static final String TPA_RESULT_ERROR = "ERR";

	// Email Group Code
	public static final String TPA_VOID_GROUP_CODE = "TPV";
	public static final String TPA_SUBMIT_GROUP_CODE = "TPS";

	public static final String TPA_EMAIL_SENDER = "jp@jp.com.sg";

	// System para for auto assign and approve
	public static final String TPA_AUTO_ASSIGN_APPROVE_CD = "TPAAT";

	// Black list group code in Text_para
	public static final String TPA_BLACK_LIST_GROUP_CODE = "TPA_BLKLST";

	public static final String TPA_TRAILER_SIZE_20_CD = "20";
	public static final String TPA_TRAILER_SIZE_40_CD = "40";

	public static final String HOUR_PER_BLOCK = "12";

	// Properties for Send Email/Message
	public static final String TPA_EMAIL_PROPERTY_PREFIX = "EMAIL.";
	public static final String TPA_SMS_PROPERTY_PREFIX = "SMS.";
	public static final String TPA_SENDING_CASE_SUBMITTED = "TpaSubmitted.";
	public static final String TPA_SENDING_CASE_APPROVED_REJECTED = "TpaApprovedRejected.";
	public static final String TPA_SENDING_CASE_VOIDED = "TpaVoided.";
	public static final String TPA_FROM = "from";
	public static final String TPA_SUBJECT = "subject";
	public static final String TPA_BODY = "body";

}

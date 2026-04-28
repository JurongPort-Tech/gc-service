package sg.com.jp.generalcargo.util;
import java.util.Hashtable;

/**
 *System Name: GBMS (General Bulk Cargo Management System)
 *Component ID: MiscAppConstValueObject.java
 *Component Description: Const Application Types 
 * 
 *@author      Anandhi
 *@version     1.0
 *@since       19 March 2007
 */

/*Revision History
 * ================
 * Author   Request Number  Description of Change   Version     Date Released
 * ------   --------------  ---------------------   -------     -------------
 * Anandhi                  Creation                1.0         19 March 2007
 * Thanhnv2  TPA  		  Added type code for TP   1.1         02 Mar 2011
 *
 */

public final class MiscAppConstValueObject {

	public static final String MISC_APP_CONTRACTOR_PERMIT = "CTP";
	public static final String MISC_APP_HOT_WORK_PERMIT = "HOT";
	public static final String MISC_APP_HIRE_OF_MOBILE_HARBOUR_CRANE = "MHC";
	public static final String MISC_APP_OVERNIGHT_PARKING_OF_FORKLIFT_SHORE_CRANE = "ONE";
	public static final String MISC_APP_OVERNIGHT_PARKING_OF_VEHICLE = "ONV";
	public static final String MISC_APP_PARKING_OF_LINE_TOW_BARGE = "LTB";
	public static final String MISC_APP_REEFER_CONTAINER_POWER_OUTLET = "ELE";
	public static final String MISC_APP_STATIONING_OF_FORKLIFT_CNTRLIFT_WHEELLOADER_SHORECRANE = "STE";
	public static final String MISC_APP_USE_OF_SPACE = "SPA";
	public static final String MISC_APP_HIRE_OF_WOODEN_STEEL_SPREADER = "WSS";
	// START 02-Mar-2011 - TPA � Thanhnv2 added type code for trailer parking
	// applications
	public static final String MISC_APP_TRAILER_PARKING_APPLICATION = "TPA";
	// END 02-Mar-2011 - TPA � Thanhnv2 added type code for trailer parking
	// applications

	public final static  String SLOT_STATUS_NAME_EMPTY_NORMAL = "Normal";

	public final static  String SLOT_STATUS_CODE_EMPTY_NORMAL= "N";

	public final static  String SLOT_STATUS_NAME_DG= "DG";

	public final static  String SLOT_STATUS_CODE_DG= "D";

	public final static  String SLOT_STATUS_NAME_OOG= "OOG";

	public final static  String SLOT_STATUS_CODE_OOG= "O";

	//Start added by thanhbtl6b for TPA Enhancement
	public final static String TRAILER_TYPE_CODE_E= "E";

	public final static String TRAILER_TYPE_CODE_L= "L";

	public final static String TRAILER_TYPE_NAME_E= "Empty";

	public final static String TRAILER_TYPE_NAME_L= "Laden";	

	//Start added by thanhbtl6b for TPA Enhancement 

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
	public static Hashtable<String, String> SLOT_TYPE_TABLE = new Hashtable<String, String>();
	static {
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_EMPTY_NORMAL, SLOT_STATUS_NAME_EMPTY_NORMAL);
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_DG, SLOT_STATUS_NAME_DG);
		SLOT_TYPE_TABLE.put(SLOT_STATUS_CODE_OOG, SLOT_STATUS_NAME_OOG);
	}

	//trailers
	public static Hashtable<String, String> TRAILER_TYPE_TABLE = new Hashtable<String, String>();
	static{
		TRAILER_TYPE_TABLE.put(TRAILER_TYPE_CODE_E,TRAILER_TYPE_NAME_E);
		TRAILER_TYPE_TABLE.put(TRAILER_TYPE_CODE_L, TRAILER_TYPE_NAME_L);
	}

	public static Hashtable<String, String> SLOT_STATUS_TABLE = new Hashtable<String, String>();

	static {
		SLOT_STATUS_TABLE.put(SLOT_STATUS_DELETED, SLOT_STATUS_DELETED_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_OPEN, SLOT_STATUS_OPEN_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_CLOSE, SLOT_STATUS_CLOSE_CD);
		SLOT_STATUS_TABLE.put(SLOT_STATUS_RESERVED, SLOT_STATUS_RESERVED_CD);
	}

	public static String[] HOUR_LIST  = {"0000", "0030", "0100", "0130", "0200", "0230", "0300", "0330", "0400", "0430", "0500", 
			"0530", "0530", "0600", "0630", "0700", "0730", "0800", "0830", "0900", "0930", "1000", 
			"1030", "1100", "1130", "1200", "1230", "1300", "1330", "1400", "1430", "1500", "1530", 
			"1600", "1630", "1700", "1730", "1800", "1830", "1900", "1930", "2000", "2030", "2100", 
			"2130", "2200", "2230", "2300", "2330"};


}


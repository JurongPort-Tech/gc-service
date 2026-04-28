package sg.com.jp.generalcargo.util;

import java.util.HashMap;

/**
 * System Name: TOPs (Terminal Operation and Planning System) Component ID:
 * ProcessChargeConst.java (CAB-Process Charge) Component Description: All the
 * constants
 *
 * @author Ho Si Wai
 * @version 29/11/2001
 */

/*
 * Revision History ---------------- Author Date Released Request Number
 * Description of Change
 * ---------------------------------------------------------------------------
 * HoSW 29/11/2001 Creation Valli 11-Oct-2005 CR-CAB-20040922-2 DG billing
 * changes 1. Tariff sub category Chassis Parking 'CP' is added 2. Changed Local
 * import LL to IM and local export LE to EX Valli 21-Oct-2005 CR-CAB-20040922-2
 * Local import back to LL and local export back to LE Added local import ct IM
 * and local export CT EX Valli 05-Dec-2005 CR-CAB-20040922-2 Added DG
 * non-storable grace period para_cd (SYSTEM_PARA table) Valli 24-Mar-2006
 * CR-CAB-20050823-02 Added constants for Lolo rebates Valli 27-Apr-2006
 * CTCIM200400TT TT constants are added Valli 25-May-2006 CR-CAB-20050823-02
 * Added LOREB for Lolo rebates Valli 2-Nov-06 CR-CAB-20060224-01 Reefer IM
 * billing to different bill parties Valli 4-Apr-2007 GB-CAB-20061211-1
 * Automation of GB Misc charges Ai Lin 17-Jul-2007 GB-CAB-20061211-1 Change
 * 'Loaded' to 'Laden' CFG 25-Jun-2007 CR-CAB-0000014 Automation of GB Warehouse
 * charges Thangtv 10-Aug-2007 JPOnline Added JPOnline tariff GiangNN
 * 13-Sep-2007 JPOnline Added new constant for Bill type ThangTV 20-Feb-2008
 * Penjuru Billing Added more setting tariff Cecille 12-May-2008 Added new
 * vessel scheme for wooden craft ThanhTH 12-May-2008 CR-CAB-20080409-02 Added
 * more setting tariff for Mooring Cecille 31 Jul 2008 Uncommented constant for
 * Service Charge for Overside Cargo Cecille 07-Oct-2008 Added DEFAULT_CNTR_SIZE
 * for Lashing/unlashing Cecille 17 Dec 2008 Adding Cally's vendor's changes for
 * Vessel Productivity Surcharge DucTA 15-Jan-2008 Added cargo_category_code
 * Huey Min 01 Apr 2009 SL-CAB-20090327 Unable to create customise tariff for
 * Penjuru Customer VietNguyen/FPT CR-CAB-20080805-37 implement
 * CR-CAB-20080805-37 Uturn 26-Sep-2009 VietNguyen 09 Nov 2009 CCTS Added to
 * billing MOT for CR-Conventional Container Tracking System VietNguyen/FPT02
 * Jan 2010 CR-CIM-20091203-34 New sub cat for Billing -Change Manifest
 * Submission cut-off time from ATU to ATB VietNguyen 01 Sep 2009 Empty Scheme
 * new constant Cecille 14 Jan 2011 CR-CAB-20101124-011 Lashing-Unlashing: no
 * charges for cntrs below deck (CMA CGM) Ding Xijia(harbortek) 24 Jan 2011
 * Added lct scheme Cecille 12 May 2011 CR-CAB-20110429-009 Lashing-Unlashing
 * II: add MISC_TYPE_CODE_UDECK_INCL_SO Cecille 22 Jun 2011 harvest issue manual
 * merge with harvest prod (somehow prod version different from checked-out) Hu
 * Jun 2 AUG 2011 add shutout cargo sub category Cecille 13 Oct 2011
 * CR-CAB-20100506-005 add TARIFFTIER_BP_NUM_PER_PAGE for tariff tier bill party
 * pagination VietNguyen(FPT) 07 Dec 2011 OMC added for OMC billing ThangPV(FPT)
 * 17 Apr 2013 LWMS added for LWMS Ashish(MCC) 07 Jul 2013 VTSB Project add
 * billing parameters for VTSB MCConsulting 10 Dec 2014 Added for Ship store
 * admin charges MC Consulting 01 Feb 2015 LWMS KCP MC Consulting 20 Mar 2015
 * Admin fee waiver oscar calls & periodic store rent constants Jade Sep 2017
 * NOM Billing CT Nov OMC billing Sripriya Feb 2018 New EVM Enhancements
 * Sripriya May 2018 Combi Enhancements for Line Tow Barge
 */

public final class ProcessChargeConst {
	public static final String TARIFF_MAIN_STEVEDORAGE = "SV";
	public static final String TARIFF_MAIN_MARINE = "MA";
	public static final String TARIFF_MAIN_STORE_RENT = "ST";
	public static final String TARIFF_MAIN_LOLO = "LO";
	public static final String TARIFF_MAIN_ADMIN = "AD";
	public static final String TARIFF_MAIN_OTHERS_BY_VESSEL = "OV";
	public static final String TARIFF_MAIN_OTHERS_BY_FREQUENCY = "OF";
	public static final String TARIFF_MAIN_CLOSING_TIME_PENALTY = "CL";

	// added by Thangtv 10-Aug-2007
	// JPOnline Billing
	public static final String TARIFF_MAIN_JPONLINE = "JO";
	// end add by Thangtv

	// added by swho on 12/06/2002 for GBMS General CargSCVo processing
	public static final String TARIFF_MAIN_WHARFAGE = "WF";
	public static final String TARIFF_MAIN_SERVICE_CHARGE = "SC";
	// end add by swho

	// Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing
	public static final String TARIFF_MAIN_GB_STORE_RENT = "SR";
	// End Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing

	// added by GCL on 13/08/2002
	public static final String TARIFF_MAIN_GB_STORAGE = "SW";
	public static final String TARIFF_MAIN_GB_CONTAINER_SERVICE_CHARGE = "CC";
	public static final String TARIFF_MAIN_GB_CONTAINER_STORE_RENT = "CR";
	public static final String TARIFF_MAIN_GB = "GB";
	public static final String TARIFF_MAIN_BC = "BC";
	public static final String TARIFF_MAIN_CT = "CT";
	// end of addition by GCL

	// VietNguyen added on 02-Jan-2010 for CR-CIM-20091203-34 : START
	// Billing for Change Manifest Submission cut-off time from ATU to ATB
	// new mainCat for billing
	public static final String TARIFF_MAIN_MANIFEST_AMENDMENT = "MF";
	// VietNguyen added on 02-Jan-2010 for CR-CIM-20091203-34 : END

	// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
	public static final String OMC_BUSINESS_TYPE = "J";
	public static final String OMC_SCHEME_CODE = "000";

	public static final String TARIFF_MAIN_OMC_DOCKAGE = "JM";
	public static final String TARIFF_MAIN_OMC_BERTH_UNBERTH = "JM";
	public static final String TARIFF_MAIN_OMC_FM = "JM";
	public static final String TARIFF_MAIN_OMC_MPA = "JW";
	public static final String TARIFF_MAIN_OMC_STEVEDORAGE = "JW";
	public static final String TARIFF_MAIN_OMC_WHARFAGE = "JW";

	public static final String TARIFF_MAIN_OMC_WHARFSIDE = "WS";
	public static final String TARIFF_MAIN_OMC_EQUIPMENT_RENTAL = "EL";
	public static final String TARIFF_MAIN_OMC_OTHER = "AS";
	public static final String TARIFF_MAIN_OMC_PLOT_USAGE = "PL";

	public static final String TARIFF_SUB_OMC_DOCKAGE = "DK";
	public static final String TARIFF_SUB_OMC_BERTH_UNBERTH = "MR";
	public static final String TARIFF_SUB_OMC_WHARFAGE = "WF";
	public static final String TARIFF_SUB_OMC_FM = "FM";

	public static final String TARIFF_SUB_OMC_WHARFSIDE = "QQ";
	public static final String TARIFF_SUB_OMC_PLOT_USAGE = "QQ";
	public static final String TARIFF_SUB_OMC_EQUIPMENT_RENTAL = "QQ";
	public static final String TARIFF_SUB_OMC_OTHER = "QQ";
	public static final String TARIFF_SUB_OMC_MPA = "MP";
	public static final String TARIFF_SUB_OMC_STEVEDORAGE = "SV";

	public static final String TARIFF_MAIN_OMC_RENTAL = "JT";
	public static final String TARIFF_SUB_OMC_RENTAL_CANTEEN = "CR";

	public static final String TARIFF_MAIN_OMC_UTIL = "JV";
	public static final String TARIFF_SUB_OMC_UTIL_ELECTRICITY = "EL";
	public static final String TARIFF_SUB_OMC_UTIL_WATER_BOND = "WB";
	public static final String TARIFF_SUB_OMC_UTIL_WATER_FEE = "WF";
	public static final String TARIFF_SUB_OMC_UTIL_WATER_SURCHARGE = "WS";

	// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END

	/* Added for Water Order Activity OMC Start - 26 NOV 2013 */
	public static final String TARIFF_MAIN_OMC_WATER_ORDER = "WB";
	public static final String TARIFF_MAIN_OMC_WATER_ORDER_CHARGE = "AD";
	public static final String TARIFF_SUB_OMC_WATER_ORDER_SUPPLY = "SW";
	public static final String TARIFF_SUB_OMC_WATER_ORDER_WATERBORNE_FEE = "WF";
	public static final String TARIFF_SUB_OMC_WATER_ORDER_GOVT_TAX = "GC";
	public static final String TARIFF_SUB_OMC_WATER_ORDER_LATE_FEE = "WL";
	public static final String WATER_ORDER_OMC_SCHEME_CODE = "000";
	public static final String WATER_ORDER_OMC_BUSINESS_TYPE = "J";
	/* Added for Water Order Activity OMC End */

	// added on 03/04/2003 by Irene Tan - For Bulk Cargo processing
	public static final String TARIFF_MAIN_GB_BULK_HANDLING = "HC";
	public static final String TARIFF_MAIN_GB_BULK_OTHERS = "OT";
	// end added by Irene tan on 03/04/2003

	// added by swho on 14/01/2004 for GBMS Containerized Cargo processing
	public static final String TARIFF_MAIN_GC = "GC";
	// end add by swho

	// added by Thangtv 10-Aug-2007
	// JPOnline Billing
	public static final String TARIFF_SUB_JPONLINE_REG = "01";
	public static final String TARIFF_SUB_JPONLINE_SUB = "02";
	public static final String TARIFF_SUB_JPONLINE_SUB_ADD = "03";
	// end add by Thangtv

	public static final String TARIFF_SUB_STE_GENERAL = "GL";
	public static final String TARIFF_SUB_STE_SHIFT = "SH";
	public static final String TARIFF_SUB_STE_LASH_CELL = "XC";
	public static final String TARIFF_SUB_STE_LASH_NON = "XN";
	public static final String TARIFF_SUB_STE_REEFER = "CN";
	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : START
	public static final String TARIFF_SUB_STE_SP = "SP";
	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : END

	public static final String TARIFF_SUB_MAR_DOCKAGE = "01";
	public static final String TARIFF_SUB_MAR_OVERSTAY = "02";
	public static final String TARIFF_SUB_MAR_BERTH_UNBERTH = "03";
	public static final String TARIFF_SUB_MAR_BERTH_APPL_QUAYCRANE = "04";
	public static final String TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE = "05";

	// Combi tariff Line Tow Barge New Sub Cat 18th May 2018 Start
	public static final String TARIFF_SUB_MAR_CT_SPECIAL_LINETOW = "TB";
	// Combi tariff Line Tow Barge New Sub Cat 18th May 2018 End

	// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
	public static final String TARIFF_SUB_MAR_LATE_ARRIVAL = "06";
	public static final String TARIFF_SUB_MAR_SPECIAL_DOCKAGE = "07";
	// end add by swho

	// Added by Ruchika (Zensar) on 13/08/2007 for new overstay dockage category in
	// main category Marine
	public static final String TARIFF_SUB_MAR_OVERSTAY_DOCKAGE = "08";
	// end of addition by Ruchika (Zensar)

	// <cfg adding cally's vendor's codes, 17.dec.08>
//  Added by Venkat (Zensar) for Productivity Surcharge CR start
	public static final String TARIFF_SUB_MAR_PROD_SURCHG_TIER1 = "09";
	public static final String TARIFF_SUB_MAR_PROD_SURCHG_TIER2 = "10";

	// end of addtion by Venkat for Porudctivity Surcharge end
	// <cfg adding cally's vendor's codes, 17.dec.08/>

	public static final String TARIFF_SUB_STO_GENERAL = "GL";
	// DG Billing change
	// Chassis Parking CP added - Valli 11-Oct-2005
	public static final String TARIFF_SUB_STO_CHASSIS_PARKING = "CP";

	public static final String TARIFF_SUB_LOLO = "LO";

	public static final String TARIFF_SUB_OTHERS_VSL_SHUTOUT = "ST";
	public static final String TARIFF_SUB_OTHERS_VSL_DIRECT_LOADING = "DL";
	public static final String TARIFF_SUB_OTHERS_VSL_STANDBY_LABOUR = "01";
	public static final String TARIFF_SUB_OTHERS_VSL_STANDBY_QUAY_CRANE = "02";
	public static final String TARIFF_SUB_OTHERS_VSL_STOWAGE_AMEND_REQUEST = "03";
	public static final String TARIFF_SUB_OTHERS_VSL_STOWAGE_AMEND_CNTR = "04";

	public static final String TARIFF_SUB_OTHERS_FREQ_RENOM = "RN";
	public static final String TARIFF_SUB_OTHERS_FREQ_EXTRA_MVT = "MV";
	public static final String TARIFF_SUB_OTHERS_FREQ_ITH_LOLO = "IL";
	public static final String TARIFF_SUB_OTHERS_FREQ_ITH = "IH";
	public static final String TARIFF_SUB_OTHERS_FREQ_REEFER_PRETRIP = "PT";
	public static final String TARIFF_SUB_OTHERS_FREQ_ELEC_SUPPLY = "RE";
	public static final String TARIFF_SUB_OTHERS_FREQ_CHANGE_STATUS = "CG";
	/*
	 * added by UmaDevi.Y Request No: CR-CAB-20040922-4 on 12\11\2004 to cater
	 * Container with Transhipment status to local status
	 */
	public static final String TARIFF_SUB_OTHERS_FREQ_CHANGE_STATUS_IMPORT = "CI";

	/* Ended by UmaDevi.Y */
	public static final String TARIFF_SUB_CLOSING_BERTH_APPL = "01";
	public static final String TARIFF_SUB_CLOSING_SUBMIT_BAYPLAN = "02";
	public static final String TARIFF_SUB_CLOSING_BERTH_AMEND = "03";
	public static final String TARIFF_SUB_CLOSING_ESN = "04";
	public static final String TARIFF_SUB_CLOSING_TLI = "05";
	public static final String TARIFF_SUB_CLOSING_CNTR_BOOKING = "06";
	public static final String TARIFF_SUB_CLOSING_STOWAGE_INSTRUCTION = "07";

	public static final String TARIFF_SUB_ADMIN_IMPORT_STATUS = "01";
	public static final String TARIFF_SUB_ADMIN_BERTH_APPL = "02";
	public static final String TARIFF_SUB_ADMIN_PIPELINE_WATER = "03";
	public static final String TARIFF_SUB_ADMIN_WATER_REQUEST = "04";
	public static final String TARIFF_SUB_ADMIN_DELIVERY_ORDER = "05";
	public static final String TARIFF_SUB_ADMIN_SHIPPING_NOTE = "06";
	public static final String TARIFF_SUB_ADMIN_REEXPORT_STATUS = "07";
	public static final String TARIFF_SUB_ADMIN_PREGATE_CANCEL = "08";
	public static final String TARIFF_SUB_ADMIN_SUBMIT_BAYPLAN = "09";
	public static final String TARIFF_SUB_ADMIN_DO_CANCEL = "10";
	public static final String TARIFF_SUB_ADMIN_CHANGES_AFTER_CLOSING = "11";
	public static final String TARIFF_SUB_ADMIN_CNTR_DETAILS_AMEND = "12";
	public static final String TARIFF_SUB_ADMIN_SEAL_AMEND = "13";
	public static final String TARIFF_SUB_ADMIN_WEIGHT_WRONG = "14";
	// Added on 05/07/2002 by Irene Tan - For Cash Sales processing
	public static final String TARIFF_SUB_ADMIN_WATER_SUPPLY = "01";
	public static final String TARIFF_SUB_ADMIN_FORKLIFT_BELOW_10_TON = "16";
	// End Added on 05/07/2002 by Irene Tan - For Cash Sales processing

	// Added on 08/07/2002 by Irene Tan - For GBMS General Cargo processing
	public static final String TARIFF_SUB_WHARF_GENERAL = "GL";
	public static final String TARIFF_SUB_WHARF_OVERSIDE = "OV";
	public static final String TARIFF_SUB_WHARF_SHIP_STORE = "SS";
	public static final String TARIFF_SUB_WHARF_SHUTOUT = "SO";
	public static final String TARIFF_SUB_WHARF_RORO_VSL = "RO";
	public static final String TARIFF_SUB_WHARF_ANIMAL = "AN";
	public static final String TARIFF_SUB_WHARF_REEXPORT = "RX";
	public static final String TARIFF_SUB_WHARF_STUFF_UNSTUFF = "SU";
	// Added on 6/1/2003 by Irene Tan - For GBMS General Cargo Pprocessing
	public static final String TARIFF_SUB_WHARF_EXPORT = "EX";
	// End Added by Irene Tan on 6/1/2003

	public static final String TARIFF_SUB_SERVICE_CHARGE_GENERAL = "GL";
	public static final String TARIFF_SUB_SERVICE_CHARGE_LAND_RESHIP = "LS";
	// Added on 25/07/2002 by Ai Lin - For GBMS General Cargo processing
	public static final String TARIFF_SUB_SERVICE_CHARGE_SHIP_STORE = "SS";

	/*
	 * // deprecated by GCL 13/08/2002 public static final String
	 * TARIFF_SUB_SERVICE_CHARGE_STUFF_UNSTUFF = "SU"; public static final String
	 * TARIFF_SUB_SERVICE_CHARGE_OVERSIDE = "OV"; //
	 */
	// <cfg, uncomment: SC for overside Cargo, 31.jul.08>
	public static final String TARIFF_SUB_SERVICE_CHARGE_OVERSIDE = "OV";

	// added by GCL 13/08/2002
	public static final String TARIFF_SUB_GB_CONTAINER_SERVICE_CHARGE_GENERAL = "GL";
	public static final String TARIFF_SUB_GB_CONTAINER_SERVICE_CHARGE_LAND_RESHIP = "LS";
	public static final String TARIFF_SUB_GB_CONTAINER_SERVICE_CHARGE_STUFF_UNSTUFF = "SU";
	public static final String TARIFF_SUB_GB_CONTAINER_SERVICE_CHARGE_OVERSIDE = "OV";

	public static final String TARIFF_SUB_GB_STORE_RENT_GENERAL = "GL";
	public static final String TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE = "US";
	public static final String TARIFF_SUB_GB_STORE_RENT_GENERAL_TRANSHIP = "GT";

	// START : added by VietNV 10-Nov-2009
	// for CR - Conventional Container Tracking System
	// for identify getting gate in time or gate out time
	public static final String GATE_OPERATION_IN = "I";
	public static final String GATE_OPERATION_OUT = "O";
	// For those storing order enter/exit within 24 hours, no store rent charged
	public static final double MOT_BILLING_FREE_STORENT_PERIOD = 24.0;
	// Time hour in day
	public static final double TIME_HOUR_IN_DAY = 24.0;
	// sub - tariff in case use flat rate
	public static final String TARIFF_SUB_GB_STORE_RENT_FLAT_RATE = "GY";
	// status in case flat rate
	public static final String STATUS_BOTH = "U";
	// new movement type for flatrate
	public static final String MVMT_STORAGE = "ST";
	// Only use 1 container category for flatrate - cat cd
	public static final String CAT_STORAGE_CD = "ST";
	// Only use 1 container category for flatrate - cat name
	public static final String CAT_STORAGE = "Storing Order";
	// Only use 1 container category for flatrate - cat name
	public static final String CAT_STORAGE_DG_IND = "N";

	// END : add by VietNV

	/*
	 * // deprecated by GCL 13/08/2002 public static final String
	 * TARIFF_SUB_GB_STORE_RENT_TRANSIT_WAREHOUSE = "TW"; public static final String
	 * TARIFF_SUB_GB_STORE_RENT_BACKUP_WAREHOUSE = "BW"; public static final String
	 * TARIFF_SUB_GB_STORE_RENT_OPEN_SPACE = "OS"; public static final String
	 * TARIFF_SUB_GB_STORE_RENT_CNTR_YARD = "CY"; //
	 */
	// added by GCL 13/08/2002
	public static final String TARIFF_SUB_GB_STORAGE_TRANSIT_WAREHOUSE = "TW";
	public static final String TARIFF_SUB_GB_STORAGE_BACKUP_WAREHOUSE = "BW";
	public static final String TARIFF_SUB_GB_STORAGE_OPEN_SPACE = "OS";
	public static final String TARIFF_SUB_GB_STORAGE_CNTR_YARD = "CY";
	public static final String TARIFF_SUB_GB_CONTAINER_STORE_RENT_GENERAL = "GL";
	// added by Alicia on 15/12/2003 (for Containerised cargo)
	// public static final String TARIFF_SUB_GB_CT_GENERAL_LINER_SCHEME = "GL";
	public static final String TARIFF_SUB_GB_CT_GENERAL_LINER_SCHEME = "GI";
	public static final String TARIFF_SUB_GB_CT_GENERAL_NON_LINER_SCHEME = "GN";
	public static final String TARIFF_SUB_GB_CT_GENERAL_BARTER_TRADER_SCHEME = "GB";
	public static final String TARIFF_SUB_GB_CT_GENERAL_OTHER_SCHEME = "GO";
	public static final String TARIFF_SUB_GB_CT_GENERAL_LCT_SCHEME = "GC";

	// public static final String TARIFF_SUB_GB_CT_OVERSIDE_LINER_SCHEME = "OL";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE_LINER_SCHEME = "OI";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE_NON_LINER_SCHEME = "ON";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE_BARTER_TRADER_SCHEME = "OB";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE_OTHER_SCHEME = "OO";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE_LCT_SCHEME = "OC";

	// public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_LINER_SCHEME = "LL";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_LINER_SCHEME = "LI";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_NON_LINER_SCHEME = "LN";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_BARTER_TRADER_SCHEME = "LB";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_OTHER_SCHEME = "LO";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP_LCT_SCHEME = "LC";

	public static final String TARIFF_SUB_GB_CT_GENERAL = "GL";
	public static final String TARIFF_SUB_GB_CT_OVERSIDE = "OS";
	public static final String TARIFF_SUB_GB_CT_LAND_RESHIP = "LS";
	// end add by Alicia on 15/12/2003 (for Containerised cargo)
	// added by Irene Tan on 07/04/2003 - for bulk cargo processing
	public static final String TARIFF_SUB_OTHERS_UNLOADER_STANDBY_FROM_VSL_ARRIVAL = "S1";
	public static final String TARIFF_SUB_OTHERS_UNLOADER_STANDBY_FROM_BERTH_TIME = "S2";
	public static final String TARIFF_SUB_OTHERS_JP_UNLOADER_STANDBY = "S3";
	public static final String TARIFF_SUB_OTHERS_NATSTEEL_UNLOADER_STANDBY = "S4";
	public static final String TARIFF_SUB_OTHERS_UNLOADER_STANDBY_SAFETY_INSPECT_FEE = "S5";
	public static final String TARIFF_SUB_OTHERS_UNLOADER_STANDBY_SAFETY_INSPECT_FEE_GST = "S6";
	// end added by Irene Tan on 07/04/2003 - for bulk cargo processing

	// VietNguyen added on 02-Jan-2010 for CR-CIM-20091203-34 : START
	// Billing for Change Manifest Submission cut-off time from ATU to ATB
	// new subCat for billing
	public static final String TARIFF_SUB_MANIFEST_AMENDMENT = "MF";
	// VietNguyen added on 02-Jan-2010 for CR-CIM-20091203-34 : END

	// GiangNN: Start: Add new contant for bill type
	// JPOnline Billing
	public static final String BILL_TYPE_SS = "SS";
	public static final String BILL_TYPE_SP = "SP";
	public static final String BILL_TYPE_SC = "SC";
	public static final String BILL_TYPE_R = "R";
	public static final String BILL_TYPE_FF = "FF";
	public static final String MISC_FLEXI_ACCESS_CAT_CODE = "JO_BILL";
	public static final String MISC_FLEXI_ACCESS_TYPE_CODE_USER_NUM = "USER_COUNT";
	public static final String MISC_FLEXI_ACCESS_TYPE_CODE_BLOCK_SIZE = "FLEXI_BLK";
	public static final String TARIFF_SUB_JPONLINE_FLEXI_ACCESS = "04";

	// GiangNN: End: Add new contant for bill type

	public static final String TYPE_TS_DELIVER_LOCALLY = "TS";
	public static final String TYPE_CAR = "01";
	public static final String TYPE_VAN_STATION_WAGON = "02";
	public static final String TYPE_BUS_LORRY = "03";
	public static final String TYPE_LIVE_STOCK = "LS";
	public static final String TYPE_WILD_CAGED_ANIMAL = "WA";
	public static final String TYPE_OTHER = "00";
	public static final String TYPE_BY_TON = "TN";
	public static final String TYPE_BY_AREA = "AR";
	// Added on 08/07/2002 by Irene Tan - For GBMS General Cargo processing

	public static final String MVMT_LOCAL = "LL";
	public static final String MVMT_TRANSHIP = "TS";
	// Added on 14/10/2003 by Satish - For Store Rent
	// Change on Import and Export from LL to IM and LE to EX - Valli 11-Oct-2005
	// back to LL and LE - 21-Oct-2005
	public static final String MVMT_LOCAL_IMPORT = "LL";
	public static final String MVMT_LOCAL_EXPORT = "LE";
	// Added by Valli 18-Oct-2005 for CT
	public static final String MVMT_LOCAL_IMPORT_CT = "IM";
	public static final String MVMT_LOCAL_EXPORT_CT = "EX";
	// End Added on 14/10/2003 by Satish - For Store Rent
	// Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing
	public static final String MVMT_ITH = "IT";
	public static final String NOT_APPLICABLE_MVMT = "00";
	// End Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing

	// Added by Valli
	public static final String MVMT_LAND_AND_RESHIP = "LN"; // CR-CAB-20060224-01

	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : START
	public static final String MVMT_EMPTY_SCHEME = "MT";
	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : END
	public static final String PURP_CD_EXPORT = "EX";
	public static final String PURP_CD_IMPORT = "IM";
	public static final String PURP_CD_LAND_RESHIP = "LN";
	public static final String PURP_CD_REEXPORT = "RE";
	public static final String PURP_CD_RESHIPMENT = "RS";
	public static final String PURP_CD_SHIFTING = "SH";
	public static final String PURP_CD_STORAGE = "ST";
	public static final String PURP_CD_TRANSHIP = "TS";
	public static final String PURP_CD_TRANSIT = "TX";
	// add Minimum Charge for LCT scheme, 28.feb.11 by hpeng
	public static final String PURP_CD_MINIMUM_CHARGE = "MC";

	public static final String STATUS_FULL = "F";
	public static final String STATUS_LCL = "L";
	public static final String STATUS_EMPTY = "E";

	public static final String TARIFF_TYPE_PUBLISH = "P";
	public static final String TARIFF_TYPE_CUSTOMIZE = "C";

	public static final String TARIFF_ADJ_TYPE_PERCENT = "P";
	public static final String TARIFF_ADJ_TYPE_AMOUNT = "A";

	public static final String BILL_PARTY_CNTR_OPR = "CO";
	public static final String BILL_PARTY_HAUL_OPR = "HAU";
	public static final String BILL_PARTY_SLOT_OPR = "SO";
	public static final String BILL_PARTY_VESL_OPR = "VO";

	public static final String DEFAULT_CAT_GENERAL = "GP";
	public static final String DEFAULT_CAT_GENERAL_EMP = "MT";

	// public static final String CONTAINER_BUSINESS = "CT";
	// public static final String GENERAL_BULK_BUSINESS = "GB";

	// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
	public static final String TARIFF_HOUR_RATE_HOUR = "H";
	public static final String TARIFF_HOUR_RATE_MIN = "M";

	public static final String CONTAINER_BUSINESS = "C";
	public static final String GENERAL_BUSINESS = "G";
	// added by Cally on 11/05/2016 New Business Type OMC Homeport
	public static final String HOMEPORT_BUSINESS = "H";
	public static final String BULK_BUSINESS = "B";
	// added by huey min on 01/04/2009 for checking of penjuru biz
	public static final String PENJURU_BUSINESS = "P";
	// linhph3 add 20/03/2013
	public static final String MARINA_SOUTH_BUSINESS = "N";

	public static final String CONTAINER_VSL = "Y";
	public static final String GENERAL_BULK_VSL = "N";
	public static final String SEMI_CONTAINER_VSL = "B";

	public static final String BUSINESS_OMC = "J";

	public static final String CONTAINER_TERMINAL = "CT";
	public static final String GENERAL_BULK_TERMINAL = "GB";
	public static final String ZERO_RATED = "ZZ";
	// added by swho on 290103 for using the new cab sequence table instead of
	// oracle sequence
	public static final String CONTAINER_WITHOUT_TARIFF = "MC";
	public static final String GENERAL_BULK_WITHOUT_TARIFF = "MB";
	// end add by swho

	public static final String OMC_TERMINAL = "OJ";

// added by swho on 14/01/2004 for GBMS Containerized Cargo processing
	public static final String CONTAINER_SCHEME = "JCT";
// end add by swho
	public static final String LINER_SCHEME = "JLR";
	public static final String NON_LINER_SCHEME = "JNL";
	public static final String BARTER_TRADER_SCHEME = "JBT";
	// Added by Ding Xijia(harbortek) 24-Jan-2011 : START
	public static String LCT_SCHEME = "JCL";
	// Added by Ding Xijia(harbortek) 24-Jan-2011 : END
	public static final String NOT_APPLICABLE_SCHEME = "000";

	public static final String FREE_BERTH_AMEND_NBR_CT = "FBACT";
	public static final String FREE_BERTH_AMEND_NBR_GB = "FBAGB";

	public static final String OVERSTAY_DOCK_ALLOW_CT = "OSDCT";
	public static final String OVERSTAY_DOCK_ALLOW_GNRL = "OSDGL";
	public static final String OVERSTAY_DOCK_ALLOW_BULK = "OSDBL";
	// end add by swho

	// <added scheme for wooden craft, 12.may.08 - cfg>
	public static final String WOODEN_CRAFT_SCHEME = "JWP";
	// <added scheme for wooden craft, 12.may.08 - cfg/>

	// added by swho on 30/09/2002 for determining number of chargeable berthing
	// charges
	public static final String BERTH_CHARGE_COUNT_CT = "BTHCT";
	public static final String BERTH_CHARGE_COUNT_GB = "BTHGB";
	// end add by swho

	public static final String STORE_RENT_IM_START_VAR = "SRIMS";
	public static final String STORE_RENT_EX_EXIT_VAR = "SREXE";
	public static final String STORE_RENT_TS_START_VAR = "SRTSS";
	public static final String STORE_RENT_TS_EXIT_VAR = "SRTSE";

	// START: FPT implement CR-CAB-20080805-37 Uturn 26-Sep-2009
	// Added by CungTD for billing for enhancement to Storage - Exit (Export ->
	// Shut)
	public static final String STORE_RENT_EX_SHUT_VAR = "SRESE";
	// END: FPT implement CR-CAB-20080805-37 Uturn 26-Sep-2009

	// added by swho on 15/04/2003 for psa link store rent
	public static final String STORE_RENT_ITH_PSA_JP_START_VAR = "SRPJS";
	public static final String STORE_RENT_ITH_PSA_JP_EXIT_VAR = "SRPJE";
	public static final String STORE_RENT_ITH_JP_PSA_START_VAR = "SRJPS";
	// end add by swho

	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : START
	public static final String TYPE_MVMT_ITH = "ITH";
	// Added by VietNguyen for CR Empty Scheme 19-SEP-2009 : END

	public static final String STORE_RENT_PARAM_COD = "COD";
	public static final String STORE_RENT_PARAM_DDT = "DDT";
	public static final String STORE_RENT_PARAM_ATB = "ATB";
	public static final String STORE_RENT_PARAM_LOAD = "LOAD";
	// added by swho on 15/04/2003 for psa link store rent
	public static final String STORE_RENT_PARAM_NOM_COD = "NCOD";
	public static final String STORE_RENT_PARAM_GATEIN = "ARR";
	// end add by swho

	public static final String CAT_CD_HW = "HW";
	public static final String CAT_CD_OW = "OW";
	public static final String CAT_CD_OH = "OH";
	public static final String CAT_CD_OG = "OG";
	public static final String CAT_CD_UC = "UC";
	public static final String CAT_CD_FLATRACK = "FR";
	public static final String CAT_CD_PLATFORM = "PF";

	public static final String DTTM_TYPE_CD_GATE_IN = "GI";
	public static final String DTTM_TYPE_CD_GATE_OUT = "GO";
	public static final String DTTM_TYPE_CD_COD_1 = "CD";
	public static final String DTTM_TYPE_CD_ATD = "AD";
	public static final String DTTM_TYPE_CD_ATB_2 = "AB";
	public static final String DTTM_TYPE_CD_ATL = "AL";
	// added by Alicia on 13/01/2004
	public static final String DTTM_TYPE_CD_CT_LOLO = "CL";
	// end add
	// Added by SONLT 02/12/2009
	public static final String DTTM_TYPE_CD_UNSTUFF = "US";
	// End add by SONLT
	// added by swho on 15/04/2003 for psa link store rent
	public static final String DTTM_TYPE_CD_NOM_COD = "NC";
	// end add by swho
	// START: added by VietNV 10-Nov-2009
	// for CR - Conventional Container Tracking System
	public static final String DTTM_TYPE_CD_DN = "DN";
	public static final String DTTM_TYPE_CD_UA = "UA";

	public static final String PREFERENCE_PUBLIC = "P";
	public static final String PREFERENCE_CUSTOMIZED = "C";
	// END: add by VietNV

	public static final String LOLO_PARTY_IND_HAUL = "H";
	public static final String LOLO_PARTY_IND_SLOT = "S";
	public static final String LOLO_PARTY_IND_CNTR = "C";

	public static final String VSL_CALL_TXN_BA_CREATE = "BACR";
	public static final String VSL_CALL_TXN_BA_AMEND = "BAAM";
	public static final String VSL_CALL_TXN_BA_CANCEL = "BACX";

	// Added on 07/05/2002 by Ai Lin - For Late Arrival
	public static final String LATE_ARRIVAL_CHARGE_VAR = "LARRV";
	// End added on 07/05/2002 by Ai Lin - For Late Arrival

	// added by swho on 12/06/2002 for GBMS General Cargo processing
	public static String REF_IND_DN = "DN";
	public static String REF_IND_UA = "UA";
	public static final String REF_IND_BL = "BL";
	public static final String REF_IND_BR = "BR";
	// add Minimum Charge for LCT scheme, 28.feb.11 by hpeng
	public static final String REF_IND_MC = "MC";
	// end add
	// Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing
	public static final String REF_IND_ESN = "ES";
	public static String REF_IND_EDO = "ED";
	// End Added on 05/07/2002 by Irene Tan - For GBMS General Cargo processing
	// added by Irene Tan on 3 Jun 2003 : for stuffing/unstuffing
	public static final String REF_IND_STUFF = "SU";
	// end added by Irene Tan on 3 Jun 2003

	// add feb 2012 mongkey for lwms
	public static final String REF_IND_LWMS_DSA = "DS";
	public static final String REF_IND_LWMS_DOCKAGE = "DK";
	public static final String REF_IND_LWMS_SPACE = "SP";
	public static final String REF_IND_LWMS_MOORING = "MO";
	public static final String REF_IND_LWMS_UTILITY = "UT";
	public static final String REF_IND_LWMS_TENANCY = "TN";
	public static final String REF_IND_LWMS = "LT";

	public static final String REF_IND_LWMS_DSA_WHARHAGE = "DW";
	public static final String REF_IND_LWMS_DSA_CRANAGE = "DC";
	public static final String REF_IND_LWMS_DSA_CRANAGEOPERATOR = "DO";
	// end added feb 2012 mongkey for lwms

	// Added on 05/07/2002 by Irene Tan - For Cash Sales / GBMS General Cargo
	// processing
	public static final String MIN_BILL_AMT = "MINAM";
	public static String DISC_VV_IND = "D";
	public static String LOAD_VV_IND = "L";
	public static final String CASH_CUST_CD = "CASH";
	public static final String CASH_CUST_PARA_CD = "CCODE";
	public static final String CASH_CUST_ACCT_NBR = "CACCT";
	public static String CASH_RECEIPT_PREFIX = "GR";
	// End Added on 05/07/2002 by Irene Tan - For Cash Sales / GBMS General Cargo
	// processing

	// Added for NETS Receipt Prefix by Chua 29/04/2008
	public static final String NETS_ATM_RECEIPT_PREFIX = "NJR";
	public static final String NETS_CSH_RECEIPT_PREFIX = "CJR";

	// Added on 19/08/2002 by Kumaran - for Bulk Cargo Rates- Service Charge
	public static final String TARIFF_SUB_SHORE_INSTALLATION = "CI";
	public static final String TARIFF_SUB_MAINLAND_MANUAL_HANDLING = "NM";
	public static final String TARIFF_SUB_PDL_PIPELINE = "PP";
	public static final String TARIFF_SUB_PDL_MANUAL_HANDLING = "NP";
	public static final String TARIFF_SUB_LIQUID_BULK = "BL";

	public static final String TARIFF_SUB_MAINLAND_UNLOADER_MECHANICAL_LOCAL = "ML";
	public static final String TARIFF_SUB_MAINLAND_UNLOADER_MECHANICAL_TS = "MT";
	public static final String TARIFF_MAIN_MAINLAND_UNLOADER_MECHANICAL = "HC";

	// public static final String TARIFF_SUB_PDL_MECHANICAL = "MC";
	public static final String TARIFF_MAIN_PDL_UNLOADER_MECHANICAL = "HC";
	public static final String TARIFF_SUB_PDL_UNLOADER_MECHANICAL = "MP";

	public static final String TARIFF_MAIN_CONVEYOR_SYSTEM_MAINLAND = "HC";
	public static final String TARIFF_SUB_CONVEYOR_SYSTEM_MAINLAND = "CM";

	public static final String TARIFF_MAIN_CONVEYOR_SYSTEM_PDL = "HC";
	public static final String TARIFF_SUB_CONVEYOR_SYSTEM_PDL = "CP";

	public static final String TARIFF_MAIN_PIPE_DECOMPRESSION = "HC";
	public static final String TARIFF_SUB_PIPE_DECOMPRESSION = "PM";

	public static final String TARIFF_MAIN_LICENSE_FEE = "OT";
	public static final String TARIFF_SUB_LICENSE_FEE = "LF";

	public static final String TARIFF_MAIN_HANDLING_LIFT_ON_OFF = "OT";
	public static final String TARIFF_SUB_HANDLING_LIFT_ON_OFF = "HV";

	// Added on 03/10/2002 by Kumaran - for Bulk Cargo Rates- Wharfage
	public static final String TARIFF_SUB_WFG_SHORE_INSTALLATION = "CI";
	public static final String TARIFF_SUB_WFG_MANUAL_HANDLING = "MN";
	public static final String TARIFF_SUB_WFG_PDL_PIPELINE = "PP";
	public static final String TARIFF_SUB_WFG_MAINLAND_PIPELINE = "PM";
	public static final String TARIFF_SUB_WFG_MAINLAND_MECHANICAL = "MM";
	public static final String TARIFF_SUB_WFG_PDL_MECHANICAL = "MP";

	// For Container Terminal Rates - ITH Trucking & ITH LoLo
	public static final String TARIFF_MAIN_ITH_TRUCKING_LOLO = "OF";
	public static final String TARIFF_SUB_ITH_TRUCKING = "IH";
	public static final String TARIFF_SUB_ITH_LOLO = "IL";

	// For Container Terminal Rates - Other Charges
	public static final String TARIFF_MAIN_OTHER_CHARGES_RENOM = "OF";
	public static final String TARIFF_MAIN_OTHER_CHARGES_SHUTOUT = "OV";
	public static final String TARIFF_MAIN_OTHER_CHARGES_REEFER_PLUG = "SV";
	public static final String TARIFF_MAIN_OTHER_CHARGES_REEFER_ELECTRICITY = "OF";
	public static final String TARIFF_MAIN_OTHER_CHARGES_CHANGE_OF_STATUS = "OF";
	public static final String TARIFF_MAIN_OTHER_CHARGES_DIRECT_LOADING = "OV";

	public static final String TARIFF_SUB_OTHER_CHARGES_RENOM = "RN";
	public static final String TARIFF_SUB_OTHER_CHARGES_SHUTOUT = "ST";
	public static final String TARIFF_SUB_OTHER_CHARGES_REEFER_PLUG = "CN";
	public static final String TARIFF_SUB_OTHER_CHARGES_REEFER_ELECTRICITY = "RE";
	public static final String TARIFF_SUB_OTHER_CHARGES_CHANGE_OF_STATUS = "CG";
	public static final String TARIFF_SUB_OTHER_CHARGES_DIRECT_LOADING = "DL";

	// Free Storage Period for General Cargo - Local & TS - Liner & Non-Liner
	public static final String TARIFF_MAIN_FREE_STORAGE_PERIOD_GENERAL_CARGO = "SR";
	public static final String TARIFF_SUB_FREE_STORAGE_PERIOD_GENERAL_CARGO = "GL";

	public static final String TARIFF_MAIN_FREE_STORAGE_PERIOD_BULK_CARGO = "SR";
	public static final String TARIFF_SUB_FREE_STORAGE_PERIOD_BULK_CARGO = "GL";

	public static final String TARIFF_MAIN_FREE_STORAGE_PERIOD_CONVENTIONAL_CONTAINER = "SR";
	public static final String TARIFF_SUB_FREE_STORAGE_PERIOD_CONVENTIONAL_CONTAINER = "GL";

	public static final String TARIFF_MAIN_FREE_STORAGE_PERIOD_CONTAINER_TERMINAL = "ST";
	public static final String TARIFF_SUB_FREE_STORAGE_PERIOD_CONTAINER_TERMINAL = "GL";

	/* VV Cargo Status (use in EC) added by Alicia 18 Nov 2003 */
	public static final String VV_CARGO_STATUS_TRANSSHIPMENT = "TS"; // Transshipment
	public static final String VV_CARGO_STATUS_OVERSIDE = "OS";// Overside
	public static final String VV_CARGO_STATUS_LANDING_RESHIPPING = "LR";// Landing and re-shipping
	public static final String VV_CARGO_STATUS_RE_EXPORT = "RE";// Re Export;
	public static final String VV_CARGO_STATUS_DIRECT_DELIVERY = "DD"; // Direct Delivery
	public static final String VV_CARGO_STATUS_LOCAL = "LC";// Local

	// added by Alicia for containerised cargo
	// movement
	public static String GB_CNTR_LOCAL_IMPORT = "LI";
	public static String GB_CNTR_LOCAL_EXPORT = "LE";
	public static String GB_CNTR_GB_GB = "GG";
	public static String GB_CNTR_CT_CT = "CC";
	public static String GB_CNTR_GB_CT = "GC";
	public static String GB_CNTR_CT_GB = "CG";
	public static String GB_CNTR_PSA_GB = "PG";
	public static String GB_CNTR_PSA_CT = "PC";
	public static String GB_CNTR_GB_PSA = "GP";
	public static String GB_CNTR_CT_PSA = "CP";
	// leg
	public static String GB_CNTR_LEG_INWARD = "IW";
	public static String GB_CNTR_LEG_OUTWARD = "OW";

	public static String GB_CNTR_MVMT_TYPE_GB = "G";
	public static String GB_CNTR_MVMT_TYPE_CT = "C";
	public static String GB_CNTR_MVMT_TYPE_GX = "X";
	public static String GB_CNTR_MVMT_TYPE_CY = "Y";
	// end add By Alicia

	// Added by valli 1-Dec-2005
	public static String TYPE_CD_DG_NON_STORABLE_GRACE_PERIOD = "DGNGP";

//  Lolo rebates & TT booking charges - changes Added by Valli 13-Feb-2006        
	public static final String LOLO_REBATES_LOCAL_IMPORT = "LRBLI";
	public static final String LOLO_REBATES_LOCAL_EXPORT = "LRBLE";
	public static final String LOLO_PARAM_COD = "COD";
	public static final String LOLO_PARAM_ATB = "ATB";
	public static final String LRBLI_NO = "N";
	public static final String LRBLE_NO = "N";
	public static final String TARIFF_SUB_LOLO_REBATES_PEAK = "RP";
	public static final String TARIFF_SUB_LOLO_REBATES_OFF_PEAK = "RO";
	public static final String TARIFF_SUB_LOLO_TT_NO_SHOW = "NS";
	public static final String TARIFF_SUB_LOLO_TT_SUPP_BOOKING = "SB";
	public static final String TARIFF_SUB_LOLO_TT_CANCELLATION_CHARGE = "TC";
	public static final String TARIFF_SUB_LOLO_TT_SHORTFALL_CHARGE = "SF";
	public static final String LOLO_REBATES_BILL_NBR_SUFFIX = "RB";
	public static final String LOLO_REBATES_TARIFF_TYPE = "LOREB";
	// Added for TT short fall
	public static final String TT_SHORTFALL_CNTR_SIZE = "20";
	public static final String TT_SHORTFALL_CNTR_STATUS = "F";
	public static final String TT_SHORTFALL_CNTR_CAT_CD = "GP";

	// START: Automation of GB Misc Charges - Added by Valli - 4-Apr-2007
	public static final String TARIFF_SUB_ADMIN_OVERNIGHT_PARK_AUTH = "38";

	// START 25-Feb-2011 - TPA ?ThangNC added to add sub cat for trailer parking
	// applications
	public static final String TARIFF_SUB_ADMIN_TPA_AUTH = "63";
	// END 25-Feb-2011 - TPA ?ThangNC added to add sub cat for trailer parking
	// applications

	// START 08-Oct-2013 - TPA ?HoaNT3 added for trailer parking applications
	// enhancement
	public static final String TARIFF_SUB_ADMIN_TPA_AUTH_NORMAL_CARGO = "65";
	public static final String TARIFF_SUB_ADMIN_TPA_AUTH_DG_CARGO = "67";
	public static final String TARIFF_SUB_ADMIN_TPA_AUTH_OOG_CARGO = "69";
	// END 08-Oct-2013 - TPA ?HoaNT3 added for trailer parking applications
	// enhancement

	public static final String TARIFF_SUB_ADMIN_SUPPLY_ELEC_20 = "36";
	public static final String TARIFF_SUB_ADMIN_SUPPLY_ELEC_40 = "37";
	public static final String TARIFF_SUB_ADMIN_CLOSE_SPACE = "09";
	public static final String TARIFF_SUB_ADMIN_OPEN_SPACE = "10";
	public static final String TARIFF_SUB_ADMIN_CLOSE_SPACE_GST = "41";
	public static final String TARIFF_SUB_ADMIN_OPEN_SPACE_GST = "42";
	public static final String TARIFF_SUB_ADMIN_FORKLIFT_BELOW_10 = "26";
	public static final String TARIFF_SUB_ADMIN_FORKLIFT_BELOW_10_GST = "27";
	public static final String TARIFF_SUB_ADMIN_FORKLIFT_ABOVE_10 = "28";
	public static final String TARIFF_SUB_ADMIN_FORKLIFT_ABOVE_10_GST = "29";
	public static final String TARIFF_SUB_ADMIN_CNTR_LIFTER = "30";
	public static final String TARIFF_SUB_ADMIN_CNTR_LIFTER_GST = "31";
	public static final String TARIFF_SUB_ADMIN_WHEEL_LOADER = "40";
	public static final String TARIFF_SUB_ADMIN_MOBILE_CRANE = "32";
	public static final String TARIFF_SUB_ADMIN_SPREADER = "23";
	public static final String TARIFF_SUB_ADMIN_SPREADER_GST = "48";
	public static final String TARIFF_SUB_ADMIN_STEEL_SPREADER = "52";
	public static final String TARIFF_SUB_ADMIN_STEEL_SPREADER_GST = "53";
	public static final String TARIFF_SUB_ADMIN_CONTRACTOR_PERMIT = "33";
	public static final String TARIFF_SUB_ADMIN_CONTRACTOR_PERMIT_GST = "34";
	public static final String TARIFF_SUB_ADMIN_HOTWORK_INSP = "17";
	public static final String TARIFF_SUB_ADMIN_HOTWORK_INSP_GST = "18";
	public static final String TARIFF_SUB_ADMIN_FIREMAN = "15";
	public static final String TARIFF_SUB_ADMIN_FIREMAN_GST = "16";

	public static final String REF_IND_MISC = "MI";
	public static final String[] MISC_APP_TYPES = { "ONV", "ELE", "LTB", "SPA", "ONE", "STE", "WSS", "CTP", "HOT" };
	public static final String MISC_APP_VEHICLE = "ONV";

	// START 25-Feb-2011 - TPA ?ThangNC added to process for trailer parking
	// applications
	public static final String MISC_APP_TPA = "TPA";
	// END 25-Feb-2011 - TPA ?ThangNC added to process for trailer parking
	// applications

	public static final String MISC_APP_REEFER = "ELE";
	public static final String MISC_APP_BARGE = "LTB";
	public static final String MISC_APP_SPACE = "SPA";
	public static final String MISC_APP_PARK_MAC = "ONE";
	public static final String MISC_APP_STATION_MAC = "STE";
	public static final String MISC_APP_SPREADER = "WSS";
	public static final String MISC_APP_CONTRACTOR = "CTP";
	public static final String MISC_APP_HOTWORK = "HOT";

	public static final HashMap SPACE_TYPE_DESC_MAP;
	public static final HashMap SPREADER_TYPE_DESC_MAP;
	public static final HashMap REEFER_TYPE_DESC_MAP;
	public static final HashMap PARKMAC_TYPE_DESC_MAP;

	static {
		SPACE_TYPE_DESC_MAP = new HashMap(2);
		SPACE_TYPE_DESC_MAP.put("C", "Covered");
		SPACE_TYPE_DESC_MAP.put("O", "Open");

		SPREADER_TYPE_DESC_MAP = new HashMap(2);
		SPREADER_TYPE_DESC_MAP.put("W", "Wooden");
		SPREADER_TYPE_DESC_MAP.put("S", "Steel");

		REEFER_TYPE_DESC_MAP = new HashMap(2);
		REEFER_TYPE_DESC_MAP.put("E", "Empty");
		// Amended on 17/07/2007 by Ai lin - Change 'Loaded' to 'Laden'
		// REEFER_TYPE_DESC_MAP.put("L", "Loaded");
		REEFER_TYPE_DESC_MAP.put("L", "Laden");
		// End amended on 17/07/2007 by Ai lin - Change 'Loaded' to 'Laden'

		PARKMAC_TYPE_DESC_MAP = new HashMap(2);
		PARKMAC_TYPE_DESC_MAP.put("F", "ForkLift");
		PARKMAC_TYPE_DESC_MAP.put("S", "ShoreCrane");
	}
	// END: Automation of GB Misc Charges - Added by Valli - 4-Apr-2007

	// begin: Automation of GB Warehouse Charges - CFG - 25-Jun-2007

	public static final String TARIFF_SUB_GB_STORAGE_TRANSIT_WAREHOUSE_GST = "T*";
	public static final String TARIFF_SUB_GB_STORAGE_BACKUP_WAREHOUSE_GST = "B*";
	public static final String TARIFF_SUB_GB_STORAGE_OPEN_SPACE_GST = "O*";
	public static final String TARIFF_SUB_GB_STORAGE_CNTR_YARD_GST = "C*";
	public static final String REF_IND_SW = "SW";

	// end: Automation of GB Warehouse Charges - CFG - 25-Jun-2007

	// Penjuru tariff code
	public static final String TARIFF_MAIN_DOCKAGE = "PM";
	public static final String TARIFF_SUB_DOCKAGE = "01";

	public static final String TARIFF_MAIN_ADMIN_PEN = "PA";
	// marina admin main cagegory
	public static final String TARIFF_MAIN_ADMIN_MARINA = "NA";
	public static final String TARIFF_SUB_ADMIN_UOWS = "01";
	public static final String TARIFF_SUB_ADMIN_UOWS_GST = "02";
	public static final String TARIFF_SUB_ADMIN_RENTAL_CRANE = "SR";

	public static final String TARIFF_SUB_MOORING_DAY_BERTH = "M1";
	public static final String TARIFF_SUB_MOORING_NIGHT_BERTH = "M2";
	public static final String TARIFF_SUB_MOORING_DAY_VESSEL = "M3";
	public static final String TARIFF_SUB_MOORING_NIGHT_VESSEL = "M4";

	public static final String TARIFF_SUB_MOORING_DAY_AND_NIGHT_BERTH = "M6";
	public static final String TARIFF_SUB_MOORING_DAY_AND_NIGHT_VESSEL = "M7";
	public static final String TARIFF_SUB_MOORING_CLERICAL_ADMIN_FEE = "CA";
	public static final String TARIFF_SUB_MOORING_CONTRACT_FEE = "CF";
	public static final String TARIFF_MAIN_RENTAL = "PT";
	public static final String TARIFF_SUB_RENTAL_CANTEEN = "CR";
	public static final String TARIFF_SUB_RENTAL_OFFICE_UNIT = "OU";
	public static final String TARIFF_SUB_RENTAL_OFFICE_AREA = "OA";

	public static final String TARIFF_MAIN_UTIL = "PU";
	public static final String TARIFF_SUB_UTIL_ELECTRICITY = "EL";
	public static final String TARIFF_SUB_UTIL_WATER_BOND = "WB";
	public static final String TARIFF_SUB_UTIL_WATER_FEE = "WF";
	public static final String TARIFF_SUB_UTIL_WATER_SURCHARGE = "WS";

	public static final String TARIFF_MAIN_WHARFAGE_PEN = "PW";
	public static final String TARIFF_SUB_WHARFAGE_GENERAL = "GL";

	public static final String PENJURU_TENANCY_BUSINESS = "T";
	public static final String PENJURU_DOCKAGE_BUSINESS = "D";
	public static final String PENJURU_ADMIN_BUSINESS = "A";
	public static final String PENJURU_WHARFAGE_BUSINESS = "W";
	public static final String PENJURU_UTILITY_BUSINESS = "U";
	public static final String PENJURU_NORMAL_BUSINESS = "O";

	public static final String PENJURU_TENANCY = "PT";
	public static final String PENJURU_GENERAL = "PG";
	public static final String PENJURU_UTILITY = "PU";

	// <cfg lashing/unlashing, 07.oct.08>
	public static final String DEFAULT_CNTR_SIZE = "20";

	// add cargo_category_code
	// ducta1 starts on 23/12/2008
	public interface CARGO_CATEGORY_CODE {
		public static final String PASSENGER_CAR = "01";
		public static final String STATION_WAGON_VAN = "02";
		public static final String BUSES_LORRIES = "03";
		public static final String EMPTY_MAFI = "MF";
		public static final String PDC_CARGO_GENERAL = "PG";
		public static final String PDC_CARGO_PASSENGER_CAR = "P1";
		public static final String PDC_CARGO_STATION_WAGON = "P2";
		public static final String PDC_CARGO_LORRY = "P3";
	}

	public static final String TARIFF_SUB_SERVICE_CHARGE_RORO_VSL = "RO";
	// ducta1 end

	// <cfg, 14.jan.2011 lashing/unlashing>
	public static final String MISC_CAT_CODE_UDECK = "UDECK_LASH";
	public static final String MISC_TYPE_CODE_UDECK_ACCT = "ACCT";
	public static final String MISC_TYPE_CODE_UDECK_DECK_TIER_NBR = "DECK_TIER_NB";
	public static final String MISC_TYPE_CODE_UDECK_EXP_YMD = "EXP_YMD";
	// <cfg, 14.jan.2011 lashing/unlashing/>

	// add new scheme for LCT, 28.feb.11 by hpeng
	public static final String TARIFF_SUB_MINIMUM_CHARGE_LCT_SCHEME = "MC";

	// <cfg, 12.may.2011 lashing-unlashing II/>
	public static final String MISC_TYPE_CODE_UDECK_INCL_SO = "INCL_SO";

	// add by hujun 3/6/2011, handle the GB Shutout Cargo billing
	public static final String TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO = "SO";
	// add end

	// CR-CAB-20110713-011 transaction based N4 billing
	public static final String TARIFF_MAIN_JU = "JU";

	// <cfg, 13.oct.2011 CR-CAB-20100506-005 var reefer/>
	public static final int TARIFFTIER_BP_NUM_PER_PAGE = 12;

	// BEGIN add for LWMS, ThangPV, 22-05-2013
	public static final String PENJURU_MARINE = "PC";
	public static final String PENJURU_MARINE_LEFT = "PD";
	public static final String PENJURU_CRANE_OPR = "PO";

	public static final String MARINA_MARINE = "NC";
	public static final String MARINA_MARINE_LEFT = "ND";
	public static final String MARINA_CRANE_OPR = "NO";
	public static final String MARINA_CRANE_OPR_LEFT = "NP";
	public static final String MARINA_RENTAL = "NT";
	public static final String MARINA_UTILITIES = "NU";
	public static final String MARINA_WHARFAGE = "NW";
	// END add for LWMS, ThangPV, 22-05-2013

	// BEGIN add for LWMS, ThangPV, 22-05-2013
	public static final String[] PENJURU_MARINA_CRANE = new String[] { PENJURU_MARINE, PENJURU_MARINE_LEFT,
			PENJURU_CRANE_OPR, MARINA_MARINE, MARINA_MARINE_LEFT, MARINA_CRANE_OPR };

	public static final String[] PENJURU_MARINA_TARIFF_MAIN_CATEGORIES = new String[] { PENJURU_MARINE,
			PENJURU_MARINE_LEFT, PENJURU_CRANE_OPR, MARINA_MARINE, MARINA_MARINE_LEFT, MARINA_CRANE_OPR,
			MARINA_CRANE_OPR_LEFT, MARINA_RENTAL, MARINA_UTILITIES, MARINA_WHARFAGE };
	public static final String TARIFF_MAINCD_SPECIAL_CARGO_SUFFIX = "S";
	public static final String TARIFF_SUB_SPECIAL_CARGO_ADDTIONAL_SERVICE = "QQ";
	// END add for LWMS, ThangPV, 22-05-2013

	/* Added for Water Order Activity Start */
	public static final String TARIFF_MAIN_WATER_ORDER = "WO";
	public static final String TARIFF_MAIN_WATER_ORDER_CHARGE = "AD";
	public static final String TARIFF_SUB_WATER_ORDER_SUPPLY = "01";
	public static final String TARIFF_SUB_WATER_ORDER_WATERBORNE_FEE = "WF";
	public static final String TARIFF_SUB_WATER_ORDER_GOVT_TAX = "06";
	public static final String TARIFF_SUB_WATER_ORDER_LATE_FEE = "08";
	public static final String WATER_ORDER_SCHEME_CODE = "000";
	public static final String WATER_ORDER_BUSINESS_TYPE = "G";
	/* Added for Water Order Activity End */
	/* Added for Vtsb Start */
	public static final String TARIFF_MAIN_VTSB = "VB";
	public static final String TARIFF_SUB_VTSB_NORMAL_OC = "NG";
	public static final String TARIFF_SUB_VTSB_NORMAL_VA = "NB";
	public static final String TARIFF_SUB_VTSB_NORMAL_EA = "NE";
	public static final String TARIFF_SUB_VTSB_NORMAL_LA = "NL";
	public static final String TARIFF_SUB_VTSB_NORMAL_NS = "NN";
	public static final String TARIFF_SUB_VTSB_NORMAL_LC = "NC";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_OC = "SG";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_VA = "SB";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_EA = "SE";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_LA = "SL";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_NS = "SN";
	public static final String TARIFF_SUB_VTSB_SUPPLEMENTARY_LC = "SC";
	public static final String TARIFF_SUB_VSTB_BLOCK_OC = "BG";
	public static final String TARIFF_SUB_VSTB_BLOCK_VA = "BB";
	public static final String TARIFF_SUB_VSTB_BLOCK_EA = "BE";
	public static final String TARIFF_SUB_VSTB_BLOCK_LA = "BL";
	public static final String TARIFF_SUB_VSTB_BLOCK_NS = "BN";
	public static final String TARIFF_SUB_VSTB_BLOCK_LC = "BC";
	public static final String TARIFF_SUB_VSTB_BLOCK_SF = "BS";
	public static final String VTSB_SCHEME_CODE = "000";
	public static final String VTSB_BUSINESS_TYPE = "G";
	/* Added for Vtsb End */

	/* Added for Bulk Cargo S2 Start */
	public static final String TARIFF_MAIN_BULK_CARGO_S2 = "S2";
	public static final String TARIFF_MAIN_BULK_CARGO_S2_CHARGE = "OT";
	public static final String TARIFF_SUB_BULK_CARGO_S2_SUPPLY = "S2";
	public static final String BULK_CARGO_S2_SCHEME_CODE = "000";
	public static final String BULK_CARGO_S2_BUSINESS_TYPE = "B";
	public static final String BULK_CARGO_S2_NC_CAT_CD = "S2_BILL";
	public static final String BULK_CARGO_S2_NC_TYPE_CD = "S2_NCHRG_DUR";
	public static final String BULK_CARGO_S2_STBY_ST = "55";
	public static final String BULK_CARGO_S2_STBY_END = "76";
	public static final String BULK_CARGO_CEMENT_S2_BUSINESS_TYPE = "E"; // EVM Enhancements
	/* Added for Bulk Cargo S2 End */

	/* Added for Bulk Cargo S3 Mainland Start */
	public static final String TARIFF_MAIN_BULK_CARGO_S3M = "3M";
	public static final String TARIFF_MAIN_BULK_CARGO_S3M_CHARGE = "OT";
	public static final String TARIFF_SUB_BULK_CARGO_S3M_SUPPLY = "S3";
	public static final String BULK_CARGO_S3M_SCHEME_CODE = "000";
	public static final String BULK_CARGO_S3M_BUSINESS_TYPE = "B";
	public static final String BULK_CARGO_CEMENT_S3M_BUSINESS_TYPE = "E"; // EVM Enhancements
	public static final String BULK_CARGO_S3M_CRANE_ID = "'M1', 'M2', 'L1', 'L2'";
	/* Added for Bulk Cargo S3 Mainland End */

	/* Added for Bulk Cargo S3 PSS Start */
	public static final String TARIFF_MAIN_BULK_CARGO_S3P = "3P";
	public static final String TARIFF_MAIN_BULK_CARGO_S3P_CHARGE = "OT";
	public static final String TARIFF_SUB_BULK_CARGO_S3P_SUPPLY = "S3";
	public static final String BULK_CARGO_S3P_SCHEME_CODE = "000";
	public static final String BULK_CARGO_S3P_BUSINESS_TYPE = "B";
	public static final String BULK_CARGO_S3P_CRANE_ID = "'P1', 'P2', 'P3'";
	public static final String BULK_CARGO_CEMENT_S3P_BUSINESS_TYPE = "E";
	public static final String BULK_CARGO_S3P_SHIFT_IND = "1";
	// public static final String BULK_CARGO_S3P_CAT_CD = "S3P_BILL";
	// public static final String BULK_CARGO_S3P_STDT_TYPE_CD = "S3P_START_DT";
	// public static final String BULK_CARGO_S3P_ENDT_TYPE_CD = "S3P_END_DT";
	/* Added for Bulk Cargo S3 PSS End */

	// Added by MC Consulting
	public static final String TARIFF_SUB_SS_ADMIN_CHARGE = "AS";

	/* Added by MC Consulting for LWMS KCP */
	public static final String TARIFF_MAIN_LWMS_KCP = "KC";

	// Cally 18 April 2017 LT Productivity based Incentive Scheme
	// public static final String TARIFF_SUB_LWMS_KCP_MONTHLY_HANDLING = "CH";
	public static final String TARIFF_SUB_LWMS_KCP_CARGO_HANDLING = "CL";

	public static final String TARIFF_SUB_LWMS_KCP_ADDITIONAL_LIFTS = "AL";

	public static final String TARIFF_SUB_LWMS_KCP_HAND_CARRY_PT = "HC";

	public static final String TARIFF_SUB_LWMS_KCP_HAND_CARRY_MSW = "HC";
	/* End of addition by MC Consulting for LWMS KCP */

	// MCC for admin fee waiver
	public static final String MF_MDEL = "MDEL";// Manifest delete
	public static final String MF_MADD = "MADD";// Manifest add
	public static final String MF_MADM = "MADM";// Manifest amend
	public static final String SS_SSAD = "SSAD";// ship store admin charge
	public static final String TARIFF_MAIN_BILL_SSADMIN = "SA"; // for ship store bill template

	// MCC for Periodic Store Rent
	public static final String TARIFF_MAIN_PERIODIC_STORERENT = "SR";
	public static final String GB_CNTR = "GBCNTR";
	public static final String MOT_CNTR = "MOTCNTR";
	public static final String GB_CNTR_IM_TXN = "GBXT";
	public static final String GB_CNTR_EX_TXN = "LDSC";
	public static final String MOT_CNTR_TXN = "STXT";

	// NOM Billing start
	public static final String TARIFF_MAIN_NOM = "MN"; // for bill printing & exception reporrt. cannot be NM as N is
														// used by Lighter terminal billing
	public static final String TARIFF_SUB_NOM_GL = "GL"; // for bill exception report
	public static final String TARIFF_MAIN_NOM_STEV = "SD";
	public static final String TARIFF_MAIN_NOM_RAIL = "RL";
	public static final String TARIFF_SUB_NOM_STEV_DISC_ROLL = "D1";
	public static final String TARIFF_SUB_NOM_STEV_DISC_BEAM = "D2";
	public static final String TARIFF_SUB_NOM_STEV_DISC_PIPE = "D3";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_ROLL = "L1";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_BEAM = "L2";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_PIPE = "L3";
	public static final String TARIFF_SUB_NOM_STEV_TS_ROLL = "T1";
	public static final String TARIFF_SUB_NOM_STEV_TS_BEAM = "T2";
	public static final String TARIFF_SUB_NOM_STEV_TS_PIPE = "T3";
	public static final String TARIFF_SUB_NOM_STEV_DISC_GENERAL_CARGO = "DG";
	public static final String TARIFF_SUB_NOM_STEV_DISC_CNTR = "DC";
	public static final String TARIFF_SUB_NOM_STEV_DISC_VEHICLE = "DV";
	public static final String TARIFF_SUB_NOM_STEV_DISC_SMALL_TRUCK = "DS";
	public static final String TARIFF_SUB_NOM_STEV_DISC_BIG_TRUCK = "DB";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_GENERAL_CARGO = "LG";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_CNTR = "LC";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_VEHICLE = "LV";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_SMALL_TRUCK = "LS";
	public static final String TARIFF_SUB_NOM_STEV_LOAD_BIG_TRUCK = "LB";
	public static final String TARIFF_SUB_NOM_STEV_TS_GENERAL_CARGO = "TG";
	public static final String TARIFF_SUB_NOM_STEV_TS_CNTR = "TC";
	public static final String TARIFF_SUB_NOM_STEV_TS_VEHICLE = "TV";
	public static final String TARIFF_SUB_NOM_STEV_TS_SMALL_TRUCK = "TS";
	public static final String TARIFF_SUB_NOM_STEV_TS_BIG_TRUCK = "TB";
	public static final String TARIFF_SUB_NOM_RAIL_TS = "RT";
	public static final String TARIFF_SUB_NOM_STEV_MIN = "MC";
	public static final String TARIFF_SUB_NOM_RAIL_LOAD = "RL";
	public static final String TARIFF_SUB_NOM_RAIL_DISC = "RD";
	// NOM Billing end

	// Start new business types New EVM Enhancements 09/02/2018 start Sripriya
	public static final String STEEL_BUSINESS = "S";
	public static final String CEMENT_BUSINESS = "E";
	public static final String J1BASIN_BUSINESS = "O";
	// End new business types New EVM Enhancements 09/02/2018 start Sripriya

	public static final String TARIFF_MAINCD_WHARFAGE_SUFFIX = "W";
	public static final String TARIFF_MAINCD_ADMIN_SUFFIX = "A";
	public static final String TARIFF_MAINCD_RENTAL_SUFFIX = "T";
	public static final String TARIFF_MAINCD_UTIL_SUFFIX = "U";
	public static final String TARIFF_MAINCD_DOCKAGE_SUFFIX = "M";
	public static final String TARIFF_MAINCD_CRANE_SUFFIX = "C";
	public static final String TARIFF_MAINCD_CRANEOPERATOR_SUFFIX = "O";

	public static final String TARIFF_MAINCD_CRANEWHARF_SUFFIX = "D";
	public static final String TARIFF_MAINCD_CRANEOPERATORWHARF_SUFFIX = "P";

	public static final String BILLREF_CRANAGE_LIFT_NORMAL = "N";
	public static final String BILLREF_CRANAGE_LIFT_LEFTONWHARF = "W";

	public static final String TARIFF_MAINCD_GENERAL_SUFFIX = "G";

	public static final String BILL_PRINT_LWMS_SUFFIX = "LT_";
}

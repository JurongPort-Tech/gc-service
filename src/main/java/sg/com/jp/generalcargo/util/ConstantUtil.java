package sg.com.jp.generalcargo.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConstantUtil {
	public static String MISC_EVENT_LOG = "MISC_EVENT_LOG";
	public static String vessel_name = "VESSEL_NAME";
	public static String inward_voyage_no = "INWARD_VOYAGE_NO";
	public static String outward_voyage_no = "OUT_VOY_NBR";
	
	public static String action = "ACTION";
	public static String bills_of_landing_no = "BILLS-OF-LANDING NO.";
	public static String hs_code = "HS_CODE";
	public static String cargo_description = "CARGO_DESCRIPTION";
	public static String cargo_selection = "CARGO_SELECTION";
	public static String cargo_marking = "CARGO_MARKING";
	public static String number_of_packages = "NUMBER_OF_PACKAGES";
	public static String gross_weight = "GROSS_WEIGHT";
	public static String gross_measurement = "GROSS_MEASUREMENT";
	public static String cargo_status = "CARGO_STATUS";
	public static String dg_indicator = "DG_INDICATOR";
	public static String storage_indicator = "STORAGE_INDICATOR";
	public static String packing_type = "PACKING_TYPE";
	public static String discharge_operation_indicator = "DISCHARGE_OPERATION INDICATOR";
	public static String consignee = "CONSIGNEE";
	public static String consignee_others = "CONSIGNEE_OTHERS";
	public static String port_of_loading = "PORT_OF_LOADING";
	public static String port_of_discharge = "PORT_OF_DISCHARGE";
	public static String port_of_final_destination = "PORT_OF_FINAL_DESTINATION";
	public static String ErrorMsg_EdoManifest = "Error in inserting the Edo Manifest";
	public static String ErrorMsg_EdoManifestJson = "Error in getting json for Edo Manifest";	
	
	// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
	public static String old_hscode = "OLD_HS_CODE";
	public static String custom_hscode = "CUSTOM_HS_CODE";
	public static String consignee_address = "CONSIGNEE_ADDRESS";
	public static String shipper_name = "SHIPPER_NAME";
	public static String shipper_address = "SHIPPER_ADDRESS";
	public static String split_bl_ind = "SPLIT_BL_IND";
	public static String split_main_bl = "SPLIT_MAIN_BL";
	public static String ErrorMsg_Duplicate_BlNo = "Error. Duplicate Bl No.";
	// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
	// START SPLIT BL - NS DEC 2024
	public static String notify_party = "NOTIFY_PARTY";
	public static String notify_party_address = "NOTIFY_PARTY_ADDRESS";
	// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
	public static String cargo_type = "CARGO_TYPE";
	public static final String JMSMsgType_CARGODISCLOADINFO_UPDATED = "CargoDiscLoadInfo_Updated";
	public static String hatch_weight = "HATCH_WEIGHT";
	public static String hatch_volume = "HATCH_VOLUME";
	public static String hatch_package = "HATCH_PACKAGE";
	public static String ErrorMsg_invalidLength = "Please remove some special characters due to length exceed for field ~ ";
	
	//-- bkref
	public static String template_version_bkref = "Cargo Booking Ref Template Version";
	public static String template_version_no_bkref = "1";
	public static String shipperOthers_index = "D";
	public static String bk_ref_nbr ="BK_REF_NBR";
	public static String var_nbr ="VAR_NBR";
	public static String vessel_nm ="VESSEL_NM";
	public static String out_voy_nbr ="OUT_VOY_NBR";
	public static String bk_nbr_pkgs ="BK_NBR_PKGS";
	public static String bk_wt ="BK_WT";
	public static String bk_vol ="BK_VOL";
	public static String crg_des ="CRG_DES";
	public static String variance_pkgs ="VARIANCE_PKGS";
	public static String variance_vol ="VARIANCE_VOL";
	public static String variance_wt ="VARIANCE_WT";
	public static String post_dis ="PORT_DIS";
	public static String declarant ="DECLARANT";
	public static String bk_create_cd ="BK_CREATE_CD";
	public static String shipper_cd ="SHIPPER_CD";
	public static String shipper_addr ="SHIPPER_ADDR";
	public static String shipper_nm ="SHIPPER_NM";
	public static String shipper_nm_others="SHIPPER_NM_OTHERS";
	public static String consignee_nm ="CONSIGNEE_NM";
	public static String consignee_addr ="CONSIGNEE_ADDR";
	public static String notify_party_nm ="NOTIFY_PARTY_NM";
	public static String notify_party_addr ="NOTIFY_PARTY_ADDR";
	public static String place_of_delivery ="PLACE_OF_DELIVERY";
	public static String place_of_receipt ="PLACE_OF_RECEIPT";
	public static String bl_number ="BL_NUMBER";
	public static String action_custom_info ="Update Customs Info";
	public static String DefaultCargoCategory ="00";
	public static String blNumber_tooltip ="Separate multiple BL No. by comma(,) - Exp: AA,BB,CC";
	public static String blNumbersplitbill_tooltip ="For splitted  BL records, the BL number you declared will be replaced with another BL number at the time of Excel processing.";
	public static String ErrorMsg_BkNoDuplicate="Booking Reference No. already Exists";
	public static String ErrorMsg_BkNoNoLength = "Booking Reference No. should not exceed 16 characters";
	public static String ErrorMsg_BkDetailsProcess = "Error in processing Booking Reference details";
	public static String ErrorMsg_BkNbr_NotExist = "Bk Ref No. does not exists";
	public static String ErrorMsg_Mandatory_BkRef = "Please fill in the Booking Reference";
	public static String ErrorMsg_Mandatory_Variance_pkg = "Please fill in the Variance Package";
	public static String ErrorMsg_Mandatory_Variance_wt = "Please fill in the Variance Weight";
	public static String ErrorMsg_Mandatory_Variance_vol = "Please fill in the Variance Volume";
	public static String ErrorMsg_Mandatory_Declarant = "Please fill in the ESN Declarant TDB CR.No./UEN No.";
	public static String ErrorMsg_Mandatory_Shipper_nm = "Please fill in the Shipper Name";
	public static String ErrorMsg_Mandatory_Consignee_nm = "Please fill in the Consignee Name";
	public static String ErrorMsg_Mandatory_Shipper_Address = "Please fill in the Shipper Address";
	public static String ErrorMsg_Mandatory_Port= "Please fill in the port";
	public static String ErrorMsg_Mandatory_HSCodeCustom= "Please fill in the HS Code";
	public static String ErrorMsg_Mandatory_IMO= "DG Indicator is selected as Yes. Please fill in the IMO Class";
	public static String ErrorMsg_Mandatory_UNDG_Nbr= "DG Indicator is selected as Yes. Please fill in the UNDG Number";
	public static String ErrorMsg_Mandatory_Flashpoint= "DG Indicator is selected as Yes. Please fill in the Flashpoint";
	public static String ErrorMsg_Mandatory_PackingGroup = "DG Indicator is selected as Yes. Please fill in the Packing Group";
	public static String ErrorMsg_Enter_Valid_Number_Zero = "Please enter a number greater than Zero!";
	public static String ErrorMsg_Existing_BkNbr = "The Bk number already exsist enter new one";
	public static String ErrorMsg_VesselUnberth_Add = "Vessel has unberth or closed. Cannot Add Booking Reference.";
	public static String ErrorMsg_Valid_Declarant = "Enter a Valid ESN Declarant Code";
	public static String ErrorMsg_Missing_BLNbr = "Please fill in the BL Number";
	public static String ErrorMsg_VesselUnberth = "Vessel has unberth. Cannot update details.";
	public static String ErrorMsg_cannot_delete = "Bk Ref details cannot be deleted.";
	public static String ErrorMsg_500Length = " should not exceed 500 characters";
	public static String ErrorMsg_200Length = " should not exceed 200 characters";
	public static String ErrorMsg_70Length = " should not exceed 70 characters";
	public static String ErrorMsg_SpecialChar = "Invalid characters. Do not insert special characters";
	public static String ErrorMsg_8Length = " should not exceed 8 characters";
	public static String ErrorMsg_BLNumberLength = "Bill-of-Lading No. should not exceed 100 characters";
	public static String ErrorMsg_BLNumberLength_Custom = "Bill-of-Lading No. should not exceed 100 characters";
	public static String ErrorMsg_ShipperOthers40Length = "Shipper Name(Others) should not exceed 40 characters";
	public static String ErrorMsg_ShipperOthers70Length = "Shipper Name(Others) should not exceed 70 characters";
	public static String ErrorMsg_NoOfPkgLength = "Packages should not exceed 6 characters";
	public static String ErrorMsg_VarianceLength = "Variance should not exceed 5 characters";
	public static String ErrorMsg_podLength = "Port of Discharge should not exceed 5 characters";
	public static String ErrorMsg_declarantLength = "ESN Declarant TDB CR.No./UEN No. should not exceed 12 characters";
	public static String ErrorMsg_CustomLength = "Customs HS Code should not exceed 10 characters";
	public static String ErrorMsg_Mandatory_Main_Bill = "Please fill in the Main Bills-of-Landing No.";
	public static String ErrorMsg_Mandatory_Main_Bill_Split_Only = "Please fill in the Main Bills-of-Landing No. only for Split BL";
	public static String bkref_filename = "BookingReference_Upload@";
	//-----
	
	public static String alertCode = "MAG";
	public static String alertCodeMAB = "MAB";

	public static final String errMsg_GBMSTriggerInd_err01 = "Unable to update the trigger indicators for ~ : ~ ";
	public static final String errMsg_GBCharge_err01 = " addGBCharge(): Unable to insert into gb_bill_charge table!";
	public static final String Reference_number_empty_or_invalid = "Reference number with reference to ~ref_ind = ~ is empty or invalid";
	public static final String errMsg_GBEventLog_err02 = "logChargeEvent(): Unable to insert into gb_charge_event_log table!";
	public static final String errMsg_GBEventLog_err03 = "Ref Ind can only be DN or UA or SU!";
	public static final String errMsg_GBEventLog_err01 = "logVesselTxnEvent(): Unable to insert into gb_vessel_txn_event_log table!";
	public static final String errMsg_DnBean_err01 = "Record Cannot be added to Database";

	public static String ErrorMsg_Mandatory_BLNO = "Enter BL No.";
	// <--JPOL //JPOM:"Please fill in the Bills-of-Lading No.";
	public static String ErrorMsg_Mandatory_CargoType = "Select a Cargo Type";
	// <--JPOL //JPOM:"Please select Cargo Type.";
	public static String ErrorMsg_Mandatory_CargoDesc = "Enter Cargo Desc";
	// <--JPOL //JPOM:"Please fill in the Cargo Description.";
	public static String ErrorMsg_Length_CargoDesc = "Maximum character for Cargo Description is 200";
	public static String ErrorMsg_Length_CargoDesc_Custom = "Maximum character for Cargo Description is 3000";
	// <--JPOL //JPOM:"Maximum character for Cargo Description is 200";
	public static String ErrorMsg_Mandatory_HSCode = "Enter HS Sub Code";// <--JPOL //JPOM:"Please select HS Code.";
	public static String ErrorMsg_Mandatory_CargoSelection = "Please select Cargo Selection.";
	public static String ErrorMsg_Invalid_CargoSelection = "Invalid Cargo Selection.";
	public static String ErrorMsg_Mandatory_CargoMarking = "Enter Markings";
	// <--JPOL //JPOM:"Please fill in the Cargo Marking.";
	public static String ErrorMsg_Length_CargoMarking = "Maximum character for Markings is 200";
	// <--JPOL //JPOM:"Cargo Markings length cannot be greater than 200 ."; //JPOM:
	public static String ErrorMsg_Mandatory_NoOfPkg = "Enter No Of Pkgs";
	// <--JPOL //JPOM:"Please fill in the Number of packages.";
	public static String ErrorMsg_Valid_noOfPkg = "Please enter a number greater than Zero!";
	// START FTZ CR - NS JULY 2024
	public static String ErrorMsg_Exceed_noOfPkg = "Number of packages exceeded/fall short. Total Packages should be equal to ";
	public static String ErrorMsg_Exceed_noOfPkg_Delete = "Number of packages updated in other details exceeded/fall short. Total Packages should be equal to ";
	public static String ErrorMsg_HSCode_Compulsary_noOfPkg = "Sub HS Details package number should be updated too";
	public static String ErrorMsg_MainHSCode_Compulsary_noOfPkg = "Main HS Details package number should be updated too";
	public static String ErrorMsg_Exceed_Weight = "Total Gross Weight exceeded/fall short. Total Gross Weight should be equal to ";
	public static String ErrorMsg_Declare_First = "Please insert or update main details first before adding new sub hs details.";
	public static String ErrorMsg_Exceed_Weight_Delete = "Weight updated in other details exceeded/fall short. Total Weight should be equal to ";
	public static String ErrorMsg_HSCode_Compulsary_Weight = "Sub HS Details weight should be updated too";
	public static String ErrorMsg_MainHSCode_Compulsary_Weight = "Main HS Details weight should be updated too";
	public static String ErrorMsg_Exceed_Vol = "Total Gross Volume exceeded/fall short. Total Gross Volume should be equal to ";
	public static String ErrorMsg_Exceed_Volume_Delete = "Volume updated in other details exceeded/fall short. Total Volume should be equal to ";
	public static String ErrorMsg_HSCode_Compulsary_Volume = "Sub HS Details volume should be updated too";
	public static String ErrorMsg_MainHSCode_Compulsary_Volume = "Main HS Details volume should be updated too";
	public static String ErrorMsg_OldHSCode_Compulsary = "Please fill in Old HS Code";
	public static String ErrorMsg_OldHSCode_Declared = "HS Code has never been declared";
	public static String ErrorMsg_Mandatory_CustomHSCode = "Please fill in customs HS Code";
	public static String ErrorMsg_Mandatory_BlNbr = "Please fill in the Bill of Lading";
	public static String ErrorMsg_Mandatory_ContainerNumber = "Please fill in the Container Number";
	public static String ErrorMsg_Length_CustomHSCode = "Wrong Customs HS Code inserted. Please insert 4/6/8 digits";
	public static String ErrorMsg_Wrong_CustomHSCode = "Wrong Customs HS Code inserted";
	public static String ErrorMsg_MainProblem_CustomHSCode = "Problem with inserting Main BL No.";
	public static String ErrorMsg_MainNotExist_Update = "Main HS Details is not included in process. Please update Main also.";
	public static String ErrorMsg_MainNotExist_Add = "Main HS Details is not included in process. Please add Main also.";
	public static String ErrorMsg_MainNotExist_HSCode = "Sub for Main shall not be updated without updating Main HS Details. Please include update for Main HS Detail also.";
	public static String ErrorMsg_MainOrSubNotExist = "Main HS Details/Other Sub HS Details is not included in process. Please update Main/Other Sub also.";
	public static String ErrorMsg_AddHSCode_Compulsary = "Missing Main HS Sub Details declaration. Please add HS Code(Sub Code) for main hs details.";
	public static String ErrorMsg_UpdateHSCode_Compulsary = "Missing Main HS Sub Details declaration. Please Update HS Code(Sub Code) for main hs details.";
	public static String ErrorMsg_UpdateHSCode_MainSub = "Main HS Code should match with sub HS Code";
	public static String ErrorMsg_UpdateCrgDesc_MainSub = "Main Cargo Description should match with sub Cargo Description";
	public static String ErrorMsg_UpdateCustom_MainSub = "Main Customs HS Code should match with main sub Customs HS Code ";
	public static String ErrorMsg_Same_Custom = "Duplicate Customs Declaration.";
	public static String ErrorMsg_UpdateCustom_MainSubMissing = "Missing Update for Main HS Sub. Please update customs hs code for main HS Sub also";
	public static String ErrorMsg_UpdateHSCode_Duplicate = "HS Code has already been declared.";
	public static String ErrorMsg_UpdateHSCode_Duplicate2 = "Duplicate HS Code Declared";
	public static String ErrorMsg_UpdateHSCode_Invalid = "Invalid HS Code. HS Code has never been declared.";
	public static String ErrorMsg_DeleteHSCode_MainSub= "Not allowed to delete Sub for Main Details";
	public static String ErrorMsg_MainAddFailed= "Error. Main Details Process Failed";
	public static String ErrorMsg_Wt_Vol_NotMatching = "Weight or Volume not matching with multiHsCode";
	public static String ErrorMsg_SubFailed= "Error. Sub Details Process Failed";
	public static String ErrorMsg_AddEdoCreated = "Edo created cannot Add.";
	public static String ErrorMsg_DeleteEdoCreated = "Edo created cannot Delete Sub HS Details.";
	// START FTZ CR - NS JULY 2024
	// <--JPOL //JPOM:"Please fill in the Number of packages.";
	public static String ErrorMsg_Mandatory_Weight = "Enter Gross Wt";
	// <--JPOL //JPOM:"Please fill in the Gross Weight.";
	public static String ErrorMsg_Valid_MinWeight = "Weight cannot be less than 10!";
	// <--JPOL //JPOM-Excel:"Please enter a number greater than or equal to 10!"
	public static String ErrorMsg_Valid_MaxWeight = "Gross Weight is more than the maximum tonnage allowed (20000mt)";
	// Weight cannot be greater than 5000000!";
	public static String ErrorMsg_Mandatory_GrossM3 = "Please fill in the Gross Measurement.";
	public static String ErrorMsg_Valid_MinGrossM3 = "Please enter a number greater than 0.01!";
	public static String ErrorMsg_Valid_MaxGrossM3 = "Please enter a number lesser than 9999.99!";
	public static String ErrorMsg_Valid_DecimalPt = "Please enter a decimal point lesser than or equal to 2!";
	public static String ErrorMsg_Mandatory_CargoStatus = "Select Cargo Status";
	public static String ErrorMsg_Mandatory_DGIndic = "Please select DG Indicator.";
	public static String ErrorMsg_Mandatory_StorageIndicator = "Please select Storage Indicator";
	public static String ErrorMsg_Mandatory_PakcagingType = "Enter Packaging Type";
	// <--JPOL //JPOM:"Please fill in the Packaging Type.";
	public static String ErrorMsg_Mandatory_DischargeOprIndic = "Please select Discharge operation indicator";
	public static String ErrorMsg_Mandatory_Consignee = "Please select a Consignee";
	// <--JPOL //JPOM:"Please fill in the Consignee";
	public static String ErrorMsg_Mandatory_PortLoading = "Enter Loading Port";
	// <--JPOL //JPOM:"Please fill in the Port of Loading.";
	public static String ErrorMsg_Mandatory_PortDischarge = "Please fill in the Port of Discharge.";
	public static String ErrorMsg_Mandatory_PortofFinalDestination = "Enter Final Destination Port";
	// <--JPOL //JPOM:"Please fill in the Port of Final Destination (compulsory for
	// transhipment cargo).";
	public static String ErrorMsg_NonNumeric = " Please enter a valid number.";
	public static String ErrorMsg_NonInteger = " Please enter a valid number(with out decimals).";
	public static String ErrorMsg_InvalidItemFromDroDown = "Invalid selection.Please select from the given list";
	public static String ErrorMsg_Mandatory_HatchData = " Please enter a number greater than 0";
	public static String ErrorMsg_Weight_HatchData = " Please enter a number between 10 and 5000000";
	public static String ErrorMsg_nonNegNo = " Please enter a number greater than 0";
	public static String ErrorMsg_ValErr = "Package Validation Error";
	public static String ErrorMsg_LaodDischarge_SamePort = "Discharge Port and Loading Port should not be the same";
	public static String ErrorMsg_FinalDestPort = "Final Destination Port cannot be Singapore";
	public static String ErrorMsg_Company_info_not_found = "Company info not found";
	public static String ErrorMsg_finding_company_error = "Finding company error!";
	public static String ErrorMsg_Shipping_Company_Not_Found = "Shipper Company info  not found!";
	public static String ErrorMsg_Cargo_Desc_size_more_than_4000_characters = "Cargo Desc size is exceeded! Please remove some special characters";
	public static String ErrorMsg_HS_Sub_Code_null = "HS Sub Code From  or  to is null";
	public static String ErrorMsg_Error_in_Cargo_List = "Error in Cargo List";
	
	public static String CUSTOM_MANDATORY_MSG_EDI = "Please insert ";
	public static String ErrorMsg_Mandatory_DG= "DG Indicator is Yes. ";

	public static int row_start = 4;
	public static int row_header = 3;

	public static String manifest_type_cd = "M";
	public static String manifest_split_bl_type_cd = "S";
	public static String bk_type_cd = "B";
	public static String packaging_type_cd = "P";

	public static String mandatory = " is mandatory";

	public static String hs_code_dropdown_index = "A4";
	public static String consignee_dropdown_index = "B4";
	public static String packaging_type_dropdown = "C4";
	public static String portlist_dropdown = "D4";
	public static String action_index = "A";
	public static String billofLanding_index = "B";
	public static String pkgNbr_index = "J";
	public static String grossWt_index = "K";
	public static String grossVol_index = "L";
	public static String hsCode_index = "G";
	public static String oldHsCode_index = "F";
	public static String crgDesc_index = "I";
	public static String consigneeOthers_index = "S";
	public static String custom_index = "H";
	
	//-----------spliBL-----------------------
	public static String splitBL_billofLanding_index = "D";
	public static String splitBL_pkgNbr_index = "L";
	public static String splitBL_grossWt_index = "M";
	public static String splitBL_grossVol_index = "N";
	public static String splitBL_hsCode_index = "I";
	public static String splitBL_oldHsCode_index = "H";
	public static String splitBL_crgDesc_index = "K";
	public static String splitBL_consigneeOthers_index = "U";
	public static String splitBL_custom_index = "J";
	//-----------spliBL-----------------------

	public static String storage_ind_open = "Open";
	public static String storage_ind_covered = "Covered";
	public static String storage_ind_open_status = "O";
	public static String storage_ind_covered_status = "C";

	public static String dis_op_ind_normal = "Normal";
	public static String dis_op_ind_normal_status = "N";
	public static String dis_op_ind_direct = "Direct";
	public static String dis_op_ind_direct_status = "D";
	public static String dis_op_ind_overside = "Overside";
	public static String dis_op_ind_overside_status = "O";

	public static String others = "Others";
	public static String transhipment = "Transhipment";
	public static String hatch_mandatory = "Error: No null and >0";
	public static String error = "Error";
	public static String success = "Success";
	public static String remarks = "Remarks";
	public static String remarks_key = "REMARKS";
	public static String remarks_msg = "Please Remove Remarks Field";

	public static int limit = 50;

	// public static String no_hatch = "No Hatch";
	// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
	public static String hs_Code = "HS Code";
	// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
	public static String no_hatch = "No Hatch(Do not Update)";
	public static String no_hatch_pkg = "No Hatch - PKG";
	public static String no_hatch_wt = "No Hatch - Weight";
	public static String no_hatch_mt = "No Hatch - Volume";
	public static int sheet_hidden_Header = 5;
	public static int sheet_hidden_rec_start = 6;

	public static String error_color = "FFFF0000";
	public static String total_line_processed = "Total Line Processed : ";
	public static String total_success = " / Total Success : ";
	public static String total_fail = " / Total Fail : ";
	public static String bill_status = "A";
	public static String action_add = "Add";
	public static String action_update = "Update";
	public static String action_delete = "Delete";
	public static String action_update_custom_details = "Update Customs Details";
	public static String action_NA = "N/A";
	public static String action_addHS = "Add HS Code (Sub Code)";
	public static String action_updateHS = "Update HS Code (Sub Code)";
	public static String action_deleteHS = "Delete HS Code (Sub Code)";

	// For Packaging
	public static String pkg_vessel_name = "VESSEL_NAME";
	public static String pkg_inward_voyage_no = "INWARD_VOYAGE_NO";
	public static String pkg_bills_of_landing_no = "BILLS-OF-LANDING NO.";
	public static String pkg_cargo_description = "CARGO_DESCRIPTION";
	public static String pkg_total_packages = "TOTAL_PACKAGES";
	public static String pkg_weight_kg = "WEIGHT_KG";
	public static String pkg_number_of_packages = "NUMBER_OF_PACKAGES";
	public static String pkg_total_package_weight_kg = "TOTAL_PACKAGE_WEIGHT_KG";
	public static String pkg_length_mm = "LENGTH_MM";
	public static String pkg_breadth_mm = "BREADTH_MM";
	public static String pkg_height_thickness_diameter_mm = "HEIGHT_THICKNESS_DIAMETER_MM";
	public static String pkg_packaging_dimension_header = "Packing Dimensions";
	public static int pakaging_bl_nbr_index = 0;
	public static int manifest_bl_nbr_index = 1;

	public static int vesselName_cell = 1;
	public static int inwardVoy_cell = 2;
	public static int Voy_cell = 2;
	public static String template_version = "Cargo Manifest Template Version";
	public static String template_version_no = "3";
	public static String yes = "Y";
	public static String no = "N";
	public static String cargo_status_local = "Local";
	public static String cargo_status_transhipment = "Transhipment";
	public static int sheet_hidden_template_row = 0;
	public static String dropdown_others = "OTHERS";
	public static String hs_code_731315 = "72(13-15)";
	public static String hs_code_722729 = "72(27-29)";
	public static String hs_code_720102 = "72(01-02)";
	public static String hs_code_720304 = "72(03-04)";
	public static String cargo_status_L = "L";
	public static String cargo_status_T = "T";
	public static String typeCd_Packaging = "P";
	public static String typeCd_Manifest = "M";
	public static String typeCd_BookingRef = "B";
	public static String typeCd_CustomDetailsExcel = "E";
	public static int bk_ref_nbr_index = 1;
	public static String packaging_sheet_name = "CargoDimension";
	public static String sheet_name_hidden = "hidden";
	public static String no_hatchpkg = "NO_HATCH_PKG";
	public static String no_hatchwt = "NO_HATCH_WT";
	public static String no_hatchmt = "NO_HATCH_MT";
	public static String packaging_filename = "Packaging_Upload@";
	public static String manifest_filename = "Manifest_Upload@";
	public static String file_ext = ".xlsx";
	public static String file_ext_status = ".txt";
	public static String manifest = "Manifest";
	public static String splitBl = "SplitBLManifest";
	public static String bkRef = "BookingReference";
	public static String customDetails = "CustomsDetails";
	// public static String exist = " exist already";
	public static String invalid = "is invalid";
	// public static String alphaNumeric = "should not be alphanumeric";
	public static String packaging_download_filename = "Packaging";

	public static String ErrorMsg_Error = "Error";
	public static String ErrorMsg_Success = "Success";
	// public static String ErrorMsg_BlNoDuplicate="Error. BlNo already Exists";
	public static String ErrorMsg_BlNoNotExist = "Error. Bl No. does not exists";
	public static String ErrorMsg_MainBlNoAlreadyExist = "Error. Main Bl No. already exists as split BL for another BL number";
	public static String ErrorMsg_MainBlNoNotExist = "Error. Main Bl No. does not exists. Please create main BL first in above row.";
	public static String ErrorMsg_ManifestDetailsProcess = "Error in processing manifest details";
	public static String ErrorMsg_CargoMarkingProcess = "Error in processing cargo marking details";
	public static String ErrorMsg_CargoSelectionProcess = "Error in processing cargo Selection details";
	public static String ErrorMsg_HatchProcess = "Error in processing hatch details";
	public static String ErrorMsg_Common = "Error in processing";
	public static String ErrorMsg_no_pkg_Exceeds = " Number of packages exceeds";
	public static String ErrorMsg_totalWt_Exceeds = " Total weight exceeds";
	public static String ErrorMsg_invalidExcel = " Error. Invalid Excel";
	public static String ErrorMsg_vslNotExist = " Error. Vessel does not Exist";
	public static String ErrorMsg_invoyNotExist = " Error. Invoy No. does not Exist";
	public static String ErrorMsg_outvoyNotExist = " Error. Outvoy No. does not Exist";
	public static String type_Input = "Input";
	public static String type_Output = "Input";
	public static String ErrorMsg_TotalPkgExceeds = "Error. Total hatch declared package exceeds the actual number of packges";
	public static String ErrorMsg_TotalWtExceeds = "Error. Total hatch declared gross weight exceeds the actual Gross weight";
	public static String ErrorMsg_TotalVolExceeds = "Error. Total hatch declared volume exceeds the actual gross measurement";
	public static String ErrorMsg_BlNoNoLength = "BL No. should not exceed 20 characters";
	public static String ErrorMsg_BlNoNoContainsSpecialChar = "BL No. should not contain special character";
	// for Hatch BDN
	public static String noHatchCode = "No_Hatch";
	public static String hatchCode = "H";
	// Manifest error messages start
	public static String Error_M20201 = "BL No already exists.";
	public static String Error_M20202 = "Edo created cannot Cancel.";
	public static String Error_M20203 = "BL Deleted cannot Amend.";
	public static String Error_M20204 = "Edo created cannot Amend.";
	public static String Error_M20205 = "Number of packages Less than EDO Nbr pkgs.";
	public static String Error_M20206 = "DN Printed cannot Amend.";
	public static String Error_M20207 = "Transhipment done cannot Amend.";
	public static String Error_M20208 = "DN Printed cannot Delete.";
	public static String Error_M20209 = "Transhipment done cannot Delete.";
	public static String Error_M20210 = "Edo with ReExport cargo cannot amend CargoStatus.";
	public static String Error_M20211 = "Manifest Creation not allowed.No approved PM4";
	public static String Error_M20212 = "Manifest Amend not allowed.No approved PM4";
	public static String Error_M20222 = "You cannot save the manifest details when cargo category and cargo type don't match.";
	public static String Error_M20223 = "HS Sub code from and to is invalid for the selected HS code";
	public static String Error_M21601 = "Invalid Loading Port.";
	public static String Error_M21602 = "Invalid Discharging Port.";
	public static String Error_M21603 = "Invalid Final Destination Port Code.";
	public static String Error_M21604 = "Invalid Packaging Type.";
	public static String Error_M21605 = "Vessel is closed";
	public static String Error_M4201 = "There are some error with your request. Please contact administrator if problem persists.";
	public static String Error_M20801 = "Invalid account number ";
	public static String Error_PD_blank = "Status is Local, leave Port of Destination blank";
	public static String Error_TransferHatch = "Please note that hatch breakdown information will be reset upon successful transfer.";
	public static String Error_M0010 = "There are some problems with your request. Please contact administrator if problem persists.";
	public static String Error_M20600 = "The weight cannot be less than the current one";
	public static String Error_M20604 = "The variance weight cannot be less than the current one";
	public static String Error_M20606 = "Status not equal to Y";
	public static String Error_M20610 = "Not UpdateStatus Y";
	// public static String Error_M1000002 = "GB_CLOSE_BJ_IND is set to Y";
	public static String Error_M1000002 = "Close BJ/ Shipment had been done already. You cannot amend this cargo.";
	public static String versionMismatch = "Uploaded Template version is outdated. Please download the latest version.";
	public static String cancel_dn_001 = "Cannot cancel DN from the next day of DN creation";
	public static String cancel_dn_002 = "Bills raised cannot Cancel DN";

	//start region special character not valid
	public static String SpecialCharNotValid = "Input for Bills-of-Lading No. contains a special character";
	//end region special character not valid
		
	public static final Map<String, String> MANIFEST_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M20201", Error_M20201);
					put("M20202", Error_M20202);
					put("M20203", Error_M20203);
					put("M20204", Error_M20204);
					put("M20205", Error_M20205);
					put("M20206", Error_M20206);
					put("M20207", Error_M20207);
					put("M20208", Error_M20208);
					put("M20209", Error_M20209);
					put("M20210", Error_M20210);
					put("M20211", Error_M20211);
					put("M20212", Error_M20212);
					put("M20222", Error_M20222);
					put("M20223", Error_M20223);
					put("M22601", Error_M21601);
					put("M22602", Error_M20223);
					put("M20223", Error_M20223);
					put("M21601", Error_M21601);
					put("M21602", Error_M21602);
					put("M21603", Error_M21603);
					put("M21604", Error_M21604);
					put("M21605", Error_M21605);
					put("M4201", Error_M4201);
					put("M20600", Error_M20600);
					put("M20604", Error_M20604);
					put("M20606", Error_M20606);
					put("M20610", Error_M20610);
					put("M1000002", Error_M1000002);
					put("M20801", Error_M20801);
					put("M0010", Error_M0010);
					put("M20802", Error_M20802);
					put("M20804", Error_M20804);
					put("M1004", Error_M1004);
					put("SpecialCharNotValid", SpecialCharNotValid);
				}
			});

	// Transfer Cargo error messages start
	public static String Error_T20201 = "No Cargo has been transferred from this vessel";
	public static String Error_T20202 = "No cargo has been tranferred to this vessel";
	public static String Error_T20203 = "You are not authorzied to view this cargo transferred";

	public static String ErrorMsg_Transfer_Of_Cargo1 = "Transfer From Voyage is invalid.";
	public static String ErrorMsg_Transfer_Of_Cargo2 = "Transfer To Voyage is closed/invalid or User is unauthorized.";

	public static final Map<String, String> TRANSFER_CARGO_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{

					put("T20201", Error_T20201);
					put("T20202", Error_T20202);
					put("T20203", Error_T20203);
					put("M4201", Error_M4201);
					put("M20635", "No Such Vessel Name / Out Voyage No. Available Please Try Again");
					put("M20608", "The Booking Reference code already exist");
				}
			});
	// Region ADP renom
	public static String EDO = "EDO";
	public static int BalanceCargoMaxRecord = 50;
	// Region End ADP renom

	// Start Region General Cargo Amendment
	public static String ErrorMsg_Vessel_Was_Close = "This Vessel was close. You cannot amend this cargo ";
	public static String ErrorMsg_Esn_Not_Found = "The Esn record cannot be found. Please try again.";
	public static String ErrorMsg_Invalid_vsl_voy = "Vessel call and voyage for this asn number is invalid";
	public static String ErrorMsg_Cannot_Get_Cargo = "Can not get Cargo for this asn number";
	public static String ErrorMsg_Atleast_One_Trucker = "At least 1 Trucker must be added.";
	public static String ErrorMsg_Pkg_Not_Greater = "Total number of packages of truckers cannot be greater than number of ESN pakages.";
	public static String ErrorMsg_Pkg_Not_Greater_Than_Declared = "The No.of Packages cannot be greater than the No.of Packages declared in the booking reference";
	public static String ErrorMsg_No_Aprroved_PM4 = " ESN update not allowed.No approved PM4.";
	public static String ErrorMsg_Invalid_Pkg_Type = " Invalid PackageType";
	public static String ErrorMsg_Weight_Not_Greater_Than_Declared = "The Weight cannot be greater than the amount declared in the booking reference";
	public static String ErrorMsg_Volume_Not_Greater_Than_Declared = "The Volume cannot be greater than the amount declared in the booking reference";
	public static String ErrorMsg_Billable_Party_Not_Valid = "Billable Party Account Number is not valid";
	public static String ErrorMsg_Cannot_Add_More_Truckers = "Cannot add more than ~ truckers";

	public static final Map<String, String> AMEND_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M1000002", Error_M1000002);
					put("M20222", Error_M20222);
					put("M20210", Error_M20210);
					put("M21601", Error_M21601);
					put("M21602", Error_M21602);
					put("M21603", Error_M21603);
					put("M21604", Error_M21604);
					put("M21605", Error_M21605);
					put("M20201", Error_M20201);
					put("M20202", Error_M20202);
					put("M20203", Error_M20203);
					put("M20204", Error_M20204);
					put("M20205", Error_M20205);
					put("M20206", Error_M20206);
					put("M20207", Error_M20207);
					put("M20208", Error_M20208);
					put("M20209", Error_M20209);
					put("M20210", Error_M20210);
					put("M20212", Error_M20212);
				}
			});

	// End Region

	// Start Region Inward Cargo

	public static String Error_M20804 = "Error in deleting Edo";
	public static String Error_M20807 = "Transhipment done Edo cannot be amended";
	public static String Error_M20809 = "Transhipment done Edo cannot be deleted";
	public static String Error_M20813 = "Transhipment done Edo cannot be Reverted for Vetting";
	public static String Error_M20802 = "Error in adding Edo";
	public static String Error_M20815 = "TESN already created. EDO deletion is not allowed. ";
	public static String Error_M1007 = "Updating record failed.";
	public static String Error_M20814 = "Edo already Deleted";
	public static String Error_M20806 = "Dn printed Edo cannot be amended";
	public static String Error_M20812 = "Dn printed Edo cannot be Reverted for Vetting";
	public static String Error_M20808 = "Dn printed Edo cannot be deleted";
	public static String Error_M20811 = "The Edo record cannot be found.  Please try again";
	public static String Error_M80003 = "Vessel has not Berthed yet.  DN cannot be printed.";
	public static String Error_M1004 = "No record found.";
	public static String Error_M4500 = "No DN detail found ! ";
	public static String Error_M80001 = "EDO has been cancelled.";
	public static String Error_M4599 = "Vessel not in Port.Re-Export is not allowed.";
	public static String ErrorMsg_Invalid_Vessel_Call = "Invalid Vessel Call values.";
	public static String ErrorMsg_Added_Atleast_One_Adp = "At least one ADP must be added";
	public static String ErrorMsg_Enter_Cargo_Agent_Name = "Enter Cargo Agent Name";
	public static String ErrorMsg_Enter_Attendance_Agent_Name = "Enter Attendance Agent Name";
	public static String ErrorMsg_Enter_Valid_Edo_Pkgs = "Enter Valid Edo Packages";
	public static String ErrorMsg_Cannot_Cancel_DN = "Cannot cancel DN from the next day of DN creation";
	public static String ErrorMsg_Cannot_Cancel_GB_BillCharge = "Unable to cancel gb_bill_charge table!";
	public static String ErrorMsg_Cannot_Cancel_GB_Vessel = "Unable to insert into gb_vessel_txn_event_log table!";
	public static String ErrorMsg_Cannot_Cancel_GB_Charge_Event = "Unable to insert into gb_charge_event_log table!";
	public static String ErrorMsg_Shipment_Has_Been_Tagged = "This shipment (ASN) has been tagged with Warehouse Application, should you require assistance, please contact SSPAT and D&C officers";
	public static String ErrorMsg_Cannot_Add_GB_Event_Table = "Unable to insert into gb_charge_event_log table! Ref No: ~ , Ref Ind: ~ ";
	public static String ErrorMsg_RefID_Type = "Ref Ind can only be DN or UA or SU!";
	public static String ErrorMsg_Reprinting_Charge = "Reprinting of charges only allowed for DN / UA and ED (Upon 1st DN creation alone)!!!";
	public static String ErrorMsg_Edo_001 = "At least one ADP must be added ";
	public static String ErrorMsg_Edo_002 = "Enter Attendance Agent Name";
	public static String ErrorMsg_Edo_003 = "Enter Valid No Of Packages";
	public static String ErrorMsg_Edo_004 = "Enter Valid No Of Volume";
	public static String ErrorMsg_Edo_005 = "Enter Valid No Of Weight";
	public static String ErrorMsg_Edo_006 = "Nominate Volume cannot be over the available volume ";
	public static String ErrorMsg_Edo_007 = "Nominate Weight cannot be over the available volume ";
	public static String ErrorMsg_Edo_008 = "Enter Cargo Agent Name";
	public static String Error_M20810 = "You are not authorized to view this record";
	// start region reexport
	public static String ErrorMsg_Vessel_Closed = "Vessel Closed cannot Create UA";
	public static String ErrorMsg_Update_Failed = "Updating record failed. ";
	// end region

	// start region cutoff
	public static String ErrorMsg_Insert_Failed = "Record Not Inserted !";
	// end region

	public static final Map<String, String> INWARD_CARGO_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M20804", Error_M20804);
					put("M1004", Error_M1004);
					put("M4500", Error_M4500);
					put("M80001", Error_M80001);
					put("M80003", Error_M80003);
					put("M20811", Error_M20811);
					put("M1007", Error_M1007);
					put("M20814", Error_M20814);
					put("M20806", Error_M20806);
					put("M20808", Error_M20808);
					put("M20812", Error_M20812);
					put("M20807", Error_M20807);
					put("M20809", Error_M20809);
					put("M20813", Error_M20813);
					put("M20802", Error_M20802);
					put("M20815", Error_M20815);
					put("M4599", Error_M4599);
					put("M20810", Error_M20810);
					put("M20801", Error_M20801);
					put("ErrorMsg_Cannot_Cancel_GB_BillCharge", ErrorMsg_Cannot_Cancel_GB_BillCharge);
				}
			});

	public static String Error_M41201 = "Stuffing Record Already Closed.";
	public static String Error_M41202 = "No Records Available or Closed By Another User.";
	public static String Error_M41203 = "Can not delete record,Stuffing Closed.";
	public static String Error_M41204 = "Stuffing Date and Time Should Be Less Than Current Date and Time.";
	public static String Error_M41205 = "No Records Available.";
	public static String Error_M41206 = "Bill already generated.Cannot waive charge.";
	public static String Error_M41207 = "Atleast one EDO / ESN should be stuffed before closing.";
	public static String Error_M41208 = "Invalid account number.";
	public static String ErrorMsg_Stuff_NotAmmend = "Stuffing Not Amended.Record Not Available or Closed By Another User.";
	public static String ErrorMsg_CannotAssign_BillableParty = "Cannot Assign Billable Party.Record Not Available or Closed By Another User.";
	public static String ErrorMsg_Update_Fail = "Updation failed.";
	public static String ErrorMsg_Stuff_Not_Cancel = "Stuffing Not Cancelled.Record Not Available or Closed By Another User.";
	public static String ErrorMsg_Bill_Already_Generate = "Bill already generated.Cannot waive charge.";
	public static String ErrorMsg_ASNNbr_Stuffing = "ESN ASN ~ not for Stuffing.";

	public static final Map<String, String> OUTWARD_STUFF_OUTBOUND_CTR_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M41201", Error_M41201);
					put("M41202", Error_M41202);
					put("M41203", Error_M41203);
					put("M41204", Error_M41204);
					put("M41205", Error_M41205);
					put("M41206", Error_M41206);
					put("M41207", Error_M41207);
					put("M41208", Error_M41208);
					put("M20201", Error_M20201);
					put("M20202", Error_M20202);
					put("M20203", Error_M20203);
					put("M20204", Error_M20204);
					put("M20205", Error_M20205);
					put("M20206", Error_M20206);
					put("M20207", Error_M20207);
					put("M20208", Error_M20208);
					put("M20209", Error_M20209);
					put("M20210", Error_M20210);
					put("M20211", Error_M20211);
					put("M20212", Error_M20212);
					put("M20222", Error_M20222);
					put("M20223", Error_M20223);
					put("M21601", Error_M21601);
					put("M21602", Error_M21602);
					put("M21603", Error_M21603);
					put("M21604", Error_M21604);
					put("M21605", Error_M21605);
					put("ErrorMsg_Stuff_NotAmmend", ErrorMsg_Stuff_NotAmmend);
					put("ErrorMsg_CannotAssign_BillableParty", ErrorMsg_CannotAssign_BillableParty);
					put("ErrorMsg_Update_Fail", ErrorMsg_Update_Fail);
					put("ErrorMsg_Stuff_Not_Cancel", ErrorMsg_Stuff_Not_Cancel);
					put("ErrorMsg_Bill_Already_Generate", ErrorMsg_Bill_Already_Generate);
				}
			});

	public static String Error_M41170 = "Can not add record,UnStuffing Closed";
	public static String Error_M41171 = "Can not amend record,UnStuffing Closed.";
	public static String Error_M41172 = "Can not delete record,UnStuffing Closed.";
	public static String Error_M41173 = "UnStuffing date and time can not be latter than current date.";
	public static String Error_M41174 = "Bill already generated.Cannot waive charge.";
	public static String Error_M41175 = "Atleast one manifest should be available before closing.";
	public static String Error_M41176 = "Invalid account number.";
	public static String Error_M41177 = "UnStuffing date and time has to be greater than ATB DTTM of vessel.";

	public static String ErrorMsg_Record_Not_Added = "Record Cannot be added to Database.";
	public static String ErrorMsg_Unstuff_Date = "UnStuffing date and time can not be latter than current date.";
	public static String ErrorMsg_Invalid_AcctNbr = "Invalid account number.";
	public static String ErrorMsg_HS_SubCode_Null = "HS Sub Code From  or  to is null";
	public static String ErrorMsg_HS_SubCode_Invalid = "HS Sub code From & To is not valid for the selected HS Sub Code";
	public static String ErrorMsg_Cargo_List = "Error in Cargo List";
	public static String ErrorMsg_blPartylist_List = "Error in blPartylist List";

	public static final Map<String, String> INWARD_UNSTUFF_OUTBOUND_CTR_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M41170", Error_M41170);
					put("M41171", Error_M41171);
					put("M41172", Error_M41172);
					put("M41173", Error_M41173);
					put("M41174", Error_M41174);
					put("M41175", Error_M41175);
					put("M41176", Error_M41176);
					put("M41177", Error_M41177);
					put("M20201", Error_M20201);
					put("M20202", Error_M20202);
					put("M20203", Error_M20203);
					put("M20204", Error_M20204);
					put("M20205", Error_M20205);
					put("M20206", Error_M20206);
					put("M20207", Error_M20207);
					put("M20208", Error_M20208);
					put("M20209", Error_M20209);
					put("M20210", Error_M20210);
					put("M20211", Error_M20211);
					put("M20212", Error_M20212);
					put("M20222", Error_M20222);
					put("M20223", Error_M20223);
					put("M21601", Error_M21601);
					put("M21602", Error_M21602);
					put("M21603", Error_M21603);
					put("M21604", Error_M21604);
					put("M21605", Error_M21605);
					put("ErrorMsg_Cannot_Delete", ErrorMsg_Cannot_Delete);
					put("ErrorMsg_Record_Not_Added", ErrorMsg_Record_Not_Added);
					put("ErrorMsg_Unstuff_Date", ErrorMsg_Unstuff_Date);
					put("ErrorMsg_Invalid_AcctNbr", ErrorMsg_Invalid_AcctNbr);
					put("ErrorMsg_HS_SubCode_Null", ErrorMsg_HS_SubCode_Null);
					put("ErrorMsg_HS_SubCode_Invalid", ErrorMsg_HS_SubCode_Invalid);
					put("ErrorMsg_Cargo_List", ErrorMsg_Cargo_List);
					put("ErrorMsg_blPartylist_List", ErrorMsg_blPartylist_List);
				}
			});

	// End Region

	// Start Region Outward Cargo
	// Outward Cargo - Booking Reference
	public static String M20600 = "The weight cannot be less than the current one";
	public static String M20601 = "The Volume cannot be less than the current one";
	public static String M20602 = "The number of Packages cannot be less than the existing ESN";
	public static String M20603 = "The Variance volume cannot be less than the current one";
	public static String M20604 = "The variance weight cannot be less than the current one";
	public static String M20605 = "The Variance package cannot be less than the current one";
	public static String Error_BK20606 = "The ESN CR NO Does not exist";
	public static String Error_M20607 = "The Port code does not exist";
	public static String Error_M20608 = "The Booking Reference code already exist";
	public static String Error_M20629 = "Booking Reference Number Does not Exist.";
	
	public static String Error_M42271 = "Not in Service Route sequence"; // Temp msg - Can't find error message in
																			// properties
	
	public static String Error_M42261 = "Invalid Port Code"; // Temp msg - Can't find error message in properties
	
	public static String ErrorMsg_Pkg_Code_Not_Valid = "Package code is not valid";
	public static String ErrorMsg_Enter_Trucker_Name = "Enter Trucker Name.";

	// start region sub adp shipment
	public static String ErrorMsg_Invalid_Number_packages = "Total of number of packages for all truckers should be equal number of package in Sub Trucker for Shipment.";
	public static String ErrorMsg_Balance_Package_Greater = "Total of number of packages cannot be greater than the balance packages declared."; // Added 07/12/2022
	public static String ErrorMsg_Trucker_Added = "At least 1 trucker must be added.";
	public static String ErrorMsg_Invalid_ESNNbr = "ESN number is not correct.";
	public static String ErrorMsg_Delete_Failed = "Delete SubADP failed. Num of Updated record = 0. ";
	public static String ErrorMsg_NotAdded_DB = "Record Cannot be added to Database.";
	public static String ErrorMsg_Invalid_ESN_Trans = "ESN transaction type is incorrect.";
	// end region

	// start region esn
	public static String ErrorMsg_Invalid_Voy = "Invalid vessel voyage values.";
	public static String ErrorMsg_HSsubCode_Null = "HS Sub Code From  or  to is null";
	public static String ErrorMsg_HSsubCode_Invalid = "HS Sub code From & To is not valid for the selected HS Sub Code";
	public static String ErrorMsg_Invalid_CntrNbr = "Invalid Container Number";
	public static String ErrorMsg_NotFound_CntrNbr = "Container number no found";
	public static String ErrorMsg_Need_Trucker = "At least 1 trucker must be added.";
	public static String ErrorMsg_Package_Equal = "Total of number of package for all truckers should be equal number of package in ESN.";
	public static String ErrorMsg_Package_Greater = "The No.of Packages cannot be greater than the No.of Packages declared in the booking reference";
	public static String ErrorMsg_ESN_Update_PM4_NotApproved = "ESN update not allowed. No approved PM4.";
	public static String ErrorMsg_Invalid_PackageType = "Invalid PackageType";
	public static String ErrorMsg_Weight_Greater = "The Weight cannot be greater than the amount declared in the booking reference";
	public static String ErrorMsg_Volume_Greater = "The Volume cannot be greater than the amount declared in the booking reference";
	public static String ErrorMsg_Invalid_Billable_AccNo = "Billable Party Account Number is not valid";
	public static String ErrorMsg_Bill_Raised_UA_Amend = "Bill Raised for UA. Cannot Amend ESN.";
	public static String ErrorMsg_Bill_Raised_UA_Delete = "Bill Raised for UA. Cannot Delete ESN.";
	public static String ErrorMsg_ASN_Tagged = "This shipment (ASN) has been tagged with Warehouse Application, should you require assistance, please contact SSPAT and D&C officers";
	public static String ErrorMsg_ESN_After_Shutout = "Cancel ESN is not allowed after Shutout.";
	public static String ErrorMsg_ESN_Cannot_Found = "The Esn record cannot be found.  Please try again.";
	public static String ErrorMsg_BkRef_Used = "The Booking Reference No. has been used already.";
	public static String ErrorMsg_ESN_Unberth = "Cannot create ESN for vessels that have unberthed";
	public static String ErrorMsg_Not_Authorized_BrNbr = "You are not authorized to make declarations for this BR. No.";
	public static String ErrorMsg_BerthApp_Cancel = "The Berth Application has been canceled for the vessel voyage";
	public static String ErrorMsg_BR_Vessel_Record_Closed = "The vessel record for this BR has been closed.";
	public static String ErrorMsg_Vessel_Record_Closed = "The Record for this vessel is closed.  No more Records can be added";
	public static String ErrorMsg_BR_ESN_Declarant = "BR cannot be used as you are not the ESN Declarant";
	public static String ErrorMsg_BR_Not_Found = "BR cannot be found!";
	public static String ErrorMsg_BR_Cannot_Used = "BR No cannot be used";
	public static String ErrorMsg_M1000001 = "Please enter a valid vehicle number.";
	public static String ErrorMsg_Vessel_ATU = "Cannot create ESN for vessels with ATU";
	public static String Msg_Custom_Success = "Successfully update customs detail.";
	// end region
	public static String Error_M80007 = "DN number of packages exceeds available number of packages.";
	
	
	public static final Map<String, String> OUTWARD_CARGO_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M42271", Error_M42271);
					put("BK20606", Error_BK20606);
					put("M20600", Error_M20600);
					put("M20607", Error_M20607);
					put("M20608", Error_M20608);
					put("M42261", Error_M42261);
					put("M20629", Error_M20629);
					put("M20811", Error_M20811);
					put("M0010", Error_M0010);
					put("M1000001", ErrorMsg_M1000001);
					put("M4500", Error_M4500);
					put("M20600", M20600);
					put("M20601", M20601);
					put("M20602", M20602);
					put("M20603", M20603);
					put("M20604", M20604);
					put("M20605", M20605);
					
				}
			});

	// End Region

	// start region general & bulk enquiry

	public static String ErrorMsg_Cust_Not_Found = "Customer account not found for acct_nbr ~ .";
	public static String ErrorMsg_Cust_Inactive = "Customer account is inactive for acct_nbr ~ .";
	public static String ErrorMsg_Bill_Adj_Null = "Bill Adj Param is null for tariff_cd = ~ .";
	public static String ErrorMsg_Berth_Timestamp_NotFound = "Berthing Timestamp not found for vv_cd: ~ , shift_ind: ~ .";
	public static String ErrorMsg_LWMS_Not_Process = "LWMS System cannot process Customise Tariff for tariffMainCat ~ .";
	public static String ErrorMsg_Tariff_Version = "Tariff Version not found for vv_cd: ~ , var_dttm: ~ .";
	public static String ErrorMsg_Publish_Tariff = "Publish Tariff cannot be found for version_nbr: ~ , tariff_main_cat_cd: ~ , tariff_sub_cat_cd: ~ ,"
			+ " business_type: ~ , scheme_cd: ~ , mvmt: ~ , type: ~ , cat_cd: ~ , cntr_size: ~ .";
	public static String ErrorMsg_GST_Rate = "GST rate cannot be found for tariff_gst_cd : ~ , eff_dttm: ~ .";
	public static String ErrorMsg_RefNbr_Invalid_Empty = "Reference number with reference to ref_ind = ~ is empty or invalid.";

	public static final Map<String, String> GENERAL_BULK_ENQUIRY_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M20802", Error_M20802);
					put("M1004", Error_M1004);
				}
			});

	// end region

	// start region outstanding edo esn
	public static final Map<String, String> OUTSTANDING_EDO_ESN_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M20802", Error_M20802);
					put("M1004", Error_M1004);
				}
			});

	// end region

	// start region general cargo shutout
	public static String ErrorMsg_Invalid_Number = "Invalid account number.";
	public static String ErrorMsg_Cannot_Delete = "Shutout Cargo EDO ~ cannot be deleted because there is DN(s) created for it";

	public static final Map<String, String> SHUTOUT_CARGO_MONITORING_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M1004", Error_M1004);
					put("M0010", Error_M0010);
					put("M20802", Error_M20802);
					put("ErrorMsg_Invalid_Number", ErrorMsg_Invalid_Number);
				}
			});

	// end region

	// start region TESN

	public static String ErrorMsg_Unable_Amend_After_ATU = "Unable to amend TESN after ATU of second vessel";
	public static String ErrorMsg_Voy_Vessel_Invalid_First = "The First Carrier Name, Inward Voyage No entered must be a valid vessel, in voyage no ";
	public static String ErrorMsg_Voy_Vessel_Invalid = "The Second Carrier Name, Outward Voyage No entered must be a valid vessel, out voyage no. ";
	public static String ErrorMsg_Unable_Create_After_ATU = "Unable to create TESN after ATU of second vessel";
	public static String ErrorMsg_UnAuthorized_Create_TESN = "You are not authorized to make create TESN for this EDO";
	public static String ErrorMsg_Cancel_TESN_AFter_Shutout = "Cancel TESN is not allowed after Shutout.";
	public static String ErrorMsg_Cargo_Desc_Length_custom = "Cargo Description cannot be more than 4000 characters.";
	public static String ErrorMsg_Cargo_Desc_Length = "Cargo Description cannot be more than 200 characters.";
	public static String ErrorMsg_Cargo_Marking_Length = "Cargo Markings cannot be more than 200 characters.";
	public static String ErrorMsg_TESN_Creation_NoApproved_MP4 = "TESN creation not allowed.No approved PM4.";
	public static String ErrorMsg_M21405 = "Nom. Wt. cannot be greater than the Nom. Wt. in Booking reference";
	public static String ErrorMsg_M21406 = "Nom. Vol. cannot be greater than the Nom. Vol. in Booking reference";
	public static String ErrorMsg_M21407 = "Local EDO cannot be Used.";

	public static final Map<String, String> TESN_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M20802", Error_M20802);
					put("M1004", Error_M1004);
					put("M21405", ErrorMsg_M21405);
					put("M21406", ErrorMsg_M21406);
					put("M21407", ErrorMsg_M21407);
				}
			});

	// end region

	// start region doc sub author
	public static String ErrorMsg_Vessel_Status_Changed_Cannot_Update = "Vessel Status Changed Cannot Update";
	public static String ErrorMsg_Authorization_Document_Submission = " Authorization Document Submission";
	public static String ErrorMsg_Enter_Valid_Authorization_Document = "Enter Valid Authorization Document";
	public static String ErrorMsg_Submission_TDB_CR_No = " Submission TDB CR No.";
	public static String ErrorMsg_Authorized_Parties_is_existing = "Authorized Parties is existing.";
	public static String ErrorMsg_Inserting_record_failed = "Inserting record failed.";
	public static String ErrorMsg_Delete_declarant_failed = "Delete declarant failed. Num of Updated record = 0";
	public static String ErrorMsg_Delete_record_failed = "Delete record failed.";
	public static String ErrorMsg_Authorised_Party_Cannot_deleted_manifest = "Authorised Party can not be deleted. Manifest is existing.";
	public static String ErrorMsg_Authorised_Party_Cannot_deleted_bookref = "Authorised Party can not be deleted. Booking ref is existing.";
	public static String Error_M20850 = "Vessel already closed";
	public static String Error_M20855 = "Vessel already cancelled";

	public static final Map<String, String> DOCSUB_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M20850", Error_M20850);
					put("M20855", Error_M20855);
					put("M1007", Error_M1007);
					put("M1004", Error_M1004);
				}
			});
	// end region

	// StartRegion Cargo Operation
	public static final String AUDIT_FNTYPE_CARGOOPS = "CARGO OPS";
	public static final String AUDIT_KEYID_STEVCOCD = "StevCoCd";
	public static final String AUDIT_KEYID_VVCD = "VV_CD";
	public static final String AUDIT_FNSUBTYPE_CARGODISCLOADINFO_UPDATE = "CrgDiscLoad Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_UPDATE = "CrgOpenBal Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLAN_UPDATE = "CrgOprPlan Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOOPR_UPDATE = "CrgOpr Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOVSLPROD_ADD = "CrgVslProd Add";
	public static final String AUDIT_FNSUBTYPE_CARGOOPRPLANDET_ADD = "CrgOprPlanDet Add";
	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEET_UPDATE = "CrgTallySheet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTALLYSHEETDET_UPDATE = "CrgTallySheetDet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEET_UPDATE = "CrgTimeSheet Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_DELETE = "CrgTimeSheetAct Del";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_UPDATE = "CrgTimeSheetAct Upd";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_DELETE = "CrgEqRental Del";
	public static final String AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_UPDATE = "CrgEqRental Upd";
	public static final String CargoOprErr_NOSTEV = "NOSTEV";
	public static final String CargoOprErr_NOOPENBAL = "NOOPENBAL";
	public static final String CargoOprErr_NOFIRSTACTIVITY = "NOFIRSTACTIVITY";
	public static final String CargoOprErr_SHIFTLESSTHANFIRSTACT = "SHIFTLESSTHANFIRSTACT";
	public static final String CargoOprErr_SHIFTGREATERTHANLASTACT = "SHIFTGREATERTHANLASTACT";
	public static final String CargoOprErr_SHIFTGREATERTHANCURRENTTIME = "SHIFTGREATERTHANCURRENTTIME";
	public static final String CargoOprErr_NOPREVSHIFT = "NOPREVSHIFT";
	public static final String CargoOprErr_NOSHIFT = "NOSHIFT";
	public static final String PARA_CD_GC_TON_THRESHOLD = "GC_TON_THRESHOLD";
	public static final String DATEFORMAT_INPUT = "ddMMyyyy HHmm";
	public static final String DATEFORMAT_INPUT_SHORT = "ddMMyyyy";
	public static final String DATEFORMAT_INPUT_LONG = "ddMMyyyy HHmmss";
	public static final String GBCC_CargoType_General = "GC";
	public static final String MISCTYPECD_DELAY_REASON = "WC_DELAY";
	public static final String MISCTYPECD_OPERATION_TYPE = "OPR_TYPE";
	public static final String MISCTYPECD_SHIFT_STEV = "SHIFT_STEV";
	public static final String MISCTYPECD_UNDERPERF_REASON = "CRG_PERFM";
	public static final String MISCTYPECD_WEATHER = "RAIN_CAT";
	public static final String MISCTYPECD_ACTIVITY = "CARGO_ACT";
	public static final String MISCTYPECD_EQTYPE = "EQUIP_RENT";
	public static final String OBJ_UPDATEMODE_DELETE = "D";
	public static final String PARAUNIT_Hours = "HRS";
	public static final String RULEPARA_CD_GCAllowance = "GCAllowance";
	public static final String SEPARATOR = ", ";
	public static final String SEQ_TALLYSHEET_ACT = "GBCC_TALLYSHEET_ACT_SEQ";
	public static final String SEQ_TALLYSHEET_EQRENTAL = "GBCC_TALLYSHEET_EQRENTAL_SEQ";
	public static final String STRING_OVERSIDE_CD = "O";
	public static final String STRING_DIRECT_CD = "D";
	public static final String STRING_NORMAL_CD = "N";
	public static final String STRING_LAND_RESHIP_CD = "L";
	public static final String STRING_OVERSIDE = "Overside";
	public static final String STRING_DIRECT = "Direct";
	public static final String STRING_NORMAL = "Normal";
	public static final String STRING_VALUE_YES_CODE = "Y";
	public static final String STRING_VALUE_YES = "Yes";
	public static final String STRING_VALUE_NO_CODE = "N";
	public static final String STRING_VALUE_NO = "No";
	public static final String STRING_LAND_RESHIP = "Land/Reship";
	public static final String TABLE_GBCCCARGOOPR = "GBCC_CARGO_OPR";
	public static final String TABLE_GBCCCARGOOPRDET = "GBCC_CARGO_OPR_DET";
	public static final String TABLE_GBCCCARGOOPENBALDET = "GBCC_CARGO_OPEN_BAL_DET";
	public static final String TABLE_GBCCCARGOOPENBAL = "GBCC_CARGO_OPEN_BAL";
	public static final String TABLE_GBCCCARGOOPRPLAN = "GBCC_CARGO_OPRPLAN";
	public static final String TABLE_GBCCCARGOOPRPLANDET = "GBCC_CARGO_OPR_PLANDET";
	public static final String TABLE_GBCCCARGOTALLYSHEET = "GBCC_CARGO_TALLYSHEET";
	public static final String TABLE_GBCCCARGOTALLYSHEETDET = "GBCC_CARGO_TALLYSHEET_DET";
	public static final String TABLE_GBCCCARGOTIMESHEET = "GBCC_CARGO_TIMESHEET";
	public static final String TABLE_GBCCCARGOTIMESHEETACT = "GBCC_CARGO_TIMESHEET_ACT";
	public static final String TABLE_GBCCVSLPROD = "GBCC_VSL_PROD";
	public static final String TABLE_GBCCCARGOTALLYSHEETEQRENTAL = "GBCC_CARGO_TIMESHEET_EQRENTAL";
	public static final String TALLYSHEET_OPRTYPE_Discharge = "D";
	public static final String TALLYSHEET_OPRTYPE_Load = "L";
	public static final String VV_STATUS_CL = "CL";
	public static final String VV_STATUS_BR = "BR";
	public static final String VV_STATUS_UB = "UB";
	public static final String YESNO_IND_NO = "N";
	public static final String YESNO_IND_YES = "Y";

	public static final Map<String, String> CARGOOPR_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
				}
			});
	// End Region

	// Start Region Amend HS Code
	public static final Map<String, String> AMENDHSCODE_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
				}
			});

	// End Region

	// Start Region Inter Terminal Store Rent Report
	public static final Map<String, String> STORERENT_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
				}
			});

	// End Region

	public static final Map<String, String> PASS_OUT_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M299402", "You cannot access any flexi alert for vessel/voyage");
					put("M299405", "You cannot access any flexi alert for container.");
					put("M4112", "Invalid ISO code.");
					put("M4077", "Invalid Company Code.");
					put("M4855", "No Records Found.");
					put("M4512", "Container No. does not exist!");
					put("M6001", "You don't have permission to see the records, please contact administrator!");
					put("M9901", "Inactive Vessel Call! ~ ");
					put("M4628", "You are not the agent/shipping line of the slot operator!");
					put("M4627", "Please enter voyage!");
					put("M4644", "SO-CO nomination does not exist");
					put("M9905", "Container No. does not exist!");
					put("M4501", "No Vessel found!");
					put("M299401", "Cancelled/Closed Vessel is not allowed.");
					put("M299402", "You cannot access any flexi alert for vessel/voyage");
					put("M299404", "Inactive Container is not allowed");
					put("M299405", "You cannot access any flexi alert for container");
					put("M299516", "Max length of event name is 200 characters.");
					put("M299515", "~ already added to List of Recipients.");
					put("M299506", "Invalid  Max hours from current ~ to send alert.");
					put("M299508", "You cannot access any flexi alert.");
					put("M299513", "Please select at least one recipient from Address Book.");
					put("M299510", "Please select at least one vessel/voyage.");
					put("M299502", "~ is already subscribed for ~.");
					put("M299509", "Please select at least one Vessel.");
					put("M299512", "Please input container number.");
					put("M299514", "Max length of container is 60 characters");
					put("M299517", "Max number of containers input is 5 ");
					put("M299504", "~ is already subscribed by ~.");
					put("M299515", "~ already added to List of Recipients.");
					put("M1132", "Max length of remarks is 255 characters. Please enter again.");
					put("M1131", "Recipient name already exists in Address Book. Please enter again.");
					put("M1010", "The Driver's Pass No. is incorrect.");
					put("M1012", "Cannot create this Pass Out Note.");
					put("M1014", "Cannot delete this Pass Out Note. Please contact your administrator.");
					put("M0010",
							"There are some problems with your request. Please contact administrator if problem persists.");
					put("M4201",
							"There are some problems with your request. Please contact administrator if problem persists.");
					put("M4204", "Invalid vessel voyage values.");
					put("M4075", "Invalid 1st carrier vessel voyage.");
					put("M4076", "Invalid 2nd carrier vessel voyage.");
					put("M4078", "Both are PSA vessel voyages.");
					put("M4079", "Invalid report type selected.");
					put("M4084", "Both are PSA carriers. Not allowed.");
					put("M4351", "Unexpected Error.");
					put("M1010", "The Driver's Pass No. is incorrect.");
					put("M1011", "Cannot create this UA (Tenant).");
					put("M1013", "Cannot delete this UA (Tenant). Please contact your administrator.");
					put("M4761", "Invalid Container Operator code");
					put("M4762", "You are not the Agent/Shipping Line of the Container Operator");
					put("M4638",
							"No records found for Final Statement Summary request for Vessel ~ with out voyage number ~");
					put("M4709",
							"You cannot Sublet back to the same haulier from whom you got this NMT Batch number ~ ");
					put("M4705", "The NMT batch Number ~ you entered does not belong to you");
					put("M4707",
							"The NMT batch Number ~ cannot be deleted as some of its containers are already pregated");
					put("M4708",
							"The NMT batch Number ~ cannot be deleted as it has been subletted to another Haulier");
					put("M4451", "Invalid vessel voyage.");
				}
			});

	// Start Region GB Misc Application
	public static String Error_M20102 = "Update is not allowed after the application is closed for billing or bill is triggered.";
	public static String Error_M20111 = "Update is not allowed for void application.";
	public static String Error_M20103 = "Update is not allowed after submission.";
	public static String Error_M20100 = "Voiding is not allowed after the bill is triggered.";
	public static String Error_M20101 = "Voiding is not allowed after submission.";
	public static String Error_M20104 = "Application has not been submitted, has closed for billing or has been billed.";
	public static String Error_M20105 = "Application has not been supported by operation.";
	public static String Error_M20106 = "Application has not been closed for billing approval.";
	public static String Error_M20110 = "This function is not applicable for this Application.";
	public static String Error_M22221 = "Close for Billing is not allowed when the application is pending billing.";
	public static String Error_M20107 = "Application is not accepted or has been billed.";
	public static String Error_M20108 = "Application has not been submitted for operation support.";
	public static String Error_M22224 = "Spreader for the chosen type is fully booked for the specified duration.";
	public static String Error_M100020 = "You should delete this Operation Details from SMART system.";
	public static String Error_M22220 = "Close for Billing should not be done by the same person who approved this application.";
	public static String Error_M4922 = "Unexpected Error.  Unable to connect to database.";
	public static final Map<String, String> GB_MISCAPP_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				{
					put("M4201", Error_M4201);
					put("M0010", Error_M0010);
					put("M20102", Error_M20102);
					put("M20103", Error_M20103);
					put("M20111", Error_M20111);
					put("M20100", Error_M20100);
					put("M20101", Error_M20101);
					put("M20104", Error_M20104);
					put("M20105", Error_M20105);
					put("M20106", Error_M20106);
					put("M20110", Error_M20110);
					put("M22221", Error_M22221);
					put("M20107", Error_M20107);
					put("M20108", Error_M20108);
					put("M22224", Error_M22224);
					put("M100020", Error_M100020);
					put("M22220", Error_M22220);
					put("M4922", Error_M4922);
				}
			});

	public static final String STRING_ZERO_TIME = "0000";
	public static final String DEFAULT_AREA_CODE = "ALL";
	public static final String DEFAULT_SLOT_TYPE = "ALL";
	public static final int DEFAULT_TRAILER_SIZE = 0;// default trailer size by thanhbtl6b
	public static final String DEFAULT_TRAILER_TYPE_CODE = "A";// default trailer type code by thanhbtl6b
	// End Region

	// START Region DN

	public static String Error_M20621 = "Vessel is closed. Creation of UA is not allowed.";
	public static String Error_M20622 = "ESN has been cancelled. Creation of UA is not allowed.";
	public static String Error_M20624 = "No more Cargo Left for Shipment";
	
	public static final Map<String, String> CONTAINERISED_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M80001", Error_M80001);
					put("M80003", Error_M80003);
					put("M20621", Error_M20621);
					put("M20622", Error_M20622);
					put("M20624", Error_M20624);
					put("M1000001", Error_M1000001);
					put("M1000002", Error_M1000002);
				}
			});

	public static final boolean ReportPrintingBean_isWindows = false;
	public static final String ReportPrintingBean_printer = "fin_printer_3:q1";
	public static final String Report_JO_Export_Format = "pdf";
	public static final String ReportPrintingBean_dateFormat = "yyyyMMdd-HHmmss";
	public static final String ReportPrintingBean_fileFormat = "${REPORT}-${OTHER}-${DATE}-${RANDOM}-${PAGE_ORIENTATION}-${PAGE_SIZE}.${EXTENSION}";
	public static final String LCT_SCHEME = "JCL";
	// END Region DN

	public static final String MISCTYPECD_TERMINAL_CD = "TERM_CD";
	public static final String JasperReportPrintingBean_JNDI_name = "JasperReportEngine";
	public static final boolean ReportPrintingBean_useEJBHomeFactory = true;

	public static final Map<String, String> JNDI_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("ReportPrintingBean.JNDI.java.naming.provider.url", "localhost:1099");
			put("ReportPrintingBean.JNDI.java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			put("ReportPrintingBean.JNDI.name", "CrystalClear");
		}
	});
	
	public static String Error_0010 = "#Customer account is inactive# for ~acct_nbr ";
	public static String Error_0011 = "#LWMS System cannot process Customise Tariff# for ~tariffMainCat (~) ";
	public static String Error_0012 = "#Berthing Timestamp not found# for ~vv_cd (~) ~shift_ind (~)";
	public static String Error_0013 = "#Berthing Timestamp not found# for ~vv_cd (~) ~vsl_nm (~) ~in_voy_nbr (~) ~out_voy_nbr (~) ~shpg_svc_cd (~) ~vsl_opr_cd (~) ~shift_ind (~)";
	public static String Error_0014 = "Exception occurred >> #Error when determining whether bulk cargo exist#  ";
	public static String Error_0015 = "#Customer account not found# for ~acct_nbr (~)";
	public static String Error_0016 = "Exception occurred >> #Error when determining new business type# : ";
	public static String Error_0017 = "#GST rate cannot be found# for ~tariff_gst_cd (~) ~eff_dttm (~)";
	public static String Error_0018 = "#Publish Tariff cannot be found# for ~version_nbr (~) ~tariff_main_cat_cd (~) ~tariff_sub_cat_cd (~) ~business_type (~) ~scheme_cd (~) ~mvmt (~) ~type (~) ~cat_cd (~) ~cntr_size (~)";
	public static String Error_0019 = "Exception occurred for ~ref_nbr (~) ~ref_ind (~) ";
	public static String Error_0020 = "Exception occurred for ~ref_nbr (~) ~ref_ind (~) ~vv_cd (~) ~local_leg (~)  ";
	public static String Error_0021 = "Reference number with reference to ~ref_ind = ~ is empty or invalid";
	public static String Error_0022 = "#No GB Store Rent Mapping found!# ~local_leg (~) ~scheme_cd (~)  ~tariff_main_cat_cd (~) ~tariff_sub_cat_cd (~)";
	public static String Error_0023 = "Exception occurred for ~ref_nbr (~) ~ref_ind (~) ~vv_cd (~) ~local_leg (~)  ";
	public static String Error_0024 = "#Exception occured when determining chargeable bill units.# ~Msg = ";
	public static String Error_0025 = "~tariff_cd (~) #Bill Adj Param is null#";
	public static String Error_0026 = "[Exception] when calculating the billable tonnage: ";
	public static String Error_0027 = "Bills raised cannot Cancel DN";
	public static String Error_0028 = "Cannot cancel DN from the next day of DN creation";
	public static String Error_0029 = "Unable to retrieve container and vessel information.";
	public static String Error_0030 = "Unable to get Msg ID";
	public static String Error_0031 = "Unable insert MQ message into table.";
	public static String Error_0032 = "Update failed due to parameter error";
	public static String Error_0033 = "First/last discharge/load timing update failed";
	public static String Error_0034 = "0 records updated to QC_TIMESHEET table.";
	public static String Error_0035 = "Unble to query QC_TIMESHEET table.";
	public static String Error_0036 = "Please release all physical quay crane first.";
	public static String Error_0037 = "Please release all prime movers first.";
	public static String Error_0038 = "Error in ATB/FD/FL/COD/COL/ATU.";
	public static String ErrMsg_OverStay_Request_Vessel_Cannot_Be_Closed = "No access to OverStay Dockage request function - Vessel cannot be closed";

	public static String errMsg999 = "Invalid Container.Re-Ship is not allowed.";
	public static String expMsg049 = "Container is in processing for other event";
	public static String expMsg022 = "Not an active container or Not an Export/Re-export/Re-shipment/Transship container";
	public static String expMsg038 = "Shutout date time cannot be a future date time";

	public static String ErrMsg_Delete_Failed = "Delete failed when preparing for sorting";
	public static String ErrMsg_Company_Code_Not_Found_For_Opr_Cd = "#Company Code not found for operator# for opr_cd (~)";
	public static String ErrMsg_Customer_Acc_Not_Found_For_Cust_Cd = "#Customer account not found# for cust_cd (~) business_type (C)";
	public static String ErrMsg_Customer_Acc_Inactive = "#Customer account is inactive# for cust_cd (~) business_type (C)";
	public static String ErrMsg_Bill_Party_Not_Found = "#Bill party not found# for tariff_cd (~)";
	public static String ErrMsg_Bill_Party_Not_Vessel_Operator = "#Bill party for Marine dockage is not vessel operator#";
	public static String ErrMsg_Insert_Failed = "#Insert failed when preparing for sorting#";
	public static String ErrMsg_Berthing_Shift_Indicater_Not_Found = "#Berthing shift indicator not found# for vv_cd (~)";

	public static String Error_M50011 = "This record cannot be updated since it is greater than '~' days old.";
	public static String Error_M50013 = "Inserting Record failed. Please try again later.";
	public static String Error_M50014 = "Record entered overlaps with an existing record, please correct.";
	public static String Error_M50010 = "This record cannot be deleted since it is greater than '~' days old.";
	public static String Error_M4975 = "Delete Failed !";
	public static String Error_M70002 = "Selected Vessels does not have Gangs supplied and Workable hatches.";
	public static String Error_Reprinting_For_Charges = "Reprinting of charges only allowed for DN / UA!!!";
	public static String Error_M1000001 = "Please enter a valid vehicle number.";
	public static String Error_M20625 = "Creation of UA is not allowed for existing ESNs which were created before shipment being re-opened.";
	public static String Error_M4101 = "Unexpected Error.";

	// BJ
	public static String Error_M3514 = "Over-side cargo/container must be cut off for Close BJ.";
	public static String Error_M3515 = "Cannot Close Shipment. This vessel has no COL.";
	public static String Error_M3516 = "Cannot Close Shipment. This vessel has no ATB.";
	public static String Error_M3517 = "Cannot Close BJ. This vessel has no COD.";
	public static String Error_M49241 = "You cannot renom this GB container after container exit.";
	public static String Error_M21408 = "You are not authorized to create new TESN";
	public static String Error_M4299 = "Process the container ~ at PSA-JP Holding Area.";
	public static String Error_M100010 = "Creation of UA for JP to JP is not allowed.";
	public static String Error_M100011 = "The IC Number entered is not valid.";
	public static String Error_M100012 = "The transaction quantity entered cannot be more than ~, the available package for this ADP.";
	public static String Error_M100013 = "Shipment is closed, creation of UA is not allowed.";
	public static String Error_M100014 = "The transaction quantity entered cannot be more than ~, the available package for this trucker.";
	public static String Error_M47114 = "You are not authorized to use this EDO ASN number.";
	public static String Error_M47115 = "You are not authorized to use this ESN ASN number.";
	public static String ErrorMsg_Cancel_UA_Created_Close_Shipment = "Cancel UA is not allowed for those UAs created before Close Shipment.";
	public static String ErrorMsg_Cancel_UA_ShutOut = "Cancel UA is not allowed after Shutout.";
	public static String ErrorMsg_Cancel_UA_Close_Shipment = "Cancel UA is not allowed after close shipment.";
	public static String ErrorMsg_Cannot_Cancel_UA = "Bills raised cannot Cancel UA";
	public static String ErrorMsg_ESN_Cancel = "ESN has been cancelled. Creation of UA is not allowed.";
	public static String ErrorMsg_Trans_Qty_Less = "Trans Qty should be less than Available Qty";
	public static String errMsg_ESN_Not_For_Stuffing = "ESN ASN ~ not for Stuffing.";

	public static String GB_ARRIVAL_WAIVER_IND_NO_WAIVER = "";
	public static String GB_ARRIVAL_WAIVER_IND_PENDING = "P";
	public static String GB_ARRIVAL_WAIVER_IND_APPROVED = "A";
	public static String GB_ARRIVAL_WAIVER_IND_REJECTED = "R";

	public static String GB_ARRIVAL_WAIVER_IND_STATUS_PENDING = "Pending";
	public static String GB_ARRIVAL_WAIVER_IND_STATUS_APPROVED = "Approved";
	public static String GB_ARRIVAL_WAIVER_IND_STATUS_REJECTED = "Rejected";

	public static String ALERT_CODE_LATE_ARRIVAL_WAIVER = "WLA";

	public static final String TXN_CD_DISCHARGE = "DISC";
	public static final String TXN_CD_LAND = "LAND";
	public static final String TXN_CD_LOAD = "LOAD";
	public static final String TXN_CD_SHIFTING = "SHFT";
	
	public static final int SP_DOCKAGE_RATE = 100;
	public static final String TXN_CD_CHANGE_STATUS_IMPORT   ="CSIM";
	
	public static final String TXN_CD_REEFER_UNPLUG = "UPLG";
	public static final String TXN_CD_GB_DISCHARGE = "DCSC";
	public static final String TXN_CD_GB_LOAD = "LDSC";
	
	public static String Error_M80008 = "Please note that by proceeding to take delivery of the cargo (i.e. creation of delivery note), the short term warehouse application shall be voided, i.e.  store rent shall apply based on the vessel scheme.  Please contact Storage Team (hp 8765 1234) for immediate assistance before you proceed to process the delivery note.";
	public static String Error_007 = "Logging into event log failed. Msg = ";
	public static String userid = "SYSTEM";
	public static String Error_M20623 = "Trans Qty should be less than Available Qty";    
	public static final Map<String, String> GC_OPS_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("0029", Error_0029);
					put("0030", Error_0030);
					put("0031", Error_0031);
					put("0032", Error_0032);
					put("0033", Error_0033);
					put("0034", Error_0034);
					put("0035", Error_0035);
					put("0036", Error_0036);
					put("0037", Error_0037);
					put("0038", Error_0038);
					put("M50011", Error_M50011);
					put("M20623", Error_M20623);
					put("M4512", "Container No. does not exist!");
					put("M20621", ErrorMsg_Vessel_Closed);
					put("M3301", "Insufficient information to proceed.");
					put("M3504", "Unable to Shortland/Shutout job");
					put("M3513", "Unable to close vessel. Please update Incentive Class.");
					put("M3506", "Unable to close vessel. Please clear all outstanding jobs first.");
					put("M3505", "Unable to close vessel file.");
					put("M80003", "Vessel has not Berthed yet.  DN cannot be printed.");
					put("M80001", " EDO has been cancelled.");
					put("M4500", "No DN detail found ! ");
					put("M1000001", "Please enter a valid vehicle number.");
					put("M70002", "Selected Vessels does not have Gangs supplied and Workable hatches.");
					put("M70008", "GB_CLOSE_SHP_IND not closed");
					put("M70009", "GB_CLOSE_BJ_IND not closed");
					put("M70010", "GB_CLOSE_VSL_IND closed");
					put("2101", Error_M20850);
					put("M20850", Error_M20850);
					put("M1007", Error_M1007);
					put("M4201", Error_M4201);
					put("M20850", "Vessel already closed");
					put("M3516", "Cannot Close Shipment. This vessel has no ATB.");
					put("M3515", "Cannot Close Shipment. This vessel has no COL.");
					put("M20635", "No Such Vessel Name / Out Voyage No. Available Please Try Again");
					put("M3518", "LCT vessel, please open Closed LCT.");
					put("M3509", "Cannot Close BJ.Discharge all Containers before Closing.");
					put("M3514", "Over-side cargo/container must be cut off for Close BJ.");
					put("M1004", "No record found.");
					put("M50014", Error_M50014);
					put("M50013", Error_M50013);
					put("M4975", Error_M4975);
					put("M50010", Error_M50010);
					put("M4101", Error_M4101);
					put("M100010", Error_M100010);
					put("M100013", Error_M100013);
					put("M20625", Error_M20625);
					put("M20622", Error_M20622);
					put("M20624", Error_M20624);
					put("M100011", Error_M100011);
					put("M1000001", Error_M1000001);
					put("M70002", Error_M70002);
					put("M80001", "EDO has been cancelled.");
					put("M3514", Error_M3514);
					put("M3515", Error_M3515);
					put("M3516", Error_M3516);
					put("M3517", Error_M3517);
					put("M49241", Error_M49241);
					put("M21408", Error_M21408);
					put("M4299", Error_M4299);
					put("M100011", Error_M100011);
					put("M100012", Error_M100012);
					put("M100014", Error_M100014);
					put("M47114", Error_M47114);
					put("M47115", Error_M47115);
					put("M80007", Error_M80007);
					put("M80008", Error_M80008);
				}
			});
	// START CR TO DISABLE VOLUME FIELD - NS FEB 2024
	public static final String CONF_HSSUBCODE = "CONF_HSSUBCODE_";
	public static final String CONF_HS_CODE = "CONF_HSCODE_";
	public static final String VOL_CONFIG = "VOL_CONFIG_";
	// END CR TO DISABLE VOLUME FIELD - NS FEB 2024
	
	// START CR FTZ HSCODE - NS JULY 2024
	public static final Object edo_hscode_msg = "Please make sure next EDO using the same BL Number will use the same HS Code(s) selected.";
	public static final String edo_hscode_error_combination = "Please choose the same HS Code(s) selected as previous EDO with same BL Number.";
	// END  CR FTZ HSCODE - NS JULY 2024
	
	public static String Error_001 = "UA(DN) Concerned TESN ASN(s) were not created";
	public static String Error_002 ="UA(DN)TESN ASN(s) were not created";
	public static String Error_003 ="UA TESN ASN(s) were not created";
	
	// KMF - NOV 2023
	public static boolean EVENT_LOG_IND=true;
	public static String EVENT_TYPE_LAT_DTTM="LAT_DTTM";
	public static String EVENT_TYPE_FAT_DTTM="FAT_DTTM";
	public static String SOURCE_SYSTEM="JPOM";
	public static String EVENT_TYPE_ATB_DTTM="ATB_DTTM";
	public static String EVENT_TYPE_ATU_DTTM="ATU_DTTM";
	
	// START #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
	public static String vesselCloseLCT = "Vessel already close LCT. No more records can be added";
	// START #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
	
	public static String manifestExist = "Manifest details exists";
	public static String ErrorMsg_Duplicate_TempBlNo = "Error. Duplicate Temporary Bl No.";
	public static String ErrorMsg_Duplicate_CargoType = "Mismatch Cargo Type For Same BL Number.";
	public static String ErrorMsg_Duplicate_CargoDescription = "Mismatch Cargo Description For Same BL Number.";
	public static String ErrorMsg_Duplicate_CargoSelection = "Mismatch Cargo Selection For Same BL Number.";
	public static String ErrorMsg_Duplicate_CargoMarking = "Mismatch Cargo Marking For Same BL Number.";
	public static String ErrorMsg_Duplicate_NbrPackages = "Mismatch Nbr Packages For Same BL Number.";
	public static String ErrorMsg_Duplicate_Grossweight = "Mismatch Gross Weight For Same BL Number.";
	public static String ErrorMsg_Duplicate_GrossMeasurement = "Mismatch Gross Measurement For Same BL Number.";
	public static String ErrorMsg_Duplicate_CargoStatus = "Mismatch Cargo Status For Same BL Number.";
	public static String ErrorMsg_Duplicate_DgIndicator = "Mismatch DG Indicator For Same BL Number.";
	public static String ErrorMsg_Duplicate_StorageIndicator = "Mismatch Storage Indicator For Same BL Number.";
	public static String ErrorMsg_Duplicate_PackingType = "Mismatch Cargo Status For Same BL Number.";
	public static String ErrorMsg_Duplicate_DOI = "Mismatch Discharge Operating Indicator For Same BL Number.";
	public static String ErrorMsg_Duplicate_Consignee = "Mismatch Consignee For Same BL Number.";
	public static String ErrorMsg_Duplicate_Consignee_Name = "Mismatch Consignee Name For Same BL Number.";
	public static String ErrorMsg_Duplicate_POL = "Mismatch Port of Loading For Same BL Number.";
	public static String ErrorMsg_Duplicate_POD = "Mismatch Port of Discharge For Same BL Number.";
	public static String ErrorMsg_Duplicate_POFD = "Mismatch Port of Final Destination For Same BL Number.";
	
	// customs details MACF
	public static String CUSTOM_VESSEL_NAME= "Vessel Name:";
	public static String CUSTOM_VOY= "Voyage No (In/Out):";
	public static String CUSTOM_VESSEL_DIS_PORT = "VESSEL_DIS_PORT";
	public static String CUSTOM_INSTRUCTION_TYPE = "INSTRUCTION_TYPE";
	public static String CUSTOM_IMPORT = "IMPORT";
	public static String CUSTOM_EXPORT = "EXPORT";
	public static String CUSTOM_TRANSHIPMENT_INWARD = "TRANSHIPMENT INWARD";
	public static String CUSTOM_TRANSHIPMENT_OUTWARD = "TRANSHIPMENT OUTWARD";
	public static String CUSTOM_TRANSIT = "TRANSIT";
	public static String CUSTOM_ORI_LOAD_PORT = "ORI_LOAD_PORT";
	public static String CUSTOM_LOAD_PORT = "LOAD_PORT";
	public static String CUSTOM_DIS_PORT = "DIS_PORT";
	public static String CUSTOM_DEST_PORT = "DEST_PORT";
	public static String CUSTOM_CONSIGNEE = "CONSIGNEE";
	public static String CUSTOM_SHIPPER = "SHIPPER";
	public static String CUSTOM_HSCODE = "HSCODE";
	public static String CUSTOM_HANDLING_INSTRUCTION = "HANDLING_INSTRUCTION";
	public static String CUSTOM_DIRECT_DELIVERY = "DIRECT DELIVERY";
	public static String CUSTOM_DISCHARGE_OVERSIDE = "DISCHARGE OVERSIDE";
	public static String CUSTOM_UC_CARGO = "UC CARGO";
	public static String CUSTOM_NO = "No";
	public static String CUSTOM_YES = "Yes";
	public static String CUSTOM_DG_IND = "DG_IND";
	public static String CUSTOM_IMO_CLASS = "IMO_CLASS";
	public static String CUSTOM_CNTR_STATUS = "CNTR_STATUS";
	public static String CUSTOM_UNDG_NBR = "UNDG_NBR";
	public static String CUSTOM_FLASHPOINT = "FLASHPOINT";
	public static String CUSTOM_PACKING_GROUP = "PACKING_GRP";
	public static String CUSTOM_BL_NBR = "BL_NBR";
	public static String CUSTOM_CNTR_NBR = "CNTR_NBR";
	public static String CUSTOM_GROSS_WEIGHT ="GROSS_WT";
	public static String CUSTOM_WEIGHT ="WEIGHT";
	public static String CUSTOM_EMPTY = "EMPTY";
	public static String CUSTOM_FCL = "FCL";
	public static String CUSTOM_PACKAGE_TYPE = "PACKAGE_TYPE";
	public static String CUSTOM_MANDATORY_MSG = "Please fills ";
	public static String CUSTOM_MAXLENGTH_MSG = " should not exceed ~ characters";
	public static int customDetails_bl_nbr_index = 2;
	public static String CUSTOM_blnbr_index = "C";
	public static String CUSTOM_CNTRNBR_INDEX = "BA";
	public static String CUSTOM_INSTRUCTIONTYPE_INDEX = "F";
	public static String CUSTOM_OTHERS = "OTHERS";
	public static String custom_details_filename = "Custom_Details_Upload@";
	public static String custom_template_version = "Customs Detail Template Version";
	public static String custom_template_version_no = "1";
	public static String CUSTOM_HSCODE_TOOLTIP = "Please insert 4/6/8 digits";
	public static String excel_type_cd = "E";
	public static String cuscar_type_cd = "C";
	public static String ErrorMsg_CustomDetailsProcess = "Error in processing customs details";
	public static String ErrorMsg_WrongBLNo = "Error. Different bill-of-lading number declared in JPOM for this vessel.";
	public static String ErrorMsg_cntrNbrNotExist = "Error. Container number does not exists";
	public static String ErrorMsg_recordsNotExist = "Error. Records does not exists";
	public static String ErrorMsg_cntrNbrNotExistContainer = "Error. Container number does not exists in JPOM for this Vessel";
	public static String ErrorMsg_InvalidCustomHSCode = "Wrong Customs HS Code inserted ! Please insert 4/6/8 digits.";
	public static String ErrorMsg_InvalidPort = "Invalid Port.";
	public static String ErrorMsg_WrongContainerStatus = "Error. Different status declared in JPOM for this vessel.";
	public static String ErrorMsg_WrongInstructionType = "Error. Different instruction type declared in JPOM for this vessel.";
	public static String ErrorMsg_WrongDangerousIndicator = "Error. Different dg indicator declared in JPOM for this vessel.";
	public static String ErrorMsg_cntrNbrAlreadyExist = "Error. Container No already exists for the selected instruction type.";
	public static String ErrorMsg_cntrNbrShipementStatusNotValid = "Error. Container No is a shutout container.";
	public static String CUSTOM_VOYAGE_NO = "VOYAGE_NO";
	public static String Custom_Excel_Notes="Note : If data discrepancies exist between this file submitted to Customs and JPOM Manifest/Booking Reference/ESN/EDO, please note the latter will prevail for JP charges computation and billing purposes. If you have any queries, kindly contact jpdoc@jp.com.sg.";
	public static String CUSTOM_VALID_WEIGHT = "Please insert the weight without the comma.";
	public static String ErrorMsg_WrongInstructionTypeInward = "Error. Please select only for Import or Transhipment Inward as only Inward Voyage is given.";
	public static String ErrorMsg_WrongInstructionTypeOutward = "Error. Please select only for Export or Transhipment Outward as only Outward Voyage is given.";	
	
	public static final Map<String, String> CUSTOM_DETAILS_ERROR_CONSTANT_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put("M4201", Error_M4201);
					put("M20201", Error_M20201);
				}
			});
	
	public static final Map<String, String> CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put(CUSTOM_IMPORT, "LI");
					put(CUSTOM_EXPORT, "LE");
					put(CUSTOM_TRANSHIPMENT_INWARD, "TS");
					put(CUSTOM_TRANSHIPMENT_OUTWARD, "TE");
					put(CUSTOM_TRANSIT, "T");
				}
			});
	
	public static final Map<String, String> CUSTOM_DETAILS_HANDLING_INSTRUCTION_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put(CUSTOM_DIRECT_DELIVERY, "DD");
					put(CUSTOM_DISCHARGE_OVERSIDE, "OS");
					put(CUSTOM_UC_CARGO, "UC");
				}
			});

	public static final Map<String, String> CUSTOM_DETAILS_CNTR_STATUS_MAP = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					put(CUSTOM_FCL, "F");
					put(CUSTOM_EMPTY, "E");
				}
			});
	
		// End Region
	
	// CUSCAR
	public static final String errMsg99998 = "Please select a file for uploading." + '\n';
	public static final String errMsg99997 = "File name should not be more than 30 characters in length." + '\n';
	public static final String errMsg_BGMFieldMissing = "BGM field missing! Cannot determine action. Please check CUSCAR file content.";
	
	public static final String UNB_MAIN = "UNB";
	public static final String UNB_SYTX_ID = "S001";
	public static final String UNB_INT_SEND = "S002";
	public static final String UNB_INT_RCV = "S003";
	public static final String UNB_DTP = "S004";
	public static final String UNB_ICR = "0020";
	
	public static final String UNH_MAIN = "UNH";
	public static final String UNH_MSG_REF_NBR = "0062";
	public static final String UNH_MSG_ID = "S009";
	public static final String UNH_COMM_ACS_REF = "0068";
	public static final String UNH_STS_TRANSF = "S010";
	
	public static final String BGM_MAIN = "BGM";
	public static final String BGM_DOC_MSG_NM = "C002";
	public static final String BGM_DOC_MSG_ID = "C106";
	public static final String BGM_MSG_FUNC_CD = "1225";
	
	public static final String DTM_MAIN = "DTM";
	public static final String DTM_DTP = "C507";
	
	public static final String TDT_MAIN = "TDT";
	public static final String TDT_TPT_STG_CD_QUAL = "8051";
	public static final String TDT_CONVY_REF_NBR = "8028";
	public static final String TDT_MOD_OF_TPT = "C220";
	public static final String TDT_TPT_ID = "C222";
	
	public static final String LOC_MAIN = "LOC";
	public static final String LOC_LOC_FUNC_CD_QUAL = "3227";
	public static final String LOC_LOC_ID = "C517";
	
	public static final String EQD_MAIN = "EQD";
	public static final String EQD_EQP_QUAL = "8053";
	public static final String EQD_EQP_ID = "C237";
	public static final String EQD_EQP_SZ_TYP = "C224";
	public static final String EQD_FULL_EMPTY_IND_CDD = "8169";
	
	public static final String SEL_MAIN = "SEL";
	public static final String SEL_SEAL_NBR = "9308";
	
	public static final String CNI_MAIN = "CNI";
	public static final String CNI_CONS_ITEM_NBR = "1490";
	public static final String CNI_DOC_MSG_DTL = "C503";
	
	public static final String RFF_MAIN = "RFF";
	public static final String RFF_REF = "C506";
	
	public static final String GEI_MAIN = "GEI";
	public static final String GEI_PROC_INFO_CD_QUAL = "9649";
	public static final String GEI_PROC_IND = "C012";
	
	public static final String NAD_MAIN = "NAD";
	public static final String NAD_PARTY_QUAL = "3035";
	public static final String NAD_NM_ADDR = "C058";
	public static final String NAD_PARTY_NM = "C080";
	
	public static final String GID_MAIN = "GID";
	public static final String GID_GDS_ITEM_NBR = "1496";
	public static final String GID_NBR_TYPE_PKG = "C213";
	
	public static final String HAN_MAIN = "HAN";
	public static final String HAN_HDLG_INSTR = "C524";
	public static final String HAN_HZRD_MTRL = "C218";
	
	public static final String FTX_MAIN = "FTX";
	public static final String FTX_TXT_SUBJ_CD_QUAL = "4451";
	public static final String FTX_TXT_REF = "C107";
	public static final String FTX_TXT_LIT = "C108";
	
	public static final String MEA_MAIN = "MEA";
	public static final String MEA_MSR_APPL_QUAL = "6311";
	public static final String MEA_MSR_DTL = "C502";
	public static final String MEA_VAL_RNG = "C174";
	
	public static final String SGP_MAIN = "SGP";
	public static final String SGP_EQP_ID = "C237";
	
	public static final String DGS_MAIN = "DGS";
	public static final String DGS_DGS_REG = "8273";
	public static final String DGS_HZD_CD = "C205";
	public static final String DGS_UNDG_INFO = "C234";
	public static final String DGS_DGS_SHPMT_FPT = "C223";
	
	public static final String PCI_MAIN = "PCI";
	public static final String PCI_MRK_INSTR_CD = "4233";
	public static final String PCI_MRK_LBL = "C210";
	
	public static final String CST_MAIN = "CST";
	public static final String CST_GDS_ITEM_NBR = "1496";
	public static final String CST_CST_ID_CD = "C246";
	
	public static final String UNT_MAIN = "UNT";
	public static final String UNT_NBR_SGMNT_IN_MSG = "0074";
	public static final String UNT_MSG_REF_NBR = "0062";
	
	public static final String UNZ_MAIN = "UNZ";
	public static final String UNZ_INTCHG_CTRL_CNT = "0036";
	public static final String UNZ_INTCHG_CTRL_REF = "0020";
	
	public static final String INVALID_DATA_FORMAT = "Non-EDIFACT/EDI file. Missing segment: ";
	public static final String CUSCAR_DATA_NOT_ENOUGH = "Data is insufficient: ";
	public static final String NON_CUSCAR_ERR = "Non-supported message type. Expected Message Type: CUSCAR, Release Nbr: 11A or 11B.\n Actual Message Type: ";
	public static final String NON_CUSCAR_ERR_1 = ", Release Nbr: ";
	
	public static final String total_rcrd_rcv = "Total Record Received : ";
	public static final String total_err_rcd = "Total Error Record Detected : ";
	public static final String total_rcrd_succ = "Total Record Processed Successfully : ";
	public static final String total_rcrd_crtd = "Total Record Created : ";
	public static final String total_rcrd_updt = "Total Record Updated : ";
	public static final String total_rcrd_del = "Total Record Deleted : ";
	
	public static final String total_rcrd_rcv_txt = "Total Record Received";
	public static final String total_err_rcd_txt = "Total Error Record Detected";
	public static final String total_rcrd_succ_txt = "Total Record Processed Successfully";
	public static final String total_rcrd_crtd_txt = "Total Record Created";
	public static final String total_rcrd_updt_txt = "Total Record Updated";
	public static final String total_rcrd_del_txt = "Total Record Deleted";
	
	public static String CUSTOM_MASTER_BL_NBR = "MASTER_BL_NBR";
	public static String CUSTOM_BL_NBR_REMARKS = "BL_NBR_REMARKS";
	public static String CUSTOM_PLACE_OF_RECEIPT_NAME = "PLACE_OF_RECEIPT_NAME";
	public static String CUSTOM_PLACE_OF_DELIVERY_NAME = "PLACE_OF_DELIVERY_NAME";
	public static String CUSTOM_CONSIGNEE_UEN = "CONSIGNEE_UEN";
	public static String CUSTOM_CONSIGNEE_ADDRESS = "CONSIGNEE_ADDRESS";
	public static String CUSTOM_SHIPPER_UEN = "SHIPPER_UEN";
	public static String CUSTOM_SHIPPER_ADDRESS = "SHIPPER_ADDRESS";
	public static String CUSTOM_NOTIFY_PARTY_NAME = "NOTIFY_PARTY_NAME";
	public static String CUSTOM_NOTIFY_PARTY_UEN = "NOTIFY_PARTY_UEN";
	public static String CUSTOM_NOTIFY_PARTY_CONTACT = "NOTIFY_PARTY_CONTACT";
	public static String CUSTOM_NOTIFY_PARTY_EMAIL = "NOTIFY_PARTY_EMAIL";
	public static String CUSTOM_NOTIFY_PARTY_ADDRESS = "NOTIFY_PARTY_ADDRESS";
	public static String CUSTOM_FREIGHT_FOWARDER_NAME = "FREIGHT_FOWARDER_NAME";
	public static String CUSTOM_FREIGHT_FOWARDER_UEN = "FREIGHT_FOWARDER_UEN";
	public static String CUSTOM_FREIGHT_FOWARDER_CONTACT = "FREIGHT_FOWARDER_CONTACT";
	public static String CUSTOM_FREIGHT_FOWARDER_EMAIL = "FREIGHT_FOWARDER_EMAIL";
	public static String CUSTOM_FREIGHT_FOWARDER_ADDRESS = "FREIGHT_FOWARDER_ADDRESS";
	public static String CUSTOM_STEVEDORE_NAME = "STEVEDORE_NAME";
	public static String CUSTOM_STEVEDORE_UEN = "STEVEDORE_UEN";
	public static String CUSTOM_STEVEDORE_CONTACT = "STEVEDORE_CONTACT";
	public static String CUSTOM_STEVEDORE_EMAIL = "STEVEDORE_EMAIL";
	public static String CUSTOM_STEVEDORE_ADDRESS = "STEVEDORE_ADDRESS";
	public static String CUSTOM_CARGO_AGENT_NAME = "CARGO_AGENT_NAME";
	public static String CUSTOM_CARGO_AGENT_UEN = "CARGO_AGENT_UEN";
	public static String CUSTOM_CARGO_AGENT_CONTACT = "CARGO_AGENT_CONTACT";
	public static String CUSTOM_CARGO_AGENT_EMAIL = "CARGO_AGENT_EMAIL";
	public static String CUSTOM_CARGO_AGENT_ADDRESS = "CARGO_AGENT_ADDRESS";
	public static String CUSTOM_ITEM_NO = "ITEM_NO";
	public static String CUSTOM_PACKAGE_QUANTITY = "PACKAGE_QUANTITY";
	public static String CUSTOM_MEASUREMENT = "MEASUREMENT";
	public static String CUSTOM_CARGO_DESCRIPTION = "CARGO_DESCRIPTION";
	public static String CUSTOM_MARK_AND_NO = "MARK_AND_NO";
	public static String CUSTOM_PACKING_GRP = "PACKING_GRP";
	public static String CUSTOM_ISO = "ISO";
	public static String CUSTOM_GROSS_WT = "GROSS_WT";
	public static String CUSTOM_SEAL_NBR_CARRIER= "SEAL_NBR_CARRIER";
	
	//CH-7 Changes
	public static String LATE_ARRIVAL_IND = "L";
	public static String OVERSTAY_DOCKAGE_IND = "O";
	public static String OSD_DATE_FORMAT = "dd/MM/yyyy/HH/mm";
	public static String OSD_ETB_FORMAT = "dd-MM-yyyy HH:mm";
	public static int OSD_EXEMPTION_DURATION = 60;
	public static int LATE_EXEMPTION_DURATION = 15;
}

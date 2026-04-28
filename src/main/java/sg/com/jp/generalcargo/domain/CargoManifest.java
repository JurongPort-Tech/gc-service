package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CargoManifest {
	private String 	mft_seq_nbr;
	private String 	action;
	private String 	bills_of_landing_no;  
	private String  bill_status;
	private int unStuff_seq_nbr;
	private String  var_nbr;
	private String 	cargoType;    
	private String 	cargo_description;  
	private String 	hs_code_sub_code;
	private String hs_code;
	private String hs_sub_code_fr;
	private String hs_sub_code_to;
	private String 	cargo_selection;  
	private String 	cargo_selection_key;  
	private String 	cargo_marking;  
	private String 	number_of_packages;  
	private String 	gross_weight_kg;  
	private String 	gross_measurement_m3;  
	private String 	cargo_status;  
	private String 	dg_indicator;  
	private String 	storage_indicator;  
	private String 	packing_type;  
	private String 	discharge_operation_indicator;  
	private String 	consignee;  
	private String 	consignee_others;  
	private String 	port_of_loading;  
	private String 	port_of_discharge;  
	private String 	port_of_final_destination;
	private String last_modify_user_id;
	private String last_modify_dttm;
	private int trans_nbr;
	
	private List<HatchDetails> hatchList;
	
	private int rownum;
	private String message;
	List<Comments> errorInfo;
	
	// START FTZ CR ADDED NEW COLUMNS
	private BigDecimal 	mft_hscode_seq_nbr; 
	private String 	custom_hs_code; 
	private String 	oldHSCode; 
	private String 	oldHSCode_fr; 
	private String 	oldHSCode_to; 
	private String 	consignee_addr;  
	private String 	shipper_nm;  
	private String 	shipper_addr;  
	private String 	notify_party;  
	private String 	notify_party_addr;  
	private String 	place_of_delivery;  
	private String 	place_of_receipt;
	private boolean multipleHsCode;
	private boolean subHSUpdate;
	private boolean hasAddProcess;
	private boolean valueChanges;
	private boolean mainSub;
	private String manifest_create_cd;
	private boolean edo_created;
	private String oldCustom;
	private boolean isCustomChanged;
	
	// Start Split BL - NS Dec 2024
	private String split_bl_ind;
	private String split_main_bl;
	private int split_id;
	
	public BigDecimal getMft_hscode_seq_nbr() {
		return mft_hscode_seq_nbr;
	}
	public void setMft_hscode_seq_nbr(BigDecimal mft_hscode_seq_nbr) {
		this.mft_hscode_seq_nbr = mft_hscode_seq_nbr;
	}
	public String getCustom_hs_code() {
		return custom_hs_code;
	}
	public void setCustom_hs_code(String custom_hs_code) {
		this.custom_hs_code = custom_hs_code;
	}
	public String getOldHSCode() {
		return oldHSCode;
	}
	public void setOldHSCode(String oldHSCode) {
		this.oldHSCode = oldHSCode;
	}
	public String getOldHSCode_fr() {
		return oldHSCode_fr;
	}
	public void setOldHSCode_fr(String oldHSCode_fr) {
		this.oldHSCode_fr = oldHSCode_fr;
	}
	public String getOldHSCode_to() {
		return oldHSCode_to;
	}
	public void setOldHSCode_to(String oldHSCode_to) {
		this.oldHSCode_to = oldHSCode_to;
	}
	public String getConsignee_addr() {
		return consignee_addr;
	}
	public void setConsignee_addr(String consignee_addr) {
		this.consignee_addr = consignee_addr;
	}
	public String getShipper_nm() {
		return shipper_nm;
	}
	public void setShipper_nm(String shipper_nm) {
		this.shipper_nm = shipper_nm;
	}
	public String getShipper_addr() {
		return shipper_addr;
	}
	public void setShipper_addr(String shipper_addr) {
		this.shipper_addr = shipper_addr;
	}
	public String getNotify_party() {
		return notify_party;
	}
	public void setNotify_party(String notify_party) {
		this.notify_party = notify_party;
	}
	public String getNotify_party_addr() {
		return notify_party_addr;
	}
	public void setNotify_party_addr(String notify_party_addr) {
		this.notify_party_addr = notify_party_addr;
	}
	public String getPlace_of_delivery() {
		return place_of_delivery;
	}
	public void setPlace_of_delivery(String place_of_delivery) {
		this.place_of_delivery = place_of_delivery;
	}
	public String getPlace_of_receipt() {
		return place_of_receipt;
	}
	public void setPlace_of_receipt(String place_of_receipt) {
		this.place_of_receipt = place_of_receipt;
	}
	public boolean getMultipleHsCode() {
		return multipleHsCode;
	}
	public void setMultipleHsCode(boolean multipleHsCode) {
		this.multipleHsCode = multipleHsCode;
	}
	public boolean getSubHSUpdate() {
		return subHSUpdate;
	}
	public void setSubHSUpdate(boolean subHSUpdate) {
		this.subHSUpdate = subHSUpdate;
	}
	public boolean getHasAddProcess() {
		return hasAddProcess;
	}
	public void setHasAddProcess(boolean hasAddProcess) {
		this.hasAddProcess = hasAddProcess;
	}
	public boolean getValueChanges() {
		return valueChanges;
	}
	public void setValueChanges(boolean valueChanges) {
		this.valueChanges = valueChanges;
	}
	public boolean getMainSub() {
		return mainSub;
	}
	public void setMainSub(boolean mainSub) {
		this.mainSub = mainSub;
	}
	public String getManifest_create_cd() {
		return manifest_create_cd;
	}
	public void setManifest_create_cd(String manifest_create_cd) {
		this.manifest_create_cd = manifest_create_cd;
	}
	public boolean getEdo_created() {
		return edo_created;
	}
	public void setEdo_created(boolean edo_created) {
		this.edo_created = edo_created;
	}
	public String getOldCustom() {
		return oldCustom;
	}
	public void setOldCustom(String oldCustom) {
		this.oldCustom = oldCustom;
	}
	public boolean isCustomChanged() {
		return isCustomChanged;
	}
	public void setCustomChanged(boolean isCustomChanged) {
		this.isCustomChanged = isCustomChanged;
	}
	// END FTZ CR ADDED NEW COLUMNS

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getBills_of_landing_no() {
		return bills_of_landing_no;
	}
	public void setBills_of_landing_no(String bills_of_landing_no) {
		this.bills_of_landing_no = bills_of_landing_no;
	}
	 
	public String getCargo_description() {
		return cargo_description;
	}
	public void setCargo_description(String cargo_description) {
		this.cargo_description = cargo_description;
	}
	public String getHs_code_sub_code() {
		return hs_code_sub_code;
	}
	public void setHs_code_sub_code(String hs_code_sub_code) {
		this.hs_code_sub_code = hs_code_sub_code;
	}
	public String getCargo_selection() {
		return cargo_selection;
	}
	public void setCargo_selection(String cargo_selection) {
		this.cargo_selection = cargo_selection;
	}
	public String getCargo_marking() {
		return cargo_marking;
	}
	public void setCargo_marking(String cargo_marking) {
		this.cargo_marking = cargo_marking;
	}
	public String getNumber_of_packages() {
		return number_of_packages;
	}
	public void setNumber_of_packages(String number_of_packages) {
		this.number_of_packages = number_of_packages;
	}
	public String getGross_weight_kg() {
		return gross_weight_kg;
	}
	public void setGross_weight_kg(String gross_weight_kg) {
		this.gross_weight_kg = gross_weight_kg;
	}
	public String getGross_measurement_m3() {
		return gross_measurement_m3;
	}
	public void setGross_measurement_m3(String gross_measurement_m3) {
		this.gross_measurement_m3 = gross_measurement_m3;
	}
	public String getCargo_status() {
		return cargo_status;
	}
	public void setCargo_status(String cargo_status) {
		this.cargo_status = cargo_status;
	}
	public String getDg_indicator() {
		return dg_indicator;
	}
	public void setDg_indicator(String dg_indicator) {
		this.dg_indicator = dg_indicator;
	}
	public String getStorage_indicator() {
		return storage_indicator;
	}
	public void setStorage_indicator(String storage_indicator) {
		this.storage_indicator = storage_indicator;
	}
	public String getPacking_type() {
		return packing_type;
	}
	public void setPacking_type(String packing_type) {
		this.packing_type = packing_type;
	}
	public String getDischarge_operation_indicator() {
		return discharge_operation_indicator;
	}
	public void setDischarge_operation_indicator(String discharge_operation_indicator) {
		this.discharge_operation_indicator = discharge_operation_indicator;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getConsignee_others() {
		return consignee_others;
	}
	public void setConsignee_others(String consignee_others) {
		this.consignee_others = consignee_others;
	}
	public String getPort_of_loading() {
		return port_of_loading;
	}
	public void setPort_of_loading(String port_of_loading) {
		this.port_of_loading = port_of_loading;
	}
	public String getPort_of_discharge() {
		return port_of_discharge;
	}
	public void setPort_of_discharge(String port_of_discharge) {
		this.port_of_discharge = port_of_discharge;
	}
	public String getPort_of_final_destination() {
		return port_of_final_destination;
	}
	public void setPort_of_final_destination(String port_of_final_destination) {
		this.port_of_final_destination = port_of_final_destination;
	}
	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}
	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}
	public int getRownum() {
		return rownum;
	}
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Comments> getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(List<Comments> errorInfo) {
		this.errorInfo = errorInfo;
	}
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
	public String getBill_status() {
		return bill_status;
	}
	public void setBill_status(String bill_status) {
		this.bill_status = bill_status;
	}
	public String getVar_nbr() {
		return var_nbr;
	}
	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}
	public int getUnStuff_seq_nbr() {
		return unStuff_seq_nbr;
	}
	public void setUnStuff_seq_nbr(int unStuff_seq_nbr) {
		this.unStuff_seq_nbr = unStuff_seq_nbr;
	}
	public String getHs_code() {
		return hs_code;
	}
	public void setHs_code(String hs_code) {
		this.hs_code = hs_code;
	}
	public String getHs_sub_code_fr() {
		return hs_sub_code_fr;
	}
	public void setHs_sub_code_fr(String hs_sub_code_fr) {
		this.hs_sub_code_fr = hs_sub_code_fr;
	}
	public String getHs_sub_code_to() {
		return hs_sub_code_to;
	}
	public void setHs_sub_code_to(String hs_sub_code_to) {
		this.hs_sub_code_to = hs_sub_code_to;
	}
	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}
	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}
	public List<HatchDetails> getHatchList() {
		return hatchList;
	}
	public void setHatchList(List<HatchDetails> hatchList) {
		this.hatchList = hatchList;
	}
	public String getCargo_selection_key() {
		return cargo_selection_key;
	}
	public void setCargo_selection_key(String cargo_selection_key) {
		this.cargo_selection_key = cargo_selection_key;
	}
	public String getCargoType() {
		return cargoType;
	}
	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}
	public String getLast_modify_dttm() {
		return last_modify_dttm;
	}
	public void setLast_modify_dttm(String last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}
	public int getTrans_nbr() {
		return trans_nbr;
	}
	public void setTrans_nbr(int trans_nbr) {
		this.trans_nbr = trans_nbr;
	}
	public String getSplit_bl_ind() {
		return split_bl_ind;
	}
	public void setSplit_bl_ind(String split_bl_ind) {
		this.split_bl_ind = split_bl_ind;
	}
	public String getSplit_main_bl() {
		return split_main_bl;
	}
	public void setSplit_main_bl(String split_main_bl) {
		this.split_main_bl = split_main_bl;
	}
	public int getSplit_id() {
		return split_id;
	}
	public void setSplit_id(int split_id) {
		this.split_id = split_id;
	}
	
}

package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookingReference {
	private String action;
	private String bk_ref_nbr;
	private String var_nbr;
	private String vessel_nm;
	private String out_voy_nbr;
	private String bk_nbr_pkgs;
	private String bk_wt;
	private String bk_vol;
	private String variance_pkgs;
	private String variance_vol;
	private String variance_wt;
	private String port_of_discharge;
	private String declarant;
	private String declarant_cd;
	private String bk_create_cd;
	private String shipper_cd;
	private String shipper_addr;
	private String shipper_nm;
	private String shipper_nm_others;
	private String cargoTypeDesc;
	private String consignee_nm;
	private String consignee_addr;
	private String notify_party_nm;
	private String notify_party_addr;
	private String place_of_delivery;
	private String place_of_receipt;
	private String bl_number;
	private String last_modify_user_id;
	private String last_modify_dttm;
	private int trans_nbr;

	private int rownum;
	private String message;
	List<Comments> errorInfo;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBk_ref_nbr() {
		return bk_ref_nbr;
	}

	public void setBk_ref_nbr(String bk_ref_nbr) {
		this.bk_ref_nbr = bk_ref_nbr;
	}

	public String getVar_nbr() {
		return var_nbr;
	}

	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}

	public String getVessel_nm() {
		return vessel_nm;
	}

	public void setVessel_nm(String vessel_nm) {
		this.vessel_nm = vessel_nm;
	}

	public String getOut_voy_nbr() {
		return out_voy_nbr;
	}

	public void setOut_voy_nbr(String out_voy_nbr) {
		this.out_voy_nbr = out_voy_nbr;
	}

	public String getBk_nbr_pkgs() {
		return bk_nbr_pkgs;
	}

	public void setBk_nbr_pkgs(String bk_nbr_pkgs) {
		this.bk_nbr_pkgs = bk_nbr_pkgs;
	}

	public String getBk_wt() {
		return bk_wt;
	}

	public void setBk_wt(String bk_wt) {
		this.bk_wt = bk_wt;
	}

	public String getBk_vol() {
		return bk_vol;
	}

	public void setBk_vol(String bk_vol) {
		this.bk_vol = bk_vol;
	}

	public String getVariance_pkgs() {
		return variance_pkgs;
	}

	public void setVariance_pkgs(String variance_pkgs) {
		this.variance_pkgs = variance_pkgs;
	}

	public String getVariance_vol() {
		return variance_vol;
	}

	public void setVariance_vol(String variance_vol) {
		this.variance_vol = variance_vol;
	}

	public String getVariance_wt() {
		return variance_wt;
	}

	public void setVariance_wt(String variance_wt) {
		this.variance_wt = variance_wt;
	}

	public String getPort_of_discharge() {
		return port_of_discharge;
	}

	public void setPort_of_discharge(String port_of_discharge) {
		this.port_of_discharge = port_of_discharge;
	}

	public String getDeclarant() {
		return declarant;
	}

	public void setDeclarant(String declarant) {
		this.declarant = declarant;
	}

	public String getDeclarant_cd() {
		return declarant_cd;
	}

	public void setDeclarant_cd(String declarant_cd) {
		this.declarant_cd = declarant_cd;
	}

	public String getBk_create_cd() {
		return bk_create_cd;
	}

	public void setBk_create_cd(String bk_create_cd) {
		this.bk_create_cd = bk_create_cd;
	}

	public String getShipper_cd() {
		return shipper_cd;
	}

	public void setShipper_cd(String shipper_cd) {
		this.shipper_cd = shipper_cd;
	}

	public String getShipper_addr() {
		return shipper_addr;
	}

	public void setShipper_addr(String shipper_addr) {
		this.shipper_addr = shipper_addr;
	}

	public String getShipper_nm() {
		return shipper_nm;
	}

	public void setShipper_nm(String shipper_nm) {
		this.shipper_nm = shipper_nm;
	}

	public String getShipper_nm_others() {
		return shipper_nm_others;
	}

	public void setShipper_nm_others(String shipper_nm_others) {
		this.shipper_nm_others = shipper_nm_others;
	}

	public String getCargoTypeDesc() {
		return cargoTypeDesc;
	}

	public void setCargoTypeDesc(String cargoTypeDesc) {
		this.cargoTypeDesc = cargoTypeDesc;
	}

	public String getConsignee_nm() {
		return consignee_nm;
	}

	public void setConsignee_nm(String consignee_nm) {
		this.consignee_nm = consignee_nm;
	}

	public String getConsignee_addr() {
		return consignee_addr;
	}

	public void setConsignee_addr(String consignee_addr) {
		this.consignee_addr = consignee_addr;
	}

	public String getNotify_party_nm() {
		return notify_party_nm;
	}

	public void setNotify_party_nm(String notify_party_nm) {
		this.notify_party_nm = notify_party_nm;
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

	public String getBl_number() {
		return bl_number;
	}

	public void setBl_number(String bl_number) {
		this.bl_number = bl_number;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
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

}

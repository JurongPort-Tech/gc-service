package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.ConstantUtil;

public class CustomDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rownum;
	private String message;
	List<Comments> errorInfo;
	private String mft_seq_nbr;
	private Long mft_cntr_seq_nbr;
	private String action;
	private String vv_cd;
	private String vessel_name;
	private String voyage_no;
	private String vessel_dis_port;
	private String bl_nbr;
	private String master_bl_nbr;
	private String bl_nbr_remarks;
	private String instruction_type;
	private String ori_load_port;
	private String load_port;
	private String dis_port;
	private String dest_port;
	private String place_of_receipt_name;
	private String place_of_delivery_name;
	private String consignee;
	private String consignee_name;
	private String consignee_cd;
	private String consignee_uen;
	private String consignee_address;
	private String shipper;
	private String shipper_name;
	private String shipper_cd;
	private String shipper_uen;
	private String shipper_address;
	private String notify_party_name;
	private String notify_party_uen;
	private String notify_party_contact;
	private String notify_party_email;
	private String notify_party_address;
	private String freight_fowarder_name;
	private String freight_fowarder_uen;
	private String freight_fowarder_contact;
	private String freight_fowarder_email;
	private String freight_fowarder_address;
	private String stevedore_name;
	private String stevedore_uen;
	private String stevedore_contact;
	private String stevedore_email;
	private String stevedore_address;
	private String cargo_agent_name;
	private String cargo_agent_uen;
	private String cargo_agent_contact;
	private String cargo_agent_email;
	private String cargo_agent_address;
	private String item_no;
	private String package_type;
	private String hscode;
	private String package_quantity;
	private String weight;
	private String measurement;
	private String handling_instruction;
	private String cargo_description;
	private String mark_and_no;
	private String dg_ind;
	private String imo_class;
	private String undg_nbr;
	private String flashpoint;
	private String packing_grp;
	private String cntr_nbr;
	private String cntr_status;
	private String iso;
	private String gross_wt;
	private String seal_nbr_carrier;
	private String last_modify_user_id;
	private String last_modify_dttm;
	private String rec_status;

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

	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}

	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}
	
	public Long getMft_cntr_seq_nbr() {
		return mft_cntr_seq_nbr;
	}

	public void setMft_cntr_seq_nbr(Long mft_cntr_seq_nbr) {
		this.mft_cntr_seq_nbr = mft_cntr_seq_nbr;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getVessel_name() {
		return vessel_name;
	}

	public void setVessel_name(String vessel_name) {
		this.vessel_name = vessel_name;
	}

	public String getVoyage_no() {
		return voyage_no;
	}

	public void setVoyage_no(String voyage_no) {
		this.voyage_no = voyage_no;
	}

	public String getVessel_dis_port() {
		return vessel_dis_port;
	}

	public void setVessel_dis_port(String vessel_dis_port) {
		this.vessel_dis_port = vessel_dis_port;
	}

	public String getBl_nbr() {
		return bl_nbr;
	}

	public void setBl_nbr(String bl_nbr) {
		this.bl_nbr = bl_nbr;
	}

	public String getMaster_bl_nbr() {
		return master_bl_nbr;
	}

	public void setMaster_bl_nbr(String master_bl_nbr) {
		this.master_bl_nbr = master_bl_nbr;
	}

	public String getBl_nbr_remarks() {
		return bl_nbr_remarks;
	}

	public void setBl_nbr_remarks(String bl_nbr_remarks) {
		this.bl_nbr_remarks = bl_nbr_remarks;
	}

	public String getInstruction_type() {
		return instruction_type;
	}

	public void setInstruction_type(String instruction_type) {
		this.instruction_type = instruction_type;
	}

	public String getOri_load_port() {
		return ori_load_port;
	}

	public void setOri_load_port(String ori_load_port) {
		this.ori_load_port = ori_load_port;
	}

	public String getLoad_port() {
		return load_port;
	}

	public void setLoad_port(String load_port) {
		this.load_port = load_port;
	}

	public String getDis_port() {
		return dis_port;
	}

	public void setDis_port(String dis_port) {
		this.dis_port = dis_port;
	}

	public String getDest_port() {
		return dest_port;
	}

	public void setDest_port(String dest_port) {
		this.dest_port = dest_port;
	}

	public String getPlace_of_receipt_name() {
		return place_of_receipt_name;
	}

	public void setPlace_of_receipt_name(String place_of_receipt_name) {
		this.place_of_receipt_name = place_of_receipt_name;
	}

	public String getPlace_of_delivery_name() {
		return place_of_delivery_name;
	}

	public void setPlace_of_delivery_name(String place_of_delivery_name) {
		this.place_of_delivery_name = place_of_delivery_name;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
//		this.setConsignee_cd(consignee.substring(consignee.indexOf("(")+1, consignee.indexOf(")")));
//		
//		
//		int lastIndxBrcaker = consignee.lastIndexOf('(');
//		String consNm = consignee.substring(0, lastIndxBrcaker);
//		this.setConsignee_name(consNm);
	}

	public String getConsignee_uen() {
		return consignee_uen;
	}

	public void setConsignee_uen(String consignee_uen) {
		this.consignee_uen = consignee_uen;
	}

	public String getConsignee_address() {
		return consignee_address;
	}

	public void setConsignee_address(String consignee_address) {
		this.consignee_address = consignee_address;
	}

	public String getShipper() {
		return shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
//		this.setShipper_cd(shipper.substring(shipper.indexOf("(")+1, shipper.indexOf(")")));
//		
//		int lastIndxBrcaker = shipper.lastIndexOf('(');
//		String shipperNm = shipper.substring(0, lastIndxBrcaker);
//		this.setShipper_name(shipperNm);
	}

	public String getShipper_uen() {
		return shipper_uen;
	}

	public void setShipper_uen(String shipper_uen) {
		this.shipper_uen = shipper_uen;
	}

	public String getShipper_address() {
		return shipper_address;
	}

	public void setShipper_address(String shipper_address) {
		this.shipper_address = shipper_address;
	}

	public String getNotify_party_name() {
		return notify_party_name;
	}

	public void setNotify_party_name(String notify_party_name) {
		this.notify_party_name = notify_party_name;
	}

	public String getNotify_party_uen() {
		return notify_party_uen;
	}

	public void setNotify_party_uen(String notify_party_uen) {
		this.notify_party_uen = notify_party_uen;
	}

	public String getNotify_party_contact() {
		return notify_party_contact;
	}

	public void setNotify_party_contact(String notify_party_contact) {
		this.notify_party_contact = notify_party_contact;
	}

	public String getNotify_party_email() {
		return notify_party_email;
	}

	public void setNotify_party_email(String notify_party_email) {
		this.notify_party_email = notify_party_email;
	}

	public String getNotify_party_address() {
		return notify_party_address;
	}

	public void setNotify_party_address(String notify_party_address) {
		this.notify_party_address = notify_party_address;
	}

	public String getFreight_fowarder_name() {
		return freight_fowarder_name;
	}

	public void setFreight_fowarder_name(String freight_fowarder_name) {
		this.freight_fowarder_name = freight_fowarder_name;
	}

	public String getFreight_fowarder_uen() {
		return freight_fowarder_uen;
	}

	public void setFreight_fowarder_uen(String freight_fowarder_uen) {
		this.freight_fowarder_uen = freight_fowarder_uen;
	}

	public String getFreight_fowarder_contact() {
		return freight_fowarder_contact;
	}

	public void setFreight_fowarder_contact(String freight_fowarder_contact) {
		this.freight_fowarder_contact = freight_fowarder_contact;
	}

	public String getFreight_fowarder_email() {
		return freight_fowarder_email;
	}

	public void setFreight_fowarder_email(String freight_fowarder_email) {
		this.freight_fowarder_email = freight_fowarder_email;
	}

	public String getFreight_fowarder_address() {
		return freight_fowarder_address;
	}

	public void setFreight_fowarder_address(String freight_fowarder_address) {
		this.freight_fowarder_address = freight_fowarder_address;
	}

	public String getStevedore_name() {
		return stevedore_name;
	}

	public void setStevedore_name(String stevedore_name) {
		this.stevedore_name = stevedore_name;
	}

	public String getStevedore_uen() {
		return stevedore_uen;
	}

	public void setStevedore_uen(String stevedore_uen) {
		this.stevedore_uen = stevedore_uen;
	}

	public String getStevedore_contact() {
		return stevedore_contact;
	}

	public void setStevedore_contact(String stevedore_contact) {
		this.stevedore_contact = stevedore_contact;
	}

	public String getStevedore_email() {
		return stevedore_email;
	}

	public void setStevedore_email(String stevedore_email) {
		this.stevedore_email = stevedore_email;
	}

	public String getStevedore_address() {
		return stevedore_address;
	}

	public void setStevedore_address(String stevedore_address) {
		this.stevedore_address = stevedore_address;
	}

	public String getCargo_agent_name() {
		return cargo_agent_name;
	}

	public void setCargo_agent_name(String cargo_agent_name) {
		this.cargo_agent_name = cargo_agent_name;
	}

	public String getCargo_agent_uen() {
		return cargo_agent_uen;
	}

	public void setCargo_agent_uen(String cargo_agent_uen) {
		this.cargo_agent_uen = cargo_agent_uen;
	}

	public String getCargo_agent_contact() {
		return cargo_agent_contact;
	}

	public void setCargo_agent_contact(String cargo_agent_contact) {
		this.cargo_agent_contact = cargo_agent_contact;
	}

	public String getCargo_agent_email() {
		return cargo_agent_email;
	}

	public void setCargo_agent_email(String cargo_agent_email) {
		this.cargo_agent_email = cargo_agent_email;
	}

	public String getCargo_agent_address() {
		return cargo_agent_address;
	}

	public void setCargo_agent_address(String cargo_agent_address) {
		this.cargo_agent_address = cargo_agent_address;
	}

	public String getItem_no() {
		return item_no;
	}

	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	public String getHscode() {
		return hscode;
	}

	public void setHscode(String hscode) {
		this.hscode = hscode;
	}

	public String getPackage_quantity() {
		return package_quantity;
	}

	public void setPackage_quantity(String package_quantity) {
		this.package_quantity = package_quantity;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getHandling_instruction() {
		return handling_instruction;
	}

	public void setHandling_instruction(String handling_instruction) {
		this.handling_instruction = handling_instruction;
	}

	public String getCargo_description() {
		return cargo_description;
	}

	public void setCargo_description(String cargo_description) {
		this.cargo_description = cargo_description;
	}

	public String getMark_and_no() {
		return mark_and_no;
	}

	public void setMark_and_no(String mark_and_no) {
		this.mark_and_no = mark_and_no;
	}

	public String getDg_ind() {
		return dg_ind;
	}

	public void setDg_ind(String dg_ind) {
		this.dg_ind = dg_ind;
	}

	public String getImo_class() {
		return imo_class;
	}

	public void setImo_class(String imo_class) {
		this.imo_class = imo_class;
	}

	public String getUndg_nbr() {
		return undg_nbr;
	}

	public void setUndg_nbr(String undg_nbr) {
		this.undg_nbr = undg_nbr;
	}

	public String getFlashpoint() {
		return flashpoint;
	}

	public void setFlashpoint(String flashpoint) {
		this.flashpoint = flashpoint;
	}

	public String getPacking_grp() {
		return packing_grp;
	}

	public void setPacking_grp(String packing_grp) {
		this.packing_grp = packing_grp;
	}

	public String getCntr_nbr() {
		return cntr_nbr;
	}

	public void setCntr_nbr(String cntr_nbr) {
		this.cntr_nbr = cntr_nbr;
	}

	public String getCntr_status() {
		return cntr_status;
	}

	public void setCntr_status(String cntr_status) {
		this.cntr_status = cntr_status;
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getGross_wt() {
		return gross_wt;
	}

	public void setGross_wt(String gross_wt) {
		this.gross_wt = gross_wt;
	}

	public String getSeal_nbr_carrier() {
		return seal_nbr_carrier;
	}

	public void setSeal_nbr_carrier(String seal_nbr_carrier) {
		this.seal_nbr_carrier = seal_nbr_carrier;
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

	public String getConsignee_name() {
		return consignee_name;
	}

	public void setConsignee_name(String consignee_name) {
		this.consignee_name = consignee_name;
	}

	public String getConsignee_cd() {
		return consignee_cd;
	}

	public void setConsignee_cd(String consignee_cd) {
		this.consignee_cd = consignee_cd;
	}

	public String getShipper_name() {
		return shipper_name;
	}

	public void setShipper_name(String shipper_name) {
		this.shipper_name = shipper_name;
	}

	public String getShipper_cd() {
		return shipper_cd;
	}

	public void setShipper_cd(String shipper_cd) {
		this.shipper_cd = shipper_cd;
	}

	public void setDynamicCol(String col, String value) {
		if (col.equalsIgnoreCase("vv_cd")) {
			this.setVv_cd(value);
		} else if (col.equalsIgnoreCase("vessel_name")) {
			this.setVessel_name(value);
		} else if (col.equalsIgnoreCase("voyage_no")) {
			this.setVoyage_no(value);
		} else if (col.equalsIgnoreCase("vessel_dis_port")) {
			this.setVessel_dis_port(value);
		} else if (col.equalsIgnoreCase("bl_nbr")) {
			this.setBl_nbr(value);
		} else if (col.equalsIgnoreCase("master_bl_nbr")) {
			this.setMaster_bl_nbr(value);
		} else if (col.equalsIgnoreCase("bl_nbr_reamrks")) {
			this.setBl_nbr_remarks(value);
		} else if (col.equalsIgnoreCase("instruction_type")) {
			this.setInstruction_type(value);
		} else if (col.equalsIgnoreCase("ori_load_port")) {
			this.setOri_load_port(value);
		} else if (col.equalsIgnoreCase("load_port")) {
			this.setLoad_port(value);
		} else if (col.equalsIgnoreCase("dis_port")) {
			this.setDis_port(value);
		} else if (col.equalsIgnoreCase("dest_port")) {
			this.setDest_port(value);
		} else if (col.equalsIgnoreCase("place_of_receipt_name")) {
			this.setPlace_of_receipt_name(value);
		} else if (col.equalsIgnoreCase("place_of_delivery_name")) {
			this.setPlace_of_delivery_name(value);
		} else if (col.equalsIgnoreCase("consignee")) {
			this.setConsignee(value);
		} else if (col.equalsIgnoreCase("consignee_uen")) {
			this.setConsignee_uen(value);
		} else if (col.equalsIgnoreCase("consignee_address")) {
			this.setConsignee_address(value);
		} else if (col.equalsIgnoreCase("shipper")) {
			this.setShipper(value);
		} else if (col.equalsIgnoreCase("shipper_uen")) {
			this.setShipper_uen(value);
		} else if (col.equalsIgnoreCase("shipper_address")) {
			this.setShipper_address(value);
		} else if (col.equalsIgnoreCase("notify_party_name")) {
			this.setNotify_party_name(value);
		} else if (col.equalsIgnoreCase("notify_party_uen")) {
			this.setNotify_party_uen(value);
		} else if (col.equalsIgnoreCase("notify_party_contact")) {
			this.setNotify_party_contact(value);
		} else if (col.equalsIgnoreCase("notify_party_email")) {
			this.setNotify_party_email(value);
		} else if (col.equalsIgnoreCase("notify_party_address")) {
			this.setNotify_party_address(value);
		} else if (col.equalsIgnoreCase("freight_fowarder_name")) {
			this.setFreight_fowarder_name(value);
		} else if (col.equalsIgnoreCase("freight_fowarder_uen")) {
			this.setFreight_fowarder_uen(value);
		} else if (col.equalsIgnoreCase("freight_fowarder_contact")) {
			this.setFreight_fowarder_contact(value);
		} else if (col.equalsIgnoreCase("freight_fowarder_email")) {
			this.setFreight_fowarder_email(value);
		} else if (col.equalsIgnoreCase("freight_fowarder_address")) {
			this.setFreight_fowarder_address(value);
		} else if (col.equalsIgnoreCase("stevedore_name")) {
			this.setStevedore_name(value);
		} else if (col.equalsIgnoreCase("stevedore_uen")) {
			this.setStevedore_uen(value);
		} else if (col.equalsIgnoreCase("stevedore_contact")) {
			this.setStevedore_contact(value);
		} else if (col.equalsIgnoreCase("stevedore_email")) {
			this.setStevedore_email(value);
		} else if (col.equalsIgnoreCase("stevedore_address")) {
			this.setStevedore_address(value);
		} else if (col.equalsIgnoreCase("cargo_agent_name")) {
			this.setCargo_agent_name(value);
		} else if (col.equalsIgnoreCase("cargo_agent_uen")) {
			this.setCargo_agent_uen(value);
		} else if (col.equalsIgnoreCase("cargo_agent_contact")) {
			this.setCargo_agent_contact(value);
		} else if (col.equalsIgnoreCase("cargo_agent_email")) {
			this.setCargo_agent_email(value);
		} else if (col.equalsIgnoreCase("cargo_agent_address")) {
			this.setCargo_agent_address(value);
		} else if (col.equalsIgnoreCase("item_no")) {
			this.setItem_no(value);
		} else if (col.equalsIgnoreCase("package_type")) {
			this.setPackage_type(value);
		} else if (col.equalsIgnoreCase("hscode")) {
			this.setHscode(value);
		} else if (col.equalsIgnoreCase("package_quantity")) {
			this.setPackage_quantity(value);
		} else if (col.equalsIgnoreCase("weight")) {
			this.setWeight(value);
		} else if (col.equalsIgnoreCase("measurement")) {
			this.setMeasurement(value);
		} else if (col.equalsIgnoreCase("handling_instruction")) {
			this.setHandling_instruction(value);
		} else if (col.equalsIgnoreCase("cargo_description")) {
			this.setCargo_description(value);
		} else if (col.equalsIgnoreCase("mark_and_no")) {
			this.setMark_and_no(value);
		} else if (col.equalsIgnoreCase("dg_ind")) {
			this.setDg_ind(value);
		} else if (col.equalsIgnoreCase("imo_class")) {
			this.setImo_class(value);
		} else if (col.equalsIgnoreCase("undg_nbr")) {
			this.setUndg_nbr(value);
		} else if (col.equalsIgnoreCase("flashpoint")) {
			this.setFlashpoint(value);
		} else if (col.equalsIgnoreCase("packing_grp")) {
			this.setPacking_grp(value);
		} else if (col.equalsIgnoreCase("cntr_nbr")) {
			this.setCntr_nbr(value);
		} else if (col.equalsIgnoreCase("cntr_status")) {
			this.setCntr_status(value);
		} else if (col.equalsIgnoreCase("iso")) {
			this.setIso(value);
		} else if (col.equalsIgnoreCase("gross_wt")) {
			this.setGross_wt(value);
		} else if (col.equalsIgnoreCase("seal_nbr_carrier")) {
			this.setSeal_nbr_carrier(value);
		} else if (col.equalsIgnoreCase("last_modify_user_id")) {
			this.setLast_modify_user_id(value);
		} else if (col.equalsIgnoreCase("last_modify_dttm")) {
			this.setLast_modify_dttm(value);
		}
	}

	public String getDynamicCol(String col) {
		if (col.equalsIgnoreCase("vv_cd")) {
			return this.getVv_cd();
		} else if (col.equalsIgnoreCase("vessel_name")) {
			return this.getVessel_name();
		} else if (col.equalsIgnoreCase("inward_voyage_no")) {
			return this.getVoyage_no();
		} else if (col.equalsIgnoreCase("vessel_dis_port")) {
			return this.getVessel_dis_port();
		} else if (col.equalsIgnoreCase("bl_nbr")) {
			return this.getBl_nbr();
		} else if (col.equalsIgnoreCase("master_bl_nbr")) {
			return this.getMaster_bl_nbr();
		} else if (col.equalsIgnoreCase("bl_nbr_reamrks")) {
			return this.getBl_nbr_remarks();
		} else if (col.equalsIgnoreCase("instruction_type")) {
			return this.getInstruction_type();
		} else if (col.equalsIgnoreCase("ori_load_port")) {
			return this.getOri_load_port();
		} else if (col.equalsIgnoreCase("load_port")) {
			return this.getLoad_port();
		} else if (col.equalsIgnoreCase("dis_port")) {
			return this.getDis_port();
		} else if (col.equalsIgnoreCase("dest_port")) {
			return this.getDest_port();
		} else if (col.equalsIgnoreCase("place_of_receipt_name")) {
			return this.getPlace_of_receipt_name();
		} else if (col.equalsIgnoreCase("place_of_delivery_name")) {
			return this.getPlace_of_delivery_name();
		} else if (col.equalsIgnoreCase("consignee")) {
			return this.getConsignee_name();
		} else if (col.equalsIgnoreCase("consignee_uen")) {
			return this.getConsignee_uen();
		} else if (col.equalsIgnoreCase("consignee_address")) {
			return this.getConsignee_address();
		} else if (col.equalsIgnoreCase("shipper")) {
			return this.getShipper_name();		
		} else if (col.equalsIgnoreCase("shipper_uen")) {
			return this.getShipper_uen();
		} else if (col.equalsIgnoreCase("shipper_address")) {
			return this.getShipper_address();
		} else if (col.equalsIgnoreCase("notify_party_name")) {
			return this.getNotify_party_name();
		} else if (col.equalsIgnoreCase("notify_party_uen")) {
			return this.getNotify_party_uen();
		} else if (col.equalsIgnoreCase("notify_party_contact")) {
			return this.getNotify_party_contact();
		} else if (col.equalsIgnoreCase("notify_party_email")) {
			return this.getNotify_party_email();
		} else if (col.equalsIgnoreCase("notify_party_address")) {
			return this.getNotify_party_address();
		} else if (col.equalsIgnoreCase("freight_fowarder_name")) {
			return this.getFreight_fowarder_name();
		} else if (col.equalsIgnoreCase("freight_fowarder_uen")) {
			return this.getFreight_fowarder_uen();
		} else if (col.equalsIgnoreCase("freight_fowarder_contact")) {
			return this.getFreight_fowarder_contact();
		} else if (col.equalsIgnoreCase("freight_fowarder_email")) {
			return this.getFreight_fowarder_email();
		} else if (col.equalsIgnoreCase("freight_fowarder_address")) {
			return this.getFreight_fowarder_address();
		} else if (col.equalsIgnoreCase("stevedore_name")) {
			return this.getStevedore_name();
		} else if (col.equalsIgnoreCase("stevedore_uen")) {
			return this.getStevedore_uen();
		} else if (col.equalsIgnoreCase("stevedore_contact")) {
			return this.getStevedore_contact();
		} else if (col.equalsIgnoreCase("stevedore_email")) {
			return this.getStevedore_email();
		} else if (col.equalsIgnoreCase("stevedore_address")) {
			return this.getStevedore_address();
		} else if (col.equalsIgnoreCase("cargo_agent_name")) {
			return this.getCargo_agent_name();
		} else if (col.equalsIgnoreCase("cargo_agent_uen")) {
			return this.getCargo_agent_uen();
		} else if (col.equalsIgnoreCase("cargo_agent_contact")) {
			return this.getCargo_agent_contact();
		} else if (col.equalsIgnoreCase("cargo_agent_email")) {
			return this.getCargo_agent_email();
		} else if (col.equalsIgnoreCase("cargo_agent_address")) {
			return this.getCargo_agent_address();
		} else if (col.equalsIgnoreCase("item_no")) {
			return this.getItem_no();
		} else if (col.equalsIgnoreCase("package_type")) {
			return this.getPackage_type();
		} else if (col.equalsIgnoreCase("hscode")) {
			return this.getHscode();
		} else if (col.equalsIgnoreCase("package_quantity")) {
			return this.getPackage_quantity();
		} else if (col.equalsIgnoreCase("weight")) {
			return this.getWeight();
		} else if (col.equalsIgnoreCase("measurement")) {
			return this.getMeasurement();
		} else if (col.equalsIgnoreCase("handling_instruction")) {
			return this.getHandling_instruction();
		} else if (col.equalsIgnoreCase("cargo_description")) {
			return this.getCargo_description();
		} else if (col.equalsIgnoreCase("mark_and_no")) {
			return this.getMark_and_no();
		} else if (col.equalsIgnoreCase("dg_ind")) {
			return this.getDg_ind();
		} else if (col.equalsIgnoreCase("imo_class")) {
			return this.getImo_class();
		} else if (col.equalsIgnoreCase("undg_nbr")) {
			return this.getUndg_nbr();
		} else if (col.equalsIgnoreCase("flashpoint")) {
			return this.getFlashpoint();
		} else if (col.equalsIgnoreCase("packing_grp")) {
			return this.getPacking_grp();
		} else if (col.equalsIgnoreCase("cntr_nbr")) {
			return this.getCntr_nbr();
		} else if (col.equalsIgnoreCase("cntr_status")) {
			return this.getCntr_status();
		} else if (col.equalsIgnoreCase("iso")) {
			return this.getIso();
		} else if (col.equalsIgnoreCase("gross_wt")) {
			return this.getGross_wt();
		} else if (col.equalsIgnoreCase("seal_nbr_carrier")) {
			return this.getSeal_nbr_carrier();
		} else if (col.equalsIgnoreCase("last_modify_user_id")) {
			return this.getLast_modify_user_id();
		} else if (col.equalsIgnoreCase("last_modify_dttm")) {
			return this.getLast_modify_dttm();
		} else if (col.equalsIgnoreCase("action")) {
			return this.getAction();
		}
		return "";
	}


	public String getRec_status() {
		return rec_status;
	}

	public void setRec_status(String rec_status) {
		this.rec_status = rec_status;
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

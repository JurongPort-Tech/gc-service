package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.TpaConstants;


public class TpaVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String misc_seq_nbr;
	private String ref_nbr;
	private String app_status;
	private String app_status_nm;
	private String cust_cd;
	private String co_nm;
	private String bill_nbr;
	private String acct_nbr;
	private String con_person;
	private String con_tel;
	private String con_email;
	private String void_remarks;
	private int no_of_vehicles;
	private String veh_chas_no;
	private String vv_cd;
	private String vsl_nm;
	private String in_voy_nbr;
	private String out_voy_nbr;
	private String atb_dttm;
	private String atu_dttm;
	private String actual_from_date_time;
	private String actual_to_date_time;
	private String from_date_time;
	private String to_date_time;
	private String tpa_from_date_time;
	private String tpa_to_date_time;
	private double duration_of_stay;
	private double nbr_of_blocks;
	private double actual_nbr_of_blocks;
	private double nbr_hour;
	private double actual_nbr_hour;
	private String trailer_type;
	private int trailer_size;
	private String park_reason;
	private String park_reason_cd;
	private String cargo_type;
	private String last_updated_by;
	private String last_modify_user_id;
	private String last_modify_date_time;
	private Timestamp last_modify_dttm;
	private String app_date_time;
	private String reason_for_application;
	private String create_user_id;
	private Timestamp create_dttm;
	private String submit_user_id;
	private Timestamp submit_dttm;
	private String app_type;
	private String approve_reject_remarks;
	// HaiTTH1 added 20/11/2013
	private String assigned_area_slot;

	public String getMisc_seq_nbr() {
		return misc_seq_nbr;
	}

	public void setMisc_seq_nbr(String misc_seq_nbr) {
		this.misc_seq_nbr = misc_seq_nbr;
	}

	public String getRef_nbr() {
		return ref_nbr;
	}

	public void setRef_nbr(String ref_nbr) {
		this.ref_nbr = ref_nbr;
	}

	public String getApp_status() {
		return app_status;
	}

	public void setApp_status(String app_status) {
		this.app_status = app_status;
	}

	public String getApp_status_nm() {
		String appStatus = this.getApp_status();
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_DRAFT)) {
			app_status_nm = "Draft";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_SUBMITTED)) {
			app_status_nm = "Submitted";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_ACCEPTED)) {
			app_status_nm = "Accepted";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_REJECTED)) {
			app_status_nm = "Rejected";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_VOID)) {
			app_status_nm = "Void";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_CLOSE_FOR_BILLING)) {
			app_status_nm = "Pending Billing";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_BILLED)) {
			app_status_nm = "Billed";
		}
		return app_status_nm;
	}

	public void setApp_status_nm(String app_status_nm) {
		this.app_status_nm = app_status_nm;
	}

	public String getCust_cd() {
		return cust_cd;
	}

	public void setCust_cd(String cust_cd) {
		this.cust_cd = cust_cd;
	}

	public String getCo_nm() {
		return co_nm;
	}

	public void setCo_nm(String co_nm) {
		this.co_nm = co_nm;
	}

	public String getBill_nbr() {
		return bill_nbr;
	}

	public void setBill_nbr(String bill_nbr) {
		this.bill_nbr = bill_nbr;
	}

	public String getAcct_nbr() {
		return acct_nbr;
	}

	public void setAcct_nbr(String acct_nbr) {
		this.acct_nbr = acct_nbr;
	}

	public String getCon_person() {
		return con_person;
	}

	public void setCon_person(String con_person) {
		this.con_person = con_person;
	}

	public String getCon_tel() {
		return con_tel;
	}

	public void setCon_tel(String con_tel) {
		this.con_tel = con_tel;
	}

	public String getCon_email() {
		return con_email;
	}

	public void setCon_email(String con_email) {
		this.con_email = con_email;
	}

	public String getVoid_remarks() {
		return void_remarks;
	}

	public void setVoid_remarks(String void_remarks) {
		this.void_remarks = void_remarks;
	}

	public int getNo_of_vehicles() {
		return no_of_vehicles;
	}

	public void setNo_of_vehicles(int no_of_vehicles) {
		this.no_of_vehicles = no_of_vehicles;
	}

	public String getVeh_chas_no() {
		return veh_chas_no;
	}

	public void setVeh_chas_no(String veh_chas_no) {
		this.veh_chas_no = veh_chas_no;
	}

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getVsl_nm() {
		return vsl_nm;
	}

	public void setVsl_nm(String vsl_nm) {
		this.vsl_nm = vsl_nm;
	}

	public String getIn_voy_nbr() {
		return in_voy_nbr;
	}

	public void setIn_voy_nbr(String in_voy_nbr) {
		this.in_voy_nbr = in_voy_nbr;
	}

	public String getOut_voy_nbr() {
		return out_voy_nbr;
	}

	public void setOut_voy_nbr(String out_voy_nbr) {
		this.out_voy_nbr = out_voy_nbr;
	}

	public String getAtb_dttm() {
		return atb_dttm;
	}

	public void setAtb_dttm(String atb_dttm) {
		this.atb_dttm = atb_dttm;
	}

	public String getAtu_dttm() {
		return atu_dttm;
	}

	public void setAtu_dttm(String atu_dttm) {
		this.atu_dttm = atu_dttm;
	}

	public String getApp_date_time() {
		return app_date_time;
	}

	public void setApp_date_time(String app_date_time) {
		this.app_date_time = app_date_time;
	}

	public String getActual_from_date_time() {
		return actual_from_date_time;
	}

	public void setActual_from_date_time(String actual_from_date_time) {
		this.actual_from_date_time = actual_from_date_time;
	}

	public String getActual_to_date_time() {
		return actual_to_date_time;
	}

	public void setActual_to_date_time(String actual_to_date_time) {
		this.actual_to_date_time = actual_to_date_time;
	}

	public String getFrom_date_time() {
		return from_date_time;
	}

	public void setFrom_date_time(String from_date_time) {
		this.from_date_time = from_date_time;
	}

	public String getTo_date_time() {
		return to_date_time;
	}

	public void setTo_date_time(String to_date_time) {
		this.to_date_time = to_date_time;
	}

	public String getTpa_from_date_time() {
		return tpa_from_date_time;
	}

	public void setTpa_from_date_time(String tpa_from_date_time) {
		this.tpa_from_date_time = tpa_from_date_time;
	}

	public String getTpa_to_date_time() {
		return tpa_to_date_time;
	}

	public void setTpa_to_date_time(String tpa_to_date_time) {
		this.tpa_to_date_time = tpa_to_date_time;
	}

	public double getDuration_of_stay() {
		return duration_of_stay;
	}

	public void setDuration_of_stay(double duration_of_stay) {
		this.duration_of_stay = duration_of_stay;
	}

	public double getNbr_of_blocks() {
		return nbr_of_blocks;
	}

	public void setNbr_of_blocks(double nbr_of_blocks) {
		this.nbr_of_blocks = nbr_of_blocks;
	}

	public double getActual_nbr_of_blocks() {
		return actual_nbr_of_blocks;
	}

	public void setActual_nbr_of_blocks(double actual_nbr_of_blocks) {
		this.actual_nbr_of_blocks = actual_nbr_of_blocks;
	}

	public double getNbr_hour() {
		return nbr_hour;
	}

	public void setNbr_hour(double nbr_hour) {
		this.nbr_hour = nbr_hour;
	}

	public double getActual_nbr_hour() {
		return actual_nbr_hour;
	}

	public void setActual_nbr_hour(double actual_nbr_hour) {
		this.actual_nbr_hour = actual_nbr_hour;
	}

	public String getTrailer_type() {
		return trailer_type;
	}

	public void setTrailer_type(String trailer_type) {
		this.trailer_type = trailer_type;
	}

	public int getTrailer_size() {
		return trailer_size;
	}

	public void setTrailer_size(int trailer_size) {
		this.trailer_size = trailer_size;
	}

	public String getPark_reason() {
		return park_reason;
	}

	public void setPark_reason(String park_reason) {
		this.park_reason = park_reason;
	}

	public String getPark_reason_cd() {
		return park_reason_cd;
	}

	public void setPark_reason_cd(String park_reason_cd) {
		this.park_reason_cd = park_reason_cd;
	}

	public String getCargo_type() {
		return cargo_type;
	}

	public void setCargo_type(String cargo_type) {
		this.cargo_type = cargo_type;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public String getLast_modify_date_time() {
		return last_modify_date_time;
	}

	public void setLast_modify_date_time(String last_modify_date_time) {
		this.last_modify_date_time = last_modify_date_time;
	}

	public Timestamp getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(Timestamp last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	public String getReason_for_application() {
		return reason_for_application;
	}

	public void setReason_for_application(String reason_for_application) {
		this.reason_for_application = reason_for_application;
	}

	/**
	 * @return the create_user_id
	 */
	public String getCreate_user_id() {
		return create_user_id;
	}

	/**
	 * @param create_user_id the create_user_id to set
	 */
	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	/**
	 * @return the create_dttm
	 */
	public Timestamp getCreate_dttm() {
		return create_dttm;
	}

	/**
	 * @param create_dttm the create_dttm to set
	 */
	public void setCreate_dttm(Timestamp create_dttm) {
		this.create_dttm = create_dttm;
	}

	/**
	 * @return the submit_user_id
	 */
	public String getSubmit_user_id() {
		return submit_user_id;
	}

	/**
	 * @param submit_user_id the submit_user_id to set
	 */
	public void setSubmit_user_id(String submit_user_id) {
		this.submit_user_id = submit_user_id;
	}

	/**
	 * @return the submit_dttm
	 */
	public Timestamp getSubmit_dttm() {
		return submit_dttm;
	}

	/**
	 * @param submit_dttm the submit_dttm to set
	 */
	public void setSubmit_dttm(Timestamp submit_dttm) {
		this.submit_dttm = submit_dttm;
	}

	/**
	 * @return the app_type
	 */
	public String getApp_type() {
		return app_type;
	}

	/**
	 * @param app_type the app_type to set
	 */
	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	/**
	 * @return the approve_reject_remarks
	 */
	public String getApprove_reject_remarks() {
		return approve_reject_remarks;
	}

	/**
	 * @param approve_reject_remarks the approve_reject_remarks to set
	 */
	public void setApprove_reject_remarks(String approve_reject_remarks) {
		this.approve_reject_remarks = approve_reject_remarks;
	}

	/**
	 * @return the assigned_area_slot
	 */
	public String getAssigned_area_slot() {
		return assigned_area_slot;
	}

	/**
	 * @param assignedAreaSlot the assigned_area_slot to set
	 */
	public void setAssigned_area_slot(String assignedAreaSlot) {
		assigned_area_slot = assignedAreaSlot;
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

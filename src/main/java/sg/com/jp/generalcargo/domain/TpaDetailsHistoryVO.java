package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.TpaConstants;


public class TpaDetailsHistoryVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String misc_seq_nbr;
	private String audit_date_time;
	private String app_status;
	private String app_status_nm;
	private String approve_remarks;
	private String extend_remarks;
	private String void_remarks;
	private String close_for_bill_remarks;
	private Timestamp extend_dttm;
	private String last_modify_user_id;
	private String last_updated_by;
	private String log_info;

	public String getMisc_seq_nbr() {
		return misc_seq_nbr;
	}

	public void setMisc_seq_nbr(String misc_seq_nbr) {
		this.misc_seq_nbr = misc_seq_nbr;
	}

	public String getAudit_date_time() {
		return audit_date_time;
	}

	public void setAudit_date_time(String audit_date_time) {
		this.audit_date_time = audit_date_time;
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
			app_status_nm = "Approved";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_REJECTED)) {
			app_status_nm = "Rejected";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_VOID)) {
			app_status_nm = "Void";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_CLOSE_FOR_BILLING)) {
			app_status_nm = "Close for Billing";
		}
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_BILLED)) {
			app_status_nm = "Billed";
		}
		return app_status_nm;
	}

	public void setApp_status_nm(String app_status_nm) {
		this.app_status_nm = app_status_nm;
	}

	public String getApprove_remarks() {
		return approve_remarks;
	}

	public void setApprove_remarks(String approve_remarks) {
		this.approve_remarks = approve_remarks;
	}

	public String getExtend_remarks() {
		return extend_remarks;
	}

	public void setExtend_remarks(String extend_remarks) {
		this.extend_remarks = extend_remarks;
	}

	public String getVoid_remarks() {
		return void_remarks;
	}

	public void setVoid_remarks(String void_remarks) {
		this.void_remarks = void_remarks;
	}

	public String getClose_for_bill_remarks() {
		return close_for_bill_remarks;
	}

	public void setClose_for_bill_remarks(String close_for_bill_remarks) {
		this.close_for_bill_remarks = close_for_bill_remarks;
	}

	public Timestamp getExtend_dttm() {
		return extend_dttm;
	}

	public void setExtend_dttm(Timestamp extend_dttm) {
		this.extend_dttm = extend_dttm;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getLog_info() {
		StringBuilder sb = new StringBuilder();
		sb.append("Updated on ");
		sb.append(this.getAudit_date_time());
		sb.append(" by ");
		sb.append(this.getLast_updated_by());
		sb.append(". Status: ");
		sb.append(this.getApp_status_nm());

		String appStatus = this.getApp_status();
		if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_ACCEPTED)
				|| this.getApp_status().equalsIgnoreCase(TpaConstants.TPA_STATUS_REJECTED)) {
			sb.append(". Remarks: ");
			sb.append(this.getApprove_remarks());
		} else if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_VOID)) {
			sb.append(". Remarks: ");
			sb.append(this.getVoid_remarks());
		} else if (appStatus.equalsIgnoreCase(TpaConstants.TPA_STATUS_CLOSE_FOR_BILLING)) {
			sb.append(". Remarks: ");
			sb.append(this.getClose_for_bill_remarks());
		} else if (this.getExtend_dttm() != null) {
			sb.append(". Remarks: ");
			sb.append(this.getExtend_remarks());
		}

		return sb.toString();
	}

	public void setLog_info(String log_info) {
		this.log_info = log_info;
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

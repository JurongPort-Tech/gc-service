package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomDetailsActionTrailDetails {
	private Long seq_id;
	private String actual_file_name;
	private String vv_cd;
	private String assigned_file_name;
	private String output_file_name;
	private String last_modified_user_id;
	private String last_modified_dttm;
	private String remarks;
	private String totalLineProcessed;
	private String totalSuccess;
	private String totalFail;
	// START CUSCAR - NS MARCH 25
	private String totalRecordRcv = "0";
	private String totalError = "0";
	private String totalCreated = "0";
	private String totalUpdated = "0";
	private String totalDeleted = "0";
	// END CUSCAR - NS MARCH 25

	
	public String getLast_modified_dttm() {
		return last_modified_dttm;
	}

	public String getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(String totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public String getTotalFail() {
		return totalFail;
	}

	public void setTotalFail(String totalFail) {
		this.totalFail = totalFail;
	}

	public void setLast_modified_dttm(String last_modified_dttm) {
		this.last_modified_dttm = last_modified_dttm;
	}

	public String getActual_file_name() {
		return actual_file_name;
	}

	public void setActual_file_name(String actual_file_name) {
		this.actual_file_name = actual_file_name;
	}

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getAssigned_file_name() {
		return assigned_file_name;
	}

	public void setAssigned_file_name(String assigned_file_name) {
		this.assigned_file_name = assigned_file_name;
	}

	public String getOutput_file_name() {
		return output_file_name;
	}

	public void setOutput_file_name(String output_file_name) {
		this.output_file_name = output_file_name;
	}

	public String getLast_modified_user_id() {
		return last_modified_user_id;
	}

	public void setLast_modified_user_id(String last_modified_user_id) {
		this.last_modified_user_id = last_modified_user_id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	 
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Long getSeq_id() {
		return seq_id;
	}

	public void setSeq_id(Long seq_id) {
		this.seq_id = seq_id;
	}

	public String getTotalLineProcessed() {
		return totalLineProcessed;
	}

	public void setTotalLineProcessed(String totalLineProcessed) {
		this.totalLineProcessed = totalLineProcessed;
	}

	// START CUSCAR - NS MARCH 25
	public String getTotalRecordRcv() {
		return totalRecordRcv;
	}

	public void setTotalRecordRcv(String totalRecordRcv) {
		this.totalRecordRcv = totalRecordRcv;
	}

	public String getTotalError() {
		return totalError;
	}

	public void setTotalError(String totalError) {
		this.totalError = totalError;
	}

	public String getTotalCreated() {
		return totalCreated;
	}

	public void setTotalCreated(String totalCreated) {
		this.totalCreated = totalCreated;
	}

	public String getTotalUpdated() {
		return totalUpdated;
	}

	public void setTotalUpdated(String totalUpdated) {
		this.totalUpdated = totalUpdated;
	}

	public String getTotalDeleted() {
		return totalDeleted;
	}

	public void setTotalDeleted(String totalDeleted) {
		this.totalDeleted = totalDeleted;
	}

	// END CUSCAR - NS MARCH 25
	
}

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomDetailsActionTrail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long custom_details_act_trl_id;
	private String vv_cd;
	private String type_cd;
	private String type;
	private String last_modify_user_id;
	private String remarks;
	private String last_modify_dttm;

	public Long getCustom_details_act_trl_id() {
		return custom_details_act_trl_id;
	}

	public void setCustom_details_act_trl_id(Long custom_details_act_trl_id) {
		this.custom_details_act_trl_id = custom_details_act_trl_id;
	}

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getType_cd() {
		return type_cd;
	}

	public void setType_cd(String type_cd) {
		this.type_cd = type_cd;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(String last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

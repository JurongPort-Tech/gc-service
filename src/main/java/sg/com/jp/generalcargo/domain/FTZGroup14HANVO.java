package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 HAN HANDLING INSTRUCTION
public class FTZGroup14HANVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// handling instruction
	private String handl_instr_cdd;
	private String cd_list_qual;
	private String cd_list_resp_agency_cdd;
	private String handl_instr;
	
	// hazardous material
	private String hzrd_mtrl_class_cd_id;
	private String hzrd_cd_list_qual;
	private String hzrd_cd_list_resp_agency_cdd;
	private String hzrd_mtrl_cat_nm;
	
	public String getHandl_instr_cdd() {
		return handl_instr_cdd;
	}
	public void setHandl_instr_cdd(String handl_instr_cdd) {
		this.handl_instr_cdd = handl_instr_cdd;
	}
	public String getCd_list_qual() {
		return cd_list_qual;
	}
	public void setCd_list_qual(String cd_list_qual) {
		this.cd_list_qual = cd_list_qual;
	}
	public String getCd_list_resp_agency_cdd() {
		return cd_list_resp_agency_cdd;
	}
	public void setCd_list_resp_agency_cdd(String cd_list_resp_agency_cdd) {
		this.cd_list_resp_agency_cdd = cd_list_resp_agency_cdd;
	}
	public String getHandl_instr() {
		return handl_instr;
	}
	public void setHandl_instr(String handl_instr) {
		this.handl_instr = handl_instr;
	}
	public String getHzrd_mtrl_class_cd_id() {
		return hzrd_mtrl_class_cd_id;
	}
	public void setHzrd_mtrl_class_cd_id(String hzrd_mtrl_class_cd_id) {
		this.hzrd_mtrl_class_cd_id = hzrd_mtrl_class_cd_id;
	}
	public String getHzrd_cd_list_qual() {
		return hzrd_cd_list_qual;
	}
	public void setHzrd_cd_list_qual(String hzrd_cd_list_qual) {
		this.hzrd_cd_list_qual = hzrd_cd_list_qual;
	}
	public String getHzrd_cd_list_resp_agency_cdd() {
		return hzrd_cd_list_resp_agency_cdd;
	}
	public void setHzrd_cd_list_resp_agency_cdd(String hzrd_cd_list_resp_agency_cdd) {
		this.hzrd_cd_list_resp_agency_cdd = hzrd_cd_list_resp_agency_cdd;
	}
	public String getHzrd_mtrl_cat_nm() {
		return hzrd_mtrl_cat_nm;
	}
	public void setHzrd_mtrl_cat_nm(String hzrd_mtrl_cat_nm) {
		this.hzrd_mtrl_cat_nm = hzrd_mtrl_cat_nm;
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

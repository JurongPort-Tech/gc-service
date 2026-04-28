package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 8 GEI PROCESSING INFORMATION
public class FTZGroup8GEIVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// processing information code qualifier
	private String proc_info_cd_qual;
	
	// processing indicator
	private String proc_ind_desc_cd;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String proc_ind_desc;
	
	public String getProc_info_cd_qual() {
		return proc_info_cd_qual;
	}
	public void setProc_info_cd_qual(String proc_info_cd_qual) {
		this.proc_info_cd_qual = proc_info_cd_qual;
	}
	public String getProc_ind_desc_cd() {
		return proc_ind_desc_cd;
	}
	public void setProc_ind_desc_cd(String proc_ind_desc_cd) {
		this.proc_ind_desc_cd = proc_ind_desc_cd;
	}
	public String getCd_list_id_cd() {
		return cd_list_id_cd;
	}
	public void setCd_list_id_cd(String cd_list_id_cd) {
		this.cd_list_id_cd = cd_list_id_cd;
	}
	public String getCd_list_resp_agency_cd() {
		return cd_list_resp_agency_cd;
	}
	public void setCd_list_resp_agency_cd(String cd_list_resp_agency_cd) {
		this.cd_list_resp_agency_cd = cd_list_resp_agency_cd;
	}
	public String getProc_ind_desc() {
		return proc_ind_desc;
	}
	public void setProc_ind_desc(String proc_ind_desc) {
		this.proc_ind_desc = proc_ind_desc;
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

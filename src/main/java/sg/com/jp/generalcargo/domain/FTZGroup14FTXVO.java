package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 FTX FREE TEXT
public class FTZGroup14FTXVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// text subject code
	private String txt_subj_cd_qual;
	
	// text function, coded
	private String txt_func_cdd;
	
	// text reference
	private String free_txt_val_cd;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	
	// text literal
	private String free_txt_val;
	
	// language name code
	private String lang_nm_cd;
	
	// free etxt format code
	private String free_txt_fmt_cd;

	public String getTxt_subj_cd_qual() {
		return txt_subj_cd_qual;
	}

	public void setTxt_subj_cd_qual(String txt_subj_cd_qual) {
		this.txt_subj_cd_qual = txt_subj_cd_qual;
	}

	public String getTxt_func_cdd() {
		return txt_func_cdd;
	}

	public void setTxt_func_cdd(String txt_func_cdd) {
		this.txt_func_cdd = txt_func_cdd;
	}

	public String getFree_txt_val_cd() {
		return free_txt_val_cd;
	}

	public void setFree_txt_val_cd(String free_txt_val_cd) {
		this.free_txt_val_cd = free_txt_val_cd;
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

	public String getFree_txt_val() {
		return free_txt_val;
	}

	public void setFree_txt_val(String free_txt_val) {
		this.free_txt_val = free_txt_val;
	}

	public String getLang_nm_cd() {
		return lang_nm_cd;
	}

	public void setLang_nm_cd(String lang_nm_cd) {
		this.lang_nm_cd = lang_nm_cd;
	}

	public String getFree_txt_fmt_cd() {
		return free_txt_fmt_cd;
	}

	public void setFree_txt_fmt_cd(String free_txt_fmt_cd) {
		this.free_txt_fmt_cd = free_txt_fmt_cd;
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

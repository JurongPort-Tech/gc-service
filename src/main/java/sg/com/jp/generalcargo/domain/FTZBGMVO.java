package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// BGM BEGINNING OF MESSAGE
public class FTZBGMVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// document/message name
	private String doc_nm_cd;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String doc_nm;
	
	// document/message identification
	private String doc_id;
	private String ver_id;
	private String rev_id;
	
	// message function code
	private String msg_func_cd;
	
	// response type code
	private String resp_type_cd;

	public String getDoc_nm_cd() {
		return doc_nm_cd;
	}

	public void setDoc_nm_cd(String doc_nm_cd) {
		this.doc_nm_cd = doc_nm_cd;
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

	public String getDoc_nm() {
		return doc_nm;
	}

	public void setDoc_nm(String doc_nm) {
		this.doc_nm = doc_nm;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String getVer_id() {
		return ver_id;
	}

	public void setVer_id(String ver_id) {
		this.ver_id = ver_id;
	}

	public String getRev_id() {
		return rev_id;
	}

	public void setRev_id(String rev_id) {
		this.rev_id = rev_id;
	}

	public String getMsg_func_cd() {
		return msg_func_cd;
	}

	public void setMsg_func_cd(String msg_func_cd) {
		this.msg_func_cd = msg_func_cd;
	}

	public String getResp_type_cd() {
		return resp_type_cd;
	}

	public void setResp_type_cd(String resp_type_cd) {
		this.resp_type_cd = resp_type_cd;
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

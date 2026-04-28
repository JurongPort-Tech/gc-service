package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 8 RFF REFERENCE
public class FTZGroup8RFF implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// reference
	private String ref_func_cd_qual;
	private String ref_id;
	private String line_nbr;
	private String ver_id;
	private String rev_id;
	
	public String getRef_func_cd_qual() {
		return ref_func_cd_qual;
	}
	public void setRef_func_cd_qual(String ref_func_cd_qual) {
		this.ref_func_cd_qual = ref_func_cd_qual;
	}
	public String getRef_id() {
		return ref_id;
	}
	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}
	public String getLine_nbr() {
		return line_nbr;
	}
	public void setLine_nbr(String line_nbr) {
		this.line_nbr = line_nbr;
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
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// DTM DATE/TIME/PERIOD
public class FTZDTMVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// date/time/period
	private String dtp_func_cd_qual;
	private String dtp_value;
	private String dtp_fmt_cd;
	
	public String getDtp_func_cd_qual() {
		return dtp_func_cd_qual;
	}
	public void setDtp_func_cd_qual(String dtp_func_cd_qual) {
		this.dtp_func_cd_qual = dtp_func_cd_qual;
	}
	public String getDtp_value() {
		return dtp_value;
	}
	public void setDtp_value(String dtp_value) {
		this.dtp_value = dtp_value;
	}
	public String getDtp_fmt_cd() {
		return dtp_fmt_cd;
	}
	public void setDtp_fmt_cd(String dtp_fmt_cd) {
		this.dtp_fmt_cd = dtp_fmt_cd;
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

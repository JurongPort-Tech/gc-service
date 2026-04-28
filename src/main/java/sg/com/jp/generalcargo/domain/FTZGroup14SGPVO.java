package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 SGP SPLIT GOODS PLACEMENT
public class FTZGroup14SGPVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// equipment identification
	private String eqp_idf;
	private String blNbr;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String ctry_id;
	
	// packaging quantity
	private int pkg_qty;

	public String getEqp_idf() {
		return eqp_idf;
	}

	public void setEqp_idf(String eqp_idf) {
		this.eqp_idf = eqp_idf;
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

	public String getCtry_id() {
		return ctry_id;
	}

	public void setCtry_id(String ctry_id) {
		this.ctry_id = ctry_id;
	}

	public int getPkg_qty() {
		return pkg_qty;
	}

	public void setPkg_qty(int pkg_qty) {
		this.pkg_qty = pkg_qty;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getBlNbr() {
		return blNbr;
	}

	public void setBlNbr(String blNbr) {
		this.blNbr = blNbr;
	}
	
}

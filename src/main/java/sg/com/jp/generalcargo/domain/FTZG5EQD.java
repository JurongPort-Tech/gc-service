package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 5 EQD EQUIPMENT DETAILS
public class FTZG5EQD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// equipment qualifier
	private String eqp_qual;
	
	// equipment identification
	private String eqp_id_nbr;
	private String cd_list_qual;
	private String cd_list_resp_agency_cdd;
	private String cntry_cdd;
	
	// equipment size and type
	private String eqp_sz_type_id;
	private String sz_type_cd_list_qual;
	private String sz_type_cd_list_resp_agency_cdd;
	private String eqp_sz_type;
	
	// equipment supplier, coded
	private String eqp_suppl_cdd;
	
	// equipment status, coded
	private String eqp_status_cdd;
	
	// full/empty indicator, coded
	private String full_empty_ind_cdd;

	public String getEqp_qual() {
		return eqp_qual;
	}

	public void setEqp_qual(String eqp_qual) {
		this.eqp_qual = eqp_qual;
	}

	public String getEqp_id_nbr() {
		return eqp_id_nbr;
	}

	public void setEqp_id_nbr(String eqp_id_nbr) {
		this.eqp_id_nbr = eqp_id_nbr;
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

	public String getCntry_cdd() {
		return cntry_cdd;
	}

	public void setCntry_cdd(String cntry_cdd) {
		this.cntry_cdd = cntry_cdd;
	}

	public String getEqp_sz_type_id() {
		return eqp_sz_type_id;
	}

	public void setEqp_sz_type_id(String eqp_sz_type_id) {
		this.eqp_sz_type_id = eqp_sz_type_id;
	}

	public String getSz_type_cd_list_qual() {
		return sz_type_cd_list_qual;
	}

	public void setSz_type_cd_list_qual(String sz_type_cd_list_qual) {
		this.sz_type_cd_list_qual = sz_type_cd_list_qual;
	}

	public String getSz_type_cd_list_resp_agency_cdd() {
		return sz_type_cd_list_resp_agency_cdd;
	}

	public void setSz_type_cd_list_resp_agency_cdd(String sz_type_cd_list_resp_agency_cdd) {
		this.sz_type_cd_list_resp_agency_cdd = sz_type_cd_list_resp_agency_cdd;
	}

	public String getEqp_sz_type() {
		return eqp_sz_type;
	}

	public void setEqp_sz_type(String eqp_sz_type) {
		this.eqp_sz_type = eqp_sz_type;
	}

	public String getEqp_suppl_cdd() {
		return eqp_suppl_cdd;
	}

	public void setEqp_suppl_cdd(String eqp_suppl_cdd) {
		this.eqp_suppl_cdd = eqp_suppl_cdd;
	}

	public String getEqp_status_cdd() {
		return eqp_status_cdd;
	}

	public void setEqp_status_cdd(String eqp_status_cdd) {
		this.eqp_status_cdd = eqp_status_cdd;
	}

	public String getFull_empty_ind_cdd() {
		return full_empty_ind_cdd;
	}

	public void setFull_empty_ind_cdd(String full_empty_ind_cdd) {
		this.full_empty_ind_cdd = full_empty_ind_cdd;
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

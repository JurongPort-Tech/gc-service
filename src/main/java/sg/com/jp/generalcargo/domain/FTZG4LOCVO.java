package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 4 LOC PLACE/LOCATION IDENTIFICATION
public class FTZG4LOCVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// location function code qualifier
	private String loc_func_cd_qual;
	
	// location identification
	private String loc_nm_cd;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String loc_nm;
	
	// related location one identification
	private String related_plc_loc_one_id;
	private String rl_one_cd_list_id_cd;
	private String rl_one_cd_list_resp_agency_cd;
	private String rel_plc_loc_one;
	
	// related location two identification
	private String related_plc_loc_two_id;
	private String rl_two_cd_list_id_cd;
	private String rl_two_cd_list_resp_agency_cd;
	private String rel_plc_loc_two;
	
	// relation, coded
	private String rel_cdd;

	public String getLoc_func_cd_qual() {
		return loc_func_cd_qual;
	}

	public void setLoc_func_cd_qual(String loc_func_cd_qual) {
		this.loc_func_cd_qual = loc_func_cd_qual;
	}

	public String getLoc_nm_cd() {
		return loc_nm_cd;
	}

	public void setLoc_nm_cd(String loc_nm_cd) {
		this.loc_nm_cd = loc_nm_cd;
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

	public String getLoc_nm() {
		return loc_nm;
	}

	public void setLoc_nm(String loc_nm) {
		this.loc_nm = loc_nm;
	}

	public String getRelated_plc_loc_one_id() {
		return related_plc_loc_one_id;
	}

	public void setRelated_plc_loc_one_id(String related_plc_loc_one_id) {
		this.related_plc_loc_one_id = related_plc_loc_one_id;
	}

	public String getRl_one_cd_list_id_cd() {
		return rl_one_cd_list_id_cd;
	}

	public void setRl_one_cd_list_id_cd(String rl_one_cd_list_id_cd) {
		this.rl_one_cd_list_id_cd = rl_one_cd_list_id_cd;
	}

	public String getRl_one_cd_list_resp_agency_cd() {
		return rl_one_cd_list_resp_agency_cd;
	}

	public void setRl_one_cd_list_resp_agency_cd(String rl_one_cd_list_resp_agency_cd) {
		this.rl_one_cd_list_resp_agency_cd = rl_one_cd_list_resp_agency_cd;
	}

	public String getRel_plc_loc_one() {
		return rel_plc_loc_one;
	}

	public void setRel_plc_loc_one(String rel_plc_loc_one) {
		this.rel_plc_loc_one = rel_plc_loc_one;
	}

	public String getRelated_plc_loc_two_id() {
		return related_plc_loc_two_id;
	}

	public void setRelated_plc_loc_two_id(String related_plc_loc_two_id) {
		this.related_plc_loc_two_id = related_plc_loc_two_id;
	}

	public String getRl_two_cd_list_id_cd() {
		return rl_two_cd_list_id_cd;
	}

	public void setRl_two_cd_list_id_cd(String rl_two_cd_list_id_cd) {
		this.rl_two_cd_list_id_cd = rl_two_cd_list_id_cd;
	}

	public String getRl_two_cd_list_resp_agency_cd() {
		return rl_two_cd_list_resp_agency_cd;
	}

	public void setRl_two_cd_list_resp_agency_cd(String rl_two_cd_list_resp_agency_cd) {
		this.rl_two_cd_list_resp_agency_cd = rl_two_cd_list_resp_agency_cd;
	}

	public String getRel_plc_loc_two() {
		return rel_plc_loc_two;
	}

	public void setRel_plc_loc_two(String rel_plc_loc_two) {
		this.rel_plc_loc_two = rel_plc_loc_two;
	}

	public String getRel_cdd() {
		return rel_cdd;
	}

	public void setRel_cdd(String rel_cdd) {
		this.rel_cdd = rel_cdd;
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

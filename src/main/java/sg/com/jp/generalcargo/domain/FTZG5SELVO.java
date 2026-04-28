package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 5 SEL SEAL NUMBER
public class FTZG5SELVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// seal number
	private String seal_nbr;
	
	// seal issuer
	private String sealing_party_cdd;
	private String cd_list_qual;
	private String cd_list_resp_agency_cdd;
	private String sealing_party;
	
	// seal condition, coded
	private String seal_cond_cdd;
	
	// identity number range
	private String obj_id_1;
	private String obj_id_2;
	
	public String getSeal_nbr() {
		return seal_nbr;
	}
	public void setSeal_nbr(String seal_nbr) {
		this.seal_nbr = seal_nbr;
	}
	public String getSealing_party_cdd() {
		return sealing_party_cdd;
	}
	public void setSealing_party_cdd(String sealing_party_cdd) {
		this.sealing_party_cdd = sealing_party_cdd;
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
	public String getSealing_party() {
		return sealing_party;
	}
	public void setSealing_party(String sealing_party) {
		this.sealing_party = sealing_party;
	}
	public String getSeal_cond_cdd() {
		return seal_cond_cdd;
	}
	public void setSeal_cond_cdd(String seal_cond_cdd) {
		this.seal_cond_cdd = seal_cond_cdd;
	}
	public String getObj_id_1() {
		return obj_id_1;
	}
	public void setObj_id_1(String obj_id_1) {
		this.obj_id_1 = obj_id_1;
	}
	public String getObj_id_2() {
		return obj_id_2;
	}
	public void setObj_id_2(String obj_id_2) {
		this.obj_id_2 = obj_id_2;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 11 NAD NAME AND ADDRESS
public class FTZGroup11NADVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// party qualifier
	private String party_qual;
	
	// party identification details
	private String party_id;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cdd;
	
	// name and address
	private String nm_addr_desc_1;
	private String nm_addr_desc_2;
	private String nm_addr_desc_3;
	private String nm_addr_desc_4;
	private String nm_addr_desc_5;
	
	// party name
	private String party_name_1;
	private String party_name_2;
	private String party_name_3;
	private String party_name_4;
	private String party_name_5;
	private String party_name_fmt_cdd;
	
	// street
	private String street_nbr_pbox_1;
	private String street_nbr_pbox_2;
	private String street_nbr_pbox_3;
	
	// city name
	private String city_nm;
	
	// country sub-entity details
	private String ctry_sub_entity_nm_cd;
	private String ctry_cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String ctry_sub_entity_nm;
	
	// postal identification
	private String postal_id;
	
	// country, coded
	private String ctry_cdd;

	public String getParty_qual() {
		return party_qual;
	}

	public void setParty_qual(String party_qual) {
		this.party_qual = party_qual;
	}

	public String getParty_id() {
		return party_id;
	}

	public void setParty_id(String party_id) {
		this.party_id = party_id;
	}

	public String getCd_list_id_cd() {
		return cd_list_id_cd;
	}

	public void setCd_list_id_cd(String cd_list_id_cd) {
		this.cd_list_id_cd = cd_list_id_cd;
	}

	public String getCd_list_resp_agency_cdd() {
		return cd_list_resp_agency_cdd;
	}

	public void setCd_list_resp_agency_cdd(String cd_list_resp_agency_cdd) {
		this.cd_list_resp_agency_cdd = cd_list_resp_agency_cdd;
	}

	public String getNm_addr_desc_1() {
		return nm_addr_desc_1;
	}

	public void setNm_addr_desc_1(String nm_addr_desc_1) {
		this.nm_addr_desc_1 = nm_addr_desc_1;
	}

	public String getNm_addr_desc_2() {
		return nm_addr_desc_2;
	}

	public void setNm_addr_desc_2(String nm_addr_desc_2) {
		this.nm_addr_desc_2 = nm_addr_desc_2;
	}

	public String getNm_addr_desc_3() {
		return nm_addr_desc_3;
	}

	public void setNm_addr_desc_3(String nm_addr_desc_3) {
		this.nm_addr_desc_3 = nm_addr_desc_3;
	}

	public String getNm_addr_desc_4() {
		return nm_addr_desc_4;
	}

	public void setNm_addr_desc_4(String nm_addr_desc_4) {
		this.nm_addr_desc_4 = nm_addr_desc_4;
	}

	public String getNm_addr_desc_5() {
		return nm_addr_desc_5;
	}

	public void setNm_addr_desc_5(String nm_addr_desc_5) {
		this.nm_addr_desc_5 = nm_addr_desc_5;
	}

	public String getParty_name_1() {
		return party_name_1;
	}

	public void setParty_name_1(String party_name_1) {
		this.party_name_1 = party_name_1;
	}

	public String getParty_name_2() {
		return party_name_2;
	}

	public void setParty_name_2(String party_name_2) {
		this.party_name_2 = party_name_2;
	}

	public String getParty_name_3() {
		return party_name_3;
	}

	public void setParty_name_3(String party_name_3) {
		this.party_name_3 = party_name_3;
	}

	public String getParty_name_4() {
		return party_name_4;
	}

	public void setParty_name_4(String party_name_4) {
		this.party_name_4 = party_name_4;
	}

	public String getParty_name_5() {
		return party_name_5;
	}

	public void setParty_name_5(String party_name_5) {
		this.party_name_5 = party_name_5;
	}

	public String getParty_name_fmt_cdd() {
		return party_name_fmt_cdd;
	}

	public void setParty_name_fmt_cdd(String party_name_fmt_cdd) {
		this.party_name_fmt_cdd = party_name_fmt_cdd;
	}

	public String getStreet_nbr_pbox_1() {
		return street_nbr_pbox_1;
	}

	public void setStreet_nbr_pbox_1(String street_nbr_pbox_1) {
		this.street_nbr_pbox_1 = street_nbr_pbox_1;
	}

	public String getStreet_nbr_pbox_2() {
		return street_nbr_pbox_2;
	}

	public void setStreet_nbr_pbox_2(String street_nbr_pbox_2) {
		this.street_nbr_pbox_2 = street_nbr_pbox_2;
	}

	public String getStreet_nbr_pbox_3() {
		return street_nbr_pbox_3;
	}

	public void setStreet_nbr_pbox_3(String street_nbr_pbox_3) {
		this.street_nbr_pbox_3 = street_nbr_pbox_3;
	}

	public String getCity_nm() {
		return city_nm;
	}

	public void setCity_nm(String city_nm) {
		this.city_nm = city_nm;
	}

	public String getCtry_sub_entity_nm_cd() {
		return ctry_sub_entity_nm_cd;
	}

	public void setCtry_sub_entity_nm_cd(String ctry_sub_entity_nm_cd) {
		this.ctry_sub_entity_nm_cd = ctry_sub_entity_nm_cd;
	}

	public String getCtry_cd_list_id_cd() {
		return ctry_cd_list_id_cd;
	}

	public void setCtry_cd_list_id_cd(String ctry_cd_list_id_cd) {
		this.ctry_cd_list_id_cd = ctry_cd_list_id_cd;
	}

	public String getCd_list_resp_agency_cd() {
		return cd_list_resp_agency_cd;
	}

	public void setCd_list_resp_agency_cd(String cd_list_resp_agency_cd) {
		this.cd_list_resp_agency_cd = cd_list_resp_agency_cd;
	}

	public String getCtry_sub_entity_nm() {
		return ctry_sub_entity_nm;
	}

	public void setCtry_sub_entity_nm(String ctry_sub_entity_nm) {
		this.ctry_sub_entity_nm = ctry_sub_entity_nm;
	}

	public String getPostal_id() {
		return postal_id;
	}

	public void setPostal_id(String postal_id) {
		this.postal_id = postal_id;
	}

	public String getCtry_cdd() {
		return ctry_cdd;
	}

	public void setCtry_cdd(String ctry_cdd) {
		this.ctry_cdd = ctry_cdd;
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

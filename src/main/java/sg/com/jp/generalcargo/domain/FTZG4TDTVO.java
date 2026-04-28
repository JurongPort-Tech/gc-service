package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 4 TDT DETAILS OF TRANSPORT
public class FTZG4TDTVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// transport stage code qualifier
	private String tpt_stage_cd_qual;
	
	// conveyance reference number
	private String conv_ref_nbr;
	
	// mode of transport
	private String tpt_mode_nm_cd;
	private String tpt_mode_nm;
	
	// transport means
	private String tpt_means_desc_cd;
	private String tpt_means_desc;
	
	// carrier
	private String carrier_id;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String carrier_nm;
	
	// transit direction indicator code
	private String transit_dir_ind_cd;
	
	// excess transportation information
	private String excess_tpt_reason_cdd;
	private String excess_tpt_resp_cdd;
	private String cust_auth_nbr;
	
	// transport identification
	private String tpt_means_id_nm_id;
	private String tpt_id_cd_list_id_cd;
	private String tpt_id_cd_list_resp_agency_cd;
	private String tpt_means_id_nm;
	private String nat_means_tpt_cdd;
	
	// transport ownership, coded
	private String tpt_ownership_cdd;

	public String getTpt_stage_cd_qual() {
		return tpt_stage_cd_qual;
	}

	public void setTpt_stage_cd_qual(String tpt_stage_cd_qual) {
		this.tpt_stage_cd_qual = tpt_stage_cd_qual;
	}

	public String getConv_ref_nbr() {
		return conv_ref_nbr;
	}

	public void setConv_ref_nbr(String conv_ref_nbr) {
		this.conv_ref_nbr = conv_ref_nbr;
	}

	public String getTpt_mode_nm_cd() {
		return tpt_mode_nm_cd;
	}

	public void setTpt_mode_nm_cd(String tpt_mode_nm_cd) {
		this.tpt_mode_nm_cd = tpt_mode_nm_cd;
	}

	public String getTpt_mode_nm() {
		return tpt_mode_nm;
	}

	public void setTpt_mode_nm(String tpt_mode_nm) {
		this.tpt_mode_nm = tpt_mode_nm;
	}

	public String getTpt_means_desc_cd() {
		return tpt_means_desc_cd;
	}

	public void setTpt_means_desc_cd(String tpt_means_desc_cd) {
		this.tpt_means_desc_cd = tpt_means_desc_cd;
	}

	public String getTpt_means_desc() {
		return tpt_means_desc;
	}

	public void setTpt_means_desc(String tpt_means_desc) {
		this.tpt_means_desc = tpt_means_desc;
	}

	public String getCarrier_id() {
		return carrier_id;
	}

	public void setCarrier_id(String carrier_id) {
		this.carrier_id = carrier_id;
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

	public String getCarrier_nm() {
		return carrier_nm;
	}

	public void setCarrier_nm(String carrier_nm) {
		this.carrier_nm = carrier_nm;
	}

	public String getTransit_dir_ind_cd() {
		return transit_dir_ind_cd;
	}

	public void setTransit_dir_ind_cd(String transit_dir_ind_cd) {
		this.transit_dir_ind_cd = transit_dir_ind_cd;
	}

	public String getExcess_tpt_reason_cdd() {
		return excess_tpt_reason_cdd;
	}

	public void setExcess_tpt_reason_cdd(String excess_tpt_reason_cdd) {
		this.excess_tpt_reason_cdd = excess_tpt_reason_cdd;
	}

	public String getExcess_tpt_resp_cdd() {
		return excess_tpt_resp_cdd;
	}

	public void setExcess_tpt_resp_cdd(String excess_tpt_resp_cdd) {
		this.excess_tpt_resp_cdd = excess_tpt_resp_cdd;
	}

	public String getCust_auth_nbr() {
		return cust_auth_nbr;
	}

	public void setCust_auth_nbr(String cust_auth_nbr) {
		this.cust_auth_nbr = cust_auth_nbr;
	}

	public String getTpt_means_id_nm_id() {
		return tpt_means_id_nm_id;
	}

	public void setTpt_means_id_nm_id(String tpt_means_id_nm_id) {
		this.tpt_means_id_nm_id = tpt_means_id_nm_id;
	}

	public String getTpt_id_cd_list_id_cd() {
		return tpt_id_cd_list_id_cd;
	}

	public void setTpt_id_cd_list_id_cd(String tpt_id_cd_list_id_cd) {
		this.tpt_id_cd_list_id_cd = tpt_id_cd_list_id_cd;
	}

	public String getTpt_id_cd_list_resp_agency_cd() {
		return tpt_id_cd_list_resp_agency_cd;
	}

	public void setTpt_id_cd_list_resp_agency_cd(String tpt_id_cd_list_resp_agency_cd) {
		this.tpt_id_cd_list_resp_agency_cd = tpt_id_cd_list_resp_agency_cd;
	}

	public String getTpt_means_id_nm() {
		return tpt_means_id_nm;
	}

	public void setTpt_means_id_nm(String tpt_means_id_nm) {
		this.tpt_means_id_nm = tpt_means_id_nm;
	}

	public String getNat_means_tpt_cdd() {
		return nat_means_tpt_cdd;
	}

	public void setNat_means_tpt_cdd(String nat_means_tpt_cdd) {
		this.nat_means_tpt_cdd = nat_means_tpt_cdd;
	}

	public String getTpt_ownership_cdd() {
		return tpt_ownership_cdd;
	}

	public void setTpt_ownership_cdd(String tpt_ownership_cdd) {
		this.tpt_ownership_cdd = tpt_ownership_cdd;
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

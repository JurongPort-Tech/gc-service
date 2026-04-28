package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 * System Name          : GBMS (General and Bulk Cargo Management Systems)
 * Module               : cargo - manifest
 * Component ID         : ManifestValueObject.java
 * Component Description:
 *
 * @author
 * @version
 */

/*
 * Revision History
 * ----------------
 * Author    Request Number      Description of Change                Version   Date Released
 *           JPPL/IT/001/2001    Creation                             1.0
 *           - Phase 1
 *
 * Vani      JPPL/IT/001/2001    Changed to add UnStuff               1.3       30 Oct 2003
 */

public class SubAdpValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String subAdp_nbr;
	String truck_ic;
    String truck_nm;
    String contact_no;
    String status;    
    String co_Cd;
    String truck_pkgs;

    public String getCo_Cd() {
		return co_Cd;
	}

	public void setCo_Cd(String co_Cd) {
		this.co_Cd = co_Cd;
	}

	public SubAdpValueObject() {
    }

	public String getTruck_ic() {
		return truck_ic;
	}

	public void setTruck_ic(String truck_ic) {
		this.truck_ic = truck_ic;
	}

	public String getTruck_nm() {
		return truck_nm;
	}

	public void setTruck_nm(String truck_nm) {
		this.truck_nm = truck_nm;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public String getSubAdp_nbr() {
		return subAdp_nbr;
	}

	public void setSubAdp_nbr(String subAdp_nbr) {
		this.subAdp_nbr = subAdp_nbr;
	}

	public String getTruck_pkgs() {
		return truck_pkgs;
	}
	public void setTruck_pkgs(String truck_pkgs) {
		this.truck_pkgs = truck_pkgs;
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

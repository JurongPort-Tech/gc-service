package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsnHistory {
	
	private String event;
	private String first_carrier;
	private String second_carrier;
	private String tesn_asn_nbr;
	private String igw_direction;
	private Date last_modify_dttm;
	private String last_modify_user_id;
	private String edo_asn_nbr;
	private String co_nm;

	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getFirst_carrier() {
		return first_carrier;
	}
	public void setFirst_carrier(String first_carrier) {
		this.first_carrier = first_carrier;
	}
	public String getSecond_carrier() {
		return second_carrier;
	}
	public void setSecond_carrier(String second_carrier) {
		this.second_carrier = second_carrier;
	}
	public String getTesn_asn_nbr() {
		return tesn_asn_nbr;
	}
	public void setTesn_asn_nbr(String tesn_asn_nbr) {
		this.tesn_asn_nbr = tesn_asn_nbr;
	}
	public String getIgw_direction() {
		return igw_direction;
	}
	public void setIgw_direction(String igw_direction) {
		this.igw_direction = igw_direction;
	}
	public Date getLast_modify_dttm() {
		return last_modify_dttm;
	}
	public void setLast_modify_dttm(Date last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}
	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}
	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}
	public String getEdo_asn_nbr() {
		return edo_asn_nbr;
	}
	public void setEdo_asn_nbr(String edo_asn_nbr) {
		this.edo_asn_nbr = edo_asn_nbr;
	}
	/**
	 * @return the co_nm
	 */
	public String getCo_nm() {
		return co_nm;
	}
	/**
	 * @param co_nm the co_nm to set
	 */
	public void setCo_nm(String co_nm) {
		this.co_nm = co_nm;
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

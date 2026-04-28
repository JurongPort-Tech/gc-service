package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VehicleVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String misc_seq_nbr;
	private double no_of_hours;
	private String trailer_type;
	private int trailer_size;
	private String park_reason;
	private String park_reason_cd;
	private String cargo_type;
	private String last_modify_user_id;
	private String last_modify_dttm;
	private String fr_dttm;
	private String to_dttm;

	/**
	 * @return the misc_seq_nbr
	 */
	public String getMisc_seq_nbr() {
		return misc_seq_nbr;
	}

	/**
	 * @param misc_seq_nbr the misc_seq_nbr to set
	 */
	public void setMisc_seq_nbr(String misc_seq_nbr) {
		this.misc_seq_nbr = misc_seq_nbr;
	}

	/**
	 * @return the no_of_hours
	 */
	public double getNo_of_hours() {
		return no_of_hours;
	}

	/**
	 * @param no_of_hours the no_of_hours to set
	 */
	public void setNo_of_hours(double no_of_hours) {
		this.no_of_hours = no_of_hours;
	}

	/**
	 * @return the trailer_type
	 */
	public String getTrailer_type() {
		return trailer_type;
	}

	/**
	 * @param trailer_type the trailer_type to set
	 */
	public void setTrailer_type(String trailer_type) {
		this.trailer_type = trailer_type;
	}

	/**
	 * @return the trailer_size
	 */
	public int getTrailer_size() {
		return trailer_size;
	}

	/**
	 * @param trailer_size the trailer_size to set
	 */
	public void setTrailer_size(int trailer_size) {
		this.trailer_size = trailer_size;
	}

	/**
	 * @return the park_reason
	 */
	public String getPark_reason() {
		return park_reason;
	}

	/**
	 * @param park_reason the park_reason to set
	 */
	public void setPark_reason(String park_reason) {
		this.park_reason = park_reason;
	}

	/**
	 * @return the park_reason_cd
	 */
	public String getPark_reason_cd() {
		return park_reason_cd;
	}

	/**
	 * @param park_reason_cd the park_reason_cd to set
	 */
	public void setPark_reason_cd(String park_reason_cd) {
		this.park_reason_cd = park_reason_cd;
	}

	/**
	 * @return the cargo_type
	 */
	public String getCargo_type() {
		return cargo_type;
	}

	/**
	 * @param cargo_type the cargo_type to set
	 */
	public void setCargo_type(String cargo_type) {
		this.cargo_type = cargo_type;
	}

	/**
	 * @return the last_modify_user_id
	 */
	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	/**
	 * @param last_modify_user_id the last_modify_user_id to set
	 */
	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	/**
	 * @return the last_modify_dttm
	 */
	public String getLast_modify_dttm() {
		return last_modify_dttm;
	}

	/**
	 * @param last_modify_dttm the last_modify_dttm to set
	 */
	public void setLast_modify_dttm(String last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	/**
	 * @return the fr_dttm
	 */
	public String getFr_dttm() {
		return fr_dttm;
	}

	/**
	 * @param fr_dttm the fr_dttm to set
	 */
	public void setFr_dttm(String fr_dttm) {
		this.fr_dttm = fr_dttm;
	}

	/**
	 * @return the to_dttm
	 */
	public String getTo_dttm() {
		return to_dttm;
	}

	/**
	 * @param to_dttm the to_dttm to set
	 */
	public void setTo_dttm(String to_dttm) {
		this.to_dttm = to_dttm;
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

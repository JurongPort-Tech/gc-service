package sg.com.jp.generalcargo.domain;

import java.sql.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOpr {
	
	private String vv_Cd;
	private String stev_co_cd;
	private Date create_dttm;
	private int hatch_nbr;
	private int disc_completed_ton;
	private int load_completed_ton;
	private String last_modify_user_id;
	private Date last_modify_dttm;
	private String disc_remarks;
	private String load_remarks;
	private String disc_weather_cd;
	private String disc_activity_cd;
	private String load_weather_cd;
	private String load_activity_cd;
	
	public GbccCargoOpr() {
		super();
	}

	public String getVv_Cd() {
		return vv_Cd;
	}

	public void setVv_Cd(String vv_Cd) {
		this.vv_Cd = vv_Cd;
	}

	public String getStev_co_cd() {
		return stev_co_cd;
	}

	public void setStev_co_cd(String stev_co_cd) {
		this.stev_co_cd = stev_co_cd;
	}

	public Date getCreate_dttm() {
		return create_dttm;
	}

	public void setCreate_dttm(Date date) {
		this.create_dttm = date;
	}

	public int getHatch_nbr() {
		return hatch_nbr;
	}

	public void setHatch_nbr(int i) {
		this.hatch_nbr = i;
	}

	public int getDisc_completed_ton() {
		return disc_completed_ton;
	}

	public void setDisc_completed_ton(int i) {
		this.disc_completed_ton = i;
	}

	public int getLoad_completed_ton() {
		return load_completed_ton;
	}

	public void setLoad_completed_ton(int i) {
		this.load_completed_ton = i;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public Date getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(Date date) {
		this.last_modify_dttm = date;
	}

	public String getDisc_remarks() {
		return disc_remarks;
	}

	public void setDisc_remarks(String disc_remarks) {
		this.disc_remarks = disc_remarks;
	}

	public String getLoad_remarks() {
		return load_remarks;
	}

	public void setLoad_remarks(String load_remarks) {
		this.load_remarks = load_remarks;
	}

	public String getDisc_weather_cd() {
		return disc_weather_cd;
	}

	public void setDisc_weather_cd(String disc_weather_cd) {
		this.disc_weather_cd = disc_weather_cd;
	}

	public String getDisc_activity_cd() {
		return disc_activity_cd;
	}

	public void setDisc_activity_cd(String disc_activity_cd) {
		this.disc_activity_cd = disc_activity_cd;
	}

	public String getLoad_weather_cd() {
		return load_weather_cd;
	}

	public void setLoad_weather_cd(String load_weather_cd) {
		this.load_weather_cd = load_weather_cd;
	}

	public String getLoad_activity_cd() {
		return load_activity_cd;
	}

	public void setLoad_activity_cd(String load_activity_cd) {
		this.load_activity_cd = load_activity_cd;
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

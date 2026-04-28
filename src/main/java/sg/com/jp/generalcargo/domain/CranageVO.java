package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement
public class CranageVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dsa_nbr;
	private long cranage_line_nbr;
	private String cargo_type;
	private String cargo_type_nm;
	private double norm_lift_nbr;
	private double wharf_lift_nbr;
	private double from_ton;
	private double to_ton;
	private String str_weight_range;
	private String last_modify_user_id;
	private Timestamp last_modify_dttm;
	
	/**
	 * @return the dsa_nbr
	 */
	public String getDsa_nbr() {
		return dsa_nbr;
	}
	/**
	 * @param dsa_nbr the dsa_nbr to set
	 */
	public void setDsa_nbr(String dsa_nbr) {
		this.dsa_nbr = dsa_nbr;
	}
	/**
	 * @return the cranage_line_nbr
	 */
	public long getCranage_line_nbr() {
		return cranage_line_nbr;
	}
	/**
	 * @param cranage_line_nbr the cranage_line_nbr to set
	 */
	public void setCranage_line_nbr(long cranage_line_nbr) {
		this.cranage_line_nbr = cranage_line_nbr;
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
	 * @return the norm_lift_nbr
	 */
	public double getNorm_lift_nbr() {
		return norm_lift_nbr;
	}
	/**
	 * @param norm_lift_nbr the norm_lift_nbr to set
	 */
	public void setNorm_lift_nbr(double norm_lift_nbr) {
		this.norm_lift_nbr = norm_lift_nbr;
	}
	/**
	 * @return the wharf_lift_nbr
	 */
	public double getWharf_lift_nbr() {
		return wharf_lift_nbr;
	}
	/**
	 * @param wharf_lift_nbr the wharf_lift_nbr to set
	 */
	public void setWharf_lift_nbr(double wharf_lift_nbr) {
		this.wharf_lift_nbr = wharf_lift_nbr;
	}
	/**
	 * @return the from_ton
	 */
	public double getFrom_ton() {
		return from_ton;
	}
	/**
	 * @param from_ton the from_ton to set
	 */
	public void setFrom_ton(double from_ton) {
		this.from_ton = from_ton;
	}
	/**
	 * @return the to_ton
	 */
	public double getTo_ton() {
		return to_ton;
	}
	/**
	 * @param to_ton the to_ton to set
	 */
	public void setTo_ton(double to_ton) {
		this.to_ton = to_ton;
	}
	public String getStr_weight_range() {
		return str_weight_range;
	}
	public void setStr_weight_range(String str_weight_range) {
		this.str_weight_range = str_weight_range;
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
//	@XmlJavaTypeAdapter( TimestampAdapter.class)
	public Timestamp getLast_modify_dttm() {
		return last_modify_dttm;
	}
	/**
	 * @param last_modify_dttm the last_modify_dttm to set
	 */
	public void setLast_modify_dttm(Timestamp last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}
	public String getCargo_type_nm() {
		return cargo_type_nm;
	}
	public void setCargo_type_nm(String cargo_type_nm) {
		this.cargo_type_nm = cargo_type_nm;
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

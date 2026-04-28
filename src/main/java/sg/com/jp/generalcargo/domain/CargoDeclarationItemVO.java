package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement
public class CargoDeclarationItemVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long dsa_item_id;
	private String dsa_nbr;
	private long cargo_line_nbr;	
	private String direction_ind;
	private String cargo_desc;
	private String commodity_type_cd;
	private String commodity_type_nm;
	private String commodity_sub_type_cd;
	private String commodity_sub_type_nm;
	private String cargo_type_cd;
	private String cargo_type_nm;
	private double declared_wt_ton;
	private double actual_wt_ton;
	private double rejected_wt_ton;
	private double declared_vol_m3;
	private double actual_vol_m3;
	private double rejected_vol_m3;
	private double declared_qty;
	private double actual_qty;
	private double rejected_qty;
	private String status_cd;
	private String last_modify_user_id;
	private Timestamp last_modify_dttm;
	private String file_upload;
	
	
	/**
	 * @return the file_upload
	 */
	public String getFile_upload() {
		return file_upload;
	}
	/**
	 * @param file_upload the file_upload to set
	 */
	public void setFile_upload(String file_upload) {
		this.file_upload = file_upload;
	}
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
	 * @return the cargo_line_nbr
	 */
	public long getCargo_line_nbr() {
		return cargo_line_nbr;
	}
	/**
	 * @param cargo_line_nbr the cargo_line_nbr to set
	 */
	public void setCargo_line_nbr(long cargo_line_nbr) {
		this.cargo_line_nbr = cargo_line_nbr;
	}
	/**
	 * @return the cargo_ind
	 */
	public String getDirection_ind() {
		return direction_ind;
	}
	/**
	 * @param cargo_ind the cargo_ind to set
	 */
	public void setDirection_ind(String direction_ind) {
		this.direction_ind = direction_ind;
	}
	/**
	 * @return the cargo_desc
	 */
	public String getCargo_desc() {
		return cargo_desc;
	}
	/**
	 * @param cargo_desc the cargo_desc to set
	 */
	public void setCargo_desc(String cargo_desc) {
		this.cargo_desc = cargo_desc;
	}
	/**
	 * @return the commodity_type_cd
	 */
	public String getCommodity_type_cd() {
		return commodity_type_cd;
	}
	/**
	 * @param commodity_type_cd the commodity_type_cd to set
	 */
	public void setCommodity_type_cd(String commodity_type_cd) {
		this.commodity_type_cd = commodity_type_cd;
	}
	/**
	 * @return the commodity_type_nm
	 */
	public String getCommodity_type_nm() {
		return commodity_type_nm;
	}
	/**
	 * @param commodity_type_nm the commodity_type_nm to set
	 */
	public void setCommodity_type_nm(String commodity_type_nm) {
		this.commodity_type_nm = commodity_type_nm;
	}
	/**
	 * @return the commodity_sub_type_cd
	 */
	public String getCommodity_sub_type_cd() {
		return commodity_sub_type_cd;
	}
	/**
	 * @param commodity_sub_type_cd the commodity_sub_type_cd to set
	 */
	public void setCommodity_sub_type_cd(String commodity_sub_type_cd) {
		this.commodity_sub_type_cd = commodity_sub_type_cd;
	}
	/**
	 * @return the commodity_sub_type_nm
	 */
	public String getCommodity_sub_type_nm() {
		return commodity_sub_type_nm;
	}
	/**
	 * @param commodity_sub_type_nm the commodity_sub_type_nm to set
	 */
	public void setCommodity_sub_type_nm(String commodity_sub_type_nm) {
		this.commodity_sub_type_nm = commodity_sub_type_nm;
	}
	/**
	 * @return the cargo_type_cd
	 */
	public String getCargo_type_cd() {
		return cargo_type_cd;
	}
	/**
	 * @param cargo_type_cd the cargo_type_cd to set
	 */
	public void setCargo_type_cd(String cargo_type_cd) {
		this.cargo_type_cd = cargo_type_cd;
	}
	/**
	 * @return the cargo_type_nm
	 */
	public String getCargo_type_nm() {
		return cargo_type_nm;
	}
	/**
	 * @param cargo_type_nm the cargo_type_nm to set
	 */
	public void setCargo_type_nm(String cargo_type_nm) {
		this.cargo_type_nm = cargo_type_nm;
	}
	/**
	 * @return the declared_wt_ton
	 */
	public double getDeclared_wt_ton() {
		return declared_wt_ton;
	}
	/**
	 * @param declared_wt_ton the declared_wt_ton to set
	 */
	public void setDeclared_wt_ton(double declared_wt_ton) {
		this.declared_wt_ton = declared_wt_ton;
	}
	/**
	 * @return the actual_wt_ton
	 */
	public double getActual_wt_ton() {
		return actual_wt_ton;
	}
	/**
	 * @param actual_wt_ton the actual_wt_ton to set
	 */
	public void setActual_wt_ton(double actual_wt_ton) {
		this.actual_wt_ton = actual_wt_ton;
	}
	/**
	 * @return the declared_vol_m3
	 */
	public double getDeclared_vol_m3() {
		return declared_vol_m3;
	}
	/**
	 * @param declared_vol_m3 the declared_vol_m3 to set
	 */
	public void setDeclared_vol_m3(double declared_vol_m3) {
		this.declared_vol_m3 = declared_vol_m3;
	}
	/**
	 * @return the actual_vol_m3
	 */
	public double getActual_vol_m3() {
		return actual_vol_m3;
	}
	/**
	 * @param actual_vol_m3 the actual_vol_m3 to set
	 */
	public void setActual_vol_m3(double actual_vol_m3) {
		this.actual_vol_m3 = actual_vol_m3;
	}
	/**
	 * @return the declared_qty
	 */
	public double getDeclared_qty() {
		return declared_qty;
	}
	/**
	 * @param declared_qty the declared_qty to set
	 */
	public void setDeclared_qty(double declared_qty) {
		this.declared_qty = declared_qty;
	}
	/**
	 * @return the actual_qty
	 */
	public double getActual_qty() {
		return actual_qty;
	}
	/**
	 * @param actual_qty the actual_qty to set
	 */
	public void setActual_qty(double actual_qty) {
		this.actual_qty = actual_qty;
	}
	/**
	 * @return the status_cd
	 */
	public String getStatus_cd() {
		return status_cd;
	}
	/**
	 * @param status_cd the status_cd to set
	 */
	public void setStatus_cd(String status_cd) {
		this.status_cd = status_cd;
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
	public Timestamp getLast_modify_dttm() {
		return last_modify_dttm;
	}
	/**
	 * @param last_modify_dttm the last_modify_dttm to set
	 */
	public void setLast_modify_dttm(Timestamp last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}
	/**
	 * @return the dsa_item_id
	 */
	public long getDsa_item_id() {
		return dsa_item_id;
	}
	/**
	 * @param dsa_item_id the dsa_item_id to set
	 */
	public void setDsa_item_id(long dsa_item_id) {
		this.dsa_item_id = dsa_item_id;
	}
	/**
	 * @return the rejected_wt_ton
	 */
	public double getRejected_wt_ton() {
		return rejected_wt_ton;
	}
	/**
	 * @param rejected_wt_ton the rejected_wt_ton to set
	 */
	public void setRejected_wt_ton(double rejected_wt_ton) {
		this.rejected_wt_ton = rejected_wt_ton;
	}
	/**
	 * @return the rejected_vol_m3
	 */
	public double getRejected_vol_m3() {
		return rejected_vol_m3;
	}
	/**
	 * @param rejected_vol_m3 the rejected_vol_m3 to set
	 */
	public void setRejected_vol_m3(double rejected_vol_m3) {
		this.rejected_vol_m3 = rejected_vol_m3;
	}
	/**
	 * @return the rejected_qty
	 */
	public double getRejected_qty() {
		return rejected_qty;
	}
	/**
	 * @param rejected_qty the rejected_qty to set
	 */
	public void setRejected_qty(double rejected_qty) {
		this.rejected_qty = rejected_qty;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 GID GOODS ITEM DETAILS
public class FTZGroup14GIDVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// goods item number
	private int goods_item_nbr;
	
	// number and types of packages
	private double pkg_qty;
	private String pkg_type_desc_cd;
	private String cd_list_id_cd;
	private String cd_list_resp_agency_cd;
	private String type_pkg;
	private String pkg_related_desc_cd;
	
	public int getGoods_item_nbr() {
		return goods_item_nbr;
	}
	public void setGoods_item_nbr(int goods_item_nbr) {
		this.goods_item_nbr = goods_item_nbr;
	}
	public double getPkg_qty() {
		return pkg_qty;
	}
	public void setPkg_qty(double pkg_qty) {
		this.pkg_qty = pkg_qty;
	}
	public String getPkg_type_desc_cd() {
		return pkg_type_desc_cd;
	}
	public void setPkg_type_desc_cd(String pkg_type_desc_cd) {
		this.pkg_type_desc_cd = pkg_type_desc_cd;
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
	public String getType_pkg() {
		return type_pkg;
	}
	public void setType_pkg(String type_pkg) {
		this.type_pkg = type_pkg;
	}
	public String getPkg_related_desc_cd() {
		return pkg_related_desc_cd;
	}
	public void setPkg_related_desc_cd(String pkg_related_desc_cd) {
		this.pkg_related_desc_cd = pkg_related_desc_cd;
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

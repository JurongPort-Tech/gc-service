package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 CST CUSTOMS STATUS OF GOODS
public class FTZGroup14CSTVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// goods item number
	private int goods_item_nbr;

	// customs identity codes
	private String customs_goods_id1;
	private String cd_list_id_cd1;
	private String cd_list_resp_agency_cd1;

	// customs identity codes
	private String customs_goods_id2;
	private String cd_list_id_cd2;
	private String cd_list_resp_agency_cd2;

	// customs identity codes
	private String customs_goods_id3;
	private String cd_list_id_cd3;
	private String cd_list_resp_agency_cd3;

	// customs identity codes
	private String customs_goods_id4;
	private String cd_list_id_cd4;
	private String cd_list_resp_agency_cd4;

	// customs identity codes
	private String customs_goods_id5;
	private String cd_list_id_cd5;
	private String cd_list_resp_agency_cd5;
	
	public int getGoods_item_nbr() {
		return goods_item_nbr;
	}
	public void setGoods_item_nbr(int goods_item_nbr) {
		this.goods_item_nbr = goods_item_nbr;
	}
	public String getCustoms_goods_id1() {
		return customs_goods_id1;
	}
	public void setCustoms_goods_id1(String customs_goods_id1) {
		this.customs_goods_id1 = customs_goods_id1;
	}
	public String getCd_list_id_cd1() {
		return cd_list_id_cd1;
	}
	public void setCd_list_id_cd1(String cd_list_id_cd1) {
		this.cd_list_id_cd1 = cd_list_id_cd1;
	}
	public String getCd_list_resp_agency_cd1() {
		return cd_list_resp_agency_cd1;
	}
	public void setCd_list_resp_agency_cd1(String cd_list_resp_agency_cd1) {
		this.cd_list_resp_agency_cd1 = cd_list_resp_agency_cd1;
	}
	public String getCustoms_goods_id2() {
		return customs_goods_id2;
	}
	public void setCustoms_goods_id2(String customs_goods_id2) {
		this.customs_goods_id2 = customs_goods_id2;
	}
	public String getCd_list_id_cd2() {
		return cd_list_id_cd2;
	}
	public void setCd_list_id_cd2(String cd_list_id_cd2) {
		this.cd_list_id_cd2 = cd_list_id_cd2;
	}
	public String getCd_list_resp_agency_cd2() {
		return cd_list_resp_agency_cd2;
	}
	public void setCd_list_resp_agency_cd2(String cd_list_resp_agency_cd2) {
		this.cd_list_resp_agency_cd2 = cd_list_resp_agency_cd2;
	}
	public String getCustoms_goods_id3() {
		return customs_goods_id3;
	}
	public void setCustoms_goods_id3(String customs_goods_id3) {
		this.customs_goods_id3 = customs_goods_id3;
	}
	public String getCd_list_id_cd3() {
		return cd_list_id_cd3;
	}
	public void setCd_list_id_cd3(String cd_list_id_cd3) {
		this.cd_list_id_cd3 = cd_list_id_cd3;
	}
	public String getCd_list_resp_agency_cd3() {
		return cd_list_resp_agency_cd3;
	}
	public void setCd_list_resp_agency_cd3(String cd_list_resp_agency_cd3) {
		this.cd_list_resp_agency_cd3 = cd_list_resp_agency_cd3;
	}
	public String getCustoms_goods_id4() {
		return customs_goods_id4;
	}
	public void setCustoms_goods_id4(String customs_goods_id4) {
		this.customs_goods_id4 = customs_goods_id4;
	}
	public String getCd_list_id_cd4() {
		return cd_list_id_cd4;
	}
	public void setCd_list_id_cd4(String cd_list_id_cd4) {
		this.cd_list_id_cd4 = cd_list_id_cd4;
	}
	public String getCd_list_resp_agency_cd4() {
		return cd_list_resp_agency_cd4;
	}
	public void setCd_list_resp_agency_cd4(String cd_list_resp_agency_cd4) {
		this.cd_list_resp_agency_cd4 = cd_list_resp_agency_cd4;
	}
	public String getCustoms_goods_id5() {
		return customs_goods_id5;
	}
	public void setCustoms_goods_id5(String customs_goods_id5) {
		this.customs_goods_id5 = customs_goods_id5;
	}
	public String getCd_list_id_cd5() {
		return cd_list_id_cd5;
	}
	public void setCd_list_id_cd5(String cd_list_id_cd5) {
		this.cd_list_id_cd5 = cd_list_id_cd5;
	}
	public String getCd_list_resp_agency_cd5() {
		return cd_list_resp_agency_cd5;
	}
	public void setCd_list_resp_agency_cd5(String cd_list_resp_agency_cd5) {
		this.cd_list_resp_agency_cd5 = cd_list_resp_agency_cd5;
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

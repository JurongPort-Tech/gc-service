package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 DGS DANGEROUS GOODS
public class FTZGroup14DGSVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// dangerous goods regulations, coded
	private String dgs_reg_cdd;
	
	// hazard code
	private String hzrd_cd_id;
	private String hzrd_subs_item_pg_nbr;
	private String hzrd_cd_ver_nbr;
	
	// undg information
	private int undg_nbr;
	private String dgs_flash;
	
	// dangerous goods shipment flashpoint
	private double shipment_flash;
	private String measure_unit_qual;
	
	// packaging group, coded
	private String pkg_group_cdd;
	
	// ems number
	private int ems_nbr;
	
	// mfag
	private String mfag;
	
	// trem card number
	private String trem_card_nbr;
	
	// hazard identification
	private String hzrd_id_nbr_upper;
	private String subs_id_nbr_lower;
	
	// dangerous goods label
	private String dgs_label;
	private String dgs_label_mrk_1;
	private String dgs_label_mrk_2;
	private String dgs_label_mrk_3;
	
	// packaging instruction, coded
	private String pkg_instr_cdd;
	
	// category of means of transport, coded
	private String cat_mot_cdd;
	
	// permissioin for transport, coded
	private String permission_tpt_cdd;

	public String getDgs_reg_cdd() {
		return dgs_reg_cdd;
	}

	public void setDgs_reg_cdd(String dgs_reg_cdd) {
		this.dgs_reg_cdd = dgs_reg_cdd;
	}

	public String getHzrd_cd_id() {
		return hzrd_cd_id;
	}

	public void setHzrd_cd_id(String hzrd_cd_id) {
		this.hzrd_cd_id = hzrd_cd_id;
	}

	public String getHzrd_subs_item_pg_nbr() {
		return hzrd_subs_item_pg_nbr;
	}

	public void setHzrd_subs_item_pg_nbr(String hzrd_subs_item_pg_nbr) {
		this.hzrd_subs_item_pg_nbr = hzrd_subs_item_pg_nbr;
	}

	public String getHzrd_cd_ver_nbr() {
		return hzrd_cd_ver_nbr;
	}

	public void setHzrd_cd_ver_nbr(String hzrd_cd_ver_nbr) {
		this.hzrd_cd_ver_nbr = hzrd_cd_ver_nbr;
	}

	public int getUndg_nbr() {
		return undg_nbr;
	}

	public void setUndg_nbr(int undg_nbr) {
		this.undg_nbr = undg_nbr;
	}

	public String getDgs_flash() {
		return dgs_flash;
	}

	public void setDgs_flash(String dgs_flash) {
		this.dgs_flash = dgs_flash;
	}

	public double getShipment_flash() {
		return shipment_flash;
	}

	public void setShipment_flash(double shipment_flash) {
		this.shipment_flash = shipment_flash;
	}

	public String getMeasure_unit_qual() {
		return measure_unit_qual;
	}

	public void setMeasure_unit_qual(String measure_unit_qual) {
		this.measure_unit_qual = measure_unit_qual;
	}

	public String getPkg_group_cdd() {
		return pkg_group_cdd;
	}

	public void setPkg_group_cdd(String pkg_group_cdd) {
		this.pkg_group_cdd = pkg_group_cdd;
	}

	public int getEms_nbr() {
		return ems_nbr;
	}

	public void setEms_nbr(int ems_nbr) {
		this.ems_nbr = ems_nbr;
	}

	public String getMfag() {
		return mfag;
	}

	public void setMfag(String mfag) {
		this.mfag = mfag;
	}

	public String getTrem_card_nbr() {
		return trem_card_nbr;
	}

	public void setTrem_card_nbr(String trem_card_nbr) {
		this.trem_card_nbr = trem_card_nbr;
	}

	public String getHzrd_id_nbr_upper() {
		return hzrd_id_nbr_upper;
	}

	public void setHzrd_id_nbr_upper(String hzrd_id_nbr_upper) {
		this.hzrd_id_nbr_upper = hzrd_id_nbr_upper;
	}

	public String getSubs_id_nbr_lower() {
		return subs_id_nbr_lower;
	}

	public void setSubs_id_nbr_lower(String subs_id_nbr_lower) {
		this.subs_id_nbr_lower = subs_id_nbr_lower;
	}

	public String getDgs_label() {
		return dgs_label;
	}

	public void setDgs_label(String dgs_label) {
		this.dgs_label = dgs_label;
	}

	public String getDgs_label_mrk_1() {
		return dgs_label_mrk_1;
	}

	public void setDgs_label_mrk_1(String dgs_label_mrk_1) {
		this.dgs_label_mrk_1 = dgs_label_mrk_1;
	}

	public String getDgs_label_mrk_2() {
		return dgs_label_mrk_2;
	}

	public void setDgs_label_mrk_2(String dgs_label_mrk_2) {
		this.dgs_label_mrk_2 = dgs_label_mrk_2;
	}

	public String getDgs_label_mrk_3() {
		return dgs_label_mrk_3;
	}

	public void setDgs_label_mrk_3(String dgs_label_mrk_3) {
		this.dgs_label_mrk_3 = dgs_label_mrk_3;
	}

	public String getPkg_instr_cdd() {
		return pkg_instr_cdd;
	}

	public void setPkg_instr_cdd(String pkg_instr_cdd) {
		this.pkg_instr_cdd = pkg_instr_cdd;
	}

	public String getCat_mot_cdd() {
		return cat_mot_cdd;
	}

	public void setCat_mot_cdd(String cat_mot_cdd) {
		this.cat_mot_cdd = cat_mot_cdd;
	}

	public String getPermission_tpt_cdd() {
		return permission_tpt_cdd;
	}

	public void setPermission_tpt_cdd(String permission_tpt_cdd) {
		this.permission_tpt_cdd = permission_tpt_cdd;
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

package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.TpaConstants;


public class VehicleDetailsVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String misc_seq_nbr;
	private int item_nbr;
	private String veh_chas_nbr;
	private String cntr_nbr;
	private String asn_nbr;
	private String area_cd;
	private String slot_nbr;
	private String pref_area_cd;
	private String cntr_crg_status;
	private String cntr_crg_status_nm;
	private String remarks;
	private String last_updated_by;
	private String last_modify_user_id;
	private Timestamp last_modify_dttm;

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
	 * @return the item_nbr
	 */
	public int getItem_nbr() {
		return item_nbr;
	}

	/**
	 * @param item_nbr the item_nbr to set
	 */
	public void setItem_nbr(int item_nbr) {
		this.item_nbr = item_nbr;
	}

	/**
	 * @return the veh_chas_nbr
	 */
	public String getVeh_chas_nbr() {
		return veh_chas_nbr;
	}

	/**
	 * @param veh_chas_nbr the veh_chas_nbr to set
	 */
	public void setVeh_chas_nbr(String veh_chas_nbr) {
		this.veh_chas_nbr = veh_chas_nbr;
	}

	/**
	 * @return the cntr_nbr
	 */
	public String getCntr_nbr() {
		return cntr_nbr;
	}

	/**
	 * @param cntr_nbr the cntr_nbr to set
	 */
	public void setCntr_nbr(String cntr_nbr) {
		this.cntr_nbr = cntr_nbr;
	}

	/**
	 * @return the asn_nbr
	 */
	public String getAsn_nbr() {
		return asn_nbr;
	}

	/**
	 * @param asn_nbr the asn_nbr to set
	 */
	public void setAsn_nbr(String asn_nbr) {
		this.asn_nbr = asn_nbr;
	}

	/**
	 * @return the area_cd
	 */
	public String getArea_cd() {
		return area_cd;
	}

	/**
	 * @param area_cd the area_cd to set
	 */
	public void setArea_cd(String area_cd) {
		this.area_cd = area_cd;
	}

	/**
	 * @return the slot_nbr
	 */
	public String getSlot_nbr() {
		return slot_nbr;
	}

	/**
	 * @param slot_nbr the slot_nbr to set
	 */
	public void setSlot_nbr(String slot_nbr) {
		this.slot_nbr = slot_nbr;
	}

	/**
	 * @return the pref_area_cd
	 */
	public String getPref_area_cd() {
		return pref_area_cd;
	}

	/**
	 * @param pref_area_cd the pref_area_cd to set
	 */
	public void setPref_area_cd(String pref_area_cd) {
		this.pref_area_cd = pref_area_cd;
	}

	/**
	 * @return the cntr_crg_status
	 */
	public String getCntr_crg_status() {
		return cntr_crg_status;
	}

	/**
	 * @param cntr_crg_status the cntr_crg_status to set
	 */
	public void setCntr_crg_status(String cntr_crg_status) {
		this.cntr_crg_status = cntr_crg_status;
	}

	/**
	 * @return the cntr_crg_status_nm
	 */
	public String getCntr_crg_status_nm() {
		return cntr_crg_status_nm;
	}

	/**
	 * @param cntr_crg_status_nm the cntr_crg_status_nm to set
	 */
	public void setCntr_crg_status_nm(String cntr_crg_status_nm) {
		this.cntr_crg_status_nm = cntr_crg_status_nm;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the last_updated_by
	 */
	public String getLast_updated_by() {
		return last_updated_by;
	}

	/**
	 * @param last_updated_by the last_updated_by to set
	 */
	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
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

	//
	public String getCntr_crg_status_cd(String status_nm) {
		String status_cd = null;
		if (TpaConstants.TPA_CNTR_CRG_STATUS_EX_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_EX;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_IM_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_IM;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_LN_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_LN;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_RE_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_RE;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_RS_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_RS;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_SH_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_SH;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_ST_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_ST;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_TS_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_TS;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_TX_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_TX;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_R_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_R;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_L_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_L;
		}
		if (TpaConstants.TPA_CNTR_CRG_STATUS_T_NM.equalsIgnoreCase(status_nm)) {
			status_cd = TpaConstants.TPA_CNTR_CRG_STATUS_T;
		}
		return status_cd;
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

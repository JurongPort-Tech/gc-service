package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HatchDetails {
	
	private String var_nbr;
	private Long mft_hatch_seq_nbr;
	private String mft_seq_nbr;
	private String hatch_cd;
	private String nbr_pkgs;
	private String gross_wt;
	private String gross_vol;
	private String last_modify_user_id;
	private String last_modify_dttm;
	private String noOfHatches;
	private String billNo;
	private String cargoDesc;
	private String hsCode;

	
	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public String getNoOfHatches() {
		return noOfHatches;
	}

	public void setNoOfHatches(String noOfHatches) {
		this.noOfHatches = noOfHatches;
	}


	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}

	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}

	public String getHatch_cd() {
		return hatch_cd;
	}

	public void setHatch_cd(String hatch_cd) {
		this.hatch_cd = hatch_cd;
	}

	public String getNbr_pkgs() {
		return nbr_pkgs;
	}

	public void setNbr_pkgs(String nbr_pkgs) {
		this.nbr_pkgs = nbr_pkgs;
	}

	public String getGross_wt() {
		return gross_wt;
	}

	public void setGross_wt(String gross_wt) {
		this.gross_wt = gross_wt;
	}

	public String getGross_vol() {
		return gross_vol;
	}

	public void setGross_vol(String gross_vol) {
		this.gross_vol = gross_vol;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public String getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(String last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}


	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getCargoDesc() {
		return cargoDesc;
	}

	public void setCargoDesc(String cargoDesc) {
		this.cargoDesc = cargoDesc;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Long getMft_hatch_seq_nbr() {
		return mft_hatch_seq_nbr;
	}

	public void setMft_hatch_seq_nbr(Long mft_hatch_seq_nbr) {
		this.mft_hatch_seq_nbr = mft_hatch_seq_nbr;
	}

	public String getVar_nbr() {
		return var_nbr;
	}

	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}

}

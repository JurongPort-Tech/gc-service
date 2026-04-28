package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManifestDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vvCd;
	private String vesselName;
	private String voyageNo;
	private String mftSeq;
	private String billNo;
	private String cargoDes;
	private String nbrPkgs;
	private String grossWt;
	private String grossVol;
	private String noOfHatch;
	private String hsCode;
	private String remarks;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public String getNoOfHatch() {
		return noOfHatch;
	}

	public void setNoOfHatch(String noOfHatch) {
		this.noOfHatch = noOfHatch;
	}

	public String getNbrPkgs() {
		return nbrPkgs;
	}

	public void setNbrPkgs(String nbrPkgs) {
		this.nbrPkgs = nbrPkgs;
	}

	public String getGrossWt() {
		return grossWt;
	}

	public void setGrossWt(String grossWt) {
		this.grossWt = grossWt;
	}

	public String getGrossVol() {
		return grossVol;
	}

	public void setGrossVol(String grossVol) {
		this.grossVol = grossVol;
	}

	public String getCargoDes() {
		return cargoDes;
	}

	public void setCargoDes(String cargoDes) {
		this.cargoDes = cargoDes;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public String getMftSeq() {
		return mftSeq;
	}

	public void setMftSeq(String mftSeq) {
		this.mftSeq = mftSeq;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
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

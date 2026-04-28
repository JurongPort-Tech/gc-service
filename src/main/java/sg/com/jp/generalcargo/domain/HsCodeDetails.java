package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HsCodeDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String CrgDes;
	private String HsCode;
	private String NbrPkgs;
	private String customHsCode;
	private String grossWt;
	private String HsSubCodeFr;
	private String HsSubCodeTo;
	private String GrossVol;
	private String HsSubCodeDesc;
	private String isHsCodeChange;
	private String HscodeSeqNbr;
	
	

	public String getHscodeSeqNbr() {
		return HscodeSeqNbr;
	}

	public void setHscodeSeqNbr(String HscodeSeqNbr) {
		this.HscodeSeqNbr = HscodeSeqNbr;
	}

	public String getCrgDes() {
		return CrgDes;
	}

	public void setCrgDes(String crgDes) {
		CrgDes = crgDes;
	}

	public String getHsCode() {
		return HsCode;
	}

	public void setHsCode(String hsCode) {
		HsCode = hsCode;
	}

	public String getNbrPkgs() {
		return NbrPkgs;
	}

	public void setNbrPkgs(String nbrPkgs) {
		NbrPkgs = nbrPkgs;
	}

	public String getCustomHsCode() {
		return customHsCode;
	}

	public void setCustomHsCode(String customHsCode) {
		this.customHsCode = customHsCode;
	}

	public String getGrossWt() {
		return grossWt;
	}

	public void setGrossWt(String grossWt) {
		this.grossWt = grossWt;
	}

	public String getHsSubCodeFr() {
		return HsSubCodeFr;
	}

	public void setHsSubCodeFr(String hsSubCodeFr) {
		HsSubCodeFr = hsSubCodeFr;
	}

	public String getHsSubCodeTo() {
		return HsSubCodeTo;
	}

	public void setHsSubCodeTo(String hsSubCodeTo) {
		HsSubCodeTo = hsSubCodeTo;
	}

	public String getGrossVol() {
		return GrossVol;
	}

	public void setGrossVol(String grossVol) {
		GrossVol = grossVol;
	}

	public String getHsSubCodeDesc() {
		return HsSubCodeDesc;
	}

	public void setHsSubCodeDesc(String hsSubCodeDesc) {
		HsSubCodeDesc = hsSubCodeDesc;
	}

	public String getIsHsCodeChange() {
		return isHsCodeChange;
	}

	public void setIsHsCodeChange(String isHsCodeChange) {
		this.isHsCodeChange = isHsCodeChange;
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

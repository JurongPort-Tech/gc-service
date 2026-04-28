package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VesselDetail {

	private String vvCd;
	private String vesselName;
	private String inVoyageNbr;
	private String outVoyageNbr;
	private String terminal;
	private String vslNameVoyage;
	private String createdCustCode;

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

	public String getInVoyageNbr() {
		return inVoyageNbr;
	}

	public void setInVoyageNbr(String inVoyageNbr) {
		this.inVoyageNbr = inVoyageNbr;
	}

	public String getOutVoyageNbr() {
		return outVoyageNbr;
	}

	public void setOutVoyageNbr(String outVoyageNbr) {
		this.outVoyageNbr = outVoyageNbr;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getVslNameVoyage() {
		return vslNameVoyage;
	}

	public void setVslNameVoyage(String vslNameVoyage) {
		this.vslNameVoyage = vslNameVoyage;
	}

	public String getCreatedCustCode() {
		return createdCustCode;
	}

	public void setCreatedCustCode(String createdCustCode) {
		this.createdCustCode = createdCustCode;
	}

}

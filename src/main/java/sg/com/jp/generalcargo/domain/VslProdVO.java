package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VslProdVO implements TopsIObject	{
	
	/**
	 * It is the serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * It is the schemeCode.
	 */
	private String schemeCode;
	
	/**
	 * It is the schemeDescription.
	 */
	private String schemeDesc;
	
	/**
	 * It is the vessel name.
	 */
	private String vesselName;
	/**
	 * It is the vessel code.
	 */
	private String vvcd;
	/**
	 * It is the inVoyageNumber.
	 */
	private String inVoygNo;
	/**
	 * It is the outVoyageNumber.
	 */
	private String outVoygNo;
	/**
	 * It is the atu.
	 */
	private String atu;
	/**
	 * It is the atb.
	 */
	private String atb;
	/**
	 * It is the firstActDTTM.
	 */
	private String firstActDTTM;
	/**
	 * It is the firstCargoActDTTM.
	 */
	private String firstCargoActDTTM;
	/**
	 * It is the noOfGangs.
	 */
	private String noOfGangs;
	/**
	 * It is the workableHatches.
	 */
	private String workableHatches;
	/**
	 * It is the delaywork.
	 */
	private String delaywork;
	/**
	 * It is the remarks.
	 */
	private String remarks;
	/**
	 * @return the atb
	 */
	public String getAtb() {
		return atb;
	}
	/**
	 * @param atb the atb to set
	 */
	public void setAtb(String atb) {
		this.atb = atb;
	}
	/**
	 * @return the atu
	 */
	public String getAtu() {
		return atu;
	}
	/**
	 * @param atu the atu to set
	 */
	public void setAtu(String atu) {
		this.atu = atu;
	}
	/**
	 * @return the firstActDTTM
	 */
	public String getFirstActDTTM() {
		return firstActDTTM;
	}
	/**
	 * @param atb the firstActDTTM to set
	 */
	public void setFirstActDTTM(String firstActDTTM) {
		this.firstActDTTM = firstActDTTM;
	}
	/**
	 * @return the firstCargoActDTTM
	 */
	public String getFirstCargoActDTTM() {
		return firstCargoActDTTM;
	}
	/**
	 * @param atb the firstCargoActDTTM to set
	 */
	public void setFirstCargoActDTTM(String firstCargoActDTTM) {
		this.firstCargoActDTTM = firstCargoActDTTM;
	}
	/**
	 * @return the delaywork
	 */
	public String getDelaywork() {
		return delaywork;
	}
	/**
	 * @param delaywork the delaywork to set
	 */
	public void setDelaywork(String delaywork) {
		this.delaywork = delaywork;
	}
	/**
	 * @return the inVoygNo
	 */
	public String getInVoygNo() {
		return inVoygNo;
	}
	/**
	 * @param inVoygNo the inVoygNo to set
	 */
	public void setInVoygNo(String inVoygNo) {
		this.inVoygNo = inVoygNo;
	}
	/**
	 * @return the noOfGangs
	 */
	public String getNoOfGangs() {
		return noOfGangs;
	}
	/**
	 * @param noOfGangs the noOfGangs to set
	 */
	public void setNoOfGangs(String noOfGangs) {
		this.noOfGangs = noOfGangs;
	}
	/**
	 * @return the outVoygNo
	 */
	public String getOutVoygNo() {
		return outVoygNo;
	}
	/**
	 * @param outVoygNo the outVoygNo to set
	 */
	public void setOutVoygNo(String outVoygNo) {
		this.outVoygNo = outVoygNo;
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
	 * @return the schemeCode
	 */
	public String getSchemeCode() {
		return schemeCode;
	}
	/**
	 * @param schemeCode the schemeCode to set
	 */
	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}
	/**
	 * @return the vesselName
	 */
	public String getVesselName() {
		return vesselName;
	}
	/**
	 * @param vesselName the vesselName to set
	 */
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}
	/**
	 * @return the vvcd
	 */
	public String getVvcd() {
		return vvcd;
	}
	/**
	 * @param vvcd the vvcd to set
	 */
	public void setVvcd(String vvcd) {
		this.vvcd = vvcd;
	}
	/**
	 * @return the workableHatches
	 */
	public String getWorkableHatches() {
		return workableHatches;
	}
	/**
	 * @param workableHatches the workableHatches to set
	 */
	public void setWorkableHatches(String workableHatches) {
		this.workableHatches = workableHatches;
	}
	/**
	 * @return the schemeDesc
	 */
	public String getSchemeDesc() {
		return schemeDesc;
	}
	/**
	 * @param schemeDesc the schemeDesc to set
	 */
	public void setSchemeDesc(String schemeDesc) {
		this.schemeDesc = schemeDesc;
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

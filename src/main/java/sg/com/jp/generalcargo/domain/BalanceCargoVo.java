package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(value = Include.NON_NULL)
public class BalanceCargoVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String terminal;
	private String subScheme;
	private String gcOperations;
	private String vesselName;
	private String outVoyNbr;
	private String inVoyNbr;// added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
	private String atuDttm;
	//added by Dongsheng on 5/2/2013
	private String atbDttm;
	private String acctNo;
	
	private String blNbr;
	private String edoAsnNbr;
	
	private String totalPkgs;
	private String balancePkgs;
	private String balanceWeight;
	private String balanceVolume;
	
	private String totalDeliveredPkgs;
	
	private String specialActionRemark;
	private String specialActionPkgs;
	
	//Added by Punitha on 16/02/2009.To add shortlandPkgs
	private String shortlandPkgs;
	
	//Added by Punitha on 27/02/2009
	private String vesselVvCd;
	
	//Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
	private String scheme;
	private String crgStatus;
	private String dwellDays;
	private String Cod;
	private String adpName;
	private String agentName;
	private String warehouseExpDt;
	private String deliveredPkgs;
	
	// VietNguyen added on 09 Dec 2013 for DPE 
	private String coCd;
	private String coNm;
	private String crgDes;
	private String warehouseRefNbr;
	private String warehouseInd;
	private String role;
	
	public String getTerminal() {
		return terminal;
	}
	
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}	
	
	public String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(String subScheme) {
		this.subScheme = subScheme;
	}

	public String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(String gcOperations) {
		this.gcOperations = gcOperations;
	}

	public String getAtuDttm() {
		return atuDttm;
	}
	public void setAtuDttm(String atuDttm) {
		this.atuDttm = atuDttm;
	}
	//added by Dongsheng on 5/2/2013
	public String getAtbDttm() {
		return atbDttm;
	}
	public void setAtbDttm(String atbDttm) {
		this.atbDttm = atbDttm;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getBalancePkgs() {
		return balancePkgs;
	}
	public void setBalancePkgs(String balancePkgs) {
		this.balancePkgs = balancePkgs;
	}
	public String getBalanceVolume() {
		return balanceVolume;
	}
	public void setBalanceVolume(String balanceVolume) {
		this.balanceVolume = balanceVolume;
	}
	public String getBalanceWeight() {
		return balanceWeight;
	}
	public void setBalanceWeight(String balanceWeight) {
		this.balanceWeight = balanceWeight;
	}
	public String getBlNbr() {
		return blNbr;
	}
	public void setBlNbr(String blNbr) {
		this.blNbr = blNbr;
	}
	public String getEdoAsnNbr() {
		return edoAsnNbr;
	}
	public void setEdoAsnNbr(String edoAsnNbr) {
		this.edoAsnNbr = edoAsnNbr;
	}
	public String getOutVoyNbr() {
		return outVoyNbr;
	}
	public void setOutVoyNbr(String outVoyNbr) {
		this.outVoyNbr = outVoyNbr;
	}
	
	public String getInVoyNbr() {
		return inVoyNbr;
	}
	public void setInVoyNbr(String inVoyNbr) {
		this.inVoyNbr = inVoyNbr;
	}

	public String getTotalPkgs() {
		return totalPkgs;
	}
	public void setTotalPkgs(String totalPkgs) {
		this.totalPkgs = totalPkgs;
	}
	public String getVesselName() {
		return vesselName;
	}
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}
	public String getTotalDeliveredPkgs() {
		return totalDeliveredPkgs;
	}
	public void setTotalDeliveredPkgs(String totalDeliveredPkgs) {
		this.totalDeliveredPkgs = totalDeliveredPkgs;
	}
	public String getSpecialActionPkgs() {
		return specialActionPkgs;
	}
	public void setSpecialActionPkgs(String specialActionPkgs) {
		this.specialActionPkgs = specialActionPkgs;
	}
	public String getSpecialActionRemark() {
		return specialActionRemark;
	}
	public void setSpecialActionRemark(String specialActionRemark) {
		this.specialActionRemark = specialActionRemark;
	}
	
	//Added by Punitha on 16/02/2009 to add the shorlandPkgs
	public String getShortlandPkgs() {
		return shortlandPkgs;
	}
	public void setShortlandPkgs(String shortlandPkgs) {
		this.shortlandPkgs = shortlandPkgs;
	}
	
	//Added on 27/02/2009 by Punitha
	
	public String getVesselVvCd() {
		return vesselVvCd;
	}
	public void setVesselVvCd(String vesselVvCd) {
		this.vesselVvCd = vesselVvCd;
	}
	
	//Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getCrgStatus() {
		return crgStatus;
	}
	public void setCrgStatus(String crgStatus) {
		this.crgStatus = crgStatus;
	}
	
	public String getDwellDays() {
		return dwellDays;
	}
	public void setDwellDays(String dwellDays) {
		this.dwellDays = dwellDays;
	}
	public String getCod() {
		return Cod;
	}
	public void setCod(String cod) {
		Cod = cod;
	}
	public String getAdpName() {
		return adpName;
	}
	public void setAdpName(String adpName) {
		this.adpName = adpName;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getWarehouseExpDt() {
		return warehouseExpDt;
	}
	public void setWarehouseExpDt(String warehouseExpDt) {
		this.warehouseExpDt = warehouseExpDt;
	}
	public String getDeliveredPkgs() {
		return deliveredPkgs;
	}
	public void setDeliveredPkgs(String deliveredPkgs) {
		this.deliveredPkgs = deliveredPkgs;
	}
	/**
	 * @return the crgDes
	 */
	public String getCrgDes() {
		return crgDes;
	}
	/**
	 * @param crgDes the crgDes to set
	 */
	public void setCrgDes(String crgDes) {
		this.crgDes = crgDes;
	}
	/**
	 * @return the warehouseRefNbr
	 */
	public String getWarehouseRefNbr() {
		return warehouseRefNbr;
	}
	/**
	 * @param warehouseRefNbr the warehouseRefNbr to set
	 */
	public void setWarehouseRefNbr(String warehouseRefNbr) {
		this.warehouseRefNbr = warehouseRefNbr;
	}
	/**
	 * @return the warehouseInd
	 */
	public String getWarehouseInd() {
		return warehouseInd;
	}
	/**
	 * @param warehouseInd the warehouseInd to set
	 */
	public void setWarehouseInd(String warehouseInd) {
		this.warehouseInd = warehouseInd;
	}
	/**
	 * @return the coCd
	 */
	public String getCoCd() {
		return coCd;
	}
	/**
	 * @param coCd the coCd to set
	 */
	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}
	/**
	 * @return the coNm
	 */
	public String getCoNm() {
		return coNm;
	}
	/**
	 * @param coNm the coNm to set
	 */
	public void setCoNm(String coNm) {
		this.coNm = coNm;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
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

package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TariffTierBillPartyVO extends UserTimestampVO  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**<cfg, 12.oct.2010>*/
	protected String businessTypeDesc;
	protected String tariffMainCatDesc;
	protected String tariffSubCatDesc;
	protected String oprName;
	protected String billPartyDesc;	
	protected String lastModifyUserId;
	/**</cfg>*/
	
	protected String tariffMainCat;

	protected String tariffSubCat;

	protected String mvmtType;

	protected String businessType;

	protected Timestamp effStartDttm;

	protected Timestamp effEndDttm;

	protected double cutOff;

	protected String billParty;
	
	protected String oprCd; // CR-CAB-20100506-005 

	public String getBillParty() {
		return billParty;
	}

	public void setBillParty(String billParty) {
		this.billParty = billParty;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public double getCutOff() {
		return cutOff;
	}

	public void setCutOff(double cutOff) {
		this.cutOff = cutOff;
	}

	public Timestamp getEffEndDttm() {
		return effEndDttm;
	}

	public void setEffEndDttm(Timestamp effEndDttm) {
		this.effEndDttm = effEndDttm;
	}

	public Timestamp getEffStartDttm() {
		return effStartDttm;
	}

	public void setEffStartDttm(Timestamp effStartDttm) {
		this.effStartDttm = effStartDttm;
	}

	public String getMvmtType() {
		return mvmtType;
	}

	public void setMvmtType(String mvmtType) {
		this.mvmtType = mvmtType;
	}

	public String getTariffMainCat() {
		return tariffMainCat;
	}

	public void setTariffMainCat(String tariffMainCat) {
		this.tariffMainCat = tariffMainCat;
	}

	public String getTariffSubCat() {
		return tariffSubCat;
	}

	public void setTariffSubCat(String tariffSubCat) {
		this.tariffSubCat = tariffSubCat;
	}

	// new getter setter for new string  // CR-CAB-20100506-005 
	public String getOprCd() {
		return oprCd;
	}
	
	public void setOprCd(String oprCd) {
		this.oprCd = oprCd;
	}
	
	/**	<cfg, 12.oct.2010, generate getters and setters>*/
	/**
	 * @return Returns the billPartyDesc.
	 */
	public String getBillPartyDesc() {
		return billPartyDesc;
	}

	/**
	 * @param billPartyDesc The billPartyDesc to set.
	 */
	public void setBillPartyDesc(String billPartyDesc) {
		this.billPartyDesc = billPartyDesc;
	}

	/**
	 * @return Returns the businessTypeDesc.
	 */
	public String getBusinessTypeDesc() { 
		return businessTypeDesc;
	}

	/**
	 * @param businessTypeDesc The businessTypeDesc to set.
	 */
	public void setBusinessTypeDesc(String businessTypeDesc) {
		this.businessTypeDesc = businessTypeDesc;
	}

	/**
	 * @return Returns the oprName.
	 */
	public String getOprName() {
		return oprName;
	}

	/**
	 * @param oprName The oprName to set.
	 */
	public void setOprName(String oprName) {
		this.oprName = oprName;
	}

	/**
	 * @return Returns the tariffMainCatDesc.
	 */
	public String getTariffMainCatDesc() {
		return tariffMainCatDesc;
	}

	/**
	 * @param tariffMainCatDesc The tariffMainCatDesc to set.
	 */
	public void setTariffMainCatDesc(String tariffMainCatDesc) {
		this.tariffMainCatDesc = tariffMainCatDesc;
	}

	/**
	 * @return Returns the tariffSubCatDesc.
	 */
	public String getTariffSubCatDesc() {
		return tariffSubCatDesc;
	}

	/**
	 * @param tariffSubCatDesc The tariffSubCatDesc to set.
	 */
	public void setTariffSubCatDesc(String tariffSubCatDesc) {
		this.tariffSubCatDesc = tariffSubCatDesc;
	}

	/**
	 * @return Returns the lastModifyUserId.
	 */
	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	/**
	 * @param lastModifyUserId The lastModifyUserId to set.
	 */
	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
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

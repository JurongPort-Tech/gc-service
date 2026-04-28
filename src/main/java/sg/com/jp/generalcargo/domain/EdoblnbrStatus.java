package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdoblnbrStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String blnbr;
	private String newnbrpkgs;
    private String newWt;
    private String newVol;
    private String size;
    private String adpnbr;
    private String adpnmstatus;
    private String crgagtnm;
    private String agtattnm;
    private String deliveryto;
    private String vslnmvoynbr;
    private String edostatus;
    private String crgagtnbr;
    private String crgagtnmstatus;
    private String agtattnbr;
    private String agtattnmstatus;
    private String distype;
    private String taUenNo;
    private String taCCode;
    private String taEndorser;
    private String varnbr;
    private String hsCodeSize;
    private List<HsCodeDetails> hsCodeDetails;
    
    @JsonProperty("adpObject")
    private AdpValueObject adpValueObject;
    
    private String companyCode;
    private String userAccount;
    private String adpnm;
    private String jpbnbr;
    
    public String getJpbnbr() {
		return jpbnbr;
	}
	public void setJpbnbr(String jpbnbr) {
		this.jpbnbr = jpbnbr;
	}
	public String getAdpnm() {
		return adpnm;
	}
	public void setAdpnm(String adpnm) {
		this.adpnm = adpnm;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
    
    
    public AdpValueObject getAdpValueObject() {
		return adpValueObject;
	}
	public void setAdpValueObject(AdpValueObject adpValueObject) {
		this.adpValueObject = adpValueObject;
	}
	
	public String getBlnbr() {
		return blnbr;
	}
	public void setBlnbr(String blnbr) {
		this.blnbr = blnbr;
	}
	public String getNewnbrpkgs() {
		return newnbrpkgs;
	}
	public void setNewnbrpkgs(String newnbrpkgs) {
		this.newnbrpkgs = newnbrpkgs;
	}
	public String getNewWt() {
		return newWt;
	}
	public void setNewWt(String newWt) {
		this.newWt = newWt;
	}
	public String getNewVol() {
		return newVol;
	}
	public void setNewVol(String newVol) {
		this.newVol = newVol;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getAdpnbr() {
		return adpnbr;
	}
	public void setAdpnbr(String adpnbr) {
		this.adpnbr = adpnbr;
	}
	public String getAdpnmstatus() {
		return adpnmstatus;
	}
	public void setAdpnmstatus(String adpnmstatus) {
		this.adpnmstatus = adpnmstatus;
	}
	public String getCrgagtnm() {
		return crgagtnm;
	}
	public void setCrgagtnm(String crgagtnm) {
		this.crgagtnm = crgagtnm;
	}
	public String getAgtattnm() {
		return agtattnm;
	}
	public void setAgtattnm(String agtattnm) {
		this.agtattnm = agtattnm;
	}
	public String getDeliveryto() {
		return deliveryto;
	}
	public void setDeliveryto(String deliveryto) {
		this.deliveryto = deliveryto;
	}
	public String getVslnmvoynbr() {
		return vslnmvoynbr;
	}
	public void setVslnmvoynbr(String vslnmvoynbr) {
		this.vslnmvoynbr = vslnmvoynbr;
	}
	public String getEdostatus() {
		return edostatus;
	}
	public void setEdostatus(String edostatus) {
		this.edostatus = edostatus;
	}
	public String getCrgagtnbr() {
		return crgagtnbr;
	}
	public void setCrgagtnbr(String crgagtnbr) {
		this.crgagtnbr = crgagtnbr;
	}
	public String getCrgagtnmstatus() {
		return crgagtnmstatus;
	}
	public void setCrgagtnmstatus(String crgagtnmstatus) {
		this.crgagtnmstatus = crgagtnmstatus;
	}
	public String getAgtattnbr() {
		return agtattnbr;
	}
	public void setAgtattnbr(String agtattnbr) {
		this.agtattnbr = agtattnbr;
	}
	public String getAgtattnmstatus() {
		return agtattnmstatus;
	}
	public void setAgtattnmstatus(String agtattnmstatus) {
		this.agtattnmstatus = agtattnmstatus;
	}
	public String getDistype() {
		return distype;
	}
	public void setDistype(String distype) {
		this.distype = distype;
	}
	public String getTaUenNo() {
		return taUenNo;
	}
	public void setTaUenNo(String taUenNo) {
		this.taUenNo = taUenNo;
	}
	public String getTaCCode() {
		return taCCode;
	}
	public void setTaCCode(String taCCode) {
		this.taCCode = taCCode;
	}
	public String getTaEndorser() {
		return taEndorser;
	}
	public void setTaEndorser(String taEndorser) {
		this.taEndorser = taEndorser;
	}
	public String getVarnbr() {
		return varnbr;
	}
	public void setVarnbr(String varnbr) {
		this.varnbr = varnbr;
	}
	public String getHsCodeSize() {
		return hsCodeSize;
	}
	public void setHsCodeSize(String hsCodeSize) {
		this.hsCodeSize = hsCodeSize;
	}
	public List<HsCodeDetails> getHsCodeDetails() {
		return hsCodeDetails;
	}
	public void setHsCodeDetails(List<HsCodeDetails> hsCodeDetails) {
		this.hsCodeDetails = hsCodeDetails;
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

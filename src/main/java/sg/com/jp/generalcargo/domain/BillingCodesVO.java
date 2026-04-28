package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillingCodesVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billingCode;
	private String billingNumber;
	private String vesselTypeCode;
	private String status;
	/**
	 * @return the billingCode
	 */
	public String getBillingCode() {
		return billingCode;
	}
	/**
	 * @param billingCode the billingCode to set
	 */
	public void setBillingCode(String billingCode) {
		this.billingCode = billingCode;
	}
	/**
	 * @return the billingNumber
	 */
	public String getBillingNumber() {
		return billingNumber;
	}
	/**
	 * @param billingNumber the billingNumber to set
	 */
	public void setBillingNumber(String billingNumber) {
		this.billingNumber = billingNumber;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the vesselTypeCode
	 */
	public String getVesselTypeCode() {
		return vesselTypeCode;
	}
	/**
	 * @param vesselTypeCode the vesselTypeCode to set
	 */
	public void setVesselTypeCode(String vesselTypeCode) {
		this.vesselTypeCode = vesselTypeCode;
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

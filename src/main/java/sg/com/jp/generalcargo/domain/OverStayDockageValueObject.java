package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OverStayDockageValueObject extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Waiver name  corresponding to the code
	 */
	String waiverName;
	
	/**
	 * Billing name corresponding to the code
	 */
	String billingName;	
	
	/**
	 * Status whether the waiver has been approved or not.
	 */
	String ISRejected;

	/**
	 * @return the billingName
	 */
	public String getBillingName() {
		return billingName;
	}

	/**
	 * @param billingName the billingName to set
	 */
	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	/**
	 * @return the iSRejected
	 */
	public String getISRejected() {
		return ISRejected;
	}

	/**
	 * @param rejected the iSRejected to set
	 */
	public void setISRejected(String rejected) {
		ISRejected = rejected;
	}

	/**
	 * @return the waiverName
	 */
	public String getWaiverName() {
		return waiverName;
	}

	/**
	 * @param waiverName the waiverName to set
	 */
	public void setWaiverName(String waiverName) {
		this.waiverName = waiverName;
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

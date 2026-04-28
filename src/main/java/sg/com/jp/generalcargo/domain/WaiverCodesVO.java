package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WaiverCodesVO  extends UserTimestampVO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String waiverCode;
	private String waiverNumber;
	private String vesselTypeCode;
	private String status;
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
	/**
	 * @return the waiverCode
	 */
	public String getWaiverCode() {
		return waiverCode;
	}
	/**
	 * @param waiverCode the waiverCode to set
	 */
	public void setWaiverCode(String waiverCode) {
		this.waiverCode = waiverCode;
	}
	/**
	 * @return the waiverNumber
	 */
	public String getWaiverNumber() {
		return waiverNumber;
	}
	/**
	 * @param waiverNumber the waiverNumber to set
	 */
	public void setWaiverNumber(String waiverNumber) {
		this.waiverNumber = waiverNumber;
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

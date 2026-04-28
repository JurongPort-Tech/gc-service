package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
*System Name: GBMS (General Bulk Cargo Management System)
*Component ID: OvrNghtPrkgVehValueObject.java
*Component Description: Shows List of Overnight Parking Vehicles 
* 
*@author      Dong Sheng
*@version     1.0
*@since       03 Jan 2011
*/

/*Revision History
*================
* Author       Request Number      Description of Change           Version     Date Released
* -----------  ------------------  -----------------------------   -------     -----------------
* Dong Sheng   CR-OPS-20110110-09  Creation                        1.0         03 January 2011
*
*/


public class OvrNghtPrkgVehValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sNo;
	private String vehNbr;
	private String fromDate;
	private String toDate;
	private String status;
	private String companyName;
	private String acctNbr;
	private String contactPerson;
	private String contactNbr;
	private String refNbr;
	private String areaCode;

	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}

	/**
	 * @param areaCode the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 * @return the sNo
	 */
	public int getSNo() {
		return sNo;
	}

	/**
	 * @param no the sNo to set
	 */
	public void setSNo(int no) {
		sNo = no;
	}

	/**
	 * @return the vehNbr
	 */
	public String getVehNbr() {
		return vehNbr;
	}

	/**
	 * @param vehNbr the vehNbr to set
	 */
	public void setVehNbr(String vehNbr) {
		this.vehNbr = vehNbr;
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the acctNbr
	 */
	public String getAcctNbr() {
		return acctNbr;
	}

	/**
	 * @param acctNbr the acctNbr to set
	 */
	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @return the contactNbr
	 */
	public String getContactNbr() {
		return contactNbr;
	}

	/**
	 * @param contactNbr the contactNbr to set
	 */
	public void setContactNbr(String contactNbr) {
		this.contactNbr = contactNbr;
	}

	/**
	 * @return the refNbr
	 */
	public String getRefNbr() {
		return refNbr;
	}

	/**
	 * @param refNbr the refNbr to set
	 */
	public void setRefNbr(String refNbr) {
		this.refNbr = refNbr;
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
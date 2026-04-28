package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CompanyValueObject.java
 *
 * @author Ka Boon
 * @date December 07, 2001 Revision History
 *
 *       Author Request No Description version Date
 *
 *       Manohar CTCIM20030058 added psa_co_cd 1.4 15/10/2003
 * 
 *       TuanTA10 - Update 5 new fields in company table 24-Aug-2007 NgocNN1 Add
 *       new 2 fields acToBill, contractNumber 14-Oct-2007 sauwoon
 *       CR-CAB-20110713-011 add jp_pkg_nm 1.5 29 Nov 2011 Jade Huang
 *       CR-FMAS-20120202-001 Credit Control Risk 1.6 26 Jun 2012
 */

public class CompanyValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Attributes of the Company Code table
	private String co_cd; // Company code
	private String co_nm; // Company name
	private String lob_cd; // Line of business code
	private String lob_desc; // Line of business description
	private String cust_relation_ind; // Customer relationship indicator
	private String rec_status; // Status
	private String last_modify_user_id; // Last modified user id
	private String last_modify_dttm; // Last modified timestamp
	private String psa_co_cd; // PSA Company Code

	private String allowJPOnline; // Allow to JP Online
	private String regFeeChargeStatus; // Registration Fee
	private String subFeeChargeStatus; // Subscription Fee
	private String acToBill; // Account to bill
	private String contractNumber; // Contract Number
	private String jpPkgNm; // jpOnline subscription package name

	// Added by Jade for CR-FMAS-20120202-001
	private String creditControlStatus;
	private String creditControlInd;
	private String bgSdAmt;
	private String unpaidPercent;
	private String unpaid60Amt;
	private String unpaid90Amt;
	// End of adding by Jade for CR-FMAS-20120202-001

	/** Creates new CompanyValueObject */
	public CompanyValueObject() {
//      LogManager.instance.logInfo("CompanyValueObject");    	
	}

	/**
	 * Set the Company Code field
	 *
	 * @param String Company code
	 *
	 */
	public void setCompanyCode(String companyCode) {
		co_cd = companyCode;
	}

	/**
	 * Returns the Company Code value
	 */
	public String getCompanyCode() {
		return co_cd;
	}

	/**
	 * Set the Company Name field
	 *
	 * @param String Company code
	 *
	 */
	public void setCompanyName(String companyName) {
		co_nm = companyName.toUpperCase();
	}

	/**
	 * Returns the Company Name value
	 */
	public String getCompanyName() {
		return co_nm;
	}

	/**
	 * Set the Line of business code field
	 *
	 * @param String Company code
	 *
	 */
	public void setLOB(String lob) {
		lob_cd = lob;
	}

	/**
	 * Returns the Line of business code value
	 */
	public String getLOB() {
		return lob_cd;
	}

	/**
	 * Set the Line of business description field
	 *
	 * @param String Line of business description
	 *
	 */
	public void setLOBDesc(String lobDesc) {
		lob_desc = lobDesc;
	}

	/**
	 * Returns the Line of business description value
	 */
	public String getLOBDesc() {
		return lob_desc;
	}

	/**
	 * Set the Customer relationship indicator field
	 *
	 * @param String Company code
	 *
	 */
	public void setCustRelationInd(String custRelationInd) {
		cust_relation_ind = custRelationInd;
	}

	/**
	 * Returns the Customer relationship indicator value
	 */
	public String getCustRelationInd() {
		return cust_relation_ind;
	}

	/**
	 * Set the Status field
	 *
	 * @param String Status
	 *
	 */
	public void setStatus(String status) {
		rec_status = status;
	}

	/**
	 * Returns the Status value
	 */
	public String getStatus() {
		return rec_status;
	}

	/**
	 * Set the Last Modified By field
	 *
	 * @param String Last Modified By
	 *
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		last_modify_user_id = lastModifiedBy;
	}

	/**
	 * Returns the Last Modified By value
	 */
	public String getLastModifiedBy() {
		return last_modify_user_id;
	}

	/**
	 * Set the Last Modified Date field
	 *
	 * @param String Last Modified Date
	 *
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		last_modify_dttm = lastModifiedDate;
	}

	/**
	 * Returns the Last Modified Date value
	 */
	public String getLastModifiedDate() {
		return last_modify_dttm;
	}

	/**
	 * Set the PSA Company Code
	 *
	 * @param String psaCompanyCode
	 *
	 */
	public void setPsaCompanyCode(String psaCompanyCode) {
		psa_co_cd = psaCompanyCode;
	}

	/**
	 * Returns the Last PSA Company Code
	 */
	public String getPsaCompanyCode() {
		return psa_co_cd;
	}

	/**
	 * Returns the account for billing
	 */
	public String getAcToBill() {
		return acToBill;
	}

	/**
	 * Set the account for billing
	 * 
	 * @param acToBill String acToBill
	 */
	public void setAcToBill(String acToBill) {
		this.acToBill = acToBill;
	}

	/**
	 * Return value of field AllowJPOnline
	 */
	public String getAllowJPOnline() {
		return allowJPOnline;
	}

	/**
	 * Set the allowJPOnline
	 * 
	 * @param allowJPOnline String AllowJPOnline
	 */
	public void setAllowJPOnline(String allowJPOnline) {
		this.allowJPOnline = allowJPOnline;
	}

	/**
	 * Return the contract number
	 */
	public String getContractNumber() {
		return contractNumber;
	}

	/**
	 * Set the contract number
	 * 
	 * @param contractNumber String Contract Number
	 */
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	/**
	 * Return the registration fee status
	 */
	public String getRegFeeChargeStatus() {
		return regFeeChargeStatus;
	}

	/**
	 * Set the Registration fee status
	 * 
	 * @param regFeeChargeStatus String regFeeChargeStatus
	 */
	public void setRegFeeChargeStatus(String regFeeChargeStatus) {
		this.regFeeChargeStatus = regFeeChargeStatus;
	}

	/**
	 * Return the subscription fee status
	 */
	public String getSubFeeChargeStatus() {
		return subFeeChargeStatus;
	}

	/**
	 * Set the registration fee status
	 */
	public void setSubFeeChargeStatus(String subFeeChargeStatus) {
		this.subFeeChargeStatus = subFeeChargeStatus;
	}

	/**
	 * Return the subscription pkg name
	 */
	public String getJpPkgNm() {
		return jpPkgNm;
	}

	/**
	 * Set the subscription pkg name
	 */
	public void setJpPkgNm(String jpPkgNm) {
		this.jpPkgNm = jpPkgNm;
	}

	// Added by Jade for CR-FMAS-20120202-001
	public String getCreditControlStatus() {
		return creditControlStatus;
	}

	public void setCreditControlStatus(String creditControlStatus) {
		this.creditControlStatus = creditControlStatus;
	}

	public String getCreditControlInd() {
		return creditControlInd;
	}

	public void setCreditControlInd(String creditControlInd) {
		this.creditControlInd = creditControlInd;
	}

	public String getBgSdAmt() {
		return bgSdAmt;
	}

	public void setBgSdAmt(String bgSdAmt) {
		this.bgSdAmt = bgSdAmt;
	}

	public String getUnpaidPercent() {
		return unpaidPercent;
	}

	public void setUnpaidPercent(String unpaidPercent) {
		this.unpaidPercent = unpaidPercent;
	}

	public String getUnpaid60Amt() {
		return unpaid60Amt;
	}

	public void setUnpaid60Amt(String unpaid60Amt) {
		this.unpaid60Amt = unpaid60Amt;
	}

	public String getUnpaid90Amt() {
		return unpaid90Amt;
	}

	public void setUnpaid90Amt(String unpaid90Amt) {
		this.unpaid90Amt = unpaid90Amt;
	}
	// End of adding by Jade for CR-FMAS-20120202-001

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

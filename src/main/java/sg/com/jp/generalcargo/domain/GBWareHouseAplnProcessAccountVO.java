package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GBWareHouseAplnProcessAccountVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GBWareHouseAplnProcessAccountVO() {
	}

	private String sNo;
	private String schemeCode;
	private String schemeDesc;
	private String schemeDetails;
	private String storagePeriodFrom;
	private String storagePeriodTo;
	private String storageDays;
	private String userGroup;
	private String loginId;
	private String loginDetails;
	private String userName;
	private String telephone;
	private String emailAddress;
	private String designation;
	private String department;
	private String recordStatus;
	private String lastModifyUserId;
	private String lastModifyDTTM;
	private String[] loginIds;
	private String[] userNames;
	private String[] telephones;
	private String[] emailAddresses;
	// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.
	private String status;
	// END FPT modify for Warehouse Management CR, 10/02/2014.

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getLastModifyDTTM() {
		return lastModifyDTTM;
	}

	public void setLastModifyDTTM(String lastModifyDTTM) {
		this.lastModifyDTTM = lastModifyDTTM;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public String getLoginDetails() {
		return loginDetails;
	}

	public void setLoginDetails(String loginDetails) {
		this.loginDetails = loginDetails;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getSchemeDesc() {
		return schemeDesc;
	}

	public void setSchemeDesc(String schemeDesc) {
		this.schemeDesc = schemeDesc;
	}

	public String getSchemeDetails() {
		return schemeDetails;
	}

	public void setSchemeDetails(String schemeDetails) {
		this.schemeDetails = schemeDetails;
	}

	public String getStorageDays() {
		return storageDays;
	}

	public void setStorageDays(String storageDays) {
		this.storageDays = storageDays;
	}

	public String getStoragePeriodFrom() {
		return storagePeriodFrom;
	}

	public void setStoragePeriodFrom(String storagePeriodFrom) {
		this.storagePeriodFrom = storagePeriodFrom;
	}

	public String getStoragePeriodTo() {
		return storagePeriodTo;
	}

	public void setStoragePeriodTo(String storagePeriodTo) {
		this.storagePeriodTo = storagePeriodTo;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSNo() {
		return sNo;
	}

	public void setSNo(String no) {
		sNo = no;
	}

	public String[] getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(String[] emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	public String[] getLoginIds() {
		return loginIds;
	}

	public void setLoginIds(String[] loginIds) {
		this.loginIds = loginIds;
	}

	public String[] getTelephones() {
		return telephones;
	}

	public void setTelephones(String[] telephones) {
		this.telephones = telephones;
	}

	public String[] getUserNames() {
		return userNames;
	}

	public void setUserNames(String[] userNames) {
		this.userNames = userNames;
	}

	// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	// END FPT modify for Warehouse Management CR, 10/02/2014.

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

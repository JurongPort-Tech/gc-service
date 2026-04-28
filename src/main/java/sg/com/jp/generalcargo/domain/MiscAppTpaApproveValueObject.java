package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscAppTpaApproveValueObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -471386197488470397L;
	private String fromDate;
	private String toDate;
	private String durationOfStay;
	private String chassisNo;
	private String assignedArea;
	private String assignedSlot;
	private String slotType;
	private String companyName;
	private String accountNo;
	private String contactPerson;
	private String contactTel;
	private String referenceNo;
	private String sNo;
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
	 * @return the durationOfStay
	 */
	public String getDurationOfStay() {
		return durationOfStay;
	}
	/**
	 * @param durationOfStay the durationOfStay to set
	 */
	public void setDurationOfStay(String durationOfStay) {
		this.durationOfStay = durationOfStay;
	}
	/**
	 * @return the chassisNo
	 */
	public String getChassisNo() {
		return chassisNo;
	}
	/**
	 * @param chassisNo the chassisNo to set
	 */
	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}
	/**
	 * @return the assignedArea
	 */
	public String getAssignedArea() {
		return assignedArea;
	}
	/**
	 * @param assignedArea the assignedArea to set
	 */
	public void setAssignedArea(String assignedArea) {
		this.assignedArea = assignedArea;
	}
	/**
	 * @return the assignedSlot
	 */
	public String getAssignedSlot() {
		return assignedSlot;
	}
	/**
	 * @param assignedSlot the assignedSlot to set
	 */
	public void setAssignedSlot(String assignedSlot) {
		this.assignedSlot = assignedSlot;
	}
	/**
	 * @return the slotType
	 */
	public String getSlotType() {
		return slotType;
	}
	/**
	 * @param slotType the slotType to set
	 */
	public void setSlotType(String slotType) {
		this.slotType = slotType;
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
	 * @return the accountNo
	 */
	public String getAccountNo() {
		return accountNo;
	}
	/**
	 * @param accountNo the accountNo to set
	 */
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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
	 * @return the contactTel
	 */
	public String getContactTel() {
		return contactTel;
	}
	/**
	 * @param contactTel the contactTel to set
	 */
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	/**
	 * @return the referenceNo
	 */
	public String getReferenceNo() {
		return referenceNo;
	}
	/**
	 * @param referenceNo the referenceNo to set
	 */
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	/**
	 * @return the sNo
	 */
	public String getSNo() {
		return sNo;
	}
	/**
	 * @param no the sNo to set
	 */
	public void setSNo(String no) {
		sNo = no;
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

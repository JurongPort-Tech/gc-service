package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscContractValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String location = null;
	String description = null;
	String descName = null;
	String others = null;
	String fromDate = null;
	String fromTime = null;
	String toDate = null;
	String toTime = null;
	String licType = null;
	String licNo = null;
	String remarks = null;
	String waiver = null;
	String contCoNm = null;
	String contCoAddr = null;
	String contactNm = null;
	String contactNric = null;
	String designation = null;
	String[] docType = null;
	String[] docTypeCd = null;
	String[] docName = null;
	String[] uploadDttm = null;
	String[] uploadBy = null;
	String[] assignedFileName = null;
	//Added by Punitha on 06/05/2008
	String reasonWaive = null;
	
	/**
     * @return Returns the contactNm.
     */
    public String getReasonWaive() {
        return reasonWaive;
    }
    /**
     * @param contactNm The contactNm to set.
     */
    public void setReasonWaive(String reasonWaive) {
        this.reasonWaive = reasonWaive;
    }
    //End by Punitha
    /**
     * @return Returns the contactNm.
     */
    public String getContactNm() {
        return contactNm;
    }
    /**
     * @param contactNm The contactNm to set.
     */
    public void setContactNm(String contactNm) {
        this.contactNm = contactNm;
    }
    /**
     * @return Returns the contactNric.
     */
    public String getContactNric() {
        return contactNric;
    }
    /**
     * @param contactNric The contactNric to set.
     */
    public void setContactNric(String contactNric) {
        this.contactNric = contactNric;
    }
    /**
     * @return Returns the contCoAddr.
     */
    public String getContCoAddr() {
        return contCoAddr;
    }
    /**
     * @param contCoAddr The contCoAddr to set.
     */
    public void setContCoAddr(String contCoAddr) {
        this.contCoAddr = contCoAddr;
    }
    /**
     * @return Returns the contCoNm.
     */
    public String getContCoNm() {
        return contCoNm;
    }
    /**
     * @param contCoNm The contCoNm to set.
     */
    public void setContCoNm(String contCoNm) {
        this.contCoNm = contCoNm;
    }
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Returns the designation.
     */
    public String getDesignation() {
        return designation;
    }
    /**
     * @param designation The designation to set.
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    /**
     * @return Returns the fromDate.
     */
    public String getFromDate() {
        return fromDate;
    }
    /**
     * @param fromDate The fromDate to set.
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
    /**
     * @return Returns the fromTime.
     */
    public String getFromTime() {
        return fromTime;
    }
    /**
     * @param fromTime The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }
    /**
     * @return Returns the licNo.
     */
    public String getLicNo() {
        return licNo;
    }
    /**
     * @param licNo The licNo to set.
     */
    public void setLicNo(String licNo) {
        this.licNo = licNo;
    }
    /**
     * @return Returns the licType.
     */
    public String getLicType() {
        return licType;
    }
    /**
     * @param licType The licType to set.
     */
    public void setLicType(String licType) {
        this.licType = licType;
    }
    /**
     * @return Returns the location.
     */
    public String getLocation() {
        return location;
    }
    /**
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * @return Returns the others.
     */
    public String getOthers() {
        return others;
    }
    /**
     * @param others The others to set.
     */
    public void setOthers(String others) {
        this.others = others;
    }
    /**
     * @return Returns the remarks.
     */
    public String getRemarks() {
        return remarks;
    }
    /**
     * @param remarks The remarks to set.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    /**
     * @return Returns the toDate.
     */
    public String getToDate() {
        return toDate;
    }
    /**
     * @param toDate The toDate to set.
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    /**
     * @return Returns the toTime.
     */
    public String getToTime() {
        return toTime;
    }
    /**
     * @param toTime The toTime to set.
     */
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
    /**
     * @return Returns the waiver.
     */
    public String getWaiver() {
        return waiver;
    }
    /**
     * @param waiver The waiver to set.
     */
    public void setWaiver(String waiver) {
        this.waiver = waiver;
    }
    
    /**
     * @return Returns the docName.
     */
    public String[] getDocName() {
        return docName;
    }
    /**
     * @param docName The docName to set.
     */
    public void setDocName(String[] docName) {
        this.docName = docName;
    }
    /**
     * @return Returns the docType.
     */
    public String[] getDocType() {
        return docType;
    }
    /**
     * @param docType The docType to set.
     */
    public void setDocType(String[] docType) {
        this.docType = docType;
    }
    
    /**
     * @return Returns the uploadBy.
     */
    public String[] getUploadBy() {
        return uploadBy;
    }
    /**
     * @param uploadBy The uploadBy to set.
     */
    public void setUploadBy(String[] uploadBy) {
        this.uploadBy = uploadBy;
    }
    /**
     * @return Returns the uploadDttm.
     */
    public String[] getUploadDttm() {
        return uploadDttm;
    }
    /**
     * @param uploadDttm The uploadDttm to set.
     */
    public void setUploadDttm(String[] uploadDttm) {
        this.uploadDttm = uploadDttm;
    }
    
    /**
     * @return Returns the docTypeCd.
     */
    public String[] getDocTypeCd() {
        return docTypeCd;
    }
    /**
     * @param docTypeCd The docTypeCd to set.
     */
    public void setDocTypeCd(String[] docTypeCd) {
        this.docTypeCd = docTypeCd;
    }
    /**
     * @return Returns the assignedFileName.
     */
    public String[] getAssignedFileName() {
        return assignedFileName;
    }
    /**
     * @param assignedFileName The assignedFileName to set.
     */
    public void setAssignedFileName(String[] assignedFileName) {
        this.assignedFileName = assignedFileName;
    }
    /**
     * @return Returns the descName.
     */
    public String getDescName() {
        return descName;
    }
    /**
     * @param descName The descName to set.
     */
    public void setDescName(String descName) {
        this.descName = descName;
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

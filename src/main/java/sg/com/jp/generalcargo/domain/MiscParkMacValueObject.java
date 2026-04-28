package sg.com.jp.generalcargo.domain;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscParkMacValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String macType;
    private String fromDate;
    private String toDate;
    private String fromTime;
    private String toTime;
    private String remarks;
    private String[] regNbr;
    private String[] docType;
    private String[] docTypeCd;
    private String[] docName;
    private String[] uploadDttm;
    private String[] uploadBy;
    private String[] assignedFileName;
    
    private String[] liftCapacity;
    private String[] insuranceNbr;
    private String[] insExpDttm;
    private String[] phaseOutDt;
    //Added by Punitha on 22/01/2008
    private String regNbrValue;
    private String liftCapacityValue;
    private String insuranceNbrValue;
    private String insExpDttmValue;
    private String phaseOutDtValue;
    private List<MiscParkMacValueObject> macDetList;
    private List<MiscParkMacValueObject> details;
    private MiscParkMacValueObject dvo;
    
    
    //End
    

    
    /**
     * @return Returns the docName.
     */
    public String[] getDocName() {
        return docName;
    }
    public List<MiscParkMacValueObject> getMacDetList() {
		return macDetList;
	}
	public void setMacDetList(List<MiscParkMacValueObject> macDetList) {
		this.macDetList = macDetList;
	}
	public MiscParkMacValueObject getDvo() {
		return dvo;
	}
	public void setDvo(MiscParkMacValueObject dvo) {
		this.dvo = dvo;
	}
	public List<MiscParkMacValueObject> getDetails() {
		return details;
	}
	public void setDetails(List<MiscParkMacValueObject> details) {
		this.details = details;
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
     * @return Returns the macType.
     */
    public String getMacType() {
        return macType;
    }
    /**
     * @param macType The macType to set.
     */
    public void setMacType(String macType) {
        this.macType = macType;
    }
    /**
     * @return Returns the regNbr.
     */
    public String[] getRegNbr() {
        return regNbr;
    }
    /**
     * @param regNbr The regNbr to set.
     */
    public void setRegNbr(String[] regNbr) {
        this.regNbr = regNbr;
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
     * @return Returns the insExpDttm.
     */
    public String[] getInsExpDttm() {
        return insExpDttm;
    }
    /**
     * @param insExpDttm The insExpDttm to set.
     */
    public void setInsExpDttm(String[] insExpDttm) {
        this.insExpDttm = insExpDttm;
    }
    /**
     * @return Returns the insuranceNbr.
     */
    public String[] getInsuranceNbr() {
        return insuranceNbr;
    }
    /**
     * @param insuranceNbr The insuranceNbr to set.
     */
    public void setInsuranceNbr(String[] insuranceNbr) {
        this.insuranceNbr = insuranceNbr;
    }
    //Added by Punitha on 22/01/2008
    
    /**
     * @return Returns the insExpDttmValue.
     */
    public String getInsExpDttmValue() {
        return insExpDttmValue;
    }
    /**
     * @param insExpDttmValue The insExpDttm to set.
     */
    public void setInsExpDttmValue(String insExpDttmValue) {
        this.insExpDttmValue = insExpDttmValue;
    }
    /**
     * @return Returns the insuranceNbrValue.
     */
    public String getInsuranceNbrValue() {
        return insuranceNbrValue;
    }
    /**
     * @param insuranceNbrValue The insuranceNbr to set.
     */
    public void setInsuranceNbrValue(String insuranceNbrValue) {
        this.insuranceNbrValue = insuranceNbrValue;
    }
    /**
     * @return Returns the regNbrValue.
     */
    public String getRegNbrValue() {
        return regNbrValue;
    }
    /**
     * @param regNbrValue The regNbr to set.
     */
    public void setRegNbrValue(String regNbrValue) {
        this.regNbrValue = regNbrValue;
    }
    /**
     * @return Returns the liftCapacityValue.
     */
    public String getLiftCapacityValue() {
        return liftCapacityValue;
    }
    /**
     * @param liftCapacityValue The liftCapacity to set.
     */
    public void setLiftCapacityValue(String liftCapacityValue) {
        this.liftCapacityValue = liftCapacityValue;
    }
    /**
     * @return Returns the phaseOutDtValue.
     */
    public String getPhaseOutDtValue() {
        return phaseOutDtValue;
    }
    /**
     * @param phaseOutDtValue The phaseOutDt to set.
     */
    public void setPhaseOutDtValue(String phaseOutDtValue) {
        this.phaseOutDtValue = phaseOutDtValue;
    }
    //End By Punitha
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
     * @return Returns the liftCapacity.
     */
    public String[] getLiftCapacity() {
        return liftCapacity;
    }
    /**
     * @param liftCapacity The liftCapacity to set.
     */
    public void setLiftCapacity(String[] liftCapacity) {
        this.liftCapacity = liftCapacity;
    }
    /**
     * @return Returns the phaseOutDt.
     */
    public String[] getPhaseOutDt() {
        return phaseOutDt;
    }
    /**
     * @param phaseOutDt The phaseOutDt to set.
     */
    public void setPhaseOutDt(String[] phaseOutDt) {
        this.phaseOutDt = phaseOutDt;
    }
    
//    /**
//     * @return Returns the macDetList.
//     */
//    public ArrayList getMacDetList() {
//        return macDetList;
//    }
//    /**
//     * @param phaseOutDt The phaseOutDt to set.
//     */
//    public void setMacDetList(ArrayList macDetList) {
//        this.macDetList = macDetList;
//    }
//    
    @Override
   	public String toString() {
   		try {
   			return new ObjectMapper().writeValueAsString(this);
   		} catch (JsonProcessingException e) {
   			return "";
   		}
   	}
}

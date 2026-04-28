package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscSpaceValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String spaceType;
    private String purpose;
    private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private String reason;
    private String billNbr;
    private String marks;
    private String packages;
    private String cargoDesc;
    private String tonnage;
    private String newMarks;
    private String newPackages;
    private String newCargoDesc;
    private String newTonnage;
    private String[] bayNbr;
    private String[] areaUsed;
    private String[] opsStartDttm;
    private String[] opsEndDttm;
    
    /**
     * @return Returns the billNbr.
     */
    public String getBillNbr() {
        return billNbr;
    }
    /**
     * @param billNbr The billNbr to set.
     */
    public void setBillNbr(String billNbr) {
        this.billNbr = billNbr;
    }
    /**
     * @return Returns the cargoDesc.
     */
    public String getCargoDesc() {
        return cargoDesc;
    }
    /**
     * @param cargoDesc The cargoDesc to set.
     */
    public void setCargoDesc(String cargoDesc) {
        this.cargoDesc = cargoDesc;
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
     * @return Returns the marks.
     */
    public String getMarks() {
        return marks;
    }
    /**
     * @param marks The marks to set.
     */
    public void setMarks(String marks) {
        this.marks = marks;
    }
    /**
     * @return Returns the newCargoDesc.
     */
    public String getNewCargoDesc() {
        return newCargoDesc;
    }
    /**
     * @param newCargoDesc The newCargoDesc to set.
     */
    public void setNewCargoDesc(String newCargoDesc) {
        this.newCargoDesc = newCargoDesc;
    }
    /**
     * @return Returns the newMarks.
     */
    public String getNewMarks() {
        return newMarks;
    }
    /**
     * @param newMarks The newMarks to set.
     */
    public void setNewMarks(String newMarks) {
        this.newMarks = newMarks;
    }
    /**
     * @return Returns the newPackages.
     */
    public String getNewPackages() {
        return newPackages;
    }
    /**
     * @param newPackages The newPackages to set.
     */
    public void setNewPackages(String newPackages) {
        this.newPackages = newPackages;
    }
    /**
     * @return Returns the newTonnage.
     */
    public String getNewTonnage() {
        return newTonnage;
    }
    /**
     * @param newTonnage The newTonnage to set.
     */
    public void setNewTonnage(String newTonnage) {
        this.newTonnage = newTonnage;
    }
    /**
     * @return Returns the packages.
     */
    public String getPackages() {
        return packages;
    }
    /**
     * @param packages The packages to set.
     */
    public void setPackages(String packages) {
        this.packages = packages;
    }
    /**
     * @return Returns the purpose.
     */
    public String getPurpose() {
        return purpose;
    }
    /**
     * @param purpose The purpose to set.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    /**
     * @return Returns the reason.
     */
    public String getReason() {
        return reason;
    }
    /**
     * @param reason The reason to set.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    /**
     * @return Returns the spaceType.
     */
    public String getSpaceType() {
        return spaceType;
    }
    /**
     * @param spaceType The spaceType to set.
     */
    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
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
     * @return Returns the tonnage.
     */
    public String getTonnage() {
        return tonnage;
    }
    /**
     * @param tonnage The tonnage to set.
     */
    public void setTonnage(String tonnage) {
        this.tonnage = tonnage;
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
     * @return Returns the areaUsed.
     */
    public String[] getAreaUsed() {
        return areaUsed;
    }
    /**
     * @param areaUsed The areaUsed to set.
     */
    public void setAreaUsed(String[] areaUsed) {
        this.areaUsed = areaUsed;
    }
    /**
     * @return Returns the bayNbr.
     */
    public String[] getBayNbr() {
        return bayNbr;
    }
    /**
     * @param bayNbr The bayNbr to set.
     */
    public void setBayNbr(String[] bayNbr) {
        this.bayNbr = bayNbr;
    }
    /**
     * @return Returns the opsEndDttm.
     */
    public String[] getOpsEndDttm() {
        return opsEndDttm;
    }
    /**
     * @param opsEndDttm The opsEndDttm to set.
     */
    public void setOpsEndDttm(String[] opsEndDttm) {
        this.opsEndDttm = opsEndDttm;
    }
    /**
     * @return Returns the opsStartDttm.
     */
    public String[] getOpsStartDttm() {
        return opsStartDttm;
    }
    /**
     * @param opsStartDttm The opsStartDttm to set.
     */
    public void setOpsStartDttm(String[] opsStartDttm) {
        this.opsStartDttm = opsStartDttm;
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

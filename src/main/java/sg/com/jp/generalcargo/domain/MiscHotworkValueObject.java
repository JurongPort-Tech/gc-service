package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscHotworkValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String location;
    private String description;
    private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private String totStandbyHr;
    private String[] standbyFrDttm;
    private String[] standbyFrTime;
    private String[] standbyToTime;
    private String[] chargeTime;
    private String[] fireManNm;
    private String inspectInd;
    
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
     * @return Returns the chargeTime.
     */
    public String[] getChargeTime() {
        return chargeTime;
    }
    /**
     * @param chargeTime The chargeTime to set.
     */
    public void setChargeTime(String[] chargeTime) {
        this.chargeTime = chargeTime;
    }
    /**
     * @return Returns the standbyFrDttm.
     */
    public String[] getStandbyFrDttm() {
        return standbyFrDttm;
    }
    /**
     * @param standbyFrDttm The standbyFrDttm to set.
     */
    public void setStandbyFrDttm(String[] standbyFrDttm) {
        this.standbyFrDttm = standbyFrDttm;
    }

    /**
     * @return Returns the fireManNm.
     */
    public String[] getFireManNm() {
        return fireManNm;
    }
    /**
     * @param fireManNm The fireManNm to set.
     */
    public void setFireManNm(String[] fireManNm) {
        this.fireManNm = fireManNm;
    }
    
    /**
     * @return Returns the standbyFrTime.
     */
    public String[] getStandbyFrTime() {
        return standbyFrTime;
    }
    /**
     * @param standbyFrTime The standbyFrTime to set.
     */
    public void setStandbyFrTime(String[] standbyFrTime) {
        this.standbyFrTime = standbyFrTime;
    }
    /**
     * @return Returns the standbyToTime.
     */
    public String[] getStandbyToTime() {
        return standbyToTime;
    }
    /**
     * @param standbyToTime The standbyToTime to set.
     */
    public void setStandbyToTime(String[] standbyToTime) {
        this.standbyToTime = standbyToTime;
    }
    /**
     * @return Returns the totStandbyHr.
     */
    public String getTotStandbyHr() {
        return totStandbyHr;
    }
    /**
     * @param totStandbyHr The totStandbyHr to set.
     */
    public void setTotStandbyHr(String totStandbyHr) {
        this.totStandbyHr = totStandbyHr;
    }
    /**
     * @return Returns the inspectInd.
     */
    public String getInspectInd(){
        return inspectInd;
    }
    /**
     * @param inspectInd The inspectInd to set.
     */
    public void setInspectInd(String inspectInd) {
        this.inspectInd = inspectInd;
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

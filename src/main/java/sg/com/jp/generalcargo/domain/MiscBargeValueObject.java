package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscBargeValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bargeName;
    private String bargeLOA;
    private String draft;
    private String tugboat;
    private String contactNo;
    private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private String motherShip;
    private String berthNo;
    private String dg;
    private String cargoType;
    private String className;
    private String allocBerthNo;
    private String wharfMarkFr;
    private String wharfMarkTo;
    private String bargeAtbDttm;
    private String bargeAtbTime;
    private String bargeAtuDttm;
    private String bargeAtuTime;
    private String actwharfMarkFr;
    private String actwharfMarkTo;
    
    /**
     * @return Returns the bargeLOA.
     */
    public String getBargeLOA() {
        return bargeLOA;
    }
    /**
     * @param bargeLOA The bargeLOA to set.
     */
    public void setBargeLOA(String bargeLOA) {
        this.bargeLOA = bargeLOA;
    }
    /**
     * @return Returns the bargeName.
     */
    public String getBargeName() {
        return bargeName;
    }
    /**
     * @param bargeName The bargeName to set.
     */
    public void setBargeName(String bargeName) {
        this.bargeName = bargeName;
    }
    /**
     * @return Returns the berthNo.
     */
    public String getBerthNo() {
        return berthNo;
    }
    /**
     * @param berthNo The berthNo to set.
     */
    public void setBerthNo(String berthNo) {
        this.berthNo = berthNo;
    }
    /**
     * @return Returns the cargoType.
     */
    public String getCargoType() {
        return cargoType;
    }
    /**
     * @param cargoType The cargoType to set.
     */
    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return Returns the contactNo.
     */
    public String getContactNo() {
        return contactNo;
    }
    /**
     * @param contactNo The contactNo to set.
     */
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    /**
     * @return Returns the dg.
     */
    public String getDg() {
        return dg;
    }
    /**
     * @param dg The dg to set.
     */
    public void setDg(String dg) {
        this.dg = dg;
    }
    /**
     * @return Returns the draft.
     */
    public String getDraft() {
        return draft;
    }
    /**
     * @param draft The draft to set.
     */
    public void setDraft(String draft) {
        this.draft = draft;
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
     * @return Returns the motherShip.
     */
    public String getMotherShip() {
        return motherShip;
    }
    /**
     * @param motherShip The motherShip to set.
     */
    public void setMotherShip(String motherShip) {
        this.motherShip = motherShip;
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
     * @return Returns the tugboat.
     */
    public String getTugboat() {
        return tugboat;
    }
    /**
     * @param tugboat The tugboat to set.
     */
    public void setTugboat(String tugboat) {
        this.tugboat = tugboat;
    }
    
    /**
     * @return Returns the allocBerthNo.
     */
    public String getAllocBerthNo() {
        return allocBerthNo;
    }
    /**
     * @param allocBerthNo The allocBerthNo to set.
     */
    public void setAllocBerthNo(String allocBerthNo) {
        this.allocBerthNo = allocBerthNo;
    }
    /**
     * @return Returns the wharfMarkFr.
     */
    public String getWharfMarkFr() {
        return wharfMarkFr;
    }
    /**
     * @param wharfMarkFr The wharfMarkFr to set.
     */
    public void setWharfMarkFr(String wharfMarkFr) {
        this.wharfMarkFr = wharfMarkFr;
    }
    /**
     * @return Returns the wharfMarkTo.
     */
    public String getWharfMarkTo() {
        return wharfMarkTo;
    }
    /**
     * @param wharfMarkTo The wharfMarkTo to set.
     */
    public void setWharfMarkTo(String wharfMarkTo) {
        this.wharfMarkTo = wharfMarkTo;
    }
    /**
     * @return Returns the bargeAtbDttm.
     */
    public String getBargeAtbDttm() {
        return bargeAtbDttm;
    }
    /**
     * @param bargeAtbDttm The bargeAtbDttm to set.
     */
    public void setBargeAtbDttm(String bargeAtbDttm) {
        this.bargeAtbDttm = bargeAtbDttm;
    }
    /**
     * @return Returns the bargeAtbTime.
     */
    public String getBargeAtbTime() {
        return bargeAtbTime;
    }
    /**
     * @param bargeAtbTime The bargeAtbTime to set.
     */
    public void setBargeAtbTime(String bargeAtbTime) {
        this.bargeAtbTime = bargeAtbTime;
    }
    /**
     * @return Returns the bargeAtuDttm.
     */
    public String getBargeAtuDttm() {
        return bargeAtuDttm;
    }
    /**
     * @param bargeAtuDttm The bargeAtuDttm to set.
     */
    public void setBargeAtuDttm(String bargeAtuDttm) {
        this.bargeAtuDttm = bargeAtuDttm;
    }
    /**
     * @return Returns the bargeAtuTime.
     */
    public String getBargeAtuTime() {
        return bargeAtuTime;
    }
    /**
     * @param bargeAtuTime The bargeAtuTime to set.
     */
    public void setBargeAtuTime(String bargeAtuTime) {
        this.bargeAtuTime = bargeAtuTime;
    }
	/**
	 * @return the actwharfMarkFr
	 */
	public String getActwharfMarkFr() {
		return actwharfMarkFr;
	}
	/**
	 * @param actwharfMarkFr the actwharfMarkFr to set
	 */
	public void setActwharfMarkFr(String actwharfMarkFr) {
		this.actwharfMarkFr = actwharfMarkFr;
	}
	/**
	 * @return the actwharfMarkTo
	 */
	public String getActwharfMarkTo() {
		return actwharfMarkTo;
	}
	/**
	 * @param actwharfMarkTo the actwharfMarkTo to set
	 */
	public void setActwharfMarkTo(String actwharfMarkTo) {
		this.actwharfMarkTo = actwharfMarkTo;
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

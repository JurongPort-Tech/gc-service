package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscVehValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private String noNights;
    private String actFromDate;
    private String actFromTime;
    private String actToDate;
    private String actToTime;
    private String actNoNights;

    private String parkReason;
    private String[] vehChasNbr;
    private String[] cntrNbr;
    private String[] asnNbr;
	// START 01-Mar-2011 - TPA – ThangNC added for trailer parking applications
    private String[] remarks;
    private String[] preferredArea;
    private String[] area;
    private String[] slot;
    private String noHours;
    private String cargoType;
    private String applicationReason;
    private String actNoHours;
	// START 01-Mar-2011 - TPA – ThangNC added for trailer parking applications
    
	// START 02-Mar-2011 - TPA – Thanhnv2 added for trailer parking applications
    private String[] dgInfo;
    private String[] oogInfo;
	// START 02-Mar-2011 - TPA – Thanhnv2 added for trailer parking applications
	/**
     * @return Returns the asnNbr.
     */
    public String[] getAsnNbr() {
        return asnNbr;
    }
    /**
     * @param asnNbr The asnNbr to set.
     */
    public void setAsnNbr(String[] asnNbr) {
        this.asnNbr = asnNbr;
    }
    /**
     * @return Returns the cntrNbr.
     */
    public String[] getCntrNbr() {
        return cntrNbr;
    }
    /**
     * @param cntrNbr The cntrNbr to set.
     */
    public void setCntrNbr(String[] cntrNbr) {
        this.cntrNbr = cntrNbr;
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
     * @return Returns the noNights.
     */
    public String getNoNights() {
        return noNights;
    }
    /**
     * @param noNights The noNights to set.
     */
    public void setNoNights(String noNights) {
        this.noNights = noNights;
    }
    /**
     * @return Returns the parkReason.
     */
    public String getParkReason() {
        return parkReason;
    }
    /**
     * @param parkReason The parkReason to set.
     */
    public void setParkReason(String parkReason) {
        this.parkReason = parkReason;
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
     * @return Returns the vehChasNbr.
     */
    public String[] getVehChasNbr() {
        return vehChasNbr;
    }
    /**
     * @param vehChasNbr The vehChasNbr to set.
     */
    public void setVehChasNbr(String[] vehChasNbr) {
        this.vehChasNbr = vehChasNbr;
    }
    
    /**
     * @return Returns the actFromDate.
     */
    public String getActFromDate() {
        return actFromDate;
    }
    /**
     * @param actFromDate The actFromDate to set.
     */
    public void setActFromDate(String actFromDate) {
        this.actFromDate = actFromDate;
    }
    /**
     * @return Returns the actNoNights.
     */
    public String getActNoNights() {
        return actNoNights;
    }
    /**
     * @param actNoNights The actNoNights to set.
     */
    public void setActNoNights(String actNoNights) {
        this.actNoNights = actNoNights;
    }
    /**
     * @return Returns the actToDate.
     */
    public String getActToDate() {
        return actToDate;
    }
    /**
     * @param actToDate The actToDate to set.
     */
    public void setActToDate(String actToDate) {
        this.actToDate = actToDate;
    }
    /**
     * @return Returns the actFromTime.
     */
    public String getActFromTime() {
        return actFromTime;
    }
    /**
     * @param actFromTime The actFromTime to set.
     */
    public void setActFromTime(String actFromTime) {
        this.actFromTime = actFromTime;
    }
    /**
     * @return Returns the actToTime.
     */
    public String getActToTime() {
        return actToTime;
    }
    /**
     * @param actToTime The actToTime to set.
     */
    public void setActToTime(String actToTime) {
        this.actToTime = actToTime;
    }
    /**
     * @return Returns the fromTime.
     */
    public String getFromTime() {
        return fromTime;
    }
    public String[] getRemarks() {
		return remarks;
	}
	public void setRemarks(String[] remarks) {
		this.remarks = remarks;
	}
	public String[] getPreferredArea() {
		return preferredArea;
	}
	public void setPreferredArea(String[] preferredArea) {
		this.preferredArea = preferredArea;
	}
	public String getNoHours() {
		return noHours;
	}
	public void setNoHours(String noHours) {
		this.noHours = noHours;
	}
	public String getCargoType() {
		return cargoType;
	}
	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}
	public String getApplicationReason() {
		return applicationReason;
	}
	public void setApplicationReason(String applicationReason) {
		this.applicationReason = applicationReason;
	}
	/**
     * @param fromTime The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
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
    
    public String getActNoHours() {
		return actNoHours;
	}
	public void setActNoHours(String actNoHours) {
		this.actNoHours = actNoHours;
	}
	public String[] getArea() {
		return area;
	}
	public void setArea(String[] area) {
		this.area = area;
	}
	public String[] getSlot() {
		return slot;
	}
	public void setSlot(String[] slot) {
		this.slot = slot;
	}
	/**
	 * @return the dgInfo
	 */
	public String[] getDgInfo() {
		return dgInfo;
	}
	/**
	 * @param dgInfo the dgInfo to set
	 */
	public void setDgInfo(String[] dgInfo) {
		this.dgInfo = dgInfo;
	}
	/**
	 * @return the oogInfo
	 */
	public String[] getOogInfo() {
		return oogInfo;
	}
	/**
	 * @param oogInfo the oogInfo to set
	 */
	public void setOogInfo(String[] oogInfo) {
		this.oogInfo = oogInfo;
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

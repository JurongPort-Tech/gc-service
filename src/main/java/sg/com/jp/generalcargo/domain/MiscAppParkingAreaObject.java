package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscAppParkingAreaObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String areaCode;
	private String noOfSlot;
	private String slotNumber;
	private String slotType;
	private String slotStatus;
	
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getNoOfSlot() {
		return noOfSlot;
	}
	public void setNoOfSlot(String noOfSlot) {
		this.noOfSlot = noOfSlot;
	}
	public String getSlotNumber() {
		return slotNumber;
	}
	public void setSlotNumber(String slotNumber) {
		this.slotNumber = slotNumber;
	}
	public String getSlotType() {
		return slotType;
	}
	public void setSlotType(String slotType) {
		this.slotType = slotType;
	}
	public String getSlotStatus() {
		return slotStatus;
	}
	public void setSlotStatus(String slotStatus) {
		this.slotStatus = slotStatus;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		MiscAppParkingAreaObject paramObj = (MiscAppParkingAreaObject) arg0;
	
		if(paramObj == null) {
			return false;
		}
		
		if (this.getSlotNumber() == null ) {
			return (paramObj.getSlotNumber() == null);
		} else {
			return this.getSlotNumber().equals(paramObj.getSlotNumber());
		}
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

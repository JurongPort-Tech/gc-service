package sg.com.jp.generalcargo.domain;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EnquireQueryObject implements TopsIObject  {

private static final long serialVersionUID = -798698662354385918L;
	
	private String startDate;
	
	private String startTime;
	
	private String endDate;
	
	private String endTime;
	
	private String areaCode;
	
	private String slotType;
	
	private int[] intializeCountSlotNumber;

	private boolean isStaff;
	
	private boolean isDownload;
	
	//Begin update on 26/09/13 by thanhbtl6b
	
	//new trailer type
  
	private String trailerType;
	
	//new trailer size
	private int trailerSize;
	
//	public static final int NUMBER_OF_TIME_SLOT = 6; // change from 48 to 6
	
	//End update on 26/09/13 by thanhbtl6b
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getSlotType() {
		return slotType;
	}

	public void setSlotType(String slotType) {
		this.slotType = slotType;
	}

	public int[] getIntializeCountSlotNumber(int numberOfTimeSlot) {
		if(intializeCountSlotNumber == null) { 
			return intializeCountSlotNumber;
		}

		int [] tmp = new int[numberOfTimeSlot];
		System.arraycopy(intializeCountSlotNumber, 0, tmp, 0, numberOfTimeSlot);
		return tmp;
	
	}

	public void setIntializeCountSlotNumber(int[] intializeCountSlotNumber) {
		this.intializeCountSlotNumber = intializeCountSlotNumber;
	}

	public boolean isStaff() {
		return isStaff;
	}

	public void setStaff(boolean isStaff) {
		this.isStaff = isStaff;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getTrailerType() {
		return trailerType;
	}

	public void setTrailerType(String trailerType) {
		this.trailerType = trailerType;
	}

	public int getTrailerSize() {
		return trailerSize;
	}

	public void setTrailerSize(int trailerSize) {
		this.trailerSize = trailerSize;
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

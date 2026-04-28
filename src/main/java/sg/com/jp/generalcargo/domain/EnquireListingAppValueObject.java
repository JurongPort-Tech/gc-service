package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.MiscAppConstValueObject;

public class EnquireListingAppValueObject implements TopsIObject {
	private static final long serialVersionUID = 4618930304758089051L;

	private String areaCode;

	private String slotNumber;

	private String slotType;

	private String slotStatus;

	private String miscSeqNumber;

	private String appTypeCode;

	private String appTypeName;

	private String vehChassNo;

	private String fromDate;

	private String fromTime;

	private String toDate;

	private String toTime;

	private String remarks;

	String[] cellValueArray;

	private String slotTypeName;

	// trailer: added by thanhbtl6b on 20/09/13
	private String trailerTypeCode;

	private String trailerTypeName;

	private int trailerSize;
	// end

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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

	public String getMiscSeqNumber() {
		return miscSeqNumber;
	}

	public void setMiscSeqNumber(String miscSeqNumber) {
		this.miscSeqNumber = miscSeqNumber;
	}

	public String getVehChassNo() {
		return vehChassNo;
	}

	public void setVehChassNo(String vehChassNo) {
		this.vehChassNo = vehChassNo;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String[] getCellValueArray() {
		return cellValueArray;
	}

	public void setCellValueArray(String[] cellValueArray) {
		this.cellValueArray = cellValueArray;
	}

	public String getSlotTypeName() {
		if (slotType != null) {
			return (String) MiscAppConstValueObject.SLOT_TYPE_TABLE.get(slotType);
		}
		return slotTypeName;
	}

	public void setSlotTypeName(String slotTypeName) {
		this.slotTypeName = slotTypeName;
	}

	public String getAppTypeCode() {
		return appTypeCode;
	}

	public void setAppTypeCode(String appTypeCode) {
		this.appTypeCode = appTypeCode;
	}

	public String getAppTypeName() {
		return appTypeName;
	}

	public void setAppTypeName(String appTypeName) {
		this.appTypeName = appTypeName;
	}

	public String getTrailerTypeCode() {
		return trailerTypeCode;
	}

	public void setTrailerTypeCode(String trailerTypeCode) {
		this.trailerTypeCode = trailerTypeCode;
	}

	public String getTrailerTypeName() {
		return trailerTypeName;
	}

	public void setTrailerTypeName(String trailerTypeName) {
		this.trailerTypeName = trailerTypeName;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Revision Change ================ Author Date Request No Description of Change
 * Version
 * --------------------------------------------------------------------------------------------------------
 * HanhTD 9 March 2011 TPA Object that represent a row of summary table 1.0 of
 * Enquire availability Parking Area Slot function.
 * 
 * ThanhBTL6B 25 Sept 2013 TPA Update Trailer Size, Trailer Type for new TPA 2.0
 *
 */

public class EnquireSummarySlotValueObject implements Serializable {

	private static final long serialVersionUID = -8215350480086519128L;

	private String areaCode;

	private String slotType;

	private int[] numberFreeSlot;

	/* use only for tempt row from query object. */
	private String slotNumber;

	private Date fromDate;

	private Date toDate;

	private int[] flagColunm;

	// new trailer type

	private String trailerType;
	// new trailer size
	private int trailerSize;

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

	public int[] getNumberFreeSlot() {
		return numberFreeSlot;
	}

	public void setNumberFreeSlot(int[] numberFreeSlot) {
		this.numberFreeSlot = numberFreeSlot;
	}

	public String getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(String slotNumber) {
		this.slotNumber = slotNumber;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int[] getFlagColunm() {
		return flagColunm;
	}

	public void setFlagColunm(int[] flagColunm) {
		this.flagColunm = flagColunm;
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

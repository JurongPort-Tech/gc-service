package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RainRecordsValueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, String> rainCategory;

	private String startDate;

	private String startTime;

	private String endDate;

	private String endTime;

	private String selectedRaincategorycode;

	private String selectedRaincategorytext;

	private String rainCNT;

	private String selectedRainLocationCode = "";

	private String selectedRainLocationText = "";

	private String lastModifiedBy = "";

	private double timeDifference = 0.0; // in seconds

	public double getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(double timeDifference) {
		this.timeDifference = timeDifference;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getSelectedRainLocationText() {
		return selectedRainLocationText;
	}

	public void setSelectedRainLocationText(String selectedRainLocationText) {
		this.selectedRainLocationText = selectedRainLocationText;
	}

	public String getSelectedRainLocationCode() {
		return selectedRainLocationCode;
	}

	public void setSelectedRainLocationCode(String selectedRainLocationCode) {
		this.selectedRainLocationCode = selectedRainLocationCode;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the rainCategory
	 */
	public Map<String, String> getRainCategory() {
		return rainCategory;
	}

	/**
	 * @param rainCategory the rainCategory to set
	 */
	public void setRainCategory(Map<String, String> rainCategory) {
		this.rainCategory = rainCategory;
	}

	/**
	 * @return the selectedRaincategorycode
	 */
	public String getSelectedRaincategorycode() {
		return selectedRaincategorycode;
	}

	/**
	 * @param selectedRaincategorycode the selectedRaincategorycode to set
	 */
	public void setSelectedRaincategorycode(String selectedRaincategorycode) {
		this.selectedRaincategorycode = selectedRaincategorycode;
	}

	/**
	 * @return the selectedRaincategorytext
	 */
	public String getSelectedRaincategorytext() {
		return selectedRaincategorytext;
	}

	/**
	 * @param selectedRaincategorytext the selectedRaincategorytext to set
	 */
	public void setSelectedRaincategorytext(String selectedRaincategorytext) {
		this.selectedRaincategorytext = selectedRaincategorytext;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the rainCNT
	 */
	public String getRainCNT() {
		return rainCNT;
	}

	/**
	 * @param rainCNT the rainCNT to set
	 */
	public void setRainCNT(String rainCNT) {
		this.rainCNT = rainCNT;
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

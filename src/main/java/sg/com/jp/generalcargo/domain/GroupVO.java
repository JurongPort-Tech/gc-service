package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GroupVO {

	private String groupId;
	private String groupName;
	private double minAmount;
	private double maxAmount;
	private String status;
	private String vesselType;
	

	/**
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return
	 */
	public double getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount
	 */
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return
	 */
	public double getMinAmount() {
		return minAmount;
	}
	/**
	 * @param minAmount
	 */
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}
	/**
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return
	 */
	public String getVesselType() {
		return vesselType;
	}
	/**
	 * @param vesselType
	 */
	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
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

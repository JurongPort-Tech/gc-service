package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

public class VesselSearchResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vesselCode;
	private String vesselName;
	private String vesselVoy; 
	private String displayName; 
	
	public String getVesselCode() {
		return vesselCode;
	}
	public void setVesselCode(String vesselCode) {
		this.vesselCode = vesselCode;
	}
	public String getVesselName() {
		return vesselName;
	}
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}
	public String getVesselVoy() {
		return vesselVoy;
	}
	public void setVesselVoy(String vesselVoy) {
		this.vesselVoy = vesselVoy;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@Override
	public String toString() {
		return "VesselSearchResponse [vesselCode=" + vesselCode + ", vesselName=" + vesselName + ", vesselVoy="
				+ vesselVoy + ", displayName=" + displayName + "]";
	}
	
	
	
}

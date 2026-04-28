package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FTZGroup8VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FTZGroup8RFF rff = new FTZGroup8RFF(); // mandatory for BL number
	private List<FTZGroup8LOCVO> loc = new ArrayList<>(); // optional, max 99
	private List<FTZGroup8GEIVO> gei = new ArrayList<>(); // max 9
	
	public FTZGroup8RFF getRff() {
		return rff;
	}
	public void setRff(FTZGroup8RFF rff) {
		this.rff = rff;
	}
	public List<FTZGroup8LOCVO> getLoc() {
		return loc;
	}
	public void setLoc(List<FTZGroup8LOCVO> loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
	public List<FTZGroup8GEIVO> getGei() {
		return gei;
	}
	public void setGei(List<FTZGroup8GEIVO> gei) {
		this.gei = gei;
	}
}

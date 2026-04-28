package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group4
public class FTZGroup4VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FTZG4TDTVO tdt = new FTZG4TDTVO();
	private List<FTZG4LOCVO> loc = new ArrayList<>(); // optional, max 99

	public FTZG4TDTVO getTdt() {
		return tdt;
	}

	public void setTdt(FTZG4TDTVO tdt) {
		this.tdt = tdt;
	}

	public List<FTZG4LOCVO> getLoc() {
		return loc;
	}

	public void setLoc(List<FTZG4LOCVO> loc) {
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
	
}

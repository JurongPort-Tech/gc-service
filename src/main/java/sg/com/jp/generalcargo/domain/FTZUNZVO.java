package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// UNZ INTERCHANGE TRAILER
public class FTZUNZVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// interchange control count
	private int interchange_ctrl_cnt;
	
	// interchange control reference
	private String interchange_ctrl_ref;

	public int getInterchange_ctrl_cnt() {
		return interchange_ctrl_cnt;
	}

	public void setInterchange_ctrl_cnt(int interchange_ctrl_cnt) {
		this.interchange_ctrl_cnt = interchange_ctrl_cnt;
	}

	public String getInterchange_ctrl_ref() {
		return interchange_ctrl_ref;
	}

	public void setInterchange_ctrl_ref(String interchange_ctrl_ref) {
		this.interchange_ctrl_ref = interchange_ctrl_ref;
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

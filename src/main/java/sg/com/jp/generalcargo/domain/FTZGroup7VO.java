package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FTZGroup7VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6956784528466049504L;
	
	private FTZG7CNIVO cni = new FTZG7CNIVO();

	public FTZG7CNIVO getCni() {
		return cni;
	}

	public void setCni(FTZG7CNIVO cni) {
		this.cni = cni;
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

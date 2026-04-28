package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FTZGroup11VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FTZGroup11NADVO nad = new FTZGroup11NADVO(); // mandatory for shipper

	public FTZGroup11NADVO getNad() {
		return nad;
	}

	public void setNad(FTZGroup11NADVO nad) {
		this.nad = nad;
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

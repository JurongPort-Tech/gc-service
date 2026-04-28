package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PubVslProdValueObject extends VslProdValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isRateExist;

	public boolean isRateExist() {
		return isRateExist;
	}

	public void setRateExist(boolean isRateExist) {
		this.isRateExist = isRateExist;
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

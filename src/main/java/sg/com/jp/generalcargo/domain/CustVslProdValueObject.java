package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustVslProdValueObject extends VslProdValueObject
implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isRateExist;

	public boolean isRateExist() {
		return isRateExist;
	}

	public void setRateExist(boolean isRateExist)   {
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

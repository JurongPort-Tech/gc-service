package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContainerDetails {
	
	private String cntrType;
	private String cntrSize;
	private String cntrNbr;
	public String getCntrType() {
		return cntrType;
	}
	public void setCntrType(String cntrType) {
		this.cntrType = cntrType;
	}
	public String getCntrSize() {
		return cntrSize;
	}
	public void setCntrSize(String cntrSize) {
		this.cntrSize = cntrSize;
	}
	public String getCntrNbr() {
		return cntrNbr;
	}
	public void setCntrNbr(String cntrNbr) {
		this.cntrNbr = cntrNbr;
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

package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JMSMsgValueObject {
	String type;
	String subType;
	String opsType;
	Object refObject;
	Date creationDttm;
	
	public String getType() {
		return type;
	}
	public void setType(String sType) {
		type = sType;
	}
	
	public String getSubType() {
		return subType;
	}
	public void setSubType(String sSubType) {
		subType = sSubType;
	}
	
	public String getOpsType() {
		return opsType;
	}
	public void setOpsType(String sOpsType) {
		opsType = sOpsType;
	}
	
	public Object getRefObject() {
		return refObject;
	}
	public void setRefObject(Object oRefObject) {
		refObject = oRefObject;
	}
	
	public Date getCreationDttm() {
		return creationDttm;
	}
	public void setCreationDttm(Date creationDttm) {
		this.creationDttm = creationDttm;
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

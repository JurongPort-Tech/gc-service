package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.Auditable;

public class AuditLogRecord implements Serializable, Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String auditUserId;
	private String fnType;
	private String fnsubType;
	private String keyType1;
	private String keyVal1;
	private String keyType2;
	private String keyVal2;
	private String tableNm;
	private String fieldOldValue = "";
	private String fieldNewValue = "";
	
	protected Set  auditFields = null;
	protected Set  dateFields = null;
	
	private static final Log log = LogFactory.getLog(AuditLogRecord.class);
	
	public AuditLogRecord() {
		setAuditableFields();
		setDateFields();
	}
	
	public String getAuditUserId() {
		return auditUserId;
	}
	public void setAuditUserId(String userId) {
		this.auditUserId = userId;
	}
	public String getFnType() {
		return fnType;
	}
	public void setFnType(String fnType) {
		this.fnType = fnType;
	}
	public String getFnsubType() {
		return fnsubType;
	}
	public void setFnsubType(String fnsubType) {
		this.fnsubType = fnsubType;
	}
	public String getKeyType1() {
		return keyType1;
	}
	public void setKeyType1(String keyType1) {
		this.keyType1 = keyType1;
	}
	public String getKeyVal1() {
		return keyVal1;
	}
	public void setKeyVal1(String keyVal1) {
		this.keyVal1 = keyVal1;
	}
	public String getKeyType2() {
		return keyType2;
	}
	public void setKeyType2(String keyType2) {
		this.keyType2 = keyType2;
	}
	public String getKeyVal2() {
		return keyVal2;
	}
	public void setKeyVal2(String keyVal2) {
		this.keyVal2 = keyVal2;
	}
	public String getTableNm() {
		return tableNm;
	}
	public void setTableNm(String tableNm) {
		this.tableNm = tableNm;
	}
	public String getFieldOldValue() {
		return fieldOldValue;
	}
	public void setFieldOldValue(String fieldOldValue) {
		this.fieldOldValue = fieldOldValue;
	}
	public String getFieldNewValue() {
		return fieldNewValue;
	}
	public void setFieldNewValue(String fieldNewValue) {
		this.fieldNewValue = fieldNewValue;
	}
	
	public boolean isFieldAuditable(String fieldname) {
	    boolean result = false;
	    
	    if (auditFields != null) {
	      if (auditFields.contains(fieldname)) {
	    	  result = true;
	      }
	    }
	    
	    return result;
	}
	
	public String formatFieldValue(String fieldname, String fieldvalue) {
		String tmp = fieldvalue;
		DateFormat formatter1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date field_dttm;
			
		if (!"".equalsIgnoreCase(fieldvalue)) { 
		  if (dateFields != null) {
		    if (dateFields.contains(fieldname)) {
			  // format the date  
			  try {			  
				if (fieldvalue.length() > 21) {
			      field_dttm = formatter1.parse(fieldvalue);
				} else {
				  field_dttm = formatter2.parse(fieldvalue);	
				}
			    tmp = formatter2.format(field_dttm);
			  } catch (Exception e) {
			    log.info("formatFieldValue Exception::", e);			  
			  }
		    }		  	
		  }
		}
		
		return tmp;
	}
	
	protected void setAuditableFields() { }
	protected void setDateFields() { }

	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

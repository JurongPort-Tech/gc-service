package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExceptionAlertValueObject implements TopsIObject{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String ALERT_CODE_ICD ="ICD";
    public static String ALERT_CODE_IGD ="IGD";
    public static String ALERT_CODE_IDV ="IDV";
    public static String ALERT_CODE_ILV ="ILV";
    
    public static String RECORD_STATUS_ACTIVE="A";
    public static String RECORD_STATUS_INACTIVE="I";
    
    public static String DELIVERY_MODE_SMS = "SMS";
    public static String DELIVERY_MODE_PGN = "PGN";
    public static String DELIVERY_MODE_PGM = "PGM";
    public static String DELIVERY_MODE_EML = "EML";
    
    
    private String name;
    private String alertCode;
    private String deliveryMode;
    private String account;
    private String recordStatus;
    private String lastModifyUserId;
    private Timestamp lastModifyDttm;
    
    public ExceptionAlertValueObject() {    
    }
    
    public String getName() {
        return name;
    }
    
    public String getAlertCode() {
        return alertCode;
    }
    
    public String getDeliveryMode() {
        return deliveryMode;
    }
    
    public String getAccount() {
        return account;
    }
    
    public String getRecordStatus() {
        return recordStatus;
    }
    
    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    public Timestamp getLastModifyDttm() {
        return lastModifyDttm;
    }
    
    public void setName(String val) {
        name=val;
    }
    
    public void setAlertCode(String val) {
        alertCode=val;
    }
    
    public void setDeliveryMode(String val) {
        deliveryMode=val;
    }
    
    public void setAccount(String val) {
        account=val;
    }
    
    public void setRecordStatus(String val) {
        recordStatus=val;
    }
    
    public void setLastModifyUserId(String val) {
        lastModifyUserId=val;
    }

    public void setLastModifyDttm(Timestamp val) {
        lastModifyDttm=val;
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

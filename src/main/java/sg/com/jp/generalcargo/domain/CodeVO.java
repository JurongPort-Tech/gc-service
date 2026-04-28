package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class CodeVO extends UserTimestampVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//added by zhangwenxing 03/06/2011
	protected String groupCd;
	//end by zhangwenxing 03/06/2011

    protected String code;

    protected String desc;

    protected String status;

    protected String dgInd;
    
    protected String jpDgGroup;

    public static final int TARIFF_MAIN_CATEGORY = 1;

    public static final int TARIFF_SUB_CATEGORY = 2;

    public static final int CONTAINER_SIZE = 3;

    public static final int CONTAINER_TYPE = 4;

    public static final int CONTAINER_CHARACTERISTICS = 5;

    // for checking the validity of the above constants
    public static final int MIN_RANGE = 1;

    public static final int MAX_RANGE = 5;

    public CodeVO() {
    }

    public CodeVO(String code, String desc, String lastModifyUserId, Timestamp lastModifyTimestamp, String dgInd) {
        this.setCode(code);
        this.setDescription(desc);
        this.setUser(lastModifyUserId);
        this.setTimestamp(lastModifyTimestamp);
        this.setDgInd(dgInd);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return desc;
    }

    public String getStatus() {
        return status;
    }

    public String getDgInd() {
        return dgInd;
    }

    public String getJpDgGroup() {
    	return jpDgGroup;
    }
    
    public void setCode(String value) {
        code = value;
    }

    public void setDescription(String value) {
        desc = value;
    }

    public void setStatus(String value) {
        status = value;
    }

    public void setDgInd(String value) {
        dgInd = value;
    }
    
    public void setJpDgGroup(String value) {
    	jpDgGroup = value;
    }

	public String getGroupCd() {
		return groupCd;
	}

	public void setGroupCd(String groupCd) {
		this.groupCd = groupCd;
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

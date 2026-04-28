package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TextParaVO.java
 *
 * @author Satish
 * @date October 14, 2002 Modifications: Date(By) Changes
 *
 */

/*
 * Revision History Date Author SR#/CS/PM#/OTHERS Description
 * -----------------------------------------------------------------------------
 * ----------- 19-Mar-2009 Bhuvana SL-OPS-20090316-01 Add description column in
 * Text Para
 */

public class TextParaVO {

	/** public constants */
	public static final String CODE = "para_cd";
	public static final String DESC = "value";
	public static final String LMU = "last_modify_user_id";
	public static final String LMD = "last_modify_dttm";
	public static final String DESCRIPTION = "paraDesc";
	private String para_cd;
	private String value;
	private String lastModifyUserId;
	private Timestamp lastModifyTimestamp;
	private String paraDesc;

	public TextParaVO() {
		this.para_cd = null;
		this.value = null;
		this.lastModifyTimestamp = null;
		this.lastModifyUserId = null;
	}

	public void setParaCode(String para_cd) {
		this.para_cd = para_cd;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setUser(String user) {
		this.lastModifyUserId = user;
	}

	public void setTimestamp(Timestamp value) {
		lastModifyTimestamp = value;
	}

	public String getParaCode() {
		return para_cd;
	}

	public String getValue() {
		return value;
	}

	public String getUser() {
		return lastModifyUserId;
	}

	public Timestamp getTimestamp() {
		return lastModifyTimestamp;
	}

	public String getParaDesc() {
		return paraDesc;
	}

	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
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

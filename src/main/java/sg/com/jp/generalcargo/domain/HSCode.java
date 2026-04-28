package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HSCode implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hsCode;
	private String hsDesc;
	private String recStatus;
	private String createUserId;
	private Date createUserDttm;
	private String lastModifyUserId;
	private Date lastModifyDttm;
	
	private String hsSubCodeFr;
	private String hsSubCodeTo;
	private String hsSubDesc;
	
	public HSCode() {
	}
	
	public String getHsCode() {
		return hsCode;
	}
	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}
	
	public String getHsDesc() {
		return hsDesc;
	}

	public void setHsDesc(String hsDesc) {
		this.hsDesc = hsDesc;
	}

	public String getHsSubDesc() {
		return hsSubDesc;
	}

	public void setHsSubDesc(String hsSubDesc) {
		this.hsSubDesc = hsSubDesc;
	}

	public String getRecStatus() {
		return recStatus;
	}
	public void setRecStatus(String recStatus) {
		this.recStatus = recStatus;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getCreateUserDttm() {
		return createUserDttm;
	}
	public void setCreateUserDttm(Date createUserDttm) {
		this.createUserDttm = createUserDttm;
	}
	public String getLastModifyUserId() {
		return lastModifyUserId;
	}
	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}
	public Date getLastModifyDttm() {
		return lastModifyDttm;
	}
	public void setLastModifyDttm(Date lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
	}
	public String getHsSubCodeFr() {
		return hsSubCodeFr;
	}
	public void setHsSubCodeFr(String hsSubCodeFr) {
		this.hsSubCodeFr = hsSubCodeFr;
	}
	public String getHsSubCodeTo() {
		return hsSubCodeTo;
	}
	public void setHsSubCodeTo(String hsSubCodeTo) {
		this.hsSubCodeTo = hsSubCodeTo;
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

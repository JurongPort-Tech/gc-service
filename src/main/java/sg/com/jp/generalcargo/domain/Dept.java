package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dept {
	private Long deptId;
	private String deptName;
	private String coCd;
	private String activeInd;
	private Long lastModifyId;
	private Date lastModifyDate;
	private Long createId;
	private Date createDate;
	
	private String coNm;

	public Long getDeptId() {
		return this.deptId;
	}

	public void setDeptId(Long aDeptId) {
		this.deptId = aDeptId;
	}

	public String getDeptName() {
		return this.deptName;
	}

	public void setDeptName(String aDeptName) {
		this.deptName = aDeptName;
	}

	public String getCoCd() {
		return this.coCd;
	}

	public void setCoCd(String aCoCd) {
		this.coCd = aCoCd;
	}

	public String getActiveInd() {
		return this.activeInd;
	}

	public void setActiveInd(String aActiveInd) {
		this.activeInd = aActiveInd;
	}

	public Long getLastModifyId() {
		return this.lastModifyId;
	}

	public void setLastModifyId(Long aLastModifyId) {
		this.lastModifyId = aLastModifyId;
	}

	public Date getLastModifyDate() {
		return this.lastModifyDate;
	}

	public void setLastModifyDate(Date aLastModifyDate) {
		this.lastModifyDate = aLastModifyDate;
	}

	public Long getCreateId() {
		return this.createId;
	}

	public void setCreateId(Long aCreateId) {
		this.createId = aCreateId;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date aCreateDate) {
		this.createDate = aCreateDate;
	}

	public String getCoNm() {
		return coNm;
	}

	public void setCoNm(String coNm) {
		this.coNm = coNm;
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
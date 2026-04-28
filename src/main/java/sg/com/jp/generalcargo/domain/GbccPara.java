package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * xe4.GbccPara 12/23/2009 19:22:11
 * 
 */
public class GbccPara implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paraCd;
	private String value;
	private String lastModifyUserId;
	private Date lastModifyDttm;
	private String paraDesc;
	private String paraCatCd;
	private String paraUnit;

	public GbccPara() {
	}

	public GbccPara(String paraCd, String value, String lastModifyUserId, Date lastModifyDttm, String paraDesc,
			String paraCatCd, String paraUnit) {
		this.paraCd = paraCd;
		this.value = value;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.paraDesc = paraDesc;
		this.paraCatCd = paraCatCd;
		this.paraUnit = paraUnit;
	}

	public String getParaCd() {
		return paraCd;
	}

	public void setParaCd(String paraCd) {
		this.paraCd = paraCd;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public String getParaDesc() {
		return paraDesc;
	}

	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
	}

	public String getParaCatCd() {
		return paraCatCd;
	}

	public void setParaCatCd(String paraCatCd) {
		this.paraCatCd = paraCatCd;
	}

	public String getParaUnit() {
		return paraUnit;
	}

	public void setParaUnit(String paraUnit) {
		this.paraUnit = paraUnit;
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

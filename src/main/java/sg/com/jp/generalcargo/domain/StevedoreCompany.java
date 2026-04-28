package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * xe.StevedoreCompany 11/18/2009 07:33:58
 * 
 */
public class StevedoreCompany implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stevCoCd;
	private String stevCoNm;
	private String coCd;

	private String stevRep;
	private String stevContact;
	private String recStatus;
	private String lastModifyUserId;
	private Date lastModifyDttm;

	private String stevEmailAddr;
	private String stevSnetAlertMode;

	public StevedoreCompany() {
	}

	public StevedoreCompany(String stevCoCd, String stevCoNm, String coCd, String stevRep, String stevContact,
			String recStatus, String lastModifyUserId, Date lastModifyDttm) {
		this.stevCoCd = stevCoCd;
		this.stevCoNm = stevCoNm;
		this.coCd = coCd;
		this.stevRep = stevRep;
		this.stevContact = stevContact;
		this.recStatus = recStatus;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
	}

	public String getStevCoCd() {
		return stevCoCd;
	}

	public void setStevCoCd(String stevCoCd) {
		this.stevCoCd = stevCoCd;
	}

	public String getStevCoNm() {
		return stevCoNm;
	}

	public void setStevCoNm(String stevCoNm) {
		this.stevCoNm = stevCoNm;
	}

	public String getCoCd() {
		return coCd;
	}

	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}

	public String getStevRep() {
		return stevRep;
	}

	public void setStevRep(String stevRep) {
		this.stevRep = stevRep;
	}

	public String getStevContact() {
		return stevContact;
	}

	public void setStevContact(String stevContact) {
		this.stevContact = stevContact;
	}

	public String getRecStatus() {
		return recStatus;
	}

	public void setRecStatus(String recStatus) {
		this.recStatus = recStatus;
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

	public String getStevEmailAddr() {
		return stevEmailAddr;
	}

	public void setStevEmailAddr(String stevEmailAddr) {
		this.stevEmailAddr = stevEmailAddr;
	}

	public String getStevSnetAlertMode() {
		return stevSnetAlertMode;
	}

	public void setStevSnetAlertMode(String stevSnetAlertMode) {
		this.stevSnetAlertMode = stevSnetAlertMode;
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

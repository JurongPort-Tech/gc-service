package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LineTowedVesselValueObject implements TopsIObject {
	private static final long serialVersionUID = 1L;

	public String getVvCode() {
		return vvCode;
	}

	public void setVvCode(String vvCode) {
		this.vvCode = vvCode;
	}

	public String getTariffMainCatCode() {
		return tariffMainCatCode;
	}

	public void setTariffMainCatCode(String tariffMainCatCode) {
		this.tariffMainCatCode = tariffMainCatCode;
	}

	public String getTariffSubCatCode() {
		return tariffSubCatCode;
	}

	public void setTariffSubCatCode(String tariffSubCatCode) {
		this.tariffSubCatCode = tariffSubCatCode;
	}

	public java.sql.Timestamp getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(java.sql.Timestamp startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public java.sql.Timestamp getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(java.sql.Timestamp endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public java.sql.Timestamp getLastModifyTimestamp() {
		return lastModifyTimestamp;
	}

	public void setLastModifyTimestamp(java.sql.Timestamp lastModifyTimestamp) {
		this.lastModifyTimestamp = lastModifyTimestamp;
	}

	private String vvCode;
	private String tariffMainCatCode;
	private String tariffSubCatCode;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private String lastModifyUserId;
	private Timestamp lastModifyTimestamp;

	public LineTowedVesselValueObject() {

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

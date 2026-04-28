package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillCreditNoteAdviceNumberVO {

	private String adviceNumber;
	private CreditNoteStatus status;
	private Timestamp lastModifyDttm;

	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String adviceNumber) {
		this.adviceNumber = adviceNumber;
	}

	public CreditNoteStatus getStatus() {
		return status;
	}

	public void setStatus(CreditNoteStatus status) {
		this.status = status;
	}

	public Timestamp getLastModifyDttm() {
		return lastModifyDttm;
	}

	public void setLastModifyDttm(Timestamp lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
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

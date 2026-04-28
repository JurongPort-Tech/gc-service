package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContractSearchKeyValueObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private String CUST_CD;
	private String ACCT_NBR;
	private String CONTRACT_NBR;
	private String ID_CODE;
	private String ID_CODE_IND;
	private String LAST_MODIFY_USER_ID;
	private Timestamp LAST_MODIFY_DTTM;

	public ContractSearchKeyValueObject() {
	}

	public void setCustomerCode(String code) {
		CUST_CD = code;
	}

	public String getCustomerCode() {
		return CUST_CD;
	}

	public void setAccountNumber(String nbr) {
		ACCT_NBR = nbr;
	}

	public String getAccountNumber() {
		return ACCT_NBR;
	}

	public void setContractNumber(String nbr) {
		CONTRACT_NBR = nbr;
	}

	public String getContractNumber() {
		return CONTRACT_NBR;
	}

	public void setIDCode(String code) {
		ID_CODE = code;
	}

	public String getIDCode() {
		return ID_CODE;
	}

	public void setIDCodeIndicator(String ind) {
		ID_CODE_IND = ind;
	}

	public String getIDCodeIndicator() {
		return ID_CODE_IND;
	}

	public void setLastUpdateUserID(String userID) {
		LAST_MODIFY_USER_ID = userID;
	}

	public String getLastUpdateUserID() {
		return LAST_MODIFY_USER_ID;
	}

	public void setLastUpdateTimestamp(Timestamp timestamp) {
		LAST_MODIFY_DTTM = timestamp;
	}

	public Timestamp getLastUpdateTimestamp() {
		return LAST_MODIFY_DTTM;
	}

	public boolean isModified(ContractSearchKeyValueObject contractSearchKeyValueObject) {
		ContractSearchKeyValueObject origVO = new ContractSearchKeyValueObject();
		if (contractSearchKeyValueObject.getCustomerCode() == origVO.getCustomerCode()
				&& contractSearchKeyValueObject.getAccountNumber() == origVO.getAccountNumber()
				&& contractSearchKeyValueObject.getContractNumber() == origVO.getContractNumber()
				&& contractSearchKeyValueObject.getIDCode() == origVO.getIDCode()
				&& contractSearchKeyValueObject.getIDCodeIndicator() == origVO.getIDCodeIndicator()
				&& contractSearchKeyValueObject.getLastUpdateUserID() == origVO.getLastUpdateUserID()
				&& contractSearchKeyValueObject.getLastUpdateTimestamp() == origVO.getLastUpdateTimestamp())
			return false;
		else
			return true;
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

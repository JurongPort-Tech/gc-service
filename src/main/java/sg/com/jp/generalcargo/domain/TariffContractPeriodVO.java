package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TariffContractPeriodVO {
	private String customer;
	private String account;
	private String contract;
	private int contractYear;
	private Timestamp periodFrom;
	private Timestamp periodTo;

	public TariffContractPeriodVO() {
		super();
		customer = null;
		account = null;
		contract = null;
		periodFrom = null;
		periodTo = null;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public void setContractYear(int contractYear) {
		this.contractYear = contractYear;
	}

	public void setPeriodFrom(Timestamp periodFrom) {
		this.periodFrom = periodFrom;
	}

	public void setPeriodTo(Timestamp periodTo) {
		this.periodTo = periodTo;
	}

	public String getCustomer() {
		return (this.customer);
	}

	public String getAccount() {
		return (this.account);
	}

	public String getContract() {
		return (this.contract);
	}

	public int getContractYear() {
		return (this.contractYear);
	}

	public Timestamp getPeriodFrom() {
		return (this.periodFrom);
	}

	public Timestamp getPeriodTo() {
		return (this.periodTo);
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

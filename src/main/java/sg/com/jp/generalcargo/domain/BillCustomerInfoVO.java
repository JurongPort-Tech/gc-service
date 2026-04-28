package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillCustomerInfoVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	private String name; // customer name
	private String code; // customer code
	private String billPresentation; // display discount or std rates
	private String account; // account number
	private String businessType; // account type
	private String enquiryContact; // bill enq contact number
	private String address1; // address line 1
	private String address2; // address line 2
	private String city; // city
	private String countryCode; // country code;
	private String country; // country
	private String postCode; // postal code
	private String addressee; // address to who
	private String status; // account status
	private String currency; // account currency
	private List<BillContractInfoVO> a = new ArrayList<BillContractInfoVO>(); // for list of contracts

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setBillPresentation(String billPresentation) {
		this.billPresentation = billPresentation;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public void setEnquiryContact(String enquiryContact) {
		this.enquiryContact = enquiryContact;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getName() {
		return (this.name);
	}

	public String getCode() {
		return (this.code);
	}

	public String getBillPresentation() {
		return (this.billPresentation);
	}

	public String getAccount() {
		return (this.account);
	}

	public String getBusinessType() {
		return (this.businessType);
	}

	public String getEnquiryContact() {
		return (this.enquiryContact);
	}

	public String getAddress1() {
		return (this.address1);
	}

	public String getAddress2() {
		return (this.address2);
	}

	public String getCity() {
		return (this.city);
	}

	public String getCountryCode() {
		return (this.countryCode);
	}

	public String getCountry() {
		return (this.country);
	}

	public String getPostCode() {
		return (this.postCode);
	}

	public String getAddressee() {
		return (this.addressee);
	}

	public String getCurrency() {
		return (this.currency);
	}

	public int getContractCount() {
		return a.size();
	}
	
	public String getStatus() {
		return status;
	}

	public BillContractInfoVO getContract(int index) {
		if (index < a.size()) {
			return (BillContractInfoVO) a.get(index);
		} else {
			return null;
		}
	}

	public void setAllContract(List<BillContractInfoVO> a) {
		if (a != null) {
			this.a = a;
		}
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

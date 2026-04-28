package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Hashtable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.TimeFunction;

public class AccountValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String CUST_CD;
	private String ACCT_NBR;
	private String ACCT_STATUS_CD;
	private String BUSINESS_TYPE;
	private double TOTAL_BG_AMT;
	private double TOTAL_SD_AMT;
	private double TOTAL_BILL_AMT;
	private double TOTAL_PAID_AMT;
	private String TRIAL_IND;
	private String NO_AGREEMENT_IND;
	private String BILL_ENQ_CONTACT;
	private int ADD_SEQ_NBR;
	private String LAST_MODIFY_USER_ID;
	private Timestamp LAST_MODIFY_DTTM;
// [0.2] New in DB schema ver 3.2
	private String CURRENCY;
// [0.9]
	private String PREV_ACCT_NBR;

	private String CreateUserID;
	private String PostInd;
	private Timestamp CreateDateTimestamp;

	private static NumberFormat nf = NumberFormat.getInstance();
	static {
		nf.setMinimumFractionDigits(2);
		nf.setGroupingUsed(false);
	}

	/** Creates new AccountValueObject */
	public AccountValueObject() {
	}

	public void doGet(Object object) {
	}

	public void doSet(Object object) {
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

	public void setStatusCode(String code) {
		ACCT_STATUS_CD = code;
	}

	public String getStatusCode() {
		return ACCT_STATUS_CD;
	}

	public void setBusinessType(String type) {
		BUSINESS_TYPE = type;
	}

	public String getBusinessType() {
		return BUSINESS_TYPE;
	}

	public void setTotalBGAmount(double amt) {
		TOTAL_BG_AMT = amt;
	}

	public double getTotalBGAmount() {
		return TOTAL_BG_AMT;
	}

	public void setTotalSDAmount(double amt) {
		TOTAL_SD_AMT = amt;
	}

	public double getTotalSDAmount() {
		return TOTAL_SD_AMT;
	}

	public void setTotalBillAmount(double amt) {
		TOTAL_BILL_AMT = amt;
	}

	public double getTotalBillAmount() {
		return TOTAL_BILL_AMT;
	}

	public void setTotalPaidAmount(double amt) {
		TOTAL_PAID_AMT = amt;
	}

	public double getTotalPaidAmount() {
		return TOTAL_PAID_AMT;
	}

	public void setTrialIndicator(String ind) {
		TRIAL_IND = ind;
	}

	public String getTrialIndicator() {
		return TRIAL_IND;
	}

	public void setAgreementIndicator(String ind) {
		NO_AGREEMENT_IND = ind;
	}

	public String getAgreementIndicator() {
		return NO_AGREEMENT_IND;
	}

	public void setBillEnquiryNumber(String nbr) {
		BILL_ENQ_CONTACT = nbr;
	}

	public String getBillEnquiryNumber() {
		return BILL_ENQ_CONTACT;
	}

	public void setAddressSequenceNumber(int nbr) {
		ADD_SEQ_NBR = nbr;
	}

	public int getAddressSequenceNumber() {
		return ADD_SEQ_NBR;
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

// [0.2] New in DB schema ver 3.2
	public void setCurrency(String crncy) {
		CURRENCY = crncy;
	}

// [0.2] New in DB schema ver 3.2
	public String getCurrency() {
		return CURRENCY;
	}

// [0.5]
	public void setPreviousAccountNumber(String nbr) {
		PREV_ACCT_NBR = nbr;
	}

// [0.5]
	public String getPreviousAccountNumber() {
		return PREV_ACCT_NBR;
	}

	public void setPostInd(String PostInd) {
		this.PostInd = PostInd;
	}

	public String getPostInd() {
		return PostInd;
	}

	public void setCreateUserID(String userID) {
		CreateUserID = userID;
	}

	public String getCreateUserID() {
		return CreateUserID;
	}

	public void setCreateDateTimestamp(Timestamp timestamp) {
		CreateDateTimestamp = timestamp;
	}

	public Timestamp getCreateDateTimestamp() {
		return CreateDateTimestamp;
	}

	/**
	 * Converts the value object into a data array.
	 *
	 * @return A string array.
	 */
	public String[] convertToArray() {
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		// String[] dataArray = new String[16];
		String[] dataArray = new String[17];
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
		dataArray[0] = this.getCustomerCode();
		dataArray[1] = this.getAccountNumber();
		dataArray[2] = this.getStatusCode();
		dataArray[3] = this.getBusinessType();
		dataArray[4] = nf.format(this.getTotalBGAmount());
		dataArray[5] = nf.format(this.getTotalSDAmount());
		dataArray[6] = nf.format(this.getTotalBillAmount());
		dataArray[7] = nf.format(this.getTotalPaidAmount());
		dataArray[8] = this.getTrialIndicator();
		dataArray[9] = this.getAgreementIndicator();
		dataArray[10] = this.getBillEnquiryNumber();
		dataArray[11] = Integer.toString(this.getAddressSequenceNumber());
		dataArray[12] = this.getLastUpdateUserID();
		dataArray[13] = TimeFunction.convertDBTimestampToDate(this.getLastUpdateTimestamp(), "/", true);
// [0.2] New in DB schema ver 3.2
		dataArray[14] = this.getCurrency();
// [0.5]
		dataArray[15] = this.getPreviousAccountNumber();
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		dataArray[16] = this.getGiroInd();
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
// [0.2] New in DB schema ver 3.2
// [0.5]
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)

		return dataArray;
	}

	/**
	 * Converts the value object into a data array.
	 *
	 * @param ht A hashtable containing the AccountCurrencyValueObject details.
	 * @return A string array with element [15] set to the currency name.
	 */
	public String[] convertToArray(Hashtable ht) {
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		// String[] dataArray = new String[20];
		String[] dataArray = new String[21];
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
		dataArray[0] = this.getCustomerCode();
		dataArray[1] = this.getAccountNumber();
		dataArray[2] = this.getStatusCode();
		dataArray[3] = this.getBusinessType();
		dataArray[4] = nf.format(this.getTotalBGAmount());
		dataArray[5] = nf.format(this.getTotalSDAmount());
		dataArray[6] = nf.format(this.getTotalBillAmount());
		dataArray[7] = nf.format(this.getTotalPaidAmount());
		dataArray[8] = this.getTrialIndicator();
		dataArray[9] = this.getAgreementIndicator();
		dataArray[10] = this.getBillEnquiryNumber();
		dataArray[11] = Integer.toString(this.getAddressSequenceNumber());
		dataArray[12] = this.getLastUpdateUserID();
		dataArray[13] = TimeFunction.convertDBTimestampToDate(this.getLastUpdateTimestamp(), "/", true);
// [0.2] New in DB schema ver 3.2
		dataArray[14] = this.getCurrency();
		dataArray[15] = this.getPostInd();
		dataArray[16] = this.getCreateUserID();
		dataArray[17] = TimeFunction.convertDBTimestampToDate(this.getCreateDateTimestamp(), "/", true);
		dataArray[18] = (String) ht.get(dataArray[17]);
// [0.5]
		dataArray[19] = this.getPreviousAccountNumber();
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		dataArray[20] = this.getGiroInd();
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
// [0.2] New in DB schema ver 3.2
// [0.5]
		// Added by MC Consulting for E-Invoice enhancements (Phase 2)
		// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
		return dataArray;
	}

	public boolean isModified(AccountValueObject accountValueObject) {
		AccountValueObject origVO = new AccountValueObject();

		if (accountValueObject.getCustomerCode() == origVO.getCustomerCode()
				&& accountValueObject.getAccountNumber() == origVO.getAccountNumber()
				&& accountValueObject.getStatusCode() == origVO.getStatusCode()
				&& accountValueObject.getBusinessType() == origVO.getBusinessType()
				&& accountValueObject.getTotalBGAmount() == origVO.getTotalBGAmount()
				&& accountValueObject.getTotalSDAmount() == origVO.getTotalSDAmount()
				&& accountValueObject.getTotalBillAmount() == origVO.getTotalBillAmount()
				&& accountValueObject.getTotalPaidAmount() == origVO.getTotalPaidAmount()
				&& accountValueObject.getTrialIndicator() == origVO.getTrialIndicator()
				&& accountValueObject.getBillEnquiryNumber() == origVO.getBillEnquiryNumber()
				&& accountValueObject.getAddressSequenceNumber() == origVO.getAddressSequenceNumber()
				&& accountValueObject.getCurrency() == origVO.getCurrency()
				&& accountValueObject.getLastUpdateUserID() == origVO.getLastUpdateUserID()
				&& accountValueObject.getLastUpdateTimestamp() == origVO.getLastUpdateTimestamp())
			return false;
		else
			return true;
	}

	// Added by MC Consulting for E-Invoice enhancements (Phase 2)
	private String giroInd;

	public String getGiroInd() {
		return giroInd;
	}

	public void setGiroInd(String giroInd) {
		this.giroInd = giroInd;
	}
	// End of addition by MC Consulting for E-Invoice enhancements (Phase 2)
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

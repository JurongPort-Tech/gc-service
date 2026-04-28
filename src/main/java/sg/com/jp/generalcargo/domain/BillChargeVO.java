package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillChargeVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String reference;
	private Timestamp transactionTime;
	private String referenceIndicator;
	private BillSupportInfoVO supportInfo;
	
    public BillChargeVO() {
    	reference = null;
    }
    public BillChargeVO(BillChargeVO chg) {
    	this.copy(chg);
    }
	public void doGet(Object object) {}
	public void doSet(Object object) {}

	public boolean isModified(){
		return (reference == null);
	}

	// get set methods
	public void setSupportInfo(BillSupportInfoVO supportInfo) { this.supportInfo = supportInfo; }
	public void setReference(String reference) { this.reference = reference; }
	public void setTransactionTime(Timestamp transactionTime) { this.transactionTime = transactionTime; }
	public void setReferenceIndicator(String referenceIndicator) { this.referenceIndicator = referenceIndicator; }

	public BillSupportInfoVO getSupportInfo() { return (this.supportInfo); }
	public String getReference() { return (this.reference); }
	public Timestamp getTransactionTime() { return (this.transactionTime); }
	public String getReferenceIndicator() { return (this.referenceIndicator); }
	

	
	public void copy(BillChargeVO chg){
		this.reference = chg.getReference();
		this.referenceIndicator = chg.getReferenceIndicator();
		this.transactionTime = chg.getTransactionTime();
		this.lastModifyUserId = chg.getUser();
		this.lastModifyTimestamp = chg.getTimestamp();
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

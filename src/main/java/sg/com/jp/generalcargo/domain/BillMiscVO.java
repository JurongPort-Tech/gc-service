package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillMiscVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// member variable
		private long miscSeqNbr;	
		private String refNbr;
		private String transactionCode;
		private Timestamp transactionTime;	
		private BillSupportInfoVO supportInfo;
		
	    public BillMiscVO() {
	    	miscSeqNbr = -1;
	    }
	    public BillMiscVO(BillMiscVO cntr){
	    	this.copy(cntr);
	    }
		public void doGet(Object object) {}
		public void doSet(Object object) {}

		public boolean isModified(){
			return (miscSeqNbr > 0);
		}
		
		public long getMiscSeqNbr() {
			return miscSeqNbr;
		}
		public void setMiscSeqNbr(long miscSeqNbr) {
			this.miscSeqNbr = miscSeqNbr;
		}
		public BillSupportInfoVO getSupportInfo() {
			return supportInfo;
		}
		public void setSupportInfo(BillSupportInfoVO supportInfo) {
			this.supportInfo = supportInfo;
		}
		public String getTransactionCode() {
			return transactionCode;
		}
		public void setTransactionCode(String transactionCode) {
			this.transactionCode = transactionCode;
		}
		public Timestamp getTransactionTime() {
			return transactionTime;
		}
		public void setTransactionTime(Timestamp transactionTime) {
			this.transactionTime = transactionTime;
		}
		public String getRefNbr() {
			return refNbr;
		}
		public void setRefNbr(String refNbr) {
			this.refNbr = refNbr;
		}

		public void copy(BillMiscVO misc){
			this.miscSeqNbr = misc.getMiscSeqNbr();	
			this.refNbr = misc.getRefNbr();
			this.transactionCode = misc.getTransactionCode();
			this.transactionTime = misc.getTransactionTime();
			this.lastModifyUserId = misc.getUser();
			this.lastModifyTimestamp = misc.getTimestamp();
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

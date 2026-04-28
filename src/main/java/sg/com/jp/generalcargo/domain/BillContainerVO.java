package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

public class BillContainerVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// member variable
	private long containerSequenceNumber;
	private Timestamp startTime;
	private Timestamp endTime;
	private String transactionCode;
	private Timestamp transactionTime;
	private String startDttmTypeCd;
	private String endDttmTypeCd;
	private BillSupportInfoVO supportInfo;
	private String purp_cd;
	
    public BillContainerVO() {
    	containerSequenceNumber = -1;
    }
    public BillContainerVO(BillContainerVO cntr){
    	this.copy(cntr);
    }
	public void doGet(Object object) {}
	public void doSet(Object object) {}

	public boolean isModified(){
		return (containerSequenceNumber > 0);
	}

	// get set methods
	public void setContainerSequenceNumber(long containerSequenceNumber) { this.containerSequenceNumber = containerSequenceNumber; }
	public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
	public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
	public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
	public void setTransactionTime(Timestamp transactionTime) { this.transactionTime = transactionTime; }
	public void setStartDttmTypeCd(String startDttmTypeCd){ this.startDttmTypeCd = startDttmTypeCd; }
	public void setEndDttmTypeCd(String endDttmTypeCd){ this.endDttmTypeCd = endDttmTypeCd; }
	public void setSupportInfo(BillSupportInfoVO supportInfo) { this.supportInfo = supportInfo; }
	
	public long getContainerSequenceNumber() { return (this.containerSequenceNumber); }
	public Timestamp getStartTime() { return (this.startTime); }
	public Timestamp getEndTime() { return (this.endTime); }
	public String getTransactionCode() { return (this.transactionCode); }
	public Timestamp getTransactionTime() { return (this.transactionTime); }
	public String getStartDttmTypeCd(){ return (this.startDttmTypeCd); }
	public String getEndDttmTypeCd(){ return (this.endDttmTypeCd); }
	public BillSupportInfoVO getSupportInfo() { return (this.supportInfo); }
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t\t");
		sb.append("CntrSeqNbr : ");
		sb.append(this.getContainerSequenceNumber());
		sb.append(" TxnCode : ");
		sb.append(this.getTransactionCode());
		sb.append(" TxnDttm : ");
		sb.append(this.getTransactionTime());
		sb.append("\n");
		return sb.toString();
	}
	
	public void copy(BillContainerVO cntr){
		this.containerSequenceNumber = cntr.getContainerSequenceNumber();
		this.endDttmTypeCd = cntr.getEndDttmTypeCd();
		this.endTime = cntr.getEndTime();
		this.startDttmTypeCd = cntr.getStartDttmTypeCd();
		this.startTime = cntr.getStartTime();
		this.transactionCode = cntr.getTransactionCode();
		this.transactionTime = cntr.getTransactionTime();
		this.lastModifyUserId = cntr.getUser();
		this.lastModifyTimestamp = cntr.getTimestamp();
	}
	
    public String getPurp_cd() {
        return purp_cd;
    }
    public void setPurp_cd(String purpCd) {
        purp_cd = purpCd;
    }
	
	
}

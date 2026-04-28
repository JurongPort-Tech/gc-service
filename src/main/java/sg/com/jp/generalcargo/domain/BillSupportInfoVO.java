package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtility;

public abstract class BillSupportInfoVO extends UserTimestampVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TXN_DISCHARGE = "DISC";
	public static final String TXN_LOAD = "LOAD";
	public static final String TXN_EXIT = "EXIT";
	public static final String TXN_OFF_LOAD = "OFLD";
	public static final String TXN_MOUNT = "MOUT";
	public static final String TXN_SHIFT = "SHFT";

	protected String containerNumber;
	protected String cntrSize;
	protected String cntrCatCd;
	protected String transactionCode;
	protected Timestamp transactionTime;
	protected long cntrEventLog;
	protected static final SimpleDateFormat sdf;
	protected Timestamp timeIn; // in time
	protected Timestamp timeOut; // out time
	protected String timeInType;
	protected String timeOutType;
    /* Added extra filelds to show extra support info description
        - Manohar for CR-CAB-20040922-4 on 09 Dec 2004 */
    protected String vslFullName;
    protected String vslOutVoyNbr;
    protected boolean isCancelledVsl; // to check whether vessel is canceled or not
    protected Timestamp etbDttm; // estimated time of berthing
    protected Timestamp etuDttm; // estimated time of unberth
    protected Timestamp btrDttm; // berthing time required
    protected Timestamp etdDttm; // estimated time of discharge

    // bug fix by Mohan/HKM on 28 Jul 2005
    
    protected String vvCd;
    // START - CR-CAB-20050823-02 TT billing changes - Valli 11-Aug-2006
    protected long miscSeqNbr;
    protected String refNbr;
    // END - CR-CAB-20050823-02 TT billing changes - Valli 11-Aug-2006
    
    
	static{
		sdf = new SimpleDateFormat("dd/MM/yyyy - HHmm");
	}

	public String getTransactionCode() { return (this.transactionCode); }
	public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

	public Timestamp getTransactionTime() { return (this.transactionTime); }
	public void setTransactionTime(Timestamp transactionTime) { this.transactionTime = transactionTime; }

	public String getContainerNumber() { return (this.containerNumber); }
	public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }

	public void setCntrEventLog(long cntrEventLog) { this.cntrEventLog = cntrEventLog; }
	public long getCntrEventLog() { return (this.cntrEventLog); }

	public void setTimeIn(Timestamp timeIn) { this.timeIn = timeIn; }
	public void setTimeOut(Timestamp timeOut) { this.timeOut = timeOut; }
	public void setTimeInType(String timeInType) { this.timeInType = timeInType; }
	public void setTimeOutType(String timeOutType) { this.timeOutType = timeOutType; }

	public Timestamp getTimeIn() { return (this.timeIn); }
	public Timestamp getTimeOut() { return (this.timeOut); }
	public String getTimeInType() { return (this.timeInType); }
	public String getTimeOutType() { return (this.timeOutType); }
    /* Added methods to show extra support info description
        - Manohar for CR-CAB-20040922-4 on 09 Dec 2004 */
    public void setVslFullName(String vslFullName) { this.vslFullName = vslFullName; }
    public void setVslOutVoyNbr(String vslOutVoyNbr) { this.vslOutVoyNbr = vslOutVoyNbr; }
    public void setIsCancelledVsl(boolean isCancelledVsl) { this.isCancelledVsl = isCancelledVsl; }
    public void setEtbDttm(Timestamp etbDttm) { this.etbDttm = etbDttm; }
    public void setEtuDttm(Timestamp etuDttm) { this.etuDttm = etuDttm; }
    public void setBtrDttm(Timestamp btrDttm) { this.btrDttm = btrDttm; }
    public void setEtdDttm(Timestamp etdDttm) { this.etdDttm = etdDttm; }

    public String getVslFullName() { return(this.vslFullName); }
    public String getVslOutVoyNbr() { return(this.vslOutVoyNbr); }
    public boolean getIsCancelledVsl() { return (this.isCancelledVsl); }
    public Timestamp getEtbDttm() { return (this.etbDttm); }
    public Timestamp getEtuDttm() { return (this.etuDttm); }
    public Timestamp getBtrDttm() { return (this.btrDttm); }
    public Timestamp getEtdDttm() { return (this.etdDttm); }

    public String getSupportExtraDesc(){
        StringBuffer sbExtraDesc = new StringBuffer();
        if(isCancelledVsl){
            sbExtraDesc.setLength(0);
            sbExtraDesc.append("\n");
            sbExtraDesc.append("To Vessel: ");
            sbExtraDesc.append(CommonUtility.deNull(vslFullName));
            sbExtraDesc.append(" Out Voy: ");
            sbExtraDesc.append(CommonUtility.deNull(vslOutVoyNbr));
            sbExtraDesc.append(" is a cancelled BA");
            if(etbDttm == null && etuDttm == null){
                sbExtraDesc.append("\n");
                sbExtraDesc.append("BTR: ");
                sbExtraDesc.append(this.formatTime(this.btrDttm));
                sbExtraDesc.append(" ETD: ");
                sbExtraDesc.append(this.formatTime(this.etdDttm));
            }
        }
        return sbExtraDesc.toString();
    }
	public abstract String getSupportDesc();

	public static String formatTime(Timestamp ts){
		if (ts == null) return "-";
		return sdf.format(new java.util.Date(ts.getTime()));
	}

	public void copy (BillSupportInfoVO other){
		this.setCntrEventLog(other.getCntrEventLog());
		this.setContainerNumber(other.getContainerNumber());
		this.setTransactionCode(other.getTransactionCode());
		this.setTransactionTime(other.getTransactionTime());
		this.setTimeIn(other.getTimeIn());
		this.setTimeInType(other.getTimeInType());
		this.setTimeOut(other.getTimeOut());
		this.setTimeOutType(other.getTimeOutType());
	}
    
    public String getVvCd() {
        return this.vvCd;
    }
    
    public void setVvCd(String vvCd) {
        this.vvCd = vvCd;
    }
	public String getCntrCatCd() {
		return cntrCatCd;
	}
	public void setCntrCatCd(String cntrCatCd) {
		this.cntrCatCd = cntrCatCd;
	}
	public String getCntrSize() {
		return cntrSize;
	}
	public void setCntrSize(String cntrSize) {
		this.cntrSize = cntrSize;
	}
	
	// START - CR-CAB-20050823-02 TT billing changes - Valli 11-Aug-2006
	public long getMiscSeqNbr() {
		return miscSeqNbr;
	}
	public void setMiscSeqNbr(long miscSeqNbr) {
		this.miscSeqNbr = miscSeqNbr;
	}
	public String getRefNbr() {
		return refNbr;
	}
	public void setRefNbr(String refNbr) {
		this.refNbr = refNbr;
	}
	// END - CR-CAB-20050823-02 TT billing changes - Valli 11-Aug-2006
    
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
    
}

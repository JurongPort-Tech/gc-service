package sg.com.jp.generalcargo.domain;
import java.sql.Timestamp;

public class VesselTxnEventLogValueObject extends UserTimestampVO {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String      vvCd;
    private Timestamp	txnDttm;
    private String	billWharfInd;
    private String	billSvcChargeInd;
    private String	billStoreInd;
    private String	billProcessInd;
    // Added by Irene Tan on 15 Apr 2003 : to add additional fields for bulk cargo billing
    private String      billBulkInd;
    private String      billBulkProcessInd;
    private String      billStandbyInd;
    private String      billStandbyProcessInd;
    // end added by Irene Tan on 15 Apr 2003
    //added by Irene Tan on 29 May 2003 : to add additional fields for stuffing/unstuffing billing
    private String      billStuffInd;
    // end added by Irene Tan on 29 May 2003
    //added by Alicia on  27 Nov 2003 : to add additional fields for  Containerised Cargo
    private String     billCntrSvcInd;
    private String     billCntrStoreInd;
    private String     billCntrSvcProcessInd;
    private String     billCntrStoreProcessInd;
    // end added by Alicia on 27 Nov 2003
    private String	lastModifyUserId;
    private Timestamp 	lastModifyDttm;
	
    /** Creates new vesselTxnEventLogValueObject **/
    public VesselTxnEventLogValueObject() {
	vvCd                    = "";
	txnDttm                 = null;
	billWharfInd            = "N";
	billSvcChargeInd        = "N";
	billStoreInd            = "N";
	billProcessInd          = "N";
        // Added by Irene Tan on 15 Apr 2003 : to add additional fields for bulk cargo billing
        billBulkInd             = "N";
        billBulkProcessInd      = "N";
        billStandbyInd          = "N";
        billStandbyProcessInd   = "N";
        // end added by Irene Tan on 15 Apr 2003
        //added by Irene Tan on 29 May 2003 : to add additional fields for stuffing/unstuffing billing
        billStuffInd            = "N";
        // end added by Irene Tan on 29 May 2003 
         //added by Alicia on  27 Nov 2003 : to add additional fields for  Containerised Cargo
         billCntrSvcInd           = "N";
        billCntrStoreInd         = "N";
        billCntrSvcProcessInd= "N";
        billCntrStoreProcessInd= "N";
         // end added by Alicia on 27 Nov 2003
	lastModifyUserId	= "";
	lastModifyDttm		= null;
    }

    public void doGet(Object object) {}
    public void doSet(Object object) {}

    /** get methods **/
    public String getVvCd() { return vvCd; }
    public Timestamp getTxnDttm() { return txnDttm; }
    public String getBillWharfInd() { return billWharfInd; }
    public String getBillSvcChargeInd() { return billSvcChargeInd; }
    public String getBillStoreInd() { return billStoreInd; }
    public String getBillProcessInd() { return billProcessInd; }
    // Added by Irene Tan on 15 Apr 2003 : to add additional fields for bulk cargo billing
    public String getBillBulkInd() { return billBulkInd; }
    public String getBillBulkProcessInd() { return billBulkProcessInd; }
    public String getBillStandbyInd() { return billStandbyInd; }
    public String getBillStandbyProcessInd() { return billStandbyProcessInd; }
    // end added by Irene Tan on 15 Apr 2003
    //added by Irene Tan on 29 May 2003 : to add additional fields for stuffing/unstuffing billing
    public String getBillStuffInd() { return billStuffInd; }
    // end added by Irene Tan on 29 May 2003    
    //added by Alicia on  27 Nov 2003 : to add additional fields for  Containerised Cargo
    public String getBillCntrSvcInd() { return billCntrSvcInd; }
    public String getBillCntrStoreInd() { return billCntrStoreInd; }
    public String getBillCntrSvcProcessInd() { return billCntrSvcProcessInd; }
    public String getBillCntrStoreProcessInd(){ return billCntrStoreProcessInd; }
    // end added by Alicia on 27 Nov 2003
    public String getLastModifyUserId() { return lastModifyUserId; } 
    public Timestamp getLastModifyDttm() { return lastModifyDttm; }
	
    /** set method **/
    public void setVvCd(String vvCd) { this.vvCd = vvCd; }
    public void setTxnDttm(Timestamp txnDttm) { this.txnDttm = txnDttm; }
    public void setBillWharfInd(String billWharfInd) { this.billWharfInd = billWharfInd; }
    public void setBillSvcChargeInd(String billSvcChargeInd) { this.billSvcChargeInd = billSvcChargeInd; }
    public void setBillStoreInd(String billStoreInd) { this.billStoreInd = billStoreInd; }
    public void setBillProcessInd(String billProcessInd) { this.billProcessInd = billProcessInd; }
    // Added by Irene Tan on 15 Apr 2003 : to add additional fields for bulk cargo billing
    public void setBillBulkInd(String billBulkInd) { this.billBulkInd = billBulkInd; }
    public void setBillBulkProcessInd(String billBulkProcessInd) { this.billBulkProcessInd = billBulkProcessInd; }
    public void setBillStandbyInd(String billStandbyInd) { this.billStandbyInd = billStandbyInd; }
    public void setBillStandbyProcessInd(String billStandbyProcessInd) { this.billStandbyProcessInd = billStandbyProcessInd; }
    // end added by Irene Tan on 15 Apr 2003
    //added by Irene Tan on 29 May 2003 : to add additional fields for stuffing/unstuffing billing
    public void setBillStuffInd(String billStuffInd) { this.billStuffInd = billStuffInd; }
    // end added by Irene Tan on 29 May 2003   
     //added by Alicia on  27 Nov 2003 : to add additional fields for  Containerised Cargo
    public void setBillCntrSvcInd(String billCntrSvcInd) { this.billCntrSvcInd = billCntrSvcInd; }
    public void setBillCntrStoreInd  (String billCntrStoreInd  ) { this.billCntrStoreInd   = billCntrStoreInd  ; }
    public void setBillCntrSvcProcessInd(String  billCntrSvcProcessInd) { this. billCntrSvcProcessInd =  billCntrSvcProcessInd; }
    public void setBillCntrStoreProcessInd(String billCntrStoreProcessInd) { this.billCntrStoreProcessInd = billCntrStoreProcessInd; }
    // end added by Alicia on 27 Nov 2003
    public void setLastModifyUserId(String lastModifyUserId) { this.lastModifyUserId= lastModifyUserId; } 
    public void setLastModifyDttm(Timestamp lastModifyDttm) { this.lastModifyDttm	= lastModifyDttm; } 
    
    /** isModified method, returns true when a change is detected else returns false **/
    public boolean isModified(VesselTxnEventLogValueObject vesselTxnEventLogValueObject) {
    VesselTxnEventLogValueObject origVO  = new VesselTxnEventLogValueObject();
        
    if (vesselTxnEventLogValueObject.getVvCd().equals(origVO.getVvCd())							&&
	vesselTxnEventLogValueObject.getTxnDttm() == origVO.getTxnDttm()						&&
	vesselTxnEventLogValueObject.getBillWharfInd().equals(origVO.getBillWharfInd())			&&
	vesselTxnEventLogValueObject.getBillSvcChargeInd().equals(origVO.getBillSvcChargeInd())	&&
	vesselTxnEventLogValueObject.getBillStoreInd().equals(origVO.getBillStoreInd())			&&
	vesselTxnEventLogValueObject.getBillProcessInd().equals(origVO.getBillProcessInd())		&&
        // Added by Irene Tan on 15 Apr 2003 : to add additional fields for bulk cargo billing
        vesselTxnEventLogValueObject.getBillBulkInd().equals(origVO.getBillBulkInd())                       &&
	vesselTxnEventLogValueObject.getBillBulkProcessInd().equals(origVO.getBillBulkProcessInd())         &&
	vesselTxnEventLogValueObject.getBillStandbyInd().equals(origVO.getBillStandbyInd())                 &&
	vesselTxnEventLogValueObject.getBillStandbyProcessInd().equals(origVO.getBillStandbyProcessInd())   &&
        // end added by Irene Tan on 15 Apr 2003
        //added by Irene Tan on 29 May 2003 : to add additional fields for stuffing/unstuffing billing
        vesselTxnEventLogValueObject.getBillStuffInd().equals(origVO.getBillStuffInd()) &&
        // end added by Irene Tan on 29 May 2003    
        //added by Alicia on  27 Nov 2003 : to add additional fields for  Containerised Cargo
        vesselTxnEventLogValueObject.getBillCntrSvcInd().equals(origVO.getBillCntrSvcInd()) &&
        vesselTxnEventLogValueObject.getBillCntrStoreInd().equals(origVO.getBillCntrStoreInd()) &&
        vesselTxnEventLogValueObject.getBillCntrSvcProcessInd().equals(origVO.getBillCntrSvcProcessInd()) &&
        vesselTxnEventLogValueObject.getBillCntrStoreProcessInd().equals(origVO.getBillCntrStoreProcessInd()) &&      
        // end added by Alicia on 27 Nov 2003
	vesselTxnEventLogValueObject.getLastModifyUserId().equals(origVO.getLastModifyUserId())	&&
	vesselTxnEventLogValueObject.getLastModifyDttm() == origVO.getLastModifyDttm()			)
        return false;
    else
        return true;    
    }
}

package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TempProcessChargeValueObject extends UserTimestampVO {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer                 versionNbr;
	    private String                  tariffCd;
	    private Integer                 tierSeqNbr;
	    private double                  nbrTimeUnit;
	    private double                  nbrOtherUnit;
	    private String                  custCd; 
	    private String                  acctNbr;
	    private String                  contractNbr;
	    private String                  vvCd;
	    private String                  slotOprCd;
	    private String                  cntrOprCd;
	    private Integer                 cntrSeqNbr;
	    private Timestamp	            txnDttm;
	    private String                  txnCd;
	    private Integer                 seqNbr;

	    /** Creates new TempProcessChargeValueObject */
	    public TempProcessChargeValueObject() {
	        versionNbr                  = new Integer(0);
	        tariffCd                    = null;
	        tierSeqNbr                  = new Integer(0);
	        nbrTimeUnit                 = 0;
	        nbrOtherUnit                = 0;
	        custCd                      = null;
	        acctNbr                     = null;
	        contractNbr                 = null;
	        vvCd                        = null;
	        slotOprCd                   = null;
	        cntrOprCd                   = null;
	        cntrSeqNbr                  = new Integer(0);
	        txnDttm                     = null;
	        txnCd                       = null;
	        seqNbr                      = new Integer(0);
	    }

	    public void doGet(Object object) {
	    }
	    
	    public void doSet(Object object) {
	    }

	    /**
	     *	This method retrieves the version number 
	     *
	     *  @param		void
	     *  @return 	Integer
	     */	 
	    public Integer getVersionNbr() {
	    	return(versionNbr);
	    } 
		
	    /**
	     *	This method retrieves the tariff code 
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getTariffCd() {
	    	return(tariffCd);
	    } 

	    /**
	     *	This method retrieves the tariff tier seq number
	     *
	     *  @param		void
	     *  @return 	Integer
	     */	 
	    public Integer getTierSeqNbr() {
	    	return(tierSeqNbr);
	    } 

	    /**
	     *	This method retrieves the number of time unit
	     *
	     *  @param		void
	     *  @return 	double
	     */	 
	    public double getNbrTimeUnit() {
	    	return(nbrTimeUnit);
	    } 
	    
	    /**
	     *	This method retrieves the number of other unit
	     *
	     *  @param		void
	     *  @return 	double
	     */	 
	    public double getNbrOtherUnit() {
	    	return(nbrOtherUnit);
	    } 
	        
	    /**
	     *	This method retrieves the Customer code
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getCustCd() {
	    	return(custCd);
	    } 
		
	    /**
	     *	This method retrieves the Account number
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getAcctNbr() {
	    	return(acctNbr);
	    } 

	    /**
	     *	This method retrieves the Contract number
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getContractNbr() {
	    	return(contractNbr);
	    } 

	    /**
	     *	This method retrieves the Vessel voyage code
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getVvCd() {
	    	return(vvCd);
	    } 
	    
	    /**
	     *	This method retrieves the Slot operator code
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getSlotOprCd() {
	    	return(slotOprCd);
	    } 
	    
	    /**
	     *	This method retrieves the Container operator code
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getCntrOprCd() {
	    	return(cntrOprCd);
	    } 
	    
	    /**
	     *	This method retrieves the container sequence number
	     *
	     *  @param		void
	     *  @return 	Integer
	     */	 
	    public Integer getCntrSeqNbr() {
	    	return(cntrSeqNbr);
	    } 

	    /**
	     *	This method retrieves the transaction timestamp
	     *
	     *  @param		void
	     *  @return 	Timestamp
	     */	 
	    public Timestamp getTxnDttm() {
	    	return(txnDttm);
	    } 

	    /**
	     *	This method retrieves the transaction code
	     *
	     *  @param		void
	     *  @return 	String
	     */	 
	    public String getTxnCd() {
	    	return(txnCd);
	    } 
	        
	    /**
	     *	This method retrieves the sequence number
	     *
	     *  @param		void
	     *  @return 	Integer
	     */	 
	    public Integer getSeqNbr() {
	    	return(seqNbr);
	    } 

	    /**
	     *	This method sets the version number 
	     *
	     *  @param		Integer
	     *  @return 	void
	     */	 
	    public void setVersionNbr(Integer versionNbr) {
	    	this.versionNbr                 = versionNbr;
	    } 
		
	    /**
	     *	This method sets the tariff code 
	     *
	     *  @param		String
	     *  @return 	void
	     */	 
	    public void setTariffCd(String tariffCd) {
	    	this.tariffCd                   = tariffCd;
	    } 
	    
	    /**
	     *	This method sets the tariff tier seq number
	     *
	     *  @param		Integer
	     *  @return 	void
	     */	 
	    public void setTierSeqNbr(Integer tierSeqNbr) {
	    	this.tierSeqNbr                 = tierSeqNbr;
	    } 

	    /**
	     *	This method sets the number of time unit
	     *
	     *  @param		double
	     *  @return 	void
	     */	 
	    public void setNbrTimeUnit(double nbrTimeUnit) {
	    	this.nbrTimeUnit                = nbrTimeUnit;
	    } 
	    
	    /**
	     *	This method sets the number of other unit
	     *
	     *  @param		double
	     *  @return 	void
	     */	 
	    public void setNbrOtherUnit(double nbrOtherUnit) {
	    	this.nbrOtherUnit               = nbrOtherUnit;
	    } 
	    
	    /**
	     *	This method sets the Customer code
	     *
	     *  @param		String
	     *  @return 	void
	     */	 
	    public void setCustCd(String custCd) {
	    	this.custCd                     = custCd;
	    } 
		
	    /**
	     *	This method sets the Account number
	     *
	     *  @param		String
	     *  @return 	void
	     */	 
	    public void setAcctNbr(String acctNbr) {
	    	this.acctNbr                    = acctNbr;
	    } 
		
	    /**
	     *	This method sets the Contract number
	     *
	     *  @param		String 
	     *  @return 	void
	     */	 
	    public void setContractNbr(String contractNbr) {
	    	this.contractNbr                = contractNbr;
	    } 
		
	    /**
	     *	This method sets the Vessel voyage code
	     *
	     *  @param		String 
	     *  @return 	void
	     */	 
	    public void setVvCd(String vvCd) {
	    	this.vvCd                       = vvCd;
	    } 

	    /**
	     *	This method sets the Slot operator code
	     *
	     *  @param		String 
	     *  @return 	void
	     */	 
	    public void setSlotOprCd(String slotOprCd) {
	    	this.slotOprCd                  = slotOprCd;
	    } 
	    
	    /**
	     *	This method sets the Container operator code
	     *
	     *  @param		String 
	     *  @return 	void
	     */	 
	    public void setCntrOprCd(String cntrOprCd) {
	    	this.cntrOprCd                  = cntrOprCd;
	    } 

	    /**
	     *	This method sets the container sequence number
	     *
	     *  @param		Integer
	     *  @return 	void
	     */	 
	    public void setCntrSeqNbr(Integer cntrSeqNbr) {
	    	this.cntrSeqNbr                 = cntrSeqNbr;
	    } 

	    /**
	     *	This method sets the transaction timestamp
	     *
	     *  @param		Timestamp
	     *  @return 	void
	     */	 
	    public void setTxnDttm(Timestamp txnDttm) {
	    	this.txnDttm                    = txnDttm;
	    } 

	    /**
	     *	This method sets the transaction code
	     *
	     *  @param		String
	     *  @return 	void
	     */	 
	    public void setTxnCd(String txnCd) {
	    	this.txnCd                      = txnCd;
	    } 
	    
	    /**
	     *	This method sets the sequence number
	     *
	     *  @param		Integer
	     *  @return 	void
	     */	 
	    public void setSeqNbr(Integer seqNbr) {
	    	this.seqNbr                     = seqNbr;
	    } 


	    /**
	     *	This method checks whether the value object has been modified before
	     *
	     *  @param		TempProcessChargeValueObject
	     *  @return 	boolean
	     */	 
	    public boolean isModified(TempProcessChargeValueObject tempProcessChargeValueObject) {
	        TempProcessChargeValueObject  origVO = new TempProcessChargeValueObject();
	            
	        if (tempProcessChargeValueObject.getVersionNbr().equals(origVO.getVersionNbr())         &&
	            tempProcessChargeValueObject.getTariffCd()==origVO.getTariffCd()                    &&
	            tempProcessChargeValueObject.getTierSeqNbr().equals(origVO.getTierSeqNbr())         &&
	            tempProcessChargeValueObject.getNbrTimeUnit()==origVO.getNbrTimeUnit()              &&
	            tempProcessChargeValueObject.getNbrOtherUnit()==origVO.getNbrOtherUnit()            &&
	            tempProcessChargeValueObject.getCustCd()==origVO.getCustCd()                        &&
	            tempProcessChargeValueObject.getAcctNbr()==origVO.getAcctNbr()                      &&
	            tempProcessChargeValueObject.getContractNbr()==origVO.getContractNbr()              &&
	            tempProcessChargeValueObject.getVvCd()==origVO.getVvCd()                            &&
	            tempProcessChargeValueObject.getSlotOprCd()==origVO.getSlotOprCd()                  &&
	            tempProcessChargeValueObject.getCntrOprCd()==origVO.getCntrOprCd()                  &&
	            tempProcessChargeValueObject.getCntrSeqNbr().equals(origVO.getCntrSeqNbr())         &&
	            tempProcessChargeValueObject.getTxnDttm()==origVO.getTxnDttm()                      &&
	            tempProcessChargeValueObject.getTxnCd()==origVO.getTxnCd()                          &&
	            tempProcessChargeValueObject.getSeqNbr().equals(origVO.getSeqNbr())                 ) {
	            return false;
	        }
	        else {
	            return true;
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

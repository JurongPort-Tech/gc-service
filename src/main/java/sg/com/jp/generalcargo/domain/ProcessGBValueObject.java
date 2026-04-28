package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessGBValueObject extends UserTimestampVO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ProcessGBValueObject.class);
    private String                  custCd; 
    private String                  acctNbr;
	private String					vvCd;
	private String					refInd;
	private String					refNbr;
	private String					tariffCd;
	private int						contractualYr;
	private int						tierSeqNbr;
	    
	private String					tariffMainCatCd;
	private String					tariffSubCatCd;
	protected String				schemeCd;
	
    private List<ChargeableBillValueObject> chargeableBillList	= new ArrayList<ChargeableBillValueObject>();
    private boolean                 errorInd;

    /** Creates new ProcessGBValueObject */
    public ProcessGBValueObject() {
        custCd                      = "";
        acctNbr                     = "";
		vvCd						= "";
		refInd						= "";
		refNbr						= "";
		tariffCd					= "";
		contractualYr				= 0;
		tierSeqNbr					= 0;

		tariffMainCatCd				= "";
		tariffSubCatCd				= "";
		schemeCd					= "";
				
        chargeableBillList			= new ArrayList<ChargeableBillValueObject>();
        
        errorInd                    = false;
    }

    /** Creates new ProcessGBValueObject */
    public ProcessGBValueObject(ProcessGBValueObject processGBValueObject) {
        this.copy(processGBValueObject);
    }
	
	public void copy(ProcessGBValueObject processGBValueObject){
        this.custCd				= processGBValueObject.getCustCd();
        this.acctNbr            = processGBValueObject.getAcctNbr();
		this.vvCd				= processGBValueObject.getVvCd();
		this.refInd				= processGBValueObject.getRefInd();
		this.refNbr				= processGBValueObject.getRefNbr();
	    this.tariffCd			= processGBValueObject.getTariffCd();
		this.contractualYr		= processGBValueObject.getContractualYr();
		this.tierSeqNbr			= processGBValueObject.getTierSeqNbr();
		this.tariffMainCatCd	= processGBValueObject.getTariffMainCatCd();
		this.tariffSubCatCd		= processGBValueObject.getTariffSubCatCd();
		this.schemeCd			= processGBValueObject.getSchemeCd();
		
        this.errorInd           = processGBValueObject.getErrorInd();
        
		if (processGBValueObject.getChargeCount() != 0) {
            this.removeAllCharge();
            for (int xcnt=0; xcnt<processGBValueObject.getChargeCount(); xcnt++) {
                ChargeableBillValueObject chargeableBillValueObject = new ChargeableBillValueObject(processGBValueObject.getCharge(xcnt));
                this.addCharge(chargeableBillValueObject);
            }
        }
    }

    public void doGet(Object object) {
    }
    
    public void doSet(Object object) {
    }

	/** get methods **/
	public String getCustCd(){ return custCd; }
	public String getAcctNbr(){ return acctNbr; }
	public String getVvCd(){ return vvCd; }
	public String getRefInd(){ return refInd; }
	public String getRefNbr(){ return refNbr; }
	public String getTariffCd(){ return tariffCd; }
	public int getContractualYr() { return contractualYr; }
	public int getTierSeqNbr(){	return tierSeqNbr; }
	public String getTariffMainCatCd(){ return tariffMainCatCd; }
	public String getTariffSubCatCd(){ return tariffSubCatCd; }
    public String getSchemeCd(){ return schemeCd; }
	public boolean getErrorInd() { return(errorInd); } 
	
	/** set methods **/
	public void setCustCd(String custCd){ this.custCd = custCd; }
	public void setAcctNbr(String acctNbr){ this.acctNbr = acctNbr; }
	public void setVvCd(String vvCd){ this.vvCd = vvCd; }
	public void setRefInd(String refInd){ this.refInd = refInd; }
	public void setRefNbr(String refNbr){ this.refNbr = refNbr; }
	public void setTariffCd(String tariffCd){ this.tariffCd = tariffCd; }
	public void setContractualYr(int contractualYr){ this.contractualYr = contractualYr; }
	public void setTierSeqNbr(int tierSeqNbr){ this.tierSeqNbr = tierSeqNbr; }
	public void setTariffMainCatCd(String tariffMainCatCd){ this.tariffMainCatCd = tariffMainCatCd; }
	public void setTariffSubCatCd(String tariffSubCatCd){ this.tariffSubCatCd = tariffSubCatCd; }
	public void setSchemeCd(String schemeCd){ this.schemeCd = schemeCd; }
	
    public void setErrorInd(boolean errorInd) { this.errorInd = errorInd; } 
    
    /**
     *	This method gets the total number of chargeable bill value objects
     * in the chargeable bill array
     *
     *  @param		ChargeableBillValueObject
     *  @return 	void
     */	 
    public int getChargeCount() {
        return chargeableBillList.size();
    }
    
    /**
     *	This method retrieve a chargeable bill value object to the chargeable bill array
     *
     *  @param		ChargeableBillValueObject
     *  @return 	void
     */	 
    public void addCharge(ChargeableBillValueObject chargeableBillValueObject) {
        chargeableBillList.add(chargeableBillValueObject);
    }
    
    /**
     *  This method gets a chargeable bill value object from the chargeable bill array 
	 *  based on the index 
     *
     *  @param		int
     *  @return 	ChargeableBillValueObject
     */	 
    public ChargeableBillValueObject getCharge(int index) {
        if (index >= chargeableBillList.size() || index < 0) {
            return null;
        }
        return (ChargeableBillValueObject)chargeableBillList.get(index);
    }
    
    /**
     *	This method gets all chargeable bill value objects to the chargeable bill array
     *
     *  @param		void
     *  @return 	ChargeableBillValueObject[]
     */	 
    public ChargeableBillValueObject[] getAllCharge() {
        ChargeableBillValueObject[] valueObject     = null;
        int							xcnt            = 0;
        
        if (chargeableBillList.size() < 1) {
            return null;
        }
        
        valueObject = new ChargeableBillValueObject[chargeableBillList.size()];
        
        for (xcnt=0; xcnt<chargeableBillList.size(); xcnt++) {
            valueObject[xcnt] = (ChargeableBillValueObject)chargeableBillList.get(xcnt);
        }
        return valueObject;
    }

    /**
     *	This method removes a chargeable bill value object from the chargeable bill array
     *
     *  @param		int
     *  @return 	ChargeableBillValueObject
     */	 
    public ChargeableBillValueObject removeCharge(int index) {
        return (ChargeableBillValueObject)chargeableBillList.remove(index);
    }
    
    /**
     *	This method removes all chargeable bill value object from the chargeable bill array
     *
     *  @param		void
     *  @return 	void
     */	 
    public void removeAllCharge() {
        if (chargeableBillList != null){
            chargeableBillList.clear();
        }
		else{
            log.info("List is null");
        }
    }
    
    /**
     *	This method checks whether the value object has been modified before
     *
     *  @param		ProcessGBValueObject
     *  @return 	boolean
     */	 
    public boolean isModified(ProcessGBValueObject processGBValueObject) {
        ProcessGBValueObject	origVO   = new ProcessGBValueObject();
                    
	    if (processGBValueObject.getCustCd().equals(origVO.getCustCd())						&& 
	        processGBValueObject.getAcctNbr().equals(origVO.getAcctNbr())					&&
	        processGBValueObject.getVvCd().equals(origVO.getVvCd())							&&
	        processGBValueObject.getRefInd().equals(origVO.getRefInd())						&&	
	        processGBValueObject.getRefNbr().equals(origVO.getRefNbr())						&&
	        processGBValueObject.getTariffCd().equals(origVO.getTariffCd())					&& 
	        processGBValueObject.getContractualYr() == origVO.getContractualYr()			&&
	        processGBValueObject.getTierSeqNbr() == origVO.getTierSeqNbr()					&&
			processGBValueObject.getTariffMainCatCd().equals(origVO.getTariffMainCatCd())	&&
			processGBValueObject.getTariffSubCatCd().equals(origVO.getTariffSubCatCd())		&& 
			processGBValueObject.getSchemeCd().equals(origVO.getSchemeCd())					&&
	        processGBValueObject.getAllCharge() == origVO.getAllCharge()					&&
            processGBValueObject.getErrorInd() == origVO.getErrorInd()						) {
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

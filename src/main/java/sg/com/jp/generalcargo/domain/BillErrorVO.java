package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class BillErrorVO extends UserTimestampVO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timestamp   runDttm;
    private String      runInd;
    private String      tariffMainCat;
    private String      tariffSubCat;
	private String      remarks;
	private String      processInd;
	
    public static final String RUN_IND_CREATE_BILL  = "CB";
    public static final String RUN_IND_CREATE_CN_DN = "CC";
    public static final String RUN_IND_PRINT_BILL   = "PB";
    public static final String RUN_IND_PRINT_CN_DN  = "PC";
    public static final String RUN_IND_POST_BILL    = "PO";
    
    /** Creates new BillErrorVO */
    public BillErrorVO() {
        runDttm         = null;
        runInd          = null;
        tariffMainCat   = null;
        tariffSubCat    = null;
        remarks         = null;
        processInd      = null;
	}

    public void doGet(Object object) {
    }
    
    public void doSet(Object object) {
    }

    /**
	 *  This method retrieves the run timestamp
	 *
	 *  @param	void
     *  @return Timestamp
     */	 
	public Timestamp getRunDttm() {
		return(runDttm);
	} 

    /**
	 *  This method retrieves the run indicator
	 *
	 *  @param	void
     *  @return String
     */	 
	public String getRunInd() {
		return(runInd);
	} 

    /**
	 *  This method retrieves the tariff main category
	 *
	 *  @param	void
     *  @return String
     */	 
	public String getTariffMainCat() {
		return(tariffMainCat);
	} 

    /**
	 *  This method retrieves the tariff sub category
	 *
	 *  @param	void
     *  @return String
     */	 
	public String getTariffSubCat() {
		return(tariffSubCat);
	} 

    /**
	 *  This method retrieves the remarks
	 *
	 *  @param	void
     *  @return String
     */	 
	public String getRemarks() {
		return(remarks);
	} 
    
    /**
	 *  This method retrieves the process indicator
	 *
	 *  @param	void
     *  @return String
     */	 
	public String getProcessInd() {
		return(processInd);
	} 

	/**
	 *  This method sets the run timestamp
	 *
	 *  @param	Timestamp
     *  @return void
     */	 
    public void setRunDttm(Timestamp runDttm) {
    	this.runDttm        = runDttm;
    }
    
    /**
	 *  This method sets the run indicator
	 *
	 *  @param	String
     *  @return void
     */	 
    public void setRunInd(String runInd) {
    	this.runInd         = runInd;
    }

    /**
	 *  This method sets the tariff main category
	 *
	 *  @param	String
     *  @return void
     */	 
    public void setTariffMainCat(String tariffMainCat) {
    	this.tariffMainCat  = tariffMainCat;
    }
    
    /**
	 *  This method sets the tariff sub category
	 *
	 *  @param	String
     *  @return void
     */	 
    public void setTariffSubCat(String tariffSubCat) {
    	this.tariffSubCat  = tariffSubCat;
    }
    
    /**
	 *  This method sets the remarks
	 *
	 *  @param	String
     *  @return void
     */	 
    public void setRemarks(String remarks) {
    	this.remarks        = remarks;
    }

    /**
	 *  This method sets the process indicator
	 *
	 *  @param	String
     *  @return void
     */	 
    public void setProcessInd(String processInd) {
    	this.processInd    = processInd;
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

package sg.com.jp.generalcargo.domain;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BerthRelatedValueObject extends UserTimestampVO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gcOperations;
    private Timestamp   atbDttm;
    private Timestamp   atuDttm;
    private Timestamp   codDttm;
    private Timestamp   colDttm;
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
    private Timestamp   etbDttm;
    private Timestamp   etuDttm;
    private Timestamp   gbFirstActDttm;
    private Timestamp   gbLastActDttm;
    private Timestamp   gbCodDttm;
    private Timestamp   gbColDttm;
    private Timestamp   gbBCodDttm;
    private Timestamp   gbBColDttm;
// end add by swho
	// Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
	private Timestamp	btrDttm;
	private Timestamp	etdDttm;
	// End added by Irene Tan on 18 Nov 2004 : CTCAB20030010
	
	  // EVM Enhancements Start Sripriya Feb 2018
	  private String berthNbr;
	  // EVM Enhancements End Sripriya Feb 2018
	  
    /** Creates new BerthRelatedValueObject */
    public BerthRelatedValueObject() {    	
        atbDttm         = null;
        atuDttm         = null;
        codDttm         = null;
        colDttm         = null;
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
        etbDttm         = null;
        etuDttm         = null;
        gbFirstActDttm  = null;
        gbLastActDttm   = null;
        gbCodDttm       = null;
        gbColDttm       = null;
        gbBCodDttm      = null;
        gbBColDttm      = null;
// end add by swho    
		// Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
		btrDttm			= null;
		etdDttm			= null;
		// End Added by Irene Tan on 18 Nov 2004 : CTCAB20030010   
		berthNbr = null;
		gcOperations 	= null;
    }

    public void doGet(Object object) {
    }
    
    public void doSet(Object object) {
    }

    public String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(String gcOperations) {
		this.gcOperations = gcOperations;
	}

	/**
     *  This method retrieves the ATB timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getAtbDttm() {
    	return(atbDttm);
    } 

    /**
     *  This method retrieves the ATU timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getAtuDttm() {
    	return(atuDttm);
    } 

    /**
     *  This method retrieves the COD timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getCodDttm() {
    	return(codDttm);
    } 

    /**
     *  This method retrieves the COL timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getColDttm() {
    	return(colDttm);
    } 

// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
    /**
     *  This method retrieves the ETB timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getEtbDttm() {
    	return(etbDttm);
    } 
    
    /**
     *  This method retrieves the ETU timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getEtuDttm() {
    	return(etuDttm);
    } 

    /**
     *  This method retrieves the GB First Activity timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbFirstActDttm() {
    	return(gbFirstActDttm);
    } 

    /**
     *  This method retrieves the GB Last Activity timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbLastActDttm() {
    	return(gbLastActDttm);
    } 
    
    /**
     *  This method retrieves the GB COD timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbCodDttm() {
    	return(gbCodDttm);
    } 
    
    /**
     *  This method retrieves the GB COL timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbColDttm() {
    	return(gbColDttm);
    } 

    /**
     *  This method retrieves the GB BCOD timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbBCodDttm() {
    	return(gbBCodDttm);
    } 
    
    /**
     *  This method retrieves the GB BCOL timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getGbBColDttm() {
    	return(gbBColDttm);
    } 
// end add by swho    

	// Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
    /**
     *  This method retrieves the BTR timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getBtrDttm() {
    	return(btrDttm);
    } 
	
    /**
     *  This method retrieves the ETD timestamp
     *
     *  @param	void
     *  @return Timestamp
     */	 
    public Timestamp getEtdDttm() {
    	return(etdDttm);
    } 
	// End Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
	    
    /**
     *  This method sets the ATB timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setAtbDttm(Timestamp atbDttm) {
    	this.atbDttm    = atbDttm;
    }

    /**
     *  This method sets the ATU timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setAtuDttm(Timestamp atuDttm) {
    	this.atuDttm    = atuDttm;
    }

    /**
     *  This method sets the COD timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setCodDttm(Timestamp codDttm) {
    	this.codDttm    = codDttm;
    }

    /**
     *  This method sets the COL timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setColDttm(Timestamp colDttm) {
    	this.colDttm    = colDttm;
    }

// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
    /**
     *  This method sets the ETB timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setEtbDttm(Timestamp etbDttm) {
    	this.etbDttm    = etbDttm;
    } 
    
    /**
     *  This method sets the ETU timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setEtuDttm(Timestamp etuDttm) {
    	this.etuDttm    = etuDttm;
    } 

    /**
     *  This method sets the GB First Activity timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbFirstActDttm(Timestamp gbFirstActDttm) {
    	this.gbFirstActDttm = gbFirstActDttm;
    } 

    /**
     *  This method sets the GB Last Activity timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbLastActDttm(Timestamp gbLastActDttm) {
    	this.gbLastActDttm  = gbLastActDttm;
    } 
    
    /**
     *  This method sets the GB COD timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbCodDttm(Timestamp gbCodDttm) {
    	this.gbCodDttm  = gbCodDttm;
    } 
    
    /**
     *  This method sets the GB COL timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbColDttm(Timestamp gbColDttm) {
    	this.gbColDttm  = gbColDttm;
    } 

    /**
     *  This method sets the GB BCOD timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbBCodDttm(Timestamp gbBCodDttm) {
    	this.gbBCodDttm = gbBCodDttm;
    } 
    
    /**
     *  This method sets the GB BCOL timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setGbBColDttm(Timestamp gbBColDttm) {
    	this.gbBColDttm = gbBColDttm;
    } 
// end add by swho    

	// Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
    /**
     *  This method sets the BTR timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setBtrDttm(Timestamp btrDttm) {
    	this.btrDttm = btrDttm;
    } 

    /**
     *  This method sets the ETD timestamp
     *
     *  @param	Timestamp
     *  @return void
     */	 
    public void setEtdDttm(Timestamp etdDttm) {
    	this.etdDttm = etdDttm;
    } 
	// End Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
    
    /**
     *	This method checks whether the value object has been modified before
     *
     *  @param		BerthRelatedValueObject
     *  @return 	boolean
     */	 
    public boolean isModified(BerthRelatedValueObject berthRelatedValueObject) {
        BerthRelatedValueObject origVO = new BerthRelatedValueObject();
            
        if (berthRelatedValueObject.getAtbDttm()        ==origVO.getAtbDttm()       &&
            berthRelatedValueObject.getAtuDttm()        ==origVO.getAtuDttm()       &&
            berthRelatedValueObject.getCodDttm()        ==origVO.getCodDttm()       &&
            berthRelatedValueObject.getColDttm()        ==origVO.getColDttm()       &&
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
            berthRelatedValueObject.getEtbDttm()        ==origVO.getEtbDttm()       &&
            berthRelatedValueObject.getEtuDttm()        ==origVO.getEtuDttm()       &&
            berthRelatedValueObject.getGbFirstActDttm() ==origVO.getGbFirstActDttm()&&
            berthRelatedValueObject.getGbLastActDttm()  ==origVO.getGbLastActDttm() &&
            berthRelatedValueObject.getGbCodDttm()      ==origVO.getGbCodDttm()     &&
            berthRelatedValueObject.getGbColDttm()      ==origVO.getGbColDttm()     &&
            berthRelatedValueObject.getGbBCodDttm()     ==origVO.getGbBCodDttm()    &&
            berthRelatedValueObject.getGbBColDttm()     ==origVO.getGbBColDttm()    &&
            // Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
            berthRelatedValueObject.getBtrDttm()     	==origVO.getBtrDttm()    	&&
            berthRelatedValueObject.getEtdDttm()     	==origVO.getEtdDttm()    &&	
            // End Added by Irene Tan on 18 Nov 2004 : CTCAB20030010
// end add by swho                
            berthRelatedValueObject.getBerthNbr()  == origVO.getBerthNbr()			&&
            berthRelatedValueObject.getGcOperations()  == origVO.getGcOperations()
        ) {
            return false;
        }
        else {
            return true;
        }
    }

	/**
	 * @return the berthNbr
	 */
	public String getBerthNbr() {
		return berthNbr;
	}

	/**
	 * @param berthNbr the berthNbr to set
	 */
	public void setBerthNbr(String berthNbr) {
		this.berthNbr = berthNbr;
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

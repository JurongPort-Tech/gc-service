package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VesselRelatedValueObject extends UserTimestampVO
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String  vvCd;
    private String  vslNm;
    private String  inVoyNbr;
    private String  outVoyNbr;
    private String  shpgSvcCd;
    private String  vslOprCd;
    private String terminal;
    
  //31082010 Cally CR
    private String shpgAgent = null;
    private String TAHolder = null; //vessel_scheme.ab_cd
    private String TAAcctNbr = null; //vessel_scheme.acct_nbr
    
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
    private String  cntrVslInd;
    private String  schemeType;
    private String  termType;
    private String  billAcctNbr;
    private String  gbArrivalWaiveCd;
    private String  gbDepartureWaiveCd;
    private String  gbBerthWaiveInd;
// end add    
    //Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
    private String 	osdWaiverCd;
    private String	isBilled;
    private String	isRejected;
    //End of change by Ruchika (Zensar)
//  Added by Madhu (Zensar): 14/11/2008 for vessel productivity billing
    private String	prodSurchargeInd;
    //End of change by Madhu (Zensar)

    private String  reeferParty; //<cfg for Reefer Connect/Disconnect option, 12.Nov.08> 
    
    //Added changes for OverStayDockage Partial Waiver input -Mcconsulting pteLtd Aug'15
    private String waiverType;
    private String billableDuartion;
    //Changes ends for OverStayDockage Partial Waiver request form changes-MCConsulting pte ltd
    
    // EVM Enhancements Start Sripriya Feb 2018
    private String cementVslInd;
    // EVM Enhancements End Sripriya Feb 2018
    
    //Combi Enhancements
    private String combi_gc_scheme;
    
    public String getCombi_gc_scheme() {
		return combi_gc_scheme;
	}


	public void setCombi_gc_scheme(String combiGcScheme) {
		combi_gc_scheme = combiGcScheme;
	}


	/** Creates new VesselRelatedValueObject */
    public VesselRelatedValueObject() {
    	vvCd                = null;
    	vslNm               = null;
        inVoyNbr            = null;
        outVoyNbr           = null;
        shpgSvcCd           = null;
        vslOprCd            = null;
//31082010 Cally CR
        shpgAgent 			= null;
        TAHolder 			= null; //vessel_scheme.ab_cd
        TAAcctNbr 			= null; //vessel_scheme.acct_nbr
        
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
        cntrVslInd          = null;
        schemeType          = null;
        termType            = null;
        billAcctNbr         = null;
        gbArrivalWaiveCd    = null;
        gbDepartureWaiveCd  = null;
        gbBerthWaiveInd     = null;
// end add by swho
        //Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
        osdWaiverCd			= null;
        isBilled			= null;
        isRejected			= null;
        //End of change by Ruchika (Zensar)
        //Added by Madhu (Zensar): 14/11/2008 for vessel productivity billing
        prodSurchargeInd	= null;
        //End of change by Madhu (Zensar)

        reeferParty			= null; //<cfg for Reefer Connect/Disconnect option, 12.Nov.08>

    }
    

    public void doGet(Object object) {
    }
    
    public void doSet(Object object) {
    }

    /** 
     *  This method retrieves the Vessel Voyage Code 
     *  @param	void
     *  @return String
     */	 
    public String getVvCd() {
        return(vvCd);
    } 

   /**
     *  This method retrieves the Vessel Name
     *
     *  @param	void
     *  @return String
     */	 
    public String getVslNm() {
        return(vslNm);
    } 

   /**
     *
     *  This method retrieves the In Voyage Number
     *
     *  @param	void
     *  @return String
     */	 
    public String getInVoyNbr() {
        return(inVoyNbr);
    } 
        
   /**
     *  This method retrieves the Out Voyage Number
     *
     *  @param	void
     *  @return String
     */	 
    public String getOutVoyNbr() {
    	return(outVoyNbr);
    } 
        
   /**
     *  This method retrieves the Shipping Service Code
     *
     *  @param 	void
     *  @return String
     */	 
    public String getShpgSvcCd() {
    	return(shpgSvcCd);
    } 
	
   /**
     *  This method retrieves the Vessel Operator Code
     *
     *  @param	void
     *  @return String
     */	 
    public String getVslOprCd() {
    	return(vslOprCd);
    } 

  //31082010 Cally CR
     public String getShpgAgent() {
    	return(shpgAgent);
    } 
	
   /**
     *  This method retrieves the vessel_scheme.ab_cd
     *
     *  @param	void
     *  @return String
     */	 
    public String getTAHolder() {
    	return(TAHolder);
    } 
    
    /**
     *  This method retrieves the vessel_scheme.acct_nbr
     *
     *  @param	void
     *  @return String
     */	 
    public String getTAAcctNbr() {
    	return(TAAcctNbr);
    } 
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
   /**
     *  This method retrieves the Container Vessel Indicator
     *
     *  @param	void
     *  @return String
     */	 
    public String getCntrVslInd() {
    	return(cntrVslInd);
    } 

   /**
     *  This method retrieves the Scheme Type
     *
     *  @param	void
     *  @return String
     */	 
    public String getSchemeType() {
    	return(schemeType);
    } 

   /**
     *  This method retrieves the Terminal Type
     *
     *  @param	void
     *  @return String
     */	 
    public String getTermType() {
    	return(termType);
    } 
    
   /**
     *  This method retrieves the Billing Account Number
     *
     *  @param	void
     *  @return String
     */	 
    public String getBillAcctNbr() {
    	return(billAcctNbr);
    } 

   /**
     *  This method retrieves the GB Arrival Waiver Code
     *
     *  @param	void
     *  @return String
     */	 
    public String getGbArrivalWaiveCd() {
    	return(gbArrivalWaiveCd);
    } 

   /**
     *  This method retrieves the GB Departure Waiver Code
     *
     *  @param	void
     *  @return String
     */	 
    public String getGbDepartureWaiveCd() {
    	return(gbDepartureWaiveCd);
    } 

   /**
     *  This method retrieves the GB Berthing Waiver Indicator
     *
     *  @param	void
     *  @return String
     */	 
    public String getGbBerthWaiveInd() {
    	return(gbBerthWaiveInd);
    } 
// end add by swho
    
    //Ruchika (Zensar): 15/08/2007 Added getters for the overstay dockage waiver details
    /**
     *  This method retrieves the Overstay Dockage Waiver Billing Indicator
     *
     *  @param	void
     *  @return String
     */
	public String getIsBilled() {
		return isBilled;
	}
	
	/**
     *  This method retrieves the overstay dockage waiver rejection Indicator
     *
     *  @param	void
     *  @return String
     */
	public String getIsRejected() {
		return isRejected;
	}
	
	/**
     *  This method retrieves the Overstay Dockage Waiver code
     *
     *  @param	void
     *  @return String
     */
	public String getOsdWaiverCd() {
		return osdWaiverCd;
	}
    //End of change by Ruchika (Zensar)
    
    /**
     *  This method sets the Vessel Voyage Code
     *
     *  @param	String
     *  @return void
     */	 
    public void setVvCd(String vvCd) {
    	this.vvCd       = vvCd;
    }

   /**
     *  This method sets the Vessel Name
     *
     *  @param	String
     *  @return void
     */	 
    public void setVslNm(String vslNm) {
    	this.vslNm      = vslNm;
    }

   /**
     *  This method sets the In Voyage Number
     *
     *  @param	String
     *  @return void
     */	 
    public void setInVoyNbr(String inVoyNbr) {
    	this.inVoyNbr   = inVoyNbr;
    }

   /**
     *  This method sets the Out Voyage Number
     *
     *  @param	String
     *  @return void
     */	 
    public void setOutVoyNbr(String outVoyNbr) {
    	this.outVoyNbr  = outVoyNbr;
    }

   /**
     *  This method sets the Shipping Service Code
     *
     *  @param	String
     *  @return void
     */	 
    public void setShpgSvcCd(String shpgSvcCd) {
    	this.shpgSvcCd  = shpgSvcCd;
    }

   /**
     *  This method sets the Vessel Operator Code
     *
     *  @param	String
     *  @return void
     */	 
    public void setVslOprCd(String vslOprCd) {
    	this.vslOprCd   = vslOprCd;
    }

  //31082010 Cally CR
    public void setShpgAgent(String shpgAgent) {
    	this.shpgAgent   = shpgAgent;
   } 
	
  /**
    *  This method retrieves the vessel_scheme.ab_cd
    *
    *  @param	void
    *  @return String
    */	 
   public void setTAHolder(String TAHolder) {
	   this.TAHolder   = TAHolder;
   } 
   
   /**
    *  This method retrieves the vessel_scheme.acct_nbr
    *
    *  @param	void
    *  @return String
    */	 
   public void setTAAcctNbr(String TAAcctNbr) {
	   this.TAAcctNbr   = TAAcctNbr;
   } 
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
   /**
     *  This method sets the Container Vessel Indicator
     *
     *  @param	String
     *  @return void
     */	 
    public void setCntrVslInd(String cntrVslInd) {
    	this.cntrVslInd = cntrVslInd;
    } 

   /**
     *  This method sets the Scheme Type
     *
     *  @param	String
     *  @return void
     */	 
    public void setSchemeType(String schemeType) {
    	this.schemeType = schemeType;
    } 

   /**
     *  This method sets the Terminal Type
     *
     *  @param	String
     *  @return void
     */	 
    public void setTermType(String termType) {
    	this.termType   = termType;
    } 
    
   /**
     *  This method sets the Billing Account Number
     *
     *  @param	String
     *  @return void
     */	 
    public void setBillAcctNbr(String billAcctNbr) {
    	this.billAcctNbr= billAcctNbr;
    } 

   /**
     *  This method sets the GB Arrival Waiver Code
     *
     *  @param	String
     *  @return void
     */	 
    public void setGbArrivalWaiveCd(String gbArrivalWaiveCd) {
    	this.gbArrivalWaiveCd   = gbArrivalWaiveCd;
    } 

   /**
     *  This method sets the GB Departure Waiver Code
     *
     *  @param	String
     *  @return void
     */	 
    public void setGbDepartureWaiveCd(String gbDepartureWaiveCd) {
    	this.gbDepartureWaiveCd   = gbDepartureWaiveCd;
    } 

   /**
     *  This method sets the GB Berthing Waiver Indicator
     *
     *  @param	String
     *  @return void
     */	 
    public void setGbBerthWaiveInd(String gbBerthWaiveInd) {
    	this.gbBerthWaiveInd   = gbBerthWaiveInd;
    } 
// end add by swho    

    //Ruchika (Zensar): 15/08/2007 Added setters for the overstay dockage waiver details
    /**
     *  This method sets the Overstay Dockage waiver billing indicator
     *
     *  @param	String
     *  @return void
     */	 
	public void setIsBilled(String isBilled) {
		this.isBilled = isBilled;
	}
	
	/**
     *  This method sets the Overstay Dockage Waiver rejection indicator
     *
     *  @param	String
     *  @return void
     */	 
	public void setIsRejected(String isRejected) {
		this.isRejected = isRejected;
	}
	
	/**
     *  This method sets the Overstay Dockage Waiver Code
     *
     *  @param	String
     *  @return void
     */	 
	public void setOsdWaiverCd(String osdWaiverCd) {
		this.osdWaiverCd = osdWaiverCd;
	}
    //End of change by Ruchika (Zensar)
    
	 //Added by Madhu (Zensar): 14/11/2008 Added setters for the for vessel productivity billing
    
	/**
	 * @return the prodSurchargeInd
	 */
	public String getProdSurchargeInd() {
		return prodSurchargeInd;
	}

	/**
	 * @param prodSurchargeInd the prodSurchargeInd to set
	 */
	public void setProdSurchargeInd(String prodSurchargeInd) {
		this.prodSurchargeInd = prodSurchargeInd;
	}
	//	End of change by Madhu (Zensar)

	// <cfg Reefer Connect/Disconnect,12.nov.08>
	public String getReeferParty() {
		return reeferParty;
	}

	public void setReeferParty(String reeferParty) {
		this.reeferParty = reeferParty;
	}
	//<cfg Reefer Connect/Disconnect,12.nov.08/>
	

    /**
     *	This method checks whether the value object has been modified before
     *
     *  @param		VesselRelatedValueObject
     *  @return 	boolean
     */	 
    public boolean isModified(VesselRelatedValueObject vesselRelatedValueObject) {
        VesselRelatedValueObject origVO = new VesselRelatedValueObject();
            
        if (vesselRelatedValueObject.getVvCd()              ==origVO.getVvCd()              &&
            vesselRelatedValueObject.getVslNm()             ==origVO.getVslNm()             &&
            vesselRelatedValueObject.getInVoyNbr()          ==origVO.getInVoyNbr()          &&
            vesselRelatedValueObject.getOutVoyNbr()         ==origVO.getOutVoyNbr()         &&
            vesselRelatedValueObject.getShpgSvcCd()         ==origVO.getShpgSvcCd()         &&
            vesselRelatedValueObject.getVslOprCd()          ==origVO.getVslOprCd()          &&
// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
            vesselRelatedValueObject.getCntrVslInd()        ==origVO.getCntrVslInd()        &&
            vesselRelatedValueObject.getSchemeType()        ==origVO.getSchemeType()        &&
            vesselRelatedValueObject.getTermType()          ==origVO.getTermType()          &&
            vesselRelatedValueObject.getBillAcctNbr()       ==origVO.getBillAcctNbr()       &&
            vesselRelatedValueObject.getGbArrivalWaiveCd()  ==origVO.getGbArrivalWaiveCd()  &&
            vesselRelatedValueObject.getGbDepartureWaiveCd()==origVO.getGbDepartureWaiveCd()&&
            vesselRelatedValueObject.getGbBerthWaiveInd()   ==origVO.getGbBerthWaiveInd()   &&
// end add by swho
        	
        	//Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
			vesselRelatedValueObject.getIsBilled()  		==origVO.getIsBilled()  		&&
            vesselRelatedValueObject.getIsRejected()		==origVO.getIsRejected()		&&
            //vesselRelatedValueObject.getOsdWaiverCd()   	==origVO.getOsdWaiverCd()		) {<cfg commented, Reefer Connect/Disconnect,12.nov.08>
            vesselRelatedValueObject.getOsdWaiverCd()   	==origVO.getOsdWaiverCd()		&&
            vesselRelatedValueObject.getReeferParty()		== origVO.getReeferParty()		&&
            //Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
            vesselRelatedValueObject.getBillableDuartion()  == origVO.getBillableDuartion() &&
            vesselRelatedValueObject.getWaiverType()        == origVO.getWaiverType() &&
            //Changes ends for OverStayDockage Partial Waiver request form changes-MCConsulting pte ltd
            
            //Added changes start for EVM Enhancements 13 Feb 2018
            vesselRelatedValueObject.getCementVslInd()  == origVO.getCementVslInd() &&
            //Added changes end for EVM Enhancements 13 Feb 2018
            
            //Combi Enhancements
            vesselRelatedValueObject.getCombi_gc_scheme() == origVO.getCombi_gc_scheme()
            
        ) { //<cfg added Reefer Connect/Disconnect,12.nov.08>
            //End of change by Ruchika (Zensar)
                return false;
        }
        else {
            return true;
        }
    }
/**
 * 
 * @param waiverType
 */

    public void setWaiverType(String waiverType) {
        this.waiverType = waiverType;
    }

/**
 * 
 * @return waiverType
 */
    public String getWaiverType() {
        return waiverType;
    }
/**
 * 
 * @param billableDuartion
 */

    public void setBillableDuartion(String billableDuartion) {
        this.billableDuartion = billableDuartion;
    }
/**
 * 
 * @return billableDuartion
 */

    public String getBillableDuartion() {
        return billableDuartion;
    }


/**
 * @return the cementVslInd
 */
public String getCementVslInd() {
	return cementVslInd;
}


/**
 * @param cementVslInd the cementVslInd to set
 */
public void setCementVslInd(String cementVslInd) {
	this.cementVslInd = cementVslInd;
}


public String getTerminal() {
	return terminal;
}


public void setTerminal(String terminal) {
	this.terminal = terminal;
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

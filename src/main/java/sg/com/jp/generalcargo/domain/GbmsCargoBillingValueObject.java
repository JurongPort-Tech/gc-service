package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *System Name: GBMS (General Bulk Cargo Management System)
 *Component ID: GbmsCargoBillingValueObject.java
 *Component Description: Stores  Gbms Cab Value Objects
 *
 *@author      Balaji R.k.
 *@version     20 July 2002
 */

/*
 Revision History
 ================
 * Author   Request Number  Description of Change               Version     Date Released
 * Balaji                      Creation				1.0         20 July 2002
 * TVS                      Added Method to set and get
 *                          UnStuffSeqNo                        1.3         23 Sep 2003
 * ThachPhung/FPT           CR-CAB-20081119-45 RORO - Added new property to support
 *                          RORO get corrected cargo status     1.4         19 Jan 2009
 * Cecille					GB bill display enhancements					15 June 2009
 * MCConsulting             Add deliveryToEPC field             1.5         28 Nov 2014
 */

public class GbmsCargoBillingValueObject
    extends GeneralEventLogValueObject {
  protected String cargoCategory;
  protected String vesselScheme;
  protected String cargoStatus;
  protected String transStatus;
  protected String mixedSchemeAcct;
  protected String saAcct;
  protected String abAcct;
  protected String cargoAcct;
  protected String paymentMode;
  protected String abCd;
  protected String transType;
  protected String vesselType;
  protected String opsInd;
  protected String subCat;
  protected String mschInd;
  protected String chType;
  protected String edotAcctNbr;
  protected String firstcarSch;
  protected String impSAacct;
  protected String impABacct;
  protected String impmixschactnbr;
  protected String edopaymode;
  //added by Balaji (10th Jan 2003)
  protected String edoBillServiceTriggeredInd;
  protected String edoBillWharfTriggeredInd;
  //end added by Balaji (10th Jan 2003)
  protected String edoBillStoreTriggeredInd;
  protected String satenind;
  protected String sctenind;
  protected String whind;
  protected String fsdays;
  //added by TVS 23-09-2003 for supressing of charges
  protected String unstuffseqno;
  protected String cargoStatusForRORO;//ThachPhung added 19Jan09 to support for RORO get corrected Mvmt

  protected String ssRefNbr; //<cfg, GB bill display enhancements, 15.jun.09/>

  protected String deliveryToEPC; //MCC add new field deliveryToEPC
  
  private String type;
  private String tariffSubCatCd;


public GbmsCargoBillingValueObject() {
    cargoCategory = "";
    vesselScheme = "";
    cargoStatus = "";
    cargoStatusForRORO = "";
    transStatus = "";
    mixedSchemeAcct = "";
    saAcct = "";
    abAcct = "";
    cargoAcct = "";
    abCd = "";
    transType = "";
    vesselType = "";
    opsInd = "";
    subCat = "";
    mschInd = "";
    chType = "";
    edotAcctNbr = "";
    firstcarSch = "";
    impSAacct = "";
    impABacct = "";
    impmixschactnbr = "";
    edopaymode = "";
    //added by Balaji (10th Jan 2003)
    edoBillServiceTriggeredInd = "";
    edoBillWharfTriggeredInd = "";
    //end added by Balaji (10th Jan 2003)
    satenind = "";
    sctenind = "";
    whind = "";
    fsdays = "";
    edoBillStoreTriggeredInd = "";

    //added by TVS 23-09-2003 for supressing of charges
    unstuffseqno="0";

    ssRefNbr="";//<cfg, GB bill display enhancements, 15.jun.09/>

    deliveryToEPC=""; //MCC add new field

  }

  /** get methods **/
  public String getCargoCategory() {
    return cargoCategory;
  }

  public String getVesselScheme() {
    return vesselScheme;
  }

  public String getCargoStatus() {
	  //ThachPhung starts on 19Jan09 - work around to get corrected cargo status for RORO and do not change anything in old flow
	  if (("01".equalsIgnoreCase(type) ||  "02".equalsIgnoreCase(type) || "03".equalsIgnoreCase(type)) &&
			  "RO".equalsIgnoreCase(tariffSubCatCd)) {
		  return getCargoStatusForRORO();
	  } else {
    return cargoStatus;
	  }
    // ThachPhung end
  }

  public String getTransStatus() {
    return transStatus;
  }

  public String getMixedSchemeAcct() {
    return mixedSchemeAcct;
  }

  public String getSaAcct() {
    return saAcct;
  }

  public String getAbAcct() {
    return abAcct;
  }

  public String getCargoAcct() {
    return cargoAcct;
  }

  public String getAbCd() {
    return abCd;
  }

  public String getPaymentMode() {
    return paymentMode;
  }

  public String getTransType() {
    return transType;
  }

  public String getVesselType() {
    return vesselType;
  }

  public String getOpsInd() {
    return opsInd;
  }

  public String getSubCat() {
    return subCat;
  }

  public String getMschInd() {
    return mschInd;
  }

  public String getChType() {
    return chType;
  }

  public String getEdotAcctNbr() {
    return edotAcctNbr;
  }

  public String getFirstcarSch() {
    return firstcarSch;
  }

  public String getImpSAacct() {
    return impSAacct;
  }

  public String getImpABacct() {
    return impABacct;
  }

  public String getImpmixschactnbr() {
    return impmixschactnbr;
  }

  public String getEdopaymode() {
    return edopaymode;
  }

  //added by Balaji (10th Jan 2003)
  public String getEdoBillServiceTriggeredInd() {
    return edoBillServiceTriggeredInd;
  }

  public String getEdoBillWharfTriggeredInd() {
    return edoBillWharfTriggeredInd;
  }

  //end added by Balaji (10th Jan 2003)
  public String getEdoBillStoreTriggeredInd() {
    return edoBillStoreTriggeredInd;
  }

  public String getSatenind() {
    return satenind;
  }

  public String getSctenind() {
    return sctenind;
  }

  public String getWhind() {
    return whind;
  }

  public String getFsdays() {
    return fsdays;
  }

//added by TVS 23-09-2003 for suppressing of charges-Start
  public String getUnStuffSeqNo(){
    return unstuffseqno;
  }
//added by TVS 23-09-2003 for suppressing of charges-End

  //MCC add new field
  public String getDeliveryToEPC() {
      return deliveryToEPC;
  }


  public void setDeliveryToEPC(String deliveryToEPC) {
      this.deliveryToEPC = deliveryToEPC;
  }

  /** set method **/
  public void setCargoCategory(String cargoCategory) {
    this.cargoCategory = cargoCategory;
  }

  public void setVesselScheme(String vesselScheme) {
    this.vesselScheme = vesselScheme;
  }

  public void setCargoStatus(String crgStatus) {
    this.cargoStatus = cargoStatus;
    // ThachPhung starts on 19Jan09 - work around to get corrected cargo status for RORO and do not change anything in old flow
    setCargoStatusForRORO(crgStatus);
    // ThachPhung end
  }

  public void setTransStatus(String transStatus) {
    this.transStatus = transStatus;
  }

  public void setMixedSchemeAcct(String mixedSchemeAcct) {
    this.mixedSchemeAcct = mixedSchemeAcct;
  }

  public void setSaAcct(String saAcct) {
    this.saAcct = saAcct;
  }

  public void setAbAcct(String abAcct) {
    this.abAcct = abAcct;
  }

  public void setCargoAcct(String cargoAcct) {
    this.cargoAcct = cargoAcct;
  }

  public void setPaymentMode(String paymentMode) {
    this.paymentMode = paymentMode;
  }

  public void setAbCd(String abCd) {
    this.abCd = abCd;
  }

  public void setTransType(String transType) {
    this.transType = transType;
  }

  public void setVesselType(String vesselType) {
    this.vesselType = vesselType;
  }

  public void setOpsInd(String opsInd) {
    this.opsInd = opsInd;
  }

  public void setSubCat(String subCat) {
    this.subCat = subCat;
  }

  public void setMschInd(String mschInd) {
    this.mschInd = mschInd;
  }

  public void setChType(String chType) {
    this.chType = chType;
  }

  public void setEdotAcctNbr(String edotAcctNbr) {
    this.edotAcctNbr = edotAcctNbr;
  }

  public void setFirstcarSch(String firstcarSch) {
    this.firstcarSch = firstcarSch;
  }

  public void setImpSAacct(String impSAacct) {
    this.impSAacct = impSAacct;
  }

  public void setImpABacct(String impABacct) {
    this.impABacct = impABacct;
  }

  public void setImpmixschactnbr(String impmixschactnbr) {
    this.impmixschactnbr = impmixschactnbr;
  }

  public void setEdopaymode(String edopaymode) {
    this.edopaymode = edopaymode;
  }

  //added by Balaji (10th Jan 2003)
  public void setEdoBillServiceTriggeredInd(String edoBillServiceTriggeredInd) {
    this.edoBillServiceTriggeredInd = edoBillServiceTriggeredInd;
  }

  public void setEdoBillWharfTriggeredInd(String edoBillWharfTriggeredInd) {
    this.edoBillWharfTriggeredInd = edoBillWharfTriggeredInd;
  }

  //end added by Balaji (10th Jan 2003)
  public void setEdoBillStoreTriggeredInd(String edoBillStoreTriggeredInd) {
    this.edoBillStoreTriggeredInd = edoBillStoreTriggeredInd;
  }

  public void setSatenind(String satenind) {
    this.satenind = satenind;
  }

  public void setSctenind(String sctenind) {
    this.sctenind = sctenind;
  }

  public void setWhind(String whind) {
    this.whind = whind;
  }

  public void setFsdays(String fsdays) {
    this.fsdays = fsdays;
  }

  //added by TVS 23-09-2003 for suppressing of charges-Start
  public void setUnStuffSeqNo(String unstuffseqno){
    this.unstuffseqno=unstuffseqno;
  }
  //added by TVS 23-09-2003 for suppressing of charges-End

	public String getCargoStatusForRORO() {
		return cargoStatusForRORO;
	}

	public void setCargoStatusForRORO(String cargoStatusForRORO) {
		this.cargoStatusForRORO = cargoStatusForRORO;
	}

	//<cfg, GB bill display enhancements, 15.jun.09>

	/**
	 * @return Returns the ssRefNbr.
	 */
	public String getSsRefNbr() {
		return ssRefNbr;
	}

	/**
	 * @param ssRefNbr The ssRefNbr to set.
	 */
	public void setSsRefNbr(String ssRefNbr) {
		this.ssRefNbr = ssRefNbr;
	}


	//<cfg, GB bill display enhancements, 15.jun.09/>
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}


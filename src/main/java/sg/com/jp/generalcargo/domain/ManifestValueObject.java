package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 *            System Name : GBMS (General and Bulk Cargo Management Systems)
 *            Module : cargo - manifest Component ID : ManifestValueObject.java
 *            Component Description:
 *
 * @author
 * @version
 */

/*
 * Revision History ---------------- Author Request Number Description of Change
 * Version Date Released JPPL/IT/001/2001 Creation 1.0 - Phase 1
 *
 * Vani JPPL/IT/001/2001 Changed to add UnStuff 1.3 30 Oct 2003 - Phase 2
 * Indicator
 *
 * TVS JPPL/IT/001/2001 Added new variable - Phase 2 unstuffclosestatus and 1.4
 * 27 Nov 2003 set and get method for accessing it
 *
 * Irene Tan GSL-2003-000084 To disallow deletion of bulk 1.5 19 Dec 2003
 * manifest after EDO created MCC 20 Feb 2015 Added EPC_IND in ESN
 */

public class ManifestValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String varNbr;
	String blNo;
	String crgType;
	String hsCode;
	String crgDesc;
	String crgMarking;
	String pkgType;
	String noofPkgs;
	String grWt;
	String grMsmt;
	String crgStatus;
	String dgInd;
	String opInd;
	String stgInd;
	String portL;
	String portD;
	String portFD;
	String consignee;
	String consigneeCoyCode;
	String cntrType;
	String cntrSize;
	String edostat;
	String cntr1;
	String cntr2;
	String cntr3;
	String cntr4;
	String seqNo;
	String edonbrpkgs;
	String pkgn;
	String crgn;
	String vslstat;
	String portLn;
	String portDn;
	String portFDn;
	String dnstat;
	String tnstat;
	String tdnstat;
	String strUnStfInd;
	String unstuffclosestatus;
	// Add by thanhnv2::Start
	String adviseBy = "";
	String adviseDate = "";
	String adviseMode = "";
	String amendCharged = "";
	String waiveCharge = "";
	String waiveReason = "";
	// Add by thanhnv2::End
	// Add by Zhenguo Deng
	String category;

	// VietNguyen (FPT) added on 03-Jul-2012 for Enhancement HS Code : START
	String hsSubCodeFr;
	String hsSubCodeTo;
	String hsSubCodeDesc;

	// VietNguyen (FPT) added on 03-Jul-2012 for Enhancement HS Code : END
	String scheme;
	String vvStatusInd;

	// MCC for EPC_IND
	String deliveryToEPC;

	// Start ThanhPT6, JCMS CR 6.10
	String closeBJInd;
	// End ThanhPT6

	String subScheme;
	String gcOperations;
	String terminal;
	// Added by NS on 25-09-20
	private String selectedCargo;
	private String selectedCargoValue;
	
	// Start CR FTZ HSCODE - NS JULY 2024
	String customHsCode;
	String consigneeAddr = "";
	String shipperNm = "";
	String shipperAddr = "";
	String notifyParty = "";
	String notifyPartyAddr = "";
	String placeOfDelivery = "";
	String placeOfReceipt = "";
	// ENd CR FTZ HSCODE - NS JULY 2024

	public String getSelectedCargoValue() {
		return selectedCargoValue;
	}

	public void setSelectedCargoValue(String selectedCargoValue) {
		this.selectedCargoValue = selectedCargoValue;
	}

	public String getSelectedCargo() {
		return selectedCargo;
	}

	public void setSelectedCargo(String selectedCargo) {
		this.selectedCargo = selectedCargo;
	}

	public String getCloseBJInd() {
		return closeBJInd;
	}

	public void setCloseBJInd(String closeBJInd) {
		this.closeBJInd = closeBJInd;
	}

	public String getDeliveryToEPC() {
		return deliveryToEPC;
	}

	public void setDeliveryToEPC(String deliveryToEPC) {
		this.deliveryToEPC = deliveryToEPC;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	String createCustCd; // added by thanhnv2

	public String getCreateCustCd() {
		return createCustCd;
	}

	public void setCreateCustCd(String createCustCd) {
		this.createCustCd = createCustCd;
	}

	// Add by thanhnv2::Start
	public String getAdviseBy() {
		return adviseBy;
	}

	public void setAdviseBy(String adviseBy) {
		this.adviseBy = adviseBy;
	}

	public String getAdviseDate() {
		return adviseDate;
	}

	public void setAdviseDate(String adviseDate) {
		this.adviseDate = adviseDate;
	}

	public String getAdviseMode() {
		return adviseMode;
	}

	public void setAdviseMode(String adviseMode) {
		this.adviseMode = adviseMode;
	}

	public String getAmendCharged() {
		return amendCharged;
	}

	public void setAmendCharged(String amendCharged) {
		this.amendCharged = amendCharged;
	}

	public String getWaiveCharge() {
		return waiveCharge;
	}

	public void setWaiveCharge(String waiveCharge) {
		this.waiveCharge = waiveCharge;
	}

	public String getWaiveReason() {
		return waiveReason;
	}

	public void setWaiveReason(String waiveReason) {
		this.waiveReason = waiveReason;
	}
	// Add by thanhnv2::End

	// added by Irene Tan on 19 Dec 2003 : GSL-2003-000084
	double edoWt = 0.0;
	// end added by Irene Tan on 19 Dec 2003

	public ManifestValueObject() {
	}

	public void setVarNbr(String varNbr) {
		this.varNbr = varNbr;
	}

	public void setBlNo(String blNo) {
		this.blNo = blNo;
	}

	public void setCrgType(String crgType) {
		this.crgType = crgType;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public void setCrgDesc(String crgDesc) {
		this.crgDesc = crgDesc;
	}

	public void setCrgMarking(String crgMarking) {
		this.crgMarking = crgMarking;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	public void setNoofPkgs(String noofPkgs) {
		this.noofPkgs = noofPkgs;
	}

	public void setGrWt(String grWt) {
		this.grWt = grWt;
	}

	public void setGrMsmt(String grMsmt) {
		this.grMsmt = grMsmt;
	}

	public void setCrgStatus(String crgStatus) {
		this.crgStatus = crgStatus;
	}

	public void setDgInd(String dgInd) {
		this.dgInd = dgInd;
	}

	public void setOpInd(String opInd) {
		this.opInd = opInd;
	}

	public void setStgInd(String stgInd) {
		this.stgInd = stgInd;
	}

	public void setPortL(String portL) {
		this.portL = portL;
	}

	public void setPortD(String portD) {
		this.portD = portD;
	}

	public void setPortFD(String portFD) {
		this.portFD = portFD;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public void setCntrType(String cntrType) {
		this.cntrType = cntrType;
	}

	public void setCntrSize(String cntrSize) {
		this.cntrSize = cntrSize;
	}

	public void setEdostat(String edoStat) {
		this.edostat = edoStat;
	}

	public void setCntr1(String cntr1) {
		this.cntr1 = cntr1;
	}

	public void setCntr2(String cntr2) {
		this.cntr2 = cntr2;
	}

	public void setCntr3(String cntr3) {
		this.cntr3 = cntr3;
	}

	public void setCntr4(String cntr4) {
		this.cntr4 = cntr4;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public void setEdonbrpkgs(String edonbrpkgs) {
		this.edonbrpkgs = edonbrpkgs;
	}

	public void setPkgn(String pkgn) {
		this.pkgn = pkgn;
	}

	public void setCrgn(String crgn) {
		this.crgn = crgn;
	}

	public void setVslstat(String vslstat) {
		vslstat = vslstat;
	}

	public void setPortDn(String portDn) {
		this.portDn = portDn;
	}

	public void setPortLn(String portLn) {
		this.portLn = portLn;
	}

	public void setPortFDn(String portFDn) {
		this.portFDn = portFDn;
	}

	public void setDnstat(String dnstat) {
		this.dnstat = dnstat;
	}

	public void setTnstat(String tnstat) {
		this.tnstat = tnstat;
	}

	public void setTdnstat(String tdnstat) {
		this.tdnstat = tdnstat;
	}

	public void setUnStfInd(String strUnStfInd) {
		this.strUnStfInd = strUnStfInd;
	}

	public void setUnStuffCloseStatus(String unstuffclosestatus) {
		this.unstuffclosestatus = unstuffclosestatus;
	}

	// added by Irene Tan on 19 Dec 2003 : GSL-2003-000084
	public void setEdoWt(double edoWt) {
		this.edoWt = edoWt;
	}
	// end added by Irene Tan on 19 Dec 2003

	public String getVarNbr() {
		return varNbr;
	}

	public String getBlNo() {
		return blNo;
	}

	public String getCrgType() {
		return crgType;
	}

	public String getHsCode() {
		return hsCode;
	}

	public String getCrgDesc() {
		return crgDesc;
	}

	public String getCrgMarking() {
		return crgMarking;
	}

	public String getPkgType() {
		return pkgType;
	}

	public String getNoofPkgs() {
		return noofPkgs;
	}

	public String getGrWt() {
		return grWt;
	}

	public String getGrMsmt() {
		return grMsmt;
	}

	public String getCrgStatus() {
		return crgStatus;
	}

	public String getDgInd() {
		return dgInd;
	}

	public String getOpInd() {
		return opInd;
	}

	public String getStgInd() {
		return stgInd;
	}

	public String getPortL() {
		return portL;
	}

	public String getPortD() {
		return portD;
	}

	public String getPortFD() {
		return portFD;
	}

	public String getConsignee() {
		return consignee;
	}

	public String getCntrType() {
		return cntrType;
	}

	public String getCntrSize() {
		return cntrSize;
	}

	public String getEdostat() {
		return edostat;
	}

	public String getCntr1() {
		return cntr1;
	}

	public String getCntr2() {
		return cntr2;
	}

	public String getCntr3() {
		return cntr3;
	}

	public String getCntr4() {
		return cntr4;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public String getEdonbrpkgs() {
		return edonbrpkgs;
	}

	public String getPkgn() {
		return pkgn;
	}

	public String getCrgn() {
		return crgn;
	}

	public String getVslstat() {
		return vslstat;
	}

	public String getPortDn() {
		return portDn;
	}

	public String getPortLn() {
		return portLn;
	}

	public String getPortFDn() {
		return portFDn;
	}

	public String getDnstat() {
		return dnstat;
	}

	public String getTnstat() {
		return tnstat;
	}

	public String getTdnstat() {
		return tdnstat;
	}

	public String getUnStfInd() {
		return strUnStfInd;
	}

	public String getUnStuffCloseStatus() {
		return unstuffclosestatus;
	}

	// added by Irene Tan on 19 Dec 2003 : GSL-2003-000084
	public double getEdoWt() {
		return edoWt;
	}
	// end added by Irene Tan on 19 Dec 2003

	/**
	 * @return the hsSubCodeFr
	 */
	public String getHsSubCodeFr() {
		return hsSubCodeFr;
	}

	/**
	 * @param hsSubCodeFr the hsSubCodeFr to set
	 */
	public void setHsSubCodeFr(String hsSubCodeFr) {
		this.hsSubCodeFr = hsSubCodeFr;
	}

	/**
	 * @return the hsSubCodeTo
	 */
	public String getHsSubCodeTo() {
		return hsSubCodeTo;
	}

	/**
	 * @param hsSubCodeTo the hsSubCodeTo to set
	 */
	public void setHsSubCodeTo(String hsSubCodeTo) {
		this.hsSubCodeTo = hsSubCodeTo;
	}

	/**
	 * @return the hsSubCodeDesc
	 */
	public String getHsSubCodeDesc() {
		return hsSubCodeDesc;
	}

	/**
	 * @param hsSubCodeDesc the hsSubCodeDesc to set
	 */
	public void setHsSubCodeDesc(String hsSubCodeDesc) {
		this.hsSubCodeDesc = hsSubCodeDesc;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getConsigneeCoyCode() {
		return consigneeCoyCode;
	}

	public void setConsigneeCoyCode(String consigneeCoyCode) {
		this.consigneeCoyCode = consigneeCoyCode;
	}

	public String getVvStatusInd() {
		return vvStatusInd;
	}

	public void setVvStatusInd(String vvStatusInd) {
		this.vvStatusInd = vvStatusInd;
	}

	public String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(String subScheme) {
		this.subScheme = subScheme;
	}

	public String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(String gcOperations) {
		this.gcOperations = gcOperations;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getCustomHsCode() {
		return customHsCode;
	}

	public void setCustomHsCode(String customHsCode) {
		this.customHsCode = customHsCode;
	}

	public String getConsigneeAddr() {
		return consigneeAddr;
	}

	public void setConsigneeAddr(String consigneeAddr) {
		this.consigneeAddr = consigneeAddr;
	}

	public String getShipperNm() {
		return shipperNm;
	}

	public void setShipperNm(String shipperNm) {
		this.shipperNm = shipperNm;
	}

	public String getShipperAddr() {
		return shipperAddr;
	}

	public void setShipperAddr(String shipperAddr) {
		this.shipperAddr = shipperAddr;
	}

	public String getNotifyParty() {
		return notifyParty;
	}

	public void setNotifyParty(String notifyParty) {
		this.notifyParty = notifyParty;
	}

	public String getNotifyPartyAddr() {
		return notifyPartyAddr;
	}

	public void setNotifyPartyAddr(String notifyPartyAddr) {
		this.notifyPartyAddr = notifyPartyAddr;
	}

	public String getPlaceOfDelivery() {
		return placeOfDelivery;
	}

	public void setPlaceOfDelivery(String placeOfDelivery) {
		this.placeOfDelivery = placeOfDelivery;
	}

	public String getPlaceOfReceipt() {
		return placeOfReceipt;
	}

	public void setPlaceOfReceipt(String placeOfReceipt) {
		this.placeOfReceipt = placeOfReceipt;
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

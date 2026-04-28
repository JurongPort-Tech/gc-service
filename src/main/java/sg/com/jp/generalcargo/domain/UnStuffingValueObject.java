package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 *            System Name : GBMS (General and Bulk Cargo Management Systems)
 *            Module : containerised - unstuffing Component ID :
 *            UnStuffingValueObject.java Component Description: This class
 *            stores Unstuffing Values.
 * 
 * @author Vani
 * @version 19th Sept 2003
 * 
 * Revision History --------------- Author Request Number Description of Change
 * Version Date Released Vani - Creation 1.1 19 Sept 2003 TVS Added new variable
 * unstuffclosestatus and 1.2 27 Nov 2003 set and get method for accessing it
 * Satish - Added Storage Indicator, 1.3 23 Feb 2004 Port of Loading & Port of
 * Discharge.
 */

public class UnStuffingValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String varNbr;
	String blNo;
	String crgType;
	String hsCode;
	String hsCodeFr;
	String hsCodeTo;
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
	String edostat;
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
	String billaccno;
	String splcatcd;
	String grsVol;
	String cntrSz;
	String cntrNbr;
	String consNm;
	String blNbr;
	String billParty;
	String unstuffclosestatus;
	String consigneeCoyCode;
	String hsSubCodeDesc;

	public UnStuffingValueObject() {
	}

	public void setCrgn(String s) {
		crgn = s;
	}

	public String getCrgn() {
		return crgn;
	}

	public void setPkgn(String s) {
		pkgn = s;
	}

	public String getPkgn() {
		return pkgn;
	}

	public void setEdonbrpkgs(String s) {
		edonbrpkgs = s;
	}

	public String getEdonbrpkgs() {
		return edonbrpkgs;
	}

	public void setSeqNo(String s) {
		seqNo = s;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setVslstat(String s) {
		vslstat = s;
	}

	public String getVslstat() {
		return vslstat;
	}

	public void setBlNo(String s) {
		blNo = s;
	}

	public String getBlNo() {
		return blNo;
	}

	public void setCrgType(String s) {
		crgType = s;
	}

	public String getCrgType() {
		return crgType;
	}

	public void setHsCode(String s) {
		hsCode = s;
	}

	public String getHsCode() {
		return hsCode;
	}

	public void setCrgDesc(String s) {
		crgDesc = s;
	}

	public String getCrgDesc() {
		return crgDesc;
	}

	public void setCrgMarking(String s) {
		crgMarking = s;
	}

	public String getCrgMarking() {
		return crgMarking;
	}

	public void setPkgType(String s) {
		pkgType = s;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setNoofPkgs(String s) {
		noofPkgs = s;
	}

	public String getNoofPkgs() {
		return noofPkgs;
	}

	public void setGrWt(String s) {
		grWt = s;
	}

	public String getGrWt() {
		return grWt;
	}

	public void setGrMsmt(String s) {
		grMsmt = s;
	}

	public String getGrMsmt() {
		return grMsmt;
	}

	public void setCrgStatus(String s) {
		crgStatus = s;
	}

	public String getCrgStatus() {
		return crgStatus;
	}

	public void setDgInd(String s) {
		dgInd = s;
	}

	public String getDgInd() {
		return dgInd;
	}

	public void setOpInd(String s) {
		opInd = s;
	}

	public String getOpInd() {
		return opInd;
	}

	public void setStgInd(String stgInd) {
		this.stgInd = stgInd;
	}

	public String getStgInd() {
		return this.stgInd;
	}

	public void setPortL(String s) {
		portL = s;
	}

	public String getPortL() {
		return portL;
	}

	public void setPortD(String portD) {
		this.portD = portD;
	}

	public String getPortD() {
		return this.portD;
	}

	public void setPortLn(String s) {
		portLn = s;
	}

	public String getPortLn() {
		return portLn;
	}

	public void setPortDn(String portDn) {
		this.portDn = portDn;
	}

	public String getPortDn() {
		return portDn;
	}

	public void setPortFD(String s) {
		portFD = s;
	}

	public String getPortFD() {
		return portFD;
	}

	public void setPortFDn(String s) {
		portFDn = s;
	}

	public String getPortFDn() {
		return portFDn;
	}

	public void setVarNbr(String s) {
		varNbr = s;
	}

	public String getVarNbr() {
		return varNbr;
	}

	public void setEdostat(String s) {
		edostat = s;
	}

	public String getEdostat() {
		return edostat;
	}

	public void setDnstat(String s) {
		dnstat = s;
	}

	public String getDnstat() {
		return dnstat;
	}

	public void setTnstat(String s) {
		tnstat = s;
	}

	public String getTnstat() {
		return tnstat;
	}

	public void setTdnstat(String s) {
		tdnstat = s;
	}

	public String getTdnstat() {
		return tdnstat;
	}

	public void setBillAccNo(String s) {
		billaccno = s;
	}

	public String getBillAccNo() {
		return billaccno;
	}

	public void setSplCatCode(String s) {
		splcatcd = s;
	}

	public String getSplCatCode() {
		return splcatcd;
	}

	public void setGrossVol(String vol) {
		grsVol = vol;
	}

	public String getGrossVol() {
		return grsVol;
	}

	public void setCntrSize(String sz) {
		cntrSz = sz;
	}

	public String getCntrSize() {
		return cntrSz;
	}

	public void setCntrNbr(String cntrNo) {
		cntrNbr = cntrNo;
	}

	public String getCntrNbr() {
		return cntrNbr;
	}

	public void setConsigneeNM(String consigneeNM) {
		consNm = consigneeNM;
	}

	public String getConsigneeNM() {
		return consNm;
	}

	public void setBillNbr(String blNo) {
		blNbr = blNo;
	}

	public String getBillNbr() {
		return blNbr;
	}

	public void setBillableParty(String blParty) {
		billParty = blParty;
	}

	public String getBillableParty() {
		return billParty;
	}

	public void setUnStuffCloseStatus(String closestatus) {
		unstuffclosestatus = closestatus;
	}

	public String getUnStuffCloseStatus() {
		return unstuffclosestatus;
	}

	public String getConsigneeCoyCode() {
		return consigneeCoyCode;
	}

	public void setConsigneeCoyCode(String consigneeCoyCode) {
		this.consigneeCoyCode = consigneeCoyCode;
	}

	/**
	 * @return the hsCodeFr
	 */
	public String getHsCodeFr() {
		return hsCodeFr;
	}

	/**
	 * @param hsCodeFr the hsCodeFr to set
	 */
	public void setHsCodeFr(String hsCodeFr) {
		this.hsCodeFr = hsCodeFr;
	}

	/**
	 * @return the hsCodeTo
	 */
	public String getHsCodeTo() {
		return hsCodeTo;
	}

	/**
	 * @param hsCodeTo the hsCodeTo to set
	 */
	public void setHsCodeTo(String hsCodeTo) {
		this.hsCodeTo = hsCodeTo;
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
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

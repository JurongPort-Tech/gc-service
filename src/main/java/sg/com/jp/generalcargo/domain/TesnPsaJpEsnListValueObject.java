package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TesnPsaJpEsnListValueObject implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String vslName;
	String bookingRefNo;
	String shipperName;
	String billPartyName = "";
	String custId;
	String voyNo;
	long esnNbr;
	String crgType;
	String hsCode;
	String crgDesc;
	String crgMarking;
	String pkgType;
	String pkgDesc;
	int noofPkgs;
	int bnoofPkgs;
	int noOfDays;
	double grWt;
	double bgrWt;
	double varGrWt;
	double varGrVol;
	double varNoofPkgs;
	double grVol;
	double bgrVol;
	String crgStatus;
	String dgInd;
	String opInd;
	String stgInd;
	String portL;
	String portD;
	String portFD;
	String consignee;
	String cntrType;
	String cntrSize;
	String clsShpInd;
	int uaNofPkgs;
	int noOfCntr;
	String accNo;
	String payMode;
	String firstCName = "";
	String invoyageNo = "";
	String truckerContactNo = "";
	String dutiGI = "";
	String varNbr = "";
	String portDesc = "";
	String cntr1;
	String cntr2;
	String cntr3;
	String cntr4;
	String cc_cd;
	String cc_name;
	String cicos_cd;
	String stuffind;
	//add by Zhenguo Deng on 14/02/2011 for Cargo Category
	String category;
	String categoryValue;
	//end add

	String hsSubCodeFr;
	String hsSubCodeTo;
	String hsSubCodeDesc;

	//add by VietNguyen on 03/01/2014 for DPE
	private String loadVsl;
	private String loadInVoy;
	private String loadOutVoy;
	private String truckerNm;
	private String lastModifyDttm;
	private String createdBy;
	private String scheme;
	//end add
	private String deliveryToEPC; //MCC for EPC IND
	private String subScheme;
	private String gcOperations;
	private String terminal;

	// start ftz - NS July 2024
	private String customHsCode;
	// end ftz - NS July 2024

	public TesnPsaJpEsnListValueObject() {
	}

	public String getDeliveryToEPC() {
		return deliveryToEPC;
	}

	public void setDeliveryToEPC(String deliveryToEPC) {
		this.deliveryToEPC = deliveryToEPC;
	}

	public void setAccNo(String s) {
		accNo = s;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setVslName(String s) {
		vslName = s;
	}

	public String getVslName() {
		return vslName;
	}

	public void setVoyNo(String s) {
		voyNo = s;
	}

	public String getVoyNo() {
		return voyNo;
	}

	public void setVarNbr(String s) {
		varNbr = s;
	}

	public String getVarNbr() {
		return varNbr;
	}

	public void setClsShpInd(String s) {
		clsShpInd = s;
	}

	public String getClsShpInd() {
		return clsShpInd;
	}

	public void setPayMode(String s) {
		payMode = s;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setBillPartyName(String s) {
		billPartyName = s;
	}

	public String getBillPartyName() {
		return billPartyName;
	}

	public void setBookingRefNo(String s) {
		bookingRefNo = s;
	}

	public String getBookingRefNo() {
		return bookingRefNo;
	}

	public void setShipperName(String s) {
		shipperName = s;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setCustId(String s) {
		custId = s;
	}

	public String getCustId() {
		return custId;
	}

	public void setFirstCName(String s) {
		firstCName = s;
	}

	public String getFirstCName() {
		return firstCName;
	}

	public void SetTruckerCNo(String s) {
		truckerContactNo = s;
	}

	public String getTruckerCNo() {
		return truckerContactNo;
	}

	public void setInvoyageNo(String s) {
		invoyageNo = s;
	}

	public String getInvoyageNo() {
		return invoyageNo;
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

	public void setPkgDesc(String s) {
		pkgDesc = s;
	}

	public String getPkgDesc() {
		return pkgDesc;
	}

	public void setNoofPkgs(int s) {
		noofPkgs = s;
	}

	public int getNoofPkgs() {
		return bnoofPkgs;
	}

	public void setBNoofPkgs(int s) {
		bnoofPkgs = s;
	}

	public int getBNoofPkgs() {
		return bnoofPkgs;
	}

	public void setUaNoofPkgs(int s) {
		uaNofPkgs = s;
	}

	public int getUaNoofPkgs() {
		return uaNofPkgs;
	}

	public void setNoOfCntr(int s) {
		noOfCntr = s;
	}

	public int getNoOfCntr() {
		return noOfCntr;
	}

	public void setNoOfdays(int s) {
		noOfDays = s;
	}

	public int getNoOfdays() {
		return noOfDays;
	}

	public void setGrWt(double s) {
		grWt = s;
	}

	public double getGrWt() {
		return grWt;
	}

	public void setBGrWt(double s) {
		bgrWt = s;
	}

	public double getBGrWt() {
		return bgrWt;
	}

	public void setGrVolume(double s) {
		grVol = s;
	}

	public double getGrVolume() {
		return grVol;
	}

	public void setBGrVolume(double s) {
		bgrVol = s;
	}

	public double getBGrVolume() {
		return bgrVol;
	}

	public void setVarGrVolume(double s) {
		varGrVol = s;
	}

	public double getVarGrVolume() {
		return varGrVol;
	}

	public void setVarGrWt(double s) {
		varGrWt = s;
	}

	public double getVarGrWt() {
		return varGrWt;
	}

	public void setVarNoofPakgs(double s) {
		varNoofPkgs = s;
	}

	public double getVarNoofPakgs() {
		return varNoofPkgs;
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

	public void setStgInd(String s) {
		stgInd = s;
	}

	public String getStgInd() {
		return stgInd;
	}

	public void setPortD(String s) {
		portD = s;
	}

	public String getPortD() {
		return portD;
	}

	public void setPortDesc(String s) {
		portDesc = s;
	}

	public String getPortDesc() {
		return portDesc;
	}

	public void setPortL(String s) {
		portL = s;
	}

	public String getPortL() {
		return portL;
	}

	public void setPortFD(String s) {
		portFD = s;
	}

	public String getPortFD() {
		return portFD;
	}

	public void setConsignee(String s) {
		consignee = s;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setCntrType(String s) {
		cntrType = s;
	}

	public String getCntrType() {
		return cntrType;
	}

	public void setCntrSize(String s) {
		cntrSize = s;
	}

	public String getCntrSize() {
		return cntrSize;
	}

	public void setDutiGI(String s) {
		dutiGI = s;
	}

	public String getDutiGI() {
		return dutiGI;
	}

	public void setEsnNbr(long s) {
		esnNbr = s;
	}

	public long getEsnNbr() {
		return esnNbr;
	}

	public void setCntr1(String s) {
		cntr1 = s;
	}

	public String getCntr1() {
		return cntr1;
	}

	public void setCntr2(String s) {
		cntr2 = s;
	}

	public String getCntr2() {
		return cntr2;
	}

	public void setCntr3(String s) {
		cntr3 = s;
	}

	public String getCntr3() {
		return cntr3;
	}

	public void setCntr4(String s) {
		cntr4 = s;
	}

	public String getCntr4() {
		return cntr4;
	}

	public void setCc_cd(String s) {
		cc_cd = s;
	}

	public String getCc_cd() {
		return cc_cd;
	}

	public void setCc_name(String s) {
		cc_name = s;
	}

	public String getCc_name() {
		return cc_name;
	}

	public void setCicos_cd(String s) {
		cicos_cd = s;
	}

	public String getCicos_cd() {
		return cicos_cd;
	}

	public void setStuffingIndicator(String s) {
		stuffind = s;
	}

	public String getStuffingIndicator() {
		return stuffind;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryValue() {
		return categoryValue;
	}

	public void setCategoryValue(String categoryValue) {
		this.categoryValue = categoryValue;
	}

	public String getHsSubCodeFr() {
		return hsSubCodeFr;
	}

	public void setHsSubCodeFr(String hsSubCodeFr) {
		this.hsSubCodeFr = hsSubCodeFr;
	}

	public String getHsSubCodeTo() {
		return hsSubCodeTo;
	}

	public void setHsSubCodeTo(String hsSubCodeTo) {
		this.hsSubCodeTo = hsSubCodeTo;
	}

	public String getHsSubCodeDesc() {
		return hsSubCodeDesc;
	}

	public void setHsSubCodeDesc(String hsSubCodeDesc) {
		this.hsSubCodeDesc = hsSubCodeDesc;
	}

	public String getLoadVsl() {
		return loadVsl;
	}

	public void setLoadVsl(String loadVsl) {
		this.loadVsl = loadVsl;
	}

	public String getLoadInVoy() {
		return loadInVoy;
	}

	public void setLoadInVoy(String loadInVoy) {
		this.loadInVoy = loadInVoy;
	}

	public String getLoadOutVoy() {
		return loadOutVoy;
	}

	public void setLoadOutVoy(String loadOutVoy) {
		this.loadOutVoy = loadOutVoy;
	}

	public String getTruckerNm() {
		return truckerNm;
	}

	public void setTruckerNm(String truckerNm) {
		this.truckerNm = truckerNm;
	}

	public String getLastModifyDttm() {
		return lastModifyDttm;
	}

	public void setLastModifyDttm(String lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
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

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

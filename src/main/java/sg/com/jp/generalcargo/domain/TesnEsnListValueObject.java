package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TesnEsnListValueObject implements TopsIObject
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TesnEsnListValueObject()
    {
        billPartyName = "";
        truckerNo = "";
        truckerName = "";
        truckerContactNo = "";
        dutiGI = "";
        varNbr = "";
        portDesc = "";
        stfInd = "";
        // Added by Ding Xijia(harbortek) 24-Jan-2011 : START
        companyName = "";
        // Added by Ding Xijia(harbortek) 24-Jan-2011 : END

        // HaiTTH1 added on 10/1/2014
        scheme = "";
        trucker_nbr_pkg = "";
        deliveryToEPC = ""; //MCC for EPC_IND
        // BEGIN added by Maksym JCMS Smart CR 6.5
        ibcInd = "";
        // END added by Maksym JCMS Smart CR 6.5
        subScheme = "";
        gcOperations = "";
        terminal = "";
    }

    public void setAccNo(String s)
    {
        accNo = s;
    }

    public String getAccNo()
    {
        return accNo;
    }

    public void setVslName(String s)
    {
        vslName = s;
    }

    public String getVslName()
    {
        return vslName;
    }

    public void setVoyNo(String s)
    {
        voyNo = s;
    }

    public String getVoyNo()
    {
        return voyNo;
    }

    public void setVarNbr(String s)
    {
        varNbr = s;
    }

    public String getVarNbr()
    {
        return varNbr;
    }

    public void setClsShpInd(String s)
    {
        clsShpInd = s;
    }

    public String getClsShpInd()
    {
        return clsShpInd;
    }

    public void setPayMode(String s)
    {
        payMode = s;
    }

    public String getPayMode()
    {
        return payMode;
    }

    public void setBillPartyName(String s)
    {
        billPartyName = s;
    }

    public String getBillPartyName()
    {
        return billPartyName;
    }

    public void setBookingRefNo(String s)
    {
        bookingRefNo = s;
    }

    public String getBookingRefNo()
    {
        return bookingRefNo;
    }

    public void setShipperName(String s)
    {
        shipperName = s;
    }

    public String getShipperName()
    {
        return shipperName;
    }

    public void setCustId(String s)
    {
        custId = s;
    }

    public String getCustId()
    {
        return custId;
    }

    public void setTruckerNo(String s)
    {
        truckerNo = s;
    }

    public String getTruckerNo()
    {
        return truckerNo;
    }

    public void SetTruckerCNo(String s)
    {
        truckerContactNo = s;
    }

    public String getTruckerCNo()
    {
        return truckerContactNo;
    }

    public void setTruckerName(String s)
    {
        truckerName = s;
    }

    public String getTruckerName()
    {
        return truckerName;
    }

    public void setCrgType(String s)
    {
        crgType = s;
    }

    public String getCrgType()
    {
        return crgType;
    }

    public void setHsCode(String s)
    {
        hsCode = s;
    }

    public String getHsCode()
    {
        return hsCode;
    }

    public void setCrgDesc(String s)
    {
        crgDesc = s;
    }

    public String getCrgDesc()
    {
        return crgDesc;
    }

    public void setCrgMarking(String s)
    {
        crgMarking = s;
    }

    public String getCrgMarking()
    {
        return crgMarking;
    }

    public void setPkgType(String s)
    {
        pkgType = s;
    }

    public String getPkgType()
    {
        return pkgType;
    }

    public void setPkgDesc(String s)
    {
        pkgDesc = s;
    }

    public String getPkgDesc()
    {
        return pkgDesc;
    }

    public void setNoofPkgs(int i)
    {
        noofPkgs = i;
    }

    public int getNoofPkgs()
    {
        return noofPkgs;
    }

    public void setBNoofPkgs(int i)
    {
        bnoofPkgs = i;
    }

    public int getBNoofPkgs()
    {
        return bnoofPkgs;
    }

    public void setUaNoofPkgs(int i)
    {
        uaNofPkgs = i;
    }

    public int getUaNoofPkgs()
    {
        return uaNofPkgs;
    }

    public void setNoOfCntr(int i)
    {
        noOfCntr = i;
    }

    public int getNoOfCntr()
    {
        return noOfCntr;
    }

    public void setNoOfdays(int i)
    {
        noOfDays = i;
    }

    public int getNoOfdays()
    {
        return noOfDays;
    }

    public void setGrWt(double d)
    {
        grWt = d;
    }

    public double getGrWt()
    {
        return grWt;
    }

    public void setBGrWt(double d)
    {
        bgrWt = d;
    }

    public double getBGrWt()
    {
        return bgrWt;
    }

    public void setGrVolume(double d)
    {
        grVol = d;
    }

    public double getGrVolume()
    {
        return grVol;
    }

    public void setBGrVolume(double d)
    {
        bgrVol = d;
    }

    public double getBGrVolume()
    {
        return bgrVol;
    }

    public void setVarGrVolume(double d)
    {
        varGrVol = d;
    }

    public double getVarGrVolume()
    {
        return varGrVol;
    }

    public void setVarGrWt(double d)
    {
        varGrWt = d;
    }

    public double getVarGrWt()
    {
        return varGrWt;
    }

    public void setVarNoofPakgs(double d)
    {
        varNoofPkgs = d;
    }

    public double getVarNoofPakgs()
    {
        return varNoofPkgs;
    }

    public void setCrgStatus(String s)
    {
        crgStatus = s;
    }

    public String getCrgStatus()
    {
        return crgStatus;
    }

    public void setDgInd(String s)
    {
        dgInd = s;
    }

    public String getDgInd()
    {
        return dgInd;
    }

    public void setOpInd(String s)
    {
        opInd = s;
    }

    public String getOpInd()
    {
        return opInd;
    }

    public void setStgInd(String s)
    {
        stgInd = s;
    }

    public String getStgInd()
    {
        return stgInd;
    }

    public void setPortD(String s)
    {
        portD = s;
    }

    public String getPortD()
    {
        return portD;
    }

    public void setPortDesc(String s)
    {
        portDesc = s;
    }

    public String getPortDesc()
    {
        return portDesc;
    }

    public void setPortL(String s)
    {
        portL = s;
    }

    public String getPortL()
    {
        return portL;
    }

    public void setPortFD(String s)
    {
        portFD = s;
    }

    public String getPortFD()
    {
        return portFD;
    }

    public void setConsignee(String s)
    {
        consignee = s;
    }

    public String getConsignee()
    {
        return consignee;
    }

    public void setCntrType(String s)
    {
        cntrType = s;
    }

    public String getCntrType()
    {
        return cntrType;
    }

    public void setCntrSize(String s)
    {
        cntrSize = s;
    }

    public String getCntrSize()
    {
        return cntrSize;
    }

    public void setDutiGI(String s)
    {
        dutiGI = s;
    }

    public String getDutiGI()
    {
        return dutiGI;
    }

    public void setEsnNbr(long l)
    {
        esnNbr = l;
    }

    public long getEsnNbr()
    {
        return esnNbr;
    }

    public void setCntr1(String s)
    {
        cntr1 = s;
    }

    public String getCntr1()
    {
        return cntr1;
    }

    public void setCntr2(String s)
    {
        cntr2 = s;
    }

    public String getCntr2()
    {
        return cntr2;
    }

    public void setCntr3(String s)
    {
        cntr3 = s;
    }

    public String getCntr3()
    {
        return cntr3;
    }

    public void setCntr4(String s)
    {
        cntr4 = s;
    }

    public String getCntr4()
    {
        return cntr4;
    }

    public void setCc_cd(String s)
    {
        cc_cd = s;
    }

    public String getCc_cd()
    {
        return cc_cd;
    }

    public void setCc_name(String s)
    {
        cc_name = s;
    }

    public String getCc_name()
    {
        return cc_name;
    }

    public void setCicos_cd(String s)
    {
        cicos_cd = s;
    }

    public String getCicos_cd()
    {
        return cicos_cd;
    }

    public void setTranstype(String s)
    {
        transtype = s;
    }

    public String getTranstype()
    {
        return transtype;
    }

    public void setInvoyvarnbr(String s)
    {
        invoyvarnbr = s;
    }

    public String getInvoyvarnbr()
    {
        return invoyvarnbr;
    }

    public void setEsnstatus(String s)
    {
        esnstatus = s;
    }

    public String getEsnstatus()
    {
        return esnstatus;
    }

    public void setDeclarantcd(String s)
    {
        declarantcd = s;
    }

    public String getDeclarantcd()
    {
        return declarantcd;
    }

    public void setCrgcatcd(String s)
    {
        crgcatcd = s;
    }

    public String getCrgcatcd()
    {
        return crgcatcd;
    }

    public void setWhind(String s)
    {
        whind = s;
    }

    public String getWhind()
    {
        return whind;
    }

    public void setFsdays(String s)
    {
        fsdays = s;
    }

    public String getFsdays()
    {
        return fsdays;
    }

    public void setWhrem(String s)
    {
        whrem = s;
    }

    public String getWhrem()
    {
        return whrem;
    }

    public void setWhaggrnbr(String s)
    {
        whaggrnbr = s;
    }

    public String getWhaggrnbr()
    {
        return whaggrnbr;
    }

    public void setInvoyageNo(String s)
    {
        invoyageNo = s;
    }

    public String getInvoyageNo()
    {
        return invoyageNo;
    }

    public void setFirstCName(String s)
    {
        firstCName = s;
    }
    public String getFirstCName()
    {
        return firstCName;
    }
	//added by vani to store stuff_indicator
    public String getStfInd()
    {
        return stfInd;
    }

    public void setStfInd(String s)
    {
        stfInd = s;
    }
	//added by vani to store stuff_indicator

    String vslName;
    String bookingRefNo;
    String shipperName;
    String billPartyName;
    String service_other_billPartyName;
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
    String service_other_accNo;
    String payMode;
    String truckerNo;
    String truckerName;
    String truckerContactNo;
    String dutiGI;
    String varNbr;
    String portDesc;
    String cntr1;
    String cntr2;
    String cntr3;
    String cntr4;
    String cc_cd;
    String cc_name;
    String cicos_cd;
    String transtype;
    String invoyvarnbr;
    String esnstatus;
    String declarantcd;
    String crgcatcd;
    String whind;
    String fsdays;
    String whrem;
    String whaggrnbr;
    String invoyageNo;
    String firstCName;
    String stfInd;
    String category;
    String companyName;

    String hsSubCodeFr;
    String hsSubCodeTo;
    String hsSubCodeDesc;
    // HaiTTH1 added on 10/1/2014
    String scheme;
    String trucker_nbr_pkg;
    String truckerIcNo;
    String truckerCoCd;
    //MCC for EPC_IND
    String deliveryToEPC;
    // BEGIN added by Maksym JCMS Smart CR 6.5
    String ibcInd;
    // END added by Maksym JCMS Smart CR 6.5

    String cntrNbr;
    String miscAppNo;
    
    String subScheme;
    String gcOperations;
    String terminal;

	public String getCntrNbr() {
        return cntrNbr;
    }

    public void setCntrNbr(String cntrNbr) {
        this.cntrNbr = cntrNbr;
    }

    public String getMiscAppNo() {
        return miscAppNo;
    }

    public void setMiscAppNo(String miscAppNo) {
        this.miscAppNo = miscAppNo;
    }

    public String getDeliveryToEPC() {
        return deliveryToEPC;
    }

    public void setDeliveryToEPC(String deliveryToEPC) {
        this.deliveryToEPC = deliveryToEPC;
    }

	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

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

	/**
	 * @return the trucker_nbr_pkg
	 */
	public String getTrucker_nbr_pkg() {
		return trucker_nbr_pkg;
	}

	/**
	 * @param trucker_nbr_pkg the trucker_nbr_pkg to set
	 */
	public void setTrucker_nbr_pkg(String trucker_nbr_pkg) {
		this.trucker_nbr_pkg = trucker_nbr_pkg;
	}

    public String getTruckerIcNo() {
        return truckerIcNo;
    }

    public void setTruckerIcNo(String truckerIcNo) {
        this.truckerIcNo = truckerIcNo;
    }

    public String getTruckerCoCd() {
        return truckerCoCd;
    }

    public void setTruckerCoCd(String truckerCoCd) {
        this.truckerCoCd = truckerCoCd;
    }

	public String getService_other_accNo() {
        return service_other_accNo;
    }

    public void setService_other_accNo(String serviceOtherAccNo) {
        service_other_accNo = serviceOtherAccNo;
    }

    public String getService_other_billPartyName() {
        return service_other_billPartyName;
    }

    public void setService_other_billPartyName(String serviceOtherBillPartyName) {
        service_other_billPartyName = serviceOtherBillPartyName;
    }

    public String getIbcInd() {
		return "Y".equalsIgnoreCase(ibcInd) ? "Yes": "No";
	}

	public void setIbcInd(String ibcInd) {
		this.ibcInd = ibcInd;
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
	
	   @Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return "";
			}
		}

}

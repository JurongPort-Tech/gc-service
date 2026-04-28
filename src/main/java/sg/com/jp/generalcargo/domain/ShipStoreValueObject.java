package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShipStoreValueObject implements TopsIObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String terminal;
	String scheme;
	String subScheme;
	String gcOperations;
	String vslName;
        String bookingRefNo;
        String shipperName;
        String billPartyName ="";
        String custId;
	String voyNo;
	long ShpStrNbr;
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
        String truckerNo = "";
        String truckerName = "";
        String truckerContactNo = "";
        String dutiGI="";
        String varNbr ="";
        String portDesc = "";
	String cntr1;
	String cntr2;
	String cntr3;
	String cntr4;

	  String adminFeeInd="";
      String reasonForWaive="";


	public ShipStoreValueObject()
	{
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
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
	public void setNoofPkgs(int s)
	{
		noofPkgs = s;
	}
	public int getNoofPkgs()
	{
		return bnoofPkgs;
	}
	public void setBNoofPkgs(int s)
	{
		bnoofPkgs = s;
	}
	public int getBNoofPkgs()
	{
		return bnoofPkgs;
	}
	public void setUaNoofPkgs(int s)
	{
		uaNofPkgs = s;
	}
	public int getUaNoofPkgs()
	{
		return uaNofPkgs;
	}
	public void setNoOfCntr(int s)
	{
		noOfCntr = s;
	}
	public int getNoOfCntr()
	{
		return noOfCntr;
	}
	public void setNoOfdays(int s)
	{
		noOfDays = s;
	}

	public int getNoOfdays()
	{
		return noOfDays;
	}
	public void setGrWt(double s)
	{
		grWt = s;
	}

	public double getGrWt()
	{
		return grWt;
	}
	public void setBGrWt(double s)
	{
		bgrWt = s;
	}

	public double getBGrWt()
	{
		return bgrWt;
	}
	public void setGrVolume(double s)
	{
		grVol = s;
	}

	public double getGrVolume()
	{
		return grVol;
	}
	public void setBGrVolume(double s)
	{
		bgrVol = s;
	}

	public double getBGrVolume()
	{
		return bgrVol;
	}
	public void setVarGrVolume(double s)
	{
		varGrVol = s;
	}

	public double getVarGrVolume()
	{
		return varGrVol;
	}
	public void setVarGrWt(double s)
	{
		varGrWt = s;
	}

	public double getVarGrWt()
	{
		return varGrWt;
	}
	public void setVarNoofPakgs(double s)
	{
		varNoofPkgs = s;
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
	public void setShpStrNbr(long s)
	{
		ShpStrNbr = s;
	}

	public long getShpStrNbr()
	{
		return ShpStrNbr;
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

    public String getAdminFeeInd() {
        return adminFeeInd;
    }

    public void setAdminFeeInd(String adminFeeInd) {
        this.adminFeeInd = adminFeeInd;
    }

    public String getReasonForWaive() {
        return reasonForWaive;
    }

    public void setReasonForWaive(String reasonForWaive) {
        this.reasonForWaive = reasonForWaive;
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

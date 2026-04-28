package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 *            System Name : GBMS (General and Bulk Cargo Management Systems)
 *            Module : cargo - edo Component ID : EdoValueObject.java Component
 *            Description:
 *
 * @author
 * @version
 */

/*
 * Revision History ---------------- Author Request Number Description of Change
 * Version Date Released Creation 1.0 Vani Changed to add Stuff Indicator 1.3 03
 * Sept 2003 Zhengguo Deng add shutout cargo edo 08 June 2011 MCConsulting
 * Include EPC_IND deliveryTo EPC 1.5 15 Nov 2014 field in EDO for WWL logon MC
 * Consulting 23 JAN 2015, Added Billing accounts each for Wharfage and
 * Service/Others charges.
 */

public class EdoValueObjectOps implements Serializable

{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EdoValueObjectOps()
    {
    	terminal = "";
    	scheme = "";
    	subScheme = "";
    	gcOperations = "";
        trans = "";
        cntrNo = "";
        cntrSize = "";
        cntrType = "";
        edoAsnNbr = "";
        transType = "";
        vec1 = "";
        vec2 = "";
        vec3 = "";
        vec4 = "";
        vec5 = "";
        crgDestination = "";
    }

    public String getBlNbr()
    {
        return blNbr;
    }

    public void setBlNbr(String BlNbr)
    {
        blNbr = BlNbr;
    }

    public String getAcctNo()
    {
        return accNbr;
    }

    public void setAcctNo(String AccNbr)
    {
        accNbr = AccNbr;
    }

    public String getPayMode()
    {
        return payNbr;
    }

    public void setPayMode(String PayNbr)
    {
        payNbr = PayNbr;
    }

    public String getNomWt()
    {
        return nomWt;
    }

    public void setNomWt(String NomWt)
    {
        nomWt = NomWt;
    }

    public String getNomVol()
    {
        return nomVol;
    }

    public void setNomVol(String NomVol)
    {
        nomVol = NomVol;
    }

    public String getAdpcustcd()
    {
        return adpcustcd;
    }

    public void setAdpcustcd(String Adpcustcd)
    {
        adpcustcd = Adpcustcd;
    }

    public String getAdpIcNbr()
    {
        return adpIcNbr;
    }

    public void setAdpIcNbr(String AdpIcNbr)
    {
        adpIcNbr = AdpIcNbr;
    }

    public String getAdpNm()
    {
        return adpNm;
    }

    public void setAdpNm(String AdpNm)
    {
        adpNm = AdpNm;
    }

    public String getCaCustcd()
    {
        return caCustcd;
    }

    public void setCaCustcd(String CaCustcd)
    {
        caCustcd = CaCustcd;
    }

    public String getCaIcNbr()
    {
        return caIcNbr;
    }

    public void setCaIcNbr(String CaIcNbr)
    {
        caIcNbr = CaIcNbr;
    }

    public String getCaName()
    {
        return caNm;
    }

    public void setCaName(String CaNm)
    {
        caNm = CaNm;
    }

    public void setAAName(String AaNm)
    {
        aaNm = AaNm;
    }

    public String getAAName()
    {
        return aaNm;
    }

    public String getATB()
    {
        return atb;
    }

    public void setATB(String Atb)
    {
        atb = Atb;
    }

    public void setCOD(String Cod)
    {
        cod = Cod;
    }

    public String getCOD()
    {
        return cod;
    }

    public void setCrgDes(String CrgDes)
    {
        crgDes = CrgDes;
    }

    public String getCrgDes()
    {
        return crgDes;
    }

    public void setDeclPkgs(String DeclPkgs)
    {
        declPkgs = DeclPkgs;
    }

    public String getDeclPkgs()
    {
        return declPkgs;
    }

    public void setBalance(String Balance)
    {
        balance = Balance;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setDeliveredPkgs(String DnPkgs)
    {
        dnPkgs = DnPkgs;
    }

    public String getDeliveredPkgs()
    {
        return dnPkgs;
    }

    public void setCrgStatus(String CrgStatus)
    {
        crgStatus = CrgStatus;
    }

    public String getCrgStatus()
    {
        return crgStatus;
    }

    public void setMarkings(String Mftmarkings)
    {
        mftmarkings = Mftmarkings;
    }

    public String getMarkings()
    {
        return mftmarkings;
    }

    public void setInVoyNbr(String InvoyNbr)
    {
        invoyNbr = InvoyNbr;
    }

    public String getInVoyNbr()
    {
        return invoyNbr;
    }

    public void setOutVoyNbr(String OutvoyNbr)
    {
        outvoyNbr = OutvoyNbr;
    }

    public String getOutVoyNbr()
    {
        return outvoyNbr;
    }

    public void setAACustCD(String AaCustCd)
    {
        aaCustCd = AaCustCd;
    }

    public String getAACustCD()
    {
        return aaCustCd;
    }

    public void setAAIcNbr(String AaIcNbr)
    {
        aaIcNbr = AaIcNbr;
    }

    public String getAAIcNbr()
    {
        return aaIcNbr;
    }

    public void setVslName(String VslName)
    {
        vslName = VslName;
    }

    public String getVslName()
    {
        return vslName;
    }

    public void setConsName(String consName)
    {
        ConsName = consName;
    }

    public String getConsName()
    {
        return ConsName;
    }

    public void setBillParty(String billparty)
    {
        BillParty = billparty;
    }

    public String getBillParty()
    {
        return BillParty;
    }

    public String getDnNbr()
    {
        return dnNbr;
    }

    public void setDnNbr(String s)
    {
        dnNbr = s;
    }

    public String getNoPkgs()
    {
        return noPkgs;
    }

    public void setNoPkgs(String s)
    {
        noPkgs = s;
    }

    public String getBillTon()
    {
        return billTon;
    }

    public void setBillTon(String s)
    {
        billTon = s;
    }

    public String getDnStatus()
    {
        return dnStatus;
    }

    public void setDnStatus(String s)
    {
        dnStatus = s;
    }

    public String getBillStatus()
    {
        return billStatus;
    }

    public void setBillStatus(String s)
    {
        billStatus = s;
    }

    public String getTransDate()
    {
        return trans;
    }

    public void setTransDate(String s)
    {
        trans = s;
    }

    public String getCntrNo()
    {
        return cntrNo;
    }

    public void setCntrNo(String s)
    {
        cntrNo = s;
    }

    public String getCntrType()
    {
        return cntrType;
    }

    public void setCntrType(String s)
    {
        cntrType = s;
    }

    public String getCntrSize()
    {
        return cntrSize;
    }

    public void setCntrSize(String s)
    {
        cntrSize = s;
    }

    public String getTransType()
    {
        return transType;
    }

    public void setTransType(String s)
    {
        transType = s;
    }

    public String getEdoAsnNbr()
    {
        return edoAsnNbr;
    }

    public void setEdoAsnNbr(String s)
    {
        edoAsnNbr = s;
    }

    public String getVech1()
    {
        return vec1;
    }

    public void setVech1(String s)
    {
        vec1 = s;
    }

    public String getVech2()
    {
        return vec2;
    }

    public void setVech2(String s)
    {
        vec2 = s;
    }

    public String getVech3()
    {
        return vec3;
    }

    public void setVech3(String s)
    {
        vec3 = s;
    }

    public String getVech4()
    {
        return vec4;
    }

    public void setVech4(String s)
    {
        vec4 = s;
    }

    public String getVech5()
    {
        return vec5;
    }

    public void setVech5(String s)
    {
        vec5 = s;
    }

    //vinayak added on 7 jan 2004
    public boolean getChkEdoIndRecStatus()
	{
	    return bolStatus;
	}
	public void setChkEdoIndRecStatus(boolean des)
	{
	    bolStatus = des;
    }
	
	//Added by Punitha
	 public void setWhInd(String WhInd)
	    {
	        whInd = WhInd;
	    }

	    public String getWhInd()
	    {
	        return whInd;
	    }
	    public void setWhAggrNbr(String WhAggrNbr)
	    {
	    	whAggrNbr = WhAggrNbr;
	    }

	    public String getWhAggrNbr()
	    {
	        return whAggrNbr;
	    }
	    public void setWhRemarks(String WhRemarks)
	    {
	        whRemarks = WhRemarks;
	    }

	    public String getWhRemarks()
	    {
	        return whRemarks;
	    }

	private String terminal;
	private String scheme;
	private String subScheme;
	private String gcOperations;
    private String blNbr;
    private String accNbr;
    private String payNbr;
    private String nomWt;
    private String nomVol;
    private String adpcustcd;
    private String adpIcNbr;
    private String adpNm;
    private String caCustcd;
    private String caIcNbr;
    private String caNm;
    private String aaNm;
    private String aaCustCd;
    private String aaIcNbr;
    private String vslName;
    private String atb;
    private String cod;
    private String crgDes;
    private String declPkgs;
    private String balance;
    private String dnPkgs;
    private String crgStatus;
    private String mftmarkings;
    private String invoyNbr;
    private String outvoyNbr;
    private String ConsName;
    private String BillParty;
    private boolean bolStatus;
    String dnNbr;
    String noPkgs;
    String billTon;
    String dnStatus;
    String trans;
    String billStatus;
    String cntrNo;
    String cntrSize;
    String cntrType;
    String edoAsnNbr;
    String transType;
    String vec1;
    String vec2;
    String vec3;
    String vec4;
    String vec5;
    //Warehouse
    private String whInd;
    private String whAggrNbr;
    private String whRemarks;
    
    //++ vietnd02
    String crgDestination;
    //--

	public String getCrgDestination() {
		return crgDestination;
	}

	public void setCrgDestination(String crgDestination) {
		this.crgDestination = crgDestination;
	}
	//Added By HuJianPing on 30 May - begin
	private String mftseqnbr;
	private String cargoType;
	private String edohscode;
	private String esnDgInd;
	private String stgInd;
	private String pkgType;
	private String uaNbrPkgs;
	private String spkgs;
	private String ospkgs;
	private String adpIcTdbcrNbr;
	private String nbrPkgs;
	private String edoDeliveryTo;
	private String freeStgDays;
	private String esnAsnNbr;
	private String esnPkgsWt;
	private String esnPkgsVol;
	private String spkgsWt;
	private String spkgsVol;
	private String ospkgsWt;
	private String ospkgsVol;
	private String crgTypeName;
	private String pkgTypeDesc;
	private String transCrg;
	
	public String getPkgTypeDesc() {
		return pkgTypeDesc;
	}

	public void setPkgTypeDesc(String pkgTypeDesc) {
		this.pkgTypeDesc = pkgTypeDesc;
	}

	public String getCrgTypeName() {
		return crgTypeName;
	}

	public void setCrgTypeName(String crgTypeName) {
		this.crgTypeName = crgTypeName;
	}

	public String getEsnPkgsWt() {
		return esnPkgsWt;
	}

	public void setEsnPkgsWt(String esnPkgsWt) {
		this.esnPkgsWt = esnPkgsWt;
	}

	public String getEsnPkgsVol() {
		return esnPkgsVol;
	}

	public void setEsnPkgsVol(String esnPkgsVol) {
		this.esnPkgsVol = esnPkgsVol;
	}

	public String getSpkgsWt() {
		return spkgsWt;
	}

	public void setSpkgsWt(String spkgsWt) {
		this.spkgsWt = spkgsWt;
	}

	public String getSpkgsVol() {
		return spkgsVol;
	}

	public void setSpkgsVol(String spkgsVol) {
		this.spkgsVol = spkgsVol;
	}

	public String getOspkgsWt() {
		return ospkgsWt;
	}

	public void setOspkgsWt(String ospkgsWt) {
		this.ospkgsWt = ospkgsWt;
	}

	public String getOspkgsVol() {
		return ospkgsVol;
	}

	public void setOspkgsVol(String ospkgsVol) {
		this.ospkgsVol = ospkgsVol;
	}

	public String getEsnAsnNbr() {
		return esnAsnNbr;
	}

	public void setEsnAsnNbr(String esnAsnNbr) {
		this.esnAsnNbr = esnAsnNbr;
	}

	public String getFreeStgDays() {
		return freeStgDays;
	}

	public void setFreeStgDays(String freeStgDays) {
		this.freeStgDays = freeStgDays;
	}

	public String getEdoDeliveryTo() {
		return edoDeliveryTo;
	}

	public void setEdoDeliveryTo(String edoDeliveryTo) {
		this.edoDeliveryTo = edoDeliveryTo;
	}

	public String getMftseqnbr() {
		return mftseqnbr;
	}

	public void setMftseqnbr(String mftseqnbr) {
		this.mftseqnbr = mftseqnbr;
	}
	public String getCargoType() {
		return cargoType;
	}

	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}

	public String getEdohscode() {
		return edohscode;
	}

	public void setEdohscode(String edohscode) {
		this.edohscode = edohscode;
	}

	public String getEsnDgInd() {
		return esnDgInd;
	}

	public void setEsnDgInd(String esnDgInd) {
		this.esnDgInd = esnDgInd;
	}

	public String getStgInd() {
		return stgInd;
	}

	public void setStgInd(String stgInd) {
		this.stgInd = stgInd;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	public String getUaNbrPkgs() {
		return uaNbrPkgs;
	}

	public void setUaNbrPkgs(String uaNbrPkgs) {
		this.uaNbrPkgs = uaNbrPkgs;
	}

	public String getSpkgs() {
		return spkgs;
	}

	public void setSpkgs(String spkgs) {
		this.spkgs = spkgs;
	}

	public String getOspkgs() {
		return ospkgs;
	}

	public void setOspkgs(String ospkgs) {
		this.ospkgs = ospkgs;
	}

	public String getAdpIcTdbcrNbr() {
		return adpIcTdbcrNbr;
	}

	public void setAdpIcTdbcrNbr(String adpIcTdbcrNbr) {
		this.adpIcTdbcrNbr = adpIcTdbcrNbr;
	}

	public String getNbrPkgs() {
		return nbrPkgs;
	}

	public void setNbrPkgs(String nbrPkgs) {
		this.nbrPkgs = nbrPkgs;
	}
	
	//Added by HuJianPing on 30 May - end
	
	private String firstUa;

	public String getFirstUa() {
		return firstUa;
	}

	public void setFirstUa(String firstUa) {
		this.firstUa = firstUa;
	}
	//Added by HoaBT2: get DN_DETAILS.LAST_MODIFY_USER_ID
	String last_modify_user_id;

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	//Added by HoaBT2: get lane_no
	String lane_nbr;

	public String getLane_nbr() {
		return lane_nbr;
	}

	public void setLane_nbr(String lane_nbr) {
		this.lane_nbr = lane_nbr;
	}

//Start added for SMART CR by FPT on 14-Feb-2014
	private String vvCd;

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}
	//End added for SMART CR by FPT on 14-Feb-2014
	public void setTrans_crg(String transcrg) {
		this.transCrg = transcrg;
	}
	public String getTrans_crg(){
		return this.transCrg;
	}
	
	//Begin ThanhPT6 SGS 25 sep 2015
	private String mtf_seq_nbr;
	private String var_nbr;

	public String getMtf_seq_nbr() {
		return mtf_seq_nbr;
	}

	public void setMtf_seq_nbr(String mtf_seq_nbr) {
		this.mtf_seq_nbr = mtf_seq_nbr;
	}

	public String getVar_nbr() {
		return var_nbr;
	}

	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}
	//End ThanhPT6 SGS 25 sep 2015

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
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

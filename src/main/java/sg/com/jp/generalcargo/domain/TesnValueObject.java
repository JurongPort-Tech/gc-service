

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TesnValueObject
    implements TopsIObject
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TesnValueObject()
    {
        tesnSecCar = "";
        tesnSecVoy = "";
        tesnPortDis = "";
        tesnShipper = "";
        tesnNbrPkgs = "";
        tesnNomWt = "";
        tesnNomVol = "";
        portL = "";
        portLn = "";
        edoNbrPkgs = "";
        bkref = "";
        acctNbr = "";
        scheme ="";
        subScheme = "";
        gcOperations = "";
        terminal = "";
        
        
    }

    public void setBkRef(String s)
	{
	        bkref = s;
	}

	public String getBkRef()
	{
	        return bkref;
    }

    public void setAccount(String s)
	{
		   acctNbr = s;
	}

	public String getAccount()
	{
		   return acctNbr;
    }



    public void setEsnAsnNbr(String s)
    {
        esnAsnNbr = s;
    }

    public String getEsnAsnNbr()
    {
        return esnAsnNbr;
    }

    public void setTesnAsnNbr(String s)
    {
        tesnAsnNbr = s;
    }

    public String getTesnAsnNbr()
    {
        return tesnAsnNbr;
    }

    public void setEdoAsnNbr(String s)
    {
        edoAsnNbr = s;
    }

    public String getEdoAsnNbr()
    {
        return edoAsnNbr;
    }

    public void setVarNbr(String s)
    {
        varNbr = s;
    }

    public String getVarNbr()
    {
        return varNbr;
    }

    public void setVslNm(String s)
    {
        vslNm = s;
    }

    public String getVslNm()
    {
        return vslNm;
    }

    public void setInVoyNbr(String s)
    {
        inVoyNbr = s;
    }

    public String getInVoyNbr()
    {
        return inVoyNbr;
    }

    public void setNbrPkgs(String s)
    {
        nbrPkgs = s;
    }

    public String getNbrPkgs()
    {
        return nbrPkgs;
    }

    public void setCrgDes(String s)
    {
        crgDes = s;
    }

    public String getCrgDes()
    {
        return crgDes;
    }

    public void setHsCode(String s)
    {
        hsCode = s;
    }

    public String getHsCode()
    {
        return hsCode;
    }

    public void setPkgType(String s)
    {
        pkgType = s;
    }

    public String getPkgType()
    {
        return pkgType;
    }

    public void setPkgTypeDesc(String s)
    {
        pkgTypeDesc = s;
    }

    public String getPkgTypeDesc()
    {
        return pkgTypeDesc;
    }

    public void setMftMarkings(String s)
    {
        mftMarkings = s;
    }

    public String getMftMarkings()
    {
        return mftMarkings;
    }

    public void setDgInd(String s)
    {
        dgInd = s;
    }

    public String getDgInd()
    {
        return dgInd;
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

    public void setNomWt(String s)
    {
        nomWt = s;
    }

    public String getNomWt()
    {
        return nomWt;
    }

    public void setNomVol(String s)
    {
        nomVol = s;
    }

    public String getNomVol()
    {
        return nomVol;
    }

    public void setCargoType(String s)
    {
        cargoType = s;
    }

    public String getCargoType()
    {
        return cargoType;
    }

    public void setCargoTypeNm(String s)
    {
        cargoTypeNm = s;
    }

    public String getCargoTypeNm()
    {
        return cargoTypeNm;
    }

    public void setCntrNbr1(String s)
    {
        cntrNbr1 = s;
    }

    public String getCntrNbr1()
    {
        return cntrNbr1;
    }

    public void setCntrNbr2(String s)
    {
        cntrNbr2 = s;
    }

    public String getCntrNbr2()
    {
        return cntrNbr2;
    }

    public void setCntrNbr3(String s)
    {
        cntrNbr3 = s;
    }

    public String getCntrNbr3()
    {
        return cntrNbr3;
    }

    public void setCntrNbr4(String s)
    {
        cntrNbr4 = s;
    }

    public String getCntrNbr4()
    {
        return cntrNbr4;
    }

    public void setTesnSecCar(String s)
    {
        tesnSecCar = s;
    }

    public String getTesnSecCar()
    {
        return tesnSecCar;
    }

    public void setTesnSecVoy(String s)
    {
        tesnSecVoy = s;
    }

    public String getTesnSecVoy()
    {
        return tesnSecVoy;
    }

    public void setTesnPortDis(String s)
    {
        tesnPortDis = s;
    }

    public String getTesnPortDis()
    {
        return tesnPortDis;
    }

    public void setTesnShipper(String s)
    {
        tesnShipper = s;
    }

    public String getTesnShipper()
    {
        return tesnShipper;
    }

    public void setTesnNbrPkgs(String s)
    {
        tesnNbrPkgs = s;
    }

    public String getTesnNbrPkgs()
    {
        return tesnNbrPkgs;
    }

    public void setTesnNomWt(String s)
    {
        tesnNomWt = s;
    }

    public String getTesnNomWt()
    {
        return tesnNomWt;
    }

    public void setTesnNomVol(String s)
    {
        tesnNomVol = s;
    }

    public String getTesnNomVol()
    {
        return tesnNomVol;
    }

    public void setPortL(String s)
    {
        portL = s;
    }

    public String getPortL()
    {
        return portL;
    }

    public void setPortLn(String s)
    {
        portLn = s;
    }

    public String getPortLn()
    {
        return portLn;
    }

    public void setInVslNm(String s)
    {
        inVslNm = s;
    }

    public String getInVslNm()
    {
        return inVslNm;
    }

    public void setInVarNbr(String s)
    {
        inVarNbr = s;
    }

    public String getInVarNbr()
    {
        return inVarNbr;
    }

    public void setEdoNbrPkgs(String s)
    {
        edoNbrPkgs = s;
    }

    public String getEdoNbrPkgs()
    {
        return edoNbrPkgs;
    }

    public void setTransDnNbrPkgs(String s)
    {
        transDnNbrPkgs = s;
    }

    public String getTransDnNbrPkgs()
    {
        return transDnNbrPkgs;
    }

    public void setGbCloseBjInd(String s)
    {
        gbCloseBjInd = s;
    }

    public String getGbCloseBjInd()
    {
        return gbCloseBjInd;
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

	public String getTruckerNm() {
		return truckerNm;
	}

	public void setTruckerNm(String truckerNm) {
		this.truckerNm = truckerNm;
	}
	public String getScheme() {
        return scheme;
    }
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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

	String gbCloseBjInd;
    String transDnNbrPkgs;
    String inVslNm;
    String inVarNbr;
    String esnAsnNbr;
    String tesnAsnNbr;
    String edoAsnNbr;
    String varNbr;
    String vslNm;
    String inVoyNbr;
    String nbrPkgs;
    String crgDes;
    String hsCode;
	// START CR FTZ - NS JUNE 2024
    String hsCodeDisp;
	// END CR FTZ - NS JUNE 2024
    String pkgType;
    String pkgTypeDesc;
    String mftMarkings;
    String dgInd;
    String cntrType;
    String cntrSize;
    String nomWt;
    String nomVol;
    String cntrNbr1;
    String cntrNbr2;
    String cntrNbr3;
    String cntrNbr4;
    String cargoType;
    String cargoTypeNm;
    String tesnSecCar;
    String tesnSecVoy;
    String tesnPortDis;
    String tesnShipper;
    String tesnNbrPkgs;
    String tesnNomWt;
    String tesnNomVol;
    String portL;
    String portLn;
    String edoNbrPkgs;
    String bkref;
    String acctNbr;
	//add by Zhenguo Deng on 14/02/2011 for Cargo Category
    String category;
    String categoryValue;

    //VietNguyen (FPT) Document Process Enhancement 07-Jan-2014
    String lastModifyDttm;
    String createdBy;
    String truckerNm;
    String scheme;
    String balance;
    String subScheme;
    String gcOperations;
    String terminal;
    
    // START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
    String edoNomWt;
    String edoNomVol;
    
    public String getEdoNomWt() {
		return edoNomWt;
	}

	public String getEdoNomVol() {
		return edoNomVol;
	}

	public void setEdoNomWt(String edoNomWt) {
		this.edoNomWt = edoNomWt;
	}

	public void setEdoNomVol(String edoNomVol) {
		this.edoNomVol = edoNomVol;
	}
	// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023

	// START CR FTZ - NS JUNE 2024
	public String getHsCodeDisp() {
		return hsCodeDisp;
	}

	public void setHsCodeDisp(String hsCodeDisp) {
		this.hsCodeDisp = hsCodeDisp;
	}
	// END CR FTZ - NS JUNE 2024
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
    
}

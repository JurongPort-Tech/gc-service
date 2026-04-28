package sg.com.jp.generalcargo.domain;

import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * System Name          : TOPs (Terminal Operation and Planning System)
 * Component ID         : JPPSAValueObject.java (CIM - Vessel Service Operator)
 * Component Description: This is the valueObject used for processing the PSA IGD file.
 *
 * @author      Rajesh
 * @version     01 June 2002
 *
 * Change Revision
 * ---------------
 * Author     Request Number  Description of Change   Version     Date Released
 * Rajesh            -        Creation                  1.0       01 June 2002
 */

public class JPPSAValueObject implements TopsIObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ichRecType;
    private String ichCreateDate;
    private String ichAbbVslName;
    private String ichDisAbbVoyNo;
    private String icdRecType;
    private String icdFunction;
    private String icdBillLad;
    private String icdHsCode;
    private String icdPackType;
    private String icdNoPack;
    private String icdWeight;
    private String icdVolume;
    private String icdDGind;
    private String icdShpName;
    private String icdCargoType;
    private String icdLdVessel;
    private String icdLdVoyage;
    private String icdPrtDis;
    private String icdCntrNo;
    private String icdDirIntergateway;
    private String icdBookRef;
    private String icdAcctNbr;
    private String icdTdb;
    private String icdCargoDes;
    private String icdCargoMark;
    private String icsRecordType;
    private String icsTotalRec;
    private String filename;
    private String rDate;
    private String user;
    List<JPPSAValueObject> cDetail;
    
    public JPPSAValueObject() {
        user = "";
        cDetail = new Vector();
    }

    public void setICHRecType(String ichRecType) {
        this.ichRecType = ichRecType;
    }

    public void setICHCreateDate(String ichCreateDate) {
        this.ichCreateDate = ichCreateDate;
    }

    public void setICHAbbVslName(String ichAbbVslName) {
        this.ichAbbVslName = ichAbbVslName;
    }

    public void setICHDisAbbVslName(String ichDisAbbVoyNo) {
        this.ichDisAbbVoyNo = ichDisAbbVoyNo;
    }

    public void setICDRecordType(String ichRecType) {
        this.ichRecType = ichRecType;
    }

    public void setICDFunction(String icdFunction) {
        this.icdFunction = icdFunction;
    }

    public void setICDBillofLading(String icdBillLad) {
        this.icdBillLad = icdBillLad;
    }

    public void setICDHScode(String icdHsCode) {
        this.icdHsCode = icdHsCode;
    }

    public void setICDPackageType(String icdPackType) {
        this.icdPackType = icdPackType;
    }

    public void setICDNoofPackage(String icdNoPack) {
        this.icdNoPack = icdNoPack;
    }

    public void setICDWeight(String icdWeight) {
        this.icdWeight = icdWeight;
    }

    public void setICDVolume(String icdVolume) {
        this.icdVolume = icdVolume;
    }

    public void setICDDGIndicator(String icdDGind) {
        this.icdDGind = icdDGind;
    }

    public void setICDShipperName(String icdShpName) {
        this.icdShpName = icdShpName;
    }

    public void setICDCargoType(String icdCargoType) {
        this.icdCargoType = icdCargoType;
    }

    public void setICDLoadingVessel(String icdLdVessel) {
        this.icdLdVessel = icdLdVessel;
    }

    public void setICDLoadingVoyage(String icdLdVoyage) {
        this.icdLdVoyage = icdLdVoyage;
    }

    public void setICDPortOfDischarge(String icdPrtDis) {
        this.icdPrtDis = icdPrtDis;
    }

    public void setICDContainerNumber(String icdCntrNo) {
        this.icdCntrNo = icdCntrNo;
    }

    public void setICDDirectInterGateWay(String icdDirIntergateway) {
        this.icdDirIntergateway = icdDirIntergateway;
    }

    public void setICDBookingRef(String icdBookRef) {
        this.icdBookRef = icdBookRef;
    }

    public void setICDAccount(String icdAcctNbr) {
        this.icdAcctNbr = icdAcctNbr;
    }

    public void setICDTdbNo(String icdTdb) {
        this.icdTdb = icdTdb;
    }

    public void setICDCargoDescription(String icdCargoDes) {
        this.icdCargoDes = icdCargoDes;
    }

    public void setICDMarking(String icdCargoMark) {
        this.icdCargoMark = icdCargoMark;
    }


    public List<JPPSAValueObject> getCargoDetails() {
        return cDetail;
    }

    public void setICSRecordtype(String icsRecordType) {
        this.icsRecordType = icsRecordType;
    }

    public void setICSTotalRec(String icsTotalRec) {
        this.icsTotalRec = icsTotalRec;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(String rDate) {
        this.rDate = rDate;
    }

    public void setCargoDetails(List<JPPSAValueObject> v) {
        this.cDetail = v;
    }
    
    public String getICHRecType()  {
        return ichRecType;
    }

    public String getICHCreateDate() {
        return ichCreateDate;
    }

    public String getICHAbbVslName() {
        return ichAbbVslName;
    }

    public String getICHDisAbbVslName() {
        return ichDisAbbVoyNo;
    }

    public String getICDRecordType() {
        return ichRecType;
    }

    public String getICDFunction() {
        return icdFunction;
    }

    public String getICDBillofLading() {
        return icdBillLad;
    }

    public String getICDHScode() {
        return icdHsCode;
    }

    public String getICDPackagingType() {
        return icdPackType;
    }

    public String getICDNoofPackage() {
        return icdNoPack;
    }

    public String getICDWeight() {
        return icdWeight;
    }

    public String getICDVolume() {
        return icdVolume;
    }

    public String getICDDGIndicator() {
        return icdDGind;
    }

    public String getICDShipperName() {
        return icdShpName;
    }

    public String getICDCargoType() {
        return icdCargoType;
    }

    public String getICDLoadingVessel() {
        return icdLdVessel;
    }

    public String getICDLoadingVoyage() {
        return icdLdVoyage;
    }

    public String getICDPortOfDischarge() {
        return icdPrtDis;
    }

    public String getICDContainerNumber() {
        return icdCntrNo;
    }

    public String getICDDirectInterGateWay() {
        return icdDirIntergateway;
    }

    public String getICDBookingRef() {
        return icdBookRef;
    }

    public String getICDAccount() {
        return icdAcctNbr;
    }

    public String getICDTdbNo() {
        return icdTdb;
    }

    public String getICDCargoDescription() {
        return icdCargoDes;
    }

    public String getICDMarking() {
        return icdCargoMark;
    }

    public String getICSRecordtype() {
        return icsRecordType;
    }

    public String getICSTotalRec() {
        return icsTotalRec;
    }

    public String getFileName() {
        return filename;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return rDate;
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

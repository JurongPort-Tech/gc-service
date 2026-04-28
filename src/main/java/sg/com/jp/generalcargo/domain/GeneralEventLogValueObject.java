package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class GeneralEventLogValueObject {

	private String blNbr;
	private String edoAsnNbr;
	private String bkRefNbr;
	private String esnAsnNbr;
	private String dnNbr;
	private String uaNbr;
	private String discVvCd;
	private String refInd;
	private String vvInd;
	private String billAcctNbr;
	private String loadVvCd;
	private String discGateway;
	private String businessType;
	private String schemeCd;
	private String tariffMainCatCd;
	private String tariffSubCatCd;
	private String type;
	private String localLeg;
	private String cargoType;
	private String billInd;
	private String cntrNbr;
	private double billTonBl;
	private double billTonEdo;
	private double billTonDn;
	private double billTonEsn;
	private double billTonBkg;
	private double loadTonCs;
	private double shutoutTonCs;
	private int countUnit;
	private int totalPackEdo;
	private int totalPackDn;
	private int cntrSeqNbr;
	private Timestamp varDttm;
	private Timestamp printDttm;
	private Timestamp lastModifyDttm;
	private Timestamp lastTriggerPSRDttm;
	protected String mvmt;
	private String lastModifyUserId;
	private String cntrCat;
	private String cntrSize;
	private String ssRefNbr;
	private String isFirstDNForEDO;
	private String isEPCForTS;

	public int getCntrSeqNbr() {
		return cntrSeqNbr;
	}

	public void setCntrSeqNbr(int cntrSeqNbr) {
		this.cntrSeqNbr = cntrSeqNbr;
	}

	public String getMvmt() {
		return mvmt;
	}

	public void setMvmt(String mvmt) {
		this.mvmt = mvmt;
	}

	public String getBlNbr() {
		return blNbr;
	}

	public void setBlNbr(String blNbr) {
		this.blNbr = blNbr;
	}

	public String getEdoAsnNbr() {
		return edoAsnNbr;
	}

	public void setEdoAsnNbr(String edoAsnNbr) {
		this.edoAsnNbr = edoAsnNbr;
	}

	public String getBkRefNbr() {
		return bkRefNbr;
	}

	public void setBkRefNbr(String bkRefNbr) {
		this.bkRefNbr = bkRefNbr;
	}

	public String getEsnAsnNbr() {
		return esnAsnNbr;
	}

	public void setEsnAsnNbr(String esnAsnNbr) {
		this.esnAsnNbr = esnAsnNbr;
	}

	public String getDnNbr() {
		return dnNbr;
	}

	public void setDnNbr(String dnNbr) {
		this.dnNbr = dnNbr;
	}

	public String getUaNbr() {
		return uaNbr;
	}

	public void setUaNbr(String uaNbr) {
		this.uaNbr = uaNbr;
	}

	public double getBillTonBl() {
		return billTonBl;
	}

	public void setBillTonBl(double billTonBl) {
		this.billTonBl = billTonBl;
	}

	public double getBillTonEdo() {
		return billTonEdo;
	}

	public void setBillTonEdo(double billTonEdo) {
		this.billTonEdo = billTonEdo;
	}

	public double getBillTonDn() {
		return billTonDn;
	}

	public void setBillTonDn(double billTonDn) {
		this.billTonDn = billTonDn;
	}

	public double getBillTonEsn() {
		return billTonEsn;
	}

	public void setBillTonEsn(double billTonEsn) {
		this.billTonEsn = billTonEsn;
	}

	public double getBillTonBkg() {
		return billTonBkg;
	}

	public void setBillTonBkg(double billTonBkg) {
		this.billTonBkg = billTonBkg;
	}

	public double getLoadTonCs() {
		return loadTonCs;
	}

	public void setLoadTonCs(double loadTonCs) {
		this.loadTonCs = loadTonCs;
	}

	public double getShutoutTonCs() {
		return shutoutTonCs;
	}

	public void setShutoutTonCs(double shutoutTonCs) {
		this.shutoutTonCs = shutoutTonCs;
	}

	public int getCountUnit() {
		return countUnit;
	}

	public void setCountUnit(int countUnit) {
		this.countUnit = countUnit;
	}

	public int getTotalPackEdo() {
		return totalPackEdo;
	}

	public void setTotalPackEdo(int totalPackEdo) {
		this.totalPackEdo = totalPackEdo;
	}

	public int getTotalPackDn() {
		return totalPackDn;
	}

	public void setTotalPackDn(int totalPackDn) {
		this.totalPackDn = totalPackDn;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getDiscVvCd() {
		return discVvCd;
	}

	public void setDiscVvCd(String discVvCd) {
		this.discVvCd = discVvCd;
	}

	public String getRefInd() {
		return refInd;
	}

	public void setRefInd(String refInd) {
		this.refInd = refInd;
	}

	public String getVvInd() {
		return vvInd;
	}

	public void setVvInd(String vvInd) {
		this.vvInd = vvInd;
	}

	public String getLoadVvCd() {
		return loadVvCd;
	}

	public void setLoadVvCd(String loadVvCd) {
		this.loadVvCd = loadVvCd;
	}

	public String getBillAcctNbr() {
		return billAcctNbr;
	}

	public void setBillAcctNbr(String billAcctNbr) {
		this.billAcctNbr = billAcctNbr;
	}

	public Timestamp getVarDttm() {
		return varDttm;
	}

	public void setVarDttm(Timestamp varDttm) {
		this.varDttm = varDttm;
	}

	public String getDiscGateway() {
		return discGateway;
	}

	public void setDiscGateway(String discGateway) {
		this.discGateway = discGateway;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getSchemeCd() {
		return schemeCd;
	}

	public void setSchemeCd(String schemeCd) {
		this.schemeCd = schemeCd;
	}

	public String getTariffMainCatCd() {
		return tariffMainCatCd;
	}

	public void setTariffMainCatCd(String tariffMainCatCd) {
		this.tariffMainCatCd = tariffMainCatCd;
	}

	public String getTariffSubCatCd() {
		return tariffSubCatCd;
	}

	public void setTariffSubCatCd(String tariffSubCatCd) {
		this.tariffSubCatCd = tariffSubCatCd;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getLastModifyDttm() {
		return lastModifyDttm;
	}

	public void setLastModifyDttm(Timestamp lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
	}

	public String getBillInd() {
		return billInd;
	}

	public void setBillInd(String billInd) {
		this.billInd = billInd;
	}

	public String getCargoType() {
		return cargoType;
	}

	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}

	public String getLocalLeg() {
		return localLeg;
	}

	public void setLocalLeg(String localLeg) {
		this.localLeg = localLeg;
	}

	public Timestamp getLastTriggerPSRDttm() {
		return lastTriggerPSRDttm;
	}

	public void setLastTriggerPSRDttm(Timestamp lastTriggerPSRDttm) {
		this.lastTriggerPSRDttm = lastTriggerPSRDttm;
	}

	public Timestamp getPrintDttm() {
		return printDttm;
	}

	public void setPrintDttm(Timestamp printDttm) {
		this.printDttm = printDttm;
	}

	public String getCntrNbr() {
		return cntrNbr;
	}

	public void setCntrNbr(String cntrNbr) {
		this.cntrNbr = cntrNbr;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public String getCntrCat() {
		return cntrCat;
	}

	public void setCntrCat(String cntrCat) {
		this.cntrCat = cntrCat;
	}

	public String getCntrSize() {
		return cntrSize;
	}

	public void setCntrSize(String cntrSize) {
		this.cntrSize = cntrSize;
	}

	public String getSsRefNbr() {
		return ssRefNbr;
	}

	public void setSsRefNbr(String ssRefNbr) {
		this.ssRefNbr = ssRefNbr;
	}

	public String getIsFirstDNForEDO() {
		return isFirstDNForEDO;
	}

	public void setIsFirstDNForEDO(String isFirstDNForEDO) {
		this.isFirstDNForEDO = isFirstDNForEDO;
	}

	public String getIsEPCForTS() {
		return isEPCForTS;
	}

	public void setIsEPCForTS(String isEPCForTS) {
		this.isEPCForTS = isEPCForTS;
	}
}

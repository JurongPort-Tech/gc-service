package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class ChargeableBillValueObject {

	private String refNbr;
	private Timestamp txnDttm;
	private String refInd;
	private String tariffType;
	private String tariffMainCatCd;
	private String tariffSubCatCd;
	private int versionNbr;
	private String tariffCd;
	private int tierSeqNbr;
	private String custCd;
	private String acctNbr;
	private String contractNbr;
	private int contractualYr;
	private String tariffDesc;
	private int nbrCntr;
	private double nbrTimeUnit;
	private double nbrOtherUnit;
	private double unitRate;
	private double gstCharge;
	private double gstAmt;
	private double totalChargeAmt;
	private String vvCd;
	private String billInd;
	private String lastModifyUserId;
	private Timestamp lastModifyDttm;
	private String errorMsg;
	private String localLeg;
	private String miscSeqNbr;
	private Integer[] items;
	private String[] remarks;
	private Timestamp varDttm;
	private String billCriteria;
	private String actualBillCriteria;
	private double tonnage;
	private double area;
	private int diffDttm;
	private int itemNbr;
	private int nbrPackages;
	private int approvedNbrPackages;
	private String billRunningNbr;
	private String billNbr;
	private Timestamp paymentDttm;
	private String bizType;
	private String schemeCd;
	private String supportInfo;
	private String fmasGstCd;

	public ChargeableBillValueObject(ChargeableBillValueObject chargeableBillValueObject) {
		this.copy(chargeableBillValueObject);
	}

	public ChargeableBillValueObject() {
		// TODO Auto-generated constructor stub
	}

	public void copy(ChargeableBillValueObject chargeableBillValueObject) {
		this.refNbr = chargeableBillValueObject.getRefNbr();
		this.txnDttm = chargeableBillValueObject.getTxnDttm();
		this.refInd = chargeableBillValueObject.getRefInd();
		this.tariffType = chargeableBillValueObject.getTariffType();
		this.tariffMainCatCd = chargeableBillValueObject.getTariffMainCatCd();
		this.tariffSubCatCd = chargeableBillValueObject.getTariffSubCatCd();
		this.versionNbr = chargeableBillValueObject.getVersionNbr();
		this.tariffCd = chargeableBillValueObject.getTariffCd();
		this.tierSeqNbr = chargeableBillValueObject.getTierSeqNbr();
		this.custCd = chargeableBillValueObject.getCustCd();
		this.acctNbr = chargeableBillValueObject.getAcctNbr();
		this.contractNbr = chargeableBillValueObject.getContractNbr();
		this.contractualYr = chargeableBillValueObject.getContractualYr();
		this.tariffDesc = chargeableBillValueObject.getTariffDesc();
		this.nbrCntr = chargeableBillValueObject.getNbrCntr();
		this.nbrTimeUnit = chargeableBillValueObject.getNbrTimeUnit();
		this.nbrOtherUnit = chargeableBillValueObject.getNbrOtherUnit();
		this.unitRate = chargeableBillValueObject.getUnitRate();
		this.gstCharge = chargeableBillValueObject.getGstCharge();
		this.gstAmt = chargeableBillValueObject.getGstAmt();
		this.totalChargeAmt = chargeableBillValueObject.getTotalChargeAmt();
		this.vvCd = chargeableBillValueObject.getVvCd();
		this.billInd = chargeableBillValueObject.getBillInd();
		this.lastModifyUserId = chargeableBillValueObject.getLastModifyUserId();
		this.lastModifyDttm = chargeableBillValueObject.getLastModifyDttm();
		this.errorMsg = chargeableBillValueObject.getErrorMsg();
		this.localLeg = chargeableBillValueObject.getLocalLeg();
		this.fmasGstCd = chargeableBillValueObject.getFmasGstCd();
		this.miscSeqNbr = chargeableBillValueObject.getMiscSeqNbr();
		this.items = chargeableBillValueObject.getItems();
		this.varDttm = chargeableBillValueObject.getVarDttm();
		this.remarks = chargeableBillValueObject.getRemarks();
		this.billCriteria = chargeableBillValueObject.getBillCriteria();
		this.actualBillCriteria = chargeableBillValueObject.getActualBillCriteria();
		this.tonnage = chargeableBillValueObject.getTonnage();
		this.area = chargeableBillValueObject.getArea();
		this.diffDttm = chargeableBillValueObject.getDiffDttm();
		this.itemNbr = chargeableBillValueObject.getItemNbr();
		this.nbrPackages = chargeableBillValueObject.getNbrPackages();
		this.approvedNbrPackages = chargeableBillValueObject.getApprovedNbrPackages();
		this.billRunningNbr = chargeableBillValueObject.getBillRunningNbr();
		this.bizType = chargeableBillValueObject.getBizType();
		this.schemeCd = chargeableBillValueObject.getSchemeCd();
		this.supportInfo = chargeableBillValueObject.getSupportInfo();
	}

	public String getMiscSeqNbr() {
		return miscSeqNbr;
	}

	public void setMiscSeqNbr(String miscSeqNbr) {
		this.miscSeqNbr = miscSeqNbr;
	}

	public Integer[] getItems() {
		return items;
	}

	public void setItems(Integer[] items) {
		this.items = items;
	}

	public String[] getRemarks() {
		return remarks;
	}

	public void setRemarks(String[] remarks) {
		this.remarks = remarks;
	}

	public Timestamp getVarDttm() {
		return varDttm;
	}

	public void setVarDttm(Timestamp varDttm) {
		this.varDttm = varDttm;
	}

	public String getBillCriteria() {
		return billCriteria;
	}

	public void setBillCriteria(String billCriteria) {
		this.billCriteria = billCriteria;
	}

	public String getActualBillCriteria() {
		return actualBillCriteria;
	}

	public void setActualBillCriteria(String actualBillCriteria) {
		this.actualBillCriteria = actualBillCriteria;
	}

	public double getTonnage() {
		return tonnage;
	}

	public void setTonnage(double tonnage) {
		this.tonnage = tonnage;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public int getDiffDttm() {
		return diffDttm;
	}

	public void setDiffDttm(int diffDttm) {
		this.diffDttm = diffDttm;
	}

	public int getItemNbr() {
		return itemNbr;
	}

	public void setItemNbr(int itemNbr) {
		this.itemNbr = itemNbr;
	}

	public int getNbrPackages() {
		return nbrPackages;
	}

	public void setNbrPackages(int nbrPackages) {
		this.nbrPackages = nbrPackages;
	}

	public int getApprovedNbrPackages() {
		return approvedNbrPackages;
	}

	public void setApprovedNbrPackages(int approvedNbrPackages) {
		this.approvedNbrPackages = approvedNbrPackages;
	}

	public String getBillRunningNbr() {
		return billRunningNbr;
	}

	public void setBillRunningNbr(String billRunningNbr) {
		this.billRunningNbr = billRunningNbr;
	}

	public String getBillNbr() {
		return billNbr;
	}

	public void setBillNbr(String billNbr) {
		this.billNbr = billNbr;
	}

	public Timestamp getPaymentDttm() {
		return paymentDttm;
	}

	public void setPaymentDttm(Timestamp paymentDttm) {
		this.paymentDttm = paymentDttm;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getSchemeCd() {
		return schemeCd;
	}

	public void setSchemeCd(String schemeCd) {
		this.schemeCd = schemeCd;
	}

	public String getSupportInfo() {
		return supportInfo;
	}

	public void setSupportInfo(String supportInfo) {
		this.supportInfo = supportInfo;
	}

	public String getFmasGstCd() {
		return fmasGstCd;
	}

	public void setFmasGstCd(String fmasGstCd) {
		this.fmasGstCd = fmasGstCd;
	}

	public String getRefNbr() {
		return refNbr;
	}

	public void setRefNbr(String refNbr) {
		this.refNbr = refNbr;
	}

	public Timestamp getTxnDttm() {
		return txnDttm;
	}

	public void setTxnDttm(Timestamp txnDttm) {
		this.txnDttm = txnDttm;
	}

	public String getRefInd() {
		return refInd;
	}

	public void setRefInd(String refInd) {
		this.refInd = refInd;
	}

	public String getTariffType() {
		return tariffType;
	}

	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
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

	public int getVersionNbr() {
		return versionNbr;
	}

	public void setVersionNbr(int versionNbr) {
		this.versionNbr = versionNbr;
	}

	public String getTariffCd() {
		return tariffCd;
	}

	public void setTariffCd(String tariffCd) {
		this.tariffCd = tariffCd;
	}

	public int getTierSeqNbr() {
		return tierSeqNbr;
	}

	public void setTierSeqNbr(int tierSeqNbr) {
		this.tierSeqNbr = tierSeqNbr;
	}

	public String getCustCd() {
		return custCd;
	}

	public void setCustCd(String custCd) {
		this.custCd = custCd;
	}

	public String getAcctNbr() {
		return acctNbr;
	}

	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	public String getContractNbr() {
		return contractNbr;
	}

	public void setContractNbr(String contractNbr) {
		this.contractNbr = contractNbr;
	}

	public int getContractualYr() {
		return contractualYr;
	}

	public void setContractualYr(int contractualYr) {
		this.contractualYr = contractualYr;
	}

	public String getTariffDesc() {
		return tariffDesc;
	}

	public void setTariffDesc(String tariffDesc) {
		this.tariffDesc = tariffDesc;
	}

	public int getNbrCntr() {
		return nbrCntr;
	}

	public void setNbrCntr(int nbrCntr) {
		this.nbrCntr = nbrCntr;
	}

	public double getNbrTimeUnit() {
		return nbrTimeUnit;
	}

	public void setNbrTimeUnit(double nbrTimeUnit) {
		this.nbrTimeUnit = nbrTimeUnit;
	}

	public double getNbrOtherUnit() {
		return nbrOtherUnit;
	}

	public void setNbrOtherUnit(double nbrOtherUnit) {
		this.nbrOtherUnit = nbrOtherUnit;
	}

	public double getUnitRate() {
		return unitRate;
	}

	public void setUnitRate(double unitRate) {
		this.unitRate = unitRate;
	}

	public double getGstCharge() {
		return gstCharge;
	}

	public void setGstCharge(double gstCharge) {
		this.gstCharge = gstCharge;
	}

	public double getGstAmt() {
		return gstAmt;
	}

	public void setGstAmt(double gstAmt) {
		this.gstAmt = gstAmt;
	}

	public double getTotalChargeAmt() {
		return totalChargeAmt;
	}

	public void setTotalChargeAmt(double totalChargeAmt) {
		this.totalChargeAmt = totalChargeAmt;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getBillInd() {
		return billInd;
	}

	public void setBillInd(String billInd) {
		this.billInd = billInd;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public Timestamp getLastModifyDttm() {
		return lastModifyDttm;
	}

	public void setLastModifyDttm(Timestamp lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getLocalLeg() {
		return localLeg;
	}

	public void setLocalLeg(String localLeg) {
		this.localLeg = localLeg;
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

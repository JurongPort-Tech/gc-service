package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.ShutoutEdoDnReport;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardShutoutCargoEdoService {

	public List<EdoValueObjectCargo> getShutoutVesselVoyageNbrList() throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutVesselList(String vesselName, String voyageNumber) throws BusinessException;

	public TableResult getShutoutEdoList(String strvarnbr, Criteria criteria) throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutEdoDetail(String edoAsnNbr) throws BusinessException ;

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoAsnNbr) throws BusinessException;

	public String deleteShutoutEdoDetails(String stredoasnnbr, String userId)  throws BusinessException;

	public List<EdoValueObjectCargo> getShutoutAddDetail(String esnAsnNo) throws BusinessException;

	public String getCompanyName(String adpNbr) throws BusinessException;

	public List<EdoJpBilling> getEdoJpBillingNbr(String adpNbr, String companyCode, String vvCd) throws BusinessException;

	public String getCustomerNbr(String adpNbr) throws BusinessException;

	public String updateShutoutEdo(EdoValueObjectCargo edo, String userId) throws BusinessException;

	public List<String> getWHIndicator(String edoasnnbr) throws BusinessException;

	public void updateWHIndicator(String edoasnnbr, String whind, String whappnbr, String remarks, String nodays,
			String loginId) throws BusinessException;

	public List<List<String>> getEsnList(String vvCd) throws BusinessException;

	public String insertShutoutEdoForDPE(EdoValueObjectCargo edo, String userId) throws BusinessException;

	public boolean chkVslStat(String strvarnbr) throws BusinessException;

	public List<EdoValueObjectCargo> getOutStandingList(String strvarnbr) throws BusinessException;

	public boolean chktesnJpJp(String string) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNDetail(String deNull, String deNull2) throws BusinessException;

	public List<EdoValueObjectOps> getVechDetails(String deNull) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNDetail(String deNull, String deNull2, String deNull3, String s4, String s5) throws BusinessException;

	public boolean checkESNCntr(String deNull) throws BusinessException;

	public String getCntrNo(String deNull) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String deNull, String deNull2, String s4, String s5) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNCreateDetail(String deNull, String deNull2, String s4, String s5) throws BusinessException;

	public List<ChargeableBillValueObject> getGBBillCharge(String deNull, String string) throws BusinessException;

	public void purgetemptableDN(String deNull) throws BusinessException;

	public String insertTempDNPrintOut(String deNull, String deNull2, String deNull3, String s4, String s5) throws BusinessException;

	public String insertTempBill(String deNull, String s6, String s9, double d6, double d, double d3, String s12,
			String s7, String s10, double d7, double d1, double d4, String s13, String s8, String s11, double d8,
			double d2, double d5, String s14, String s, String s23, String s17, String s18, double d11, double d9,
			double d10, String s19, String s20, String s21, double d14, double d12, double d13, String s22, double d15,
			double d16, double d17, double d18, double d19) throws BusinessException;

	public boolean checkVehicleExit(String dnnbr) throws BusinessException;

	public List<String []> getCntrNbr(String edoasn) throws BusinessException;

	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException;

	public String getMachineID(String cash_receipt_nbr) throws BusinessException;

	public String getCashSalesPaymentCode(String csType) throws BusinessException;

	public String getNETSRefID(String cash_receipt_nbr) throws BusinessException;

	public boolean isValidVehicleNumber(String new_vehicleNo, String companyCode) throws BusinessException;

	public void updateVehicleNo(String dnNbr, String new_vehicleNo) throws BusinessException;

	public String getCntrSeq(String cntrNbr) throws BusinessException;

	public boolean checkCancelDN(String dnNbr) throws BusinessException;

	public void checkAndUpdateFirstDN(String string, String dnNbr) throws BusinessException;

	public boolean cancelBillableCharges(String string, String string2) throws BusinessException;

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	public String cancelShutoutDN(String string, String string2, String s5) throws BusinessException;

	public String cancelDN(String string, String string2, String s5, String string3, String searchCrg, String s11) throws BusinessException;

	public String checkTransType(String s11) throws BusinessException;

	public String getUaNbr(String s11, int nbrPkg, String transDttm, String dpNm, String dpIcNbr) throws BusinessException;

	public void cancelUA(String uaNbr, String s11, String transType, String s5, String string) throws BusinessException;

	public boolean countDNBalance(String cntrNbr) throws BusinessException;

	public void updateCntrStatus(String cntrSeq, String s5) throws BusinessException;

	public int checkFirstDN(String dnNbr, String cntrNbr) throws BusinessException;

	public String getNewCatCd(String cntrSeq) throws BusinessException;

	public void changeStatusCntr(String cntrSeq, String s5, String newCatCd) throws BusinessException;

	public void cancel1stDn(String cntrSeq, String cntrNbr, String s5) throws BusinessException;

	public void updateWeight(String cntrSeq, long weight, String s5, String string) throws BusinessException;

	public String chktesnJpPsa_nbr(String s11) throws BusinessException;

	public List<EdoValueObjectOps> fetchEdoDetails(String string, String searchCrg, String s11) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNList(String string, String string2, String s11) throws BusinessException;

	public String chktesnJpJp_nbr(String s11) throws BusinessException;

	public boolean chkEDOStuffing(String string) throws BusinessException;

	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> vector1) throws BusinessException;

	public List<EdoValueObjectOps> fetchSubAdpDetails(String string) throws BusinessException;

	public int getSpencialPackage(String string) throws BusinessException;

	public int getTotalCustCdByIcNumber(String s10, String s17) throws BusinessException;

	public boolean isExistWarehouseApplicationWithASNNubmer(String edoNbr) throws BusinessException;

	public List<GBWareHouseAplnVO> getWarehouseApplicationListByASNNubmer(String edoNbr) throws BusinessException;

	public void sendMessage(Sms vo) throws BusinessException;

	public void voidWarehouseApplicationWithASNNubmer(String edoNbr, String s) throws BusinessException;

	public String createShutoutDN(String s3, String s4, String s5, String s6, String s7, String s8, String s9,
			String s10, String s11, String s12, String s, String s17, String s18, String s19, String cargoDes) throws BusinessException;

	public String triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException;

	public String createDN(String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
			String s11, String s12, String s, String s17, String s18, String s19, String cargoDes) throws BusinessException;

	public boolean isTESN_JP_JP(String s3, String s19) throws BusinessException;

	public String createUA(String s19, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
			String s17, String s11, String s12, String s) throws BusinessException;

	public String getUANbr(String s19) throws BusinessException;

	public String getVcd(String s19) throws BusinessException;

	public String TriggerUa(String uaNbr, String s, String vvCd) throws BusinessException;

	public void updateDN(String cntrNo, String s2) throws BusinessException;

	public void updateCntr(String cntrSeq, String cntrNo, String s, String newCatCd) throws BusinessException;

	public boolean chkCntrCrgDn(String s2) throws BusinessException;

	public boolean chkraiseCharge(String s3) throws BusinessException;

	public String TriggerDN(String s2, String s) throws BusinessException; 
	
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters, String dnNbr, List<?> Record) throws BusinessException, Exception;

	public List<ShutoutEdoDnReport> getdnReportDetails(String dnNbr) throws BusinessException;

}

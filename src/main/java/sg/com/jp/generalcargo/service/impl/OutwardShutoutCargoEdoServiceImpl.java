package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.CashSalesRepository;
import sg.com.jp.generalcargo.dao.DnEdoDetailRepository;
import sg.com.jp.generalcargo.dao.DnRepo;
import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.GBWareHouseAplnRepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepository;
import sg.com.jp.generalcargo.dao.UARepository;
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
import sg.com.jp.generalcargo.service.OutwardShutoutCargoEdoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service("outwardShutoutCargoEdoService")
public class OutwardShutoutCargoEdoServiceImpl implements OutwardShutoutCargoEdoService{

	private static final Log log = LogFactory.getLog(OutwardShutoutCargoEdoServiceImpl.class);
	@Autowired
	private EdoRepository edoRepo;

	@Autowired
	private ManifestRepository manifestRepo;

	@Autowired
	private DnEdoDetailRepository dnEdoDetailRepository;

	@Autowired
	private DnRepo dnRepo;
	
	@Autowired
	private UARepository UARepo;

	@Autowired
	private ProcessGBLogRepository processGBLogRepo;
	
	@Autowired
	private CashSalesRepository cashSalesRepo;
	
	@Autowired
	private GBWareHouseAplnRepository GBWareHouseAplnRepo;
	
	@Autowired
	private TransactionLoggerRepository transactionLoggerRepo;
	
	@Autowired
	private TransactionLoggerRepo transLoggerRepo;

	@Override
	public List<EdoValueObjectCargo> getShutoutVesselVoyageNbrList() throws BusinessException {

		return edoRepo.getShutoutVesselVoyageNbrList();
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutVesselList(String vesselName, String voyageNumber) throws BusinessException {

		return edoRepo.getShutoutVesselList(vesselName, voyageNumber);
	}

	@Override
	public TableResult getShutoutEdoList(String strvarnbr, Criteria criteria) throws BusinessException {

		return edoRepo.getShutoutEdoList(strvarnbr, criteria);
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutEdoDetail(String edoAsnNbr)  throws BusinessException {

		return edoRepo.getShutoutEdoDetail(edoAsnNbr);
	}

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNList(String edoAsnNbr) throws BusinessException {

		return dnEdoDetailRepository.fetchShutoutDNList(edoAsnNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String deleteShutoutEdoDetails(String stredoasnnbr, String userId) throws BusinessException {

		return edoRepo.deleteShutoutEdoDetails(stredoasnnbr, userId);
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutAddDetail(String esnAsnNo) throws BusinessException {

		return edoRepo.getShutoutAddDetail(esnAsnNo);
	}

	@Override
	public String getCompanyName(String adpNbr) throws BusinessException {

		return edoRepo.getCompanyName(adpNbr);
	}

	@Override
	public List<EdoJpBilling> getEdoJpBillingNbr(String adpNbr, String companyCode, String vvCd) throws BusinessException {

		return edoRepo.getEdoJpBillingNbr(adpNbr, companyCode, vvCd);
	}

	@Override
	public String getCustomerNbr(String adpNbr) throws BusinessException {

		return edoRepo.getCustomerNbr(adpNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateShutoutEdo(EdoValueObjectCargo edo, String userId) throws BusinessException {

		return edoRepo.updateShutoutEdo(edo, userId);
	}

	@Override
	public List<String> getWHIndicator(String edoasnnbr) throws BusinessException {

		return edoRepo.getWHIndicator(edoasnnbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWHIndicator(String edoasnnbr, String whind, String whappnbr, String remarks, String nodays,
			String loginId) throws BusinessException {

		edoRepo.updateWHIndicator(edoasnnbr, whind, whappnbr, remarks, nodays, loginId);
	}

	@Override
	public List<List<String>> getEsnList(String vvCd) throws BusinessException {

		return edoRepo.getEsnList(vvCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertShutoutEdoForDPE(EdoValueObjectCargo edo, String userId) throws BusinessException {

		return edoRepo.insertShutoutEdoForDPE(edo, userId);
	}

	@Override
	public boolean chkVslStat(String strvarnbr) throws BusinessException {

		return manifestRepo.chkVslStat(strvarnbr);
	}

	@Override
	public List<EdoValueObjectCargo> getOutStandingList(String strvarnbr) throws BusinessException {

		return edoRepo.getOutStandingList(strvarnbr);
	}

	@Override
	public boolean chktesnJpJp(String string) throws BusinessException {

		return dnRepo.chktesnJpJp(string);
	}

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNDetail(String deNull, String deNull2) throws BusinessException {

		return dnRepo.fetchShutoutDNDetail(deNull, deNull2);
	}

	@Override
	public List<EdoValueObjectOps> getVechDetails(String deNull) throws BusinessException {

		return dnRepo.getVechDetails(deNull);
	}

	@Override
	public List<EdoValueObjectOps> fetchDNDetail(String deNull, String deNull2, String deNull3, String s4, String s5)
			throws BusinessException {

		return dnRepo.fetchDNDetail(deNull, deNull2, deNull3, s4, s5);
	}

	@Override
	public boolean checkESNCntr(String deNull) throws BusinessException {

		return dnRepo.checkESNCntr(deNull);
	}

	@Override
	public String getCntrNo(String deNull) throws BusinessException {

		return dnRepo.getCntrNo(deNull);
	}

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String deNull, String deNull2, String s4, String s5)
			throws BusinessException {

		return dnRepo.fetchShutoutDNCreateDetail(deNull, deNull2, s4, s5);
	}

	@Override
	public List<EdoValueObjectOps> fetchDNCreateDetail(String deNull, String deNull2, String s4, String s5) throws BusinessException {

		return dnRepo.fetchDNCreateDetail(deNull, deNull2, s4, s5);
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String deNull, String string) throws BusinessException {

		return processGBLogRepo.getGBBillCharge(deNull, string);
	}

	@Override
	public void purgetemptableDN(String deNull) throws BusinessException {

		dnRepo.purgetemptableDN(deNull);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertTempDNPrintOut(String deNull, String deNull2, String deNull3, String s4, String s5)
			throws BusinessException {

		return dnRepo.insertTempDNPrintOut(deNull, deNull2, deNull3, s4, s5);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertTempBill(String deNull, String s6, String s9, double d6, double d, double d3, String s12,
			String s7, String s10, double d7, double d1, double d4, String s13, String s8, String s11, double d8,
			double d2, double d5, String s14, String s, String s23, String s17, String s18, double d11, double d9,
			double d10, String s19, String s20, String s21, double d14, double d12, double d13, String s22, double d15,
			double d16, double d17, double d18, double d19) throws BusinessException {

		return dnRepo.insertTempBill( deNull,  s6,  s9,  d6,  d,  d3,  s12,s7,  s10,  d7,  d1,  d4,  s13,  s8,  s11,  d8,
				d2,  d5,  s14,  s,  s23,  s17,  s18,  d11,  d9,d10,  s19,  s20,  s21,  d14,  d12,  d13,  s22,  d15,
				d16,  d17,  d18,  d19);
	}

	@Override
	public boolean checkVehicleExit(String dnnbr) throws BusinessException {

		return dnRepo.checkVehicleExit(dnnbr);
	}

	@Override
	public List<String []> getCntrNbr(String edoasn) throws BusinessException {

		return dnRepo.getCntrNbr(edoasn);
	}

	@Override
	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {

		return cashSalesRepo.getCashSales(refNbr);
	}

	@Override
	public String getMachineID(String cash_receipt_nbr) throws BusinessException {

		return cashSalesRepo.getMachineID(cash_receipt_nbr);
	}

	@Override
	public String getCashSalesPaymentCode(String csType) throws BusinessException {

		return cashSalesRepo.getCashSalesPaymentCode(csType);
	}

	@Override
	public String getNETSRefID(String cash_receipt_nbr) throws BusinessException {

		return cashSalesRepo.getNETSRefID(cash_receipt_nbr);
	}

	@Override
	public boolean isValidVehicleNumber(String new_vehicleNo, String companyCode) throws BusinessException {
		
		return dnRepo.isValidVehicleNumber(new_vehicleNo, companyCode);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateVehicleNo(String dnNbr, String new_vehicleNo) throws BusinessException {
		
		dnRepo.updateVehicleNo(dnNbr, new_vehicleNo);
	}

	@Override
	public String getCntrSeq(String cntrNbr) throws BusinessException {
		
		return dnRepo.getCntrSeq(cntrNbr);
	}

	@Override
	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		
		return dnRepo.checkCancelDN(dnNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void checkAndUpdateFirstDN(String string, String dnNbr) throws BusinessException {
		
		processGBLogRepo.checkAndUpdateFirstDN(string, dnNbr);
	}

	@Override
	public boolean cancelBillableCharges(String string, String string2) throws BusinessException {
		
		return processGBLogRepo.cancelBillableCharges(string, string2);
	}

	@Override
	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		
		return dnRepo.getDnCntrFirst(cntrSeq, cntrNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String cancelShutoutDN(String string, String string2, String s5) throws BusinessException {
		
		return dnRepo.cancelShutoutDN(string, string2, s5);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String cancelDN(String string, String string2, String s5, String string3, String searchCrg, String s11)
			throws BusinessException {
		
		return dnRepo.cancelDN(string, string2, s5, string3, searchCrg, s11);
	}

	@Override
	public String checkTransType(String s11) throws BusinessException {
		
		return UARepo.checkTransType(s11);
	}

	@Override
	public String getUaNbr(String s11, int nbrPkg, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		
		return dnRepo.getUaNbr(s11, nbrPkg, transDttm, dpNm, dpIcNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void cancelUA(String uaNbr, String s11, String transType, String s5, String string)
			throws BusinessException {
		
		UARepo.cancelUA(uaNbr, s11, transType, s5, string);
	}

	@Override
	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		
		return dnRepo.countDNBalance(cntrNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateCntrStatus(String cntrSeq, String s5) throws BusinessException {
		
		dnRepo.updateCntrStatus(cntrSeq, s5);
	}

	@Override
	public int checkFirstDN(String dnNbr, String cntrNbr) throws BusinessException {
		
		return dnRepo.checkFirstDN(dnNbr, cntrNbr);
	}

	@Override
	public String getNewCatCd(String cntrSeq) throws BusinessException {
		
		return dnRepo.getNewCatCd(cntrSeq);
	}

	@Override
	public void changeStatusCntr(String cntrSeq, String s5, String newCatCd) throws BusinessException {
		
		dnRepo.changeStatusCntr(cntrSeq, s5, newCatCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void cancel1stDn(String cntrSeq, String cntrNbr, String s5) throws BusinessException {
		
		dnRepo.cancel1stDn(cntrSeq, cntrNbr, s5);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWeight(String cntrSeq, long weight, String s5, String string) throws BusinessException {
		
		dnRepo.updateWeight(cntrSeq, weight, s5, string);
	}

	@Override
	public String chktesnJpPsa_nbr(String s11) throws BusinessException {
		
		return dnRepo.chktesnJpPsa_nbr(s11);
	}

	@Override
	public List<EdoValueObjectOps> fetchEdoDetails(String string, String searchCrg, String s11) throws BusinessException {
		
		return dnRepo.fetchEdoDetails(string, searchCrg, s11);
	}
	
	@Override
	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> vector1) throws BusinessException {
		
		return cashSalesRepo.getCashSales(vector1);
	}

	@Override
	public List<EdoValueObjectOps> fetchDNList(String string, String string2, String s11) throws BusinessException {
		
		return dnRepo.fetchDNList(string, string2, s11);
	}

	@Override
	public String chktesnJpJp_nbr(String s11) throws BusinessException {
		
		return dnRepo.chktesnJpJp_nbr(s11);
	}

	@Override
	public boolean chkEDOStuffing(String string) throws BusinessException {
		
		return dnRepo.chkEDOStuffing(string);
	}

	
	@Override
	public List<EdoValueObjectOps> fetchSubAdpDetails(String string) throws BusinessException {
		
		return dnRepo.fetchSubAdpDetails(string);
	}

	@Override
	public int getSpencialPackage(String string) throws BusinessException {
		
		return dnRepo.getSpencialPackage(string);
	}

	@Override
	public int getTotalCustCdByIcNumber(String s10, String s17) throws BusinessException {
		
		return dnRepo.getTotalCustCdByIcNumber(s10, s17);
	}

	@Override
	public boolean isExistWarehouseApplicationWithASNNubmer(String edoNbr) throws BusinessException {
		
		return GBWareHouseAplnRepo.isExistWarehouseApplicationWithASNNubmer(edoNbr);
	}

	@Override
	public List<GBWareHouseAplnVO> getWarehouseApplicationListByASNNubmer(String edoNbr) throws BusinessException {
		
		return GBWareHouseAplnRepo.getWarehouseApplicationListByASNNubmer(edoNbr);
	}

	@Override
	public void sendMessage(Sms vo) throws BusinessException {
		
		GBWareHouseAplnRepo.sendMessage(vo);
	}

	@Override
	public void voidWarehouseApplicationWithASNNubmer(String edoNbr, String s) throws BusinessException {
		
		GBWareHouseAplnRepo.voidWarehouseApplicationWithASNNubmer(edoNbr, s);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String createShutoutDN(String s3, String s4, String s5, String s6, String s7, String s8, String s9,
			String s10, String s11, String s12, String s, String s17, String s18, String s19, String cargoDes)
			throws BusinessException {
		
		return dnRepo.createShutoutDN( s3,  s4,  s5,  s6,  s7,  s8,  s9,
			 s10,  s11,  s12,  s,  s17,  s18,  s19,  cargoDes);
	}

	@Override
	public String triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException {
		
		return transactionLoggerRepo.triggerShutoutCargoDN(dn_nbr, userId);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String createDN(String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
			String s11, String s12, String s, String s17, String s18, String s19, String cargoDes)
			throws BusinessException {
		
		return dnRepo.createDN( s3,  s4,  s5,  s6,  s7,  s8,  s9,  s10,
			 s11,  s12,  s,  s17,  s18,  s19,  cargoDes);
	}

	@Override
	public boolean isTESN_JP_JP(String s3, String s19) throws BusinessException {
		
		return dnRepo.isTESN_JP_JP(s3, s19);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String createUA(String s19, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
			String s17, String s11, String s12, String s) throws BusinessException {
		
		return UARepo.createUA( s19,  s4,  s5,  s6,  s7,  s8,  s9,  s10,
			 s17,  s11,  s12,  s);
	}

	@Override
	public String getUANbr(String s19) throws BusinessException {
		
		return UARepo.getUANbr(s19);
	}

	@Override
	public String getVcd(String s19) throws BusinessException {
		
		return UARepo.getVcd(s19);
	}

	@Override
	public String TriggerUa(String uaNbr, String s, String vvCd) throws BusinessException {
		
		return transLoggerRepo.TriggerUa(uaNbr, s, vvCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateDN(String cntrNo, String s2) throws BusinessException {
		
		dnRepo.updateDN(cntrNo, s2);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateCntr(String cntrSeq, String cntrNo, String s, String newCatCd) throws BusinessException {
		
		dnRepo.updateCntr(cntrSeq, cntrNo, s, newCatCd);
	}

	@Override
	public boolean chkCntrCrgDn(String s2) throws BusinessException {
		
		return dnRepo.chkCntrCrgDn(s2);
	}

	@Override
	public boolean chkraiseCharge(String s3) throws BusinessException {
		
		return dnRepo.chkraiseCharge(s3);
	}

	@Override
	public String TriggerDN(String s2, String s) throws BusinessException {
		
		return transactionLoggerRepo.TriggerDN(s2, s);
	}

	@Override
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters,String nbr, List<?> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" jasperName:"+CommonUtility.deNull(jasperName) +" parameters:"+ parameters
					+" nbr:"+ CommonUtility.deNull(nbr) +" records:"+records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, jasperName, nbr, records);
			log.info("END: *** getJasperPrint Result *****" + jasperPrint.toString());
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
//		
	}

	@Override
	public List<ShutoutEdoDnReport> getdnReportDetails(String dnNbr) throws BusinessException {
		return dnRepo.getdnReportDetails(dnNbr);
	}

}

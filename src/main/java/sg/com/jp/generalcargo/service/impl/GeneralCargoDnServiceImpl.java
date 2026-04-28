package sg.com.jp.generalcargo.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.CashSalesRepository;
import sg.com.jp.generalcargo.dao.GBEventLogRepository;
import sg.com.jp.generalcargo.dao.GBWareHouseAplnRepository;
import sg.com.jp.generalcargo.dao.GeneralCargoDnRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.dao.impl.EdoJdbcRepository;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.EdoVO;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.GbmsCabValueObject;
import sg.com.jp.generalcargo.domain.GcOpsDnReport;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.service.GeneralCargoDnService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Service
public class GeneralCargoDnServiceImpl implements GeneralCargoDnService {

	private static final Log log = LogFactory.getLog(GeneralCargoDnServiceImpl.class);

	@Autowired
	private GeneralCargoDnRepository dnRepo;

	@Autowired
	private ProcessGBLogRepository processGBLogRepo;

	@Autowired
	private CashSalesRepository cashSalesRepo;

	@Autowired
	private UARepository uARepo;

	@Autowired
	private EdoJdbcRepository edoRepo;

	@Autowired
	private GBWareHouseAplnRepository GBWareHouseAplnRepo;

//	@Autowired
//	private TransactionLoggerRepository transactionLoggerRepo;

	@Autowired
	private TransactionLoggerRepo transLoggerRepo;

	@Autowired
	private GBEventLogRepository gbEvntLogRepos;

	@Override
	public List<String[]> getCntrNbr(String edoasn) throws BusinessException {
		return dnRepo.getCntrNbr(edoasn);
	}

	@Override
	public boolean isValidVehicleNumber(String vehicleNumber, String companyCode) throws BusinessException {
		return dnRepo.isValidVehicleNumber(vehicleNumber, companyCode);
	}

	@Override
	public void updateVehicleNo(String dnNo, String vehicleNo) throws BusinessException {
		dnRepo.updateVehicleNo(dnNo, vehicleNo);

	}

	@Override
	public List<EdoVO> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchDNCreateDetail(edoNbr, transType, searchcrg, tesn_nbr);
	}

	@Override
	public String getNETSRefID(String receiptNo) throws BusinessException {
		return cashSalesRepo.getNETSRefID(receiptNo);
	}

	@Override
	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException {
		return cashSalesRepo.getCashSalesPaymentCode(cashsalesType);
	}

	@Override
	public boolean checkVehicleExit(String dnnbr) throws BusinessException {
		return dnRepo.checkVehicleExit(dnnbr);
	}

	@Override
	public String getMachineID(String recNbr) throws BusinessException {
		return cashSalesRepo.getMachineID(recNbr);
	}

	@Override
	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {
		return cashSalesRepo.getCashSales(refNbr);
	}

	@Override
	public List<EdoVO> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchShutoutDNCreateDetail(edoNbr, transType, searchcrg, tesn_nbr);
	}

	@Override
	public String getCntrNo(String dnNbr) throws BusinessException {
		return dnRepo.getCntrNo(dnNbr);
	}

	@Override
	public boolean checkESNCntr(String edoasn) throws BusinessException {
		return dnRepo.checkESNCntr(edoasn);
	}

	@Override
	public List<EdoVO> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchDNDetail(strEdoNo, edoNbr, status, searchcrg, tesn_nbr);
	}

	@Override
	public List<EdoVO> getVechDetails(String dnNbr) throws BusinessException {
		return dnRepo.getVechDetails(dnNbr);
	}

	@Override
	public List<EdoVO> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException {
		return dnRepo.fetchShutoutDNDetail(strEdoNo, dnNo);
	}

	@Override
	public boolean chktesnJpJp(String edoNbr) throws BusinessException {
		return dnRepo.chktesnJpJp(edoNbr);
	}

	@Override
	public String insertTempDNPrintOut(String strEdoNo, String DNNbr, String transtype, String searchcrg,
			String esnasnnbr) throws BusinessException {
		return dnRepo.insertTempDNPrintOut(strEdoNo, DNNbr, transtype, searchcrg, esnasnnbr);
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String refNo, String refInd) throws BusinessException {
		return processGBLogRepo.getGBBillCharge(refNo, refInd);
	}

	@Override
	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {
		return dnRepo.insertTempBill(uanbr, tarcdser, tardescser, billtonsser, urateser, totchrgamtser, actnbrser,
				tarcdwf, tardescwf, billtonswf, uratewf, totchrgamtwf, actnbrwf, tarcdsr, tardescsr, billtonssr,
				uratesr, totchrgamtsr, actnbrsr, UserID, esnactnbr, tarcdsr1, tardescsr1, billtonssr1, uratesr1,
				totchrgamtsr1, actnbrsr1, tarcdsr2, tardescsr2, billtonssr2, uratesr2, totchrgamtsr2, actnbrsr2,
				tunitser, tunitwhf, tunitsr, tunitstore, tunitserwhf);
	}

	@Override
	public void purgetemptableDN(String dnnbr) throws BusinessException {
		dnRepo.purgetemptableDN(dnnbr);
	}

	@Override
	public int getSpencialPackage(String edoNbr) throws BusinessException {
		return dnRepo.getSpencialPackage(edoNbr);
	}

	@Override
	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException {
		return dnRepo.fetchSubAdpDetails(edoNbr);
	}

	@Override
	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException {
		return cashSalesRepo.getCashSales(dnList);
	}

	@Override
	public boolean chkEDOStuffing(String edoNbr) throws BusinessException {
		return dnRepo.chkEDOStuffing(edoNbr);
	}

	@Override
	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchDNList(edoNbr, searchcrg, tesn_nbr);
	}

	@Override
	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException {
		return dnRepo.chktesnJpJp_nbr(esn_asnNbr);
	}

	@Override
	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException {
		return dnRepo.fetchEdoDetails(edoNbr, searchcrg, tesnnbr);
	}

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException {
		return dnRepo.fetchShutoutDNList(edoNbr);
	}

	@Override
	public List<EdoValueObjectCargo> getShutoutEdoDetail(String edoAsnNbr) throws BusinessException {
		return edoRepo.getShutoutEdoDetail(edoAsnNbr);
	}

	@Override
	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException {
		return dnRepo.chktesnJpPsa_nbr(esn_asnNbr);
	}

	@Override
	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException {
		dnRepo.updateWeight(cntrSeq, weight, user, times);
	}

	@Override
	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException {
		dnRepo.cancel1stDn(cntrSeq, cntrNbr, user);

	}

	@Override
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {
		dnRepo.changeStatusCntr(cntrSeq, user, newCatCode);

	}

	@Override
	public String getNewCatCd(String cntrSeq) throws BusinessException {
		return dnRepo.getNewCatCd(cntrSeq);
	}

	@Override
	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException {
		return dnRepo.checkFirstDN(edoNbr, cntrNo);
	}

	@Override
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {
		return dnRepo.updateCntrStatus(cntrSeq, userID);
	}

	@Override
	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		return dnRepo.countDNBalance(cntrNbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void cancelUA(String uaNbr, String esnAsnNbr, String transType, String userId, String uaNbrPkgs)
			throws BusinessException {
		uARepo.cancelUA(uaNbr, esnAsnNbr, transType, userId, uaNbrPkgs);

	}

	@Override
	public String checkTransType(String esnNbr) throws BusinessException {
		return uARepo.checkTransType(esnNbr);
	}

	@Override
	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		return dnRepo.getUaNbr(esnNbr, nbrPkgs, transDttm, dpNm, dpIcNbr);
	}

	@Override
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		return dnRepo.cancelDN(edoNbr, dnNbr, userid, transtype, searchcrg, tesn_nbr);
	}

	@Override
	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException {
		return dnRepo.cancelShutoutDN(edoNbr, dnNbr, userid);
	}

	@Override
	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		return dnRepo.getDnCntrFirst(cntrSeq, cntrNbr);
	}

	@Override
	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException {
		return processGBLogRepo.cancelBillableCharges(refNo, refInd);
	}

	@Override
	public void checkAndUpdateFirstDN(String edoAsnNo, String dnRefNo) throws BusinessException {
		processGBLogRepo.checkAndUpdateFirstDN(edoAsnNo, dnRefNo);
	}

	@Override
	public String getCntrSeq(String cntrNo) throws BusinessException {
		return dnRepo.getCntrSeq(cntrNo);
	}

	@Override
	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		return dnRepo.checkCancelDN(dnNbr);
	}

	@Override
	public boolean chktesnJpPsa(String edoNbr) throws BusinessException {
		return dnRepo.chktesnJpPsa(edoNbr);
	}

	@Override
	public String chkEdoNbr(String edoNbr) throws BusinessException {
		return dnRepo.chkEdoNbr(edoNbr);
	}

	@Override
	public String chktesnEdo(String edoNbr) throws BusinessException {
		return dnRepo.chktesnEdo(edoNbr);
	}

	@Override
	public List<EdoVO> fetchEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException {
		return dnRepo.fetchEdo(edoNbr, compCode, searchcrg);
	}

	@Override
	public List<EdoVO> fetchShutoutEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException {
		return dnRepo.fetchShutoutEdo(edoNbr, compCode, searchcrg);
	}

	@Override
	public List<GcOpsDnReport> getDNPrintJasper(String dnNbr) throws BusinessException {
		return dnRepo.getDNPrintJasper(dnNbr);
	}

	@Override
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters, String nbr,
			List<GcOpsDnReport> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint " + " jasperName:" + CommonUtility.deNull(jasperName) + " parameters:"
					+ parameters + " nbr:" + CommonUtility.deNull(nbr) + " records:" + records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, jasperName, nbr, records);
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception("M4201");
		} finally {
			log.info("END: getJasperPrint SERVICE Result:" + jasperPrint);
		}
		return jasperPrint;
	}

	@Override
	public int getTotalCustCdByIcNumber(String nricno, String ictype) throws BusinessException {
		return dnRepo.getTotalCustCdByIcNumber(nricno, ictype);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createShutoutDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		return dnRepo.createShutoutDN(edoNbr, transtype, edo_Nbr_Pkgs, NomWt, NomVol, date_time, transQty, nric_no,
				dpname, veh1, userid, icType, searchcrg, tesn_nbr, cargoDes);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		return dnRepo.createDN(edoNbr, transtype, edo_Nbr_Pkgs, NomWt, NomVol, date_time, transQty, nric_no, dpname,
				veh1, userid, icType, searchcrg, tesn_nbr, cargoDes);
	}

	@Override
	public boolean isTESN_JP_JP(String s3, String s19) throws BusinessException {
		return dnRepo.isTESN_JP_JP(s3, s19);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createUA(String s19, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
			String s17, String s11, String s12, String s) throws BusinessException {
		return uARepo.createUA(s19, s4, s5, s6, s7, s8, s9, s10, s17, s11, s12, s);
	}

	@Override
	public String getVcd(String s19) throws BusinessException {
		return uARepo.getVcd(s19);
	}

	@Override
	public String getUANbr(String s19) throws BusinessException {
		return uARepo.getUANbr(s19);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateDN(String cntrNo, String s2) throws BusinessException {
		dnRepo.updateDN(cntrNo, s2);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
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
	public int truckerOut(String edoNbr, String veh1) throws BusinessException {
		return dnRepo.truckerOut(edoNbr, veh1);
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

	@Override
	public String triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException {
		String status = "";
		try {
			log.info("START: triggerShutoutCargoDN SERVICE dn_nbr:" + dn_nbr + " userId:" + userId);
			Map<String, Object> parameters1 = transLoggerRepo.triggerShutoutCargoDN(dn_nbr, userId);
			status = triggerShutoutCargoDNExecuteBillCharges(parameters1);
		} catch (Exception e) {
			log.info("Exception triggerShutoutCargoDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: triggerShutoutCargoDN SERVICE Result:" + status);
		}
		return status;
	}

	@Override
	public String TriggerUa(String ua_nbr, String currentUserAcct, String vvcd) throws Exception {
		String updatestatus = transLoggerRepo.TriggerUa(ua_nbr, currentUserAcct, vvcd);
		return updatestatus;
	}

	@Override
	public String TriggerDN(String dnnbr, String struserid) throws BusinessException {
		log.info("START: TriggerDN DAO dnnbr: " + CommonUtility.deNull(dnnbr) + ", struserid: "
				+ CommonUtility.deNull(struserid));

		// add by hujun on 15/8/2011
		if (transLoggerRepo.isShutoutCargoDN(dnnbr)) {
			Map<String, Object> parameters1 = transLoggerRepo.triggerShutoutCargoDN(dnnbr, struserid);
			String status = triggerShutoutCargoDNExecuteBillCharges(parameters1);
			log.info("Status triggerShutoutCargoDNExecuteBillCharges : " +  status);
			return status;
		}
		// add end
		log.info("INSIDE Trigger CAB dn METHOD");
		Map<String, Object> parameters = transLoggerRepo.CabDN(dnnbr, struserid);
		GbmsCabValueObject gbmsCabValueObject = cabDnExecuteBillCharges(parameters);
		return transLoggerRepo.TriggerDN(gbmsCabValueObject);
	}

	private GbmsCabValueObject cabDnExecuteBillCharges(Map<String, Object> parameters) throws BusinessException {
		GbmsCabValueObject gbmsCabValueObject = null;
		List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList = new ArrayList<GeneralEventLogValueObject>();
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		try {
			log.info("START: cabDnExecuteBillCharges DAO dnnbr: " + parameters.toString());
			String billWharfInd = (String) parameters.get("billWharfInd");
			String billSvcChargeInd = (String) parameters.get("billSvcChargeInd");
			String billStoreInd = (String) parameters.get("billStoreInd");
			String billProcessInd = (String) parameters.get("billProcessInd");

			int count1 = (int) parameters.get("count1");
			int count2 = (int) parameters.get("count2");
			int count4 = (int) parameters.get("count4");
			int count5 = (int) parameters.get("count5");
			int count6 = (int) parameters.get("count6");

			Timestamp txnDttm = transLoggerRepo.getSystemDate();

			String bill_store_triggered_ind = (String) parameters.get("bill_store_triggered_ind");
			String strvvcd = (String) parameters.get("strvvcd");
			String struserid = (String) parameters.get("struserid");

			Object listObj = parameters.get("GeneralEventLogEDOArrayList");
			if (listObj instanceof List) {
				for (int j = 0; j < ((List<?>) listObj).size(); j++) {
					Object item = ((List<?>) listObj).get(j);
					if (item instanceof Object) {
						GeneralEventLogEDOArrayList.add((GeneralEventLogValueObject) item);
					}
				}
			}

			listObj = parameters.get("GeneralEventLogArrayList");
			if (listObj instanceof List) {
				for (int j = 0; j < ((List<?>) listObj).size(); j++) {
					Object item = ((List<?>) listObj).get(j);
					if (item instanceof Object) {
						GeneralEventLogArrayList.add((GeneralEventLogValueObject) item);
					}
				}
			}

			gbmsCabValueObject = (GbmsCabValueObject) parameters.get("gbmsCabValueObject");

			if (count1 > 0 || count2 > 0 || count4 > 0 || count5 > 0 || count6 > 0) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();

				vesselTxnEventLogValueObject.setVvCd(strvvcd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				if (bill_store_triggered_ind.equals("Y")) {
					vesselTxnEventLogValueObject.setBillStoreInd(bill_store_triggered_ind);
				}
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(struserid);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR DN VOS ******");
				log.info("|strvvcd : " + strvvcd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + struserid);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR DN VOS ====");

				List<ChargeableBillValueObject> ChargeableBillArrayList = new ArrayList<ChargeableBillValueObject>();
				log.info("**** Process Log created for DN to not overwrite business type EVM******");

				// inserts the chargeable events into the event log

				this.logGBEvent(vesselTxnEventLogValueObject, GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);

				ChargeableBillArrayList = this.executeBillCharges(vesselTxnEventLogValueObject,
						GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);
				log.info("**** END Process Log created for DN to not overwrite business type EVM******");
				int i = ChargeableBillArrayList.size();
				log.info("ChargeableBillArrayList : " + i);
				gbmsCabValueObject.setStatus("TRUE");

				// MCC log additional event for EDO for WF and SC during 1st DN creation
				List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList2 = new ArrayList<GeneralEventLogValueObject>();
				if (GeneralEventLogEDOArrayList.size() > 0) {

					for (int cnt = 0; cnt < GeneralEventLogEDOArrayList.size(); cnt++) {
						GeneralEventLogValueObject generalEventLogValueObject = (GeneralEventLogValueObject) GeneralEventLogEDOArrayList
								.get(cnt);
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
						generalEventLogValueObject.setIsFirstDNForEDO("Y");
						generalEventLogValueObject.setTotalPackDn(0);
						GeneralEventLogEDOArrayList2.add(generalEventLogValueObject);
					}

					List<ChargeableBillValueObject> chargeableEDOBillArrayList = new ArrayList<ChargeableBillValueObject>();
					log.info("**** Process Log created for EDO to not overwrite business type******");

					// inserts the chargeable events into the event log
					this.logGBEvent(vesselTxnEventLogValueObject, GeneralEventLogArrayList,
							ProcessChargeConst.REF_IND_EDO);

					chargeableEDOBillArrayList = this.executeBillCharges(vesselTxnEventLogValueObject,
							GeneralEventLogEDOArrayList2, ProcessChargeConst.REF_IND_EDO);
					log.info("**** END Process Log created for EDO to not overwrite business type******");
					int edoBillCount = chargeableEDOBillArrayList.size();
					log.info("EDO bill ChargeableBillArrayList : " + edoBillCount);
				}

			}
		} catch (BusinessException e) {
			log.info("Exception cabDnExecuteBillCharges :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cabDnExecuteBillCharges :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cabDnExecuteBillCharges SERVICE Result:"
					+ (gbmsCabValueObject != null ? gbmsCabValueObject.toString() : ""));
		}
		return gbmsCabValueObject;
	}

	@Transactional(rollbackFor = BusinessException.class)
	private List<ChargeableBillValueObject> executeBillCharges(
			VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList2, String refId) throws BusinessException {
		try {
			return processGBLogRepo.executeBillCharges(vesselTxnEventLogValueObject, GeneralEventLogEDOArrayList2, refId);
		} catch (BusinessException e) {
			log.info("Exception executeBillCharges :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception executeBillCharges :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: executeBillCharges SERVICE Result:");					
		}
	}

	@Transactional(rollbackFor = BusinessException.class)
	private void logGBEvent(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> GeneralEventLogArrayList, String refId) throws Exception {
		gbEvntLogRepos.logGBEvent(vesselTxnEventLogValueObject, GeneralEventLogArrayList, refId);
	}

	private String triggerShutoutCargoDNExecuteBillCharges(Map<String, Object> parameters) throws BusinessException {
		GbmsCabValueObject gbmsCabValueObject = null;
		List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList = new ArrayList<GeneralEventLogValueObject>();
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		try {
			log.info("START: cabDnExecuteBillCharges DAO dnnbr: " + parameters.toString());
			String billWharfInd = (String) parameters.get("billWharfInd");
			String billSvcChargeInd = (String) parameters.get("billSvcChargeInd");
			String billStoreInd = (String) parameters.get("billStoreInd");
			String billProcessInd = (String) parameters.get("billProcessInd");
			String vvCd = (String) parameters.get("vvCd");
			String userId = (String) parameters.get("userId");

			Object listObj = parameters.get("GeneralEventLogEDOArrayList");
			if (listObj instanceof List) {
				for (int j = 0; j < ((List<?>) listObj).size(); j++) {
					Object item = ((List<?>) listObj).get(j);
					if (item instanceof Object) {
						GeneralEventLogEDOArrayList.add((GeneralEventLogValueObject) item);
					}
				}
			}

			listObj = parameters.get("GeneralEventLogArrayList");
			if (listObj instanceof List) {
				for (int j = 0; j < ((List<?>) listObj).size(); j++) {
					Object item = ((List<?>) listObj).get(j);
					if (item instanceof Object) {
						GeneralEventLogArrayList.add((GeneralEventLogValueObject) item);
					}
				}
			}

			gbmsCabValueObject = (GbmsCabValueObject) parameters.get("gbmsCabValueObject");

			Timestamp txnDttm = transLoggerRepo.getSystemDate();

			if ("Y".equalsIgnoreCase(billStoreInd) || "Y".equalsIgnoreCase(billWharfInd)) {
				VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();

				vesselTxnEventLogValueObject.setVvCd(vvCd);
				vesselTxnEventLogValueObject.setTxnDttm(txnDttm);
				vesselTxnEventLogValueObject.setBillWharfInd(billWharfInd);
				vesselTxnEventLogValueObject.setBillSvcChargeInd(billSvcChargeInd);
				vesselTxnEventLogValueObject.setBillStoreInd(billStoreInd);
				vesselTxnEventLogValueObject.setBillProcessInd(billProcessInd);
				vesselTxnEventLogValueObject.setLastModifyUserId(userId);
				vesselTxnEventLogValueObject.setLastModifyDttm(txnDttm);
				log.info("**** VESSEL TXN EVENT LOG FOR DN VOS ******");
				log.info("|strvvcd : " + vvCd);
				log.info("|txnDttm : " + txnDttm);
				log.info("|billWharfInd : " + billWharfInd);
				log.info("|billSvcChargeInd : " + billSvcChargeInd);
				log.info("|billStoreInd : " + billStoreInd);
				log.info("|billProcessInd : " + billProcessInd);
				log.info("|struserid : " + userId);
				log.info("|txnDttm : " + txnDttm);
				log.info("==== END OF VESSEL TXN EVENT LOG FOR DN VOS ====");

				List<ChargeableBillValueObject> ChargeableBillArrayList = new ArrayList<ChargeableBillValueObject>();
				log.info("**** Process Log created for DN after not overwrite business type******");
				// New EVM ENhancement Sripriya 2nd April 2018 to not overwrite the business
				// type

				// inserts the chargeable events into the event log
				this.logGBEvent(vesselTxnEventLogValueObject, GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);

				ChargeableBillArrayList = this.executeBillCharges(vesselTxnEventLogValueObject,
						GeneralEventLogArrayList, ProcessChargeConst.REF_IND_DN);
				log.info("**** END Process Log created for DN after not overwrite business type******"
						+ ChargeableBillArrayList.size());
				int i = ChargeableBillArrayList.size();
				log.info("ChargeableBillArrayList : " + i);

				// MCC log additional event for shutout EDO for WF during 1st DN creation
				List<GeneralEventLogValueObject> GeneralEventLogEDOArrayList2 = new ArrayList<GeneralEventLogValueObject>();

				if (GeneralEventLogEDOArrayList.size() > 0) {

					for (int cnt = 0; cnt < GeneralEventLogEDOArrayList.size(); cnt++) {

						GeneralEventLogValueObject generalEventLogValueObject = (GeneralEventLogValueObject) GeneralEventLogEDOArrayList
								.get(cnt);
						generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
						generalEventLogValueObject.setIsFirstDNForEDO("Y");
						GeneralEventLogEDOArrayList2.add(generalEventLogValueObject);
					}

					List<ChargeableBillValueObject> chargeableEDOBillArrayList = new ArrayList<ChargeableBillValueObject>();
					log.info("**** Process Log created for shutout EDO after not overwritting business type******");
					// New EVM ENhancement Sripriya 2nd April 2018 to not overwrite the business
					// type

					// inserts the chargeable events into the event log
					this.logGBEvent(vesselTxnEventLogValueObject, GeneralEventLogEDOArrayList2,
							ProcessChargeConst.REF_IND_EDO);

					chargeableEDOBillArrayList = this.executeBillCharges(vesselTxnEventLogValueObject,
							GeneralEventLogEDOArrayList2, ProcessChargeConst.REF_IND_EDO);
					log.info("**** END Process Log created for shutout EDO after not overwritting business type******"
							+ chargeableEDOBillArrayList.size());
					int edoBillCount = chargeableEDOBillArrayList.size();
					log.info("EDO bill ChargeableBillArrayList after not overwritting business type: " + edoBillCount);

				}

				gbmsCabValueObject.setStatus("TRUE");

			}

		} catch (BusinessException e) {
			log.info("Exception cabDnExecuteBillCharges :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cabDnExecuteBillCharges :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cabDnExecuteBillCharges SERVICE Result:"
					+ (gbmsCabValueObject != null ? gbmsCabValueObject.toString() : ""));
		}

		return gbmsCabValueObject.getStatus();
	}
}

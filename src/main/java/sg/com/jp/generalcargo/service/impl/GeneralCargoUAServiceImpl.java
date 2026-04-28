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
import sg.com.jp.generalcargo.dao.DnRepo;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepository;
import sg.com.jp.generalcargo.dao.UAOpsRepository;
import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GcOpsUaReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.service.GeneralCargoUAService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service
public class GeneralCargoUAServiceImpl implements GeneralCargoUAService {
	
	private static final Log log = LogFactory.getLog(GeneralCargoUAServiceImpl.class);

	@Autowired
	private UARepository uARepo;
	
	@Autowired
	private UAOpsRepository uaOpsRepo;

	@Autowired
	private TransactionLoggerRepository transactionLoggerRepo;

	@Autowired
	private DnRepo dnRepo;

	@Autowired
	private ProcessGBLogRepository processGBLogRepo;

	@Autowired
	private CashSalesRepository cashSalesRepo;

	@Override
	public boolean chkVslStat(String esnNo) throws BusinessException {

		return uARepo.chkVslStat(esnNo);
	}

	@Override
	public boolean chkESNStatus(String esnNo) throws BusinessException {

		return uARepo.chkESNStatus(esnNo);
	}

	@Override
	public boolean chkESNPkgs(String esnNo, String transtype) throws BusinessException {

		return uARepo.chkESNPkgs(esnNo, transtype);
	}

	@Override
	public List<UaEsnDetValueObject> getCreateUADisp(String esnNo, String transtype) throws BusinessException {

		return uARepo.getCreateUADisp(esnNo, transtype);
	}

	@Override
	public String getUANbr(String esnNo) throws BusinessException {

		return uARepo.getUANbr(esnNo);
	}

	@Override
	public String getVcd(String esnNo) throws BusinessException {

		return uARepo.getVcd(esnNo);
	}

	@Override
	public String TriggerUa(String uanbr, String userID, String vvcd) throws BusinessException {

		return transactionLoggerRepo.TriggerUa(uanbr, userID, vvcd);
	}

	@Override
	public String getSysdate() throws BusinessException {

		return uARepo.getSysdate();
	}

	@Override
	public String createUA(String esnNo, String transtype, String esn_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String uA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2) throws BusinessException {

		return uARepo.createUA(esnNo, transtype, esn_Nbr_Pkgs, nomWt, nomVol, date_time, uA_Nbr_Pkgs, nric_no, ictype,
				dpname, veh1, veh2);
	}

	@Override
	public boolean isTESN_JP_JP(String esnNo) throws BusinessException {

		return uaOpsRepo.isTESN_JP_JP(esnNo);
	}

	@Override
	public boolean isClosedShipment(String bkRef) throws BusinessException {

		return uaOpsRepo.isClosedShipment(bkRef);
	}

	@Override
	public boolean checkBKCreatedAfterSHPReopen(String esnNo) throws BusinessException {

		return uaOpsRepo.checkBKCreatedAfterSHPReopen(esnNo);
	}

	@Override
	public List<String[]> getCntrNbr(String esnNo) throws BusinessException {

		return uaOpsRepo.getCntrNbr(esnNo);
	}

	@Override
	public boolean checkESNCntr(String esnNo) throws BusinessException {

		return uARepo.checkESNCntr(esnNo);
	}

	@Override
	public boolean checkEsnStuffIndicator(String esnNo) throws BusinessException {

		return uaOpsRepo.checkEsnStuffIndicator(esnNo);
	}

	@Override
	public String getCustCdByIcNbr(String ictype, String nric_no) throws BusinessException {

		return uaOpsRepo.getCustCdByIcNbr(ictype, nric_no);
	}

	@Override
	public boolean isValidVehicleNumber(String veh1, String coCd) throws BusinessException {

		return dnRepo.isValidVehicleNumber(veh1, coCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateUA(String uanbr, String cntrNo) throws BusinessException {

		uaOpsRepo.updateUA(uanbr, cntrNo);
	}

	@Override
	public int checkFirstUA(String esnNo, String cntrNo) throws BusinessException {

		return uaOpsRepo.checkFirstUA(esnNo, cntrNo);
	}

	@Override
	public String getNewCatCd(String cntrSeq) throws BusinessException {

		return uaOpsRepo.getNewCatCd(cntrSeq);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateStdWeigth(String cntrSeq, String cntrNo, String userID, String newCatCd)
			throws BusinessException {

		uaOpsRepo.updateStdWeigth(cntrSeq, cntrNo, userID, newCatCd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException {

		uARepo.updFtrans(esnNo, transtype, ftransdtm);
	}

	@Override
	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException {

		return uARepo.getEsnList(esn_asn_nbr);
	}

	@Override
	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException {

		return uARepo.getEsnView(esnNo, transtype);
	}

	@Override
	public List<UaListObject> getUAList(String esnNo) throws BusinessException {

		return uaOpsRepo.getUAList(esnNo);
	}

	@Override
	public List<UaEsnListValueObject> getTransferredCargo(String s2) throws BusinessException {

		return uARepo.getTransferredCargo(s2);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateVehicleNo(String uanbr, String new_vehicleNo) throws BusinessException {

		uaOpsRepo.updateVehicleNo(uanbr, new_vehicleNo);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void purgetemptableUA(String uanbr) throws BusinessException {
		uARepo.purgetemptableUA(uanbr);
	}

	@Override
	public boolean checkVehicleExist(String uanbr) throws BusinessException {

		return uaOpsRepo.checkVehicleExist(uanbr);
	}

	@Override
	public List<UaEsnDetValueObject> getUAViewPrint(String UANbr, String esnasnnbr, String transtype)
			throws BusinessException {

		return uaOpsRepo.getUAViewPrint(UANbr, esnasnnbr, transtype);
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String refNo, String refInd) throws BusinessException {

		return processGBLogRepo.getGBBillCharge(refNo, refInd);
	}

	@Override
	public String insertTempUAPrintOut(String UANbr, String esnasnnbr, String transtype) throws BusinessException {

		return uARepo.insertTempUAPrintOut(UANbr, esnasnnbr, transtype);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {

		return uaOpsRepo.updateCntrStatus(cntrSeq, userID);
	}

	@Override
	public boolean countUABalance(String cntrNbr) throws BusinessException {

		return uaOpsRepo.countUABalance(cntrNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void cancel1stUa(String cntrSeq, String cntrNbr, String userID) throws BusinessException {

		uaOpsRepo.cancel1stUa(cntrSeq, cntrNbr, userID);
	}

	@Override
	public String getUaCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {

		return uaOpsRepo.getUaCntrFirst(cntrSeq, cntrNbr);
	}

	@Override
	public boolean isUABefCloseShp(String vvcode, String uaCreateDttm) throws BusinessException {

		return uaOpsRepo.isUABefCloseShp(vvcode, uaCreateDttm);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {

		uaOpsRepo.changeStatusCntr(cntrSeq, user, newCatCode);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void cancelUA(String uaNbr, String esnAsnNbr, String transType, String userId, String uaNbrPkgs)
			throws BusinessException {

		uARepo.cancelUA(uaNbr, esnAsnNbr, transType, userId, uaNbrPkgs);
	}

	@Override
	public String getCntrSeq(String cntrNo) throws BusinessException {

		return uaOpsRepo.getCntrSeq(cntrNo);
	}

	@Override
	public boolean isAsnShut(String esnasnnbr) throws BusinessException {

		return uARepo.isAsnShut(esnasnnbr);
	}

	@Override
	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException {

		return processGBLogRepo.cancelBillableCharges(refNo, refInd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertTempBill(String dnNbr, String tarCdSer, String tarDescSer, double billTonSer, double urateSer,
			double totChrgamtSer, String actNbrSer, String tarCdwf, String tardescwf, double billTonswf, double uratewf,
			double totChrgamtwf, String actNbrwf, String tarCdSr, String tarDescSr, double billTonsSr, double urateSr,
			double totChrgamtSr, String actNbrSr, String UserID, String edoActNbr, String tarCdSr1, String tardeScSr1,
			double billTonsSr1, double urateSr1, double totChrgAmtSr1, String actNbrSr1, String tarCdSr2,
			String tarDescSr2, double billTonsSr2, double urateSr2, double totChrgAmtSr2, String actNbrSr2,
			double tunitSer, double tunitWhf, double tunitSr, double tunitStore, double tunitSerWhf)
			throws BusinessException {

		return uARepo.insertTempBill(dnNbr, tarCdSer, tarDescSer, billTonSer, urateSer, totChrgamtSer, actNbrSer,
				tarCdwf, tardescwf, billTonswf, uratewf, totChrgamtwf, actNbrwf, tarCdSr, tarDescSr, billTonsSr,
				urateSr, totChrgamtSr, actNbrSr, UserID, edoActNbr, tarCdSr1, tardeScSr1, billTonsSr1, urateSr1,
				totChrgAmtSr1, actNbrSr1, tarCdSr2, tarDescSr2, billTonsSr2, urateSr2, totChrgAmtSr2, actNbrSr2,
				tunitSer, tunitWhf, tunitSr, tunitStore, tunitSerWhf);
	}

	@Override
	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException {

		return cashSalesRepo.getCashSales(dnList);
	}

	@Override
	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {

		return cashSalesRepo.getCashSales(refNbr);
	}

	@Override
	public String getMachineID(String recNbr) throws BusinessException {

		return cashSalesRepo.getMachineID(recNbr);
	}

	@Override
	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException {

		return cashSalesRepo.getCashSalesPaymentCode(cashsalesType);
	}

	@Override
	public String getNETSRefID(String receiptNo) throws BusinessException {

		return cashSalesRepo.getNETSRefID(receiptNo);
	}

	@Override
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters, String nbr, List<?> records)
			throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" jasperName:"+CommonUtility.deNull(jasperName) +" parameters:"+ parameters
					+" nbr:"+ CommonUtility.deNull(nbr) +" records:"+records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, jasperName, nbr, records);
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
	}

	@Override
	public List<GcOpsUaReport> getUAPrintJasper(String uaNbr) throws BusinessException {
		return uaOpsRepo.getUAPrintJasper(uaNbr);
	}

	@Override
	public boolean checkCancelUA(String uanbr) throws BusinessException {
		return uaOpsRepo.checkCancelUA(uanbr);
	}

	@Override
	public boolean hasVesselSailed(String vvCd) throws BusinessException {
		return uaOpsRepo.hasVesselSailed(vvCd);
	}
}

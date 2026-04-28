package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepository;
import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.UACntrJasperReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.service.UAService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service("UAService")
public class UAServiceImpl implements UAService {


	private static final Log log = LogFactory.getLog(UAServiceImpl.class);
	
	@Autowired
	private UARepository uARepo;
	
	@Autowired
	private ProcessGBLogRepository processGBLogRepo;

	@Autowired
	private TransactionLoggerRepository transactionLoggerRepo;

	@Override
	public boolean chkVslStat(String esnNo) throws BusinessException {

		return uARepo.chkVslStat(esnNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cancelBillableCharges(String uanbr, String string) throws BusinessException {

		return processGBLogRepo.cancelBillableCharges(uanbr, string);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void cancelUA(String uanbr, String esnasnnbr, String transtype, String userid, String UA_Nbr_Pkgs)
			throws BusinessException {

		uARepo.cancelUA(uanbr, esnasnnbr, transtype, userid, UA_Nbr_Pkgs);
	}

	@Override
	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException {

		return uARepo.getEsnView(esnNo, transtype);
	}

	@Override
	public List<UaListObject> getUAList(String esnNo) throws BusinessException {

		return uARepo.getUAList(esnNo);
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
	@Transactional(rollbackFor = BusinessException.class)
	public String createUA(String esnNo, String transtype, String esn_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String uA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2, String veh3, String veh4, String veh5, String userID, String strCntrNum, String strUnStuffDt)
			throws BusinessException {

		return uARepo.createUA(esnNo, transtype, esn_Nbr_Pkgs, nomWt, nomVol, date_time, uA_Nbr_Pkgs, nric_no, ictype,
				dpname, veh1, veh2, veh3, veh4, veh5, userID, strCntrNum, strUnStuffDt);
	}

	@Override
	public String getVcd(String esnNo) throws BusinessException {

		return uARepo.getVcd(esnNo);
	}

	@Override 
	@Transactional(rollbackFor = BusinessException.class)
	public String TriggerUa(String uanbr, String userID, String vvcd) throws BusinessException {

		return transactionLoggerRepo.TriggerUa(uanbr, userID, vvcd);
	}

	@Override
	public String getSysdate() throws BusinessException {

		return uARepo.getSysdate();
	}

	@Override
	public TableResult getEsnList(String esn_asn_nbr,Criteria criteria) throws BusinessException {
		return uARepo.getEsnList(esn_asn_nbr, criteria);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException {
		uARepo.updFtrans(esnNo, transtype, ftransdtm);
	}

	@Override
	public List<UaEsnDetValueObject> getUAViewPrint(String uanbr, String esnNo, String transtype)
			throws BusinessException {
		return uARepo.getUAViewPrint(uanbr, esnNo, transtype);
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String uanbr, String refInd) throws BusinessException {
		return processGBLogRepo.getGBBillCharge(uanbr, refInd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void purgetemptableUA(String uanbr) throws BusinessException {
		uARepo.purgetemptableUA(uanbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertTempUAPrintOut(String uanbr, String esnNo, String transtype) throws BusinessException {
		return uARepo.insertTempUAPrintOut(uanbr, esnNo, transtype);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String userID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {
		return uARepo.insertTempBill(uanbr, tarcdser, tardescser, billtonsser, urateser, totchrgamtser, actnbrser,
				tarcdwf, tardescwf, billtonswf, uratewf, totchrgamtwf, actnbrwf, tarcdsr, tardescsr, billtonssr,
				uratesr, totchrgamtsr, actnbrsr, userID, esnactnbr, tarcdsr1, tardescsr1, billtonssr1, uratesr1,
				totchrgamtsr1, actnbrsr1, tarcdsr2, tardescsr2, billtonssr2, uratesr2, totchrgamtsr2, actnbrsr2,
				tunitser, tunitwhf, tunitsr, tunitstore, tunitserwhf);
	}


	@Override
	public List<UACntrJasperReport> getUaCntrJasperContent(String uaNbr) throws BusinessException {
		return uARepo.getUaCntrJasperContent(uaNbr);
	}

	@Override
	public JasperPrint getJasperPrint(String reportFilename, Map<String, Object> parameters, String uaNbr,
			List<UACntrJasperReport> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" reportFilename:"+CommonUtility.deNull(reportFilename) +" parameters:"+ parameters
					+" uaNbr:"+ CommonUtility.deNull(uaNbr) +" records:"+records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, reportFilename, uaNbr, records);
			
			log.info("END: *** getJasperPrint Result *****" + jasperPrint.toString());
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
	}

	@Override
	public String getPdfFileName(ReportValueObject rvo, String uaNbr) throws BusinessException {
		return uARepo.getPdfFileName(rvo, uaNbr);
	}

}

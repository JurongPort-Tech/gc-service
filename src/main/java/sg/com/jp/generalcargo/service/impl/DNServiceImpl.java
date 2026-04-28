package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.DnRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepository;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.DNCntrJasperReport;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.service.DNService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service("dnService")
public class DNServiceImpl implements DNService {

	private static final Log log = LogFactory.getLog(DNServiceImpl.class);

	@Autowired
	DnRepository dnRepo;

	@Autowired
	TransactionLoggerRepository transactionLoggerRepo;

	@Autowired
	ProcessGBLogRepository processGBLogRepo;

	@Autowired
	ProcessGBLogRepository processGBLogRepository;

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String veh2, String veh3,
			String veh4, String veh5, String userid, String icType, String searchcrg, String tesn_nbr, String cntrNum,
			String stuffDt) throws BusinessException {

		return dnRepo.createDN(edoNbr, transtype, edo_Nbr_Pkgs, nomWt, nomVol, date_time, transQty, nric_no, dpname,
				veh1, veh2, veh3, veh4, veh5, userid, icType, searchcrg, tesn_nbr, cntrNum, stuffDt);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean chkraiseCharge(String edoNbr) throws BusinessException {
		return dnRepo.chkraiseCharge(edoNbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String TriggerDN(String dnNbr, String userid) throws BusinessException {
		return transactionLoggerRepo.TriggerDN(dnNbr, userid);
	}

	
	@Override
	public List<EdoValueObjectContainerised> fetchDNDetail(String dnNbr, String transtype, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchDNDetail(dnNbr, transtype, searchcrg, tesn_nbr);
	}

	@Override
	public List<EdoValueObjectContainerised> getVechDetails(String dnNbr) throws BusinessException {
		return dnRepo.getVechDetails(dnNbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public List<EdoValueObjectContainerised> fetchDNCreateDetail(String deNull, String deNull2, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.fetchDNCreateDetail(deNull, deNull2, searchcrg, tesn_nbr);
	}

	@Override
	public List<ChargeableBillValueObject> getGBBillCharge(String deNull, String string) throws BusinessException {
		return processGBLogRepo.getGBBillCharge(deNull, string);

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void purgetemptableDN(String deNull) throws BusinessException {
		dnRepo.purgetemptableDN(deNull);

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertTempDNPrintOut(String DNNbr, String transtype, String searchcrg, String esnasnnbr)
			throws BusinessException {
		return dnRepo.insertTempDNPrintOut(DNNbr, transtype, searchcrg, esnasnnbr);

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertTempBill(String dnnbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String login, String edo_act_nbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {
		return dnRepo.insertTempBill(dnnbr, tarcdser, tardescser, billtonsser, urateser, totchrgamtser, actnbrser,
				tarcdwf, tardescwf, billtonswf, uratewf, totchrgamtwf, actnbrwf, tarcdsr, tardescsr, billtonssr,
				uratesr, totchrgamtsr, actnbrsr, login, edo_act_nbr, tarcdsr1, tardescsr1, billtonssr1, uratesr1,
				totchrgamtsr1, actnbrsr1, tarcdsr2, tardescsr2, billtonssr2, uratesr2, totchrgamtsr2, actnbrsr2,
				tunitser, tunitwhf, tunitsr, tunitstore, tunitserwhf);

	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cancelBillableCharges(String dnNbr, String dn) throws BusinessException {
		return processGBLogRepository.cancelBillableCharges(dnNbr, dn);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String cancelDN(String edo, String dnnbr, String login, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnRepo.cancelDN(edo, dnnbr, login, transType, searchcrg, tesn_nbr);
	}

	@Override
	public List<EdoValueObjectContainerised> fetchEdoDetails(String edo, String searchcrg, String tesn_nbr)
			throws BusinessException {

		return dnRepo.fetchEdoDetails(edo, searchcrg, tesn_nbr);
	}

	@Override
	public List<EdoValueObjectContainerised> fetchDNList(String edo, String searchcrg, String tesn_nbr) throws BusinessException {

		return dnRepo.fetchDNList(edo, searchcrg, tesn_nbr);
	}

	@Override
	public String chktesnJpJp_nbr(String tesn_nbr) throws BusinessException {

		return dnRepo.chktesnJpJp_nbr(tesn_nbr);
	}

	@Override
	public String chktesnJpPsa_nbr(String tesn_nbr) throws BusinessException {
		return dnRepo.chktesnJpPsa_nbr(tesn_nbr);
	}

	@Override
	public List<EdoValueObjectContainerised> fetchEdo(String txtedo, String ccode, String searchcrg) throws BusinessException {
		return dnRepo.fetchEdo(txtedo, ccode, searchcrg);
	}

	@Override
	public String chktesnEdo(String txtedo) throws BusinessException {
		return dnRepo.chktesnEdo(txtedo);
	}

	@Override
	public String chkEdoNbr(String txtedo) throws BusinessException {
		return dnRepo.chkEdoNbr(txtedo);
	}

	@Override
	public boolean chktesnJpJp(String txtedo) throws BusinessException {

		return dnRepo.chktesnJpJp(txtedo);
	}

	@Override
	public boolean chktesnJpPsa(String txtedo) throws BusinessException {
		return dnRepo.chktesnJpPsa(txtedo);

	}

	@Override
	public List<DNCntrJasperReport> getDnCntrJasperContent(String dnNbr) throws BusinessException {
		return dnRepo.getDnCntrJasperContent(dnNbr);
	}

	@Override
	public JasperPrint getJasperPrint(String reportFilename, Map<String, Object> parameters, String dnNbr,
			List<DNCntrJasperReport> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" reportFilename:"+CommonUtility.deNull(reportFilename) +" parameters:"+ parameters
					+" dnNbr:"+ CommonUtility.deNull(dnNbr) +" records:"+records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, reportFilename, dnNbr, records);
			
			log.info("END: *** getJasperPrint Result *****" + jasperPrint.toString());
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
	}

	@Override
	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException {
		return dnRepo.getPdfFileName(rvo, dnNbr);
	}

}

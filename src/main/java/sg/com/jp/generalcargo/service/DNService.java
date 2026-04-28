package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.DNCntrJasperReport;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DNService {

	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String veh2, String veh3,
			String veh4, String veh5, String userid, String icType, String searchcrg, String tesn_nbr, String cntrNum,
			String stuffDt) throws BusinessException;

	public boolean chkraiseCharge(String edoNbr) throws BusinessException;

	public String TriggerDN(String dnNbr, String userid) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNDetail(String dnNbr, String transtype, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoValueObjectContainerised> getVechDetails(String dnNbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNCreateDetail(String deNull, String deNull2, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<ChargeableBillValueObject> getGBBillCharge(String deNull, String string) throws BusinessException;

	public void purgetemptableDN(String deNull) throws BusinessException;

	public String insertTempDNPrintOut(String deNull, String deNull2, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public String insertTempBill(String deNull, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String login, String edo_act_nbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException;

	public boolean cancelBillableCharges(String dnNbr, String dn) throws BusinessException;

	public String cancelDN(String edo, String dnnbr, String login, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoValueObjectContainerised> fetchEdoDetails(String edo, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNList(String edo, String searchcrg, String tesn_nbr) throws BusinessException;

	public String chktesnJpJp_nbr(String tesn_nbr) throws BusinessException;

	public String chktesnJpPsa_nbr(String tesn_nbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchEdo(String txtedo, String ccode, String searchcrg) throws BusinessException;

	public String chktesnEdo(String txtedo) throws BusinessException;

	public String chkEdoNbr(String txtedo) throws BusinessException;

	public boolean chktesnJpJp(String txtedo) throws BusinessException;

	public boolean chktesnJpPsa(String txtedo) throws BusinessException;

	public List<DNCntrJasperReport> getDnCntrJasperContent(String dnNbr) throws BusinessException;

	public JasperPrint getJasperPrint(String reportFilename, Map<String, Object> parameters, String dnNbr,
			List<DNCntrJasperReport> records) throws BusinessException, Exception;

	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException;

}

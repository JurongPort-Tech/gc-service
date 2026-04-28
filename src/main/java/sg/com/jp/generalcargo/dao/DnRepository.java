package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.DNCntrJasperReport;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DnRepository {

	public boolean chkraiseCharge(String edonbr) throws BusinessException;

	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String veh2, String veh3,
			String veh4, String veh5, String userid, String icType, String searchcrg, String tesn_nbr,
			String strCntrNum, String strStuffDt) throws BusinessException;

	public String insertTempBill(String dnnbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String edo_act_nbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException;

	public String insertTempDNPrintOut(String DNNbr, String transtype, String searchcrg, String esnasnnbr)
			throws BusinessException;

	public void purgetemptableDN(String dnnbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public List<EdoValueObjectContainerised> getVechDetails(String dnNbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoValueObjectContainerised> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException;

	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException;

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException;

	public String chkEdoNbr(String edoNbr) throws BusinessException;

	public String chktesnEdo(String edoNbr) throws BusinessException;

	public boolean chktesnJpJp(String edoNbr) throws BusinessException;

	public boolean chktesnJpPsa(String edoNbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException;

	public List<DNCntrJasperReport> getDnCntrJasperContent(String dnNbr) throws BusinessException;

	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException;

}

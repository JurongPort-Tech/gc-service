package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.UACntrJasperReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;

//ejb.sessionBeans.gbms.containerised.ua;
public interface UARepository {

	public boolean chkVslStat(String esnasnnbr) throws BusinessException;

	public void cancelUA(String uanbr, String esnasnnbr, String transtype, String userid, String UA_Nbr_Pkgs)
			throws BusinessException;

	public List<UaEsnDetValueObject> getEsnView(String esnasnnbr, String transtype) throws BusinessException;

	public List<UaListObject> getUAList(String esnasnnbr) throws BusinessException;

	public boolean chkESNStatus(String esnasnnbr) throws BusinessException;

	public boolean chkESNPkgs(String esnasnnbr, String transtype) throws BusinessException;

	public List<UaEsnDetValueObject> getCreateUADisp(String esnasnnbr, String transtype) throws BusinessException;

	public String getUANbr(String esnNo) throws BusinessException;

	public String createUA(String esnasnnbr, String transtype, String Esn_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String UA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2, String veh3, String veh4, String veh5, String userid, String strCntrNum, String strUnStuffDt)
			throws BusinessException;

	public String getVcd(String esnNo) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public TableResult getEsnList(String esnasnnbr, Criteria criteria) throws BusinessException;

	public void updFtrans(String esnasnnbr, String transtype, String ftransdate) throws BusinessException;

	public List<UaEsnDetValueObject> getUAViewPrint(String UANbr, String esnasnnbr, String transtype)
			throws BusinessException;

	public void purgetemptableUA(String uanbr) throws BusinessException;

	public String insertTempUAPrintOut(String UANbr, String esnasnnbr, String transtype) throws BusinessException;


	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String userID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException;

	public String checkTransType(String esnNbr) throws BusinessException;

	public List<UaEsnListValueObject> getTransferredCargo(String esn) throws BusinessException;

	public boolean checkESNCntr(String esnasn) throws BusinessException;

	public boolean isAsnShut(String esnasnnbr) throws BusinessException;

	public String createUA(String esnasnnbr, String transtype, String Esn_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String UA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String userid) throws BusinessException;
	
	public List<UACntrJasperReport> getUaCntrJasperContent(String uaNbr) throws BusinessException;

	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException;

	public List<UaEsnListValueObject> getEsnList(String esnasnnbr) throws BusinessException;


}

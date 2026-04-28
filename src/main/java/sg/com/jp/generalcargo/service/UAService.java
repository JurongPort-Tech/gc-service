package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.UACntrJasperReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface UAService {

	public boolean chkVslStat(String esnNo) throws BusinessException;

	public boolean cancelBillableCharges(String uanbr, String string) throws BusinessException;

	public void cancelUA(String uanbr, String esnasnnbr, String transtype, String userid, String UA_Nbr_Pkgs)
			throws BusinessException;

	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException;

	public List<UaListObject> getUAList(String esnNo) throws BusinessException;

	public boolean chkESNStatus(String esnNo) throws BusinessException;

	public boolean chkESNPkgs(String esnNo, String transtype) throws BusinessException;

	public List<UaEsnDetValueObject> getCreateUADisp(String esnNo, String transtype) throws BusinessException;

	public String getUANbr(String esnNo) throws BusinessException;

	public String createUA(String esnNo, String transtype, String esn_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String uA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2, String veh3, String veh4, String veh5, String userID, String strCntrNum, String strUnStuffDt)
			throws BusinessException;

	public String getVcd(String esnNo) throws BusinessException;

	public String TriggerUa(String uanbr, String userID, String vvcd) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException;

	public TableResult getEsnList(String esn_asn_nbr, Criteria criteria) throws BusinessException;

	public List<UaEsnDetValueObject> getUAViewPrint(String uanbr, String esnNo, String transtype)
			throws BusinessException;

	public List<ChargeableBillValueObject> getGBBillCharge(String uanbr, String refInd) throws BusinessException;

	public void purgetemptableUA(String uanbr) throws BusinessException;

	public String insertTempUAPrintOut(String uanbr, String esnNo, String transtype) throws BusinessException;

	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String userID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException;

	
	public List<UACntrJasperReport> getUaCntrJasperContent(String uaNbr) throws BusinessException;

	public JasperPrint getJasperPrint(String reportFilename, Map<String, Object> parameters, String uaNbr,
			List<UACntrJasperReport> records) throws BusinessException, Exception;

	public String getPdfFileName(ReportValueObject rvo, String uaNbr) throws BusinessException;

}

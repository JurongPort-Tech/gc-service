package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.EdoVO;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GcOpsDnReport;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GeneralCargoDnRepository {

	public void updateVehicleNo(String dnNo, String vehicleNo) throws BusinessException;

	public boolean isValidVehicleNumber(String vehicleNumber, String companyCode) throws BusinessException;

	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException;

	public String insertTempDNPrintOut(String strEdoNo, String DNNbr, String transtype, String searchcrg,
			String esnasnnbr) throws BusinessException;

	public void purgetemptableDN(String dnnbr) throws BusinessException;

	public List<String[]> getCntrNbr(String edoasn) throws BusinessException;

	public List<EdoVO> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public boolean checkVehicleExit(String dnnbr) throws BusinessException;

	public List<EdoVO> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public String getCntrNo(String dnNbr) throws BusinessException;

	public List<EdoVO> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public List<EdoVO> getVechDetails(String dnNbr) throws BusinessException;

	public List<EdoVO> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException;

	public int getSpencialPackage(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException;

	public boolean chkEDOStuffing(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException;

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException;

	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException;

	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException;

	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;

	public String getNewCatCd(String cntrSeq) throws BusinessException;

	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException;

	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException;

	public boolean countDNBalance(String cntrNbr) throws BusinessException;

	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException;

	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException;

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	public boolean checkCancelDN(String dnNbr) throws BusinessException;

	public String getCntrSeq(String cntrNo) throws BusinessException;

	public boolean checkESNCntr(String edoasn) throws BusinessException;

	public boolean BchktesnJpJp(String edoNbr) throws BusinessException;

	public boolean chktesnJpPsa(String edoNbr) throws BusinessException;

	public boolean chktesnJpJp(String edoNbr) throws BusinessException;

	public String chkEdoNbr(String edoNbr) throws BusinessException;

	public String chktesnEdo(String edoNbr) throws BusinessException;

	public List<EdoVO> fetchEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException;

	public List<EdoVO> fetchShutoutEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException;

	public int getTotalCustCdByIcNumber(String nricno, String ictype) throws BusinessException;

	public String createShutoutDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException;

	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException;

	public boolean isTESN_JP_JP(String s3, String s19) throws BusinessException;

	public void updateDN(String cntrNo, String s2) throws BusinessException;

	public void updateCntr(String cntrSeq, String cntrNo, String s, String newCatCd) throws BusinessException;

	public boolean chkCntrCrgDn(String s2) throws BusinessException;

	public boolean chkraiseCharge(String s3) throws BusinessException;

	public List<GcOpsDnReport> getDNPrintJasper(String dnNbr) throws BusinessException;

	public int truckerOut(String edoNbr, String veh1) throws BusinessException;

}

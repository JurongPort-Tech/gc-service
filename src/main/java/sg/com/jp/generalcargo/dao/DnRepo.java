package sg.com.jp.generalcargo.dao;

import java.rmi.RemoteException;
import java.util.List;

import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.ShutoutEdoDnReport;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DnRepo {
	
	public boolean chkraiseCharge(String edonbr) throws BusinessException;
	
	public boolean chkCntrCrgDn(String strDnNbr) throws BusinessException;
	
	public void updateCntr(String cntrseq, String cntrNo, String user, String newCatCode)
    		throws  BusinessException;
	
	public void updateDN(String cntrNo, String dnNo) throws BusinessException;
	
	public boolean isTESN_JP_JP(String edoNbr, String esnNbr)throws BusinessException;
	
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs,
			String NomWt, String NomVol, String date_time,
			String transQty, String nric_no, String dpname,
			String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException ;
	
	public String createShutoutDN(String edoNbr, String transtype,
			String edo_Nbr_Pkgs, String NomWt, String NomVol, String date_time,
			String transQty, String nric_no, String dpname, String veh1,
			String userid, String icType, String searchcrg, String tesn_nbr,
			String cargoDes) throws BusinessException;
	
	public int getTotalCustCdByIcNumber(String nric, String type) throws BusinessException;
	
	public boolean checkCancelDN(String dnNbr) throws  BusinessException;
	
	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;
	
	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid)
			throws BusinessException ;
	
	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException;
	
	public boolean countDNBalance(String cntrNbr) throws BusinessException ;
	
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException ;
	
	public int checkFirstDN(String edoNbr, String cntrNo) throws  BusinessException;
	
	public String getNewCatCd(String cntrSeq) throws BusinessException;
	
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;
	
	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException ;
	
	public void updateWeight(String cntrSeq, long weight, String user, String times)
			throws BusinessException ;
	
	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws  BusinessException;
	
	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException ;
	
	public int getSpencialPackage(String edoNbr) throws BusinessException;
	
	public boolean chkEDOStuffing(String edoNbr) throws BusinessException;
	
	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException ;
	
	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException;
	
	public String getCntrSeq(String cntrNo) throws BusinessException;
	
	public boolean isValidVehicleNumber(String vehicleNumber, String companyCode)  throws BusinessException;
	
	public void updateVehicleNo(String dnNo, String vehicleNo)  throws BusinessException;
	
	public List<String []> getCntrNbr(String edoasn) throws BusinessException;
	
	public boolean checkVehicleExit(String dnnbr) throws BusinessException;
	
	public String insertTempBill(String uanbr,String tarcdser,String tardescser,double billtonsser,double urateser,double totchrgamtser,String actnbrser,String tarcdwf,String tardescwf,double billtonswf,
			double uratewf,double totchrgamtwf,String actnbrwf,String tarcdsr,String tardescsr,double billtonssr,double uratesr,double totchrgamtsr,String actnbrsr,String UserID,String esnactnbr,String tarcdsr1,
			String tardescsr1,double billtonssr1,double uratesr1,double totchrgamtsr1,String actnbrsr1,String tarcdsr2,String tardescsr2,double billtonssr2,double uratesr2,double totchrgamtsr2,String actnbrsr2,
			double tunitser,double tunitwhf,double tunitsr,double tunitstore,double tunitserwhf) throws BusinessException;
	
	public String insertTempDNPrintOut(String strEdoNo, String DNNbr, String transtype, String searchcrg,
			String esnasnnbr) throws BusinessException;
	
	public void purgetemptableDN(String dnnbr) throws BusinessException ;
	
	public boolean checkESNCntr(String edoasn) throws BusinessException ;
	
	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException;
	
	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException;
	
	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException;
	
	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException;
	
	public String getCntrNo(String dnNbr) throws BusinessException;
	
	public String getVesselATBDate(String esnNbrR) throws BusinessException ;
	
	public boolean chkEdoStatus(String esnNbrR) throws BusinessException;
	
	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException ;
	
	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;
	
	public boolean chktesnJpJp(String edoNbr) throws BusinessException;
	
	//StartRegion  DnRepository
	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			 throws  BusinessException;
	public List<EdoValueObjectContainerised> fetchEdo(String edoNbr, String compCode, String searchcrg) throws RemoteException, BusinessException;
	
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException;
	
	public String createDNDPE(String edoNbr,String transtype,String edo_Nbr_Pkgs,String NomWt,String NomVol,String date_time,String transQty,String nric_no,String dpname,String veh1,String veh2,String veh3,String veh4,String veh5,String userid,String icType,String searchcrg,String tesn_nbr,String strCntrNum,String strStuffDt, String vehNo, String tesnEsnNbr) throws BusinessException,RemoteException;
	//EndRegion DnRepository

	public List<ShutoutEdoDnReport> getdnReportDetails(String dnNbr) throws BusinessException;

}

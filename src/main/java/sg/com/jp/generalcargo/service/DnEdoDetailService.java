package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DnEdoDetailService {

	// StartRegion DnEdoDetailService
	public boolean checkESNCntr(String edoasn) throws BusinessException;

	public String getCntrSeq(String cntrNo) throws BusinessException;

	public boolean checkCancelDN(String dnNbr) throws BusinessException;

	public void checkAndUpdateFirstDN(String edoAsnNo, String dnRefNo) throws BusinessException;

	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException;

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException;

	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public String checkTransType(String esnNbr) throws BusinessException;

	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException;

	public void cancelUA(String uaNbr, String esnAsnNbr, String transType, String userId, String uaNbrPkgs)
			throws BusinessException;

	public boolean countDNBalance(String cntrNbr) throws BusinessException;

	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException;

	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException;

	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;

	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException;

	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException;

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr) throws BusinessException;

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr) throws BusinessException;

	public boolean chkEDOStuffing(String edoNbr) throws BusinessException;

	public boolean chktesnJpJp(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException;

	public int getSpencialPackage(String edoNbr) throws BusinessException;

	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> vector1) throws BusinessException;

	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException;

	public String getCntrNo(String dnNbr) throws BusinessException;

	public String getMachineID(String recNbr) throws BusinessException;

	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException;

	public String getNETSRefID(String receiptNo) throws BusinessException;

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException;

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException;

	public boolean chkVslStat(String varno) throws BusinessException;

	public String getVslStatus(String varno) throws BusinessException;

	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException;

	public List<AdpValueObject> getAdpList(String edoNbr) throws BusinessException;

	public List<CashSalesValueObject> getCashSalesList() throws BusinessException;
	
	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;
	
	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException;

	void updateWHIndicator(String stredoasnnbr, String strWhInd, String strWhAggrNbr, String strWhRemarks,
			String strFreeStgDays, String struserid) throws BusinessException;

	// EndRegion DnEdoDetailService

}

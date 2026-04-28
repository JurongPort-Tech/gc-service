package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.GcOpsUaReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GeneralCargoUAService {

	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException;

	public String getMachineID(String recNbr) throws BusinessException;

	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException;

	public String getNETSRefID(String receiptNo) throws BusinessException;

	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException;

	public boolean countUABalance(String cntrNbr) throws BusinessException;

	public void cancel1stUa(String cntrSeq, String cntrNbr, String userID) throws BusinessException;

	public String getUaCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	public boolean isUABefCloseShp(String vvcode, String uaCreateDttm) throws BusinessException;

	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;

	public void cancelUA(String uaNbr, String esnAsnNbr, String transType, String userId, String uaNbrPkgs)
			throws BusinessException;

	public String getCntrSeq(String cntrNo) throws BusinessException;

	public boolean isAsnShut(String esnasnnbr) throws BusinessException;

	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException;

	public String insertTempBill(String dnNbr, String tarCdSer, String tarDescSer, double billTonSer, double urateSer,
			double totChrgamtSer, String actNbrSer, String tarCdwf, String tardescwf, double billTonswf, double uratewf,
			double totChrgamtwf, String actNbrwf, String tarCdSr, String tarDescSr, double billTonsSr, double urateSr,
			double totChrgamtSr, String actNbrSr, String UserID, String edoActNbr, String tarCdSr1, String tardeScSr1,
			double billTonsSr1, double urateSr1, double totChrgAmtSr1, String actNbrSr1, String tarCdSr2,
			String tarDescSr2, double billTonsSr2, double urateSr2, double totChrgAmtSr2, String actNbrSr2,
			double tunitSer, double tunitWhf, double tunitSr, double tunitStore, double tunitSerWhf)
			throws BusinessException;

	public String insertTempUAPrintOut(String UANbr, String esnasnnbr, String transtype) throws BusinessException;

	public void purgetemptableUA(String uanbr) throws BusinessException;

	public boolean chkVslStat(String esnNo) throws BusinessException;

	public boolean chkESNStatus(String esnNo) throws BusinessException;

	public boolean chkESNPkgs(String esnNo, String transtype) throws BusinessException;

	public List<UaEsnDetValueObject> getCreateUADisp(String esnNo, String transtype) throws BusinessException;

	public String getUANbr(String esnNo) throws BusinessException;

	public String createUA(String esnNo, String transtype, String esn_Nbr_Pkgs, String nomWt, String nomVol,
			String date_time, String uA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2) throws BusinessException;

	public String getVcd(String esnNo) throws BusinessException;

	public String TriggerUa(String uanbr, String userID, String vvcd) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public boolean isTESN_JP_JP(String esnNo) throws BusinessException;

	public boolean isClosedShipment(String bkRef) throws BusinessException;

	public boolean checkBKCreatedAfterSHPReopen(String esnNo) throws BusinessException;

	public List<String[]> getCntrNbr(String esnNo) throws BusinessException;

	public boolean checkESNCntr(String esnNo) throws BusinessException;

	public boolean checkEsnStuffIndicator(String esnNo) throws BusinessException;

	public String getCustCdByIcNbr(String ictype, String nric_no) throws BusinessException;

	public boolean isValidVehicleNumber(String veh1, String coCd) throws BusinessException;

	public void updateUA(String uanbr, String cntrNo) throws BusinessException;

	public int checkFirstUA(String esnNo, String cntrNo) throws BusinessException;

	public String getNewCatCd(String cntrSeq) throws BusinessException;

	public void updateStdWeigth(String cntrSeq, String cntrNo, String userID, String newCatCd) throws BusinessException;

	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException;

	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException;

	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException;

	public List<UaListObject> getUAList(String esnNo) throws BusinessException;

	public List<UaEsnListValueObject> getTransferredCargo(String s2) throws BusinessException;

	public void updateVehicleNo(String uanbr, String new_vehicleNo) throws BusinessException;

	public boolean checkVehicleExist(String uanbr) throws BusinessException;

	public List<UaEsnDetValueObject> getUAViewPrint(String UANbr, String esnasnnbr, String transtype)
			throws BusinessException;

	public List<ChargeableBillValueObject> getGBBillCharge(String refNo, String refInd) throws BusinessException;
	
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters, String uaNbr, List<?> Record) throws BusinessException, Exception;
	
	public List<GcOpsUaReport> getUAPrintJasper(String uaNbr) throws BusinessException;

	List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException;

	public boolean checkCancelUA(String uanbr) throws BusinessException;

	public boolean hasVesselSailed(String vvCode) throws BusinessException;

}

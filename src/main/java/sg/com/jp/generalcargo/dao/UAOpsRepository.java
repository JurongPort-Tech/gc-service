package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.GcOpsUaReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;


//ejb.sessionBeans.gbms.ops.dnua.ua;
public interface UAOpsRepository {

	List<UaEsnDetValueObject> getUAViewPrint(String uANbr, String esnasnnbr, String transtype) throws BusinessException;

	boolean isTESN_JP_JP(String esnNo) throws BusinessException;

	boolean isClosedShipment(String bkRef) throws BusinessException;

	boolean checkBKCreatedAfterSHPReopen(String esnNo) throws BusinessException;

	List<String[]> getCntrNbr(String esnNo) throws BusinessException;

	boolean checkEsnStuffIndicator(String esnNo) throws BusinessException;

	String getCustCdByIcNbr(String ictype, String nric_no) throws BusinessException;

	void updateUA(String uanbr, String cntrNo) throws BusinessException;

	int checkFirstUA(String esnNo, String cntrNo) throws BusinessException;

	String getNewCatCd(String cntrSeq) throws BusinessException;

	void updateStdWeigth(String cntrSeq, String cntrNo, String userID, String newCatCd) throws BusinessException;

	void updateVehicleNo(String uanbr, String new_vehicleNo) throws BusinessException;

	boolean checkVehicleExist(String uanbr) throws BusinessException;

	String updateCntrStatus(String cntrSeq, String userID) throws BusinessException;

	boolean countUABalance(String cntrNbr) throws BusinessException;

	void cancel1stUa(String cntrSeq, String cntrNbr, String userID) throws BusinessException;

	String getUaCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	boolean isUABefCloseShp(String vvcode, String uaCreateDttm) throws BusinessException;

	void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;

	String getCntrSeq(String cntrNo) throws BusinessException;

	List<GcOpsUaReport> getUAPrintJasper(String uaNbr) throws BusinessException;

	List<UaListObject> getUAList(String esnNo) throws BusinessException;
	
	String getUAtransDttm(String esnasnnbr) throws BusinessException;
	
	void updFtrans(String esno, String transtype, String ftransdate,String userId) throws BusinessException;

	boolean checkCancelUA(String uanbr) throws BusinessException;

	boolean hasVesselSailed(String vvCd) throws BusinessException;
}

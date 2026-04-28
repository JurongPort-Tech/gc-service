package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBEventLogRepository {

	//StartRegion  GBEventLogRepository
	public void cancelGBEventLog(int cntrSeqNo, String refInd, String vvCd, String vvInd) throws BusinessException;
	public void cancelGBEventLog (String refNbr, String refInd) throws BusinessException;

	public GeneralEventLogValueObject[] getGBEventLog(String refNo, String string) throws BusinessException;

	public List<String> executeBillableWarehouseCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> generalEventLogArrayList, String refIndEdo) throws SQLException, Exception;
	
	public void logGBEvent(VesselTxnEventLogValueObject vesselTxnEventLogValueObject, List<GeneralEventLogValueObject> chargeEventLogList,
			String refInd) throws Exception;

	public void updateProcessedGBEventLog(String refNo, String refInd, String tariffMainCat, String vvCd)
			throws SQLException, NamingException, Exception;
	
	public void logVesselTxnEvent(VesselTxnEventLogValueObject vesselTxnEventLogValueObject) throws Exception;
	//EndRegion GBEventLogRepository

}

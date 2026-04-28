package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ProcessChargeException;

public interface ProcessGBLogRepository {

	// StartRegion ProcessGBLogRepository

	public boolean cancelStuffCharges(int cntrSeqNo, String refInd, String vvCd, String vvInd) throws BusinessException;

	public void executeGBCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> chargeEventLogList, String refInd) throws Exception;

	public void checkAndUpdateFirstDN(String edoAsnNo, String dnRefNo) throws BusinessException;

	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException;

	public List<ChargeableBillValueObject> getGBBillCharge(String refNo, String refInd) throws BusinessException;

	public boolean isFirstDNForEDO(String dn_nbr) throws BusinessException;

	public boolean checkAnySR(GeneralEventLogValueObject generalEventLogValueObject, int fsdays) throws Exception;

	public List<ChargeableBillValueObject> executeBillCharges(VesselTxnEventLogValueObject vesselTxnEventLogValueObject,
			List<GeneralEventLogValueObject> generalEventLogArrayList, String refIndDn)
			throws NamingException, SQLException, ProcessChargeException, Exception;

	public String determineRefNbr(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws Exception;

	public boolean checkAnyStoreRent(GeneralEventLogValueObject generalEventLogValueObject, int freeStoreDays)
			throws Exception;

	/**
	 * Retrieves a vessel call record
	 * 
	 * @param vvCd - Vessel Voyage Code
	 * @return VesselRelatedValueObject - Vessel related value object that stores
	 *         the generic vessel information
	 * @throws BusinessException
	 */
	VesselRelatedValueObject retrieveVesselCallDtl(String vvCd) throws BusinessException;

	public void insertBillError(BillErrorVO billErrorValueObject) throws BusinessException;
	// EndRegion ProcessGBLogRepository

}

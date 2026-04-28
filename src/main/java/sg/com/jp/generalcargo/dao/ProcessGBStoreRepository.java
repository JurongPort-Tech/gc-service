package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ProcessChargeException;

public interface ProcessGBStoreRepository {

	//StartRegion  ProcessGBStoreRepository
	
	public List<ChargeableBillValueObject> calculateStoreBillCharge(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws NamingException, SQLException, ProcessChargeException, Exception;
	
	public double determineTimeUnit(GeneralEventLogValueObject generalEventLogValueObject, String refInd) throws BusinessException;

	public List<ChargeableBillValueObject> calSRBillCharge(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws Exception;

	public List<ChargeableBillValueObject> determineStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon, double deriveTimePeriod, int fsp)
			throws ProcessChargeException, BusinessException;

	public double computeBillTon(double billTonEdo, int totalPkgEdo, int totalPkgDn) throws BusinessException;

	
	//EndRegion ProcessGBStoreRepository

}

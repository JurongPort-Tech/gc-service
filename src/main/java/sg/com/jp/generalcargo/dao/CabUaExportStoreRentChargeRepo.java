package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CabUaExportStoreRentChargeRepo {

	public List<GeneralEventLogValueObject> processDetails(String strcode, String struserid) throws BusinessException;
}

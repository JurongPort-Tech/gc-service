package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface GBMSTriggerIndRepository {
	
	public void updateStuffUnstuffInd(String vvCd, String refNo, String refInd, String userId, String stuffInd,
			String localLeg, String otherRefNo) throws BusinessException;

	public void updateWarehouseEDOInd(String edoAsnNbr, String lastModifyUserId, String billWharfInd,
			String billSvcChargeInd) throws BusinessException;
	
	void updateGBMSInd(String string, String refNbr, String refInd, String lastModifyUserId, String billWharfInd,
			String billSvcChargeInd, String billStoreInd) throws Exception;
}

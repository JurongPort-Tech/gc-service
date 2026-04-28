package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface AdminFeeWaiverRepo {

	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID)
			throws BusinessException;

	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException;

	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd) throws BusinessException;

}

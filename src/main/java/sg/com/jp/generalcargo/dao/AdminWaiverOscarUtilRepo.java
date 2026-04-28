package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface AdminWaiverOscarUtilRepo {

	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException;

}

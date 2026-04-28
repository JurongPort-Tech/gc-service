package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CCEventLogRepo {

	public String[] getCatCodeNStatus(int cntrSeqNbr) throws BusinessException;

	public void insertOpsCCEventLogForLct(CloseLctValueObject vo, String userId) throws BusinessException;

	public boolean insertOpsCCEventLog(String vv_Cd, String triggerType, String UserID) throws BusinessException;

}

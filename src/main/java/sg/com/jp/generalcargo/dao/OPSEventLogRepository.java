package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;

import sg.com.jp.generalcargo.util.BusinessException;

public interface OPSEventLogRepository {

	public boolean insertOpsMiscEventLog(Timestamp txnDttm, String txnCd, String haulCd, String vvCd, String refNbr,
			String lastModifyUserId) throws BusinessException;

}

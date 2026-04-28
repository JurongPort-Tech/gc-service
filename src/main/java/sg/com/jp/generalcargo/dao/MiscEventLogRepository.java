package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;

import sg.com.jp.generalcargo.util.BusinessException;

public interface MiscEventLogRepository {

	public boolean insertMiscEventLog(Long nextMiscSeqNbr, Timestamp txnDttm, String txnCd, String haulCd, String vvCd,
			String billInd, String refNbr, String lastModifyUserId, Timestamp lastModifyDttm, String pdisc1,
			Integer cntrSeqNbr) throws BusinessException;

}

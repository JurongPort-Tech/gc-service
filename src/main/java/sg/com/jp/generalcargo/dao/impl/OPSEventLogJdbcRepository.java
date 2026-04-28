package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.MiscEventLogRepository;
import sg.com.jp.generalcargo.dao.OPSEventLogRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.CreateException;

@Repository("oPSEventLogRepo")
public class OPSEventLogJdbcRepository implements OPSEventLogRepository {

	private static final Log log = LogFactory.getLog(OPSEventLogJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private MiscEventLogRepository miscEventLog;

	// ejb.sessionBeans.ops.EventLog-->OPSEventLogEJB
	public boolean insertOpsMiscEventLog(Timestamp txnDttm, String txnCd, String haulCd, String vvCd, String refNbr,
			String lastModifyUserId) throws BusinessException {
		String pDisc1 = null;
		Integer cntrSeqNbr = null;
		return insertOpsMiscEventLog(txnDttm, txnCd, haulCd, vvCd, refNbr, lastModifyUserId, pDisc1, cntrSeqNbr);

	}

	/**
	 * @param txnDttm
	 * @param txnCd
	 * @param haulCd
	 * @param vvCd
	 * @param refNbr
	 * @param lastModifyUserId
	 * @return
	 * @throws CreateException
	 * @throws FinderException
	 * @throws BusinessException
	 */
	public boolean insertOpsMiscEventLog(Timestamp txnDttm, String txnCd, String haulCd, String vvCd, String refNbr,
			String lastModifyUserId, String pDisc1, Integer cntrSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Timestamp lastModifyDttm = null;
		String billInd = "N";
		String query1 = null;
		long nextValue = 0;
		Long nextMiscSeqNbr = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: insertOpsMiscEventLog  DAO  Start txnDttm " + CommonUtility.deNull(String.valueOf(txnDttm)) + "txnCd" + CommonUtility.deNull(txnCd)
					+ "haulCd" + CommonUtility.deNull(haulCd) + " vvCd" + CommonUtility.deNull(vvCd) + " refNbr" + CommonUtility.deNull(refNbr)
					+ " lastModifyUserId " + CommonUtility.deNull(lastModifyUserId) + " pDisc1" + CommonUtility.deNull(pDisc1)
					+ " cntrSeqNbr" + CommonUtility.deNull(String.valueOf(cntrSeqNbr)));
			query1 = "SELECT MISC_EVENT_LOG_SEQ_NBR.nextVal FROM DUAL ";
			log.info(" insertOpsMiscEventLog  DAO  SQL " + query1 + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(query1, paramMap);
			if (rs.next()) {
				nextValue = rs.getLong("NEXTVAL");
				nextMiscSeqNbr = new Long(nextValue);
			}
			lastModifyDttm = new Timestamp(System.currentTimeMillis());
			miscEventLog.insertMiscEventLog(nextMiscSeqNbr, txnDttm, txnCd, haulCd, vvCd, billInd, refNbr,
					lastModifyUserId, lastModifyDttm, pDisc1, cntrSeqNbr);
			// END - Code added for CR - CR-CAB-20050518-1 by Robert D, 09-June-2006

		} catch (BusinessException e) {
			log.info("Exception insertOpsMiscEventLog : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception insertOpsMiscEventLog : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertOpsMiscEventLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertOpsMiscEventLog  DAO  END");
		}
		return true;
	}

}

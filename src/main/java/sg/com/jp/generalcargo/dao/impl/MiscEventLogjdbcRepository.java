package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.MiscEventLogRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("miscEventLogRepository")
public class MiscEventLogjdbcRepository implements MiscEventLogRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static final Log log = LogFactory.getLog(MiscEventLogjdbcRepository.class);
	// ejb.sessionBeans.cab.MiscEventLog-->MiscEventLogEJB

	public boolean insertMiscEventLog(Long nextMiscSeqNbr, Timestamp txnDttm, String txnCd, String haulCd, String vvCd,
			String billInd, String refNbr, String lastModifyUserId, Timestamp lastModifyDttm, String pdisc1,
			Integer cntrSeqNbr) throws BusinessException {
		StringBuffer query = new StringBuffer();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		String msgInfo = "InsertMiscEventLog:- NextMiscSeqNbr= " + CommonUtility.deNull(String.valueOf(nextMiscSeqNbr)) + "; TxnDttm= " + CommonUtility.deNull(String.valueOf(txnDttm))
				+ "; TxnCd= " + CommonUtility.deNull(txnCd) + "; HaulCd= " + CommonUtility.deNull(haulCd) + "; VVCd= " + CommonUtility.deNull(vvCd) + "; BillInd= " + CommonUtility.deNull(billInd)
				+ "; RefNbr= " + refNbr + "; LastModifyUserId= " + lastModifyUserId + "; LastModifyDttm= " + CommonUtility.deNull(String.valueOf(lastModifyDttm))
				+ " ; PDISC1= " + CommonUtility.deNull(pdisc1) + " ; Container Sequence Number : " + CommonUtility.deNull(String.valueOf(cntrSeqNbr));

		boolean result = true;
		try {
			log.info("START: insertMiscEventLog  DAO  Start " + msgInfo);

			query.setLength(0);
			query.append("INSERT /* MiscEventLogEJB - insertMiscEventLog() */ ");
			query.append("INTO MISC_EVENT_LOG  ");
			query.append("(MISC_SEQ_NBR, TXN_DTTM, TXN_CD, HAUL_CD, VV_CD, BILL_IND, REF_NBR, ");
			query.append(" LAST_MODIFY_USER_ID,  LAST_MODIFY_DTTM, PDISC1, CNTR_SEQ_NBR ) VALUES  ");
			query.append("( :nextMiscSeqNbr, :txnDttm, :txnCd, :haulCd, :vvCd, :billInd, :refNbr, ");
			query.append(":lastModifyUserId, :lastModifyDttm, :pdisc1, :cntrSeqNbr ) ");

			paramMap.put("nextMiscSeqNbr", (nextMiscSeqNbr).longValue());
			paramMap.put("txnDttm", txnDttm);
			paramMap.put("txnCd", txnCd);
			paramMap.put("haulCd", haulCd);
			paramMap.put("vvCd", vvCd);
			paramMap.put("billInd", billInd);
			paramMap.put("refNbr", refNbr);
			paramMap.put("lastModifyUserId", lastModifyUserId);
			paramMap.put("lastModifyDttm", lastModifyDttm);
			paramMap.put("pdisc1", pdisc1);
			if (cntrSeqNbr == null) {
				paramMap.put("cntrSeqNbr", null);
			} else {
				paramMap.put("cntrSeqNbr", cntrSeqNbr.intValue());
			}
			log.info(" insertMiscEventLog  DAO  SQL " + query.toString() + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(query.toString(), paramMap);
			if (count == 0) {
				throw new BusinessException("M4201");
			}
			log.info(msgInfo + " Inserted to MISC_EVENT_LOG");
			
			log.info("END: *** insertMiscEventLog Result *****" + result);
		} catch (BusinessException e) {
			log.info("Exception insertMiscEventLog : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception insertMiscEventLog : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertMiscEventLog : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertMiscEventLog  DAO  END");
		}
		return result;
	}

}

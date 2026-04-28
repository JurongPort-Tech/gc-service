package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CommonOpsRepo;
import sg.com.jp.generalcargo.dao.ExceptionAlertRepository;
import sg.com.jp.generalcargo.dao.LateArrivalRepo;
import sg.com.jp.generalcargo.domain.GbArrivalWaiver;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("LateArrivalRepo")
public class LateArrivalJdbcRepo implements LateArrivalRepo {
	private static final Log log = LogFactory.getLog(LateArrivalJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ExceptionAlertRepository exceptionAlertRepo;

	@Autowired
	private CommonOpsRepo commonOpsRepo;

	@Value("${jp.lateArrivalWaiverSubmission.emailSubject}")
	String EMAIL_SUBJECT_LATE_ARRIVAL_WAIVER_SUBMISSION;
	
	// ejb.sessionBeans.gbms.ops.latearrv -->LateArrivalEjb
	@Override
	public void sendSubmissionAlert(String vvCd, String[] approverEmail) throws BusinessException {
		try {
			log.info("START: sendSubmissionAlert  DAO vvCd:" + vvCd + ",approverEmail:" + approverEmail.toString());
			String message = prepareAlertMessage(vvCd);

			// send alert to data officer
			exceptionAlertRepo.sendFlexiAlert(ConstantUtil.ALERT_CODE_LATE_ARRIVAL_WAIVER, message, message,
					EMAIL_SUBJECT_LATE_ARRIVAL_WAIVER_SUBMISSION, "", "");

			// send alert to approving officer
			// need to fix email alert.
			commonOpsRepo.sendAlert(approverEmail, EMAIL_SUBJECT_LATE_ARRIVAL_WAIVER_SUBMISSION, message);
		} catch (Exception e) {
			log.error("Exception: sendSubmissionAlert ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sendSubmissionAlert  DAO  END");
		}
	}

	private String prepareAlertMessage(String vvCd) throws BusinessException {
		StringBuffer message = new StringBuffer();
		StringBuffer messageHeaderSql = new StringBuffer();
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		try {
			log.info("START: prepareAlertMessage  DAO vvCd:" + vvCd);

			GbArrivalWaiver gbArrivalWaiver = retrieveGbArrivalWaiver(vvCd);

			messageHeaderSql.append(" SELECT ('Vessel/Voyage: ' || VSL_NM || ' / ' || OUT_VOY_NBR || CHR(13) ||");
			messageHeaderSql.append(" 'ATB: ' || TO_CHAR(ATB_DTTM,'DD/MM/YYYY HH24MI') || CHR(13) ||");
			messageHeaderSql.append(
					" 'ATU: ' || TO_CHAR((SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD=V.VV_CD),'DD/MM/YYYY HH24MI')) || CHR(13) ||");
			messageHeaderSql.append(" 'Reason: ' || WAIVER_NM || ' (' || GB_ARRIVAL_WAIVER_REASON || ')' VSL_INFO");
			messageHeaderSql.append(" FROM VESSEL_CALL V, BERTHING B, WAIVER_CODE W");
			messageHeaderSql.append(" WHERE V.VV_CD=:vvCd");
			messageHeaderSql.append(" AND V.VV_CD=B.VV_CD");
			messageHeaderSql.append(" AND V.GB_ARRIVAL_WAIVER_CD=W.WAIVER_CD");
			messageHeaderSql.append(" AND B.SHIFT_IND=1");

			String status = "";
			if (gbArrivalWaiver.getGbArrivalWaiverInd() == null || gbArrivalWaiver.getGbArrivalWaiverInd().equals("")) {

			} else if (gbArrivalWaiver.getGbArrivalWaiverInd().equals(ConstantUtil.GB_ARRIVAL_WAIVER_IND_PENDING)) {
				status = ConstantUtil.GB_ARRIVAL_WAIVER_IND_STATUS_PENDING;
			} else if (gbArrivalWaiver.getGbArrivalWaiverInd().equals(ConstantUtil.GB_ARRIVAL_WAIVER_IND_APPROVED)) {
				status = ConstantUtil.GB_ARRIVAL_WAIVER_IND_STATUS_APPROVED;
			} else if (gbArrivalWaiver.getGbArrivalWaiverInd().equals(ConstantUtil.GB_ARRIVAL_WAIVER_IND_REJECTED)) {
				status = ConstantUtil.GB_ARRIVAL_WAIVER_IND_STATUS_REJECTED;
			}

			message = new StringBuffer(commonOpsRepo.prepareMessageHeader(messageHeaderSql.toString(), vvCd));
			message.append("\n" + "Status: " + status);
			message.append("\n" + "Late Arrival Charges: $ "
					+ decimalFormat.format(gbArrivalWaiver.getGbArrivalWaiverAmount()));
		} catch (Exception e) {
			log.error("Exception: prepareAlertMessage ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: prepareAlertMessage DAO Result:" + message.toString());
		}
		return message.toString();
	}

	@Override
	public String[] retrieveApproverEmail(double gbArrivalWaiverAmount) throws BusinessException {
		String[] approverEmail = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {
			log.info("START: retrieveApproverEmail  DAO  Start Obj " + " gbArrivalWaiverAmount:"
					+ gbArrivalWaiverAmount);

			sql.append(" SELECT ACCT.LOGIN_ID AS LOGIN_ID, ACCT.EMAIL_ADDR AS EMAIL_ADDR");
			sql.append("   FROM LOGON_ACCT ACCT, LATE_ARRV_APPV_USER APPV_USER");
			sql.append("  WHERE ACCT.LOGIN_ID = APPV_USER.USER_ID");
			sql.append("    AND APPV_USER.GROUP_ID IN (");
			sql.append("     SELECT GROUP_ID");
			sql.append("       FROM LATE_ARRV_APPV_GRP");
			sql.append("      WHERE MIN_AMOUNT <= :gbArrivalWaiverAmount");
			sql.append("        AND :gbArrivalWaiverAmount <= MAX_AMOUNT");
			sql.append("        AND STATUS = 'A'");
			sql.append("   )");
			sql.append("    AND APPV_USER.STATUS = 'A'");

			paramMap.put("gbArrivalWaiverAmount", gbArrivalWaiverAmount);
			log.info(" *** retrieveApproverEmail SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			SqlRowSet resultSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			List<String> approverEmailList = new ArrayList<String>();
			while (resultSet.next()) {
				approverEmailList.add(resultSet.getString("EMAIL_ADDR"));
			}
			approverEmail = (String[]) approverEmailList.toArray(new String[0]);

			log.info("END: *** retrieveApproverEmail Result *****" + approverEmailList.toString());
		} catch (NullPointerException e) {
			log.error("Exception: retrieveApproverEmail ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveApproverEmail ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveApproverEmail  DAO  END");
		}
		return approverEmail;
	}

	@Override
	public void updateGbArrivalWaiver(String vvCd, String gbArrivalWaiverInd, BigDecimal gbArrivalWaiverAmount,
			String lastModifyUserId) throws BusinessException {
		int count = 0;
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			log.info("START: updateGbArrivalWaiver  DAO  Start Obj " + " vvCd:" + vvCd + " gbArrivalWaiverInd:"
					+ gbArrivalWaiverInd + " gbArrivalWaiverAmount:" + gbArrivalWaiverAmount + " lastModifyUserId:"
					+ lastModifyUserId);

			sql.append(" UPDATE VESSEL_CALL");
			sql.append("    SET ");
			if (gbArrivalWaiverInd != null && !gbArrivalWaiverInd.equals("")) {
				sql.append("        GB_ARRIVAL_WAIVER_IND = :gbArrivalWaiverInd,");
			}
			if (gbArrivalWaiverAmount != null) {
				sql.append("        GB_ARRIVAL_WAIVER_AMOUNT = :gbArrivalWaiverAmount,");
			}
			sql.append("        LAST_MODIFY_USER_ID = :lastModifyUserId,");
			sql.append("        LAST_MODIFY_DTTM = SYSDATE");
			sql.append("  WHERE VV_CD = :vvCd");

			if (gbArrivalWaiverInd != null && !gbArrivalWaiverInd.equals("")) {
				paramMap.put("gbArrivalWaiverInd", gbArrivalWaiverInd);
			}
			if (gbArrivalWaiverAmount != null) {
				paramMap.put("gbArrivalWaiverAmount", gbArrivalWaiverAmount.doubleValue());
			}
			paramMap.put("lastModifyUserId", lastModifyUserId);
			paramMap.put("vvCd", vvCd);

			log.info(" *** updateGbArrivalWaiver SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		} catch (NullPointerException e) {
			log.error("Exception: updateGbArrivalWaiver ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateGbArrivalWaiver ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateGbArrivalWaiver  DAO Result:" + count);
		}
	}

	@Override
	public BigDecimal calculateGbArrivalWaiverAmount(String vvCd) throws BusinessException {
		BigDecimal gbArrivalWaiverAmount = new BigDecimal(0);
		SqlRowSet resultSet = null;
		double lateArrivalMinute = 0.0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {
			log.info("START: calculateGbArrivalWaiverAmount  DAO vvCd:" + vvCd);

			sql.append(" SELECT ((ATB_DTTM -  ETB_DTTM) * 24 * 60) AS LATE_ARRIVAL_MINUTE");
			sql.append("   FROM VESSEL_CALL V, BERTHING B");
			sql.append("  WHERE V.VV_CD = :vvCd");
			sql.append("    AND V.VV_CD = B.VV_CD");
			sql.append("    AND B.SHIFT_IND = 1");

			paramMap.put("vvCd", vvCd);

			log.info(" *** calculateGbArrivalWaiverAmount SQL *****" + sql.toString() + " paramMap "
					+ paramMap.toString());
			resultSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (resultSet.next()) {
				lateArrivalMinute = resultSet.getDouble("LATE_ARRIVAL_MINUTE");
			}

			// first 15 minutes grace period waive
			if (lateArrivalMinute > 15.0) {
				// subsequent 15 minutes charge $ 100
				gbArrivalWaiverAmount = new BigDecimal(100);

				// after first 15 minutes, charge $ 35 per 5 minutes
				if (lateArrivalMinute > 30.0) {
					int noOfFiveMinute = (int) Math.ceil((lateArrivalMinute - 30.0) / 5.0);

					gbArrivalWaiverAmount = gbArrivalWaiverAmount.add(new BigDecimal(noOfFiveMinute * 35));
				}
			}
			log.info("END: *** calculateGbArrivalWaiverAmount Result *****" + gbArrivalWaiverAmount.toString());
		} catch (NullPointerException e) {
			log.error("Exception: calculateGbArrivalWaiverAmount ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: calculateGbArrivalWaiverAmount ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: calculateGbArrivalWaiverAmount  DAO  END");
		}
		return gbArrivalWaiverAmount;
	}

	@Override
	public GbArrivalWaiver retrieveGbArrivalWaiver(String vvCd) throws BusinessException {
		GbArrivalWaiver gbArrivalWaiver = new GbArrivalWaiver();
		StringBuffer sql = new StringBuffer();
		SqlRowSet resultSet = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: retrieveGbArrivalWaiver  DAO vvCd:" + vvCd);

			sql.append(" SELECT GB_ARRIVAL_WAIVER_IND, GB_ARRIVAL_WAIVER_AMOUNT");
			sql.append("   FROM VESSEL_CALL");
			sql.append("  WHERE VV_CD = :vvCd ");

			paramMap.put("vvCd", vvCd);

			log.info(" *** retrieveGbArrivalWaiver SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			resultSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (resultSet.next()) {
				gbArrivalWaiver.setGbArrivalWaiverInd(resultSet.getString("GB_ARRIVAL_WAIVER_IND"));
				gbArrivalWaiver
						.setGbArrivalWaiverAmount(new BigDecimal(resultSet.getDouble("GB_ARRIVAL_WAIVER_AMOUNT")));
			}

			log.info("END: *** retrieveGbArrivalWaiver Result *****" + gbArrivalWaiver.toString());
		} catch (NullPointerException e) {
			log.error("Exception: retrieveGbArrivalWaiver ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveGbArrivalWaiver ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveGbArrivalWaiver  DAO  END");
		}
		return gbArrivalWaiver;
	}

}

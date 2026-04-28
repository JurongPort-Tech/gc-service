package sg.com.jp.generalcargo.dao.impl;

import java.io.File;
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

import sg.com.jp.generalcargo.dao.ExceptionAlertRepository;
import sg.com.jp.generalcargo.dao.MessengerRepository;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.ExceptionAlertValueObject;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constants;

@Repository("exceptionAlertRepository")
public class ExceptionAlertJdbcRepository implements ExceptionAlertRepository {

	private static final Log log = LogFactory.getLog(ExceptionAlertJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private MessengerRepository messengerRepo;

	@Value("${jp.common.notificationProperties.Alert.Sender}")
	String alert_Sender;

	/// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB -->sendFlexiAlert
	@Override
	public void sendFlexiAlert(String alertCode, String smsMessage, String emailMessage, String emailSubject,
			String emailSender, String emailAttachmentFilename) throws BusinessException {
		ExceptionAlertValueObject alertVO;
		boolean send = false;
		try {
			log.info("START: sendFlexiAlert - alertCode: " + CommonUtility.deNull(alertCode) + ", smsMessage: "
					+ CommonUtility.deNull(smsMessage) + ", emailMessage: " + CommonUtility.deNull(emailMessage)
					+ ", emailSubject: " + CommonUtility.deNull(emailSubject) + ", emailSender: "
					+ CommonUtility.deNull(emailSender) + CommonUtility.deNull(emailAttachmentFilename));
			List<ExceptionAlertValueObject> vector = getExceptionAlertInfo(alertCode);
			if (vector == null || vector.size() < 1)
				return; // nothing to send

			List<String> emailArr = new ArrayList<String>();
			List<String> smsArr = new ArrayList<String>();

			// process all recipients into email or sms recipients
			for (int i = 0; i < vector.size(); i++) {
				alertVO = (ExceptionAlertValueObject) vector.get(i);

				if (alertVO.getDeliveryMode().equals(ExceptionAlertValueObject.DELIVERY_MODE_EML)) {
					// by email
					emailArr.add(alertVO.getAccount());
				} else if (alertVO.getDeliveryMode().equals(ExceptionAlertValueObject.DELIVERY_MODE_SMS)) {
					// by sms or pager
					smsArr.add(alertVO.getAccount());
				}
			}

			// send email if any
			if (emailArr.size() > 0) {
				EmailValueObject emailVO = new EmailValueObject();
				emailVO.setMessage(emailMessage);
				if ("".equals(emailSender) || emailSender.equals(null)) {
					String sender = alert_Sender;
					emailVO.setSenderAddress(sender);
				} else {
					emailVO.setSenderAddress(emailSender);
				}
				if ("".equals(emailSubject) || emailSubject.equals(null))
					emailVO.setSubject(getAlertDesc(alertCode) + " alert");
				else
					emailVO.setSubject(emailSubject);
				if (emailAttachmentFilename != null && !"".equals(emailAttachmentFilename)) {
					String fileName = "";
					String reportroot = Constants.ExceptionAlert_ReportRoot;
					fileName = reportroot + emailAttachmentFilename;

					File ne = new File(fileName);
					if (!ne.exists()) {
						log.info(fileName + " does not exist in the system");
					}
					emailVO.addAttachment(ne.getName(), ne.getParent());
				}
				emailVO.setRecipientAddress((String[]) emailArr.toArray(new String[0]));

				send = messengerRepo.sendMessage(emailVO);
			}

			if (smsArr.size() > 0) {
				Sms smsVO = new Sms();
				smsVO.setMessage(smsMessage);
				smsVO.setToList(smsArr);
				send = messengerRepo.sendMessage(smsVO);
			}

		} catch (BusinessException e) {
			log.info("exception: sendFlexiAlert ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception exp) {
			log.info("exception: sendFlexiAlert ", exp);
		} finally {
			log.info("END: sendFlexiAlert Result:" + send);
		}
	}

	private List<ExceptionAlertValueObject> getExceptionAlertInfo(String alertCode) throws BusinessException {
		return getExceptionAlertInfo(alertCode, null);
	}

	// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB -->getAlertDesc
	private String getAlertDesc(String alertCode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getAlertDesc  DAO  Start alertCode: " + alertCode);

			if (alertCode == null || alertCode.equals("")) {
				return "";
			}

			String sql = "SELECT MISC_TYPE_NM FROM MISC_TYPE_CODE WHERE CAT_CD='ALERT_CODE' AND MISC_TYPE_CD=:alertCode";
			paramMap.put("alertCode", alertCode);
			log.info(" *** getAlertDesc SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				return rs.getString("MISC_TYPE_NM");
			}
			log.info("END: *** getAlertDesc Result *****" + alertCode.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getAlertDesc ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getAlertDesc ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getAlertDesc  DAO  END");
		}
		return alertCode;
	}

	// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB
	// -->getExceptionAlertInfo
	private List<ExceptionAlertValueObject> getExceptionAlertInfo(String alertCode, String deliveryMode)
			throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer queryString = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<ExceptionAlertValueObject> vector = new ArrayList<ExceptionAlertValueObject>();
		try {
			log.info("START: getExceptionAlertInfo  DAO  Start Obj " + " alertCode:" + alertCode + " deliveryMode:"
					+ deliveryMode);

			if (alertCode == null || alertCode.equals("")) {
				// no alert code given!
				log.error("getExceptionAlertInfo called with invalid alertCode");
				return vector;
			}

			queryString.append("select name, account, delivery_mode from exception_alert where alert_code=:alertCode");
			queryString.append(" and rec_status='A'");
			if (deliveryMode != null && !deliveryMode.equals("")) {
				queryString.append(" and delivery_mode =:deliveryMode");

			}

			String sql = queryString.toString();

			paramMap.put("alertCode", alertCode);
			if (deliveryMode != null && !deliveryMode.equals(""))
				paramMap.put("deliveryMode", deliveryMode);

			log.info(" *** getExceptionAlertInfo SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				ExceptionAlertValueObject exceptionAlertValueObj = new ExceptionAlertValueObject();

				exceptionAlertValueObj.setName(rs.getString("name"));
				exceptionAlertValueObj.setAccount(rs.getString("account"));
				exceptionAlertValueObj.setDeliveryMode(rs.getString("delivery_mode"));
				exceptionAlertValueObj.setAlertCode(alertCode);

				vector.add(exceptionAlertValueObj);
			}
			log.info("END: *** getExceptionAlertInfo Result *****" + vector.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getExceptionAlertInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getExceptionAlertInfo ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getExceptionAlertInfo  DAO  END");
		}
		return vector;
	}
}

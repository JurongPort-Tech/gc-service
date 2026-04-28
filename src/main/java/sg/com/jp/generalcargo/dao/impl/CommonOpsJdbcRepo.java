package sg.com.jp.generalcargo.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CommonOpsRepo;
import sg.com.jp.generalcargo.dao.MessengerRepository;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("commonOpsRepo")
public class CommonOpsJdbcRepo implements CommonOpsRepo {

	private static final Log log = LogFactory.getLog(CommonOpsJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private MessengerRepository messengerRepo;

	@Value("${jp.common.notificationProperties.Alert.Sender}")
	String alertSender;

	// ejb.sessionBeans.ops.Common-->CommonOpsEJB-->prepareMessageHeader()
	@Override
	public String prepareMessageHeader(String sql, String vv_cd) throws BusinessException {
		String messageHeader = "";
		SqlRowSet resultSet = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: prepareMessageHeader  DAO  Start sql: " + sql + ",vvcd:" + vv_cd);
			paramMap.put("vvCd", vv_cd);
			
			resultSet = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (resultSet.next()) {
				messageHeader = resultSet.getString("VSL_INFO");
			}
		} catch (NullPointerException e) {
			log.error("Exception: prepareMessageHeader ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: prepareMessageHeader ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: prepareMessageHeader  DAO  Result:" + messageHeader);
		}
		return messageHeader;
	}

	@Override
	public void sendAlert(String[] emailAddress, String emailSubject, String emailMessage) throws BusinessException {
		EmailValueObject emailValueObject = new EmailValueObject();
		boolean send = false;
		try {
			log.info("START sendAlert DAO emailAddress: " + Arrays.toString(emailAddress) + " emailSubject: "
					+ emailSubject + " emailMessage: " + emailMessage);
			emailValueObject.setSenderAddress(alertSender);
			emailValueObject.setRecipientAddress(emailAddress);
			emailValueObject.setSubject(emailSubject);
			emailValueObject.setMessage(emailMessage);

			send = messengerRepo.sendMessage(emailValueObject);
		} catch (BusinessException e) {
			log.info("Exception: sendAlert :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception: sendAlert :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: sendAlert :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sendAlert  DAO  Result:" + send);
		}

	}

}

package sg.com.jp.generalcargo.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.MessengerRepository;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.MessageEventLogVO;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;

@Repository("MessengerRepository")
public class MessengerJdbcRepository implements MessengerRepository {
	private static final Log log = LogFactory.getLog(MessengerJdbcRepository.class);

	@Value("${jp.common.notificationProperties.emailEndpoint}")
	String commonServiceUrl;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// package: ejb.sessionBeans.messenger-->MessengerEJB
	// method: sendMessage()
	@Override
	public boolean sendMessage(Object mVO) throws BusinessException {
		boolean send = false;
		try {
			log.info("START: sendMessage  DAO mVO: " + mVO.toString());
			if (mVO instanceof EmailValueObject) {
				EmailValueObject emailVO = (EmailValueObject) mVO;
				Email emailObj = new Email();
				emailObj.setFrom(emailVO.getSenderAddress());
				emailObj.setFromName(emailVO.getSenderAddress());
				emailObj.setToList(Arrays.asList(emailVO.getRecipientAddress()));
				emailObj.setSubject(emailVO.getSubject());
				emailObj.setContent(emailVO.getMessage());
				emailObj.setEmailSvcUrl(commonServiceUrl);
				if(emailVO.getMessage().charAt(0)== '<') {
					emailObj.setContentType("text/html");
				}else {
					emailObj.setContentType("text/plain");
				}
				log.info("***emailObj*******" + emailObj.toString());
				send = CommonUtil.sendEmail(emailObj, "");
			} else if (mVO instanceof Sms) {
				Sms smsVO = (Sms) mVO;
				send = CommonUtil.sendSMS(smsVO);
			}
		} catch (NullPointerException e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sendMessage  DAO Result:" + send);
		}
		return send;
	}

	// ejb.sessionBeans.messenger --> MessengerEJB -->logMessage
	@Override
	public boolean logMessage(MessageEventLogVO mVO) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer query = new StringBuffer();
		int update = 0;
		MessageEventLogVO eventVO = mVO;
		try {
			log.info("START: logMessage  DAO mVO: " + mVO.toString());

			query.append("INSERT into TOPS.FA_ALERT_SENT(ALERT_SEQ_NBR,TXN_DTTM,REG_SEQ_NBR,ADDR_BOOK_SEQ_NBR,");
			query.append("ALERT_TYPE,ALERT_CATEGORY,HP_NBR,EMAIL_ADDR, ");
			query.append("ALERT_NM,ALERT_CONTENT,CUST_CD,FILE_NM,ALERT_CD) ");
			query.append("VALUES(:Alert_seq_nbr,sysdate,:Reg_seq_nbr,:Addr_book_seq_nbr, ");
			query.append(
					":Alert_type,:Alert_category,:Hp_nbr,:Email_addr,:Alert_nm,:Alert_content,:Cust_cd,:File_nm,:Alert_cd)  ");

			log.info(" *** logMessage SQL *****" + query.toString());
			paramMap.put("Alert_seq_nbr", eventVO.getAlert_seq_nbr());
			paramMap.put("Reg_seq_nbr", eventVO.getReg_seq_nbr());
			paramMap.put("Addr_book_seq_nbr", eventVO.getAddr_book_seq_nbr());
			paramMap.put("Alert_type", eventVO.getAlert_type());
			paramMap.put("Alert_category", eventVO.getAlert_category());
			paramMap.put("Hp_nbr", eventVO.getHp_nbr());
			paramMap.put("Email_addr", eventVO.getEmail_addr());
			paramMap.put("Alert_nm", eventVO.getAlert_nm());
			paramMap.put("Alert_content", eventVO.getAlert_content());
			paramMap.put("Cust_cd", eventVO.getCust_cd());
			paramMap.put("File_nm", eventVO.getFile_nm());
			paramMap.put("Alert_cd", eventVO.getAlert_cd());

			log.info(" *** logMessage SQL *****" + query.toString() + " paramMap " + paramMap.toString());
			update = namedParameterJdbcTemplate.update(query.toString(), paramMap);

			log.info("END: *** logMessage Result *****" + update);
		} catch (NullPointerException e) {
			log.info("Exception: logMessage ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: logMessage ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: logMessage  DAO  END");
		}
		return update > 0 ? true : false;
	}
}

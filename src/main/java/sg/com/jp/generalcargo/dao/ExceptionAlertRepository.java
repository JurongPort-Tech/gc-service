package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface ExceptionAlertRepository {

	public void sendFlexiAlert(String alertCode, String smsMessage, String emailMessage, String emailSubject,
			String emailSender, String emailAttachmentFilename) throws BusinessException;
}

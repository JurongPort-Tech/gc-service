package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.MessageEventLogVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface MessengerRepository {

	public boolean sendMessage(Object mVO) throws BusinessException;

	public boolean logMessage(MessageEventLogVO mVO) throws BusinessException;
}

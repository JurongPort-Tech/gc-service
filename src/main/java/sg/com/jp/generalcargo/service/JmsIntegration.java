package sg.com.jp.generalcargo.service;

import sg.com.jp.generalcargo.domain.JMSMsgValueObject;

public interface JmsIntegration {
	public void triggerNormalQueue(JMSMsgValueObject Message);
	public void triggerPriorityQueue(JMSMsgValueObject Message);
	public void triggerProcessQueue(JMSMsgValueObject Message);
}

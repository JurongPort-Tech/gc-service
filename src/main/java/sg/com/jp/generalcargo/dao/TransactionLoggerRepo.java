package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;
import java.util.Map;

import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.GbmsCabValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TransactionLoggerRepo {
	
	public String TriggerDN(GbmsCabValueObject gbmsCabValueObject) throws BusinessException;

	public String TriggerUa(String strcode, String struserid, String vvcd) throws BusinessException;

	public Timestamp getTimestamp(Object o, String s) throws BusinessException;

	public double computeBillTon(double billTonEdo, int totalPkgEdo, int totalPkgDn) throws BusinessException;
	
	public void TriggerLct(CloseLctValueObject vo, String userId) throws BusinessException;

	public boolean isShutoutCargoDN(String dnnbr) throws BusinessException;

	public Map<String, Object> triggerShutoutCargoDN(String dnnbr, String struserid) throws BusinessException;

	public Map<String, Object> CabDN(String dnnbr, String struserid) throws BusinessException;

	public Timestamp getSystemDate() throws BusinessException;

}

package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface TransactionLoggerRepository {

	public String triggerShutoutCargoDN(String dn_nbr, String userId) throws BusinessException;

	public String TriggerDN(String dnnbr, String struserid) throws BusinessException;

	public double computeBillTon(double billTonEdo, int totalPkgEdo, int totalPkgDn) throws BusinessException;

	public String TriggerUa (String strcode, String struserid, String vvcd) throws BusinessException;
}

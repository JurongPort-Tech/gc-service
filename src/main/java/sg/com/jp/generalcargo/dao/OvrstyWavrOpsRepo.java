package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.BillingCodesVO;
import sg.com.jp.generalcargo.domain.OverStayDockageValueObject;
import sg.com.jp.generalcargo.domain.WaiverCodesVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OvrstyWavrOpsRepo {

	public List<BillingCodesVO> getBillingReasons(BillingCodesVO valueObject) throws BusinessException;

	public List<WaiverCodesVO> getWaiverCodes(WaiverCodesVO valueObject) throws BusinessException;

	public boolean hasAccesstoOSD(String userId) throws BusinessException;

	public OverStayDockageValueObject getWaiverStatus(String vvcd) throws BusinessException;
}

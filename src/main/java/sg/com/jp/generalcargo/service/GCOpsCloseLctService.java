package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GCOpsCloseLctService {

	public void insertOpsCCEventLogForLct(CloseLctValueObject vo, String userId) throws BusinessException;

	public void closeLct(String vv_cds, String userId) throws BusinessException;

	public List<CloseLctValueObject> listLct(String vv_cds) throws BusinessException;

	public void openLct(String vv_cds, String userId) throws BusinessException;

	public List<CloseLctValueObject> listVessel() throws BusinessException;

	public TableResult listLct(String vslName, String inVoNo, String outVoNo, int searchMode,Criteria criteria) throws BusinessException;

	public void TriggerLct(CloseLctValueObject vo, String userId) throws Exception;

}

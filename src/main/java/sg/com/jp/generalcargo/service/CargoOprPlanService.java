package sg.com.jp.generalcargo.service;

import java.util.Date;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoOprPlanService {

	public GbccCargoOprPlanVO cc_getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public boolean cc_persistCargoOprPlan(GbccCargoOprPlanVO transientObject) throws BusinessException;

	public TableResult cc_getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData)
			throws BusinessException;

	public TableResult cc_getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData,
			String ETBFrom, String ETBTo, String listAllChk) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;
}

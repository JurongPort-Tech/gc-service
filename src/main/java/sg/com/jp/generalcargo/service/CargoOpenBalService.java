package sg.com.jp.generalcargo.service;

import java.util.Date;
import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoOpenBalService {

	public List<MiscTypeCode> cc_getMiscTypeCodeDelayReason() throws BusinessException;

	public GbccCargoOpenBalVO cc_getCargoOpenBalById(String vvCd, String stevCd) throws BusinessException;

	public GbccCargoOprPlanVO cc_getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public boolean cc_persistCargoOpenBal(GbccCargoOpenBalVO transientObject) throws BusinessException;

	public List<GbccCargoOpenBalVO> cc_getCargoOpenBal(String CustCode, String sortBy, Criteria criteria, Boolean needAllData)
			throws BusinessException;

	public List<GbccCargoOpenBalVO> cc_getCargoOpenBal(String CustCode, String sortBy, Criteria criteria, Boolean needAllData,
			String ATBFrom, String ATBTo, String listAllChk) throws BusinessException;

}

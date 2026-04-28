package sg.com.jp.generalcargo.service;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoSummaryService {

	public TableResult getCargoSummaryList(String strCustCode, Criteria criteria) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;

}

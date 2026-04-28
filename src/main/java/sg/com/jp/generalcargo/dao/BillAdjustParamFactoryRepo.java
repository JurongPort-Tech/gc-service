package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.util.BusinessException;

public interface BillAdjustParamFactoryRepo {

	public BillAdjustParam create(String tariffCode) throws BusinessException;
}

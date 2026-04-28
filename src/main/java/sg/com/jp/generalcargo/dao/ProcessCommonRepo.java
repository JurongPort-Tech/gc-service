package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.ProcessValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ProcessCommonRepo {

	public List<Object> processBill(Object[] cntrEventDtlArray, List<Object> billCollection, String tariffType)
			throws BusinessException;

	public List<ProcessValueObject> sortCntrEventDtl(Object[] cntrEventDtlArray, String tariffType,
			String tariffMainCat) throws BusinessException;
}

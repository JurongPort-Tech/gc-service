package sg.com.jp.generalcargo.dao;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface LWMSCommonUtilRepository {

	//StartRegion  LWMSCommonUtilRepository
	
	public boolean isBusTypeLighterTerminal(String busType) throws BusinessException;
	
	public String getCsTypeByTerminal(String terminalCd) throws BusinessException;
	
	public AccountValueObject retrieveCustAcct(String salesType) throws BusinessException;
	
	public Map<String,Object> getMiscType(String catCd) throws BusinessException;
	
	//EndRegion LWMSCommonUtilRepository

}

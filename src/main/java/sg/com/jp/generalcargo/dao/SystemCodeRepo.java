package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface SystemCodeRepo {
	public String getValue(String paraCd) throws BusinessException;

}

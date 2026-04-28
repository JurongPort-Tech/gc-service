package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnSearchRepository {
	
	 public String tesnSearch(String tesnNo)
		        throws BusinessException;

}

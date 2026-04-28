package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.OutstandingVO;
import sg.com.jp.generalcargo.util.BusinessException;
public interface DpeOutstandingRepository {

	public List<OutstandingVO> listRecords(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters, Criteria criteria, Boolean needAllData) throws BusinessException;
	
	public int countRecords(Map<String, Object> filters) throws BusinessException;
	
}

package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.GroupVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface MaintWaiverRangeRepo {

	public GroupVO getLevelId(String amount) throws BusinessException;
}

package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.BillJobVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ProcessOMCGenericRepo {

	public List<BillJobVO> retriveveOMCJobOrderDetail(long jobOrderId) throws BusinessException;
}

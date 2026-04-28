package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBWareHouseAplnRepository {
	
	public Integer voidWarehouseApplicationWithASNNubmer(String edoNbr, String userId) throws BusinessException ;
	
	public List<GBWareHouseAplnVO> getWarehouseApplicationListByASNNubmer(String edoNbr) throws BusinessException;
	
	public Boolean isExistWarehouseApplicationWithASNNubmer(String edoNbr) throws BusinessException;
	
	public boolean sendMessage(IMessageValueObject mVO);

}

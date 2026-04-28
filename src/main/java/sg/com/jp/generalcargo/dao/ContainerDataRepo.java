package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ContainerDataRepo {

	public ContainerValueObject getActiveContainerByCntrNo(String cntrNo) throws BusinessException;

	public ContainerValueObject getContainerByPrimaryKey(long containerSequenceNo) throws BusinessException;

	public List<ContainerValueObject> getContainerByTLIBatchRef(String batchNo) throws BusinessException;

}

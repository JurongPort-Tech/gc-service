package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ContainerRepository {
	
	public ContainerValueObject getContainerInformation(String cntrNo) throws BusinessException;

	public boolean validateGCStuffIndicatorCntr(String loadVVCd, String cntrNbr) throws BusinessException;
}

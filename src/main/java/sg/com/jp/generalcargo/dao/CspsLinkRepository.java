package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.CspsLinkValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CspsLinkRepository {

	public List<CspsLinkValueObject> getAreaListBasedOnStorageZone(String stgType, String stgZone)
			throws BusinessException;
	
	public List<CspsLinkValueObject> getLocationListBasedOnLocationType(String locType) throws BusinessException;
	

}

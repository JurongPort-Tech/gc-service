package sg.com.jp.generalcargo.dao;

import java.util.Map;

import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ProcessMarRepo {
	
	public Map<String, Object> determineOverStayAndAmount(String vvCode) throws BusinessException;
	
	public VesselRelatedValueObject getVesselInfo(String vvCode) throws BusinessException;

}

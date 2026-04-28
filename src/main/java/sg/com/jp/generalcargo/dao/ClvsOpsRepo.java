package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.OpsValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ClvsOpsRepo {

	public OpsValueObject getVesselInfo(String vvCode, OpsValueObject opsValueObject) throws BusinessException;

	public OpsValueObject getVessels(OpsValueObject opsValueObject) throws BusinessException;
}

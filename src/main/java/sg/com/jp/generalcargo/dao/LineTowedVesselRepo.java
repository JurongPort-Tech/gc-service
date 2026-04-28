package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.LineTowedVesselValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface LineTowedVesselRepo {

	public List<LineTowedVesselValueObject> getDockageList(String vvcd) throws BusinessException;

	public void addDockage(String vvcd, List<LineTowedVesselValueObject> co) throws BusinessException;

	public boolean getDockageStatus(String custCd) throws BusinessException;

}

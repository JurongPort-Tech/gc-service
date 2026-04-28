package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.util.BusinessException;

public interface MaintenanceTpaRepo {

	public List<String> getParkingAreaList() throws BusinessException;

}

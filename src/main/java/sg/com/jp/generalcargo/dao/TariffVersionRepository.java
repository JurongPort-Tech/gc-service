package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;

import sg.com.jp.generalcargo.util.BusinessException;

public interface TariffVersionRepository {

	// StartRegion TariffVersionRepository
	public int getCurrentVersion(Timestamp ts) throws BusinessException;
	// EndRegion TariffVersionRepository

}

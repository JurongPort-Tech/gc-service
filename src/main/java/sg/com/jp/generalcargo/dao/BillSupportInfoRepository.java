package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;

import sg.com.jp.generalcargo.util.BusinessException;

public interface BillSupportInfoRepository {

	//StartRegion  BillSupportInfoRepository
	
	public int[] getIndicator(String tariffCode) throws BusinessException, SQLException;
	
	//EndRegion BillSupportInfoRepository

}

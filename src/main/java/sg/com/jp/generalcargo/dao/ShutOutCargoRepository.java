package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShutOutCargoVo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ShutOutCargoRepository {

	public List<ShutOutCargoVo> getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName,
			String outVoyNbr, String esnEdoNbr, String cargoType, String terminal, String dwellDays)
			throws BusinessException;

	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId,
			String userName, String dateTime, String status) throws BusinessException, SQLException;

	public TableResult getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String cargoType, String terminal, String dwellDays, String custCode, Criteria criteria)
			throws BusinessException;

	public String getUserNameMap(String userId) throws BusinessException;
}

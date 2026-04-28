package sg.com.jp.generalcargo.service;

import java.sql.SQLException;

import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardCargoShutoutReportService {

	public TableResult getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String cargoType, String terminal, String dwellDays, String custCode, Criteria criteria)
			throws BusinessException;

	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId, String userName, String dateTime, String status) throws BusinessException, SQLException;

	public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException;

	public String getUserNameMap(String userId) throws BusinessException;

}

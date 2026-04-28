package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GstCodeValueObject;
import sg.com.jp.generalcargo.domain.ProcessGBValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ProcessChargeException;

public interface ProcessGBGenericRepository {

	// StartRegion ProcessGBGenericRepository

	public List<ProcessGBValueObject> retrieveBillable(List<ChargeableBillValueObject> discChargeableBillList) throws ProcessChargeException, BusinessException;
	
	public Timestamp retrieveGbCurrentTimestamp(String vvCd, String tariffMainCatCd) throws ProcessChargeException, BusinessException;

	public List<GstCodeValueObject> getGstCharge(String gstCode, Timestamp date) throws SQLException, Exception;

	// EndRegion ProcessGBGenericRepository

}

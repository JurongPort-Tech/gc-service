package sg.com.jp.generalcargo.dao;

import java.util.Date;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface VesselProdRepo {

	public String getColNames() throws BusinessException;

	public TableResult getVesselProdReport(Date fromDt, Date toDt, String displayView, String[] category,
			String tonnage, String rainRecord, String vesselType, String dateType, Criteria criteria)
			throws BusinessException;

	public Integer getPaginationRecordsNumber() throws BusinessException;

	public TableResult getBerthUtilisationRpt(java.util.Date dateFrom, java.util.Date dateTo, String cntrTonnParam,
			Criteria criteria) throws BusinessException;

}

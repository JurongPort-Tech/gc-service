package sg.com.jp.generalcargo.service;

import java.util.Date;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GCOpsVesselProductivityService {

	public String getColNames() throws BusinessException;

	public TableResult getVesselProdRpt(Date fromDt, Date toDt, String displayView, String[] category, String tonnage,
			String rainRecord, String vesselType, String dateType, Criteria criteria) throws BusinessException;


}

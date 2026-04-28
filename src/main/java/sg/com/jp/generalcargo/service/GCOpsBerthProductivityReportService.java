package sg.com.jp.generalcargo.service;

import java.util.Date;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GCOpsBerthProductivityReportService {
	
	public Integer getPaginationRecordsNumber() throws BusinessException;
	
	public TableResult getBerthUtilisationRpt(Date fromDt, java.util.Date toDt,String cntrTonnParam,Criteria criteria) throws BusinessException;

}

package sg.com.jp.generalcargo.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.VesselProdRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.GCOpsBerthProductivityReportService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class GCOpsBerthProductivityReportServiceImpl implements GCOpsBerthProductivityReportService {

	@Autowired
	private VesselProdRepo vslProdRepo;

	@Override
	public Integer getPaginationRecordsNumber() throws BusinessException {
		return vslProdRepo.getPaginationRecordsNumber();
	}

	@Override
	public TableResult getBerthUtilisationRpt(Date fromDt, Date toDt, String cntrTonnParam, Criteria criteria)
			throws BusinessException {
		return vslProdRepo.getBerthUtilisationRpt(fromDt, toDt, cntrTonnParam, criteria);
	}

}

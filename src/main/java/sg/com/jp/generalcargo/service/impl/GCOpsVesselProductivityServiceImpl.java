package sg.com.jp.generalcargo.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.VesselProdRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.GCOpsVesselProductivityService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class GCOpsVesselProductivityServiceImpl implements GCOpsVesselProductivityService {

	@Autowired
	private VesselProdRepo vesselProdRepo;

	@Override
	public String getColNames() throws BusinessException {
		return vesselProdRepo.getColNames();
	}

	@Override
	public TableResult getVesselProdRpt(Date fromDt, Date toDt, String displayView, String[] category, String tonnage,
			String rainRecord, String vesselType, String dateType, Criteria criteria) throws BusinessException {
		return vesselProdRepo.getVesselProdReport(fromDt, toDt, displayView, category, tonnage, rainRecord, vesselType,
				dateType, criteria);
	}

}

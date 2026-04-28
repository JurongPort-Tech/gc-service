package sg.com.jp.generalcargo.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.RainRecordsRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.GCOpsRainRecordsService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("rainRecordsService")
public class GCOpsRainRecordsServiceImpl implements GCOpsRainRecordsService {

	@Autowired
	private RainRecordsRepo rainRecordsRepo;

	@Override
	public Integer getPaginationRecordCount() throws BusinessException {
		return rainRecordsRepo.getPaginationRecordCount();
	}

	@Override
	public Integer getRainRecordEditableDays() throws BusinessException {
		return rainRecordsRepo.getRainRecordEditableDays();
	}

	@Override
	public Integer getAllRainRecordsCount() throws BusinessException {
		return rainRecordsRepo.getAllRainRecordsCount();
	}

	@Override
	public Map<String, String> getRainCategories() throws BusinessException {
		return rainRecordsRepo.getRainCategories();
	}

	@Override
	public Map<String, String> getRainLocations() throws BusinessException {
		return rainRecordsRepo.getRainLocations();
	}

	@Override
	public TableResult getRainRecordsByDate(String listFromDate, String listToDate, String location, Criteria criteria)
			throws BusinessException {
		return rainRecordsRepo.getRainRecordsByDate(listFromDate, listToDate, location, criteria);
	}

	@Override
	public boolean isEditableRainRecord(String rainCntr, String mode) throws BusinessException {
		return rainRecordsRepo.isEditableRainRecord(rainCntr, mode);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean addRainRecord(String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String currUser, String selectedRainLocation) throws BusinessException {
		return rainRecordsRepo.addRainRecord(startDate, startTime, endDate, endTime, rainCategoryCd, currUser,
				selectedRainLocation);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean updateRainRecord(String rainCntr, String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String currUser, String statusCd, String selectedRainLocation)
			throws BusinessException {
		return rainRecordsRepo.updateRainRecord(rainCntr, startDate, startTime, endDate, endTime, rainCategoryCd,
				currUser, statusCd, selectedRainLocation);
	}

}

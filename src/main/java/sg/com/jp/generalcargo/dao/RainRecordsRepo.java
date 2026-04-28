package sg.com.jp.generalcargo.dao;

import java.util.Map;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface RainRecordsRepo {

	public Integer getPaginationRecordCount() throws BusinessException;

	public Integer getRainRecordEditableDays() throws BusinessException;

	public Integer getAllRainRecordsCount() throws BusinessException;

	public Map<String, String> getRainCategories() throws BusinessException;

	public Map<String, String> getRainLocations() throws BusinessException;

	public TableResult getRainRecordsByDate(String dateFrom, String dateTo, String location, Criteria criteria)
			throws BusinessException;

	public boolean isEditableRainRecord(String rainCntr, String mode) throws BusinessException;

	public boolean addRainRecord(String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String userId, String location) throws BusinessException;

	public boolean updateRainRecord(String rainCntr, String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String userId, String statusCd, String location) throws BusinessException;

}

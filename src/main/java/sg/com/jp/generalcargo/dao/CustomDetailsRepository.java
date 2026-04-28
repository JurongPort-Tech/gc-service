package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrailDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsUploadConfig;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CustomDetailsRepository {

	public List<VesselVoyValueObject> getlistVessel(String coCd, String search) throws BusinessException;

	public TableResult getCustomDetailsActionTrail(Criteria criteria) throws BusinessException;

	public PageDetails customDetailsUploadDetail(String vvCd) throws BusinessException;

	public String getTemplateVersionNo() throws BusinessException;

	public List<CustomDetails> getCustomDetails(String vvCd) throws BusinessException;

	public List<CustomDetailsUploadConfig> getTemplateHeader() throws BusinessException;

	public List<String> getIMOClassList() throws BusinessException;

	public boolean insertActionTrial(String varNbr, String typeCd, String summary, String lastTimestamp, String userId)
			throws BusinessException;

	public Long insertCustomExcelDetails(CustomDetailsFileUploadDetails customDetailsFileUploadDetails)
			throws BusinessException;

	public boolean updateCustomDetailsExcelDetails(Long seq_id, String outputFileName) throws BusinessException;

	public List<CustomDetails> insertCustomDetailsData(List<CustomDetails> customDetailsRecords, String varNbr,
			String userId, String companyCode) throws BusinessException;

	public CustomDetailsActionTrailDetails customDetailsActionTrailDetail(String custom_act_trl_id, String typeCd)
			throws BusinessException;

	public BookingReferenceFileUploadDetails getCustomDetailFileUploadDetails(String seq_id) throws BusinessException;

	public int customDetailIsExist(String upperCellData, String varNbr, String instructionType) throws BusinessException;

	public int containerIsExist(String cntrNbr, String varNbr) throws BusinessException;

	public boolean isCntrDetailsMatch(String status, String cntrNbr, String type, String varNbr) throws BusinessException;

	public PageDetails getVesselCallDetails(String vvCd) throws BusinessException;

	public String getTotalCntrForVessel(String varNbr) throws BusinessException;

	public String getConsigneeShipperCd(String upperCellData) throws BusinessException;
	
	public boolean isShipmentStatusValid(String upperCellData, String varNbr) throws BusinessException;

	public String getVvcdFromVesselDetails(String vslName, String inVoyNo, String outVoyNo) throws BusinessException;

	public Map<String, String> getCntrdetailsMap(String cellData_cntr, String cellData_instructionType, String varNbr) throws BusinessException;
	
	public String getPartyName(String uenNbr) throws BusinessException;
	
	public Long insertCustomCUSCAR(CustomDetailsFileUploadDetails customDetailsFileUploadDetails) throws BusinessException;

	public List<String> getSelectionList(String type) throws BusinessException;
}

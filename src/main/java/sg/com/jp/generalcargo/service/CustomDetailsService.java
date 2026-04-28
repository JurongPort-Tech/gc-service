package sg.com.jp.generalcargo.service;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrailDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.SummaryCuscar;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CustomDetailsService {

	public List<VesselVoyValueObject> getlistVessel(String coCd, String search) throws BusinessException;

	public XSSFWorkbook customDetailsExcelDownload(String vvCd) throws BusinessException;

	public PageDetails customDetailsUploadDetail(String vvCd) throws BusinessException;

	public TableResult getCustomDetailsActionTrail(Criteria criteria) throws BusinessException;

	public CustomDetailsActionTrailDetails customDetailsActionTrailDetail(String mft_act_trl_id, String typeCd)
			throws BusinessException;

	public String fileUpload(MultipartFile uploadingFile, String varNbr) throws BusinessException;

	public String getTimeStamp() throws BusinessException;

	public Summary processCustomDetailsExcelFile(MultipartFile uploadingFile,
			CustomDetailsFileUploadDetails customDetailsFileUploadDetails, String varNbr, String userId,
			String companyCode) throws BusinessException;

	public boolean insertActionTrial(String varNbr, String typeCd, Summary summary, String lastTimestamp, String userId)
			throws BusinessException;

	public Resource excelProcessDownload(String refId, String type) throws BusinessException;

	public boolean insertActionTrialCuscar(String varNbr, String typeCd, SummaryCuscar summary, String lastTimestamp,
			String userId) throws BusinessException;

	public String getTotalCntrForVessel(String vvCd) throws BusinessException;

	public String getVvcdFromVesselDetails(String vslName, String inVoyNo, String outVoyNo) throws BusinessException;


}

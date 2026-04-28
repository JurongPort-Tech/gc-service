package sg.com.jp.generalcargo.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.CargoDocUpload;
import sg.com.jp.generalcargo.domain.CargoDocUploadDetail;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselDetail;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoDocumentService {

	public List<VesselDetail> getVesselInfo(String vesselName) throws BusinessException;

	public CargoDocUpload getCargoDocUploadDetails(Criteria criteria) throws BusinessException; 

	public Result saveCargoDocUpload(Criteria criteria) throws BusinessException;

	public Result saveCargoDocUploadDetail(CargoDocUploadDetail obj) throws BusinessException;

	public CargoDocUpload getCargoDocUploadAuditDetail(Criteria criteria) throws BusinessException;

	public String fileUpload(MultipartFile uploadFile, String vvCd) throws BusinessException;

	public Resource fileDownload(Criteria criteria) throws BusinessException;

	public TableResult getCargoDocUploadAuditInfo(Criteria criteria) throws BusinessException; 
	
	public Boolean isDocSubmissionAllowed(String vvCd,String coCd) throws BusinessException;
	
	public boolean sendNotification(Criteria criteria) throws BusinessException;

}

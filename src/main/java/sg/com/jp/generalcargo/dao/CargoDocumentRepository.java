package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.CargoDocUpload;
import sg.com.jp.generalcargo.domain.CargoDocUploadDetail;
import sg.com.jp.generalcargo.domain.CargoDocUploadNotificationDetail;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselDetail;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoDocumentRepository {

	public List<VesselDetail> getVesselInfo(String vesselName) throws BusinessException;

	public CargoDocUpload getCargoDocUploadDetails(Criteria criteria) throws BusinessException;

	public Result saveCargoDocUpload(Criteria criteria) throws BusinessException;

	public Result saveCargoDocUploadDetail(CargoDocUploadDetail obj) throws BusinessException;

	public CargoDocUpload getCargoDocUploadAuditDetail(Criteria criteria) throws BusinessException;

	public TableResult getCargoDocUploadAuditInfo(Criteria criteria) throws BusinessException;
	
	public Boolean isDocSubmissionAllowed(String vvCd,String coCd) throws BusinessException;

	public CargoDocUploadNotificationDetail getNotificationDetails(Criteria criteria) throws BusinessException;
	
	public List<MiscDetail> getCargoDocEmail(String catCd) throws BusinessException;

}

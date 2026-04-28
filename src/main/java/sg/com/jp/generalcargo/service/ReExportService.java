package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReExportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ReExportService {

	public List<ReExportValueObject> getVesselVoy(String coCd) throws BusinessException;

	public List<ReExportValueObject> getManifestList(String selVoyno, String coCd,Criteria criteria) throws BusinessException;

	public String checkReExportStatus(String mftSeqNo, String coCd)  throws BusinessException;

	public boolean chkPortCode(String portL) throws BusinessException;

	public String updateReExportDetails(String mftSeqNo, String portL, String userId, String coCd) throws BusinessException;

	public List<ReExportValueObject> getPortList() throws BusinessException;
	
	public int getManifestListCount(String vvcode,String coCd,Criteria criteria) throws BusinessException;

}

package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReExportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ReExportRepository {
	
	public List<ReExportValueObject> getPortList() throws BusinessException;
	
	public String updateReExportDetails(java.lang.String edoasnnbr, String PortL,String struserid,String cocode) throws BusinessException;
	
	public boolean chkPortCode(String portcd) throws BusinessException;
	
	public java.lang.String checkReExportStatus(java.lang.String edoasnnbr,String coCd) throws BusinessException;
	
	public List<ReExportValueObject> getVesselVoy(String cocode) throws BusinessException;
	
	public List<ReExportValueObject> getManifestList(String vvcode,String coCd,Criteria criteria) throws BusinessException;
	
	public int getManifestListCount(String vvcode,String coCd,Criteria criteria) throws BusinessException;

}

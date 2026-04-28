package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.CargoEnquiryMgmtAction;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GeneralBulkEnquiryService {
	
	//Start Region General and bulk cargo enquiry
	public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd) throws BusinessException;

	public int countVesselByName(String name, String coCd) throws BusinessException;
	
	public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException;

	public List<DPEUtil> getOutVoyageList(String name, String coCd, String voy_nbr, String ind) throws BusinessException;
	
	public DPEUtil getVesselDetail(String name) throws BusinessException;
	
	public  List<DPEUtil> listCompanyByName(Integer start, Integer limit, String name) throws BusinessException;
	
	public int countCompanyByName(String name) throws BusinessException;
	
	public Map<String, Object> getCargoEnquiryParamsMapByCustCd(String companyCd) throws BusinessException;

	public List<CargoEnquiryMgmtAction> listCargoRecords(Integer start, Integer limit, String sort, String dir, Map<String,Object> filters, Criteria criteria) throws Exception;

	public int countCargoRecords(Map<String, Object> filters) throws Exception;
	
	public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;

	//End Region - General and bulk cargo enquiry

}

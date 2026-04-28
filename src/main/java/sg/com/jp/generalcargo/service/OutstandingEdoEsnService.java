package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.OutstandingVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutstandingEdoEsnService {
	
	// StartRegion General Cargo Outstanding EDO/ESN for haulier
	public List<DPEUtil> listHaulierCompanyByName(Integer start, Integer limit, String name) throws BusinessException;
	
	public int countHaulierCompanyByName(String name) throws BusinessException;
	
	public int countRecords(Map<String, Object> filters) throws BusinessException;
	
	public List<OutstandingVO> listRecords(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters, Criteria criteria, Boolean needAllData) throws BusinessException;
	
	public DPEUtil getVesselDetail(String name) throws BusinessException;
	
	public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd) throws BusinessException;
	
	public int countVesselByName(String name, String coCd) throws BusinessException;
	
	public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException;

	public List<DPEUtil> getOutVoyageList(String name, String coCd, String voy_nbr, String ind) throws BusinessException;
	
	public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException;

	public List<List<String>> getCargoType() throws BusinessException;

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException;

	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws BusinessException;

	public String chkCancelAmend(String brno, String userCoyCode, String mode) throws BusinessException;

	public boolean getCheckUserBookingReference(String coCd, String brno) throws BusinessException;

	public int retrieveMaxCargoTon(String varno) throws BusinessException;
	//End Region
}

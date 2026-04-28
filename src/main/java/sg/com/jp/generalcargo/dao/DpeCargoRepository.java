package sg.com.jp.generalcargo.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AsnHistory;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.CargoEnquiryMgmtAction;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DNUADetail;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DpeCargoRepository {

	public CargoEnquiryDetails getCargoRecord(String edoNbr, Long esnNbr, String type) throws BusinessException;

	public List<GeneralEventLogValueObject> getGBEventLog(Date modifyDate, String vvCd, String asnNbr,
			String vvInd) throws BusinessException;

	public List<VesselTxnEventLogValueObject> getUnprocessGBVesselTxnEventLog(String vvCd) throws BusinessException;

	public List<DNUADetail> listDnUaRecordsByAsnNbr(String edoNbr, Long esnNbr, boolean b) throws BusinessException;

	public List<AsnHistory> listAsnHistoryRecords(CargoEnquiryDetails record, String type) throws BusinessException;

	public List<CargoEnquiryMgmtAction> listCargoRecords(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria) throws Exception;

	public List<String> getAccountNbrByCustCd(String custCode) throws BusinessException;

	public List<String> getTruckerIcByCustCd(String custCode) throws BusinessException;

	public List<String> getVvCdByAbCd(String custCode) throws BusinessException;

	public List<String> getEdoNbrBySubAdp(String custCode) throws BusinessException;

	public List<String> getEsnNbrBySubTrucker(String custCode) throws BusinessException;

	public int countCargoRecords(Map<String, Object> filters) throws Exception;

	// method : sg.com.jp.dpe.dao.DpeCargoJdbcDao-->listGeneralShutoutCargo
	public TableResult listGeneralShutoutCargo(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria) throws BusinessException;

	public int updateShutOutPkgBkDetail(String shut_qty, String userId, String bk_ref_nbr) throws BusinessException;

	public DPECargo loadTransferCargo(String esn_asn_nbr) throws BusinessException;

	public boolean chkBkRefNo(String bk_ref_nbr) throws BusinessException;

	public DPECargo loadGeneralShutoutCargoByESN(String esn_asn_nbr, String trans_type) throws BusinessException;

	public DPEUtil getAdpNmByAdpIc(String adpIc) throws BusinessException;

	public TableResult listShutoutCargoEDO(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException;

	public TableResult listTransferCargo(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException;

	public DPECargo loadTransferCargoForView(String esn_asn_nbr) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;
}

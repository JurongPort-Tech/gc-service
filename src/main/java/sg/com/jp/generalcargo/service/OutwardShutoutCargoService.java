package sg.com.jp.generalcargo.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.ShutOutCargoVo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardShutoutCargoService {

	public DPECargo loadTransferCargo(String esn_asn_nbr) throws BusinessException;

	public List<ShutOutCargoVo> getShutoutCargoMtgList(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String cargoType, String terminal, String dwellDays) throws BusinessException;

	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId, String userName, String dateTime, String status) throws BusinessException, SQLException;

	// StartRegion DPEUtilActionService
	public TableResult listGeneralShutoutCargo(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria) throws BusinessException;

	public List<DPEUtil> listVesselByNameForMonitoring(String name, String coCd) throws BusinessException;

	public int countVesselByNameForMonitoring(String name, String coCd) throws BusinessException;

	public int updateShutOutPkgBkDetail(String shut_qty, String userId, String bk_ref_nbr) throws BusinessException;

//	
	public boolean chkBkRefNo(String bk_ref_nbr) throws BusinessException;

	public List<EsnListValueObject> TransferCrgUpdateForDPE(String bk_ref_nbr[], String newbkrefnbr[], String esnarr[],
			String transtypearr[], String transNbr[], String shutoutqty[], String actnbrshped[], String uanbrpkgs[],
			String uaftdttm[], String Toutvoynbr, String varnoF, String varnoT, String UserID) throws BusinessException;

	public boolean checkAccountNbr(String accNbr) throws BusinessException;

	public String updateShutoutEdo(EdoValueObjectCargo edoValueObject, String userId) throws BusinessException;

	public String insertShutoutEdoForDPE(EdoValueObjectCargo edoValueObject, String userId) throws BusinessException;

	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd) throws BusinessException;

	public String deleteShutoutEdoDetails(String stredoasnnbr, String struserid) throws BusinessException;

	public void updateShutEdoQtyAfterCancel(String esnAsnNbr, int qty, String userid) throws BusinessException;

	public List<DPEUtil> listVesselForAddTransferCargo(Integer start, Integer limit, String name, String coCd) throws BusinessException;

	public List<DPEUtil> getOutVoyageList4Transfer(String name, String coCd, String voyNbr, String ind) throws BusinessException;

	public DPEUtil getAdpNmByAdpIc(String adpIc) throws BusinessException;

	public TableResult listShutoutCargoEDO(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException;

	public TableResult listTransferCargo(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException;

	public DPECargo loadGeneralShutoutCargoByESN(String esn_asn_nbr, String trans_type) throws BusinessException;

	public DPECargo loadGeneralShutoutCargoByEDO(String edo_asn_nbr, String trans_type) throws BusinessException;

	public DPECargo loadTransferCargoForView(String esn_asn_nbr) throws BusinessException;

	public String getUserNameMap(String userId) throws BusinessException;
}

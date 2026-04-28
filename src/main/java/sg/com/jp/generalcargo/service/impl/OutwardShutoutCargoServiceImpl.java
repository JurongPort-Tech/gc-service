package sg.com.jp.generalcargo.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.DpeCargoRepository;
import sg.com.jp.generalcargo.dao.DpeUtilRepository;
import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.ShutOutCargoRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPECargo;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.ShutOutCargoVo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.OutwardShutoutCargoService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class OutwardShutoutCargoServiceImpl implements OutwardShutoutCargoService {

	@Autowired
	DpeCargoRepository dpeCargoDao;

	@Autowired
	EdoRepository edoRepository;

	@Autowired
	EsnRepository esnRepository;
	
	@Autowired
	ShutOutCargoRepository shutOutCargoRepo;
	
	@Autowired
	DpeUtilRepository dpeUtilRepository;

	@Override
	public List<ShutOutCargoVo> getShutoutCargoMtgList(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String cargoType, String terminal, String dwellDays) throws BusinessException {
		return shutOutCargoRepo.getShutoutCargoMtrgList(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
				terminal, dwellDays);
	}

	@Override
	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId, String userName, String dateTime, String status) throws BusinessException, SQLException {
		return shutOutCargoRepo.updateDeliveryStatus(bkgRefNbr, deliveredPackages, deliveryRemarks, userId, userName, dateTime, status);
	}

	@Override
	public TableResult listGeneralShutoutCargo(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria) throws BusinessException {
		return dpeCargoDao.listGeneralShutoutCargo(start, limit, sort, dir, filters, criteria);
	}

	@Override
	public List<DPEUtil> listVesselByNameForMonitoring(String name, String coCd) throws BusinessException {
		return dpeUtilRepository.listVesselByNameForMonitoring(name, coCd);
	}

	@Override
	public int countVesselByNameForMonitoring(String name, String coCd) throws BusinessException {
		return dpeUtilRepository.countVesselByNameForMonitoring(name, coCd);
	}

	@Override
	public int updateShutOutPkgBkDetail(String shut_qty, String userId, String bk_ref_nbr) throws BusinessException {
		return dpeCargoDao.updateShutOutPkgBkDetail(shut_qty, userId, bk_ref_nbr);
	}

	@Override
	public DPECargo loadTransferCargo(String esn_asn_nbr) throws BusinessException {
		return dpeCargoDao.loadTransferCargo(esn_asn_nbr);
	}

	@Override
	public boolean chkBkRefNo(String bk_ref_nbr) throws BusinessException {
		return dpeCargoDao.chkBkRefNo(bk_ref_nbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public List<EsnListValueObject> TransferCrgUpdateForDPE(String bk_ref_nbr[], String newbkrefnbr[], String esnarr[],
			String transtypearr[], String transNbr[], String shutoutqty[], String actnbrshped[], String uanbrpkgs[],
			String uaftdttm[], String Toutvoynbr, String varnoF, String varnoT, String UserID)
			throws BusinessException {
		return esnRepository.TransferCrgUpdateForDPE(bk_ref_nbr, newbkrefnbr, esnarr, transtypearr, transNbr,
				shutoutqty, actnbrshped, uanbrpkgs, uaftdttm, Toutvoynbr, varnoF, varnoT, UserID);
	}

	@Override
	public DPECargo loadGeneralShutoutCargoByESN(String esn_asn_nbr, String trans_type) throws BusinessException {

		return dpeCargoDao.loadGeneralShutoutCargoByESN(esn_asn_nbr, trans_type);
	}

	@Override
	public DPECargo loadGeneralShutoutCargoByEDO(String edo_asn_nbr, String trans_type) throws BusinessException {

		return edoRepository.loadGeneralShutoutCargoByEDO(edo_asn_nbr, trans_type);
	}

	@Override
	public boolean checkAccountNbr(String accNbr) throws BusinessException {

		return edoRepository.checkAccountNbr(accNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateShutoutEdo(EdoValueObjectCargo edo, String userId) throws BusinessException {

		return edoRepository.updateShutoutEdo(edo, userId);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertShutoutEdoForDPE(EdoValueObjectCargo edo, String userId) throws BusinessException {

		return edoRepository.insertShutoutEdoForDPE(edo, userId);
	}

	@Override
	public List<EdoJpBilling> getEdoJpBillingNbr(String strAdpNbr, String strcustcd, String strVslCd) throws BusinessException {

		return edoRepository.getEdoJpBillingNbr(strAdpNbr, strcustcd, strVslCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String deleteShutoutEdoDetails(String stredoasnnbr, String struserid) throws BusinessException {
		return edoRepository.deleteShutoutEdoDetails(stredoasnnbr, struserid);
	}

	@Override
	public void updateShutEdoQtyAfterCancel(String esnAsnNbr, int qty, String userid) throws BusinessException {
		esnRepository.updateShutEdoQtyAfterCancel(esnAsnNbr, qty, userid);
	}

	@Override
	public List<DPEUtil> listVesselForAddTransferCargo(Integer start, Integer limit, String name, String coCd) throws BusinessException {
		return dpeUtilRepository.listVesselForAddTransferCargo(start, limit, name, coCd);
	}

	@Override
	public List<DPEUtil> getOutVoyageList4Transfer(String name, String coCd, String voyNbr, String ind) throws BusinessException {
		return dpeUtilRepository.getOutVoyageList4Transfer(name, coCd, voyNbr, ind);
	}

	@Override
	public DPEUtil getAdpNmByAdpIc(String adpIc) throws BusinessException {
		return dpeCargoDao.getAdpNmByAdpIc(adpIc);
	}

	@Override
	public TableResult listShutoutCargoEDO(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException {
		return dpeCargoDao.listShutoutCargoEDO(esn_asn_nbr, sort, dir, criteria);
	}

	@Override
	public TableResult listTransferCargo(String esn_asn_nbr, String sort, String dir, Criteria criteria)
			throws BusinessException {
		return dpeCargoDao.listTransferCargo(esn_asn_nbr, sort, dir, criteria);
	}

	@Override
	public DPECargo loadTransferCargoForView(String esn_asn_nbr) throws BusinessException {
		return dpeCargoDao.loadTransferCargoForView(esn_asn_nbr);
	}

	@Override
	public String getUserNameMap(String userId) throws BusinessException {
		return shutOutCargoRepo.getUserNameMap(userId);
	}
}

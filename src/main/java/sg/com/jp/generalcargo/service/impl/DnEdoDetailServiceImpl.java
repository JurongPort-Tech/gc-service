package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.CashSalesRepository;
import sg.com.jp.generalcargo.dao.DnEdoDetailRepository;
import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.service.DnEdoDetailService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("DnEdoDetailService")
public class DnEdoDetailServiceImpl implements DnEdoDetailService {

	@Autowired
	private DnEdoDetailRepository dnEdoDetailRepository;

	@Autowired
	private ProcessGBLogRepository processGBLogRepository;

	@Autowired
	private UARepository UARepository;

	@Autowired
	private CashSalesRepository cashSalesRepository;

	@Autowired
	private EdoRepository EdoRepository;
	
	@Autowired
	private ManifestRepository manifestRepo;
	// StartRegion DnEdoDetailServiceImpl

	@Override
	public boolean checkESNCntr(String edoasn) throws BusinessException {
		return dnEdoDetailRepository.checkESNCntr(edoasn);
	}

	@Override
	public String getCntrSeq(String cntrNo) throws BusinessException {
		return dnEdoDetailRepository.getCntrSeq(cntrNo);
	}

	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		return dnEdoDetailRepository.checkCancelDN(dnNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void checkAndUpdateFirstDN(String edoAsnNo, String dnRefNo) throws BusinessException {
		processGBLogRepository.checkAndUpdateFirstDN(edoAsnNo, dnRefNo);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public boolean cancelBillableCharges(String refNo, String refInd) throws BusinessException {
		return processGBLogRepository.cancelBillableCharges(refNo, refInd);
	}

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		return dnEdoDetailRepository.getDnCntrFirst(cntrSeq, cntrNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid)
			throws BusinessException {
		return dnEdoDetailRepository.cancelShutoutDN(edoNbr, dnNbr, userid);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		return dnEdoDetailRepository.cancelDN(edoNbr, dnNbr, userid, transtype, searchcrg, tesn_nbr);
	}

	public String checkTransType(String esnNbr) throws BusinessException {
		return UARepository.checkTransType(esnNbr);
	}

	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		return dnEdoDetailRepository.getUaNbr(esnNbr, nbrPkgs, transDttm, dpNm, dpIcNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void cancelUA(String uaNbr, String esnAsnNbr, String transType, String userId, String uaNbrPkgs)
			throws BusinessException {
		UARepository.cancelUA(uaNbr, esnAsnNbr, transType, userId, uaNbrPkgs);
	}

	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		return dnEdoDetailRepository.countDNBalance(cntrNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {
		return dnEdoDetailRepository.updateCntrStatus(cntrSeq, userID);
	}

	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException {
		return dnEdoDetailRepository.checkFirstDN(edoNbr, cntrNo);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {
		dnEdoDetailRepository.changeStatusCntr(cntrSeq, user, newCatCode);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException {
		dnEdoDetailRepository.cancel1stDn(cntrSeq, cntrNbr, user);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException {
		dnEdoDetailRepository.updateWeight(cntrSeq, weight, user, times);
	}

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException {
		return dnEdoDetailRepository.chktesnJpPsa_nbr(esn_asnNbr);
	}

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException {
		return dnEdoDetailRepository.fetchShutoutDNList(edoNbr);
	}

	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr) throws BusinessException {
		return dnEdoDetailRepository.fetchEdoDetails(edoNbr, searchcrg, tesnnbr);
	}

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException {
		return dnEdoDetailRepository.chktesnJpJp_nbr(esn_asnNbr);
	}

	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr) throws BusinessException {
		return dnEdoDetailRepository.fetchDNList(edoNbr, searchcrg, tesn_nbr);
	}

	public boolean chkEDOStuffing(String edoNbr) throws BusinessException {
		return dnEdoDetailRepository.chkEDOStuffing(edoNbr);
	}

	public List<CashSalesValueObject> getCashSalesList() throws BusinessException {
		return cashSalesRepository.getCashSalesList();
	}

	public boolean chktesnJpJp(String edoNbr) throws BusinessException {
		return dnEdoDetailRepository.chktesnJpJp(edoNbr);
	}

	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException {
		return dnEdoDetailRepository.fetchSubAdpDetails(edoNbr);
	}

	public int getSpencialPackage(String edoNbr) throws BusinessException {
		return dnEdoDetailRepository.getSpencialPackage(edoNbr);
	}

	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException {
		return cashSalesRepository.getCashSales(dnList);
	}
	// EndRegion DnEdoDetailServiceImpl

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException {
		return dnEdoDetailRepository.fetchShutoutDNDetail(strEdoNo, dnNo);
	}

	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException {
		return dnEdoDetailRepository.getVechDetails(dnNbr);
	}

	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException {
		return dnEdoDetailRepository.fetchDNDetail(edoNbr, status, searchcrg, tesn_nbr);
	}

	public String getCntrNo(String dnNbr) throws BusinessException {
		return dnEdoDetailRepository.getCntrNo(dnNbr);
	}

	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {
		return cashSalesRepository.getCashSales(refNbr);
	}

	public String getMachineID(String recNbr) throws BusinessException {
		return cashSalesRepository.getMachineID(recNbr);
	}

	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException {
		return cashSalesRepository.getCashSalesPaymentCode(cashsalesType);
	}

	public String getNETSRefID(String receiptNo) throws BusinessException {
		return cashSalesRepository.getNETSRefID(receiptNo);
	}

	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException {
		return dnEdoDetailRepository.fetchDNDetail(strEdoNo, edoNbr, status, searchcrg, tesn_nbr);
	}

	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {
		return dnEdoDetailRepository.fetchShutoutDNCreateDetail(edoNbr, transType, searchcrg, tesn_nbr);
	}

	public boolean chkVVStatus(String esnNbrR) throws BusinessException {
		return dnEdoDetailRepository.chkVVStatus(esnNbrR);
	}

	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {
		return dnEdoDetailRepository.fetchDNCreateDetail(edoNbr, transType, searchcrg, tesn_nbr);
	}

	// region asn link start

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd) throws BusinessException {
		return EdoRepository.getVesselVoyageNbrList(strCustCode, strmodulecd);
	}

	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String strCustCode, String strmodulecd, String vesselName,
			String voyageNumber) throws BusinessException {
		return EdoRepository.getVesselVoyageNbrList(strCustCode, strmodulecd, vesselName, voyageNumber);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWHIndicator(String stredoasnnbr, String strWhInd, String strWhAggrNbr, String strWhRemarks,
			String strFreeStgDays, String struserid) throws BusinessException {
		EdoRepository.updateWHIndicator(stredoasnnbr, strWhInd, strWhAggrNbr, strWhRemarks, strFreeStgDays,
				struserid);
	}

	public String getSearchDetails(String strCustCode, String stredoasnnbr) throws BusinessException {
		return EdoRepository.getSearchDetails(strCustCode, stredoasnnbr);
	}

	public AdpValueObject getTaEndorserNmByUENNo(Criteria criteria) {
		return EdoRepository.getTaEndorserNmByUENNo(criteria);
	}

	public boolean chkVslStat(String varno) throws BusinessException {
		return manifestRepo.chkVslStat(varno);
	}

	public String getVslStatus(String varno) throws BusinessException{
		return EdoRepository.getVslStatus(varno);
	}

	public List<EdoValueObjectCargo> viewEdoDetails(String stredoasnnbr) throws BusinessException {
		return EdoRepository.viewEdoDetails(stredoasnnbr);
	}

	public List<AdpValueObject> getAdpList(String edoNbr) throws BusinessException {
		return EdoRepository.getAdpList(edoNbr);
	}
	//

}

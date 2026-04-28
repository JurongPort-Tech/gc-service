package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.AdminFeeWaiverRepo;
import sg.com.jp.generalcargo.dao.AdminWaiverOscarUtilRepo;
import sg.com.jp.generalcargo.dao.ShipStoreRepository;
import sg.com.jp.generalcargo.dao.SystemCodeRepo;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShipStoreValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoShipStoreService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class OutwardCargoShipStoreServiceImpl implements OutwardCargoShipStoreService {

	@Autowired
	private SystemCodeRepo systemCode;
	@Autowired
	private ShipStoreRepository shipStore;
	@Autowired
	private AdminFeeWaiverRepo adminFeeWaiver;
	@Autowired
	private AdminWaiverOscarUtilRepo adminWaiverOscarUtil;

	@Override
	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException {

		return shipStore.getVesselList(custId);
	}

	@Override
	public List<ShipStoreValueObject> getshpStrList(String selectVoyNo, String custId, Criteria criteria) throws BusinessException {

		return shipStore.getshpStrList(selectVoyNo, custId, criteria);
	}

	@Override
	public String getValue(String paraCd) throws BusinessException {

		return systemCode.getValue(paraCd);
	}
	
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID)
			throws BusinessException {

		return adminFeeWaiver.updateWaiverAdvice(adminFeeWaiverVO, userID);
	}

	@Override
	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException {
		return adminWaiverOscarUtil.sendAdminWaiverRequestToOscar(adminFeeWaiverVO);
	}

	@Override
	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException {

		return adminFeeWaiver.invokeOscarWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd) throws BusinessException {

		return adminFeeWaiver.captureWaiverAdviceRequest(waiverRefNo, userID, waiverRefType, resendReq, adviceIdStr,
				vvCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertSSDetailsForDPE(String varno, String custCd, String truckerIcNo, String truckerCNo,
			String marking, String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType,
			int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String truckerCd, String UserID, String crgType, String shpStrRefNo, String shpradd, String adminFeeInd,
			String reasonForWaive) throws BusinessException {

		return shipStore.insertSSDetailsForDPE(varno, custCd, truckerIcNo, truckerCNo, marking, dgIn, hsCode, dutiDI,
				truckerName, pkgsType, noOfPkgs, weight, volume, accNo, payMode, cargoDesc, truckerCd, UserID, crgType,
				shpStrRefNo, shpradd, adminFeeInd, reasonForWaive);
	}

	@Override
	public boolean chkAccNo(String accNo) throws BusinessException {

		return shipStore.chkAccNo(accNo);
	}

	@Override
	public List<ShipStoreValueObject> getUserAccNo(String custId, String accNbr) throws BusinessException {

		return shipStore.getUserAccNo(custId, accNbr);
	}

	@Override
	public String getTruckerName(String custCode, String trcIcNo) throws BusinessException {

		return shipStore.getTruckerName(custCode, trcIcNo);
	}

	@Override
	public List<ShipStoreValueObject> getAccNo(String truckerIcNo) throws BusinessException {

		return shipStore.getAccNo(truckerIcNo);
	}

	@Override
	public String getTruckerCd(String trcIcNo) throws BusinessException {

		return shipStore.getTruckerCd(trcIcNo);
	}

	@Override
	public boolean chkPkgsType(String pkgs_Type) throws BusinessException {

		return shipStore.chkPkgsType(pkgs_Type);
	}

	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {

		return shipStore.getTruckerDetails(truckerIc);
	}

	@Override
	public String getVslScheme(String vvCd) throws BusinessException {

		return shipStore.getVslScheme(vvCd);
	}

	@Override
	public List<ShipStoreValueObject> getShpStrDetails(String esnNbr, String custId) throws BusinessException {

		return shipStore.getShpStrDetails(esnNbr, custId);
	}

	@Override
	public void insertSSAdminFeeEvent(String esnNo, String userID) throws BusinessException {
		shipStore.insertSSAdminFeeEvent(esnNo, userID);

	}

	@Override
	public List<ShipStoreValueObject> getPkgList(String text) throws BusinessException {

		return shipStore.getPkgList(text);
	}

	@Override
	public void shpStrCancel(String esnNo) throws BusinessException {
		shipStore.shpStrCancel(esnNo);

	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateSSDetails(String ShpStrNo, String custCd, String truckerIcNo, String truckerCNo, String marking,
			String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType, int noOfPkgs, double weight,
			double volume, String accNo, String payMode, String cargoDesc, String truckerCd, String UserID,
			String crgType, String shpradd, String adminFeeInd, String reasonForWaive)
			throws BusinessException {

		return shipStore.updateSSDetails(ShpStrNo, custCd, truckerIcNo, truckerCNo, marking, dgIn, hsCode, dutiDI,
				truckerName, pkgsType, noOfPkgs, weight, volume, accNo, payMode, cargoDesc, truckerCd, UserID, crgType,
				shpradd, adminFeeInd, reasonForWaive);
	}

	@Override
	public int getshpStrListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException {

		return shipStore.getshpStrListCount(selectVoyNo, custId);
	}

	@Override
	public String getEsnDeclarantCd(String coCd, String varno) throws BusinessException {

		return shipStore.getEsnDeclarantCd(coCd, varno);
	}

	public String getEsnDeclarantName(String custCode, String trcIcNo) throws BusinessException {
		return shipStore.getEsnDeclarantName(custCode, trcIcNo);

	}

}

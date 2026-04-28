package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShipStoreValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardCargoShipStoreService {
	public String updateSSDetails(String ShpStrNo, String custCd, String truckerIcNo, String truckerCNo, String marking,
			String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType, int noOfPkgs, double weight,
			double volume, String accNo, String payMode, String cargoDesc, String truckerCd, String UserID,
			String crgType, String shpradd, String adminFeeInd, String reasonForWaive)
			throws BusinessException;

	public void shpStrCancel(String esnNo) throws BusinessException;

	public List<ShipStoreValueObject> getPkgList(String text) throws BusinessException;

	public String getVslScheme(String vvCd) throws BusinessException;

	public List<ShipStoreValueObject> getShpStrDetails(String esnNbr, String custId) throws BusinessException;

	public void insertSSAdminFeeEvent(String esnNo, String userID) throws BusinessException;

	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID)
			throws BusinessException;

	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException;

	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException;

	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd) throws BusinessException;

	public String insertSSDetailsForDPE(String varno, String custCd, String truckerIcNo, String truckerCNo,
			String marking, String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType,
			int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String truckerCd, String UserID, String crgType, String shpStrRefNo, String shpradd, String adminFeeInd,
			String reasonForWaive) throws BusinessException;

	public boolean chkAccNo(String accNo) throws BusinessException;

	public List<ShipStoreValueObject> getUserAccNo(String custId, String accNbr) throws BusinessException;

	public String getTruckerName(String custCode, String trcIcNo) throws BusinessException;

	public List<ShipStoreValueObject> getAccNo(String truckerIcNo) throws BusinessException;

	public String getTruckerCd(String trcIcNo) throws BusinessException;

	public boolean chkPkgsType(String pkgs_Type) throws BusinessException;

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;

	public String getValue(String paraCd) throws BusinessException;

	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException;

	public List<ShipStoreValueObject> getshpStrList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException;

	public int getshpStrListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException;

	public String getEsnDeclarantCd(String coCd, String varno) throws BusinessException;

	public String getEsnDeclarantName(String custCode, String trcIcNo) throws BusinessException;
}

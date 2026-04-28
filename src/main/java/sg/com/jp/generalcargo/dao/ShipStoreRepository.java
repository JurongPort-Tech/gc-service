package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShipStoreValueObject;
import sg.com.jp.generalcargo.domain.StoreRentCrReport;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ShipStoreRepository {

	public String updateSSDetails(String ShpStrNo, String custCd, String truckerIcNo, String truckerCNo, String marking,
			String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType, int noOfPkgs, double weight,
			double volume, String accNo, String payMode, String cargoDesc, String truckerCd, String UserID,
			String crgType, String shpradd, String adminFeeInd, String reasonForWaive) throws BusinessException;

	public void shpStrCancel(String esnNo) throws BusinessException;

	public List<ShipStoreValueObject> getPkgList(String text) throws BusinessException;

	public void insertSSAdminFeeEvent(String esnNo, String userID) throws BusinessException;

	public String getVslScheme(String vvCd) throws BusinessException;

	public List<ShipStoreValueObject> getShpStrDetails(String esnNbr, String custId) throws BusinessException;

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

	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException;

	public List<ShipStoreValueObject> getshpStrList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException;

	public int getshpStrListCount(String selectVoyNo, String custId) throws BusinessException;

	public String getEsnDeclarantCd(String coCd, String varno) throws BusinessException;

	public String getEsnDeclarantName(String custCode, String trcIcNo) throws BusinessException;
	
	public List<StoreRentCrReport> getStoreRentReports(String billmonth, String tsdirection) throws BusinessException;

}

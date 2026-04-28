package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnPsaJpRepository {

	public String insertEsnDetailsForDPE(String UserID, String varno, String custCd, String bookingRefNo,
			String marking, String portD, String lopInd, String dgIn, String hsCode, int storageDay, String storageInd,
			String pkgsType, int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String cntr1, String cntr2, String cntr3, String cntr4, String firstCName, String inVoyageNo,
			String stuffind, String category, String hsSubCodeFr, String hsSubCodeTo,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode, 
			List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public String createNomVesselPsaJp(String vslName, String voyNbr, String userid) throws BusinessException;

	public boolean chkFirstCarrierVsl(String vslNm, String inVoyNbr) throws BusinessException;

	public String getClsBjInd(String bookref) throws BusinessException;

	public String getClsVslInd(String bookref) throws BusinessException;

	public String getVvStatus(String bookref) throws BusinessException;

	public String getDeclarentCd(String bookref) throws BusinessException;

	public String getBkStatus(String bookref) throws BusinessException;

	public String getEsnDeclared(String bookref) throws BusinessException;

	public void esnCancel(String esnNo, String bookingRefno, String UserId) throws BusinessException;

	public String esnUpdateForDPE(int noOfPkgs, String hscd, String hsSubCodeFr, String hsSubCodeTo, String pkgsType,
			String mark, String lopInd, String dgInd, String stgInd, String poD, int noOfStorageDay, String payMode,
			String accNo, String esnNbr, String cargoDes, double weight, double volume, String cntr1, String cntr2,
			String cntr3, String cntr4, String bookingRefNo, String stuffind, String UserId, String category,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public String getBillablePartyName(String accNbr) throws BusinessException;

	public String getPkgsDesc(String esnNbr) throws BusinessException;

	public String getClsShipInd(String varNo) throws BusinessException;

	public int getUaNoPkgs(String esnNo) throws BusinessException;

	public boolean chkAccNo(String accNo) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getUserAccNo(String bookingRfnbr, String custId, String accNbr)
			throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getAccNo(String vslCd) throws BusinessException;

	public boolean chkVolume(String bookRefNo, double volume_s) throws BusinessException;

	public boolean chkWeight(String bookRefNo, double weight_s) throws BusinessException;

	public boolean chkPkgsType(String pkgs_Type) throws BusinessException;

	public boolean chkNoOfPkgs(String bookRefNo, int noOfpk) throws BusinessException;

	public boolean chkOutwardPM4(String bkRefNo, String vvCd) throws BusinessException;

	public boolean chkDttmOfSecondCarrierVsl(String vvCd) throws BusinessException;

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getBkRefNo(String bkRefNo, String cutId) throws BusinessException;

	public void AssignWhindUpdate(String crgval, String esnnbr, String whappnbr, String remarks, String nodays,
			String userId) throws BusinessException;

	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException;

	public void EsnAssignBillUpdate(String acctnbr, String esno, String userid) throws BusinessException;

	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getAssignCargo() throws BusinessException;

	public String AssignCrgvalCheck(String esnnbr) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getEdiDetails(String bkNbr) throws BusinessException;

	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException;

	public String getEdiUpdate(String bkrefnbr, String status) throws BusinessException;

	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException;

	public List<String> getWHDetails(String esnWhindcheck, String esnnbr) throws BusinessException;

	public String AssignWhindCheck(String esnnbr) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String esnNbr) throws BusinessException;

	public String getSchemeInd(String out_voyno) throws BusinessException;

	public String getScheme(String out_voyno) throws BusinessException;

	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException;

	public String getABactnbr(String voy_nbr) throws BusinessException;

	public String getVCactnbr(String voy_nbr) throws BusinessException;

	public String getSchemeName(String voy_nbr) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getABacctno(String out_voyno) throws BusinessException;

	public List<String> getSAacctno(String vv_cd) throws BusinessException;

	public VesselVoyValueObject getVessel(String vesselName, String outvoyNbr, String coCd) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException;

	public TableResult getEsnList(String selectVoyNo, String custId, Criteria criteria) throws BusinessException;

	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException;

}

package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface EsnRepository {
	public void updateShutEdoQtyAfterCancel(String esnAsnNbr, int qty, String userid) throws BusinessException;

	public boolean chkBKNo(String[] newbkrefnbr) throws BusinessException;

	public List<BookingReferenceValueObject> getBKDetails(String bkrNbr_arr) throws BusinessException;

	public List<EsnListValueObject> getEsndtls(String esnnbr) throws BusinessException;

	public String getEsnNoForDPE() throws BusinessException;

	public List<EsnListValueObject> getEsnDetails(String esnnbr, String string) throws BusinessException;

	public String getTruckerCd(String truckerNo) throws BusinessException;

	public String getUAfirsttransdttm(String esnnbr, String transtype) throws BusinessException;

	public String getEdiBlNbr(String bkrNbr_arr) throws BusinessException;

	public List<EsnListValueObject> getTesnPsaJpDetails(String esnnbr, String string) throws BusinessException;

	public List<EsnListValueObject> getJpJpDetails(String esnnbr) throws BusinessException;

	public String getMarkings(String esnnbr) throws BusinessException;

	public List<EsnListValueObject> getCntrDetails(String esnnbr) throws BusinessException;

	public List<EsnListValueObject> TransferCrgUpdateForDPE(String[] bk_ref_nbr, String[] newbkrefnbr, String[] esnarr,
			String[] transtypearr, String[] transNbr, String[] shutoutqty, String[] actnbrshped, String[] uanbrpkgs,
			String[] uaftdttm, String Toutvoynbr, String varnoF, String varnoT, String UserID) throws BusinessException;

	public List<VesselVoyValueObject> getVesselListSearch(String custId, String esnNo) throws Exception;

	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException;

	public List<EsnListValueObject> getAccNo(String truckerIcNo) throws BusinessException;

	public List<EsnListValueObject> getUserAccNo(String bookingRfnbr, String custId, String accNbr)
			throws BusinessException;

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;

	public boolean checkValidTrucker(String truckerIcNo) throws BusinessException;

	public boolean chkNoOfPkgs(String bookRefNo, int noOfpk) throws BusinessException;

	public boolean isOutWardPm(String bkRefNo, String vvCd) throws BusinessException;

	public boolean chkPkgsType(String pkgs_Type) throws BusinessException;

	public boolean chkWeight(String bookRefNo, double volume) throws BusinessException;

	public boolean chkVolume(String bookRefNo, double volume) throws BusinessException;

	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerNo,
			String truckerName, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNo, String cargoDes,
			String truckerCd, double weight, double volume, String truckerCNo, String cntr1, String cntr2, String cntr3,
			String cntr4, String stfInd, String strUAFlag, String strUserID, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerList, int truckerNbrPkgs, String custCd)
			throws BusinessException;

	// ejb.sessionBeans.gbms.cargo.esn -->Esn
	public boolean chkAccNo(String AccNo) throws BusinessException;

	public void assignWhindUpdate(String string, String asnNbr, String aplnRefNo, String cargoLocation, String string2,
			String verifyUserId) throws BusinessException;

	public String getVesselType(String bookref) throws BusinessException;

	public void updateCargoCategoryCode(String cargoCategoryCode, String bkRefNbr) throws BusinessException;

	public String getCategoryValue(String ccCd) throws BusinessException;

	public List<Map<String, Object>> getCategoryList() throws BusinessException;

	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException;

	public VesselVoyValueObject getVessel(String vesselName, String voyageNbr, String custId) throws BusinessException;

	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException;

	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId) throws BusinessException;

	public String getScheme(String out_voyno) throws BusinessException;

	public String getSchemeInd(String out_voyno) throws BusinessException;

	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException;

	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException;

	public String AssignWhindCheck(String esnnbr) throws BusinessException;

	public List<String> getWHDetails(String esnWhindcheck, String esnnbr) throws BusinessException;

	public String AssignCrgvalCheck(String esnnbr) throws BusinessException;

	public List<EsnListValueObject> getAssignCargo() throws BusinessException;

	public boolean checkExistSubAdp(String esnNo) throws BusinessException;

	public boolean isEsnCreator(String esnAsnNbr, String esnCreateCd) throws BusinessException;

	public List<EsnListValueObject> getBkRefNo(String bkRefNo, String cutId) throws BusinessException;

	public boolean checkDisbaleOverSideFroDPE(String varno) throws BusinessException;

	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException;

	public int getUaNoPkgs(String esnNo) throws BusinessException;

	public boolean isBillChargesRaised(String esnNo) throws BusinessException;

	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerName,
			String truckerNo, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNbr, String cargoDes,
			String truckerCd, double weight, double volume, String truckerCNo, String cntr1, String cntr2, String cntr3,
			String cntr4, String stfInd, String strUAFlag, String strUserID, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerVector, int truckerNbrPkgs, String custCd,
			String deliveryToEPC, String cntrSeqNbr, String miscAppNo,String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException; // esn

	public String getPkgsDesc(String esnNbr) throws BusinessException;

	public String getBillablePartyName(String accNbr) throws BusinessException;

	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException;

	public String getABactnbr(String voy_nbr) throws BusinessException;

	public String getVCactnbr(String voy_nbr) throws BusinessException;

	public String getSchemeName(String voy_nbr) throws BusinessException;

	public List<EsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException;

	public List<EsnListValueObject> getABacctno(String out_voyno) throws BusinessException;

	public List<String> getSAacctno(String vv_cd) throws BusinessException;

	public boolean checkDeleteEsn(String edoEsnno) throws BusinessException;

	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException;

	public void EsnAssignBillUpdate(String acctnbr, String esno, String userid) throws BusinessException;

	public void esnCancel(String esnNo, String bookingRefno, String strUAFlag, String strUserID)
			throws BusinessException;

	public String getEsnDeclared(String bookref) throws BusinessException;

	public String getTerminal(String bookref) throws BusinessException;

	public String getVvStatus(String bookref) throws BusinessException;

	public String getDeclarentCd(String bookref) throws BusinessException;

	public String getCrgTypeCd(String crgType) throws BusinessException;

	public String getClsVslInd(String bookref) throws BusinessException;

	public String getClsBjInd(String bookref) throws BusinessException;

	public String insertEsnDetailsForDPE(String varno, String truckerIcNo, String truckerCNo, String custCd,
			String bookingRefNo, String marking, String portD, String lopInd, String loadFrom, String dgIn,
			String hsCode, String dutiDI, String truckerName, int storageDay, String storageInd, String pkgsType,
			int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String truckerCd, String cntr1, String cntr2, String cntr3, String cntr4, String UserID,
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			// String strStfInd,String category) throws BusinessException, RemoteException {
			String strStfInd, String category, String hsSubCodeFr, String hsSubCodeTo, int trucker_nbr_pkgs,
			String deliveryToEPC, String cntr_seq_nbr, String miscAppNo, String customHsCode, List<HsCodeDetails> multiHsCodeList) // MCC for EPC_IND
			throws BusinessException;

	public void insertTruckerInfor(String truckerCiNo, String truckerName, String truckerContact, int truckerPackage,
			String truckerCd, String esnAsnNbr, String statusCd, int edoEsn, String userId) throws BusinessException;

	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException;

	public int getEsnListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException;

	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId, Criteria criteria)
			throws BusinessException;

	public List<VesselVoyValueObject> getTransferVslCrgList_T(String vslnm, String ovoynbr) throws BusinessException;

	public List<VesselVoyValueObject> getTransferVslCrgList_F(String vslnm, String ovoynbr) throws BusinessException;

	public String getTransferVarno(String vslnm, String voynbr, String ind, String cust_cd) throws BusinessException;

	public List<EsnListValueObject> getTransferDetails(String vv_cd, String cust_cd) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public String getClsShipInd(String s41) throws BusinessException;
	
	public String getBkStatus(String bookref) throws BusinessException;

	public Boolean getVesselATUDttm(String bookingRefNo) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException;

	public boolean updateCustomDetail(List<HsCodeDetails> multiHsCodeList,String esnNo, String userId) throws BusinessException;
}

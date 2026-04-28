package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardCargoESNService {

	public List<VesselVoyValueObject> getVesselList(String custCd) throws BusinessException;

	public VesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String custCd)
			throws BusinessException;

	public VesselVoyValueObject getVesselInfo(String selVoyno) throws BusinessException;

	public List<EsnListValueObject> getEsnList(String selVoyno, String custCd) throws BusinessException;

	public List<EsnListValueObject> getEsnDetails(String esnNo, String custCd) throws BusinessException;

	public List<EsnListValueObject> getCntrDetails(String s5) throws BusinessException;

	public List<TruckerValueObject> getTruckerList(String s5) throws BusinessException;

	public String getScheme(String s4) throws BusinessException;

	public String getSchemeInd(String s4) throws BusinessException;

	public String getClsShipInd_bkr(String s12) throws BusinessException;

	public void AssignCrgvalUpdate(String s13, String s5, String s1) throws BusinessException;

	public String AssignWhindCheck(String s5) throws BusinessException;

	public List<String> getWHDetails(String s17, String s5) throws BusinessException;

	public String AssignCrgvalCheck(String s5) throws BusinessException;

	public List<EsnListValueObject> getAssignCargo() throws BusinessException;

	public void AssignWhindUpdate(String s15, String s5, String s19, String s24, String s28, String s1)
			throws BusinessException;

	public boolean checkExistSubAdp(String s5) throws BusinessException;

	public boolean isEsnCreator(String esnAsnNbr, String companyCode) throws BusinessException;

	public String getVesselType(String bookingRefNo) throws BusinessException;

	public List<Map<String, Object>> getCategoryList() throws BusinessException;

	public List<EsnListValueObject> getBkRefNo(String bookingRefNo, String custCd) throws BusinessException;

	public boolean checkDisbaleOverSideFroDPE(String vvCd) throws BusinessException;

	public TruckerValueObject getTruckerDetails(String trucker_ciNo) throws BusinessException;

	public List<ManifestValueObject> getHSCodeList(String string) throws BusinessException;

	public String getCategoryValue(String category) throws BusinessException;

	public String getHSSubCodeDes(String s3, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException;

	public boolean validateGCStuffIndicatorCntr(String loadVVCd, String cntrNbr) throws BusinessException;

	public ContainerValueObject getContainerInformation(String cntrNbr) throws BusinessException;

	public boolean chkNoOfPkgs(String s32, int i) throws BusinessException;

	public boolean isOutWardPm(String s32, String s41) throws BusinessException;

	public boolean chkPkgsType(String s5) throws BusinessException;

	public boolean chkWeight(String s32, double d) throws BusinessException;

	public boolean chkVolume(String s32, double d1) throws BusinessException;

	public List<EsnListValueObject> getAccNo(String s19) throws BusinessException;

	public List<EsnListValueObject> getUserAccNo(String s32, String s1, String s73) throws BusinessException;

	public boolean chkAccNo(String s28) throws BusinessException;

	public int getUaNoPkgs(String s35) throws BusinessException;

	public String getClsShipInd(String s41) throws BusinessException;

	public boolean isBillChargesRaised(String s35) throws BusinessException;

	public void esnUpdateForDPE(int i, String s3, String s5, String s9, String trucker_nm, String trucker_ic,
			String s11, String s15, String s17, String s13, String s24, int j, String s26, String s31, String s28,
			String s35, String s7, String trucker_cd, double d, double d1, String trucker_ct, String s46, String s48,
			String s50, String s52, String stfInd, String strUAFlag, String s, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerVector, int trucker_pkg, String s1,
			String deliveryToEPC, String cntrSeqNbr, String appNo, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException; //esn

	public String getPkgsDesc(String s35) throws BusinessException;

	public String getBillablePartyName(String s28) throws BusinessException;

	public List<String> getSAacctno(String s41) throws BusinessException;

	public List<EsnListValueObject> getABacctno(String s41) throws BusinessException;

	public List<EsnListValueObject> getABacctnoForSA(String s41) throws BusinessException;

	public String getBPacctnbr(String s35, String s41) throws BusinessException;

	public String getSchemeName(String s41) throws BusinessException;

	public String getVCactnbr(String s41) throws BusinessException;

	public String getABactnbr(String s41) throws BusinessException;

	public void EsnAssignBillUpdate(String s59, String s35, String s) throws BusinessException;

	public void EsnAssignVslUpdate(String s41, String string, String s) throws BusinessException;

	public String getVesselScheme(String s41) throws BusinessException;

	public boolean checkDeleteEsn(String esnNo) throws BusinessException;

	public void esnCancel(String esnNo, String bookingRefNo, String strUAFlag, String strUserId)
			throws BusinessException;

	public boolean isAsnShut(String esnNo) throws BusinessException;

	public void updFtrans(String esnNo, String transtype, String ftransdtm, String userId) throws BusinessException;

	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException;

	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException;

	public List<UaListObject> getUAList(String esnNo) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public List<UaEsnListValueObject> getTransferredCargo(String s2) throws BusinessException;

	public boolean checkESNCntr(String s5) throws BusinessException;

	public List<VesselVoyValueObject> getVesselListSearch(String s, String s1) throws Exception;

	public String getEsnDeclared(String bookingRefNo) throws BusinessException;

	public String getBkStatus(String bookingRefNo) throws BusinessException;

	public String getDeclarentCd(String bookingRefNo) throws BusinessException;

	public String getVvStatus(String bookingRefNo) throws BusinessException;

	public String getTerminal(String bookingRefNo) throws BusinessException;

	public String getClsBjInd(String bookingRefNo) throws BusinessException;

	public String getClsVslInd(String bookingRefNo) throws BusinessException;

	public String getCrgTypeCd(String crgType) throws BusinessException;

	public String insertEsnDetailsForDPE(String varNbr, String truckerIcNo, String truckerCNo, String custCd,
			String bookingRefNo, String marking, String portD, String loadOperInd, String loadFrom, String dgInd,
			String hsCode, String dutyGoodInd, String truckerName, int noOfStorageDay, String storageInd,
			String pkgsType, int noOfPkgs, double weight, double volume, String accNo_I, String payMode,
			String cargoDesc, String truckerCd, String cntr1, String cntr2, String cntr3, String cntr4, String userID,
			String strStfInd, String category, String hsSubCodeFr, String hsSubCodeTo, int truckerPkg,
			String deliveryToEPC, String cntrSeqNbr, String appNo, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public void insertTruckerInfor(String string, String string2, String string3, int parseInt, String string4,
			String esnNo, String string5, int i, String userID) throws BusinessException;

	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException;

	public int getEsnListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException;

	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId, Criteria criteria)
			throws BusinessException;

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException;

	public CashSalesValueObject getCashSales(String ua_nbr) throws BusinessException;
	
	public String getUAtransDttm(String esnasnnbr) throws BusinessException;

	public List<String> indicationStatus(String selVoyno) throws BusinessException;

	public Boolean getVesselATUDttm(String bookingRefNo) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException;

	public boolean updateCustomDetail(List<HsCodeDetails> multiHsCodeList,String esnNo, String userId) throws BusinessException;
}

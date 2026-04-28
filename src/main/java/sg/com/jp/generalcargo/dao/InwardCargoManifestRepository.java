package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclaration;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails;
import sg.com.jp.generalcargo.domain.CargoManifest;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.CargoSelection;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HatchDetails;
import sg.com.jp.generalcargo.domain.HatchWisePackageDetail;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestActionTrailDetails;
import sg.com.jp.generalcargo.domain.ManifestCargoValueObject;
import sg.com.jp.generalcargo.domain.ManifestDetails;
import sg.com.jp.generalcargo.domain.ManifestPkgDimDetails;
import sg.com.jp.generalcargo.domain.ManifestUploadConfig;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.PackageDimension;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SystemConfigList;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoManifestRepository {

	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException;

	public List<ManifestValueObject> getManifestList(String vvcode, String coCode, Criteria criteria) throws BusinessException;
	
	public boolean isManClose(String vesselCd) throws BusinessException;

	public boolean chkVslStat(String varno) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal, String vslName) throws BusinessException;

	public List<VesselVoyValueObject> getVsNmVoy(String varNbr) throws BusinessException;

	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException;

	public String getCategoryValue(String ccCd) throws BusinessException;

	public boolean checkAddManifest(String varno, String coCd) throws BusinessException;

	public List<Map<String, String>> getCategoryList() throws BusinessException;

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException;

	public List<BookingReferenceValueObject> getBRVOList(String module) throws BusinessException;

	public boolean isShowAllCargoCategoryCode(String companyCode) throws BusinessException;

	public String getCompanyCodeAllCargoCategory() throws BusinessException;

	public String getNotShowCargoCategoryCode() throws BusinessException;

	public String getCarCarrierVesselCode() throws BusinessException;

	public String getDefaultCargoCategoryCode() throws BusinessException;

	public String getCargoTypeNotShow() throws BusinessException;

	public CompanyValueObject getCompanyInfo(String companyCode) throws BusinessException;

	public String MftInsertionForEnhancementHSCode(String distype, String addval, String coCd, String varno,
			String blno, String crgtyp, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc,
			String mark, String nopkgs, String gwt, String gvol, String crgstat, String dgind, String stgind,
			String dop, String pkgtyp, String coname, String consigneeCoyCode, String poL, String poD, String poFD,
			String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty,
			String adviseBy, String adviseDate, String adviseMode, String amendChargedTo, String waiveCharge,
			String waiveReason, String category, String deliveryToEPC, String userId, String selectedCargo, String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl)
			throws BusinessException;

	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd, String waiveReason) throws BusinessException;

	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException;

	public String insertMiscEvtLog(String type, String varno, String blno, String coCd) throws BusinessException;

	public String MftInsertion(String distype, String addval, String coCd, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String crg_category,
			String deliveryToEPC, String userId, String selectedCargo, String conAddr, String notifyParty,
			String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd,
			String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl) throws BusinessException;

	public String getPortName(String portcd) throws BusinessException;

	public String getPkgName(String pkgtype) throws BusinessException;

	public boolean chkEdonbrPkgs(String seqno, String varno, String blno) throws BusinessException;

	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException;

	public boolean chkDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException;

	public boolean chkTDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException;

	public boolean chkTnbrPkgs(String seqno, String varno, String blno) throws BusinessException;

	public String getClBjInd(String seqnbr) throws BusinessException;

	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException;

	public List<ManifestValueObject> getAddcrgList() throws BusinessException;

	public List<ManifestValueObject> getHSCodeList(String status, String query) throws BusinessException;

	public String getVesselTypeByVslNm(String vslNm) throws BusinessException;

	public boolean getUserAdminVessel(String coCd, String vesselNameLogin, String voyLogin, String blnum) throws BusinessException;

	public List<AccessCompanyValueObject> getAutPartyListOfVessel(String vvcode) throws BusinessException;

	public List<AccountValueObject> getListAmendmentChargedTo(String varno) throws BusinessException;

	public String getCreateCustCdOfVessel(String vvcode) throws BusinessException;

	public boolean checkDisbaleOverSideFroDPE(String varno) throws BusinessException;

	public int retrieveMaxCargoTon(String vvCd) throws BusinessException;

	public List<AccessCompanyValueObject> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException;

	public List<AccessCompanyValueObject> listCompany(String keyword, Integer start, Integer limit) throws BusinessException;

	public List<HSCode> getHSSubCodeList(String hsCd) throws BusinessException;

	public List<ManifestValueObject> getPkgList() throws BusinessException;

	public List<EsnListValueObject> getPkgList(String text) throws BusinessException;

	public List<ManifestValueObject> getPortList() throws BusinessException;

	public List<ManifestValueObject> getPortList(String portCd, String portName) throws BusinessException;

	public String MftUpdationWhenClosedDPE(String usrid, String coCd, String seqno, String varno, String blno,
			String crgdesc, String mark, String adviseBy, String adviseDate, String adviseMode, String amendChargedTo,
			String waiveCharge, String waiveReason, String hscd, String hsSubCodeFr, String hsSubCodeTo, String coname,
			String consigneeCoyCode, String selectedCargo, String conAddr, String notifyParty, String notifyPartyAddr,
			String placeDel, String placeReceipt, String shipperNm, String shipperAdd, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public String MftUpdationForEnhancementHSCode(String userID, String coCd, String seqno, String varno, String blno,
			String crgtype, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark,
			String nop, String gwt, String gvol, String crgstat, String dgind, String stgind, String dopind,
			String pkgtype, String coname, String consigneeCoyCode, String pol, String pod, String pofd,
			String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty,
			String adviseBy, String adviceDttm, String adviseMode, String amendChargedTo, String waiveCharge,
			String waiveReason, String category, String deliveryToEPC, String selectedCargo, String conAddr,
			String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm,
			String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID) throws BusinessException;

	public String getScheme(String voy_nbr) throws BusinessException;

	public String getSchemeInd(String voy_nbr) throws BusinessException;

	public List<ManifestValueObject> getCargoType() throws BusinessException;

	public List<String> getSAacctno(String voy_nbr) throws BusinessException;

	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException;

	public List<ManifestValueObject> getABacctno(String voy_nbr) throws BusinessException;

	public List<ManifestValueObject> getABacctnoForSA(String voy_nbr) throws BusinessException;

	public String getSchemeName(String voy_nbr) throws BusinessException;

	public String getVCactnbr(String voy_nbr) throws BusinessException;

	public String getABactnbr(String voy_nbr) throws BusinessException;

	public void MftAssignBillUpdate(String voy_nbr, String acctnbr, String seqno, String userid) throws Exception;

	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException;

	public void MftAssignCargoCategoryCargoTypeUpdate(String voy_nbr, String cargoCategory, String cargoType,
			String seqno, String userid) throws BusinessException;

	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException;

	public List<ManifestCargoValueObject> getMftAssignCargo() throws BusinessException;

	public List<MiscDetail> getCargoSelectionList(Criteria criteria) throws BusinessException;

	// NEW Features Addition BY NS

	public Long insertManifestExcelDetails(CargoManifestFileUploadDetails cargoManifestFileUploadDetails) throws BusinessException;

	public boolean updateManifestExcelDetails(Long seq_id, String string) throws BusinessException;

	public CargoManifestFileUploadDetails getCargoManifestFileUploadDetails(String seq_id) throws BusinessException;

	public List<ManifestUploadConfig> getTemplateHeader() throws BusinessException;

	public List<ManifestUploadConfig> getHatchTemplate() throws BusinessException;

	public List<CargoManifest> getManifestDetails(String vvCd) throws BusinessException;

	public PageDetails getVesselCallDetails(String vvCd) throws BusinessException;

	public List<String> getCargoTypeDropDown() throws BusinessException;

	public List<String> getPackagingTypeDropDown() throws BusinessException;

	public List<String> getDischargeTypeIndicatorDropdown() throws BusinessException;

	public List<String> getHs_code_sub_code() throws BusinessException;

	public int noOfHatchesOnPageLoad(String varCode) throws BusinessException;

	public List<String> getConsigneee() throws BusinessException;

	public List<String> getPortListForExcelProcessing(boolean withoutName) throws BusinessException;

	public List<HatchDetails> getManifestHatchDetails(String varCode) throws BusinessException;

	public List<CargoManifest> insertManifestData(List<CargoManifest> manifestRecords, String vvCd,
			String userId, String companyCode, List<String> mainHaveError, List<String> subHaveError, boolean isSplitBL) throws BusinessException;

	public List<MiscDetail> getCargoSelectionDropdown() throws BusinessException;

	public PageDetails manifestUploadDetail(String vvCd) throws BusinessException;

	public boolean insertManifest_action_trial(String vvCd, String typeCd, String summary, String lastTimestamp,
			String userId) throws BusinessException;

	public TableResult getManifestActionTrial(Criteria criteria) throws BusinessException;

	public String getTimeStamp() throws BusinessException;

	public ManifestActionTrailDetails getManifestActionTrialDetail(String mft_act_trl_id) throws BusinessException;

	// new
	public CargoSelection CargoSelectionData(String cargoSelection) throws BusinessException;

	public int mainfestDetailIsExist(String bl_nbr, String vvcd) throws BusinessException;

	// Region HatchBD
	public List<ManifestDetails> getManifestDetails(Criteria criteria) throws BusinessException;

	// public List<HatchDetails> getManifestHatchDetails(Criteria criteria);
	
	public List<ManifestDetails> getManifestHistoryDetailsForHBD(Criteria criteria) throws BusinessException;

	public List<HatchDetails> getManifestHatchBDHistoryDetails(Criteria criteria) throws BusinessException;

	public boolean saveManifestHatchDetails(HatchWisePackageDetail saveManifestHatchDet) throws BusinessException;

	// CargoDimensionDeclaration
	public List<CargoDimensionDeclaration> getCargoDimensionDeclarationInfo(String vvCd, String userId) throws BusinessException;

	public List<SystemConfigList> getCargoDimensionDeclarationInfo(String userId) throws BusinessException;

	public List<CargoDimensionDeclaration> getHsCodeInfo() throws BusinessException;

	public Result saveCargoDimensionDeclaration(List<CargoDimensionDeclaration> obj,String userAcct) throws BusinessException;
	
	// CD
	public TableResult getCargoDimensionList(Criteria criteria) throws BusinessException;

	public List<ManifestPkgDimDetails> getCargoDimensionDetails(Criteria criteria) throws BusinessException;

	public boolean saveCargoDimensionDetails(CargoDimensionDetails saveCargoDimensionDetails) throws BusinessException;

	//CDE
	List<ManifestUploadConfig> getPackagingTemplate() throws BusinessException;
	
	List<PackageDimension> getPackageDimensionDetails(String varCode) throws BusinessException;

	List<PackageDimension> insertPackagingData(List<PackageDimension> packageDimensionsRecords, String vvCd,
			String userId) throws BusinessException;

	List<CargoManifest> getManifestDetailsForPackage(String vvCd) throws BusinessException;
	
	public Boolean isManifestSubmissionAllowed(Criteria criteria) throws BusinessException;
	
	public Result validateTransferofManifest(Criteria criteria) throws BusinessException;

	public Result removeHatchBreakDownDetails(Criteria criteria) throws BusinessException;
	
	public String getTemplateVersionNo(String type) throws BusinessException;
	
	public List<ManifestPkgDimDetails> getCargoDimensionAuditDetails(Criteria criteria) throws BusinessException;

	//boolean chkDGInd(Connection con, String blno, String varno);

	public boolean chkDGInd(String cellData, String vvCd) throws BusinessException;
	
	public String getMftSeqNoForDelete(String vvCd,String blNo) throws BusinessException;
	
	public String getVesselCreatedCustomerCode(String vvCd) throws BusinessException;
	
	public Boolean vesselDeclarantExists(String vvCd) throws BusinessException;

	public boolean mftCancel(String userID, String seqno, String varno, String blno) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyTo(String cocode) throws BusinessException;

	public boolean isDisabledVolume(Criteria criteria) throws BusinessException;

	public boolean checkCloseLCT(String vvcd) throws BusinessException;

	// START CR FTZ HSCODE - NS JULY 2024

	public List<MiscDetail> loadHSSubCode(String query, String hsCode) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String seqno) throws BusinessException;

	public List<CargoManifest> getManifestHSDetails(String mft_seq_nbr) throws BusinessException;

	public CargoManifest getMainfestDetails(String bl_nbr, String vvcd) throws BusinessException;

	public boolean getMainfestHSDetails(String mftSeq, String hsCode, String hsCodeFr, String hsCodeTo) throws BusinessException;

	public List<String> getMainfestHSCode(String mft_seq_nbr) throws BusinessException;
	
	boolean checkMultipleMainfestHSDetails(String mftSeq) throws BusinessException;

	public List<CargoManifest> getMainfestHSDetail(CargoManifest manifestDetail) throws BusinessException;
	// END CR FTZ HSCODE - NS JULY 2024

	public String generateSplitBl(String blRoot, String vvcd) throws BusinessException;

	public List<ManifestUploadConfig> getSplitBlTemplateHeader() throws BusinessException;

	public List<ManifestUploadConfig> getSplitBlHatchTemplate() throws BusinessException;

	public boolean isMainandBLExist(String mainBL, String BLNbr) throws BusinessException;
	
	public TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException;

	boolean isShowManifestInfo(String companyCode, TextParaVO result) throws BusinessException;
}

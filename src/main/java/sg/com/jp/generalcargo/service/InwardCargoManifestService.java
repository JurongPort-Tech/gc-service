package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclaration;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HatchBreakDownPageDetail;
import sg.com.jp.generalcargo.domain.HatchWisePackageDetail;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestActionTrailDetails;
import sg.com.jp.generalcargo.domain.ManifestCargoValueObject;
import sg.com.jp.generalcargo.domain.ManifestPkgDimDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoManifestService {

	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException;

	public List<VesselVoyValueObject> getVsNmVoy(String varNbr) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal, String vslName) throws BusinessException;

	public boolean chkVslStat(String varno) throws BusinessException;

	public boolean isManClose(String vesselCd) throws BusinessException;

	public List<ManifestValueObject> getManifestList(String vvcode, String coCode, Criteria criteria) throws BusinessException;

	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException;

	public String getCategoryValue(String ccCd) throws BusinessException;

	public boolean checkAddManifest(String varno, String coCd) throws BusinessException;

	// Region Manifest Add/Amend
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
			String waiveReason, String category, String deliveryToEPC, String userId, String selectedCargo,
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl) throws BusinessException;

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

	public String fileUpload( MultipartFile uploadFile, String vvCd);

	public Summary processManifestDetails(MultipartFile uploadingFile,
			CargoManifestFileUploadDetails cargoManifestFileUploadDetails, String vvCd, String userId,
			String companyCode, boolean isSplitBL) throws BusinessException;

	public Resource fileDownload(String seq_id, String type) throws BusinessException;

	public XSSFWorkbook manifestDetailExcelDownload(String vvCd, boolean isSplitBL) throws BusinessException;

	public PageDetails manifestUploadDetail(String vvCd) throws BusinessException;

	public boolean insertActionTrial(String vvCd, String typeCd, Summary summary, String lastTimestamp, String userId) throws BusinessException;

	public TableResult getManifestActionTrail(Criteria criteria) throws BusinessException;

	public String getTimeStamp() throws BusinessException;

	public ManifestActionTrailDetails manifestActionTrailDetail(String mft_act_trl_id) throws BusinessException;

	//public Summary excelTemplateValidation(MultipartFile uploadingFile);

	//public Summary vesselDetailsValidation(MultipartFile file, String vvCd);

	public Boolean isManifestSubmissionAllowed(Criteria criteria) throws BusinessException;

	// Hatch BD
	public HatchBreakDownPageDetail getHatchBreakDownDetails(Criteria criteria) throws BusinessException;

	public boolean saveManifestHatchDetails(HatchWisePackageDetail saveManifestHatchDet) throws BusinessException;

	public HatchBreakDownPageDetail getHatchBreakDownHistoryDetail(Criteria criteria) throws BusinessException;

	// CargoDimensionDeclaration
	public List<CargoDimensionDeclaration> getCargoDimensionDeclaration(Criteria criteria) throws BusinessException;

	public Result saveCargoDimensionDeclaration(List<CargoDimensionDeclaration> declInfo,String userAcct) throws BusinessException;

	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException;

	// CargoDimension

	public TableResult getCargoDimensionList(Criteria criteria) throws BusinessException;

	public List<ManifestPkgDimDetails> getCargoDimensionDetails(Criteria criteria) throws BusinessException;

	public boolean saveCargoDimensionDetails(CargoDimensionDetails saveCargoDimensionDetails) throws BusinessException;

	// CDE
	public XSSFWorkbook packagingDownload(String vvCd) throws BusinessException;

	public Summary processPackagingExcel(MultipartFile uploadingFile,
			CargoManifestFileUploadDetails cargoManifestFileUploadDetails, String vvCd, String userId) throws BusinessException;
	
	public Result validateTransferofManifest(Criteria criteria) throws BusinessException;
	
	public Result removeHatchBreakDownDetails(Criteria criteria) throws BusinessException;
		
	public List<ManifestPkgDimDetails> getCargoDimensionAuditDetails(Criteria criteria) throws BusinessException;

	public String getVesselCreatedCustomerCode(String vvCd) throws BusinessException;
	
	public Boolean vesselDeclarantExists(String vvCd) throws BusinessException;

	public boolean mftCancel(String userID, String seqno, String varno, String blno) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyTo(String coCd) throws BusinessException;

	public boolean isDisabledVolume(Criteria criteria) throws BusinessException;

	public boolean checkCloseLCT(String vvcd) throws BusinessException;

	// START CR FTZ HSCODE - NS JULY 2024

	public List<MiscDetail> loadHSSubCode(String query, String hsCode) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String seqno) throws BusinessException;
	// END CR FTZ HSCODE - NS JULY 2024

	public String generateSplitBl(String blRoot, String vvcd) throws BusinessException;
}

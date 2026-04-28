package sg.com.jp.generalcargo.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.BkRefUploadConfig;
import sg.com.jp.generalcargo.domain.BookRefvoyageOutwardValueObject;
import sg.com.jp.generalcargo.domain.BookingReference;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoManifest;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface BookingRefRepository {

	public List<BookingReferenceValueObject> fetchBKDetails(String bkRefNo) throws BusinessException;

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException;

	public List<List<String>> getCargoType() throws BusinessException;

	public String chkCancelAmend(String bkRefNumber, String bkCmpCode, String mode) throws BusinessException;

	public boolean getCheckUserBookingReference(String coCd, String brNo) throws BusinessException;

	public int retrieveMaxCargoTon(String vvCd) throws BusinessException;

	public String getCargoTypeNotShow() throws BusinessException;

	public boolean isShowAllCargoCategoryCode(String companyCode) throws BusinessException;

	public String getNotShowCargoCategoryCode() throws BusinessException;

	public List<BookingReferenceValueObject> getBRVOList(String module) throws BusinessException;

	public String getAmendParaCargoTypeCode_CargoCategoryCode(String module) throws BusinessException;

	public String updateCargoTypeCargoCategory(String bookingRefNbr, String cargoCategoryCode, String cargoTypeCode)
			throws BusinessException;

	public List<BookRefvoyageOutwardValueObject> getVoyageName(String crNo) throws BusinessException;

	public List<VesselVoyValueObject> getVslDetailsForDPE(String fetchVesselName, String fetchVoyageNbr, String s6)
			throws BusinessException;

	public List<VesselVoyValueObject> getVslDetails(String varNo, String coCode) throws BusinessException;

	public List<BookingReferenceValueObject> getBKDetailsList(String varNo, String coCode, Criteria criteria)
			throws BusinessException;

	public String getCarCarrierVesselCode() throws BusinessException;

	public String updateBKForDPE(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
			String cntrSize, String vslId,
			// BEGIN amended by Maksym JCMS Smart CR 6.10
			// String outVoyNbr, String conrCode, String cargoType,
			String outVoyNbr, String conrCode, String cargoType, String cargoCategory,
			// END amended by Maksym JCMS Smart CR 6.10
			String shpCrNo, String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol,
			String bkNoOfPkg, String varPkgs, String varVol, String varWt, String portDis, String adpCustCd,
			String bkCmpCode, String user, boolean checkAmendConsignee, String conName, String consigneeAddr,
			String notifyParty, String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr, boolean isExcelUpload)
			throws BusinessException;

	public String chkPortCode(String portCode) throws BusinessException;

	public String chkCrNo(String crNo) throws BusinessException;

	public String chkQuantity(String bkWt, String bkVol, String bkNoOfPkg, String varPkgs, String varVol, String varWt,
			String bkRefNbr) throws BusinessException;

	public List<BookingReferenceValueObject> getBrSearchDetails(String bkRefNo, String coCode) throws BusinessException;

	public Hashtable<String, String> getVoyageDetails(String brn) throws BusinessException;

	public String cancelBK(String bkRefNbr, String userId) throws BusinessException;

	public String getVslTypeCdByFullName(String fullName) throws BusinessException;

	public String getDefaultCargoCategoryCode() throws BusinessException;

	public String chkBKCode(String bkCode) throws BusinessException;

	public String insertBK(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
			String cntrSize, String outVoyNbr,
			// BEGIN amended by Maksym JCMS Smart CR 6.10
			// String conrCode, String cargoType, String shpCrNo,
			String conrCode, String cargoType, String cargoCategory, String shpCrNo,
			// END amended by Maksym JCMS Smart CR 6.10
			String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol, String bkNoOfPkg,
			String varPkgs, String varVol, String varWt, String portDis, String esnCustCd, String bkCreateCD,
			String user, String conName, String consigneeAddr, String notifyParty, String notifyPartyAddr,
			String placeofDelivery, String placeofReceipt, String blNbr, boolean isExcelUpload) throws BusinessException;

	public int getBKDetailsListCount(String varNo, String coCode, Criteria criteria) throws BusinessException;

	public TableResult getPortCode(Criteria criteria) throws BusinessException;

	// START CR FTZ - NS JUNE 2024
	
	public PageDetails getBkRefDocumentDetail(String vvCd) throws BusinessException;

	public TableResult getBkActionTrail(Criteria criteria) throws BusinessException;

	public List<BkRefUploadConfig> getBkTemplateHeader() throws BusinessException;

	public List<BookingReference> getBkRefDetails(String vvCd) throws BusinessException; // excel data

	public Long insertBkrefExcelDetails(BookingReferenceFileUploadDetails bookingReferenceFileUploadDetails)
			throws BusinessException;
	
	public boolean updateBkExcelDetails(Long seq_id, String outputFileName) throws BusinessException; 

	public List<BookingReference> insertBkRefData(List<BookingReference> bkrefRecords, String vvCd, String userId,
			String companyCode) throws BusinessException;

	public void updateBlNbr(Criteria criteria) throws BusinessException;

	public boolean insertActionTrail(String vvCd, String summaryDet, String lastTimestamp, String userId) throws BusinessException;
	
	public BkRefActionTrailDetails getBkActionTrailDetail(String bk_act_trl_id) throws BusinessException;

	public Boolean isBkSubmissionAllowed(Criteria criteria) throws BusinessException;

	public BookingReferenceFileUploadDetails getCargoBkFileUploadDetails(String seq_id) throws BusinessException;

	public String getVarcode(Criteria criteria) throws BusinessException;

	public boolean checkATUDttm(String vvCd) throws BusinessException;

	public int bookingRefExist(String upperCellData, String vvCd) throws BusinessException;

	public String getTemplateVersionNo() throws BusinessException;

	// END CR FTZ - NS JUNE 2024
}

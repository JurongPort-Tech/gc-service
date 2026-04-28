package sg.com.jp.generalcargo.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.BookRefvoyageOutwardValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardCargoBookingReferenceService {

	public List<BookRefvoyageOutwardValueObject> getVoyageName(String s6) throws BusinessException;

	public List<VesselVoyValueObject> getVslDetailsForDPE(String fetchVesselName, String fetchVoyageNbr, String s6) throws BusinessException;

	public List<VesselVoyValueObject> getVslDetails(String vvCd, String s6) throws BusinessException;

	public List<BookingReferenceValueObject> getBKDetailsList(String s8, String s6,Criteria criteria) throws BusinessException;

	public List<List<String>> getCargoType() throws BusinessException;

	public Map<String, String> getCargoCategoryCode_CargoCategoryName()throws BusinessException;

	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws BusinessException;

	public String chkCancelAmend(String brno, String userCoyCode, String string) throws BusinessException;

	public boolean getCheckUserBookingReference(String coCd, String brno) throws BusinessException;

	public CompanyValueObject getCompanyInfo(String shipperCoyCode) throws BusinessException;

	public List<AccessCompanyValueObject> getAutPartyListOfVessel(String varno) throws BusinessException;

	public List<BookingReferenceValueObject> getBRVOList(String string) throws BusinessException;

	public boolean isShowAllCargoCategoryCode(String userCoyCode) throws BusinessException;

	public String getNotShowCargoCategoryCode() throws BusinessException;

	public String getVesselType(String bkRefNbr) throws BusinessException;

	public String getCarCarrierVesselCode() throws BusinessException;

	public String getCargoTypeNotShow() throws BusinessException;

	public String updateBKForDPE(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
			String cntrSize, String vslId, String outVoyNbr, String conrCode, String cargoType, String cargoCategory,
			String shpCrNo, String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol,
			String bkNoOfPkg, String varPkgs, String varVol, String varWt, String portDis, String adpCustCd,
			String bkCmpCode, String user, boolean checkAmendConsignee, String conName, String consigneeAddr,
			String notifyParty, String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr)
			throws BusinessException;

	public String chkPortCode(String portDisc) throws BusinessException;

	public String chkCrNo(String esnDecl) throws BusinessException;

	public String chkQuantity(String bkWt, String bkVol, String bkNoOfPkg,
			String varPkgs, String varVol, String varWt, String bkRefNbr) throws BusinessException;

	public int retrieveMaxCargoTon(String varno) throws BusinessException;

	public List<AccessCompanyValueObject> listCompanyStart(String keyword, int start, int limit) throws BusinessException;
	
	public List<AccessCompanyValueObject> listCompany(String keyword, Integer start, Integer limit) throws BusinessException;

	public List<BookingReferenceValueObject> getBrSearchDetails(String bkRefNo, String coCode) throws BusinessException;

	public Hashtable<String,String> getVoyageDetails(String brn) throws BusinessException;

	public String cancelBK(String bkRefNbr, String userId) throws BusinessException;

	public String getVslTypeCdByFullName(String vslName) throws BusinessException;

	public String getDefaultCargoCategoryCode() throws BusinessException;

	public String getCreateCustCdOfVessel(String varno) throws BusinessException;

	public String chkBKCode(String bkRefNbr) throws BusinessException;

	public String insertBK(String bkRefNbr, String crgStatus, String varno, String cntrNo, String contType,
			String contSize, String outVoyNbr, String conrCode, String cargoType, String cargoCategory, String shpCrNo,
			String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol, String bkNoOfPkg,
			String varPkgs, String varVol, String varWt, String portDis, String adpCustCd, String coCd, String userId,
			String conName, String consigneeAddr, String notifyParty, String notifyPartyAddr, String placeofDelivery,
			String placeofReceipt, String blNbr) throws BusinessException;

	public int getBKDetailsListCount(String varNo, String coCode,Criteria criteria) throws  BusinessException;

	public TableResult getPortCode(Criteria criteria) throws BusinessException;

	public List<String> indicationStatus(String vvCd) throws BusinessException;

	// START CR FTZ - NS JUNE 2024
	public XSSFWorkbook bkDetailExcelDownload(String vvCd, String coCd, Criteria criteria) throws BusinessException;

	public PageDetails getBkRefDocumentDetail(String vvCd) throws BusinessException;	

	public Resource fileDownload(String seq_id, String type) throws BusinessException;	

	public Boolean isBkSubmissionAllowed(Criteria criteria) throws BusinessException;

	public TableResult getBkActionTrail(Criteria criteria) throws BusinessException;

	public String fileUpload(MultipartFile uploadingFile, String vvCd) throws BusinessException;

	public String getTimeStamp() throws BusinessException;

	public Summary processBkrefDetails(MultipartFile uploadingFile,
			BookingReferenceFileUploadDetails bookingReferenceFileUploadDetails, String vvCd, String userId,
			String companyCode) throws BusinessException;

	public boolean insertActionTrail(String vvCd, Summary summary, String lastTimestamp,
			String userId) throws BusinessException;

	public void updateBlNbr(Criteria criteria) throws BusinessException;
	
	public String getVarcode(Criteria criteria) throws BusinessException;

	public BkRefActionTrailDetails bkActionTrailDetail(String bk_act_trl_id) throws BusinessException;
	// END CR FTZ - NS JUNE 2024


}

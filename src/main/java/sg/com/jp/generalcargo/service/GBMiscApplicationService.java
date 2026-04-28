package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.AttachmentFileValueObject;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CspsLinkValueObject;
import sg.com.jp.generalcargo.domain.EnquireQueryObject;
import sg.com.jp.generalcargo.domain.EnquireSummarySlotValueObject;
import sg.com.jp.generalcargo.domain.MiscAppParkingAreaObject;
import sg.com.jp.generalcargo.domain.MiscAppValueObject;
import sg.com.jp.generalcargo.domain.MiscBargeValueObject;
import sg.com.jp.generalcargo.domain.MiscContractValueObject;
import sg.com.jp.generalcargo.domain.MiscCustValueObject;
import sg.com.jp.generalcargo.domain.MiscHotworkValueObject;
import sg.com.jp.generalcargo.domain.MiscParkMacValueObject;
import sg.com.jp.generalcargo.domain.MiscReeferValueObject;
import sg.com.jp.generalcargo.domain.MiscSpaceValueObject;
import sg.com.jp.generalcargo.domain.MiscSpreaderValueObject;
import sg.com.jp.generalcargo.domain.MiscVehValueObject;
import sg.com.jp.generalcargo.domain.OvrNghtPrkgVehValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.StorageOrderValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBMiscApplicationService {

	public String checkVarcode(String varcode) throws BusinessException;

	public List<MiscAppValueObject> getVesselDetails(String varCode) throws BusinessException;

	public List<MiscAppValueObject> getCustomerList(String userId, String coCd, String custName)
			throws BusinessException;

	public List<MiscAppValueObject> getAccountList(String userId, String coCd, String custName)
			throws BusinessException;

	public List<MiscCustValueObject> getCustomerDetails(String userId, String cust, String account)
			throws BusinessException;

	public void addParkingOfLineTowBargeDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String bargeName, String bargeLOA, String draft, String tugboat,
			String contactNo, String fromDate, String toDate, String motherShip, String berthNo, String dg,
			String cargoType, String className, String coName, String appDate, String conPerson, String conTel)
			throws BusinessException;

	public void addContractorPermitDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String location, String description, String others, String fromDate,
			String toDate, String licType, String licNo, String remarks, String waiver, String contCoNm,
			String contCoAddr, String contactNm, String contactNric, String designation,
			List<MiscAppValueObject> docTypeList, List<String> docNameList, String appDate) throws BusinessException;

	public void addHotworkDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String location, String description, String fromDate, String toDate,
			String appDate) throws BusinessException;

	public List<MiscAppValueObject> getUploadDocumentList() throws BusinessException;

	public void addParkingOfForkliftShorecrane(String userId, String applyType, String status, String cust,
			String account, String varcode, String coName, String macType, String fromDate, String toDate,
			String remarks, List<MiscAppValueObject> docTypeList, List<String> docNameList, List<String> regNbrList,
			String appDate) throws BusinessException;

	public void addUseOfSpaceDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spaceType, String purpose, String fromDate, String toDate,
			String reason, String billNbr, String marks, String packages, String cargoDesc, String tonnage,
			String newMarks, String newPackages, String newCargoDesc, String newTonnage, String appDate,
			String conPerson, String conTel) throws BusinessException;

	public void addReeferContainerPowerOutletDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String[] cntrNo, String[] cntrSize, String[] cntrStatus, String coName,
			String appDate, String conPerson, String conTel) throws BusinessException;

	public boolean addSpreaderDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spreaderType, String fromDate, String toDate, String remarks,
			String appDate) throws BusinessException;

	/*
	 * public String addStationingOfMacDetails(String userId, String applyType,
	 * String status, String cust, String account, String varcode, String coName,
	 * MiscParkMacValueObject obj, String appDate) throws BusinessException;
	 */

	public void addOvernightParkingVehicleDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
			String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
			String conTel, String conEmail) throws BusinessException;

	public void addTpaDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String appRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApp, String cargoType) throws BusinessException;

	public List<Object> getStationingOfMacDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException;

	public void supportApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String supportDate) throws BusinessException;

	public String getClosingTime() throws BusinessException;

	public List<String> getVesselDetails() throws BusinessException;

	public List<MiscAppValueObject> getContractTypeList() throws BusinessException;

	public List<MiscAppValueObject> getContractUploadDocumentList() throws BusinessException;

	public List<MiscAppValueObject> getUploadDocumentTypeList() throws BusinessException;

	public List<MiscAppValueObject> getPurposeList() throws BusinessException;

	public List<String> checkMiscAppOnvChassis(String[] vehNo) throws BusinessException;

	public List<String> checkMiscAppOnvContainer(String[] cntNo) throws BusinessException;

	public List<String> checkMiscAppOnvAsn(String[] asnNo) throws BusinessException;

	public String processForTpaEmptyNormal(String userId, String applyType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String appRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApp, String cargoType) throws BusinessException;

	public List<Map<String, Object>> getParkingReasonList() throws BusinessException;

	public List<Object> getTrailerParkingApplicationDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public List<MiscAppParkingAreaObject> getParkingAreaList(String cargoType, String startDate, String toDate)
			throws BusinessException;

	public int getTpaDatePeriod() throws BusinessException;

	public List<MiscAppParkingAreaObject> getAvailableParkingSlots(String cargoType, String fromDate, String toDate)
			throws BusinessException;

	public List<MiscAppValueObject> getVoyageList(String selVessel) throws BusinessException;

	public List<String> getVesselList(String vesselName) throws BusinessException;

	// misc app top
	public List<MiscAppValueObject> getApplicationTypeList() throws BusinessException;

	public List<MiscAppValueObject> getApplicationStatusList() throws BusinessException;

	public List<CompanyValueObject> getCompanyList1() throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;
	// end

	// misc app void
	public void voidApplication(String userId, String coCd, String appSeqNbr) throws BusinessException;

	public void sendFlexiAlert(String alertCode, String smsMessage, String emailMessage, String emailSubject,
			String emailSender, String emailAttachmentFilename) throws BusinessException;
	// end

	public List<EnquireSummarySlotValueObject> enquireSummaryParkingSlot(EnquireQueryObject queryObj)
			throws BusinessException;

	// misc app List
	public String getUserName(String userId) throws BusinessException;

	public List<OvrNghtPrkgVehValueObject> getOvernightParkingVehList(String type) throws BusinessException;

	public TableResult getApplicationList(String coCd, String appType, String refNo, String appStatus,
			String appFromDttm, String appToDttm, String payMode, String companyCD, String machineNo, String vehicleNo,
			String containerNo, Criteria criteria, Boolean allData) throws BusinessException;

	public int checkApprovePermission(String loginId, String appType) throws BusinessException;

	public List<Object> getParkingOfLineTowBargeDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public String[] fileUpload(MultipartFile[] uploadFile, String userId) throws BusinessException;

	public Resource fileDownload(Criteria criteria) throws BusinessException;

	public List<Object> getContractorPermitDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public List<Object> getUseOfSpaceDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException;

	public List<StorageOrderValueObject> getMotCntrList(String refNo, String appSeqNbr) throws BusinessException;

	public List<StorageOrderValueObject> getMotCntrSummary(String refNo, String appSeqNbr) throws BusinessException;

	public List<Object> getHotworkDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException;

	public List<Object> getParkingOfForkliftShorecrane(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public List<Object> getReeferContainerPowerOutletDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public List<Object> getOvernightParkingVehicleDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException;

	public List<Object> getSpreaderDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException;

	public List<Object> listParkingSlotForEnquire(EnquireQueryObject queryObj, Criteria criteria, Boolean excel)
			throws BusinessException;

	public void updateParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscBargeValueObject obj, String conPerson,
			String conTel) throws BusinessException;

	public void updateContractorPermitDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscContractValueObject obj) throws BusinessException;

	public void updateHotworkDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscHotworkValueObject obj) throws BusinessException;

	public void updateParkingOfForkliftShorecrane(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException;

	public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpaceValueObject obj, String conPerson, String conTel)
			throws BusinessException;

	public boolean updateSpreaderDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpreaderValueObject obj) throws BusinessException;

	public void updateStationingOfMacDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException;

	public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String appType, String account, String appStatusCd, String conPerson, String conTel,
			String conEmail) throws BusinessException;

	public void updateReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscReeferValueObject obj, String conPerson,
			String conTel) throws BusinessException;

	public void approveBargeApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			MiscBargeValueObject obj, String approveDate) throws BusinessException;

	public void approveContractApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate, String waiveInd, String reasonWaive) throws BusinessException;

	public void approveApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate) throws BusinessException;

	public String getTpaBlockOfHours() throws BusinessException;

	public TableResult getApproveTpaList(String startDate, String toDate, Criteria criteria)
			throws BusinessException;

	public List<Object> getTpaForApproveDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException;

	public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList(String string, String cargoType,
			String string2, String string3) throws BusinessException;

	public void approveTpaApplication(String[] areaCd, String[] slotCd, String userId, String applicationStatus,
			String miscSeqNbr, String remarks, String approveDate) throws BusinessException;

	public void approveOnvApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate) throws BusinessException;

	public void approveBillHotworkApplication(String userId, String miscSeqNbr, String approveBillDate)
			throws BusinessException;

	public void closeBillHotworkDetails(String userId, String miscSeqNbr, MiscHotworkValueObject obj, String closeDate)
			throws BusinessException;

	public void closeBillOvernightParkingVehicleDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException;

	public void closeBillParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, MiscBargeValueObject obj,
			String closeDate) throws BusinessException;

	public void closeBillTrailerParkingApplicationDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException;

	public void closeBillSpreaderDetails(String userId, String miscSeqNbr, MiscSpreaderValueObject obj,
			String closeDate) throws BusinessException;

	public void closeBillReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, MiscReeferValueObject obj,
			String closeDate) throws BusinessException;

	public String updateForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String parkReason, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType, String miscSeqNbr,
			String appStatusCd) throws BusinessException;

	public void updateTrailerParkingApplicationDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noHours, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String appType, String account, String appStatusCd, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
			throws BusinessException;

	public void approveSpaceApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			MiscSpaceValueObject obj, String approveDate) throws BusinessException;

	public void closeBillUseOfSpaceDetails(String userId, String miscSeqNbr, MiscSpaceValueObject obj, String closeDate)
			throws BusinessException;

	public List<CspsLinkValueObject> getAreaListBasedOnStorageZone(String stgType, String stgZone)
			throws BusinessException;

	public List<CspsLinkValueObject> getLocationListBasedOnLocationType(String locType) throws BusinessException;

	public Result saveFileAttachment(Criteria criteria, MultipartFile[] uploadingFiles) throws BusinessException;

	public List<AttachmentFileValueObject> getFileAttachmentList(Criteria criteria) throws BusinessException;

	public void deleteFileData(String userId, String miscSeqNbr, String assignedName) throws BusinessException;

	public String addStationingOfMacDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, MiscParkMacValueObject obj, String appDate) throws BusinessException;

	public List<String> getParkingAreaList() throws BusinessException;
	
	public List<String> getDelFile(String fileNames, String miscSeqNbr, String Type, String catCd) throws BusinessException;
	// end
}

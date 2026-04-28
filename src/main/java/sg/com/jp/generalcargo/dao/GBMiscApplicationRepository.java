package sg.com.jp.generalcargo.dao;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.AttachmentFileValueObject;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EnquireQueryObject;
import sg.com.jp.generalcargo.domain.EnquireSummarySlotValueObject;
import sg.com.jp.generalcargo.domain.ExpiredCompanyValueObject;
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
import sg.com.jp.generalcargo.domain.TpaDetailsHistoryVO;
import sg.com.jp.generalcargo.domain.TpaVO;
import sg.com.jp.generalcargo.domain.TypeCdVO;
import sg.com.jp.generalcargo.domain.VehicleDetailsVO;
import sg.com.jp.generalcargo.domain.VehicleVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBMiscApplicationRepository {

	// public String checkVarcode(String varcode) throws BusinessException;

	// public ArrayList getVesselDetails(String varCode) throws BusinessException;

	// public ArrayList getCustomerList(String userId, String coCd, String custName) throws BusinessException;

	// public ArrayList getAccountList(String userId, String coCd, String custName) throws BusinessException;

	// public ArrayList getCustomerDetails(String userId, String cust, String account) throws BusinessException;

	
	// misc app list repository
	
		public String getUserName(String userId) throws BusinessException;

		public List<OvrNghtPrkgVehValueObject> getOvernightParkingVehList(String type) throws BusinessException;

		public TableResult getApplicationList(String coCd, String appType, String refNo, String appStatus, String appFromDttm,
				String appToDttm, String payMode, String companyCD, String machineNo, String vehicleNo, String containerNo, Criteria criteria, Boolean allData)
				throws BusinessException;

		public int checkApprovePermission(String loginId, String appType) throws BusinessException;

		public List<MiscAppValueObject> getApplicationTypeList() throws BusinessException;

		public List<CompanyValueObject> getCompanyList1() throws BusinessException;

		public List<MiscAppValueObject> getApplicationStatusList() throws BusinessException;

		public List<MiscAppValueObject> getPurposeList() throws BusinessException;

		public List<MiscAppValueObject> getUploadDocumentTypeList() throws BusinessException;

		public List<MiscAppValueObject> getContractTypeList() throws BusinessException;

		public List<MiscAppValueObject> getUploadDocumentList() throws BusinessException;

		public List<MiscAppValueObject> getContractUploadDocumentList() throws BusinessException;

		public List<MiscAppValueObject> getCustomerList(String userId, String coCd, String custName) throws BusinessException;

		public List<MiscAppValueObject> getAccountList(String userId, String coCd, String custName) throws BusinessException;

		public List<String> getVesselList(String vesselName) throws BusinessException;

		public List<MiscAppValueObject> getVoyageList(String vesselName) throws BusinessException;

		public String checkVarcode(String varcode) throws BusinessException;

		public List<MiscCustValueObject> getCustomerDetails(String userId, String cust, String account) throws BusinessException;

		public void addOvernightParkingVehicleDetails(String userId, String applyType, String applyStatus, String cust,
				String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
				String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
				String conTel) throws BusinessException;

		public void addOvernightParkingVehicleDetails(String userId, String applyType, String applyStatus, String cust,
				String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
				String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
				String conTel, String conEmail) throws BusinessException;

		public List<String> getVesselDetails() throws BusinessException;

		public void addParkingOfLineTowBargeDetails(String userId, String applyType, String applyStatus, String cust,
				String account, String varcode, String bargeName, String bargeLOA, String draft, String tugboat,
				String contactNo, String fromDate, String toDate, String motherShip, String berthNo, String dg,
				String cargoType, String className, String coName, String appDate, String conPerson, String conTel)
				throws BusinessException;

		public void addReeferContainerPowerOutletDetails(String userId, String applyType, String status, String cust,
				String account, String varcode, String[] cntrNo, String[] cntrSize, String[] cntrStatus, String coName,
				String appDate, String conPerson, String conTel) throws BusinessException;

		public void addUseOfSpaceDetails(String userId, String applyType, String status, String cust, String account,
				String varcode, String coName, String spaceType, String purpose, String fromDate, String toDate,
				String reason, String billNbr, String marks, String packages, String cargoDesc, String tonnage,
				String newMarks, String newPackages, String newCargoDesc, String newTonnage, String appDate,
				String conPerson, String conTel) throws BusinessException;

		public String addParkingOfForkliftShorecrane(String userId, String applyType, String status, String cust,
				String account, String varcode, String coName, String macType, String fromDate, String toDate,
				String remarks, List<MiscAppValueObject> docTypeList, List<String> docNameList, List<String> regNbrList, String appDate)
				throws BusinessException;

		public String addStationingOfMacDetails(String userId, String applyType, String status, String cust, String account,
				String varcode, String coName, MiscParkMacValueObject obj, String appDate) throws BusinessException;

		public String addContractorPermitDetails(String userId, String applyType, String status, String cust, String account,
				String varcode, String coName, String location, String description, String others, String fromDate,
				String toDate, String licType, String licNo, String remarks, String waiver, String contCoNm,
				String contCoAddr, String contactNm, String contactNric, String designation, List<MiscAppValueObject> docTypeList,
				List<String> docNameList, String appDate) throws BusinessException;

		public boolean addSpreaderDetails(String userId, String applyType, String status, String cust, String account,
				String varcode, String coName, String spreaderType, String fromDate, String toDate, String remarks,
				String appDate) throws BusinessException;

		public void addHotworkDetails(String userId, String applyType, String status, String cust, String account,
				String varcode, String coName, String location, String description, String fromDate, String toDate,
				String appDate) throws BusinessException;

		public List<Object> getOvernightParkingVehicleDetails(String userId, String applyType, String appSeqNbr,
				String applyTypeNm) throws BusinessException;

		public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
				String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
				String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel)
				throws BusinessException;
		// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

		public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
				String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
				String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel,
				String conEmail) throws BusinessException;

		public List<Object> getStationingOfMacDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		public void updateStationingOfMacDetails(String userId, String miscSeqNbr, String status, String coName,
				String applyType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException;

		public List<Object> getParkingOfLineTowBargeDetails(String userId, String applyType, String appSeqNbr,
				String applyTypeNm) throws BusinessException;

		public void updateParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, String status, String coName,
				String appType, String account, String appStatusCd, MiscBargeValueObject obj, String conPerson,
				String conTel) throws BusinessException;

		public List<Object> getParkingOfForkliftShorecrane(String userId, String applyType, String appSeqNbr,
				String applyTypeNm) throws BusinessException;

		public void updateParkingOfForkliftShorecrane(String userId, String miscSeqNbr, String status, String coName,
				String applyType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException;

		public List<Object> getReeferContainerPowerOutletDetails(String userId, String applyType, String appSeqNbr,
				String applyTypeNm) throws BusinessException;

		public void updateReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, String status, String coName,
				String appType, String account, String appStatusCd, MiscReeferValueObject obj, String conPerson,
				String conTel) throws BusinessException;
		// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

		public List<Object> getUseOfSpaceDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		/*
		 * public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String
		 * status, String coName, String appType, String account, String appStatusCd,
		 * MiscSpaceValueObject obj) throws BusinessException;
		 */
		// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
		public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
				String account, String appStatusCd, MiscSpaceValueObject obj, String conPerson, String conTel)
				throws BusinessException;
		// Added on 08/06/2007 by Punitha. To add Contact Person and Contact Tel

		public List<Object> getSpreaderDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		public boolean updateSpreaderDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
				String account, String appStatusCd, MiscSpreaderValueObject obj) throws BusinessException;

		public List<Object> getHotworkDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		public void updateHotworkDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
				String account, String appStatusCd, MiscHotworkValueObject obj) throws BusinessException;

		public List<Object> getContractorPermitDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		public void updateContractorPermitDetails(String userId, String miscSeqNbr, String status, String coName,
				String applyType, String account, String appStatusCd, MiscContractValueObject obj) throws BusinessException;

		public void voidApplication(String userId, String coCd, String appSeqNbr) throws BusinessException;

		public void supportApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				String supportDate) throws BusinessException;

		public void approveApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				String approveDate) throws BusinessException;

		public void approveOnvApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				String approveDate) throws BusinessException;

		public void approveContractApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				String approveDate, String waivePermit, String reasonWaive) throws BusinessException;

		public void approveBargeApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				MiscBargeValueObject obj, String approveDate) throws BusinessException;

		public void approveBillHotworkApplication(String userId, String appSeqNbr, String approveBillDate)
				throws BusinessException;

		public void closeBillOvernightParkingVehicleDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
				String closeDate) throws BusinessException;

		public void closeBillParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, MiscBargeValueObject obj,
				String closeDate) throws BusinessException;

		public void closeBillSpreaderDetails(String userId, String miscSeqNbr, MiscSpreaderValueObject obj,
				String closeDate) throws BusinessException;

		public void closeBillReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, MiscReeferValueObject obj,
				String closeDate) throws BusinessException;

		public void closeBillUseOfSpaceDetails(String userId, String miscSeqNbr, MiscSpaceValueObject obj, String closeDate)
				throws BusinessException;

		public void closeBillHotworkDetails(String userId, String miscSeqNbr, MiscHotworkValueObject obj, String closeDate)
				throws BusinessException;

		public String uploadDocument(String userId, String miscSeqNbr, String status, String docType, String docName)
				throws BusinessException;

		public List<MiscParkMacValueObject> getUploadDocumentDetails(String appSeqNbr) throws BusinessException;

		public List<MiscContractValueObject> getContractUploadDocumentDetails(String appSeqNbr) throws BusinessException;

		public void deleteDocument(String miscSeqNbr, String[] docName) throws BusinessException;

		public String getNextDocSeqNumber() throws BusinessException;

		public String getClosingTime() throws BusinessException;

		public List<MiscAppValueObject> getVesselDetails(String varCode) throws BusinessException;

		public String getApproverId(String applnSeqNbr) throws BusinessException;

		public String getWaiveIndicator(String appSeqNbr) throws BusinessException;

		public List<ExpiredCompanyValueObject> getExpiredCompanyList() throws BusinessException;

		public List<String> getAllMailAccounts() throws BusinessException;

		public String getSenderAccount() throws BusinessException;

		public void approveSpaceApplication(String userId, String appStatus, String appSeqNbr, String remarks,
				MiscSpaceValueObject obj, String approveDate) throws BusinessException;

		public List<StorageOrderValueObject> getMotCntrList(String refNo, String appSeqNbr) throws BusinessException;

		public List<StorageOrderValueObject> getMotCntrSummary(String refNo, String appSeqNbr) throws BusinessException;

		public void addTpaDetails(String userId, String appType, String status, String cust, String account, String varcode,
				String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo, String[] cntNo,
				String[] asnNo, String coName, String appDate, String conPerson, String conTel, String[] preferredArea,
				String[] remarks, String reasonForApplication, String cargoType) throws BusinessException;

		public List<Object> getTrailerParkingApplicationDetails(String userId, String applType, String appSeqNbr,
				String applyTypeNm) throws BusinessException;

		public List<Map<String, Object>> getParkingReasonList() throws BusinessException;

		public List<MiscAppParkingAreaObject> getParkingAreaList(String slotType, String startDate, String toDate) throws BusinessException;

		public List<Object> getTpaForApproveDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
				throws BusinessException;

		public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList(String areaCd, String slotType, String startDate, String toDate)
				throws BusinessException;

		public void approveTpaApplication(String[] areaCd, String[] slotCd, String userId, String appStatus,
				String appSeqNbr, String remarks, String approveDate) throws BusinessException;

		public TableResult getApproveTpaList(String startDate, String toDate, Criteria criteria) throws BusinessException;

		public String getTpaBlockOfHours() throws BusinessException;

		public void closeBillTrailerParkingApplicationDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
				String closeDate) throws BusinessException;

		public String processForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
				String varcode, String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo,
				String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
				String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
				throws BusinessException;

		public int getTpaDatePeriod() throws BusinessException;

		public void updateTrailerParkingApplicationDetails(String userId, String miscSeqNbr, String status, String fromDate,
				String toDate, String noHours, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
				String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel,
				String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
				throws BusinessException;

		public String updateForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
				String varcode, String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo,
				String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
				String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType, String miscSeqNbr,
				String appStatusCd) throws BusinessException;

		public List<MiscAppParkingAreaObject> getAvailableParkingSlots(String slotType, String startDate, String toDate)
				throws BusinessException;

		public List<EnquireSummarySlotValueObject> enquireSummaryParkingSlot(EnquireQueryObject queryObj) throws BusinessException;

		public List<Object> listParkingSlotForEnquire(EnquireQueryObject queryObj, Criteria criteria, Boolean excel) throws BusinessException;

		public List<TpaVO> getTpaList(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters)
				throws BusinessException;

		public int getCountRecords_TpaList(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters)
				throws BusinessException;

		public List<TpaVO> getActiveTpaList(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters)
				throws BusinessException;

		public List<TpaVO> getTpaListForDownloading(Integer start, Integer limit, String sort, String dir,
				Map<String, Object> filters) throws BusinessException;

		public List<VehicleDetailsVO> getTpaVehicleDetails(Integer start, Integer limit, String sort, String dir,
				Map<String, Object> filters) throws BusinessException;

		public List<VehicleDetailsVO> getTpaVehicleDetailsByMiscSeqNumber(String miscSeqNumber)
				throws BusinessException;

		public List<TpaDetailsHistoryVO> getTpaDetailsHistory(Integer start, Integer limit, String sort, String dir,
				Map<String, Object> filters) throws BusinessException;

		public void voidApplication4NewTpa(TpaVO vo) throws BusinessException;

		public void closeTpaForBill(Map<String, Object> filters) throws BusinessException;

		public void extendReduceTpa(Map<String, Object> filters) throws BusinessException;

		public TpaVO getTpaDetails(String misc_seq_nbr) throws BusinessException;

		public List<MiscAppValueObject> getInVoyageList(String vesselName) throws BusinessException;

		public List<MiscAppValueObject> getOutVoyageList(String vesselName) throws BusinessException;

		public List<Map<String, Object>> getMiscTypeCode(String catCdParam, String miscTypeCdParam) throws BusinessException;

		public List<MiscAppParkingAreaObject> getParkingAreaList4NewTpa(String slotType, String startDate, String toDate, String trailerSize,
				String trailerType) throws BusinessException;

		public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList4NewTpa(String areaCd, String slotType, String startDate,
				String toDate, String trailerSize, String trailerType) throws BusinessException;

		public List<MiscAppParkingAreaObject> getAvailableParkingSlots4NewTpa(String slotType, String startDate, String toDate,
				String trailerSize, String trailerType) throws BusinessException;

		public Boolean checkActiveASNNo(String asnNo, String vv_cd) throws BusinessException;

		public Boolean checkActiveContainerNo(String containerNo, String vv_cd) throws BusinessException;

		public TypeCdVO getCargoContainerStatus(String cntrAsnNo, Boolean isCntr) throws BusinessException;

		public String getPM4ID(String cntrAsnNo, String varCode, Boolean isCntr) throws BusinessException;

		public List<ContainerValueObject> getOogDimension(String cntrNo, String varCode) throws BusinessException;

		public String processForNewTpaEmptyNormal(MiscAppValueObject miscApp, VehicleVO vehVo, String vehDet,
				int hoursPerBlock) throws BusinessException;

		public String submitNewTpa(MiscAppValueObject miscApp, VehicleVO veh, String vehDet) throws BusinessException;

		public String approveRejectNewTpa(String userId, String status, String remarks, String misc_seq_nbr,
				String vehicleItems, String apprDttm, String preStatus, int hoursPerBlock) throws BusinessException;

		public void updateNewTpa(MiscAppValueObject miscApp, VehicleVO veh, String vehDet) throws BusinessException;

		public String getEmailAddress(String userAcct) throws BusinessException;

		public List<MiscAppValueObject> getCustomerList4NewTpa(String userId, String coCd, String custName) throws BusinessException;

		public void updateVehForApproveReject(String userId, String misc_seq_nbr, String vehicleItems, List<Object> vector)
				throws BusinessException;

		public VehicleVO getVehicle4NewTPA(String miscSeqNumber) throws BusinessException;

		public List<MiscAppValueObject> getMiscAppTypePendingCases() throws BusinessException;

		public boolean validateGCStuffIndicatorMiscApp(String refNbr) throws BusinessException;

		public List<String> checkMiscAppOnvChassis(String[] chassisNo) throws BusinessException;

		public List<String> checkMiscAppOnvContainer(String[] containerNo) throws BusinessException;

		public List<String> checkMiscAppOnvAsn(String[] asn) throws BusinessException;

		public  List<AttachmentFileValueObject> getFileAttachmentList (String miscSeqNumber, String string) throws BusinessException;

		public Result saveFileAttachment(AttachmentFileValueObject attObj) throws BusinessException;

		public AttachmentFileValueObject getFileAttachment(String miscSeqNumber,String docType) throws BusinessException;

		public void deleteFileData(String userId, String miscSeqNbr, String assignedName) throws BusinessException;
		
		public Map<String,String> getUserInfo(String userAccount) throws BusinessException;
		
		// gb misc app top
		// public ArrayList getApplicationTypeList() throws BusinessException;

		// public ArrayList getApplicationStatusList() throws BusinessException;

		// public ArrayList getCompanyList1() throws BusinessException;

		public String getCompanyName(String coCd) throws BusinessException;
		// end
		
		// gb misc app void 
		// exception alert repo
		public void sendFlexiAlert (
	    		String alertCode,			
	    		String smsMessage,			
	    		String emailMessage,			
	    		String emailSubject,			
	    		String emailSender,			
	    		String emailAttachmentFilename	
	    		) throws BusinessException;
		// end

		public List<String> getDelFile(String fileNames, String miscSeqNbr, String type, String catCd) throws BusinessException;
}

package sg.com.jp.generalcargo.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.controller.UploadDocument;
import sg.com.jp.generalcargo.dao.CspsLinkRepository;
import sg.com.jp.generalcargo.dao.GBMiscApplicationRepository;
import sg.com.jp.generalcargo.dao.MaintenanceTpaRepo;
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
import sg.com.jp.generalcargo.service.GBMiscApplicationService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Service
public class GBMiscApplicationServiceImpl implements GBMiscApplicationService {

	@Autowired
	private GBMiscApplicationRepository gbMiscApplicationRepository;
	
	@Autowired
	private MaintenanceTpaRepo maintenanceTpaRepo;
	
	@Value("${MiscApp.file.upload.path}")
	String folderPath;
	
	@Autowired
	private CspsLinkRepository cspsLinkRepo;
	
	private static final Log log = LogFactory.getLog(GBMiscApplicationServiceImpl.class);
	
	// @Autowired
	//private gbMiscApplicationRepositorysitory gbMiscApplicationRepository;
	
	@Override
	public String checkVarcode(String varcode) throws BusinessException {

		return gbMiscApplicationRepository.checkVarcode(varcode);
	}

	@Override
	public List<MiscAppValueObject> getVesselDetails(String varCode) throws BusinessException {

		return gbMiscApplicationRepository.getVesselDetails(varCode);
	}

	@Override
	public List<MiscAppValueObject> getCustomerList(String userId, String coCd, String custName) throws BusinessException {

		return gbMiscApplicationRepository.getCustomerList(userId, coCd, custName);
	}
	@Override
	public List<Object> getStationingOfMacDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getStationingOfMacDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}
	@Override
	public List<MiscAppValueObject> getAccountList(String userId, String coCd, String custName) throws BusinessException {

		return gbMiscApplicationRepository.getAccountList(userId, coCd, custName);
	}

	@Override
	public List<MiscCustValueObject> getCustomerDetails(String userId, String cust, String account) throws BusinessException {

		return gbMiscApplicationRepository.getCustomerDetails(userId, cust, account);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addParkingOfLineTowBargeDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String bargeName, String bargeLOA, String draft, String tugboat,
			String contactNo, String fromDate, String toDate, String motherShip, String berthNo, String dg,
			String cargoType, String className, String coName, String appDate, String conPerson, String conTel)
			throws BusinessException {
		
		gbMiscApplicationRepository.addParkingOfLineTowBargeDetails(userId, applyType, status, cust, account, varcode, bargeName, bargeLOA, draft, tugboat, contactNo, fromDate, toDate, motherShip, berthNo, dg, cargoType, className, coName, appDate, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addContractorPermitDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String location, String description, String others, String fromDate,
			String toDate, String licType, String licNo, String remarks, String waiver, String contCoNm,
			String contCoAddr, String contactNm, String contactNric, String designation, List<MiscAppValueObject> docTypeList,
			List<String> docNameList, String appDate) throws BusinessException {
		
		gbMiscApplicationRepository.addContractorPermitDetails(userId, applyType, status, cust, account, varcode, coName, location, description, others, fromDate, toDate, licType, licNo, remarks, waiver, contCoNm, contCoAddr, contactNm, contactNric, designation, docTypeList, docNameList, appDate);
	}
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addHotworkDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String location, String description, String fromDate, String toDate,
			String appDate) throws BusinessException {
		
		gbMiscApplicationRepository.addHotworkDetails(userId, applyType, status, cust, account, varcode, coName, location, description, fromDate, toDate, appDate);
	}

	@Override
	public List<MiscAppValueObject> getUploadDocumentList() throws BusinessException {
		
		return gbMiscApplicationRepository.getUploadDocumentList();
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addParkingOfForkliftShorecrane(String userId, String applyType, String status, String cust,
			String account, String varcode, String coName, String macType, String fromDate, String toDate,
			String remarks, List<MiscAppValueObject> docTypeList, List<String> docNameList, List<String> regNbrList, String appDate)
			throws BusinessException {
		
		gbMiscApplicationRepository.addParkingOfForkliftShorecrane(userId, applyType, status, cust, account, varcode, coName, macType, fromDate, toDate, remarks, docTypeList, docNameList, regNbrList, appDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addUseOfSpaceDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spaceType, String purpose, String fromDate, String toDate,
			String reason, String billNbr, String marks, String packages, String cargoDesc, String tonnage,
			String newMarks, String newPackages, String newCargoDesc, String newTonnage, String appDate,
			String conPerson, String conTel) throws BusinessException {
		
		gbMiscApplicationRepository.addUseOfSpaceDetails(userId, applyType, status, cust, account, varcode, coName, spaceType, purpose, fromDate, toDate, reason, billNbr, marks, packages, cargoDesc, tonnage, newMarks, newPackages, newCargoDesc, newTonnage, appDate, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addReeferContainerPowerOutletDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String[] cntrNo, String[] cntrSize, String[] cntrStatus, String coName,
			String appDate, String conPerson, String conTel) throws BusinessException {
		
		gbMiscApplicationRepository.addReeferContainerPowerOutletDetails(userId, applyType, status, cust, account, varcode, cntrNo, cntrSize, cntrStatus, coName, appDate, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean addSpreaderDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spreaderType, String fromDate, String toDate, String remarks,
			String appDate) throws BusinessException {
		
		return gbMiscApplicationRepository.addSpreaderDetails(userId, applyType, status, cust, account, varcode, coName, spreaderType, fromDate, toDate, remarks, appDate);
	}

	// !!!!!!!!!!!!!
	/* 
	 * @Override public String addStationingOfMacDetails(String userId, String
	 * applyType, String status, String cust, String account, String varcode, String
	 * coName, MiscParkMacValueObject obj, String appDate) throws BusinessException
	 * {
	 * 
	 * return gbMiscApplicationRepository.addStationingOfMacDetails(userId,
	 * applyType, status, cust, account, varcode, coName, obj, appDate); }
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addOvernightParkingVehicleDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
			String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
			String conTel, String conEmail) throws BusinessException {
		
		gbMiscApplicationRepository.addOvernightParkingVehicleDetails(userId, applyType, status, cust, account, varcode, fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel,conEmail);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addTpaDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String appRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApp, String cargoType) throws BusinessException {
		
		gbMiscApplicationRepository.addTpaDetails(userId, applyType, status, cust, account, varcode, fromDate, toDate, noHours, appRemarks, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel, preferredArea, remarks, reasonForApp, cargoType);
	}
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void supportApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String supportDate) throws BusinessException {
		
		gbMiscApplicationRepository.supportApplication(userId, applicationStatus, miscSeqNbr, remarks, supportDate);
	}
	@Override
	public String getClosingTime() throws BusinessException {
		
		return gbMiscApplicationRepository.getClosingTime();
	}
	@Override
	public List<String> getVesselDetails() throws BusinessException {
		
		return gbMiscApplicationRepository.getVesselDetails();
	}
	@Override
	public List<MiscAppValueObject> getContractTypeList() throws BusinessException {
		
		return gbMiscApplicationRepository.getContractTypeList();
	}

	@Override
	public List<MiscAppValueObject> getContractUploadDocumentList() throws BusinessException {
		
		return gbMiscApplicationRepository.getContractUploadDocumentList();
	}
	@Override
	public List<MiscAppValueObject> getUploadDocumentTypeList() throws BusinessException {
		
		return gbMiscApplicationRepository.getUploadDocumentTypeList();
	}
	@Override
	public List<MiscAppValueObject> getPurposeList() throws BusinessException {
		
		return gbMiscApplicationRepository.getPurposeList();
	}
	@Override
	public List<String> checkMiscAppOnvChassis(String[] vehNo) throws BusinessException {
		
		return gbMiscApplicationRepository.checkMiscAppOnvChassis(vehNo);
	}
	@Override
	public List<String> checkMiscAppOnvContainer(String[] cntNo) throws BusinessException {
		
		return gbMiscApplicationRepository.checkMiscAppOnvContainer(cntNo);
	}
	@Override
	public List<String> checkMiscAppOnvAsn(String[] asnNo) throws BusinessException {
		
		return gbMiscApplicationRepository.checkMiscAppOnvAsn(asnNo);
	}
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String processForTpaEmptyNormal(String userId, String applyType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String appRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApp, String cargoType) throws BusinessException {
		
		return gbMiscApplicationRepository.processForTpaEmptyNormal(userId, applyType, status, cust, account, varcode, fromDate, toDate, noHours, appRemarks, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel, preferredArea, remarks, reasonForApp, cargoType);
	}
	@Override
	public List<Map<String, Object>> getParkingReasonList() throws BusinessException {
		
		return gbMiscApplicationRepository.getParkingReasonList();
	}
	@Override
	public List<Object> getTrailerParkingApplicationDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		
		return gbMiscApplicationRepository.getTrailerParkingApplicationDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}
	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaList(String cargoType, String startDate, String toDate) throws BusinessException {
		
		return gbMiscApplicationRepository.getParkingAreaList(cargoType, startDate, toDate);
	}
	@Override
	public int getTpaDatePeriod() throws BusinessException {
		
		return gbMiscApplicationRepository.getTpaDatePeriod();
	}
	
	@Override
	public List<MiscAppParkingAreaObject> getAvailableParkingSlots(String cargoType, String fromDate, String toDate)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getAvailableParkingSlots(cargoType, fromDate, toDate);
	}

	@Override
	public List<MiscAppValueObject> getVoyageList(String selVessel) throws BusinessException{
		
		return gbMiscApplicationRepository.getVoyageList(selVessel);
	}

	@Override
	public List<String> getVesselList(String vesselName) throws BusinessException{
		return gbMiscApplicationRepository.getVesselList(vesselName);
	}
	
	
	
	// misc app top
	@Override
	public List<MiscAppValueObject> getApplicationTypeList() throws BusinessException {
		return gbMiscApplicationRepository.getApplicationTypeList();
	}
	
	@Override
	public List<MiscAppValueObject> getApplicationStatusList() throws BusinessException {
		return gbMiscApplicationRepository.getApplicationStatusList();
	}

	@Override
	public List<CompanyValueObject> getCompanyList1() throws BusinessException {
		return gbMiscApplicationRepository.getCompanyList1();
	}
	
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		return gbMiscApplicationRepository.getCompanyName(coCd);
	}
	// end
	
	// misc app void
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void voidApplication(String userId, String coCd, String appSeqNbr) throws BusinessException{
		gbMiscApplicationRepository.voidApplication(userId, coCd, appSeqNbr);
	}
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void sendFlexiAlert(String alertCode, String smsMessage, String emailMessage, String emailSubject,
			String emailSender, String emailAttachmentFilename) throws BusinessException {
		
		gbMiscApplicationRepository.sendFlexiAlert(alertCode, smsMessage, emailMessage, emailSubject, emailSender, emailAttachmentFilename);
	}
	// end
	
	// SUMMARRY PARKING SLOT
	@Override
	public List<EnquireSummarySlotValueObject> enquireSummaryParkingSlot(EnquireQueryObject queryObj) throws BusinessException {
			return gbMiscApplicationRepository.enquireSummaryParkingSlot(queryObj);
	}	
	// END

	
	// MISC APP LIST

	@Override
	public String getUserName(String userId) throws BusinessException {

		return gbMiscApplicationRepository.getUserName(userId);
	}

	@Override
	public List<OvrNghtPrkgVehValueObject> getOvernightParkingVehList(String type) throws BusinessException {

		return gbMiscApplicationRepository.getOvernightParkingVehList(type);
	}

	@Override
	public TableResult getApplicationList(String coCd, String appType, String refNo, String appStatus, String appFromDttm,
			String appToDttm, String payMode, String companyCD, String machineNo, String vehicleNo, String containerNo, Criteria criteria, Boolean allData)
			throws BusinessException {

		return gbMiscApplicationRepository.getApplicationList(coCd, appType, refNo, appStatus, appFromDttm, appToDttm, payMode,
				companyCD, machineNo, vehicleNo, containerNo, criteria, allData);
	}

	@Override
	public int checkApprovePermission(String loginId, String appType) throws BusinessException {

		return gbMiscApplicationRepository.checkApprovePermission(loginId, appType);
	}

	@Override
	public List<Object> getParkingOfLineTowBargeDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		
		return gbMiscApplicationRepository.getParkingOfLineTowBargeDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getContractorPermitDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getContractorPermitDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getUseOfSpaceDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getUseOfSpaceDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<StorageOrderValueObject> getMotCntrList(String refNo, String appSeqNbr) throws BusinessException {
		
		return gbMiscApplicationRepository.getMotCntrList(refNo, appSeqNbr);
	}

	@Override
	public List<StorageOrderValueObject> getMotCntrSummary(String refNo, String appSeqNbr) throws BusinessException {
		
		return gbMiscApplicationRepository.getMotCntrSummary(refNo, appSeqNbr);
	}

	@Override
	public List<Object> getHotworkDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getHotworkDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getParkingOfForkliftShorecrane(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		
		return gbMiscApplicationRepository.getParkingOfForkliftShorecrane(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getReeferContainerPowerOutletDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		
		return gbMiscApplicationRepository.getReeferContainerPowerOutletDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getOvernightParkingVehicleDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		
		return gbMiscApplicationRepository.getOvernightParkingVehicleDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> getSpreaderDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getSpreaderDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<Object> listParkingSlotForEnquire(EnquireQueryObject queryObj, Criteria criteria, Boolean excel) throws BusinessException {
		
		return gbMiscApplicationRepository.listParkingSlotForEnquire(queryObj, criteria, excel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscBargeValueObject obj, String conPerson,
			String conTel) throws BusinessException {
		
		gbMiscApplicationRepository.updateParkingOfLineTowBargeDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateContractorPermitDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscContractValueObject obj) throws BusinessException {
		
		gbMiscApplicationRepository.updateContractorPermitDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateHotworkDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscHotworkValueObject obj) throws BusinessException {
		
		gbMiscApplicationRepository.updateHotworkDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpaceValueObject obj, String conPerson, String conTel)
			throws BusinessException {
		
		gbMiscApplicationRepository.updateUseOfSpaceDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean updateSpreaderDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpreaderValueObject obj) throws BusinessException {
		
		return gbMiscApplicationRepository.updateSpreaderDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String appType, String account, String appStatusCd, String conPerson, String conTel,
			String conEmail) throws BusinessException {
		
		gbMiscApplicationRepository.updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status, fromDate, toDate, noNights, parkReason, vehNo, cntNo, asnNo, coName, appType, account, appStatusCd, conPerson, conTel, conEmail);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscReeferValueObject obj, String conPerson,
			String conTel) throws BusinessException {
		
		gbMiscApplicationRepository.updateReeferContainerPowerOutletDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj, conPerson, conTel);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveBargeApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			MiscBargeValueObject obj, String approveDate) throws BusinessException {
		
		gbMiscApplicationRepository.approveBargeApplication(userId, applicationStatus, miscSeqNbr, remarks, obj, approveDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveContractApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate, String waiveInd, String reasonWaive) throws BusinessException {
		
		gbMiscApplicationRepository.approveContractApplication(userId, applicationStatus, miscSeqNbr, remarks, approveDate, waiveInd, reasonWaive);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate) throws BusinessException {
		
		gbMiscApplicationRepository.approveApplication(userId, applicationStatus, miscSeqNbr, remarks, approveDate);
	}

	@Override
	public String getTpaBlockOfHours() throws BusinessException {
		
		return gbMiscApplicationRepository.getTpaBlockOfHours();
	}

	@Override
	public TableResult getApproveTpaList(String startDate, String toDate, Criteria criteria) throws BusinessException {
		
		return gbMiscApplicationRepository.getApproveTpaList(startDate, toDate, criteria);
	}

	@Override
	public List<Object> getTpaForApproveDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getTpaForApproveDetails(userId, applyType, appSeqNbr, applyTypeNm);
	}

	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList(String areaCd, String slotType, String startDate, String toDate)
			throws BusinessException {
		
		return gbMiscApplicationRepository.getParkingAreaSlotAvailableList(areaCd, slotType, startDate, toDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveTpaApplication(String[] areaCd, String[] slotCd, String userId, String applicationStatus,
			String miscSeqNbr, String remarks, String approveDate) throws BusinessException {
		
		gbMiscApplicationRepository.approveTpaApplication(areaCd, slotCd, userId, applicationStatus, miscSeqNbr, remarks, approveDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveOnvApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			String approveDate) throws BusinessException {
		
		gbMiscApplicationRepository.approveOnvApplication(userId, applicationStatus, miscSeqNbr, remarks, approveDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveBillHotworkApplication(String userId, String miscSeqNbr, String approveBillDate)
			throws BusinessException {
		
		gbMiscApplicationRepository.approveBillHotworkApplication(userId, miscSeqNbr, approveBillDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillHotworkDetails(String userId, String miscSeqNbr, MiscHotworkValueObject obj, String closeDate)
			throws BusinessException {
		
		gbMiscApplicationRepository.closeBillHotworkDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillOvernightParkingVehicleDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException {
		
		gbMiscApplicationRepository.closeBillOvernightParkingVehicleDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, MiscBargeValueObject obj,
			String closeDate) throws BusinessException {
		
		gbMiscApplicationRepository.closeBillParkingOfLineTowBargeDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillTrailerParkingApplicationDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException {
		
		gbMiscApplicationRepository.closeBillTrailerParkingApplicationDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillSpreaderDetails(String userId, String miscSeqNbr, MiscSpreaderValueObject obj,
			String closeDate) throws BusinessException {
		
		gbMiscApplicationRepository.closeBillSpreaderDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, MiscReeferValueObject obj,
			String closeDate) throws BusinessException {
		
		gbMiscApplicationRepository.closeBillReeferContainerPowerOutletDetails(userId, miscSeqNbr, obj, closeDate);
	}




	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String updateForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String parkReason, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType, String miscSeqNbr,
			String appStatusCd) throws BusinessException {
		
		return gbMiscApplicationRepository.updateForTpaEmptyNormal(userId, appType, status, cust, account, varcode, fromDate, toDate, noHours, parkReason, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel, preferredArea, remarks, reasonForApplication, cargoType, miscSeqNbr, appStatusCd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateTrailerParkingApplicationDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noHours, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String appType, String account, String appStatusCd, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
			throws BusinessException {
		
		gbMiscApplicationRepository.updateTrailerParkingApplicationDetails(userId, miscSeqNbr, status, fromDate, toDate, noHours, parkReason, vehNo, cntNo, asnNo, coName, appType, account, appStatusCd, conPerson, conTel, preferredArea, remarks, reasonForApplication, cargoType);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void approveSpaceApplication(String userId, String applicationStatus, String miscSeqNbr, String remarks,
			MiscSpaceValueObject obj, String approveDate) throws BusinessException {
		
		gbMiscApplicationRepository.approveSpaceApplication(userId, applicationStatus, miscSeqNbr, remarks, obj, approveDate);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void closeBillUseOfSpaceDetails(String userId, String miscSeqNbr, MiscSpaceValueObject obj, String closeDate)
			throws BusinessException {
		
		gbMiscApplicationRepository.closeBillUseOfSpaceDetails(userId, miscSeqNbr, obj, closeDate);
	}

	@Override
	public List<CspsLinkValueObject> getAreaListBasedOnStorageZone(String stgType, String stgZone) throws BusinessException {
	
		return cspsLinkRepo.getAreaListBasedOnStorageZone(stgType, stgZone);
	}

	@Override
	public List<CspsLinkValueObject> getLocationListBasedOnLocationType(String locType) throws BusinessException {
		
		return cspsLinkRepo.getLocationListBasedOnLocationType(locType);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public Result saveFileAttachment(Criteria criteria, MultipartFile[] uploadingFiles) throws BusinessException{
		
		Result result = new Result();
		try {
			log.info("saveFileAttachment SERVICE" +" criteria:"+ criteria.toString() + "uploadingFiles :" + uploadingFiles.toString());
			String updateFlag = criteria.getPredicates().get("updateFlag");
			String userId = criteria.getPredicates().get("userAccount");			
			String misSeqNumber =criteria.getPredicates().get("misSeqNumber");
			//String photoSeqId = criteria.getPredicates().get("photoSeqId");
			if (uploadingFiles.length > 0) {
				log.info("saveFileAttachment :\n " + uploadingFiles.toString() + ", length :" + uploadingFiles.length
						+ "  Name :" + uploadingFiles[0].getOriginalFilename());
			} else {
				log.info("saveFileAttachment : No Files " + ", length :" + uploadingFiles.length);
			}
			//HOTOAttachment attObj = new HOTOAttachment();
			String docPath = UploadDocument.getOutputFileDir(criteria.getPredicates().get("folderDir"), "upload");
			String[] assignFileNmList =fileUpload(uploadingFiles, docPath);
			
			List<Object> attObjList = new ArrayList<Object>();
			AttachmentFileValueObject attObj = new AttachmentFileValueObject();
			int j = 0;
			if (uploadingFiles.length > 0) {
				log.info("saveFileAttachment : " + uploadingFiles.toString() + ", length :" + uploadingFiles.length);
				for (MultipartFile files : uploadingFiles) {
					
					String updFileNm = files.getOriginalFilename();
					
					attObj = new AttachmentFileValueObject();
					attObj.setDocPath(docPath + assignFileNmList[j]);
					attObj.setItemCd(criteria.getPredicates().get("itemCd"));
					attObj.setDoc_type(criteria.getPredicates().get("type_"+j));
					attObj.setUserId(userId);
					attObj.setCreate_user_id(userId);
					attObj.setUpdateInd(updateFlag);
					attObj.setUpload_file_nm(updFileNm);
					attObj.setAssign_file_nm(assignFileNmList[j]);
					attObj.setMisc_seq_nbr(misSeqNumber);
					result = gbMiscApplicationRepository.saveFileAttachment(attObj);
					if (result.getSuccess()) {
						result.setSuccess(true);
						attObjList.add(result.getData());
					} else {
						result.setSuccess(false);
					}
					j++;
				}
				result.setData(attObjList);
				
			} else {
				if (updateFlag.equalsIgnoreCase("D")) {
					// log.info("DELETE Attachment ");
					//attObj.setPhotoSeqId(photoSeqId);
					result = gbMiscApplicationRepository.saveFileAttachment(attObj);
				}
			}
			log.info("result:" + result.toString());
			
		} catch (Exception e) {
			result.setSuccess(false);
		} finally {
			 log.info("END:saveFileAttachment SERVICE");
		}
		return result;
	}
	
	@Override
	public String[] fileUpload(MultipartFile[] uploadFile, String docPath) throws BusinessException{
		String fileNameDoc = "";
		String[] docListName = new String[uploadFile.length];
		try {
			log.info("fileUpload SERVICE uploadFile :" + uploadFile.toString() +" docPath:"+ CommonUtility.deNull(docPath) );
			for (int i = 0; i < uploadFile.length; i++) {
				log.info("uploadPath Document is: " + docPath);
				if (uploadFile[i].getOriginalFilename().indexOf("/") >= 0
						|| uploadFile[i].getOriginalFilename().indexOf("\\") >= 0) {
					log.info("File name validation failed!");
					return null;
				}
				String extension = FilenameUtils.getExtension(uploadFile[i].getOriginalFilename());
				UUID uuid = UUID.randomUUID();
				fileNameDoc = uuid.toString() + "." + extension;
				
				if (fileNameDoc.indexOf("/") >= 0 || fileNameDoc.indexOf("\\") >= 0) {
					log.info("File name validation failed!");
					return null;
				}
				Path rootLocation = Paths.get(docPath).toAbsolutePath().normalize();
				if (!Files.exists(rootLocation)) {
					Files.createDirectories(rootLocation);
				}
				log.info("uploadFile data :" + uploadFile[i].getInputStream());
				Path folderLocation = rootLocation;
				if (!Files.exists(folderLocation)) {
					log.info("create root location directory");
					Files.createDirectories(folderLocation);
				}
				Path fileToDeletePath = folderLocation.resolve(fileNameDoc);
				Files.deleteIfExists(fileToDeletePath);
				log.info("fileUpload folderLocation :" + folderLocation);

				InputStream inputStream = null;
				OutputStream outputStream = null;
				if (uploadFile[i].getSize() > 0) {
					if (log.isInfoEnabled()) {
						log.info("uploadFile.getSize() > 0");
					}

					try {
						inputStream = uploadFile[i].getInputStream();

						Path allPath = Paths.get(folderLocation+"/"+fileNameDoc).toAbsolutePath().normalize();
						outputStream = new FileOutputStream(allPath.toString());
						int readBytes = 0;
						byte[] buffer = new byte[10000];
						while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
							outputStream.write(buffer, 0, readBytes);
						}
						outputStream.close();
						inputStream.close();
					} catch (FileNotFoundException fnfe) {
						log.info(fnfe.getMessage());
					} catch (IOException ioe) {
						log.info(ioe.getMessage());
					} catch (Exception e) {
						log.info(e.getMessage());
					}
					
					docListName[i] = fileNameDoc;
				}
				
			}
			

		} catch (Exception e) {
			log.info("Exception fileUpload : ", e);
			return null;
		}
			return docListName;
	}
	
	@Override
	public Resource fileDownload(Criteria criteria) throws BusinessException{
		try {
			String docPath = UploadDocument.getOutputFileDir(criteria.getPredicates().get("folderDir"), "donwload");
			Path rootLocation = Paths.get(folderPath + docPath).toAbsolutePath().normalize();

			log.info("fileDownload criteria :" + criteria.toString());

			String fileName = criteria.getPredicates().get("attachFileName");
			String miscSeqNumber= criteria.getPredicates().get("miscSeqNumber");
			AttachmentFileValueObject vo=gbMiscApplicationRepository.getFileAttachment(miscSeqNumber,fileName);
			
			if (vo != null) {
				log.info("FILE ");
				fileName = vo.getAssign_file_nm();
			} else {
				throw new FileSystemNotFoundException("File not found " + criteria);
			}


			//Path filePath = rootLocation.resolve(vo.getAssign_file_nm().replace(folderPath, "")).normalize();
			Path filePath = rootLocation.resolve(fileName).normalize();

			log.info("fileDownload :" + filePath.toString());

			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileSystemNotFoundException("File not found " + criteria);
			}
		} catch (Exception ex) {
			log.info("Exception fileDownload : ", ex);
			throw new FileSystemNotFoundException("File not found " + criteria);
		}
	}

	@Override
	public List<AttachmentFileValueObject> getFileAttachmentList(Criteria criteria) throws BusinessException{
		String miscSeqNumber = criteria.getPredicates().get("miscSeqNumber");

		return gbMiscApplicationRepository.getFileAttachmentList(miscSeqNumber, "");
		 
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void deleteFileData(String userId, String miscSeqNbr, String assignedName) throws BusinessException {
		 gbMiscApplicationRepository.deleteFileData(userId, miscSeqNbr,assignedName);
		
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateParkingOfForkliftShorecrane(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException {
		
		gbMiscApplicationRepository.updateParkingOfForkliftShorecrane(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj);
	}
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String addStationingOfMacDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, MiscParkMacValueObject obj, String appDate) throws BusinessException {
		
		return gbMiscApplicationRepository.addStationingOfMacDetails(userId, applyType, status, cust, account, varcode, coName, obj, appDate);
	}
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateStationingOfMacDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException {
		
		gbMiscApplicationRepository.updateStationingOfMacDetails(userId, miscSeqNbr, status, coName, appType, account, appStatusCd, obj);
	}
	
	@Override
	public List<String> getParkingAreaList() throws BusinessException {
		return maintenanceTpaRepo.getParkingAreaList();
	}
	// END

	@Override
	public List<String> getDelFile(String fileNames, String miscSeqNbr, String Type, String catCd) throws BusinessException {
		return gbMiscApplicationRepository.getDelFile(fileNames, miscSeqNbr, Type, catCd);
	}
	
	

}

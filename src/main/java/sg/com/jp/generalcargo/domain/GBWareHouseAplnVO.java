package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GBWareHouseAplnVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String terminal;
	private String sNo;
	private String aplnRefNo;
	private String aplnStatus;
	private String aplnRunningNo;
	private String vesselCargoInd;
	private String customerCode;
	private String customerName;
	private String accountNumber;
	private String vesselVoyageCode;
	private String vesselDetails;
	private String vesselName;
	private String vesselVoyNbr;
	private String vesselETBDTTM;
	private String vesselATBDTTM;
	private String vesselCOD;
	private String edoEsnNumber;
	private String cargoDescription;
	private String cargoQtyInSqMeters;
	private String cargoTonnage;
	private String commenceDate;
	private String endDate;
	private String customerEmail;
	private String customerTelephone;
	private String emailAlertIndicator;
	private String smsAlertIndicator;
	private String remarks;
	private String createUserId;
	private String createUserName;
	private String createDTTM;
	private String extendUserId;
	private String extendDTTM;
	private String lastModifiedUserId;
	private String lastModifiedDTTM;
	private String processUserId;
	private String processUserName;
	private String processDTTM;
	private String processRemarks;
	private String closedBillingByUserId;
	private String closedBillingByUserName;
	private String closedBillingByDTTM;
	private String asnNbr;
	private String purpCode;
	private String sysdate;
	private String submitDTTM;
	private String submitUserName;
	private String applicantCompanyName;
	private String applicantCompanyAccount;
	private String billableCompanyName;
	private String billableCompanyAccount;

	private String billingCriteria;
	private String specialRateIndicator;

	private String verifyUserId;
	private String verifyUserName;
	private String verifyDTTM;
	private String verifyRemarks;
	private String approveUserId;
	private String approveUserName;
	private String approveUserEmail;
	private String approveDTTM;
	private String approveRemarks;
	private String rejectUserId;
	private String rejectUserName;
	private String rejectDTTM;
	private String rejectRemarks;
	private String currentTonnage;
	private String cargoStorageType;
	private String cargoLocation;

//	private String storageDays;
//	private String schemeCode;

	private String billableTerm;
	private String measurementDate;
	private String storageTypeDesc;
	private String storageTypeList;

	private String contractNbr;

	private String prevCargoMeasureArea;;
	private String prevMeasureDate;
	private String vesselETUDTTM;
	private String vesselATUDTTM;
	private String vesselCOL;
	private String vesselBerthDTTM;

	private String packages;
	private String newPackages;
	private String dnPackages;
	private String closeRemarks;
	private String shutOutInd;
	 private String loginId;
	    private String coCd;
	    private String billableAccount;
	    private String func;
	    private String request;
	    private String userId;
		// Added by Punitha on 25/11/2009
		private String[] bayNbr;
		private String[] areaUsed;
	    private String refNo;
		// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.
		private String tariffcd;
		private String mainCategory;
		private ArrayList bayNbrList;
		private ArrayList areaUsedList;
		private String telephone;
		private String email;
		private String notifyEmail;
		private String notifySMS;
		private String extendEndDate;
		private String storageType;
		private String cargoDesc;
		private String cargoStorage;
		private String cargoInd;
		private String commDate;
		private String shutOut;
		private String billCriteria;
		private String tempCargoLocation;
		private String validate;
		private ArrayList storageTypeList1;
		private String[] appvOfficerEmailId;
		private String cargoTonnages;
		private String vvCd;
		private ArrayList gbWHAplnDet;
		private String locType;
		private String berthNbr;
		private String stgZone;
		private String stgName;
		private Object inSmartInd;
		private String[] opsCheck;
		private String areaUsed1;
		private String EmailAddress;
		private String[] cargoTonnage1;
	private GBWareHouseAplnProcessAccountVO processVO = new GBWareHouseAplnProcessAccountVO();

	
	
	
	
	public String[] getCargoTonnage1() {
		return cargoTonnage1;
	}

	public void setCargoTonnage1(String[] cargoTonnage1) {
		this.cargoTonnage1 = cargoTonnage1;
	}

	public String getEmailAddress() {
		return EmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}

	public String getAreaUsed1() {
		return areaUsed1;
	}

	public void setAreaUsed1(String areaUsed1) {
		this.areaUsed1 = areaUsed1;
	}

	public String getsNo() {
		return sNo;
	}

	public void setsNo(String sNo) {
		this.sNo = sNo;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getCoCd() {
		return coCd;
	}

	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}

	public String getBillableAccount() {
		return billableAccount;
	}

	public void setBillableAccount(String billableAccount) {
		this.billableAccount = billableAccount;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public ArrayList getBayNbrList() {
		return bayNbrList;
	}

	public void setBayNbrList(ArrayList bayNbrList) {
		this.bayNbrList = bayNbrList;
	}

	public ArrayList getAreaUsedList() {
		return areaUsedList;
	}

	public void setAreaUsedList(ArrayList areaUsedList) {
		this.areaUsedList = areaUsedList;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotifyEmail() {
		return notifyEmail;
	}

	public void setNotifyEmail(String notifyEmail) {
		this.notifyEmail = notifyEmail;
	}

	public String getNotifySMS() {
		return notifySMS;
	}

	public void setNotifySMS(String notifySMS) {
		this.notifySMS = notifySMS;
	}

	public String getExtendEndDate() {
		return extendEndDate;
	}

	public void setExtendEndDate(String extendEndDate) {
		this.extendEndDate = extendEndDate;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getCargoDesc() {
		return cargoDesc;
	}

	public void setCargoDesc(String cargoDesc) {
		this.cargoDesc = cargoDesc;
	}

	public String getCargoStorage() {
		return cargoStorage;
	}

	public void setCargoStorage(String cargoStorage) {
		this.cargoStorage = cargoStorage;
	}

	public String getCargoInd() {
		return cargoInd;
	}

	public void setCargoInd(String cargoInd) {
		this.cargoInd = cargoInd;
	}

	public String getCommDate() {
		return commDate;
	}

	public void setCommDate(String commDate) {
		this.commDate = commDate;
	}

	public String getShutOut() {
		return shutOut;
	}

	public void setShutOut(String shutOut) {
		this.shutOut = shutOut;
	}

	public String getBillCriteria() {
		return billCriteria;
	}

	public void setBillCriteria(String billCriteria) {
		this.billCriteria = billCriteria;
	}

	public String getTempCargoLocation() {
		return tempCargoLocation;
	}

	public void setTempCargoLocation(String tempCargoLocation) {
		this.tempCargoLocation = tempCargoLocation;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public ArrayList getStorageTypeList1() {
		return storageTypeList1;
	}

	public void setStorageTypeList1(ArrayList storageTypeList1) {
		this.storageTypeList1 = storageTypeList1;
	}

	public String[] getAppvOfficerEmailId() {
		return appvOfficerEmailId;
	}

	public void setAppvOfficerEmailId(String[] appvOfficerEmailId) {
		this.appvOfficerEmailId = appvOfficerEmailId;
	}

	public String getCargoTonnages() {
		return cargoTonnages;
	}

	public void setCargoTonnages(String cargoTonnages) {
		this.cargoTonnages = cargoTonnages;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public ArrayList getGbWHAplnDet() {
		return gbWHAplnDet;
	}

	public void setGbWHAplnDet(ArrayList gbWHAplnDet) {
		this.gbWHAplnDet = gbWHAplnDet;
	}

	public String getLocType() {
		return locType;
	}

	public void setLocType(String locType) {
		this.locType = locType;
	}

	public String getBerthNbr() {
		return berthNbr;
	}

	public void setBerthNbr(String berthNbr) {
		this.berthNbr = berthNbr;
	}

	public String getStgZone() {
		return stgZone;
	}

	public void setStgZone(String stgZone) {
		this.stgZone = stgZone;
	}

	public String getStgName() {
		return stgName;
	}

	public void setStgName(String stgName) {
		this.stgName = stgName;
	}

	public Object getInSmartInd() {
		return inSmartInd;
	}

	public void setInSmartInd(Object inSmartInd) {
		this.inSmartInd = inSmartInd;
	}

	public String[] getOpsCheck() {
		return opsCheck;
	}

	public void setOpsCheck(String[] opsCheck) {
		this.opsCheck = opsCheck;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public GBWareHouseAplnProcessAccountVO getProcessVO() {
		return processVO;
	}

	public void setProcessVO(GBWareHouseAplnProcessAccountVO processVO) {
		this.processVO = processVO;
	}

	public String[] getUserEmailAddresses() {
		return processVO.getEmailAddresses();
	}

	public String[] getUserNames() {
		return processVO.getUserNames();
	}

	public String[] getLoginIds() {
		return processVO.getLoginIds();
	}

	public String[] getTelephones() {
		return processVO.getTelephones();
	}

	public void setUserEmailAddresses(String[] emailAddresses) {
		processVO.setEmailAddresses(emailAddresses);
	}

	public void setUserNames(String[] userNames) {
		processVO.setUserNames(userNames);
	}

	public void setLoginIds(String[] loginIds) {
		processVO.setLoginIds(loginIds);
	}

	public void setTelephones(String[] telephones) {
		processVO.setTelephones(telephones);
	}

	public String getStorageDays() {
		return processVO.getStorageDays();
	}

	public void setStorageDays(String storageDays) {
		this.processVO.setStorageDays(storageDays);
	}

	public String getSchemeCode() {
		return processVO.getSchemeCode();
	}

	public void setSchemeCode(String schemeCode) {
		this.processVO.setSchemeCode(schemeCode);
	}

	public String getCurrentTonnage() {
		return currentTonnage;
	}

	public void setCurrentTonnage(String currentTonnage) {
		this.currentTonnage = currentTonnage;
	}

	public String getApproveDTTM() {
		return approveDTTM;
	}

	public void setApproveDTTM(String approveDTTM) {
		this.approveDTTM = approveDTTM;
	}

	public String getApproveRemarks() {
		return approveRemarks;
	}

	public void setApproveRemarks(String approveRemarks) {
		this.approveRemarks = approveRemarks;
	}

	public String getApproveUserId() {
		return approveUserId;
	}

	public void setApproveUserId(String approveUserId) {
		this.approveUserId = approveUserId;
	}

	public String getApproveUserName() {
		return approveUserName;
	}

	public void setApproveUserName(String approveUserName) {
		this.approveUserName = approveUserName;
	}

	public String getBillingCriteria() {
		return billingCriteria;
	}

	public void setBillingCriteria(String billingCriteria) {
		this.billingCriteria = billingCriteria;
	}

	public String getSpecialRateIndicator() {
		return specialRateIndicator;
	}

	public void setSpecialRateIndicator(String specialRateIndicator) {
		this.specialRateIndicator = specialRateIndicator;
	}

	public String getPrevCargoMeasureArea() {
		return prevCargoMeasureArea;
	}

	public void setPrevCargoMeasureArea(String prevCargoMeasureArea) {
		this.prevCargoMeasureArea = prevCargoMeasureArea;
	}

	public String getPrevMeasureDate() {
		return prevMeasureDate;
	}

	public void setPrevMeasureDate(String prevMeasureDate) {
		this.prevMeasureDate = prevMeasureDate;
	}

	public String getVerifyRemarks() {
		return verifyRemarks;
	}

	public void setVerifyRemarks(String verfyRemarks) {
		this.verifyRemarks = verfyRemarks;
	}

	public String getVerifyDTTM() {
		return verifyDTTM;
	}

	public void setVerifyDTTM(String verifyDTTM) {
		this.verifyDTTM = verifyDTTM;
	}

	public String getVerifyUserId() {
		return verifyUserId;
	}

	public void setVerifyUserId(String verifyUserId) {
		this.verifyUserId = verifyUserId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAplnRefNo() {
		return aplnRefNo;
	}

	public void setAplnRefNo(String aplnRefNo) {
		this.aplnRefNo = aplnRefNo;
	}

	public String getAplnRunningNo() {
		return aplnRunningNo;
	}

	public void setAplnRunningNo(String aplnRunningNo) {
		this.aplnRunningNo = aplnRunningNo;
	}

	public String getAplnStatus() {
		return aplnStatus;
	}

	public void setAplnStatus(String aplnStatus) {
		this.aplnStatus = aplnStatus;
	}

	public String getAsnNbr() {
		return asnNbr;
	}

	public void setAsnNbr(String asnNbr) {
		this.asnNbr = asnNbr;
	}

	public String getCargoDescription() {
		return cargoDescription;
	}

	public void setCargoDescription(String cargoDescription) {
		this.cargoDescription = cargoDescription;
	}

	public String getCargoQtyInSqMeters() {
		return cargoQtyInSqMeters;
	}

	public void setCargoQtyInSqMeters(String cargoQtyInSqMeters) {
		this.cargoQtyInSqMeters = cargoQtyInSqMeters;
	}

	public String getCargoTonnage() {
		return cargoTonnage;
	}

	public void setCargoTonnage(String cargoTonnage) {
		this.cargoTonnage = cargoTonnage;
	}

	public String getClosedBillingByDTTM() {
		return closedBillingByDTTM;
	}

	public void setClosedBillingByDTTM(String closedBillingByDTTM) {
		this.closedBillingByDTTM = closedBillingByDTTM;
	}

	public String getClosedBillingByUserId() {
		return closedBillingByUserId;
	}

	public void setClosedBillingByUserId(String closedBillingByUserId) {
		this.closedBillingByUserId = closedBillingByUserId;
	}

	public String getClosedBillingByUserName() {
		return closedBillingByUserName;
	}

	public void setClosedBillingByUserName(String closedBillingByUserName) {
		this.closedBillingByUserName = closedBillingByUserName;
	}

	public String getCommenceDate() {
		return commenceDate;
	}

	public void setCommenceDate(String commenceDate) {
		this.commenceDate = commenceDate;
	}

	public String getCreateDTTM() {
		return createDTTM;
	}

	public void setCreateDTTM(String createDTTM) {
		this.createDTTM = createDTTM;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerTelephone() {
		return customerTelephone;
	}

	public void setCustomerTelephone(String customerTelephone) {
		this.customerTelephone = customerTelephone;
	}

	public String getEdoEsnNumber() {
		return edoEsnNumber;
	}

	public void setEdoEsnNumber(String edoEsnNumber) {
		this.edoEsnNumber = edoEsnNumber;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExtendDTTM() {
		return extendDTTM;
	}

	public void setExtendDTTM(String extendDTTM) {
		this.extendDTTM = extendDTTM;
	}

	public String getExtendUserId() {
		return extendUserId;
	}

	public void setExtendUserId(String extendUserId) {
		this.extendUserId = extendUserId;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getLastModifiedDTTM() {
		return lastModifiedDTTM;
	}

	public void setLastModifiedDTTM(String lastModifiedDTTM) {
		this.lastModifiedDTTM = lastModifiedDTTM;
	}

	public String getProcessDTTM() {
		return processDTTM;
	}

	public void setProcessDTTM(String processDTTM) {
		this.processDTTM = processDTTM;
	}

	public String getProcessUserId() {
		return processUserId;
	}

	public void setProcessUserId(String processUserId) {
		this.processUserId = processUserId;
	}

	public String getProcessUserName() {
		return processUserName;
	}

	public void setProcessUserName(String processUserName) {
		this.processUserName = processUserName;
	}

	public String getProcessRemarks() {
		return processRemarks;
	}

	public void setProcessRemarks(String processRemarks) {
		this.processRemarks = processRemarks;
	}

	public String getPurpCode() {
		return purpCode;
	}

	public void setPurpCode(String purpCode) {
		this.purpCode = purpCode;
	}

	public String getRejectDTTM() {
		return rejectDTTM;
	}

	public void setRejectDTTM(String rejectDTTM) {
		this.rejectDTTM = rejectDTTM;
	}

	public String getRejectRemarks() {
		return rejectRemarks;
	}

	public void setRejectRemarks(String rejectRemarks) {
		this.rejectRemarks = rejectRemarks;
	}

	public String getRejectUserId() {
		return rejectUserId;
	}

	public void setRejectUserId(String rejectUserId) {
		this.rejectUserId = rejectUserId;
	}

	public String getRejectUserName() {
		return rejectUserName;
	}

	public void setRejectUserName(String rejectUserName) {
		this.rejectUserName = rejectUserName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSNo() {
		return sNo;
	}

	public void setSNo(String sNo) {
		this.sNo = sNo;
	}

	public String getSubmitDTTM() {
		return submitDTTM;
	}

	public void setSubmitDTTM(String submitDTTM) {
		this.submitDTTM = submitDTTM;
	}

	public String getSubmitUserName() {
		return submitUserName;
	}

	public void setSubmitUserName(String submitUserName) {
		this.submitUserName = submitUserName;
	}

	public String getSysdate() {
		return sysdate;
	}

	public void setSysdate(String sysdate) {
		this.sysdate = sysdate;
	}

	public String getVesselCargoInd() {
		return vesselCargoInd;
	}

	public void setVesselCargoInd(String vesselCargoInd) {
		this.vesselCargoInd = vesselCargoInd;
	}

	public String getVesselDetails() {
		return vesselDetails;
	}

	public void setVesselDetails(String vesselDetails) {
		this.vesselDetails = vesselDetails;
	}

	public String getVesselVoyageCode() {
		return vesselVoyageCode;
	}

	public void setVesselVoyageCode(String vesselVoyageCode) {
		this.vesselVoyageCode = vesselVoyageCode;
	}

	public String getVerifyUserName() {
		return verifyUserName;
	}

	public void setVerifyUserName(String verifyUserName) {
		this.verifyUserName = verifyUserName;
	}

	public String getApproveUserEmail() {
		return approveUserEmail;
	}

	public void setApproveUserEmail(String approveUserEmail) {
		this.approveUserEmail = approveUserEmail;
	}

	public String getCargoStorageType() {
		return cargoStorageType;
	}

	public void setCargoStorageType(String cargoStorageType) {
		this.cargoStorageType = cargoStorageType;
	}

	public String getVesselATBDTTM() {
		return vesselATBDTTM;
	}

	public void setVesselATBDTTM(String vesselATBDTTM) {
		this.vesselATBDTTM = vesselATBDTTM;
	}

	public String getVesselCOD() {
		return vesselCOD;
	}

	public void setVesselCOD(String vesselCOD) {
		this.vesselCOD = vesselCOD;
	}

	public String getVesselETBDTTM() {
		return vesselETBDTTM;
	}

	public void setVesselETBDTTM(String vesselETBDTTM) {
		this.vesselETBDTTM = vesselETBDTTM;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVesselVoyNbr() {
		return vesselVoyNbr;
	}

	public void setVesselVoyNbr(String vesselVoyNbr) {
		this.vesselVoyNbr = vesselVoyNbr;
	}

	public String getEmailAlertIndicator() {
		return emailAlertIndicator;
	}

	public void setEmailAlertIndicator(String emailAlertIndicator) {
		this.emailAlertIndicator = emailAlertIndicator;
	}

	public String getSmsAlertIndicator() {
		return smsAlertIndicator;
	}

	public void setSmsAlertIndicator(String smsAlertIndicator) {
		this.smsAlertIndicator = smsAlertIndicator;
	}

	public String getApplicantCompanyName() {
		return applicantCompanyName;
	}

	public void setApplicantCompanyName(String applicantCompanyName) {
		this.applicantCompanyName = applicantCompanyName;
	}

	public String getApplicantCompanyAccount() {
		return applicantCompanyAccount;
	}

	public void setApplicantCompanyAccount(String applicantCompanyAccount) {
		this.applicantCompanyAccount = applicantCompanyAccount;
	}

	public String getBillableCompanyAccount() {
		return billableCompanyAccount;
	}

	public void setBillableCompanyAccount(String billableCompanyAccount) {
		this.billableCompanyAccount = billableCompanyAccount;
	}

	public String getBillableCompanyName() {
		return billableCompanyName;
	}

	public void setBillableCompanyName(String billableCompanyName) {
		this.billableCompanyName = billableCompanyName;
	}

	public String getCargoLocation() {
		return cargoLocation;
	}

	public void setCargoLocation(String cargoLocation) {
		this.cargoLocation = cargoLocation;
	}

	public String getBillableTerm() {
		return billableTerm;
	}

	public void setBillableTerm(String billableTerm) {
		this.billableTerm = billableTerm;
	}

	public String getMeasurementDate() {
		return measurementDate;
	}

	public void setMeasurementDate(String measurementDate) {
		this.measurementDate = measurementDate;
	}

	public String getStorageTypeDesc() {
		return storageTypeDesc;
	}

	public void setStorageTypeDesc(String storageTypeDesc) {
		this.storageTypeDesc = storageTypeDesc;
	}

	public String getStorageTypeList() {
		return storageTypeList;
	}

	public void setStorageTypeList(String storageTypeList) {
		this.storageTypeList = storageTypeList;
	}

	public String getContractNbr() {
		return contractNbr;
	}

	public void setContractNbr(String contractNbr) {
		this.contractNbr = contractNbr;
	}

	public String getVesselATUDTTM() {
		return vesselATUDTTM;
	}

	public void setVesselATUDTTM(String vesselATUDTTM) {
		this.vesselATUDTTM = vesselATUDTTM;
	}

	public String getVesselCOL() {
		return vesselCOL;
	}

	public void setVesselCOL(String vesselCOL) {
		this.vesselCOL = vesselCOL;
	}

	public String getVesselETUDTTM() {
		return vesselETUDTTM;
	}

	public void setVesselETUDTTM(String vesselETUDTTM) {
		this.vesselETUDTTM = vesselETUDTTM;
	}

	public String getVesselBerthDTTM() {
		return vesselBerthDTTM;
	}

	public void setVesselBerthDTTM(String vesselBerthDTTM) {
		this.vesselBerthDTTM = vesselBerthDTTM;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getNewPackages() {
		return newPackages;
	}

	public void setNewPackages(String newPackages) {
		this.newPackages = newPackages;
	}

	public String getDnPackages() {
		return dnPackages;
	}

	public void setDnPackages(String dnPackages) {
		this.dnPackages = dnPackages;
	}

	public String getCloseRemarks() {
		return closeRemarks;
	}

	public void setCloseRemarks(String closeRemarks) {
		this.closeRemarks = closeRemarks;
	}

	public String getShutOutInd() {
		return shutOutInd;
	}

	public void setShutOutInd(String shutOutInd) {
		this.shutOutInd = shutOutInd;
	}

	// Added by Punitha on 25/11/2009
	public String[] getAreaUsed() {
		return areaUsed;
	}

	public void setAreaUsed(String[] areaUsed) {
		this.areaUsed = areaUsed;
	}

	public String[] getBayNbr() {
		return bayNbr;
	}

	public void setBayNbr(String[] bayNbr) {
		this.bayNbr = bayNbr;
	}

	// BEGIN FPT modify for Warehouse Management CR, 10/02/2014.
	public String getTariffcd() {
		return tariffcd;
	}

	public void setTariffcd(String tariffcd) {
		this.tariffcd = tariffcd;
	}

	public String getMainCategory() {
		if (tariffcd != null && tariffcd.length() >= 5) {
			mainCategory = StringUtils.substring(tariffcd, 3, 5);
		} else {
			mainCategory = "";
		}
		return mainCategory;
	}
	// END FPT modify for Warehouse Management CR, 10/02/2014.

	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

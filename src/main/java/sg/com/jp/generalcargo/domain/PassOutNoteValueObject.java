package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PassOutNoteValueObject {
	private String passOutNoteId;
	private String loginUser;
	private String truckPlaceNo;
	private String driverPassNo;
	private String driverName;
	private String noOfPkgs;
	private String cargoMarks;
	private String cargoDesc;
	private String remarks;
	private String dateTime;
	private String createdBy;
	private String companyName;
	private String companyCode;
	private String status;
	private String gateOutDttm;
	private String nameDecl;
	private String companyDecl;
	private String nricNo;
	private String consignee;
	private String deleteRemark;
	private String searchDateFrom;
	private String searchDateTo;
	private String searchCompanyCode;
	private String searchCompanyName;
	private String searchPassOutNoteNo;
	private boolean searchActive = false;
	private boolean searchNew = false;
	private int totalPage;
	private int pageIndex;

	public String getTruckPlaceNo() {
		return truckPlaceNo;
	}

	public void setTruckPlaceNo(String truckPlaceNo) {
		this.truckPlaceNo = truckPlaceNo;
	}

	public String getDriverPassNo() {
		return driverPassNo;
	}

	public void setDriverPassNo(String driverPassNo) {
		this.driverPassNo = driverPassNo;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getNoOfPkgs() {
		return noOfPkgs;
	}

	public void setNoOfPkgs(String noOfPkgs) {
		this.noOfPkgs = noOfPkgs;
	}

	public String getCargoMarks() {
		return cargoMarks;
	}

	public void setCargoMarks(String cargoMarks) {
		this.cargoMarks = cargoMarks;
	}

	public String getCargoDesc() {
		return cargoDesc;
	}

	public void setCargoDesc(String cargoDesc) {
		this.cargoDesc = cargoDesc;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNameDecl() {
		return nameDecl;
	}

	public void setNameDecl(String nameDecl) {
		this.nameDecl = nameDecl;
	}

	public String getCompanyDecl() {
		return companyDecl;
	}

	public void setCompanyDecl(String companyDecl) {
		this.companyDecl = companyDecl;
	}

	public String getNricNo() {
		return nricNo;
	}

	public void setNricNo(String nricNo) {
		this.nricNo = nricNo;
	}

	public String getDeleteRemark() {
		return deleteRemark;
	}

	public void setDeleteRemark(String deleteRemark) {
		this.deleteRemark = deleteRemark;
	}

	public String getSearchDateFrom() {
		return searchDateFrom;
	}

	public void setSearchDateFrom(String searchDateFrom) {
		this.searchDateFrom = searchDateFrom;
	}

	public String getSearchDateTo() {
		return searchDateTo;
	}

	public void setSearchDateTo(String searchDateTo) {
		this.searchDateTo = searchDateTo;
	}

	public String getSearchCompanyCode() {
		return searchCompanyCode;
	}

	public void setSearchCompanyCode(String searchCompanyCode) {
		this.searchCompanyCode = searchCompanyCode;
	}

	public boolean isSearchActive() {
		return searchActive;
	}

	public void setSearchActive(boolean searchActive) {
		this.searchActive = searchActive;
	}

	public boolean isSearchNew() {
		return searchNew;
	}

	public void setSearchNew(boolean searchNew) {
		this.searchNew = searchNew;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setPassOutNoteId(String passOutNoteId) {
		this.passOutNoteId = passOutNoteId;
	}

	public String getPassOutNoteId() {
		return passOutNoteId;
	}

	public void setGateOutDttm(String gateOutDttm) {
		this.gateOutDttm = gateOutDttm;
	}

	public String getGateOutDttm() {
		return gateOutDttm;
	}

	public void setSearchPassOutNoteNo(String searchPassOutNoteNo) {
		this.searchPassOutNoteNo = searchPassOutNoteNo;
	}

	public String getSearchPassOutNoteNo() {
		return searchPassOutNoteNo;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setSearchCompanyName(String searchCompanyName) {
		this.searchCompanyName = searchCompanyName;
	}

	public String getSearchCompanyName() {
		return searchCompanyName;
	}
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UatValueObject {

	private String uatId;
	private String loginUser;
	private String userType;
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
	private String deleteRemark;
	private String searchDateFrom;
	private String searchDateTo;
	private String searchCompanyCode;
	private String searchCompanyName;
	private String searchUatNo;
	private boolean searchActive = false;
	private boolean searchNew = false;
	private int totalPage;
	private int pageIndex;

	public String getUatId() {
		return uatId;
	}

	public void setUatId(String uatId) {
		this.uatId = uatId;
	}

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

	public void setCompanyName(String company) {
		this.companyName = company;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
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

	public String getSearchUatNo() {
		return searchUatNo;
	}

	public void setSearchUatNo(String searchUatNo) {
		this.searchUatNo = searchUatNo;
	}

	public void setSearchActive(boolean searchActive) {
		this.searchActive = searchActive;
	}

	public boolean isSearchActive() {
		return searchActive;
	}

	public void setGateOutDttm(String gateOutDttm) {
		this.gateOutDttm = gateOutDttm;
	}

	public String getGateOutDttm() {
		return gateOutDttm;
	}

	public void setSearchNew(boolean searchNew) {
		this.searchNew = searchNew;
	}

	public boolean isSearchNew() {
		return searchNew;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setDeleteRemark(String deleteRemark) {
		this.deleteRemark = deleteRemark;
	}

	public String getDeleteRemark() {
		return deleteRemark;
	}

	public void setSearchCompanyName(String searchCompanyName) {
		this.searchCompanyName = searchCompanyName;
	}

	public String getSearchCompanyName() {
		return searchCompanyName;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserType() {
		return userType;
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

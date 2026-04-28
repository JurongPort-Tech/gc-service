package sg.com.jp.generalcargo.domain;

/**
*
*/
/*
Revision History
================
* Author      Request Number      Description of Change   Version     Date Released
* ------      --------------      ---------------------   -------     -------------
* Hoang Chu   CR-CAB-20080116-01          Creation          1.4         6/6/2008
* 
* */

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Hoang Chu
 *
 */
public class ExpiredCompanyValueObject implements TopsIValueObject, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appRefNo;
	private Date appDate;
	private String companyName;
	private Date fromDate;
	private String machineType;
	private Double liftCapacity;
	private String machineryRegNo;
	private String insuranceNo;
	private Date insuranceExpiryDate;

//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		sb.append( "ExpiredCompanyBean[" );
//		sb.append( "appRefNo=" + appRefNo );
//		sb.append( ";appDate=" + appDate );
//		sb.append( ";companyName=" + companyName );
//		sb.append( ";fromDate=" + fromDate );
//		sb.append( ";machineType=" + machineType );
//		sb.append( ";liftCapacity=" + liftCapacity );
//		sb.append( ";insuranceNo=" + insuranceNo );
//		sb.append( ";insuranceExpiryDate=" + insuranceExpiryDate );
//		sb.append( "]" );
//
//		return sb.toString();
//	} // end of toString()

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getAppRefNo() {
		return appRefNo;
	}

	public void setAppRefNo(String appRefNo) {
		this.appRefNo = appRefNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getInsuranceExpiryDate() {
		return insuranceExpiryDate;
	}

	public void setInsuranceExpiryDate(Date insuranceExpiryDate) {
		this.insuranceExpiryDate = insuranceExpiryDate;
	}

	public String getInsuranceNo() {
		return insuranceNo;
	}

	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}

	public Double getLiftCapacity() {
		return liftCapacity;
	}

	public void setLiftCapacity(Double liftCapacity) {
		this.liftCapacity = liftCapacity;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public String getMachineryRegNo() {
		return machineryRegNo;
	}

	public void setMachineryRegNo(String machineryRegNo) {
		this.machineryRegNo = machineryRegNo;
	}

	public void doGet(Object arg0) {

	}

	public void doSet(Object arg0) {

	}

	public Object clone() {
		Object o = new Object();
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {

		}
		return o;
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

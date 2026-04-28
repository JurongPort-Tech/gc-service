package sg.com.jp.generalcargo.domain;
/**
*System Name: GBMS (General Bulk Cargo Management System)
*Component ID: MiscAppValueObject.java
*Component Description: Shows List of Application Types
*
*@author      Anandhi
*@version     1.0
*@since       01 March 2007
*/

/*Revision History
*================
 Revision History
 ================
* Author	  Request Number	  Description of Change   			       Version     Date Released
* ----------- ------------------- ---------------------------------------- ----------  ------------------
* Anandhi                         Creation                                 1.0         01 March 2007
* Punitha				          To add Contact Person and                1.1         28 May 2007
*                                 Contact Tel
* Punitha				          To display vessel details		           1.2		   20 July 2007
* Dong Sheng  CR-OPS-20110110-09  Added nbrOfVehicle.                      1.3         04 January 2011
*/

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscAppValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String terminal;
	private String appSeqNbr;
	private String appTypeCd;
	private String appTypeName;
	private String appRefNbr;
	private String appStatusCd;
	private String appStatusName;
	private String appDttm;
	private String coCd;
	private String coName;
	private String billNbr;
	private String acctNbr;
	private String varCode;
	private String vslName;
	private String voyNbr;
	private String typeCd;
	private String typeName;
	private String docName;
	private String userId;
	private String submitDttm;
	private String submitBy;
	private String supportDttm;
	private String supportBy;
	private String approveDttm;
	private String approveBy;
	private String closeDttm;
	private String closeBy;
	private String appRemarks;
	private String supportRemarks;
	private String approveBillDttm;
	private String approveBillBy;
	private String sNo;
	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
	private String conPerson;
	private String conTel;
	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
	// Added on 20/07/2007 by Punitha.To display vessel details
	private String inVoyNbr;
	private String outVoyNbr;
	private String atbDttm;
	private String atuDttm;
	private int noOfCases;

	private String approveRemarks;
	private String noOfNights;

	private Date from_dttm;
	private Date to_dttm;

	private String str_from_dttm;
	private String str_to_dttm;

	// Ended by Punitha

	public String getStr_from_dttm() {
		return str_from_dttm;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public void setStr_from_dttm(String strFromDttm) {
		str_from_dttm = strFromDttm;
	}

	public String getStr_to_dttm() {
		return str_to_dttm;
	}

	public void setStr_to_dttm(String strToDttm) {
		str_to_dttm = strToDttm;
	}

	public Date getFrom_dttm() {
		return from_dttm;
	}

	public void setFrom_dttm(Date fromDttm) {
		from_dttm = fromDttm;
	}

	public Date getTo_dttm() {
		return to_dttm;
	}

	public void setTo_dttm(Date toDttm) {
		to_dttm = toDttm;
	}

	public String getApproveRemarks() {
		return approveRemarks;
	}

	public void setApproveRemarks(String approveRemarks) {
		this.approveRemarks = approveRemarks;
	}

	public String getNoOfNights() {
		return noOfNights;
	}

	public void setNoOfNights(String noOfNights) {
		this.noOfNights = noOfNights;
	}

	public int getNoOfCases() {
		return noOfCases;
	}

	public void setNoOfCases(int noOfCases) {
		this.noOfCases = noOfCases;
	}

	// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application CR.
	private String nbrOfVehicle;
	// End

	// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application CR.
	private String conEmail;
	// End

	/**
	 * @return Returns the sNo.
	 */
	public String getSNo() {
		return sNo;
	}

	/**
	 * @param no The sNo to set.
	 */
	public void setSNo(String no) {
		sNo = no;
	}

	/**
	 * @return Returns the appSeqNbr.
	 */
	public String getAppSeqNbr() {
		return appSeqNbr;
	}

	/**
	 * @param appSeqNbr The appSeqNbr to set.
	 */
	public void setAppSeqNbr(String appSeqNbr) {
		this.appSeqNbr = appSeqNbr;
	}

	/**
	 * @return Returns the varCode.
	 */
	public String getVarCode() {
		return varCode;
	}

	/**
	 * @param varCode The varCode to set.
	 */
	public void setVarCode(String varCode) {
		this.varCode = varCode;
	}

	/**
	 * @return Returns the voyNbr.
	 */
	public String getVoyNbr() {
		return voyNbr;
	}

	/**
	 * @param voyNbr The voyNbr to set.
	 */
	public void setVoyNbr(String voyNbr) {
		this.voyNbr = voyNbr;
	}

	/**
	 * @return Returns the appRefNbr.
	 */
	public String getAppRefNbr() {
		return appRefNbr;
	}

	/**
	 * @param appRefNbr The appRefNbr to set.
	 */
	public void setAppRefNbr(String appRefNbr) {
		this.appRefNbr = appRefNbr;
	}

	/**
	 * @return Returns the appTypeCd.
	 */
	public String getAppTypeCd() {
		return appTypeCd;
	}

	/**
	 * @param appTypeCd The appTypeCd to set.
	 */
	public void setAppTypeCd(String appTypeCd) {
		this.appTypeCd = appTypeCd;
	}

	/**
	 * @return Returns the appTypeName.
	 */
	public String getAppTypeName() {
		return appTypeName;
	}

	/**
	 * @param appTypeName The appTypeName to set.
	 */
	public void setAppTypeName(String appTypeName) {
		this.appTypeName = appTypeName;
	}

	/**
	 * @return Returns the appDttm.
	 */
	public String getAppDttm() {
		return appDttm;
	}

	/**
	 * @param appDttm The appDttm to set.
	 */
	public void setAppDttm(String appDttm) {
		this.appDttm = appDttm;
	}

	/**
	 * @return Returns the billNbr.
	 */
	public String getBillNbr() {
		return billNbr;
	}

	/**
	 * @param billNbr The billNbr to set.
	 */
	public void setBillNbr(String billNbr) {
		this.billNbr = billNbr;
	}

	/**
	 * @return Returns the coName.
	 */
	public String getCoName() {
		return coName;
	}

	/**
	 * @param coName The coName to set.
	 */
	public void setCoName(String coName) {
		this.coName = coName;
	}

	/**
	 * @return Returns the appStatusCd.
	 */
	public String getAppStatusCd() {
		return appStatusCd;
	}

	/**
	 * @param appStatusCd The appStatusCd to set.
	 */
	public void setAppStatusCd(String appStatusCd) {
		this.appStatusCd = appStatusCd;
	}

	/**
	 * @return Returns the appStatusName.
	 */
	public String getAppStatusName() {
		return appStatusName;
	}

	/**
	 * @param appStatusName The appStatusName to set.
	 */
	public void setAppStatusName(String appStatusName) {
		this.appStatusName = appStatusName;
	}

	/**
	 * @return Returns the coCd.
	 */
	public String getCoCd() {
		return coCd;
	}

	/**
	 * @param coCd The coCd to set.
	 */
	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}

	/**
	 * @return Returns the acctNbr.
	 */
	public String getAcctNbr() {
		return acctNbr;
	}

	/**
	 * @param acctNbr The acctNbr to set.
	 */
	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	/**
	 * @return Returns the vslName.
	 */
	public String getVslName() {
		return vslName;
	}

	/**
	 * @param vslName The vslName to set.
	 */
	public void setVslName(String vslName) {
		this.vslName = vslName;
	}

	/**
	 * @return Returns the typeCd.
	 */
	public String getTypeCd() {
		return typeCd;
	}

	/**
	 * @param typeCd The typeCd to set.
	 */
	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	/**
	 * @return Returns the typeName.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName The typeName to set.
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return Returns the docName.
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @param docName The docName to set.
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}

	/**
	 * @return Returns the userId.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return Returns the approveBy.
	 */
	public String getApproveBy() {
		return approveBy;
	}

	/**
	 * @param approveBy The approveBy to set.
	 */
	public void setApproveBy(String approveBy) {
		this.approveBy = approveBy;
	}

	/**
	 * @return Returns the approveDttm.
	 */
	public String getApproveDttm() {
		return approveDttm;
	}

	/**
	 * @param approveDttm The approveDttm to set.
	 */
	public void setApproveDttm(String approveDttm) {
		this.approveDttm = approveDttm;
	}

	/**
	 * @return Returns the closeBy.
	 */
	public String getCloseBy() {
		return closeBy;
	}

	/**
	 * @param closeBy The closeBy to set.
	 */
	public void setCloseBy(String closeBy) {
		this.closeBy = closeBy;
	}

	/**
	 * @return Returns the closeDttm.
	 */
	public String getCloseDttm() {
		return closeDttm;
	}

	/**
	 * @param closeDttm The closeDttm to set.
	 */
	public void setCloseDttm(String closeDttm) {
		this.closeDttm = closeDttm;
	}

	/**
	 * @return Returns the appRemarks.
	 */
	public String getAppRemarks() {
		return appRemarks;
	}

	/**
	 * @param appRemarks The appRemarks to set.
	 */
	public void setAppRemarks(String appRemarks) {
		this.appRemarks = appRemarks;
	}

	/**
	 * @return Returns the submitBy.
	 */
	public String getSubmitBy() {
		return submitBy;
	}

	/**
	 * @param submitBy The submitBy to set.
	 */
	public void setSubmitBy(String submitBy) {
		this.submitBy = submitBy;
	}

	/**
	 * @return Returns the submitDttm.
	 */
	public String getSubmitDttm() {
		return submitDttm;
	}

	/**
	 * @param submitDttm The submitDttm to set.
	 */
	public void setSubmitDttm(String submitDttm) {
		this.submitDttm = submitDttm;
	}

	/**
	 * @return Returns the approveBillBy.
	 */
	public String getApproveBillBy() {
		return approveBillBy;
	}

	/**
	 * @param approveBillBy The approveBillBy to set.
	 */
	public void setApproveBillBy(String approveBillBy) {
		this.approveBillBy = approveBillBy;
	}

	/**
	 * @return Returns the approveBillDttm.
	 */
	public String getApproveBillDttm() {
		return approveBillDttm;
	}

	/**
	 * @param approveBillDttm The approveBillDttm to set.
	 */
	public void setApproveBillDttm(String approveBillDttm) {
		this.approveBillDttm = approveBillDttm;
	}

	/**
	 * @return Returns the supportBy.
	 */
	public String getSupportBy() {
		return supportBy;
	}

	/**
	 * @param supportBy The supportBy to set.
	 */
	public void setSupportBy(String supportBy) {
		this.supportBy = supportBy;
	}

	/**
	 * @return Returns the supportDttm.
	 */
	public String getSupportDttm() {
		return supportDttm;
	}

	/**
	 * @param supportDttm The supportDttm to set.
	 */
	public void setSupportDttm(String supportDttm) {
		this.supportDttm = supportDttm;
	}

	/**
	 * @return Returns the supportRemarks.
	 */
	public String getSupportRemarks() {
		return supportRemarks;
	}

	/**
	 * @param supportRemarks The supportRemarks to set.
	 */
	public void setSupportRemarks(String supportRemarks) {
		this.supportRemarks = supportRemarks;
	}

	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
	/**
	 * @return Returns the conPerson.
	 */
	public String getConPerson() {
		return conPerson;
	}

	/**
	 * @param conPerson The conPerson to set.
	 */
	public void setConPerson(String conPerson) {
		this.conPerson = conPerson;
	}

	/**
	 * @return Returns the conTel.
	 */
	public String getConTel() {
		return conTel;
	}

	/**
	 * @param conTel The conTel to set.
	 */
	public void setConTel(String conTel) {
		this.conTel = conTel;
	}

	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
	// Added on 20/07/2007 by Punitha.To display vessel details
	/**
	 * @return Returns the inVoyNbr.
	 */
	public String getInVoyNbr() {
		return inVoyNbr;
	}

	/**
	 * @param inVoyNbr The inVoyNbr to set.
	 */
	public void setInVoyNbr(String inVoyNbr) {
		this.inVoyNbr = inVoyNbr;
	}

	/**
	 * @return Returns the outVoyNbr.
	 */
	public String getOutVoyNbr() {
		return outVoyNbr;
	}

	/**
	 * @param outVoyNbr The outVoyNbr to set.
	 */
	public void setOutVoyNbr(String outVoyNbr) {
		this.outVoyNbr = outVoyNbr;
	}

	/**
	 * @return Returns the atbDttm.
	 */
	public String getAtbDttm() {
		return atbDttm;
	}

	/**
	 * @param atbDttm The atbDttm to set.
	 */
	public void setAtbDttm(String atbDttm) {
		this.atbDttm = atbDttm;
	}

	/**
	 * @return Returns the atuDttm.
	 */
	public String getAtuDttm() {
		return atuDttm;
	}

	/**
	 * @param atbDttm The atuDttm to set.
	 */
	public void setAtuDttm(String atuDttm) {
		this.atuDttm = atuDttm;
	}
	// Ended by Punitha

	// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application
	// CR-OPS-20110110-09.
	public String getNbrOfVehicle() {
		return nbrOfVehicle;
	}

	public void setNbrOfVehicle(String nbrOfVehicle) {
		this.nbrOfVehicle = nbrOfVehicle;
	}

	// End
	/**
	 * @return the conEmail
	 */
	public String getConEmail() {
		return conEmail;
	}

	/**
	 * @param conEmail the conEmail to set
	 */
	public void setConEmail(String conEmail) {
		this.conEmail = conEmail;
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

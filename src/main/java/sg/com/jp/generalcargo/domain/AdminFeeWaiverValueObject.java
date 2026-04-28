package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * AdminFeeWaiverValueObject.
 *
 * @author  MCConsulting
 * @version 0.1
 *  * Revision History
 * ---------------
 * Author     Request Number    Description of Change     Version     Date Released
 * MCCOnsulting              Admin fee waiver calls to oscar and waiver report 20 Feb 2015
 */
public class AdminFeeWaiverValueObject implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -184869795344957197L;

    String adviceId;
    String wanAdviceNbr;
    String waiverType;
    String vesselVoy;
    String refNumber;
    String asnNumber;
    String requestedAt;
    String requestedBy;
    String waiverReasons;
    String waiverStatus;
    String approvedBy;
    String approvedAt;
    String waiverCompany;
    String companyAddress;
    String companyAccount;
    String varCode;
    String atbEtbBtr;
    String requestDate;
    String tariffDesc;
    String unitNbr;
    String unitRate;
    String gst;
    String totalAmount;
    String approvalRemarks;
    String createUserId;
    String waiverReportType;
	String vvCd;


    public AdminFeeWaiverValueObject(){
        adviceId = "";
        wanAdviceNbr = "";
        waiverType = "";
        vesselVoy = "";
        refNumber = "";
        asnNumber = "";
        requestedAt = "";
        requestedBy = "";
        waiverReasons = "";
        waiverStatus = "";
        approvedBy = "";
        approvedAt = "";
        waiverCompany = "";
        companyAddress = "";
        companyAccount = "";
        varCode = "";
        atbEtbBtr = "";
        requestDate = "";
        tariffDesc = "";
        unitNbr = "";
        unitRate = "";
        gst = "";
        totalAmount = "";
        approvalRemarks = "";
        createUserId = "";
        waiverReportType = "";
		vvCd = "";
    }


	public String getVvCd() {
        return vvCd;
    }


    public void setVvCd(String vvCd) {
        this.vvCd = vvCd;
    }
	
    public String getWaiverReportType() {
        return waiverReportType;
    }


    public void setWaiverReportType(String waiverReportType) {
        this.waiverReportType = waiverReportType;
    }


    public String getWanAdviceNbr() {
        return wanAdviceNbr;
    }


    public void setWanAdviceNbr(String wanAdviceNbr) {
        this.wanAdviceNbr = wanAdviceNbr;
    }


    public String getCreateUserId() {
        return createUserId;
    }


    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }


    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

    public String getWaiverType() {
        return waiverType;
    }
    public void setWaiverType(String waiverType) {
        this.waiverType = waiverType;
    }
    public String getVesselVoy() {
        return vesselVoy;
    }
    public void setVesselVoy(String vesselVoy) {
        this.vesselVoy = vesselVoy;
    }
    public String getAsnNumber() {
        return asnNumber;
    }
    public void setAsnNumber(String asnNumber) {
        this.asnNumber = asnNumber;
    }
    public String getRequestedAt() {
        return requestedAt;
    }
    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }
    public String getRequestedBy() {
        return requestedBy;
    }
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
    public String getWaiverReasons() {
        return waiverReasons;
    }
    public void setWaiverReasons(String waiverReasons) {
        this.waiverReasons = waiverReasons;
    }
    public String getWaiverStatus() {
        return waiverStatus;
    }
    public void setWaiverStatus(String waiverStatus) {
        this.waiverStatus = waiverStatus;
    }
    public String getApprovedBy() {
        return approvedBy;
    }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    public String getApprovedAt() {
        return approvedAt;
    }
    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }
    public String getAdviceId() {
        return adviceId;
    }

    public void setAdviceId(String adviceId) {
        this.adviceId = adviceId;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getWaiverCompany() {
        return waiverCompany;
    }

    public void setWaiverCompany(String waiverCompany) {
        this.waiverCompany = waiverCompany;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }

    public String getVarCode() {
        return varCode;
    }

    public void setVarCode(String varCode) {
        this.varCode = varCode;
    }

    public String getAtbEtbBtr() {
        return atbEtbBtr;
    }

    public void setAtbEtbBtr(String atbEtbBtr) {
        this.atbEtbBtr = atbEtbBtr;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getTariffDesc() {
        return tariffDesc;
    }

    public void setTariffDesc(String tariffDesc) {
        this.tariffDesc = tariffDesc;
    }

    public String getUnitNbr() {
        return unitNbr;
    }

    public void setUnitNbr(String unitNbr) {
        this.unitNbr = unitNbr;
    }

    public String getUnitRate() {
        return unitRate;
    }

    public void setUnitRate(String unitRate) {
        this.unitRate = unitRate;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

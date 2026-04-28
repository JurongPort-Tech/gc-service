package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscSpreaderValueObject implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String spreaderType;
    private String fromDate;
    private String fromTime;
    private String toDate;
    private String toTime;
    private String remarks;
    private String issueDt;
    private String issueTime;
    private String issueByStaff;
    private String receiveByCust;
    private String receiveDt;
    private String receiveTime;
    private String returnDttm;
    private String receiveByStaff;
    private String returnByCust;
    
    /**
     * @return Returns the fromDate.
     */
    public String getFromDate() {
        return fromDate;
    }
    /**
     * @param fromDate The fromDate to set.
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
    /**
     * @return Returns the fromTime.
     */
    public String getFromTime() {
        return fromTime;
    }
    /**
     * @param fromTime The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }
    /**
     * @return Returns the remarks.
     */
    public String getRemarks() {
        return remarks;
    }
    /**
     * @param remarks The remarks to set.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    /**
     * @return Returns the spreaderType.
     */
    public String getSpreaderType() {
        return spreaderType;
    }
    /**
     * @param spreaderType The spreaderType to set.
     */
    public void setSpreaderType(String spreaderType) {
        this.spreaderType = spreaderType;
    }
    /**
     * @return Returns the toDate.
     */
    public String getToDate() {
        return toDate;
    }
    /**
     * @param toDate The toDate to set.
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    /**
     * @return Returns the toTime.
     */
    public String getToTime() {
        return toTime;
    }
    /**
     * @param toTime The toTime to set.
     */
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
    
    /**
     * @return Returns the issueByStaff.
     */
    public String getIssueByStaff() {
        return issueByStaff;
    }
    /**
     * @param issueByStaff The issueByStaff to set.
     */
    public void setIssueByStaff(String issueByStaff) {
        this.issueByStaff = issueByStaff;
    }

    /**
     * @return Returns the issueDt.
     */
    public String getIssueDt() {
        return issueDt;
    }
    /**
     * @param issueDt The issueDt to set.
     */
    public void setIssueDt(String issueDt) {
        this.issueDt = issueDt;
    }
    /**
     * @return Returns the issueTime.
     */
    public String getIssueTime() {
        return issueTime;
    }
    /**
     * @param issueTime The issueTime to set.
     */
    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }
    /**
     * @return Returns the receiveDt.
     */
    public String getReceiveDt() {
        return receiveDt;
    }
    /**
     * @param receiveDt The receiveDt to set.
     */
    public void setReceiveDt(String receiveDt) {
        this.receiveDt = receiveDt;
    }
    /**
     * @return Returns the receiveTime.
     */
    public String getReceiveTime() {
        return receiveTime;
    }
    /**
     * @param receiveTime The receiveTime to set.
     */
    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }
    /**
     * @return Returns the receiveByCust.
     */
    public String getReceiveByCust() {
        return receiveByCust;
    }
    /**
     * @param receiveByCust The receiveByCust to set.
     */
    public void setReceiveByCust(String receiveByCust) {
        this.receiveByCust = receiveByCust;
    }
    /**
     * @return Returns the receiveByStaff.
     */
    public String getReceiveByStaff() {
        return receiveByStaff;
    }
    /**
     * @param receiveByStaff The receiveByStaff to set.
     */
    public void setReceiveByStaff(String receiveByStaff) {
        this.receiveByStaff = receiveByStaff;
    }
    /**
     * @return Returns the returnByCust.
     */
    public String getReturnByCust() {
        return returnByCust;
    }
    /**
     * @param returnByCust The returnByCust to set.
     */
    public void setReturnByCust(String returnByCust) {
        this.returnByCust = returnByCust;
    }
    /**
     * @return Returns the returnDttm.
     */
    public String getReturnDttm() {
        return returnDttm;
    }
    /**
     * @param returnDttm The returnDttm to set.
     */
    public void setReturnDttm(String returnDttm) {
        this.returnDttm = returnDttm;
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

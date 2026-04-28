package sg.com.jp.generalcargo.domain;

public class OSDReviewObject {

	    private Long osdReviewId;
	    private String vvcd;
	    private String osdReviewOption;            // Converted text (WE→With Operation Exemption, etc.)
	    private String lateArrivalReviewOption;
	 
	    private String queryOption;                // L / O / B
	    private String queryRemarks;
	    private String queryDttm;
	 
	    private String submitStatus;               // D / S / Q / P / A
	    private String submitUserId;
	    private String submitDttm;
	 
	    private String approveUserId;
	    private String approveDttm;
	 
	    private String createDttm;
	    private String createUserId;
	 
	    // -----------------------------
	    // Exemption row (OSD_REVIEW_EXEMPTION)
	    // -----------------------------
	    private String exemptionType;              // O or L
	    private String exemptionCode;              // exemption_code
	    private String exemptionMins;              // exemption_mins
	 
	    // -----------------------------
	    // File row (OSD_REVIEW_FILE_UPLOAD)
	    // -----------------------------
	    private String actualFileName;
	    private String assignedFileName;
	    private String fileExemptionType;          // O or L (for file row)
	 
	    // -----------------------------
	    // Getters / Setters
	    // -----------------------------
	    public Long getOsdReviewId() {
	        return osdReviewId;
	    }
	 
	    public void setOsdReviewId(Long osdReviewId) {
	        this.osdReviewId = osdReviewId;
	    }
	 
	    public String getVvcd() {
	        return vvcd;
	    }
	 
	    public void setVvcd(String vvcd) {
	        this.vvcd = vvcd;
	    }
	 
	    public String getOsdReviewOption() {
	        return osdReviewOption;
	    }
	 
	    public void setOsdReviewOption(String osdReviewOption) {
	        this.osdReviewOption = osdReviewOption;
	    }
	 
	    public String getLateArrivalReviewOption() {
	        return lateArrivalReviewOption;
	    }
	 
	    public void setLateArrivalReviewOption(String lateArrivalReviewOption) {
	        this.lateArrivalReviewOption = lateArrivalReviewOption;
	    }
	 
	    public String getQueryOption() {
	        return queryOption;
	    }
	 
	    public void setQueryOption(String queryOption) {
	        this.queryOption = queryOption;
	    }
	 
	    public String getQueryRemarks() {
	        return queryRemarks;
	    }
	 
	    public void setQueryRemarks(String queryRemarks) {
	        this.queryRemarks = queryRemarks;
	    }
	 
	    public String getQueryDttm() {
	        return queryDttm;
	    }
	 
	    public void setQueryDttm(String queryDttm) {
	        this.queryDttm = queryDttm;
	    }
	 
	    public String getSubmitStatus() {
	        return submitStatus;
	    }
	 
	    public void setSubmitStatus(String submitStatus) {
	        this.submitStatus = submitStatus;
	    }
	 
	    public String getSubmitUserId() {
	        return submitUserId;
	    }
	 
	    public void setSubmitUserId(String submitUserId) {
	        this.submitUserId = submitUserId;
	    }
	 
	    public String getSubmitDttm() {
	        return submitDttm;
	    }
	 
	    public void setSubmitDttm(String submitDttm) {
	        this.submitDttm = submitDttm;
	    }
	 
	    public String getApproveUserId() {
	        return approveUserId;
	    }
	 
	    public void setApproveUserId(String approveUserId) {
	        this.approveUserId = approveUserId;
	    }
	 
	    public String getApproveDttm() {
	        return approveDttm;
	    }
	 
	    public void setApproveDttm(String approveDttm) {
	        this.approveDttm = approveDttm;
	    }
	 
	    public String getCreateDttm() {
	        return createDttm;
	    }
	 
	    public void setCreateDttm(String createDttm) {
	        this.createDttm = createDttm;
	    }
	 
	    public String getCreateUserId() {
	        return createUserId;
	    }
	 
	    public void setCreateUserId(String createUserId) {
	        this.createUserId = createUserId;
	    }
	 
	    public String getExemptionType() {
	        return exemptionType;
	    }
	 
	    public void setExemptionType(String exemptionType) {
	        this.exemptionType = exemptionType;
	    }
	 
	    public String getExemptionCode() {
	        return exemptionCode;
	    }
	 
	    public void setExemptionCode(String exemptionCode) {
	        this.exemptionCode = exemptionCode;
	    }
	 
	    public String getExemptionMins() {
	        return exemptionMins;
	    }
	 
	    public void setExemptionMins(String exemptionMins) {
	        this.exemptionMins = exemptionMins;
	    }
	 
	    public String getActualFileName() {
	        return actualFileName;
	    }
	 
	    public void setActualFileName(String actualFileName) {
	        this.actualFileName = actualFileName;
	    }
	 
	    public String getAssignedFileName() {
	        return assignedFileName;
	    }
	 
	    public void setAssignedFileName(String assignedFileName) {
	        this.assignedFileName = assignedFileName;
	    }
	 
	    public String getFileExemptionType() {
	        return fileExemptionType;
	    }
	 
	    public void setFileExemptionType(String fileExemptionType) {
	        this.fileExemptionType = fileExemptionType;
	    }
	 
}
	 
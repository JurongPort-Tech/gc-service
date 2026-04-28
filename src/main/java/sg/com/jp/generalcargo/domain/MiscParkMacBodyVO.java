package sg.com.jp.generalcargo.domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscParkMacBodyVO extends MiscParkMacValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MiscParkMacValueObject objDettails;
	
	private String command;
    private String request;
    private String status;
    private String appDate;
    private String macType;
    private String fromDate;
    private String toDate;
    private String applyType;
    private String cust;
    private String account;
    private String varcode;
    private String coName;
    private String miscSeqNbr;
    private String appStatusCd;
    private String conPerson;
    private String conTel;
    private String appTypeCd;
    private String docMiscNumList;
    private String docCheck;
    private String macCheck;
    private String assignedName;
	
    public MiscParkMacValueObject getObjDettails() {
		return objDettails;
	}


	public void setObjDettails(MiscParkMacValueObject objDettails) {
		this.objDettails = objDettails;
	}
	

	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public String getRequest() {
		return request;
	}


	public void setRequest(String request) {
		this.request = request;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getAppDate() {
		return appDate;
	}


	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}


	public String getMacType() {
		return macType;
	}


	public void setMacType(String macType) {
		this.macType = macType;
	}


	public String getFromDate() {
		return fromDate;
	}


	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	
	
	public String getToDate() {
		return toDate;
	}


	public void setToDate(String toDate) {
		this.toDate = toDate;
	}


	public String getApplyType() {
		return applyType;
	}


	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}


	public String getCust() {
		return cust;
	}


	public void setCust(String cust) {
		this.cust = cust;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getVarcode() {
		return varcode;
	}


	public void setVarcode(String varcode) {
		this.varcode = varcode;
	}


	public String getCoName() {
		return coName;
	}


	public void setCoName(String coName) {
		this.coName = coName;
	}


	public String getMiscSeqNbr() {
		return miscSeqNbr;
	}


	public void setMiscSeqNbr(String miscSeqNbr) {
		this.miscSeqNbr = miscSeqNbr;
	}


	public String getAppStatusCd() {
		return appStatusCd;
	}


	public void setAppStatusCd(String appStatusCd) {
		this.appStatusCd = appStatusCd;
	}


	public String getConPerson() {
		return conPerson;
	}


	public void setConPerson(String conPerson) {
		this.conPerson = conPerson;
	}


	public String getConTel() {
		return conTel;
	}


	public void setConTel(String conTel) {
		this.conTel = conTel;
	}


	public String getAppTypeCd() {
		return appTypeCd;
	}


	public void setAppTypeCd(String appTypeCd) {
		this.appTypeCd = appTypeCd;
	}


	public String getDocMiscNumList() {
		return docMiscNumList;
	}


	public void setDocMiscNumList(String docMiscNumList) {
		this.docMiscNumList = docMiscNumList;
	}


	public String getDocCheck() {
		return docCheck;
	}


	public void setDocCheck(String docCheck) {
		this.docCheck = docCheck;
	}

	public String getMacCheck() {
		return macCheck;
	}


	public void setMacCheck(String macCheck) {
		this.macCheck = macCheck;
	}

	public String getAssignedName() {
		return assignedName;
	}


	public void setAssignedName(String assignedName) {
		this.assignedName = assignedName;
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

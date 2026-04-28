package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BillVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	private String billNumber;
	private Timestamp billDate;
	private String tariffType;
	private String vesselVoyageCode;
	private double gstAmount;
	private double totalAmount;
	private String currency;
	private int totalPage;
	private String printIndicator;
	private String postIndicator;
	private Timestamp postDate;
	private String mainCategory;
	private String operatorCode;
	private String agentAccount;
	private String remarks;
	// added by madhu(Zensar) for insert penalty of
	// rainRecords,totalTonnage,benchMarkRate into printing tables
	private String totalTonnage;
	// private String rainRecords;
	// private String benchMarkRate;
	private String vslProdRate;
	private String allocProdPrd;
	private Timestamp allocatedLastDttm;
	// ended here by madhu
	private List<BillAccountVO> a = new ArrayList<BillAccountVO>();
	private BillVesselInfoVO vesselInfo;
	// START - Code added for CR - CR-CAB-20050823-02 by Robert D, 31-May-2006
	private List<String> rebatebillList = new ArrayList<String>();
	// END - Code added for CR - CR-CAB-20050823-02 by Robert D, 31-May-2006
	// START - Code added for CR - OMC by VietNguyen D, 20-Jun-2006
	private String jobOrderRef;
	// START - Code added for CR - OMC by VietNguyen D, 20-Jun-2006

	/**
	 * for JPPL/IT/029/2013 Store all credit note advice number and show to client
	 * side
	 */
	private List<BillCreditNoteAdviceNumberVO> creditNoteAdviceNumberVOs = new ArrayList<BillCreditNoteAdviceNumberVO>();

	public BillVO() {
		billNumber = null;
		billDate = null;
	}

	// get and set methods
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public void setBillDate(Timestamp billDate) {
		this.billDate = billDate;
	}

	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
	}

	public void setVesselVoyageCode(String vesselVoyageCode) {
		this.vesselVoyageCode = vesselVoyageCode;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public void setPrintIndicator(String printIndicator) {
		this.printIndicator = printIndicator;
	}

	public void setPostIndicator(String postIndicator) {
		this.postIndicator = postIndicator;
	}

	public void setPostDate(Timestamp postDate) {
		this.postDate = postDate;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public void setAgentAccount(String agentAccount) {
		this.agentAccount = agentAccount;
	}

	public void setVesselInfo(BillVesselInfoVO vesselInfo) {
		this.vesselInfo = vesselInfo;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setTotalTonnage(String totalTonnage) {
		this.totalTonnage = totalTonnage;
	}

	public void setAllocatedLastDttm(Timestamp allocatedLastDttm) {
		this.allocatedLastDttm = allocatedLastDttm;
	}

	public String getBillNumber() {
		return (this.billNumber);
	}

	public Timestamp getBillDate() {
		return (this.billDate);
	}

	public String getTariffType() {
		return (this.tariffType);
	}

	public String getVesselVoyageCode() {
		return (this.vesselVoyageCode);
	}

	public double getGstAmount() {
		return (this.gstAmount);
	}

	public double getTotalAmount() {
		return (this.totalAmount);
	}

	public String getCurrency() {
		return (this.currency);
	}

	public int getTotalPage() {
		return (this.totalPage);
	}

	public String getPrintIndicator() {
		return (this.printIndicator);
	}

	public String getPostIndicator() {
		return (this.postIndicator);
	}

	public Timestamp getPostDate() {
		return (this.postDate);
	}

	public String getMainCategory() {
		return (this.mainCategory);
	}

	public String getOperatorCode() {
		return (this.operatorCode);
	}

	public String getAgentAccount() {
		return (this.agentAccount);
	}

	public BillVesselInfoVO getVesselInfo() {
		return (this.vesselInfo);
	}

	public String getRemarks() {
		return (this.remarks);
	}

	public String getTotalTonnage() {
		return (this.totalTonnage);
	}

	public Timestamp getAllocatedLastDttm() {
		return allocatedLastDttm;
	}

	public List<BillCreditNoteAdviceNumberVO> getCreditNoteAdviceNumberVOs() {
		return creditNoteAdviceNumberVOs;
	}

	public void setCreditNoteAdviceNumberVOs(List<BillCreditNoteAdviceNumberVO> creditNoteAdviceNumberVOs) {
		this.creditNoteAdviceNumberVOs = creditNoteAdviceNumberVOs;
	}

	public String getCustomerCode() {
		BillAccountVO vo = getAccount(0);
		if (vo != null) {
			return vo.getCustomer();
		} else {
			return null;
		}
	}

	public boolean isModified() {
		return (billNumber != null && billDate != null);
	}

	public boolean isDisplayPublished() {
		if (printIndicator != null && printIndicator.equals("P")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDisplayCustomised() {
		if (printIndicator != null && printIndicator.equals("C")) {
			return true;
		} else {
			return false;
		}
	}

	public void addAccount(BillAccountVO vo) {
		a.add(vo);
	}

	private BillAccountVO getAccount(int index) {
		if (index >= a.size() || index < 0)
			return null;
		return (BillAccountVO) a.get(index);
	}

	public BillAccountVO[] getAllAccount() {
		if (a.size() < 1)
			return null;
		BillAccountVO[] vo = new BillAccountVO[a.size()];
		for (int i = 0; i < a.size(); i++) {
			vo[i] = (BillAccountVO) a.get(i);
		}
		return vo;
	}

	public int getAccountCount() {
		return a.size();
	}

	public void setAllAccount(List<BillAccountVO> newA) {
		if (newA != null) {
			this.a = newA;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("---Bill---\n");
		sb.append("BillNumber : ");
		sb.append(this.getBillNumber());
		sb.append("\n");
		sb.append("BillDttm   : ");
		sb.append(this.getBillDate());
		sb.append("\n");
		sb.append("BillType   : ");
		sb.append(this.getTariffType());
		sb.append("\n");
		sb.append("BillAmt    : ");
		sb.append(this.getTotalAmount() + this.getGstAmount());
		sb.append("\n");

		for (int i = 0; i < this.getAccountCount(); i++) {
			sb.append(this.getAccount(i).toString());
		}
		return sb.toString();
	}

	// START - Code added for CR - CR-CAB-20050823-02 by Robert D, 31-May-2006
	public List<String> getRebatebillList() {
		return rebatebillList;
	}

	public void setRebatebillList(List<String> rebatebillList) {
		this.rebatebillList = rebatebillList;
	}

	// END - Code added for CR - CR-CAB-20050823-02 by Robert D, 31-May-2006
	public String getVslProdRate() {
		return vslProdRate;
	}

	public void setVslProdRate(String vslProdRate) {
		this.vslProdRate = vslProdRate;
	}

	public String getAllocProdPrd() {
		return allocProdPrd;
	}

	public void setAllocProdPrd(String allocProdPrd) {
		this.allocProdPrd = allocProdPrd;
	}

	public String getJobOrderRef() {
		return jobOrderRef;
	}

	public void setJobOrderRef(String jobOrderRef) {
		this.jobOrderRef = jobOrderRef;
	}
}

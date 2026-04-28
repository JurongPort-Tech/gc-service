package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillItemVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	private int id;
	private String coa;
	private String mainCategory;
	private String subCategory;
	private int version;
	private String tariffCode;
	private String tariffDescription;
	private int tierSequenceNumber;
	private String contract;
	private int totalContainer;
	private double totalTime;
	private double totalOtherUnit;
	private double unitRate;
	private double gst;
	private double gstAmount;
	private double totalAmount;
	private String vesselVoyageCode;
	private String slotOperatorCode;
	private String containerOperatorCode;
	private String transactionCode;
	private String postIndicator;
	private Timestamp postTime;
	private BillVesselInfoVO vesselInfo;
	private BillSupportInfoVO supportInfo;
	private String rateDescription;
	private int contractYear;
	private String remarks;
	private String fmasGstCode;
	private String invoiceType;
	/*------ START - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/
	// package name for bill item - for N4 Billing.
	private String packageName;
	// event type for bill item - for N4 Billing.
	private String eventType;
	/*------ END - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/
	/*------ START - Added - LongDH1/FPT (15-Nov-2011) - CAP for N4 Billing CR ------*/
	private XMLGregorianCalendar eventStartDate;
	/*------ END - Added - LongDH1/FPT (15-Nov-2011) - CAP for N4 Billing CR ------*/
	// bill support info specifically marine ba info
	private List<BillSupportInfoVO> si = new ArrayList<BillSupportInfoVO>();
	// bill container
	private List<BillContainerVO> aCntr = new ArrayList<BillContainerVO>();
	// bill charge
	private List<BillChargeVO> aCharge = new ArrayList<BillChargeVO>();
	// TT billing changes CR-CAB-20050823-02 - Valli
	private List<Double> aOtherUnit = new ArrayList<Double>();
	private List<BillMiscVO> aMisc = new ArrayList<BillMiscVO>();
	private List<BillJobVO> aJob = new ArrayList<BillJobVO>();
	private List<VtsbValueObject> aVb = new ArrayList<VtsbValueObject>();

	public BillItemVO() {
		id = -1;
		contractYear = 0;
	}

	// get set methods
	public void setId(int id) {
		this.id = id;
	}

	public void setCoa(String coa) {
		this.coa = coa;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setTariffCode(String tariffCode) {
		this.tariffCode = tariffCode;
	}

	public void setTariffDescription(String tariffDescription) {
		this.tariffDescription = tariffDescription;
	}

	public void setTierSequenceNumber(int tierSequenceNumber) {
		this.tierSequenceNumber = tierSequenceNumber;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public void setTotalContainer(int totalContainer) {
		this.totalContainer = totalContainer;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public void setTotalOtherUnit(double totalOtherUnit) {
		this.totalOtherUnit = totalOtherUnit;
	}

	public void setUnitRate(double unitRate) {
		this.unitRate = unitRate;
	}

	public void setGst(double gst) {
		this.gst = gst;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setVesselVoyageCode(String vesselVoyageCode) {
		this.vesselVoyageCode = vesselVoyageCode;
	}

	public void setSlotOperatorCode(String slotOperatorCode) {
		this.slotOperatorCode = slotOperatorCode;
	}

	public void setContainerOperatorCode(String containerOperatorCode) {
		this.containerOperatorCode = containerOperatorCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public void setPostIndicator(String postIndicator) {
		this.postIndicator = postIndicator;
	}

	public void setPostTime(Timestamp postTime) {
		this.postTime = postTime;
	}

	public void setVesselInfo(BillVesselInfoVO vesselInfo) {
		this.vesselInfo = vesselInfo;
	}

	public void setSupportInfo(BillSupportInfoVO supportInfo) {
		this.supportInfo = supportInfo;
	}

	public void setRateDescription(String rateDescription) {
		this.rateDescription = rateDescription;
	}

	public void setContractYear(int contractYear) {
		this.contractYear = contractYear;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setFmasGstCode(String fmasGstCode) {
		this.fmasGstCode = fmasGstCode;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	/*------ START - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	/*------ END - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/

	public int getId() {
		return (this.id);
	}

	public String getCoa() {
		return (this.coa);
	}

	public String getMainCategory() {
		return (this.mainCategory);
	}

	public String getSubCategory() {
		return (this.subCategory);
	}

	public int getVersion() {
		return (this.version);
	}

	public String getTariffCode() {
		return (this.tariffCode);
	}

	public String getTariffDescription() {
		return (this.tariffDescription);
	}

	public int getTierSequenceNumber() {
		return (this.tierSequenceNumber);
	}

	public String getContract() {
		return (this.contract);
	}

	public int getTotalContainer() {
		return (this.totalContainer);
	}

	public double getTotalTime() {
		return (this.totalTime);
	}

	public double getTotalOtherUnit() {
		return (this.totalOtherUnit);
	}

	public double getUnitRate() {
		return (this.unitRate);
	}

	public double getGst() {
		return (this.gst);
	}

	public double getGstAmount() {
		return (this.gstAmount);
	}

	public double getTotalAmount() {
		return (this.totalAmount);
	}

	public String getVesselVoyageCode() {
		return (this.vesselVoyageCode);
	}

	public String getSlotOperatorCode() {
		return (this.slotOperatorCode);
	}

	public String getContainerOperatorCode() {
		return (this.containerOperatorCode);
	}

	public String getTransactionCode() {
		return (this.transactionCode);
	}

	public String getPostIndicator() {
		return (this.postIndicator);
	}

	public Timestamp getPostTime() {
		return (this.postTime);
	}

	public BillVesselInfoVO getVesselInfo() {
		return (this.vesselInfo);
	}

	public BillSupportInfoVO getSupportInfo() {
		return (this.supportInfo);
	}

	public String getRateDescription() {
		return (this.rateDescription);
	}

	public int getContractYear() {
		return (this.contractYear);
	}

	public String getRemarks() {
		return (this.remarks);
	}

	public String getFmasGstCode() {
		return (this.fmasGstCode);
	}

	public String getInvoiceType() {
		return (this.invoiceType);
	}

	/*------ START - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/
	public String getEventType() {
		return eventType;
	}

	public String getPackageName() {
		return packageName;
	}
	/*------ END - Added - LongDH1/FPT (03-Nov-2011) - CAP for N4 Billing CR ------*/

	public boolean isModified() {
		return (id > 0);
	}

	// add bill container
	public void addContainer(BillContainerVO vo) {
		aCntr.add(vo);
	}

	public BillContainerVO getContainer(int index) { // NO_UCD (use private)
		if (index >= aCntr.size() || index < 0)
			return null;
		return (BillContainerVO) aCntr.get(index);
	}

	public BillContainerVO[] getAllContainer() {
		if (aCntr.size() < 1)
			return null;
		BillContainerVO[] vo = new BillContainerVO[aCntr.size()];
		for (int i = 0; i < aCntr.size(); i++) {
			vo[i] = (BillContainerVO) aCntr.get(i);
		}
		return vo;
	}

	public int getContainerCount() {
		return aCntr.size();
	}

	public void setAllContainer(List<BillContainerVO> a) {
		if (a != null) {
			this.aCntr = a;
		}
	}

	public BillChargeVO[] getAllCharge() {
		if (aCharge.size() < 1)
			return null;
		BillChargeVO[] vo = new BillChargeVO[aCharge.size()];
		for (int i = 0; i < aCharge.size(); i++) {
			vo[i] = (BillChargeVO) aCharge.get(i);
		}
		return vo;
	}

	public int getChargeCount() {
		return aCharge.size();
	}

	public void setAllCharge(List<BillChargeVO> a) {
		if (a != null) {
			this.aCharge = a;
		}
	}

	// support info
	public int getSupportInfoCount() {
		return si.size();
	}

	public BillSupportInfoVO[] getAllSupportInfo() {
		if (si.size() < 1)
			return null;
		BillSupportInfoVO[] vo = new BillSupportInfoVO[si.size()];
		for (int i = 0; i < si.size(); i++) {
			vo[i] = (BillSupportInfoVO) si.get(i);
		}
		return vo;
	}

	public void setAllSupportInfo(List<BillSupportInfoVO> newSi) {
		if (newSi != null) {
			this.si = newSi;
		}
	}

	// START - CR-CAB-20050823-02 TT Billing changes - Valli 14-Aug-2006
	// add bill misc
	public void addMisc(BillMiscVO vo) {
		aMisc.add(vo);
	}

	public BillMiscVO[] getAllMisc() {
		if (aMisc.size() < 1)
			return null;
		BillMiscVO[] vo = new BillMiscVO[aMisc.size()];
		for (int i = 0; i < aMisc.size(); i++) {
			vo[i] = (BillMiscVO) aMisc.get(i);
		}
		return vo;
	}

	public int getMiscCount() {
		return aMisc.size();
	}

	public void setAllMisc(List<BillMiscVO> a) {
		if (a != null) {
			this.aMisc = a;
		}
	}
	// END - CR-CAB-20050823-02 TT Billing changes - Valli 14-Aug-2006

	// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
	public void addJob(BillJobVO vo) {
		aJob.add(vo);
	}

	public BillJobVO[] getAllJob() {
		if (aJob.size() < 1)
			return null;
		BillJobVO[] vo = new BillJobVO[aJob.size()];
		for (int i = 0; i < aJob.size(); i++) {
			vo[i] = (BillJobVO) aJob.get(i);
		}
		return vo;
	}

	public int getJobCount() {
		return aJob.size();
	}

	public void setAllJob(List<BillJobVO> a) {
		if (a != null) {
			this.aJob = a;
		}
	}
	// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END

	// Added for Vehicle Booking : Start
//	 add bill vb
	public void addVb(VtsbValueObject vo) {
		aVb.add(vo);
	}

	public VtsbValueObject[] getAllVb() {
		if (aVb.size() < 1)
			return null;
		VtsbValueObject[] vo = new VtsbValueObject[aVb.size()];
		for (int i = 0; i < aVb.size(); i++) {
			vo[i] = (VtsbValueObject) aVb.get(i);
		}
		return vo;
	}

	public int getVbCount() {
		return aVb.size();
	}

	public void setAllVb(List<VtsbValueObject> a) {
		if (a != null) {
			this.aVb = a;
		}
	}
	// Added for Vehicle Booking : End

	// START - CR-CAB-20050823-02 TT Billing changes - Valli
	//	 other unit
	public void addOtherUnit(double otherUnit) {
		aOtherUnit.add(new Double(otherUnit));
	}

	public double[] getAllOtherUnit() {
		if (aOtherUnit.size() < 1)
			return null;
		double[] allOtherUnits = new double[aOtherUnit.size()];
		for (int i = 0; i < aOtherUnit.size(); i++) {
			allOtherUnits[i] = ((Double) aOtherUnit.get(i)).doubleValue();
		}
		return allOtherUnits;
	}

	public int getOtherUnitCount() {
		return aOtherUnit.size();
	}

	public void setAllOtherUnit(List<Double> a) {
		if (a != null) {
			this.aOtherUnit = a;
		}
	}
	// END - CR-CAB-20050823-02 TT Billing changes - Valli 14-Aug-2006

	/*------ START - Added - LongDH1/FPT (15-Nov-2011) - CAP for N4 Billing CR ------*/
	public XMLGregorianCalendar getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(XMLGregorianCalendar eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	/*------ END - Added - LongDH1/FPT (15-Nov-2011) - CAP for N4 Billing CR ------*/
	// GSS Enhancements
	private String vtsb_remarks;

	public String getVtsb_remarks() {
		return vtsb_remarks;
	}

	public void setVtsb_remarks(String vtsbRemarks) {
		vtsb_remarks = vtsbRemarks;
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

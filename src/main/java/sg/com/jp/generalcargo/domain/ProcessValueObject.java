package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessValueObject extends UserTimestampVO {
	private static final Log log = LogFactory.getLog(ProcessValueObject.class);
	private static final long serialVersionUID = 1L;
	private Integer versionNbrPubl;
	private Integer versionNbrCust;
	private String custCd;
	private String acctNbr;
	private String contractNbr;
	// added by swho on 27022004 to cater for containerized cargo
	private Integer contractualYr;
	// end add by swho
	private String currency;
	private String cntrOprCd;
	private String slotOprCd;
	private String oprCd;
	private Timestamp startDttm;
	private String startDttmTypeCd;
	private Timestamp endDttm;
	private String endDttmTypeCd;
	private Integer chargeTier;
	// Added by Jitten starts here
	private long overStayPeriod;
	private long amount;
	// Added by Jitten ends here
	private CntrEventLogValueObject cntrEventLogValueObject;
	private TariffMainVO tariffMainValueObject;
	private TariffContainerVO tariffContainerValueObject;
	private List<TariffMainVO> tariffMainList = new ArrayList<TariffMainVO>();
	private boolean errorInd;
	// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
	private String jobOrderRef;
	private long jobId;
	private String jobType;
	private double totalAmount;
	private String jobStWaCd;
	private String jobStWsCd;
	// Chue Thing added on 29 Feb 2013
	private String poNum;
	private String jobExeDttm;
	// VietNguyen (FPT) added on 07-Dec-2011 for OMC : END
	private List<VtsbValueObject> vbSupportList;
	// Added changes for OverStayDockage Partial Waiver input -Mcconsulting pteLtd
	// Aug'15
	private String waiverType;
	private String billableDuration;
	private String isRejected;
	// changes ends for Over stay dockage waiver -MCconsulting pte ltd(Aug'15)

	public ProcessValueObject() {
		versionNbrPubl = new Integer(0);
		versionNbrCust = new Integer(0);
		custCd = null;
		acctNbr = null;
		contractNbr = null;
		// added by swho on 27022004 to cater for containerized cargo
		contractualYr = new Integer(0);
		// end add by swho
		currency = null;
		cntrOprCd = null;
		slotOprCd = null;
		oprCd = null;
		startDttm = null;
		startDttmTypeCd = null;
		endDttm = null;
		endDttmTypeCd = null;
		chargeTier = new Integer(0);
		cntrEventLogValueObject = new CntrEventLogValueObject();
		tariffMainValueObject = new TariffMainVO();
		tariffContainerValueObject = new TariffContainerVO();
		tariffMainList = new ArrayList<TariffMainVO>();
		errorInd = false;
		// Added by Jitten starts here
		overStayPeriod = 0;
		amount = 0;
		// Added by Jitten ends here
		// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
		jobOrderRef = null;
		jobType = null;
		totalAmount = 0;
		jobId = 0;
		jobStWaCd = null;
		jobStWsCd = null;
		// Chue Thing - added on 19 Feb 2013
		poNum = null;
		jobExeDttm = null;
		// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END
		vbSupportList = new ArrayList<VtsbValueObject>();
	}

	/** Creates new ProcessValueObject */
	public ProcessValueObject(ProcessValueObject processValueObject) {
		this.copy(processValueObject);
	}

	public void copy(ProcessValueObject processValueObject) {
		this.versionNbrPubl = processValueObject.getVersionNbrPubl();
		this.versionNbrCust = processValueObject.getVersionNbrCust();
		this.custCd = processValueObject.getCustCd();
		this.acctNbr = processValueObject.getAcctNbr();
		this.contractNbr = processValueObject.getContractNbr();
		// added by swho on 27022004 to cater for containerized cargo
		this.contractualYr = processValueObject.getContractualYr();
		// end add by swho
		this.currency = processValueObject.getCurrency();
		this.cntrOprCd = processValueObject.getCntrOprCd();
		this.slotOprCd = processValueObject.getSlotOprCd();
		this.oprCd = processValueObject.getOprCd();
		this.startDttm = processValueObject.getStartDttm();
		this.startDttmTypeCd = processValueObject.getStartDttmTypeCd();
		this.endDttm = processValueObject.getEndDttm();
		this.endDttmTypeCd = processValueObject.getEndDttmTypeCd();
		this.chargeTier = processValueObject.getChargeTier();
		this.errorInd = processValueObject.getErrorInd();
		// Added by Jitten starts here
		this.overStayPeriod = processValueObject.getOverStayPeriod();
		this.amount = processValueObject.getAmount();
		// Added by Jitten ends here
		// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
		this.jobOrderRef = processValueObject.getJobOrderRef();
		this.totalAmount = processValueObject.getTotalAmount();
		this.jobId = processValueObject.getJobId();
		this.jobType = processValueObject.getJobType();
		this.jobStWsCd = processValueObject.getJobStWsCd();
		this.jobStWaCd = processValueObject.getJobStWaCd();
		// chue thing - added on 19 Feb 2013
		this.poNum = processValueObject.getPoNum();
		this.jobExeDttm = processValueObject.getJobExeDttm();
		// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END
		this.vbSupportList = processValueObject.getVbSupportList();
		// Added for Over stay dockage waiver -MCconsulting pte ltd(Aug'15)
		this.billableDuration = processValueObject.getBillableDuration();
		this.waiverType = processValueObject.getWaiverType();
		this.isRejected = processValueObject.getIsRejected();
		// changes ends for Over stay dockage waiver -MCconsulting pte ltd(Aug'15)

		if (processValueObject.getCntrEventLogValueObject()
				.isModified(processValueObject.getCntrEventLogValueObject())) {
			this.cntrEventLogValueObject = new CntrEventLogValueObject(processValueObject.getCntrEventLogValueObject());
		} else {
			this.cntrEventLogValueObject = new CntrEventLogValueObject();
		}
		if (processValueObject.getTariffMainValueObject().isModified(processValueObject.getTariffMainValueObject())) {
			this.tariffMainValueObject = new TariffMainVO(processValueObject.getTariffMainValueObject());
		} else {
			this.tariffMainValueObject = new TariffMainVO();
		}
		if (processValueObject.getTariffContainerValueObject()
				.isModified(processValueObject.getTariffContainerValueObject())) {
			this.tariffContainerValueObject = new TariffContainerVO(processValueObject.getTariffContainerValueObject());
		} else {
			this.tariffContainerValueObject = new TariffContainerVO();
		}
		if (processValueObject.getMainCount() != 0) {
			this.removeAllMain();
			for (int xcnt = 0; xcnt < processValueObject.getMainCount(); xcnt++) {
				TariffMainVO tariffMainValueObject = new TariffMainVO(processValueObject.getMain(xcnt));
				this.addMain(tariffMainValueObject);
			}
		}
	}

	public Integer getVersionNbrPubl() {
		return (versionNbrPubl);
	}

	public Integer getVersionNbrCust() {
		return (versionNbrCust);
	}

	public String getCustCd() {
		return (custCd);
	}

	public String getAcctNbr() {
		return (acctNbr);
	}

	public String getContractNbr() {
		return (contractNbr);
	}

	public Integer getContractualYr() {
		return (contractualYr);
	}

	public String getCurrency() {
		return (currency);
	}

	public String getCntrOprCd() {
		return (cntrOprCd);
	}

	public String getSlotOprCd() {
		return (slotOprCd);
	}

	public String getOprCd() {
		return (oprCd);
	}

	public Timestamp getStartDttm() {
		return (startDttm);
	}

	public String getStartDttmTypeCd() {
		return (startDttmTypeCd);
	}

	public Timestamp getEndDttm() {
		return (endDttm);
	}

	public String getEndDttmTypeCd() {
		return (endDttmTypeCd);
	}

	public Integer getChargeTier() {
		return (chargeTier);
	}

	public CntrEventLogValueObject getCntrEventLogValueObject() {
		return (cntrEventLogValueObject);
	}

	public TariffMainVO getTariffMainValueObject() {
		return (tariffMainValueObject);
	}

	public TariffContainerVO getTariffContainerValueObject() {
		return (tariffContainerValueObject);
	}

	public boolean getErrorInd() {
		return (errorInd);
	}

	public void setVersionNbrPubl(Integer versionNbrPubl) {
		this.versionNbrPubl = versionNbrPubl;
	}

	public void setVersionNbrCust(Integer versionNbrCust) {
		this.versionNbrCust = versionNbrCust;
	}

	public void setCustCd(String custCd) {
		this.custCd = custCd;
	}

	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	public void setContractNbr(String contractNbr) {
		this.contractNbr = contractNbr;
	}

	public void setContractualYr(Integer contractualYr) {
		this.contractualYr = contractualYr;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCntrOprCd(String cntrOprCd) {
		this.cntrOprCd = cntrOprCd;
	}

	public void setSlotOprCd(String slotOprCd) {
		this.slotOprCd = slotOprCd;
	}

	public void setOprCd(String oprCd) {
		this.oprCd = oprCd;
	}

	public void setStartDttm(Timestamp startDttm) {
		this.startDttm = startDttm;
	}

	public void setStartDttmTypeCd(String startDttmTypeCd) {
		this.startDttmTypeCd = startDttmTypeCd;
	}

	public void setEndDttm(Timestamp endDttm) {
		this.endDttm = endDttm;
	}

	public void setEndDttmTypeCd(String endDttmTypeCd) {
		this.endDttmTypeCd = endDttmTypeCd;
	}

	public void setChargeTier(Integer chargeTier) {
		this.chargeTier = chargeTier;
	}

	public void setCntrEventLogValueObject(CntrEventLogValueObject cntrEventLogValueObject) {
		this.cntrEventLogValueObject = cntrEventLogValueObject;
	}

	public void setTariffMainValueObject(TariffMainVO tariffMainValueObject) {
		this.tariffMainValueObject = tariffMainValueObject;
	}

	public void setTariffContainerValueObject(TariffContainerVO tariffContainerValueObject) {
		this.tariffContainerValueObject = tariffContainerValueObject;
	}

	public void setErrorInd(boolean errorInd) {
		this.errorInd = errorInd;
	}

	public int getMainCount() {
		return tariffMainList.size();
	}

	public void addMain(TariffMainVO tariffMainValueObject) {
		tariffMainList.add(tariffMainValueObject);
	}

	public TariffMainVO getMain(int index) {
		if (index >= tariffMainList.size() || index < 0) {
			return null;
		}
		return (TariffMainVO) tariffMainList.get(index);
	}

	public TariffMainVO[] getAllMain() {
		TariffMainVO[] valueObject = null;
		int xcnt = 0;

		if (tariffMainList.size() < 1) {
			return null;
		}

		valueObject = new TariffMainVO[tariffMainList.size()];

		for (xcnt = 0; xcnt < tariffMainList.size(); xcnt++) {
			valueObject[xcnt] = (TariffMainVO) tariffMainList.get(xcnt);
		}
		return valueObject;
	}

	private void removeAllMain() {
		if (tariffMainList != null) {
			tariffMainList.clear();
		} else {
			log.info("null value");
		}
	}

	public boolean isModified(ProcessValueObject processValueObject) {
		ProcessValueObject origVO = new ProcessValueObject();

		if (processValueObject.getVersionNbrPubl().equals(origVO.getVersionNbrPubl())
				&& processValueObject.getVersionNbrCust().equals(origVO.getVersionNbrCust())
				&& processValueObject.getCustCd() == origVO.getCustCd()
				&& processValueObject.getAcctNbr() == origVO.getAcctNbr()
				&& processValueObject.getContractNbr() == origVO.getContractNbr()
				&& processValueObject.getContractualYr().equals(origVO.getContractualYr())
				&& processValueObject.getCurrency() == origVO.getCurrency()
				&& processValueObject.getCntrOprCd() == origVO.getCntrOprCd()
				&& processValueObject.getSlotOprCd() == origVO.getSlotOprCd()
				&& processValueObject.getOprCd() == origVO.getOprCd()
				&& processValueObject.getStartDttm() == origVO.getStartDttm()
				&& processValueObject.getStartDttmTypeCd() == origVO.getStartDttmTypeCd()
				&& processValueObject.getEndDttm() == origVO.getEndDttm()
				&& processValueObject.getEndDttmTypeCd() == origVO.getEndDttmTypeCd()
				&& processValueObject.getChargeTier().equals(origVO.getChargeTier())
				&& !processValueObject.getCntrEventLogValueObject().isModified(origVO.getCntrEventLogValueObject())
				&& !processValueObject.getTariffMainValueObject().isModified(origVO.getTariffMainValueObject())
				&& !processValueObject.getTariffContainerValueObject()
						.isModified(origVO.getTariffContainerValueObject())
				&& processValueObject.getAllMain() == origVO.getAllMain()
				&& processValueObject.getErrorInd() == origVO.getErrorInd()
				&& processValueObject.getOverStayPeriod() == origVO.getOverStayPeriod()
				&& processValueObject.getAmount() == origVO.getAmount()
				&& processValueObject.getTotalAmount() == origVO.getTotalAmount()
				&& processValueObject.getJobId() == origVO.getJobId()
				&& processValueObject.getJobOrderRef() == origVO.getJobOrderRef()
				&& processValueObject.getJobType() == origVO.getJobType()
				&& processValueObject.getJobStWaCd() == origVO.getJobStWaCd()
				&& processValueObject.getJobStWsCd() == origVO.getJobStWsCd()
				&& processValueObject.getPoNum() == origVO.getPoNum()
				&& processValueObject.getJobExeDttm() == origVO.getJobExeDttm() &&
				// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
				processValueObject.getWaiverType() == origVO.getWaiverType()
				&& processValueObject.getBillableDuration() == origVO.getBillableDuration()
				&& processValueObject.getIsRejected() == origVO.getIsRejected()
		// Changes ends for OverStayDockage Partial Waiver request form
		// changes-MCConsulting pte ltd
		) {
			return false;
		} else {
			return true;
		}
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	// Added by Jitten starts here
	public long getOverStayPeriod() {
		return overStayPeriod;
	}

	public void setOverStayPeriod(long overStayPeriod) {
		this.overStayPeriod = overStayPeriod;
	}
	// Added by Jitten ends here

	// VietNguyen (FPT) added on 07-Dec-2011 for OMC : START
	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getJobOrderRef() {
		return jobOrderRef;
	}

	public void setJobOrderRef(String jobOrderRef) {
		this.jobOrderRef = jobOrderRef;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobStWaCd() {
		return jobStWaCd;
	}

	public void setJobStWaCd(String jobStWaCd) {
		this.jobStWaCd = jobStWaCd;
	}

	public String getJobStWsCd() {
		return jobStWsCd;
	}

	public void setJobStWsCd(String jobStWsCd) {
		this.jobStWsCd = jobStWsCd;
	}

	// VietNguyen (FPT) added on 02-Dec-2011 for OMC : END

	// chue thing added
	public String getPoNum() {
		return poNum;
	}

	public void setPoNum(String poNum) {
		this.poNum = poNum;
	}

	public String getJobExeDttm() {
		return jobExeDttm;
	}

	public void setJobExeDttm(String jobExeDttm) {
		this.jobExeDttm = jobExeDttm;
	}

	public List<VtsbValueObject> getVbSupportList() {
		return vbSupportList;
	}

	public void setVbSupportList(List<VtsbValueObject> vbSupportList) {
		this.vbSupportList = vbSupportList;
	}

	public void setWaiverType(String waiverType) {
		this.waiverType = waiverType;
	}

	public String getWaiverType() {
		return waiverType;
	}

	public void setBillableDuration(String billableDuration) {
		this.billableDuration = billableDuration;
	}

	public String getBillableDuration() {
		return billableDuration;
	}

	public void setIsRejected(String isRejected) {
		this.isRejected = isRejected;
	}

	public String getIsRejected() {
		return isRejected;
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

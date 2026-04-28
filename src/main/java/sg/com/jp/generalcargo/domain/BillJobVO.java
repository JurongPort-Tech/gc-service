package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillJobVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	private long jobId;
	private long jobDetId;
	private String jobRefNbr;
	private String jobType;
	private String serviceCd;
	private String subServiceCd;
	private String location;
	private Timestamp fromDttm;
	private Timestamp toDttm;
	private int unit;
	private double totalAmt;
	private BillSupportInfoVO supportInfo;

	public BillJobVO() {
		jobDetId = -1;
	}

	public boolean isModified() {
		return (jobDetId > 0);
	}

	public BillSupportInfoVO getSupportInfo() {
		return supportInfo;
	}

	public void setSupportInfo(BillSupportInfoVO supportInfo) {
		this.supportInfo = supportInfo;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public long getJobDetId() {
		return jobDetId;
	}

	public void setJobDetId(long jobDetId) {
		this.jobDetId = jobDetId;
	}

	public String getServiceCd() {
		return serviceCd;
	}

	public void setServiceCd(String serviceCd) {
		this.serviceCd = serviceCd;
	}

	public String getSubServiceCd() {
		return subServiceCd;
	}

	public void setSubServiceCd(String subServiceCd) {
		this.subServiceCd = subServiceCd;
	}

	public Timestamp getFromDttm() {
		return fromDttm;
	}

	public void setFromDttm(Timestamp fromDttm) {
		this.fromDttm = fromDttm;
	}

	public Timestamp getToDttm() {
		return toDttm;
	}

	public void setToDttm(Timestamp toDttm) {
		this.toDttm = toDttm;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public String getJobRefNbr() {
		return jobRefNbr;
	}

	public void setJobRefNbr(String jobRefNbr) {
		this.jobRefNbr = jobRefNbr;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
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

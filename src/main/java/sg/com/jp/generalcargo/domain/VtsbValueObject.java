package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

public class VtsbValueObject extends UserTimestampVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ref_nbr;
	private String seq_nbr;
	private String co_cd;
	private String status_cd;
	private String type_cd;
	private String veh_nbr;
	private Timestamp from_dttm;
	private String cets_status_cd;
	private double count;

	private String jobOrderRef;
	private String supportData;
	private int id;

	public String getRef_nbr() {
		return ref_nbr;
	}

	public void setRef_nbr(String ref_nbr) {
		this.ref_nbr = ref_nbr;
	}

	public String getSeq_nbr() {
		return seq_nbr;
	}

	public void setSeq_nbr(String seq_nbr) {
		this.seq_nbr = seq_nbr;
	}

	public String getCo_cd() {
		return co_cd;
	}

	public void setCo_cd(String co_cd) {
		this.co_cd = co_cd;
	}

	public String getStatus_cd() {
		return status_cd;
	}

	public void setStatus_cd(String status_cd) {
		this.status_cd = status_cd;
	}

	public String getType_cd() {
		return type_cd;
	}

	public void setType_cd(String type_cd) {
		this.type_cd = type_cd;
	}

	public String getVeh_nbr() {
		return veh_nbr;
	}

	public void setVeh_nbr(String veh_nbr) {
		this.veh_nbr = veh_nbr;
	}

	public Timestamp getFrom_dttm() {
		return from_dttm;
	}

	public void setFrom_dttm(Timestamp from_dttm) {
		this.from_dttm = from_dttm;
	}

	public String getCets_status_cd() {
		return cets_status_cd;
	}

	public void setCets_status_cd(String cets_status_cd) {
		this.cets_status_cd = cets_status_cd;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public String getJobOrderRef() {
		return jobOrderRef;
	}

	public void setJobOrderRef(String jobOrderRef) {
		this.jobOrderRef = jobOrderRef;
	}

	public String getSupportData() {
		return supportData;
	}

	public void setSupportData(String supportData) {
		this.supportData = supportData;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

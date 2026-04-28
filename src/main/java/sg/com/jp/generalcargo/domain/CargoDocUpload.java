package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoDocUpload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4181802214962735553L;
	
	private String vvCd;
	private String remarks;
	private String vesselNameInward;
	private String lastModifiedDate;
	private Boolean isSubmissionAllowed;

	private String VslName;
	private String VoyNo;
	private String VslNameVoyage;
	private String createdCustCode;
	
	private List<AuditTrailDetail> auditInfo;
	private List<CargoDocUploadDetail> CargoDocUploadInfo;

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getVesselNameInward() {
		return vesselNameInward;
	}

	public void setVesselNameInward(String vesselNameInward) {
		this.vesselNameInward = vesselNameInward;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public List<CargoDocUploadDetail> getCargoDocUploadInfo() {
		return CargoDocUploadInfo;
	}

	public void setCargoDocUploadInfo(List<CargoDocUploadDetail> cargoDocUploadInfo) {
		CargoDocUploadInfo = cargoDocUploadInfo;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public List<AuditTrailDetail> getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(List<AuditTrailDetail> auditInfo) {
		this.auditInfo = auditInfo;
	}

	public Boolean getIsSubmissionAllowed() {
		return isSubmissionAllowed;
	}

	public void setIsSubmissionAllowed(Boolean isSubmissionAllowed) {
		this.isSubmissionAllowed = isSubmissionAllowed;
	}

	public String getVslName() {
		return VslName;
	}

	public void setVslName(String vslName) {
		VslName = vslName;
	}

	public String getVoyNo() {
		return VoyNo;
	}

	public void setVoyNo(String voyNo) {
		VoyNo = voyNo;
	}

	public String getVslNameVoyage() {
		return VslNameVoyage;
	}

	public void setVslNameVoyage(String vslNameVoyage) {
		VslNameVoyage = vslNameVoyage;
	}

	public String getCreatedCustCode() {
		return createdCustCode;
	}

	public void setCreatedCustCode(String createdCustCode) {
		this.createdCustCode = createdCustCode;
	}

}

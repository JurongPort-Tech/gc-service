package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageDetails {
	private String vvCd;
	private String inwardVoyNo;
	private String vesselName;
	private String outVoyNo;
	private String voyageNo;
	private List<String> instructions;
	private List<Template> template;
	private Boolean isSubmissionAllowed;
	private String summary;
	
	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getInwardVoyNo() {
		return inwardVoyNo;
	}

	public void setInwardVoyNo(String inwardVoyNo) {
		this.inwardVoyNo = inwardVoyNo;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<String> instructions) {
		this.instructions = instructions;
	}

	public String getOutVoyNo() {
		return outVoyNo;
	}

	public void setOutVoyNo(String outVoyNo) {
		this.outVoyNo = outVoyNo;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public List<Template> getTemplate() {
		return template;
	}

	public void setTemplate(List<Template> template) {
		this.template = template;
	}

	public Boolean getIsSubmissionAllowed() {
		return isSubmissionAllowed;
	}

	public void setIsSubmissionAllowed(Boolean isSubmissionAllowed) {
		this.isSubmissionAllowed = isSubmissionAllowed;
	}


	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoDimensionDeclarationResponse extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5948478010657034556L;
	
	private List<CargoDimensionDeclaration> cargoDimensDecDet;
	private Boolean isSubmissionAllowed;

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public Boolean getIsSubmissionAllowed() {
		return isSubmissionAllowed;
	}

	public void setIsSubmissionAllowed(Boolean isSubmissionAllowed) {
		this.isSubmissionAllowed = isSubmissionAllowed;
	}

	public List<CargoDimensionDeclaration> getCargoDimensDecDet() {
		return cargoDimensDecDet;
	}

	public void setCargoDimensDecDet(List<CargoDimensionDeclaration> cargoDimensDecDet) {
		this.cargoDimensDecDet = cargoDimensDecDet;
	}

	

}

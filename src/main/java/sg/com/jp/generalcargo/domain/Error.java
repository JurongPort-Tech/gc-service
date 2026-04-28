package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */

@JsonInclude(Include.NON_NULL)
public class Error implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;

	private List<?> errors;

	public Error(String message) {
		this.message = message;
	}

	public Error(List<?> errors) {
		this.message = "Found " + errors.size() + " error" + (errors.size() > 1 ? "(s)" : "");
		this.errors = errors;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<?> getErrors() {
		return errors;
	}

	public void setErrors(List<?> errors) {
		this.errors = errors;
	}

}

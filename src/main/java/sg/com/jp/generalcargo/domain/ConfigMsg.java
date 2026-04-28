package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigMsg {
	private String hsCode;
	private String hsSubCodeFrom;
	private String hsSubCodeTo;
	private String question;
	@JsonProperty("Options") 
    public List<Option> options;
	public String answer;
	

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public String getHsSubCodeFrom() {
		return hsSubCodeFrom;
	}

	public void setHsSubCodeFrom(String hsSubCodeFrom) {
		this.hsSubCodeFrom = hsSubCodeFrom;
	}

	public String getHsSubCodeTo() {
		return hsSubCodeTo;
	}

	public void setHsSubCodeTo(String hsSubCodeTo) {
		this.hsSubCodeTo = hsSubCodeTo;
	}

	public String getQuestion() {
		return question;
	}


	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
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



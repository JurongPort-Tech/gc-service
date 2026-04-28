package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoDimensionDeclaration extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long dimDeclarSeqNum;
	private String vvCd;
	private String hsSubDesc;
	private String hsCode;
	private String hsSubCodeFrom;
	private String hsSubCodeTo;
	private String question;
	private List<Option> options;
	private String inputTime;
	private String answer;
	private String optionsString;

	public String getOptionsString() {
		optionsString = "";
		if (getOptions() != null && getOptions().size() > 0) {
			for (Option option : getOptions()) {
				if (optionsString != null && !optionsString.isEmpty()) {
					optionsString += " ; ";
				}
				optionsString += option.getValue();
			}
		}
		return optionsString;
	}

	public void setOptionsString(String optionsString) {
		if (optionsString != null && !optionsString.isEmpty()) {
			this.options = new ArrayList<>();
			for (String option : optionsString.split(" ; ")) {
				this.options.add(new Option(option));
			}
		}
		this.optionsString = optionsString;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
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

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}

	public long getDimDeclarSeqNum() {
		return dimDeclarSeqNum;
	}

	public void setDimDeclarSeqNum(long dimDeclarSeqNum) {
		this.dimDeclarSeqNum = dimDeclarSeqNum;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getHsSubDesc() {
		return hsSubDesc;
	}

	public void setHsSubDesc(String hsSubDesc) {
		this.hsSubDesc = hsSubDesc;
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

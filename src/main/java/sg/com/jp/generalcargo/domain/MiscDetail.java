package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class MiscDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String category;

	private String typeCode;

	private String typeValue;

	private String type;

	private String typeView; // for hssubfr and hssubto same
	private String typeViewDesc;

	// added to sort date
	private LocalDateTime startDate;

	private static final List<String> FIELDS = Arrays.asList("category", "typeCode", "typeValue", "type");

	public static boolean isFieldValid(String field) {
		return field == null ? false : FIELDS.contains(field);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public String getTypeView() {
		return typeView;
	}

	public void setTypeView(String typeView) {
		this.typeView = typeView;
	}

	
	public String getTypeViewDesc() {
		return typeViewDesc;
	}

	public void setTypeViewDesc(String typeViewDesc) {
		this.typeViewDesc = typeViewDesc;
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

package sg.com.jp.generalcargo.domain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UatFormValueObject {
	private UatValueObject uatValueObject;
	private List<UatValueObject> uatValueObjectList;
	private String companyName;
	private String companyCode;
	private String dateFrom;
	private String dateTo;
	private String deleteRemarks;
	private String[] selectedUat;
	private boolean result;

	public UatValueObject getUatValueObject() {
		return uatValueObject;
	}

	public void setUatValueObject(UatValueObject uatValueObject) {
		this.uatValueObject = uatValueObject;
	}

	public List<UatValueObject> getUatValueObjectList() {
		return uatValueObjectList;
	}

	public void setUatValueObjectList(List<UatValueObject> uatValueObjectList) {
		this.uatValueObjectList = uatValueObjectList;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getDeleteRemarks() {
		return deleteRemarks;
	}

	public void setDeleteRemarks(String deleteRemarks) {
		this.deleteRemarks = deleteRemarks;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setSelectedUat(String[] selectedUat) {
		this.selectedUat = selectedUat;
	}

	public String[] getSelectedUat() {
		return selectedUat;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isResult() {
		return result;
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

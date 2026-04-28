package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PassOutNoteFormValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PassOutNoteValueObject> passOutNoteValueObjectList;
	private PassOutNoteValueObject passOutNoteValueObject;
	private String[] selectedPassOutNote;
	private boolean result;

	public void setPassOutNoteValueObject(PassOutNoteValueObject passOutNoteValueObject) {
		this.passOutNoteValueObject = passOutNoteValueObject;
	}

	public PassOutNoteValueObject getPassOutNoteValueObject() {
		return passOutNoteValueObject;
	}

	public void setPassOutNoteValueObjectList(List<PassOutNoteValueObject> passOutNoteValueObjectList) {
		this.passOutNoteValueObjectList = passOutNoteValueObjectList;
	}

	public List<PassOutNoteValueObject> getPassOutNoteValueObjectList() {
		return passOutNoteValueObjectList;
	}

	public void setSelectedPassOutNote(String[] selectedPassOutNote) {
		this.selectedPassOutNote = selectedPassOutNote;
	}

	public String[] getSelectedPassOutNote() {
		return selectedPassOutNote;
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

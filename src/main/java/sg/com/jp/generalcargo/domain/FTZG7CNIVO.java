package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 7 CNI CONSIGNMENT INFORMATION
public class FTZG7CNIVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// consolidation item number
	private String cons_item_nbr;
	
	// document/message details
	private String doc_id;

	public String getCons_item_nbr() {
		return cons_item_nbr;
	}

	public void setCons_item_nbr(String cons_item_nbr) {
		this.cons_item_nbr = cons_item_nbr;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
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

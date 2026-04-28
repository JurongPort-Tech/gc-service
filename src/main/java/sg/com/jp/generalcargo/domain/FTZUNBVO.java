package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// UNB INTERCHANGE HEADER
public class FTZUNBVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// syntax identifier
	private String syntax_id;
	private String syntax_ver_nbr;
	
	// interchange sender
	private String sender_id;
	
	// interchange recipient
	private String recipient_id;
	
	// date and time of preparation
	private int date;
	private int time;
	
	// interchange control reference
	private String interchange_ctrl_ref;
	
	public String getSyntax_id() {
		return syntax_id;
	}
	public void setSyntax_id(String syntax_id) {
		this.syntax_id = syntax_id;
	}
	public String getSyntax_ver_nbr() {
		return syntax_ver_nbr;
	}
	public void setSyntax_ver_nbr(String syntax_ver_nbr) {
		this.syntax_ver_nbr = syntax_ver_nbr;
	}
	public String getSender_id() {
		return sender_id;
	}
	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}
	public String getRecipient_id() {
		return recipient_id;
	}
	public void setRecipient_id(String recipient_id) {
		this.recipient_id = recipient_id;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getInterchange_ctrl_ref() {
		return interchange_ctrl_ref;
	}
	public void setInterchange_ctrl_ref(String interchange_ctrl_ref) {
		this.interchange_ctrl_ref = interchange_ctrl_ref;
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

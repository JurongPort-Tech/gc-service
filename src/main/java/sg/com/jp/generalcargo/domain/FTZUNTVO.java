package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// UNT MESSAGE TRAILER
public class FTZUNTVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// number of segments in the message
	private int nbr_of_segments_in_msg;
	
	// message reference number
	private String msg_ref_nbr;

	public int getNbr_of_segments_in_msg() {
		return nbr_of_segments_in_msg;
	}

	public void setNbr_of_segments_in_msg(int nbr_of_segments_in_msg) {
		this.nbr_of_segments_in_msg = nbr_of_segments_in_msg;
	}

	public String getMsg_ref_nbr() {
		return msg_ref_nbr;
	}

	public void setMsg_ref_nbr(String msg_ref_nbr) {
		this.msg_ref_nbr = msg_ref_nbr;
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

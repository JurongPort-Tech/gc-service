package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// UNH MESSAGE HEADER
public class FTZUNHVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// message reference number
	private String msg_ref_nbr;
	
	// message identifier
	private String msg_type;
	private String msg_ver_nbr;
	private String msg_release_nbr;
	private String ctrl_agency;
	private String assoc_assign_cd;
	
	// common access reference
	private String common_access_ref;
	
	// status of the transfer
	private int seq_of_transfer;
	private String first_and_last_transfer;
	
	public String getMsg_ref_nbr() {
		return msg_ref_nbr;
	}
	public void setMsg_ref_nbr(String msg_ref_nbr) {
		this.msg_ref_nbr = msg_ref_nbr;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public String getMsg_ver_nbr() {
		return msg_ver_nbr;
	}
	public void setMsg_ver_nbr(String msg_ver_nbr) {
		this.msg_ver_nbr = msg_ver_nbr;
	}
	public String getMsg_release_nbr() {
		return msg_release_nbr;
	}
	public void setMsg_release_nbr(String msg_release_nbr) {
		this.msg_release_nbr = msg_release_nbr;
	}
	public String getCtrl_agency() {
		return ctrl_agency;
	}
	public void setCtrl_agency(String ctrl_agency) {
		this.ctrl_agency = ctrl_agency;
	}
	public String getAssoc_assign_cd() {
		return assoc_assign_cd;
	}
	public void setAssoc_assign_cd(String assoc_assign_cd) {
		this.assoc_assign_cd = assoc_assign_cd;
	}
	public String getCommon_access_ref() {
		return common_access_ref;
	}
	public void setCommon_access_ref(String common_access_ref) {
		this.common_access_ref = common_access_ref;
	}
	public int getSeq_of_transfer() {
		return seq_of_transfer;
	}
	public void setSeq_of_transfer(int seq_of_transfer) {
		this.seq_of_transfer = seq_of_transfer;
	}
	public String getFirst_and_last_transfer() {
		return first_and_last_transfer;
	}
	public void setFirst_and_last_transfer(String first_and_last_transfer) {
		this.first_and_last_transfer = first_and_last_transfer;
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

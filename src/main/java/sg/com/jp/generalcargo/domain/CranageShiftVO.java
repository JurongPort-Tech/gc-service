package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * System Name          : LWMS Terminal Billing System<br>
 * Component ID         : CranageShiftVO.java <br>
 * Component Description: .
 *
 * Change Revision
 * ---------------
 * Author		Request Number		Description of Change											Version     Date Released
 * ---------------------------------------------------------------------------------------------------------------------------											
 * mongkey							Modify for multi LT												1.0			Feb 2012
 * 
 */

public class CranageShiftVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String shiftNo;
	private Timestamp shiftAtb;
	
	public CranageShiftVO() {
		
	}
	
	public void setShiftNo(String shiftNo) {
		this.shiftNo = shiftNo;
	}

	public String getShiftNo() {
		return shiftNo;
	}
	
	public void setShiftAtb(Timestamp shiftAtb) {
		this.shiftAtb = shiftAtb;
	}

	public Timestamp getShiftAtb() {
		return this.shiftAtb;
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

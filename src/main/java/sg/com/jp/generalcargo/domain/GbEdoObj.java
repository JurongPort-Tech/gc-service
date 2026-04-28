package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbEdoObj implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String edoAsnNo;
	private String blNo;
	private String acctNo;
	private String cargoStatus;
        public String mftSeqNo = "";

	public GbEdoObj() {}

	public void setEdoAsnNo(String s) {
		this.edoAsnNo = s;
	}

	public String getEdoAsnNo() {
		return this.edoAsnNo;
	}

	public void setBlNo(String s) {
		this.blNo = s;
	}

	public String getBlNo() {
		return this.blNo;
	}

	public void setAcctNo(String s) {
		this.acctNo = s;
	}

	public String getAcctNo() {
		return this.acctNo;
	}

	public void setCargoStatus(String s) {
		this.cargoStatus = s;
	}

	public String getCargoStatus() {
		return this.cargoStatus;
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

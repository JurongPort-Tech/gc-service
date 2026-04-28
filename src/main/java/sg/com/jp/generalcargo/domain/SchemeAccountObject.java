package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>Title: SchemeAccountObject </p>
 * <p>Description: CLass to encapsulate scheme details like scheme code,name,account number</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SoftwareDesign & Consultancy. </p>
 * @author TVS not attributable
 * @version 1.0
 */

public class SchemeAccountObject implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String schemecode = new String();
	private String schemename = new String();
	private String accountno = new String();

	/**
	 * Public constructor.
	 */
	public SchemeAccountObject() {
	}

	/**
	 * Sets the scheme code.
	 * 
	 * @param schemecode schemecode to be set as string
	 */
	public void setSchemeCode(String schemecode) {
		this.schemecode = schemecode;
	}

	/**
	 * Sets the scheme name.
	 * 
	 * @param schemename schemename to be set as string
	 */
	public void setSchemeName(String schemename) {
		this.schemename = schemename;
	}

	/**
	 * Sets the account number.
	 * 
	 * @param accountno account number to be set as string
	 */
	public void setAccountNumber(String accountno) {
		this.accountno = accountno;
	}

	// end of set method

	/**
	 * Returns Scheme Code if already set or empty string.
	 * 
	 * @return schemecode
	 */
	public String getSchemeCode() {
		return this.schemecode;
	}

	/**
	 * Returns Scheme Name if already set or empty string.
	 * 
	 * @return schemename
	 */
	public String getSchemeName() {
		return this.schemename;
	}

	/**
	 * Returns Account Number if aleady set or empty string.
	 * 
	 * @return accountno
	 */
	public String getAccountNumber() {
		return this.accountno;
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

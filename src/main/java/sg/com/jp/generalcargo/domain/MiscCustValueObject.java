package sg.com.jp.generalcargo.domain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
*System Name: GBMS (General Bulk Cargo Management System)
*Component ID: MiscCustValueObject.java
*Component Description: Shows List of Application Types 
* 
*@author      Anandhi
*@version     1.0
*@since       01 March 2007
*/

/*Revision History
*================
* Author   Request Number  Description of Change   Version     Date Released
* ------   --------------  ---------------------   -------     -------------
* Anandhi                  Creation                1.0         08 March 2007
*
*/

public class MiscCustValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String coCd;
	private String coName;
	private String acctNbr;
	private String address1;
	private String address2;
	private String city;
	private String pin;
	private String contact1;

	/**
	 * @return Returns the acctNbr.
	 */
	public String getAcctNbr() {
		return acctNbr;
	}

	/**
	 * @param acctNbr The acctNbr to set.
	 */
	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	/**
	 * @return Returns the address1.
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 The address1 to set.
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return Returns the address2.
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 The address2 to set.
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return Returns the coCd.
	 */
	public String getCoCd() {
		return coCd;
	}

	/**
	 * @param coCd The coCd to set.
	 */
	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}

	/**
	 * @return Returns the coName.
	 */
	public String getCoName() {
		return coName;
	}

	/**
	 * @param coName The coName to set.
	 */
	public void setCoName(String coName) {
		this.coName = coName;
	}

	/**
	 * @return Returns the contact1.
	 */
	public String getContact1() {
		return contact1;
	}

	/**
	 * @param contact1 The contact1 to set.
	 */
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	/**
	 * @return Returns the pin.
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin The pin to set.
	 */
	public void setPin(String pin) {
		this.pin = pin;
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

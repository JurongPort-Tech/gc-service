package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CspsLinkValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String locType;

	private String locCd;
	
	private String locDesc;

	private String stgZone;

	private String stgName;

	/**
	 * @return Returns the locType.
	 */
	public String getLocType() {
		return locType;
	}

	/**
	 * @param locType
	 *            The locType to set.
	 */
	public void setLocType(String locType) {
		this.locType = locType;
	}

	/**
	 * @return Returns the stgZone.
	 */
	public String getStgZone() {
		return stgZone;
	}

	/**
	 * @param stgZone
	 *            The stgZone to set.
	 */
	public void setStgZone(String stgZone) {
		this.stgZone = stgZone;
	}

	/**
	 * @return Returns the stgName.
	 */
	public String getStgName() {
		return stgName;
	}

	/**
	 * @param stgName
	 *            The stgName to set.
	 */
	public void setStgName(String stgName) {
		this.stgName = stgName;
	}

	/**
	 * @return Returns the locCd.
	 */
	public String getLocCd() {
		return locCd;
	}
	
	/**
	 * @param locCd
	 *            The locCd to set.
	 */
	public void setLocCd(String locCd) {
		this.locCd = locCd;
	}

	/**
	 * @return Returns the locDesc.
	 */
	public String getLocDesc() {
		return locDesc;
	}

	/**
	 * @param locDesc
	 *            The locDesc to set.
	 */
	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
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

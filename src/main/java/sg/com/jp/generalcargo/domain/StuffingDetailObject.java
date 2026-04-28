package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>Title: StuffingDetailObject </p>
 * <p>Description:StuffingDetailObject implements TopsIObject Interface contains
 * component to encapsulate stuffing details fetched from database and to pass
 * it to and from handler to bean.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SoftwareDesign & Consultancy Pte Ltd.</p>
 * @author TVS not attributable
 * @version 1.0
 */

public class StuffingDetailObject implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String stuffingseqno = new String();
	private String stuffingdetseqno = new String();
	private String edoesnind = new String();
	private String edoesnno = new String();
	private String pkgs = new String();
	private String activestatus = new String();
	private String closedstatus = new String();

	/**
	 * public constructor
	 */
	public StuffingDetailObject() {
	}

	/**
	 * Sets the stuffing sequence number passed in as string. Also takes null.
	 * 
	 * @param stuffingseqno Master Stuffing Sequence Number
	 */
	public void setStuffingSeqNo(String stuffingseqno) {
		this.stuffingseqno = stuffingseqno;
	}

	/**
	 * Sets the stuffing detail sequence number passed in as string. Also takes
	 * null.
	 * 
	 * @param stuffingdetseqno Detail Stuffing Sequence Number
	 */
	public void setStuffingDetSeqNo(String stuffingdetseqno) {
		this.stuffingdetseqno = stuffingdetseqno;
	}

	/**
	 * Sets the ESO or ESN indicator passed in as string. Also takes null.
	 * 
	 * @param edoesnind EDO or ESN Indicator "EDO" or "ESN"
	 */
	public void setEdoEsnInd(String edoesnind) {
		this.edoesnind = edoesnind;
	}

	/**
	 * Sets the EDO or ESN number passed in as string. Also takes null.
	 * 
	 * @param edoesnno EDO or ESN Number
	 */
	public void setEdoEsnNo(String edoesnno) {
		this.edoesnno = edoesnno;
	}

	/**
	 * Sets the number of packages passed in as string. Also takes null.
	 * 
	 * @param pkgs Number of Packages
	 */
	public void setPkgs(String pkgs) {
		this.pkgs = pkgs;
	}

	/**
	 * Sets the active status of particular detail record passed in as string. Also
	 * takes null.
	 * 
	 * @param activestatus Status of the detail record.
	 */
	public void setActiveStatus(String activestatus) {
		this.activestatus = activestatus;
	}

	/**
	 * Sets the closed status of related master record passed in as string. Also
	 * takes null.
	 * 
	 * @param closedstatus Status of the master record.
	 */
	public void setClosedStatus(String closedstatus) {
		this.closedstatus = closedstatus;
	}

	// end of set methods
	/**
	 * Returns Stuffing Sequence Number.
	 * 
	 * @return Master Stuffing Sequence Number if already set or empty string.
	 */
	public String getStuffingSeqNo() {
		return this.stuffingseqno;
	}

	/**
	 * Returns Stuffing Detail Sequence Number.
	 * 
	 * @return Stuffing Detail Sequence Number if already set or else empty string.
	 */
	public String getStuffingDetSeqNo() {
		return this.stuffingdetseqno;
	}

	/**
	 * Returns EDO or ESN indicator.
	 * 
	 * @return EDO or ESN indicator if already set or else empty string.
	 */
	public String getEdoEsnInd() {
		return this.edoesnind;
	}

	/**
	 * Returns EDO or ESN number.
	 * 
	 * @return EDO or ESN number if already set or else empty string.
	 */
	public String getEdoEsnNo() {
		return this.edoesnno;
	}

	/**
	 * Returns number of packages
	 * 
	 * @return number of packages if already set or else empty string.
	 */
	public String getPkgs() {
		return this.pkgs;
	}

	/**
	 * Returns status of the detail record.
	 * 
	 * @return if already set or else empty string.
	 */
	public String getActiveStatus() {
		return this.activestatus;
	}

	/**
	 * Returns closed status of the related master record.
	 * 
	 * @return if already set or else empty string.
	 */
	public String getClosedStatus() {
		return this.closedstatus;
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

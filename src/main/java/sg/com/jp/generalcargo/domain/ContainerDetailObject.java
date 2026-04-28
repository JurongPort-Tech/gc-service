package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * <p>Title: ContainerDetailObject </p>
 * <p>Description:ContainerDetailObject implements TopsIObject Interface contains
 * component to encapsulate container details fetched from database and to pass
 * it to and from handler and bean.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SoftwareDesign & Consultancy Pte Ltd.</p>
 * @author TVS not attributable
 * @version 1.0
 *
  * Vinayak               wrtten 4 methods to check esn numbers                1.2        05 Jan 2004
 *                        entered have stuff indicator.
 */
public class ContainerDetailObject implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cntrno = new String();
	private String cntrsize = new String();
	private String cntrseqno = new String();
	private String stuffingdttm = new String();
	private String stuffingclosed = new String();
	private String varno = new String();
	private String seqno = new String();
	private String waivecharge = new String();
	private String billaccountno = new String();
	private String strEsnNbr = new String();
	private String strDmEsn = new String();

	/**
	 * public constructor.
	 */
	public ContainerDetailObject() {
	}

	/**
	 * Sets the Container Number
	 * 
	 * @param cntrno Container Number
	 */
	public void setContainerNo(String cntrno) {
		this.cntrno = cntrno;
	}

	/**
	 * Sets the Container Size.
	 * 
	 * @param cntrsize Container Size.
	 */
	public void setContainerSize(String cntrsize) {
		this.cntrsize = cntrsize;
	}

	/**
	 * Sets Container Sequence Number.
	 * 
	 * @param cntrseqno Container Sequence Number
	 */
	public void setContainerSeqNo(String cntrseqno) {
		this.cntrseqno = cntrseqno;
	}

	/**
	 * Sets Stuffing Date and Time
	 * 
	 * @param stuffingdttm Stuffing date and time.
	 */
	public void setStuffingDttm(String stuffingdttm) {
		this.stuffingdttm = stuffingdttm;
	}

	/**
	 * Sets the Stuffing Closed Indiactor.
	 * 
	 * @param stuffingclosed Stuffing Closed Indicator. "Closed" if closed and "Not
	 *                       Closed" of open.
	 */
	public void setStuffingClosed(String stuffingclosed) {
		this.stuffingclosed = stuffingclosed;
	}

	/**
	 * Sets the vvcode.
	 * 
	 * @param varno Var Nbr
	 */
	public void setVarNo(String varno) {
		this.varno = varno;
	}

	/**
	 * sets the Master Stuffing Seq Number.
	 * 
	 * @param seqno Master Stuffing Seq Number.
	 */
	public void setSeqNo(String seqno) {
		this.seqno = seqno;
	}

	/**
	 * Sets waive charge indicator
	 * 
	 * @param waivecharge "Y" if waived and "N" if not waived.
	 */
	public void setWaiveCharge(String waivecharge) {
		this.waivecharge = waivecharge;
	}

	/**
	 * Sets the Bill Party Account Number.
	 * 
	 * @param accountno billable party account number
	 */
	public void setBillAccountNumber(String accountno) {
		this.billaccountno = accountno;
	}

	// end of set methods

	/**
	 * Returns Container No if already set or empty string.
	 * 
	 * @return Container Number.
	 */
	public String getContainerNo() {
		return this.cntrno;
	}

	/**
	 * Returns Container Size if already set or empty string.
	 * 
	 * @return Container Size.
	 */
	public String getContainerSize() {
		return this.cntrsize;
	}

	/**
	 * Returns Container Seq No if already set or empty string.
	 * 
	 * @return Container Sequence Number.
	 */
	public String getContainerSeqNo() {
		return this.cntrseqno;
	}

	/**
	 * Returns Stuffing date and Time if already set or empty string.
	 * 
	 * @return Stuffing date and Time
	 */
	public String getStuffingDttm() {
		return this.stuffingdttm;
	}

	/**
	 * Returns Stuffing Closed Indicator if already set or empty string.
	 * 
	 * @return Stuffing Closed Indicator "Closed" if Closed else "Not Closed"
	 */
	public String getStuffingClosed() {
		return this.stuffingclosed;
	}

	/**
	 * Returns VV code if already set or empty string.
	 * 
	 * @return vvcode.
	 */
	public String getVarNo() {
		return this.varno;
	}

	/**
	 * Returns Stuffing Master Seq No if already set or empty string.
	 * 
	 * @return Stuffing Master Sequence Number.
	 */
	public String getSeqNo() {
		return this.seqno;
	}

	/**
	 * Returns Waive Charge Indicator if already set or empty string.
	 * 
	 * @return Waive Charge Indicator "Waived" if waived and "Not Waived" if not
	 *         waived.
	 */
	public String getWaiveCharge() {
		return this.waivecharge;
	}

	/**
	 * Returns Billable Party Account No if already set or empty string.
	 * 
	 * @return Billable Party Account Number.
	 */
	public String getBillAccountNumber() {
		return this.billaccountno;
	}

// vinayak added on 2 jan 2004
	public void setEsnNbr(String esnno) {
		this.strEsnNbr = esnno;
	}

	public String getEsnNbr() {
		return strEsnNbr;
	}

	public void setDummyEsn(String dmesn) {
		this.strDmEsn = dmesn;
	}

	public String getDummyEsn() {
		return strDmEsn;
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


package sg.com.jp.generalcargo.domain;

/**
 * <p>Title: uiServlet.gbms.ops.vesselproductivity.ViewProductivityListServlet</p>
 *
 * <p>Description: This class is for redirecting to jsp 
 * .</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:harbortek .</p>
 *
 * @author Ding Xijia
 * @version 1.0
 */
/*
 * Revision History
 * ----------------
 * Author   Request Number  		Description of Change                  Version     Date Released
 * Jade		CR-CAB-20161006-002												1.1			15/11/2016
 */

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloseLctValueObject implements TopsIObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vvCd) {
		vv_cd = vvCd;
	}

	public String getVessel_Name() {
		return vessel_Name;
	}

	public void setVessel_Name(String vesselName) {
		vessel_Name = vesselName;
	}

	public String getIn_voyage_No() {
		return in_voyage_No;
	}

	public void setIn_voyage_No(String inVoyageNo) {
		in_voyage_No = inVoyageNo;
	}

	public String getOut_voyage_No() {
		return out_voyage_No;
	}

	public void setOut_voyage_No(String outVoyageNo) {
		out_voyage_No = outVoyageNo;
	}

	public String getClose_bj() {
		return close_bj;
	}

	public void setClose_bj(String closeBj) {
		close_bj = closeBj;
	}

	public String getClose_shipment() {
		return close_shipment;
	}

	public void setClose_shipment(String closeShipment) {
		close_shipment = closeShipment;
	}

	public String getClose_lct() {
		return close_lct;
	}

	public void setClose_lct(String closeLct) {
		close_lct = closeLct;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getAtb() {
		return atb;
	}

	public void setAtb(String atb) {
		this.atb = atb;
	}

	public String getAtu() {
		return atu;
	}

	public void setAtu(String atu) {
		this.atu = atu;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClose_at() {
		return close_at;
	}

	public void setClose_at(String closeAt) {
		close_at = closeAt;
	}

	public String getClose_by() {
		return close_by;
	}

	public void setClose_by(String closeBy) {
		close_by = closeBy;
	}

	public void setUaNbrs(List<String> uaNbrs) {
		this.uaNbrs = uaNbrs;
	}

	public List<String> getUaNbrs() {
		return uaNbrs;
	}

	public void setDnNbrs(List<String> dnNbrs) {
		this.dnNbrs = dnNbrs;
	}

	public List<String> getDnNbrs() {
		return dnNbrs;
	}

	private String vv_cd;
	private String vessel_Name;
	private String in_voyage_No;
	private String out_voyage_No;
	private String close_bj;
	private String close_shipment;
	private String close_lct;
	private String scheme;
	private String atb;
	private String atu;
	private String status;
	private String close_at;
	private String close_by;

	private List<String> uaNbrs;
	private List<String> dnNbrs;

	// Added by Jade for CR-CAB-20161006-002
	private boolean hasDisCrgCntr;
	private boolean hasLdCrgCntr;

	public boolean getHasDisCrgCntr() {
		return hasDisCrgCntr;
	}

	public void setHasDisCrgCntr(boolean hasDisCrgCntr) {
		this.hasDisCrgCntr = hasDisCrgCntr;
	}

	public boolean getHasLdCrgCntr() {
		return hasLdCrgCntr;
	}

	public void setHasLdCrgCntr(boolean hasLdCrgCntr) {
		this.hasLdCrgCntr = hasLdCrgCntr;
	}
	// CR-CAB-20161006-002

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

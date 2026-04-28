package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VesselVoyValueObject implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public VesselVoyValueObject() {
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public void setVslName(String s) {
		vslName = s;
	}

	public String getVslName() {
		return vslName;
	}

	public void setVoyNo(String s) {
		voyNo = s;
	}

	public String getVoyNo() {
		return voyNo;
	}

	public void setVarNbr(String s) {
		varNbr = s;
	}

	public String getVarNbr() {
		return varNbr;
	}

	/**
	 * @return the arrival
	 */
	public String getArrival() {
		return arrival;
	}

	/**
	 * @param arrival the arrival to set
	 */
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	/**
	 * @return the departural
	 */
	public String getDepartural() {
		return departural;
	}

	/**
	 * @param departural the departural to set
	 */
	public void setDepartural(String departural) {
		this.departural = departural;
	}

	/**
	 * @return the atb_dttm
	 */
	public String getAtb_dttm() {
		return atb_dttm;
	}

	/**
	 * @return the atu_dttm
	 */
	public String getAtu_dttm() {
		return atu_dttm;
	}

	/**
	 * @param atb_dttm the atb_dttm to set
	 */
	public void setAtb_dttm(String atb_dttm) {
		this.atb_dttm = atb_dttm;
	}

	/**
	 * @param atu_dttm the atu_dttm to set
	 */
	public void setAtu_dttm(String atu_dttm) {
		this.atu_dttm = atu_dttm;
	}

	/**
	 * @return the cod_dttm
	 */
	public String getCod_dttm() {
		return cod_dttm;
	}

	/**
	 * @return the etb_dttm
	 */
	public String getEtb_dttm() {
		return etb_dttm;
	}

	/**
	 * @param cod_dttm the cod_dttm to set
	 */
	public void setCod_dttm(String cod_dttm) {
		this.cod_dttm = cod_dttm;
	}

	/**
	 * @param etb_dttm the etb_dttm to set
	 */
	public void setEtb_dttm(String etb_dttm) {
		this.etb_dttm = etb_dttm;
	}

	/**
	 * @return the col_dttm
	 */
	public String getCol_dttm() {
		return col_dttm;
	}

	/**
	 * @param col_dttm the col_dttm to set
	 */
	public void setCol_dttm(String col_dttm) {
		this.col_dttm = col_dttm;
	}

	/**
	 * @return the vv_status_ind
	 */
	public String getVv_status_ind() {
		return vv_status_ind;
	}

	/**
	 * @param vv_status_ind the vv_status_ind to set
	 */
	public void setVv_status_ind(String vv_status_ind) {
		this.vv_status_ind = vv_status_ind;
	}

	public String getIndicationOfArrival() {
		return indicationOfArrival;
	}

	public void setIndicationOfArrival(String indicationOfArrival) {
		this.indicationOfArrival = indicationOfArrival;
	}

	public String getIndicationOfDeparture() {
		return indicationOfDeparture;
	}

	public void setIndicationOfDeparture(String indicationOfDeparture) {
		this.indicationOfDeparture = indicationOfDeparture;
	}

	public String getBerthNo() {
		return berthNo;
	}

	public void setBerthNo(String berthNo) {
		this.berthNo = berthNo;
	}

	public String getVesselType() {
		return vesselType;
	}

	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
	}

	public String getInVoyNo() {
		return inVoyNo;
	}

	public void setInVoyNo(String inVoyNo) {
		this.inVoyNo = inVoyNo;
	}

	public String getOutVoyNo() {
		return outVoyNo;
	}

	public void setOutVoyNo(String outVoyNo) {
		this.outVoyNo = outVoyNo;
	}
	
	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getVsl_nm() {
		return vsl_nm;
	}

	public void setVsl_nm(String vsl_nm) {
		this.vsl_nm = vsl_nm;
	}

	public String getIn_voy_nbr() {
		return in_voy_nbr;
	}

	public void setIn_voy_nbr(String in_voy_nbr) {
		this.in_voy_nbr = in_voy_nbr;
	}
	
	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	   
    public String getVslVoy() {
            return vslVoy;
    }
    public void setVslVoy(String vslVoy) {
            this.vslVoy = vslVoy;
    }
	
	String terminal;
	String vslName;
	String voyNo;
	String varNbr;
	String arrival;
	String departural;
	String atb_dttm;
	String atu_dttm;
	String cod_dttm;
	String etb_dttm;
	String col_dttm;
	String vv_status_ind;
	// Added By NS on 18-08-20
	String indicationOfArrival;
	String indicationOfDeparture;
	String berthNo;
	String vesselType;

	String  vv_cd;
	String vsl_nm;
	String in_voy_nbr;
// Added By NS for Cargo Doc upload
	String inVoyNo;
	String outVoyNo;
	String agent;
	String departure;
	String scheme;
	String vslVoy;
    
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *System Name: GBMS (General Bulk Cargo Management System)
*Component ID: CargoSummaryValueObject.java
*Component Description: Stores CargoSummary Value Objects
* 
*@author      Balaji R.k.
*@version     8 February 2002
 */

/*
 Revision History
================
* Author   Request Number  Description of Change   Version     Date Released
* Balaji                      Creation                1.0         8 February 2002
 */

public class CargoSummaryValueObject implements TopsIObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.String terminal;
	private java.lang.String var_nbr;
	private java.lang.String vsl_nm;
	private java.lang.String in_voy_nbr;
	private java.lang.String etb_dttm;
	private java.lang.String etu_dttm;
	private java.lang.String mft_nbr_pkgs;
	private java.lang.String edo_nbr_pkgs;

	public CargoSummaryValueObject(){
	}
	
	public java.lang.String getTerminal() {
		return terminal;
	}

	public void setTerminal(java.lang.String terminal) {
		this.terminal = terminal;
	}

	public java.lang.String getVarNbr(){
		return var_nbr; 
	}
	public void setVarNbr(java.lang.String varNbr){
		var_nbr=varNbr; 
	}
	public java.lang.String getVslNm(){
		return vsl_nm; 
	}
	public void setVslNm(java.lang.String vslNm){
		vsl_nm=vslNm; 
	}
	public java.lang.String getInVoyNbr(){
		return in_voy_nbr; 
	}
	public void setInVoyNbr(java.lang.String inVoyNbr){
		in_voy_nbr=inVoyNbr; 
	}
	public java.lang.String getEtbDttm(){
		return etb_dttm; 
	}
	public void setEtbDttm(java.lang.String etbDttm){
		etb_dttm=etbDttm; 
	}
	public java.lang.String getEtuDttm(){
		return etu_dttm; 
	}
	public void setEtuDttm(java.lang.String etuDttm){
		etu_dttm=etuDttm; 
	}
	public java.lang.String getMftNbrPkgs(){
		return mft_nbr_pkgs; 
	}
	public void setMftNbrPkgs(java.lang.String mftnbrPkgs){
		mft_nbr_pkgs=mftnbrPkgs; 
	}
	public java.lang.String getEdoNbrPkgs(){
		return edo_nbr_pkgs;
	}
	public void setEdoNbrPkgs(java.lang.String edoNbrPkgs){
		edo_nbr_pkgs=edoNbrPkgs; 
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

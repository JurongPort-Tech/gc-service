package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CutoffValueObject  implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.String terminal ;
	private java.lang.String scheme;
	private java.lang.String subScheme;
	private java.lang.String gcOperations;
	
	//private String bean_name;
	private java.lang.String edo_asn_nbr;
	private java.lang.String cut_off_nbr;
	private java.lang.String cut_off_type;
	private java.lang.String cut_off_qty;
	private java.lang.String bl_nbr;
	private java.lang.String total_pkgs;
	
	// Change by Liu Foong on 14 Feb 2005 : To include mft_seq_nbr
	private java.lang.String mft_seq_nbr;
	// End Changes
	
	private java.lang.String crgdesc;
	private java.lang.String hscode;
	private java.lang.String pkgtype;
	private java.lang.String consignee;
	private java.lang.String crgmarking;
	private java.lang.String crgtype;

	public java.lang.String getTerminal() {
		return terminal;
	}

	public void setTerminal(java.lang.String terminal) {
		this.terminal = terminal;
	}

	public java.lang.String getScheme() {
		return scheme;
	}

	public void setScheme(java.lang.String scheme) {
		this.scheme = scheme;
	}

	public java.lang.String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(java.lang.String subScheme) {
		this.subScheme = subScheme;
	}

	public java.lang.String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(java.lang.String gcOperations) {
		this.gcOperations = gcOperations;
	}

	public java.lang.String getCargoType(){
		return crgtype;
	}

	public void setCargoType(java.lang.String s){
		crgtype=s; 
	}

	public java.lang.String getCargoMarking(){
		return crgmarking;
	}

	public void setCargoMarking(java.lang.String s){
		crgmarking=s; 
	}

	
	public java.lang.String getConsignee(){
		return consignee;
	}

	public void setConsignee(java.lang.String s){
		consignee=s; 
	}

	public java.lang.String getPkgType(){
		return pkgtype;
	}

	public void setPkgType(java.lang.String s){
		pkgtype=s; 
	}

	
	public java.lang.String getHsCode(){
		return hscode;
	}

	public void setHsCode(java.lang.String s){
		hscode=s; 
	}

	public java.lang.String getCargoDesc(){
		return crgdesc;
	}

	public void setCargoDesc(java.lang.String s){
		crgdesc=s; 
	}


	public java.lang.String getEdoAsnNbr(){
		return edo_asn_nbr;
	}

	public void setEdoAsnNbr(java.lang.String s){
		edo_asn_nbr=s; 
	}
	
	public java.lang.String getCutoffNbr(){
		return cut_off_nbr; 
	}

	public void setCutoffNbr(java.lang.String s){
		cut_off_nbr=s; 
	}

	public java.lang.String getCutoffType(){
		return cut_off_type; 
	}

	public void setCutoffType(java.lang.String s){
		cut_off_type=s; 
	}

	public java.lang.String getCutoffQty(){
		return cut_off_qty; 
	}

	public void setCutoffQty(java.lang.String s){
		cut_off_qty=s; 
	}

	public java.lang.String getBlNbr(){
		return bl_nbr; 
	}

	public void setBlNbr(java.lang.String s){
		bl_nbr=s; 
	}

	public java.lang.String getTotalPkgs(){
		return total_pkgs; 
	}

	public void setTotalPkgs(java.lang.String s){
		total_pkgs=s; 
	}

	//SL-GBMS-20050214-1:  Change by Liu Foong on 14 Feb 2005 : To include mft_seq_nbr
	public java.lang.String getMftSeqNbr(){
		return mft_seq_nbr; 
	}

	public void setMftSeqNbr(java.lang.String s){
		mft_seq_nbr=s; 
	}
	// End Changes

	
	public CutoffValueObject(){
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

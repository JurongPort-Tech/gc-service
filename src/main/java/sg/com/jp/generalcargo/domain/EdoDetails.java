package sg.com.jp.generalcargo.domain;

public class EdoDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String blnbr;
	private String asnNbr;
	private String edoPkg;
	private String nominatedWt;
	private String nominatedVol;
	private String deliveryToEPC;
	private String distype;	
	
	public String getDistype() {
		return distype;
	}
	public void setDistype(String distype) {
		this.distype = distype;
	}
	public String getDeliveryToEPC() {
		return deliveryToEPC;
	}
	public void setDeliveryToEPC(String deliveryToEPC) {
		this.deliveryToEPC = deliveryToEPC;
	}
	public String getBlnbr() {
		return blnbr;
	}
	public void setBlnbr(String blnbr) {
		this.blnbr = blnbr;
	}
	public String getAsnNbr() {
		return asnNbr;
	}
	public void setAsnNbr(String asnNbr) {
		this.asnNbr = asnNbr;
	}
	public String getEdoPkg() {
		return edoPkg;
	}
	public void setEdoPkg(String edoPkg) {
		this.edoPkg = edoPkg;
	}
	public String getNominatedWt() {
		return nominatedWt;
	}
	public void setNominatedWt(String nominatedWt) {
		this.nominatedWt = nominatedWt;
	}
	public String getNominatedVol() {
		return nominatedVol;
	}
	public void setNominatedVol(String nominatedVol) {
		this.nominatedVol = nominatedVol;
	}

}

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BookRefvoyageOutwardValueObject.java
 *

 */

public class BookRefvoyageOutwardValueObject   {

	private String vvCd;
	private String outVoyNbr;
	private String vslNm;
	private String vslFullNm;
	private String terminal;

	/** Creates new BookRefvoyageOutwardValueObject */
	public BookRefvoyageOutwardValueObject() {
//      LogManager.instance.logInfo("BookRefvoyageOutwardValueObject");    	
	}
	
	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getOutVoyNbr() {
		return outVoyNbr;
	}

	public void setOutVoyNbr(String outVoyNbr) {
		this.outVoyNbr = outVoyNbr;
	}

	public String getVslNm() {
		return vslNm;
	}

	public void setVslNm(String vslNm) {
		this.vslNm = vslNm;
	}

	public String getVslFullNm() {
		return vslFullNm;
	}

	public void setVslFullNm(String vslFullNm) {
		this.vslFullNm = vslFullNm;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
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

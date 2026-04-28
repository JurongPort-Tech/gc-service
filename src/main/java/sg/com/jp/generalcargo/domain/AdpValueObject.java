package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * System Name : GBMS (General and Bulk Cargo Management Systems) Component ID :
 * ediValueObject.java Component Description: This is the valueObject used for
 * viewing of the EDI message for TESN-PSA-JP.
 *
 * @author Rajesh
 * @version 01 June 2002
 *
 *          Change Revision --------------- Author Request Number Description of
 *          Change Version Date Released Rajesh - Creation 1.1 01 June 2002
 */
public class AdpValueObject {
	// Multi Adp
	public static final int MAX_ADP_TRUCKER = 5;

	private String adpCustCd;
	private String adpIcTdbcrNbr;
	private String adpNm;
	private String adpNbrPkgs;
	private String adpContact;

	public AdpValueObject() {
	}

	public String getAdpCustCd() {
		return adpCustCd;
	}

	public void setAdpCustCd(String adpCustCd) {
		this.adpCustCd = adpCustCd;
	}

	public String getAdpIcTdbcrNbr() {
		return adpIcTdbcrNbr;
	}

	public void setAdpIcTdbcrNbr(String adpIcTdbcrNbr) {
		this.adpIcTdbcrNbr = adpIcTdbcrNbr;
	}

	public String getAdpNm() {
		return adpNm;
	}

	public void setAdpNm(String adpNm) {
		this.adpNm = adpNm;
	}

	public String getAdpNbrPkgs() {
		return adpNbrPkgs;
	}

	public void setAdpNbrPkgs(String adpNbrPkgs) {
		this.adpNbrPkgs = adpNbrPkgs;
	}

	/**
	 * @return the adpContact
	 */
	public String getAdpContact() {
		return adpContact;
	}

	/**
	 * @param adpContact the adpContact to set
	 */
	public void setAdpContact(String adpContact) {
		this.adpContact = adpContact;
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

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * System Name          : GBMS (General and Bulk Cargo Management Systems)
 * Component ID         : TesnSearchValueObject.java 
 * Component Description: This is the valueObject used for searching of TESN.
 *
 * @author      Thiru
 * @version     01 June 2002
 *
 * Change Revision
 * ---------------
 * Author     Request Number  Description of Change   Version     Date Released
 * Thiru             -        Creation                  1.1       01 June 2002
 */

public class TesnSearchValueObject {
    private String inVesName;
    private String inVoyNo;
    private String outVesName;
    private String outVoyNo;
    private String nbrPkgs;
    private String crgDes;
    private String esnAsnNbr;
    private String edoAsnNbr;

    public TesnSearchValueObject() {
    }

    public void setInVesName(String inVesName) {
        this.inVesName = inVesName;
    }

    public void setInVoyNo(String inVoyNo) {
        this.inVoyNo = inVoyNo;
    }

    public void setOutVesName(String outVesName) {
        this.outVesName = outVesName;
    }

    public void setOutVoyNo(String outVoyNo) {
        this.outVoyNo = outVoyNo;
    }

    public void setNbrPkgs(String nbrPkgs) {
        this.nbrPkgs = nbrPkgs;
    }

    public void setCrgDes(String crgDes) {
        this.crgDes = crgDes;
    }

    public void setEsnAsnNbr(String esnAsnNbr) {
        this.esnAsnNbr = esnAsnNbr;
    }

    public void setEdoAsnNbr(String edoAsnNbr) {
        this.edoAsnNbr = edoAsnNbr;
    }

    public String getInVesName() {
        return inVesName;
    }

    public String getInVoyNo() {
        return inVoyNo;
    }

    public String getOutVesName() {
        return outVesName;
    }

    public String getOutVoyNo() {
        return outVoyNo;
    }

    public String getNbrPkgs() {
        return nbrPkgs;
    }

    public String getCrgDes() {
        return crgDes;
    }

    public String getEsnAsnNbr() {
        return esnAsnNbr;
    }

    public String getEdoAsnNbr() {
        return edoAsnNbr;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccRulePara implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ruleParaCd;
    private String ruleNbr;
    private String ruleCatCd;
    private String ruleParaDesc;
    private String ruleParaValue;
    private String ruleParaUnit;
    private String lastModifyUserId;
    private Date lastModifyDttm;

    public GbccRulePara() {
    }

    public GbccRulePara(String ruleParaCd, String ruleNbr, String ruleCatCd, String ruleParaDesc, String ruleParaValue, String ruleParaUnit, String lastModifyUserId, Date lastModifyDttm) {
        this.ruleParaCd = ruleParaCd;
        this.ruleNbr = ruleNbr;
        this.ruleCatCd = ruleCatCd;
        this.ruleParaDesc = ruleParaDesc;
        this.ruleParaValue = ruleParaValue;
        this.ruleParaUnit = ruleParaUnit;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public String getRuleParaCd() {
        return ruleParaCd;
    }

    public void setRuleParaCd(String ruleParaCd) {
        this.ruleParaCd = ruleParaCd;
    }

    public String getRuleNbr() {
        return ruleNbr;
    }

    public void setRuleNbr(String ruleNbr) {
        this.ruleNbr = ruleNbr;
    }

    public String getRuleCatCd() {
        return ruleCatCd;
    }

    public void setRuleCatCd(String ruleCatCd) {
        this.ruleCatCd = ruleCatCd;
    }

    public String getRuleParaDesc() {
        return ruleParaDesc;
    }

    public void setRuleParaDesc(String ruleParaDesc) {
        this.ruleParaDesc = ruleParaDesc;
    }

    public String getRuleParaValue() {
        return ruleParaValue;
    }

    public void setRuleParaValue(String ruleParaValue) {
        this.ruleParaValue = ruleParaValue;
    }

    public String getRuleParaUnit() {
        return ruleParaUnit;
    }

    public void setRuleParaUnit(String ruleParaUnit) {
        this.ruleParaUnit = ruleParaUnit;
    }

    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    public void setLastModifyUserId(String lastModifyUserId) {
        this.lastModifyUserId = lastModifyUserId;
    }

    public Date getLastModifyDttm() {
        return lastModifyDttm;
    }

    public void setLastModifyDttm(Date lastModifyDttm) {
        this.lastModifyDttm = lastModifyDttm;
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

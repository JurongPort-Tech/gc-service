package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EdoJpBilling {
	
	private String strAdpNbr;
	private String strCustCd;
    private String strVslCd;
    private String acctNbr;
    private String coNm;
    private String strjpbnbr;
    private String strAdp;
    private String strShippingAgent;
    private String fullNm;
    
	public String getStrAdpNbr() {
		return strAdpNbr;
	}
	public void setStrAdpNbr(String strAdpNbr) {
		this.strAdpNbr = strAdpNbr;
	}
	public String getStrCustCd() {
		return strCustCd;
	}
	public void setStrCustCd(String strCustCd) {
		this.strCustCd = strCustCd;
	}
	public String getStrvslcd() {
		return strVslCd;
	}
	public void setStrvslcd(String strVslCd) {
		this.strVslCd = strVslCd;
	}
	public String getAcctNbr() {
		return acctNbr;
	}
	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}
	public String getCoNm() {
		return coNm;
	}
	public void setCoNm(String coNm) {
		this.coNm = coNm;
	}
    

	public String getStrjpbnbr() {
		return strjpbnbr;
	}
	public void setStrjpbnbr(String strjpbnbr) {
		this.strjpbnbr = strjpbnbr;
	}
	
	public String getStrAdp() {
		return strAdp;
	}
	public void setStrAdp(String strAdp) {
		this.strAdp = strAdp;
	}
	
	
	public String getStrShippingAgent() {
		return strShippingAgent;
	}
	public void setStrShippingAgent(String strShippingAgent) {
		this.strShippingAgent = strShippingAgent;
	}
	@Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
	public String getFullNm() {
		return fullNm;
	}
	public void setFullNm(String fullNm) {
		this.fullNm = fullNm;
	}
	

}

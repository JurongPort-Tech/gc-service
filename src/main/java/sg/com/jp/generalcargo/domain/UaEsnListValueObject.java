package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UaEsnListValueObject implements TopsIObject {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UaEsnListValueObject()
	    {
	    	terminal = null;
	    	scheme = null;
	    	subScheme = null;
	    	gcOperations = null;
	        esn_asn_nbr = null;
	        bk_ref_nbr = null;
	        nbr_pkgs = null;
	        ua_nbr_pkgs = null;
	        trans_type = null;
	        esn_status = null;
	        balance = null;
	    }

	    public String getTerminal() {
			return terminal;
		}

		public void setTerminal(String terminal) {
			this.terminal = terminal;
		}

		public String getScheme() {
			return scheme;
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}

		public String getSubScheme() {
			return subScheme;
		}

		public void setSubScheme(String subScheme) {
			this.subScheme = subScheme;
		}

		public String getGcOperations() {
			return gcOperations;
		}

		public void setGcOperations(String gcOperations) {
			this.gcOperations = gcOperations;
		}

		public void setEsn_asn_nbr(String s)
	    {
	        esn_asn_nbr = s;
	    }

	    public String getEsn_asn_nbr()
	    {
	        return esn_asn_nbr;
	    }

	    public void setBk_ref_nbr(String s)
	    {
	        bk_ref_nbr = s;
	    }

	    public String getBk_ref_nbr()
	    {
	        return bk_ref_nbr;
	    }

	    public void setNbr_pkgs(String s)
	    {
	        nbr_pkgs = s;
	    }

	    public String getNbr_pkgs()
	    {
	        return nbr_pkgs;
	    }

	    public void setUa_nbr_pkgs(String s)
	    {
	        ua_nbr_pkgs = s;
	    }

	    public String getUa_nbr_pkgs()
	    {
	        return ua_nbr_pkgs;
	    }

	    public void setBalance(String s)
	    {
	        balance = s;
	    }

	    public String getBalance()
	    {
	        return balance;
	    }

	    public void setTrans_type(String s)
	    {
	        trans_type = s;
	    }

	    public String getTrans_type()
	    {
	        return trans_type;
	    }

	    public void setEsn_status(String s)
	    {
	        esn_status = s;
	    }

	    public String getEsn_status()
	    {
	        return esn_status;
	    }
	    public String getEdo_asn_nbr() {
	        return edo_asn_nbr;
	    }

	    public void setEdo_asn_nbr(String edo_asn_nbr) {
	        this.edo_asn_nbr = edo_asn_nbr;
	    }
	    public String getMvt() {
	        return mvt;
	    }
	    public void setMvt(String mvt) {
	        this.mvt = mvt;
	    }
	    String terminal;
	    String scheme;
	    String subScheme;
	    String gcOperations;
	    String esn_asn_nbr;
	    String bk_ref_nbr;
	    String nbr_pkgs;
	    String ua_nbr_pkgs;
	    String trans_type;
	    String esn_status;
	    String balance;
	    String edo_asn_nbr;
	    String mvt;
	    
		@Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return "";
			}
		}
}

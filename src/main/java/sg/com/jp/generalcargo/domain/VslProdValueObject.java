package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VslProdValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String version_nbr = "";
	private String eff_start_Dt = "";
	private String eff_end_Dt = "";
	private String rate = "";
	private String grace_prd_hrs = "";
	private String grace_prd_pct = "";
	private String grace_prd_max = "";

	public String getEff_end_Dt() {
		return eff_end_Dt;
	}

	public void setEff_end_Dt(String eff_end_Dt) {
		this.eff_end_Dt = eff_end_Dt;
	}

	public String getEff_start_Dt() {
		return eff_start_Dt;
	}

	public void setEff_start_Dt(String eff_start_Dt) {
		this.eff_start_Dt = eff_start_Dt;
	}

	public String getGrace_prd_hrs() {
		return grace_prd_hrs;
	}

	public void setGrace_prd_hrs(String grace_prd_hrs) {
		this.grace_prd_hrs = grace_prd_hrs;
	}

	public String getGrace_prd_max() {
		return grace_prd_max;
	}

	public void setGrace_prd_max(String grace_prd_max) {
		this.grace_prd_max = grace_prd_max;
	}

	public String getGrace_prd_pct() {
		return grace_prd_pct;
	}

	public void setGrace_prd_pct(String grace_prd_pct) {
		this.grace_prd_pct = grace_prd_pct;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getVersion_nbr() {
		return version_nbr;
	}

	public void setVersion_nbr(String version_nbr) {
		this.version_nbr = version_nbr;
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

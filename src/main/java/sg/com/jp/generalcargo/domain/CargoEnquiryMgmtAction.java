package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CargoEnquiryMgmtAction extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long edo_nbr;
	private Long esn_nbr;
	private String terminal;
	private String scheme;
	private String vv_cd;	
	private String vsl_nm;
	private String in_voy_nbr;
	private String out_voy_nbr;
	private String type;
	private String crg_status;
	private String shipping_agent;
	private String last_modify_user_id;
	private Date last_modify_dttm;
	private String crg_type;
	
	private Long edo_pkgs;
	private Long deliverd_pkgs;
	private Long balance_pkgs;
	
	private Long esn_pkgs;
	private Long esn_rcvd;
	private Long esn_load;
	private String company_id;
	private String main_adp_trk;
	
	private String disc_vsl;
	private String load_vsl;
	
	public Long getEdo_nbr() {
		return edo_nbr;
	}

	public void setEdo_nbr(Long edo_nbr) {
		this.edo_nbr = edo_nbr;
	}

	public Long getEsn_nbr() {
		return esn_nbr;
	}

	public void setEsn_nbr(Long esn_nbr) {
		this.esn_nbr = esn_nbr;
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

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getVsl_nm() {
		return vsl_nm;
	}

	public void setVsl_nm(String vsl_nm) {
		this.vsl_nm = vsl_nm;
	}

	public String getIn_voy_nbr() {
		return in_voy_nbr;
	}

	public void setIn_voy_nbr(String in_voy_nbr) {
		this.in_voy_nbr = in_voy_nbr;
	}

	public String getOut_voy_nbr() {
		return out_voy_nbr;
	}

	public void setOut_voy_nbr(String out_voy_nbr) {
		this.out_voy_nbr = out_voy_nbr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCrg_status() {
		return crg_status;
	}

	public void setCrg_status(String crg_status) {
		this.crg_status = crg_status;
	}

	public String getShipping_agent() {
		return shipping_agent;
	}

	public void setShipping_agent(String shipping_agent) {
		this.shipping_agent = shipping_agent;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public Date getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(Date last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	public String getCrg_type() {
		return crg_type;
	}

	public void setCrg_type(String crg_type) {
		this.crg_type = crg_type;
	}

	public Long getEdo_pkgs() {
		return edo_pkgs;
	}

	public void setEdo_pkgs(Long edo_pkgs) {
		this.edo_pkgs = edo_pkgs;
	}

	public Long getDeliverd_pkgs() {
		return deliverd_pkgs;
	}

	public void setDeliverd_pkgs(Long deliverd_pkgs) {
		this.deliverd_pkgs = deliverd_pkgs;
	}

	public Long getBalance_pkgs() {
		return balance_pkgs;
	}

	public void setBalance_pkgs(Long balance_pkgs) {
		this.balance_pkgs = balance_pkgs;
	}

	public Long getEsn_pkgs() {
		return esn_pkgs;
	}

	public void setEsn_pkgs(Long esn_pkgs) {
		this.esn_pkgs = esn_pkgs;
	}

	public Long getEsn_rcvd() {
		return esn_rcvd;
	}

	public void setEsn_rcvd(Long esn_rcvd) {
		this.esn_rcvd = esn_rcvd;
	}

	public Long getEsn_load() {
		return esn_load;
	}

	public void setEsn_load(Long esn_load) {
		this.esn_load = esn_load;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getMain_adp_trk() {
		return main_adp_trk;
	}

	public void setMain_adp_trk(String main_adp_trk) {
		this.main_adp_trk = main_adp_trk;
	}

	public String getDisc_vsl() {
		return disc_vsl;
	}

	public void setDisc_vsl(String disc_vsl) {
		this.disc_vsl = disc_vsl;
	}

	public String getLoad_vsl() {
		return load_vsl;
	}

	public void setLoad_vsl(String load_vsl) {
		this.load_vsl = load_vsl;
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

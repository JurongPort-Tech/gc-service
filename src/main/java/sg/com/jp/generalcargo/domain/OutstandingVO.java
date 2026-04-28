package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OutstandingVO extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String terminal;
	private String scheme;
	
	private String edo_asn_nbr;
	private String bl_nbr;
	private Integer bl_pkgs;
	private Integer pkgs;
	private Integer delivered_pkgs;
	private Integer bal_pkgs;
	private String vsl_nm;
	private String in_voy_nbr;
	private Date atb_dttm;
	private Date cod_dttm;
	private String free_stg_end;
	private String free_sqr;
	private String rent_end;
	private String adp;
	private String status;
	private String movement;


	private String esn_asn_nbr;
	private String bk_ref_nbr;
	private Integer bk_ref_pkgs;
	private Integer stored_pkgs;
	private String out_voy_nbr;

	private Integer bk_pkgs;
	private Date etb_dttm;
	private Date first_ua;
	private Date esn_free_sr_end;
	private String trucker;

	private String crg_type;
	private String etb_atb;
	private String mft_seq_nbr;
	private String vv_cd;
	private String var_nbr;
	private String crg_status;
	private Date edo_free_stg_end;
	private Date esn_free_stg_end;

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

	/**
	 * @return the bl_nbr
	 */
	public String getBl_nbr() {
		return bl_nbr;
	}
	/**
	 * @param bl_nbr the bl_nbr to set
	 */
	public void setBl_nbr(String bl_nbr) {
		this.bl_nbr = bl_nbr;
	}
	/**
	 * @return the bl_pkgs
	 */
	public Integer getBl_pkgs() {
		return bl_pkgs;
	}
	/**
	 * @param bl_pkgs the bl_pkgs to set
	 */
	public void setBl_pkgs(Integer bl_pkgs) {
		this.bl_pkgs = bl_pkgs;
	}
	/**
	 * @return the pkgs
	 */
	public Integer getPkgs() {
		return pkgs;
	}
	/**
	 * @param pkgs the pkgs to set
	 */
	public void setPkgs(Integer pkgs) {
		this.pkgs = pkgs;
	}
	/**
	 * @return the delivered_pkgs
	 */
	public Integer getDelivered_pkgs() {
		return delivered_pkgs;
	}
	/**
	 * @param delivered_pkgs the delivered_pkgs to set
	 */
	public void setDelivered_pkgs(Integer delivered_pkgs) {
		this.delivered_pkgs = delivered_pkgs;
	}
	/**
	 * @return the bal_pkgs
	 */
	public Integer getBal_pkgs() {
		return bal_pkgs;
	}
	/**
	 * @param bal_pkgs the bal_pkgs to set
	 */
	public void setBal_pkgs(Integer bal_pkgs) {
		this.bal_pkgs = bal_pkgs;
	}
	/**
	 * @return the vsl_nm
	 */
	public String getVsl_nm() {
		return vsl_nm;
	}
	/**
	 * @param vsl_nm the vsl_nm to set
	 */
	public void setVsl_nm(String vsl_nm) {
		this.vsl_nm = vsl_nm;
	}
	/**
	 * @return the in_voy_nbr
	 */
	public String getIn_voy_nbr() {
		return in_voy_nbr;
	}
	/**
	 * @param in_voy_nbr the in_voy_nbr to set
	 */
	public void setIn_voy_nbr(String in_voy_nbr) {
		this.in_voy_nbr = in_voy_nbr;
	}
	/**
	 * @return the atb_dttm
	 */
	public Date getAtb_dttm() {
		return atb_dttm;
	}
	/**
	 * @param atb_dttm the atb_dttm to set
	 */
	public void setAtb_dttm(Date atb_dttm) {
		this.atb_dttm = atb_dttm;
	}
	/**
	 * @return the cod_dttm
	 */
	public Date getCod_dttm() {
		return cod_dttm;
	}
	/**
	 * @param cod_dttm the cod_dttm to set
	 */
	public void setCod_dttm(Date cod_dttm) {
		this.cod_dttm = cod_dttm;
	}
	/**
	 * @return the free_sqr
	 */
	public String getFree_sqr() {
		return free_sqr;
	}
	/**
	 * @param free_sqr the free_sqr to set
	 */
	public void setFree_sqr(String free_sqr) {
		this.free_sqr = free_sqr;
	}
	/**
	 * @return the rent_end
	 */
	public String getRent_end() {
		return rent_end;
	}
	/**
	 * @param rent_end the rent_end to set
	 */
	public void setRent_end(String rent_end) {
		this.rent_end = rent_end;
	}
	/**
	 * @return the adp
	 */
	public String getAdp() {
		return adp;
	}
	/**
	 * @param adp the adp to set
	 */
	public void setAdp(String adp) {
		this.adp = adp;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the movement
	 */
	public String getMovement() {
		return movement;
	}
	/**
	 * @param movement the movement to set
	 */
	public void setMovement(String movement) {
		this.movement = movement;
	}
	/**
	 * @return the bk_ref_nbr
	 */
	public String getBk_ref_nbr() {
		return bk_ref_nbr;
	}
	/**
	 * @param bk_ref_nbr the bk_ref_nbr to set
	 */
	public void setBk_ref_nbr(String bk_ref_nbr) {
		this.bk_ref_nbr = bk_ref_nbr;
	}
	/**
	 * @return the bk_pkgs
	 */
	public Integer getBk_pkgs() {
		return bk_pkgs;
	}
	/**
	 * @param bk_pkgs the bk_pkgs to set
	 */
	public void setBk_pkgs(Integer bk_pkgs) {
		this.bk_pkgs = bk_pkgs;
	}
	/**
	 * @return the stored_pkgs
	 */
	public Integer getStored_pkgs() {
		return stored_pkgs;
	}
	/**
	 * @param stored_pkgs the stored_pkgs to set
	 */
	public void setStored_pkgs(Integer stored_pkgs) {
		this.stored_pkgs = stored_pkgs;
	}
	/**
	 * @return the out_voy_nbr
	 */
	public String getOut_voy_nbr() {
		return out_voy_nbr;
	}
	/**
	 * @param out_voy_nbr the out_voy_nbr to set
	 */
	public void setOut_voy_nbr(String out_voy_nbr) {
		this.out_voy_nbr = out_voy_nbr;
	}
	/**
	 * @return the etb_dttm
	 */
	public Date getEtb_dttm() {
		return etb_dttm;
	}
	/**
	 * @param etb_dttm the etb_dttm to set
	 */
	public void setEtb_dttm(Date etb_dttm) {
		this.etb_dttm = etb_dttm;
	}

	/**
	 * @return the trucker
	 */
	public String getTrucker() {
		return trucker;
	}
	/**
	 * @param trucker the trucker to set
	 */
	public void setTrucker(String trucker) {
		this.trucker = trucker;
	}

	/**
	 * @return the edo_asn_nbr
	 */
	public String getEdo_asn_nbr() {
		return edo_asn_nbr;
	}
	/**
	 * @param edo_asn_nbr the edo_asn_nbr to set
	 */
	public void setEdo_asn_nbr(String edo_asn_nbr) {
		this.edo_asn_nbr = edo_asn_nbr;
	}
	/**
	 * @return the free_stg_end
	 */
	public String getFree_stg_end() {
		return free_stg_end;
	}
	/**
	 * @param free_stg_end the free_stg_end to set
	 */
	public void setFree_stg_end(String free_stg_end) {
		this.free_stg_end = free_stg_end;
	}
	public String getEsn_asn_nbr() {
		return esn_asn_nbr;
	}
	public void setEsn_asn_nbr(String esn_asn_nbr) {
		this.esn_asn_nbr = esn_asn_nbr;
	}
	public Integer getBk_ref_pkgs() {
		return bk_ref_pkgs;
	}
	public void setBk_ref_pkgs(Integer bk_ref_pkgs) {
		this.bk_ref_pkgs = bk_ref_pkgs;
	}
	public Date getFirst_ua() {
		return first_ua;
	}
	public void setFirst_ua(Date first_ua) {
		this.first_ua = first_ua;
	}

	public Date getEsn_free_sr_end() {
		return esn_free_sr_end;
	}
	public void setEsn_free_sr_end(Date esn_free_sr_end) {
		this.esn_free_sr_end = esn_free_sr_end;
	}

	public String getCrg_type() {
		return crg_type;
	}
	public void setCrg_type(String crg_type) {
		this.crg_type = crg_type;
	}
	public String getEtb_atb() {
		return etb_atb;
	}
	public void setEtb_atb(String etb_atb) {
		this.etb_atb = etb_atb;
	}
	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}
	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}
	public String getVv_cd() {
		return vv_cd;
	}
	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}
	public String getVar_nbr() {
		return var_nbr;
	}
	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}
	public String getCrg_status() {
		return crg_status;
	}
	public void setCrg_status(String crg_status) {
		this.crg_status = crg_status;
	}
	public Date getEdo_free_stg_end() {
		return edo_free_stg_end;
	}
	public void setEdo_free_stg_end(Date edo_free_stg_end) {
		this.edo_free_stg_end = edo_free_stg_end;
	}
	public Date getEsn_free_stg_end() {
		return esn_free_stg_end;
	}
	public void setEsn_free_stg_end(Date esn_free_stg_end) {
		this.esn_free_stg_end = esn_free_stg_end;
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
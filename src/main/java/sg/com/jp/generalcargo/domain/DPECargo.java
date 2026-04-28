package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Revision Change
 * ===========================================================================================================================================
 * Author Date Request No Description of Change Version
 * -------------------------------------------------------------------------------------------------------------------------------------------
 * VietNguyen (FPT) Nov 08, 2013 Documentation Process Enhancement Initial 1.0
 */
@JsonInclude(value = Include.NON_NULL)
public class DPECargo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String terminal;
	private String scheme;
	private String subScheme;
	private String fromTerminal;
	private String toTerminal;
	private String fromScheme;
	private String toScheme;
	// For esn
	private String esn_asn_nbr;
	private String trucker_nm;
	private String trans_type;
	private String stuff_ind;
	private String shipper_nm;

	// For esn_details table
	private int ua_nbr_pkgs;
	private String crg_des;
	private String esn_hs_code;
	private double esn_vol;
	private double esn_wt;

	// For bk_details table
	private String cargo_type;
	private String esn_dg_ind;
	private String stg_ind;
	private String pkg_type;

	// for ESN_MARKINGS
	private String markings;

	// for GB_EDO
	private String edo_asn_nbr;
	private String adp_nm;
	private int nbr_pkgs;
	private String crg_status;
	private int dn_nbr_pkgs;
	private int trans_dn_nbr_pkgs;
	private int trans_nbr_pkgs;
	private String edo_status;
	private String adp_ic_tdbcr_nbr;
	private int edo_pkgs;
	private double nom_wt;
	private double nom_vol;
	private String acct_nbr;
	private String edo_delivery_to;
	private String wh_ind;
	private String wh_aggr_nbr;
	private String wh_remarks;

	// for CRG_TYPE
	private String crg_type_nm;

	// PKG_TYPES
	private String pkg_desc;

	// for bk_details table
	private int shutout_delivery_pkgs;
	private int actual_nbr_shipped;
	private String bk_ref_nbr;
	private int bk_nbr_pkgs;
	private String old_bk_ref;

	private int transfer_nbr_pkgs;

	// For Vessel_call Table
	private String vsl_nm;
	private String in_voy_nbr;
	private String out_voy_nbr;
	private String vv_cd;
	private String fr_vsl_nm;
	private String fr_out_voy_nbr;
	private String gb_close_shp_ind;
	private String vv_status_ind;

	// for CARGO_CATEGORY_CODE
	private String cc_name;

	private int shutout_pkgs;
	private int balance_to_load;
	private int loaded_pkgs;

	private int esn_pkg;
	private double esnpkg_wt;
	private double esnpkg_vol;

	private int shutout_pkg;
	private double shutoutpkg_wt;
	private double shutoutpkg_vol;

	private int outstanding_pkg;
	private double outstanding_wt;
	private double outstanding_vol;
	private int max_edo_pkg;
	private String esn_asn_nbr1;
	private double max_edo_pkg_wt;
	private double max_edo_pkg_vol;

	private int tesnpj_nbr_pkgs;
	private int tesnjj_nbr_pkgs;

	private String esn_first_trans_dttm;
	private String tesnjj_first_trans_dttm;
	private String tesnpj_first_trans_dttm;
	private String first_trans_dttm;
	private String transfer_pkgs;
	private String shutout_edo_pkgs;
	private String balance_pkgs;
	private String short_ship_pkgs;
	// added by ns
	private String vslNmOutVoy;

	public String getVslNmOutVoy() {
		return vslNmOutVoy;
	}

	public void setVslNmOutVoy(String vslNmOutVoy) {
		
		this.vslNmOutVoy = vslNmOutVoy;
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

	public String getFromTerminal() {
		return fromTerminal;
	}

	public void setFromTerminal(String fromTerminal) {
		this.fromTerminal = fromTerminal;
	}

	public String getToTerminal() {
		return toTerminal;
	}

	public void setToTerminal(String toTerminal) {
		this.toTerminal = toTerminal;
	}

	public String getFromScheme() {
		return fromScheme;
	}

	public void setFromScheme(String fromScheme) {
		this.fromScheme = fromScheme;
	}

	public String getToScheme() {
		return toScheme;
	}

	public void setToScheme(String toScheme) {
		this.toScheme = toScheme;
	}

	/**
	 * @return the short_ship_pkgs
	 */
	public String getShort_ship_pkgs() {
		return short_ship_pkgs;
	}

	/**
	 * @param short_ship_pkgs the short_ship_pkgs to set
	 */
	public void setShort_ship_pkgs(String short_ship_pkgs) {
		this.short_ship_pkgs = short_ship_pkgs;
	}

	/**
	 * @return the balance_pkgs
	 */
	public String getBalance_pkgs() {
		return balance_pkgs;
	}

	/**
	 * @param balance_pkgs the balance_pkgs to set
	 */
	public void setBalance_pkgs(String balance_pkgs) {
		this.balance_pkgs = balance_pkgs;
	}

	/**
	 * @return the transfer_pkgs
	 */
	public String getTransfer_pkgs() {
		return transfer_pkgs;
	}

	/**
	 * @param transfer_pkgs the transfer_pkgs to set
	 */
	public void setTransfer_pkgs(String transfer_pkgs) {
		this.transfer_pkgs = transfer_pkgs;
	}

	/**
	 * @return the shutout_edo_pkgs
	 */
	public String getShutout_edo_pkgs() {
		return shutout_edo_pkgs;
	}

	/**
	 * @param shutout_edo_pkgs the shutout_edo_pkgs to set
	 */
	public void setShutout_edo_pkgs(String shutout_edo_pkgs) {
		this.shutout_edo_pkgs = shutout_edo_pkgs;
	}

	/**
	 * @return the esn_asn_nbr
	 */
	public String getEsn_asn_nbr() {
		return esn_asn_nbr;
	}

	/**
	 * @param esn_asn_nbr the esn_asn_nbr to set
	 */
	public void setEsn_asn_nbr(String esn_asn_nbr) {
		this.esn_asn_nbr = esn_asn_nbr;
	}

	/**
	 * @return the ua_nbr_pkgs
	 */
	public int getUa_nbr_pkgs() {
		return ua_nbr_pkgs;
	}

	/**
	 * @param ua_nbr_pkgs the ua_nbr_pkgs to set
	 */
	public void setUa_nbr_pkgs(int ua_nbr_pkgs) {
		this.ua_nbr_pkgs = ua_nbr_pkgs;
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
	 * @return the vv_cd
	 */
	public String getVv_cd() {
		return vv_cd;
	}

	/**
	 * @param vv_cd the vv_cd to set
	 */
	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	/**
	 * @return the trucker_nm
	 */
	public String getTrucker_nm() {
		return trucker_nm;
	}

	/**
	 * @param trucker_nm the trucker_nm to set
	 */
	public void setTrucker_nm(String trucker_nm) {
		this.trucker_nm = trucker_nm;
	}

	/**
	 * @return the crg_des
	 */
	public String getCrg_des() {
		return crg_des;
	}

	/**
	 * @param crg_des the crg_des to set
	 */
	public void setCrg_des(String crg_des) {
		this.crg_des = crg_des;
	}

	/**
	 * @return the cargo_type
	 */
	public String getCargo_type() {
		return cargo_type;
	}

	/**
	 * @param cargo_type the cargo_type to set
	 */
	public void setCargo_type(String cargo_type) {
		this.cargo_type = cargo_type;
	}

	/**
	 * @return the nbr_pkgs
	 */
	public int getNbr_pkgs() {
		return nbr_pkgs;
	}

	/**
	 * @param nbr_pkgs the nbr_pkgs to set
	 */
	public void setNbr_pkgs(int nbr_pkgs) {
		this.nbr_pkgs = nbr_pkgs;
	}

	/**
	 * @return the crg_status
	 */
	public String getCrg_status() {
		return crg_status;
	}

	/**
	 * @param crg_status the crg_status to set
	 */
	public void setCrg_status(String crg_status) {
		this.crg_status = crg_status;
	}

	/**
	 * @return the dn_nbr_pkgs
	 */
	public int getDn_nbr_pkgs() {
		return dn_nbr_pkgs;
	}

	/**
	 * @param dn_nbr_pkgs the dn_nbr_pkgs to set
	 */
	public void setDn_nbr_pkgs(int dn_nbr_pkgs) {
		this.dn_nbr_pkgs = dn_nbr_pkgs;
	}

	/**
	 * @return the trans_dn_nbr_pkgs
	 */
	public int getTrans_dn_nbr_pkgs() {
		return trans_dn_nbr_pkgs;
	}

	/**
	 * @param trans_dn_nbr_pkgs the trans_dn_nbr_pkgs to set
	 */
	public void setTrans_dn_nbr_pkgs(int trans_dn_nbr_pkgs) {
		this.trans_dn_nbr_pkgs = trans_dn_nbr_pkgs;
	}

	/**
	 * @return the trans_nbr_pkgs
	 */
	public int getTrans_nbr_pkgs() {
		return trans_nbr_pkgs;
	}

	/**
	 * @param trans_nbr_pkgs the trans_nbr_pkgs to set
	 */
	public void setTrans_nbr_pkgs(int trans_nbr_pkgs) {
		this.trans_nbr_pkgs = trans_nbr_pkgs;
	}

	/**
	 * @return the crg_type_nm
	 */
	public String getCrg_type_nm() {
		return crg_type_nm;
	}

	/**
	 * @param crg_type_nm the crg_type_nm to set
	 */
	public void setCrg_type_nm(String crg_type_nm) {
		this.crg_type_nm = crg_type_nm;
	}

	/**
	 * @return the shutout_delivery_pkgs
	 */
	public int getShutout_delivery_pkgs() {
		return shutout_delivery_pkgs;
	}

	/**
	 * @param shutout_delivery_pkgs the shutout_delivery_pkgs to set
	 */
	public void setShutout_delivery_pkgs(int shutout_delivery_pkgs) {
		this.shutout_delivery_pkgs = shutout_delivery_pkgs;
	}

	/**
	 * @return the actual_nbr_shipped
	 */
	public int getActual_nbr_shipped() {
		return actual_nbr_shipped;
	}

	/**
	 * @param actual_nbr_shipped the actual_nbr_shipped to set
	 */
	public void setActual_nbr_shipped(int actual_nbr_shipped) {
		this.actual_nbr_shipped = actual_nbr_shipped;
	}

	/**
	 * @return the bk_nbr_pkgs
	 */
	public int getBk_nbr_pkgs() {
		return bk_nbr_pkgs;
	}

	/**
	 * @param bk_nbr_pkgs the bk_nbr_pkgs to set
	 */
	public void setBk_nbr_pkgs(int bk_nbr_pkgs) {
		this.bk_nbr_pkgs = bk_nbr_pkgs;
	}

	/**
	 * @return the transfer_nbr_pkgs
	 */
	public int getTransfer_nbr_pkgs() {
		return transfer_nbr_pkgs;
	}

	/**
	 * @param transfer_nbr_pkgs the transfer_nbr_pkgs to set
	 */
	public void setTransfer_nbr_pkgs(int transfer_nbr_pkgs) {
		this.transfer_nbr_pkgs = transfer_nbr_pkgs;
	}

	/**
	 * @return the shutout_pkgs
	 */
	public int getShutout_pkgs() {
		return shutout_pkgs;
	}

	/**
	 * @param shutout_pkgs the shutout_pkgs to set
	 */
	public void setShutout_pkgs(int shutout_pkgs) {
		this.shutout_pkgs = shutout_pkgs;
	}

	/**
	 * @return the balance_to_load
	 */
	public int getBalance_to_load() {
		return balance_to_load;
	}

	/**
	 * @param balance_to_load the balance_to_load to set
	 */
	public void setBalance_to_load(int balance_to_load) {
		this.balance_to_load = balance_to_load;
	}

	/**
	 * @return the loaded_pkgs
	 */
	public int getLoaded_pkgs() {
		return loaded_pkgs;
	}

	/**
	 * @param loaded_pkgs the loaded_pkgs to set
	 */
	public void setLoaded_pkgs(int loaded_pkgs) {
		this.loaded_pkgs = loaded_pkgs;
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
	 * @return the adp_nm
	 */
	public String getAdp_nm() {
		return adp_nm;
	}

	/**
	 * @param adp_nm the adp_nm to set
	 */
	public void setAdp_nm(String adp_nm) {
		this.adp_nm = adp_nm;
	}

	/**
	 * @return the esn_asn_nbr1
	 */
	public String getEsn_asn_nbr1() {
		return esn_asn_nbr1;
	}

	/**
	 * @param esn_asn_nbr1 the esn_asn_nbr1 to set
	 */
	public void setEsn_asn_nbr1(String esn_asn_nbr1) {
		this.esn_asn_nbr1 = esn_asn_nbr1;
	}

	/**
	 * @return the trans_type
	 */
	public String getTrans_type() {
		return trans_type;
	}

	/**
	 * @param trans_type the trans_type to set
	 */
	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
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
	 * @return the esn_hs_code
	 */
	public String getEsn_hs_code() {
		return esn_hs_code;
	}

	/**
	 * @param esn_hs_code the esn_hs_code to set
	 */
	public void setEsn_hs_code(String esn_hs_code) {
		this.esn_hs_code = esn_hs_code;
	}

	/**
	 * @return the esn_dg_ind
	 */
	public String getEsn_dg_ind() {
		return esn_dg_ind;
	}

	/**
	 * @param esn_dg_ind the esn_dg_ind to set
	 */
	public void setEsn_dg_ind(String esn_dg_ind) {
		this.esn_dg_ind = esn_dg_ind;
	}

	/**
	 * @return the stg_ind
	 */
	public String getStg_ind() {
		return stg_ind;
	}

	/**
	 * @param stg_ind the stg_ind to set
	 */
	public void setStg_ind(String stg_ind) {
		this.stg_ind = stg_ind;
	}

	/**
	 * @return the pkg_type
	 */
	public String getPkg_type() {
		return pkg_type;
	}

	/**
	 * @param pkg_type the pkg_type to set
	 */
	public void setPkg_type(String pkg_type) {
		this.pkg_type = pkg_type;
	}

	/**
	 * @return the markings
	 */
	public String getMarkings() {
		return markings;
	}

	/**
	 * @param markings the markings to set
	 */
	public void setMarkings(String markings) {
		this.markings = markings;
	}

	/**
	 * @return the pkg_desc
	 */
	public String getPkg_desc() {
		return pkg_desc;
	}

	/**
	 * @param pkg_desc the pkg_desc to set
	 */
	public void setPkg_desc(String pkg_desc) {
		this.pkg_desc = pkg_desc;
	}

	/**
	 * @return the esn_pkg
	 */
	public int getEsn_pkg() {
		return esn_pkg;
	}

	/**
	 * @param esn_pkg the esn_pkg to set
	 */
	public void setEsn_pkg(int esn_pkg) {
		this.esn_pkg = esn_pkg;
	}

	/**
	 * @return the shutout_pkg
	 */
	public int getShutout_pkg() {
		return shutout_pkg;
	}

	/**
	 * @param shutout_pkg the shutout_pkg to set
	 */
	public void setShutout_pkg(int shutout_pkg) {
		this.shutout_pkg = shutout_pkg;
	}

	/**
	 * @return the outstanding_pkg
	 */
	public int getOutstanding_pkg() {
		return outstanding_pkg;
	}

	/**
	 * @param outstanding_pkg the outstanding_pkg to set
	 */
	public void setOutstanding_pkg(int outstanding_pkg) {
		this.outstanding_pkg = outstanding_pkg;
	}

	/**
	 * @return the max_edo_pkg
	 */
	public int getMax_edo_pkg() {
		return max_edo_pkg;
	}

	/**
	 * @param max_edo_pkg the max_edo_pkg to set
	 */
	public void setMax_edo_pkg(int max_edo_pkg) {
		this.max_edo_pkg = max_edo_pkg;
	}

	/**
	 * @return the esnpkg_wt
	 */
	public double getEsnpkg_wt() {
		return esnpkg_wt;
	}

	/**
	 * @param esnpkg_wt the esnpkg_wt to set
	 */
	public void setEsnpkg_wt(double esnpkg_wt) {
		this.esnpkg_wt = esnpkg_wt;
	}

	/**
	 * @return the esnpkg_vol
	 */
	public double getEsnpkg_vol() {
		return esnpkg_vol;
	}

	/**
	 * @param esnpkg_vol the esnpkg_vol to set
	 */
	public void setEsnpkg_vol(double esnpkg_vol) {
		this.esnpkg_vol = esnpkg_vol;
	}

	/**
	 * @return the shutoutpkg_wt
	 */
	public double getShutoutpkg_wt() {
		return shutoutpkg_wt;
	}

	/**
	 * @param shutoutpkg_wt the shutoutpkg_wt to set
	 */
	public void setShutoutpkg_wt(double shutoutpkg_wt) {
		this.shutoutpkg_wt = shutoutpkg_wt;
	}

	/**
	 * @return the shutoutpkg_vol
	 */
	public double getShutoutpkg_vol() {
		return shutoutpkg_vol;
	}

	/**
	 * @param shutoutpkg_vol the shutoutpkg_vol to set
	 */
	public void setShutoutpkg_vol(double shutoutpkg_vol) {
		this.shutoutpkg_vol = shutoutpkg_vol;
	}

	/**
	 * @return the outstanding_wt
	 */
	public double getOutstanding_wt() {
		return outstanding_wt;
	}

	/**
	 * @param outstanding_wt the outstanding_wt to set
	 */
	public void setOutstanding_wt(double outstanding_wt) {
		this.outstanding_wt = outstanding_wt;
	}

	/**
	 * @return the outstanding_vol
	 */
	public double getOutstanding_vol() {
		return outstanding_vol;
	}

	/**
	 * @param outstanding_vol the outstanding_vol to set
	 */
	public void setOutstanding_vol(double outstanding_vol) {
		this.outstanding_vol = outstanding_vol;
	}

	/**
	 * @return the tesnpj_nbr_pkgs
	 */
	public int getTesnpj_nbr_pkgs() {
		return tesnpj_nbr_pkgs;
	}

	/**
	 * @param tesnpj_nbr_pkgs the tesnpj_nbr_pkgs to set
	 */
	public void setTesnpj_nbr_pkgs(int tesnpj_nbr_pkgs) {
		this.tesnpj_nbr_pkgs = tesnpj_nbr_pkgs;
	}

	/**
	 * @return the tesnjj_nbr_pkgs
	 */
	public int getTesnjj_nbr_pkgs() {
		return tesnjj_nbr_pkgs;
	}

	/**
	 * @param tesnjj_nbr_pkgs the tesnjj_nbr_pkgs to set
	 */
	public void setTesnjj_nbr_pkgs(int tesnjj_nbr_pkgs) {
		this.tesnjj_nbr_pkgs = tesnjj_nbr_pkgs;
	}

	/**
	 * @return the esn_first_trans_dttm
	 */
	public String getEsn_first_trans_dttm() {
		return esn_first_trans_dttm;
	}

	/**
	 * @param esn_first_trans_dttm the esn_first_trans_dttm to set
	 */
	public void setEsn_first_trans_dttm(String esn_first_trans_dttm) {
		this.esn_first_trans_dttm = esn_first_trans_dttm;
	}

	/**
	 * @return the tesnjj_first_trans_dttm
	 */
	public String getTesnjj_first_trans_dttm() {
		return tesnjj_first_trans_dttm;
	}

	/**
	 * @param tesnjj_first_trans_dttm the tesnjj_first_trans_dttm to set
	 */
	public void setTesnjj_first_trans_dttm(String tesnjj_first_trans_dttm) {
		this.tesnjj_first_trans_dttm = tesnjj_first_trans_dttm;
	}

	/**
	 * @return the tesnpj_first_trans_dttm
	 */
	public String getTesnpj_first_trans_dttm() {
		return tesnpj_first_trans_dttm;
	}

	/**
	 * @param tesnpj_first_trans_dttm the tesnpj_first_trans_dttm to set
	 */
	public void setTesnpj_first_trans_dttm(String tesnpj_first_trans_dttm) {
		this.tesnpj_first_trans_dttm = tesnpj_first_trans_dttm;
	}

	/**
	 * @return the first_trans_dttm
	 */
	public String getFirst_trans_dttm() {
		return first_trans_dttm;
	}

	/**
	 * @param first_trans_dttm the first_trans_dttm to set
	 */
	public void setFirst_trans_dttm(String first_trans_dttm) {
		this.first_trans_dttm = first_trans_dttm;
	}

	/**
	 * @return the edo_status
	 */
	public String getEdo_status() {
		return edo_status;
	}

	/**
	 * @param edo_status the edo_status to set
	 */
	public void setEdo_status(String edo_status) {
		this.edo_status = edo_status;
	}

	/**
	 * @return the stuff_ind
	 */
	public String getStuff_ind() {
		return stuff_ind;
	}

	/**
	 * @param stuff_ind the stuff_ind to set
	 */
	public void setStuff_ind(String stuff_ind) {
		this.stuff_ind = stuff_ind;
	}

	/**
	 * @return the shipper_nm
	 */
	public String getShipper_nm() {
		return shipper_nm;
	}

	/**
	 * @param shipper_nm the shipper_nm to set
	 */
	public void setShipper_nm(String shipper_nm) {
		this.shipper_nm = shipper_nm;
	}

	/**
	 * @return the esn_vol
	 */
	public double getEsn_vol() {
		return esn_vol;
	}

	/**
	 * @param esn_vol the esn_vol to set
	 */
	public void setEsn_vol(double esn_vol) {
		this.esn_vol = esn_vol;
	}

	/**
	 * @return the esn_wt
	 */
	public double getEsn_wt() {
		return esn_wt;
	}

	/**
	 * @param esn_wt the esn_wt to set
	 */
	public void setEsn_wt(double esn_wt) {
		this.esn_wt = esn_wt;
	}

	/**
	 * @return the cc_name
	 */
	public String getCc_name() {
		return cc_name;
	}

	/**
	 * @param cc_name the cc_name to set
	 */
	public void setCc_name(String cc_name) {
		this.cc_name = cc_name;
	}

	/**
	 * @return the adp_ic_tdbcr_nbr
	 */
	public String getAdp_ic_tdbcr_nbr() {
		return adp_ic_tdbcr_nbr;
	}

	/**
	 * @param adp_ic_tdbcr_nbr the adp_ic_tdbcr_nbr to set
	 */
	public void setAdp_ic_tdbcr_nbr(String adp_ic_tdbcr_nbr) {
		this.adp_ic_tdbcr_nbr = adp_ic_tdbcr_nbr;
	}

	/**
	 * @return the edo_pkgs
	 */
	public int getEdo_pkgs() {
		return edo_pkgs;
	}

	/**
	 * @param edo_pkgs the edo_pkgs to set
	 */
	public void setEdo_pkgs(int edo_pkgs) {
		this.edo_pkgs = edo_pkgs;
	}

	/**
	 * @return the nom_wt
	 */
	public double getNom_wt() {
		return nom_wt;
	}

	/**
	 * @param nom_wt the nom_wt to set
	 */
	public void setNom_wt(double nom_wt) {
		this.nom_wt = nom_wt;
	}

	/**
	 * @return the nom_vol
	 */
	public double getNom_vol() {
		return nom_vol;
	}

	/**
	 * @param nom_vol the nom_vol to set
	 */
	public void setNom_vol(double nom_vol) {
		this.nom_vol = nom_vol;
	}

	/**
	 * @return the acct_nbr
	 */
	public String getAcct_nbr() {
		return acct_nbr;
	}

	/**
	 * @param acct_nbr the acct_nbr to set
	 */
	public void setAcct_nbr(String acct_nbr) {
		this.acct_nbr = acct_nbr;
	}

	/**
	 * @return the edo_delivery_to
	 */
	public String getEdo_delivery_to() {
		return edo_delivery_to;
	}

	/**
	 * @param edo_delivery_to the edo_delivery_to to set
	 */
	public void setEdo_delivery_to(String edo_delivery_to) {
		this.edo_delivery_to = edo_delivery_to;
	}

	/**
	 * @return the wh_ind
	 */
	public String getWh_ind() {
		return wh_ind;
	}

	/**
	 * @param wh_ind the wh_ind to set
	 */
	public void setWh_ind(String wh_ind) {
		this.wh_ind = wh_ind;
	}

	/**
	 * @return the wh_aggr_nbr
	 */
	public String getWh_aggr_nbr() {
		return wh_aggr_nbr;
	}

	/**
	 * @param wh_aggr_nbr the wh_aggr_nbr to set
	 */
	public void setWh_aggr_nbr(String wh_aggr_nbr) {
		this.wh_aggr_nbr = wh_aggr_nbr;
	}

	/**
	 * @return the wh_remarks
	 */
	public String getWh_remarks() {
		return wh_remarks;
	}

	/**
	 * @param wh_remarks the wh_remarks to set
	 */
	public void setWh_remarks(String wh_remarks) {
		this.wh_remarks = wh_remarks;
	}

	/**
	 * @return the max_edo_pkg_wt
	 */
	public double getMax_edo_pkg_wt() {
		return max_edo_pkg_wt;
	}

	/**
	 * @param max_edo_pkg_wt the max_edo_pkg_wt to set
	 */
	public void setMax_edo_pkg_wt(double max_edo_pkg_wt) {
		this.max_edo_pkg_wt = max_edo_pkg_wt;
	}

	/**
	 * @return the max_edo_pkg_vol
	 */
	public double getMax_edo_pkg_vol() {
		return max_edo_pkg_vol;
	}

	/**
	 * @param max_edo_pkg_vol the max_edo_pkg_vol to set
	 */
	public void setMax_edo_pkg_vol(double max_edo_pkg_vol) {
		this.max_edo_pkg_vol = max_edo_pkg_vol;
	}

	/**
	 * @return the old_bk_ref
	 */
	public String getOld_bk_ref() {
		return old_bk_ref;
	}

	/**
	 * @return the fr_vsl_nm
	 */
	public String getFr_vsl_nm() {
		return fr_vsl_nm;
	}

	/**
	 * @param old_bk_ref the old_bk_ref to set
	 */
	public void setOld_bk_ref(String old_bk_ref) {
		this.old_bk_ref = old_bk_ref;
	}

	/**
	 * @param fr_vsl_nm the fr_vsl_nm to set
	 */
	public void setFr_vsl_nm(String fr_vsl_nm) {
		this.fr_vsl_nm = fr_vsl_nm;
	}

	/**
	 * @return the fr_out_voy_nbr
	 */
	public String getFr_out_voy_nbr() {
		return fr_out_voy_nbr;
	}

	/**
	 * @param fr_out_voy_nbr the fr_out_voy_nbr to set
	 */
	public void setFr_out_voy_nbr(String fr_out_voy_nbr) {
		this.fr_out_voy_nbr = fr_out_voy_nbr;
	}

	public String getGb_close_shp_ind() {
		return gb_close_shp_ind;
	}

	public void setGb_close_shp_ind(String gb_close_shp_ind) {
		this.gb_close_shp_ind = gb_close_shp_ind;
	}

	public String getVv_status_ind() {
		return vv_status_ind;
	}

	public void setVv_status_ind(String vv_status_ind) {
		this.vv_status_ind = vv_status_ind;
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

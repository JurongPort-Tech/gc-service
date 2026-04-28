package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Revision Change
 * ===========================================================================================================================================
 * Author              Date                           Request No                     Description of Change                      Version
 * -------------------------------------------------------------------------------------------------------------------------------------------
 * VietNguyen (FPT)   Nov 08, 2013                 Documentation Process Enhancement    Initial                                   1.0
 */
@JsonInclude(value = Include.NON_NULL)
public class DPEUtil extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String terminal;
	// For COMPANY_CODE table
	private String co_cd;
	private String co_nm;

	//For Vessel table
	private String vsl_full_nm;

	// For VESSEL_CALL table
	private String vv_cd;
	private String vsl_nm;
	private String in_voy_nbr;
	private String out_voy_nbr;
	private String vv_status_ind;
	private String gb_close_shp_ind;
	private String vsl_type_cd;
	private String scheme;

	//For Berthing table
	private String vsl_berth_dttm;
	private String etb_dttm;
	private String atb_dttm;
	private String atu_dttm;
	private String vsl_etd_dttm;
	private Date atuDttm;

	// For CNTR table
	private String cntr_opr_cd;
	private String cntr_seq_nbr;
	private String cntr_nbr;
	private String size_ft;
	private String cat_cd;
	private String declr_wt;
	private String disc_vv_cd;
	private String load_vv_cd;
	private String nom_load_vv_cd;
	private String purp_cd;

	//for CNTR_OPERATION table
	private String pm_exit_dttm;
	private String load_dttm;
	private String mount_dttm;
	private String opr_state;
	private String load_key_ind;

	// For MISC_TYPE_CODE table
	private String misc_type_cd;
	private String misc_type_nm;

	//for CUST_CONTACT table
	private String email_add;
	private String cust_cd;

	// for TIMESHEET_VSL_OPS table
	private Timestamp ops_dttm;

	// for TIMESHEET_VSL_OPS_DETAILS table
	private String start_time;

	//for GB_EDO table
	private String edo_asn_nbr;
	private Integer dn_nbr_pkgs;
	private String gb_close_bj_ind;
	private Integer trans_dn_nbr_pkgs;

	//for EFORM_APP table
	private String ref_nbr;
	private String app_data;


	//for ESN table
	private String esn_asn_nbr;
	private String bk_ref_nbr;
	private String total_esn_wt;
	private String total_esn_vol;
	private String lyingPkgs;
	private String lyingDays;
	private String crg_status;
	private String edoShutOutPkgs;
	private String shutOutPkgs;
	private String shutout_delivery_pkgs;
	private String trucker_name;


	private String last_modify_user_id;
	private String last_modify_dttm;


	//for Manifest_details table
	private String bl_nbr;
	private String mft_seq_nbr;
	private String var_nbr;
	private String cargo_category_cd;
	private Double maxCargoTon;

	//Port
	private String port_cd;
	private String port_nm;

	//PackingTYpe
	private String pkg_type_cd;
	private String pkg_desc;

	//Crg type
	private String crg_type_cd;
	private String crg_type_nm;

	//Account number
	private String acct_nbr;

	// for exception_alert table
	private String account;

	 //from HsSubCode
	 private String hsSubCode;
	 private String hs_sub_desc;
	 private String hs_sub_code_fr;
	 private String hs_sub_code_to;

	 //from CARGO_CATEGORY_CODE
	 private String cc_cd;
	 private String cc_name;

	 // for combobox
	private String key;
	private String value;


	//shipperName
	private String add_l1;
	private String uen_nbr;
	private String tdb_cr_nbr;

	private String dwell_days;
	private String agent_email;
	private String adp_email;
	private String ta_email;

	//Added by HoaBT2: properties for table PREGATE
	private String gate_in_dttm;
	private String day_in_port;

	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
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
	 * @return the co_cd
	 */
	public String getCo_cd() {
		return co_cd;
	}
	/**
	 * @param co_cd the co_cd to set
	 */
	public void setCo_cd(String co_cd) {
		this.co_cd = co_cd;
	}
	/**
	 * @return the co_nm
	 */
	public String getCo_nm() {
		return co_nm;
	}
	/**
	 * @param co_nm the co_nm to set
	 */
	public void setCo_nm(String co_nm) {
		this.co_nm = co_nm;
	}
	/**
	 * @return the misc_type_cd
	 */
	public String getMisc_type_cd() {
		return misc_type_cd;
	}
	/**
	 * @param misc_type_cd the misc_type_cd to set
	 */
	public void setMisc_type_cd(String misc_type_cd) {
		this.misc_type_cd = misc_type_cd;
	}
	/**
	 * @return the misc_type_nm
	 */
	public String getMisc_type_nm() {
		return misc_type_nm;
	}
	/**
	 * @param misc_type_nm the misc_type_nm to set
	 */
	public void setMisc_type_nm(String misc_type_nm) {
		this.misc_type_nm = misc_type_nm;
	}

	/**
	 * @return the vv_cd
	 */
	public String getVv_cd() {
		return vv_cd;
	}

	/**
	 * @param vv_cd
	 *            the vv_cd to set
	 */
	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
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
	 * @return the email_add
	 */
	public String getEmail_add() {
		return email_add;
	}
	/**
	 * @param email_add the email_add to set
	 */
	public void setEmail_add(String email_add) {
		this.email_add = email_add;
	}
	/**
	 * @return the cust_cd
	 */
	public String getCust_cd() {
		return cust_cd;
	}
	/**
	 * @param cust_cd the cust_cd to set
	 */
	public void setCust_cd(String cust_cd) {
		this.cust_cd = cust_cd;
	}
	/**
	 * @return the ops_dttm
	 */
	public Timestamp getOps_dttm() {
		return ops_dttm;
	}
	/**
	 * @param ops_dttm the ops_dttm to set
	 */
	public void setOps_dttm(Timestamp ops_dttm) {
		this.ops_dttm = ops_dttm;
	}
	/**
	 * @return the start_time
	 */
	public String getStart_time() {
		return start_time;
	}
	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(String start_time) {
		this.start_time = start_time;
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
	 * @return the ref_nbr
	 */
	public String getRef_nbr() {
		return ref_nbr;
	}
	/**
	 * @param ref_nbr the ref_nbr to set
	 */
	public void setRef_nbr(String ref_nbr) {
		this.ref_nbr = ref_nbr;
	}
	/**
	 * @return the app_data
	 */
	public String getApp_data() {
		return app_data;
	}
	/**
	 * @param app_data the app_data to set
	 */
	public void setApp_data(String app_data) {
		this.app_data = app_data;
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
	public String getEtb_dttm() {
		return etb_dttm;
	}
	/**
	 * @param etb_dttm the etb_dttm to set
	 */
	public void setEtb_dttm(String etb_dttm) {
		this.etb_dttm = etb_dttm;
	}
	/**
	 * @return the atb_dttm
	 */
	public String getAtb_dttm() {
		return atb_dttm;
	}
	/**
	 * @param atb_dttm the atb_dttm to set
	 */
	public void setAtb_dttm(String atb_dttm) {
		this.atb_dttm = atb_dttm;
	}
	/**
	 * @return the atu_dttm
	 */
	public String getAtu_dttm() {
		return atu_dttm;
	}
	/**
	 * @param atu_dttm the atu_dttm to set
	 */
	public void setAtu_dttm(String atu_dttm) {
		this.atu_dttm = atu_dttm;
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
     * @return the last_modify_user_id
     */
    public String getLast_modify_user_id() {
        return last_modify_user_id;
    }
    /**
     * @param last_modify_user_id the last_modify_user_id to set
     */
    public void setLast_modify_user_id(String last_modify_user_id) {
        this.last_modify_user_id = last_modify_user_id;
    }
    /**
     * @return the last_modify_dttm
     */
    public String getLast_modify_dttm() {
        return last_modify_dttm;
    }
    /**
     * @param last_modify_dttm the last_modify_dttm to set
     */
    public void setLast_modify_dttm(String last_modify_dttm) {
        this.last_modify_dttm = last_modify_dttm;
    }
    /**
     * @return the vsl_full_nm
     */
    public String getVsl_full_nm() {
        return vsl_full_nm;
    }
    /**
     * @param vsl_full_nm the vsl_full_nm to set
     */
    public void setVsl_full_nm(String vsl_full_nm) {
        this.vsl_full_nm = vsl_full_nm;
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
	 * @return the mft_seq_nbr
	 */
	public String getMft_seq_nbr() {
		return mft_seq_nbr;
	}
	/**
	 * @param mft_seq_nbr the mft_seq_nbr to set
	 */
	public void setMft_seq_nbr(String mft_seq_nbr) {
		this.mft_seq_nbr = mft_seq_nbr;
	}
	/**
	 * @return the var_nbr
	 */
	public String getVar_nbr() {
		return var_nbr;
	}
	/**
	 * @param var_nbr the var_nbr to set
	 */
	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}
	/**
	 * @return the port_cd
	 */
	public String getPort_cd() {
		return port_cd;
	}
	/**
	 * @param port_cd the port_cd to set
	 */
	public void setPort_cd(String port_cd) {
		this.port_cd = port_cd;
	}
	/**
	 * @return the port_nm
	 */
	public String getPort_nm() {
		return port_nm;
	}
	/**
	 * @param port_nm the port_nm to set
	 */
	public void setPort_nm(String port_nm) {
		this.port_nm = port_nm;
	}
	/**
	 * @return the pkg_type_cd
	 */
	public String getPkg_type_cd() {
		return pkg_type_cd;
	}
	/**
	 * @return the pkg_desc
	 */
	public String getPkg_desc() {
		return pkg_desc;
	}
	/**
	 * @param pkg_type_cd the pkg_type_cd to set
	 */
	public void setPkg_type_cd(String pkg_type_cd) {
		this.pkg_type_cd = pkg_type_cd;
	}
	/**
	 * @param pkg_desc the pkg_desc to set
	 */
	public void setPkg_desc(String pkg_desc) {
		this.pkg_desc = pkg_desc;
	}
	/**
	 * @return the crg_type_cd
	 */
	public String getCrg_type_cd() {
		return crg_type_cd;
	}
	/**
	 * @param crg_type_cd the crg_type_cd to set
	 */
	public void setCrg_type_cd(String crg_type_cd) {
		this.crg_type_cd = crg_type_cd;
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
	 * @return the dn_nbr_pkgs
	 */
	public Integer getDn_nbr_pkgs() {
		return dn_nbr_pkgs;
	}
	/**
	 * @param dn_nbr_pkgs the dn_nbr_pkgs to set
	 */
	public void setDn_nbr_pkgs(Integer dn_nbr_pkgs) {
		this.dn_nbr_pkgs = dn_nbr_pkgs;
	}
	/**
	 * @return the gb_close_bj_ind
	 */
	public String getGb_close_bj_ind() {
		return gb_close_bj_ind;
	}
	/**
	 * @param gb_close_bj_ind the gb_close_bj_ind to set
	 */
	public void setGb_close_bj_ind(String gb_close_bj_ind) {
		this.gb_close_bj_ind = gb_close_bj_ind;
	}
	/**
	 * @return the hsSubCode
	 */
	public String getHsSubCode() {
		return hsSubCode;
	}
	/**
	 * @param hsSubCode the hsSubCode to set
	 */
	public void setHsSubCode(String hsSubCode) {
		this.hsSubCode = hsSubCode;
	}
	/**
	 * @return the hs_sub_desc
	 */
	public String getHs_sub_desc() {
		return hs_sub_desc;
	}
	/**
	 * @param hs_sub_desc the hs_sub_desc to set
	 */
	public void setHs_sub_desc(String hs_sub_desc) {
		this.hs_sub_desc = hs_sub_desc;
	}
	/**
	 * @return the hs_sub_code_fr
	 */
	public String getHs_sub_code_fr() {
		return hs_sub_code_fr;
	}
	/**
	 * @param hs_sub_code_fr the hs_sub_code_fr to set
	 */
	public void setHs_sub_code_fr(String hs_sub_code_fr) {
		this.hs_sub_code_fr = hs_sub_code_fr;
	}
	/**
	 * @return the hs_sub_code_to
	 */
	public String getHs_sub_code_to() {
		return hs_sub_code_to;
	}
	/**
	 * @param hs_sub_code_to the hs_sub_code_to to set
	 */
	public void setHs_sub_code_to(String hs_sub_code_to) {
		this.hs_sub_code_to = hs_sub_code_to;
	}
	/**
	 * @return the cc_cd
	 */
	public String getCc_cd() {
		return cc_cd;
	}
	/**
	 * @param cc_cd the cc_cd to set
	 */
	public void setCc_cd(String cc_cd) {
		this.cc_cd = cc_cd;
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
	 * @return the atuDttm
	 */
	public Date getAtuDttm() {
		return atuDttm;
	}
	/**
	 * @param atuDttm the atuDttm to set
	 */
	public void setAtuDttm(Date atuDttm) {
		this.atuDttm = atuDttm;
	}
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * @return the vsl_berth_dttm
	 */
	public String getVsl_berth_dttm() {
		return vsl_berth_dttm;
	}
	/**
	 * @param vsl_berth_dttm the vsl_berth_dttm to set
	 */
	public void setVsl_berth_dttm(String vsl_berth_dttm) {
		this.vsl_berth_dttm = vsl_berth_dttm;
	}
	/**
	 * @return the vsl_etd_dttm
	 */
	public String getVsl_etd_dttm() {
		return vsl_etd_dttm;
	}
	/**
	 * @param vsl_etd_dttm the vsl_etd_dttm to set
	 */
	public void setVsl_etd_dttm(String vsl_etd_dttm) {
		this.vsl_etd_dttm = vsl_etd_dttm;
	}
	/**
	 * @return the vv_status_ind
	 */
	public String getVv_status_ind() {
		return vv_status_ind;
	}
	/**
	 * @param vv_status_ind the vv_status_ind to set
	 */
	public void setVv_status_ind(String vv_status_ind) {
		this.vv_status_ind = vv_status_ind;
	}
	/**
	 * @return the gb_close_shp_ind
	 */
	public String getGb_close_shp_ind() {
		return gb_close_shp_ind;
	}
	/**
	 * @param gb_close_shp_ind the gb_close_shp_ind to set
	 */
	public void setGb_close_shp_ind(String gb_close_shp_ind) {
		this.gb_close_shp_ind = gb_close_shp_ind;
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
	 * @return the cntr_opr_cd
	 */
	public String getCntr_opr_cd() {
		return cntr_opr_cd;
	}
	/**
	 * @param cntr_opr_cd the cntr_opr_cd to set
	 */
	public void setCntr_opr_cd(String cntr_opr_cd) {
		this.cntr_opr_cd = cntr_opr_cd;
	}
	/**
	 * @return the cntr_seq_nbr
	 */
	public String getCntr_seq_nbr() {
		return cntr_seq_nbr;
	}
	/**
	 * @param cntr_seq_nbr the cntr_seq_nbr to set
	 */
	public void setCntr_seq_nbr(String cntr_seq_nbr) {
		this.cntr_seq_nbr = cntr_seq_nbr;
	}
	/**
	 * @return the cntr_nbr
	 */
	public String getCntr_nbr() {
		return cntr_nbr;
	}
	/**
	 * @param cntr_nbr the cntr_nbr to set
	 */
	public void setCntr_nbr(String cntr_nbr) {
		this.cntr_nbr = cntr_nbr;
	}
	/**
	 * @return the size_ft
	 */
	public String getSize_ft() {
		return size_ft;
	}
	/**
	 * @param size_ft the size_ft to set
	 */
	public void setSize_ft(String size_ft) {
		this.size_ft = size_ft;
	}
	/**
	 * @return the cat_cd
	 */
	public String getCat_cd() {
		return cat_cd;
	}
	/**
	 * @param cat_cd the cat_cd to set
	 */
	public void setCat_cd(String cat_cd) {
		this.cat_cd = cat_cd;
	}
	/**
	 * @return the declr_wt
	 */
	public String getDeclr_wt() {
		return declr_wt;
	}
	/**
	 * @param declr_wt the declr_wt to set
	 */
	public void setDeclr_wt(String declr_wt) {
		this.declr_wt = declr_wt;
	}
	/**
	 * @return the load_vv_cd
	 */
	public String getLoad_vv_cd() {
		return load_vv_cd;
	}
	/**
	 * @param load_vv_cd the load_vv_cd to set
	 */
	public void setLoad_vv_cd(String load_vv_cd) {
		this.load_vv_cd = load_vv_cd;
	}
	/**
	 * @return the purp_cd
	 */
	public String getPurp_cd() {
		return purp_cd;
	}
	/**
	 * @param purp_cd the purp_cd to set
	 */
	public void setPurp_cd(String purp_cd) {
		this.purp_cd = purp_cd;
	}
	/**
	 * @return the pm_exit_dttm
	 */
	public String getPm_exit_dttm() {
		return pm_exit_dttm;
	}
	/**
	 * @param pm_exit_dttm the pm_exit_dttm to set
	 */
	public void setPm_exit_dttm(String pm_exit_dttm) {
		this.pm_exit_dttm = pm_exit_dttm;
	}
	/**
	 * @return the load_dttm
	 */
	public String getLoad_dttm() {
		return load_dttm;
	}
	/**
	 * @param load_dttm the load_dttm to set
	 */
	public void setLoad_dttm(String load_dttm) {
		this.load_dttm = load_dttm;
	}
	/**
	 * @return the mount_dttm
	 */
	public String getMount_dttm() {
		return mount_dttm;
	}
	/**
	 * @param mount_dttm the mount_dttm to set
	 */
	public void setMount_dttm(String mount_dttm) {
		this.mount_dttm = mount_dttm;
	}
	/**
	 * @return the opr_state
	 */
	public String getOpr_state() {
		return opr_state;
	}
	/**
	 * @param opr_state the opr_state to set
	 */
	public void setOpr_state(String opr_state) {
		this.opr_state = opr_state;
	}
	/**
	 * @return the load_key_ind
	 */
	public String getLoad_key_ind() {
		return load_key_ind;
	}
	/**
	 * @param load_key_ind the load_key_ind to set
	 */
	public void setLoad_key_ind(String load_key_ind) {
		this.load_key_ind = load_key_ind;
	}
	/**
	 * @return the disc_vv_cd
	 */
	public String getDisc_vv_cd() {
		return disc_vv_cd;
	}
	/**
	 * @param disc_vv_cd the disc_vv_cd to set
	 */
	public void setDisc_vv_cd(String disc_vv_cd) {
		this.disc_vv_cd = disc_vv_cd;
	}
	/**
	 * @return the nom_load_vv_cd
	 */
	public String getNom_load_vv_cd() {
		return nom_load_vv_cd;
	}
	/**
	 * @param nom_load_vv_cd the nom_load_vv_cd to set
	 */
	public void setNom_load_vv_cd(String nom_load_vv_cd) {
		this.nom_load_vv_cd = nom_load_vv_cd;
	}

	/**
	 * @return the vsl_type_cd
	 */
	public String getVsl_type_cd() {
		return vsl_type_cd;
	}
	/**
	 * @param vsl_type_cd the vsl_type_cd to set
	 */
	public void setVsl_type_cd(String vsl_type_cd) {
		this.vsl_type_cd = vsl_type_cd;
	}
	/**
	 * @return the cargo_category_cd
	 */
	public String getCargo_category_cd() {
		return cargo_category_cd;
	}
	/**
	 * @param cargo_category_cd the cargo_category_cd to set
	 */
	public void setCargo_category_cd(String cargo_category_cd) {
		this.cargo_category_cd = cargo_category_cd;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the dwell_days
	 */
	public String getDwell_days() {
		return dwell_days;
	}
	/**
	 * @param dwell_days the dwell_days to set
	 */
	public void setDwell_days(String dwell_days) {
		this.dwell_days = dwell_days;
	}
	/**
	 * @return the agent_email
	 */
	public String getAgent_email() {
		return agent_email;
	}
	/**
	 * @param agent_email the agent_email to set
	 */
	public void setAgent_email(String agent_email) {
		this.agent_email = agent_email;
	}
	/**
	 * @return the adp_email
	 */
	public String getAdp_email() {
		return adp_email;
	}
	/**
	 * @param adp_email the adp_email to set
	 */
	public void setAdp_email(String adp_email) {
		this.adp_email = adp_email;
	}
	/**
	 * @return the ta_email
	 */
	public String getTa_email() {
		return ta_email;
	}
	/**
	 * @param ta_email the ta_email to set
	 */
	public void setTa_email(String ta_email) {
		this.ta_email = ta_email;
	}

	/**
	 * @return the add_l1
	 */
	public String getAdd_l1() {
		return add_l1;
	}
	/**
	 * @param add_l1 the add_l1 to set
	 */
	public void setAdd_l1(String add_l1) {
		this.add_l1 = add_l1;
	}
	/**
	 * @return the uen_nbr
	 */
	public String getUen_nbr() {
		return uen_nbr;
	}
	/**
	 * @param uen_nbr the uen_nbr to set
	 */
	public void setUen_nbr(String uen_nbr) {
		this.uen_nbr = uen_nbr;
	}
	/**
	 * @return the tdb_cr_nbr
	 */
	public String getTdb_cr_nbr() {
		return tdb_cr_nbr;
	}
	/**
	 * @param tdb_cr_nbr the tdb_cr_nbr to set
	 */
	public void setTdb_cr_nbr(String tdb_cr_nbr) {
		this.tdb_cr_nbr = tdb_cr_nbr;
	}
	/**
	 * @return the total_esn_wt
	 */
	public String getTotal_esn_wt() {
		return total_esn_wt;
	}
	/**
	 * @param total_esn_wt the total_esn_wt to set
	 */
	public void setTotal_esn_wt(String total_esn_wt) {
		this.total_esn_wt = total_esn_wt;
	}
	/**
	 * @return the total_esn_vol
	 */
	public String getTotal_esn_vol() {
		return total_esn_vol;
	}
	/**
	 * @param total_esn_vol the total_esn_vol to set
	 */
	public void setTotal_esn_vol(String total_esn_vol) {
		this.total_esn_vol = total_esn_vol;
	}
	/**
	 * @return the lyingPkgs
	 */
	public String getLyingPkgs() {
		return lyingPkgs;
	}
	/**
	 * @param lyingPkgs the lyingPkgs to set
	 */
	public void setLyingPkgs(String lyingPkgs) {
		this.lyingPkgs = lyingPkgs;
	}
	/**
	 * @return the lyingDays
	 */
	public String getLyingDays() {
		return lyingDays;
	}
	/**
	 * @param lyingDays the lyingDays to set
	 */
	public void setLyingDays(String lyingDays) {
		this.lyingDays = lyingDays;
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
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}
	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	/**
	 * @return the edoShutOutPkgs
	 */
	public String getEdoShutOutPkgs() {
		return edoShutOutPkgs;
	}
	/**
	 * @param edoShutOutPkgs the edoShutOutPkgs to set
	 */
	public void setEdoShutOutPkgs(String edoShutOutPkgs) {
		this.edoShutOutPkgs = edoShutOutPkgs;
	}
	/**
	 * @return the shutOutPkgs
	 */
	public String getShutOutPkgs() {
		return shutOutPkgs;
	}
	/**
	 * @param shutOutPkgs the shutOutPkgs to set
	 */
	public void setShutOutPkgs(String shutOutPkgs) {
		this.shutOutPkgs = shutOutPkgs;
	}
	/**
	 * @return the shutout_delivery_pkgs
	 */
	public String getShutout_delivery_pkgs() {
		return shutout_delivery_pkgs;
	}
	/**
	 * @param shutout_delivery_pkgs the shutout_delivery_pkgs to set
	 */
	public void setShutout_delivery_pkgs(String shutout_delivery_pkgs) {
		this.shutout_delivery_pkgs = shutout_delivery_pkgs;
	}
	/**
	 * @return the trucker_name
	 */
	public String getTrucker_name() {
		return trucker_name;
	}
	/**
	 * @param trucker_name the trucker_name to set
	 */
	public void setTrucker_name(String trucker_name) {
		this.trucker_name = trucker_name;
	}
	public String getGate_in_dttm() {
		return gate_in_dttm;
	}
	public void setGate_in_dttm(String gate_in_dttm) {
		this.gate_in_dttm = gate_in_dttm;
	}
	public String getDay_in_port() {
		return day_in_port;
	}
	public void setDay_in_port(String day_in_port) {
		this.day_in_port = day_in_port;
	}
	/**
	 * @return the maxCargoTon
	 */
	public Double getMaxCargoTon() {
		return maxCargoTon;
	}
	/**
	 * @param maxCargoTon the maxCargoTon to set
	 */
	public void setMaxCargoTon(Double maxCargoTon) {
		this.maxCargoTon = maxCargoTon;
	}
	/**
	 * @return the trans_dn_nbr_pkgs
	 */
	public Integer getTrans_dn_nbr_pkgs() {
		return trans_dn_nbr_pkgs;
	}
	/**
	 * @param trans_dn_nbr_pkgs the trans_dn_nbr_pkgs to set
	 */
	public void setTrans_dn_nbr_pkgs(Integer trans_dn_nbr_pkgs) {
		this.trans_dn_nbr_pkgs = trans_dn_nbr_pkgs;
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

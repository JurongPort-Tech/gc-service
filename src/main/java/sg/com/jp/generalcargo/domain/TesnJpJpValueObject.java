package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TesnJpJpValueObject  implements TopsIObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String terminal = null;
	String subScheme = null;
	String gcOperations = null;
	String inTerminal = null;
	String inScheme = null;
	String inSubScheme = null;
	String ingcOperations = null;
    String esn_asn_nbr=null;
    String edo_asn_nbr=null;
    String bk_ref_nbr=null;
    String in_vsl_nm=null;
    String in_voy_nbr=null;
    String out_vsl_nm=null;
    String out_voy_nbr=null;
    String in_voy_var_nbr = null;
    String out_voy_var_nbr = null;
    String mft_seq_nbr = null;
    String cargo_type=null;
    String cargo_type_nm=null;
    String pkg_type = null;
    String pkg_type_nm = null;
    String hs_code = null;
    String crg_desc = null;
    String old_mark = null;
    String num_pkgs = null;
    String port_dis_nm = null;
    String port_dis_cd = null;
    String shipper_nm=null;
    String acct_nbr = null;
    String bill_party = null;
    String ld_ind=null;
    String dg_ind = null;
    String cntr_type=null;
    String cntr_size=null;
    String cont1=null;
    String cont2=null;
    String cont3=null;
    String cont4=null;
    String pay_mode = null;
    String acc_num = null;
    String trns_nbr_pkgs = null;
    String edo_nbr_pkgs = null;
    String nom_wt= null;
    String nom_vol=null;
    String bk_nbr_pkgs=null;
    String bk_wt = null;
    String bk_vol = null;
    String variance_pkgs = null;
    String variance_wt = null;
    String variance_vol = null;
    String dn_nbr_pkgs = null;
    String stuffind = null;
    String edoAcctNo = null;
	//add by Zhenguo Deng on 14/02/2011 for Cargo Category
    String category = null;        String categoryView = null;
    String caCustCd = null;
    //add by VietNguyen on 20/03/2014 for Scheme
    String scheme = null;
    String edo_nom_wt = null;
    String edo_nom_vol = null;
    
    String create_user = null;
    String modify_dttm = null;
    String crg_status = null;
    
    public TesnJpJpValueObject()
    {
    }

    public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
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

	public String getInTerminal() {
		return inTerminal;
	}

	public void setInTerminal(String inTerminal) {
		this.inTerminal = inTerminal;
	}

	public String getInScheme() {
		return inScheme;
	}

	public void setInScheme(String inScheme) {
		this.inScheme = inScheme;
	}

	public String getInSubScheme() {
		return inSubScheme;
	}

	public void setInSubScheme(String inSubScheme) {
		this.inSubScheme = inSubScheme;
	}

	public String getIngcOperations() {
		return ingcOperations;
	}

	public void setIngcOperations(String ingcOperations) {
		this.ingcOperations = ingcOperations;
	}

	public String getStuffind() {
		return stuffind;
	}

	public void setStuffind(String stuffind) {
		this.stuffind = stuffind;
	}

	public void setEsn_asn_nbr(String s)
    {
            this.esn_asn_nbr = s;
    }

    public String getEsn_asn_nbr()
    {
            return esn_asn_nbr ;
    }

    public void setEdo_asn_nbr(String s)
    {
            this.edo_asn_nbr = s;
    }
    public String getEdo_asn_nbr()
    {
            return edo_asn_nbr ;
    }

    public void setBk_ref_nbr(String s)
    {
            this.bk_ref_nbr = s;
    }

    public String getBk_ref_nbr()
    {
            return bk_ref_nbr ;
    }


    public void setIn_vsl_nm(String s)
    {
            this.in_vsl_nm = s;
    }
    public String getIn_vsl_nm()
    {
            return in_vsl_nm;
    }

    public void setIn_voy_nbr(String s)
    {
            this.in_voy_nbr = s;
    }
    public String getIn_voy_nbr()
    {
            return in_voy_nbr;
    }

    public void setIn_voy_var_nbr(String s)
    {
            this.in_voy_var_nbr = s;
    }
    public String getIn_voy_var_nbr()
    {
            return in_voy_var_nbr;
    }

    public void setOut_vsl_nm(String s)
    {
            this.out_vsl_nm = s;
    }
    public String getOut_vsl_nm()
    {
            return out_vsl_nm;
    }

    public void setOut_voy_nbr(String s)
    {
            this.out_voy_nbr = s;
    }
    public String getOut_voy_nbr()
    {
            return out_voy_nbr;
    }

    public void setOut_voy_var_nbr(String s)
    {
            this.out_voy_var_nbr = s;
    }
    public String getOut_voy_var_nbr()
    {
            return out_voy_var_nbr;
    }

    public void setMft_seq_nbr(String s)
    {
            this.mft_seq_nbr = s;
    }
    public String getMft_seq_nbr()
    {
            return mft_seq_nbr;
    }

    public void setNum_pkgs(String s)
    {
            this.num_pkgs = s;
    }

    public String getNum_pkgs()
    {
            return num_pkgs;
    }

    public void setCrg_desc(String s)
    {
            this.crg_desc = s;
    }
    public String getCrg_desc()
    {
            return crg_desc;
    }

    public void setCargo_type(String s)
    {
            this.cargo_type = s;
    }
    public String getCargo_type()
    {
            return cargo_type;
    }

    public void setCargo_type_nm(String s)
    {
            this.cargo_type_nm= s;
    }
    public String getCargo_type_nm()
    {
            return cargo_type_nm;
    }


    public void setPkg_type(String s)
    {
            this.pkg_type = s;
    }
    public String getPkg_type()
    {
            return pkg_type;
    }

    public void setPkg_type_nm(String s)
    {
            this.pkg_type_nm = s;
    }
    public String getPkg_type_nm()
    {
            return pkg_type_nm;
    }

    public void setHs_code(String s)
    {
            this.hs_code = s;
    }
    public String getHs_code()
    {
            return hs_code;
    }

    public void setOld_mark(String s)
    {
            this.old_mark = s;
    }
    public String getOld_mark()
    {
            return old_mark;
    }

    public void setPort_dis_nm(String s)
    {
            this.port_dis_nm = s;
    }
    public String getPort_dis_nm()
    {
            return port_dis_nm;
    }

    public void setPort_dis_cd(String s)
    {
            this.port_dis_cd = s;
    }
    public String getPort_dis_cd()
    {
            return port_dis_cd;
    }

    public void setShipper_nm(String s)
    {
            this.shipper_nm = s;
    }
    public String getShipper_nm()
    {
            return shipper_nm;
    }

    public void setAcct_nbr(String s)
    {
            this.acct_nbr = s;
    }
    public String getAcct_nbr()
    {
            return acct_nbr;
    }

    public void setBill_party(String s)
    {
            this.bill_party = s;
    }
    public String getBill_party()
    {
            return bill_party;
    }

    public void setLd_ind(String s)
    {
            this.ld_ind= s;
    }
    public String getLd_ind()
    {
            return ld_ind;
    }

    public void setDg_ind(String s)
    {
            this.dg_ind = s;
    }
    public String getDg_ind()
    {
            return dg_ind;
    }

    public void setCntr_type(String s)
    {
            this.cntr_type = s;
    }
    public String getCntr_type()
    {
            return cntr_type;
    }

    public void setCntr_size(String s)
    {
            this.cntr_size = s;
    }
    public String getCntr_size()
    {
            return cntr_size;
    }

    public void setCont1(String s)
    {
            this.cont1 = s;
    }
    public String getCont1()
    {
            return cont1;
    }

    public void setCont2(String s)
    {
            this.cont2 = s;
    }
    public String getCont2()
    {
            return cont2;
    }

    public void setCont3(String s)
    {
            this.cont3 = s;
    }
    public String getCont3()
    {
            return cont3;
    }

    public void setCont4(String s)
    {
            this.cont4 = s;
    }
    public String getCont4()
    {
            return cont4;
    }

    public void setPay_mode(String s)
    {
            this.pay_mode = s;
    }
    public String getPay_mode()
    {
            return pay_mode;
    }

    public void setAcc_num(String s)
    {
            this.acc_num = s;
    }
    public String getAcc_num()
    {
            return acc_num;
    }

    public void setTrns_nbr_pkgs(String s)
    {
            this.trns_nbr_pkgs = s;
    }
    public String getTrns_nbr_pkgs()
    {
            return trns_nbr_pkgs;
    }

    public void setEdo_nbr_pkgs(String s)
    {
            this.edo_nbr_pkgs = s;
    }
    public String getEdo_nbr_pkgs()
    {
            return edo_nbr_pkgs;
    }

    public void setNom_wt(String s)
    {
            this.nom_wt = s;
    }
    public String getNom_wt()
    {
            return nom_wt;
    }

    public void setNom_vol(String s)
    {
            this.nom_vol = s;
    }
    public String getNom_vol()
    {
            return nom_vol;
    }

    public void setBk_nbr_pkgs(String s)
    {
            this.bk_nbr_pkgs = s;
    }
    public String getBk_nbr_pkgs()
    {
            return bk_nbr_pkgs;
    }

    public void setBk_wt(String s)
    {
            this.bk_wt = s;
    }
    public String getBk_wt()
    {
            return bk_wt;
    }

    public void setBk_vol(String s)
    {
            this.bk_vol = s;
    }
    public String getBk_vol()
    {
            return bk_vol;
    }

    public void setVariance_pkgs(String s)
    {
            this.variance_pkgs = s;
    }
    public String getVariance_pkgs()
    {
            return variance_pkgs;
    }

    public void setVariance_wt(String s)
    {
            this.variance_wt = s;
    }
    public String getVariance_wt()
    {
            return variance_wt;
    }

    public void setVariance_vol(String s)
    {
            this.variance_vol = s;
    }
    public String getVariance_vol()
    {
            return variance_vol;
    }
    public void setDn_nbr_pkgs(String s)
    {
            this.dn_nbr_pkgs = s;
    }
    public String getDn_nbr_pkgs()
    {
            return dn_nbr_pkgs;
    }

    public void setStuffInd(String stuffind)
    {
            this.stuffind = stuffind;
    }
    public String getStuffInd()
    {
            return stuffind;
    }
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryView() {
		return categoryView;
	}

	public void setCategoryView(String categoryView) {
		this.categoryView = categoryView;
	}


	public String getEdoAcctNo()
	{
		return edoAcctNo;
	}

	public void setEdoAcctNo(String edoAcctNo)
	{
		this.edoAcctNo = edoAcctNo;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getEdo_nom_wt() {
		return edo_nom_wt;
	}

	public void setEdo_nom_wt(String edo_nom_wt) {
		this.edo_nom_wt = edo_nom_wt;
	}

	public String getEdo_nom_vol() {
		return edo_nom_vol;
	}

	public void setEdo_nom_vol(String edo_nom_vol) {
		this.edo_nom_vol = edo_nom_vol;
	}

	/**
	 * @return the create_user
	 */
	public String getCreate_user() {
		return create_user;
	}

	/**
	 * @param create_user the create_user to set
	 */
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}

	/**
	 * @return the modify_dttm
	 */
	public String getModify_dttm() {
		return modify_dttm;
	}

	/**
	 * @param modify_dttm the modify_dttm to set
	 */
	public void setModify_dttm(String modify_dttm) {
		this.modify_dttm = modify_dttm;
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
    public String getCaCustCd() {
        return caCustCd;
    }
    public void setCaCustCd(String caCustCd) {
        this.caCustCd = caCustCd;
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

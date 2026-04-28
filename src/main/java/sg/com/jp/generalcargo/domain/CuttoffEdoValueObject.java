package sg.com.jp.generalcargo.domain;

import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CuttoffEdoValueObject implements TopsIObject{

	private static final long serialVersionUID = -8311001015262968358L;

	public CuttoffEdoValueObject()
    {
        container_details = new Vector();
    }

    public String getVarNbr()
    {
        return var_nbr;
    }

    public void setVarNbr(String s)
    {
        var_nbr = s;
    }

    public String getVslNbr()
    {
        return vsl_nm;
    }

    public void setVslNm(String s)
    {
        vsl_nm = s;
    }

    public String getInVoyNbr()
    {
        return in_voy_nbr;
    }

    public void setInVoyNbr(String s)
    {
        in_voy_nbr = s;
    }

    public String getEdoAsnNbr()
    {
        return edo_asn_nbr;
    }

    public void setEdoAsnNbr(String s)
    {
        edo_asn_nbr = s;
    }

    public String getCrgDes()
    {
        return crg_des;
    }

    public void setCrgDes(String s)
    {
        crg_des = s;
    }

    public String getCrgTypeNm()
    {
        return crg_type_nm;
    }

    public void setCrgTypeNm(String s)
    {
        crg_type_nm = s;
    }

    public String getAdpCustCd()
    {
        return adp_cust_cd;
    }

    public void setAdpCustCd(String s)
    {
        adp_cust_cd = s;
    }

    public String getCrgTypeCd()
    {
        return crg_type_cd;
    }

    public void setCrgTypeCd(String s)
    {
        crg_type_cd = s;
    }

    public String getHsCode()
    {
        return hs_code;
    }

    public void setHsCode(String s)
    {
        hs_code = s;
    }

    public String getMftMarkings()
    {
        return mft_markings;
    }

    public void setMftMarkings(String s)
    {
        mft_markings = s;
    }

    public String getCrgStatus()
    {
        return crg_status;
    }

    public void setCrgStatus(String s)
    {
        crg_status = s;
    }

    public String getEdoStatus()
    {
        return edo_status;
    }

    public void setEdoStatus(String s)
    {
        edo_status = s;
    }

    public String getDgInd()
    {
        return dg_ind;
    }

    public void setDgInd(String s)
    {
        dg_ind = s;
    }

    public String getStgType()
    {
        return stg_type;
    }

    public void setStgType(String s)
    {
        stg_type = s;
    }

    public String getDisType()
    {
        return dis_type;
    }

    public void setDisType(String s)
    {
        dis_type = s;
    }

    public String getPkgTypeCd()
    {
        return pkg_type_cd;
    }

    public void setPkgTypeCd(String s)
    {
        pkg_type_cd = s;
    }

    public String getPkgTypeDesc()
    {
        return pkg_type_desc;
    }

    public void setPkgTypeDesc(String s)
    {
        pkg_type_desc = s;
    }

    public String getNbrPkgs()
    {
        return nbr_pkgs;
    }

    public void setNbrPkgs(String s)
    {
        nbr_pkgs = s;
    }

    public String getBlNbr()
    {
        return bl_nbr;
    }

    public void setBlNbr(String s)
    {
        bl_nbr = s;
    }

    public String getMftSeqNbr()
    {
        return mft_seq_nbr;
    }

    public void setMftSeqNbr(String s)
    {
        mft_seq_nbr = s;
    }

    public String getAdpNbr()
    {
        return adp_nbr;
    }

    public void setAdpNbr(String s)
    {
        adp_nbr = s;
    }

    public String getAdpNm()
    {
        return adp_nm;
    }

    public void setAdpNm(String s)
    {
        adp_nm = s;
    }

    public String getNomWeight()
    {
        return nom_weight;
    }

    public void setNomWeight(String s)
    {
        nom_weight = s;
    }

    public String getNomVolume()
    {
        return nom_volume;
    }

    public void setNomVolume(String s)
    {
        nom_volume = s;
    }

    public String getCrgAgtNbr()
    {
        return crg_agt_nbr;
    }

    public void setCrgAgtNbr(String s)
    {
        crg_agt_nbr = s;
    }

    public String getCrgAgtNm()
    {
        return crg_agt_nm;
    }

    public void setCrgAgtNm(String s)
    {
        crg_agt_nm = s;
    }

    public String getAgtAttNbr()
    {
        return agt_att_nbr;
    }

    public void setAgtAttNbr(String s)
    {
        agt_att_nbr = s;
    }

    public String getAgtAttNm()
    {
        return agt_att_nm;
    }

    public void setAgtAttNm(String s)
    {
        agt_att_nm = s;
    }

    public String getDeliveryTo()
    {
        return delivery_to;
    }

    public void setDeliveryTo(String s)
    {
        delivery_to = s;
    }

    public String getDisOprInd()
    {
        return dis_opr_ind;
    }

    public void setDisOprInd(String s)
    {
        dis_opr_ind = s;
    }

    public String getAcctNbr()
    {
        return acct_nbr;
    }

    public void setAcctNbr(String s)
    {
        acct_nbr = s;
    }

    public String getConsNm()
    {
        return cons_nm;
    }

    public void setConsNm(String s)
    {
        cons_nm = s;
    }

    public String getDnNbrPkgs()
    {
        return dn_nbr_pkgs;
    }

    public void setDnNbrPkgs(String s)
    {
        dn_nbr_pkgs = s;
    }

    public String getTransNbrPkgs()
    {
        return trans_nbr_pkgs;
    }

    public void setTransNbrPkgs(String s)
    {
        trans_nbr_pkgs = s;
    }

    public String getTransDnNbrPkgs()
    {
        return trans_dn_nbr_pkgs;
    }

    public void setTransDnNbrPkgs(String s)
    {
        trans_dn_nbr_pkgs = s;
    }

    public String getEdoNbrPkgs()
    {
        return edo_nbr_pkgs;
    }

    public void setEdoNbrPkgs(String s)
    {
        edo_nbr_pkgs = s;
    }

    public List<String> getContinerDetails()
    {
        return container_details;
    }

    public void setContinerDetails(List<String> containerList)
    {
        container_details = containerList;
    }

    public String getAppointedAdpCustCd()
    {
        return appointed_adp_cust_cd;
    }

    public void setAppointedAdpCustCd(String s)
    {
        appointed_adp_cust_cd = s;
    }

    public String getAppointedAdpIcTdbcrNbr()
    {
        return appointed_adp_ic_tdbcr_nbr;
    }

    public void setAppointedAdpIcTdbcrNbr(String s)
    {
        appointed_adp_ic_tdbcr_nbr = s;
    }

    public String getAppointedAdpNm()
    {
        return appointed_adp_nm;
    }

    public void setAppointedAdpNm(String s)
    {
        appointed_adp_nm = s;
    }

    public String getWhInd()
    {
        return wh_ind;
    }

    public void setWhInd(String s)
    {
        wh_ind = s;
    }

    public String getWhAggrNbr()
    {
        return wh_aggr_nbr;
    }

    public void setWhAggrNbr(String s)
    {
        wh_aggr_nbr = s;
    }

    public String getWhRemarks()
    {
        return wh_remarks;
    }

    public void setWhRemarks(String s)
    {
        wh_remarks = s;
    }

    public String getFreeStgDays()
    {
        return free_stg_days;
    }

    public void setFreeStgDays(String s)
    {
        free_stg_days = s;
    }

    public String getStfInd()
    {
        return strStfInd;
    }
    public void setStfInd(String s)
    {
        strStfInd = s;
    }

    private String var_nbr;
    private String vsl_nm;
    private String in_voy_nbr;
    private String edo_asn_nbr;
    private String crg_des;
    private String crg_type_nm;
    private String adp_cust_cd;
    private String crg_type_cd;
    private String hs_code;
    private String mft_markings;
    private String crg_status;
    private String edo_status;
    private String dg_ind;
    private String stg_type;
    private String dis_type;
    private String pkg_type_cd;
    private String pkg_type_desc;
    private String nbr_pkgs;
    private String bl_nbr;
    private String mft_seq_nbr;
    private String adp_nbr;
    private String adp_nm;
    private String nom_weight;
    private String nom_volume;
    private String crg_agt_nbr;
    private String crg_agt_nm;
    private String agt_att_nbr;
    private String agt_att_nm;
    private String delivery_to;
    private String dis_opr_ind;
    private String acct_nbr;
    private String service_other_acct_nbr;
    private String cons_nm;
    private String dn_nbr_pkgs;
    private String trans_nbr_pkgs;
    private String trans_dn_nbr_pkgs;
    private String edo_nbr_pkgs;
    private List<String> container_details;
    private String appointed_adp_cust_cd;
    private String appointed_adp_ic_tdbcr_nbr;
    private String appointed_adp_nm;
    private String wh_ind;
    private String free_stg_days;
    private String wh_remarks;
    private String wh_aggr_nbr;
    private String strStfInd;

    private String unstuffInd;
    private String crgCategoryCd;
    private String crgCategoryName;

    //add by Zhenguo Deng(harbor) on 08/06/2011
    private String esnpkgs;
    private String esnpkgs_wt;
    private String esnpkgs_vol;
    private String shutoutpkgs;
    private String shutoutpkgs_wt;
    private String shutoutpkgs_vol;
    private String outstandingpkgs;
    private String outstandingpkgs_wt;
    private String outstandingpkgs_vol;
    private String esnAsnNbr;
    private String esnDgInd;
    private String stgInd;
    private String outVoyNbr;
    private String bkNbr;
    private String shipperNm;
    private String stuffInd;

	//add by hujun on 17/8/2011
    private String maxEdoPkgs;
    private String maxEdoPkgs_wt;
    private String maxEdoPkgs_vol;
	//add end

    // HaiTTH1 added on 14/1/2014
    private String arrival;
    private String departure;
    private String cod_dttm;
    private String etb_dttm;
    private String deliver_pkgs;
    private String scheme;
    private String short_landed_pkgs;
    private String dn_nbr;
    private String acct_nm;
    private String adp_nbr_pkgs;

    //MCC added for EPC area
    private String deliveryToEPC;

	public String getDeliveryToEPC() {
        return deliveryToEPC;
    }

    public void setDeliveryToEPC(String deliveryToEPC) {
        this.deliveryToEPC = deliveryToEPC;
    }

    public String getEsnpkgs() {
		return esnpkgs;
	}

	public void setEsnpkgs(String esnpkgs) {
		this.esnpkgs = esnpkgs;
	}

	public String getEsnpkgs_wt() {
		return esnpkgs_wt;
	}

	public void setEsnpkgs_wt(String esnpkgsWt) {
		esnpkgs_wt = esnpkgsWt;
	}

	public String getEsnpkgs_vol() {
		return esnpkgs_vol;
	}

	public void setEsnpkgs_vol(String esnpkgsVol) {
		esnpkgs_vol = esnpkgsVol;
	}

	public String getShutoutpkgs() {
		return shutoutpkgs;
	}

	public void setShutoutpkgs(String shutoutpkgs) {
		this.shutoutpkgs = shutoutpkgs;
	}

	public String getShutoutpkgs_wt() {
		return shutoutpkgs_wt;
	}

	public void setShutoutpkgs_wt(String shutoutpkgsWt) {
		shutoutpkgs_wt = shutoutpkgsWt;
	}

	public String getShutoutpkgs_vol() {
		return shutoutpkgs_vol;
	}

	public void setShutoutpkgs_vol(String shutoutpkgsVol) {
		shutoutpkgs_vol = shutoutpkgsVol;
	}

	public String getOutstandingpkgs() {
		return outstandingpkgs;
	}

	public void setOutstandingpkgs(String outstandingpkgs) {
		this.outstandingpkgs = outstandingpkgs;
	}

	public String getOutstandingpkgs_wt() {
		return outstandingpkgs_wt;
	}

	public void setOutstandingpkgs_wt(String outstandingpkgsWt) {
		outstandingpkgs_wt = outstandingpkgsWt;
	}

	public String getOutstandingpkgs_vol() {
		return outstandingpkgs_vol;
	}

	public void setOutstandingpkgs_vol(String outstandingpkgsVol) {
		outstandingpkgs_vol = outstandingpkgsVol;
	}

	public String getEsnAsnNbr() {
		return esnAsnNbr;
	}

	public void setEsnAsnNbr(String esnAsnNbr) {
		this.esnAsnNbr = esnAsnNbr;
	}

	public String getEsnDgInd() {
		return esnDgInd;
	}

	public void setEsnDgInd(String esnDgInd) {
		this.esnDgInd = esnDgInd;
	}

	public String getStgInd() {
		return stgInd;
	}

	public void setStgInd(String stgInd) {
		this.stgInd = stgInd;
	}

	public String getVsl_nm() {
		return vsl_nm;
	}
	public String getStuffInd() {
		return stuffInd;
	}

	public void setStuffInd(String stuffInd) {
		this.stuffInd = stuffInd;
	}

	public String getShipperNm() {
		return shipperNm;
	}

	public void setShipperNm(String shipperNm) {
		this.shipperNm = shipperNm;
	}

	public String getBkNbr() {
		return bkNbr;
	}

	public void setBkNbr(String bkNbr) {
		this.bkNbr = bkNbr;
	}

	public String getOutVoyNbr() {
		return outVoyNbr;
	}

	public void setOutVoyNbr(String outVoyNbr) {
		this.outVoyNbr = outVoyNbr;
	}

    //end add by Zhenguo Deng(harbor) on 08/06/2011

	//add by hujun on 17/8/2011
	public String getMaxEdoPkgs() {
		return maxEdoPkgs;
	}

	public void setMaxEdoPkgs(String maxEdoPkgs) {
		this.maxEdoPkgs = maxEdoPkgs;
	}

	public String getMaxEdoPkgs_wt() {
		return maxEdoPkgs_wt;
	}

	public void setMaxEdoPkgs_wt(String maxEdoPkgs_wt) {
		this.maxEdoPkgs_wt = maxEdoPkgs_wt;
	}

	public String getMaxEdoPkgs_vol() {
		return maxEdoPkgs_vol;
	}

	public void setMaxEdoPkgs_vol(String maxEdoPkgs_vol) {
		this.maxEdoPkgs_vol = maxEdoPkgs_vol;
	}
	//add end

	public String getCrgCategoryCd() {
		return crgCategoryCd;
	}

	public void setCrgCategoryCd(String crgCategoryCd) {
		this.crgCategoryCd = crgCategoryCd;
	}

	public String getCrgCategoryName() {
		return crgCategoryName;
	}

	public void setCrgCategoryName(String crgCategoryName) {
		this.crgCategoryName = crgCategoryName;
	}

	public String getUnstuffInd() {
		return unstuffInd;
	}

	public void setUnstuffInd(String unstuffInd) {
		this.unstuffInd = unstuffInd;
	}

	/**
	 * @return the arrival
	 */
	public String getArrival() {
		return arrival;
	}

	/**
	 * @param arrival the arrival to set
	 */
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	/**
	 * @return the departure
	 */
	public String getDeparture() {
		return departure;
	}

	/**
	 * @param departure the departure to set
	 */
	public void setDeparture(String departure) {
		this.departure = departure;
	}

	/**
	 * @return the cod_dttm
	 */
	public String getCod_dttm() {
		return cod_dttm;
	}

	/**
	 * @param cod_dttm the cod_dttm to set
	 */
	public void setCod_dttm(String cod_dttm) {
		this.cod_dttm = cod_dttm;
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
	 * @return the deliver_pkgs
	 */
	public String getDeliver_pkgs() {
		return deliver_pkgs;
	}

	/**
	 * @param deliver_pkgs the deliver_pkgs to set
	 */
	public void setDeliver_pkgs(String deliver_pkgs) {
		this.deliver_pkgs = deliver_pkgs;
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
	 * @return the short_landed_pkgs
	 */
	public String getShort_landed_pkgs() {
		return short_landed_pkgs;
	}

	/**
	 * @param short_landed_pkgs the short_landed_pkgs to set
	 */
	public void setShort_landed_pkgs(String short_landed_pkgs) {
		this.short_landed_pkgs = short_landed_pkgs;
	}

	/**
	 * @return the dn_nbr
	 */
	public String getDn_nbr() {
		return dn_nbr;
	}

	/**
	 * @param dn_nbr the dn_nbr to set
	 */
	public void setDn_nbr(String dn_nbr) {
		this.dn_nbr = dn_nbr;
	}

	/**
	 * @return the acct_nm
	 */
	public String getAcct_nm() {
		return acct_nm;
	}

	/**
	 * @param acct_nm the acct_nm to set
	 */
	public void setAcct_nm(String acct_nm) {
		this.acct_nm = acct_nm;
	}

	public String getService_other_acct_nbr() {
        return service_other_acct_nbr;
    }

    public void setService_other_acct_nbr(String serviceOtherAcctNbr) {
        service_other_acct_nbr = serviceOtherAcctNbr;
    }

    /**
	 * @return the adp_nbr_pkgs
	 */
	public String getAdp_nbr_pkgs() {
		return adp_nbr_pkgs;
	}

	/**
	 * @param adp_nbr_pkgs the adp_nbr_pkgs to set
	 */
	public void setAdp_nbr_pkgs(String adp_nbr_pkgs) {
		this.adp_nbr_pkgs = adp_nbr_pkgs;
	}

	
	//Begin ThanhPT6, CR of JPOnline and SMART Enhancement, 06/01/2016
	private String taUenNo;
	private String taCCode;
	private String taNmByJP;

	public String getTaUenNo() {
		return taUenNo;
	}

	public void setTaUenNo(String taUenNo) {
		this.taUenNo = taUenNo;
	}

	public String getTaCCode() {
		return taCCode;
	}

	public void setTaCCode(String taCCode) {
		this.taCCode = taCCode;
	}

	public String getTaNmByJP() {
		return taNmByJP;
	}

	public void setTaNmByJP(String taNmByJP) {
		this.taNmByJP = taNmByJP;
	}
	//End ThanhPT6
	
	private String terminal;
	private String subScheme;
	private String gcOperations;
	
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
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtil;
@JsonInclude(value = Include.NON_NULL)
public class CargoEnquiryDetails {
	//Cargo information section
	private String edo_asn_nbr;
	private String bl_nbr;
	private Long edo_asn_pkgs;
	private BigDecimal weight;
	private String hs_code;
	private String hs_sub_code;
	private String crg_des;
	private String dg_ind;
	private String imo_cl;
	private String stg_type;
	private Long ucr_nbr;
	private String mft_seq_nbr;

	private Date creation_dttm;
	private String esn_asn_nbr;
	private String bk_ref_nbr;
	private Long esn_asn_pkgs;
	private BigDecimal nom_vol;
	private String shutout_ind;
	private String wh_aggr_nbr;
	
	//Shipment information section
	private String disc_gate_way;
	private String disc_terminal;
	private String disc_scheme;
	private String disc_vsl;
	private Timestamp disc_arrival;
	private String disc_berth;
	private String disc_status;
	private String disc_oper;
	private String jp_yard_location;
	private Timestamp completetion_disc;
	private String disc_free_store_rent_expiry;
	private BigDecimal disc_store_rent_amount;
	private String port_load;
	private String remarks;
	
	private String load_gate_way;
	private String load_terminal;
	private String load_scheme;
	private String load_vsl;
	private Timestamp load_arrival;
	private String load_berth;
	private String shipment_status;
	private String load_oper;
	private Timestamp completetion_load;
	private String load_free_store_rent_expiry;
	private BigDecimal load_store_rent_amount;
	private String port_disc;
	
	private String asn_status;

	private Collection<DNUADetail> dnList;
	private Collection<DNUADetail> uaList;
	private Collection<DNUADetail> orgUAList;
	private Collection<AsnHistory> asnHistoryList;
	
	private Date firstUa;
	private String disc_vv_cd;
	private String load_vv_cd;
	
	private String org_esn_nbr;
	private String org_esn_pkgs;
	private String org_balance_ua_pkgs;
	private String org_ua_pkgs;
	
	//check JP
	private Boolean isUser;
	
	public Boolean getIsUser() {
		return isUser;
	}
	public void setIsUser(Boolean isUser) {
		this.isUser = isUser;
	}
	public String getEdo_asn_nbr() {
		return edo_asn_nbr;
	}
	public void setEdo_asn_nbr(String edo_asn_nbr) {
		this.edo_asn_nbr = edo_asn_nbr;
	}
	public String getBl_nbr() {
		return bl_nbr;
	}
	public void setBl_nbr(String bl_nbr) {
		this.bl_nbr = bl_nbr;
	}
	public Long getEdo_asn_pkgs() {
		return edo_asn_pkgs;
	}
	public void setEdo_asn_pkgs(Long edo_asn_pkgs) {
		this.edo_asn_pkgs = edo_asn_pkgs;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getHs_code() {
		return hs_code;
	}
	public void setHs_code(String hs_code) {
		this.hs_code = hs_code;
	}
	public String getHs_sub_code() {
		return hs_sub_code;
	}
	public void setHs_sub_code(String hs_sub_code) {
		this.hs_sub_code = hs_sub_code;
	}
	public String getCrg_des() {
		return crg_des;
	}
	public void setCrg_des(String crg_des) {
		this.crg_des = crg_des;
	}
	public String getDg_ind() {
		return dg_ind;
	}
	public void setDg_ind(String dg_ind) {
		this.dg_ind = dg_ind;
	}
	public String getImo_cl() {
		return imo_cl;
	}
	public void setImo_cl(String imo_cl) {
		this.imo_cl = imo_cl;
	}
	public String getStg_type() {
		return stg_type;
	}
	public void setStg_type(String stg_type) {
		this.stg_type = stg_type;
	}
	public Long getUcr_nbr() {
		return ucr_nbr;
	}
	public void setUcr_nbr(Long ucr_nbr) {
		this.ucr_nbr = ucr_nbr;
	}
	public Date getCreation_dttm() {
		return creation_dttm;
	}
	public void setCreation_dttm(Date creation_dttm) {
		this.creation_dttm = creation_dttm;
	}
	public String getEsn_asn_nbr() {
		return esn_asn_nbr;
	}
	public void setEsn_asn_nbr(String esn_asn_nbr) {
		this.esn_asn_nbr = esn_asn_nbr;
	}
	public String getBk_ref_nbr() {
		return bk_ref_nbr;
	}
	public void setBk_ref_nbr(String bk_ref_nbr) {
		this.bk_ref_nbr = bk_ref_nbr;
	}
	public Date getFirstUa() {
		return firstUa;
	}
	public void setFirstUa(Date firstUa) {
		this.firstUa = firstUa;
	}
	public String getDisc_vv_cd() {
		return disc_vv_cd;
	}
	public void setDisc_vv_cd(String disc_vv_cd) {
		this.disc_vv_cd = disc_vv_cd;
	}
	public String getLoad_vv_cd() {
		if ("-/-/-".equalsIgnoreCase(load_vv_cd)) {
			return "";
		}
		return load_vv_cd;
	}
	public void setLoad_vv_cd(String load_vv_cd) {
		this.load_vv_cd = load_vv_cd;
	}
	public Long getEsn_asn_pkgs() {
		return esn_asn_pkgs;
	}
	public void setEsn_asn_pkgs(Long esn_asn_pkgs) {
		this.esn_asn_pkgs = esn_asn_pkgs;
	}
	public BigDecimal getNom_vol() {
		return nom_vol;
	}
	public void setNom_vol(BigDecimal nom_vol) {
		this.nom_vol = nom_vol;
	}
	public String getShutout_ind() {
		return shutout_ind;
	}
	public void setShutout_ind(String shutout_ind) {
		this.shutout_ind = shutout_ind;
	}
	public String getWh_aggr_nbr() {
		return wh_aggr_nbr;
	}
	public void setWh_aggr_nbr(String wh_aggr_nbr) {
		this.wh_aggr_nbr = wh_aggr_nbr;
	}
	public String getDisc_gate_way() {
		return disc_gate_way;
	}
	public void setDisc_gate_way(String disc_gate_way) {
		this.disc_gate_way = disc_gate_way;
	}	
	public String getDisc_terminal() {
		return disc_terminal;
	}
	public void setDisc_terminal(String disc_terminal) {
		this.disc_terminal = disc_terminal;
	}
	public String getDisc_scheme() {
		return disc_scheme;
	}
	public void setDisc_scheme(String disc_scheme) {
		this.disc_scheme = disc_scheme;
	}
	public String getDisc_vsl() {
		if ("-/-/-".equalsIgnoreCase(disc_vsl)) {
			return "";
		}
		return disc_vsl;
	}
	public void setDisc_vsl(String disc_vsl) {
		this.disc_vsl = disc_vsl;
	}
	public Timestamp getDisc_arrival() {
		return disc_arrival;
	}
	public void setDisc_arrival(Timestamp disc_arrival) {
		this.disc_arrival = disc_arrival;
	}
	public String getDisc_berth() {
		return disc_berth;
	}
	public void setDisc_berth(String disc_berth) {
		this.disc_berth = disc_berth;
	}
	public String getDisc_status() {
		return disc_status;
	}
	public void setDisc_status(String disc_status) {
		this.disc_status = disc_status;
	}
	public String getDisc_oper() {
		return disc_oper;
	}
	public void setDisc_oper(String disc_oper) {
		this.disc_oper = disc_oper;
	}
	public String getJp_yard_location() {
		return jp_yard_location;
	}
	public void setJp_yard_location(String jp_yard_location) {
		this.jp_yard_location = jp_yard_location;
	}
	public Timestamp getCompletetion_disc() {
		return completetion_disc;
	}
	public void setCompletetion_disc(Timestamp completetion_disc) {
		this.completetion_disc = completetion_disc;
	}
	public String getDisc_free_store_rent_expiry() {
		return disc_free_store_rent_expiry;
	}
	public void setDisc_free_store_rent_expiry(String disc_free_store_rent_expiry) {
		this.disc_free_store_rent_expiry = disc_free_store_rent_expiry;
	}
	public BigDecimal getDisc_store_rent_amount() {
		return disc_store_rent_amount;
	}
	public void setDisc_store_rent_amount(BigDecimal disc_store_rent_amount) {
		this.disc_store_rent_amount = disc_store_rent_amount;
	}
	public String getPort_load() {
		return port_load;
	}
	public void setPort_load(String port_load) {
		this.port_load = port_load;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getLoad_gate_way() {
		return load_gate_way;
	}
	public void setLoad_gate_way(String load_gate_way) {
		this.load_gate_way = load_gate_way;
	}	
	public String getLoad_terminal() {
		return load_terminal;
	}
	public void setLoad_terminal(String load_terminal) {
		this.load_terminal = load_terminal;
	}
	public String getLoad_scheme() {
		return load_scheme;
	}
	public void setLoad_scheme(String load_scheme) {
		this.load_scheme = load_scheme;
	}
	public String getLoad_vsl() {
		return load_vsl;
	}
	public void setLoad_vsl(String load_vsl) {
		this.load_vsl = load_vsl;
	}
	public Timestamp getLoad_arrival() {
		return load_arrival;
	}
	public void setLoad_arrival(Timestamp load_arrival) {
		this.load_arrival = load_arrival;
	}
	public String getLoad_berth() {
		return load_berth;
	}
	public void setLoad_berth(String load_berth) {
		this.load_berth = load_berth;
	}
	public String getShipment_status() {
		return shipment_status;
	}
	public void setShipment_status(String shipment_status) {
		this.shipment_status = shipment_status;
	}
	public String getLoad_oper() {
		return load_oper;
	}
	public void setLoad_oper(String load_oper) {
		this.load_oper = load_oper;
	}
	public String getLoad_free_store_rent_expiry() {
		return load_free_store_rent_expiry;
	}
	public void setLoad_free_store_rent_expiry(String load_free_store_rent_expiry) {
		this.load_free_store_rent_expiry = load_free_store_rent_expiry;
	}
	public BigDecimal getLoad_store_rent_amount() {
		return load_store_rent_amount;
	}
	public void setLoad_store_rent_amount(BigDecimal load_store_rent_amount) {
		this.load_store_rent_amount = load_store_rent_amount;
	}
	public String getPort_disc() {
		return port_disc;
	}
	public void setPort_disc(String port_disc) {
		this.port_disc = port_disc;
	}
	
	public String getHs_code_sub_code() {
		if (StringUtils.isEmpty(hs_code)&& StringUtils.isEmpty(hs_sub_code))
			return "";
		
		String rs = "-";
		if (StringUtils.isNotEmpty(hs_code)) {
			rs = hs_code;
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(hs_sub_code)) {
			rs = rs + hs_sub_code;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	
	public String getDg_imo_storage() {
		if (StringUtils.isEmpty(dg_ind)&& StringUtils.isEmpty(imo_cl) && StringUtils.isEmpty(stg_type))
			return "";
		
		String rs = "-";
		if (StringUtils.isNotEmpty(dg_ind)) {
			rs = dg_ind;
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(imo_cl)) {
			rs = rs + imo_cl ;
		} else {
			rs = rs + "-";
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(stg_type)) {
			rs = rs + stg_type;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	
	public String getDisc_gate_way_scheme() {
		if (StringUtils.isEmpty(disc_gate_way)&& StringUtils.isEmpty(disc_scheme))
			return "";
		
		String rs = "-";
		if (StringUtils.isNotEmpty(disc_gate_way)) {
			rs = disc_gate_way;
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(disc_scheme)) {
			rs = rs + disc_scheme;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	
	public String getLoad_gate_way_scheme() {
		if (StringUtils.isEmpty(load_gate_way)&& StringUtils.isEmpty(load_scheme))
			return "";
		
		String rs = "-";
		if (StringUtils.isNotEmpty(load_gate_way)) {
			rs = load_gate_way;
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(load_scheme)) {
			rs = rs + load_scheme;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	
	public String getDisc_arrival_berth() {
		if (disc_arrival == null && StringUtils.isEmpty(disc_berth))
			return "";
		
		String rs = "-";
		if (disc_arrival != null) {
			rs = CommonUtil.parseDBDateToStr(disc_arrival);
		} 
		
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(disc_berth)) {
			rs = rs + disc_berth;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	
	public String getLoad_arrival_berth() {
		if (load_arrival == null && StringUtils.isEmpty(load_berth))
			return "";
		
		String rs = "-";
		if (load_arrival != null) {
			rs = CommonUtil.parseDBDateToStr(load_arrival);
		}
		rs = rs + " / ";
		if (StringUtils.isNotEmpty(load_berth)) {
			rs = rs + load_berth;
		} else {
			rs = rs + "-";
		}
		return rs;
	}
	public Timestamp getCompletetion_load() {
		return completetion_load;
	}
	public void setCompletetion_load(Timestamp completetion_load) {
		this.completetion_load = completetion_load;
	}
	public Collection<DNUADetail> getDnList() {
		return dnList;
	}
	public void setDnList(Collection<DNUADetail> dnList) {
		this.dnList = dnList;
	}
	public Collection<DNUADetail> getUaList() {
		return uaList;
	}
	public void setUaList(Collection<DNUADetail> uaList) {
		this.uaList = uaList;
	}
	public Collection<AsnHistory> getAsnHistoryList() {
		return asnHistoryList;
	}
	public void setAsnHistoryList(Collection<AsnHistory> asnHistoryList) {
		this.asnHistoryList = asnHistoryList;
	}
	
	public String getCreationDttmStr() {
		if (creation_dttm == null)
			return "";
		
		return CommonUtil.formatDateTime(creation_dttm);
	}
	
	public String getCompletetionLoadStr() {
		if (completetion_load == null)
			return "";
		
		return CommonUtil.parseDBDateToStr(completetion_load);
	}
	
	public String getCompletetionDiscStr() {
		if (completetion_disc == null)
			return "";
		
		return CommonUtil.parseDBDateToStr(completetion_disc);
	}
	
	public String getDiscCrgStatus() {
		if ("Local".equalsIgnoreCase(disc_status)) {
			return "L";
		} else if ("Re-export".equalsIgnoreCase(disc_status)) {
			return "R";
		} else {
			return "T";
		}
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
	public String getOrg_esn_pkgs() {
		return org_esn_pkgs;
	}
	public void setOrg_esn_pkgs(String org_esn_pkgs) {
		this.org_esn_pkgs = org_esn_pkgs;
	}

	public String getOrg_ua_pkgs() {
		return org_ua_pkgs;
	}
	public void setOrg_ua_pkgs(String org_ua_pkgs) {
		this.org_ua_pkgs = org_ua_pkgs;
	}
	public String getOrg_esn_nbr() {
		return org_esn_nbr;
	}
	public void setOrg_esn_nbr(String org_esn_nbr) {
		this.org_esn_nbr = org_esn_nbr;
	}
	public String getOrg_balance_ua_pkgs() {
		return org_balance_ua_pkgs;
	}
	public void setOrg_balance_ua_pkgs(String org_balance_ua_pkgs) {
		this.org_balance_ua_pkgs = org_balance_ua_pkgs;
	}
	public Collection<DNUADetail> getOrgUAList() {
		return orgUAList;
	}
	public void setOrgUAList(Collection<DNUADetail> orgUAList) {
		this.orgUAList = orgUAList;
	}
    public String getAsn_status() {
        return asn_status;
    }
    public void setAsn_status(String asn_status) {
        this.asn_status = asn_status;
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

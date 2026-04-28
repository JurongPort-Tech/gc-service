package sg.com.jp.generalcargo.domain;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class DNUADetail {

	private String dn_nbr;
	private Long nbr_pkgs;
	private String dn_status;
	private Date trans_dttm;
	private String print_location;
	private Date gate_out_dttm;
	private String cash_receipt_nbr;
	private String truck_nbr;
	private String sst_machine_nbr;

	private String ua_nbr;
	private String ua_status;
	private String edo_asn_nbr;
	private Long esn_asn_nbr;
	private Long bal_pkgs;
	private String dp_ic_type;
	private BigDecimal wt;
	private BigDecimal vol;
	private String cntr_nbr;
	private Date dn_create_dttm;
	private Date ua_create_dttm;

	private String vehicle_type;
	private String lane_nbr;
	private String user_id;
	private Date scan_dttm;
	private Date exit_dttm;
	private String direction;

	private String ref_nbr;
	private Date create_dttm;

	private String createDttmStr;
	private String exitDttmStr;
	private String scanDttmStr;
	//Added by HoaBT2: get vehicle_nbr
	private String vehicle_nbr;
	//Added by HoaBT2: get hs_code
	private String hs_code;
	//BEGIN Added by FPT 08/2015 SGS project
	private String var_nbr;
	private Double in_captured_wt;
	private Double out_captured_wt;
	private String trans_type;
	private String shutout_ind;
	private String vsl_nm;
	private String in_voy_nbr;
	
	
	public String getDn_nbr() {
		return dn_nbr;
	}


	public void setDn_nbr(String dn_nbr) {
		this.dn_nbr = dn_nbr;
	}


	public Long getNbr_pkgs() {
		return nbr_pkgs;
	}


	public void setNbr_pkgs(Long nbr_pkgs) {
		this.nbr_pkgs = nbr_pkgs;
	}


	public String getDn_status() {
		return dn_status;
	}


	public void setDn_status(String dn_status) {
		this.dn_status = dn_status;
	}


	public Date getTrans_dttm() {
		return trans_dttm;
	}


	public void setTrans_dttm(Date trans_dttm) {
		this.trans_dttm = trans_dttm;
	}


	public String getPrint_location() {
		return print_location;
	}


	public void setPrint_location(String print_location) {
		this.print_location = print_location;
	}


	public Date getGate_out_dttm() {
		return gate_out_dttm;
	}


	public void setGate_out_dttm(Date gate_out_dttm) {
		this.gate_out_dttm = gate_out_dttm;
	}


	public String getCash_receipt_nbr() {
		return cash_receipt_nbr;
	}


	public void setCash_receipt_nbr(String cash_receipt_nbr) {
		this.cash_receipt_nbr = cash_receipt_nbr;
	}


	public String getTruck_nbr() {
		return truck_nbr;
	}


	public void setTruck_nbr(String truck_nbr) {
		this.truck_nbr = truck_nbr;
	}


	public String getSst_machine_nbr() {
		return sst_machine_nbr;
	}


	public void setSst_machine_nbr(String sst_machine_nbr) {
		this.sst_machine_nbr = sst_machine_nbr;
	}


	public String getUa_nbr() {
		return ua_nbr;
	}


	public void setUa_nbr(String ua_nbr) {
		this.ua_nbr = ua_nbr;
	}


	public String getUa_status() {
		return ua_status;
	}


	public void setUa_status(String ua_status) {
		this.ua_status = ua_status;
	}


	public String getEdo_asn_nbr() {
		return edo_asn_nbr;
	}


	public void setEdo_asn_nbr(String edo_asn_nbr) {
		this.edo_asn_nbr = edo_asn_nbr;
	}


	public Long getEsn_asn_nbr() {
		return esn_asn_nbr;
	}


	public void setEsn_asn_nbr(Long esn_asn_nbr) {
		this.esn_asn_nbr = esn_asn_nbr;
	}


	public Long getBal_pkgs() {
		return bal_pkgs;
	}


	public void setBal_pkgs(Long bal_pkgs) {
		this.bal_pkgs = bal_pkgs;
	}


	public String getDp_ic_type() {
		return dp_ic_type;
	}


	public void setDp_ic_type(String dp_ic_type) {
		this.dp_ic_type = dp_ic_type;
	}


	public BigDecimal getWt() {
		return wt;
	}


	public void setWt(BigDecimal wt) {
		this.wt = wt;
	}


	public BigDecimal getVol() {
		return vol;
	}


	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}


	public String getCntr_nbr() {
		return cntr_nbr;
	}


	public void setCntr_nbr(String cntr_nbr) {
		this.cntr_nbr = cntr_nbr;
	}


	public Date getDn_create_dttm() {
		return dn_create_dttm;
	}


	public void setDn_create_dttm(Date dn_create_dttm) {
		this.dn_create_dttm = dn_create_dttm;
	}


	public Date getUa_create_dttm() {
		return ua_create_dttm;
	}


	public void setUa_create_dttm(Date ua_create_dttm) {
		this.ua_create_dttm = ua_create_dttm;
	}


	public String getVehicle_type() {
		return vehicle_type;
	}


	public void setVehicle_type(String vehicle_type) {
		this.vehicle_type = vehicle_type;
	}


	public String getLane_nbr() {
		return lane_nbr;
	}


	public void setLane_nbr(String lane_nbr) {
		this.lane_nbr = lane_nbr;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public Date getScan_dttm() {
		return scan_dttm;
	}


	public void setScan_dttm(Date scan_dttm) {
		this.scan_dttm = scan_dttm;
	}


	public Date getExit_dttm() {
		return exit_dttm;
	}


	public void setExit_dttm(Date exit_dttm) {
		this.exit_dttm = exit_dttm;
	}


	public String getDirection() {
		return direction;
	}


	public void setDirection(String direction) {
		this.direction = direction;
	}


	public String getRef_nbr() {
		return ref_nbr;
	}


	public void setRef_nbr(String ref_nbr) {
		this.ref_nbr = ref_nbr;
	}


	public Date getCreate_dttm() {
		return create_dttm;
	}


	public void setCreate_dttm(Date create_dttm) {
		this.create_dttm = create_dttm;
	}


	public String getCreateDttmStr() {
		return createDttmStr;
	}


	public void setCreateDttmStr(String createDttmStr) {
		this.createDttmStr = createDttmStr;
	}


	public String getExitDttmStr() {
		return exitDttmStr;
	}


	public void setExitDttmStr(String exitDttmStr) {
		this.exitDttmStr = exitDttmStr;
	}


	public String getScanDttmStr() {
		return scanDttmStr;
	}


	public void setScanDttmStr(String scanDttmStr) {
		this.scanDttmStr = scanDttmStr;
	}


	public String getVehicle_nbr() {
		return vehicle_nbr;
	}


	public void setVehicle_nbr(String vehicle_nbr) {
		this.vehicle_nbr = vehicle_nbr;
	}


	public String getHs_code() {
		return hs_code;
	}


	public void setHs_code(String hs_code) {
		this.hs_code = hs_code;
	}


	public String getVar_nbr() {
		return var_nbr;
	}


	public void setVar_nbr(String var_nbr) {
		this.var_nbr = var_nbr;
	}


	public Double getIn_captured_wt() {
		return in_captured_wt;
	}


	public void setIn_captured_wt(Double in_captured_wt) {
		this.in_captured_wt = in_captured_wt;
	}


	public Double getOut_captured_wt() {
		return out_captured_wt;
	}


	public void setOut_captured_wt(Double out_captured_wt) {
		this.out_captured_wt = out_captured_wt;
	}


	public String getTrans_type() {
		return trans_type;
	}


	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}


	public String getShutout_ind() {
		return shutout_ind;
	}


	public void setShutout_ind(String shutout_ind) {
		this.shutout_ind = shutout_ind;
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


	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

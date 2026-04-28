package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UaEsnDetValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String esn_asn_nbr = null;
	String vessel_name = null;
	String in_voy_nbr = null;
	String out_voy_nbr = null;
	String bk_ref_nbr = null;
	String pay_mode = null;
	String bill_party = null;
	String decl_pkg = null;
	String bal_pkg = null;
	String pkg_stored = null;
	String weight = null;
	String volume = null;
	String trucker_name = null;
	String trucker_cont_no = null;
	String trucker_ic = null;
	String first_trans = null;
	String cargo_desc = null;
	String cargo_markings = null;
	String trans_type = "";
	String contno = "";
	String contsize = "";
	String conttype = "";
	String atb = "";
	String date_time = "";
	String uanbrpkgs = "";
	String nric_no = "";
	String ictype = "";
	String dpname = "";
	String strCntrNum = "";
	String strUnStuffDt = "";
	String veh1 = "";
	String veh2 = "";
	String veh3 = "";
	String veh4 = "";
	String veh5 = "";
	String billtons = "";
	String cod = "";
	String act_no = "";
	String etb = "";
	String btr = "";
	String terminal = null;
	String scheme = null;
	String subScheme = null;
	String gcOperations = null;
	String vvCode = "";
	String uadate_time = "";
	String trucker_nbr_pkg = "";

	// ++ vietnd02
	String atu = "";
	// --

//	Warehouse
	String whInd;
	String whAggrNbr;
	String whRemarks;

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

	public UaEsnDetValueObject() {
	}

	public void setEsn_asn_nbr(String s) {
		this.esn_asn_nbr = s;
	}

	public String getEsn_asn_nbr() {
		return esn_asn_nbr;
	}

	public void setVessel_name(String s) {
		this.vessel_name = s;
	}

	public String getVessel_name() {
		return vessel_name;
	}

	public void setOut_voy_nbr(String s) {
		this.out_voy_nbr = s;
	}

	public String getOut_voy_nbr() {
		return out_voy_nbr;
	}

	public void setIn_voy_nbr(String s) {
		this.in_voy_nbr = s;
	}

	public String getIn_voy_nbr() {
		return in_voy_nbr;
	}

	public void setBk_ref_nbr(String s) {
		this.bk_ref_nbr = s;
	}

	public String getBk_ref_nbr() {
		return bk_ref_nbr;
	}

	public void setPay_mode(String s) {
		this.pay_mode = s;
	}

	public String getPay_mode() {
		return pay_mode;
	}

	public void setBill_party(String s) {
		this.bill_party = s;
	}

	public String getBill_party() {
		return bill_party;
	}

	public void setDecl_pkg(String s) {
		this.decl_pkg = s;
	}

	public String getDecl_pkg() {
		return decl_pkg;
	}

	public void setBal_pkg(String s) {
		this.bal_pkg = s;
	}

	public String getBal_pkg() {
		return bal_pkg;
	}

	public void setPkg_stored(String s) {
		this.pkg_stored = s;
	}

	public String getPkg_stored() {
		return pkg_stored;
	}

	public void setWeight(String s) {
		this.weight = s;
	}

	public String getWeight() {
		return weight;
	}

	public void setVolume(String s) {
		this.volume = s;
	}

	public String getVolume() {
		return volume;
	}

	public void setTrucker_name(String s) {
		this.trucker_name = s;
	}

	public String getTrucker_name() {
		return trucker_name;
	}

	public void setTrucker_cont_no(String s) {
		this.trucker_cont_no = s;
	}

	public String getTrucker_cont_no() {
		return trucker_cont_no;
	}

	public void setTrucker_ic(String s) {
		this.trucker_ic = s;
	}

	public String getTrucker_ic() {
		return trucker_ic;
	}

	public void setFirst_trans(String s) {
		this.first_trans = s;
	}

	public String getFirst_trans() {
		return first_trans;
	}

	public void setCargo_desc(String s) {
		this.cargo_desc = s;
	}

	public String getCargo_desc() {
		return cargo_desc;
	}

	public void setCargo_markings(String s) {
		this.cargo_markings = s;
	}

	public String getCargo_markings() {
		return cargo_markings;
	}

	public void setTrans_type(String s) {
		this.trans_type = s;
	}

	public String getTrans_type() {
		return trans_type;
	}

	public void setContno(String s) {
		this.contno = s;
	}

	public String getContno() {
		return contno;
	}

	public void setConttype(String s) {
		this.conttype = s;
	}

	public String getConttype() {
		return conttype;
	}

	public void setContsize(String s) {
		this.contsize = s;
	}

	public String getContsize() {
		return contsize;
	}

	public void setAtb(String s) {
		this.atb = s;
	}

	public String getAtb() {
		return atb;
	}

	public void setDate_time(String s) {
		this.date_time = s;
	}

	public String getDate_time() {
		return date_time;
	}

	public void setUanbrpkgs(String s) {
		this.uanbrpkgs = s;
	}

	public String getUanbrpkgs() {
		return uanbrpkgs;
	}

	public void setNric_no(String s) {
		this.nric_no = s;
	}

	public String getNric_no() {
		return nric_no;
	}

	public void setIctype(String s) {
		this.ictype = s;
	}

	public String getIctype() {
		return ictype;
	}

	public void setDpname(String s) {
		this.dpname = s;
	}

	public String getDpname() {
		return dpname;
	}

	public void setVeh1(String s) {
		this.veh1 = s;
	}

	public void setCntrNbr(String szCntrNum) {
		strCntrNum = szCntrNum;
	}

	public String getCntrNbr() {
		return strCntrNum;
	}

	public void setUnStuffDate(String szUnStuffDt) {
		strUnStuffDt = szUnStuffDt;
	}

	public String getUnStuffDate() {
		return strUnStuffDt;
	}

	public String getVeh1() {
		return veh1;
	}

	public void setVeh2(String s) {
		this.veh2 = s;
	}

	public String getVeh2() {
		return veh2;
	}

	public void setVeh3(String s) {
		this.veh3 = s;
	}

	public String getVeh3() {
		return veh3;
	}

	public void setVeh4(String s) {
		this.veh4 = s;
	}

	public String getVeh4() {
		return veh4;
	}

	public void setVeh5(String s) {
		this.veh5 = s;
	}

	public String getVeh5() {
		return veh5;
	}

	public void setBilltons(String s) {
		this.billtons = s;
	}

	public String getBilltons() {
		return billtons;
	}

	public void setCod(String s) {
		this.cod = s;
	}

	public String getCod() {
		return cod;
	}

	public void setAct_no(String s) {
		this.act_no = s;
	}

	public String getAct_no() {
		return act_no;
	}

	public void setEtb(String s) {
		this.etb = s;
	}

	public String getEtb() {
		return etb;
	}

	public void setBtr(String s) {
		this.btr = s;
	}

	public String getBtr() {
		return btr;
	}

//	Added by Punitha
	public void setWhInd(String WhInd) {
		whInd = WhInd;
	}

	public String getWhInd() {
		return whInd;
	}

	public void setWhAggrNbr(String WhAggrNbr) {
		whAggrNbr = WhAggrNbr;
	}

	public String getWhAggrNbr() {
		return whAggrNbr;
	}

	public void setWhRemarks(String WhRemarks) {
		whRemarks = WhRemarks;
	}

	public String getWhRemarks() {
		return whRemarks;
	}

	public String getAtu() {
		return atu;
	}

	public void setAtu(String atu) {
		this.atu = atu;
	}

	/**
	 * @return the vvCode
	 */
	public String getVvCode() {
		return vvCode;
	}

	/**
	 * @param vvCode the vvCode to set
	 */
	public void setVvCode(String vvCode) {
		this.vvCode = vvCode;
	}

	/**
	 * @return the uadate_time
	 */
	public String getUadate_time() {
		return uadate_time;
	}

	/**
	 * @param uadateTime the uadate_time to set
	 */
	public void setUadate_time(String uadateTime) {
		uadate_time = uadateTime;
	}

	/**
	 * @return the trucker_nbr_pkg
	 */
	public String getTrucker_nbr_pkg() {
		return trucker_nbr_pkg;
	}

	/**
	 * @param trucker_nbr_pkg the trucker_nbr_pkg to set
	 */
	public void setTrucker_nbr_pkg(String trucker_nbr_pkg) {
		this.trucker_nbr_pkg = trucker_nbr_pkg;
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

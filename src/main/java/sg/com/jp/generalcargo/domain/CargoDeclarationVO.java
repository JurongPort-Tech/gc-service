package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.ProcessChargeConst;

@XmlRootElement
public class CargoDeclarationVO extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CargoDeclarationVO.class);
	private long dsa_id;
	private String dsa_nbr;	
	private String cust_cd;
	private String cust_name;
	private String lighter_opr_cd;
	private String lighter_opr_nm;
	private String terminal_cd;
	private String terminal_name;
	private String dsa_status;
	private String dsa_status_nm;
	private String billing_status;
	private String billing_status_nm;
	private String call_sign;
	private String lighter_nm;
	private Timestamp lighter_eta;
	private String dcl_truck_nbr;
	private String act_truck_Nbr;
	private String truck_status;
	private String driver1_pass_id;
	private String driver2_pass_id;
	private String paym_mode;
	private String acct_nbr;
	private String bill_nbr;
	private Timestamp bill_dttm;
	private String ship_nm;
	private String dg_cargo_ind;
	private String cargo_dest_type;
	private String cargo_dest_cd;
	private Timestamp gate_in_dttm;
	private Timestamp gate_out_dttm;
	private String gate_status;
	private String crane_nbr;
	private String crane_opr_cd;
	private String crane_opr_nm;
	private Timestamp crane_start_dttm;
	//Cally Neo 20/11/2015 CR-LWMS-20151118-005 Add a new field
	private Timestamp crane_end_dttm;
	private Timestamp check_in_dttm;
	
	private Timestamp pan_dttm;
	private String vsl_mvmt_nbr;
	
	private Timestamp atb_dttm;
	private String str_atb_dttm;
	private Timestamp atu_dttm;
	private String str_atu_dttm;
	
	private String cashsales_ref;
	private String create_user_id;
	private String create_user_nm;
	private Timestamp create_dttm;
	private String submitted_user_id;
	private String submitted_user_nm;
	private Timestamp submitted_dttm;
	private String last_modify_user_id;
	private String last_modify_user_nm;
	private Timestamp last_modify_dttm;	
	private Timestamp outward_dttm;
    private int totalLifts;    
    private String crane_name;
    
    //Add cargo type for [New Special Cargo]
    private String cargo_type;
    
    //Added by HuyLQ2
    private String cargo_type_cd;
    
    private double lift_charge;
    private double additional_charge;
    private double total_charge;
    private String quote_rem;
    private String suggestion_crane;
    private String internal_rem;
    private double addtional_charge_rate;

	private String cust_rem;
    private String approve_rem;
    private String approve_status;
    private String verify_status;
    private String quotation_user_id;
    
    // add attributes for amendment module
    private String amend_rem;    
    private Timestamp last_amend_dttm;
    
    private String approved_user_id;    
    private String verified_user_id;
    
    //Cally Neo SL-LWMS-20150907-01: Crane Detail Report:  For Ready for Billing option, all lifting date/time are current time.
  	private String lifting_day;
  	
  	public String getLifting_day() {		
  		return lifting_day;
  	}

  	/**
  	 * @param lifting_day the lifting_day to set
  	 */
  	public void setLifting_day(String lifting_day) {
  		this.lifting_day = lifting_day;
  	}
	
	public String getApproved_user_id() {
		return approved_user_id;
	}
	public void setApproved_user_id(String approved_user_id) {
		this.approved_user_id = approved_user_id;
	}
	public String getVerified_user_id() {
		return verified_user_id;
	}
	public void setVerified_user_id(String verified_user_id) {
		this.verified_user_id = verified_user_id;
	}
	/**
	 * @return the dsa_nbr
	 */
	public String getDsa_nbr() {
		return dsa_nbr;
	}
	/**
	 * @param dsa_nbr the dsa_nbr to set
	 */
	public void setDsa_nbr(String dsa_nbr) {
		this.dsa_nbr = dsa_nbr;
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
	 * @return the lighter_opr_cd
	 */
	public String getLighter_opr_cd() {
		return lighter_opr_cd;
	}
	/**
	 * @param lighter_opr_cd the lighter_opr_cd to set
	 */
	public void setLighter_opr_cd(String lighter_opr_cd) {
		this.lighter_opr_cd = lighter_opr_cd;
	}
	/**
	 * @return the terminal_cd
	 */
	public String getTerminal_cd() {
		return terminal_cd;
	}
	/**
	 * @param terminal_cd the terminal_cd to set
	 */
	public void setTerminal_cd(String terminal_cd) {
		this.terminal_cd = terminal_cd;
	}
	/**
	 * @return the dsa_status
	 */
	public String getDsa_status() {
		return dsa_status;
	}
	/**
	 * @param dsa_status the dsa_status to set
	 */
	public void setDsa_status(String dsa_status) {
		this.dsa_status = dsa_status;
	}
	/**
	 * @return the billing_status
	 */
	public String getBilling_status() {
		return billing_status;
	}
	/**
	 * @param billing_status the billing_status to set
	 */
	public void setBilling_status(String billing_status) {
		this.billing_status = billing_status;
	}
	/**
	 * @return the lighter_nm
	 */
	public String getLighter_nm() {
		return lighter_nm;
	}
	/**
	 * @param lighter_nm the lighter_nm to set
	 */
	public void setLighter_nm(String lighter_nm) {
		this.lighter_nm = lighter_nm;
	}
	/**
	 * @return the lighter_eta
	 */
	public Timestamp getLighter_eta() {
		return lighter_eta;
	}
	/**
	 * @param lighter_eta the lighter_eta to set
	 */
	public void setLighter_eta(Timestamp lighter_eta) {
		this.lighter_eta = lighter_eta;
	}
	/**
	 * @return the dcl_truck_nbr
	 */
	public String getDcl_truck_nbr() {
		return dcl_truck_nbr;
	}
	/**
	 * @param dcl_truck_nbr the dcl_truck_nbr to set
	 */
	public void setDcl_truck_nbr(String dcl_truck_nbr) {
		this.dcl_truck_nbr = dcl_truck_nbr;
	}
	/**
	 * @return the act_truck_Nbr
	 */
	public String getAct_truck_Nbr() {
		return act_truck_Nbr;
	}
	/**
	 * @param act_truck_Nbr the act_truck_Nbr to set
	 */
	public void setAct_truck_Nbr(String act_truck_Nbr) {
		this.act_truck_Nbr = act_truck_Nbr;
	}
	/**
	 * @return the truck_status
	 */
	public String getTruck_status() {
		return truck_status;
	}
	/**
	 * @param truck_status the truck_status to set
	 */
	public void setTruck_status(String truck_status) {
		this.truck_status = truck_status;
	}
	/**
	 * @return the driver1_pass_id
	 */
	public String getDriver1_pass_id() {
		return driver1_pass_id;
	}
	/**
	 * @param driver1_pass_id the driver1_pass_id to set
	 */
	public void setDriver1_pass_id(String driver1_pass_id) {
		this.driver1_pass_id = driver1_pass_id;
	}
	/**
	 * @return the driver2_pass_id
	 */
	public String getDriver2_pass_id() {
		return driver2_pass_id;
	}
	/**
	 * @param driver2_pass_id the driver2_pass_id to set
	 */
	public void setDriver2_pass_id(String driver2_pass_id) {
		this.driver2_pass_id = driver2_pass_id;
	}
	/**
	 * @return the paym_mode
	 */
	public String getPaym_mode() {
		return paym_mode;
	}
	/**
	 * @param paym_mode the paym_mode to set
	 */
	public void setPaym_mode(String paym_mode) {
		this.paym_mode = paym_mode;
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
	 * @return the bill_nbr
	 */
	public String getBill_nbr() {
		return bill_nbr;
	}
	/**
	 * @param bill_nbr the bill_nbr to set
	 */
	public void setBill_nbr(String bill_nbr) {
		this.bill_nbr = bill_nbr;
	}
	/**
	 * @return the bill_dttm
	 */
	public Timestamp getBill_dttm() {
		return bill_dttm;
	}
	/**
	 * @param bill_dttm the bill_dttm to set
	 */
	public void setBill_dttm(Timestamp bill_dttm) {
		this.bill_dttm = bill_dttm;
	}
	/**
	 * @return the ship_nm
	 */
	public String getShip_nm() {
		return ship_nm;
	}
	/**
	 * @param ship_nm the ship_nm to set
	 */
	public void setShip_nm(String ship_nm) {
		this.ship_nm = ship_nm;
	}
	/**
	 * @return the dg_cargo_ind
	 */
	public String getDg_cargo_ind() {
		return dg_cargo_ind;
	}
	/**
	 * @param dg_cargo_ind the dg_cargo_ind to set
	 */
	public void setDg_cargo_ind(String dg_cargo_ind) {
		this.dg_cargo_ind = dg_cargo_ind;
	}
	/**
	 * @return the cargo_dest_type
	 */
	public String getCargo_dest_type() {
		return cargo_dest_type;
	}
	/**
	 * @param cargo_dest_type the cargo_dest_type to set
	 */
	public void setCargo_dest_type(String cargo_dest_type) {
		this.cargo_dest_type = cargo_dest_type;
	}
	/**
	 * @return the cargo_dest_cd
	 */
	public String getCargo_dest_cd() {
		return cargo_dest_cd;
	}
	/**
	 * @param cargo_dest_cd the cargo_dest_cd to set
	 */
	public void setCargo_dest_cd(String cargo_dest_cd) {
		this.cargo_dest_cd = cargo_dest_cd;
	}
	/**
	 * @return the gate_in_dttm
	 */
	public Timestamp getGate_in_dttm() {
		return gate_in_dttm;
	}
	/**
	 * @param gate_in_dttm the gate_in_dttm to set
	 */
	public void setGate_in_dttm(Timestamp gate_in_dttm) {
		this.gate_in_dttm = gate_in_dttm;
	}
	/**
	 * @return the gate_out_dttm
	 */
	public Timestamp getGate_out_dttm() {
		return gate_out_dttm;
	}
	/**
	 * @param gate_out_dttm the gate_out_dttm to set
	 */
	public void setGate_out_dttm(Timestamp gate_out_dttm) {
		this.gate_out_dttm = gate_out_dttm;
	}
	/**
	 * @return the gate_status
	 */
	public String getGate_status() {
		return gate_status;
	}
	/**
	 * @param gate_status the gate_status to set
	 */
	public void setGate_status(String gate_status) {
		this.gate_status = gate_status;
	}
	/**
	 * @return the crane_nbr
	 */
	public String getCrane_nbr() {
		return crane_nbr;
	}
	/**
	 * @param crane_nbr the crane_nbr to set
	 */
	public void setCrane_nbr(String crane_nbr) {
		this.crane_nbr = crane_nbr;
	}
	/**
	 * @return the crane_opr_cd
	 */
	public String getCrane_opr_cd() {
		return crane_opr_cd;
	}
	/**
	 * @param crane_opr_cd the crane_opr_cd to set
	 */
	public void setCrane_opr_cd(String crane_opr_cd) {
		this.crane_opr_cd = crane_opr_cd;
	}
	/**
	 * @return the crane_start_dttm
	 */
	public Timestamp getCrane_start_dttm() {
		return crane_start_dttm;
	}
	/**
	 * @param crane_start_dttm the crane_start_dttm to set
	 */
	public void setCrane_start_dttm(Timestamp crane_start_dttm) {
		this.crane_start_dttm = crane_start_dttm;
	}
	//Cally Neo 20/11/2015 CR-LWMS-20151118-005 Add a new field
	/**
	 * @return the crane_end_dttm
	 */
	public Timestamp getCrane_end_dttm() {
		return crane_end_dttm;
	}
	/**
	 * @param crane_start_dttm the crane_end_dttm to set
	 */
	public void setCrane_end_dttm(Timestamp crane_end_dttm) {
		this.crane_end_dttm = crane_end_dttm;
	}
	
	/**
	 * @return the check_in_dttm
	 */
	public Timestamp getCheck_in_dttm() {
		return check_in_dttm;
	}
	/**
	 * @param check_in_dttm the check_in_dttm to set
	 */
	public void setCheck_in_dttm(Timestamp check_in_dttm) {
		this.check_in_dttm = check_in_dttm;
	}
	
	/**
	 * @return the pan_dttm
	 */
	public Timestamp getPan_dttm() {
		return pan_dttm;
	}
	/**
	 * @param pan_dttm the pan_dttm to set
	 */
	public void setPan_dttm(Timestamp pan_dttm) {
		this.pan_dttm = pan_dttm;
	}
	/**
	 * @return the vsl_mvmt_nbr
	 */
	public String getVsl_mvmt_nbr() {
		return vsl_mvmt_nbr;
	}
	/**
	 * @param vsl_mvmt_nbr the vsl_mvmt_nbr to set
	 */
	public void setVsl_mvmt_nbr(String vsl_mvmt_nbr) {
		this.vsl_mvmt_nbr = vsl_mvmt_nbr;
	}
	
	/**
	 * @return the str_atb_dttm
	 */
	public String getStr_atb_dttm() {
		return str_atb_dttm;
	}
	/**
	 * @param str_atb_dttm the str_atb_dttm to set
	 */
	public void setStr_atb_dttm(String str_atb_dttm) {
		this.str_atb_dttm = str_atb_dttm;
	}
	/**
	 * @return the str_atu_dttm
	 */
	public String getStr_atu_dttm() {
		return str_atu_dttm;
	}
	/**
	 * @param str_atu_dttm the str_atu_dttm to set
	 */
	public void setStr_atu_dttm(String str_atu_dttm) {
		this.str_atu_dttm = str_atu_dttm;
	}
//	@XmlJavaTypeAdapter( TimestampAdapter.class)
	public Timestamp getAtb_dttm() {
		return atb_dttm;
	}
	/**
	 * @param atb_dttm the atb_dttm to set
	 */
	public void setAtb_dttm(Timestamp atb_dttm) {
		this.atb_dttm = atb_dttm;
	}
	/**
	 * @return the atu_dttm
	 */
//	@XmlJavaTypeAdapter( TimestampAdapter.class)
	public Timestamp getAtu_dttm() {
		return atu_dttm;
	}
	/**
	 * @param atu_dttm the atu_dttm to set
	 */
	public void setAtu_dttm(Timestamp atu_dttm) {
		this.atu_dttm = atu_dttm;
	}
	/**
	 * @return the cashsales_ref
	 */
	public String getCashsales_ref() {
		return cashsales_ref;
	}
	/**
	 * @param cashsales_ref the cashsales_ref to set
	 */
	public void setCashsales_ref(String cashsales_ref) {
		this.cashsales_ref = cashsales_ref;
	}
	/**
	 * @return the create_user_id
	 */
	public String getCreate_user_id() {
		return create_user_id;
	}
	/**
	 * @param create_user_id the create_user_id to set
	 */
	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}
	/**
	 * @return the create_dttm
	 */
	public Timestamp getCreate_dttm() {
		return create_dttm;
	}
	/**
	 * @param create_dttm the create_dttm to set
	 */
	public void setCreate_dttm(Timestamp create_dttm) {
		this.create_dttm = create_dttm;
	}
	/**
	 * @return the submitted_user_id
	 */
	public String getSubmitted_user_id() {
		return submitted_user_id;
	}
	/**
	 * @param submitted_user_id the submitted_user_id to set
	 */
	public void setSubmitted_user_id(String submitted_user_id) {
		this.submitted_user_id = submitted_user_id;
	}
	/**
	 * @return the submitted_ttm
	 */
	public Timestamp getSubmitted_dttm() {
		return submitted_dttm;
	}
	/**
	 * @param submitted_ttm the submitted_ttm to set
	 */
	public void setSubmitted_dttm(Timestamp submitted_dttm) {
		this.submitted_dttm = submitted_dttm;
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
	public Timestamp getLast_modify_dttm() {
		return last_modify_dttm;
	}
	/**
	 * @param last_modify_dttm the last_modify_dttm to set
	 */
	public void setLast_modify_dttm(Timestamp last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}
	/**
	 * @return the terminal_name
	 */
	public String getTerminal_name() {
		return terminal_name;
	}
	/**
	 * @param terminal_name the terminal_name to set
	 */
	public void setTerminal_name(String terminal_name) {
		this.terminal_name = terminal_name;
	}
	/**
	 * @return the dsa_id
	 */
	public long getDsa_id() {
		return dsa_id;
	}
	/**
	 * @param dsa_id the dsa_id to set
	 */
	public void setDsa_id(long dsa_id) {
		this.dsa_id = dsa_id;
	}
	
	
	private List<CargoDeclarationItemVO> dsaItems;
	public List<CargoDeclarationItemVO> getDsaItems() {
		return dsaItems;
	}
	public void setDsaItems(List<CargoDeclarationItemVO> dsaitems) {
		this.dsaItems = dsaitems;
	}
	private List<CranageVO> dsaCranages;
	public List<CranageVO> getDsaCranages() {
		return dsaCranages;
	}
	public void setDsaCranages(List<CranageVO> dsacranages) {
		this.dsaCranages = dsacranages;
	}
	
	
	// mongkey
	private Timestamp dockageATB;
	public Timestamp getDockageATB() {
		return dockageATB;
	}
	public void setDockageATB(Timestamp dockageATB) {
		this.dockageATB = dockageATB;
	}
	
	
	// mongkey start - for billing purpose
	/**
	 * 
	 * @param billAdjParam
	 * @param tariffMainVO
	 * @param factorPerHour
	 * @param tariffTierId
	 * @param rate
	 * @return
	 */
	public ChargeableBillValueObject buildWharhageChargeableBill(
			BillAdjustParam billAdjParam, TariffMainVO tariffMainVO,
			int tariffTierId, double rate, int refNum, CargoDeclarationItemVO itemVO) {
		ChargeableBillValueObject vo = new ChargeableBillValueObject();

		// Tarif main info
		vo.setTariffMainCatCd(tariffMainVO.getMainCategory());
		vo.setTariffSubCatCd(tariffMainVO.getSubCategory());
		vo.setVersionNbr(tariffMainVO.getVersion());
		vo.setTariffType(tariffMainVO.getTariffTypeInd());
		vo.setTariffCd(tariffMainVO.getCode());
		vo.setTariffDesc(tariffMainVO.getDescription());
		vo.setGstCharge(tariffMainVO.getGST());
		vo.setFmasGstCd(tariffMainVO.getGSTCode());

		// VO info
		vo.setCustCd(getCust_cd());
		vo.setAcctNbr(getAcct_nbr());
		vo.setContractNbr("");

		// General Info
		vo.setContractualYr(0);
		vo.setVvCd("");
		vo.setBillInd("");
		vo.setLastModifyUserId("SYSTEM");

		vo.setUnitRate(rate);
		vo.setTierSeqNbr(tariffTierId);

		// Specific attributes
		vo.setNbrOtherUnit(billAdjParam.getTotalOtherUnit());
		vo.setNbrCntr(0);
		vo.setNbrTimeUnit(billAdjParam.getTotalTime());
		vo.setRemarks(new String[] { generateWharfageRemarks(tariffMainVO, itemVO) });
		vo.setRefInd(new Integer(refNum).toString());
		
		//TuanTA10 add
		//vo.setRefNbr(getDsa_nbr() + "~" + itemVO.getCargo_line_nbr());
		vo.setRefNbr(getDsa_nbr() + "~" + itemVO.getDirection_ind());
		
		// Get gstAmount
		vo.setGstAmt(billAdjParam.getGstAmount());
		// Get Total Charge Amount
		vo.setTotalChargeAmt(billAdjParam.getTotalAmount());

		vo.setLastModifyDttm(new Timestamp(System.currentTimeMillis()));
		vo.setVarDttm(UserTimestampVO.getCurrentTimestamp());
		
		vo.setTxnDttm(getCreate_dttm());
		//End
		
		return vo;
	}
	
	public ChargeableBillValueObject buildCranageChargeableBill(
			BillAdjustParam billAdjParam, TariffMainVO tariffMainVO,
			int tariffTierId, double rate, int refNum, CranageVO craneVO, String liftType, Timestamp varDttm) {
		
		ChargeableBillValueObject vo = new ChargeableBillValueObject();

		// Tarif main info
		vo.setTariffMainCatCd(tariffMainVO.getMainCategory());
		vo.setTariffSubCatCd(tariffMainVO.getSubCategory());
		vo.setVersionNbr(tariffMainVO.getVersion());
		vo.setTariffType(tariffMainVO.getTariffTypeInd());
		vo.setTariffCd(tariffMainVO.getCode());
		vo.setTariffDesc(tariffMainVO.getDescription());
		vo.setGstCharge(tariffMainVO.getGST());
		vo.setFmasGstCd(tariffMainVO.getGSTCode());

		// VO info
		vo.setCustCd(getCust_cd());
		vo.setAcctNbr(getAcct_nbr());
		vo.setContractNbr("");

		// General Info
		vo.setContractualYr(0);
		vo.setVvCd("");
		vo.setBillInd("");
		vo.setLastModifyUserId("SYSTEM");

		vo.setUnitRate(rate);
		vo.setTierSeqNbr(tariffTierId);

		// Specific attributes
		vo.setNbrOtherUnit(billAdjParam.getTotalOtherUnit());
		vo.setNbrCntr(0);
		vo.setNbrTimeUnit(billAdjParam.getTotalTime());
		vo.setRemarks(new String[] { generateCranageRemarks(tariffMainVO, craneVO, liftType) });
		vo.setRefInd(new Integer(refNum).toString());
		
		//TuanTA10 add
		vo.setRefNbr(getDsa_nbr() + "~" + craneVO.getCranage_line_nbr() + "~" + liftType);
		
		// Get gstAmount
		vo.setGstAmt(billAdjParam.getGstAmount());
		// Get Total Charge Amount
		vo.setTotalChargeAmt(billAdjParam.getTotalAmount());

		vo.setLastModifyDttm(new Timestamp(System.currentTimeMillis()));
		// BEGIN: ThangPV update to use lifting date instead of current date time. 
		vo.setVarDttm(varDttm);
		// END: ThangPV update to use lifting date instead of current date time.
		vo.setTxnDttm(getCreate_dttm());
		//End
		
		return vo;
	}
	
	public ChargeableBillValueObject buildCranageChargeableBill(
			BillAdjustParam billAdjParam, TariffMainVO tariffMainVO,
			int tariffTierId, double rate, int refNum, CranageVO craneVO, String liftType) {
		return this.buildCranageChargeableBill(billAdjParam, tariffMainVO, tariffTierId, rate, refNum, craneVO, liftType, UserTimestampVO.getCurrentTimestamp());
	}
	
	public ChargeableBillValueObject buildCraneOperatorChargeableBill(
			BillAdjustParam billAdjParam, TariffMainVO tariffMainVO,
			int tariffTierId, double rate, int refNum, CranageVO craneVO, String liftType) {
		
		ChargeableBillValueObject vo = new ChargeableBillValueObject();

		// Tarif main info
		vo.setTariffMainCatCd(tariffMainVO.getMainCategory());
		vo.setTariffSubCatCd(tariffMainVO.getSubCategory());
		vo.setVersionNbr(tariffMainVO.getVersion());
		vo.setTariffType(tariffMainVO.getTariffTypeInd());
		vo.setTariffCd(tariffMainVO.getCode());
		vo.setTariffDesc(tariffMainVO.getDescription());
		vo.setGstCharge(tariffMainVO.getGST());
		vo.setFmasGstCd(tariffMainVO.getGSTCode());

		// VO info
		vo.setCustCd(getCust_cd());
		vo.setAcctNbr(getAcct_nbr());
		vo.setContractNbr("");

		// General Info
		vo.setContractualYr(0);
		vo.setVvCd("");
		vo.setBillInd("");
		vo.setLastModifyUserId("SYSTEM");

		vo.setUnitRate(rate);
		vo.setTierSeqNbr(tariffTierId);

		// Specific attributes
		vo.setNbrOtherUnit(billAdjParam.getTotalOtherUnit());
		vo.setNbrCntr(0);
		vo.setNbrTimeUnit(billAdjParam.getTotalTime());
		vo.setRemarks(new String[] { generateCranageRemarks(tariffMainVO, craneVO, liftType) });
		//vo.setRefInd(new Integer(refNum).toString());
		
		//TuanTA10 add
		vo.setRefNbr(getDsa_nbr() + "~" + craneVO.getCranage_line_nbr() + "~" + liftType);
		vo.setRefInd(ProcessChargeConst.REF_IND_LWMS_DSA_CRANAGEOPERATOR);
		
		// Get gstAmount
		vo.setGstAmt(billAdjParam.getGstAmount());
		// Get Total Charge Amount
		vo.setTotalChargeAmt(billAdjParam.getTotalAmount());

		vo.setLastModifyDttm(new Timestamp(System.currentTimeMillis()));
		vo.setVarDttm(UserTimestampVO.getCurrentTimestamp());
		
		vo.setTxnDttm(getCreate_dttm());
		//End
		
		return vo;
	}
	
	private String generateWharfageRemarks(TariffMainVO tariffMainVO, CargoDeclarationItemVO itemVO) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("DSA: ").append(getDsa_nbr()).append(", ");
		//Hoa Nguyen: Start add for LWMS Enhancement in 15-May
//		sb.append("In: ").append(DateParser.date24ToString(gate_in_dttm)).append(", ");
//		sb.append("Out: ").append(DateParser.date24ToString(gate_out_dttm)).append(", ");		
//		sb.append("Lift: ").append(DateParser.date24ToString(getCrane_start_dttm())).append(", ");				
		sb.append("Vessel: ").append(getShip_nm()).append(", ");
		sb.append("Truck: ").append(getAct_truck_Nbr()).append(", ");	
		
		//Hoa Nguyen: End add for LWMS Enhancement in 15-May
		if (itemVO.getDirection_ind() != null) {
		  if (itemVO.getDirection_ind().equalsIgnoreCase("E")) {
			sb.append("Type: ").append("Export");	
		  } else {
			sb.append("Type: ").append("Import");
		  }
		}
		
		
		//sb.append("Lighter Opr Cd: ").append(lighter_opr_cd).append(", ");
		//sb.append("Lighter Name: ").append((null == lighter_nm || lighter_nm.length() == 0) ? "UNKNOWN" : lighter_nm).append(", ");
		//sb.append("DCL Truck No: ").append(dcl_truck_nbr);
		//sb.append("ACT Truck No: ").append(act_truck_Nbr);
		log.info("==============generateWharfageRemarks=[" +sb.toString()+ "]=================");
		//end of adding
		return sb.toString();
	}
	private String generateCranageRemarks(TariffMainVO tariffMainVO, CranageVO craneVO, String liftType) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbTemp = new StringBuffer();
		
//		sb.append("Lifting Time: ").append(DateParser.date24ToString(getCrane_start_dttm())).append(", ");
		//sb.append("Type: ").append("").append(getType(true)).append(", ");;
		
		//Hoa Nguyen: Start add for LWMS Enhancement in 15-May
		sb.append("DSA: ").append(getDsa_nbr()).append(", ");
//		sb.append("In: ").append(DateParser.date24ToString(gate_in_dttm)).append(", ");
//		sb.append("Out: ").append(DateParser.date24ToString(gate_out_dttm)).append(", ");		
//		sb.append("Lift: ").append(DateParser.date24ToString(getCrane_start_dttm())).append(", ");		
		sb.append("Vessel: ").append(getShip_nm()).append(", ");
		sb.append("Truck: ").append(getAct_truck_Nbr()).append(", ");	
		//Hoa Nguyen: End add for LWMS Enhancement in 15-May
		
		//sb.append("Lighter Opr Cd: ").append(lighter_opr_cd).append(", ");
		//added by jadehuang for CR-CAB-20090701-18 14 July, 2009
		//sb.append("Lighter Name: ").append((null == lighter_nm || lighter_nm.length() == 0) ? "UNKNOWN" : lighter_nm).append(", ");
		//sb.append("DCL Truck No: ").append(dcl_truck_nbr).append(", ");
		
		
		//sb.append("ATB: ").append(DateParser.date24ToString(getDockageATB())).append(", ");
		//sb.append("Cargo Type: ").append(craneVO.getCargo_type()).append(", ");
		if (liftType.equalsIgnoreCase(ProcessChargeConst.BILLREF_CRANAGE_LIFT_NORMAL)) {
			int lifts = (int) craneVO.getNorm_lift_nbr();
			sb.append("Lift: ").append(lifts).append(", ");
		}
		else {
			int lifts = (int) craneVO.getWharf_lift_nbr();
			sb.append("Wharf Lift: ").append(lifts).append(", ");
		}
		sb.append(craneVO.getFrom_ton()).append("-");
		sb.append(craneVO.getTo_ton()).append("Ton");
//		sb.append("Vessel: ").append(getShip_nm()).append(" ");

		//end of adding
		return sb.toString();
	}
	// mongkey end
	/**
	 * @return the lighter_opr_name
	 */
	public String getLighter_opr_nm() {
		return lighter_opr_nm;
	}
	/**
	 * @param lighter_opr_name the lighter_opr_name to set
	 */
	public void setLighter_opr_nm(String lighter_opr_nm) {
		this.lighter_opr_nm = lighter_opr_nm;
	}
	/**
	 * @return the call_sign
	 */
	public String getCall_sign() {
		return call_sign;
	}
	/**
	 * @param call_sign the call_sign to set
	 */
	public void setCall_sign(String call_sign) {
		this.call_sign = call_sign;
	}
	/**
	 * @return the dsa_status_nm
	 */
	public String getDsa_status_nm() {
		return dsa_status_nm;
	}
	/**
	 * @param dsa_status_nm the dsa_status_nm to set
	 */
	public void setDsa_status_nm(String dsa_status_nm) {
		this.dsa_status_nm = dsa_status_nm;
	}
	/**
	 * @return the billing_status_nm
	 */
	public String getBilling_status_nm() {
		return billing_status_nm;
	}
	/**
	 * @param billing_status_nm the billing_status_nm to set
	 */
	public void setBilling_status_nm(String billing_status_nm) {
		this.billing_status_nm = billing_status_nm;
	}
	/**
	 * @return the create_user_nm
	 */
	public String getCreate_user_nm() {
		return create_user_nm;
	}
	/**
	 * @param create_user_nm the create_user_nm to set
	 */
	public void setCreate_user_nm(String create_user_nm) {
		this.create_user_nm = create_user_nm;
	}
	/**
	 * @return the submitted_user_nm
	 */
	public String getSubmitted_user_nm() {
		return submitted_user_nm;
	}
	/**
	 * @param submitted_user_nm the submitted_user_nm to set
	 */
	public void setSubmitted_user_nm(String submitted_user_nm) {
		this.submitted_user_nm = submitted_user_nm;
	}
	/**
	 * @return the last_modify_user_nm
	 */
	public String getLast_modify_user_nm() {
		return last_modify_user_nm;
	}
	/**
	 * @param last_modify_user_nm the last_modify_user_nm to set
	 */
	public void setLast_modify_user_nm(String last_modify_user_nm) {
		this.last_modify_user_nm = last_modify_user_nm;
	}
	/**
	 * @return the crane_opr_nm
	 */
	public String getCrane_opr_nm() {
		return crane_opr_nm;
	}
	/**
	 * @param crane_opr_nm the crane_opr_nm to set
	 */
	public void setCrane_opr_nm(String crane_opr_nm) {
		this.crane_opr_nm = crane_opr_nm;
	}
		
	/**
	 * @param gate_in_dttm the gate_in_dttm to set
	 */
	public void setOutward_dttm(Timestamp outward_dttm) {
		this.outward_dttm = outward_dttm;
	}
	/**
	 * @return the gate_out_dttm
	 */
	public Timestamp getOutward_dttm() {
		return outward_dttm;
	}
	/**
	 * @return the cust_name
	 */
	public String getCust_name() {
		return cust_name;
	}
	/**
	 * @param cust_name the cust_name to set
	 */
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	/**
	 * @return the totalLifts
	 */
	public int getTotalLifts() {
		return totalLifts;
	}
	/**
	 * @param totalLifts the totalLifts to set
	 */
	public void setTotalLifts(int totalLifts) {
		this.totalLifts = totalLifts;
	}
	/**
	 * @return the crane_name
	 */
	public String getCrane_name() {
		return crane_name;
	}
	/**
	 * @param crane_name the crane_name to set
	 */
	public void setCrane_name(String crane_name) {
		this.crane_name = crane_name;
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
		
	public String getAmend_rem() {
		return amend_rem;
	}
	public void setAmend_rem(String amend_rem) {
		this.amend_rem = amend_rem;
	}	
//	@XmlJavaTypeAdapter( TimestampAdapter.class)
	public Timestamp getLast_amend_dttm() {
		return last_amend_dttm;
	}
	public void setLast_amend_dttm(Timestamp last_amend_dttm) {
		this.last_amend_dttm = last_amend_dttm;
	}
	/**
	 * @return the lift_charge
	 */
	public double getLift_charge() {
		return lift_charge;
	}

	/**
	 * @param lift_charge the lift_charge to set
	 */
	public void setLift_charge(double lift_charge) {
		this.lift_charge = lift_charge;
	}

	/**
	 * @return the additional_charge
	 */
	public double getAdditional_charge() {
		return additional_charge;
	}

	/**
	 * @param additional_charge the additional_charge to set
	 */
	public void setAdditional_charge(double additional_charge) {
		this.additional_charge = additional_charge;
	}

	/**
	 * @return the total_charge
	 */
	public double getTotal_charge() {
		return total_charge;
	}

	/**
	 * @param total_charge the total_charge to set
	 */
	public void setTotal_charge(double total_charge) {
		this.total_charge = total_charge;
	}

	/**
	 * @return the quotation_remark
	 */
	public String getQuote_rem() {
		return quote_rem;
	}

	/**
	 * @param quotation_remark the quotation_remark to set
	 */
	public void setQuote_rem(String quote_rem) {
		this.quote_rem = quote_rem;
	}

	/**
	 * @return the suggestion_crane
	 */
	public String getSuggestion_crane() {
		return suggestion_crane;
	}

	/**
	 * @param suggestion_crane the suggestion_crane to set
	 */
	public void setSuggestion_crane(String suggestion_crane) {
		this.suggestion_crane = suggestion_crane;
	}

	/**
	 * @return the internal_note
	 */
	public String getInternal_rem() {
		return internal_rem;
	}

	/**
	 * @param internal_note the internal_note to set
	 */
	public void setInternal_rem(String internal_rem) {
		this.internal_rem = internal_rem;
	}
	/**
	 * @return the customer_remark
	 */
	public String getCust_rem() {
		return cust_rem;
	}
	/**
	 * @param customer_remark the customer_remark to set
	 */
	public void setCust_rem(String cust_rem) {
		this.cust_rem = cust_rem;
	}
	/**
	 * @return the approve_remark
	 */
	public String getApprove_rem() {
		return approve_rem;
	}
	/**
	 * @param approve_remark the approve_remark to set
	 */
	public void setApprove_rem(String approve_rem) {
		this.approve_rem = approve_rem;
	}
	/**
	 * @return the approve_status
	 */
	public String getApprove_status() {
		return approve_status;
	}
	/**
	 * @param approve_status the approve_status to set
	 */
	public void setApprove_status(String approve_status) {
		this.approve_status = approve_status;
	}
	/**
	 * @return the verify_status
	 */
	public String getVerify_status() {
		return verify_status;
	}
	/**
	 * @param verify_status the verify_status to set
	 */
	public void setVerify_status(String verify_status) {
		this.verify_status = verify_status;
	}
	/**
	 * @return the quotation_user_id
	 */
	public String getQuotation_user_id() {
		return quotation_user_id;
	}
	/**
	 * @param quotation_user_id the quotation_user_id to set
	 */
	public void setQuotation_user_id(String quotation_user_id) {
		this.quotation_user_id = quotation_user_id;
	}
	
    public double getAddtional_charge_rate() {
		return addtional_charge_rate;
	}
	public void setAddtional_charge_rate(double addtional_charge_rate) {
		this.addtional_charge_rate = addtional_charge_rate;
	}
	public String getCargo_type_cd() {
		return cargo_type_cd;
	}
	public void setCargo_type_cd(String cargo_type_cd) {
		this.cargo_type_cd = cargo_type_cd;
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

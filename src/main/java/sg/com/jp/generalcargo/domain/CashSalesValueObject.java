package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class CashSalesValueObject {

	private String cash_receipt_nbr;
	private String receipt_dttm;
	private double gst_amt;
	private double total_amt;
	private double round_up_amt;
	private double total_cash_amt;
	private double total_cheq_amt;
	private String currency;
	private int total_page_nbr;
	private int total_item_nbr;
	private int total_cheq_item_nbr;
	private String cust_nm;
	private String add_l1;
	private String add_l2;
	private String add_city;
	private String add_post_cd;
	private String add_ctry_cd;
	private String phone_nbr;
	private String manual_cash_receipt_nbr;
	private String vv_cd;
	private String post_ind;
	private long post_dttm;
	private String status;
	private String last_modify_user_id;
	private long last_modify_dttm;
	private double min_bill_amt;
	private String collectedBy;
	private String sheetNo;
	private String closedAt;
	private String terminal;
	private double total_cash_nets_amt;
	private Timestamp paymtDttm;
	private double total_cash_card_amt;
	private double total_net_amt;
	// ThangTV added on Feb 25 2008
	private String csType;

	public String getCsType() {
		return csType;
	}

	public void setCsType(String csType) {
		this.csType = csType;
	}

	public double getTotal_net_amt() {
		return total_net_amt;
	}

	public void setTotal_net_amt(double total_net_amt) {
		this.total_net_amt = total_net_amt;
	}

	public double getTotal_cash_card_amt() {
		return total_cash_card_amt;
	}

	public void setTotal_cash_card_amt(double total_cash_card_amt) {
		this.total_cash_card_amt = total_cash_card_amt;
	}

	public String getCash_receipt_nbr() {
		return cash_receipt_nbr;
	}
	
	public String getCashReceiptNbr()
    {
        return cash_receipt_nbr;
    }

	public void setCash_receipt_nbr(String cash_receipt_nbr) {
		this.cash_receipt_nbr = cash_receipt_nbr;
	}

	public String getReceipt_dttm() {
		return receipt_dttm;
	}

	public void setReceipt_dttm(String receipt_dttm) {
		this.receipt_dttm = receipt_dttm;
	}

	public double getGst_amt() {
		return gst_amt;
	}

	public void setGst_amt(double gst_amt) {
		this.gst_amt = gst_amt;
	}

	public double getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(double total_amt) {
		this.total_amt = total_amt;
	}

	public double getRound_up_amt() {
		return round_up_amt;
	}

	public void setRound_up_amt(double round_up_amt) {
		this.round_up_amt = round_up_amt;
	}

	public double getTotal_cash_amt() {
		return total_cash_amt;
	}

	public void setTotal_cash_amt(double total_cash_amt) {
		this.total_cash_amt = total_cash_amt;
	}

	public double getTotal_cheq_amt() {
		return total_cheq_amt;
	}

	public void setTotal_cheq_amt(double total_cheq_amt) {
		this.total_cheq_amt = total_cheq_amt;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getTotal_page_nbr() {
		return total_page_nbr;
	}

	public void setTotal_page_nbr(int total_page_nbr) {
		this.total_page_nbr = total_page_nbr;
	}

	public int getTotal_item_nbr() {
		return total_item_nbr;
	}

	public void setTotal_item_nbr(int total_item_nbr) {
		this.total_item_nbr = total_item_nbr;
	}

	public int getTotal_cheq_item_nbr() {
		return total_cheq_item_nbr;
	}

	public void setTotal_cheq_item_nbr(int total_cheq_item_nbr) {
		this.total_cheq_item_nbr = total_cheq_item_nbr;
	}

	public String getCust_nm() {
		return cust_nm;
	}

	public void setCust_nm(String cust_nm) {
		this.cust_nm = cust_nm;
	}

	public String getAdd_l1() {
		return add_l1;
	}

	public void setAdd_l1(String add_l1) {
		this.add_l1 = add_l1;
	}

	public String getAdd_l2() {
		return add_l2;
	}

	public void setAdd_l2(String add_l2) {
		this.add_l2 = add_l2;
	}

	public String getAdd_city() {
		return add_city;
	}

	public void setAdd_city(String add_city) {
		this.add_city = add_city;
	}

	public String getAdd_post_cd() {
		return add_post_cd;
	}

	public void setAdd_post_cd(String add_post_cd) {
		this.add_post_cd = add_post_cd;
	}

	public String getAdd_ctry_cd() {
		return add_ctry_cd;
	}

	public void setAdd_ctry_cd(String add_ctry_cd) {
		this.add_ctry_cd = add_ctry_cd;
	}

	public String getPhone_nbr() {
		return phone_nbr;
	}

	public void setPhone_nbr(String phone_nbr) {
		this.phone_nbr = phone_nbr;
	}

	public String getManual_cash_receipt_nbr() {
		return manual_cash_receipt_nbr;
	}

	public void setManual_cash_receipt_nbr(String manual_cash_receipt_nbr) {
		this.manual_cash_receipt_nbr = manual_cash_receipt_nbr;
	}

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getPost_ind() {
		return post_ind;
	}

	public void setPost_ind(String post_ind) {
		this.post_ind = post_ind;
	}

	public long getPost_dttm() {
		return post_dttm;
	}

	public void setPost_dttm(long post_dttm) {
		this.post_dttm = post_dttm;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}

	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}

	public long getLast_modify_dttm() {
		return last_modify_dttm;
	}

	public void setLast_modify_dttm(long last_modify_dttm) {
		this.last_modify_dttm = last_modify_dttm;
	}

	public double getMin_bill_amt() {
		return min_bill_amt;
	}

	public void setMin_bill_amt(double min_bill_amt) {
		this.min_bill_amt = min_bill_amt;
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getSheetNo() {
		return sheetNo;
	}

	public void setSheetNo(String sheetNo) {
		this.sheetNo = sheetNo;
	}

	public String getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(String closedAt) {
		this.closedAt = closedAt;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public double getTotal_cash_nets_amt() {
		return total_cash_nets_amt;
	}

	public void setTotal_cash_nets_amt(double total_cash_nets_amt) {
		this.total_cash_nets_amt = total_cash_nets_amt;
	}

	public Timestamp getPaymtDttm() {
		return paymtDttm;
	}

	public void setPaymtDttm(Timestamp paymtDttm) {
		this.paymtDttm = paymtDttm;
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

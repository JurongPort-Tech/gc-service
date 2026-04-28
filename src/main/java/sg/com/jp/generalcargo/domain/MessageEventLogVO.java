package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageEventLogVO implements TopsIObject {
	private static final long serialVersionUID = 1L;

	public MessageEventLogVO() {

	}

	public Date getTxn_dttm() {
		return txn_dttm;
	}

	public void setTxn_dttm(Date txnDttm) {
		txn_dttm = txnDttm;
	}

	public String getReg_seq_nbr() {
		return reg_seq_nbr;
	}

	public void setReg_seq_nbr(String regSeqNbr) {
		reg_seq_nbr = regSeqNbr;
	}

	public String getAddr_book_seq_nbr() {
		return addr_book_seq_nbr;
	}

	public void setAddr_book_seq_nbr(String addrBookSeqNbr) {
		addr_book_seq_nbr = addrBookSeqNbr;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alertType) {
		alert_type = alertType;
	}

	public String getHp_nbr() {
		return hp_nbr;
	}

	public void setHp_nbr(String hpNbr) {
		hp_nbr = hpNbr;
	}

	public String getEmail_addr() {
		return email_addr;
	}

	public void setEmail_addr(String emailAddr) {
		email_addr = emailAddr;
	}

	public String getAlert_nm() {
		return alert_nm;
	}

	public void setAlert_nm(String alertNm) {
		alert_nm = alertNm;
	}

	public String getAlert_content() {
		return alert_content;
	}

	public void setAlert_content(String alertContent) {
		alert_content = alertContent;
	}

	public String getCust_cd() {
		return cust_cd;
	}

	public void setCust_cd(String custCd) {
		cust_cd = custCd;
	}

	public String getAlert_category() {
		return alert_category;
	}

	public void setAlert_category(String alertCategory) {
		alert_category = alertCategory;
	}

	public String getFile_nm() {
		return file_nm;
	}

	public void setFile_nm(String fileNm) {
		file_nm = fileNm;
	}

	public String getAlert_seq_nbr() {
		return alert_seq_nbr;
	}

	public void setAlert_seq_nbr(String alertSeqNbr) {
		alert_seq_nbr = alertSeqNbr;
	}

	public String getAlert_cd() {
		return alert_cd;
	}

	public void setAlert_cd(String alertCd) {
		alert_cd = alertCd;
	}

	private Date txn_dttm;
	private String reg_seq_nbr;
	private String addr_book_seq_nbr;
	private String alert_type;
	private String hp_nbr;
	private String email_addr;
	private String alert_nm;
	private String alert_content;
	private String cust_cd;
	private String alert_category;
	private String file_nm;
	private String alert_seq_nbr;
	private String alert_cd;

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}

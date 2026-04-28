package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StoreRentCrReport implements Serializable {

	public static final long serialVersionUID = 1L;
	private String discVoy;
	private String loadVvCd;
	private String loadVoy;
	private Integer pkgs;
	private BigDecimal BillTon;
	private String acct_nbr;
	private String StoreRentDays;
	private String TotalAmount;
	private String sno1;
	private String discVvCd;

	public String getDiscVoy() {
		return discVoy;
	}

	public void setDiscVoy(String discVoy) {
		this.discVoy = discVoy;
	}

	public String getLoadVvCd() {
		return loadVvCd;
	}

	public void setLoadVvCd(String loadVvCd) {
		this.loadVvCd = loadVvCd;
	}

	public String getLoadVoy() {
		return loadVoy;
	}

	public void setLoadVoy(String loadVoy) {
		this.loadVoy = loadVoy;
	}

	public Integer getPkgs() {
		return pkgs;
	}

	public void setPkgs(Integer pkgs) {
		this.pkgs = pkgs;
	}

	public BigDecimal getBillTon() {
		return BillTon;
	}

	public void setBillTon(BigDecimal BillTon) {
		this.BillTon = BillTon;
	}

	public String getAcct_nbr() {
		return acct_nbr;
	}

	public void setAcct_nbr(String acct_nbr) {
		this.acct_nbr = acct_nbr;
	}

	public String getStoreRentDays() {
		return StoreRentDays;
	}

	public void setStoreRentDays(String StoreRentDays) {
		this.StoreRentDays = StoreRentDays;
	}

	public String getTotalAmount() {
		return TotalAmount;
	}

	public void setTotalAmount(String TotalAmount) {
		this.TotalAmount = TotalAmount;
	}

	public String getSno1() {
		return sno1;
	}

	public void setSno1(String sno1) {
		this.sno1 = sno1;
	}

	public String getDiscVvCd() {
		return discVvCd;
	}

	public void setDiscVvCd(String discVvCd) {
		this.discVvCd = discVvCd;
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

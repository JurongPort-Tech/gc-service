package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BillContractInfoVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	private String customer;
	private String account;
	private String contract;
	private Timestamp start;
	private Timestamp end;
	private double creditLimit;
	private int creditDays;
	private String remarks;
	private List<TariffContractPeriodVO> a = new ArrayList<TariffContractPeriodVO>(10);
	private PeriodSorter sorter = new PeriodSorter();

	public BillContractInfoVO() {
		reset();
	}

	public void reset() {
		contract = null;
		start = null;
		end = null;
		creditLimit = 0;
		creditDays = 0;
		remarks = null;
		a.clear();
	}

	public boolean isModified() {
		return (this.contract == null);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Contract : ");
		sb.append(contract);
		return sb.toString();
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public void setStart(Timestamp start) {
		this.start = start;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public void setCreditDays(int creditDays) {
		this.creditDays = creditDays;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCustomer() {
		return (this.customer);
	}

	public String getAccount() {
		return (this.account);
	}

	public String getContract() {
		return (this.contract);
	}

	public Timestamp getStart() {
		return (this.start);
	}

	public Timestamp getEnd() {
		return (this.end);
	}

	public double getCreditLimit() {
		return (this.creditLimit);
	}

	public int getCreditDays() {
		return (this.creditDays);
	}

	public String getRemarks() {
		return (this.remarks);
	}

	public int getPeriodCount() {
		return a.size();
	}

	public TariffContractPeriodVO getPeriod(int index) {
		if (index < a.size()) {
			return (TariffContractPeriodVO) a.get(index);
		} else {
			return null;
		}
	}

	public void addPeriod(TariffContractPeriodVO vo) {
		a.add(vo);
		if (a.size() > 1)
			sort();
	}

	public TariffContractPeriodVO removePeriod(int index) {
		return (TariffContractPeriodVO) a.remove(index);
	}

	public void removeAllPeriod() {
		a.clear();
	}

	public void setAllPeriod(List<TariffContractPeriodVO> a) {
		if (a != null) {
			this.a = a;
		}
	}

	public Collection<TariffContractPeriodVO> getAllPeriod() {
		return (Collection<TariffContractPeriodVO>) a;
	}

	public void sort() {
		Collections.sort(a, sorter);
	}

	public class PeriodSorter implements Comparator<Object>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(Object o1, Object o2) {
			TariffContractPeriodVO c1 = (TariffContractPeriodVO) o1;
			TariffContractPeriodVO c2 = (TariffContractPeriodVO) o2;
			String s1 = c1.getPeriodFrom().toString();
			String s2 = c2.getPeriodFrom().toString();
			return s1.compareTo(s2);
		}
	}
}

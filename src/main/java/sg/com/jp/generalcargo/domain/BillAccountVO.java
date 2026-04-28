package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.List;

public class BillAccountVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	// member variable
	private String customer;
	private String account;
	private double gstAmount;
	private double totalAmount;
	private BillCustomerInfoVO customerInfo;

	private List<BillItemVO> a = new ArrayList<BillItemVO>(50);

	public BillAccountVO() {
		customer = null;
		account = null;
	}

	// get set methods
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setCustomerInfo(BillCustomerInfoVO customerInfo) {
		this.customerInfo = customerInfo;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCustomer() {
		return (this.customer);
	}

	public String getAccount() {
		return (this.account);
	}

	public BillCustomerInfoVO getCustomerInfo() {
		return (this.customerInfo);
	}

	public double getGstAmount() {
		return (this.gstAmount);
	}

	public double getTotalAmount() {
		return (this.totalAmount);
	}

	public boolean isModified() {
		return (customer != null && account != null);
	}

	// tier methods
	public int getItemCount() {
		return a.size();
	}

	public void addItem(BillItemVO ivo) {
		a.add(ivo);
	}

	private BillItemVO getItem(int index) {
		if (index >= a.size() || index < 0)
			return null;
		return (BillItemVO) a.get(index);
	}

	public BillItemVO[] getAllItem() {
		if (a.size() < 1)
			return null;
		BillItemVO[] vo = new BillItemVO[a.size()];
		for (int i = 0; i < a.size(); i++) {
			vo[i] = (BillItemVO) a.get(i);
		}
		return vo;
	}

	public void setAllItem(List<BillItemVO> newA) {
		if (newA != null)
			this.a = newA;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t");
		sb.append("Customer   : ");
		sb.append(this.getCustomer());
		sb.append("\n");
		sb.append("\t");
		sb.append("Account    : ");
		sb.append(this.getAccount());
		sb.append("\n");
		sb.append("\t");
		sb.append("Amount     : ");
		sb.append(this.getTotalAmount() + this.getGstAmount());
		sb.append("\n");
		for (int i = 0; i < this.getItemCount(); i++) {
			sb.append(this.getItem(i).toString());
		}
		return sb.toString();
	}
}

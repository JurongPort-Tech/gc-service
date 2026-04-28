package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;

public class AdjustedBillItemVO extends UserTimestampVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private double totalTime;
	private double totalOtherUnit;
	private int totalContainer;
	private double unitRate;
	private double adjUnitRate; // CR-CAB-20050518-01 - Added by Mickeal
	private double gst;
	private double gstAmount;
	private double totalAmount;
	private String status;
	private String remarks;
	private String itemDescription;
	// Added by Valli CR-CAB-20050823-02
	private String tariffMainCat;
	private String tariffSubCat;

	private ArrayList<BillContainerVO> a = new ArrayList<BillContainerVO>();
	
	public AdjustedBillItemVO(){
		id = -1;
	}
	public void doGet(Object object) {}
	public void doSet(Object object) {}

	// get set methods
	public void setId(int id) { this.id = id; }
	public void setTotalTime(double totalTime) { this.totalTime = totalTime; }
	public void setTotalOtherUnit(double totalOtherUnit) { this.totalOtherUnit = totalOtherUnit; }
	public void setTotalContainer(int totalContainer) { this.totalContainer = totalContainer; }
	public void setUnitRate(double unitRate) { this.unitRate = unitRate; }
	public void setGst(double gst) { this.gst = gst; }
	public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }
	public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
	public void setStatus(String status) { this.status = status; }
	public void setRemarks(String remarks){ this.remarks = remarks; }
	public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
	public void setTariffMainCat(String tariffMainCat) { this.tariffMainCat = tariffMainCat; }
	public void setTariffSubCat(String tariffSubCat) { this.tariffSubCat = tariffSubCat; }

	public int getId() { return (this.id); }
	public double getTotalTime() { return (this.totalTime); }
	public double getTotalOtherUnit() { return (this.totalOtherUnit); }
	public int getTotalContainer() { return (this.totalContainer); }
	public double getUnitRate() { return (this.unitRate); }
	public double getGst() { return (this.gst); }
	public double getGstAmount() { return (this.gstAmount); }
	public double getTotalAmount() { return (this.totalAmount); }
	public String getStatus() { return (this.status); }
	public String getRemarks() { return (this.remarks); }
	public String getItemDescription() { return (this.itemDescription); }
	public String getTariffMainCat() { return (this.tariffMainCat); }
	public String getTariffSubCat() { return (this.tariffSubCat); }
	
	public boolean isModified(){
		return (id > 0);
	}
	// CR-CAB-20050518-01 - Added by Mickeal
	public double getAdjUnitRate() {
		return adjUnitRate;
	}
	public void setAdjUnitRate(double adjUnitRate) {
		this.adjUnitRate = adjUnitRate;
	}
	// CR-CAB-20050518-01 - Added by Mickeal
	// tier methods
	public int getContainerCount(){
		return a.size();
	}
	
	
	public void addContainer(BillContainerVO containerInfo){
		a.add(containerInfo);
	}
	public BillContainerVO getContainer(int index){
		if (index >= a.size() || index < 0)
			return null;
		return (BillContainerVO)a.get(index);
	}
	public BillContainerVO[] getAllContainer(){
		if (a.size() < 1) return null;
		BillContainerVO[] vo = new BillContainerVO[a.size()];
		for (int i=0; i<a.size(); i++){
			vo[i] = (BillContainerVO)a.get(i);
		}
		return vo;
	}
	public BillContainerVO removeItem(int index){
		return (BillContainerVO)a.remove(index);
	}
	public void removeAllItem(){
		a.clear();
	}
	public void setAllItem(ArrayList<BillContainerVO> newA){
		if (newA != null)
			this.a = newA;
	}
	/*
	public String toString(){
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
		for (int i=0; i<this.getItemCount(); i++){
			sb.append(this.getItem(i).toString());
		}
		return sb.toString();
	}
	
	public void copy(BillAccountVO acct){
		this.account = acct.getAccount();
		this.customer = acct.getCustomer();
		this.gstAmount = acct.getGstAmount();
		this.totalAmount = acct.getTotalAmount();
		this.lastModifyUserId = acct.getUser();
		this.lastModifyTimestamp = acct.getTimestamp();
		
		this.removeAllItem();
		for (int i=0; i<acct.getItemCount(); i++){
			BillItemVO item = new BillItemVO(acct.getItem(i));
			this.addItem(item);
		}
	}
	*/
}

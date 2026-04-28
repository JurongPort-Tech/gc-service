package sg.com.jp.generalcargo.domain;

public interface BillAdjustParam {
	
	/** Reset the state */
	public void reset();

	/** Setter methods */
	public void setTotalContainer(int totalContainer);
	public void setTotalTime(double totalTime);
	public void setTotalOtherUnit(double totalOtherUnit);
	public void setUnitRate(double unitRate);
	public void setGst(double gst);
	
	/** getter methods */
	public int getTotalContainer();
	public double getTotalTime();
	public double getTotalOtherUnit();
	public double getUnitRate();
	public double getGst();
	
	/** get the gst amount*/
	public double getGstAmount();
	
	/** get the total amount*/
	public double getTotalAmount();
}
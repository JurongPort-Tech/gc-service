package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class BillAbstractAdjParamVO extends UserTimestampVO 
	implements BillAdjustParam {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(BillAbstractAdjParamVO.class);
	public static final boolean DEBUG = false;
	public static final boolean INFO = false;

	public static final String BILL_NBR = "billNbr";
	public static final String BILL_DATE = "billDate";
	public static final String CUSTOMER = "customer";
	public static final String ACCOUNT = "account";
	public static final String ITEM_NBR = "itemNbr";
	public static final String TOTAL_CNTR = "totalCntr";
	public static final String TOTAL_TIME = "totalTime";
	public static final String TOTAL_OTHER = "totalOther";
	public static final String UNIT_RATE = "unitRate";
	public static final String GST = "gst";
	public static final String GST_AMT = "gstAmt";
	public static final String TOTAL_AMT = "totalAmt";
	public static final String REMARKS = "remarks";


	// member variable
	protected Timestamp billDate;
	protected String billNumber; // number
	protected String customer;
	protected String account;
	protected int itemNumber;
	protected int totalContainer;
	protected double totalTime;
	protected double totalOtherUnit;
	protected double unitRate;
	protected double gst;
	protected String remarks;
	protected String description;
	
	// html parameters
	protected int size;
	protected int maxlength;
	
	protected StringBuffer sb = new StringBuffer();

    public BillAbstractAdjParamVO() {
    }
	public void doGet(Object object) {}
	public void doSet(Object object) {}

	private static SimpleDateFormat dateFmt;
	static{
		dateFmt = new SimpleDateFormat("dd/MM/yyyy");
	}

	private final int LOG_INFO = 1;
	private final int LOG_TXN = 2;
	private final int LOG_WARN = 3;
	private final int LOG_DEBUG = 4;
	private final int LOG_ERROR = 5;
	private final int LOG_FATAL = 6;
	private boolean showMsg = false;

	/**
	 * Logs the message to the log file.
	 */
	private void log(String s, int type){
		String msg = "BPVO: " + s;
		if (showMsg){
			log.info(msg);
		}else{
			switch(type){
				case LOG_INFO:
					log.info(msg);
					break;
				case LOG_ERROR:
					log.info(msg);
					break;
				case LOG_DEBUG:
					log.info(msg);
					break;
				case LOG_FATAL:
					log.info(msg);
					break;
				case LOG_TXN:
					log.info(msg);
					break;
				case LOG_WARN:
					log.info(msg);
					break;
			}
		}
	}

	/**
	 * Get the necessary parameter from the request.
	 */
	public void getValue(HttpServletRequest request){
		billNumber = request.getParameter(BILL_NBR);
		
		if (billNumber != null && billNumber.equals("")){
			billNumber = null;
		}
		
		String s = null;
		try{
			s = request.getParameter(BILL_DATE);
			billDate = Timestamp.valueOf(s);
		}catch(Exception e){
			log("Parse dateFrom failed - " + e.getMessage(), LOG_INFO);
		}
		
		customer = request.getParameter(CUSTOMER);
		account = request.getParameter(ACCOUNT);
		
		try{
			s = request.getParameter(ITEM_NBR);
			itemNumber = Integer.parseInt(s);
		}catch(Exception e){}
		
		try{
			s = request.getParameter(TOTAL_CNTR);
			this.totalContainer = Integer.parseInt(s);
		}catch(Exception e){}
		
		try{
			s = request.getParameter(TOTAL_TIME);
			this.totalTime = Double.parseDouble(s);
		}catch(Exception e){}

		try{
			s = request.getParameter(TOTAL_OTHER);
			this.totalOtherUnit = Double.parseDouble(s);
		}catch(Exception e){}

		try{
			s = request.getParameter(UNIT_RATE);
			this.unitRate = Double.parseDouble(s);
		}catch(Exception e){}

		try{
			s = request.getParameter(GST);
			this.gst = Double.parseDouble(s);
		}catch(Exception e){}
		
		s = request.getParameter(REMARKS);
		if (s != null){
			this.remarks = s;
		}else{
			this.remarks = "";
		}
	}

	/**
	 * Reset the state.
	 */
	public void reset(){
		billDate = null;
		billNumber = null;
		this.customer = null;
		this.account = null;
		this.itemNumber = 0;
		this.totalContainer = 0;
		this.totalTime = 0.0;
		this.totalOtherUnit = 0.0;
		this.unitRate = 0.0;
		this.gst = 0.0;
		this.remarks = null;
		sb.setLength(0);
	}

	/**
	 * Getter and Setter methods
	 */
	public void setBillDate(Timestamp billDate) { this.billDate = billDate; }
	public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
	public void setCustomer(String customer) { this.customer = customer; }
	public void setAccount(String account) { this.account = account; }
	public void setItemNumber(int itemNumber){ this.itemNumber = itemNumber; }
	public void setTotalContainer(int totalContainer) { this.totalContainer = totalContainer; }
	public void setTotalTime(double totalTime) { this.totalTime = totalTime; }
	public void setTotalOtherUnit(double totalOtherUnit) { this.totalOtherUnit = totalOtherUnit; }
	public void setUnitRate(double unitRate) { this.unitRate = unitRate; }
	public void setGst(double gst) { this.gst = gst; }
	public void setRemarks(String remarks){ this.remarks = remarks; }
	public void setDescription(String description){ this.description = description; }

	public Timestamp getBillDate() { return (this.billDate); }
	public String getBillNumber() { return (this.billNumber); }
	public String getCustomer() { return (this.customer); }
	public String getAccount() { return (this.account); }
	public int getItemNumber() { return (this.itemNumber); }
	public int getTotalContainer() { return (this.totalContainer); }
	public double getTotalTime() { return (this.totalTime); }
	public double getTotalOtherUnit() { return (this.totalOtherUnit); }
	public double getUnitRate() { return (this.unitRate); }
	public double getGst() { return (this.gst); }
	public String getRemarks(){ return (this.remarks); }
	public String getDescription(){ return (this.description); }
	
	public double getGstAmount(){
		BigDecimal gst = new BigDecimal("" + this.getGst());
		BigDecimal total = new BigDecimal("" + this.getTotalAmount());
		BigDecimal base = new BigDecimal("" + 100.0);
		if (DEBUG) log.info("Gst   : " + gst.toString());
		if (DEBUG) log.info("Total : " + total.toString());
		if (DEBUG) log.info("Base  : " + base.toString());
		BigDecimal result = gst.multiply(total).divide(base, BigDecimal.ROUND_HALF_UP, 5);
		if (DEBUG) log.info("Result: " + result.toString());
		double retVal = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (DEBUG) log.info("RetVal: " + retVal);
		return retVal;
		//return (new BigDecimal(""+this.gst).multiply(new BigDecimal(""+this.getTotalAmount())).divide(new BigDecimal("100"),BigDecimal.ROUND_HALF_UP)).doubleValue();
	}
	public abstract double getTotalAmount();

	/**
	 * Return a AdjustedBillItemVO
	 */
	public AdjustedBillItemVO getVO(){
		AdjustedBillItemVO ivo = new AdjustedBillItemVO();
		ivo.setId(this.getItemNumber());
		ivo.setTotalContainer(this.getTotalContainer());
		ivo.setTotalOtherUnit(this.getTotalOtherUnit());
		ivo.setTotalTime(this.getTotalTime());
		ivo.setUnitRate(this.getUnitRate());
		ivo.setGst(this.getGst());
		ivo.setGstAmount(this.getGstAmount());
		ivo.setTotalAmount(this.getTotalAmount());
		return ivo;
	}

	
	//public abstract String writeTotalContainer();
	//public abstract String writeTotalTime();
	//public abstract String writeTotalOtherUnit();
	
	public abstract String writeJS();
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}
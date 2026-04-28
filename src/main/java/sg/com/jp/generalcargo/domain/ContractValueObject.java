package sg.com.jp.generalcargo.domain;
import java.sql.Timestamp;
import java.text.NumberFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.TimeFunction;

public class ContractValueObject implements TopsIValueObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String CUST_CD;
    private String ACCT_NBR;
    private String CONTRACT_NBR;
    private String BUSINESS_TYPE;
    private Timestamp COMMENCE_DTTM;
    private Timestamp EXPIRY_DTTM;
    private String CREDIT_RATING;
    private float CREDIT_LIMIT;
    private int CREDIT_DAYS;
    private float INT_RATE;
    private int MTG;
    private int NBR_PRIORITY_BERTH;
    private int NBR_BERTH_ALLOCATED;
    private int BERTH_WINDOW;
    private int VSL_PRODUCT_RATE;
// [0.2] Changed in DB schema ver 3.1
    private float TOTAL_THROUGHPUT_ITH;
// [0.2] Changed in DB schema ver 3.1
    private float TS_THROUGHPUT_ITH;
    private String ITH_UNIT;
    private int MAX_THROUGHPUT_VAL;
    private float APPL_ITH_FEE;
    private String REMARKS;
    private String LAST_MODIFY_USER_ID;
    private Timestamp LAST_MODIFY_DTTM;
// [0.2] New in DB schema ver 3.1
    private int CUST_TARIFF_VERSION_NBR;
// [0.2] New in DB schema ver 3.1
    private int TOTAL_NBR_SVCS;
// [0.4] Operator code
    private String opCode;
// [0.6] MTG unit field
    private String MTG_UNIT;
// [0.7]
    private NumberFormat nf = NumberFormat.getInstance();
    
    public static final String NO_CONTRACT_INDICATOR = "*N_CNTRCT*";

    private String CreateUserID;
    private String PostInd;
    private Timestamp CreateDateTimestamp;    
    
    /** Creates new ContractValueObject */
    public ContractValueObject() {
    }

    public void doGet(Object object) {
    }
    
    public void doSet(Object object) {
    }
    
    public void setCustomerCode(String code) {
        CUST_CD = code;
    }
    
    public String getCustomerCode() {
        return CUST_CD;
    }
    
    public void setAccountNumber(String nbr) {
        ACCT_NBR = nbr;
    }
    
    public String getAccountNumber() {
        return ACCT_NBR;
    }
    
    public void setContractNumber(String nbr) {
        CONTRACT_NBR = nbr;
    }
    
    public String getContractNumber() {
        return CONTRACT_NBR;
    }
    
    public void setBusinessType(String type) {
        BUSINESS_TYPE = type;
    }
    
    public String getBusinessType() {
        return BUSINESS_TYPE;
    }
    
    public void setCommenceDate(Timestamp timestamp) {
        COMMENCE_DTTM = timestamp;
    }
    
    public Timestamp getCommenceDate() {
        return COMMENCE_DTTM;
    }
    
    public void setExpiryDate(Timestamp timestamp) {
        EXPIRY_DTTM = timestamp;
    }
    
    public Timestamp getExpiryDate() {
        return EXPIRY_DTTM;
    }
    
    public void setCreditRating(String rate) {
        CREDIT_RATING = rate;
    }
    
    public String getCreditRating() {
        return CREDIT_RATING;
    }

    public void setCreditLimit(float limit) {
        CREDIT_LIMIT = limit;
    }
    
    public float getCreditLimit() {
        return CREDIT_LIMIT;
    }
    
    public void setCreditDays(int days) {
        CREDIT_DAYS = days;
    }
    
    public int getCreditDays() {
        return CREDIT_DAYS;
    }
    
    public void setInterestRate(float rate) {
        INT_RATE = rate;
    }
    
    public float getInterestRate() {
        return INT_RATE;
    }
    
    public void setMTG(int nbr) {
        MTG = nbr;
    }
    
    public int getMTG() {
        return MTG;
    }
    
    public void setNumberOfPriorityBerth(int nbr) {
        NBR_PRIORITY_BERTH = nbr;
    }
    
    public int getNumberOfPriorityBerth() {
        return NBR_PRIORITY_BERTH;
    }
    
    public void setNumberOfBerthAllocated(int nbr) {
        NBR_BERTH_ALLOCATED = nbr;
    }
    
    public int getNumberOfBerthAllocated() {
        return NBR_BERTH_ALLOCATED;
    }
    
    public void setBerthWindow(int nbr) {
        BERTH_WINDOW = nbr;
    }
    
    public int getBerthWindow() {
        return BERTH_WINDOW;
    }
    
    public void setVesselProductivityRate(int rate) {
        VSL_PRODUCT_RATE = rate;
    }
    
    public int getVesselProductivityRate() {
        return VSL_PRODUCT_RATE;
    }
    
// [0.2] Changed in DB schema ver 3.1
    public void setTotalThroughputITH(float val) {
        TOTAL_THROUGHPUT_ITH = val;
    }
    
// [0.2] Changed in DB schema ver 3.1
    public float getTotalThroughputITH() {
        return TOTAL_THROUGHPUT_ITH;
    }
    
// [0.2] Changed in DB schema ver 3.1
    public void setTranshipmentThroughputITH(float val) {
        TS_THROUGHPUT_ITH = val;
    }
    
// [0.2] Changed in DB schema ver 3.1
    public float getTranshipmentThroughputITH() {
        return TS_THROUGHPUT_ITH;
    }
    
    public void setITHUnit(String unit) {
        ITH_UNIT = unit;
    }
    
    public String getITHUnit() {
        return ITH_UNIT;
    }
    
    public void setMaxThroughputValue(int val) {
        MAX_THROUGHPUT_VAL = val;
    }
    
    public int getMaxThroughputValue() {
        return MAX_THROUGHPUT_VAL;
    }
    
    public void setApplicableITHFee(float val) {
        APPL_ITH_FEE = val;
    }
    
    public float getApplicableITHFee() {
        return APPL_ITH_FEE;
    }
    
    public void setRemarks(String rem) {
        REMARKS = rem;
    }
    
    public String getRemarks() {
        return REMARKS;
    }
    
    public void setLastUpdateUserID(String userID) {
        LAST_MODIFY_USER_ID = userID;
    }
    
    public String getLastUpdateUserID() {
        return LAST_MODIFY_USER_ID;
    }
    
    public void setLastUpdateTimestamp(Timestamp timestamp) {
        LAST_MODIFY_DTTM = timestamp;
    }
    
    public Timestamp getLastUpdateTimestamp() {
        return LAST_MODIFY_DTTM;
    }

// [0.2] New in DB schema ver 3.1
    public void setCustomerTariffVersion(int nbr) {
        CUST_TARIFF_VERSION_NBR = nbr;
    }
    
// [0.2] New in DB schema ver 3.1
    public int getCustomerTariffVersion() {
        return CUST_TARIFF_VERSION_NBR;
    }
    
// [0.2] New in DB schema ver 3.1
    public void setTotalServices(int nbr) {
        TOTAL_NBR_SVCS = nbr;
    }
    
// [0.2] New in DB schema ver 3.1
    public int getTotalServices() {
        return TOTAL_NBR_SVCS;
    }

// [0.4] Operator code
    public void setOperatorCode(String opCode) {
        this.opCode = opCode;
    }
    
// [0.4] Operator code
    public String getOperatorCode() {
        return opCode;
    }

// [0.6] MTG unit field
    public void setMTGUnit(String unit) {
        MTG_UNIT = unit;
    }
    
// [0.6] MTG unit field
    public String getMTGUnit() {
        return MTG_UNIT;
    }
    public void setPostInd(String PostInd) {
        this.PostInd = PostInd;
    }
    
    public String getPostInd() {
        return PostInd;
    }
    
    public void setCreateUserID(String userID) {
        CreateUserID = userID;
    }
    
    public String getCreateUserID() {
        return CreateUserID;
    }
    public void setCreateDateTimestamp(Timestamp timestamp) {
        CreateDateTimestamp = timestamp;
    }
    
    public Timestamp getCreateDateTimestamp() {
        return CreateDateTimestamp;
    }
    
    /**
     * Converts the value object into a data array.
     *
     * @return A string array.
     */
    public String[] convertToArray() {
// [0.7]
        nf.setMinimumFractionDigits(2);
        
        String[] dataArray = new String[27];
        dataArray[0] = this.getCustomerCode();
        dataArray[1] = this.getAccountNumber();
        dataArray[2] = this.getContractNumber();
        dataArray[3] = this.getBusinessType();
        dataArray[4] = TimeFunction.convertDBTimestampToDate(
            this.getCommenceDate(), "/", true);
        dataArray[5] = TimeFunction.convertDBTimestampToDate(
            this.getExpiryDate(), "/", true);
        dataArray[6] = this.getCreditRating();
        dataArray[7] = nf.format(this.getCreditLimit());
        dataArray[8] = Integer.toString(this.getCreditDays());
        dataArray[9] = nf.format(this.getInterestRate());
        dataArray[10] = Integer.toString(this.getMTG());
        dataArray[11] = Integer.toString(this.getNumberOfPriorityBerth());
        dataArray[12] = Integer.toString(this.getNumberOfBerthAllocated());
        dataArray[13] = Integer.toString(this.getBerthWindow());
        dataArray[14] = Integer.toString(this.getVesselProductivityRate());
// [0.2] Changed in DB schema ver 3.1
        dataArray[15] = nf.format(this.getTotalThroughputITH());
// [0.2] Changed in DB schema ver 3.1
        dataArray[16] = nf.format(this.getTranshipmentThroughputITH());
        dataArray[17] = this.getITHUnit();
        dataArray[18] = Integer.toString(this.getMaxThroughputValue());
        dataArray[19] = nf.format(this.getApplicableITHFee());
        dataArray[20] = this.getRemarks();
        dataArray[21] = this.getLastUpdateUserID();
        dataArray[22] = TimeFunction.convertDBTimestampToDate(
            this.getLastUpdateTimestamp(), "/", true);
// [0.2] New in DB schema ver 3.1
        dataArray[23] = Integer.toString(this.getCustomerTariffVersion());
// [0.2] New in DB schema ver 3.1
        dataArray[24] = Integer.toString(this.getTotalServices());
// [0.4] Operator code
        dataArray[25] = this.getOperatorCode();
// [0.6] MTG unit field
        dataArray[26] = this.getMTGUnit();
//DebugOut.println("[ContractValueObject:convertToArray] CUST_CD : '" + dataArray[0] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] ACCT_NBR : '" + dataArray[1] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] CONTRACT_NBR : '" + dataArray[2] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] BUSINESS_TYPE : '" + dataArray[3] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] COMMENCE_DTTM : '" + dataArray[4] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] EXPIRY_DTTM : '" + dataArray[5] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] CREDIT_RATING : '" + dataArray[6] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] CREDIT_LIMIT : '" + dataArray[7] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] CREDIT_DAYS : '" + dataArray[8] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] INT_RATE : '" + dataArray[9] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] MTG : '" + dataArray[10] + "'");
//// [0.6] MTG unit field
//DebugOut.println("[ContractValueObject:convertToArray] MTG_UNIT : '" + dataArray[26] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] NBR_PRIORITY_BERTH : '" + dataArray[11] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] NBR_BERTH_ALLOCATED : '" + dataArray[12] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] BERTH_WINDOW : '" + dataArray[13] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] VSL_PRODUCT_RATE : '" + dataArray[14] + "'");
//// [0.2] Changed in DB schema ver 3.1
//DebugOut.println("[ContractValueObject:convertToArray] TOTAL_THROUGHPUT_ITH : '" + dataArray[15] + "'");
//// [0.2] Changed in DB schema ver 3.1
//DebugOut.println("[ContractValueObject:convertToArray] TS_THROUGHPUT_ITH : '" + dataArray[16] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] ITH_UNIT : '" + dataArray[17] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] MAX_THROUGHPUT_VAL : '" + dataArray[18] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] APPL_ITH_FEE : '" + dataArray[19] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] REMARKS : '" + dataArray[20] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] LAST_MODIFY_USER_ID : '" + dataArray[21] + "'");
//DebugOut.println("[ContractValueObject:convertToArray] LAST_MODIFY_DTTM : '" + dataArray[22] + "'");
//// [0.2] New in DB schema ver 3.1
//DebugOut.println("[ContractValueObject:convertToArray] CUST_TARIFF_VERSION_NBR : '" + dataArray[23] + "'");
//// [0.2] New in DB schema ver 3.1
//DebugOut.println("[ContractValueObject:convertToArray] TOTAL_NBR_SVCS : '" + dataArray[24] + "'");
//// [0.4] Operator code
//DebugOut.println("[ContractValueObject:convertToArray] opCode : '" + dataArray[25] + "'");

        return dataArray;
    }
	
	public boolean isModified(ContractValueObject contractValueObject) {
		ContractValueObject origVO = new ContractValueObject();

		if (contractValueObject.getCustomerCode()				== origVO.getCustomerCode() && 
			contractValueObject.getAccountNumber()				== origVO.getAccountNumber() &&
			contractValueObject.getContractNumber()				== origVO.getContractNumber() &&
			contractValueObject.getBusinessType()				== origVO.getBusinessType() &&
			contractValueObject.getCommenceDate()				== origVO.getCommenceDate() &&
			contractValueObject.getExpiryDate()					== origVO.getExpiryDate() &&
			contractValueObject.getCreditRating()				== origVO.getCreditRating() &&
			contractValueObject.getCreditLimit()				== origVO.getCreditLimit() &&
			contractValueObject.getCreditDays()					== origVO.getCreditDays() &&
			contractValueObject.getInterestRate()				== origVO.getInterestRate() &&
			contractValueObject.getMTG()						== origVO.getMTG() &&
			contractValueObject.getNumberOfPriorityBerth()      == origVO.getNumberOfPriorityBerth() &&
			contractValueObject.getNumberOfBerthAllocated()		== origVO.getNumberOfBerthAllocated() &&
			contractValueObject.getBerthWindow()				== origVO.getBerthWindow() &&
			contractValueObject.getVesselProductivityRate()     == origVO.getVesselProductivityRate() &&
			contractValueObject.getTotalThroughputITH()			== origVO.getTotalThroughputITH() &&
			contractValueObject.getTranshipmentThroughputITH()	== origVO.getTranshipmentThroughputITH() &&
			contractValueObject.getITHUnit()					== origVO.getITHUnit() &&
			contractValueObject.getMaxThroughputValue()			== origVO.getMaxThroughputValue() &&
			contractValueObject.getApplicableITHFee()			== origVO.getApplicableITHFee() &&
			contractValueObject.getRemarks()					== origVO.getRemarks() &&
			contractValueObject.getLastUpdateUserID()			== origVO.getLastUpdateUserID() &&
			contractValueObject.getLastUpdateTimestamp()		== origVO.getLastUpdateTimestamp() && 
			contractValueObject.getCustomerTariffVersion()      == origVO.getCustomerTariffVersion() &&
			contractValueObject.getTotalServices()				== origVO.getTotalServices() &&
			contractValueObject.getOperatorCode()				== origVO.getOperatorCode() &&
			contractValueObject.getMTGUnit()					== origVO.getMTGUnit() &&
			contractValueObject.getPostInd()					== origVO.getPostInd() &&
			contractValueObject.getCreateUserID()				== origVO.getCreateUserID() &&
			contractValueObject.getCreateDateTimestamp()		== origVO.getCreateDateTimestamp())
			return false;
		else
			return true;
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

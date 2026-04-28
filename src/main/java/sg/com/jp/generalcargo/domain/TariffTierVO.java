package sg.com.jp.generalcargo.domain;

public class TariffTierVO extends UserTimestampVO {
	
	private static final long serialVersionUID = 1L;	
	// public static variable to map to database name
	public static final String TABLE_NAME = "tariff_tier";
	public static final String ID = "tier_seq_nbr";
	public static final String RANGE_FROM = "fr_range";
	public static final String RANGE_TO = "to_range";
	public static final String PER_UNIT = "other_rate";
	public static final String PER_UNIT_TYPE = "other_rate_type";
	public static final String PER_HOUR = "hour_rate";
	public static final String PER_HOUR_TYPE = "hour_rate_type";
	public static final String RATE = "amt_charge";
	public static final String ADJUSTMENT = "adj_amt";
	public static final String ADJUSTMENT_TYPE = "adj_type";
	public static final String PERCENT_CHARGE = "percent_charge";

	// member variable
	protected int id; // sequence number
	protected double rangeFrom;
	protected double rangeTo;
	protected double perHour; // per how many hour unit (in hours)
	protected double perUnit; // per how many other unit
	protected double rate; // the rate
	protected double percentCharge; // if certain condition applies
	protected double adjustment;
	protected String adjustmentType;
    protected double timeUnit;
    protected double otherUnit;
    protected String perHourType; // in hours (H) or min(M)
    protected String perUnitType;
    
    
    /*
     * Added by zhangwenxing
     */
    protected double amtChange;
    protected String mainCate;
    protected String subCate;
    protected String schemeCd;
    protected int version;
    protected String code ;
    protected String custCd;
    protected String acctNbr;
    protected String contractNbr;
    protected int tierSeqNbr;
    
    protected double netRate;//the net rate
    
    
    public double getNetRate() {
		return netRate;
	}

	public void setNetRate(double netRate) {
		this.netRate = netRate;
	}

	public String getCustCd() {
		return custCd;
	}

	public void setCustCd(String custCd) {
		this.custCd = custCd;
	}

	public String getAcctNbr() {
		return acctNbr;
	}

	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	public String getContractNbr() {
		return contractNbr;
	}

	public void setContractNbr(String contractNbr) {
		this.contractNbr = contractNbr;
	}

	public int getTierSeqNbr() {
		return tierSeqNbr;
	}

	public void setTierSeqNbr(int tierSeqNbr) {
		this.tierSeqNbr = tierSeqNbr;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


	public double getAmtChange() {
		return amtChange;
	}

	public void setAmtChange(double amtChange) {
		this.amtChange = amtChange;
	}

	public String getMainCate() {
		return mainCate;
	}

	public void setMainCate(String mainCate) {
		this.mainCate = mainCate;
	}

	public String getSubCate() {
		return subCate;
	}

	public void setSubCate(String subCate) {
		this.subCate = subCate;
	}

	public String getSchemeCd() {
		return schemeCd;
	}

	public void setSchemeCd(String schemeCd) {
		this.schemeCd = schemeCd;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	protected String businessType;
    
    
/*    	
	protected boolean rangeValid; // validity of the fields
	protected boolean perUnitValid;
	protected boolean perHourValid;
	protected boolean percentChargeValid;
	protected boolean adjustmentValid;
	
	public static final int RANGE = 1;
	public static final int PER_UNIT = 2;
	public static final int PER_HOUR = 3;
	public static final int PERCENT = 4;
	public static final int ADJUSTMENT = 5;
	protected boolean isValid(int fieldName){
		switch(fieldName){
			case RANGE:
				return rangeValid;
			case PER_UNIT:
				return perUnitValid;
			case PER_HOUR:
				return perHourValid;
			case PERCENT:
				return percentChargeValid;
			case ADJUSTMENT:
				return adjustmentValid;
			default:
				return false;
		}
	}
	//*/
    public TariffTierVO() {
		id = -1;
		/*
		rangeValid = false;
		perUnitValid = false;
		perHourValid = false;
		percentChargeValid = false;
		adjustmentValid = false;//*/
		rangeFrom = -1;
		rangeTo = -1;
		perUnit = -1;
		perHour = -1;
		rate = -1;
		percentCharge = -1;
		adjustment = 0;
		adjustmentType = "Z";
        timeUnit = -1;
        otherUnit = -1;
        perHourType = "Z";
        perUnitType = "Z";
        
        //added by zhangwenxing 13/06/2011
        netRate=-1;
        
        //end by zhangwenxing
	}

	public TariffTierVO(TariffTierVO vo){
		this.copy(vo);
	}
	
	public void copy(TariffTierVO vo){
		this.setId(vo.getId());
		this.setRange(vo.getRangeFrom(), vo.getRangeTo());
		this.setPerUnit(vo.getPerUnit());
		this.setPerHour(vo.getPerHour());
		this.setRate(vo.getRate());
		this.setPercentCharge(vo.getPercentCharge());
		this.setAdjustment(vo.getAdjustment());
		this.setAdjustmentType(vo.getAdjustmentType());
		this.setOtherUnit(vo.getOtherUnit());
		this.setTimeUnit(vo.getTimeUnit());
		this.setPerHourType(vo.getPerHourType());
		this.setPerUnitType(vo.getPerUnitType());
		
		//added by zhangwenxing on 13/06/2011
		this.setNetRate(vo.getNetRate());
		
		//end by zhangwenxing
	}

	public void doGet(Object object) {}
	public void doSet(Object object) {}
	
	public int getId(){return id;}
	public double getRangeFrom(){return rangeFrom;}
	public double getRangeTo(){return rangeTo;}
	public double getPerHour(){return perHour;}
	public double getPerUnit(){return perUnit;}
	public double getRate(){return rate;}
	public double getPercentCharge(){return percentCharge;}
	public double getAdjustment(){return adjustment;}
	public String getAdjustmentType(){return adjustmentType;}
    public double getTimeUnit() {return(timeUnit);} 
    public double getOtherUnit() {return(otherUnit);} 
    public String getPerHourType() { return(this.perHourType); }
    public String getPerUnitType() { return(this.perUnitType); }

	public boolean isAmount(){
		if (adjustmentType.equalsIgnoreCase("A")) return true;
		else if (adjustmentType.equalsIgnoreCase("P")) return false;
		else return false;
	}

	public boolean isPercentage(){
		if (adjustmentType.equalsIgnoreCase("P")) return true;
		else if (adjustmentType.equalsIgnoreCase("A")) return false;
		else return false;
	}

	public void setId(int value){id = value;}
	public void setRangeFrom(double value){ 
		rangeFrom = value; 
		//rangeValid = true;
	}
	public void setRangeTo(double value){ 
		if (value < rangeFrom) return;
		rangeTo = value;
		//rangeValid = true;
	}
	public void setRange(double from, double to){
		if (from > to) return;
		rangeFrom = from;
		rangeTo = to;
		//rangeValid = true;
	}
	public void setPerHour(double value){
		perHour = value;
		//perHourValid = true;
	}
	public void setPerUnit(double value){
		perUnit = value;
		//perUnitValid = true;
	}
	public void setRate(double value){rate = value;}
	public void setPercentCharge(double value){
		percentCharge = value;
		//percentChargeValid = true;
	}
	public void setAdjustment(double value, boolean isAmount){
		adjustment = value;
		adjustmentType = (isAmount)?"A":"P";
		//adjustmentValid = true;
	}
	public void setAdjustment(double value){
		adjustment = value;
		//adjustmentValid = true;
	}
	public void setAdjustmentType(boolean isAmount){
		adjustmentType = (isAmount)?"A":"P";
		//adjustmentValid = true;
	}
	public void setAdjustmentType(String value){ 
		adjustmentType = value;
		//adjustmentValid = true;		
	}

    public void setTimeUnit(double value) {
    	timeUnit = value;
    } 

    public void setOtherUnit(double value) {
    	this.otherUnit = value;
    } 
	public void setPerHourType(String value){ this.perHourType = value; }
	public void setPerUnitType(String value){ this.perUnitType = value; }
	
	public boolean equals(Object o){
		if (o.getClass().getName() != this.getClass().getName())
			return false;
		TariffTierVO vo = (TariffTierVO)o;
		if (vo.getId() == this.getId() &&
		vo.getRangeFrom() == this.getRangeFrom() &&
		vo.getRangeTo() == this.getRangeTo() &&
		vo.getPerHour() == this.getPerHour() &&
		vo.getPerUnit() == this.getPerUnit() &&
		vo.getRate() == this.getRate() &&
		vo.getPercentCharge() == this.getPercentCharge() &&
		vo.getAdjustment() == this.getAdjustment() &&
		vo.getAdjustmentType().equals(this.getAdjustmentType()) &&
		vo.getTimeUnit() == this.getTimeUnit() &&
		vo.getOtherUnit() == this.getOtherUnit() &&
		vo.getPerHourType().equals(this.getPerHourType()) &&
		vo.getPerUnitType().equals(this.getPerUnitType())
		//amended by zhangwenxing 13/06/2011
		&&vo.getNetRate()==this.netRate
		//end by zhangwenxing
		
		)
			return true;
		return false;
	}
		
	public boolean isModified(TariffTierVO tariffTierVO) {
		TariffTierVO origVO	= new TariffTierVO();
		
		if (tariffTierVO.getId()			== origVO.getId()			&&
			tariffTierVO.getRangeFrom()		== origVO.getRangeFrom()	&&
			tariffTierVO.getRangeTo()		== origVO.getRangeTo()		&&
			tariffTierVO.getPerHour()		== origVO.getPerHour()		&&
			tariffTierVO.getPerUnit()		== origVO.getPerUnit()		&&
			tariffTierVO.getRate()			== origVO.getRate()			&&
			tariffTierVO.getPercentCharge() == origVO.getPercentCharge()&&
			tariffTierVO.getAdjustment()	== origVO.getAdjustment()	&&
			tariffTierVO.getAdjustmentType().equals(origVO.getAdjustmentType()) &&
			tariffTierVO.getTimeUnit()		== origVO.getTimeUnit()		&&
			tariffTierVO.getOtherUnit()		== origVO.getOtherUnit()	&&
			tariffTierVO.getPerHourType().equals(origVO.getPerHourType()) &&
			tariffTierVO.getPerUnitType().equals(origVO.getPerUnitType())
		//amended by zhangwenxing on 13/06/2011
			&&tariffTierVO.getNetRate()==origVO.getNetRate()
		//end by zhangwenxing
		) {
			return false;
		} else {
			return true;
		}
	}
}

package sg.com.jp.generalcargo.domain;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TariffMainVO extends UserTimestampVO {
	private static final long serialVersionUID = 1L;
	// database field names
	public static final String TABLE_NAME = "tariff_main";
	public static final String VERSION = "version_nbr";
	public static final String CUSTOMER = "cust_cd";
	public static final String ACCOUNT = "acct_nbr";
	public static final String CONTRACT = "contract_nbr";
	public static final String CONTRACT_YEAR = "contractual_yr";
	public static final String CODE = "tariff_cd";
	public static final String DESCRIPTION = "tariff_desc";
	public static final String MAIN_CATEGORY = "tariff_main_cat_cd";
	public static final String SUB_CATEGORY = "tariff_sub_cat_cd";
	public static final String BILL_PARTY = "bill_party";
	public static final String BILL_ACCOUNT = "bill_acct_nbr";
	public static final String BILL_CONTRACT = "bill_contract_nbr";
	public static final String COA = "coa_acct_nbr";
	public static final String GST = "gst_charge";
	public static final String GST_CODE = "gst_cd";
	public static final String WAIVE_IND = "waive_ind";
	public static final String WAIVE_REASON = "waive_reason";
	public static final String WAIVE_START = "commence_dttm";
	public static final String WAIVE_END = "expiry_dttm";
	public static final String CREATE_DATE = "create_dttm";
	public static final String SCHEME = "scheme_cd";
	public static final String BUSINESS_TYPE = "business_type";
	public static final String MIN_TON = "min_ton";
 

	// member variable

	protected int version;
	protected String customer;
	protected String account;
	protected String contract;
	protected int contractYear;
//*/
	protected String code;  
	protected String description;
	protected String mainCategory;
	protected String subCategory;
	protected String billParty;
	protected String coa;
	protected double gst;
	protected String gstCode;
	protected String waiveInd;
	protected Timestamp waiveStartDate;
	protected Timestamp waiveEndDate;
	protected String waiveReason;
	protected String tariffTypeInd;
	protected Timestamp createDate;
	protected String scheme;
	protected String businessType; // C G B (W?)
	protected String billAccount;
	protected String billContract;
	protected double minTonnage;
	
	//added by zhangwenxing on 27/05/2011
	protected Date effectiveDate;
	protected String postInd;
	//end by zhangwenxing
	
	public String getPostInd() {
		return postInd;
	}

	public void setPostInd(String postInd) {
		this.postInd = postInd;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	//linhph3 add 03/04/2013
	protected int curTier = 0;
    
    public int getCurTier() {
		return curTier;
	}

	public void setCurTier(int curTier) {
		this.curTier = curTier;
	}
	//linhph3 end 03/04/2013
	// vector
	protected ArrayList aTier = new ArrayList(10);
	protected ArrayList aCntr = new ArrayList(5);
	protected ArrayList aGenChar = new ArrayList(5);
	private String FSPMode;
	
    public TariffMainVO() {
    	
    	version = 0;
		customer = null;
		account = null;
		contract = null;
		contractYear = 0;
		//*/
		code = null;
		description = null;
		mainCategory = null;
		subCategory = null;
		billParty = null;
		coa = null;
		gst = -1.0;
		gstCode = null;
		waiveInd = null;
		waiveStartDate = null;
		waiveEndDate = null;
		waiveReason = null;
		tariffTypeInd = null;
		businessType = null;
		scheme = null;
		billAccount = null;
		billContract = null;
		minTonnage = 0;
	}
	
	public TariffMainVO(TariffMainVO vo){
		this.copy(vo);
	}
	
	public void copy(TariffMainVO vo){
		//amended by zhangwenxing on 09/06/2011
		this.effectiveDate = vo.getEffectiveDate();
		//end by zhangwenxing
		
		this.version = vo.getVersion();
		this.customer = vo.getCustomer();
		this.account = vo.getAccount();
		this.contract = vo.getContract();
		this.contractYear = vo.getContractYear();

		this.code = vo.getCode();
		this.description = vo.getDescription();
		this.mainCategory = vo.getMainCategory();
		this.subCategory = vo.getSubCategory();
		this.billParty = vo.getBillParty();
		this.coa = vo.getCOA();
		this.gst = vo.getGST();
		this.gstCode = vo.getGSTCode();
		this.waiveInd = vo.getWaiveInd();
		this.waiveStartDate = vo.getWaiveStartDate();
		this.waiveEndDate = vo.getWaiveEndDate();
		this.waiveReason = vo.getWaiveReason();
		this.tariffTypeInd = vo.getTariffTypeInd();
		this.scheme = vo.getScheme();
		this.businessType = vo.getBusinessType();
		this.billAccount = vo.getBillAccount();
		this.billContract = vo.getBillContract();
		this.minTonnage = vo.getMinTonnage();
		
		this.removeAllContainer();
		this.removeAllTier();
		this.removeAllGenChar();

		for (int i=0; i<vo.getTierCount(); i++){
			TariffTierVO tvo = new TariffTierVO(vo.getTier(i));
			this.addTier(tvo);
		}
		for (int i=0; i<vo.getContainerCount(); i++){
			TariffContainerVO cvo = new TariffContainerVO(vo.getContainer(i));
			this.addContainer(cvo);
		}
		for (int i=0; i<vo.getGenCharCount(); i++){
			TariffGenCharVO gvo = new TariffGenCharVO(vo.getGenChar(i));
			this.addGenChar(gvo);
		}
	}

	/**Reset the object for reuse*/
	public void reset(){
		this.version = 0;
		this.customer = null;
		this.account = null;
		this.contract = null;
		this.contractYear = 0;
	
		this.code = null;
		this.description = null;
		this.mainCategory = null;
		this.subCategory = null;
		this.billParty = null;
		this.coa = null;
		this.gst = 0.0;
		this.gstCode = null;
		this.waiveInd = null;
		this.waiveStartDate = null;
		this.waiveEndDate = null;
		this.waiveReason = null;
		this.tariffTypeInd = null;
		this.scheme = null;
		this.businessType = null;
		this.billAccount = null;
		this.billContract = null;
		this.minTonnage = 0.0;
		
		this.removeAllContainer();
		this.removeAllTier();
		this.removeAllGenChar();
	}


	public void doGet(Object object) {}
	public void doSet(Object object) {}
	


	public int getVersion(){ return version; }
	public String getCustomer(){ return customer; }
	public String getAccount(){ return account; }
	public String getContract(){return contract; }
	public int getContractYear(){ return contractYear; }
	public String getCode(){return code;}
	public String getDescription(){return description;}
	public String getMainCategory(){return mainCategory;}
	public String getSubCategory(){return subCategory;}
	public String getBillParty(){return billParty;}
	public String getCOA(){return coa;}
	public double getGST(){return gst;}
	public String getGSTCode(){return gstCode;}
	public String getWaiveInd(){return waiveInd; }
	public Timestamp getWaiveStartDate(){return waiveStartDate;}
	public Timestamp getWaiveEndDate(){return waiveEndDate;}
	public String getWaiveReason(){return waiveReason;}
	public String getTariffTypeInd(){return tariffTypeInd;}
	public Timestamp getCreateDate(){return createDate; }
	public String getScheme(){ return scheme; }
	public String getBusinessType(){ return businessType; }
	public String getBillAccount(){ return billAccount; }
	public String getBillContract(){ return billContract; }
	public double getMinTonnage(){return minTonnage;}
	// added by suba
	public String getFSPMode(){return FSPMode;}
	//End
	
	public void setVersion(int value){version = value;}
	public void setCustomer(String value){customer = value;}
	public void setAccount(String value){account = value;}
	public void setContract(String value){contract = value;}
	public void setContractYear(int value){contractYear = value;}
	public void setCode(String value){code = value;}
	public void setDescription(String value){description = value;}
	public void setMainCategory(String value){mainCategory = value;}
	public void setSubCategory(String value){subCategory = value;}
	public void setBillParty(String value){billParty = value;}
	public void setCOA(String value){coa = value;}
	public void setGST(double value){gst = value;}
	public void setGSTCode(String value){gstCode = value;}
	public void setWaived(String value){waiveInd = value;}
	public void setWaiveStartDate(Timestamp value){waiveStartDate = value;}
	public void setWaiveEndDate(Timestamp value){waiveEndDate = value;}
	public void setWaiveReason(String value){waiveReason = value;}
	public void setTariffTypeInd(String value){tariffTypeInd = value;}
	public void setCreateDate(Timestamp value){createDate = value;}
	public void setScheme(String value){scheme = value;}
	public void setBusinessType(String value){businessType = value;}
	public void setBillAccount(String value){billAccount = value;}
	public void setBillContract(String value){billContract = value; }
	public void setMinTonnage(double value){minTonnage = value;}
	//added by suba
	public void setFSPMode(String fspmode){FSPMode = fspmode;}
	//End
	
	public boolean isWaived(){
		if (waiveInd == null) return false;
		else if(waiveInd.equals("1")||waiveInd.equalsIgnoreCase("Y")||waiveInd.equalsIgnoreCase("T")) return true;
		else if(waiveInd.equals("0")||waiveInd.equalsIgnoreCase("N")||waiveInd.equalsIgnoreCase("F")) return false;
		else return false;
	}
	
	// tier methods
	public int getTierCount(){
		return aTier.size();
	}
	public void addTier(TariffTierVO t){
		aTier.add(t);
	}
	public TariffTierVO getTier(int index){
		if (index >= aTier.size() || index < 0)
			return null;
		return (TariffTierVO)aTier.get(index);
	}
	public TariffTierVO[] getAllTier(){
		if (aTier.size() < 1) return null;
		TariffTierVO[] vo = new TariffTierVO[aTier.size()];
		for (int i=0; i<aTier.size(); i++){
			vo[i] = (TariffTierVO)aTier.get(i);
		}
		return vo;
	}
	public TariffTierVO removeTier(int index){
		return (TariffTierVO)aTier.remove(index);
	}
	public void removeAllTier(){
		aTier.clear();
	}
	// sorting Tariff Tier objects Based on Tariff Rate.
	public void sortAllTier(){
		TariffTierVO[] vo = (TariffTierVO[])getAllTier();
		for (int i=0; i<vo.length; i++){
			for (int j=i+1; j<vo.length; j++){
				if (vo[i].getRate()>vo[j].getRate()){
					TariffTierVO temp = new TariffTierVO();
					temp.copy(vo[j]);
					vo[j].copy(vo[i]);
					vo[i].copy(temp);
				}
			}
		}
	}

	// container methods
	public int getContainerCount(){
		return aCntr.size();
	}
	public void addContainer(TariffContainerVO t){
		aCntr.add(t);
	}
	public TariffContainerVO getContainer(int index){
		if (index >= aCntr.size() || index < 0)
			return null;
		return (TariffContainerVO)aCntr.get(index);
	}
	public TariffContainerVO[] getAllContainer(){
		if (aCntr.size() < 1) return null;
		TariffContainerVO[] vo = new TariffContainerVO[aCntr.size()];
		for (int i=0; i<aCntr.size(); i++){
			vo[i] = (TariffContainerVO)aCntr.get(i);
		}
		return vo;
	}
	public TariffContainerVO removeContainer(int index){
		if (index >= aCntr.size() || index < 0)
			return null;
		return (TariffContainerVO)aCntr.remove(index);
	}
	public void removeAllContainer(){
		aCntr.clear();
	}
	
	// gen char methods
	public int getGenCharCount(){
		return aGenChar.size();
	}
	public void addGenChar(TariffGenCharVO g){
		aGenChar.add(g);
	}
	public TariffGenCharVO getGenChar(int index){
		if (index >= aGenChar.size() || index < 0)
			return null;
		return (TariffGenCharVO)aGenChar.get(index);
	}
	public TariffGenCharVO[] getAllGenChar(){
		if (aGenChar.size() < 1) return null;
		TariffGenCharVO[] vo = new TariffGenCharVO[aCntr.size()];
		for (int i=0; i<aGenChar.size(); i++){
			vo[i] = (TariffGenCharVO)aGenChar.get(i);
		}
		return vo;
	}
	public TariffGenCharVO removeGenChar(int index){
		if (index >= aGenChar.size() || index < 0)
			return null;
		return (TariffGenCharVO)aGenChar.remove(index);
	}
	public void removeAllGenChar(){
		aGenChar.clear();
	}	

	// used by bill processing
	public boolean isModified(TariffMainVO tariffMainVO) {
		TariffMainVO origVO = new TariffMainVO();

		if (tariffMainVO.getCode()          == origVO.getCode()             && 
			tariffMainVO.getDescription()   == origVO.getDescription()      &&
			tariffMainVO.getMainCategory()  == origVO.getMainCategory()     &&
			tariffMainVO.getSubCategory()   == origVO.getSubCategory()      &&
			tariffMainVO.getBillParty()     == origVO.getBillParty()        &&
			tariffMainVO.getCOA()           == origVO.getCOA()              &&
			tariffMainVO.getGST()           == origVO.getGST()              &&   
			tariffMainVO.getGSTCode()		== origVO.getGSTCode()			&&
			tariffMainVO.getWaiveInd()      == origVO.getWaiveInd()         &&
			tariffMainVO.getWaiveStartDate()== origVO.getWaiveStartDate()   &&
			tariffMainVO.getWaiveEndDate()  == origVO.getWaiveEndDate()     &&
			tariffMainVO.getWaiveReason()   == origVO.getWaiveReason()      &&
			tariffMainVO.getTariffTypeInd() == origVO.getTariffTypeInd()	&&
			tariffMainVO.getScheme()		== origVO.getScheme()			&&
			tariffMainVO.getBusinessType()	== origVO.getBusinessType()		&&
			tariffMainVO.getBillAccount()	== origVO.getBillAccount()		&&
			tariffMainVO.getBillContract()	== origVO.getBillContract()		&&
			tariffMainVO.getMinTonnage()	== origVO.getMinTonnage())
			return false;
		else
			return true;
	}
}
		

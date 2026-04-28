package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TariffGenCharVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// database field name
	public static final String TABLE_NAME = "tariff_gen_char";
	public static final String MOVEMENT = "mvmt";
	public static final String TYPE = "type";
	public static final String CNTR_CAT = "cntr_cat";
	public static final String CNTR_SIZE = "cntr_size";

	protected String movement;
	protected String type;
	protected String containerCategory;
	protected String containerSize;

	public void setMovement(String movement) { this.movement = movement; }
	public void setType(String type) { this.type = type; }
	public void setContainerCategory(String containerCategory) { this.containerCategory = containerCategory; }
	public void setContainerSize(String containerSize) { this.containerSize = containerSize; }
	public String getMovement() { return (this.movement); }
	public String getType() { return (this.type); }
	public String getContainerCategory() { return (this.containerCategory); }
	public String getContainerSize() { return (this.containerSize); }

	public TariffGenCharVO(){
		super();
		this.movement = null;
		this.type = null;
		this.containerCategory = null;
		this.containerSize = null;
	}
	
	public TariffGenCharVO(TariffGenCharVO vo){
		this.copy(vo);
	}
	
	public boolean equals(Object o){
		TariffGenCharVO gc = (TariffGenCharVO)o;
		
		if (this.movement != null && this.movement.equals(gc.getMovement()) &&
			this.type != null && this.type.equals(gc.getType()) &&
			this.containerCategory != null && this.containerCategory.equals(gc.getContainerCategory()) &&
			this.containerSize != null && this.containerSize.equals(gc.getContainerSize())) {
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isModified(){
		TariffGenCharVO newVO = new TariffGenCharVO();
		
		// newVO is all null so just need to make sure the 
		// this object is not null can oredi.
		boolean retVal = false;
		if (this.getContainerCategory() == newVO.getContainerCategory() &&
			this.getContainerSize() == newVO.getContainerSize() &&
			this.getMovement() == newVO.getMovement() &&
			this.getType() == newVO.getType()){
			retVal = true;
		}else{
			retVal = false;
		}
		return retVal;
	}
	
	public static boolean isModified(TariffGenCharVO vo){
		return vo.isModified();
	}
	
	public void copy(TariffGenCharVO vo){
		this.setContainerCategory(vo.getContainerCategory());
		this.setContainerSize(vo.getContainerSize());
		this.setMovement(vo.getMovement());
		this.setType(vo.getType());
		this.setUser(vo.getUser());
		this.setTimestamp(vo.getTimestamp());
	}
	
	//Added by zhangwenxing on 20/06/2011
	private String code;
	private String custCd;
	private String acctNbr;
	private String contractNbr;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public static String getTableName() {
		return TABLE_NAME;
	}
	public static String getCntrSize() {
		return CNTR_SIZE;
	}
	//end by zhangwenxing
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}

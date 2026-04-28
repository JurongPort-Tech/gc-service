package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TariffContainerVO extends UserTimestampVO {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// database field names
    public static final String TABLE_NAME = "tariff_cntr";

    public static final String MOVEMENT = "mvmt";

    public static final String STATUS = "status";

    public static final String SIZE = "cntr_size";

    public static final String CATEGORY = "cat_cd";

    public static final String DG_IND = "dg_ind";

    public static final String DGCATEGORY = "dgCatcd";

    // member variable
    protected int id;

    protected String movement;

    protected String status;

    protected String size;

    protected String category;

    protected String dgcategory;

    protected String dg_ind;

    public TariffContainerVO() {
        movement = "";
        status = "";
        size = "";
        category = "";
        dgcategory = "";
        dg_ind = "";
    }

    public TariffContainerVO(TariffContainerVO vo) {
        this.copy(vo);
    }

    public void copy(TariffContainerVO vo) {
        this.setId(vo.getId());
        this.setMovement(vo.getMovement());
        this.setStatus(vo.getStatus());
        this.setSize(vo.getSize());
        this.setCategory(vo.getCategory());
        this.setDgInd(vo.getDgInd());

        this.setDgCategory(vo.getDgCategory());
    }

    // current values	
    public String getMovement() {
        return movement;
    }

    public String getStatus() {
        return status;
    }

    public String getSize() {
        return size;
    }

    public String getCategory() {
        return category;
    }

    public String getDgInd() {
        return dg_ind;
    }

    public int getId() {
        return id;
    }

    public String getDgCategory() {
        return dgcategory;
    }

    public void setMovement(String value) {
        movement = value;
    }

    public void setStatus(String value) {
        status = value;
    }

    public void setSize(String value) {
        size = value;
    }

    public void setCategory(String value) {
        category = value;
    }

    public void setDgInd(String value) {
        dg_ind = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDgCategory(String value) {
        dgcategory = value;
    }

    public boolean equals(Object o) {
        if (o.getClass().getName() != this.getClass().getName())
            return false;

        TariffContainerVO vo = (TariffContainerVO) o;
        if (vo.getMovement().equals(this.getMovement()) && vo.getStatus().equals(this.getStatus())
                && vo.getSize().equals(this.getSize()) && vo.getCategory().equals(this.getCategory())
                && vo.getDgInd().equals(this.getDgInd()) && vo.getDgCategory().equals(this.getDgCategory()))
            return true;
        return false;
    }

    public boolean isModified(TariffContainerVO tariffContainerVO) {
        TariffContainerVO origVO = new TariffContainerVO();

        if (tariffContainerVO.getMovement().equals(origVO.getMovement())
                && tariffContainerVO.getStatus().equals(origVO.getStatus())
                && tariffContainerVO.getSize().equals(origVO.getSize())
                && tariffContainerVO.getCategory().equals(origVO.getCategory())
                && tariffContainerVO.getDgInd().equals(origVO.getDgInd())
                && tariffContainerVO.getDgCategory().equals(origVO.getDgCategory())) {
            return false;
        } else {
            return true;
        }
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOprPlanDet extends AuditLogRecord implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GbccCargoOprPlanDetId id;
    
   	private String vvCd;
    private Date createDttm;
    private Integer hatchNbr;
    private Integer discTon;
    private Integer loadTon;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    
    //VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : start
    private String remark;
    //VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : end

    public GbccCargoOprPlanDet() {
    }

    public GbccCargoOprPlanDet(Integer discTon, Integer loadTon, String lastModifyUserId, Date lastModifyDttm) {
        this.discTon = discTon;
        this.loadTon = loadTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoOprPlanDet(GbccCargoOprPlanDetId id, Integer discTon, Integer loadTon, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.discTon = discTon;
        this.loadTon = loadTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    
    
    
    
    public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public Date getCreateDttm() {
		return createDttm;
	}

	public void setCreateDttm(Date createDttm) {
		this.createDttm = createDttm;
	}

	public Integer getHatchNbr() {
		return hatchNbr;
	}

	public void setHatchNbr(Integer hatchNbr) {
		this.hatchNbr = hatchNbr;
	}

	public GbccCargoOprPlanDetId getId() {
        return id;
    }

    public void setId(GbccCargoOprPlanDetId id) {
        this.id = id;
    }

    public Integer getDiscTon() {
        return discTon;
    }

    public void setDiscTon(Integer discTon) {
        this.discTon = discTon;
    }

    public Integer getLoadTon() {
        return loadTon;
    }

    public void setLoadTon(Integer loadTon) {
        this.loadTon = loadTon;
    }

    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    public void setLastModifyUserId(String lastModifyUserId) {
        this.lastModifyUserId = lastModifyUserId;
    }

    public Date getLastModifyDttm() {
        return lastModifyDttm;
    }

    public void setLastModifyDttm(Date lastModifyDttm) {
        this.lastModifyDttm = lastModifyDttm;
    }
    
    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("discTon");
    	auditFields.add("loadTon");
    	auditFields.add("lastModifyUserId");
    	auditFields.add("lastModifyDttm");    		
    	
    }
    
    
    protected void setDateFields() { 
		dateFields = new HashSet();
		dateFields.add("lastModifyDttm");
		
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

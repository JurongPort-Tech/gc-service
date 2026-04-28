package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTimesheetEqrental extends AuditLogRecord implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private GbccCargoTimesheetEqrentalId id;
    private Date fromDttm;
    private Date toDttm;
    private String eqTypeCd;
    private String eqTypeDesc;
    private Integer eqTon;
    private Integer eqUnit;
    private String lastModifyUserId;
    private Date lastModifyDttm;

    private String eqTypeName;
    
    private String objUpdateMode;
    
	//VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : start
    private String vvCd;
    private Date createDttm;
    private Integer recSeqNbr;
	//VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : end
	    
    public GbccCargoTimesheetEqrental() {
    }

    public GbccCargoTimesheetEqrental(Date fromDttm, Date toDttm, String eqTypeCd, String eqTypeDesc, Integer eqTon, Integer eqUnit, String lastModifyUserId, Date lastModifyDttm) {
        this.fromDttm = fromDttm;
        this.toDttm = toDttm;
        this.eqTypeCd = eqTypeCd;
        this.eqTypeDesc = eqTypeDesc;
        this.eqTon = eqTon;
        this.eqUnit = eqUnit;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoTimesheetEqrental(GbccCargoTimesheetEqrentalId id, Date fromDttm, Date toDttm, String eqTypeCd, String eqTypeDesc, Integer eqTon, Integer eqUnit, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.fromDttm = fromDttm;
        this.toDttm = toDttm;
        this.eqTypeCd = eqTypeCd;
        this.eqTypeDesc = eqTypeDesc;
        this.eqTon = eqTon;
        this.eqUnit = eqUnit;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoTimesheetEqrentalId getId() {
        return id;
    }

    public void setId(GbccCargoTimesheetEqrentalId id) {
        this.id = id;
    }

    public Date getFromDttm() {
        return fromDttm;
    }

    public void setFromDttm(Date fromDttm) {
        this.fromDttm = fromDttm;
    }

    public Date getToDttm() {
        return toDttm;
    }

    public void setToDttm(Date toDttm) {
        this.toDttm = toDttm;
    }

    public String getEqTypeCd() {
        return eqTypeCd;
    }

    public void setEqTypeCd(String eqTypeCd) {
        this.eqTypeCd = eqTypeCd;
    }

    public String getEqTypeDesc() {
        return eqTypeDesc;
    }

    public void setEqTypeDesc(String eqTypeDesc) {
        this.eqTypeDesc = eqTypeDesc;
    }

    public Integer getEqTon() {
        return eqTon;
    }

    public void setEqTon(Integer eqTon) {
        this.eqTon = eqTon;
    }

    public Integer getEqUnit() {
        return eqUnit;
    }

    public void setEqUnit(Integer eqUnit) {
        this.eqUnit = eqUnit;
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

    public String getEqTypeName() {
        return eqTypeName;
    }

    public void setEqTypeName(String eqTypeName) {
        this.eqTypeName = eqTypeName;
    }
    
    public String getObjUpdateMode() {
    	if (objUpdateMode == null)
    		return "";
        return objUpdateMode;
    }

    public void setObjUpdateMode(String objUpdateMode) {
        this.objUpdateMode = objUpdateMode;
    }
    
    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("fromDttm");
    	auditFields.add("toDttm");
    	auditFields.add("eqTypeCd");
    	auditFields.add("eqTypeDesc");
    	auditFields.add("eqTon");
    	auditFields.add("eqUnit");
    	auditFields.add("lastModifyUserId");
    	auditFields.add("lastModifyDttm");
    	
    	    
    }
    
    
	protected void setDateFields() { 
		dateFields = new HashSet();
		dateFields.add("fromDttm");
		dateFields.add("toDttm");
		dateFields.add("lastModifyDttm");
		
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

	public Integer getRecSeqNbr() {
		return recSeqNbr;
	}

	public void setRecSeqNbr(Integer recSeqNbr) {
		this.recSeqNbr = recSeqNbr;
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

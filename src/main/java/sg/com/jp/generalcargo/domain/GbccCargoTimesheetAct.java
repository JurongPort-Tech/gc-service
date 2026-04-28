package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTimesheetAct extends AuditLogRecord implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private GbccCargoTimesheetActId id;
	
    private Date fromDttm;
    private Date toDttm;
    private Integer hatchNbr;
    private String weatherCd;
    private String activityCd;
    private String remarks;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    
    private String weatherName;
    private String activityName;
    
    private String objUpdateMode;
    
	//VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : start
    private String vvCd;
    private Date createDttm;
    private Integer recSeqNbr;
    private String oprType;
	//VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : end
    
    //LinhPh (FPT) add new field for StevedoreNet 10-Jul-2012 : start
    private Date cargoDttm;
    //LinhPh (FPT) end new field for StevedoreNet 10-Jul-2012 : end
    
  
	    
    public GbccCargoTimesheetAct() {
    }

    public GbccCargoTimesheetAct(Date fromDttm, Date toDttm, Integer hatchNbr, String weatherCd, String activityCd, String remarks, String lastModifyUserId, Date lastModifyDttm) {
        this.fromDttm = fromDttm;
        this.toDttm = toDttm;
        this.hatchNbr = hatchNbr;
        this.weatherCd = weatherCd;
        this.activityCd = activityCd;
        this.remarks = remarks;
        
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoTimesheetAct(GbccCargoTimesheetActId id, Date fromDttm, Date toDttm, Integer hatchNbr, String weatherCd, String activityCd, String remarks, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.fromDttm = fromDttm;
        this.toDttm = toDttm;
        this.hatchNbr = hatchNbr;
        this.weatherCd = weatherCd;
        this.activityCd = activityCd;
        this.remarks = remarks;
        
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoTimesheetActId getId() {
        return id;
    }

    public void setId(GbccCargoTimesheetActId id) {
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

    public Integer getHatchNbr() {
        return hatchNbr;
    }

    public void setHatchNbr(Integer hatchNbr) {
        this.hatchNbr = hatchNbr;
    }

    public String getWeatherCd() {
        return weatherCd;
    }

    public void setWeatherCd(String weatherCd) {
        this.weatherCd = weatherCd;
    }

    public String getActivityCd() {
        return activityCd;
    }

    public void setActivityCd(String activityCd) {
        this.activityCd = activityCd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
    
    public String getWeatherName() {
        return weatherName;
    }

    public void setWeatherName(String weatherName) {
        this.weatherName = weatherName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
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
    	auditFields.add("hatchNbr");
    	auditFields.add("weatherCd");
    	auditFields.add("activityCd");
    	auditFields.add("remarks");
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

	public String getOprType() {
		return oprType;
	}

	public void setOprType(String oprType) {
		this.oprType = oprType;
	}

	public Date getCargoDttm() {
		return cargoDttm;
	}

	public void setCargoDttm(Date cargoDttm) {
		this.cargoDttm = cargoDttm;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOpenBalDet extends AuditLogRecord implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private GbccCargoOpenBalDetId id;
	private String vvCd;
    private String stevCoCd;
    private Integer hatchNbr;
    private Integer discOpenBalTon;
    private String discOversideInd;
    private Integer loadOpenBalTon;
    private String loadOversideInd;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    private String discRemarks;
    private String loadRemarks;

    public GbccCargoOpenBalDet() {
    }

    public GbccCargoOpenBalDet(Integer discOpenBalTon, String discOversideInd, Integer loadOpenBalTon, String loadOversideInd, String lastModifyUserId, Date lastModifyDttm, String discRemarks, String loadRemarks) {
        this.discOpenBalTon = discOpenBalTon;
        this.discOversideInd = discOversideInd;
        this.loadOpenBalTon = loadOpenBalTon;
        this.loadOversideInd = loadOversideInd;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
        this.discRemarks = discRemarks;
        this.loadRemarks = loadRemarks;
    }

    public GbccCargoOpenBalDet(GbccCargoOpenBalDetId id, Integer discOpenBalTon, String discOversideInd, Integer loadOpenBalTon, String loadOversideInd, String lastModifyUserId, Date lastModifyDttm, String discRemarks, String loadRemarks) {
        this.id = id;
        this.discOpenBalTon = discOpenBalTon;
        this.discOversideInd = discOversideInd;
        this.loadOpenBalTon = loadOpenBalTon;
        this.loadOversideInd = loadOversideInd;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
        this.discRemarks = discRemarks;
        this.loadRemarks = loadRemarks;
    }

    
    
    
    public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getStevCoCd() {
		return stevCoCd;
	}

	public void setStevCoCd(String stevCoCd) {
		this.stevCoCd = stevCoCd;
	}

	public Integer getHatchNbr() {
		return hatchNbr;
	}

	public void setHatchNbr(Integer hatchNbr) {
		this.hatchNbr = hatchNbr;
	}

	public GbccCargoOpenBalDetId getId() {
        return id;
    }

    public void setId(GbccCargoOpenBalDetId id) {
        this.id = id;
    }

    public Integer getDiscOpenBalTon() {
        return discOpenBalTon;
    }

    public void setDiscOpenBalTon(Integer discOpenBalTon) {
        this.discOpenBalTon = discOpenBalTon;
    }

    public String getDiscOversideInd() {
        return discOversideInd;
    }

    public void setDiscOversideInd(String discOversideInd) {
        this.discOversideInd = discOversideInd;
    }

    public Integer getLoadOpenBalTon() {
        return loadOpenBalTon;
    }

    public void setLoadOpenBalTon(Integer loadOpenBalTon) {
        this.loadOpenBalTon = loadOpenBalTon;
    }

    public String getLoadOversideInd() {
        return loadOversideInd;
    }

    public void setLoadOversideInd(String loadOversideInd) {
        this.loadOversideInd = loadOversideInd;
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

    public String getDiscRemarks() {
        return discRemarks;
    }

    public void setDiscRemarks(String discRemarks) {
        this.discRemarks = discRemarks;
    }

    public String getLoadRemarks() {
        return loadRemarks;
    }

    public void setLoadRemarks(String loadRemarks) {
        this.loadRemarks = loadRemarks;
    }

    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("discOpenBalTon");
    	auditFields.add("discOversideInd");
    	auditFields.add("loadOpenBalTon");
    	auditFields.add("loadOversideInd");
    	auditFields.add("discRemarks");
    	auditFields.add("loadRemarks");
    
    }
    
	protected void setDateFields() { 
		dateFields = new HashSet();
		
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

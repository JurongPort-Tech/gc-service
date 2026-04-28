package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOprDet extends AuditLogRecord implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/**
	 * 
	 */
	private GbccCargoOpr gbcc;
	


	private GbccCargoOprDetId id;
	private String vvCd;
    private String stevCoCd;
    private Date createDttm;
    private Integer hatchNbr;
    private Integer discCompletedTon;
    private Integer loadCompletedTon;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    
    private String discRemarks;
    private String loadRemarks;

    private Integer discOpenBal;
    private Integer loadOpenBal;
    private Integer discBal;
    private Integer loadBal;
    private Integer discTotalCompleted;
    private Integer loadTotalCompleted;
    
    // START: Added by VietNguyen 28-May-2012 for StevedoreNet
	private String discWeatherCd;
	private String discActivityCd;
	private String loadWeatherCd;
	private String loadActivityCd;
	
	
	private Integer disc_open_bal_ton;
	private Integer load_open_bal_ton;
	private Integer total_disc_completed;
	private Integer total_load_completed;
	private Integer bal_disc_ton;
	private Integer bal_load_ton;
	private Integer bal_hatch_nbr;
	
	
	// END: Added by VietNguyen 28-May-2012 for StevedoreNet
	public GbccCargoOprDet() {
    }

    public GbccCargoOprDet(Integer discCompletedTon, Integer loadCompletedTon, String lastModifyUserId, Date lastModifyDttm, String discRemarks, String loadRemarks) {
        this.discCompletedTon = discCompletedTon;
        this.loadCompletedTon = loadCompletedTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
        this.discRemarks = discRemarks;
        this.loadRemarks = loadRemarks;
    }

    public GbccCargoOprDet(GbccCargoOprDetId id, Integer discCompletedTon, Integer loadCompletedTon, String lastModifyUserId, Date lastModifyDttm, String discRemarks, String loadRemarks) {
        this.id = id;
        this.discCompletedTon = discCompletedTon;
        this.loadCompletedTon = loadCompletedTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
        this.discRemarks = discRemarks;
        this.loadRemarks = loadRemarks;
    }

    
    public GbccCargoOpr getGbcc() {
		return gbcc;
	}

	public void setGbcc(GbccCargoOpr gbcc) {
		this.gbcc = gbcc;
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

	public Integer getDisc_open_bal_ton() {
		return disc_open_bal_ton;
	}

	public void setDisc_open_bal_ton(Integer disc_open_bal_ton) {
		this.disc_open_bal_ton = disc_open_bal_ton;
	}

	public Integer getLoad_open_bal_ton() {
		return load_open_bal_ton;
	}

	public void setLoad_open_bal_ton(Integer load_open_bal_ton) {
		this.load_open_bal_ton = load_open_bal_ton;
	}

	public Integer getTotal_disc_completed() {
		return total_disc_completed;
	}

	public void setTotal_disc_completed(Integer total_disc_completed) {
		this.total_disc_completed = total_disc_completed;
	}

	public Integer getTotal_load_completed() {
		return total_load_completed;
	}

	public void setTotal_load_completed(Integer total_load_completed) {
		this.total_load_completed = total_load_completed;
	}

	public Integer getBal_disc_ton() {
		return bal_disc_ton;
	}

	public void setBal_disc_ton(Integer bal_disc_ton) {
		this.bal_disc_ton = bal_disc_ton;
	}

	public Integer getBal_load_ton() {
		return bal_load_ton;
	}

	public void setBal_load_ton(Integer bal_load_ton) {
		this.bal_load_ton = bal_load_ton;
	}

	public Integer getBal_hatch_nbr() {
		return bal_hatch_nbr;
	}

	public void setBal_hatch_nbr(Integer bal_hatch_nbr) {
		this.bal_hatch_nbr = bal_hatch_nbr;
	}

	public GbccCargoOprDetId getId() {
        return id;
    }

    public void setId(GbccCargoOprDetId id) {
        this.id = id;
    }

    public Integer getDiscCompletedTon() {
        return discCompletedTon;
    }

    public void setDiscCompletedTon(Integer discCompletedTon) {
        this.discCompletedTon = discCompletedTon;
    }

    public Integer getLoadCompletedTon() {
        return loadCompletedTon;
    }

    public void setLoadCompletedTon(Integer loadCompletedTon) {
        this.loadCompletedTon = loadCompletedTon;
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

    public Integer getDiscOpenBal() {
        return discOpenBal;
    }
    public void setDiscOpenBal(Integer discOpenBal) {
        this.discOpenBal = discOpenBal;
    }
    public Integer getLoadOpenBal() {
        return loadOpenBal;
    }
    public void setLoadOpenBal(Integer loadOpenBal) {
        this.loadOpenBal = loadOpenBal;
    }
    public Integer getDiscBal() {
        return discBal;
    }
    public void setDiscBal(Integer discBal) {
        this.discBal = discBal;
    }
    public Integer getLoadBal() {
        return loadBal;
    }
    public void setLoadBal(Integer loadBal) {
        this.loadBal = loadBal;
    }
    public Integer getDiscTotalCompleted() {
        return discTotalCompleted;
    }
    public void setDiscTotalCompleted(Integer discTotalCompleted) {
        this.discTotalCompleted = discTotalCompleted;
    }
    public Integer getLoadTotalCompleted() {
        return loadTotalCompleted;
    }
    public void setLoadTotalCompleted(Integer loadTotalCompleted) {
        this.loadTotalCompleted = loadTotalCompleted;
    }
    
    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("discCompletedTon");
    	auditFields.add("loadCompletedTon");
    	auditFields.add("discRemarks");
    	auditFields.add("loadRemarks");
    	auditFields.add("discOpenBal");
    	auditFields.add("loadOpenBal");
    	auditFields.add("discBal");
    	auditFields.add("loadBal");
    	auditFields.add("discTotalCompleted");
    	auditFields.add("loadTotalCompleted");
    	auditFields.add("discWeatherCd");
    	auditFields.add("discActivityCd");
    	auditFields.add("loadWeatherCd");
    	auditFields.add("loadActivityCd");
    }
    
	protected void setDateFields() { 
		dateFields = new HashSet();
	}
	
    public String getDiscWeatherCd() {
		return discWeatherCd;
	}

	public void setDiscWeatherCd(String discWeatherCd) {
		this.discWeatherCd = discWeatherCd;
	}

	public String getDiscActivityCd() {
		return discActivityCd;
	}

	public void setDiscActivityCd(String discActivityCd) {
		this.discActivityCd = discActivityCd;
	}

	public String getLoadWeatherCd() {
		return loadWeatherCd;
	}

	public void setLoadWeatherCd(String loadWeatherCd) {
		this.loadWeatherCd = loadWeatherCd;
	}

	public String getLoadActivityCd() {
		return loadActivityCd;
	}

	public void setLoadActivityCd(String loadActivityCd) {
		this.loadActivityCd = loadActivityCd;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTallysheetDet extends AuditLogRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private GbccCargoTallysheetDetId id;
	
	private String vvCd;
    private Date createDttm;
    private String oprType;
    private Integer hatch_nbr;
 
    private Integer transQty;	//Package Received
    private BigDecimal transTon; //Tons Received
    private String recvInd;
    private String lastModifyUserId;
    private Date lastModifyDttm;

    private BigDecimal nbrTon;	//Tons
    private Integer nbrPkgs; //No. of Package

    private Integer completedQty;
    private BigDecimal completedTon;
    private Integer balQty;
    private BigDecimal balTon;
    
    // START: Added by VietNguyen 28-May-2012 for StevedoreNet
    private String blBkNbr;//for get data from JSON, not map with DB
    private String status;
    private String ldPort;
    private String detail;
    private String markNbr;
    private String cargoDes;

    private String remarks;
    
    private String bl_bk_nbr;
    private Integer nbr_pkgs;
    private BigDecimal total_ton;
    private Integer total_comp_qty;
    private BigDecimal total_comp_ton;
    private Integer bal_comp_qty;
    private BigDecimal bal_comp_ton;
    private String ld_port;
    private String  detail1;
    private String detail2;
    private String  mark_nbr;
    private String crg_des;
    
    
    // END: Added by VietNguyen 28-May-2012 for StevedoreNet
    
    public GbccCargoTallysheetDet() {
    }

    public GbccCargoTallysheetDet(Integer transQty, BigDecimal transTon, String recvInd, String lastModifyUserId, Date lastModifyDttm) {
        this.transQty = transQty;
        this.transTon = transTon;
        this.recvInd = recvInd;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoTallysheetDet(GbccCargoTallysheetDetId id, Integer transQty, BigDecimal transTon, String recvInd, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.transQty = transQty;
        this.transTon = transTon;
        this.recvInd = recvInd;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    
    
    
	public String getBl_bk_nbr() {
		return bl_bk_nbr;
	}

	public void setBl_bk_nbr(String bl_bk_nbr) {
		this.bl_bk_nbr = bl_bk_nbr;
	}

	public Integer getNbr_pkgs() {
		return nbr_pkgs;
	}

	public void setNbr_pkgs(Integer nbr_pkgs) {
		this.nbr_pkgs = nbr_pkgs;
	}

	public BigDecimal getTotal_ton() {
		return total_ton;
	}

	public void setTotal_ton(BigDecimal total_ton) {
		this.total_ton = total_ton;
	}

	public Integer getTotal_comp_qty() {
		return total_comp_qty;
	}

	public void setTotal_comp_qty(Integer total_comp_qty) {
		this.total_comp_qty = total_comp_qty;
	}

	public BigDecimal getTotal_comp_ton() {
		return total_comp_ton;
	}

	public void setTotal_comp_ton(BigDecimal total_comp_ton) {
		this.total_comp_ton = total_comp_ton;
	}

	public Integer getBal_comp_qty() {
		return bal_comp_qty;
	}

	public void setBal_comp_qty(Integer bal_comp_qty) {
		this.bal_comp_qty = bal_comp_qty;
	}

	public BigDecimal getBal_comp_ton() {
		return bal_comp_ton;
	}

	public void setBal_comp_ton(BigDecimal bal_comp_ton) {
		this.bal_comp_ton = bal_comp_ton;
	}

	public String getLd_port() {
		return ld_port;
	}

	public void setLd_port(String ld_port) {
		this.ld_port = ld_port;
	}

	public String getDetail1() {
		return detail1;
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	public String getMark_nbr() {
		return mark_nbr;
	}

	public void setMark_nbr(String mark_nbr) {
		this.mark_nbr = mark_nbr;
	}

	public String getCrg_des() {
		return crg_des;
	}

	public void setCrg_des(String crg_des) {
		this.crg_des = crg_des;
	}

	public GbccCargoTallysheetDetId getId() {
        return id;
    }

    public void setId(GbccCargoTallysheetDetId id) {
        this.id = id;
    }

    public Integer getTransQty() {
        return transQty;
    }

    public void setTransQty(Integer transQty) {
        this.transQty = transQty;
    }

    public BigDecimal getTransTon() {
        return transTon;
    }

    public void setTransTon(BigDecimal transTon) {
        this.transTon = transTon;
    }

    public String getRecvInd() {
        return recvInd;
    }

    public void setRecvInd(String recvInd) {
        this.recvInd = recvInd;
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

    public BigDecimal getNbrTon() {
    	return nbrTon;
    }
    public void setNbrTon(BigDecimal nbrTon) {
    	this.nbrTon = nbrTon;
    }

    public Integer getNbrPkgs() {
    	return nbrPkgs;
    }
    public void setNbrPkgs(Integer nbrPkgs) {
    	this.nbrPkgs = nbrPkgs;
    }

    public BigDecimal getTonPerPkgs() {
    	if (nbrTon.intValue() == 0)
    		return new BigDecimal(0);
    	if(nbrPkgs.intValue() == 0)
    		return new BigDecimal(0);

    	return new BigDecimal(nbrTon.doubleValue()/nbrPkgs.intValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Integer getCompletedQty() {
    	return completedQty;
    }
    public void setCompletedQty(Integer completedQty) {
    	this.completedQty = completedQty;
    }
    public BigDecimal getCompletedTon() {
    	return completedTon;
    }
    public void setCompletedTon(BigDecimal completedTon) {
    	this.completedTon = completedTon;
    }

    public Integer getBalQty() {
    	return balQty;
    }
    public void setBalQty(Integer balQty) {
    	this.balQty = balQty;
    }
    public BigDecimal getBalTon() {
    	return balTon;
    }
    public void setBalTon(BigDecimal balTon) {
    	this.balTon = balTon;
    }

	protected void setAuditableFields() {
    	auditFields = new HashSet();
    	auditFields.add("transQty");
    	auditFields.add("transTon");
    	auditFields.add("recvInd");
    	auditFields.add("lastModifyUserId");
    	auditFields.add("lastModifyDttm");

    }

	protected void setDateFields() {
		dateFields = new HashSet();
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

	public String getOprType() {
		return oprType;
	}

	public void setOprType(String oprType) {
		this.oprType = oprType;
	}

	public Integer getHatchNbr() {
		return hatch_nbr;
	}

	public void setHatchNbr(Integer hatchNbr) {
		this.hatch_nbr = hatchNbr;
	}

	public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
	
	public String getBlBkNbr() {
        return blBkNbr;
    }

    public void setBlBkNbr(String blBkNbr) {
        this.blBkNbr = blBkNbr;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the ldPort
     */
    public String getLdPort() {
        return ldPort;
    }

    /**
     * @param ldPort the ldPort to set
     */
    public void setLdPort(String ldPort) {
        this.ldPort = ldPort;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

	public String getMarkNbr() {
		return markNbr;
	}

	public void setMarkNbr(String markNbr) {
		this.markNbr = markNbr;
	}

	public String getCargoDes() {
		return cargoDes;
	}

	public void setCargoDes(String cargoDes) {
		this.cargoDes = cargoDes;
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

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccVslProd extends AuditLogRecord implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private GbccVslProdId id;
	private Date createDttm;
    private String vvCd;
    private String stevCoCd;
    private String shiftCd;
    private Date shiftDttm; 
    private Date shiftEndDttm;
    private String vslType;
    private Integer openBal;
    private Integer openBalDisc;
    private Integer openBalLoad;
    private Date workStartDttm;
    private BigDecimal totWrkHr;
    private BigDecimal totPortHr;
    private Integer totDiscHr;
    private Integer totLoadHr;
    private Integer totCompleted;
    private Integer totDiscCompleted;
    private Integer totLoadCompleted;
    private Integer balTotal;
    private Integer balDisc;
    private Integer balLoad;
    private Integer prodRateNett;
    private Integer prodRateGross;
    private Integer prodRateDisc;
    private Integer prodRateLoad;
    private Date projectedEtu;
    private Date etuDttm;
    private Integer etuVariationHr;
    private Date colDttm;
    private Date codDttm;
    private String affectPlannedEtu;
    private String underPerfReasonCd;
    private String underPerfRemarks;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    private Date shiftStartDttm;
    private String misc_type_nm;
    
    private String underPerfReasonName;
    
    public GbccVslProd() {
    }

    public GbccVslProd(String shiftCd, Date shiftDttm, Date shiftStartDttm, Date shiftEndDttm, String vslType, Integer openBal, Integer openBalDisc, Integer openBalLoad, Date workStartDttm, BigDecimal totWrkHr, BigDecimal totPortHr, Integer totDiscHr, Integer totLoadHr, Integer totCompleted, Integer totDiscCompleted, Integer totLoadCompleted, Integer balTotal, Integer balDisc, Integer balLoad, Integer prodRateNett, Integer prodRateGross, Integer prodRateDisc, Integer prodRateLoad, Date projectedEtu, Date etuDttm, Integer etuVariationHr, Date colDttm, Date codDttm, String affectPlannedEtu, String underPerfReasonCd, String underPerfRemarks, String lastModifyUserId, Date lastModifyDttm) {
        this.shiftCd = shiftCd;
        this.shiftDttm = shiftDttm;
        this.shiftStartDttm = shiftStartDttm;
        this.shiftEndDttm = shiftEndDttm;
        this.vslType = vslType;
        this.openBal = openBal;
        this.openBalDisc = openBalDisc;
        this.openBalLoad = openBalLoad;
        this.workStartDttm = workStartDttm;
        this.totWrkHr = totWrkHr;
        this.totPortHr = totPortHr;
        this.totDiscHr = totDiscHr;
        this.totLoadHr = totLoadHr;
        this.totCompleted = totCompleted;
        this.totDiscCompleted = totDiscCompleted;
        this.totLoadCompleted = totLoadCompleted;
        this.balTotal = balTotal;
        this.balDisc = balDisc;
        this.balLoad = balLoad;
        this.prodRateNett = prodRateNett;
        this.prodRateGross = prodRateGross;
        this.prodRateDisc = prodRateDisc;
        this.prodRateLoad = prodRateLoad;
        this.projectedEtu = projectedEtu;
        this.etuDttm = etuDttm;
        this.etuVariationHr = etuVariationHr;
        this.colDttm = colDttm;
        this.codDttm = codDttm;
        this.affectPlannedEtu = affectPlannedEtu;
        this.underPerfReasonCd = underPerfReasonCd;
        this.underPerfRemarks = underPerfRemarks;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccVslProd(GbccVslProdId id, String shiftCd, Date shiftDttm, Date shiftStartDttm, Date shiftEndDttm, String vslType, Integer openBal, Integer openBalDisc, Integer openBalLoad, Date workStartDttm, BigDecimal totWrkHr, BigDecimal totPortHr, Integer totDiscHr, Integer totLoadHr, Integer totCompleted, Integer totDiscCompleted, Integer totLoadCompleted, Integer balTotal, Integer balDisc, Integer balLoad, Integer prodRateNett, Integer prodRateGross, Integer prodRateDisc, Integer prodRateLoad, Date projectedEtu, Date etuDttm, Integer etuVariationHr, Date col, Date cod, String affectPlannedEtu, String underPerfReasonCd, String underPerfRemarks, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.shiftCd = shiftCd;
        this.shiftDttm = shiftDttm;
        this.shiftStartDttm = shiftStartDttm;
        this.shiftEndDttm = shiftEndDttm;
        this.vslType = vslType;
        this.openBal = openBal;
        this.openBalDisc = openBalDisc;
        this.openBalLoad = openBalLoad;
        this.workStartDttm = workStartDttm;
        this.totWrkHr = totWrkHr;
        this.totPortHr = totPortHr;
        this.totDiscHr = totDiscHr;
        this.totLoadHr = totLoadHr;
        this.totCompleted = totCompleted;
        this.totDiscCompleted = totDiscCompleted;
        this.totLoadCompleted = totLoadCompleted;
        this.balTotal = balTotal;
        this.balDisc = balDisc;
        this.balLoad = balLoad;
        this.prodRateNett = prodRateNett;
        this.prodRateGross = prodRateGross;
        this.prodRateDisc = prodRateDisc;
        this.prodRateLoad = prodRateLoad;
        this.projectedEtu = projectedEtu;
        this.etuDttm = etuDttm;
        this.etuVariationHr = etuVariationHr;
        this.colDttm = col;
        this.codDttm = cod;
        this.affectPlannedEtu = affectPlannedEtu;
        this.underPerfReasonCd = underPerfReasonCd;
        this.underPerfRemarks = underPerfRemarks;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    
    
    
    public String getMisc_type_nm() {
		return misc_type_nm;
	}

	public void setMisc_type_nm(String misc_type_nm) {
		this.misc_type_nm = misc_type_nm;
	}

	public Date getCreateDttm() {
		return createDttm;
	}

	public void setCreateDttm(Date createDttm) {
		this.createDttm = createDttm;
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

	public GbccVslProdId getId() {
        return id;
    }

    public void setId(GbccVslProdId id) {
        this.id = id;
    }

    public String getShiftCd() {
        return shiftCd;
    }

    public void setShiftCd(String shiftCd) {
        this.shiftCd = shiftCd;
    }

    public Date getShiftDttm() {
        return shiftDttm;
    }

    public void setShiftDttm(Date shiftDttm) {
        this.shiftDttm = shiftDttm;
    }

    public Date getShiftStartDttm() {
        return shiftStartDttm;
    }

    public void setShiftStartDttm(Date shiftStartDttm) {
        this.shiftStartDttm = shiftStartDttm;
    }
    
    public Date getShiftEndDttm() {
        return shiftEndDttm;
    }

    public void setShiftEndDttm(Date shiftEndDttm) {
        this.shiftEndDttm = shiftEndDttm;
    }

    public String getVslType() {
        return vslType;
    }

    public void setVslType(String vslType) {
        this.vslType = vslType;
    }

    public Integer getOpenBal() {
        return openBal;
    }

    public void setOpenBal(Integer openBal) {
        this.openBal = openBal;
    }

    public Integer getOpenBalDisc() {
        return openBalDisc;
    }

    public void setOpenBalDisc(Integer openBalDisc) {
        this.openBalDisc = openBalDisc;
    }

    public Integer getOpenBalLoad() {
        return openBalLoad;
    }

    public void setOpenBalLoad(Integer openBalLoad) {
        this.openBalLoad = openBalLoad;
    }

    public Date getWorkStartDttm() {
        return workStartDttm;
    }

    public void setWorkStartDttm(Date workStartDttm) {
        this.workStartDttm = workStartDttm;
    }

    public BigDecimal getTotWrkHr() {
        return totWrkHr;
    }

    public void setTotWrkHr(BigDecimal totWrkHr) {
        this.totWrkHr = totWrkHr;
    }

    public BigDecimal getTotPortHr() {
        return totPortHr;
    }

    public void setTotPortHr(BigDecimal totPortHr) {
        this.totPortHr = totPortHr;
    }

    public Integer getTotDiscHr() {
        return totDiscHr;
    }

    public void setTotDiscHr(Integer totDiscHr) {
        this.totDiscHr = totDiscHr;
    }

    public Integer getTotLoadHr() {
        return totLoadHr;
    }

    public void setTotLoadHr(Integer totLoadHr) {
        this.totLoadHr = totLoadHr;
    }

    public Integer getTotCompleted() {
        return totCompleted;
    }

    public void setTotCompleted(Integer totCompleted) {
        this.totCompleted = totCompleted;
    }

    public Integer getTotDiscCompleted() {
        return totDiscCompleted;
    }

    public void setTotDiscCompleted(Integer totDiscCompleted) {
        this.totDiscCompleted = totDiscCompleted;
    }

    public Integer getTotLoadCompleted() {
        return totLoadCompleted;
    }

    public void setTotLoadCompleted(Integer totLoadCompleted) {
        this.totLoadCompleted = totLoadCompleted;
    }

    public Integer getBalTotal() {
        return balTotal;
    }

    public void setBalTotal(Integer balTotal) {
        this.balTotal = balTotal;
    }

    public Integer getBalDisc() {
        return balDisc;
    }

    public void setBalDisc(Integer balDisc) {
        this.balDisc = balDisc;
    }

    public Integer getBalLoad() {
        return balLoad;
    }

    public void setBalLoad(Integer balLoad) {
        this.balLoad = balLoad;
    }
    
    public Integer getProdRateNett() {
        return prodRateNett;
    }

    public void setProdRateNett(Integer prodRateNett) {
        this.prodRateNett = prodRateNett;
    }

    public Integer getProdRateGross() {
        return prodRateGross;
    }

    public void setProdRateGross(Integer prodRateGross) {
        this.prodRateGross = prodRateGross;
    }

    public Integer getProdRateDisc() {
        return prodRateDisc;
    }

    public void setProdRateDisc(Integer prodRateDisc) {
        this.prodRateDisc = prodRateDisc;
    }

    public Integer getProdRateLoad() {
        return prodRateLoad;
    }

    public void setProdRateLoad(Integer prodRateLoad) {
        this.prodRateLoad = prodRateLoad;
    }

    public Date getProjectedEtu() {
        return projectedEtu;
    }

    public void setProjectedEtu(Date projectedEtu) {
        this.projectedEtu = projectedEtu;
    }

    public Date getEtuDttm() {
        return etuDttm;
    }

    public void setEtuDttm(Date etuDttm) {
        this.etuDttm = etuDttm;
    }

    public Integer getEtuVariationHr() {
        return etuVariationHr;
    }

    public void setEtuVariationHr(Integer etuVariationHr) {
        this.etuVariationHr = etuVariationHr;
    }

    public Date getColDttm() {
        return colDttm;
    }

    public void setColDttm(Date colDttm) {
        this.colDttm = colDttm;
    }

    public Date getCodDttm() {
        return codDttm;
    }

    public void setCodDttm(Date codDttm) {
        this.codDttm = codDttm;
    }

    public String getAffectPlannedEtu() {
        return affectPlannedEtu;
    }

    public void setAffectPlannedEtu(String affectPlannedEtu) {
        this.affectPlannedEtu = affectPlannedEtu;
    }

    public String getUnderPerfReasonCd() {
        return underPerfReasonCd;
    }

    public void setUnderPerfReasonCd(String underPerfReasonCd) {
        this.underPerfReasonCd = underPerfReasonCd;
    }

    public String getUnderPerfRemarks() {
        return underPerfRemarks;
    }

    public void setUnderPerfRemarks(String underPerfRemarks) {
        this.underPerfRemarks = underPerfRemarks;
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
    
    public String getUnderPerfReasonName() {
        return underPerfReasonName;
    }

    public void setUnderPerfReasonName(String underPerfReasonName) {
        this.underPerfReasonName = underPerfReasonName;
    }
    
    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("shiftCd");
    	auditFields.add("shiftDttm");
    	auditFields.add("shiftStartDttm");
    	auditFields.add("shiftEndDttm");
    	auditFields.add("vslType");
    	auditFields.add("openBal");
    	auditFields.add("openBalDisc");
    	auditFields.add("openBalLoad");
    	auditFields.add("workStartDttm");
    	auditFields.add("totWrkHr");
    	auditFields.add("totPortHr");
    	auditFields.add("totDiscHr");
    	auditFields.add("totLoadHr");
    	auditFields.add("totCompleted");
    	auditFields.add("totDiscCompleted");
    	auditFields.add("totLoadCompleted");
    	auditFields.add("balTotal");
    	auditFields.add("balDisc");
    	auditFields.add("balLoad");
    	auditFields.add("prodRateNett");
    	auditFields.add("prodRateGross");
    	auditFields.add("prodRateDisc");
    	auditFields.add("prodRateLoad");
    	auditFields.add("projectedEtu");
    	auditFields.add("etuDttm");
    	auditFields.add("etuVariationHr");
    	auditFields.add("colDttm");
    	auditFields.add("codDttm");
    	auditFields.add("affectPlannedEtu");
    	auditFields.add("underPerfReasonCd");
    	auditFields.add("underPerfRemarks");
    	auditFields.add("lastModifyUserId");
    	auditFields.add("lastModifyDttm");
    	    
    }
    
    protected void setDateFields() { 
		dateFields = new HashSet();
		dateFields.add("shiftDttm");
		dateFields.add("shiftStartDttm");
		dateFields.add("shiftEndDttm");
		dateFields.add("projectedEtu");
		dateFields.add("etuDttm");
		dateFields.add("colDttm");
		dateFields.add("codDttm");
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

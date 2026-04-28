package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * xe.Berthing 11/18/2009 09:47:14
 * 
 */
public class Berthing implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private BerthingId id;
	private Date etbDttm;
	private Date etuDttm;
	private Date atbDttm;
	private Date atuDttm;
	private Date firstDiscDttm;
	private Date firstLoadDttm;
	private Date codDttm;
	private Date colDttm;
	private String berthNbr;
	private Integer wharfMarkFr;
	private Integer wharfMarkTo;
	private String wharfSideInd;
	private String lastModifyUserId;
	private Date lastModifyDttm;
	private String mvtInd;
	private Date gbCodDttm;
	private Date gbColDttm;
	private Date gbFirstActDttm;
	private Date gbLastActDttm;
	private Date gbBcodDttm;
	private Date gbBcolDttm;
	private Integer haulDist;
	private String haulDirn;
	private Integer gangNbr;
	private Integer hatchNbr;
	private String delayRsnCd;
	private String remarks;
	private Integer totGenCargoAct;
	private Date gbFirstCargoActDttm;
	private Date gbFirstDiscDttm;
	private Date gbFirstLoadDttm;
	private BigDecimal actDraftForth;
	private BigDecimal actDraftAft;
	private String actWharfSideInd;
	private Integer actWharfMarkFr;
	private Integer actWharfMarkTo;
	private String berthRemarks;

	private String mvtName;

	public Berthing() {
	}

	public Berthing(Date etbDttm, Date etuDttm, Date atbDttm, Date atuDttm, Date firstDiscDttm, Date firstLoadDttm,
			Date codDttm, Date colDttm, String berthNbr, Integer wharfMarkFr, Integer wharfMarkTo, String wharfSideInd,
			String lastModifyUserId, Date lastModifyDttm, String mvtInd, Date gbCodDttm, Date gbColDttm,
			Date gbFirstActDttm, Date gbLastActDttm, Date gbBcodDttm, Date gbBcolDttm, Integer haulDist,
			String haulDirn, Integer gangNbr, Integer hatchNbr, String delayRsnCd, String remarks,
			Integer totGenCargoAct, Date gbFirstCargoActDttm, Date gbFirstDiscDttm, Date gbFirstLoadDttm,
			BigDecimal actDraftForth, BigDecimal actDraftAft, String actWharfSideInd, Integer actWharfMarkFr,
			Integer actWharfMarkTo, String berthRemarks) {
		this.etbDttm = etbDttm;
		this.etuDttm = etuDttm;
		this.atbDttm = atbDttm;
		this.atuDttm = atuDttm;
		this.firstDiscDttm = firstDiscDttm;
		this.firstLoadDttm = firstLoadDttm;
		this.codDttm = codDttm;
		this.colDttm = colDttm;
		this.berthNbr = berthNbr;
		this.wharfMarkFr = wharfMarkFr;
		this.wharfMarkTo = wharfMarkTo;
		this.wharfSideInd = wharfSideInd;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.mvtInd = mvtInd;
		this.gbCodDttm = gbCodDttm;
		this.gbColDttm = gbColDttm;
		this.gbFirstActDttm = gbFirstActDttm;
		this.gbLastActDttm = gbLastActDttm;
		this.gbBcodDttm = gbBcodDttm;
		this.gbBcolDttm = gbBcolDttm;
		this.haulDist = haulDist;
		this.haulDirn = haulDirn;
		this.gangNbr = gangNbr;
		this.hatchNbr = hatchNbr;
		this.delayRsnCd = delayRsnCd;
		this.remarks = remarks;
		this.totGenCargoAct = totGenCargoAct;
		this.gbFirstCargoActDttm = gbFirstCargoActDttm;

		this.gbFirstDiscDttm = gbFirstDiscDttm;
		this.gbFirstLoadDttm = gbFirstLoadDttm;
		this.actDraftForth = actDraftForth;
		this.actDraftAft = actDraftAft;
		this.actWharfSideInd = actWharfSideInd;
		this.actWharfMarkFr = actWharfMarkFr;
		this.actWharfMarkTo = actWharfMarkTo;
		this.berthRemarks = berthRemarks;
	}

	public Berthing(BerthingId id, Date etbDttm, Date etuDttm, Date atbDttm, Date atuDttm, Date firstDiscDttm,
			Date firstLoadDttm, Date codDttm, Date colDttm, String berthNbr, Integer wharfMarkFr, Integer wharfMarkTo,
			String wharfSideInd, String lastModifyUserId, Date lastModifyDttm, String mvtInd, Date gbCodDttm,
			Date gbColDttm, Date gbFirstActDttm, Date gbLastActDttm, Date gbBcodDttm, Date gbBcolDttm, Integer haulDist,
			String haulDirn, Integer gangNbr, Integer hatchNbr, String delayRsnCd, String remarks,
			Integer totGenCargoAct, Date gbFirstCargoActDttm, Date gbFirstDiscDttm, Date gbFirstLoadDttm,
			BigDecimal actDraftForth, BigDecimal actDraftAft, String actWharfSideInd, Integer actWharfMarkFr,
			Integer actWharfMarkTo, String berthRemarks) {
		this.id = id;
		this.etbDttm = etbDttm;
		this.etuDttm = etuDttm;
		this.atbDttm = atbDttm;
		this.atuDttm = atuDttm;
		this.firstDiscDttm = firstDiscDttm;
		this.firstLoadDttm = firstLoadDttm;
		this.codDttm = codDttm;
		this.colDttm = colDttm;
		this.berthNbr = berthNbr;
		this.wharfMarkFr = wharfMarkFr;
		this.wharfMarkTo = wharfMarkTo;
		this.wharfSideInd = wharfSideInd;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.mvtInd = mvtInd;
		this.gbCodDttm = gbCodDttm;
		this.gbColDttm = gbColDttm;
		this.gbFirstActDttm = gbFirstActDttm;
		this.gbLastActDttm = gbLastActDttm;
		this.gbBcodDttm = gbBcodDttm;
		this.gbBcolDttm = gbBcolDttm;
		this.haulDist = haulDist;
		this.haulDirn = haulDirn;
		this.gangNbr = gangNbr;
		this.hatchNbr = hatchNbr;
		this.delayRsnCd = delayRsnCd;
		this.remarks = remarks;
		this.totGenCargoAct = totGenCargoAct;
		this.gbFirstCargoActDttm = gbFirstCargoActDttm;

		this.gbFirstDiscDttm = gbFirstDiscDttm;
		this.gbFirstLoadDttm = gbFirstLoadDttm;
		this.actDraftForth = actDraftForth;
		this.actDraftAft = actDraftAft;
		this.actWharfSideInd = actWharfSideInd;
		this.actWharfMarkFr = actWharfMarkFr;
		this.actWharfMarkTo = actWharfMarkTo;
		this.berthRemarks = berthRemarks;
	}

	public BerthingId getId() {
		return id;
	}

	public void setId(BerthingId id) {
		this.id = id;
	}

	public Date getEtbDttm() {
		return etbDttm;
	}

	public void setEtbDttm(Date etbDttm) {
		this.etbDttm = etbDttm;
	}

	public Date getEtuDttm() {
		return etuDttm;
	}

	public void setEtuDttm(Date etuDttm) {
		this.etuDttm = etuDttm;
	}

	public Date getAtbDttm() {
		return atbDttm;
	}

	public void setAtbDttm(Date atbDttm) {
		this.atbDttm = atbDttm;
	}

	public Date getAtuDttm() {
		return atuDttm;
	}

	public void setAtuDttm(Date atuDttm) {
		this.atuDttm = atuDttm;
	}

	public Date getFirstDiscDttm() {
		return firstDiscDttm;
	}

	public void setFirstDiscDttm(Date firstDiscDttm) {
		this.firstDiscDttm = firstDiscDttm;
	}

	public Date getFirstLoadDttm() {
		return firstLoadDttm;
	}

	public void setFirstLoadDttm(Date firstLoadDttm) {
		this.firstLoadDttm = firstLoadDttm;
	}

	public Date getCodDttm() {
		return codDttm;
	}

	public void setCodDttm(Date codDttm) {
		this.codDttm = codDttm;
	}

	public Date getColDttm() {
		return colDttm;
	}

	public void setColDttm(Date colDttm) {
		this.colDttm = colDttm;
	}

	public String getBerthNbr() {
		return berthNbr;
	}

	public void setBerthNbr(String berthNbr) {
		this.berthNbr = berthNbr;
	}

	public Integer getWharfMarkFr() {
		return wharfMarkFr;
	}

	public void setWharfMarkFr(Integer wharfMarkFr) {
		this.wharfMarkFr = wharfMarkFr;
	}

	public Integer getWharfMarkTo() {
		return wharfMarkTo;
	}

	public void setWharfMarkTo(Integer wharfMarkTo) {
		this.wharfMarkTo = wharfMarkTo;
	}

	public String getWharfSideInd() {
		return wharfSideInd;
	}

	public void setWharfSideInd(String wharfSideInd) {
		this.wharfSideInd = wharfSideInd;
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

	public String getMvtInd() {
		return mvtInd;
	}

	public void setMvtInd(String mvtInd) {
		this.mvtInd = mvtInd;
	}

	public Date getGbCodDttm() {
		return gbCodDttm;
	}

	public void setGbCodDttm(Date gbCodDttm) {
		this.gbCodDttm = gbCodDttm;
	}

	public Date getGbColDttm() {
		return gbColDttm;
	}

	public void setGbColDttm(Date gbColDttm) {
		this.gbColDttm = gbColDttm;
	}

	public Date getGbFirstActDttm() {
		return gbFirstActDttm;
	}

	public void setGbFirstActDttm(Date gbFirstActDttm) {
		this.gbFirstActDttm = gbFirstActDttm;
	}

	public Date getGbLastActDttm() {
		return gbLastActDttm;
	}

	public void setGbLastActDttm(Date gbLastActDttm) {
		this.gbLastActDttm = gbLastActDttm;
	}

	public Date getGbBcodDttm() {
		return gbBcodDttm;
	}

	public void setGbBcodDttm(Date gbBcodDttm) {
		this.gbBcodDttm = gbBcodDttm;
	}

	public Date getGbBcolDttm() {
		return gbBcolDttm;
	}

	public void setGbBcolDttm(Date gbBcolDttm) {
		this.gbBcolDttm = gbBcolDttm;
	}

	public Integer getHaulDist() {
		return haulDist;
	}

	public void setHaulDist(Integer haulDist) {
		this.haulDist = haulDist;
	}

	public String getHaulDirn() {
		return haulDirn;
	}

	public void setHaulDirn(String haulDirn) {
		this.haulDirn = haulDirn;
	}

	public Integer getGangNbr() {
		return gangNbr;
	}

	public void setGangNbr(Integer gangNbr) {
		this.gangNbr = gangNbr;
	}

	public Integer getHatchNbr() {
		return hatchNbr;
	}

	public void setHatchNbr(Integer hatchNbr) {
		this.hatchNbr = hatchNbr;
	}

	public String getDelayRsnCd() {
		return delayRsnCd;
	}

	public void setDelayRsnCd(String delayRsnCd) {
		this.delayRsnCd = delayRsnCd;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getTotGenCargoAct() {
		return totGenCargoAct;
	}

	public void setTotGenCargoAct(Integer totGenCargoAct) {
		this.totGenCargoAct = totGenCargoAct;
	}

	public Date getGbFirstCargoActDttm() {
		return gbFirstCargoActDttm;
	}

	public void setGbFirstCargoActDttm(Date gbFirstCargoActDttm) {
		this.gbFirstCargoActDttm = gbFirstCargoActDttm;
	}

	public Date getGbFirstDiscDttm() {
		return gbFirstDiscDttm;
	}

	public void setGbFirstDiscDttm(Date gbFirstDiscDttm) {
		this.gbFirstDiscDttm = gbFirstDiscDttm;
	}

	public Date getGbFirstLoadDttm() {
		return gbFirstLoadDttm;
	}

	public void setGbFirstLoadDttm(Date gbFirstLoadDttm) {
		this.gbFirstLoadDttm = gbFirstLoadDttm;
	}

	public BigDecimal getActDraftForth() {
		return actDraftForth;
	}

	public void setActDraftForth(BigDecimal actDraftForth) {
		this.actDraftForth = actDraftForth;
	}

	public BigDecimal getActDraftAft() {
		return actDraftAft;
	}

	public void setActDraftAft(BigDecimal actDraftAft) {
		this.actDraftAft = actDraftAft;
	}

	public String getActWharfSideInd() {
		return actWharfSideInd;
	}

	public void setActWharfSideInd(String actWharfSideInd) {
		this.actWharfSideInd = actWharfSideInd;
	}

	public Integer getActWharfMarkFr() {
		return actWharfMarkFr;
	}

	public void setActWharfMarkFr(Integer actWharfMarkFr) {
		this.actWharfMarkFr = actWharfMarkFr;
	}

	public Integer getActWharfMarkTo() {
		return actWharfMarkTo;
	}

	public void setActWharfMarkTo(Integer actWharfMarkTo) {
		this.actWharfMarkTo = actWharfMarkTo;
	}

	public String getBerthRemarks() {
		return berthRemarks;
	}

	public void setBerthRemarks(String berthRemarks) {
		this.berthRemarks = berthRemarks;
	}

	public Date getContainerFirstActivity() {

		if (this.firstDiscDttm != null && this.firstLoadDttm != null) {
			if (this.firstDiscDttm.before(this.firstLoadDttm))
				return firstDiscDttm;
			else
				return firstLoadDttm;
		}

		if (this.firstDiscDttm == null)
			return firstLoadDttm;

		if (this.firstLoadDttm == null)
			return firstDiscDttm;

		return null;
	}

	public Date getContainerLastActivity() {

		if (this.codDttm != null && this.colDttm != null) {
			if (this.codDttm.before(this.colDttm))
				return codDttm;
			else
				return colDttm;
		}

		if (this.codDttm == null)
			return colDttm;

		if (this.colDttm == null)
			return codDttm;

		return null;
	}

	public String getMvtName() {
		return mvtName;
	}

	public void setMvtName(String mvtName) {
		this.mvtName = mvtName;
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

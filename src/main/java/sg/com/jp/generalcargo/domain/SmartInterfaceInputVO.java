package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SmartInterfaceInputVO implements java.io.Serializable {

	// Field descriptor #29 J
	private static final long serialVersionUID = -2073010144960983811L;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String vvCd;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String userId;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String serverNm;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String serverIp;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String refType;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String refNbr;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String classNm;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String classDesc;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String stgType;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String stgZone;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String actionCd;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String estType;

	// Field descriptor #47 Ljava/math/BigDecimal;
	private java.math.BigDecimal tonnage;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String occSrcCd;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String cntrSize;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String cntrType;

	// Field descriptor #52 Ljava/lang/Integer;
	private java.lang.Integer nbrPkgs;

	// Field descriptor #54 Ljava/util/Date;
	private java.util.Date atbDttm;

	// Field descriptor #54 Ljava/util/Date;
	private java.util.Date atuDttm;

	// Field descriptor #54 Ljava/util/Date;
	private java.util.Date transDttm;

	// Field descriptor #34 Ljava/lang/String;
	private java.lang.String transNbr;

	// Field descriptor #59 Z
	private boolean isStayingInJP;

	// Field descriptor #61 Ljava/lang/Long;
	private java.lang.Long cntrSeqNbr;

	public java.lang.String getVvCd() {
		return vvCd;
	}

	public void setVvCd(java.lang.String vvCd) {
		this.vvCd = vvCd;
	}

	public java.lang.String getUserId() {
		return userId;
	}

	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	public java.lang.String getServerNm() {
		return serverNm;
	}

	public void setServerNm(java.lang.String serverNm) {
		this.serverNm = serverNm;
	}

	public java.lang.String getServerIp() {
		return serverIp;
	}

	public void setServerIp(java.lang.String serverIp) {
		this.serverIp = serverIp;
	}

	public java.lang.String getRefType() {
		return refType;
	}

	public void setRefType(java.lang.String refType) {
		this.refType = refType;
	}

	public java.lang.String getRefNbr() {
		return refNbr;
	}

	public void setRefNbr(java.lang.String refNbr) {
		this.refNbr = refNbr;
	}

	public java.lang.String getClassNm() {
		return classNm;
	}

	public void setClassNm(java.lang.String classNm) {
		this.classNm = classNm;
	}

	public java.lang.String getClassDesc() {
		return classDesc;
	}

	public void setClassDesc(java.lang.String classDesc) {
		this.classDesc = classDesc;
	}

	public java.lang.String getStgType() {
		return stgType;
	}

	public void setStgType(java.lang.String stgType) {
		this.stgType = stgType;
	}

	public java.lang.String getStgZone() {
		return stgZone;
	}

	public void setStgZone(java.lang.String stgZone) {
		this.stgZone = stgZone;
	}

	public java.lang.String getActionCd() {
		return actionCd;
	}

	public void setActionCd(java.lang.String actionCd) {
		this.actionCd = actionCd;
	}

	public java.lang.String getEstType() {
		return estType;
	}

	public void setEstType(java.lang.String estType) {
		this.estType = estType;
	}

	public java.math.BigDecimal getTonnage() {
		return tonnage;
	}

	public void setTonnage(java.math.BigDecimal tonnage) {
		this.tonnage = tonnage;
	}

	public java.lang.String getOccSrcCd() {
		return occSrcCd;
	}

	public void setOccSrcCd(java.lang.String occSrcCd) {
		this.occSrcCd = occSrcCd;
	}

	public java.lang.String getCntrSize() {
		return cntrSize;
	}

	public void setCntrSize(java.lang.String cntrSize) {
		this.cntrSize = cntrSize;
	}

	public java.lang.String getCntrType() {
		return cntrType;
	}

	public void setCntrType(java.lang.String cntrType) {
		this.cntrType = cntrType;
	}

	public java.lang.Integer getNbrPkgs() {
		return nbrPkgs;
	}

	public void setNbrPkgs(java.lang.Integer nbrPkgs) {
		this.nbrPkgs = nbrPkgs;
	}

	public java.util.Date getAtbDttm() {
		return atbDttm;
	}

	public void setAtbDttm(java.util.Date atbDttm) {
		this.atbDttm = atbDttm;
	}

	public java.util.Date getAtuDttm() {
		return atuDttm;
	}

	public void setAtuDttm(java.util.Date atuDttm) {
		this.atuDttm = atuDttm;
	}

	public java.util.Date getTransDttm() {
		return transDttm;
	}

	public void setTransDttm(java.util.Date transDttm) {
		this.transDttm = transDttm;
	}

	public java.lang.String getTransNbr() {
		return transNbr;
	}

	public void setTransNbr(java.lang.String transNbr) {
		this.transNbr = transNbr;
	}

	public boolean isStayingInJP() {
		return isStayingInJP;
	}

	public void setStayingInJP(boolean isStayingInJP) {
		this.isStayingInJP = isStayingInJP;
	}

	public java.lang.Long getCntrSeqNbr() {
		return cntrSeqNbr;
	}

	public void setCntrSeqNbr(java.lang.Long cntrSeqNbr) {
		this.cntrSeqNbr = cntrSeqNbr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

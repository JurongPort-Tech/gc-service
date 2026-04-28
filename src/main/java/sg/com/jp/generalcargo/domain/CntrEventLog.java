package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CntrEventLog {


    private Integer cntrSeqNbr;
    private String cntrNbr;
    private String status;
    private String prevPurpCd;
    private Timestamp txnDttm;
    private String txnCd;
    private String userId;
    private String craneNm;
    private String craneOprId;
    private String wtaId;
    private String pmType;
    private String pmNm;
    private String pmOprId;
    private String isoSizeTypeCd;
    private String sizeFt;
    private String catCd;
    private Integer declrWt;
    private Integer measureWt;
    private String purpCd;
    private String cntrOprCd;
    private String haulCd;
    private String loloPartyInd;
    private String discSlotOprCd;
    private String loadSlotOprCd;
    private String renomSlotOprCd;
    private String discVvCd;
    private String ldVvCd;
    private String renomVvCd;
    private String psrc;
    private String pload;
    private String pdisc;
    private String pdest;
    private String dgInd;
    private String imdgClCd;
    private String refrInd;
    private String ucInd;
    private String overSzInd;
    private String intergatewayInd;
    private Timestamp discDttm;
    private Timestamp loadDttm;
    private Timestamp offloadDttm;
    private Timestamp mountDttm;
    private Timestamp arrDttm;
    private Timestamp exitDttm;
    private Timestamp changePurpDttm;
    private String dirHdlgInd;
    private String chasProvInd;
    private String gearUsed;
    private String pluginTemp;
    private Timestamp pluginDttm;
    private Timestamp unplugDttm;
    private Integer ucHandlingDur;
    private String athwartshipInd;
    private String billVslInd;
    private String billYdInd;
    private String procQcIncentive;
    private String procYcIncentive;
    private String procPmIncentive;
    private String lastModifyUserId;
    private Timestamp lastModifyDttm;
    private Timestamp codArrTime;
    
	public Timestamp getCodArrTime() {
		return codArrTime;
	}
	public void setCodArrTime(Timestamp codArrTime) {
		this.codArrTime = codArrTime;
	}
	public Integer getCntrSeqNbr() {
		return cntrSeqNbr;
	}
	public void setCntrSeqNbr(Integer cntrSeqNbr) {
		this.cntrSeqNbr = cntrSeqNbr;
	}
	public String getCntrNbr() {
		return cntrNbr;
	}
	public void setCntrNbr(String cntrNbr) {
		this.cntrNbr = cntrNbr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrevPurpCd() {
		return prevPurpCd;
	}
	public void setPrevPurpCd(String prevPurpCd) {
		this.prevPurpCd = prevPurpCd;
	}
	public Timestamp getTxnDttm() {
		return txnDttm;
	}
	public void setTxnDttm(Timestamp txnDttm) {
		this.txnDttm = txnDttm;
	}
	public String getTxnCd() {
		return txnCd;
	}
	public void setTxnCd(String txnCd) {
		this.txnCd = txnCd;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCraneNm() {
		return craneNm;
	}
	public void setCraneNm(String craneNm) {
		this.craneNm = craneNm;
	}
	public String getCraneOprId() {
		return craneOprId;
	}
	public void setCraneOprId(String craneOprId) {
		this.craneOprId = craneOprId;
	}
	public String getWtaId() {
		return wtaId;
	}
	public void setWtaId(String wtaId) {
		this.wtaId = wtaId;
	}
	public String getPmType() {
		return pmType;
	}
	public void setPmType(String pmType) {
		this.pmType = pmType;
	}
	public String getPmNm() {
		return pmNm;
	}
	public void setPmNm(String pmNm) {
		this.pmNm = pmNm;
	}
	public String getPmOprId() {
		return pmOprId;
	}
	public void setPmOprId(String pmOprId) {
		this.pmOprId = pmOprId;
	}
	public String getIsoSizeTypeCd() {
		return isoSizeTypeCd;
	}
	public void setIsoSizeTypeCd(String isoSizeTypeCd) {
		this.isoSizeTypeCd = isoSizeTypeCd;
	}
	public String getSizeFt() {
		return sizeFt;
	}
	public void setSizeFt(String sizeFt) {
		this.sizeFt = sizeFt;
	}
	public String getCatCd() {
		return catCd;
	}
	public void setCatCd(String catCd) {
		this.catCd = catCd;
	}
	public Integer getDeclrWt() {
		return declrWt;
	}
	public void setDeclrWt(Integer declrWt) {
		this.declrWt = declrWt;
	}
	public Integer getMeasureWt() {
		return measureWt;
	}
	public void setMeasureWt(Integer measureWt) {
		this.measureWt = measureWt;
	}
	public String getPurpCd() {
		return purpCd;
	}
	public void setPurpCd(String purpCd) {
		this.purpCd = purpCd;
	}
	public String getCntrOprCd() {
		return cntrOprCd;
	}
	public void setCntrOprCd(String cntrOprCd) {
		this.cntrOprCd = cntrOprCd;
	}
	public String getHaulCd() {
		return haulCd;
	}
	public void setHaulCd(String haulCd) {
		this.haulCd = haulCd;
	}
	public String getLoloPartyInd() {
		return loloPartyInd;
	}
	public void setLoloPartyInd(String loloPartyInd) {
		this.loloPartyInd = loloPartyInd;
	}
	public String getDiscSlotOprCd() {
		return discSlotOprCd;
	}
	public void setDiscSlotOprCd(String discSlotOprCd) {
		this.discSlotOprCd = discSlotOprCd;
	}
	public String getLoadSlotOprCd() {
		return loadSlotOprCd;
	}
	public void setLoadSlotOprCd(String loadSlotOprCd) {
		this.loadSlotOprCd = loadSlotOprCd;
	}
	public String getRenomSlotOprCd() {
		return renomSlotOprCd;
	}
	public void setRenomSlotOprCd(String renomSlotOprCd) {
		this.renomSlotOprCd = renomSlotOprCd;
	}
	public String getDiscVvCd() {
		return discVvCd;
	}
	public void setDiscVvCd(String discVvCd) {
		this.discVvCd = discVvCd;
	}
	public String getLdVvCd() {
		return ldVvCd;
	}
	public void setLdVvCd(String ldVvCd) {
		this.ldVvCd = ldVvCd;
	}
	public String getRenomVvCd() {
		return renomVvCd;
	}
	public void setRenomVvCd(String renomVvCd) {
		this.renomVvCd = renomVvCd;
	}
	public String getPsrc() {
		return psrc;
	}
	public void setPsrc(String psrc) {
		this.psrc = psrc;
	}
	public String getPload() {
		return pload;
	}
	public void setPload(String pload) {
		this.pload = pload;
	}
	public String getPdisc() {
		return pdisc;
	}
	public void setPdisc(String pdisc) {
		this.pdisc = pdisc;
	}
	public String getPdest() {
		return pdest;
	}
	public void setPdest(String pdest) {
		this.pdest = pdest;
	}
	public String getDgInd() {
		return dgInd;
	}
	public void setDgInd(String dgInd) {
		this.dgInd = dgInd;
	}
	public String getImdgClCd() {
		return imdgClCd;
	}
	public void setImdgClCd(String imdgClCd) {
		this.imdgClCd = imdgClCd;
	}
	public String getRefrInd() {
		return refrInd;
	}
	public void setRefrInd(String refrInd) {
		this.refrInd = refrInd;
	}
	public String getUcInd() {
		return ucInd;
	}
	public void setUcInd(String ucInd) {
		this.ucInd = ucInd;
	}
	public String getOverSzInd() {
		return overSzInd;
	}
	public void setOverSzInd(String overSzInd) {
		this.overSzInd = overSzInd;
	}
	public String getIntergatewayInd() {
		return intergatewayInd;
	}
	public void setIntergatewayInd(String intergatewayInd) {
		this.intergatewayInd = intergatewayInd;
	}
	public Timestamp getDiscDttm() {
		return discDttm;
	}
	public void setDiscDttm(Timestamp discDttm) {
		this.discDttm = discDttm;
	}
	public Timestamp getLoadDttm() {
		return loadDttm;
	}
	public void setLoadDttm(Timestamp loadDttm) {
		this.loadDttm = loadDttm;
	}
	public Timestamp getOffloadDttm() {
		return offloadDttm;
	}
	public void setOffloadDttm(Timestamp offloadDttm) {
		this.offloadDttm = offloadDttm;
	}
	public Timestamp getMountDttm() {
		return mountDttm;
	}
	public void setMountDttm(Timestamp mountDttm) {
		this.mountDttm = mountDttm;
	}
	public Timestamp getArrDttm() {
		return arrDttm;
	}
	public void setArrDttm(Timestamp arrDttm) {
		this.arrDttm = arrDttm;
	}
	public Timestamp getExitDttm() {
		return exitDttm;
	}
	public void setExitDttm(Timestamp exitDttm) {
		this.exitDttm = exitDttm;
	}
	public Timestamp getChangePurpDttm() {
		return changePurpDttm;
	}
	public void setChangePurpDttm(Timestamp changePurpDttm) {
		this.changePurpDttm = changePurpDttm;
	}
	public String getDirHdlgInd() {
		return dirHdlgInd;
	}
	public void setDirHdlgInd(String dirHdlgInd) {
		this.dirHdlgInd = dirHdlgInd;
	}
	public String getChasProvInd() {
		return chasProvInd;
	}
	public void setChasProvInd(String chasProvInd) {
		this.chasProvInd = chasProvInd;
	}
	public String getGearUsed() {
		return gearUsed;
	}
	public void setGearUsed(String gearUsed) {
		this.gearUsed = gearUsed;
	}
	public String getPluginTemp() {
		return pluginTemp;
	}
	public void setPluginTemp(String pluginTemp) {
		this.pluginTemp = pluginTemp;
	}
	public Timestamp getPluginDttm() {
		return pluginDttm;
	}
	public void setPluginDttm(Timestamp pluginDttm) {
		this.pluginDttm = pluginDttm;
	}
	public Timestamp getUnplugDttm() {
		return unplugDttm;
	}
	public void setUnplugDttm(Timestamp unplugDttm) {
		this.unplugDttm = unplugDttm;
	}
	public Integer getUcHandlingDur() {
		return ucHandlingDur;
	}
	public void setUcHandlingDur(Integer ucHandlingDur) {
		this.ucHandlingDur = ucHandlingDur;
	}
	public String getAthwartshipInd() {
		return athwartshipInd;
	}
	public void setAthwartshipInd(String athwartshipInd) {
		this.athwartshipInd = athwartshipInd;
	}
	public String getBillVslInd() {
		return billVslInd;
	}
	public void setBillVslInd(String billVslInd) {
		this.billVslInd = billVslInd;
	}
	public String getBillYdInd() {
		return billYdInd;
	}
	public void setBillYdInd(String billYdInd) {
		this.billYdInd = billYdInd;
	}
	public String getProcQcIncentive() {
		return procQcIncentive;
	}
	public void setProcQcIncentive(String procQcIncentive) {
		this.procQcIncentive = procQcIncentive;
	}
	public String getProcYcIncentive() {
		return procYcIncentive;
	}
	public void setProcYcIncentive(String procYcIncentive) {
		this.procYcIncentive = procYcIncentive;
	}
	public String getProcPmIncentive() {
		return procPmIncentive;
	}
	public void setProcPmIncentive(String procPmIncentive) {
		this.procPmIncentive = procPmIncentive;
	}
	public String getLastModifyUserId() {
		return lastModifyUserId;
	}
	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}
	public Timestamp getLastModifyDttm() {
		return lastModifyDttm;
	}
	public void setLastModifyDttm(Timestamp lastModifyDttm) {
		this.lastModifyDttm = lastModifyDttm;
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

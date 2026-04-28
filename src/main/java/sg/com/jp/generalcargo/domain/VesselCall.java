package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * xe.VesselCall 11/17/2009 07:36:47
 * 
 */
public class VesselCall implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String vvCd;
	private String vslNm;
	private String inVoyNbr;
	private String outVoyNbr;
	private String vvStatusInd;
	private String shpgSvcCd;
	private Integer shpgRouteNbr;
	private String routeNm;
	private String vslOprCd;
	private Date berthApplDttm;
	private Date vslBerthDttm;
	private Date vslEtdDttm;
	private Date vvCloseDttm;
	private String portFr;
	private String portTo;
	private BigDecimal arrivalDraft;
	private BigDecimal departureDraft;
	private String berthSideInd;
	private String contactNm;
	private String contactHomeTel;
	private String contactOffTel;
	private String contactPgr;
	private String contactFax;
	private Integer cntrDisc;
	private Integer cntrLoad;
	private Integer ucLoad;
	private Integer ucDisc;
	private Date cobDttm;
	private String berthApplRem;
	private String billMarineInd;
	private String billStevInd;
	private String billOtherInd;
	private String billAdminInd;
	private String createUserId;
	private String createAcctNbr;
	private String lastModifyUserId;
	private Date lastModifyDttm;
	private String scheme;
	private String terminal;
	private Integer vslLoa;
	private String locFr;
	private String locTo;
	private BigDecimal alongsideDraft;
	private String vslUnderTowInd;
	private String contact2Nm;
	private String contact2HomeTel;
	private String contact2OffTel;
	private String contact2Pgr;
	private String contact2Fax;
	private Integer totalCargoOnboard;
	private Integer loadDisplacement;
	private String dgCargoInd;
	private Integer hlift;
	private Integer hliftOverwharf;
	private Integer hliftOverside;
	private Integer mobileCrDwt;
	private Integer mobileCrSwl;
	private String cntrVslInd;
	private String gbCloseVslInd;
	private String gbCloseBjInd;
	private String gbCloseShpInd;
	private String cargoMode;
	private String berthAllocRem;
	private String storageSpaceInd;
	private String createCustCd;
	private String billAcctNbr;
	private String gbArrivalWaiverCd;
	private String gbArrivalWaiverReason;
	private String gbDepartureWaiverCd;
	private String gbDepartureWaiverReason;
	private String gbBertBillInd;
	private Integer bridgeDistFromBow;
	private String vacateBerthInd;
	private String mixedSchemeInd;
	private String discCmCd;
	private String loadCmCd;
	private String discBerRem;
	private String loadBerRem;
	private String crgDetProc;
	private String declarantCustCd;
	private String ucDiscCmCd;
	private String ucLoadCmCd;
	private String ucDiscBerRem;
	private String ucLoadBerRem;
	private Integer estThroughputNbr;
	private Integer estLongCrMoveNbr;
	private String sentToPsaInd;
	private Date adviceDttm;
	private String abbrInVoyNbr;
	private String abbrOutVoyNbr;
	private String incentiveClass;
	private String ispsLevel;
	private String billOpenTsInd;
	private String smsAlertRep1Ind;
	private String smsAlertRep2Ind;
	private String reeferParty;
	private Integer allocProdPrd;
	private String gbCloseProdInd;
	private String billProdSurchrgInd;
	private String tandemLiftInd;
	private String protrusionInd;
	private String floatCraneInd;

	private String vvStatusIndText;

	private Date lastAtuDttm;
	
	
	

	public VesselCall() {
	}

	public VesselCall(String vvCd, String vslNm, String inVoyNbr, String outVoyNbr, String vvStatusInd,
			String shpgSvcCd, Integer shpgRouteNbr, String routeNm, String vslOprCd, Date berthApplDttm,
			Date vslBerthDttm, Date vslEtdDttm, Date vvCloseDttm, String portFr, String portTo, BigDecimal arrivalDraft,
			BigDecimal departureDraft, String berthSideInd, String contactNm, String contactHomeTel,
			String contactOffTel, String contactPgr, String contactFax, Integer cntrDisc, Integer cntrLoad,
			Integer ucLoad, Integer ucDisc, Date cobDttm, String berthApplRem, String billMarineInd, String billStevInd,
			String billOtherInd, String billAdminInd, String createUserId, String createAcctNbr,
			String lastModifyUserId, Date lastModifyDttm, String scheme, String terminal, Integer vslLoa, String locFr,
			String locTo, BigDecimal alongsideDraft, String vslUnderTowInd, String contact2Nm, String contact2HomeTel,
			String contact2OffTel, String contact2Pgr, String contact2Fax, Integer totalCargoOnboard,
			Integer loadDisplacement, String dgCargoInd, Integer hlift, Integer hliftOverwharf, Integer hliftOverside,
			Integer mobileCrDwt, Integer mobileCrSwl, String cntrVslInd, String gbCloseVslInd, String gbCloseBjInd,
			String gbCloseShpInd, String cargoMode, String berthAllocRem, String storageSpaceInd, String createCustCd,
			String billAcctNbr, String gbArrivalWaiverCd, String gbArrivalWaiverReason, String gbDepartureWaiverCd,
			String gbDepartureWaiverReason, String gbBertBillInd, Integer bridgeDistFromBow, String vacateBerthInd,
			String mixedSchemeInd, String discCmCd, String loadCmCd, String discBerRem, String loadBerRem,
			String crgDetProc, String declarantCustCd, String ucDiscCmCd, String ucLoadCmCd, String ucDiscBerRem,
			String ucLoadBerRem, Integer estThroughputNbr, Integer estLongCrMoveNbr, String sentToPsaInd,
			Date adviceDttm, String abbrInVoyNbr, String abbrOutVoyNbr, String incentiveClass, String ispsLevel,
			String billOpenTsInd, String smsAlertRep1Ind, String smsAlertRep2Ind, String reeferParty,
			Integer allocProdPrd, String gbCloseProdInd, String billProdSurchrgInd, String tandemLiftInd,
			String protrusionInd, String floatCraneInd) {
		this.vvCd = vvCd;
		this.vslNm = vslNm;
		this.inVoyNbr = inVoyNbr;
		this.outVoyNbr = outVoyNbr;
		this.vvStatusInd = vvStatusInd;
		this.shpgSvcCd = shpgSvcCd;
		this.shpgRouteNbr = shpgRouteNbr;
		this.routeNm = routeNm;
		this.vslOprCd = vslOprCd;
		this.berthApplDttm = berthApplDttm;
		this.vslBerthDttm = vslBerthDttm;
		this.vslEtdDttm = vslEtdDttm;
		this.vvCloseDttm = vvCloseDttm;
		this.portFr = portFr;
		this.portTo = portTo;
		this.arrivalDraft = arrivalDraft;
		this.departureDraft = departureDraft;
		this.berthSideInd = berthSideInd;
		this.contactNm = contactNm;
		this.contactHomeTel = contactHomeTel;
		this.contactOffTel = contactOffTel;
		this.contactPgr = contactPgr;
		this.contactFax = contactFax;
		this.cntrDisc = cntrDisc;
		this.cntrLoad = cntrLoad;
		this.ucLoad = ucLoad;
		this.ucDisc = ucDisc;
		this.cobDttm = cobDttm;
		this.berthApplRem = berthApplRem;
		this.billMarineInd = billMarineInd;
		this.billStevInd = billStevInd;
		this.billOtherInd = billOtherInd;
		this.billAdminInd = billAdminInd;
		this.createUserId = createUserId;
		this.createAcctNbr = createAcctNbr;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.scheme = scheme;
		this.terminal = terminal;
		this.vslLoa = vslLoa;
		this.locFr = locFr;
		this.locTo = locTo;
		this.alongsideDraft = alongsideDraft;
		this.vslUnderTowInd = vslUnderTowInd;
		this.contact2Nm = contact2Nm;
		this.contact2HomeTel = contact2HomeTel;
		this.contact2OffTel = contact2OffTel;
		this.contact2Pgr = contact2Pgr;
		this.contact2Fax = contact2Fax;
		this.totalCargoOnboard = totalCargoOnboard;
		this.loadDisplacement = loadDisplacement;
		this.dgCargoInd = dgCargoInd;
		this.hlift = hlift;
		this.hliftOverwharf = hliftOverwharf;
		this.hliftOverside = hliftOverside;
		this.mobileCrDwt = mobileCrDwt;
		this.mobileCrSwl = mobileCrSwl;
		this.cntrVslInd = cntrVslInd;
		this.gbCloseVslInd = gbCloseVslInd;
		this.gbCloseBjInd = gbCloseBjInd;
		this.gbCloseShpInd = gbCloseShpInd;
		this.cargoMode = cargoMode;
		this.berthAllocRem = berthAllocRem;
		this.storageSpaceInd = storageSpaceInd;
		this.createCustCd = createCustCd;
		this.billAcctNbr = billAcctNbr;
		this.gbArrivalWaiverCd = gbArrivalWaiverCd;
		this.gbArrivalWaiverReason = gbArrivalWaiverReason;
		this.gbDepartureWaiverCd = gbDepartureWaiverCd;
		this.gbDepartureWaiverReason = gbDepartureWaiverReason;
		this.gbBertBillInd = gbBertBillInd;
		this.bridgeDistFromBow = bridgeDistFromBow;
		this.vacateBerthInd = vacateBerthInd;
		this.mixedSchemeInd = mixedSchemeInd;
		this.discCmCd = discCmCd;
		this.loadCmCd = loadCmCd;
		this.discBerRem = discBerRem;
		this.loadBerRem = loadBerRem;
		this.crgDetProc = crgDetProc;
		this.declarantCustCd = declarantCustCd;
		this.ucDiscCmCd = ucDiscCmCd;
		this.ucLoadCmCd = ucLoadCmCd;
		this.ucDiscBerRem = ucDiscBerRem;
		this.ucLoadBerRem = ucLoadBerRem;
		this.estThroughputNbr = estThroughputNbr;
		this.estLongCrMoveNbr = estLongCrMoveNbr;
		this.sentToPsaInd = sentToPsaInd;
		this.adviceDttm = adviceDttm;
		this.abbrInVoyNbr = abbrInVoyNbr;
		this.abbrOutVoyNbr = abbrOutVoyNbr;
		this.incentiveClass = incentiveClass;
		this.ispsLevel = ispsLevel;
		this.billOpenTsInd = billOpenTsInd;
		this.smsAlertRep1Ind = smsAlertRep1Ind;
		this.smsAlertRep2Ind = smsAlertRep2Ind;
		this.reeferParty = reeferParty;
		this.allocProdPrd = allocProdPrd;
		this.gbCloseProdInd = gbCloseProdInd;
		this.billProdSurchrgInd = billProdSurchrgInd;
		this.tandemLiftInd = tandemLiftInd;
		this.protrusionInd = protrusionInd;
		this.floatCraneInd = floatCraneInd;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}

	public String getVslNm() {
		return vslNm;
	}

	public void setVslNm(String vslNm) {
		this.vslNm = vslNm;
	}

	public String getInVoyNbr() {
		return inVoyNbr;
	}

	public void setInVoyNbr(String inVoyNbr) {
		this.inVoyNbr = inVoyNbr;
	}

	public String getOutVoyNbr() {
		return outVoyNbr;
	}

	public void setOutVoyNbr(String outVoyNbr) {
		this.outVoyNbr = outVoyNbr;
	}

	public String getVvStatusInd() {
		return vvStatusInd;
	}

	public void setVvStatusInd(String vvStatusInd) {
		this.vvStatusInd = vvStatusInd;
	}

	public String getShpgSvcCd() {
		return shpgSvcCd;
	}

	public void setShpgSvcCd(String shpgSvcCd) {
		this.shpgSvcCd = shpgSvcCd;
	}

	public Integer getShpgRouteNbr() {
		return shpgRouteNbr;
	}

	public void setShpgRouteNbr(Integer shpgRouteNbr) {
		this.shpgRouteNbr = shpgRouteNbr;
	}

	public String getRouteNm() {
		return routeNm;
	}

	public void setRouteNm(String routeNm) {
		this.routeNm = routeNm;
	}

	public String getVslOprCd() {
		return vslOprCd;
	}

	public void setVslOprCd(String vslOprCd) {
		this.vslOprCd = vslOprCd;
	}

	public Date getBerthApplDttm() {
		return berthApplDttm;
	}

	public void setBerthApplDttm(Date berthApplDttm) {
		this.berthApplDttm = berthApplDttm;
	}

	public Date getVslBerthDttm() {
		return vslBerthDttm;
	}

	public void setVslBerthDttm(Date vslBerthDttm) {
		this.vslBerthDttm = vslBerthDttm;
	}

	public Date getVslEtdDttm() {
		return vslEtdDttm;
	}

	public void setVslEtdDttm(Date vslEtdDttm) {
		this.vslEtdDttm = vslEtdDttm;
	}

	public Date getVvCloseDttm() {
		return vvCloseDttm;
	}

	public void setVvCloseDttm(Date vvCloseDttm) {
		this.vvCloseDttm = vvCloseDttm;
	}

	public String getPortFr() {
		return portFr;
	}

	public void setPortFr(String portFr) {
		this.portFr = portFr;
	}

	public String getPortTo() {
		return portTo;
	}

	public void setPortTo(String portTo) {
		this.portTo = portTo;
	}

	public BigDecimal getArrivalDraft() {
		return arrivalDraft;
	}

	public void setArrivalDraft(BigDecimal arrivalDraft) {
		this.arrivalDraft = arrivalDraft;
	}

	public BigDecimal getDepartureDraft() {
		return departureDraft;
	}

	public void setDepartureDraft(BigDecimal departureDraft) {
		this.departureDraft = departureDraft;
	}

	public String getBerthSideInd() {
		return berthSideInd;
	}

	public void setBerthSideInd(String berthSideInd) {
		this.berthSideInd = berthSideInd;
	}

	public String getContactNm() {
		return contactNm;
	}

	public void setContactNm(String contactNm) {
		this.contactNm = contactNm;
	}

	public String getContactHomeTel() {
		return contactHomeTel;
	}

	public void setContactHomeTel(String contactHomeTel) {
		this.contactHomeTel = contactHomeTel;
	}

	public String getContactOffTel() {
		return contactOffTel;
	}

	public void setContactOffTel(String contactOffTel) {
		this.contactOffTel = contactOffTel;
	}

	public String getContactPgr() {
		return contactPgr;
	}

	public void setContactPgr(String contactPgr) {
		this.contactPgr = contactPgr;
	}

	public String getContactFax() {
		return contactFax;
	}

	public void setContactFax(String contactFax) {
		this.contactFax = contactFax;
	}

	public Integer getCntrDisc() {
		return cntrDisc;
	}

	public void setCntrDisc(Integer cntrDisc) {
		this.cntrDisc = cntrDisc;
	}

	public Integer getCntrLoad() {
		return cntrLoad;
	}

	public void setCntrLoad(Integer cntrLoad) {
		this.cntrLoad = cntrLoad;
	}

	public Integer getUcLoad() {
		return ucLoad;
	}

	public void setUcLoad(Integer ucLoad) {
		this.ucLoad = ucLoad;
	}

	public Integer getUcDisc() {
		return ucDisc;
	}

	public void setUcDisc(Integer ucDisc) {
		this.ucDisc = ucDisc;
	}

	public Date getCobDttm() {
		return cobDttm;
	}

	public void setCobDttm(Date cobDttm) {
		this.cobDttm = cobDttm;
	}

	public String getBerthApplRem() {
		return berthApplRem;
	}

	public void setBerthApplRem(String berthApplRem) {
		this.berthApplRem = berthApplRem;
	}

	public String getBillMarineInd() {
		return billMarineInd;
	}

	public void setBillMarineInd(String billMarineInd) {
		this.billMarineInd = billMarineInd;
	}

	public String getBillStevInd() {
		return billStevInd;
	}

	public void setBillStevInd(String billStevInd) {
		this.billStevInd = billStevInd;
	}

	public String getBillOtherInd() {
		return billOtherInd;
	}

	public void setBillOtherInd(String billOtherInd) {
		this.billOtherInd = billOtherInd;
	}

	public String getBillAdminInd() {
		return billAdminInd;
	}

	public void setBillAdminInd(String billAdminInd) {
		this.billAdminInd = billAdminInd;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateAcctNbr() {
		return createAcctNbr;
	}

	public void setCreateAcctNbr(String createAcctNbr) {
		this.createAcctNbr = createAcctNbr;
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

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public Integer getVslLoa() {
		return vslLoa;
	}

	public void setVslLoa(Integer vslLoa) {
		this.vslLoa = vslLoa;
	}

	public String getLocFr() {
		return locFr;
	}

	public void setLocFr(String locFr) {
		this.locFr = locFr;
	}

	public String getLocTo() {
		return locTo;
	}

	public void setLocTo(String locTo) {
		this.locTo = locTo;
	}

	public BigDecimal getAlongsideDraft() {
		return alongsideDraft;
	}

	public void setAlongsideDraft(BigDecimal alongsideDraft) {
		this.alongsideDraft = alongsideDraft;
	}

	public String getVslUnderTowInd() {
		return vslUnderTowInd;
	}

	public void setVslUnderTowInd(String vslUnderTowInd) {
		this.vslUnderTowInd = vslUnderTowInd;
	}

	public String getContact2Nm() {
		return contact2Nm;
	}

	public void setContact2Nm(String contact2Nm) {
		this.contact2Nm = contact2Nm;
	}

	public String getContact2HomeTel() {
		return contact2HomeTel;
	}

	public void setContact2HomeTel(String contact2HomeTel) {
		this.contact2HomeTel = contact2HomeTel;
	}

	public String getContact2OffTel() {
		return contact2OffTel;
	}

	public void setContact2OffTel(String contact2OffTel) {
		this.contact2OffTel = contact2OffTel;
	}

	public String getContact2Pgr() {
		return contact2Pgr;
	}

	public void setContact2Pgr(String contact2Pgr) {
		this.contact2Pgr = contact2Pgr;
	}

	public String getContact2Fax() {
		return contact2Fax;
	}

	public void setContact2Fax(String contact2Fax) {
		this.contact2Fax = contact2Fax;
	}

	public Integer getTotalCargoOnboard() {
		return totalCargoOnboard;
	}

	public void setTotalCargoOnboard(Integer totalCargoOnboard) {
		this.totalCargoOnboard = totalCargoOnboard;
	}

	public Integer getLoadDisplacement() {
		return loadDisplacement;
	}

	public void setLoadDisplacement(Integer loadDisplacement) {
		this.loadDisplacement = loadDisplacement;
	}

	public String getDgCargoInd() {
		return dgCargoInd;
	}

	public void setDgCargoInd(String dgCargoInd) {
		this.dgCargoInd = dgCargoInd;
	}

	public Integer getHlift() {
		return hlift;
	}

	public void setHlift(Integer hlift) {
		this.hlift = hlift;
	}

	public Integer getHliftOverwharf() {
		return hliftOverwharf;
	}

	public void setHliftOverwharf(Integer hliftOverwharf) {
		this.hliftOverwharf = hliftOverwharf;
	}

	public Integer getHliftOverside() {
		return hliftOverside;
	}

	public void setHliftOverside(Integer hliftOverside) {
		this.hliftOverside = hliftOverside;
	}

	public Integer getMobileCrDwt() {
		return mobileCrDwt;
	}

	public void setMobileCrDwt(Integer mobileCrDwt) {
		this.mobileCrDwt = mobileCrDwt;
	}

	public Integer getMobileCrSwl() {
		return mobileCrSwl;
	}

	public void setMobileCrSwl(Integer mobileCrSwl) {
		this.mobileCrSwl = mobileCrSwl;
	}

	public String getCntrVslInd() {
		return cntrVslInd;
	}

	public void setCntrVslInd(String cntrVslInd) {
		this.cntrVslInd = cntrVslInd;
	}

	public String getGbCloseVslInd() {
		return gbCloseVslInd;
	}

	public void setGbCloseVslInd(String gbCloseVslInd) {
		this.gbCloseVslInd = gbCloseVslInd;
	}

	public String getGbCloseBjInd() {
		return gbCloseBjInd;
	}

	public void setGbCloseBjInd(String gbCloseBjInd) {
		this.gbCloseBjInd = gbCloseBjInd;
	}

	public String getGbCloseShpInd() {
		return gbCloseShpInd;
	}

	public void setGbCloseShpInd(String gbCloseShpInd) {
		this.gbCloseShpInd = gbCloseShpInd;
	}

	public String getCargoMode() {
		return cargoMode;
	}

	public void setCargoMode(String cargoMode) {
		this.cargoMode = cargoMode;
	}

	public String getBerthAllocRem() {
		return berthAllocRem;
	}

	public void setBerthAllocRem(String berthAllocRem) {
		this.berthAllocRem = berthAllocRem;
	}

	public String getStorageSpaceInd() {
		return storageSpaceInd;
	}

	public void setStorageSpaceInd(String storageSpaceInd) {
		this.storageSpaceInd = storageSpaceInd;
	}

	public String getCreateCustCd() {
		return createCustCd;
	}

	public void setCreateCustCd(String createCustCd) {
		this.createCustCd = createCustCd;
	}

	public String getBillAcctNbr() {
		return billAcctNbr;
	}

	public void setBillAcctNbr(String billAcctNbr) {
		this.billAcctNbr = billAcctNbr;
	}

	public String getGbArrivalWaiverCd() {
		return gbArrivalWaiverCd;
	}

	public void setGbArrivalWaiverCd(String gbArrivalWaiverCd) {
		this.gbArrivalWaiverCd = gbArrivalWaiverCd;
	}

	public String getGbArrivalWaiverReason() {
		return gbArrivalWaiverReason;
	}

	public void setGbArrivalWaiverReason(String gbArrivalWaiverReason) {
		this.gbArrivalWaiverReason = gbArrivalWaiverReason;
	}

	public String getGbDepartureWaiverCd() {
		return gbDepartureWaiverCd;
	}

	public void setGbDepartureWaiverCd(String gbDepartureWaiverCd) {
		this.gbDepartureWaiverCd = gbDepartureWaiverCd;
	}

	public String getGbDepartureWaiverReason() {
		return gbDepartureWaiverReason;
	}

	public void setGbDepartureWaiverReason(String gbDepartureWaiverReason) {
		this.gbDepartureWaiverReason = gbDepartureWaiverReason;
	}

	public String getGbBertBillInd() {
		return gbBertBillInd;
	}

	public void setGbBertBillInd(String gbBertBillInd) {
		this.gbBertBillInd = gbBertBillInd;
	}

	public Integer getBridgeDistFromBow() {
		return bridgeDistFromBow;
	}

	public void setBridgeDistFromBow(Integer bridgeDistFromBow) {
		this.bridgeDistFromBow = bridgeDistFromBow;
	}

	public String getVacateBerthInd() {
		return vacateBerthInd;
	}

	public void setVacateBerthInd(String vacateBerthInd) {
		this.vacateBerthInd = vacateBerthInd;
	}

	public String getMixedSchemeInd() {
		return mixedSchemeInd;
	}

	public void setMixedSchemeInd(String mixedSchemeInd) {
		this.mixedSchemeInd = mixedSchemeInd;
	}

	public String getDiscCmCd() {
		return discCmCd;
	}

	public void setDiscCmCd(String discCmCd) {
		this.discCmCd = discCmCd;
	}

	public String getLoadCmCd() {
		return loadCmCd;
	}

	public void setLoadCmCd(String loadCmCd) {
		this.loadCmCd = loadCmCd;
	}

	public String getDiscBerRem() {
		return discBerRem;
	}

	public void setDiscBerRem(String discBerRem) {
		this.discBerRem = discBerRem;
	}

	public String getLoadBerRem() {
		return loadBerRem;
	}

	public void setLoadBerRem(String loadBerRem) {
		this.loadBerRem = loadBerRem;
	}

	public String getCrgDetProc() {
		return crgDetProc;
	}

	public void setCrgDetProc(String crgDetProc) {
		this.crgDetProc = crgDetProc;
	}

	public String getDeclarantCustCd() {
		return declarantCustCd;
	}

	public void setDeclarantCustCd(String declarantCustCd) {
		this.declarantCustCd = declarantCustCd;
	}

	public String getUcDiscCmCd() {
		return ucDiscCmCd;
	}

	public void setUcDiscCmCd(String ucDiscCmCd) {
		this.ucDiscCmCd = ucDiscCmCd;
	}

	public String getUcLoadCmCd() {
		return ucLoadCmCd;
	}

	public void setUcLoadCmCd(String ucLoadCmCd) {
		this.ucLoadCmCd = ucLoadCmCd;
	}

	public String getUcDiscBerRem() {
		return ucDiscBerRem;
	}

	public void setUcDiscBerRem(String ucDiscBerRem) {
		this.ucDiscBerRem = ucDiscBerRem;
	}

	public String getUcLoadBerRem() {
		return ucLoadBerRem;
	}

	public void setUcLoadBerRem(String ucLoadBerRem) {
		this.ucLoadBerRem = ucLoadBerRem;
	}

	public Integer getEstThroughputNbr() {
		return estThroughputNbr;
	}

	public void setEstThroughputNbr(Integer estThroughputNbr) {
		this.estThroughputNbr = estThroughputNbr;
	}

	public Integer getEstLongCrMoveNbr() {
		return estLongCrMoveNbr;
	}

	public void setEstLongCrMoveNbr(Integer estLongCrMoveNbr) {
		this.estLongCrMoveNbr = estLongCrMoveNbr;
	}

	public String getSentToPsaInd() {
		return sentToPsaInd;
	}

	public void setSentToPsaInd(String sentToPsaInd) {
		this.sentToPsaInd = sentToPsaInd;
	}

	public Date getAdviceDttm() {
		return adviceDttm;
	}

	public void setAdviceDttm(Date adviceDttm) {
		this.adviceDttm = adviceDttm;
	}

	public String getAbbrInVoyNbr() {
		return abbrInVoyNbr;
	}

	public void setAbbrInVoyNbr(String abbrInVoyNbr) {
		this.abbrInVoyNbr = abbrInVoyNbr;
	}

	public String getAbbrOutVoyNbr() {
		return abbrOutVoyNbr;
	}

	public void setAbbrOutVoyNbr(String abbrOutVoyNbr) {
		this.abbrOutVoyNbr = abbrOutVoyNbr;
	}

	public String getIncentiveClass() {
		return incentiveClass;
	}

	public void setIncentiveClass(String incentiveClass) {
		this.incentiveClass = incentiveClass;
	}

	public String getIspsLevel() {
		return ispsLevel;
	}

	public void setIspsLevel(String ispsLevel) {
		this.ispsLevel = ispsLevel;
	}

	public String getBillOpenTsInd() {
		return billOpenTsInd;
	}

	public void setBillOpenTsInd(String billOpenTsInd) {
		this.billOpenTsInd = billOpenTsInd;
	}

	public String getSmsAlertRep1Ind() {
		return smsAlertRep1Ind;
	}

	public void setSmsAlertRep1Ind(String smsAlertRep1Ind) {
		this.smsAlertRep1Ind = smsAlertRep1Ind;
	}

	public String getSmsAlertRep2Ind() {
		return smsAlertRep2Ind;
	}

	public void setSmsAlertRep2Ind(String smsAlertRep2Ind) {
		this.smsAlertRep2Ind = smsAlertRep2Ind;
	}

	public String getReeferParty() {
		return reeferParty;
	}

	public void setReeferParty(String reeferParty) {
		this.reeferParty = reeferParty;
	}

	public Integer getAllocProdPrd() {
		return allocProdPrd;
	}

	public void setAllocProdPrd(Integer allocProdPrd) {
		this.allocProdPrd = allocProdPrd;
	}

	public String getGbCloseProdInd() {
		return gbCloseProdInd;
	}

	public void setGbCloseProdInd(String gbCloseProdInd) {
		this.gbCloseProdInd = gbCloseProdInd;
	}

	public String getBillProdSurchrgInd() {
		return billProdSurchrgInd;
	}

	public void setBillProdSurchrgInd(String billProdSurchrgInd) {
		this.billProdSurchrgInd = billProdSurchrgInd;
	}

	public String getTandemLiftInd() {
		return tandemLiftInd;
	}

	public void setTandemLiftInd(String tandemLiftInd) {
		this.tandemLiftInd = tandemLiftInd;
	}

	public String getProtrusionInd() {
		return protrusionInd;
	}

	public void setProtrusionInd(String protrusionInd) {
		this.protrusionInd = protrusionInd;
	}

	public String getFloatCraneInd() {
		return floatCraneInd;
	}

	public void setFloatCraneInd(String floatCraneInd) {
		this.floatCraneInd = floatCraneInd;
	}

	public String getVvStatusIndText() {
		return vvStatusIndText;
	}

	public void setVvStatusIndText(String vvStatusIndText) {
		this.vvStatusIndText = vvStatusIndText;
	}

	public Date getLastAtuDttm() {
		return lastAtuDttm;
	}

	public void setLastAtuDttm(Date lastAtuDttm) {
		this.lastAtuDttm = lastAtuDttm;
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

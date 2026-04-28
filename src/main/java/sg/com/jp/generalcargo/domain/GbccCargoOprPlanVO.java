package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOprPlanVO extends AuditLogRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GbccCargoOprPlanId id;
	private String vvCd;
	private Date createDttm;
	private String stevCoCd;
	private String checkerNm;
	private String checkerHpNbr;
	private Integer totDiscTon;
	private Integer totLoadTon;
	private Integer craneOnboardNbr;
	private Integer craneOnboardTon;
	private Integer mobileCraneNbr;
	private Integer mobileCraneTon;
	private Integer floatingCraneNbr;
	private Integer floatingCraneTon;
	private Integer heavyLiftNbr;
	private Integer heavyLiftTon;
	private String tandemLiftInd;
	private Integer gangsNbr;
	private Integer maxWrkHatchNbr;
	private Integer ctrlHatchNbr;
	private String lastModifyUserId;
	private Date lastModifyDttm;
	private String remarks;

	// private CompanyCode agentCompanyVO;
	private GbccViewVvStevedore viewVesselStevedoreVO;
	private List<GbccCargoOprPlanDet> cargoOprPlanDetVO;

	private String agentName;

	private String stevedoreCompanyName;

	// VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : start
	private String craneOnboardTonStr;
	private String craneOnboardTonRemark;
	private String mobileCraneTonStr;
	private String mobileCraneTonRemark;
	private String floatingCraneTonStr;
	private String floatingCraneTonRemark;
	private String heavyLiftTonStr;
	private String heavyLiftTonRemark;

	// vc
	private String vv_Cd;
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
	private String vsl_lastModifyUserId;
	private Date vsl_lastModifyDttm;
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
	private String vsl_tandemLiftInd;
	private String protrusionInd;
	private String floatCraneInd;

	private String vvStatusIndText;

	private Date lastAtuDttm;

	// br
	private BerthingId berth_id;
	private String berth_VvCd;
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
	private String berth_lastModifyUserId;
	private Date berth_lastModifyDttm;
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
	private String berth_remarks;
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

	// vs
	private GbccViewVvStevedoreId gbccView_id;
	private String stev_VvCd;
	private String stevContact;
	private String stevRemarks;
	private String stevRep;
	private String gbccView_lastModifyUserId;

	private String stevCompanyName;

	private Berthing firstBerthingVO;
	private Berthing lastBerthingVO;

	// VietNguyen (FPT) add new field for StevedoreNet 09-May-2012 : end

	public GbccCargoOprPlanVO() {
	}

	public GbccCargoOprPlanVO(String stevCoCd, String checkerNm, String checkerHpNbr, Integer totDiscTon,
			Integer totLoadTon, Integer craneOnboardNbr, Integer craneOnboardTon, Integer mobileCraneNbr,
			Integer mobileCraneTon, Integer floatingCraneNbr, Integer floatingCraneTon, Integer heavyLiftNbr,
			Integer heavyLiftTon, String tandemLiftInd, Integer gangsNbr, Integer maxWrkHatchNbr, Integer ctrlHatchNbr,
			String lastModifyUserId, Date lastModifyDttm, String remarks) {
		this.stevCoCd = stevCoCd;
		this.checkerNm = checkerNm;
		this.checkerHpNbr = checkerHpNbr;
		this.totDiscTon = totDiscTon;
		this.totLoadTon = totLoadTon;
		this.craneOnboardNbr = craneOnboardNbr;
		this.craneOnboardTon = craneOnboardTon;
		this.mobileCraneNbr = mobileCraneNbr;
		this.mobileCraneTon = mobileCraneTon;
		this.floatingCraneNbr = floatingCraneNbr;
		this.floatingCraneTon = floatingCraneTon;
		this.heavyLiftNbr = heavyLiftNbr;
		this.heavyLiftTon = heavyLiftTon;
		this.tandemLiftInd = tandemLiftInd;
		this.gangsNbr = gangsNbr;
		this.maxWrkHatchNbr = maxWrkHatchNbr;
		this.ctrlHatchNbr = ctrlHatchNbr;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.remarks = remarks;
	}

	public GbccCargoOprPlanVO(GbccCargoOprPlanId id, String stevCoCd, String checkerNm, String checkerHpNbr,
			Integer totDiscTon, Integer totLoadTon, Integer craneOnboardNbr, Integer craneOnboardTon,
			Integer mobileCraneNbr, Integer mobileCraneTon, Integer floatingCraneNbr, Integer floatingCraneTon,
			Integer heavyLiftNbr, Integer heavyLiftTon, String tandemLiftInd, Integer gangsNbr, Integer maxWrkHatchNbr,
			Integer ctrlHatchNbr, String lastModifyUserId, Date lastModifyDttm, String remarks) {
		this.id = id;
		this.stevCoCd = stevCoCd;
		this.checkerNm = checkerNm;
		this.checkerHpNbr = checkerHpNbr;
		this.totDiscTon = totDiscTon;
		this.totLoadTon = totLoadTon;
		this.craneOnboardNbr = craneOnboardNbr;
		this.craneOnboardTon = craneOnboardTon;
		this.mobileCraneNbr = mobileCraneNbr;
		this.mobileCraneTon = mobileCraneTon;
		this.floatingCraneNbr = floatingCraneNbr;
		this.floatingCraneTon = floatingCraneTon;
		this.heavyLiftNbr = heavyLiftNbr;
		this.heavyLiftTon = heavyLiftTon;
		this.tandemLiftInd = tandemLiftInd;
		this.gangsNbr = gangsNbr;
		this.maxWrkHatchNbr = maxWrkHatchNbr;
		this.ctrlHatchNbr = ctrlHatchNbr;
		this.lastModifyUserId = lastModifyUserId;
		this.lastModifyDttm = lastModifyDttm;
		this.remarks = remarks;
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

	public Berthing getFirstBerthingVO() {
		return firstBerthingVO;
	}

	public void setFirstBerthingVO(Berthing firstBerthingVO) {
		this.firstBerthingVO = firstBerthingVO;
	}

	public Berthing getLastBerthingVO() {
		return lastBerthingVO;
	}

	public void setLastBerthingVO(Berthing lastBerthingVO) {
		this.lastBerthingVO = lastBerthingVO;
	}

	public String getVv_Cd() {
		return vv_Cd;
	}

	public void setVv_Cd(String vv_Cd) {
		this.vv_Cd = vv_Cd;
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

	public String getVsl_tandemLiftInd() {
		return vsl_tandemLiftInd;
	}

	public void setVsl_tandemLiftInd(String vsl_tandemLiftInd) {
		this.vsl_tandemLiftInd = vsl_tandemLiftInd;
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

	public BerthingId getBerth_id() {
		return berth_id;
	}

	public void setBerth_id(BerthingId berth_id) {
		this.berth_id = berth_id;
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

	public String getBerth_lastModifyUserId() {
		return berth_lastModifyUserId;
	}

	public void setBerth_lastModifyUserId(String berth_lastModifyUserId) {
		this.berth_lastModifyUserId = berth_lastModifyUserId;
	}

	public Date getBerth_lastModifyDttm() {
		return berth_lastModifyDttm;
	}

	public void setBerth_lastModifyDttm(Date berth_lastModifyDttm) {
		this.berth_lastModifyDttm = berth_lastModifyDttm;
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

	public String getBerth_remarks() {
		return berth_remarks;
	}

	public void setBerth_remarks(String berth_remarks) {
		this.berth_remarks = berth_remarks;
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

	public String getMvtName() {
		return mvtName;
	}

	public void setMvtName(String mvtName) {
		this.mvtName = mvtName;
	}

	public GbccViewVvStevedoreId getGbccView_id() {
		return gbccView_id;
	}

	public void setGbccView_id(GbccViewVvStevedoreId gbccView_id) {
		this.gbccView_id = gbccView_id;
	}

	public String getStevContact() {
		return stevContact;
	}

	public void setStevContact(String stevContact) {
		this.stevContact = stevContact;
	}

	public String getStevRemarks() {
		return stevRemarks;
	}

	public void setStevRemarks(String stevRemarks) {
		this.stevRemarks = stevRemarks;
	}

	public String getStevRep() {
		return stevRep;
	}

	public void setStevRep(String stevRep) {
		this.stevRep = stevRep;
	}

	public String getGbccView_lastModifyUserId() {
		return gbccView_lastModifyUserId;
	}

	public void setGbccView_lastModifyUserId(String gbccView_lastModifyUserId) {
		this.gbccView_lastModifyUserId = gbccView_lastModifyUserId;
	}

	public String getStevCompanyName() {
		return stevCompanyName;
	}

	public void setStevCompanyName(String stevCompanyName) {
		this.stevCompanyName = stevCompanyName;
	}

	public void setViewVesselStevedoreVO(GbccViewVvStevedore viewVesselStevedoreVO) {
		this.viewVesselStevedoreVO = viewVesselStevedoreVO;
	}

	public GbccCargoOprPlanId getId() {
		return id;
	}

	public void setId(GbccCargoOprPlanId id) {
		this.id = id;
	}

	public String getStevCoCd() {
		return stevCoCd;
	}

	public void setStevCoCd(String stevCoCd) {
		this.stevCoCd = stevCoCd;
	}

	public String getCheckerNm() {
		return checkerNm;
	}

	public void setCheckerNm(String checkerNm) {
		this.checkerNm = checkerNm;
	}

	public String getCheckerHpNbr() {
		return checkerHpNbr;
	}

	public void setCheckerHpNbr(String checkerHpNbr) {
		this.checkerHpNbr = checkerHpNbr;
	}

	public Integer getTotDiscTon() {
		return totDiscTon;
	}

	public void setTotDiscTon(Integer totDiscTon) {
		this.totDiscTon = totDiscTon;
	}

	public Integer getTotLoadTon() {
		return totLoadTon;
	}

	public void setTotLoadTon(Integer totLoadTon) {
		this.totLoadTon = totLoadTon;
	}

	public Integer getCraneOnboardNbr() {
		return craneOnboardNbr;
	}

	public void setCraneOnboardNbr(Integer craneOnboardNbr) {
		this.craneOnboardNbr = craneOnboardNbr;
	}

	public Integer getCraneOnboardTon() {
		return craneOnboardTon;
	}

	public void setCraneOnboardTon(Integer craneOnboardTon) {
		this.craneOnboardTon = craneOnboardTon;
	}

	public Integer getMobileCraneNbr() {
		return mobileCraneNbr;
	}

	public void setMobileCraneNbr(Integer mobileCraneNbr) {
		this.mobileCraneNbr = mobileCraneNbr;
	}

	public Integer getMobileCraneTon() {
		return mobileCraneTon;
	}

	public void setMobileCraneTon(Integer mobileCraneTon) {
		this.mobileCraneTon = mobileCraneTon;
	}

	public Integer getFloatingCraneNbr() {
		return floatingCraneNbr;
	}

	public void setFloatingCraneNbr(Integer floatingCraneNbr) {
		this.floatingCraneNbr = floatingCraneNbr;
	}

	public Integer getFloatingCraneTon() {
		return floatingCraneTon;
	}

	public void setFloatingCraneTon(Integer floatingCraneTon) {
		this.floatingCraneTon = floatingCraneTon;
	}

	public Integer getHeavyLiftNbr() {
		return heavyLiftNbr;
	}

	public void setHeavyLiftNbr(Integer heavyLiftNbr) {
		this.heavyLiftNbr = heavyLiftNbr;
	}

	public Integer getHeavyLiftTon() {
		return heavyLiftTon;
	}

	public void setHeavyLiftTon(Integer heavyLiftTon) {
		this.heavyLiftTon = heavyLiftTon;
	}

	public String getTandemLiftInd() {
		return tandemLiftInd;
	}

	public void setTandemLiftInd(String tandemLiftInd) {
		this.tandemLiftInd = tandemLiftInd;
	}

	public Integer getGangsNbr() {
		return gangsNbr;
	}

	public void setGangsNbr(Integer gangsNbr) {
		this.gangsNbr = gangsNbr;
	}

	public Integer getMaxWrkHatchNbr() {
		return maxWrkHatchNbr;
	}

	public void setMaxWrkHatchNbr(Integer maxWrkHatchNbr) {
		this.maxWrkHatchNbr = maxWrkHatchNbr;
	}

	public Integer getCtrlHatchNbr() {
		return ctrlHatchNbr;
	}

	public void setCtrlHatchNbr(Integer ctrlHatchNbr) {
		this.ctrlHatchNbr = ctrlHatchNbr;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public GbccViewVvStevedore getViewVesselStevedoreVO() {
		return viewVesselStevedoreVO;
	}

	public void setVesselStevedoreVO(GbccViewVvStevedore viewVesselStevedoreVO) {
		this.viewVesselStevedoreVO = viewVesselStevedoreVO;
	}

	public List<GbccCargoOprPlanDet> getCargoOprPlanDetVO() {
		return cargoOprPlanDetVO;
	}

	public void setCargoOprPlanDetVO(List<GbccCargoOprPlanDet> cargoOprPlanDetVO) {
		this.cargoOprPlanDetVO = cargoOprPlanDetVO;
	}

	public String getStevedoreCompanyName() {
		return stevedoreCompanyName;
	}

	/*
	 * public void setAgentCompanyVO(CompanyCode agentCompanyVO) {
	 * this.agentCompanyVO = agentCompanyVO; }
	 * 
	 * public CompanyCode getAgentCompanyVO() { return agentCompanyVO; }
	 */
	public void setStevedoreCompanyName(String stevedoreCompanyName) {
		this.stevedoreCompanyName = stevedoreCompanyName;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Integer getTotalHatchDisc() {
		int tot = 0;

		if (this.cargoOprPlanDetVO != null) {
			for (int i = 0; i < this.cargoOprPlanDetVO.size(); i++) {
				GbccCargoOprPlanDet detVO = (GbccCargoOprPlanDet) this.cargoOprPlanDetVO.get(i);
				tot += detVO.getDiscTon().intValue();
			}
		}
		return new Integer(tot);
	}

	public Integer getTotalHatchLoad() {
		int tot = 0;

		if (this.cargoOprPlanDetVO != null) {
			for (int i = 0; i < this.cargoOprPlanDetVO.size(); i++) {
				GbccCargoOprPlanDet detVO = (GbccCargoOprPlanDet) this.cargoOprPlanDetVO.get(i);
				tot += detVO.getLoadTon().intValue();
			}
		}
		return new Integer(tot);
	}

	protected void setAuditableFields() {
		auditFields = new HashSet();
		auditFields.add("stevCoCd");
		auditFields.add("checkerNm");
		auditFields.add("checkerHpNbr");
		auditFields.add("totDiscTon");
		auditFields.add("totLoadTon");
		auditFields.add("craneOnboardNbr");
		auditFields.add("craneOnboardTon");
		auditFields.add("mobileCraneNbr");
		auditFields.add("mobileCraneTon");
		auditFields.add("floatingCraneNbr");
		auditFields.add("floatingCraneTon");
		auditFields.add("heavyLiftNbr");
		auditFields.add("heavyLiftTon");
		auditFields.add("tandemLiftInd");
		auditFields.add("gangsNbr");
		auditFields.add("maxWrkHatchNbr");
		auditFields.add("ctrlHatchNbr");
		auditFields.add("lastModifyUserId");
		auditFields.add("lastModifyDttm");
		auditFields.add("remarks");
		// MC Consulting for ICR 2: START
		auditFields.add("unit");
		// MC Consulting for ICR 2: END

	}

	protected void setDateFields() {
		dateFields = new HashSet();
		dateFields.add("lastModifyDttm");

	}

	public String getCraneOnboardTonStr() {
		return craneOnboardTonStr;
	}

	public void setCraneOnboardTonStr(String craneOnboardTonStr) {
		this.craneOnboardTonStr = craneOnboardTonStr;
	}

	public String getCraneOnboardTonRemark() {
		return craneOnboardTonRemark;
	}

	public void setCraneOnboardTonRemark(String craneOnboardTonRemark) {
		this.craneOnboardTonRemark = craneOnboardTonRemark;
	}

	public String getMobileCraneTonStr() {
		return mobileCraneTonStr;
	}

	public void setMobileCraneTonStr(String mobileCraneTonStr) {
		this.mobileCraneTonStr = mobileCraneTonStr;
	}

	public String getMobileCraneTonRemark() {
		return mobileCraneTonRemark;
	}

	public void setMobileCraneTonRemark(String mobileCraneTonRemark) {
		this.mobileCraneTonRemark = mobileCraneTonRemark;
	}

	public String getFloatingCraneTonStr() {
		return floatingCraneTonStr;
	}

	public void setFloatingCraneTonStr(String floatingCraneTonStr) {
		this.floatingCraneTonStr = floatingCraneTonStr;
	}

	public String getFloatingCraneTonRemark() {
		return floatingCraneTonRemark;
	}

	public void setFloatingCraneTonRemark(String floatingCraneTonRemark) {
		this.floatingCraneTonRemark = floatingCraneTonRemark;
	}

	public String getHeavyLiftTonStr() {
		return heavyLiftTonStr;
	}

	public void setHeavyLiftTonStr(String heavyLiftTonStr) {
		this.heavyLiftTonStr = heavyLiftTonStr;
	}

	public String getHeavyLiftTonRemark() {
		return heavyLiftTonRemark;
	}

	public void setHeavyLiftTonRemark(String heavyLiftTonRemark) {
		this.heavyLiftTonRemark = heavyLiftTonRemark;
	}

	// MC Consulting for ICR 2: START
	private String unit;
	private Integer bulkLiquid; // Lookup only, not stored in database
	private Integer bulkSolid; // Lookup only, not stored in database

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getBulkLiquid() {
		return bulkLiquid;
	}

	public void setBulkLiquid(Integer bulkLiquid) {
		this.bulkLiquid = bulkLiquid;
	}

	public Integer getBulkSolid() {
		return bulkSolid;
	}

	public void setBulkSolid(Integer bulkSolid) {
		this.bulkSolid = bulkSolid;
	}
	// MC Consulting for ICR 2: END

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	public String getVsl_lastModifyUserId() {
		return vsl_lastModifyUserId;
	}

	public void setVsl_lastModifyUserId(String vsl_lastModifyUserId) {
		this.vsl_lastModifyUserId = vsl_lastModifyUserId;
	}

	public Date getVsl_lastModifyDttm() {
		return vsl_lastModifyDttm;
	}

	public void setVsl_lastModifyDttm(Date vsl_lastModifyDttm) {
		this.vsl_lastModifyDttm = vsl_lastModifyDttm;
	}

	public String getStev_VvCd() {
		return stev_VvCd;
	}

	public void setStev_VvCd(String stev_VvCd) {
		this.stev_VvCd = stev_VvCd;
	}

	public String getBerth_VvCd() {
		return berth_VvCd;
	}

	public void setBerth_VvCd(String berth_VvCd) {
		this.berth_VvCd = berth_VvCd;
	}
}

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOpenBalVO extends AuditLogRecord implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GbccCargoOpenBalId id;
	private String vvCd;
    private String stevCoCd;
    private Integer wrkHatchNbr;
    private Integer ctrlHatchNbr;
    private String checkerNm;
    private String checkerOfficeNbr;
    private String checkerHpNbr;
    private Integer gangsNbr;
    private Date wrkStartDttm;
    private String delayRsn1Cd;
    private String delayRsn2Cd;
    private String mobileCraneInd;
    private String heavyLiftInd;
    private Integer totDiscTon;
    private Integer totLoadTon;
    private String lastModifyUserId;
    private Date lastModifyDttm;
    
    private VesselCall vesselCallVO;
    private Berthing firstBerthingVO;
    private Berthing lastBerthingVO;
    //private CompanyCode agentCompanyVO;
    private GbccViewVvStevedore viewVesselStevedoreVO;
    private List<GbccCargoOpenBalDet> cargoOpenBalDetVO;
    
    private String agentName;
    
    private String stevedoreCompanyName;

    private String delayRsn1Name;
    private String delayRsn2Name;
    
    //VietNguyen (FPT) add new field for StevedoreNet 09May2012 : start
    private String delayRemarks;
    private Date createDttm;
    //VietNguyen (FPT) add new field for StevedoreNet 09May2012 : end    
    
    //vc
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
    private String tandemLiftInd;
    private String protrusionInd;
    private String floatCraneInd;

    private String vvStatusIndText;
    
    private Date lastAtuDttm;
    
    // berth
    private BerthingId berth_id;
    private String berth_vvCd;
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
    
    private GbccViewVvStevedoreId gbccView_id;
   
     private BigDecimal lineno;
    private String stevContact;
    private String stevRemarks;
    private String stevRep;
    private String gbccView_lastModifyUserId;    
    private String gbccView_stevedoreCompanyName;
    
    // vs
    private String  stev_co_nm;
    private String stev_coCd;
    private String co_nm;
    private String  delay_rsn1_name;
    private String  delay_rsn2_name;
    
    private Integer total;     
    
    public GbccCargoOpenBalVO() {
    }

    public GbccCargoOpenBalVO(Integer wrkHatchNbr, Integer ctrlHatchNbr, String checkerNm, String checkerOfficeNbr, String checkerHpNbr, Integer gangsNbr, Date wrkStartDttm, String delayRsn1Cd, String delayRsn2Cd, String mobileCraneInd, String heavyLiftInd, Integer totDiscTon, Integer totLoadTon, String lastModifyUserId, Date lastModifyDttm) {
        this.wrkHatchNbr = wrkHatchNbr;
        this.ctrlHatchNbr = ctrlHatchNbr;
        this.checkerNm = checkerNm;
        this.checkerOfficeNbr = checkerOfficeNbr;
        this.checkerHpNbr = checkerHpNbr;
        this.gangsNbr = gangsNbr;
        this.wrkStartDttm = wrkStartDttm;
        this.delayRsn1Cd = delayRsn1Cd;
        this.delayRsn2Cd = delayRsn2Cd;
        this.mobileCraneInd = mobileCraneInd;
        this.heavyLiftInd = heavyLiftInd;
        this.totDiscTon = totDiscTon;
        this.totLoadTon = totLoadTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public GbccCargoOpenBalVO(GbccCargoOpenBalId id, Integer wrkHatchNbr, Integer ctrlHatchNbr, String checkerNm, String checkerOfficeNbr, String checkerHpNbr, Integer gangsNbr, Date wrkStartDttm, String delayRsn1Cd, String delayRsn2Cd, String mobileCraneInd, String heavyLiftInd, Integer totDiscTon, Integer totLoadTon, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.wrkHatchNbr = wrkHatchNbr;
        this.ctrlHatchNbr = ctrlHatchNbr;
        this.checkerNm = checkerNm;
        this.checkerOfficeNbr = checkerOfficeNbr;
        this.checkerHpNbr = checkerHpNbr;
        this.gangsNbr = gangsNbr;
        this.wrkStartDttm = wrkStartDttm;
        this.delayRsn1Cd = delayRsn1Cd;
        this.delayRsn2Cd = delayRsn2Cd;
        this.mobileCraneInd = mobileCraneInd;
        this.heavyLiftInd = heavyLiftInd;
        this.totDiscTon = totDiscTon;
        this.totLoadTon = totLoadTon;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    
    
    
 

	public String getDelay_rsn1_name() {
		return delay_rsn1_name;
	}

	public void setDelay_rsn1_name(String delay_rsn1_name) {
		this.delay_rsn1_name = delay_rsn1_name;
	}

	public String getDelay_rsn2_name() {
		return delay_rsn2_name;
	}

	public void setDelay_rsn2_name(String delay_rsn2_name) {
		this.delay_rsn2_name = delay_rsn2_name;
	}

	public String getStevCoCd() {
		return stevCoCd;
	}

	public void setStevCoCd(String stevCoCd) {
		this.stevCoCd = stevCoCd;
	}

	public BigDecimal getLineno() {
		return lineno;
	}

	public void setLineno(BigDecimal lineno) {
		this.lineno = lineno;
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

	public String getGbccView_stevedoreCompanyName() {
		return gbccView_stevedoreCompanyName;
	}

	public void setGbccView_stevedoreCompanyName(String gbccView_stevedoreCompanyName) {
		this.gbccView_stevedoreCompanyName = gbccView_stevedoreCompanyName;
	}

	public String getStev_co_nm() {
		return stev_co_nm;
	}

	public void setStev_co_nm(String stev_co_nm) {
		this.stev_co_nm = stev_co_nm;
	}

	public String getCo_nm() {
		return co_nm;
	}

	public void setCo_nm(String co_nm) {
		this.co_nm = co_nm;
	}

	public void setViewVesselStevedoreVO(GbccViewVvStevedore viewVesselStevedoreVO) {
		this.viewVesselStevedoreVO = viewVesselStevedoreVO;
	}

	public GbccCargoOpenBalId getId() {
        return id;
    }

    public void setId(GbccCargoOpenBalId id) {
        this.id = id;
    }

    public Integer getWrkHatchNbr() {
        return wrkHatchNbr;
    }

    public void setWrkHatchNbr(Integer wrkHatchNbr) {
        this.wrkHatchNbr = wrkHatchNbr;
    }

    public Integer getCtrlHatchNbr() {
        return ctrlHatchNbr;
    }

    public void setCtrlHatchNbr(Integer ctrlHatchNbr) {
        this.ctrlHatchNbr = ctrlHatchNbr;
    }

    public String getCheckerNm() {
        return checkerNm;
    }

    public void setCheckerNm(String checkerNm) {
        this.checkerNm = checkerNm;
    }

    public String getCheckerOfficeNbr() {
        return checkerOfficeNbr;
    }

    public void setCheckerOfficeNbr(String checkerOfficeNbr) {
        this.checkerOfficeNbr = checkerOfficeNbr;
    }

    public String getCheckerHpNbr() {
        return checkerHpNbr;
    }

    public void setCheckerHpNbr(String checkerHpNbr) {
        this.checkerHpNbr = checkerHpNbr;
    }

    public Integer getGangsNbr() {
        return gangsNbr;
    }

    public void setGangsNbr(Integer gangsNbr) {
        this.gangsNbr = gangsNbr;
    }

    public Date getWrkStartDttm() {
        return wrkStartDttm;
    }

    public void setWrkStartDttm(Date wrkStartDttm) {
        this.wrkStartDttm = wrkStartDttm;
    }

    public String getDelayRsn1Cd() {
        return delayRsn1Cd;
    }

    public void setDelayRsn1Cd(String delayRsn1Cd) {
        this.delayRsn1Cd = delayRsn1Cd;
    }

    public String getDelayRsn2Cd() {
        return delayRsn2Cd;
    }

    public void setDelayRsn2Cd(String delayRsn2Cd) {
        this.delayRsn2Cd = delayRsn2Cd;
    }

    public String getMobileCraneInd() {
        return mobileCraneInd;
    }

    public void setMobileCraneInd(String mobileCraneInd) {
        this.mobileCraneInd = mobileCraneInd;
    }

    public String getHeavyLiftInd() {
        return heavyLiftInd;
    }

    public void setHeavyLiftInd(String heavyLiftInd) {
        this.heavyLiftInd = heavyLiftInd;
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

    public VesselCall getVesselCallVO() {
    	return vesselCallVO;
    }
    
    public void setVesselCallVO(VesselCall vesselCallVO) {
    	this.vesselCallVO = vesselCallVO;
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
    
    public GbccViewVvStevedore getViewVesselStevedoreVO() {
    	return viewVesselStevedoreVO;
    }
    
    public void setVesselStevedoreVO(GbccViewVvStevedore viewVesselStevedoreVO) {
    	this.viewVesselStevedoreVO = viewVesselStevedoreVO;
    }
    
    public List<GbccCargoOpenBalDet> getCargoOpenBalDetVO() {
    	return cargoOpenBalDetVO;
    }
    
    public void setCargoOpenBalDetVO(List<GbccCargoOpenBalDet> cargoOpenBalDetVO) {
    	this.cargoOpenBalDetVO = cargoOpenBalDetVO;
    }
    
    public String getStevedoreCompanyName() {
        return stevedoreCompanyName;
    }

    public void setStevedoreCompanyName(String stevedoreCompanyName) {
        this.stevedoreCompanyName = stevedoreCompanyName;
    }
    
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    public String getDelayRsn1Name() {
        return delayRsn1Name;
    }

    public void setDelayRsn1Name(String delayRsn1Name) {
        this.delayRsn1Name = delayRsn1Name;
    }

    public String getDelayRsn2Name() {
        return delayRsn2Name;
    }

    public void setDelayRsn2Name(String delayRsn2Name) {
        this.delayRsn2Name = delayRsn2Name;
    }
    
    public void recalcuteLoadDiscTotal() {

    	if (this.cargoOpenBalDetVO == null)
    		return;
    	
    	List oVOs = this.cargoOpenBalDetVO;
    	int totalLoad = 0;
    	int totalDisc = 0;
    	
    	for (int i=0; i<oVOs.size(); i++ ) {
    		GbccCargoOpenBalDet oDet = (GbccCargoOpenBalDet) oVOs.get(i); 
    		totalLoad = totalLoad + oDet.getLoadOpenBalTon().intValue();
    		totalDisc = totalDisc + oDet.getDiscOpenBalTon().intValue();
    	}
    	this.totDiscTon = new Integer(totalDisc);
    	this.totLoadTon = new Integer(totalLoad);
    }
    
    protected void setAuditableFields() { 
    	auditFields = new HashSet();   
    	auditFields.add("wrkHatchNbr");
    	auditFields.add("ctrlHatchNbr");
    	auditFields.add("checkerNm");
    	auditFields.add("checkerOfficeNbr");
    	auditFields.add("checkerHpNbr");
    	auditFields.add("gangsNbr");
    	auditFields.add("wrkStartDttm");
    	auditFields.add("delayRsn1Cd");
    	auditFields.add("delayRsn2Cd");
    	auditFields.add("mobileCraneInd");
    	auditFields.add("heavyLiftInd");
    	auditFields.add("totDiscTon");
    	auditFields.add("totLoadTon");
    	
    }
    
	protected void setDateFields() { 
		dateFields = new HashSet();
		dateFields.add("wrkStartDttm");
		
	}
	
	public String getDelayRemarks() {
		return delayRemarks;
	}

	public void setDelayRemarks(String delayRemarks) {
		this.delayRemarks = delayRemarks;
	}

	public Date getCreateDttm() {
		return createDttm;
	}

	public void setCreateDttm(Date createDttm) {
		this.createDttm = createDttm;
	}
	
	
	
	 @Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return "";
			}
		}

	public String getStev_coCd() {
		return stev_coCd;
	}

	public void setStev_coCd(String stev_coCd) {
		this.stev_coCd = stev_coCd;
	}

	public String getVv_Cd() {
		return vv_Cd;
	}

	public void setVv_Cd(String vv_Cd) {
		this.vv_Cd = vv_Cd;
	}

	public String getBerth_vvCd() {
		return berth_vvCd;
	}

	public void setBerth_vvCd(String berth_vvCd) {
		this.berth_vvCd = berth_vvCd;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}

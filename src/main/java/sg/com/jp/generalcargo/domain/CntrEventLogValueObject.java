package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CntrEventLogValueObject extends UserTimestampVO {


	private static final Log log = LogFactory.getLog(CntrEventLogValueObject.class);
	private static final long serialVersionUID = 1L;
	private	Integer		cntrSeqNbr;
	private String		cntrNbr;
	private	String 		status;
	private String		prevPurpCd;
	private Timestamp	txnDttm;
	private String		txnCd;
	private String 		userId;
	private	String		craneNm;
	private String		craneOprId;
	private String 		wtaId;
	private String		pmType;
	private String		pmNm;
	private	String		pmOprId;
	private String		isoSizeTypeCd;
	private String 		sizeFt;
	private String		catCd;
	private Integer		declrWt;
	private Integer		measureWt;
	private String		purpCd;
	private String 		cntrOprCd;
	private String		haulCd;
	private String		loloPartyInd;
	private String		discSlotOprCd;
	private String		loadSlotOprCd;
	private String		renomSlotOprCd;
	private String		discVvCd;
	private String		ldVvCd;
	private String 		renomVvCd;
	private String		psrc;
	private String		pload;
	private String		pdisc;
	private String		pdest;
	private String		dgInd;
	private String		imdgClCd;
	private String		refrInd;
	private String		ucInd;
	private String		overSzInd;
	private String		intergatewayInd;
	private Timestamp	discDttm;
	private Timestamp	loadDttm;
	private Timestamp	offloadDttm;
	private Timestamp	mountDttm;
	private Timestamp	arrDttm;
	private Timestamp	exitDttm;
	private Timestamp 	changePurpDttm;
	private String		dirHdlgInd;
	private String		chasProvInd;
	private String		gearUsed;
	private String		pluginTemp;
	private Timestamp	pluginDttm;
	private Timestamp	unplugDttm;
	private	Integer		ucHandlingDur;
	private String		athwartshipInd;
	private String		billVslInd;
	private String 		billYdInd;
	private String		procQcIncentive;
	private String		procYcIncentive;
	private String      procPmIncentive;
	private String		lastModifyUserId;
	private Timestamp 	lastModifyDttm;
	private Timestamp	codArrTime;

	//START: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
	private Long miscSeqNbr;
	private String vvCd;
	private String billInd;
	private String refNbr;
	private String pDisc1;
	//END: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006

	private String periodiSRInd;
	private int storeRentDays;

    /** Creates new CntrEventLogValueObject */
    public CntrEventLogValueObject() {
		cntrSeqNbr = null;
		cntrNbr = null;
		status = null;
		prevPurpCd = null;
		txnDttm = null;
		txnCd = null;
		userId = null;
		craneNm = null;
		craneOprId = null;
		wtaId = null;
		pmType = null;
		pmNm = null;
		pmOprId = null;
		isoSizeTypeCd = null;
		sizeFt = null;
		catCd = null;
		declrWt = null;
		measureWt = null;
		purpCd = null;
		cntrOprCd = null;
		haulCd = null;
		loloPartyInd = null;
		discSlotOprCd = null;
		loadSlotOprCd = null;
		renomSlotOprCd = null;
		discVvCd = null;
		ldVvCd = null;
		renomVvCd = null;
		psrc = null;
		pload = null;
		pdisc = null;
		pdest = null;
		dgInd = null;
		imdgClCd = null;
		refrInd = null;
		ucInd = null;
		overSzInd = null;
		intergatewayInd = null;
		discDttm = null;
		loadDttm = null;
		offloadDttm = null;
		mountDttm = null;
		arrDttm = null;
		exitDttm = null;
		changePurpDttm = null;
		dirHdlgInd = null;
		chasProvInd = null;
		gearUsed = null;
		pluginTemp = null;
		pluginDttm = null;
		unplugDttm = null;
		ucHandlingDur = null;
		athwartshipInd = null;
		billVslInd = null;
		billYdInd = null;
		procQcIncentive = null;
		procYcIncentive = null;
		procPmIncentive = null;
		lastModifyUserId = null;
		lastModifyDttm = null;
		// START: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
		miscSeqNbr = null;
		vvCd = null;
		billInd = null;
		refNbr = null;
		pDisc1 = null;
		// END: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
		periodiSRInd = null;
		storeRentDays = 0;
		codArrTime = null;
    }

    public CntrEventLogValueObject(CntrEventLogValueObject cntrEventLogValueObject) {
		this.copy(cntrEventLogValueObject);
	}

	public void copy(CntrEventLogValueObject cntrEventLogValueObject) {
       	this.cntrSeqNbr     = cntrEventLogValueObject.getCntrSeqNbr();
        this.cntrNbr        = cntrEventLogValueObject.getCntrNbr();
        this.status         = cntrEventLogValueObject.getStatus();
        this.prevPurpCd     = cntrEventLogValueObject.getPrevPurpCd();
        this.txnDttm        = cntrEventLogValueObject.getTxnDttm();
        this.txnCd          = cntrEventLogValueObject.getTxnCd();
        this.userId         = cntrEventLogValueObject.getUserId();
        this.craneNm        = cntrEventLogValueObject.getCraneNm();
        this.craneOprId     = cntrEventLogValueObject.getCraneOprId();
        this.wtaId          = cntrEventLogValueObject.getWtaId();
        this.pmType         = cntrEventLogValueObject.getPmType();
        this.pmNm           = cntrEventLogValueObject.getPmNm();
        this.pmOprId        = cntrEventLogValueObject.getPmOprId();
        this.isoSizeTypeCd  = cntrEventLogValueObject.getIsoSizeTypeCd();
        this.sizeFt         = cntrEventLogValueObject.getSizeFt();
        this.catCd          = cntrEventLogValueObject.getCatCd();
        this.declrWt        = cntrEventLogValueObject.getDeclrWt();
        this.measureWt      = cntrEventLogValueObject.getMeasureWt();
        this.purpCd         = cntrEventLogValueObject.getPurpCd();
        this.cntrOprCd      = cntrEventLogValueObject.getCntrOprCd();
        this.haulCd         = cntrEventLogValueObject.getHaulCd();
        this.loloPartyInd   = cntrEventLogValueObject.getLoloPartyInd();
        this.discSlotOprCd  = cntrEventLogValueObject.getDiscSlotOprCd();
        this.loadSlotOprCd  = cntrEventLogValueObject.getLoadSlotOprCd();
        this.renomSlotOprCd = cntrEventLogValueObject.getRenomSlotOprCd();
        this.discVvCd       = cntrEventLogValueObject.getDiscVvCd();
        this.ldVvCd         = cntrEventLogValueObject.getLdVvCd();
        this.renomVvCd      = cntrEventLogValueObject.getRenomVvCd();
        this.psrc           = cntrEventLogValueObject.getPsrc();
        this.pload          = cntrEventLogValueObject.getPload();
        this.pdisc          = cntrEventLogValueObject.getPdisc();
        this.pdest          = cntrEventLogValueObject.getPdest();
        this.dgInd          = cntrEventLogValueObject.getDgInd();
        this.imdgClCd       = cntrEventLogValueObject.getImdgClCd();
        this.refrInd        = cntrEventLogValueObject.getRefrInd();
        this.ucInd          = cntrEventLogValueObject.getUcInd();
        this.overSzInd      = cntrEventLogValueObject.getOverSzInd();
        this.intergatewayInd= cntrEventLogValueObject.getIntergatewayInd();
        this.discDttm       = cntrEventLogValueObject.getDiscDttm();
        this.loadDttm       = cntrEventLogValueObject.getLoadDttm();
        this.offloadDttm    = cntrEventLogValueObject.getOffloadDttm();
        this.mountDttm      = cntrEventLogValueObject.getMountDttm();
        this.arrDttm        = cntrEventLogValueObject.getArrDttm();
        this.exitDttm       = cntrEventLogValueObject.getExitDttm();
        this.changePurpDttm = cntrEventLogValueObject.getChangePurpDttm();
        this.dirHdlgInd     = cntrEventLogValueObject.getDirHdlgInd();
        this.chasProvInd    = cntrEventLogValueObject.getChasProvInd();
        this.gearUsed       = cntrEventLogValueObject.getGearUsed();
        this.pluginTemp     = cntrEventLogValueObject.getPluginTemp();
        this.pluginDttm     = cntrEventLogValueObject.getPluginDttm();
        this.unplugDttm     = cntrEventLogValueObject.getUnplugDttm();
        this.ucHandlingDur  = cntrEventLogValueObject.getUcHandlingDur();
        this.athwartshipInd = cntrEventLogValueObject.getAthwartshipInd();
        this.billVslInd     = cntrEventLogValueObject.getBillVslInd();
        this.billYdInd      = cntrEventLogValueObject.getBillYdInd();
        this.procQcIncentive= cntrEventLogValueObject.getProcQcIncentive();
        this.procYcIncentive= cntrEventLogValueObject.getProcYcIncentive();
        this.procPmIncentive= cntrEventLogValueObject.getProcPMIncentive();
        this.lastModifyUserId= cntrEventLogValueObject.getLastModifyUserId();
        this.lastModifyDttm = cntrEventLogValueObject.getLastModifyDttm();
        // START: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
        this.miscSeqNbr = cntrEventLogValueObject.getMiscSeqNbr();
        this.vvCd = cntrEventLogValueObject.getVvCd();
		this.billInd = cntrEventLogValueObject.getBillInd();
		this.refNbr = cntrEventLogValueObject.getRefNbr();
		this.pDisc1 = cntrEventLogValueObject.getPDisc1();
		// END: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
		this.periodiSRInd = cntrEventLogValueObject.getPeriodiSRInd();
		this.storeRentDays = cntrEventLogValueObject.getStoreRentDays();
		this.codArrTime = cntrEventLogValueObject.getCodArrTime();
    }

    public void doGet(Object object) {

        CntrEventLog cntrEventLog   = (CntrEventLog)object;

        try {
        	this.cntrSeqNbr= cntrEventLog.getCntrSeqNbr();
            this.cntrNbr= cntrEventLog.getCntrNbr();
            this.status= cntrEventLog.getStatus();
            this.prevPurpCd= cntrEventLog.getPrevPurpCd();
            this.txnDttm= cntrEventLog.getTxnDttm();
            this.txnCd= cntrEventLog.getTxnCd();
            this.userId= cntrEventLog.getUserId();
            this.craneNm= cntrEventLog.getCraneNm();
            this.craneOprId= cntrEventLog.getCraneOprId();
            this.wtaId= cntrEventLog.getWtaId();
            this.pmType= cntrEventLog.getPmType();
            this.pmNm= cntrEventLog.getPmNm();
            this.pmOprId= cntrEventLog.getPmOprId();
            this.isoSizeTypeCd= cntrEventLog.getIsoSizeTypeCd();
            this.sizeFt= cntrEventLog.getSizeFt();
            this.catCd= cntrEventLog.getCatCd();
            this.declrWt= cntrEventLog.getDeclrWt();
            this.measureWt= cntrEventLog.getMeasureWt();
            this.purpCd= cntrEventLog.getPurpCd();
            this.cntrOprCd= cntrEventLog.getCntrOprCd();
            this.haulCd= cntrEventLog.getHaulCd();
            this.loloPartyInd= cntrEventLog.getLoloPartyInd();
            this.discSlotOprCd= cntrEventLog.getDiscSlotOprCd();
            this.loadSlotOprCd= cntrEventLog.getLoadSlotOprCd();
            this.renomSlotOprCd= cntrEventLog.getRenomSlotOprCd();
            this.discVvCd= cntrEventLog.getDiscVvCd();
            this.ldVvCd= cntrEventLog.getLdVvCd();
            this.renomVvCd= cntrEventLog.getRenomVvCd();
            this.psrc= cntrEventLog.getPsrc();
            this.pload= cntrEventLog.getPload();
            this.pdisc= cntrEventLog.getPdisc();
            this.pdest= cntrEventLog.getPdest();
            this.dgInd= cntrEventLog.getDgInd();
            this.imdgClCd= cntrEventLog.getImdgClCd();
            this.refrInd= cntrEventLog.getRefrInd();
            this.ucInd= cntrEventLog.getUcInd();
            this.overSzInd= cntrEventLog.getOverSzInd();
            this.intergatewayInd= cntrEventLog.getIntergatewayInd();
            this.discDttm= cntrEventLog.getDiscDttm();
            this.loadDttm= cntrEventLog.getLoadDttm();
            this.offloadDttm= cntrEventLog.getOffloadDttm();
            this.mountDttm= cntrEventLog.getMountDttm();
            this.arrDttm= cntrEventLog.getArrDttm();
            this.exitDttm= cntrEventLog.getExitDttm();
            this.changePurpDttm= cntrEventLog.getChangePurpDttm();
            this.dirHdlgInd= cntrEventLog.getDirHdlgInd();
            this.chasProvInd= cntrEventLog.getChasProvInd();
            this.gearUsed= cntrEventLog.getGearUsed();
            this.pluginTemp= cntrEventLog.getPluginTemp();
            this.pluginDttm= cntrEventLog.getPluginDttm();
            this.unplugDttm= cntrEventLog.getUnplugDttm();
            this.ucHandlingDur= cntrEventLog.getUcHandlingDur();
            this.athwartshipInd= cntrEventLog.getAthwartshipInd();
            this.billVslInd= cntrEventLog.getBillVslInd();
            this.billYdInd= cntrEventLog.getBillYdInd();
            this.procQcIncentive= cntrEventLog.getProcQcIncentive();
            this.procYcIncentive= cntrEventLog.getProcYcIncentive();
            this.procPmIncentive= cntrEventLog.getProcPmIncentive();
            this.lastModifyUserId= cntrEventLog.getLastModifyUserId();
            this.lastModifyDttm= cntrEventLog.getLastModifyDttm();
			this.codArrTime = cntrEventLog.getCodArrTime();
        }
        catch (Exception remoteException) {
            log.info("Exception in doGet of CntrEventLogValueObject");
        }
    }

    public void doSet(Object object) {

        CntrEventLog cntrEventLog   = (CntrEventLog)object;

        try {
        	cntrEventLog.setCntrSeqNbr(this.cntrSeqNbr);
            cntrEventLog.setCntrNbr(this.cntrNbr);
            cntrEventLog.setStatus(this.status);
            cntrEventLog.setPrevPurpCd(this.prevPurpCd);
            cntrEventLog.setTxnDttm(this.txnDttm);
            cntrEventLog.setTxnCd(this.txnCd);
            cntrEventLog.setUserId(this.userId);
            cntrEventLog.setCraneNm(this.craneNm);
            cntrEventLog.setCraneOprId(this.craneOprId);
            cntrEventLog.setWtaId(this.wtaId);
            cntrEventLog.setPmType(this.pmType);
            cntrEventLog.setPmNm(this.pmNm);
            cntrEventLog.setPmOprId(this.pmOprId);
            cntrEventLog.setIsoSizeTypeCd(this.isoSizeTypeCd);
            cntrEventLog.setSizeFt(this.sizeFt);
            cntrEventLog.setCatCd(this.catCd);
            cntrEventLog.setDeclrWt(this.declrWt);
            cntrEventLog.setMeasureWt(this.measureWt);
            cntrEventLog.setPurpCd(this.purpCd);
            cntrEventLog.setCntrOprCd(this.cntrOprCd);
            cntrEventLog.setHaulCd(this.haulCd);
            cntrEventLog.setLoloPartyInd(this.loloPartyInd);
            cntrEventLog.setDiscSlotOprCd(this.discSlotOprCd);
            cntrEventLog.setLoadSlotOprCd(this.loadSlotOprCd);
            cntrEventLog.setRenomSlotOprCd(this.renomSlotOprCd);
            cntrEventLog.setDiscVvCd(this.discVvCd);
            cntrEventLog.setLdVvCd(this.ldVvCd);
            cntrEventLog.setRenomVvCd(this.renomVvCd);
            cntrEventLog.setPsrc(this.psrc);
            cntrEventLog.setPload(this.pload);
            cntrEventLog.setPdisc(this.pdisc);
            cntrEventLog.setPdest(this.pdest);
            cntrEventLog.setDgInd(this.dgInd);
            cntrEventLog.setImdgClCd(this.imdgClCd);
            cntrEventLog.setRefrInd(this.refrInd);
            cntrEventLog.setUcInd(this.ucInd);
            cntrEventLog.setOverSzInd(this.overSzInd);
            cntrEventLog.setIntergatewayInd(this.intergatewayInd);
            cntrEventLog.setDiscDttm(this.discDttm);
            cntrEventLog.setLoadDttm(this.loadDttm);
            cntrEventLog.setOffloadDttm(this.offloadDttm);
            cntrEventLog.setMountDttm(this.mountDttm);
            cntrEventLog.setArrDttm(this.arrDttm);
            cntrEventLog.setExitDttm(this.exitDttm);
            cntrEventLog.setChangePurpDttm(this.changePurpDttm);
            cntrEventLog.setDirHdlgInd(this.dirHdlgInd);
            cntrEventLog.setChasProvInd(this.chasProvInd);
            cntrEventLog.setGearUsed(this.gearUsed);
            cntrEventLog.setPluginTemp(this.pluginTemp);
            cntrEventLog.setPluginDttm(this.pluginDttm);
            cntrEventLog.setUnplugDttm(this.unplugDttm);
            cntrEventLog.setUcHandlingDur(this.ucHandlingDur);
            cntrEventLog.setAthwartshipInd(this.athwartshipInd);
            cntrEventLog.setBillVslInd(this.billVslInd);
            cntrEventLog.setBillYdInd(this.billYdInd);
            cntrEventLog.setProcQcIncentive(this.procQcIncentive);
            cntrEventLog.setProcYcIncentive(this.procYcIncentive);
            cntrEventLog.setProcPmIncentive(this.procPmIncentive);
            cntrEventLog.setLastModifyUserId(this.lastModifyUserId);
            cntrEventLog.setLastModifyDttm(this.lastModifyDttm);
			cntrEventLog.setCodArrTime(this.codArrTime);
        }
        catch (Exception remoteException) {
            log.info("Exception in doSet of CntrEventLogValueObject");
        }
    }
	
	public Timestamp getCodArrTime() {
    	return(codArrTime);
    }

    public void setCodArrTime(Timestamp codArrTime) {
    	this.codArrTime		= codArrTime;
    }	
    /**
     *	This method retrieves the Container sequence number
     *
     *  @param		void
     *  @return 	Integer
     */
    public Integer getCntrSeqNbr() {
    	return(cntrSeqNbr);
    }

    /**
     *	This method retrieves the Container number
     *
     *  @param		void
     *  @return 	String
     */
    public String getCntrNbr() {
    	return(cntrNbr);
    }

    /**
     *	This method retrieves the Container status
     *
     *  @param		void
     *  @return 	String
     */
    public String getStatus() {
    	return(status);
    }

    /**
     *	This method retrieves the Container previous purpose code
     *
     *  @param		void
     *  @return 	String
     */
    public String getPrevPurpCd() {
    	return(prevPurpCd);
    }

    /**
     *	This method retrieves the Transaction timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getTxnDttm() {
    	return(txnDttm);
    }

    /**
     *	This method retrieves the Transaction code
     *
     *  @param		void
     *  @return 	String
     */
    public String getTxnCd() {
	return(txnCd);
    }

    /**
     *	This method retrieves the User ID who confirmed the event
     *
     *  @param		void
     *  @return 	String
     */
    public String getUserId() {
    	return(userId);
    }

    /**
     *	This method retrieves the Crane name
    *
    *  @param		void
    *  @return 	String
    */
    public String getCraneNm() {
	return(craneNm);
    }

    /**
     *	This method retrieves the Crane Operator user ID
     *
     *  @param		void
     *  @return 	String
     */
    public String getCraneOprId() {
    	return(craneOprId);
    }

    /**
     *	This method retrieves the WTA user ID
     *
     *  @param		void
     *  @return 	String
     */
    public String getWtaId() {
    	return(wtaId);
    }

    /**
     *	This method retrieves the Prime Mover type
     *
     *  @param		void
     *  @return 	String
     */
    public String getPmType() {
    	return(pmType);
    }

    /**
     *	This method retrieves the Prime Mover name
     *
     *  @param		void
     *  @return 	String
     */
    public String getPmNm() {
    	return(pmNm);
    }

    /**
     *	This method retrieves the Prime Mover operator ID
     *
     *  @param		void
     *  @return 	String
     */
    public String getPmOprId() {
    	return(pmOprId);
    }

    /**
     *	This method retrieves the ISO size type code
     *
     *  @param		void
     *  @return 	String
     */
    public String getIsoSizeTypeCd() {
    	return(isoSizeTypeCd);
    }

    /**
     *	This method retrieves the Container size
     *
     *  @param		void
     *  @return 	String
     */
    public String getSizeFt() {
	return(sizeFt);
    }

    /**
     *	This method retrieves the Container category
     *
     *  @param		void
     *  @return 	String
     */
    public String getCatCd() {
    	return(catCd);
    }

    /**
     *	This method retrieves the Container declared weight
     *
     *  @param		void
     *  @return 	Integer
     */
    public Integer getDeclrWt() {
    	return(declrWt);
    }

    /**
     *	This method retrieves the Container measured weight
     *
     *  @param		void
     *  @return 	Integer
     */
    public Integer getMeasureWt() {
    	return(measureWt);
    }

    /**
     *	This method retrieves the Container purpose code
     *
     *  @param		void
     *  @return 	String
     */
    public String getPurpCd() {
    	return(purpCd);
    }

    /**
     *	This method retrieves the Container operator company code
     *
     *  @param		void
     *  @return 	String
     */
    public String getCntrOprCd() {
    	return(cntrOprCd);
    }

    /**
     *	This method retrieves the Haulier code
     *
     *  @param		void
     *  @return 	String
     */
    public String getHaulCd() {
	return(haulCd);
    }

    /**
     *	This method retrieves the LOLO billable party indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getLoloPartyInd() {
	return(loloPartyInd);
    }

    /**
     *	This method retrieves the Discharge slot operator code
     *
     *  @param		void
     *  @return 	String
     */
    public String getDiscSlotOprCd() {
	return(discSlotOprCd);
    }

    /**
     *	This method retrieves the Load slot operator code
     *
     *  @param		void
     *  @return 	String
     */
    public String getLoadSlotOprCd() {
	return(loadSlotOprCd);
    }

    /**
     *	This method retrieves the Renomination slot operator code
     *
     *  @param		void
     *  @return 	String
     */
    public String getRenomSlotOprCd() {
    	return(renomSlotOprCd);
    }

    /**
     *	This method retrieves the Discharge vessel voyage code
     *
     *  @param		void
     *  @return 	String
     */
    public String getDiscVvCd() {
    	return(discVvCd);
    }

    /**
     *	This method retrieves the Loa vessel voyage code
     *
     *  @param		void
     *  @return 	String
     */
    public String getLdVvCd() {
    	return(ldVvCd);
    }

    /**
     *	This method retrieves the Renomination vessel voyage code
     *
     *  @param		void
     *  @return 	String
     */
    public String getRenomVvCd() {
    	return(renomVvCd);
    }

    /**
     *	This method retrieves the Port of source
     *
     *  @param		void
     *  @return 	String
     */
    public String getPsrc() {
    	return(psrc);
    }

    /**
     *	This method retrieves the Port of loading
     *
     *  @param		void
     *  @return 	String
     */
    public String getPload() {
    	return(pload);
    }

    /**
     *	This method retrieves the Port of discharge
     *
     *  @param		void
     *  @return 	String
     */
    public String getPdisc() {
        return(pdisc);
    }

    /**
     *	This method retrieves the Port of destination
     *
     *  @param		void
     *  @return 	String
     */
    public String getPdest() {
    	return(pdest);
    }

    /**
     *	This method retrieves the DG indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getDgInd() {
    	return(dgInd);
    }

    /**
     *	This method retrieves the IMDG classification code
     *
     *  @param		void
     *  @return 	String
     */
    public String getImdgClCd() {
    	return(imdgClCd);
    }

    /**
     *	This method retrieves the Reefer indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getRefrInd() {
        return(refrInd);
    }

    /**
     *	This method retrieves the UC indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getUcInd() {
    	return(ucInd);
    }

    /**
     *	This method retrieves the Oversize indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getOverSzInd() {
	return(overSzInd);
    }

    /**
     *	This method retrieves the Intergateway indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getIntergatewayInd() {
    	return(intergatewayInd);
    }

    /**
     *	This method retrieves the Discharge timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getDiscDttm() {
    	return(discDttm);
    }

    /**
     *	This method retrieves the Load timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getLoadDttm() {
	return(loadDttm);
    }

    /**
     *  This method retrieves the Offload timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getOffloadDttm() {
        return(offloadDttm);
    }

    /**
     *	This method retrieves the Mount timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getMountDttm() {
	return(mountDttm);
    }

    /**
     *	This method retrieves the Arrival timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getArrDttm() {
    	return(arrDttm);
    }

    /**
     *	This method retrieves the Exit timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getExitDttm() {
    	return(exitDttm);
    }

    /**
     *	This method retrieves the Change of purpose timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getChangePurpDttm() {
    	return(changePurpDttm);
    }

    /**
     *	This method retrieves the Direct handling indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getDirHdlgInd() {
    	return(dirHdlgInd);
    }

    /**
     *	This method retrieves the Chassis provided indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getChasProvInd() {
    	return(chasProvInd);
    }

    /**
     *	This method retrieves the Gear used
     *
     *  @param		void
     *  @return 	String
     */
    public String getGearUsed() {
    	return(gearUsed);
    }

    /**
     *	This method retrieves the Plugin temperature
     *
     *  @param		void
     *  @return 	String
     */
    public String getPluginTemp() {
        return(pluginTemp);
    }

    /**
     *	This method retrieves the Plugin Timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getPluginDttm() {
    	return(pluginDttm);
    }

    /**
     *	This method retrieves the Unplug Timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getUnplugDttm() {
    	return(unplugDttm);
    }

    /**
     *	This method retrieves the UC handling time
     *
     *  @param		void
     *  @return 	Integer
     */
    public Integer getUcHandlingDur() {
        return(ucHandlingDur);
    }

    /**
     *	This method retrieves the Athwartship indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getAthwartshipInd() {
    	return(athwartshipInd);
    }

    /**
     *	This method retrieves the Bill vessel indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getBillVslInd() {
    	return(billVslInd);
    }

    /**
     *	This method retrieves the Bill yard indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getBillYdInd() {
	return(billYdInd);
    }

    /**
     *	This method retrieves the Process QC incentive indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getProcQcIncentive() {
	return(procQcIncentive);
    }

    /**
     *	This method retrieves the Process YC incentive indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getProcYcIncentive() {
    	return(procYcIncentive);
    }

    /**
     *	This method retrieves the Process PM incentive indicator
     *
     *  @param		void
     *  @return 	String
     */
    public String getProcPMIncentive() {
    	return(procPmIncentive);
    }

    /**
     *	This method retrieves the Last modified user ID
     *
     *  @param		void
     *  @return 	String
     */
    public String getLastModifyUserId() {
    	return(lastModifyUserId);
    }

    /**
     *	This method retrieves the Last modified timestamp
     *
     *  @param		void
     *  @return 	Timestamp
     */
    public Timestamp getLastModifyDttm() {
    	return(lastModifyDttm);
    }

    /**
     *	This method sets the Container sequence number
     *
     *  @param		Integer
     *  @return 	void
     */
    public void setCntrSeqNbr(Integer cntrSeqNbr) {
    	this.cntrSeqNbr		= cntrSeqNbr;
    }

    /**
     *	This method sets the Container number
     *
     *  @param		String
     *  @return 	void
     */
    public void setCntrNbr(String cntrNbr) {
    	this.cntrNbr		= cntrNbr;
    }

    /**
     *	This method sets the Container status
     *
     *  @param		String
     *  @return 	void
     */
    public void setStatus(String status) {
    	this.status			= status;
    }

    /**
     *	This method sets the Container previous purpose code
     *
     *  @param		String
     *  @return 	void
     */
    public void setPrevPurpCd(String prevPurpCd) {
    	this.prevPurpCd		= prevPurpCd;
    }

    /**
     *	This method sets the Transaction timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setTxnDttm(Timestamp txnDttm) {
    	this.txnDttm		= txnDttm;
    }

    /**
     *	This method sets the Transaction code
     *
     *  @param		String
     *  @return 	void
     */
    public void setTxnCd(String txnCd) {
    	this.txnCd			= txnCd;
    }

    /**
     *	This method sets the User ID who confirmed the event
     *
     *  @param		String
     *  @return 	void
     */
    public void setUserId(String userId) {
    	this.userId			= userId;
    }

    /**
     *	This method sets the Crane name
     *
     *  @param		String
     *  @return 	void
     */
    public void setCraneNm(String craneNm) {
    	this.craneNm		= craneNm;
    }

    /**
     *	This method sets the Crane Operator user ID
     *
     *  @param		String
     *  @return 	void
     */
    public void setCraneOprId(String craneOprId) {
    	this.craneOprId		= craneOprId;
    }

    /**
     *	This method sets the WTA user ID
     *
     *  @param		String
     *  @return 	void
     */
    public void setWtaId(String wtaId) {
   	this.wtaId			= wtaId;
    }

    /**
     *	This method sets the Prime Mover type
     *
     *  @param		String
     *  @return 	void
     */
    public void setPmType(String pmType) {
    	this.pmType			= pmType;
    }

    /**
     *	This method sets the Prime Mover name
     *
     *  @param		String
     *  @return 	void
     */
    public void setPmNm(String pmNm) {
	this.pmNm			= pmNm;
    }

    /**
     *	This method sets the Prime Mover operator ID
     *
     *  @param		String
     *  @return 	void
     */
    public void setPmOprId(String pmOprId) {
    	this.pmOprId		= pmOprId;
    }

    /**
     *	This method sets the ISO size type code
     *
     *  @param		String
     *  @return 	void
     */
    public void setIsoSizeTypeCd(String isoSizeTypeCd) {
    	this.isoSizeTypeCd	= isoSizeTypeCd;
    }

    /**
     *	This method sets the Container size
     *
     *  @param		String
     *  @return 	void
     */
    public void setSizeFt(String sizeFt) {
    	this.sizeFt			= sizeFt;
    }

    /**
     *	This method sets the Container category
     *
     *  @param		String
     *  @return 	void
     */
    public void setCatCd(String catCd) {
    	this.catCd			= catCd;
    }

    /**
     *	This method sets the Container declared weight
     *
     *  @param		Integer
     *  @return 	void
     */
    public void setDeclrWt(Integer declrWt) {
	this.declrWt		= declrWt;
    }

    /**
     *	This method sets the Container measured weight
     *
     *  @param		Integer
     *  @return 	void
     */
    public void setMeasureWt(Integer measureWt) {
    	this.measureWt		= measureWt;
    }

    /**
     *	This method sets the Container purpose code
     *
     *  @param		String
     *  @return 	void
     */
    public void setPurpCd(String purpCd) {
    	this.purpCd			= purpCd;
    }

    /**
     *	This method sets the Container operator company code
     *
     *  @param		String
     *  @return 	void
     */
    public void setCntrOprCd(String cntrOprCd) {
    	this.cntrOprCd		= cntrOprCd;
    }

    /**
     *	This method sets the Haulier code
     *
     *  @param		String
     *  @return 	void
     */
    public void setHaulCd(String haulCd) {
    	this.haulCd			= haulCd;
    }

    /**
     *	This method sets the LOLO billable party indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setLoloPartyInd(String loloPartyInd) {
    	this.loloPartyInd	= loloPartyInd;
    }

    /**
     *	This method sets the Discharge slot operator code
     *
     *  @param		String
     *  @return 	void
     */
    public void setDiscSlotOprCd(String discSlotOprCd) {
    	this.discSlotOprCd	= discSlotOprCd;
    }

    /**
     *	This method sets the Load slot operator code
     *
     *  @param		String
     *  @return 	void
     */
    public void setLoadSlotOprCd(String loadSlotOprCd) {
    	this.loadSlotOprCd	= loadSlotOprCd;
    }

    /**
     *	This method sets the Renomination slot operator code
     *
     *  @param		String
     *  @return 	void
     */
    public void setRenomSlotOprCd(String renomSlotOprCd) {
        this.renomSlotOprCd	= renomSlotOprCd;
    }

    /**
     *	This method sets the Discharge vessel voyage code
     *
     *  @param		String
     *  @return 	void
     */
    public void setDiscVvCd(String discVvCd) {
    	this.discVvCd		= discVvCd;
    }

    /**
     *	This method sets the Load vessel voyage code
     *
     *  @param		String
     *  @return 	void
     */
    public void setLdVvCd(String ldVvCd) {
    	this.ldVvCd			= ldVvCd;
    }

    /**
     *	This method sets the Renomination vessel voyage code
     *
     *  @param		String
     *  @return 	void
     */
    public void setRenomVvCd(String renomVvCd) {
    	this.renomVvCd		= renomVvCd;
    }

    /**
     *	This method sets the Port of source
     *
     *  @param		String
     *  @return 	void
     */
    public void setPsrc(String psrc) {
    	this.psrc			= psrc;
    }

    /**
     *	This method sets the Port of loading
     *
     *  @param		String
     *  @return 	void
     */
    public void setPload(String pload) {
    	this.pload			= pload;
    }

    /**
     *	This method sets the Port of discharge
     *
     *  @param		String
     *  @return 	void
     */
    public void setPdisc(String pdisc) {
    	this.pdisc			= pdisc;
    }

    /**
     *	This method sets the Port of destination
     *
     *  @param		String
     *  @return 	void
     */
    public void setPdest(String pdest) {
    	this.pdest			= pdest;
    }

    /**
     *	This method sets the DG indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setDgInd(String dgInd) {
    	this.dgInd			= dgInd;
    }

    /**
     *	This method sets the IMDG classification code
     *
     *  @param		String
     *  @return 	void
     */
    public void setImdgClCd(String imdgClCd) {
    	this.imdgClCd		= imdgClCd;
    }

    /**
     *	This method sets the Reefer indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setRefrInd(String refrInd) {
    	this.refrInd		= refrInd;
    }

    /**
     *	This method sets the UC indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setUcInd(String ucInd) {
    	this.ucInd			= ucInd;
    }

    /**
     *	This method sets the Oversize indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setOverSzInd(String overSzInd) {
    	this.overSzInd		= overSzInd;
    }

    /**
     *	This method sets the intergateway indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setIntergatewayInd(String intergatewayInd) {
    	this.intergatewayInd= intergatewayInd;
    }

    /**
     *	This method sets the Discharge timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setDiscDttm(Timestamp discDttm) {
    	this.discDttm		= discDttm;
    }

    /**
     *	This method sets the Load timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setLoadDttm(Timestamp loadDttm) {
    	this.loadDttm		= loadDttm;
    }

    /**
     *	This method sets the Offload timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setOffloadDttm(Timestamp offloadDttm) {
    	this.offloadDttm	= offloadDttm;
    }

    /**
     *	This method sets the Mount timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setMountDttm(Timestamp mountDttm) {
    	this.mountDttm		= mountDttm;
    }

    /**
     *	This method sets the Arrival timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setArrDttm(Timestamp arrDttm) {
    	this.arrDttm		= arrDttm;
    }

    /**
     *	This method sets the Exit timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setExitDttm(Timestamp exitDttm) {
    	this.exitDttm		= exitDttm;
    }

    /**
     *	This method sets the Change of purpose timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setChangePurpDttm(Timestamp changePurpDttm) {
    	this.changePurpDttm	= changePurpDttm;
    }

    /**
     *	This method sets the Direct handling indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setDirHdlgInd(String dirHdlgInd) {
    	this.dirHdlgInd		= dirHdlgInd;
    }

    /**
     *	This method sets the Chassis provided indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setChasProvInd(String chasProvInd) {
    	this.chasProvInd	= chasProvInd;
    }

    /**
     *	This method sets the Gear used
     *
     *  @param		String
     *  @return 	void
     */
    public void setGearUsed(String gearUsed) {
    	this.gearUsed		= gearUsed;
    }

    /**
     *	This method sets the Plugin temperature
     *
     *  @param		String
     *  @return 	void
     */
    public void setPluginTemp(String pluginTemp) {
    	this.pluginTemp		= pluginTemp;
    }

    /**
     *	This method sets the Plugin timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setPluginDttm(Timestamp pluginDttm) {
    	this.pluginDttm 	= pluginDttm;
    }

    /**
     *	This method sets the Unplug timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setUnplugDttm(Timestamp unplugDttm) {
    	this.unplugDttm		= unplugDttm;
    }

    /**
     *	This method sets the UC handling time
     *
     *  @param		Integer
     *  @return 	void
     */
    public void setUcHandlingDur(Integer ucHandlingDur) {
    	this.ucHandlingDur	= ucHandlingDur;
    }

    /**
     *	This method sets the Athwartship indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setAthwartshipInd(String athwartshipInd) {
        this.athwartshipInd	= athwartshipInd;
    }

    /**
     *	This method sets the Bill vessel indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setBillVslInd(String billVslInd) {
    	this.billVslInd		= billVslInd;
    }

    /**
     *	This method sets the Bill yard indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setBillYdInd(String billYdInd) {
    	this.billYdInd		= billYdInd;
    }

    /**
     *	This method sets the Process QC incentive indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setProcQcIncentive(String procQcIncentive) {
    	this.procQcIncentive= procQcIncentive;
    }

    /**
     *	This method sets the Process YC incentive indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setProcYcIncentive(String procYcIncentive) {
    	this.procYcIncentive= procYcIncentive;
    }

    /**
     *	This method sets the Process PM incentive indicator
     *
     *  @param		String
     *  @return 	void
     */
    public void setProcPmIncentive(String procPmIncentive) {
    	this.procPmIncentive= procPmIncentive;
    }

    /**
     *	This method sets the Last modified user ID
     *
     *  @param		String
     *  @return 	void
     */
    public void setLastModifyUserId(String lastModifyUserId) {
    	this.lastModifyUserId= lastModifyUserId;
    }

    /**
     *	This method sets the Last modified timestamp
     *
     *  @param		Timestamp
     *  @return 	void
     */
    public void setLastModifyDttm(Timestamp lastModifyDttm) {
    	this.lastModifyDttm	= lastModifyDttm;
    }

	public boolean isModified(CntrEventLogValueObject cntrEventLogValueObject) {
        CntrEventLogValueObject origVO  = new CntrEventLogValueObject();

        if (cntrEventLogValueObject.getCntrSeqNbr()         == origVO.getCntrSeqNbr()      &&
			cntrEventLogValueObject.getCntrNbr()            == origVO.getCntrNbr()         &&
			cntrEventLogValueObject.getStatus()             == origVO.getStatus()          &&
			cntrEventLogValueObject.getPrevPurpCd()         == origVO.getPrevPurpCd()      &&
			cntrEventLogValueObject.getTxnDttm()            == origVO.getTxnDttm()         &&
			cntrEventLogValueObject.getTxnCd()              == origVO.getTxnCd()           &&
			cntrEventLogValueObject.getUserId()             == origVO.getUserId()          &&
			cntrEventLogValueObject.getCraneNm()            == origVO.getCraneNm()         &&
			cntrEventLogValueObject.getCraneOprId()         == origVO.getCraneOprId()      &&
			cntrEventLogValueObject.getWtaId()              == origVO.getWtaId()           &&
			cntrEventLogValueObject.getPmType()             == origVO.getPmType()          &&
			cntrEventLogValueObject.getPmNm()               == origVO.getPmNm()            &&
			cntrEventLogValueObject.getPmOprId()            == origVO.getPmOprId()         &&
			cntrEventLogValueObject.getIsoSizeTypeCd()      == origVO.getIsoSizeTypeCd()   &&
			cntrEventLogValueObject.getSizeFt()             == origVO.getSizeFt()          &&
			cntrEventLogValueObject.getCatCd()              == origVO.getCatCd()           &&
			cntrEventLogValueObject.getDeclrWt()            == origVO.getDeclrWt()         &&
			cntrEventLogValueObject.getMeasureWt()          == origVO.getMeasureWt()       &&
			cntrEventLogValueObject.getPurpCd()             == origVO.getPurpCd()          &&
			cntrEventLogValueObject.getCntrOprCd()          == origVO.getCntrOprCd()       &&
			cntrEventLogValueObject.getHaulCd()             == origVO.getHaulCd()          &&
			cntrEventLogValueObject.getLoloPartyInd()       == origVO.getLoloPartyInd()    &&
			cntrEventLogValueObject.getDiscSlotOprCd()      == origVO.getDiscSlotOprCd()   &&
			cntrEventLogValueObject.getLoadSlotOprCd()      == origVO.getLoadSlotOprCd()   &&
			cntrEventLogValueObject.getRenomSlotOprCd()     == origVO.getRenomSlotOprCd()  &&
			cntrEventLogValueObject.getDiscVvCd()           == origVO.getDiscVvCd()        &&
			cntrEventLogValueObject.getLdVvCd()             == origVO.getLdVvCd()          &&
			cntrEventLogValueObject.getRenomVvCd()          == origVO.getRenomVvCd()       &&
			cntrEventLogValueObject.getPsrc()               == origVO.getPsrc()            &&
			cntrEventLogValueObject.getPload()              == origVO.getPload()           &&
			cntrEventLogValueObject.getPdisc()              == origVO.getPdisc()           &&
			cntrEventLogValueObject.getPdest()              == origVO.getPdest()           &&
			cntrEventLogValueObject.getDgInd()              == origVO.getDgInd()           &&
			cntrEventLogValueObject.getImdgClCd()           == origVO.getImdgClCd()        &&
			cntrEventLogValueObject.getRefrInd()            == origVO.getRefrInd()         &&
			cntrEventLogValueObject.getUcInd()              == origVO.getUcInd()           &&
			cntrEventLogValueObject.getOverSzInd()          == origVO.getOverSzInd()       &&
			cntrEventLogValueObject.getIntergatewayInd()    == origVO.getIntergatewayInd() &&
			cntrEventLogValueObject.getDiscDttm()           == origVO.getDiscDttm()        &&
			cntrEventLogValueObject.getLoadDttm()           == origVO.getLoadDttm()        &&
			cntrEventLogValueObject.getOffloadDttm()        == origVO.getOffloadDttm()     &&
			cntrEventLogValueObject.getMountDttm()          == origVO.getMountDttm()       &&
			cntrEventLogValueObject.getArrDttm()            == origVO.getArrDttm()         &&
			cntrEventLogValueObject.getExitDttm()           == origVO.getExitDttm()        &&
			cntrEventLogValueObject.getChangePurpDttm()     == origVO.getChangePurpDttm()  &&
			cntrEventLogValueObject.getDirHdlgInd()         == origVO.getDirHdlgInd()      &&
			cntrEventLogValueObject.getChasProvInd()        == origVO.getChasProvInd()     &&
			cntrEventLogValueObject.getGearUsed()           == origVO.getGearUsed()        &&
			cntrEventLogValueObject.getPluginTemp()         == origVO.getPluginTemp()      &&
			cntrEventLogValueObject.getPluginDttm()         == origVO.getPluginDttm()      &&
			cntrEventLogValueObject.getUnplugDttm()         == origVO.getUnplugDttm()      &&
			cntrEventLogValueObject.getUcHandlingDur()      == origVO.getUcHandlingDur()   &&
			cntrEventLogValueObject.getAthwartshipInd()     == origVO.getAthwartshipInd()  &&
			cntrEventLogValueObject.getBillVslInd()         == origVO.getBillVslInd()      &&
			cntrEventLogValueObject.getBillYdInd()          == origVO.getBillYdInd()       &&
			cntrEventLogValueObject.getProcQcIncentive()    == origVO.getProcQcIncentive() &&
			cntrEventLogValueObject.getProcYcIncentive()    == origVO.getProcYcIncentive() &&
			cntrEventLogValueObject.getProcPMIncentive()    == origVO.getProcPMIncentive() &&
			cntrEventLogValueObject.getLastModifyUserId()   == origVO.getLastModifyUserId()&&
			cntrEventLogValueObject.getLastModifyDttm()     == origVO.getLastModifyDttm()  &&
			//START: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
			cntrEventLogValueObject.getMiscSeqNbr()     == origVO.getMiscSeqNbr()  &&
			cntrEventLogValueObject.getVvCd()     == origVO.getVvCd()  &&
			cntrEventLogValueObject.getBillInd()     == origVO.getBillInd()  &&
			cntrEventLogValueObject.getRefNbr()     == origVO.getRefNbr()  &&
			cntrEventLogValueObject.getPDisc1()     == origVO.getPDisc1()  &&
			cntrEventLogValueObject.getPeriodiSRInd() == origVO.getPeriodiSRInd() &&
			cntrEventLogValueObject.getCodArrTime() == origVO.getCodArrTime() &&
			cntrEventLogValueObject.getStoreRentDays() == origVO.getStoreRentDays() )
        	//END: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
            return false;
		else
			return true;
    }

	//START: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006
	public String getBillInd() {
		return billInd;
	}

	public void setBillInd(String billInd) {
		this.billInd = billInd;
	}

	public Long getMiscSeqNbr() {
		return miscSeqNbr;
	}

	public void setMiscSeqNbr(Long miscSeqNbr) {
		this.miscSeqNbr = miscSeqNbr;
	}

	public String getPDisc1() {
		return pDisc1;
	}

	public void setPDisc1(String disc1) {
		pDisc1 = disc1;
	}

	public String getRefNbr() {
		return refNbr;
	}

	public void setRefNbr(String refNbr) {
		this.refNbr = refNbr;
	}

	public String getVvCd() {
		return vvCd;
	}

	public void setVvCd(String vvCd) {
		this.vvCd = vvCd;
	}
	//END: CR-CAB-20050823-02 - Added by Valli - 13-Jun-2006

    public String getPeriodiSRInd() {
        return periodiSRInd;
    }

    public void setPeriodiSRInd(String periodiSRInd) {
        this.periodiSRInd = periodiSRInd;
    }

    public int getStoreRentDays() {
        return storeRentDays;
    }

    public void setStoreRentDays(int storeRentDays) {
        this.storeRentDays = storeRentDays;
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

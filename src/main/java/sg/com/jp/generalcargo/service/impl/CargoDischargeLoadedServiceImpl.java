package sg.com.jp.generalcargo.service.impl;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import sg.com.jp.generalcargo.dao.GBCCCargoRepository;
import sg.com.jp.generalcargo.domain.Berthing;
import sg.com.jp.generalcargo.domain.CargoOprSummInfoValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOprVO;
import sg.com.jp.generalcargo.domain.GbccRulePara;
import sg.com.jp.generalcargo.domain.GbccViewVvStevedore;
import sg.com.jp.generalcargo.domain.GbccVslProd;
import sg.com.jp.generalcargo.domain.GbccVslProdId;
import sg.com.jp.generalcargo.domain.JMSMsgValueObject;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoDischargeLoadedService;
import sg.com.jp.generalcargo.service.JmsIntegrationService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service
public class CargoDischargeLoadedServiceImpl implements CargoDischargeLoadedService {

	@Autowired
	private GBCCCargoRepository cargoDao;
	
	protected ApplicationContext ctx;

	private static final Log log = LogFactory.getLog(CargoDischargeLoadedServiceImpl.class);

	protected int decodeTime(String s) {
		SimpleDateFormat f = new SimpleDateFormat("HHmm");
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		f.setTimeZone(utcTimeZone);
		f.setLenient(false);
		ParsePosition p = new ParsePosition(0);
		Date d = f.parse(s, p);
		return (int) d.getTime();
	}

	protected Date truncateDate(Date d) {
		GregorianCalendar gc1 = new GregorianCalendar();
		gc1.clear();
		gc1.setTime(d);
		int year = gc1.get(Calendar.YEAR), month = gc1.get(Calendar.MONTH), day = gc1.get(Calendar.DAY_OF_MONTH);
		GregorianCalendar gc2 = new GregorianCalendar(year, month, day);
		return gc2.getTime();
	}

	@Override
	public List<VesselCall> cc_getCargoOprVesselCall(String custCd) throws BusinessException {
		return cargoDao.getCargoOprVesselCall(custCd);
	}

	@Override
	public List<StevedoreCompany> cc_getCargoOprStevedore() throws BusinessException {
		return cargoDao.getCargoOprStevedore();
	}

	@Override
	public List<String> cc_getCargoOprBerth(String custCd) throws BusinessException {
		return cargoDao.getCargoOprBerth(custCd);
	}

	@Override
	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException {
		return cargoDao.getMiscTypeCode(catCd);
	}

	@Override
	public String cc_checkCargoOprAdd(String vvCd, String stevCd, String coCd, String shiftCd, Date shiftDttm)
			throws BusinessException {
		log.info("START: cc_checkCargoOprAdd "+" vvCd:"+ CommonUtility.deNull(vvCd) +" stevCd:"+ CommonUtility.deNull(stevCd) 
		+" coCd:"+ CommonUtility.deNull(coCd) +" shiftCd:"+ CommonUtility.deNull(shiftCd) + "shiftDttm:"+shiftDttm );
		
		List<GbccViewVvStevedore> stev = cargoDao.getViewVvStevedoreByVvCd(vvCd, coCd);

		if (stev.size() == 0)
			return ConstantUtil.CargoOprErr_NOSTEV;

		if (!stevCd.equalsIgnoreCase("")) {
			boolean stevokey = false;
			for (int i = 0; i < stev.size(); i++) {
				GbccViewVvStevedore s = (GbccViewVvStevedore) stev.get(i);
				if (s.getId().getStevCoCd().equalsIgnoreCase(stevCd)) {
					stevokey = true;
					break;
				}
			}
			if (!stevokey)
				return ConstantUtil.CargoOprErr_NOSTEV;
		} else {
			GbccViewVvStevedore s = (GbccViewVvStevedore) stev.get(0);
			stevCd = s.getId().getStevCoCd();
		}

		Berthing firstBerthing = cargoDao.getFirstBerthing(vvCd);
		if (firstBerthing == null)
			return ConstantUtil.CargoOprErr_NOFIRSTACTIVITY;

		if (firstBerthing.getGbFirstActDttm() == null)
			return ConstantUtil.CargoOprErr_NOFIRSTACTIVITY;

		Date firstActivity = firstBerthing.getGbFirstActDttm();
		Date lastActivity = firstBerthing.getGbLastActDttm();

		GbccCargoOprVO cVO = cargoDao.getCargoOprById(vvCd, stevCd, null, shiftCd, shiftDttm, coCd);
		if (cVO == null)
			return ConstantUtil.CargoOprErr_NOOPENBAL;

		// check if shiftDttm is between first and last activity (current date)
		List<MiscTypeCode> lstMtc = cargoDao.getMiscTypeCode(ConstantUtil.MISCTYPECD_SHIFT_STEV);
		MiscTypeCode oMst = null;
		MiscTypeCode firstMtc = (MiscTypeCode) lstMtc.get(0);

		for (int i = 0; i < lstMtc.size(); i++) {
			MiscTypeCode oVO = (MiscTypeCode) lstMtc.get(i);
			if (oVO.getId().getMiscTypeCd().equalsIgnoreCase(shiftCd)) {
				oMst = oVO;
				break;
			}
		}

		String sFirstTime = firstMtc.getMiscTypeNm();
		String firstStartTime = sFirstTime.substring(0, 4);
		String firstEndTime = sFirstTime.substring(5, 9);

		String sTime = oMst.getMiscTypeNm();
		String startTime = sTime.substring(0, 4);
		String endTime = sTime.substring(5, 9);

		Date startDttm = truncateDate(shiftDttm);
		Date endDttm = startDttm;

		if (Integer.parseInt(startTime) < Integer.parseInt(firstStartTime)) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(startDttm);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			startDttm = cal.getTime();
		}

		if (Integer.parseInt(endTime) < Integer.parseInt(firstEndTime)) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(endDttm);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			endDttm = cal.getTime();
		}
		startDttm = new Date(startDttm.getTime() + decodeTime(startTime));
		endDttm = new Date(endDttm.getTime() + decodeTime(endTime));

		log.info("cc_checkCargoOprAdd->startDttm->getTime::" + startDttm.getTime());
		log.info("cc_checkCargoOprAdd->endDttm->getTime::" + endDttm.getTime());
		log.info("cc_checkCargoOprAdd->firstActivity->getTime::" + firstActivity.getTime());

		if (startDttm.getTime() < firstActivity.getTime()) {
			if ((startDttm.getTime() <= firstActivity.getTime()) && (endDttm.getTime() > firstActivity.getTime())) {

			} else {
				return ConstantUtil.CargoOprErr_SHIFTLESSTHANFIRSTACT;
			}
		}

		if (!cc_checkCargoOprIsPrevShiftExist(vvCd, stevCd, shiftCd, shiftDttm))
			return ConstantUtil.CargoOprErr_NOPREVSHIFT;

		log.info("cc_checkCargoOprAdd->startDttm::" + startDttm);
		log.info("cc_checkCargoOprAdd->endDttm::" + endDttm);
		log.info("cc_checkCargoOprAdd->firstActivity::" + firstActivity);

		// if (startDttm.before(firstActivity))
		// return ConstantUtil.CargoOprErr_SHIFTLESSTHANFIRSTACT;

		Date dttm = new Date();

		if (startDttm.getTime() > dttm.getTime())
			return ConstantUtil.CargoOprErr_SHIFTGREATERTHANCURRENTTIME;

		if (lastActivity != null)
			if (startDttm.getTime() >= lastActivity.getTime())
				return ConstantUtil.CargoOprErr_SHIFTGREATERTHANLASTACT;

		/*
		 * if (firstActivity.getTime() >= startDttm.getTime() && firstActivity.getTime()
		 * < endDttm.getTime()) { // } else { if
		 * (!cc_checkCargoOprIsPrevShiftExist(vvCd, stevCd, shiftCd, shiftDttm)) return
		 * ConstantUtil.CargoOprErr_NOPREVSHIFT; }
		 */

		return "";
	}

	public boolean cc_checkCargoOprIsPrevShiftExist(String vvCd, String stevCd, String shiftCd, Date shiftDttm)
			throws BusinessException {
		log.info("START: cc_checkCargoOprIsPrevShiftExist "+" vvCd:"+ CommonUtility.deNull(vvCd) +" stevCd:"+ CommonUtility.deNull(stevCd) 
		+" shiftCd:"+ CommonUtility.deNull(shiftCd) + "shiftDttm:"+shiftDttm );
		
		List<GbccCargoOprVO> oVOs = cargoDao.getCargoOprByShiftDate(vvCd, stevCd, shiftDttm);
		if (oVOs == null)
			return true;

		if (oVOs.size() == 0)
			return true;

		// boolean isPrev = true;
		for (int i = 0; i < oVOs.size(); i++) {
			GbccCargoOprVO oVO = (GbccCargoOprVO) oVOs.get(i);
			if (shiftCd.equalsIgnoreCase(oVO.getShiftCd())) {
				if (i == 0)
					return true;
				else {
					GbccCargoOprVO prevVO = (GbccCargoOprVO) oVOs.get(i - 1);
					if (prevVO.getId() == null)
						// return false;
						return true;
					else
						return true;
				}
			}
			// if (oVO.getId() == null)
			// return false;
			// else
			// isPrev = true;
		}
		return true;

	}

	@Override
	public GbccCargoOprVO cc_getCargoOprById(String vvCd, String stevCd, Date crDttm, String shiftCd, Date shiftDttm,
			String custCd) throws BusinessException {
		return cargoDao.getCargoOprById(vvCd, stevCd, crDttm, shiftCd, shiftDttm, custCd);
	}

	@Override
	public List<GbccCargoOprVO> cc_getCargoOpr(String vvCd, String berthNo, String CustCode, String shiftCd,
			Date shiftDttm, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {

		return cargoDao.getCargoOpr(vvCd, berthNo, CustCode, shiftCd, shiftDttm, sortBy, criteria, needAllData);
	}

	protected Long cc_calculateProductivity(Date startDttm, Date endDttm, Integer totalCompleted) {

		log.info("cc_calculateProductivity->totalCompleted::" + totalCompleted + "startDttm:"+ startDttm + "endDttm:"+ endDttm);
		if (startDttm == null)
			return new Long(0);

		if (endDttm == null)
			return new Long(0);

		// long totalWorkingHr = (((endDttm.getTime() -
		// startDttm.getTime())/1000)/60)/60;
		double totalWorkingHr = endDttm.getTime() - startDttm.getTime();
		totalWorkingHr = (double) ((totalWorkingHr / 1000) / 60) / 60;

		log.info("cc_calculateProductivity->totalWorkingHr::" + totalWorkingHr);
		if (totalWorkingHr == 0)
			return new Long(0);
		double perHrProdDouble = (double) (totalCompleted.intValue()
				/ CommonUtility.formatNumToScale(totalWorkingHr, 2));

		long perHrProd = (long) perHrProdDouble;
		log.info("STime=" + startDttm + ";ETime=" + endDttm + " PerHrProdDoub=" + perHrProdDouble + ";PerHrProd="
				+ perHrProd);

		return new Long(perHrProd);
	}

	protected Date cc_getGeneralCargoProjectedETU(String vvCd, Integer totalTon, Integer totalCompleted,
			Date completionDate, Long prodRate) throws BusinessException {
		Date projectedETU = null;

		if (totalTon == null)
			return null;

		if (totalCompleted == null)
			return null;

		if (completionDate == null)
			return null;

		if (prodRate == null)
			return null;

		long perHrThroughput = prodRate.longValue();
		if (perHrThroughput == 0)
			return null;

		double totalRequiredHr = (double) (totalTon.intValue() - totalCompleted.intValue()) / perHrThroughput;

		long paraAllowance = cc_getGbccRuleParaMin(ConstantUtil.RULEPARA_CD_GCAllowance);

		Calendar cal = Calendar.getInstance();
		cal.setTime(completionDate);
		cal.add(Calendar.HOUR, (int) totalRequiredHr);
		cal.add(Calendar.MINUTE, (int) paraAllowance);

		projectedETU = cal.getTime();
		log.info("VVCd=" + vvCd + ";TotTon=" + totalTon + ";TotComp=" + totalCompleted + ";CompDttm=" + completionDate
				+ ";ProdGrossRate=" + prodRate + ";TotReqHr=" + totalRequiredHr + ";ProjETU=" + projectedETU
				+ ";ParaAllow=" + paraAllowance);
		return projectedETU;
	}

	protected long cc_getGbccRuleParaMin(String paraCd) throws BusinessException {
		GbccRulePara oVO = cargoDao.getGbccRuleParaById(paraCd);
		log.info("START: cc_getGbccRuleParaMin "+" paraCd:"+CommonUtility.deNull(paraCd));
		long paraMin = 0;
		if (oVO != null) {
			paraMin = Long.parseLong(oVO.getRuleParaValue());
			if (oVO.getRuleParaUnit().equalsIgnoreCase(ConstantUtil.PARAUNIT_Hours)) {
				paraMin = paraMin * 60;
			}
		}
		log.info("END: *** cc_getGbccRuleParaMin Result *****" + paraMin);
		return paraMin;
	}

	@Override
	public GbccCargoOprVO cc_getCargoOprById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return cargoDao.getCargoOprById(vvCd, stevCd, crDttm);
	}

	@Override
	public GbccCargoOprVO cc_getCargoOprByVvCdCustCd(String vvCd, String custCd) throws BusinessException {

		return cargoDao.getCargoOprByVvCdCustCd(vvCd, custCd);
	}

	@Override
	public GbccVslProd cc_getVslProdById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return cargoDao.getVslProdById(vvCd, stevCd, crDttm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cc_persistCargoOpr(GbccCargoOprVO transientObject) throws BusinessException {
		boolean result=false;
		try {
			log.info("START: cc_persistCargoOpr SERVICE GbccCargoOprVO" + transientObject.toString());
			List<MiscTypeCode> lstMtc = cargoDao.getMiscTypeCode(ConstantUtil.MISCTYPECD_SHIFT_STEV);
			MiscTypeCode oMst = null;
			// oMst = CargoDao.getMiscTypeCodeById(ConstantUtil.MISCTYPECD_SHIFT_STEV,
			// transientObject.getShiftCd());
			MiscTypeCode firstMtc = (MiscTypeCode) lstMtc.get(0);

			for (int i = 0; i < lstMtc.size(); i++) {
				MiscTypeCode oVO = (MiscTypeCode) lstMtc.get(i);
				if (oVO.getMiscTypeCd().equalsIgnoreCase(transientObject.getShiftCd())) {
					// oMst = CargoDao.getMiscTypeCodeById(ConstantUtil.MISCTYPECD_SHIFT_STEV,
					// transientObject.getShiftCd());
					oMst = oVO;
					break;
				}
			}

			String sFirstTime = firstMtc.getMiscTypeNm();
			String firstStartTime = sFirstTime.substring(0, 4);
			String firstEndTime = sFirstTime.substring(5, 9);

			String sTime = oMst.getMiscTypeNm();
			String startTime = sTime.substring(0, 4);
			String endTime = sTime.substring(5, 9);

			Date startDttm = truncateDate(transientObject.getShiftDttm());
			Date endDttm = startDttm;

			if (Integer.parseInt(startTime) < Integer.parseInt(firstStartTime)) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(startDttm);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				startDttm = cal.getTime();
			}

			if (Integer.parseInt(endTime) < Integer.parseInt(firstEndTime)) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(endDttm);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				endDttm = cal.getTime();
			}
			startDttm = new Date(startDttm.getTime() + decodeTime(startTime));
			endDttm = new Date(endDttm.getTime() + decodeTime(endTime));

			// transientObject.setShiftDttm(startDttm);
			transientObject.setShiftStartDttm(startDttm);
			transientObject.setShiftEndDttm(endDttm);

			if (!cargoDao.saveCargoOpr(transientObject))
				result= false;

				String vvCd = transientObject.getId().getVvCd();
				cc_triggerProcessQueue(ConstantUtil.JMSMsgType_CARGODISCLOADINFO_UPDATED, "", "", vvCd);

			if (cc_calculateCargoOprProductivity(transientObject))
				result= true;
			else
				result= false;
			
		} catch (BusinessException e) {
			log.info("Exception cc_persistCargoOpr : ", e);
			result= false;
		} catch (Exception e) {
			log.info("Exception cc_persistCargoOpr : ", e);
			result= false;
		}finally {
			log.info("cc_persistCargoOpr Result: "+result);
		}
		return result;
	}

	protected boolean cc_calculateCargoOprProductivity(GbccCargoOprVO oVO) throws BusinessException {
		try {
			log.info("START: cc_calculateCargoOprProductivity SERVICE GbccCargoOprVO" + oVO.toString());
			String vvCd = oVO.getId().getVvCd();
			String stevCd = oVO.getId().getStevCoCd();
			Date crDttm = oVO.getCreateDttm();

			Berthing firstBerthing = cargoDao.getFirstBerthing(vvCd);
			Berthing lastBerthing = cargoDao.getLastBerthing(vvCd);
			CargoOprSummInfoValueObject oSummVO = cargoDao.getCargoOprSumm(vvCd);

			Integer totalCompleted = oSummVO.getTotalComp();
			Integer totalTon = oSummVO.getTotalOpen();

			// Long prodRate = cc_getGeneralCargoProductivity(firstBerthing,
			// totalCompleted);
			// Long prodGrossRate = cc_getGeneralCargoProductivityGross(firstBerthing,
			// totalCompleted);

			// Date completionDttm = CargoDao.getCargoOprLastShifDttm(vvCd);
			// Date lastActivity = firstBerthing.getGbLastActDttm();
			Date completionDttm = oSummVO.getLastActDttm();

			Date atuDttm = completionDttm;
			if (lastBerthing.getAtuDttm() != null)
				atuDttm = lastBerthing.getAtuDttm();

			Date startDttm = oSummVO.getFirstActDttm();

			Date colDttmAct = lastBerthing.getGbColDttm();
			Date codDttmAct = lastBerthing.getGbCodDttm();
			Date actualCompletionDttm = completionDttm;

			if (codDttmAct != null && colDttmAct != null) {
				if (CommonUtility.isStartBeforeEndDate(colDttmAct, codDttmAct)) {
					actualCompletionDttm = codDttmAct;
				} else {
					actualCompletionDttm = colDttmAct;
				}
			} else if (colDttmAct != null && codDttmAct == null) {
				actualCompletionDttm = colDttmAct;
			} else if (codDttmAct != null && colDttmAct == null) {
				actualCompletionDttm = codDttmAct;
			}

			// long totalWorkHr = 0;
			double totalWorkHr = 0.00;
			if (actualCompletionDttm != null) {
				try {
					// totalWorkHr = completionDttm.getTime() - startDttm.getTime();//Bhuvana
					totalWorkHr = actualCompletionDttm.getTime() - (startDttm != null ? startDttm.getTime() : 0);
					totalWorkHr = (double) ((totalWorkHr / 1000) / 60) / 60;
					log.info("VVcd= " + vvCd + ";StevCd=" + stevCd + ";CrDttm=" + ";Stime=" + startDttm + ";EndDttm="
							+ actualCompletionDttm + "TotWrkHr=" + totalWorkHr);
				} catch (Exception e) {
					log.info("cc_calculateCargoOprProductivity totalWorkHr Exception ", e);
				}
			}

			// long totalPortHr = 0;
			double totalPortHr = 0.00;
			if (atuDttm != null) {
				try {
					totalPortHr = atuDttm.getTime() - firstBerthing.getAtbDttm().getTime();
					totalPortHr = (double) ((totalPortHr / 1000) / 60) / 60;
					log.info("VVcd= " + vvCd + ";StevCd=" + stevCd + ";CrDttm=" + ";Stime=" + firstBerthing.getAtbDttm()
							+ ";EndDttm=" + atuDttm + "TotPortHr=" + totalPortHr);
				} catch (Exception e) {
					log.info("cc_calculateCargoOprProductivity totalPortHr Exception::", e);
				}
			}

			// Long prodRate = cc_calculateProductivity(startDttm, completionDttm,
			// totalCompleted);
			// Long prodGrossRate = cc_calculateProductivity(firstBerthing.getAtbDttm(),
			// completionDttm, totalCompleted);
			Long prodRate = oSummVO.getProdRate();
			Long prodGrossRate = oSummVO.getProdRateGross(); // Bhuvana

			Date codDttm = firstBerthing.getGbCodDttm();
			if (codDttm == null)
				codDttm = completionDttm;

			Date colDttm = firstBerthing.getGbColDttm();
			if (colDttm == null)
				colDttm = completionDttm;

			long totalDiscHr = 0;
			if (codDttm != null) {
				try {
					totalDiscHr = (((codDttm.getTime() - (startDttm != null ? startDttm.getTime() : 0)) / 1000) / 60) / 60;
					log.info("VVcd= " + vvCd + ";StevCd=" + stevCd + ";CrDttm=" + ";Stime=" + startDttm + ";EndDttm="
							+ codDttm + "TotDiscHr=" + totalDiscHr);
				} catch (Exception e) {
					log.info("cc_calculateCargoOprProductivity totalDiscHr Exception", e);
				}
			}

			long totalLoadHr = 0;
			if (colDttm != null) {
				try {
					totalLoadHr = (((colDttm.getTime() - (startDttm != null ? startDttm.getTime() : 0)) / 1000) / 60) / 60;
					log.info("VVcd= " + vvCd + ";StevCd=" + stevCd + ";CrDttm=" + ";Stime=" + startDttm + ";EndDttm="
							+ colDttm + "TotLoadHr=" + totalLoadHr);
				} catch (Exception e) {
					log.info("cc_calculateCargoOprProductivity totalLoadHr Exception", e);
				}
			}

			GbccVslProd oVOProd = cargoDao.getVslProdById(vvCd, stevCd, crDttm);
			if (oVOProd == null) {
				oVOProd = new GbccVslProd();
				GbccVslProdId oVOProdId = new GbccVslProdId();
				oVOProdId.setVvCd(oVO.getId().getVvCd());
				oVOProdId.setCreateDttm(oVO.getId().getCreateDttm());
				oVOProdId.setStevCoCd(oVO.getId().getStevCoCd());
				oVOProd.setId(oVOProdId);

			}
			oVOProd.setStevCoCd(stevCd);
			oVOProd.setVvCd(oVO.getId().getVvCd());
			oVOProd.setCreateDttm(crDttm);
			oVOProd.setShiftCd(oVO.getShiftCd());
			oVOProd.setShiftDttm(oVO.getShiftDttm());
			oVOProd.setShiftStartDttm(oVO.getShiftStartDttm());
			oVOProd.setShiftEndDttm(oVO.getShiftEndDttm());
			oVOProd.setVslType(ConstantUtil.GBCC_CargoType_General);

			// oVOProd.setOpenBal(new Integer(totalTon.intValue()));
			// oVOProd.setOpenBalDisc(new Integer(oSummVO.getTotalOpenDisc().intValue()));
			// oVOProd.setOpenBalLoad(new Integer(oSummVO.getTotalOpenLoad().intValue()));
			CargoOprSummInfoValueObject oPreCompleted = cargoDao.getCargoOprPrevCompleted(vvCd, oVO.getId().getStevCoCd(),
					oVO.getShiftCd(), oVO.getShiftDttm());
			int TotalDiscBal = oSummVO.getTotalOpenDisc().intValue() - oPreCompleted.getTotalCompDisc().intValue();
			int TotalLoadBal = oSummVO.getTotalOpenLoad().intValue() - oPreCompleted.getTotalCompLoad().intValue();

			oVOProd.setOpenBal(new Integer(TotalDiscBal + TotalLoadBal));
			oVOProd.setOpenBalDisc(new Integer(TotalDiscBal));
			oVOProd.setOpenBalLoad(new Integer(TotalLoadBal));

			oVOProd.setWorkStartDttm(startDttm);
			oVOProd.setTotWrkHr(new BigDecimal(totalWorkHr));
			oVOProd.setTotPortHr(new BigDecimal(totalPortHr));
			oVOProd.setTotDiscHr(new Integer((int) totalDiscHr));

			oVOProd.setTotLoadHr(new Integer((int) totalLoadHr));
			oVOProd.setTotCompleted(new Integer(oSummVO.getTotalComp().intValue()));
			oVOProd.setTotDiscCompleted(new Integer(oSummVO.getTotalCompDisc().intValue()));
			oVOProd.setTotLoadCompleted(new Integer(oSummVO.getTotalCompLoad().intValue()));

			int totalBal = oSummVO.getTotalOpen().intValue() - oSummVO.getTotalComp().intValue();
			oVOProd.setBalTotal(new Integer(totalBal));

			totalBal = oSummVO.getTotalOpenDisc().intValue() - oSummVO.getTotalCompDisc().intValue();
			oVOProd.setBalDisc(new Integer(totalBal));

			totalBal = oSummVO.getTotalOpenLoad().intValue() - oSummVO.getTotalCompLoad().intValue();
			oVOProd.setBalLoad(new Integer(totalBal));

			// Date projectedETU = cc_getGeneralCargoProjectedETU(vvCd, totalTon,
			// totalCompleted, completionDttm, prodRate);Bhuvana
			Date projectedETU = cc_getGeneralCargoProjectedETU(vvCd, totalTon, totalCompleted, completionDttm,
					prodGrossRate);
			Date etuDttm = lastBerthing.getEtuDttm();

			oVOProd.setProdRateNett(new Integer(prodRate.intValue()));
			oVOProd.setProdRateGross(new Integer(prodGrossRate.intValue()));
			prodRate = cc_calculateProductivity(startDttm, codDttm, oSummVO.getTotalCompDisc());
			oVOProd.setProdRateDisc(new Integer(prodRate.intValue()));
			prodRate = cc_calculateProductivity(startDttm, colDttm, oSummVO.getTotalCompLoad());
			oVOProd.setProdRateLoad(new Integer(prodRate.intValue()));
			oVOProd.setProjectedEtu(projectedETU);
			oVOProd.setEtuDttm(etuDttm);

			oVOProd.setCodDttm(firstBerthing.getGbCodDttm());
			oVOProd.setColDttm(firstBerthing.getGbColDttm());

			long variationHr = 0;
			if (projectedETU != null && etuDttm != null) {
				variationHr = (((projectedETU.getTime() - etuDttm.getTime()) / 1000) / 60) / 60;
			}
			oVOProd.setEtuVariationHr(new Integer((int) variationHr));
			if (variationHr > 0 || projectedETU == null)
				oVOProd.setAffectPlannedEtu(ConstantUtil.YESNO_IND_YES);
			else
				oVOProd.setAffectPlannedEtu(ConstantUtil.YESNO_IND_NO);

			oVOProd.setLastModifyUserId(oVO.getLastModifyUserId());
			return cargoDao.saveVslProd(oVOProd);
		} catch (BusinessException e) {
			log.info("Exception cc_calculateCargoOprProductivity : ", e);
			return false;
		} catch (Exception e) {
			log.info("Exception cc_calculateCargoOprProductivity : ", e);
			return false;
		}
	}

	@Override
	public GbccVslProd cc_getVslProdById(String vvCd, String stevCd, String shiftCd, Date shiftDttm)
			throws BusinessException {
		return cargoDao.getVslProdById(vvCd, stevCd, shiftCd, shiftDttm);
	}
	
	@Override
	public boolean cc_persistVslProd(GbccVslProd transientObject) throws BusinessException {
		return cargoDao.saveVslProd(transientObject);
	}
	
	private void cc_delayJMSTrigger() {
		try {
		  // Using Thread.sleep() we can add delay in our application in
		  // a millisecond time. For the example below the program will
		  // take a deep breath for half second before continue to print
		  // the next value of the loop.
		  Thread.sleep(1000);
		                 
		  // The Thread.sleep() need to be executed inside a try-catch
		  // block and we need to catch the InterruptedException.
		} catch (InterruptedException ie) {
		  //ie.printStackTrace();
	      log.info("cc_delayJMSTrigger InterrupptedException::", ie);
		}
	}

public void cc_triggerProcessQueue(String sType, String sSubType, String sOpsType, Object oRef) {
	
    	try {
			this.cc_delayJMSTrigger();
			
			JMSMsgValueObject oMsg = new JMSMsgValueObject();		
			oMsg.setType(sType);
			oMsg.setSubType(sSubType);
			oMsg.setOpsType(sOpsType);
			oMsg.setCreationDttm(new Date());
			oMsg.setRefObject(oRef);
			
			JmsIntegrationService jms = (JmsIntegrationService) ctx.getBean("JmsProcessQueueServiceGateway");
			jms.triggerProcessQueue(oMsg);
			
//			log.info("stamp JMS GbccProcessQueue=>msg::" + ObjectInfo.getObjectContent(oMsg, 0));
			
		} catch (Exception e) {
			log.info("cc_triggerProcessQueue Exception::", e);
		}		
	}
}

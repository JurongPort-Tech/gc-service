package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.ProcessGBStoreRepository;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillAdjParamCOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCVO;
import sg.com.jp.generalcargo.domain.BillAdjParamOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.UserTimestampVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.DoubleMath;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.ProcessChargeException;

@Repository("ProcessGBStoreJdbcRepository")
public class ProcessGBStoreJdbcRepository implements ProcessGBStoreRepository {

	private static final Log log = LogFactory.getLog(ProcessGBStoreJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private ProcessGenericRepository processGenericRepo;
	@Autowired
	private TariffMainRepository tariffMainRepo;
	@Autowired
	private BillSupportInfoRepository billSupInfoRepo;
	@Autowired
	@Lazy
	private ProcessGBLogRepository processLogGB;
	@Autowired
	private TransactionLoggerRepo transLogRepo;
	

	// StartRegion ProcessGBStoreJdbcRepository

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->calculateStoreBillCharge()
	public  List<ChargeableBillValueObject> calculateStoreBillCharge(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws NamingException, SQLException, ProcessChargeException, Exception {
		
//		BillErrorVO billErrorValueObject = null;
		ChargeableBillValueObject chargeableBillValueObject = null;
		VesselRelatedValueObject vesselRelatedValueObject = null;
		BerthRelatedValueObject berthRelatedValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		AccountValueObject accountValueObject = null;
//			ProcessCommon processCommon = null;

		List<ChargeableBillValueObject> chargeableBillList = null;
		List<ChargeableBillValueObject> prevChargeableBillList = null;
		Timestamp varDttm = null;
		int versionNbr = 0;

		String refNbr = null;
		String discVvCd = null;
		String loadVvCd = null;
		String vvInd = null;
		String businessType = null;
		String schemeCd = null;
		String tariffMainCat = null;
		String tariffSubCat = null;
		String mvmt = null;
		String type = null;
//		String cargoType = null;
		String localLeg = null;
//		String discGateway = null;
		String billAcctNbr = null;
		String billInd = null;
		Timestamp lastModifyDttm = null;

		String varVvCd = null;
		double minTon = 0.0;
		double billTon = 0.0;
		double deriveTon = 0.0;
		double deriveTimePeriod = 0.0;
		double billTonEdo = 0.0;
		int totalPkgEdo = 0;
		int totalPkgDn = 0;
		double billTonEsn = 0.0;
		double loadTonCs = 0.0;

		// EVM Enhancements
		String edoEsnNbr = null;
		String blNbr = null;
		try {
			log.info("START calculateStoreBillCharge DAO :: generalEventLogValueObject: " + CommonUtility.deNull(generalEventLogValueObject.toString())
					+ " refInd: " + CommonUtility.deNull(refInd));
			chargeableBillValueObject = new ChargeableBillValueObject();

			refNbr = determineRefNbr(generalEventLogValueObject, refInd);
			discVvCd = generalEventLogValueObject.getDiscVvCd();
			loadVvCd = generalEventLogValueObject.getLoadVvCd();
			vvInd = generalEventLogValueObject.getVvInd();
			businessType = generalEventLogValueObject.getBusinessType();
			schemeCd = generalEventLogValueObject.getSchemeCd();
			tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
			tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();
			mvmt = generalEventLogValueObject.getMvmt();
			type = generalEventLogValueObject.getType();
//			cargoType = generalEventLogValueObject.getCargoType();
			localLeg = generalEventLogValueObject.getLocalLeg();
//			discGateway = generalEventLogValueObject.getDiscGateway();
			billAcctNbr = generalEventLogValueObject.getBillAcctNbr();
			billInd = generalEventLogValueObject.getBillInd();
			lastModifyDttm = generalEventLogValueObject.getLastModifyDttm();
			billTonEdo = generalEventLogValueObject.getBillTonEdo();
			totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
			totalPkgDn = generalEventLogValueObject.getTotalPackDn();
			billTonEsn = generalEventLogValueObject.getBillTonEsn();
			loadTonCs = generalEventLogValueObject.getLoadTonCs();
			blNbr = generalEventLogValueObject.getBlNbr();

			// EVM Enhancements
			if (localLeg.equalsIgnoreCase("IM")) {
				edoEsnNbr = generalEventLogValueObject.getEdoAsnNbr();
			} else {
				edoEsnNbr = generalEventLogValueObject.getEsnAsnNbr();
			}

			log.info("CR64 [SR] <==========================================================");
			log.info("CR64 [SR] vvInd = (" + vvInd + "), mvmt = (" + mvmt + ")," + tariffMainCat + "/" + tariffSubCat);
			log.info("CR64 [SR] schemeCd = (" + schemeCd + "), localLeg = (" + localLeg + "),disc:" + discVvCd
					+ "/load:" + loadVvCd);
			log.info("CR64 [SR] vvInd = (" + vvInd + "), mvmt = (" + mvmt + ")," + tariffMainCat + "/" + tariffSubCat);
			if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) {
				if (mvmt.trim().equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP)
						&& schemeCd.equalsIgnoreCase(ProcessChargeConst.LINER_SCHEME)
						&& (tariffMainCat.trim().equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT)
								&& (tariffSubCat.trim()
										.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL)
										|| tariffSubCat.trim().equalsIgnoreCase(
												ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)))) {
					log.info("CR64 [SR] using loadVvCd: " + loadVvCd + " instead of discVvCd: " + discVvCd);
					varVvCd = loadVvCd;
				} else {
					log.info("CR64 [SR] using discVvCd: " + discVvCd);
					varVvCd = discVvCd;
				}
			}

			else if (vvInd.equals(ProcessChargeConst.LOAD_VV_IND)) {
				varVvCd = loadVvCd;
			}
			log.info("CR64 [SR] ==========================================================>");

			vesselRelatedValueObject = new VesselRelatedValueObject();
			vesselRelatedValueObject.setVvCd(varVvCd);

			try {
				berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
						tariffMainCat);
				varDttm = berthRelatedValueObject.getAtbDttm();
				log.info("atb_dttm = " + varDttm + "...");
			} catch (Exception e) {
				log.info("Exception calculateStoreBillCharge : ", e);
				throw new BusinessException("M4201");
			}

			if (varDttm == null) {
				varDttm = UserTimestampVO.getCurrentTimestamp();
			}

			versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);
			log.info("version nbr = " + versionNbr + "...");

			schemeCd = ProcessChargeConst.NOT_APPLICABLE_SCHEME;

			if (ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO.equalsIgnoreCase(tariffSubCat)) {
				if (ProcessChargeConst.NON_LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.NON_LINER_SCHEME;
				} else if (ProcessChargeConst.BARTER_TRADER_SCHEME
						.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.BARTER_TRADER_SCHEME;
				} else if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.LCT_SCHEME;
				} else if (ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.LINER_SCHEME;
				} else if (ProcessChargeConst.WOODEN_CRAFT_SCHEME
						.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					schemeCd = ProcessChargeConst.WOODEN_CRAFT_SCHEME;
				}
			}
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
				mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
			}
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
				if (!ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
					mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
				}
			}

			String hscode = processGenericRepo.getHSCode(edoEsnNbr, localLeg, blNbr, varVvCd);
			log.info(" PRocess GB STore calculateSToreBillCharge after retrieve hsCode" + hscode + ":" + edoEsnNbr + ":"
					+ localLeg + ":" + blNbr + ":" + varVvCd);
			if (hscode != null && !hscode.equalsIgnoreCase("")) {
				businessType = processGenericRepo.getBusTypeForWFSCSR(berthRelatedValueObject, hscode);
				// reset the business type back
				generalEventLogValueObject.setBusinessType(businessType); // Added to reset the business type back
				log.info(" PRocess GB Store calculateSToreBillCharge inside if after set business TYpe:" + businessType
						+ ":" + generalEventLogValueObject.getBusinessType());
			}
			log.info(" PRocess GB Store calculateSToreBillCharge outside if after set business TYpe:" + businessType
					+ ":" + generalEventLogValueObject.getBusinessType());

			// tariffMainValueObject = processGeneric.retrievePublishTariffDtls(versionNbr,
			// tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type, "00", "00");
			tariffMainValueObject = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat,
					tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0", varDttm);

			if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
				billTon = computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
			} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
				billTon = billTonEsn;
			} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
					|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
				billTon = loadTonCs;
			}

			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				minTon = tariffMainValueObject.getMinTonnage();
				if (billTon < minTon) {
					deriveTon = minTon;
				} else {
					deriveTon = billTon;
				}
			} else {
				deriveTon = billTon;
			}

			// MCConsulting min 1 billable ton for all general cargo charges
			log.info("befor deriveTon " + deriveTon + "...");
			if (deriveTon < 1 && deriveTon > 0)
				deriveTon = 1;
			log.info("after deriveTon " + deriveTon + "...");

			// compute the derived time unit - chargeable number of time unit
			generalEventLogValueObject.setVarDttm(varDttm); // SL-CAB-20170518-01
			deriveTimePeriod = determineTimeUnit(generalEventLogValueObject, refInd);

			// To adjust the derive time period into days for unauthorised storage
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
				DoubleMath dm = new DoubleMath();
				dm.setRoundingMode(BigDecimal.ROUND_UP);

				int tierCnt = 0;
				String perHourType = "";
				for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
					// get the individual record of the tariff tier record
					TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
					perHourType = tariffTierValueObject.getPerHourType();
				}

				if (perHourType.equals("D")) {
					deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
				}
			}

			// add by hujun on 2/8/2011
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
				DoubleMath dm = new DoubleMath();
				dm.setRoundingMode(BigDecimal.ROUND_UP);

				int tierCnt = 0;
				String perHourType = "";
				for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
					// get the individual record of the tariff tier record
					TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
					perHourType = tariffTierValueObject.getPerHourType();
				}

				if (perHourType.equals("D")) {
					deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
				}
			}
			// add end

			log.info("deriveTimePeriod:  " + deriveTimePeriod);

			// set the parameters for the ChargeableBillValueObject
			chargeableBillValueObject.setRefNbr(refNbr);
			chargeableBillValueObject.setTxnDttm(lastModifyDttm);
			chargeableBillValueObject.setRefInd(refInd);
			chargeableBillValueObject.setTariffMainCatCd(tariffMainCat);
			chargeableBillValueObject.setTariffSubCatCd(tariffSubCat);
			chargeableBillValueObject.setVersionNbr(versionNbr);
			chargeableBillValueObject.setCustCd("");
			chargeableBillValueObject.setAcctNbr("");
			chargeableBillValueObject.setContractNbr("");
			chargeableBillValueObject.setContractualYr(0);
			chargeableBillValueObject.setNbrCntr(0);
			chargeableBillValueObject.setVvCd(varVvCd);
			chargeableBillValueObject.setBillInd(billInd);
			chargeableBillValueObject.setLocalLeg(localLeg);
			chargeableBillValueObject.setLastModifyUserId("SYSTEM");
			chargeableBillValueObject.setLastModifyDttm(lastModifyDttm);

			// get customer a/c & determine customer code
			if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
				accountValueObject = processGenericRepo.retrieveCashCustAcct();
			} else {
				accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);
			}
			chargeableBillValueObject.setAcctNbr(accountValueObject.getAccountNumber());
			chargeableBillValueObject.setCustCd(accountValueObject.getCustomerCode());

			// chargeableBillList = determineStoreRentBillable(chargeableBillValueObject,
			// tariffMainValueObject, deriveTon, deriveTimePeriod);
			// Added by Jade for SL-CAB-20110509-01
			int fsp = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
			chargeableBillList = determineStoreRentBillable(chargeableBillValueObject, tariffMainValueObject, deriveTon,
					deriveTimePeriod, fsp);
			// End of adding by Jade for SL-CAB-20110509-01

			// MCC check if last triggered dttm is available, if so calculate the storage
			// period for last triggered dttm
			// get ProcessGenericHome object
			Timestamp lastTriggeredDttm = null;
			if (generalEventLogValueObject.getRefInd().equals("DN")) {
				lastTriggeredDttm = processGenericRepo
						.retrievePeriodicSRLastTriggerDttm(generalEventLogValueObject.getEdoAsnNbr(), "ED", null);
			}
			if (lastTriggeredDttm != null) {
				// retrieve time period from COD to last triggered date time
				log.info("Periodic store rent lastTriggeredDttm:  " + lastTriggeredDttm);

				generalEventLogValueObject.setLastTriggerPSRDttm(lastTriggeredDttm);
				deriveTimePeriod = determineTimeUnit(generalEventLogValueObject, refInd);

				log.info("Periodic store rent previous period deriveTimePeriod:  " + deriveTimePeriod);

				prevChargeableBillList = determineStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
						deriveTon, deriveTimePeriod, fsp);

				// loop through the original charge list and current periodic charge list and
				// minus the previoud charges
				List<ChargeableBillValueObject> finalChargeableBillList = new ArrayList<ChargeableBillValueObject>();
				for (int i = 0; i < chargeableBillList.size(); i++) {
					ChargeableBillValueObject finalChargeableBillVO =  chargeableBillList.get(i);
					ChargeableBillValueObject newFinalChargeableBillVO = new ChargeableBillValueObject(
							finalChargeableBillVO);

					for (int j = 0; j < prevChargeableBillList.size(); j++) {
						ChargeableBillValueObject lastPeriodicVO = (ChargeableBillValueObject) prevChargeableBillList
								.get(i);
						if (newFinalChargeableBillVO.getTierSeqNbr() == lastPeriodicVO.getTierSeqNbr()) {

							if (newFinalChargeableBillVO.getNbrTimeUnit() - lastPeriodicVO.getNbrTimeUnit() > 0) {
								double nbrTimeUnit = newFinalChargeableBillVO.getNbrTimeUnit()
										- lastPeriodicVO.getNbrTimeUnit();
								double totalChargeAmt = newFinalChargeableBillVO.getTotalChargeAmt()
										- lastPeriodicVO.getTotalChargeAmt();
								newFinalChargeableBillVO.setNbrTimeUnit(nbrTimeUnit);
								newFinalChargeableBillVO.setTotalChargeAmt(totalChargeAmt);
								log.info("Periodic store rent adding new rates  nbrTimeUnit:" + nbrTimeUnit);
								finalChargeableBillList.add(newFinalChargeableBillVO);
							}
						}
					}
				}
				generalEventLogValueObject.setLastTriggerPSRDttm(null);
				return finalChargeableBillList;
			}

			return chargeableBillList;
		} catch (BusinessException e) {
			log.info("ProcessServiceEJB Exception " + e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
//				if (billErrorHome != null && billError != null) {
//					try {
//						billErrorValueObject = new BillErrorVO();
//						billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
//						billErrorValueObject.setTariffMainCat(tariffMainCat);
//						billErrorValueObject.setTariffSubCat(tariffSubCat);
//						billErrorValueObject.setRemarks(
//								"Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd ("
//										+ varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
//						// insert into Bill Error table
//						billError.insertBillError(billErrorValueObject);
//						if (e instanceof ProcessChargeException) {
//							throw e;
//						} else {
//							throw new Exception("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
//									+ ") ~Msg = " + e.getMessage());
//						}
//					} catch (Exception billErrorException) {
//						log.info("[ProcessGBStoreEJB Error] >> Inserting billError exception = "
//								+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
//								+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg
//								+ ") ~Msg = " + e.getMessage());
//						throw new Exception("[ProcessGBStoreEJB Error] >> Inserting billError exception = "
//								+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
//								+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg
//								+ ") ~Msg = " + e.getMessage());
//					}
//				} 
//			else {
//					log.info("[ProcessGBStoreEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//							+ billErrorHome + " BillError = " + billError + " Exception occurred for ~ref_nbr (" + refNbr
//							+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = "
//							+ e.getMessage());
//					if (e instanceof NamingException) {
//						throw new NamingException(
//								"[ProcessGBStoreEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError + " Exception occurred for ~ref_nbr ("
//										+ refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg ("
//										+ localLeg + ") ~Msg = " + e.getMessage());
//					} else if (e instanceof SQLException) {
//						throw new SQLException(
//								"[ProcessGBStoreEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError + " Exception occurred for ~ref_nbr ("
//										+ refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg ("
//										+ localLeg + ") ~Msg = " + e.getMessage());
//					} else if (e instanceof ProcessChargeException) {
//						throw e;
//					} else {
//						throw new Exception(
//								"[ProcessGBStoreEJB Error] >> Invoking BillErrorEJB billError exception. BillErrorHome = "
//										+ billErrorHome + " BillError = " + billError + " Exception occurred for ~ref_nbr ("
//										+ refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg ("
//										+ localLeg + ") ~Msg = " + e.getMessage());
//					}
//				}
			log.info("Exception calculateStoreBillCharge : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END calculateStoreBillCharge DAO");
		}
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->determineStoreRentBillable()
	public List<ChargeableBillValueObject> determineStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon, double deriveTimePeriod, int custFSP)
			throws BusinessException {

		List<ChargeableBillValueObject> chargeableBillList = null;
		String tariffSubCat = null;

		try {
			log.info("START determineStoreRentBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
					+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString())
					+ " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)) + " deriveTimePeriod: " + CommonUtility.deNull(String.valueOf(deriveTimePeriod))
					+ " custFSP: " + CommonUtility.deNull(String.valueOf(custFSP)));
			tariffSubCat = tariffMainValueObject.getSubCategory();
			if ((tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL))
					|| (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL_TRANSHIP))) {
				log.info("entering general store rent");
				chargeableBillList = determineGeneralStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
						deriveTon, deriveTimePeriod, custFSP);
			} else if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
				log.info("entering unauth store rent");
				chargeableBillList = determineUnauthStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
						deriveTon, deriveTimePeriod);
			} else if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
				chargeableBillList = determineShutoutStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
						deriveTon, deriveTimePeriod);
			} else {
				log.info("entering other store rent");
				chargeableBillList = determineOtherStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
						deriveTon);
			}
		} catch (BusinessException e) {
			log.info("Exception determineStoreRentBillable : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineStoreRentBillable : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END determineStoreRentBillable DAO  chargeableBillList: " + chargeableBillList.toString());
		}
		
		return chargeableBillList;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->determineGeneralStoreRentBillable()
	private List<ChargeableBillValueObject> determineGeneralStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon, double deriveTimePeriod, int custFSP)
			throws BusinessException {

		TariffTierVO tariffTierValueObject = null;
		ChargeableBillValueObject chargeableBillValueObjectN = null;
		BillAdjustParam billAdjParam = null;
		List<ChargeableBillValueObject> chargeableBillList = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double rangeFrom = 0;
		double rangeTo = 0;
		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		double tierPeriod = 0;
		double periodQuot = 0;
		double periodMod = 0;

		String tierPeriodString = null;
		int tierPeriodIndex = 0;
		int tierCnt = 0;
		String mvmt = null;

		try {
			log.info("START determineGeneralStoreRentBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
			+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString())
			+ " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)) + " deriveTimePeriod: " + CommonUtility.deNull(String.valueOf(deriveTimePeriod))
			+ " custFSP: " + CommonUtility.deNull(String.valueOf(custFSP)));
			
			chargeableBillList = new ArrayList<ChargeableBillValueObject>();
			String tariffSubCat = tariffMainValueObject.getSubCategory();
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();
			mvmt = tariffCd.substring(6, 8);
			int fspHours = custFSP * 24;
			log.info("fspHours = " + fspHours);
			log.info("deriveTimePeriod = " + deriveTimePeriod);

			if (fspHours >= deriveTimePeriod) {
				return chargeableBillList;
			}
			int fspTierSeq = 0;
			if (fspHours > 0) {
				for (int i = 0; i < tariffMainValueObject.getTierCount(); i++) {
					TariffTierVO tariffTier = tariffMainValueObject.getTier(i);
					if (fspHours >= tariffTier.getRangeFrom() && fspHours <= tariffTier.getRangeTo()) {
						fspTierSeq = i + 1;
						break;
					}
				}
			}
			log.info("fspTierSeq = " + fspTierSeq);
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				rangeFrom = tariffTierValueObject.getRangeFrom();
				log.info("range from = " + rangeFrom + "...");
				rangeTo = tariffTierValueObject.getRangeTo();
				log.info("range to = " + rangeTo + "...");

				timeUnitBase = tariffTierValueObject.getPerHour();
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				origTimeUnit = deriveTimePeriod;

				log.info("derive time period = " + origTimeUnit + "...");

				if (origTimeUnit > rangeFrom) {
					if (tierSeqNbr.intValue() == 1) {
						if (origTimeUnit > rangeTo) {
							tierPeriod = rangeTo;
						} else {
							tierPeriod = origTimeUnit - 0.0;
						}
						log.info("tier period first time = " + tierPeriod + "...");
					} else {
						if (origTimeUnit > rangeTo) {
							tierPeriod = rangeTo - rangeFrom;
						} else {
							tierPeriod = origTimeUnit - rangeFrom;
						}
					}

					log.info("tier period = " + tierPeriod + "...");

					tierPeriodString = String.valueOf(tierPeriod / timeUnitBase);
					log.info("tier period string = " + tierPeriodString + "...");
					tierPeriodIndex = tierPeriodString.indexOf(".");

					periodQuot = Double.parseDouble(tierPeriodString.substring(0, tierPeriodIndex));
					log.info("quotient = " + periodQuot + "...");
					periodMod = tierPeriod - (periodQuot * timeUnitBase);
					log.info("modular = " + periodMod + "...");
					if (periodMod > 0) {
						timeUnit = periodQuot + 1;
					} else {
						timeUnit = periodQuot;
					}
					if (fspHours > 0) { 
						if (tierSeqNbr.intValue() < fspTierSeq) {
							if ((mvmt.equals(ProcessChargeConst.MVMT_TRANSHIP))
									|| (mvmt.equals(ProcessChargeConst.MVMT_ITH))) {
								timeUnit = 0;
							}
						}

						else if (tierSeqNbr.intValue() == fspTierSeq) {
							if ((mvmt.equals(ProcessChargeConst.MVMT_TRANSHIP))
									|| (mvmt.equals(ProcessChargeConst.MVMT_ITH))) {
								timeUnit = timeUnit - (fspHours / timeUnitBase);
							}
						}
					} else {
						if (tierSeqNbr.intValue() == 1) {
							if ((mvmt.equals(ProcessChargeConst.MVMT_TRANSHIP))
									|| (mvmt.equals(ProcessChargeConst.MVMT_ITH)) || (tariffSubCat
											.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL_TRANSHIP))) {
								// end added by Irene Tan on 07 Apr 2003
								timeUnit = timeUnit - (rangeFrom / timeUnitBase);
							}
						}
					}

					log.info("time unit = " + timeUnit + "...");

					origOtherUnit = deriveTon;
					otherUnit = (origOtherUnit / otherUnitBase);

					log.info("other unit = " + otherUnit + "...");

					if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
						unitRate = tariffTierValueObject.getRate();
					} else {
						adjType = tariffTierValueObject.getAdjustmentType();
						adjAmt = tariffTierValueObject.getAdjustment();
						unitRate = tariffTierValueObject.getRate();
						if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
							unitRate = unitRate + (adjAmt / 100 * unitRate);
						} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
							unitRate = unitRate + adjAmt;
						}
					}

					log.info("unit rate = " + unitRate + "...");

					billAdjParam = create(tariffCd);

					if (billAdjParam == null) {
						String[] tempString2 = {tariffCd};
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
					}

					billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
					billAdjParam.setTotalTime(timeUnit);
					billAdjParam.setTotalOtherUnit(otherUnit);
					billAdjParam.setUnitRate(unitRate);
					billAdjParam.setGst(gstPercent);

					itemGst = billAdjParam.getGstAmount();
					itemAmt = billAdjParam.getTotalAmount();

					// set the parameters into the ChargeableBillValueObject
					chargeableBillValueObjectN = new ChargeableBillValueObject(chargeableBillValueObject);
					chargeableBillValueObjectN.setTariffType(tariffType);
					chargeableBillValueObjectN.setTariffCd(tariffCd);
					chargeableBillValueObjectN.setTierSeqNbr(tierSeqNbr.intValue());
					chargeableBillValueObjectN.setTariffDesc(tariffDesc);
					chargeableBillValueObjectN.setNbrTimeUnit(timeUnit);
					chargeableBillValueObjectN.setNbrOtherUnit(otherUnit);
					chargeableBillValueObjectN.setUnitRate(unitRate);
					chargeableBillValueObjectN.setGstCharge(gstPercent);
					chargeableBillValueObjectN.setGstAmt(itemGst);
					chargeableBillValueObjectN.setTotalChargeAmt(itemAmt);
					chargeableBillValueObjectN.setFmasGstCd(tariffMainValueObject.getGSTCode());

					chargeableBillList.add(chargeableBillValueObjectN);
				}
			} // end for loop

		} catch (BusinessException e) {
			log.info("Exception determineGeneralStoreRentBillable : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineGeneralStoreRentBillable : ", e);
			throw new BusinessException("Exception occured for tariff_type = " + tariffType + "when determining chargeable bill units");

		} finally {
			log.info("END determineGeneralStoreRentBillable DAO  chargeableBillList: " + chargeableBillList.toString());
		}
		
		return chargeableBillList;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->determineUnauthStoreRentBillable()
	private List<ChargeableBillValueObject> determineUnauthStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon, double deriveTimePeriod)
			throws BusinessException {

		TariffTierVO tariffTierValueObject = null;
		ChargeableBillValueObject chargeableBillValueObjectN = null;
		BillAdjustParam billAdjParam = null;
		List<ChargeableBillValueObject> chargeableBillList = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		// Added by Irene Tan on 11 Feb 2003 : To increment the store rent days by 1 as
		// long as it exceeds by 1 min
		double periodQuot = 0;
		double periodMod = 0;

		String tierPeriodString = null;
		int tierPeriodIndex = 0;
		// End Added by Irene Tan on 11 Feb 2003
		int tierCnt = 0;

		try {
			log.info("START determineUnauthStoreRentBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
			+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString())
			+ " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)) + " deriveTimePeriod: " + CommonUtility.deNull(String.valueOf(deriveTimePeriod)));
			
			chargeableBillList = new ArrayList<ChargeableBillValueObject>();
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();

			// loop thru the tariff tier records to determine what is the chargeable units
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				// get the individual record of the tariff tier record
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				// from tariffTierValueObject, get the details for processing
				timeUnitBase = tariffTierValueObject.getPerHour();
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				origTimeUnit = deriveTimePeriod;
				log.info("origTimeUnit: " + origTimeUnit + " deriveTimePeriod: " + deriveTimePeriod);
				timeUnit = (origTimeUnit / timeUnitBase);
				log.info("timeUnit:  " + timeUnit);

				origOtherUnit = deriveTon;
				otherUnit = (origOtherUnit / otherUnitBase);

				// Added by Irene Tan on 11 Feb 2003 : To increment the store rent days by 1 as
				// long as it exceeds by 1 min
				tierPeriodString = String.valueOf(timeUnit);
				log.info("tier period string = " + tierPeriodString + "...");
				tierPeriodIndex = tierPeriodString.indexOf(".");

				periodQuot = Double.parseDouble(tierPeriodString.substring(0, tierPeriodIndex));
				log.info("quotient = " + periodQuot + "...");
				periodMod = timeUnit - periodQuot;
				log.info("modular = " + periodMod + "...");

				if (periodMod > 0) {
					timeUnit = periodQuot + 1;
				} else {
					timeUnit = periodQuot;
				}

				log.info("time unit = " + timeUnit + "...");
				// End Added by Irene Tan on 11 Feb 2003

				if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
					unitRate = tariffTierValueObject.getRate();
				} else {
					adjType = tariffTierValueObject.getAdjustmentType();
					adjAmt = tariffTierValueObject.getAdjustment();
					unitRate = tariffTierValueObject.getRate();
					if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
						unitRate = unitRate + (adjAmt / 100 * unitRate);
					} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
						unitRate = unitRate + adjAmt;
					}
				}

				billAdjParam = create(tariffCd);

				if (billAdjParam == null) {
					// ex = new ProcessChargeException("Bill Adj Param is null for ~tariff_cd = " +
					// tariffCd);
					String[] tempString2 = {tariffCd};
					throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
				}

				billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
				billAdjParam.setTotalTime(timeUnit);
				billAdjParam.setTotalOtherUnit(otherUnit);
				billAdjParam.setUnitRate(unitRate);
				billAdjParam.setGst(gstPercent);

				itemGst = billAdjParam.getGstAmount();
				itemAmt = billAdjParam.getTotalAmount();

				// set the parameters into the ChargeableBillValueObject
				chargeableBillValueObjectN = new ChargeableBillValueObject(chargeableBillValueObject);
				chargeableBillValueObjectN.setTariffType(tariffType);
				chargeableBillValueObjectN.setTariffCd(tariffCd);
				chargeableBillValueObjectN.setTierSeqNbr(tierSeqNbr.intValue());
				chargeableBillValueObjectN.setTariffDesc(tariffDesc);
				chargeableBillValueObjectN.setNbrTimeUnit(timeUnit);
				chargeableBillValueObjectN.setNbrOtherUnit(otherUnit);
				chargeableBillValueObjectN.setUnitRate(unitRate);
				chargeableBillValueObjectN.setGstCharge(gstPercent);
				chargeableBillValueObjectN.setGstAmt(itemGst);
				chargeableBillValueObjectN.setTotalChargeAmt(itemAmt);
				// Added by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST changes
				// w.e.f 1 Jan 2003
				chargeableBillValueObjectN.setFmasGstCd(tariffMainValueObject.getGSTCode());
				// End Added by Irene Tan

				chargeableBillList.add(chargeableBillValueObjectN);
			} // end for loop
		
		} catch (BusinessException e) {
			log.info("Exception determineUnauthStoreRentBillable : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineUnauthStoreRentBillable : ", e);
			throw new BusinessException("Exception occured for tariff_type = " + tariffType + "when determining chargeable bill units");

		} finally {
			log.info("END determineUnauthStoreRentBillable DAO  chargeableBillList: " + chargeableBillList.toString());
		}
		
		return chargeableBillList;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->determineShutoutStoreRentBillable()
	public List<ChargeableBillValueObject> determineShutoutStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon, double deriveTimePeriod)
			throws BusinessException {

		TariffTierVO tariffTierValueObject = null;
		ChargeableBillValueObject chargeableBillValueObjectN = null;
		BillAdjustParam billAdjParam = null;
		List<ChargeableBillValueObject> chargeableBillList = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double rangeFrom = 0;
		double rangeTo = 0;
		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		double tierPeriod = 0;
		double periodQuot = 0;
		double periodMod = 0;

		String tierPeriodString = null;
		int tierPeriodIndex = 0;
		int tierCnt = 0;

		try {
			log.info("START determineShutoutStoreRentBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
			+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString())
			+ " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)) + " deriveTimePeriod: " + CommonUtility.deNull(String.valueOf(deriveTimePeriod)));
			chargeableBillList = new ArrayList<ChargeableBillValueObject>();
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();

			// chargeable units
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				// get the individual record of the tariff tier record
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				rangeFrom = tariffTierValueObject.getRangeFrom();
				log.info("range from = " + rangeFrom + "...");
				rangeTo = tariffTierValueObject.getRangeTo();
				log.info("range to = " + rangeTo + "...");

				// from tariffTierValueObject, get the details for processing
				timeUnitBase = tariffTierValueObject.getPerHour();
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				origTimeUnit = deriveTimePeriod;

				log.info("derive time period = " + origTimeUnit + "...");

				if (origTimeUnit > rangeFrom) {
					if (tierSeqNbr.intValue() == 1) {
						if (origTimeUnit > rangeTo) {
							tierPeriod = rangeTo;
						} else {
							tierPeriod = origTimeUnit - 0.0;
						}
						log.info("tier period first time = " + tierPeriod + "...");
					} else {
						if (origTimeUnit > rangeTo) {
							tierPeriod = rangeTo - rangeFrom;
						} else {
							tierPeriod = origTimeUnit - rangeFrom;
						}
					}

					log.info("tier period = " + tierPeriod + "...");

					tierPeriodString = String.valueOf(tierPeriod / timeUnitBase);
					log.info("tier period string = " + tierPeriodString + "...");
					tierPeriodIndex = tierPeriodString.indexOf(".");

					periodQuot = Double.parseDouble(tierPeriodString.substring(0, tierPeriodIndex));
					log.info("quotient = " + periodQuot + "...");
					periodMod = tierPeriod - (periodQuot * timeUnitBase);
					log.info("modular = " + periodMod + "...");

					if (periodMod > 0) {
						timeUnit = periodQuot + 1;
					} else {
						timeUnit = periodQuot;
					}

					log.info("time unit = " + timeUnit + "...");

					origOtherUnit = deriveTon;
					otherUnit = (origOtherUnit / otherUnitBase);

					log.info("other unit = " + otherUnit + "...");

					if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
						unitRate = tariffTierValueObject.getRate();
					} else {
						adjType = tariffTierValueObject.getAdjustmentType();
						adjAmt = tariffTierValueObject.getAdjustment();
						unitRate = tariffTierValueObject.getRate();
						if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
							unitRate = unitRate + (adjAmt / 100 * unitRate);
						} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
							unitRate = unitRate + adjAmt;
						}
					}

					log.info("unit rate = " + unitRate + "...");

					billAdjParam = create(tariffCd);

					if (billAdjParam == null) {
						String[] tempString2 = {tariffCd};
						throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
					}

					billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
					billAdjParam.setTotalTime(timeUnit);
					billAdjParam.setTotalOtherUnit(otherUnit);
					billAdjParam.setUnitRate(unitRate);
					billAdjParam.setGst(gstPercent);

					itemGst = billAdjParam.getGstAmount();
					itemAmt = billAdjParam.getTotalAmount();

					// set the parameters into the ChargeableBillValueObject
					chargeableBillValueObjectN = new ChargeableBillValueObject(chargeableBillValueObject);
					chargeableBillValueObjectN.setTariffType(tariffType);
					chargeableBillValueObjectN.setTariffCd(tariffCd);
					chargeableBillValueObjectN.setTierSeqNbr(tierSeqNbr.intValue());
					chargeableBillValueObjectN.setTariffDesc(tariffDesc);
					chargeableBillValueObjectN.setNbrTimeUnit(timeUnit);
					chargeableBillValueObjectN.setNbrOtherUnit(otherUnit);
					chargeableBillValueObjectN.setUnitRate(unitRate);
					chargeableBillValueObjectN.setGstCharge(gstPercent);
					chargeableBillValueObjectN.setGstAmt(itemGst);
					chargeableBillValueObjectN.setTotalChargeAmt(itemAmt);
					chargeableBillValueObjectN.setFmasGstCd(tariffMainValueObject.getGSTCode());

					chargeableBillList.add(chargeableBillValueObjectN);
				}
			} // end for loop
		} catch (BusinessException e) {
			log.info("Exception determineShutoutStoreRentBillable : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineShutoutStoreRentBillable : ", e);
			throw new BusinessException("Exception occured for tariff_type = " + tariffType + "when determining chargeable bill units");

		} finally {
			log.info("END determineShutoutStoreRentBillable DAO  chargeableBillList: " + chargeableBillList.toString());
		}
		
		return chargeableBillList;
	}

	// jp.src.ejb.sessionBeans.cab.processCharges--->ProcessGBStoreEJB-->determineOtherStoreRentBillable()
	private List<ChargeableBillValueObject> determineOtherStoreRentBillable(ChargeableBillValueObject chargeableBillValueObject,
			TariffMainVO tariffMainValueObject, double deriveTon) throws BusinessException {


		TariffTierVO tariffTierValueObject = null;
		BillAdjustParam billAdjParam = null;
		List<ChargeableBillValueObject> chargeableBillList = null;

		String tariffCd = null;
		String tariffType = null;
		Integer tierSeqNbr = null;
		String tariffDesc = null;
		
		double timeUnit = 0;
		double otherUnit = 0;
		double unitRate = 0;
		double gstPercent = 0;
		double itemGst = 0;
		double itemAmt = 0;

		double timeUnitBase = 0;
		double otherUnitBase = 0;
		double origTimeUnit = 0;
		double origOtherUnit = 0;
		String adjType = null;
		double adjAmt = 0;

		int tierCnt = 0;

		try {
			log.info("START determineOtherStoreRentBillable DAO :: chargeableBillValueObject: " + CommonUtility.deNull(chargeableBillValueObject.toString())
			+ " tariffMainValueObject: " + CommonUtility.deNull(tariffMainValueObject.toString()) + " deriveTon: " + CommonUtility.deNull(String.valueOf(deriveTon)));
			
			chargeableBillList = new ArrayList<ChargeableBillValueObject>();
			tariffCd = tariffMainValueObject.getCode();
			tariffDesc = tariffMainValueObject.getDescription();
			tariffType = tariffMainValueObject.getTariffTypeInd();
			gstPercent = tariffMainValueObject.getGST();

			// loop thru the tariff tier records to determine what is the chargeable units
			for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
				// get the individual record of the tariff tier record
				tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

				tierSeqNbr = new Integer(tierCnt + 1);

				// from tariffTierValueObject, get the details for processing
				timeUnitBase = tariffTierValueObject.getPerHour();
				if (timeUnitBase < 0) {
					timeUnitBase = 1;
				}

				otherUnitBase = tariffTierValueObject.getPerUnit();
				if (otherUnitBase < 0) {
					otherUnitBase = 1;
				}

				origTimeUnit = 0;
				timeUnit = (origTimeUnit / timeUnitBase);

				origOtherUnit = deriveTon;
				otherUnit = (origOtherUnit / otherUnitBase);
			} // end for loop

			if (tariffType.equals(ProcessChargeConst.TARIFF_TYPE_PUBLISH)) {
				unitRate = tariffTierValueObject.getRate();
			} else {
				adjType = tariffTierValueObject.getAdjustmentType();
				adjAmt = tariffTierValueObject.getAdjustment();
				unitRate = tariffTierValueObject.getRate();
				if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_PERCENT)) {
					unitRate = unitRate + (adjAmt / 100 * unitRate);
				} else if (adjType.equals(ProcessChargeConst.TARIFF_ADJ_TYPE_AMOUNT)) {
					unitRate = unitRate + adjAmt;
				}
			}

			billAdjParam = create(tariffCd);

			if (billAdjParam == null) {
				// ex = new ProcessChargeException("Bill Adj Param is null for ~tariff_cd = " +
				// tariffCd);
				String[] tempString2 = {tariffCd};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_Bill_Adj_Null, tempString2));
			}

			billAdjParam.setTotalContainer(chargeableBillValueObject.getNbrCntr());
			billAdjParam.setTotalTime(timeUnit);
			billAdjParam.setTotalOtherUnit(otherUnit);
			billAdjParam.setUnitRate(unitRate);
			billAdjParam.setGst(gstPercent);

			itemGst = billAdjParam.getGstAmount();
			itemAmt = billAdjParam.getTotalAmount();

			// set the parameters into the ChargeableBillValueObject
			chargeableBillValueObject.setTariffType(tariffType);
			chargeableBillValueObject.setTariffCd(tariffCd);
			chargeableBillValueObject.setTierSeqNbr(tierSeqNbr.intValue());
			chargeableBillValueObject.setTariffDesc(tariffDesc);
			chargeableBillValueObject.setNbrTimeUnit(timeUnit);
			chargeableBillValueObject.setNbrOtherUnit(otherUnit);
			chargeableBillValueObject.setUnitRate(unitRate);
			chargeableBillValueObject.setGstCharge(gstPercent);
			chargeableBillValueObject.setGstAmt(itemGst);
			chargeableBillValueObject.setTotalChargeAmt(itemAmt);
			// Added by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST changes
			// w.e.f 1 Jan 2003
			chargeableBillValueObject.setFmasGstCd(tariffMainValueObject.getGSTCode());
			// End Added by Irene Tan

			chargeableBillList.add(chargeableBillValueObject);
			
		} catch (BusinessException e) {
			log.info("Exception determineOtherStoreRentBillable : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineOtherStoreRentBillable : ", e);
			throw new BusinessException("Exception occured for tariff_type = " + tariffType + "when determining chargeable bill units");
		} finally {
			log.info("END determineOtherStoreRentBillable DAO  chargeableBillList: " + chargeableBillList.toString());
		}
		
		return chargeableBillList;
	}

	public double determineTimeUnit(GeneralEventLogValueObject vo, String refInd) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		double retVal = 0.0;
//			BillError error = null;
		StringBuffer str = new StringBuffer();
//			IProcessStoreRent p = null;

		try {
			log.info("START determineTimeUnit DAO :: vo: " + CommonUtility.deNull(vo.toString()) + " refInd: " + CommonUtility.deNull(refInd));
			String startCode = null, endCode = null;
			int startAdj = 0, endAdj = 0;
			
			String sql = "SELECT start_cd, start_adj, end_cd, end_adj FROM gb_store_rent_mapping WHERE local_leg= :localLeg AND scheme_cd= :schemeCd AND tariff_main_cat_cd= :tariffMainCatCd AND tariff_sub_cat_cd= :tariffSubCatCd and mvmt=:mvmt ";

			
			paramMap.put("localLeg", vo.getLocalLeg());
			paramMap.put("schemeCd", vo.getSchemeCd());
			paramMap.put("tariffMainCatCd", vo.getTariffMainCatCd());
			paramMap.put("tariffSubCatCd", vo.getTariffSubCatCd());
			paramMap.put("mvmt", vo.getMvmt());
			
			log.info("SQL: " + sql.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				startCode = rs.getString("start_cd");
				endCode = rs.getString("end_cd");
				startAdj = rs.getInt("start_adj");
				endAdj = rs.getInt("end_adj");
			} else {
				throw new Exception(
						"ProcessGBStoreEJB - determineTimeUnit(): #No GB Store Rent Mapping found!# ~local_leg ("
								+ vo.getLocalLeg() + ") ~scheme_cd (" + vo.getSchemeCd() + ")  ~tariff_main_cat_cd ("
								+ vo.getTariffMainCatCd() + ") ~tariff_sub_cat_cd (" + vo.getTariffSubCatCd() + ")");
			}

			log.info("startCode: " + startCode + " endCode: " + endCode);
			log.info("startAdj: " + startAdj + " endAdj: " + endAdj);

			// create the handler for begin time and get the time
//				p = (IProcessStoreRent) fac.create(GB_STORE_RENT + startCode);
//				p.setConnection(con);
//				p.setContext(this.context);
			Timestamp startTime = getTimestamp(vo, refInd);
//				p.setConnection(null);
//				p.setContext(null);

			// create the handler for end time and get the time
//				p = (IProcessStoreRent) fac.create(GB_STORE_RENT + endCode);
//				p.setConnection(con);
//				p.setContext(this.context);
			// MCC set print dttm as last triggered dttm to calcluate previous store rent
			Timestamp actualPrintDttm = null;
			if (vo.getLastTriggerPSRDttm() != null) {
				actualPrintDttm = vo.getPrintDttm();
				vo.setPrintDttm(vo.getLastTriggerPSRDttm());
			}
			Timestamp endTime = getTimestamp(vo, refInd);
			if (actualPrintDttm != null) {
				vo.setPrintDttm(actualPrintDttm);
			}

			long diff = 0;

			int customizedFspDays = tariffMainRepo.getGeneralCargoCustFspDays(vo);

			log.info("in determineTimeUnit(), GB customized FSP = " + customizedFspDays);

			boolean isJNL2JNL = tariffMainRepo.isJNL2JNL(vo);
			log.info("---isJNL2JNL----" + isJNL2JNL);

			if (customizedFspDays > 0) { // To apply GB customized FSP here
				diff = endTime.getTime() - startTime.getTime() - customizedFspDays * 24 * 3600 * 1000;
				log.info("GB Customized FSP applied, difference = " + diff);

				// If exceeding customized FSP, use the whole storage period instead
				if (diff > 0) {
					diff = endTime.getTime() - startTime.getTime();

					if (isJNL2JNL) { // SL-CAB-20170518-01
						log.info("unauthorized store rent, need to minus cust FSP");
						diff -= customizedFspDays * 24 * 3600 * 1000;
						log.info("unauthorized store rent, diff = " + diff);
					}
					// fixes for SL-CAB-20161003-01 END
				}

			} else { // If no customized FSP, use published FSP instead
				// End of adding by Jade for SL-CAB-20110509-01

				// end - start = no of hours
				long a = startAdj * 3600 * 1000;
				long b = endAdj * 3600 * 1000;
				// long diff = (endTime.getTime()+b) - (startTime.getTime()+a);
				diff = (endTime.getTime() + b) - (startTime.getTime() + a);

				log.info("difference=" + diff);
			}

			if (diff < 0) {
				str.setLength(0);
				str.append("~start_cd (" + startCode);
				str.append(");");
				str.append("~endCode (" + endCode);
				str.append(");");
				str.append("~startAdj (" + startAdj);
				str.append(");");
				str.append("~endAdj (" + endAdj);
				str.append(");");
				str.append("~startTime (" + startTime);
				str.append(");");
				str.append("~endTime(" + endTime);
				str.append(");");
				str.append("#The end time is earlier than start time.#");
				log.info(str.toString());
				retVal = 0;
			} else if (diff > 0) {
				if (vo.getTariffSubCatCd().equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)
						|| vo.getTariffSubCatCd().equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
					Date startDate = new Date(startTime.getTime());
					Date endDate = new Date(endTime.getTime());

					String startDateString = dateFormat.format(startDate);
					String endDateString = dateFormat.format(endDate);

					startDate = dateFormat.parse(startDateString, new ParsePosition(0));
					endDate = dateFormat.parse(endDateString, new ParsePosition(0));

					startTime = new Timestamp(startDate.getTime());
					endTime = new Timestamp(endDate.getTime());
					if (isJNL2JNL) { // SL-CAB-20170518-01
						diff = diff + 1;
					} else if (!vo.getSchemeCd().equalsIgnoreCase(ProcessChargeConst.LINER_SCHEME)) {
						diff = (endTime.getTime() - startTime.getTime()) + 1;
					}
					// add end by hujun
					log.info("diff: " + diff);
				}

				DoubleMath dm = new DoubleMath();
				dm.setRoundingMode(BigDecimal.ROUND_UP);
				retVal = dm.divide(diff, 1000.0); // from msec to sec
				retVal = dm.divide(retVal, 3600.0); // sec to min to hr
			}
		} catch (Exception e) {
			log.info("Exception determineTimeUnit : ", e);
			retVal = 0.0;
		} finally {
			log.info("END determineTimeUnit DAO  retVal: " + retVal);
		}
		return retVal;
	}

	// jp.src.cab.processCharges--->ProcessCommon-->determineRefNbr()
	public String determineRefNbr(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
			throws BusinessException {
		String refNbr = "";

		try {
			log.info("START determineRefNbr DAO :: generalEventLogValueObject: " + CommonUtility.deNull(generalEventLogValueObject.toString())
					+ " refInd: " + CommonUtility.deNull(refInd));
			if (refInd.equals(ProcessChargeConst.REF_IND_DN)) {
				refNbr = generalEventLogValueObject.getDnNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_UA)) {
				refNbr = generalEventLogValueObject.getUaNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_BL)) {
				refNbr = generalEventLogValueObject.getBlNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_BR)) {
				refNbr = generalEventLogValueObject.getBkRefNbr();
			}
			if (refInd.equals(ProcessChargeConst.REF_IND_EDO)) {
				refNbr = generalEventLogValueObject.getEdoAsnNbr();
			}

			if (refInd.equals(ProcessChargeConst.REF_IND_ESN)) {
				refNbr = generalEventLogValueObject.getEsnAsnNbr();
			}
			if (refInd.equals(ProcessChargeConst.REF_IND_STUFF)) {
				refNbr = generalEventLogValueObject.getCntrNbr();
			}
			if (refNbr.equals("")) {
				String[] tempString2 = {refInd};
				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_RefNbr_Invalid_Empty, tempString2));

			}
		} catch (BusinessException e) {
			log.info("Exception determineRefNbr : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception determineRefNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END determineRefNbr DAO  refNbr: " + refNbr);
		}
		return refNbr;
	}

	// jp.src.cab.processCharges--->ProcessCommon-->computeBillTon()
	@Override
	public double computeBillTon(double edoBillTon, int edoPkgs, int dnPkgs) throws BusinessException {
		double billTon = 0.0;
		try {
			log.info("START computeBillTon DAO :: edoBillTon" + CommonUtility.deNull(String.valueOf(edoBillTon)) + " edoPkgs: " + CommonUtility.deNull(String.valueOf(edoPkgs))
					+ " dnPkgs: " + CommonUtility.deNull(String.valueOf(dnPkgs)));
			billTon = (new BigDecimal("" + (edoBillTon / edoPkgs * dnPkgs)).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue());
			log.info("billTon: " + billTon);
		} catch (Exception ex) {
			log.info("Exception computeBillTon : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END computeBillTon DAO");
		}
		return billTon;
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->create()
	public BillAdjustParam create(String tariffCode) throws BusinessException, Exception {
		int ind[] = null;
		try {
			log.info("START create Process GB Generic DAO:: tariffCode: " + CommonUtility.deNull(tariffCode));
			ind = billSupInfoRepo.getIndicator(tariffCode);
		} catch (BusinessException be) {
			log.info("Exception create : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception create : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END create Process GB Generic DAO  ind: " + ind);
		}
		return getParam(ind);
	}

	// jp.src.valueObject.cab.billing--->BillAdjustParamFactory-->getParam()
	private static BillAdjustParam getParam(int ind[]) throws BusinessException {
		if (ind == null)
			return null;

		BillAdjustParam retVal = null;
		try {
			log.info("START getParam Process GB Generic DAO :: ind: " + CommonUtility.deNull(String.valueOf(ind)));
			if (ind[0] == 1 && ind[1] == 0 && ind[2] == 0) {
			retVal = new BillAdjParamCVO();
		} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 0) {
			retVal = new BillAdjParamTVO();
		} else if (ind[0] == 0 && ind[1] == 0 && ind[2] == 1) {
			retVal = new BillAdjParamOVO();
		} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 0) {
			retVal = new BillAdjParamCTVO();
		} else if (ind[0] == 1 && ind[1] == 0 && ind[2] == 1) {
			retVal = new BillAdjParamCOVO();
		} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 1) {
			retVal = new BillAdjParamTOVO();
		} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 1) {
			retVal = new BillAdjParamCTOVO();
		} else {
			retVal = null;
		}
		} catch (Exception e) {
			log.info("Exception getParam : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getParam Process GB Generic DAO  retVal: " + retVal);
		}
		return retVal;
	}

	// jp.src.cab.processCharges.storeRent--->StoreRentATB-->getTimestamp()
	public Timestamp getTimestamp(Object o, String s) throws BusinessException {
		Timestamp ts = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START getTimestamp Process GB Generic DAO :: o: " + CommonUtility.deNull(String.valueOf(o)) + " s: " + CommonUtility.deNull(String.valueOf(s)));
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT b.atb_dttm, b.etb_dttm, v.vsl_berth_dttm FROM berthing b, vessel_call v WHERE v.vv_cd= :vvCode ");
			sql.append("AND v.vv_cd=b.vv_cd ");
			sql.append("AND b.shift_ind= :shiftInd ");
			log.info("SQL : " + sql.toString());

			GeneralEventLogValueObject vo = (GeneralEventLogValueObject) o;
			String vvCode = null;
			if (vo.getVvInd().equals(ProcessChargeConst.DISC_VV_IND)) {
				if (vo.getMvmt().trim().equals(ProcessChargeConst.MVMT_TRANSHIP)) {
					vvCode = vo.getLoadVvCd();
				} else {
					vvCode = vo.getDiscVvCd();
				}
			} else if (vo.getVvInd().equals(ProcessChargeConst.LOAD_VV_IND)) {
				vvCode = vo.getLoadVvCd();
			} else {
				vvCode = null;
			}
			log.info("vvInd  : " + vo.getVvInd());
			log.info("vvCode : " + vvCode);
		
			log.info("Setting variable");
			paramMap.put("vvCode", vvCode);
			paramMap.put("shiftInd", 1);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			log.info("Reading result");
			if (rs.next()) {
				ts = rs.getTimestamp("atb_dttm");

				// Use ETB if ATB is null
				if (ts == null) {
					log.info("ATB is null");
					ts = rs.getTimestamp("etb_dttm");
				}

				// Use BTR is ETB is null
				if (ts == null) {
					log.info("ETB is null");
					ts = rs.getTimestamp("vsl_berth_dttm");
				}
			}
			log.info("ATB/ETB/BTR    : " + ts);
			log.info("---Done---");
		} catch (Exception e) {
			log.info("Exception getTimestamp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getTimestamp Process GB Generic DAO");
			// do not close the connection
			// it will be closed by the setter/invoker
		}
		return ts;
	}
	// EndRegion ProcessGBStoreJdbcRepository

	// package: ejb.sessionBeans.cab.processCharges-->ProcessGBStoreEJB
		// method: calculateSRBillCharge()
		// New EVM Enhancement
		/**
		 * Calculates the store rent charges based on the event log and returns result
		 * based on published rates
		 * 
		 * @param GeneralEventLogValueObject
		 * @param String
		 * @return ArrayList
		 * @exception NamingException
		 * @exception ProcessChargeException
		 * @exception SQLException
		 * @exception Exception              Used during event log update
		 */
		public List<ChargeableBillValueObject> calSRBillCharge(GeneralEventLogValueObject generalEventLogValueObject, String refInd)
				throws Exception {

			BillErrorVO billErrorValueObject = null;
			ChargeableBillValueObject chargeableBillValueObject = null;
			VesselRelatedValueObject vesselRelatedValueObject = null;
			BerthRelatedValueObject berthRelatedValueObject = null;
			TariffMainVO tariffMainValueObject = null;
			AccountValueObject accountValueObject = null;

			List<ChargeableBillValueObject> chargeableBillList = null;
			List<ChargeableBillValueObject> prevChargeableBillList = null;
			Timestamp varDttm = null;
			int versionNbr = 0;

			String refNbr = null;
			String discVvCd = null;
			String loadVvCd = null;
			String vvInd = null;
			String businessType = null;
			String schemeCd = null;
			String tariffMainCat = null;
			String tariffSubCat = null;
			String mvmt = null;
			String type = null;
			String localLeg = null;
			String billAcctNbr = null;
			String billInd = null;
			Timestamp lastModifyDttm = null;

			String varVvCd = null;
			double minTon = 0.0;
			double billTon = 0.0;
			double deriveTon = 0.0;
			double deriveTimePeriod = 0.0;
			double billTonEdo = 0.0;
			int totalPkgEdo = 0;
			int totalPkgDn = 0;
			double billTonEsn = 0.0;
			double loadTonCs = 0.0;

			try {
				log.info("START calSRBillCharge Dao :: generalEventLogValueObject: " + CommonUtility.deNull(generalEventLogValueObject.toString())
						+ " refInd: " + CommonUtility.deNull(refInd));
				refNbr = processLogGB.determineRefNbr(generalEventLogValueObject, refInd);
				discVvCd = generalEventLogValueObject.getDiscVvCd();
				loadVvCd = generalEventLogValueObject.getLoadVvCd();
				vvInd = generalEventLogValueObject.getVvInd();
				businessType = generalEventLogValueObject.getBusinessType();
				schemeCd = generalEventLogValueObject.getSchemeCd();
				tariffMainCat = generalEventLogValueObject.getTariffMainCatCd();
				tariffSubCat = generalEventLogValueObject.getTariffSubCatCd();
				mvmt = generalEventLogValueObject.getMvmt();
				type = generalEventLogValueObject.getType();
				localLeg = generalEventLogValueObject.getLocalLeg();
				billAcctNbr = generalEventLogValueObject.getBillAcctNbr();
				billInd = generalEventLogValueObject.getBillInd();
				lastModifyDttm = generalEventLogValueObject.getLastModifyDttm();
				billTonEdo = generalEventLogValueObject.getBillTonEdo();
				totalPkgEdo = generalEventLogValueObject.getTotalPackEdo();
				totalPkgDn = generalEventLogValueObject.getTotalPackDn();
				billTonEsn = generalEventLogValueObject.getBillTonEsn();
				loadTonCs = generalEventLogValueObject.getLoadTonCs();

				/*
				 * if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) { varVvCd = discVvCd; }
				 */// <cfg amend, 12.aug.09>
				//        	log.info("[CAB]","CR64 [SR]  calculateSRBillCharge<==========================================================");
				//			log.info("[CAB]","CR64 [SR] calculateSRBillCharge vvInd = ("+vvInd+"), mvmt = ("+mvmt+"),"+tariffMainCat+"/"+tariffSubCat);
				//			log.info("[CAB]","CR64 [SR] calculateSRBillCharge schemeCd = ("+schemeCd+"), localLeg = ("+localLeg+"),disc:"+discVvCd+"/load:"+loadVvCd);log.info("[CAB]","CR64 [SR] vvInd = ("+vvInd+"), mvmt = ("+mvmt+"),"+tariffMainCat+"/"+tariffSubCat);
				// TS && JLR && SRGL/SRUS
				if (vvInd.equals(ProcessChargeConst.DISC_VV_IND)) {
					if (mvmt.trim().equalsIgnoreCase(ProcessChargeConst.MVMT_TRANSHIP)
							&& schemeCd.equalsIgnoreCase(ProcessChargeConst.LINER_SCHEME) && // <cfg accdg to user,
							// 10.nov.09>
							(tariffMainCat.trim().equalsIgnoreCase(ProcessChargeConst.TARIFF_MAIN_GB_STORE_RENT)
									&& (tariffSubCat.trim()
											.equalsIgnoreCase(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_GENERAL)
											|| tariffSubCat.trim().equalsIgnoreCase(
													ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)))) {
						varVvCd = loadVvCd;
					} else {
						varVvCd = discVvCd;
					}
				} // <cfg 12.aug.09/>

				else if (vvInd.equals(ProcessChargeConst.LOAD_VV_IND)) {
					varVvCd = loadVvCd;
				}

				vesselRelatedValueObject = new VesselRelatedValueObject();
				vesselRelatedValueObject.setVvCd(varVvCd);

				try {
					berthRelatedValueObject = processGenericRepo.retrieveBerthDttm(vesselRelatedValueObject, 1,
							tariffMainCat);
					varDttm = berthRelatedValueObject.getAtbDttm();
				} catch (Exception e) {
				}

				if (varDttm == null) {
					varDttm = UserTimestampVO.getCurrentTimestamp();
				}

				// retrieve the relevant tariff version number based on the timestamp passed in
				versionNbr = processGenericRepo.retrieveTariffVersion(varDttm, vesselRelatedValueObject, tariffMainCat);

				// There's no differeniation of scheme code for Store Rent
				schemeCd = ProcessChargeConst.NOT_APPLICABLE_SCHEME;

				// add by hujun on 2/8/2011 for shutout cargo billing
				if (ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO.equalsIgnoreCase(tariffSubCat)) {
					if (ProcessChargeConst.NON_LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						schemeCd = ProcessChargeConst.NON_LINER_SCHEME;
					} else if (ProcessChargeConst.BARTER_TRADER_SCHEME
							.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						schemeCd = ProcessChargeConst.BARTER_TRADER_SCHEME;
					} else if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						schemeCd = ProcessChargeConst.LCT_SCHEME;
					} else if (ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						schemeCd = ProcessChargeConst.LINER_SCHEME;
					} else if (ProcessChargeConst.WOODEN_CRAFT_SCHEME
							.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						schemeCd = ProcessChargeConst.WOODEN_CRAFT_SCHEME;
					}
				}
				// add end

				// Set the mvmt to Not applicable for Unauthorised Store Rent
				if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
					mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
				}

				// add by hujun 2/8/2011 for shutout cargo
				if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
					if (!ProcessChargeConst.LINER_SCHEME.equalsIgnoreCase(generalEventLogValueObject.getSchemeCd())) {
						mvmt = ProcessChargeConst.NOT_APPLICABLE_MVMT;
					}
				}
				// add end

				// retrieve the published tariff
				// Change by Irene Tan on 10/12/2002 : CTCAB20020044 - To crater for GST change
				// w.e.f 1 Jan 2003
				// tariffMainValueObject = processGeneric.retrievePublishTariffDtls(versionNbr,
				// tariffMainCat, tariffSubCat, businessType, schemeCd, mvmt, type, "00", "00");
				// log.info("[CAB]", "BusinessType calculateSRBillCharge before
				// tariffMain= " +businessType);
				tariffMainValueObject = processGenericRepo.retrievePublishTariffDtls(versionNbr, tariffMainCat,
						tariffSubCat, businessType, schemeCd, mvmt, type, "0", "0", varDttm);
				// End Change by Irene Tan

				// compute bill tonnage
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
					billTon = transLogRepo.computeBillTon(billTonEdo, totalPkgEdo, totalPkgDn);
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
					billTon = billTonEsn;
				} else if ((refInd.trim().equals(ProcessChargeConst.REF_IND_ESN))
						|| (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO))) {
					billTon = loadTonCs;
				}

				if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
					minTon = tariffMainValueObject.getMinTonnage();
					if (billTon < minTon) {
						deriveTon = minTon;
					} else {
						deriveTon = billTon;
					}
				} else {
					deriveTon = billTon;
				}

				// MCConsulting min 1 billable ton for all general cargo charges
				if (deriveTon < 1 && deriveTon > 0)
					deriveTon = 1;

				// compute the derived time unit - chargeable number of time unit
				generalEventLogValueObject.setVarDttm(varDttm); // SL-CAB-20170518-01
				deriveTimePeriod = determineTimeUnit(generalEventLogValueObject, refInd);

				// To adjust the derive time period into days for unauthorised storage
				if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_UNAUTH_STORAGE)) {
					DoubleMath dm = new DoubleMath();
					dm.setRoundingMode(BigDecimal.ROUND_UP);

					int tierCnt = 0;
					String perHourType = "";
					for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
						// get the individual record of the tariff tier record
						TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
						perHourType = tariffTierValueObject.getPerHourType();
					}

					if (perHourType.equals("D")) {
						deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
					}
				}

				// add by hujun on 2/8/2011
				if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_GB_STORE_RENT_SHUTOUT_CARGO)) {
					DoubleMath dm = new DoubleMath();
					dm.setRoundingMode(BigDecimal.ROUND_UP);

					int tierCnt = 0;
					String perHourType = "";
					for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
						// get the individual record of the tariff tier record
						TariffTierVO tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);
						perHourType = tariffTierValueObject.getPerHourType();
					}

					if (perHourType.equals("D")) {
						deriveTimePeriod = dm.divide(deriveTimePeriod, 24.0); // hr to day
					}
				}
				// add end

				chargeableBillValueObject.setRefNbr(refNbr);
				chargeableBillValueObject.setTxnDttm(lastModifyDttm);
				chargeableBillValueObject.setRefInd(refInd);
				chargeableBillValueObject.setTariffMainCatCd(tariffMainCat);
				chargeableBillValueObject.setTariffSubCatCd(tariffSubCat);
				chargeableBillValueObject.setVersionNbr(versionNbr);
				chargeableBillValueObject.setCustCd("");
				chargeableBillValueObject.setAcctNbr("");
				chargeableBillValueObject.setContractNbr("");
				chargeableBillValueObject.setContractualYr(0);
				chargeableBillValueObject.setNbrCntr(0);
				chargeableBillValueObject.setVvCd(varVvCd);
				chargeableBillValueObject.setBillInd(billInd);
				chargeableBillValueObject.setLocalLeg(localLeg);
				chargeableBillValueObject.setLastModifyUserId("SYSTEM");
				chargeableBillValueObject.setLastModifyDttm(lastModifyDttm);

				// get customer a/c & determine customer code
				if (billAcctNbr.trim().equals(ProcessChargeConst.CASH_CUST_CD)) {
					accountValueObject = processGenericRepo.retrieveCashCustAcct();
				} else {
					accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);
				}
				chargeableBillValueObject.setAcctNbr(accountValueObject.getAccountNumber());
				chargeableBillValueObject.setCustCd(accountValueObject.getCustomerCode());

				// chargeableBillList = determineStoreRentBillable(chargeableBillValueObject,
				// tariffMainValueObject, deriveTon, deriveTimePeriod);
				// Added by Jade for SL-CAB-20110509-01
				int fsp = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
				chargeableBillList = determineStoreRentBillable(chargeableBillValueObject, tariffMainValueObject, deriveTon,
						deriveTimePeriod, fsp);
				// End of adding by Jade for SL-CAB-20110509-01

				// MCC check if last triggered dttm is available, if so calculate the storage
				// period for last triggered dttm
				// get ProcessGenericHome object
				Timestamp lastTriggeredDttm = null;
				if (generalEventLogValueObject.getRefInd().equals("DN")) {
					lastTriggeredDttm = processGenericRepo
							.retrievePeriodicSRLastTriggerDttm(generalEventLogValueObject.getEdoAsnNbr(), "ED", null);
				}
				if (lastTriggeredDttm != null) {
					// retrieve time period from COD to last triggered date time

					generalEventLogValueObject.setLastTriggerPSRDttm(lastTriggeredDttm);
					deriveTimePeriod = determineTimeUnit(generalEventLogValueObject, refInd);

					prevChargeableBillList = determineStoreRentBillable(chargeableBillValueObject, tariffMainValueObject,
							deriveTon, deriveTimePeriod, fsp);

					// loop through the original charge list and current periodic charge list and
					// minus the previoud charges
					List<ChargeableBillValueObject> finalChargeableBillList = new ArrayList<ChargeableBillValueObject>();
					for (int i = 0; i < chargeableBillList.size(); i++) {
						ChargeableBillValueObject finalChargeableBillVO = (ChargeableBillValueObject) chargeableBillList
								.get(i);
						ChargeableBillValueObject newFinalChargeableBillVO = new ChargeableBillValueObject(
								finalChargeableBillVO);

						for (int j = 0; j < prevChargeableBillList.size(); j++) {
							ChargeableBillValueObject lastPeriodicVO = (ChargeableBillValueObject) prevChargeableBillList
									.get(i);
							if (newFinalChargeableBillVO.getTierSeqNbr() == lastPeriodicVO.getTierSeqNbr()) {

								if (newFinalChargeableBillVO.getNbrTimeUnit() - lastPeriodicVO.getNbrTimeUnit() > 0) {
									double nbrTimeUnit = newFinalChargeableBillVO.getNbrTimeUnit()
											- lastPeriodicVO.getNbrTimeUnit();
									double totalChargeAmt = newFinalChargeableBillVO.getTotalChargeAmt()
											- lastPeriodicVO.getTotalChargeAmt();
									newFinalChargeableBillVO.setNbrTimeUnit(nbrTimeUnit);
									newFinalChargeableBillVO.setTotalChargeAmt(totalChargeAmt);
									finalChargeableBillList.add(newFinalChargeableBillVO);
								}
							}
						}
					}
					generalEventLogValueObject.setLastTriggerPSRDttm(null);
					return finalChargeableBillList;
				}
				return chargeableBillList;
			} catch (BusinessException e) {
				log.info("ProcessServiceEJB Exception " + e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
					try {
						billErrorValueObject = new BillErrorVO();
						billErrorValueObject.setRunInd(billErrorValueObject.RUN_IND_CREATE_BILL);
						billErrorValueObject.setTariffMainCat(tariffMainCat);
						billErrorValueObject.setTariffSubCat(tariffSubCat);
						billErrorValueObject.setRemarks(
								"Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd + ") ~vv_cd ("
										+ varVvCd + ") ~local_leg (" + localLeg + ") ~Msg = " + e.getMessage());
						processLogGB.insertBillError(billErrorValueObject);
						if (e instanceof Exception) {
							throw e;
						} else {
							throw new Exception("Exception occurred for ~ref_nbr (" + refNbr + ") ~ref_ind (" + refInd
									+ ") ~Msg = " + e.getMessage());
						}
					} catch (Exception billErrorException) {
						throw new Exception("[ProcessGBStoreEJB Error] >> Inserting billError exception = "
								+ billErrorException.getMessage() + "Exception occurred for ~ref_nbr (" + refNbr
								+ ") ~ref_ind (" + refInd + ") ~vv_cd (" + varVvCd + ") ~local_leg (" + localLeg
								+ ") ~Msg = " + e.getMessage());
					}
			}
		} // New EVM only during EVent log update


}
